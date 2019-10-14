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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MTQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class MobileTerminalRestIT extends AbstractRest {

	@Test
	public void createMobileTerminalTest() {
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		assertNotNull(mobileTerminal);
		assertNotNull(mobileTerminal.getId());
	}

	@Test
	public void getMobileTerminalByIdTest() {
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalDto fetchedMobileTerminal = MobileTerminalTestHelper.getMobileTerminalById(mobileTerminal.getId());
		assertNotNull(fetchedMobileTerminal);
	}

	@Test
	public void updateMobileTerminalTest() {
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		assertFalse(mobileTerminal.getArchived());
		mobileTerminal.setArchived(true);

		MobileTerminalDto updatedMobileTerminal = MobileTerminalTestHelper.updateMobileTerminal(mobileTerminal);
		assertNotNull(updatedMobileTerminal);
		assertTrue(mobileTerminal.getArchived());
	}

	@Test
	public void getMobileTerminalListTest() {
		MTQuery mtQuery = new MTQuery();
		mtQuery.setTranceiverTypes(Arrays.asList("dummy"));

		Response response = MobileTerminalTestHelper.getMobileTerminalList(mtQuery);
		assertNotNull(response);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}

	@Test
	public void assignMobileTerminalTest() {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminal();

		MobileTerminalDto mobileTerminalDto = MobileTerminalTestHelper.assignMobileTerminal(testAsset, createdMobileTerminalType);
		assertNotNull(mobileTerminalDto);
		assertNotNull(mobileTerminalDto.getAssetId());
	}

	@Test
	public void unAssignMobileTerminalTest() {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto createdMobileTerminalType = MobileTerminalTestHelper.createMobileTerminal();

		MobileTerminalDto mobileTerminalDto = MobileTerminalTestHelper.assignMobileTerminal(testAsset, createdMobileTerminalType);
		assertNotNull(mobileTerminalDto);
		assertNotNull(mobileTerminalDto.getAssetId());

		MobileTerminalDto unAssignMobileTerminal = MobileTerminalTestHelper.unAssignMobileTerminal(testAsset, createdMobileTerminalType);
		assertNotNull(unAssignMobileTerminal);
		assertNull(unAssignMobileTerminal.getAssetId());
	}

	@Test
	public void setStatusActiveTest() {
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();

		MobileTerminalDto activated = MobileTerminalTestHelper.activateMobileTerminal(mobileTerminal.getId());
		assertNotNull(activated);
		assertTrue(activated.getActive());
	}

	@Test
	public void setStatusInactiveTest() {
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();

		MobileTerminalDto inActivated = MobileTerminalTestHelper.inactivateMobileTerminal(mobileTerminal.getId());
		assertNotNull(inActivated);
		assertFalse(inActivated.getActive());
	}

	@Test
	public void setStatusRemovedTest() {
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();

		MobileTerminalDto removed = MobileTerminalTestHelper.removeMobileTerminal(mobileTerminal.getId());
		assertNotNull(removed);
		assertFalse(removed.getActive());
		assertTrue(removed.getArchived());
	}

	@Test
	public void getMobileTerminalHistoryListByMobileTerminalIdTest() {
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();

		List<MobileTerminalDto> historyList = MobileTerminalTestHelper.getMobileTerminalHistoryList(mobileTerminal.getId());
		assertNotNull(historyList);
		assertFalse(historyList.isEmpty());
	}
}
