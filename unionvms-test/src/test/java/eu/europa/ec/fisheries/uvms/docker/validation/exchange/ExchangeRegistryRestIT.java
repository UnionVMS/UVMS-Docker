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

import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

public class ExchangeRegistryRestIT extends AbstractRest {

	@Test
	public void getListTest() {

		ResponseDto response = getWebTarget()
				.path("exchange/rest/plugin/list")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(ResponseDto.class);

		assertEquals(200, response.getCode());

		ArrayList list = (ArrayList) response.getData();
		assertNotNull(list);
		assertFalse(list.isEmpty());
	}

    // Removed two tests that started and stopped the service sweagencyemail. Since sweagency is specific to swe
	// (and thus other are not supposed to have that plugin) and since sweagencyemail is not in a working order, the test where removed.
}
