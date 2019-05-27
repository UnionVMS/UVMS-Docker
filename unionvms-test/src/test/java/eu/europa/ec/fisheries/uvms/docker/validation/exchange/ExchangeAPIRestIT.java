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

import eu.europa.ec.fisheries.schema.exchange.module.v1.GetServiceListRequest;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.exchange.source.v1.GetServiceListResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

public class ExchangeAPIRestIT extends AbstractRest {
	@Test
	public void getListTest() {
		GetServiceListRequest getServiceListRequest = new GetServiceListRequest();
		getServiceListRequest.getType().add(PluginType.SATELLITE_RECEIVER);

		GetServiceListResponse response = getWebTarget()
				.path("exchange/unsecured/rest/api/serviceList")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.post(Entity.json(getServiceListRequest), GetServiceListResponse.class);

		assertNotNull(response);
		assertTrue(response.getService().size() > 0);
	}
}
