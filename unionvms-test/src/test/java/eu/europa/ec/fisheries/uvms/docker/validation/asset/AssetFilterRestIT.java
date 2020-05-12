package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterDto;
import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterQueryDto;
import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterValueDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class AssetFilterRestIT extends AbstractRest{
	
//	@Test
//	public void getAssetFilterListByUserTest() {
//	    AssetFilterDto createdAssetFilter = createAssetFilterWithQueryAndValuesDefault();
//	    AssetFilterQueryDto createdAssetFilterQuery = AssetFilterTestHelper.createBasicAssetFilterQuery(createdAssetFilter);
//	    AssetFilterValueDto createdAssetFilterValue = AssetFilterTestHelper.createBasicAssetFilterValue(createdAssetFilterQuery);
//
//		createdAssetFilter = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);
//		
//		createdAssetFilterQuery.setType("GUID");
//		createdAssetFilterQuery.setIsNumber(false);
//		createdAssetFilterValue.setValueString(createdAssetFilter.getId().toString());
//	    
//	    Set<AssetFilterValueDto> setOfAssetFilteVAlues = new HashSet<AssetFilterValueDto>();
//	    setOfAssetFilteVAlues.add(createdAssetFilterValue);
//	    createdAssetFilterQuery.setValues(setOfAssetFilteVAlues);
//	    Set<AssetFilterQueryDto> setOfAssetFilteQueries = new HashSet<AssetFilterQueryDto>();
//	    setOfAssetFilteQueries.add(createdAssetFilterQuery);
//	    createdAssetFilter.setQueries(setOfAssetFilteQueries);
//
//
//		createdAssetFilterQuery.setType("GUID");
//		createdAssetFilterQuery.setIsNumber(false);
//		createdAssetFilterValue.setValueString(createdAssetFilter.getId().toString());
//	
////		AssetGroupField assetGroupField2 = new AssetGroupField();
////		assetGroupField2.setField("GUID");
////		assetGroupField2.setValue(asset2.getId().toString());
////		assetGroupField2.setAssetGroup(createdAssetGroup);
////		AssetGroupField createdAssetGroupField2 = AssetTestHelper.createAssetGroupField(createdAssetGroup.getId(), assetGroupField2);
////	
////		List<AssetGroupField> fetchedAssetGroups = AssetTestHelper.getAssetGroupFieldByAssetGroup(createdAssetGroup.getId());
////		assertTrue(fetchedAssetGroups.stream().anyMatch(field -> field.getId().equals(createdAssetGroupField1.getId())));
////		assertTrue(fetchedAssetGroups.stream().anyMatch(field -> field.getId().equals(createdAssetGroupField2.getId())));
////		
////		List<AssetGroup> assetGroups = AssetTestHelper.getAssetGroupListByUser(createdAssetGroup.getOwner());
////
////		assertTrue(assetGroups.stream().anyMatch(group -> group.getId().equals(createdAssetGroup.getId())));
//	}
	
	@Test
	public void getAssetFilterListByUserTest() {
	    AssetFilterDto createdAssetFilter1 = AssetFilterTestHelper.createAssetFilterWithQueryAndValuesDefault();
	    AssetFilterDto createdAssetFilter2 = AssetFilterTestHelper.createAssetFilterWithQueryAndValuesDefault();
	    AssetFilterDto createdAssetFilter3 = AssetFilterTestHelper.createAssetFilterWithQueryAndValuesDefault();
	    
	    String assetFilters = AssetFilterTestHelper.getAssetFilterList();
	    
	    assertTrue(assetFilters.length() > 1);
	    assertTrue(assetFilters.contains(createdAssetFilter1.getId().toString()));
	    assertTrue(assetFilters.contains(createdAssetFilter2.getId().toString()));
	    assertTrue(assetFilters.contains(createdAssetFilter3.getId().toString()));
	}
	
	@Test
	public void createAssetGroupTest() {
		String name = "created Name";
		String owner = "Test owner";
		AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();
		createdAssetFilter.setName(name);
	    AssetFilterQueryDto createdAssetFilterQuery = AssetFilterTestHelper.createBasicAssetFilterQuery(createdAssetFilter);
	    AssetFilterValueDto createdAssetFilterValue = AssetFilterTestHelper.createBasicAssetFilterValue(createdAssetFilterQuery);
	    
	    Set<AssetFilterValueDto> setOfAssetFilteVAlues = new HashSet<AssetFilterValueDto>();
	    setOfAssetFilteVAlues.add(createdAssetFilterValue);
	    createdAssetFilterQuery.setValues(setOfAssetFilteVAlues);
	    Set<AssetFilterQueryDto> setOfAssetFilteQueries = new HashSet<AssetFilterQueryDto>();
	    setOfAssetFilteQueries.add(createdAssetFilterQuery);
	    createdAssetFilter.setQueries(setOfAssetFilteQueries);
	    
	    createdAssetFilter.setQueries(setOfAssetFilteQueries);
	    AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);
		
		assertTrue(createdAssetFilterResponse.getName().equals(name));
		assertTrue(createdAssetFilterResponse.getOwner().equals(owner));
	}
	
	@Test
	public void updateAssetGroupTest() {
		String uppdatedName = "uppdated Name";
		AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createAssetFilterWithQueryAndValuesDefault();
		createdAssetFilter.setName(uppdatedName);
		AssetFilterDto updatedAssetFilterDto = AssetFilterTestHelper.updateAssetFilterValue(createdAssetFilter);
		
		assertTrue(createdAssetFilter.getId().equals(updatedAssetFilterDto.getId()));
		assertTrue(createdAssetFilter.getOwner().equals(updatedAssetFilterDto.getOwner()));
		assertFalse(createdAssetFilter.getUpdateTime().equals(updatedAssetFilterDto.getUpdateTime()));
	}
	
	@Test
	public void deleteAssetFilterTest() {
		AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createAssetFilterWithQueryAndValuesDefault();
		AssetFilterTestHelper.deleteAssetFilter(createdAssetFilter);
		
		assertFalse(AssetFilterTestHelper.getAssetFilterByGuid(createdAssetFilter.getId()).getId().equals(createdAssetFilter.getId()));
	}
	

}
