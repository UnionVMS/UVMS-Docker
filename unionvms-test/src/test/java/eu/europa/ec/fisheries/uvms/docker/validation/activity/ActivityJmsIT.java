package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import com.google.common.collect.Lists;
import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.*;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FluxReportIdentifierDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetQuery;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.PaginatedResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetJMSHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import un.unece.uncefact.data.standard.fluxfareportmessage._3.FLUXFAReportMessage;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FAReportDocument;
import un.unece.uncefact.data.standard.reusableaggregatebusinessinformationentity._20.FLUXReportDocument;
import un.unece.uncefact.data.standard.unqualifieddatatype._20.IDType;

import javax.jms.JMSException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

public class ActivityJmsIT extends AbstractRest {

	private static final String REST_USER_ID = "rep_power";
	private static final String REST_USER_PASSWORD = "abcd-1234";
	private static final String SCOPE_NAME = "EC";
	private static final String ROLE_NAME = "rep_power_role";

	private static MessageHelper messageHelper;

	private static AssetJMSHelper assetJMSHelper;

	private static MovementHelper movementHelper;

	@BeforeClass
	public static void setup() throws JMSException {
		messageHelper = new MessageHelper();
		assetJMSHelper = new AssetJMSHelper();
		movementHelper = new MovementHelper();
	}

	@AfterClass
	public static void cleanup() {
		messageHelper.close();
	}

	@Test(timeout = 15000)
	public void createFishingReport() throws Exception {
		Asset asset = createShipAsset();

		// Wait for asset to be persisted
		Thread.sleep(1000);

		AssetDTO assetDTO = getAssetDto(asset);
		createPositionForShipAsset(assetDTO);

		String fluxfaReportMessageString = getFluxfaReportMessageFromFile("faReportMessage.xml");

		String newReportId = UUID.randomUUID().toString();

		FLUXFAReportMessage fluxfaReportMessage = updateReportId(fluxfaReportMessageString, newReportId);

		String reportMessageRequest = createReportMessage(fluxfaReportMessage);

		messageHelper.sendMessage("UVMSActivityEvent", reportMessageRequest);

		// Wait for activity to be persisted
		Thread.sleep(2000);

		FishingActivityReportDTO latestReport = getLatestFishingActivityReport();

		List<FluxReportIdentifierDTO> uniqueFAReportIdList = latestReport.getUniqueFAReportId();
		FluxReportIdentifierDTO fluxReportIdentifierDTO = uniqueFAReportIdList.get(0);

		assertEquals(newReportId, fluxReportIdentifierDTO.getFluxReportId());
	}

	private FishingActivityReportDTO getLatestFishingActivityReport() {
		FishingActivityQuery listFishingTripsRequests = new FishingActivityQuery();

		Map<SearchFilter,List<String>> searchCriteriaMapMultipleValue = new HashMap<>();
		List<String> purposeCodeList = new ArrayList<>();
		purposeCodeList.add("9");

		searchCriteriaMapMultipleValue.put(SearchFilter.PURPOSE, purposeCodeList);

		List<String> vesselNameList = new ArrayList<>();
		vesselNameList.add("Golf");
		searchCriteriaMapMultipleValue.put(SearchFilter.VESSEL_NAME, vesselNameList);

		listFishingTripsRequests.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValue);

		Response response = sendRestRequest("fa/list", listFishingTripsRequests);

		PaginatedResponse<FishingActivityReportDTO> paginatedResponse = response.readEntity(new GenericType<PaginatedResponse<FishingActivityReportDTO>>() {});

		List<FishingActivityReportDTO> resultList = paginatedResponse.getResultList();
		return resultList.get(resultList.size() - 1);
	}

	private Response sendRestRequest(String path, Object request) {
		return getWebTarget()
				.path("activity/rest/" + path)
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken(REST_USER_ID, REST_USER_PASSWORD))
				.header("roleName", ROLE_NAME)
				.header("scopeName", SCOPE_NAME)
				.post(Entity.entity(request, MediaType.APPLICATION_JSON));
	}

	private String createReportMessage(FLUXFAReportMessage fluxfaReportMessage) throws ActivityModelMarshallException {
		String updatedFluxFaReportMessageString = JAXBMarshaller.marshallJaxBObjectToString(fluxfaReportMessage);

		SetFLUXFAReportOrQueryMessageRequest request = new SetFLUXFAReportOrQueryMessageRequest();
		request.setMethod(ActivityModuleMethod.GET_FLUX_FA_REPORT);
		request.setRequest(updatedFluxFaReportMessageString);

		return JAXBMarshaller.marshallJaxBObjectToString(request);
	}

	private FLUXFAReportMessage updateReportId(String fluxfaReportMessageString, String newReportId) throws ActivityModelMarshallException {
		FLUXFAReportMessage fluxfaReportMessage = JAXBMarshaller.unmarshallTextMessage(fluxfaReportMessageString, FLUXFAReportMessage.class);
		FLUXReportDocument fluxReportDocument = fluxfaReportMessage.getFLUXReportDocument();
		List<IDType> ids = fluxReportDocument.getIDS();
		IDType idType = ids.get(0);
		idType.setValue(newReportId);

		List<FAReportDocument> faReportDocuments = fluxfaReportMessage.getFAReportDocuments();
		for (FAReportDocument faReportDocument : faReportDocuments) {
			FLUXReportDocument relatedFLUXReportDocument = faReportDocument.getRelatedFLUXReportDocument();
			List<IDType> relatedIds = relatedFLUXReportDocument.getIDS();
			IDType relatedIdType = relatedIds.get(0);
			relatedIdType.setValue(newReportId);
		}
		return fluxfaReportMessage;
	}

	private void createPositionForShipAsset(AssetDTO assetDTO) throws Exception {
		Instant locationTime = Instant.parse("2019-01-24T08:50:00Z");
		LatLong latLong = new LatLong(57.678440, 11.616953, Date.from(locationTime));

		IncomingMovement incomingMovement = movementHelper.createIncomingMovement(assetDTO, latLong);
		movementHelper.createMovementDontWaitForResponse(assetDTO, incomingMovement);
	}

	private AssetDTO getAssetDto(Asset asset) {
		AssetQuery assetQuery = new AssetQuery();
		assetQuery.setCfr(Lists.newArrayList(asset.getCfr()));
		assetQuery.setExternalMarking(Lists.newArrayList(asset.getExternalMarking()));
		assetQuery.setIrcs(Lists.newArrayList(asset.getIrcs()));

		AssetListResponse assetListResponse = AssetTestHelper.assetListQuery(assetQuery);
		List<AssetDTO> assetList = assetListResponse.getAssetList();
		return assetList.get(0);
	}

	private Asset createShipAsset() throws Exception {
		AssetGroup basicAssetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(basicAssetGroup);

		Asset asset = assetJMSHelper.createDummyAsset(AssetIdType.CFR);
		asset.setCfr("CYP123456789");
		asset.setExternalMarking("XR006");
		asset.setIrcs("IRCS6");

		assetJMSHelper.upsertAsset(asset, createdAssetGroup.getOwner());
		return asset;
	}

	private String getFluxfaReportMessageFromFile(String filename) throws IOException, URISyntaxException, ActivityModelMarshallException {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(filename);
		URI uri = resource.toURI();
		byte[] bytes = Files.readAllBytes(Paths.get(uri));
		return new String(bytes);
	}
}
