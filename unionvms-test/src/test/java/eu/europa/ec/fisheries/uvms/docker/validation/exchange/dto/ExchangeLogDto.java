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

import java.util.List;

import eu.europa.ec.fisheries.schema.exchange.v1.RelatedLogInfo;

public class ExchangeLogDto {

    private String id;
    private boolean incoming;
    private String dateRecieved;
    private String senderRecipient;
    private String source;
    private String rule;
    private String recipient;
    private String dateFwd;
    private String status;
    private String to;
    private String todt;
    private String on;
    private String df;
    private String type;
    private String typeRefType;
    private ExchangeLogData logData;
    private List<RelatedLogInfo> relatedLogData;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean isIncoming() {
        return incoming;
    }
    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
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
    public void setSenderRecipient(String from) {
        this.senderRecipient = from;
    }
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public String getRule() {
        return rule;
    }
    public void setRule(String rule) {
        this.rule = rule;
    }
    public String getRecipient() {
        return recipient;
    }
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    public String getDateFwd() {
        return dateFwd;
    }
    public void setDateFwd(String dateFwd) {
        this.dateFwd = dateFwd;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public ExchangeLogData getLogData() {
        return logData;
    }
    public void setLogData(ExchangeLogData logData) {
        this.logData = logData;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTypeRefType() {
        return typeRefType;
    }
    public void setTypeRefType(String typeRefType) {
        this.typeRefType = typeRefType;
    }
    public List<RelatedLogInfo> getRelatedLogData() {
        return relatedLogData;
    }
    public void setRelatedLogData(List<RelatedLogInfo> relatedLogData) {
        this.relatedLogData = relatedLogData;
    }
    public String getTo() {
        return to;
    }
    public void setTo(String to) {
        this.to = to;
    }
    public String getTodt() {
        return todt;
    }
    public void setTodt(String todt) {
        this.todt = todt;
    }
    public String getOn() {
        return on;
    }
    public void setOn(String on) {
        this.on = on;
    }
    public String getDf() {
        return df;
    }
    public void setDf(String df) {
        this.df = df;
    }
}
