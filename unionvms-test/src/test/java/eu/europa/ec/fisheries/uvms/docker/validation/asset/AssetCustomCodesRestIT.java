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
package eu.europa.ec.fisheries.uvms.docker.validation.asset;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

public class AssetCustomCodesRestIT extends AbstractRest {
    
    @Test
    public void listConstantsTest() throws Exception {
        HttpResponse response = Request.Get(getBaseUrl() + "asset/rest/customcodes/listconstants")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
                .returnResponse();
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

}
