/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation;

import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

/**
 * The Class ModuleAvailableSystemIT.
 */
public class ModuleAvailableSystemIT extends Assert {

	/** The base url. */
	private final String BASE_URL = "http://localhost:28080/";

	/**
	 * Check union vms web access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkUnionVmsWebAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_OK,Request.Get(BASE_URL + "unionvms/").execute().returnResponse().getStatusLine().getStatusCode());
	}

	/**
	 * Check user access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkUserAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_OK,Request.Get(BASE_URL + "usm-administration/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "/usm-administration/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	/**
	 * Check config access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkConfigAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_OK,Request.Get(BASE_URL + "config/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "config/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}	

	/**
	 * Check exchange access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkExchangeAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "exchange/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "exchange/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	/**
	 * Check spatial access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkSpatialAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "spatial/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "spatial/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}
	
	/**
	 * Check movement access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkMovementAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "movement/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "movement/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	/**
	 * Check audit access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkAuditAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_OK,Request.Get(BASE_URL + "audit/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "audit/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	/**
	 * Check asset access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkAssetAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_OK,Request.Get(BASE_URL + "asset/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "asset/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	/**
	 * Check mobileterminal access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkMobileterminalAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "mobileterminal/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "mobileterminal/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	/**
	 * Check rules access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkRulesAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "rules/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "rules/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}
	

	/**
	 * Check reporting access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkReportingAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "reporting/").execute().returnResponse().getStatusLine().getStatusCode());
	}

	
	
}
