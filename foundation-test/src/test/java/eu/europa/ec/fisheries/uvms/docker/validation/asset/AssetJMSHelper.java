package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import java.util.List;

import javax.jms.TextMessage;

import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.wsdl.asset.group.AssetGroup;
import eu.europa.ec.fisheries.wsdl.asset.group.ListAssetGroupResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.AssetModuleMethod;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleRequest;
import eu.europa.ec.fisheries.wsdl.asset.module.GetAssetModuleResponse;
import eu.europa.ec.fisheries.wsdl.asset.module.PingResponse;
import eu.europa.ec.fisheries.wsdl.asset.types.Asset;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetIdType;
import eu.europa.ec.fisheries.wsdl.asset.types.AssetListQuery;
import eu.europa.ec.fisheries.wsdl.asset.types.ListAssetResponse;

public class AssetJMSHelper {

	private static final String ASSET_QUEUE = "UVMSAssetEvent";
	
	public static Asset getAssetById(String value, AssetIdType type) throws Exception {
		String msg = AssetModuleRequestMapper.createGetAssetModuleRequest(value, type);
		TextMessage response = (TextMessage) MessageHelper.getMessageResponse(ASSET_QUEUE, msg);
		GetAssetModuleResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, GetAssetModuleResponse.class);
		return assetModuleResponse.getAsset();
	}
	
	public static List<Asset> getAssetByAssetListQuery(AssetListQuery assetListQuery) throws Exception {
		String msg = AssetModuleRequestMapper.createAssetListModuleRequest(assetListQuery);
		TextMessage response = (TextMessage) MessageHelper.getMessageResponse(ASSET_QUEUE, msg);
		ListAssetResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
		return assetModuleResponse.getAsset();
	}
	
	public static List<AssetGroup> getAssetGroupByUser(String username) throws Exception {
		String msg = AssetModuleRequestMapper.createAssetGroupListByUserModuleRequest(username);
		TextMessage response = (TextMessage) MessageHelper.getMessageResponse(ASSET_QUEUE, msg);
		ListAssetGroupResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
		return assetModuleResponse.getAssetGroup();
	}
	
	public static List<AssetGroup> getAssetGroupListByAssetGuid(String assetGuid) throws Exception {
		String msg = AssetModuleRequestMapper.createAssetGroupListByAssetGuidRequest(assetGuid);
		TextMessage response = (TextMessage) MessageHelper.getMessageResponse(ASSET_QUEUE, msg);
		ListAssetGroupResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetGroupResponse.class);
		return assetModuleResponse.getAssetGroup();
	}
	
	public static List<Asset> getAssetListByAssetGroups(List<AssetGroup> assetGroups) throws Exception {
		String msg = AssetModuleRequestMapper.createAssetListModuleRequest(assetGroups);
		TextMessage response = (TextMessage) MessageHelper.getMessageResponse(ASSET_QUEUE, msg);
		ListAssetResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, ListAssetResponse.class);
		return assetModuleResponse.getAsset();
	}
	
	public static String pingModule() throws Exception {
		GetAssetModuleRequest request = new GetAssetModuleRequest();
		request.setMethod(AssetModuleMethod.PING);
		String msg = JAXBMarshaller.marshallJaxBObjectToString(request);
		TextMessage response = (TextMessage) MessageHelper.getMessageResponse(ASSET_QUEUE, msg);
		PingResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, PingResponse.class);
		return assetModuleResponse.getResponse();
	}
	
//	public static void upsertAsset() {
//	}
//	
//	public static void fishingGearUpsert() {
//	}
}
