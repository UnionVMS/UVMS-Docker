package eu.europa.ec.fisheries.uvms.docker.validation.exchange;

import java.io.StringWriter;
import java.util.Date;

import javax.jms.Message;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.exchange.module.v1.ExchangeModuleMethod;
import eu.europa.ec.fisheries.schema.exchange.module.v1.SetMovementReportRequest;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetType;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementActivityType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementComChannelType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.movement.module.v1.CreateMovementRequest;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.MobileTerminalTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;

/**
 * The Class SetMovementReportRequestJmsIT.
 */
public class SetMovementReportRequestJmsIT extends AbstractRestServiceTest {

	private static  MovementHelper movementHelper = new MovementHelper();



	/**
	 * Creates the movement request test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test(timeout = 30000)
	@Ignore
	public void setMovementReportRequestTest() throws Exception {		
		Asset testAsset = AssetTestHelper.createTestAsset();
		MobileTerminalType mobileTerminalType = MobileTerminalTestHelper.createMobileTerminalType();
		MobileTerminalTestHelper.assignMobileTerminal(testAsset, mobileTerminalType);

		LatLong latLong = new LatLong(16.9, 32.6333333, new Date(System.currentTimeMillis()));
		final CreateMovementRequest createMovementRequest = movementHelper.createMovementRequest(testAsset,mobileTerminalType,latLong);

		Message messageResponse = MessageHelper.getMessageResponse("UVMSExchangeEvent",
				marshall(createSetReportMovementType(testAsset,mobileTerminalType,createMovementRequest)));
		assertNotNull(messageResponse);
	}

	public String marshall(final SetMovementReportRequest request) throws JAXBException {
		final StringWriter sw = new StringWriter();
		JAXBContext.newInstance(SetMovementReportRequest.class).createMarshaller().marshal(request, sw);
		return sw.toString();
	}


	private SetMovementReportRequest createSetReportMovementType(Asset testAsset, MobileTerminalType mobileTerminalType,
			CreateMovementRequest createMovementRequest) {
		final SetMovementReportRequest request = new SetMovementReportRequest();
		request.setUsername("vms_admin_com");
		request.setDate(new Date());
		request.setMethod(ExchangeModuleMethod.SET_MOVEMENT_REPORT);
		final SetReportMovementType movementType = new SetReportMovementType();
		request.setRequest(movementType);		
		
		movementType.setPluginType(PluginType.NAF);
		movementType.setTimestamp(new Date());
		movementType.setPluginName(mobileTerminalType.getPlugin().getServiceName());
		MovementBaseType movementBaseType = new MovementBaseType();
		movementType.setMovement(movementBaseType);
		MovementActivityType movementActivityType = new MovementActivityType();
		movementBaseType.setActivity(movementActivityType);
		AssetId assetId = new AssetId();
		movementBaseType.setAssetId(assetId);
		assetId.setAssetType(AssetType.VESSEL);
		AssetIdList assetIdList = new AssetIdList();
		assetIdList.setIdType(AssetIdType.GUID);
		assetIdList.setValue(testAsset.getAssetId().getGuid());
		assetId.getAssetIdList().add(assetIdList);

		
		movementBaseType.setAssetName(testAsset.getName());
		movementBaseType.setComChannelType(MovementComChannelType.NAF);
		movementBaseType.setExternalMarking(testAsset.getExternalMarking());
		movementBaseType.setFlagState(testAsset.getCountryCode());
		movementBaseType.setInternalReferenceNumber(testAsset.getIrcs());
		movementBaseType.setIrcs(testAsset.getIrcs());
		movementBaseType.setMmsi(testAsset.getMmsiNo());
		MobileTerminalId mobileTerminalId = new MobileTerminalId();
		mobileTerminalId.setConnectId(mobileTerminalType.getConnectId());
		mobileTerminalId.setGuid(mobileTerminalType.getMobileTerminalId().getGuid());
		
		movementBaseType.setMobileTerminalId(mobileTerminalId);
		movementBaseType.setMovementType(MovementTypeType.POS);
		MovementPoint movementPoint = new MovementPoint();
		movementPoint.setLongitude(createMovementRequest.getMovement().getPosition().getLongitude());
		movementPoint.setLatitude(createMovementRequest.getMovement().getPosition().getLatitude());
		movementPoint.setAltitude(createMovementRequest.getMovement().getPosition().getAltitude());
		
		movementBaseType.setPosition(movementPoint);
		movementBaseType.setPositionTime(new Date());
		movementBaseType.setReportedCourse(1d);
		movementBaseType.setReportedSpeed(1d);
		movementBaseType.setSource(MovementSourceType.INMARSAT_C);
		movementBaseType.setTripNumber(1d);		
		return request;
	}




}
