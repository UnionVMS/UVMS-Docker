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
package eu.europa.ec.fisheries.uvms.docker.validation.mobileterminal;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelCapability;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ComChannelType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.ListPagination;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAssignQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalAttribute;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalId;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalListQuery;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSearchCriteria;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalSource;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.MobileTerminalType;
import eu.europa.ec.fisheries.schema.mobileterminal.types.v1.Plugin;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class MobileTerminalRestIT.
 */

public class MobileTerminalRestIT extends AbstractRestServiceTest {

	/**
	 * Creates the mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void createMobileTerminalTest() throws Exception {
		createMobileTerminalType();
	}

	private MobileTerminalType createMobileTerminalType() throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {
		MobileTerminalType mobileTerminalRequest = new MobileTerminalType();
		mobileTerminalRequest.setSource(MobileTerminalSource.INTERNAL);
		mobileTerminalRequest.setType("INMARSAT_C");
		List<MobileTerminalAttribute> attributes = mobileTerminalRequest.getAttributes();
		addAttribute(attributes,"SERIAL_NUMBER", AssetTestHelper.generateARandomStringWithMaxLength(10));
		addAttribute(attributes,"SATELLITE_NUMBER", "S" + AssetTestHelper.generateARandomStringWithMaxLength(4));
		addAttribute(attributes,"ANTENNA", "A");
		addAttribute(attributes,"TRANSCEIVER_TYPE", "A");
		addAttribute(attributes,"SOFTWARE_VERSION", "A");
		
		List<ComChannelType> channels = mobileTerminalRequest.getChannels();
		ComChannelType comChannelType = new ComChannelType();	
		channels.add(comChannelType);
		comChannelType.setGuid(UUID.randomUUID().toString());
		comChannelType.setName("VMS");
		
		addChannelAttribute(comChannelType, "FREQUENCY_GRACE_PERIOD","54000");
		addChannelAttribute(comChannelType, "MEMBER_NUMBER","100");
		addChannelAttribute(comChannelType, "FREQUENCY_EXPECTED","7200");
		addChannelAttribute(comChannelType, "FREQUENCY_IN_PORT","10800");
		addChannelAttribute(comChannelType, "LES_DESCRIPTION","twostage");
		addChannelAttribute(comChannelType, "DNID","1" + AssetTestHelper.generateARandomStringWithMaxLength(3));
		addChannelAttribute(comChannelType, "INSTALLED_BY","Mike Great");		
				
		addChannelCapability(comChannelType, "POLLABLE", true);
		addChannelCapability(comChannelType, "CONFIGURABLE", true);
		addChannelCapability(comChannelType, "DEFAULT_REPORTING", true);
		
		Plugin plugin = new Plugin();
		plugin.setServiceName("eu.europa.ec.fisheries.uvms.plugins.inmarsat");
		plugin.setLabelName("twostage");
		plugin.setSatelliteType("INMARSAT_C");
		plugin.setInactive(false);
		
		mobileTerminalRequest.setPlugin(plugin);		
		
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/mobileterminal")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(mobileTerminalRequest).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		
		
		Map<String, Object> assetMap = (Map<String, Object>) dataMap.get("mobileTerminalId");
		assertNotNull(assetMap);
		String mobileTerminalGuid = (String) assetMap.get("guid");
		assertNotNull(mobileTerminalGuid);

		mobileTerminalRequest.setId((Integer)dataMap.get("id"));
		
		MobileTerminalId mobileTerminalId = new MobileTerminalId();
		mobileTerminalId.setGuid(mobileTerminalGuid);
		mobileTerminalRequest.setMobileTerminalId(mobileTerminalId);
		return mobileTerminalRequest;
	}

	private void addChannelCapability(ComChannelType comChannelType, String type, boolean value) {
		ComChannelCapability channelCapability = new ComChannelCapability();

		channelCapability.setType(type);
		channelCapability.setValue(value);
		comChannelType.getCapabilities().add(channelCapability);
	}

	private void addChannelAttribute(ComChannelType comChannelType, String type,String value) {
		ComChannelAttribute channelAttribute = new ComChannelAttribute();
		channelAttribute.setType(type);
		channelAttribute.setValue(value);
		comChannelType.getAttributes().add(channelAttribute);
	}

	private void addAttribute(List<MobileTerminalAttribute> attributes, String type,String value) {
		MobileTerminalAttribute serialNumberMobileTerminalAttribute = new MobileTerminalAttribute();
		serialNumberMobileTerminalAttribute.setType(type);
		serialNumberMobileTerminalAttribute.setValue(value);		
		attributes.add(serialNumberMobileTerminalAttribute);
	}

	/**
	 * Gets the mobile terminal by id test.
	 *
	 * @return the mobile terminal by id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMobileTerminalByIdTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();
		
		final HttpResponse response = Request.Get(BASE_URL + "mobileterminal/rest/mobileterminal/" + createdMobileTerminalType.getMobileTerminalId().getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Update mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void updateMobileTerminalTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();
		
		createdMobileTerminalType.setArchived(true);
		
		final HttpResponse response = Request.Put(BASE_URL + "mobileterminal/rest/mobileterminal?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the mobile terminal list test.
	 *
	 * @return the mobile terminal list test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMobileTerminalListTest() throws Exception {
		MobileTerminalListQuery queryRequest = new MobileTerminalListQuery();
		ListPagination pagination = new ListPagination();
		pagination.setListSize(100);
		pagination.setPage(1);
		queryRequest.setPagination(pagination);
		MobileTerminalSearchCriteria criteria = new MobileTerminalSearchCriteria();
		criteria.setIsDynamic(true);
		queryRequest.setMobileTerminalSearchCriteria(criteria);
		
		final HttpResponse response = Request.Post(BASE_URL + "mobileterminal/rest/mobileterminal/list")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(queryRequest).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Assign mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void assignMobileTerminalTest() throws Exception {
		final HttpResponse response = Request
				.Post(BASE_URL + "mobileterminal/rest/mobileterminal/assign?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalAssignQuery()).getBytes()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Un assign mobile terminal test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	@Ignore
	public void unAssignMobileTerminalTest() throws Exception {
		final HttpResponse response = Request
				.Post(BASE_URL + "mobileterminal/rest/mobileterminal/unassign?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(new MobileTerminalAssignQuery()).getBytes()).execute()
				.returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Sets the status active test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void setStatusActiveTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();

		final HttpResponse response = Request
				.Put(BASE_URL + "mobileterminal/rest/mobileterminal/status/activate?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType.getMobileTerminalId()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Sets the status inactive test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void setStatusInactiveTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();

		final HttpResponse response = Request
				.Put(BASE_URL + "mobileterminal/rest/mobileterminal/status/inactivate?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType.getMobileTerminalId()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Sets the status removed test.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void setStatusRemovedTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();

		final HttpResponse response = Request
				.Put(BASE_URL + "mobileterminal/rest/mobileterminal/status/remove?comment=comment")
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(createdMobileTerminalType.getMobileTerminalId()).getBytes()).execute().returnResponse();

		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

	/**
	 * Gets the mobile terminal history list by mobile terminal id test.
	 *
	 * @return the mobile terminal history list by mobile terminal id test
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void getMobileTerminalHistoryListByMobileTerminalIdTest() throws Exception {
		MobileTerminalType createdMobileTerminalType = createMobileTerminalType();

		final HttpResponse response = Request.Get(BASE_URL + "mobileterminal/rest/mobileterminal/history/" + createdMobileTerminalType.getMobileTerminalId().getGuid())
				.setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
				.returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
	}

}
