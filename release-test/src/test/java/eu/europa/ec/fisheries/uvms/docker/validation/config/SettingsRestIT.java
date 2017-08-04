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
package eu.europa.ec.fisheries.uvms.docker.validation.config;

import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingsCreateQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class SettingsRestIT.
 */

public class SettingsRestIT extends AbstractRestServiceTest {

	@Test
	public void getByModuleNameTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/settings?moduleName=audit")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	@Test
	public void getByIdTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/settings/1")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	@Ignore
	public void deleteTest() throws Exception {
		final HttpResponse response = Request.Delete(BASE_URL + "config/rest/settings/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	@Ignore
	public void updateTest() throws Exception {
		SettingType settingType = new SettingType();
		final HttpResponse response = Request.Put(BASE_URL + "config/rest/settings/{id}")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(settingType).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	@Ignore
	public void createTest() throws Exception {
		SettingsCreateQuery settingsCreateQuery = new SettingsCreateQuery();

		final HttpResponse response = Request.Post(BASE_URL + "config/rest/settings")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(settingsCreateQuery).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	public void catalogTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/catalog")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	public void getPingsTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/pings")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	@Test
	public void getGlobalSettingsTest() throws Exception {
		final HttpResponse response = Request.Get(BASE_URL + "config/rest/globals")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

}
