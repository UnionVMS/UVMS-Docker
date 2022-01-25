package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQueryWithStringMaps;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

public class ActivityRestIT {

    @Test
    public void getFaList() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        FLUXFAReportMessage dep = ActivityMessageHelper.getDeparture(asset, new Random().nextLong(), Instant.now(), "SEGOT");
        ActivityJMSHelper.sendToActivity(dep);
        FishingActivityQueryWithStringMaps query = new FishingActivityQueryWithStringMaps();
        query.setSearchCriteriaMapMultipleValues(Map.of("PURPOSE", List.of("9", "5")));

        List<FishingActivityReportDTO> faList = ActivityTestHelper.getFaList(query);
        assertThat(faList.isEmpty(), is(false));

        Map<String, FishingActivityReportDTO> faMap = faList
                .stream()
                .collect(Collectors.toMap(FishingActivityReportDTO::getVesselId, Function.identity(), (e1, e2) -> e1));
        assertThat(faMap.containsKey(asset.getId().toString()), is(true));
        assertThat(faMap.get(asset.getId().toString()).getActivityType(), is("DEPARTURE"));
    }
}
