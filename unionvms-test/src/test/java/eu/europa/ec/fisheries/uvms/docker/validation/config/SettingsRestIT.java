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
import java.util.Set;
import java.util.UUID;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingType;
import eu.europa.ec.fisheries.schema.config.types.v1.SettingsCreateQuery;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class SettingsRestIT.
 */

public class SettingsRestIT extends AbstractRest {

	/**
	 * Gets the by module name test.
	 *
	 * @return the by module name test
	 * @throws Exception the exception
	 */
	@Test
	public void getByModuleNameTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "config/rest/settings?moduleName=audit")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

	
	/**
	 * Gets the by module name all modules test.
	 *
	 * @return the by module name all modules test
	 * @throws Exception the exception
	 */
	@Test
	public void getByModuleNameAllModulesTest() throws Exception {
		String validJwtToken = getValidJwtToken();
		final HttpResponse response = Request.Get(getBaseUrl() + "config/rest/catalog")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", validJwtToken).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		Set<String> modules = dataMap.keySet();

		for (String module : modules) {
			final HttpResponse moduleResponse = Request.Get(getBaseUrl() + "config/rest/settings?moduleName=" + module)
					.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
					.returnResponse();
			List dataList = checkSuccessResponseReturnType(moduleResponse,List.class);			
		}
		
	}

	
	/**
	 * Gets the by id test.
	 *
	 * @return the by id test
	 * @throws Exception the exception
	 */
	@Test
	public void getByIdTest() throws Exception {
		SettingType settingType = createTestSettingType();
		assertNotNull(settingType);

		final HttpResponse response = Request.Get(getBaseUrl() + "config/rest/settings/"+ settingType.getId())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Delete test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void deleteTest() throws Exception {
		SettingType settingType = createTestSettingType();
		assertNotNull(settingType);
		
		final HttpResponse response = Request.Delete(getBaseUrl() + "config/rest/settings/" + settingType.getId())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void updateTest() throws Exception {
		SettingType settingType = createTestSettingType();
		assertNotNull(settingType);

		settingType.setDescription("Updated Desc" + UUID.randomUUID().toString());
		
		final HttpResponse response = Request.Put(getBaseUrl() + "config/rest/settings/" + settingType.getId())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(settingType).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Creates the test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createTest() throws Exception {
		SettingType settingType = createTestSettingType();
		assertNotNull(settingType);
	}

	/**
	 * Creates the test setting type.
	 *
	 * @return the setting type
	 * @throws Exception the exception
	 */
	private SettingType createTestSettingType() throws Exception {
		SettingsCreateQuery settingsCreateQuery = new SettingsCreateQuery();
		settingsCreateQuery.setModuleName("audit");
		SettingType settingType = new SettingType();
		settingType.setDescription("SettingsRestIt" + UUID.randomUUID().toString());
		settingType.setGlobal(false);
		settingType.setKey("audit.key.SettingsRestIt." + UUID.randomUUID().toString());
		settingType.setValue(UUID.randomUUID().toString());
		
		settingsCreateQuery.setSetting(settingType);
		final HttpResponse response = Request.Post(getBaseUrl() + "config/rest/settings")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(settingsCreateQuery).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		settingType.setId(Long.valueOf("" +dataMap.get("id")));
		return settingType;
	}

	/**
	 * Catalog test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void catalogTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "config/rest/catalog")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the pings test.
	 *
	 * @return the pings test
	 * @throws Exception the exception
	 */
	@Test
	public void getPingsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "config/rest/pings")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the global settings test.
	 *
	 * @return the global settings test
	 * @throws Exception the exception
	 */
	@Test
	public void getGlobalSettingsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "config/rest/globals")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		List dataList = checkSuccessResponseReturnType(response,List.class);
	}

}
