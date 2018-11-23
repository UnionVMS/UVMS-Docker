package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import com.peertopark.java.geocalc.Coordinate;
import com.peertopark.java.geocalc.DegreeCoordinate;
import com.peertopark.java.geocalc.EarthCalc;
import com.peertopark.java.geocalc.Point;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementBatchRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementBatchResponse;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.source.v1.GetMovementListByQueryResponse;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;

public class MovementHelper extends AbstractHelper {

	private static final String UVMS_MOVEMENT_REQUEST_QUEUE = "UVMSMovementEvent";

	private Random rnd = new Random();

	public CreateMovementRequest createMovementRequest(AssetDTO testAsset, LatLong latlong) {

		final CreateMovementRequest createMovementRequest1 = new CreateMovementRequest();
		final MovementBaseType movementBaseType = new MovementBaseType();
		AssetId assetId = new AssetId();
		assetId.setAssetType(AssetType.VESSEL);
		assetId.setIdType(AssetIdType.GUID);
		assetId.setValue(testAsset.getId().toString());
		movementBaseType.setAssetId(assetId);
		movementBaseType.setConnectId(testAsset.getHistoryId().toString());

		MovementActivityType movementActivityType = new MovementActivityType();
		movementBaseType.setActivity(movementActivityType);
		movementActivityType.setMessageId(UUID.randomUUID().toString());
		movementActivityType.setMessageType(MovementActivityTypeType.ANC);

		createMovementRequest1.setMovement(movementBaseType);
		createMovementRequest1.setMethod(MovementModuleMethod.CREATE);
		createMovementRequest1.setUsername("vms_admin_com");

		MovementPoint movementPoint = new MovementPoint();
		movementPoint.setLongitude(latlong.longitude);
		movementPoint.setLatitude(latlong.latitude);
		movementPoint.setAltitude((double) 5);

		movementBaseType.setPosition(movementPoint);
		movementBaseType.setPositionTime(latlong.positionTime);

		movementBaseType.setMovementType(MovementTypeType.POS);
		movementBaseType.setReportedCourse(latlong.bearing);
		movementBaseType.setReportedSpeed(latlong.speed);
		return createMovementRequest1;

	}

	public CreateMovementBatchRequest createMovementBatchRequest(AssetDTO testAsset, MobileTerminalType mobileTerminalType,
			List<LatLong> route) {

		final CreateMovementBatchRequest createMovementBatchRequest = new CreateMovementBatchRequest();

		AssetId assetId = new AssetId();
		assetId.setAssetType(AssetType.VESSEL);
		assetId.setIdType(AssetIdType.GUID);
		assetId.setValue(testAsset.getId().toString());

		MovementActivityType movementActivityType = new MovementActivityType();
		movementActivityType.setMessageId(UUID.randomUUID().toString());
		movementActivityType.setMessageType(MovementActivityTypeType.ANC);

		createMovementBatchRequest.setMethod(MovementModuleMethod.CREATE_BATCH);
		createMovementBatchRequest.setUsername("vms_admin_com");

		for (LatLong latlong : route) {

			MovementBaseType movementBaseType = new MovementBaseType();
			movementBaseType.setAssetId(assetId);
			movementBaseType.setConnectId(mobileTerminalType.getConnectId());
			movementBaseType.setActivity(movementActivityType);

			MovementPoint movementPoint = new MovementPoint();
			movementPoint.setLongitude(latlong.longitude);
			movementPoint.setLatitude(latlong.latitude);
			movementPoint.setAltitude((double) 5);

			movementBaseType.setPosition(movementPoint);
			movementBaseType.setPositionTime(latlong.positionTime);
			movementBaseType.setMovementType(MovementTypeType.POS);
			movementBaseType.setReportedCourse(latlong.bearing);
			movementBaseType.setReportedSpeed(latlong.speed);
			createMovementBatchRequest.getMovement().add(movementBaseType);
		}
		return createMovementBatchRequest;
	}

	public List<LatLong> createRutt(int numberPositions) {
		return createRutt(15 * 1000, numberPositions);
	}

	public List<LatLong> createRuttVarbergGrena(int numberPositions) {

		int movementTimeDeltaInMillis = 30000;
		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis();

		double divideramed = 50;
		double randomFactorLat = rnd.nextDouble() / divideramed;
		double randomFactorLong = rnd.nextDouble() / divideramed;

		double latitude = 57.110 + randomFactorLat;
		double longitude = 12.244 + randomFactorLong;

		double END_LATITUDE = 56.408;
		double END_LONGITUDE = 10.926;

		while (true) {

			if (latitude >= END_LATITUDE)
				latitude = latitude - 0.03;
			if (longitude >= END_LONGITUDE)
				longitude = longitude - 0.03;
			if (latitude < END_LATITUDE && longitude < END_LONGITUDE)
				break;
			rutt.add(new LatLong(latitude, longitude, getDate(ts += movementTimeDeltaInMillis)));
		}

		rutt = calculateReportedDataForRoute(rutt);

		if (numberPositions == -1) {
			return rutt;
		} else {
			return rutt.subList(0, numberPositions);
		}
	}

	/* 0.1 ? */
	public List<LatLong> createRuttCobhNewYork(int numberPositions, float distanceBetweenReports) {

		int movementTimeDeltaInMillis = 30000;
		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis() - 1000L * 60L * 60L * 24L * 365L;


		double divideramed = 50;
		double randomFactorLat = rnd.nextDouble() / divideramed;
		double randomFactorLong = rnd.nextDouble() / divideramed;

		double latitude = 51.844 + randomFactorLat;
		double longitude = -8.311 + randomFactorLong;


		double END_LATITUDE = 40.313;
		double END_LONGITUDE = -73.740;

		while (true) {

			if (latitude >= END_LATITUDE)
				latitude = latitude - distanceBetweenReports;
			if (longitude >= END_LONGITUDE)
				longitude = longitude - distanceBetweenReports;
			if (latitude < END_LATITUDE && longitude < END_LONGITUDE)
				break;
			rutt.add(new LatLong(latitude, longitude, getDate(ts += movementTimeDeltaInMillis)));
		}

		rutt = calculateReportedDataForRoute(rutt);

		if (numberPositions == -1) {
			return rutt;
		} else {
			return rutt.subList(0, numberPositions);
		}
	}

	public List<LatLong> createRuttGeneric(double START_LATITUDE, double START_LONGITUDE, double END_LATITUDE,
			double END_LONGITUDE, int numberPositions) {

		int movementTimeDeltaInMillis = 30000;
		List<LatLong> rutt = new ArrayList<>();
		// Avoid future dates
		long ts = System.currentTimeMillis() - movementTimeDeltaInMillis * numberPositions;

		while (true) {

			if (START_LATITUDE >= END_LATITUDE)
				START_LATITUDE = START_LATITUDE - 0.5;
			if (START_LONGITUDE >= END_LONGITUDE)
				START_LONGITUDE = START_LONGITUDE - 0.5;
			if (START_LATITUDE < END_LATITUDE && START_LONGITUDE < END_LONGITUDE)
				break;
			rutt.add(new LatLong(START_LATITUDE, START_LONGITUDE, getDate(ts += movementTimeDeltaInMillis)));
		}

		if (numberPositions == -1) {
			return rutt;
		} else {
			return rutt.subList(0, numberPositions);
		}
	}

	public List<LatLong> createRutt(int movementTimeDeltaInMillis, int numberPositions) {


		double divideramed = 50;
		double randomFactorLat = rnd.nextDouble() / divideramed;
		double randomFactorLong = rnd.nextDouble() / divideramed;



		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis();
		rutt.add(new LatLong(57.715434 + randomFactorLat, 11.970012 + randomFactorLong,
				getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.714735 + randomFactorLat, 11.968242, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.713837 + randomFactorLat, 11.965640, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.712691 + randomFactorLat, 11.963301, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.711178 + randomFactorLat, 11.960630, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.708347 + randomFactorLat, 11.956049, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.703993 + randomFactorLat, 11.951709, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.701058 + randomFactorLat, 11.932225, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.696225 + randomFactorLat, 11.913557, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.684998 + randomFactorLat, 11.888752, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.682245 + randomFactorLat, 11.861973, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.676004 + randomFactorLat, 11.845493, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.664253 + randomFactorLat, 11.754169, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.646619 + randomFactorLat, 11.719151, getDate(ts += movementTimeDeltaInMillis)));

		rutt.add(new LatLong(57.632 + randomFactorLat, 11.684, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.601 + randomFactorLat, 11.683, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.582 + randomFactorLat, 11.521, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.562 + randomFactorLat, 11.411, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.535 + randomFactorLat, 11.253, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.508 + randomFactorLat, 11.106, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.486 + randomFactorLat, 10.970, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.469 + randomFactorLat, 10.867, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.425 + randomFactorLat, 10.624, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.420 + randomFactorLat, 10.580, getDate(ts += movementTimeDeltaInMillis)));

		rutt.add(new LatLong(57.42920 + randomFactorLat, 11.58259, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42905 + randomFactorLat, 11.58192, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42897 + randomFactorLat, 11.58149, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42882 + randomFactorLat, 11.58116, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42858 + randomFactorLat, 11.58071, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42825 + randomFactorLat, 11.57973, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42796 + randomFactorLat, 11.57890, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42762 + randomFactorLat, 11.57814, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42707 + randomFactorLat, 11.57713, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42624 + randomFactorLat, 11.57576, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42550, 11.57458, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42462, 11.57373, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42386, 11.57265, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42316, 11.57141, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42264, 11.56922, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42194, 11.56721, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42148, 11.56490, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42111, 11.56212, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42091, 11.55908, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42073, 11.55707, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42011, 11.55375, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41934, 11.55112, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41829, 11.54826, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41664, 11.54486, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41529, 11.54237, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41438, 11.54038, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41312, 11.53614, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41239, 11.53068, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41131, 11.52269, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41041, 11.51412, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40870, 11.50024, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40727, 11.48819, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40563, 11.48224, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.40256, 11.47660, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.39744, 11.46579, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.39507, 11.46002, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.38956, 11.42624, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.37787, 11.40996, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.36099, 11.38318, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.34045, 11.25876, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.31126, 11.97270, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.27140, 10.46655, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.25455, 10.36438, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.28647, 10.35944, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.35723, 10.35944, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.41104, 10.36603, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42216, 10.36026, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42711, 10.36263, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42794, 10.35769, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42862, 10.35563, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42945, 10.35521, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42946, 10.35416, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42928, 10.35400, getDate(ts += movementTimeDeltaInMillis)));
		if (numberPositions == -1) {
			return rutt;
		} else {
			return rutt.subList(0, numberPositions);
		}

	}

	private Date getDate(Long millis) {
		return new Date(millis);
	}

	/**
	 * Marshall.
	 *
	 * @param createMovementRequest the create movement request
	 * @return the string
	 * @throws JAXBException the JAXB exception
	 */

	public String marshall(final CreateMovementRequest createMovementRequest) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(CreateMovementRequest.class).createMarshaller().marshal(createMovementRequest, sw);
		return sw.toString();
	}

	/**
	 * 
	 * @param createMovementBatchRequest
	 * @return
	 * @throws JAXBException
	 */
	public String marshall(final CreateMovementBatchRequest createMovementBatchRequest) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(CreateMovementBatchRequest.class).createMarshaller().marshal(createMovementBatchRequest,
				sw);
		return sw.toString();
	}

	/**
	 * Un marshall create movement response.
	 *
	 * @param response the response
	 * @return the creates the movement response
	 * @throws Exception the exception
	 */
	public CreateMovementResponse unMarshallCreateMovementResponse(final Message response) throws Exception {
		TextMessage textMessage = (TextMessage) response;
		JAXBContext jaxbContext = JAXBContext.newInstance(CreateMovementResponse.class);
		return (CreateMovementResponse) jaxbContext.createUnmarshaller()
				.unmarshal(new StringReader(textMessage.getText()));
	}

	/**
	 * 
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public CreateMovementBatchResponse unMarshallCreateMovementBatchResponse(final Message response) throws Exception {
		TextMessage textMessage = (TextMessage) response;
		JAXBContext jaxbContext = JAXBContext.newInstance(CreateMovementBatchResponse.class);
		return (CreateMovementBatchResponse) jaxbContext.createUnmarshaller()
				.unmarshal(new StringReader(textMessage.getText()));
	}

	public CreateMovementResponse createMovement(CreateMovementRequest createMovementRequest) throws Exception {

		Message messageResponse =
				MessageHelper.getMessageResponse(UVMS_MOVEMENT_REQUEST_QUEUE, marshall(createMovementRequest));
		return unMarshallCreateMovementResponse(messageResponse);
	}

	public String createMovementDontWaitForResponse(AssetDTO testAsset,CreateMovementRequest createMovementRequest, int order) throws Exception {

		String messageId =
				MessageHelper.sendMessageAndReturnMessageId(UVMS_MOVEMENT_REQUEST_QUEUE, marshall(createMovementRequest), testAsset.getId().toString(), order);
		return messageId;
	}

	public static MovementType getMovementById(String guid) {
	    ResponseDto<MovementType> response = getWebTarget()
                .path("movement/rest/movement/")
                .path(guid)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<MovementType>>() {});
        return response.getData();
	}
	
	public static List<MovementType> getListByQuery(MovementQuery movementQuery) {
		ResponseDto<GetMovementListByQueryResponse> response = getWebTarget()
		        .path("movement/rest/movement/list")
		        .request(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
		        .post(Entity.json(movementQuery), new GenericType<ResponseDto<GetMovementListByQueryResponse>>() {});
		return response.getData().getMovement();
	}

	public static List<MovementType> getMinimalListByQuery(MovementQuery movementQuery) {
		ResponseDto<GetMovementListByQueryResponse> response = getWebTarget()
                .path("movement/rest/movement/list/minimal")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(movementQuery), new GenericType<ResponseDto<GetMovementListByQueryResponse>>() {});
        return response.getData().getMovement();
	}
	
	
	public static List<MovementDto> getLatestMovements(List<String> connectIds) {
	    ResponseDto<List<MovementDto>> response = getWebTarget()
                .path("movement/rest/movement/latest/")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .post(Entity.json(connectIds), new GenericType<ResponseDto<List<MovementDto>>>() {});
        return response.getData();
    }
	
	public static List<MovementDto> getLatestMovements(Integer amount) {
        ResponseDto<List<MovementDto>> response = getWebTarget()
                .path("movement/rest/movement/latest/" + amount)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(new GenericType<ResponseDto<List<MovementDto>>>() {});
        return response.getData();
    }

	public static String getMicroMovements(String date) {
		String response = getWebTarget()
				.path("movement/rest/movement/microMovementList/" + date)
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
				.get(String.class);
		return response;
	}

	public CreateMovementBatchResponse createMovementBatch(CreateMovementBatchRequest createMovementBatchRequest) throws Exception {

		Message messageResponse =
				MessageHelper.getMessageResponse(UVMS_MOVEMENT_REQUEST_QUEUE, marshall(createMovementBatchRequest));
		return unMarshallCreateMovementBatchResponse(messageResponse);
	}



	List<LatLong> calculateReportedDataForRoute(List<LatLong> route) {

		LatLong previousPosition = null;
		LatLong currentPosition = null;
		int i = 0;
		int n = route.size();
		while (i < n) {
			currentPosition = route.get(i);
			if (i == 0) {
				previousPosition = route.get(i);
				i++;
				continue;
			}

			double bearing = bearing(previousPosition, currentPosition);
			double distance = distance(previousPosition, currentPosition);
			route.get(i - 1).bearing = bearing;
			route.get(i - 1).distance = distance;
			double speed = calcSpeed(previousPosition, currentPosition);
			route.get(i - 1).speed = speed;

			if (i < n) {
				previousPosition = currentPosition;
			}
			i++;
		}
		double bearing = bearing(previousPosition, currentPosition);
		double distance = distance(previousPosition, currentPosition);
		route.get(i - 1).distance = distance;
		route.get(i - 1).bearing = bearing;
		// double speed = calcSpeed(previousPosition, currentPosition);
		route.get(i - 1).speed = 0d;
		return route;

	}

	private Double bearing(LatLong src, LatLong dst) {

		Coordinate latFrom = new DegreeCoordinate(src.latitude);
		Coordinate lngFrom = new DegreeCoordinate(src.longitude);
		Point from = new Point(latFrom, lngFrom);

		Coordinate latTo = new DegreeCoordinate(dst.latitude);
		Coordinate lngTo = new DegreeCoordinate(dst.longitude);
		Point to = new Point(latTo, lngTo);

		double bearing = EarthCalc.getBearing(from, to); // in decimal degrees
		return bearing;

	}

	private Double distance(LatLong src, LatLong dst) {

		Coordinate latFrom = new DegreeCoordinate(src.latitude);
		Coordinate lngFrom = new DegreeCoordinate(src.longitude);
		Point from = new Point(latFrom, lngFrom);

		Coordinate latTo = new DegreeCoordinate(dst.latitude);
		Coordinate lngTo = new DegreeCoordinate(dst.longitude);
		Point to = new Point(latTo, lngTo);

		double distanceInMeters = EarthCalc.getDistance(from, to);
		return distanceInMeters;

	}


	private double calcSpeed(LatLong src, LatLong dst) {

		try {

			if (src.positionTime == null)
				return 0;
			if (dst.positionTime == null)
				return 0;

			// distance to next
			double distanceM = src.distance;

			double durationms = (double) Math.abs(dst.positionTime.getTime() - src.positionTime.getTime());
			double durationSecs = durationms / 1000;
			double speedMeterPerSecond = (distanceM / durationSecs);
			double speedMPerHour = speedMeterPerSecond * 3600;
			return speedMPerHour / 1000;
		} catch (RuntimeException e) {
			return 0.0;
		}
	}


	public List<LatLong> createSmallFishingTourFromVarberg() {

		// int numberPositions = 25;
		int movementTimeDeltaInMillis = 30000;
		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis();

		double divideramed = 50;
		double randomFactorLat = rnd.nextDouble() / divideramed;
		double randomFactorLong = rnd.nextDouble() / divideramed;

		double latitude = 57.110 + randomFactorLat;
		double longitude = 12.244 + randomFactorLong;

		// these will never be reached but still good to have to steer on
		double END_LATITUDE = 56.408;
		double END_LONGITUDE = 10.926;

		// leave the harbour
		for (int i = 0; i < 25; i++) {

			if (latitude >= END_LATITUDE)
				latitude = latitude - 0.004;
			if (longitude >= END_LONGITUDE)
				longitude = longitude - 0.004;
			if (latitude < END_LATITUDE && longitude < END_LONGITUDE)
				break;
			rutt.add(new LatLong(latitude, longitude, getDate(ts += movementTimeDeltaInMillis)));
		}
		// do some fishing
		for (int i = 0; i < 15; i++) {
			latitude = latitude - 0.001;
			longitude = longitude - 0.002;
			rutt.add(new LatLong(latitude, longitude, getDate(ts += movementTimeDeltaInMillis)));
		}
		// go home
		int n = rutt.size();
		List<LatLong> ruttHome = new ArrayList<>();
		for(int i = n - 1 ; i > 0 ; i--){
			LatLong wrk = rutt.get(i);
			ruttHome.add(new LatLong(wrk.latitude + 0.001, wrk.longitude, getDate(ts += movementTimeDeltaInMillis)));
		}
		
		rutt.addAll(ruttHome);

		rutt = calculateReportedDataForRoute(rutt);

		return rutt;
	}
	
	public static void pollMovementCreated() throws IOException {
        getWebTarget()
            .path("movement/activity/movement")
            .request(MediaType.APPLICATION_JSON)
            .get();
    }
	
}
