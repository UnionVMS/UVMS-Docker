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
package eu.europa.ec.fisheries.uvms.docker.validation.user;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Context;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Preferences;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.UserContext;
import eu.europa.ec.fisheries.wsdl.user.types.UserPreference;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class UserContextIT extends AbstractRest {

    @Test
    public void putUserContext() {
        
        UserPreference userPreference = new UserPreference();
        userPreference.setApplicationName("VMSFrontend");
        userPreference.setOptionName("settings");
        userPreference.setOptionValue("cepa".getBytes());
        userPreference.setUserName("vms_admin_se");
        userPreference.setRoleName("AdminAllUVMS");
        userPreference.setScopeName("All Vessels");
        
        Response resp = getWebTarget()
            .path("user/rest/user/putPreference")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
            .put(Entity.json(userPreference));
        
        UserContext response = getWebTarget()
                .path("usm-administration/rest/userContexts")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .get(UserContext.class);
            
        for (Context context : response.getContextSet().getContexts()) {
            for (Preferences pref : context.getPreferences().getPreferences()) {
                if (pref.getApplicationName().equals("VMSFrontend")) {
                    assertThat(pref.getOptionValue(), CoreMatchers.is("cepa"));
                }
            }
        }
    }
    
    @Test
    public void getUserContext() {
        UserContext response = getWebTarget()
            .path("usm-administration/rest/userContexts")
            .request(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, getValidJwtToken("vms_admin_se", "password"))
            .get(UserContext.class);
        
        assertNotNull(response);
    }
}
