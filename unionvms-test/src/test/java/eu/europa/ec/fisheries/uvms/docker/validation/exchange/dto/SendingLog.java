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
package eu.europa.ec.fisheries.uvms.docker.validation.exchange.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
public class SendingLog {

	@XmlElement(required = true)
	private String messageId;
	@XmlElement(required = true)
	private String dateRecieved;
	@XmlElement(required = true)
	private String senderRecipient;
    @XmlElement(required = false)
    private Map<String, String> properties;

	public String getMessageId() {
		return messageId;
	}

    public void setMessageId(String id) {
		this.messageId = id;
	}
    public String getDateRecieved() {
		return dateRecieved;
	}
    public void setDateRecieved(String dateRecieved) {
		this.dateRecieved = dateRecieved;
	}
    public String getSenderRecipient() {
		return senderRecipient;
	}
    public void setSenderRecipient(String senderRecipient) {
		this.senderRecipient = senderRecipient;
	}
    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}