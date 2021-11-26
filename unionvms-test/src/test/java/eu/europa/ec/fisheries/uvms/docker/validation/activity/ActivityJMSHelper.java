/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.activity;

import javax.jms.JMSException;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;

public class ActivityJMSHelper extends AbstractHelper implements AutoCloseable {

    private static final String ACTIVITY_QUEUE = "UVMSActivityEvent";

    private MessageHelper messageHelper;

    public ActivityJMSHelper() throws JMSException {
        messageHelper = new MessageHelper();
    }

    @Override
    public void close() {
        messageHelper.close();
    }

    public void sendActivity() {
//        String activityRequest = ActivityModuleRequestMapper.mapToSetFLUXFAReportOrQueryMessageRequest(request.getRequest(), PluginType.valueOf(request.getPluginType().name()).name(), MessageType.FLUX_FA_REPORT_MESSAGE, SyncAsyncRequestType.ASYNC, exchangeLog.getId().toString());
//        exchangeActivityProducer.sendActivityMessage(activityRequest);
    }

//    public Asset getAssetById(String value, AssetIdType type) throws Exception {
//        String msg = AssetModuleRequestMapper.createGetAssetModuleRequest(value, type);
//        TextMessage response = (TextMessage) messageHelper.getMessageResponse(ASSET_QUEUE, msg);
//        GetAssetModuleResponse assetModuleResponse = JAXBMarshaller.unmarshallTextMessage(response, GetAssetModuleResponse.class);
//        return assetModuleResponse.getAsset();
//    }

}
