package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.model.IncomingMovement;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;

public class SetMovementReportRequestJmsIT extends AbstractRest {

	private static MovementHelper movementHelper;
	private static MessageHelper messageHelper;

	@BeforeClass
	public static void setup() throws JMSException {
		movementHelper = new MovementHelper();
		messageHelper = new MessageHelper();
	}

	@AfterClass
	public static void cleanup() {
		movementHelper.close();
		messageHelper.close();
	}

	//TODO: Make theses tests actually do something and not just send a message on the queue

	@Test(timeout = 50000)
	public void setMovementReportRequestTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalDto assignMobileTerminal = MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);

		assertNotNull(assignMobileTerminal);
		assertNotNull(assignMobileTerminal.getAsset().getId());

		LatLong latLong = movementHelper.createRutt(1).get(0);
		IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, latLong);

		messageHelper.sendMessage("UVMSExchangeEvent",
				marshall(createSetReportMovementType(testAsset, assignMobileTerminal, createMovementRequest)));
	}

	@Test(timeout = 25000)
	public void setMovementReportRequestRouteTest() throws Exception {
		AssetDTO testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalDto mobileTerminal = MobileTerminalTestHelper.createMobileTerminal();
		MobileTerminalDto assignMobileTerminal = MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminal);

		List<LatLong> latLongList = movementHelper.createRutt(2);

		for (LatLong latLong : latLongList) {
			IncomingMovement createMovementRequest = movementHelper.createIncomingMovement(testAsset, latLong);
			messageHelper.sendMessage("UVMSExchangeEvent",
					marshall(createSetReportMovementType(testAsset, assignMobileTerminal, createMovementRequest)));
		}
	}

	private String marshall(final SetMovementReportRequest request) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(SetMovementReportRequest.class).createMarshaller().marshal(request, sw);
		return sw.toString();
	}

	private SetMovementReportRequest createSetReportMovementType(AssetDTO testAsset, MobileTerminalDto mobileTerminal,
			IncomingMovement createMovementRequest) {

		final SetMovementReportRequest request = new SetMovementReportRequest();
		request.setUsername("vms_admin_com");
		request.setDate(new Date());
		request.setMethod(ExchangeModuleMethod.SET_MOVEMENT_REPORT);
		request.setPluginType(PluginType.NAF);
		request.setSenderOrReceiver("NAF");
		final SetReportMovementType movementType = new SetReportMovementType();
		request.setRequest(movementType);

		movementType.setPluginType(PluginType.NAF);
		movementType.setTimestamp(new Date());
		movementType.setPluginName(mobileTerminal.getPlugin().getPluginServiceName());
		MovementBaseType movementBaseType = new MovementBaseType();
		movementType.setMovement(movementBaseType);
		MovementActivityType movementActivityType = new MovementActivityType();
		movementActivityType.setMessageType(MovementActivityTypeType.CAN);
		movementActivityType.setMessageId(createMovementRequest.getActivityMessageId());
		movementActivityType.setCallback(createMovementRequest.getActivityCallback());
		movementBaseType.setActivity(movementActivityType);
		AssetId assetId = new AssetId();
		movementBaseType.setAssetId(assetId);
		assetId.setAssetType(AssetType.VESSEL);
		AssetIdList assetIdList = new AssetIdList();
		assetIdList.setIdType(AssetIdType.CFR);
		assetIdList.setValue(testAsset.getCfr());
		assetId.getAssetIdList().add(assetIdList);

		movementBaseType.setAssetName(testAsset.getName());
		movementBaseType.setComChannelType(MovementComChannelType.NAF);
		movementBaseType.setExternalMarking(testAsset.getExternalMarking());
		movementBaseType.setFlagState(testAsset.getFlagStateCode());
		movementBaseType.setInternalReferenceNumber(testAsset.getIrcs());
		movementBaseType.setIrcs(testAsset.getIrcs());
		movementBaseType.setMmsi(testAsset.getMmsi());

		MobileTerminalId mobileTerminalId = new MobileTerminalId();
		mobileTerminalId.setConnectId(mobileTerminal.getAsset().getId().toString());
		mobileTerminalId.setGuid(mobileTerminal.getId().toString());
		movementBaseType.setMobileTerminalId(mobileTerminalId);
		movementBaseType.setMovementType(MovementTypeType.POS);
		MovementPoint movementPoint = new MovementPoint();
		movementPoint.setLongitude(createMovementRequest.getLongitude());
		movementPoint.setLatitude(createMovementRequest.getLatitude());
		movementPoint.setAltitude(createMovementRequest.getAltitude());

		movementBaseType.setPosition(movementPoint);
		movementBaseType.setPositionTime(new Date());
		movementBaseType.setReportedCourse(1d);
		movementBaseType.setReportedSpeed(1d);
		movementBaseType.setSource(MovementSourceType.INMARSAT_C);
		movementBaseType.setTripNumber(1d);
		return request;
	}
}
