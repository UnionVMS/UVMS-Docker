package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQueryWithStringMaps;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginatedResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;

public class ActivityTestHelper extends AbstractHelper {

    private ActivityTestHelper() {}

    public static List<FishingActivityReportDTO> getFaList(FishingActivityQueryWithStringMaps query) {
        PaginatedResponse<FishingActivityReportDTO> response = getWebTarget()
            .path("activity/rest/fa/list")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken("vms_admin_com", "password"))
            .header("roleName", "AdminAll")
            .header("scopeName", "All Reports")
            .post(Entity.json(query), new GenericType<PaginatedResponse<FishingActivityReportDTO>>() {});
        return response.getResultList();

    }
}
