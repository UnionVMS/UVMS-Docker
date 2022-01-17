package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.*;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.json.JsonObject;

public class AssetFilterRestIT extends AbstractRest {

    @Test
    public void createAssetFilterTest() {
        String name = "created Name";
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();
        createdAssetFilter.setName(name);
        AssetFilterQueryDto createdAssetFilterQuery = AssetFilterTestHelper
                .createBasicAssetFilterQuery(createdAssetFilter);
        AssetFilterValueDto createdAssetFilterValue = AssetFilterTestHelper
                .createBasicAssetFilterValue(createdAssetFilterQuery);

        Set<AssetFilterValueDto> setOfAssetFilteVAlues = new HashSet<AssetFilterValueDto>();
        setOfAssetFilteVAlues.add(createdAssetFilterValue);
        createdAssetFilterQuery.setValues(setOfAssetFilteVAlues);
        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        assertTrue(createdAssetFilterResponse.getName().equals(name));
    }

    @Test
    public void createAssetFilterFromJsonTest() {
        String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";
        String assetFilterCreateResp = AssetFilterTestHelper.createAssetFilterFromJson(afjson);
        assertTrue(assetFilterCreateResp.contains("operator 2 test"));
        assertTrue(assetFilterCreateResp.contains("23"));
        assertTrue(assetFilterCreateResp.contains("dsad"));

        AssetFilterDto assetFilterResponseDto = JSONB.fromJson(assetFilterCreateResp,
                AssetFilterDto.class);
        AssetFilterQueryRestDto filter = AssetFilterTestHelper.deserializeFilter(assetFilterResponseDto.getFilter().get(0));
        Double value = filter.getValues().get(0).getValue();
        assertTrue(value == 23d);
        String op = filter.getValues().get(0).getOperator();
        assertTrue(op.equals("operator 2 test"));
    }

    @Test
    public void updateAssetFilterTest() {
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();

        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        String filterId = createdAssetFilterResponse.getId().toString();

        String assetFilterToUpdate = "{\"name\":\"båtar2\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";

        String updatedAssetFilterString = AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate);

        assertTrue(updatedAssetFilterString.contains("båtar2"));
        assertTrue(updatedAssetFilterString.contains("42"));
        assertTrue(updatedAssetFilterString.contains("operator update test"));
    }
    
    @Test
    public void updateAssetFilterWithMultipleQueriesTest() {
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();

        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        String filterId = createdAssetFilterResponse.getId().toString();

        String assetFilterToUpdate = "{\"name\":\"båtar2\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate);
        String updatedAssetFilterString = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterDto updatedAssetFilter = JSONB.fromJson(updatedAssetFilterString,
                AssetFilterDto.class);
        
        assertTrue(updatedAssetFilter.getId().equals(filterId));
        assertTrue(updatedAssetFilter.getName().equals("båtar2"));
        
        String assetFilterToUpdate2 = "{\"name\":\"båtar3\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"},"
                + "{\"values\":[{\"value\":42, \"operator\":\"operator update test2\"}],"
                + "\"type\": \"test2\", \"inverse\": true,\"valueType\": \"NUMBER\"}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate2);
        String updatedAssetFilterString2 = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterDto assetFilterResponseDto2 = JSONB.fromJson(updatedAssetFilterString2,
                AssetFilterDto.class);
        
        assertTrue(updatedAssetFilter.getId().equals(assetFilterResponseDto2.getId()));
        assertFalse(updatedAssetFilter.getName().equals(assetFilterResponseDto2.getName()));
        
        List<JsonObject> assetFilterQueryRestDto = assetFilterResponseDto2.getFilter();
        AssetFilterQueryRestDto assetFilterQueryRestDto0 = AssetFilterTestHelper.deserializeFilter(assetFilterQueryRestDto.get(0));
        AssetFilterQueryRestDto assetFilterQueryRestDto1 = AssetFilterTestHelper.deserializeFilter(assetFilterQueryRestDto.get(1));
        
        assertFalse( assetFilterQueryRestDto0.getInverse() == assetFilterQueryRestDto1.getInverse() );
        assertTrue( assetFilterQueryRestDto0.getValueType() == assetFilterQueryRestDto1.getValueType());
        
        
    }
    @Test
    public void updateAssetFilterWithMultipleValueTest() {
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();

        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        String filterId = createdAssetFilterResponse.getId().toString();

        String assetFilterToUpdate = "{\"name\":\"testValue\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"test\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate);
        String updatedAssetFilterString = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterDto updatedAssetFilter = JSONB.fromJson(updatedAssetFilterString,
                AssetFilterDto.class);
        
        AssetFilterValueRestTestDto assetFilterValueRestTestDtoUpdatedFirst = AssetFilterTestHelper.deserializeFilter(updatedAssetFilter.getFilter().get(0)).getValues().get(0);
        
        assertTrue(updatedAssetFilter.getId().equals(filterId));
        assertTrue(updatedAssetFilter.getName().equals("testValue"));
        
        String assetFilterToUpdate2 = "{\"name\":\"testValue2\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"},"
                + "{\"value\":41, \"operator\":\"operator update test\"}],"
                + "\"type\": \"test\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate2);
        String updatedAssetFilterString2 = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterDto assetFilterResponseDto2 = JSONB.fromJson(updatedAssetFilterString2,
                AssetFilterDto.class);
        
        assertTrue(updatedAssetFilter.getId().equals(assetFilterResponseDto2.getId()));
        assertFalse(updatedAssetFilter.getName().equals(assetFilterResponseDto2.getName()));
        
        List<JsonObject> assetFilterQueryRestDto = assetFilterResponseDto2.getFilter();
        AssetFilterQueryRestDto assetFilterQueryRestDto0 = AssetFilterTestHelper.deserializeFilter(assetFilterQueryRestDto.get(0));
        List<AssetFilterValueRestTestDto> assetFilterValueList = assetFilterQueryRestDto0.getValues();
        AssetFilterValueRestTestDto assetFilterValueRestTestDto0 = assetFilterValueList.get(0);
        AssetFilterValueRestTestDto assetFilterValueRestTestDto1 = assetFilterValueList.get(1);
        
        assertFalse( assetFilterValueRestTestDto0.getValue()== assetFilterValueRestTestDto1.getValue() );
        assertTrue( assetFilterValueRestTestDto0.getOperator().equals(assetFilterValueRestTestDto1.getOperator()) );
        
        assertTrue( assetFilterValueRestTestDtoUpdatedFirst.getOperator().equals(assetFilterValueRestTestDto0.getOperator()) );
        assertTrue( assetFilterValueRestTestDtoUpdatedFirst.getOperator().equals(assetFilterValueRestTestDto1.getOperator()) );
    }

    @Test
    public void deleteAssetFilterTest() {
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();

        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        String filterId = createdAssetFilterResponse.getId().toString();

        String assetFilterToUpdate = "{\"name\":\"båtar2\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";

        String updatedAssetFilterString = AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate);

        assertTrue(updatedAssetFilterString.contains(filterId));

        AssetFilterTestHelper.deleteAssetFilter(filterId);
        AssetFilterListDto assetNotFound = AssetFilterTestHelper.getAssetFilterList();
        assertFalse(assetNotFound.getSavedFilters().containsKey(filterId));
    }

    @Test
    public void getAssetFilterListTest() {
        String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";
        String assetFilterCreateResp1 = AssetFilterTestHelper.createAssetFilterFromJson(afjson);
        String assetFilterCreateResp2 = AssetFilterTestHelper.createAssetFilterFromJson(afjson);
        String assetFilterCreateResp3 = AssetFilterTestHelper.createAssetFilterFromJson(afjson);

        AssetFilterDto assetFilterResponseDto1 = JSONB.fromJson(assetFilterCreateResp1,
                AssetFilterDto.class);
        String id1 = assetFilterResponseDto1.getId();
        AssetFilterDto assetFilterResponseDto2 = JSONB.fromJson(assetFilterCreateResp2,
                AssetFilterDto.class);
        String id2 = assetFilterResponseDto2.getId();
        AssetFilterDto assetFilterResponseDto3 = JSONB.fromJson(assetFilterCreateResp3,
                AssetFilterDto.class);
        String id3 = assetFilterResponseDto3.getId();

        AssetFilterListDto assetFilterList = AssetFilterTestHelper.getAssetFilterList();
        assertTrue(assetFilterList.getSavedFilters().containsKey(id1));
        assertTrue(assetFilterList.getSavedFilters().containsKey(id2));
        assertTrue(assetFilterList.getSavedFilters().containsKey(id3));
    }

    @Test
    public void getAssetFilterByIdTest() {
        String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"valueType\": \"NUMBER\"}] }";
        String assetFilterCreateResp = AssetFilterTestHelper.createAssetFilterFromJson(afjson);

        AssetFilterDto assetFilterResponseDto = JSONB.fromJson(assetFilterCreateResp,
                AssetFilterDto.class);
        String id = assetFilterResponseDto.getId();

        String assetFilter = AssetFilterTestHelper.getAssetFilterByGuid(id);
        AssetFilterDto assetFilterRespFromJson = JSONB.fromJson(assetFilter, AssetFilterDto.class);

        assertTrue(assetFilterRespFromJson.getId().contentEquals(id));
    }
}
