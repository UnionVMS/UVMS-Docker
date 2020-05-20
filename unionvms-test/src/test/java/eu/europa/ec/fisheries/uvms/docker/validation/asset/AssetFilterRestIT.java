package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterDto;
import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterQueryDto;
import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterValueDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class AssetFilterRestIT extends AbstractRest{
	
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
	    
	    AssetFilterTestHelper.deleteAssetFilter(createdAssetFilter1);
	    AssetFilterTestHelper.deleteAssetFilter(createdAssetFilter2);
	    AssetFilterTestHelper.deleteAssetFilter(createdAssetFilter3);
	}
	
	@Test
	public void createAssetFilterTest() {
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
		
		AssetFilterTestHelper.deleteAssetFilter(createdAssetFilter);
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
		
		AssetFilterTestHelper.deleteAssetFilter(createdAssetFilter);
	}
	
	@Test
	public void deleteAssetFilterTest() {
		AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createAssetFilterWithQueryAndValuesDefault();
		assertTrue(createdAssetFilter.getId() != null);
		
		AssetFilterTestHelper.deleteAssetFilter(createdAssetFilter);
		
		assertFalse(AssetFilterTestHelper.getAssetFilterByGuid(createdAssetFilter.getId()).getId().equals(createdAssetFilter.getId()));
	}
}
