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
package eu.europa.ec.fisheries.uvms.docker.validation.system.helper;

import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetId;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdList;
import eu.europa.ec.fisheries.schema.exchange.movement.asset.v1.AssetIdType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.*;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;

import java.time.Instant;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class AisPluginMock {

    public static void sendAisPosition(AssetDTO asset, LatLong position) throws Exception {
        MovementBaseType movement = new MovementBaseType();
        AssetId assetId = new AssetId();
        AssetIdList mmsi = new AssetIdList();
        mmsi.setIdType(AssetIdType.MMSI);
        mmsi.setValue(asset.getMmsi());
        assetId.getAssetIdList().add(mmsi);
        movement.setAssetId(assetId);

        movement.setMovementType(MovementTypeType.POS);
        MovementPoint point = new MovementPoint();
        point.setAltitude(0.0);
        point.setLatitude(position.latitude);
        point.setLongitude(position.longitude);
        movement.setPosition(point);

        movement.setPositionTime(position.positionTime);
        movement.setReportedCourse(position.bearing);
        movement.setReportedSpeed(position.speed);
        movement.setSource(MovementSourceType.AIS);

        SetReportMovementType reportType = new SetReportMovementType();
        reportType.setMovement(movement);
        GregorianCalendar gcal = (GregorianCalendar) GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        reportType.setTimestamp(gcal.getTime());
        reportType.setPluginName("eu.europa.ec.fisheries.uvms.plugins.ais");
        reportType.setPluginType(PluginType.OTHER);

        String text = ExchangeModuleRequestMapper.createSetMovementReportRequest(reportType, "AIS", null, Instant.now(), PluginType.OTHER, "AIS", null);
        try (MessageHelper messageHelper = new MessageHelper()) {
            messageHelper.sendMessage("UVMSExchangeEvent", text);
        }
    }
}