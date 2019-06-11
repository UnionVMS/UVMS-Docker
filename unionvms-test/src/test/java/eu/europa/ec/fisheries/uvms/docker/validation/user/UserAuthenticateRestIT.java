/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
� European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.user;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuthenticationResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.ChallengeResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.ContextSet;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.StatusResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.UserContext;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.UUID;

public class UserAuthenticateRestIT extends AbstractRest {

	@Test
	public void authenticateGetJwtTokenSuccessTest() throws Exception {
		final AuthenticationResponse data = UserHelper.authenticate("vms_admin_com", "password");
		assertTrue(data.isAuthenticated());
		assertNotNull(data.getJwtoken());
	}

	@Test
	public void authenticateGetJwtTokenFailureTest() throws Exception {
		final AuthenticationResponse data = UserHelper.authenticate("vms_admin_com", "invalidpassword");
		assertFalse(data.isAuthenticated());
		assertNull(data.getJwtoken());
	}

	@Test
	public void getUserChallenge() throws Exception {

		// logon
		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertTrue(authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();

		// use the jwToken to get challenge
		final ChallengeResponse data = UserHelper.getChallenge(jwtoken);
		assertNotNull(data.getChallenge());
		assertEquals("vms_admin_com", data.getUserName());
	}

	@Test
	public void getUserChallengeTamperedJwt() throws Exception {
		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertTrue(authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();
		jwtoken += "tampered";

		Response challengeResponse = getWebTarget()
                .path("usm-administration/rest/challenge")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtoken)
                .get();

		assertEquals(Status.FORBIDDEN.getStatusCode(), challengeResponse.getStatus());
	}

	@Test
	@Ignore
	public void challengeAuth() throws Exception {

		// until the backend jdbc code is corrected this test is
		// undeterministical
		// actually the others as well .... since jdbc is wrong coded

		/*
		 * in SCHEMA USM select t.response from usm.user_t u join
		 * usm.challenge_t t on u.user_id = t.user_id where
		 * u.user_name='vms_admin_com' and t.challenge='Name of first pet'
		 */

		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertTrue(authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();

		// use the jwToken to get challenge
		ChallengeResponse data = UserHelper.getChallenge(jwtoken);
		String challenge = data.getChallenge();
		String userName = data.getUserName();
		String challengeResponse = data.getResponse();

		// use the jwToken to get challenge

		ChallengeResponse challengeResponseTest = new ChallengeResponse(userName, challenge, challengeResponse);

		AuthenticationResponse response = getWebTarget()
                .path("usm-administration/rest/challengeauth")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtoken)
                .post(Entity.json(challengeResponseTest), AuthenticationResponse.class);

		assertTrue(response.isAuthenticated());
	}

	@Test
	public void challengeAuthVerifyWrongChallengeIsNotAccepted() throws Exception {

		/*
		 * in SCHEMA USM select t.response from usm.user_t u join
		 * usm.challenge_t t on u.user_id = t.user_id where
		 * u.user_name='vms_admin_com' and t.challenge='Name of first pet'
		 */

		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertTrue(authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();

		// use the jwToken to get challenge
		ChallengeResponse data = UserHelper.getChallenge(jwtoken);
		String challenge = data.getChallenge();

		String answer = UUID.randomUUID().toString();

		ChallengeResponse challengeResponseTest = new ChallengeResponse("vms_admin_com", challenge, answer);

		AuthenticationResponse response = getWebTarget()
                .path("usm-administration/rest/challengeauth")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtoken)
                .post(Entity.json(challengeResponseTest), AuthenticationResponse.class);

		assertFalse(response.isAuthenticated());
	}

	@Test
	public void userContexts() throws Exception {
		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertTrue(authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();

		UserContext response = getWebTarget()
                .path("usm-administration/rest/userContexts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtoken)
                .get(UserContext.class);

		ContextSet contextSet = response.getContextSet();

		assertNotNull(contextSet);
		assertTrue(contextSet.getContexts().size() > 0);
	}

	@Test
	public void ping() throws Exception {
		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertTrue(authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();

		Response response = getWebTarget()
				.path("usm-administration/rest/ping")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, jwtoken)
				.get();

		assertEquals(Status.OK.getStatusCode(), response.getStatus());

		StatusResponseDto responseDto = response.readEntity(StatusResponseDto.class);

		assertEquals(Status.OK.getStatusCode(), responseDto.getStatusCode());
		assertEquals("OK", responseDto.getMessage());
	}
}
