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
package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

public class ExchangeRegistryRestIT extends AbstractRest {

	@Test
	public void getListTest() {

		Response response = getWebTarget()
				.path("exchange/rest/plugin/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(Response.class);

		assertEquals(200, response.getStatus());

		List<Plugin> plugins = response.readEntity(new GenericType<List<Plugin>>() {});
		assertNotNull(plugins);
		assertFalse(plugins.isEmpty());
	}


    /* 	Removed two tests that started and stopped the service sweagencyemail.
    	Since sweagency is specific to swe (and thus other are not supposed to have
    	that plugin) and since sweagencyemail is not in a working order, the test where removed. */
}
