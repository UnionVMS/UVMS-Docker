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

import java.time.Instant;
import java.util.Date;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdList;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.IdType;
import eu.europa.ec.fisheries.schema.exchange.movement.mobileterminal.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementBaseType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementComChannelType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementPoint;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementSourceType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.MovementTypeType;
import eu.europa.ec.fisheries.schema.exchange.movement.v1.SetReportMovementType;
import eu.europa.ec.fisheries.schema.exchange.plugin.types.v1.PluginType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.MessageHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto.MobileTerminalDto;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.exchange.model.mapper.ExchangeModuleRequestMapper;

public class SiriusOnePluginMock {
    
    public static void sendSiriusOnePosition(MobileTerminalDto mobileTerminal, LatLong position) throws Exception {
        MovementBaseType movement = new MovementBaseType();
        
        movement.setComChannelType(MovementComChannelType.MOBILE_TERMINAL);

        MobileTerminalId mobTermId = new MobileTerminalId();
        IdList deviceId = new IdList();
        deviceId.setType(IdType.SERIAL_NUMBER);
        deviceId.setValue(mobileTerminal.getSerialNo());
        mobTermId.getMobileTerminalIdList().add(deviceId);
        movement.setMobileTerminalId(mobTermId);

        movement.setMovementType(MovementTypeType.POS);

        MovementPoint mp = new MovementPoint();
        mp.setAltitude(null);
        mp.setLatitude(position.latitude);
        mp.setLongitude(position.longitude);
        movement.setPosition(mp);

        movement.setPositionTime(position.positionTime);

        movement.setReportedCourse(position.bearing);

        movement.setReportedSpeed(position.speed);

        movement.setSource(MovementSourceType.IRIDIUM);

        movement.setLesReportTime(new Date());
        
        SetReportMovementType reportType = new SetReportMovementType();
        reportType.setMovement(movement);

        reportType.setPluginName("eu.europa.ec.fisheries.uvms.plugins.iridium.siriusone");

        reportType.setTimestamp(new Date());

        reportType.setPluginType(PluginType.SATELLITE_RECEIVER);
        
        String text = ExchangeModuleRequestMapper.createSetMovementReportRequest(reportType, "SIRIUSONE", null, Instant.now(), PluginType.SATELLITE_RECEIVER, "SIRIUSONE", null);
        try (MessageHelper messageHelper = new MessageHelper()) {
            messageHelper.sendMessage("UVMSExchangeEvent", text);
        }
    }
}
