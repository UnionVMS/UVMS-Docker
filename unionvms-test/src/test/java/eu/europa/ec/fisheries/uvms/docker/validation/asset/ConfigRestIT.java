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
package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

public class ConfigRestIT extends AbstractRest {

	@Test
	public void getConfigSearchFieldsTest() throws Exception {
	    Response response = getWebTarget()
                .path("asset/rest/config/searchfields")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();
	    assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	@Ignore("This endpoint is removed.")
	public void getConfigurationTest() throws Exception {
	    Response response = getWebTarget()
                .path("asset/rest/config")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void getParametersTest() throws Exception {
	    Response response = getWebTarget()
                .path("asset/rest/config/parameters")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get();
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
}
