package eu.europa.ec.fisheries.uvms.docker.validation.streamcollector;

import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetJMSHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.uvms.docker.validation.streamcollector.dto.ReportOneRequestDto;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

public class ReportIT extends AbstractRest {

    private static MovementHelper movementHelper;
    private static AssetJMSHelper jmsHelper;

    @BeforeClass
    public static void setup() throws JMSException {
        movementHelper = new MovementHelper();
        jmsHelper = new AssetJMSHelper();
    }

    @Test
    public void getAssetListTest() throws Exception {
        AssetDTO testAsset = AssetTestHelper.createTestAsset();

        LatLong latLong = new LatLong(16.9, 32.6333333, new Date());
        IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, latLong);
        MovementDto createMovementResponse = movementHelper.createMovement(createMovementRequest);

        SearchBranch assetQuery = AssetTestHelper.getBasicAssetSearchBranch();
        assetQuery.addNewSearchLeaf(SearchFields.GUID, testAsset.getId().toString());

        ReportOneRequestDto report1 = new ReportOneRequestDto();
        report1.setSearchBranch(assetQuery);

        Response response = getWebTarget()
                .path("stream-collector/rest/reports/tracksByAssetSearch")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(report1), Response.class);
        assertEquals(200, response.getStatus());

        String returnString = response.readEntity(String.class);
        assertTrue(returnString.contains(testAsset.getId().toString()));
        assertTrue(returnString.contains(createMovementResponse.getMovementGUID()));
    }



}
