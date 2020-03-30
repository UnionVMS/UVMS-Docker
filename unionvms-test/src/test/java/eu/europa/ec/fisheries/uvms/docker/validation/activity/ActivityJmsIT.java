package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import eu.europa.ec.fisheries.uvms.activity.model.exception.ActivityModelMarshallException;
import eu.europa.ec.fisheries.uvms.activity.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.ActivityModuleMethod;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SearchFilter;
import eu.europa.ec.fisheries.uvms.activity.model.schemas.SetFLUXFAReportOrQueryMessageRequest;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FishingActivityReportDTO;
import eu.europa.ec.fisheries.uvms.activity.service.dto.FluxReportIdentifierDTO;
import eu.europa.ec.fisheries.uvms.activity.service.search.FishingActivityQuery;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetGroup;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetListResponse;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;
import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchFields;
import eu.europa.ec.fisheries.uvms.commons.date.JsonBConfigurator;
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
import javax.json.bind.Jsonb;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	@Test(timeout = 30000)
	public void createFishingReport() throws Exception {
		Asset asset = createShipAsset();

		// Wait for asset to be persisted
		Thread.sleep(1000);

		AssetDTO assetDTO = getAssetDto(asset);
		createPositionForShipAsset(assetDTO);

		// Read XML report file to String
		String fluxfaReportMessageString = getFluxfaReportMessageFromFile("flux_fa_report_message_1_fishing_trip.xml");

		// Generate a new trip id and replace all instances of the existing trip id
		String newTripId = generateTripId();
		String fluxfaReportMessageStringWithRandomTripIds = replaceTripId(fluxfaReportMessageString, "placeholder-fishing-trip-id", newTripId);

		// Convert String into FLUXFAReportMessage
		FLUXFAReportMessage fluxfaReportMessage = convertToFLUXFAReportMessage(fluxfaReportMessageStringWithRandomTripIds);

		// Generate a new report ID for each FAReportDocument in the message and replace the existing ones with the new IDs
		List<String> generateReportIdList = generateReportIdList(fluxfaReportMessage);
		replaceFAReportDocumentIds(fluxfaReportMessage, generateReportIdList);

		// Generate a new FLUXReportDocument ID and replace the existing one
		String fluxDocumentReportId = generateReportId();
		replaceFluxReportDocumentId(fluxfaReportMessage, fluxDocumentReportId);

		String reportMessageRequest = createReportMessage(fluxfaReportMessage);

		messageHelper.sendMessage("UVMSActivityEvent", reportMessageRequest);

		List<FishingActivityReportDTO> fishingReportsForTripId = getFishingReportsForTripId(newTripId);

		int numberOfFishingActivityReports = fishingReportsForTripId.size();

		// Wait for reports to be persisted
		while (numberOfFishingActivityReports == 0) {
			Thread.sleep(500);
			fishingReportsForTripId = getFishingReportsForTripId(newTripId);
			numberOfFishingActivityReports = fishingReportsForTripId.size();
		}

		assertEquals(12, fishingReportsForTripId.size());

		for (FishingActivityReportDTO fishingActivityReportDTO : fishingReportsForTripId) {
			List<FluxReportIdentifierDTO> uniqueIdList = fishingActivityReportDTO.getUniqueFAReportId();
			FluxReportIdentifierDTO fluxReportIdentifierDTO = uniqueIdList.get(0);
			String fluxReportId = fluxReportIdentifierDTO.getFluxReportId();
			assertTrue(generateReportIdList.contains(fluxReportId));
		}
	}

	private String generateReportId() {
		return UUID.randomUUID().toString();
	}

	private String generateTripId() {
		return "SWE-TRP-" + UUID.randomUUID().toString();
	}

	private List<String> generateReportIdList(FLUXFAReportMessage fluxfaReportMessage) {
		List<FAReportDocument> faReportDocuments = fluxfaReportMessage.getFAReportDocuments();
		List<String> generatedReportIds = new ArrayList<>();
		for (int i = 0; i < faReportDocuments.size(); i++) {
			generatedReportIds.add(generateReportId());
		}
		return generatedReportIds;
	}

	private String replaceTripId(String report, String currentTripId, String newTripId) {
		return report.replace(currentTripId, newTripId);
	}

	private void replaceFluxReportDocumentId(FLUXFAReportMessage fluxfaReportMessage, String fluxReportDocumentId) {
		FLUXReportDocument fluxReportDocument = fluxfaReportMessage.getFLUXReportDocument();
		List<IDType> ids = fluxReportDocument.getIDS();
		IDType idType = ids.get(0);
		idType.setValue(fluxReportDocumentId);
	}

	private void replaceFAReportDocumentIds(FLUXFAReportMessage fluxfaReportMessage, List<String> reportDocumentIds) {
		List<FAReportDocument> faReportDocuments = fluxfaReportMessage.getFAReportDocuments();
		for (int i = 0; i < faReportDocuments.size(); i++) {
			FAReportDocument faReportDocument = faReportDocuments.get(i);
			String reportDocumentId = reportDocumentIds.get(i);

			FLUXReportDocument relatedFLUXReportDocument = faReportDocument.getRelatedFLUXReportDocument();
			List<IDType> relatedIds = relatedFLUXReportDocument.getIDS();
			IDType relatedIdType = relatedIds.get(0);
			relatedIdType.setValue(reportDocumentId);
		}
	}

	private List<FishingActivityReportDTO> getFishingReportsForTripId(String tripId) {
		FishingActivityQuery listFishingTripsRequests = new FishingActivityQuery();

		Map<SearchFilter,List<String>> searchCriteriaMapMultipleValue = new HashMap<>();
		List<String> purposeCodeList = new ArrayList<>();
		purposeCodeList.add("9");
		purposeCodeList.add("5");

		searchCriteriaMapMultipleValue.put(SearchFilter.PURPOSE, purposeCodeList);

		listFishingTripsRequests.setSearchCriteriaMapMultipleValues(searchCriteriaMapMultipleValue);

		Map<SearchFilter, String> searchCriteriaMap = new HashMap<>();
		searchCriteriaMap.put(SearchFilter.TRIP_ID, tripId);
		listFishingTripsRequests.setSearchCriteriaMap(searchCriteriaMap);

		Response response = sendRestRequest("fa/list", listFishingTripsRequests);

		String jsonString = response.readEntity(String.class);
		Jsonb jsonb = new JsonBConfigurator().getContext(null);
		PaginatedResponse<FishingActivityReportDTO> paginatedResponse = jsonb.fromJson(jsonString, new GenericType<PaginatedResponse<FishingActivityReportDTO>>(){}.getType());

		return paginatedResponse.getResultList();
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

	private FLUXFAReportMessage convertToFLUXFAReportMessage(String fluxfaReportMessageString) throws ActivityModelMarshallException {
		return JAXBMarshaller.unmarshallTextMessage(fluxfaReportMessageString, FLUXFAReportMessage.class);
	}

	private void createPositionForShipAsset(AssetDTO assetDTO) throws Exception {
		Instant locationTime = Instant.parse("2019-01-24T08:50:00Z");
		LatLong latLong = new LatLong(57.678440, 11.616953, Date.from(locationTime));

		IncomingMovement incomingMovement = movementHelper.createIncomingMovement(assetDTO, latLong);
		movementHelper.createMovementDontWaitForResponse(assetDTO, incomingMovement);
	}

	private AssetDTO getAssetDto(Asset asset) {
		SearchBranch assetQuery = new SearchBranch();
		assetQuery.addNewSearchLeaf(SearchFields.CFR, asset.getCfr());
		assetQuery.addNewSearchLeaf(SearchFields.EXTERNAL_MARKING, asset.getExternalMarking());
		assetQuery.addNewSearchLeaf(SearchFields.IRCS, asset.getIrcs());

		AssetListResponse assetListResponse = AssetTestHelper.assetListQuery(assetQuery);
		List<AssetDTO> assetList = assetListResponse.getAssetList();
		return assetList.get(0);
	}

	private Asset createShipAsset() throws Exception {
		AssetGroup basicAssetGroup = AssetTestHelper.createBasicAssetGroup();
		AssetGroup createdAssetGroup = AssetTestHelper.createAssetGroup(basicAssetGroup);

		Asset asset = assetJMSHelper.createDummyAsset(AssetIdType.CFR);
		asset.setCfr("fake-cfr-id");
		asset.setExternalMarking("fake-marking");
		asset.setIrcs("fakeircs");

		assetJMSHelper.upsertAsset(asset, createdAssetGroup.getOwner());
		return asset;
	}

	private String getFluxfaReportMessageFromFile(String filename) throws IOException, URISyntaxException {
		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource(filename);
		URI uri = resource.toURI();
		byte[] bytes = Files.readAllBytes(Paths.get(uri));
		return new String(bytes);
	}
}
