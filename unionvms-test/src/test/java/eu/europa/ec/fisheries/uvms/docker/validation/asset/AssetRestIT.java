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

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;
import com.fasterxml.jackson.databind.ser.std.ArraySerializerBase;
import eu.europa.ec.fisheries.uvms.asset.client.model.Asset;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.asset.client.model.ContactInfo;
import eu.europa.ec.fisheries.uvms.asset.client.model.Note;
import eu.europa.ec.fisheries.uvms.asset.model.constants.AuditOperationEnum;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListCriteriaPair;
import eu.europa.ec.fisheries.wsdl.asset.types.ConfigSearchField;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

/**
 * The Class AssetRestIT.
 */
public class AssetRestIT extends AbstractRestServiceTest {

	/**
	 * Gets the asset list test.
	 *
	 * @return the asset list test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetListTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		
		AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
		assetQuery.setFlagState(Arrays.asList(asset.getFlagStateCode()));
		
		AssetListResponse assetListResponse = AssetTestHelper.assetListQuery(assetQuery);
		List<Asset> assets = assetListResponse.getAssetList();
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset.getId())));
	}
	
	@Test
	public void getAssetListMultipleAssetsGuidsTest() throws Exception {
		Asset asset1 = AssetTestHelper.createTestAsset();
		Asset asset2 = AssetTestHelper.createTestAsset();
		
		AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
		assetQuery.setId(Arrays.asList(asset1.getId(), asset2.getId()));
		
		AssetListResponse assetListResponse = AssetTestHelper.assetListQuery(assetQuery);
		List<Asset> assets = assetListResponse.getAssetList();
		assertEquals(2, assets.size());
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset1.getId())));
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset2.getId())));
	}

	@Test
	public void getAssetListItemCountTest() throws Exception {
	    AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
	    assetQuery.setFlagState(Arrays.asList("SWE"));
		
		Integer countBefore = AssetTestHelper.assetListQueryCount(assetQuery);
		
		// Add new asset
		Asset asset = AssetTestHelper.createBasicAsset();
		asset.setFlagStateCode("SWE");
		AssetTestHelper.createAsset(asset);
		
		Integer countAfter = AssetTestHelper.assetListQueryCount(assetQuery);
		assertEquals(Integer.valueOf(countBefore + 1), countAfter);
	}
	
	@Test
	public void getAssetListUpdatedIRCSNotFoundTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		
		AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
		assetQuery.setIrcs(Arrays.asList(testAsset.getIrcs()));
		
		AssetListResponse assetList = AssetTestHelper.assetListQuery(assetQuery);
		List<Asset> assets = assetList.getAssetList();
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(testAsset.getId())));
		
		testAsset.setIrcs("I" + AssetTestHelper.generateARandomStringWithMaxLength(7));
		AssetTestHelper.updateAsset(testAsset);
		
		// Search with same query, the asset should not be found
		assetList = AssetTestHelper.assetListQuery(assetQuery);
		assets = assetList.getAssetList();
		assertFalse(assets.stream().anyMatch(a -> a.getId().equals(testAsset.getId())));
	}

	@Test
	public void getAssetListWithLikeSearchValue() throws Exception {
		Asset asset = AssetTestHelper.createBasicAsset();
		asset.setPortOfRegistration("MyHomePort");
		Asset createdAsset = AssetTestHelper.createAsset(asset);
		
		AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
		assetQuery.setPortOfRegistration(Arrays.asList("My*"));
		
		AssetListResponse assetList = AssetTestHelper.assetListQuery(assetQuery);
		List<Asset> assets = assetList.getAssetList();
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));
	}
	
	/**
	 * Gets the asset by id test.
	 *
	 * @return the asset by id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getAssetByIdTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		Asset assetByGuid = AssetTestHelper.getAssetByGuid(asset.getId());
		assertEquals(asset.getId(), assetByGuid.getId());
	}

	/**
	 * Creates the asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createAssetTest() throws Exception {
		AssetTestHelper.createTestAsset();
	}

	@Test
	public void createAssetAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		Asset asset = AssetTestHelper.createTestAsset();
		AssetTestHelper.assertAssetAuditLogCreated(asset.getId(), AuditOperationEnum.CREATE, fromDate);
	}
	

	@Test
	public void updateAssetAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		Asset testAsset = AssetTestHelper.createTestAsset();
		String newName = testAsset.getName() + "Changed";
		testAsset.setName(newName);
		AssetTestHelper.updateAsset(testAsset);
		
		AssetTestHelper.assertAssetAuditLogCreated(testAsset.getId(), AuditOperationEnum.UPDATE, fromDate);
	}

	/**
	 * Archive asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void archiveAssetTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		testAsset.setActive(false);
		AssetTestHelper.archiveAsset(testAsset);
	}

	@Test
	public void archiveAssetAuditLogCreatedTest() throws Exception {
		Date fromDate = DateUtils.getNowDateUTC();
		Asset testAsset = AssetTestHelper.createTestAsset();
		testAsset.setActive(false);
		AssetTestHelper.archiveAsset(testAsset);
		
		AssetTestHelper.assertAssetAuditLogCreated(testAsset.getId(), AuditOperationEnum.ARCHIVE, fromDate);
	}
	
	/**
	 * Asset list group by flag state test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Ignore // Removed resource?
	@Test
	public void assetListGroupByFlagStateTest() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		ArrayList<String> assetIdList = new ArrayList<String>();
		assetIdList.add(asset.getId().toString());

		final HttpResponse response = Request.Post(getBaseUrl() + "asset/rest/asset/listGroupByFlagState")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(assetIdList).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}


	@Test
	public void getAssetListWithLikeSearchValue_ICCAT_AND_UVI_GFCM() throws Exception {
		Asset asset = AssetTestHelper.createBasicAsset();

		String theValue = UUID.randomUUID().toString();
		asset.setIccat(theValue);
		asset.setUvi(theValue);
		asset.setGfcm(theValue);

		Asset createdAsset = AssetTestHelper.createAsset(asset);

		AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();

		assetQuery.setIccat(Arrays.asList(theValue));
		assetQuery.setUvi(Arrays.asList(theValue));
		assetQuery.setGfcm(Arrays.asList(theValue));

		AssetListResponse assetList = AssetTestHelper.assetListQuery(assetQuery);
		List<Asset> assets = assetList.getAssetList();
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(createdAsset.getId())));
	}

	@Ignore // TODO check what happens when list doesn't find any asset
	@Test
	public void getAssetListWithLikeSearchValue_CHANGE_KEY_AND_FAIL() throws Exception {
		Asset asset = AssetTestHelper.createTestAsset();
		UUID guid = asset.getId();

		String oldIccat = asset.getIccat();
		String theValue = UUID.randomUUID().toString();
		String newName = asset.getName() + "Changed";
		asset.setName(newName);
		asset.setIccat(theValue);
		AssetTestHelper.updateAsset(asset);

		AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
		assetQuery.setIccat(Arrays.asList(theValue));
		assetQuery.setId(Arrays.asList(guid));

		AssetListResponse listAssetResponse = AssetTestHelper.assetListQuery(assetQuery);
		List<Asset> fetchedAsssets = listAssetResponse.getAssetList();
		assertFalse(fetchedAsssets.stream().anyMatch(a -> a.getId().equals(guid)));
	}


	/**
	 * Update asset test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void updateAssetTest() throws Exception {
		Asset testAsset = AssetTestHelper.createTestAsset();
		String newName = testAsset.getName() + "Changed";
		testAsset.setName(newName);
		Asset updatedAsset = AssetTestHelper.updateAsset(testAsset);
		assertEquals(newName, updatedAsset.getName());
		assertEquals(testAsset.getId(), updatedAsset.getId());
		assertEquals(testAsset.getCfr(), updatedAsset.getCfr());
		assertNotEquals(testAsset.getHistoryId(), updatedAsset.getHistoryId());
	}

	@Test
	public void assetListQueryHistoryGuidTest() throws Exception {
		// Create asset versions
		Asset asset1 = AssetTestHelper.createTestAsset();

		Asset asset2 = AssetTestHelper.getAssetByGuid(asset1.getId());
		asset2.setName(asset2.getName() + "1");
		asset2 = AssetTestHelper.updateAsset(asset2);

		Asset asset3 = AssetTestHelper.getAssetByGuid(asset2.getId());
		asset3.setName(asset3.getName() + "2");
		asset3 = AssetTestHelper.updateAsset(asset3);

		AssetQuery assetQuery1 = AssetTestHelper.getBasicAssetQuery();
		assetQuery1.setHistoryId(Arrays.asList(asset1.getHistoryId()));
		
		AssetListResponse assetHistory1 = AssetTestHelper.assetListQuery(assetQuery1);
		List<Asset> assets = assetHistory1.getAssetList();
		assertEquals(1, assets.size());
		assertEquals(asset1.getId(), assets.get(0).getId());
		
		AssetQuery assetQuery2 = AssetTestHelper.getBasicAssetQuery();
		assetQuery2.setHistoryId(Arrays.asList(asset2.getHistoryId()));
		
		AssetListResponse assetHistory2 = AssetTestHelper.assetListQuery(assetQuery2);
		List<Asset> assets2 = assetHistory2.getAssetList();
		assertEquals(1, assets2.size());
		assertEquals(asset2.getId(), assets2.get(0).getId());		
		assertEquals(asset2.getName(), assets2.get(0).getName());
		
		AssetQuery assetQuery3 = AssetTestHelper.getBasicAssetQuery();
		assetQuery3.setHistoryId(Arrays.asList(asset3.getHistoryId()));
		
		AssetListResponse assetHistory3 = AssetTestHelper.assetListQuery(assetQuery3);
		List<Asset> assets3 = assetHistory3.getAssetList();
		assertEquals(1, assets3.size());
		assertEquals(asset3.getId(), assets3.get(0).getId());
		assertEquals(asset3.getName(), assets3.get(0).getName());
	}
	
	@Test
	public void assetListQueryMultipleHistoryGuidTest() throws Exception {
		// Create asset versions
		Asset asset1 = AssetTestHelper.createTestAsset();

		Asset asset2 = AssetTestHelper.getAssetByGuid(asset1.getId());
		asset2.setName(asset2.getName() + "1");
		Asset createdAsset2 = AssetTestHelper.updateAsset(asset2);

		AssetQuery assetQuery = AssetTestHelper.getBasicAssetQuery();
		assetQuery.setHistoryId(Arrays.asList(asset1.getHistoryId(), createdAsset2.getHistoryId()));
		
		AssetListResponse assetHistory = AssetTestHelper.assetListQuery(assetQuery);
		List<Asset> assets = assetHistory.getAssetList();
		assertEquals(2, assets.size());
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(asset1.getId()) && a.getName().equals(asset1.getName())));	
		assertTrue(assets.stream().anyMatch(a -> a.getId().equals(createdAsset2.getId()) && a.getName().equals(asset2.getName())));
	}
	
	@Test
	public void addContactToAsset() throws Exception {
	    Asset asset = AssetTestHelper.createTestAsset();
	    
	    ContactInfo contact = new ContactInfo();
	    contact.setName("Test contact");
	    contact.setEmail("test@mail.com");
	    contact.setPhoneNumber("123-456789");
	    ContactInfo createdContact = AssetTestHelper.createContactInfoForAsset(asset, contact);

	    assertTrue(createdContact.getId() != null);
	}
	
	@Test
	public void addNoteToAsset() throws Exception {
	    Asset asset = AssetTestHelper.createTestAsset();

	    Note note = new Note();
	    note.setActivityCode("1");
	    note.setDate(LocalDateTime.now(ZoneOffset.UTC));
	    note.setNotes("apa");

	    
	    Note createdNote = AssetTestHelper.createNoteForAsset(asset, note);
	    
	    assertTrue(createdNote.getId() != null);
	}
}
