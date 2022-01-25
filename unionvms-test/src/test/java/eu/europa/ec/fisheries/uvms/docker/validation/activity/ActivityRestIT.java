package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQueryWithStringMaps;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.date.DateUtils;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;

public class ActivityRestIT {

    @Test
    public void getFaList() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        FLUXFAReportMessage dep = ActivityMessageHelper.getDeparture(asset, new Random().nextLong(), Instant.now(), "SEGOT");
        ActivityJMSHelper.sendToActivity(dep);
        FishingActivityQueryWithStringMaps query = new FishingActivityQueryWithStringMaps();
        query.setSearchCriteriaMapMultipleValues(Map.of(SearchFilter.PURPOSE.toString(), List.of("9", "5")));

        List<FishingActivityReportDTO> faList = ActivityTestHelper.getFaList(query);
        assertThat(faList.isEmpty(), is(false));

        Map<String, FishingActivityReportDTO> faMap = faList
                .stream()
                .collect(Collectors.toMap(FishingActivityReportDTO::getVesselId, Function.identity(), (e1, e2) -> e1));
        assertThat(faMap.containsKey(asset.getId().toString()), is(true));
        assertThat(faMap.get(asset.getId().toString()).getActivityType(), is("DEPARTURE"));
    }

    @Test
    public void getFaListByVesselGuid() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        long tripId = new Random().nextLong();
        FLUXFAReportMessage dep = ActivityMessageHelper.getDeparture(asset, tripId, Instant.now().minus(3, ChronoUnit.HOURS), "SEGOT");
        ActivityJMSHelper.sendToActivity(dep);
        FLUXFAReportMessage rtp = ActivityMessageHelper.getArrival(asset, tripId, Instant.now().minus(2, ChronoUnit.HOURS), "SEGOT");
        ActivityJMSHelper.sendToActivity(rtp);
        FishingActivityQueryWithStringMaps query = new FishingActivityQueryWithStringMaps();
        query.setSearchCriteriaMap(Map.of(SearchFilter.VESSEL_GUIDS.toString(), asset.getId().toString()));
        query.setSearchCriteriaMapMultipleValues(Map.of(SearchFilter.PURPOSE.toString(), List.of("9", "5")));

        List<FishingActivityReportDTO> faList = ActivityTestHelper.getFaList(query);
        assertThat(faList.size(), is(2));
    }

    @Test
    public void getFaListByVesselGuidAndDate() throws Exception {
        AssetDTO asset = AssetTestHelper.createTestAsset();
        long tripId = new Random().nextLong();
        FLUXFAReportMessage departure = ActivityMessageHelper.getDeparture(asset, tripId, Instant.now().minus(3, ChronoUnit.HOURS), "SEGOT");
        ActivityJMSHelper.sendToActivity(departure);
        FLUXFAReportMessage arrival = ActivityMessageHelper.getArrival(asset, tripId, Instant.now().minus(1, ChronoUnit.HOURS), "SEGOT");
        ActivityJMSHelper.sendToActivity(arrival);
        FLUXFAReportMessage landing = ActivityMessageHelper.getLanding(asset, tripId, Instant.now(), "SEGOT");
        ActivityJMSHelper.sendToActivity(landing);
        FishingActivityQueryWithStringMaps query = new FishingActivityQueryWithStringMaps();
        String searchDate = DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_UI_FORMAT).format(Instant.now().minus(2, ChronoUnit.HOURS).atZone(ZoneId.of("UTC")));
        query.setSearchCriteriaMap(Map.of(SearchFilter.VESSEL_GUIDS.toString(), asset.getId().toString(),
                                          SearchFilter.PERIOD_START.toString(), searchDate));
        query.setSearchCriteriaMapMultipleValues(Map.of(SearchFilter.PURPOSE.toString(), List.of("9", "5")));

        List<FishingActivityReportDTO> faList = ActivityTestHelper.getFaList(query);
        assertThat(faList.size(), is(2));
        List<String> activityTypes = faList.stream().map(FishingActivityReportDTO::getActivityType).collect(Collectors.toList());
        assertThat(activityTypes.contains("ARRIVAL"), is(true));
        assertThat(activityTypes.contains("LANDING"), is(true));
    }
}
