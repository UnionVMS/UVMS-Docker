package eu.europa.ec.fisheries.uvms.docker.validation.webgateway;

import eu.europa.ec.fisheries.schema.mobileterminal.polltypes.v1.PollType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.Note;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.CreatePollResultDto;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.webgateway.dto.PollInfoDto;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Map;

public class PollIT extends AbstractRest {

    @Test
    public void getPollsForAsset() {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        CreatePollResultDto resultDto = MobileTerminalTestHelper.createPoll_Helper(testAsset, PollType.MANUAL_POLL);
        assertNotNull(resultDto);
        assertEquals(1, resultDto.getSentPolls().size());

        Response response = getWebTarget()
                .path("web-gateway/rest/poll/pollsForAsset")
                .path(testAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get( Response.class);
        assertEquals(200, response.getStatus());

        Map<String, PollInfoDto> pollInfoMap = response.readEntity(new GenericType<Map<String, PollInfoDto>>() {});

        assertNotNull(pollInfoMap);
        assertFalse(pollInfoMap.isEmpty());

        PollInfoDto pollInfo = pollInfoMap.get(resultDto.getSentPolls().get(0));
        assertNotNull(pollInfo.getPollInfo());
        assertNotNull(pollInfo.getPollStatus());
        assertNotNull(pollInfo.getMobileTerminalSnapshot());

        assertEquals(pollInfo.getPollInfo().getMobileterminalId() , pollInfo.getMobileTerminalSnapshot().getId());
        assertEquals(pollInfo.getMobileTerminalSnapshot().getAssetId(), testAsset.getId().toString());
        assertFalse(pollInfo.getMobileTerminalSnapshot().getChannels().isEmpty());


        assertEquals(testAsset.getId(), pollInfo.getPollInfo().getAssetId());
        assertEquals(pollInfoMap.get(pollInfo.getPollInfo().getId().toString()), pollInfo);

    }

    @Test
    public void getPollsForAssetTwoPolls() {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();
        MobileTerminalDto createdTerminal = MobileTerminalTestHelper.createMobileTerminal();
        CreatePollResultDto resultDto = MobileTerminalTestHelper.createPollWithMT_Helper(testAsset, PollType.MANUAL_POLL, createdTerminal);
        assertNotNull(resultDto);
        assertEquals(1, resultDto.getSentPolls().size());

        MobileTerminalTestHelper.unAssignMobileTerminal(testAsset, createdTerminal);

        resultDto = MobileTerminalTestHelper.createPollWithMT_Helper(testAsset, PollType.MANUAL_POLL, createdTerminal);
        assertNotNull(resultDto);
        assertEquals(1, resultDto.getSentPolls().size());

        Response response = getWebTarget()
                .path("web-gateway/rest/poll/pollsForAsset")
                .path(testAsset.getId().toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get( Response.class);
        assertEquals(200, response.getStatus());

        Map<String, PollInfoDto> pollInfoMap = response.readEntity(new GenericType<Map<String, PollInfoDto>>() {});

        assertNotNull(pollInfoMap);
        assertEquals(2, pollInfoMap.size());

        assertTrue(pollInfoMap.values().stream().allMatch(poll -> poll.getPollInfo().getAssetId().equals(testAsset.getId())));
    }
}
