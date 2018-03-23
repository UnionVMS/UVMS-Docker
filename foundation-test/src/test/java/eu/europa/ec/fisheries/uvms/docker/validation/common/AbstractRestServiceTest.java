/*

Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
� European Union, 2017.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

*/
package eu.europa.ec.fisheries.uvms.docker.validation.common;

import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import lombok.SneakyThrows;
import org.junit.Before;

/**
 * The Class AbstractRestServiceTest.
 */
public abstract class AbstractRestServiceTest extends AbstractRest {

    protected HashMap authenticateMap = new HashMap();

    @Before
    @SneakyThrows
    public void init(){

        com.mashape.unirest.http.HttpResponse<String> stringHttpResponse =
                Unirest.post("http://localhost:28080/unionvms/usm-administration/rest/authenticate")
                        .header("Content-Type", "application/json")
                        .header("Cache-Control", "no-cache")
                        .header("Postman-Token", "7b47305b-d8a2-4e2c-aa36-28ab8250670e")
                        .body("{\n   \"userName\": \"rep_power\",\n   \"password\": \"abcd-1234\"\n}")
                        .asString();


        authenticateMap = new ObjectMapper().readValue(stringHttpResponse.getBody(), HashMap.class);

    }


}
