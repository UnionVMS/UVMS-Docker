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

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.fisheries.uvms.asset.client.model.*;
import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.hamcrest.CoreMatchers;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.sse.SseEventSource;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class AssetRestIT extends AbstractRest {

    @Test
    public void getAssetListTest() {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
        assetQuery.setFlagState(Collections.singletonList(asset.getFlagStateCode()));

        AssetListResponse assetListResponse = AssetTestHelper.assetListQuery(assetQuery);
        List<AssetDTO> assets = assetListResponse.getAssetList();
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset.getId())));
    }

    @Test
    public void getAssetListMultipleAssetsGuidsTest() {
        AssetDTO asset1 = AssetTestHelper.createTestAsset();
        AssetDTO asset2 = AssetTestHelper.createTestAsset();

        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
        assetQuery.setId(Arrays.asList(asset1.getId(), asset2.getId()));

        AssetListResponse assetListResponse = AssetTestHelper.assetListQuery(assetQuery);
        List<AssetDTO> assets = assetListResponse.getAssetList();
        assertEquals(2, assets.size());
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset1.getId())));
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset2.getId())));
    }

    @Test
    public void getAssetListItemCountTest() {
        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
        assetQuery.setFlagState(Collections.singletonList("SWE"));

        Integer countBefore = AssetTestHelper.assetListQueryCount(assetQuery);

        AssetDTO asset = AssetTestHelper.createBasicAsset();
        asset.setFlagStateCode("SWE");
        AssetTestHelper.createAsset(asset);

        Integer countAfter = AssetTestHelper.assetListQueryCount(assetQuery);
        assertEquals(Integer.valueOf(countBefore + 1), countAfter);
    }

    @Test
    public void getAssetListUpdatedIRCSNotFoundTest() {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();

        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
        assetQuery.setIrcs(Collections.singletonList(testAsset.getIrcs()));

        AssetListResponse assetList = AssetTestHelper.assetListQuery(assetQuery);
        List<AssetDTO> assets = assetList.getAssetList();
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(testAsset.getId())));

        testAsset.setIrcs("I" + AssetTestHelper.generateARandomStringWithMaxLength(7));
        AssetTestHelper.updateAsset(testAsset);

        // Search with same query, the asset should not be found
        assetList = AssetTestHelper.assetListQuery(assetQuery);
        assets = assetList.getAssetList();
        assertFalse(assets.stream().anyMatch(a -> a.getId().equals(testAsset.getId())));
    }

    @Test
    public void getAssetListWithLikeSearchValue() {
        AssetDTO asset = AssetTestHelper.createBasicAsset();
        asset.setPortOfRegistration("MyHomePort");
        AssetDTO createdAsset = AssetTestHelper.createAsset(asset);

        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
        assetQuery.setPortOfRegistration(Collections.singletonList("My*"));

        AssetListResponse assetList = AssetTestHelper.assetListQuery(assetQuery);
        List<AssetDTO> assets = assetList.getAssetList();
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    @Test
    public void getAssetByIdTest() {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        AssetDTO assetByGuid = AssetTestHelper.getAssetByGuid(asset.getId());
        assertEquals(asset.getId(), assetByGuid.getId());
    }

    @Test
    public void createAssetTest() {
        AssetTestHelper.createTestAsset();
    }

    @Test
    public void createAssetAuditLogCreatedTest() throws Exception {
        Date fromDate = new Date();
        AssetDTO asset = AssetTestHelper.createTestAsset();
        AssetTestHelper.assertAssetAuditLogCreated(asset.getId(), AuditOperationEnum.CREATE, fromDate);
    }

    @Test
    public void updateAssetAuditLogCreatedTest() throws Exception {
        Date fromDate = new Date();
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        String newName = testAsset.getName() + "Changed";
        testAsset.setName(newName);
        AssetTestHelper.updateAsset(testAsset);

        AssetTestHelper.assertAssetAuditLogCreated(testAsset.getId(), AuditOperationEnum.UPDATE, fromDate);
    }

    @Test
    public void archiveAssetTest() {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        testAsset.setActive(false);
        AssetTestHelper.archiveAsset(testAsset);
    }

    @Test
    public void archiveAssetAuditLogCreatedTest() throws Exception {
        Date fromDate = new Date();
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        testAsset.setActive(false);
        AssetTestHelper.archiveAsset(testAsset);

        AssetTestHelper.assertAssetAuditLogCreated(testAsset.getId(), AuditOperationEnum.ARCHIVE, fromDate);
    }

    @Ignore("Removed resource?")
    @Test
    public void assetListGroupByFlagStateTest() {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        ArrayList<String> assetIdList = new ArrayList<>();
        assetIdList.add(asset.getId().toString());

        Response response = getWebTarget()
                .path("asset/rest/asset/listGroupByFlagState")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(assetIdList));
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void getAssetListWithLikeSearchValue_ICCAT_AND_UVI_GFCM() {
        AssetDTO asset = AssetTestHelper.createBasicAsset();

        String theValue = UUID.randomUUID().toString();
        asset.setIccat(theValue);
        asset.setUvi(theValue);
        asset.setGfcm(theValue);

        AssetDTO createdAsset = AssetTestHelper.createAsset(asset);

        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();

        assetQuery.setIccat(Collections.singletonList(theValue));
        assetQuery.setUvi(Collections.singletonList(theValue));
        assetQuery.setGfcm(Collections.singletonList(theValue));

        AssetListResponse assetList = AssetTestHelper.assetListQuery(assetQuery);
        List<AssetDTO> assets = assetList.getAssetList();
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));
    }

    @Ignore("check what happens when list doesn't find any asset")
    @Test
    public void getAssetListWithLikeSearchValue_CHANGE_KEY_AND_FAIL() {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        UUID guid = asset.getId();

        String oldIccat = asset.getIccat();
        String theValue = UUID.randomUUID().toString();
        String newName = asset.getName() + "Changed";
        asset.setName(newName);
        asset.setIccat(theValue);
        AssetTestHelper.updateAsset(asset);

        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
        assetQuery.setIccat(Collections.singletonList(theValue));
        assetQuery.setId(Collections.singletonList(guid));

        AssetListResponse listAssetResponse = AssetTestHelper.assetListQuery(assetQuery);
        List<AssetDTO> fetchedAsssets = listAssetResponse.getAssetList();
        assertFalse(fetchedAsssets.stream().anyMatch(a -> a.getId().equals(guid)));
    }

    @Test
    public void updateAssetTest() {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        String newName = testAsset.getName() + "Changed";
        testAsset.setName(newName);
        AssetDTO updatedAsset = AssetTestHelper.updateAsset(testAsset);
        assertEquals(newName, updatedAsset.getName());
        assertEquals(testAsset.getId(), updatedAsset.getId());
        assertEquals(testAsset.getCfr(), updatedAsset.getCfr());
        assertNotEquals(testAsset.getHistoryId(), updatedAsset.getHistoryId());
    }

    @Test
    public void assetListQueryHistoryGuidTest() {
        AssetDTO asset1 = AssetTestHelper.createTestAsset();

        AssetDTO asset2 = AssetTestHelper.getAssetByGuid(asset1.getId());
        asset2.setName(asset2.getName() + "1");
        asset2 = AssetTestHelper.updateAsset(asset2);

        AssetDTO asset3 = AssetTestHelper.getAssetByGuid(asset2.getId());
        asset3.setName(asset3.getName() + "2");
        asset3 = AssetTestHelper.updateAsset(asset3);

        AssetQuery assetQuery1 = AssetTestHelper.getBasicAssetQuery();
        assetQuery1.setHistoryId(Collections.singletonList(asset1.getHistoryId()));

        AssetListResponse assetHistory1 = AssetTestHelper.assetListQuery(assetQuery1);
        List<AssetDTO> assets = assetHistory1.getAssetList();
        assertEquals(1, assets.size());
        assertEquals(asset1.getId(), assets.get(0).getId());

        AssetQuery assetQuery2 = AssetTestHelper.getBasicAssetQuery();
        assetQuery2.setHistoryId(Collections.singletonList(asset2.getHistoryId()));

        AssetListResponse assetHistory2 = AssetTestHelper.assetListQuery(assetQuery2);
        List<AssetDTO> assets2 = assetHistory2.getAssetList();
        assertEquals(1, assets2.size());
        assertEquals(asset2.getId(), assets2.get(0).getId());
        assertEquals(asset2.getName(), assets2.get(0).getName());

        AssetQuery assetQuery3 = AssetTestHelper.getBasicAssetQuery();
        assetQuery3.setHistoryId(Collections.singletonList(asset3.getHistoryId()));

        AssetListResponse assetHistory3 = AssetTestHelper.assetListQuery(assetQuery3);
        List<AssetDTO> assets3 = assetHistory3.getAssetList();
        assertEquals(1, assets3.size());
        assertEquals(asset3.getId(), assets3.get(0).getId());
        assertEquals(asset3.getName(), assets3.get(0).getName());
    }

    @Test
    public void assetListQueryMultipleHistoryGuidTest() {
        AssetDTO asset1 = AssetTestHelper.createTestAsset();

        AssetDTO asset2 = AssetTestHelper.getAssetByGuid(asset1.getId());
        asset2.setName(asset2.getName() + "1");
        AssetDTO createdAsset2 = AssetTestHelper.updateAsset(asset2);

        AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
        assetQuery.setHistoryId(Arrays.asList(asset1.getHistoryId(), createdAsset2.getHistoryId()));

        AssetListResponse assetHistory = AssetTestHelper.assetListQuery(assetQuery);
        List<AssetDTO> assets = assetHistory.getAssetList();
        assertEquals(2, assets.size());
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset1.getId()) && a.getName().equals(asset1.getName())));
        assertTrue(assets.stream().anyMatch(a -> a.getId().equals(createdAsset2.getId()) && a.getName().equals(asset2.getName())));
    }

    @Test
    public void addContactToAsset() {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        ContactInfo contact = new ContactInfo();
        contact.setName("Test contact");
        contact.setEmail("test@mail.com");
        contact.setPhoneNumber("123-456789");
        ContactInfo createdContact = AssetTestHelper.createContactInfoForAsset(asset, contact);

        assertNotNull(createdContact.getId());
    }

    @Test
    public void addNoteToAsset() {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        Note note = new Note();
        note.setNote("apa");
        note.setCreatedBy("Tester");

        Note createdNote = AssetTestHelper.createNoteForAsset(asset, note);

        assertNotNull(createdNote.getId());
    }

    @Test
    public void addNoteToAssetUsingTimestamp() {
        AssetDTO asset = AssetTestHelper.createTestAsset();

        String input = "{\"id\":null,\"assetId\":\"09ec5b78-a96f-47ea-8a01-6bc876a085fc\",\"createdOn\":\"1575545948\",\"note\":\"apa\"}";
        input = input.replace("09ec5b78-a96f-47ea-8a01-6bc876a085fc", asset.getId().toString());

        Note createdNote = AssetTestHelper.createNoteForAsset(input);

        assertNotNull(createdNote.getId());
    }

    @Test
    public void testIfUserCanCreateAnAsset() {
        try {
            AssetDTO anAsset = AssetTestHelper.createBasicAsset();
            AssetTestHelper.createAsset(anAsset, "usm_user", "password");
            fail("this must not occur");
        } catch (ForbiddenException e) {
            assertTrue("logged on but have not that feature", true);
        }
    }

    @Test
    public void testIfUserCanCreateAnAssetOkUser() {
        try {
            AssetDTO anAsset = AssetTestHelper.createBasicAsset();
            anAsset = AssetTestHelper.createAsset(anAsset);
            assertTrue(true); // this must not occur
        } catch (ForbiddenException e) {
            // logged on but have not that feature
            assertFalse(true);
        }
    }

	@Test(timeout = 10000)
	public void assetSseTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();

		List<String> assets = new ArrayList<>();
		try (SseEventSource source = AssetTestHelper.getSseStream()) {
			source.register((inboundSseEvent) -> {
				if (inboundSseEvent.getComment() != null && inboundSseEvent.getComment().equals("Updated Asset")) {
					assets.add(inboundSseEvent.readData());
				}
			});
			source.open();

			testAsset.setName("new test name");
			testAsset = AssetTestHelper.updateAsset(testAsset);
			Thread.sleep(50);
			testAsset.setFlagStateCode("UNK");
			testAsset = AssetTestHelper.updateAsset(testAsset);
			Thread.sleep(50);
			testAsset.setLengthOverAll(42d);
			testAsset = AssetTestHelper.updateAsset(testAsset);
			Thread.sleep(50);

			while(assets.size() < 3) {
				Thread.sleep(100);
			}
		}
		assertThat(assets.size(), CoreMatchers.is(3));
	}
}
