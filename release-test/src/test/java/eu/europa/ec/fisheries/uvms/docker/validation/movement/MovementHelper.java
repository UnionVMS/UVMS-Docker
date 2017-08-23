package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementResponse;
import eu.europa.ec.fisheries.schema.movement.module.v1.MovementModuleMethod;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementActivityTypeType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

public class MovementHelper extends AbstractHelper {

	private static final String UVMS_MOVEMENT_REQUEST_QUEUE = "UVMSMovementEvent";

	public CreateMovementRequest createMovementRequest(Asset testAsset, MobileTerminalType mobileTerminalType,
			LatLong latlong) throws IOException, ClientProtocolException, JsonProcessingException, JsonParseException,
			JsonMappingException {

		Date positionTime = new Date(System.currentTimeMillis());
		final CreateMovementRequest createMovementRequest1 = new CreateMovementRequest();
		final MovementBaseType movementBaseType = new MovementBaseType();
		AssetId assetId = new AssetId();
		assetId.setAssetType(AssetType.VESSEL);
		assetId.setIdType(AssetIdType.GUID);
		assetId.setValue(testAsset.getAssetId().getGuid());
		movementBaseType.setAssetId(assetId);
		movementBaseType.setConnectId(mobileTerminalType.getConnectId());

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
		movementBaseType.setPositionTime(positionTime);

		movementBaseType.setMovementType(MovementTypeType.POS);
		return createMovementRequest1;

	}

	public List<LatLong> createRutt(int numberPositions) {
		return createRutt(15 * 1000, numberPositions);
	}

	public List<LatLong> createRuttVarbergGrena(int numberPositions) {

		int movementTimeDeltaInMillis = 30000;
		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis();

		double latitude = 57.110;
		double longitude = 12.244;

		// 56.408
		// 10.926

		int count = 0;
		while (true) {
			count++;
			
			if(latitude >= 56.408) latitude =latitude -  0.01;
			if(longitude >= 10.926) longitude =longitude -  0.01;
			if(latitude < 56.408 && longitude < 10.926) break;
			System.out.print(".");
			rutt.add(new LatLong(latitude, longitude, getDate(ts += movementTimeDeltaInMillis)));
		}

		if (numberPositions == -1) {
			return rutt;
		} else {
			return rutt.subList(0, numberPositions);
		}
	}
	
	public List<LatLong> createRutt(int movementTimeDeltaInMillis, int numberPositions) {

		List<LatLong> rutt = new ArrayList<>();
		long ts = System.currentTimeMillis();
		rutt.add(new LatLong(57.715434, 11.970012, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.714735, 11.968242, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.713837, 11.965640, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.712691, 11.963301, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.711178, 11.960630, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.708347, 11.956049, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.703993, 11.951709, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.701058, 11.932225, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.696225, 11.913557, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.684998, 11.888752, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.682245, 11.861973, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.676004, 11.845493, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.664253, 11.754169, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.646619, 11.719151, getDate(ts += movementTimeDeltaInMillis)));

		rutt.add(new LatLong(57.632, 11.684, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.601, 11.683, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.582, 11.521, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.562, 11.411, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.535, 11.253, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.508, 11.106, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.486, 10.970, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.469, 10.867, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.425, 10.624, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.420, 10.580, getDate(ts += movementTimeDeltaInMillis)));

		rutt.add(new LatLong(57.42920, 11.58259, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42905, 11.58192, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42897, 11.58149, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42882, 11.58116, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42858, 11.58071, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42825, 11.57973, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42796, 11.57890, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42762, 11.57814, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42707, 11.57713, getDate(ts += movementTimeDeltaInMillis)));
		rutt.add(new LatLong(57.42624, 11.57576, getDate(ts += movementTimeDeltaInMillis)));
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
	 * @param createMovementRequest
	 *            the create movement request
	 * @return the string
	 * @throws JAXBException
	 *             the JAXB exception
	 */

	public String marshall(final CreateMovementRequest createMovementRequest) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(CreateMovementRequest.class).createMarshaller().marshal(createMovementRequest, sw);
		return sw.toString();
	}

	/**
	 * Un marshall create movement response.
	 *
	 * @param response
	 *            the response
	 * @return the creates the movement response
	 * @throws Exception
	 *             the exception
	 */
	public CreateMovementResponse unMarshallCreateMovementResponse(final Message response) throws Exception {
		TextMessage textMessage = (TextMessage) response;
		JAXBContext jaxbContext = JAXBContext.newInstance(CreateMovementResponse.class);
		return (CreateMovementResponse) jaxbContext.createUnmarshaller()
				.unmarshal(new StringReader(textMessage.getText()));
	}

	public CreateMovementResponse createMovement(Asset testAsset, MobileTerminalType mobileTerminalType,
			CreateMovementRequest createMovementRequest) throws Exception {

		Message messageResponse = MessageHelper.getMessageResponse(UVMS_MOVEMENT_REQUEST_QUEUE,
				marshall(createMovementRequest));

		return unMarshallCreateMovementResponse(messageResponse);

	}

}
