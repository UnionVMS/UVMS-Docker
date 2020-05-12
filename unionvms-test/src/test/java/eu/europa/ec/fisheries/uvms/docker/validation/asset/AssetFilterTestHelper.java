package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterDto;
import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterQueryDto;
import eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto.AssetFilterValueDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class AssetFilterTestHelper extends AbstractRest {
	
	/*  AssetFilterDto  */
	public static final String  assetFilterBaseUrl = "asset/rest/filter";
	
	public static AssetFilterDto createBasicAssetFilter() {
		String user = "test User";
		AssetFilterDto assetFilter = new AssetFilterDto();
		assetFilter.setOwner("vms_admin_se");
		assetFilter.setName(user);
		assetFilter.setUpdateTime(Instant.now());
		assetFilter.setUpdatedBy(user);
		return assetFilter;
	}
	
	public static AssetFilterQueryDto createBasicAssetFilterQuery(AssetFilterDto assetFilterDto) {
		AssetFilterQueryDto assetFilterQuery = new AssetFilterQueryDto(); 
		assetFilterQuery.setAssetFilter(assetFilterDto);
		assetFilterQuery.setInverse(false);
		assetFilterQuery.setIsNumber(false);
		assetFilterQuery.setType("GUID");
		return assetFilterQuery;
	}
	
	public static AssetFilterValueDto createBasicAssetFilterValue(AssetFilterQueryDto assetFilterQueryDto) {
		AssetFilterValueDto assetFilterValue = new AssetFilterValueDto(); 
		assetFilterValue.setAssetFilterQuery(assetFilterQueryDto);
		assetFilterValue.setOperator("<=");
		return assetFilterValue;
	}
	
	public static AssetFilterDto createAssetFilterWithQueryAndValuesDefault() {
		AssetFilterDto createdAssetFilter = createBasicAssetFilter();
	    AssetFilterQueryDto createdAssetFilterQuery = createBasicAssetFilterQuery(createdAssetFilter);
	    AssetFilterValueDto createdAssetFilterValue = createBasicAssetFilterValue(createdAssetFilterQuery);
	    
	    Set<AssetFilterValueDto> setOfAssetFilteVAlues = new HashSet<AssetFilterValueDto>();
	    setOfAssetFilteVAlues.add(createdAssetFilterValue);
	    createdAssetFilterQuery.setValues(setOfAssetFilteVAlues);
	    Set<AssetFilterQueryDto> setOfAssetFilteQueries = new HashSet<AssetFilterQueryDto>();
	    setOfAssetFilteQueries.add(createdAssetFilterQuery);
	    createdAssetFilter.setQueries(setOfAssetFilteQueries);
	    
	    createdAssetFilter.setQueries(setOfAssetFilteQueries);
	    createdAssetFilter = createAssetFilter(createdAssetFilter);
	    return createdAssetFilter;
	}
	
	/* AssetFilter Integration */
	
	public static AssetFilterDto getAssetFilterByGuid(UUID assetGuid) {
		return getWebTarget()
		        .path(assetFilterBaseUrl)
                .path(assetGuid.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(AssetFilterDto.class);
	}
	
	public static AssetFilterDto createAssetFilter(AssetFilterDto assetFilter) {
		return getWebTarget()
                .path(assetFilterBaseUrl)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(assetFilter), AssetFilterDto.class);
	}
	 
	public static AssetFilterQueryDto createAssetFilterQuery(AssetFilterDto assetFilterforQuery, AssetFilterQueryDto assetFilterQuery) {
		return getWebTarget()
                .path(assetFilterBaseUrl)
                .path(assetFilterforQuery.getId().toString())
                .path("query")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(assetFilterQuery), AssetFilterQueryDto.class);
	}
	 
	public static AssetFilterValueDto createAssetFilterValue(AssetFilterQueryDto assetFilterQueryForValue, AssetFilterValueDto assetFilterValue) {
		return getWebTarget()
				.path(assetFilterBaseUrl)
                .path(assetFilterQueryForValue.getId().toString())
                .path("value")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(assetFilterValue), AssetFilterValueDto.class);
	}
	
	public static AssetFilterDto updateAssetFilterValue(AssetFilterDto assetFilterDto) {
		return getWebTarget()
				.path(assetFilterBaseUrl)
                .path(assetFilterDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .put(Entity.json(assetFilterDto), AssetFilterDto.class);
	}
	
	public static AssetFilterDto deleteAssetFilter(AssetFilterDto assetFilterDto) {
		return getWebTarget()
				.path(assetFilterBaseUrl)
                .path(assetFilterDto.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .delete(AssetFilterDto.class);
	}
	
	public static String getAssetFilterList() {
		return getWebTarget()
				.path(assetFilterBaseUrl)
				.path("list")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(String.class);
	 }
	
}
