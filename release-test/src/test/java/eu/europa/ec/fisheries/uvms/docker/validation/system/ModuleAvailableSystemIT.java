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
package eu.europa.ec.fisheries.uvms.docker.validation.system;

import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class ModuleAvailableSystemIT.
 */
public class ModuleAvailableSystemIT extends AbstractRest {

	/**
	 * Check union vms web access test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void checkUnionVmsWebAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_OK,
				Request.Get(getBaseUrl()).execute().returnResponse().getStatusLine().getStatusCode());
	}

	/**
	 * Check spatial access test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void checkSpatialAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,
				Request.Get(getBaseUrl() + "spatial/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_OK,
				Request.Get(getBaseUrl() + "spatial/monitoring").execute().returnResponse().getStatusLine().getStatusCode());		
		assertEquals(HttpStatus.SC_FORBIDDEN,
				Request.Get(getBaseUrl() + "spatial/rest").execute().returnResponse().getStatusLine().getStatusCode());
	}

	/**
	 * Check rules access test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void checkRulesAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,
				Request.Get(getBaseUrl() + "rules/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_OK,
				Request.Get(getBaseUrl() + "rules/monitoring").execute().returnResponse().getStatusLine().getStatusCode());		
		assertEquals(HttpStatus.SC_FORBIDDEN,
				Request.Get(getBaseUrl() + "rules/rest").execute().returnResponse().getStatusLine().getStatusCode());
	}

	/**
	 * Check Activity access test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void checkActivityAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "activity/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "activity/rest").execute().returnResponse().getStatusLine().getStatusCode());
	}

	/**
	 * Check Mdr access test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void checkMdrAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "mdr/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(HttpStatus.SC_FORBIDDEN,Request.Get(BASE_URL + "mdr/rest").execute().returnResponse().getStatusLine().getStatusCode());
	}

	/**
	 * Check mapfish print access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkMapfishPrintAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_FORBIDDEN,
				Request.Get("http://localhost:28080/" + "mapfish-print/").execute().returnResponse().getStatusLine().getStatusCode());
	}

	/**
	 * Check geoserver access test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void checkGeoserverAccessTest() throws Exception {
		assertEquals(HttpStatus.SC_OK,
				Request.Get("http://localhost:28080/geoserver/").execute().returnResponse().getStatusLine().getStatusCode());
	}
}
