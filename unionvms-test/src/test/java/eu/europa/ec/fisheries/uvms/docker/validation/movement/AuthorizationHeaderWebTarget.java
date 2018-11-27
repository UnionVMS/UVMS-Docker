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
package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.net.URI;
import java.util.Map;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

public class AuthorizationHeaderWebTarget implements WebTarget {

    private WebTarget base;
    private String token;

    public AuthorizationHeaderWebTarget(WebTarget base, String token) {
        this.base = base;
        this.token = token;
    }

    // Inject that cookie whenever someone requests a Builder (like EventSource does):
    public Invocation.Builder request() {
        return base.request().header(HttpHeaders.AUTHORIZATION, token);
    }

    public Invocation.Builder request(String... paramArrayOfString) {
        return base.request(paramArrayOfString).header(HttpHeaders.AUTHORIZATION, token);
    }

    public Invocation.Builder request(MediaType... paramArrayOfMediaType) {
        return base.request(paramArrayOfMediaType).header(HttpHeaders.AUTHORIZATION, token);
    }

    public Configuration getConfiguration() {
        return base.getConfiguration();
    }

    // All other methods from WebTarget are delegated as-is:
    public URI getUri() {
        return base.getUri();
    }

    public UriBuilder getUriBuilder() {
        return base.getUriBuilder();
    }

    public WebTarget path(String paramString) {
        return base.path(paramString);
    }

    public WebTarget matrixParam(String paramString, Object... paramArrayOfObject) {
        return base.matrixParam(paramString, paramArrayOfObject);
    }

    public WebTarget property(String paramString, Object paramObject) {
        return base.property(paramString, paramObject);
    }

    public WebTarget queryParam(String paramString, Object... paramArrayOfObject) {
        return base.queryParam(paramString, paramArrayOfObject);
    }

    public WebTarget register(Class<?> paramClass, Class<?>... paramArrayOfClass) {
        return base.register(paramClass, paramArrayOfClass);
    }

    public WebTarget register(Class<?> paramClass, int paramInt) {
        return base.register(paramClass, paramInt);
    }

    public WebTarget register(Class<?> paramClass, Map<Class<?>, Integer> paramMap) {
        return base.register(paramClass, paramMap);
    }

    public WebTarget register(Class<?> paramClass) {
        return base.register(paramClass);
    }

    public WebTarget register(Object paramObject, Class<?>... paramArrayOfClass) {
        return base.register(paramObject, paramArrayOfClass);
    }

    public WebTarget register(Object paramObject, int paramInt) {
        return base.register(paramObject, paramInt);
    }

    public WebTarget register(Object paramObject, Map<Class<?>, Integer> paramMap) {
        return base.register(paramObject, paramMap);
    }

    public WebTarget register(Object paramObject) {
        return base.register(paramObject);
    }

    public WebTarget resolveTemplate(String paramString, Object paramObject) {
        return base.resolveTemplate(paramString, paramObject);
    }

    public WebTarget resolveTemplate(String paramString, Object paramObject, boolean paramBoolean) {
        return base.resolveTemplate(paramString, paramObject, paramBoolean);
    }

    public WebTarget resolveTemplateFromEncoded(String paramString, Object paramObject) {
        return base.resolveTemplateFromEncoded(paramString, paramObject);
    }

    public WebTarget resolveTemplates(Map<String, Object> paramMap) {
        return base.resolveTemplates(paramMap);
    }

    public WebTarget resolveTemplates(Map<String, Object> paramMap, boolean paramBoolean) {
        return base.resolveTemplates(paramMap, paramBoolean);
    }

    public WebTarget resolveTemplatesFromEncoded(Map<String, Object> paramMap) {
        return base.resolveTemplatesFromEncoded(paramMap);
    }
}
