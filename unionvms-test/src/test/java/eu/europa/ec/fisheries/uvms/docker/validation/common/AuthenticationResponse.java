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
package eu.europa.ec.fisheries.uvms.docker.validation.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class AuthenticationResponse {
    
    private boolean authenticated;
    private int statusCode;
    private String errorDescription;
    private Map<String, Object> userMap;
    @JsonProperty("JWToken")
    private String JWToken;
    
    public boolean isAuthenticated() {
        return authenticated;
    }
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getErrorDescription() {
        return errorDescription;
    }
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
    public Map<String, Object> getUserMap() {
        return userMap;
    }
    public void setUserMap(Map<String, Object> userMap) {
        this.userMap = userMap;
    }
    public String getJWToken() {
        return JWToken;
    }
    public void setJWToken(String JWToken) {
        this.JWToken = JWToken;
    }
}