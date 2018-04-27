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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class UserAuthenticateRestIT.
 */
public class UserAuthenticateRestIT extends AbstractRestServiceTest {

	private ObjectMapper MAPPER = new ObjectMapper();

	UserHelper userHelper = new UserHelper();

	/**
	 * Authenticate get jwt token success test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void authenticateGetJwtTokenSuccessTest() throws Exception {

		final Map<String, Object> data = userHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, data.get("authenticated"));
		assertNotNull(data.get("jwtoken"));
	}

	/**
	 * Authenticate get jwt token failure test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void authenticateGetJwtTokenFailureTest() throws Exception {
		final Map<String, Object> data = userHelper.authenticate("vms_admin_com", "invalidpassword");
		assertEquals(false, data.get("authenticated"));
		assertNull(data.get("jwtoken"));
	}

	/**
	 * get challenge for an authenticated user
	 *
	 * @throws Exception
	 */
	@Test
	public void getUserChallenge() throws Exception {

		// logon
		final Map<String, Object> authData = userHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.get("authenticated"));
		assertNotNull(authData.get("jwtoken"));

		String jwtoken = String.valueOf(authData.get("jwtoken"));

		// use the jwToken to get challenge
		final Map<String, Object> data = userHelper.getChallenge(jwtoken);
		assertNotNull(data.get("challenge"));
		assertEquals("vms_admin_com", data.get("userName"));
	}

	/**
	 * check if its allowed to tamper with jwToken
	 *
	 * @throws Exception
	 */
	@Test
	public void getUserChallengeTamperedJwt() throws Exception {

		final Map<String, Object> authData = userHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.get("authenticated"));
		assertNotNull(authData.get("jwtoken"));

		String jwtoken = String.valueOf(authData.get("jwtoken"));
		jwtoken += "tampered";

		final HttpResponse response = Request.Get(getBaseUrl() + "usm-administration/rest/challenge")
				.setHeader("Content-Type", "application/json").setHeader("authorization", jwtoken).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusLine().getStatusCode());
	}

	@Test
	//@Ignore
	public void challengeAuth() throws Exception {

		// until the backend jdbc code is corrected this test is
		// undeterministical
		// actually the others as well .... since jdbc is wrong coded

		/*
		 * in SCHEMA USM select t.response from usm.user_t u join
		 * usm.challenge_t t on u.user_id = t.user_id where
		 * u.user_name='vms_admin_com' and t.challenge='Name of first pet'
		 */

		final Map<String, Object> authData = userHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.get("authenticated"));
		assertNotNull(authData.get("jwtoken"));

		String jwtoken = String.valueOf(authData.get("jwtoken"));

		// use the jwToken to get challenge
		Map<String, Object> data = userHelper.getChallenge(jwtoken);
		String challenge = String.valueOf(data.get("challenge"));
		String userName = String.valueOf(data.get("userName"));

		// use the jwToken to get challenge

		ChallengeResponseTEST challengeResponseTest = new ChallengeResponseTEST("vms_admin_com", challenge,
				"Tartampion");
		String json = MAPPER.writeValueAsString(challengeResponseTest);

		final HttpResponse response = Request.Post(getBaseUrl() + "usm-administration/rest/challengeauth")
				.setHeader("Content-Type", "application/json").bodyByteArray(json.getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		Map<String, Object> data2 = getJsonMap(response);
		String auth = String.valueOf(data2.get("authenticated"));
		assertTrue(auth.equals("true"));
	}

	@Test
	public void challengeAuthVerifyWrongChallengeIsNotAccepted() throws Exception {

		/*
		 * in SCHEMA USM select t.response from usm.user_t u join
		 * usm.challenge_t t on u.user_id = t.user_id where
		 * u.user_name='vms_admin_com' and t.challenge='Name of first pet'
		 */

		final Map<String, Object> authData = userHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.get("authenticated"));
		assertNotNull(authData.get("jwtoken"));

		String jwtoken = String.valueOf(authData.get("jwtoken"));

		// use the jwToken to get challenge
		Map<String, Object> data = userHelper.getChallenge(jwtoken);
		String challenge = String.valueOf(data.get("challenge"));

		String answer = UUID.randomUUID().toString();

		ChallengeResponseTEST challengeResponseTest = new ChallengeResponseTEST("vms_admin_com", challenge, answer);
		String json = MAPPER.writeValueAsString(challengeResponseTest);

		final HttpResponse response = Request.Post(getBaseUrl() + "usm-administration/rest/challengeauth")
				.setHeader("Content-Type", "application/json").bodyByteArray(json.getBytes()).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		Map<String, Object> data2 = getJsonMap(response);
		String auth = String.valueOf(data2.get("authenticated"));
		assertTrue(auth.equals("false"));
	}

	@Test
	public void userContexts() throws Exception {

		final Map<String, Object> authData = userHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.get("authenticated"));
		assertNotNull(authData.get("jwtoken"));

		String jwtoken = String.valueOf(authData.get("jwtoken"));

		final HttpResponse response = Request.Get(getBaseUrl() + "usm-administration/rest/userContexts")
				.setHeader("Content-Type", "application/json").setHeader("authorization", jwtoken).execute()
				.returnResponse();

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		Map<String, Object> data2 = getJsonMap(response);
		Object contextSet = data2.get("contextSet");

		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
		String ctx = String.valueOf(contextSet).trim();
		assertNotNull(contextSet);
		assertTrue(ctx.length() > 0);

	}

	@Test
	public void ping() throws Exception {

		final Map<String, Object> authData = userHelper.authenticate("vms_admin_com", "password");
		assertEquals(true, authData.get("authenticated"));
		assertNotNull(authData.get("jwtoken"));

		String jwtoken = String.valueOf(authData.get("jwtoken"));

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
