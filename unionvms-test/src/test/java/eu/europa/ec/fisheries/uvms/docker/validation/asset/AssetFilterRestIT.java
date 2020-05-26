package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.bind.Jsonb;

import org.junit.Before;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.AssetFilterDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.AssetFilterQueryDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.AssetFilterQueryRestDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.AssetFilterResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.AssetFilterValueDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.AssetFilterValueRestTestDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto.JsonBConf;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class AssetFilterRestIT extends AbstractRest {

    private Jsonb jsonb;

    @Before
    public void init() {
        jsonb = new JsonBConf().getContext(null);
    }

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
        createdAssetFilter.setOwner("ljkl");
        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        assertTrue(createdAssetFilterResponse.getName().equals(name));
    }

    @Test
    public void createAssetFilterFromJsonTest() {
        String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";
        String assetFilterCreateResp = AssetFilterTestHelper.createAssetFilterFromJson(afjson);
        assertTrue(assetFilterCreateResp.contains("operator 2 test"));
        assertTrue(assetFilterCreateResp.contains("23"));
        assertTrue(assetFilterCreateResp.contains("dsad"));

        AssetFilterResponseDto assetFilterResponseDto = jsonb.fromJson(assetFilterCreateResp,
                AssetFilterResponseDto.class);
        Double value = assetFilterResponseDto.getFilter().get(0).getValues().get(0).getValue();
        assertTrue(value == 23d);
        String op = assetFilterResponseDto.getFilter().get(0).getValues().get(0).getOperator();
        assertTrue(op.equals("operator 2 test"));
    }

    @Test
    public void updateAssetFilterTest() {
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();

        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        String filterId = createdAssetFilterResponse.getId().toString();

        String assetFilterToUpdate = "{\"name\":\"båtar2\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";

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
                + "\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate);
        String updatedAssetFilterString = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterResponseDto updatedAssetFilter = jsonb.fromJson(updatedAssetFilterString,
                AssetFilterResponseDto.class);
        
        assertTrue(updatedAssetFilter.getId().equals(filterId));
        assertTrue(updatedAssetFilter.getName().equals("båtar2"));
        
        String assetFilterToUpdate2 = "{\"name\":\"båtar3\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true},"
                + "{\"values\":[{\"value\":42, \"operator\":\"operator update test2\"}],"
                + "\"type\": \"test2\", \"inverse\": true,\"isNumber\": true}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate2);
        String updatedAssetFilterString2 = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterResponseDto assetFilterResponseDto2 = jsonb.fromJson(updatedAssetFilterString2,
                AssetFilterResponseDto.class);
        
        assertTrue(updatedAssetFilter.getId().equals(assetFilterResponseDto2.getId()));
        assertFalse(updatedAssetFilter.getName().equals(assetFilterResponseDto2.getName()));
        
        List<AssetFilterQueryRestDto> assetFilterQueryRestDto = assetFilterResponseDto2.getFilter();
        AssetFilterQueryRestDto assetFilterQueryRestDto0 = assetFilterQueryRestDto.get(0);
        AssetFilterQueryRestDto assetFilterQueryRestDto1 = assetFilterQueryRestDto.get(1);
        
        assertFalse( assetFilterQueryRestDto0.getInverse() == assetFilterQueryRestDto1.getInverse() );
        assertTrue( assetFilterQueryRestDto0.getIsNumber() == assetFilterQueryRestDto1.getIsNumber());
        
        
    }
    @Test
    public void updateAssetFilterWithMultipleValueTest() {
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();

        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        String filterId = createdAssetFilterResponse.getId().toString();

        String assetFilterToUpdate = "{\"name\":\"testValue\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"test\", \"inverse\": false,\"isNumber\": true}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate);
        String updatedAssetFilterString = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterResponseDto updatedAssetFilter = jsonb.fromJson(updatedAssetFilterString,
                AssetFilterResponseDto.class);
        
        AssetFilterValueRestTestDto assetFilterValueRestTestDtoUpdatedFirst = updatedAssetFilter.getFilter().get(0).getValues().get(0);
        
        assertTrue(updatedAssetFilter.getId().equals(filterId));
        assertTrue(updatedAssetFilter.getName().equals("båtar2"));
        
        String assetFilterToUpdate2 = "{\"name\":\"testValue2\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"},"
                + "{\"value\":41, \"operator\":\"operator update test\"}],"
                + "\"type\": \"test\", \"inverse\": false,\"isNumber\": true}] }";

        AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate2);
        String updatedAssetFilterString2 = AssetFilterTestHelper.getAssetFilterByGuid(filterId);
        AssetFilterResponseDto assetFilterResponseDto2 = jsonb.fromJson(updatedAssetFilterString2,
                AssetFilterResponseDto.class);
        
        assertTrue(updatedAssetFilter.getId().equals(assetFilterResponseDto2.getId()));
        assertFalse(updatedAssetFilter.getName().equals(assetFilterResponseDto2.getName()));
        
        List<AssetFilterQueryRestDto> assetFilterQueryRestDto = assetFilterResponseDto2.getFilter();
        AssetFilterQueryRestDto assetFilterQueryRestDto0 = assetFilterQueryRestDto.get(0);
        List<AssetFilterValueRestTestDto> assetFilterValueList = assetFilterQueryRestDto0.getValues();
        AssetFilterValueRestTestDto assetFilterValueRestTestDto0 = assetFilterValueList.get(0);
        AssetFilterValueRestTestDto assetFilterValueRestTestDto1 = assetFilterValueList.get(1);
        
        assertFalse( assetFilterValueRestTestDto0.getValue()== assetFilterValueRestTestDto1.getValue() );
        assertTrue( assetFilterValueRestTestDto0.getOperator() == assetFilterValueRestTestDto1.getOperator());
        
        assertTrue( assetFilterValueRestTestDtoUpdatedFirst.getOperator() == assetFilterValueRestTestDto0.getOperator());
        assertFalse( assetFilterValueRestTestDtoUpdatedFirst.getOperator() == assetFilterValueRestTestDto1.getOperator());
    }

    @Test
    public void deleteAssetFilterTest() {
        AssetFilterDto createdAssetFilter = AssetFilterTestHelper.createBasicAssetFilter();

        AssetFilterDto createdAssetFilterResponse = AssetFilterTestHelper.createAssetFilter(createdAssetFilter);

        String filterId = createdAssetFilterResponse.getId().toString();

        String assetFilterToUpdate = "{\"name\":\"båtar2\",\"id\":\"" + filterId
                + "\",\"filter\": [{\"values\":[{\"value\":42, \"operator\":\"operator update test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";

        String updatedAssetFilterString = AssetFilterTestHelper.updateAssetFilter(assetFilterToUpdate);

        assertTrue(updatedAssetFilterString.contains(filterId));

        AssetFilterTestHelper.deleteAssetFilter(filterId);
        String assetNotFound = AssetFilterTestHelper.getAssetFilterList();
        assertFalse(assetNotFound.contains(filterId));
    }

    @Test
    public void getAssetFilterListTest() {
        String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";
        String assetFilterCreateResp1 = AssetFilterTestHelper.createAssetFilterFromJson(afjson);
        String assetFilterCreateResp2 = AssetFilterTestHelper.createAssetFilterFromJson(afjson);
        String assetFilterCreateResp3 = AssetFilterTestHelper.createAssetFilterFromJson(afjson);

        AssetFilterResponseDto assetFilterResponseDto1 = jsonb.fromJson(assetFilterCreateResp1,
                AssetFilterResponseDto.class);
        String id1 = assetFilterResponseDto1.getId();
        AssetFilterResponseDto assetFilterResponseDto2 = jsonb.fromJson(assetFilterCreateResp2,
                AssetFilterResponseDto.class);
        String id2 = assetFilterResponseDto2.getId();
        AssetFilterResponseDto assetFilterResponseDto3 = jsonb.fromJson(assetFilterCreateResp3,
                AssetFilterResponseDto.class);
        String id3 = assetFilterResponseDto3.getId();

        String assetFilterList = AssetFilterTestHelper.getAssetFilterList();
        assertTrue(assetFilterList.contains(id1));
        assertTrue(assetFilterList.contains(id2));
        assertTrue(assetFilterList.contains(id3));
    }

    @Test
    public void getAssetFilterByIdTest() {
        String afjson = "{\"name\":\"båtar\",\"filter\": [{\"values\":[{\"value\":23, \"operator\":\"operator 2 test\"}],"
                + "\"type\": \"dsad\", \"inverse\": false,\"isNumber\": true}] }";
        String assetFilterCreateResp = AssetFilterTestHelper.createAssetFilterFromJson(afjson);

        AssetFilterResponseDto assetFilterResponseDto = jsonb.fromJson(assetFilterCreateResp,
                AssetFilterResponseDto.class);
        String id = assetFilterResponseDto.getId();

        String assetFilter = AssetFilterTestHelper.getAssetFilterByGuid(id);
        AssetFilterResponseDto assetFilterRespFromJson = jsonb.fromJson(assetFilter, AssetFilterResponseDto.class);

        assertTrue(assetFilterRespFromJson.getId().contentEquals(id));
    }
}
