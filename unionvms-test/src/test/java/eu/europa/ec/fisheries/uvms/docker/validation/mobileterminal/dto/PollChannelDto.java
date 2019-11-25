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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal.dto;

import java.util.List;

public class PollChannelDto {

	private String connectId;
	private String mobileTerminalId;
	private String mobileTerminalType;
	private String comChannelId;
	
	private PollDto poll;
	
	private List<AttributeDto> mobileTerminalAttributes;

	public String getConnectId() {
		return connectId;
	}

	public void setConnectId(String connectId) {
		this.connectId = connectId;
	}
	
	public String getMobileTerminalType() {
		return mobileTerminalType;
	}

	public void setMobileTerminalType(String mobileTerminalType) {
		this.mobileTerminalType = mobileTerminalType;
	}

	public String getComChannelId() {
		return comChannelId;
	}

	public void setComChannelId(String comChannelId) {
		this.comChannelId = comChannelId;
	}

	public List<AttributeDto> getMobileTerminalAttributes() {
		return mobileTerminalAttributes;
	}

	public void setMobileTerminalAttributes(List<AttributeDto> attributes) {
		this.mobileTerminalAttributes = attributes;
	}

	public String getMobileTerminalId() {
		return mobileTerminalId;
	}

	public void setMobileTerminalId(String mobileTerminalId) {
		this.mobileTerminalId = mobileTerminalId;
	}

	public PollDto getPoll() {
		return poll;
	}

	public void setPoll(PollDto poll) {
		this.poll = poll;
	}
}
