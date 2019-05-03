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

import java.util.Map;
import java.util.UUID;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuthenticationResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.ChallengeResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.ContextSet;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.UserContext;

/**
 * The Class UserAuthenticateRestIT.
 */
public class UserAuthenticateRestIT extends AbstractRest {

	/**
	 * Authenticate get jwt token success test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void authenticateGetJwtTokenSuccessTest() throws Exception {

		final AuthenticationResponse data = UserHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, data.isAuthenticated());
		assertNotNull(data.getJwtoken());
	}

	/**
	 * Authenticate get jwt token failure test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void authenticateGetJwtTokenFailureTest() throws Exception {
		final AuthenticationResponse data = UserHelper.authenticate("vms_admin_com", "invalidpassword");
		assertEquals(false, data.isAuthenticated());
		assertNull(data.getJwtoken());
	}

	/**
	 * get challenge for an authenticated user
	 *
	 * @throws Exception
	 */
	@Test
	public void getUserChallenge() throws Exception {

		// logon
		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();

		// use the jwToken to get challenge
		final ChallengeResponse data = UserHelper.getChallenge(jwtoken);
		assertNotNull(data.getChallenge());
		assertEquals("vms_admin_com", data.getUserName());
	}

	/**
	 * check if its allowed to tamper with jwToken
	 *
	 * @throws Exception
	 */
	@Test
	public void getUserChallengeTamperedJwt() throws Exception {

		final AuthenticationResponse authData = UserHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.isAuthenticated());
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
		assertEquals(true, authData.isAuthenticated());
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
		assertEquals(true, authData.isAuthenticated());
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
		assertEquals(true, authData.isAuthenticated());
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
		assertEquals(true, authData.isAuthenticated());
		assertNotNull(authData.getJwtoken());

		String jwtoken = authData.getJwtoken();

		final HttpResponse response = Request.Get(getBaseUrl() + "usm-administration/rest/ping")
				.setHeader("Content-Type", "application/json").setHeader("authorization", jwtoken).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		Map<String, Object> data2 = getJsonMap(response);
		String statusCode = String.valueOf(data2.get("statusCode"));
		String message = String.valueOf(data2.get("message"));
		assertTrue(statusCode.equalsIgnoreCase("200"));
		assertTrue(message.equalsIgnoreCase("OK"));

	}

}
