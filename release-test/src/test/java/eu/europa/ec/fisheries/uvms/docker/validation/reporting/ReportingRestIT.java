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
package eu.europa.ec.fisheries.uvms.docker.validation.reporting;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.junit.Ignore;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.CommonFilterDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.DisplayFormat;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.LengthType;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.ReportDTO;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.report.VisibilityEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.ReportTypeEnum;
import eu.europa.ec.fisheries.uvms.reporting.service.enums.VelocityType;

/**
 * The Class ReportingRestIT.
 */

public class ReportingRestIT extends AbstractRestServiceTest {

	/**
	 * List reports test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void listReportsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "reporting/rest/report/list")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.execute().returnResponse();
		List dataMap = checkSuccessResponseReturnType(response,List.class);
		assertNotNull(dataMap);
	}

	/**
	 * List last executed reports test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void listLastExecutedReportsTest() throws Exception {
		final HttpResponse response = Request.Get(getBaseUrl() + "reporting/rest/report/list/lastexecuted/10")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.execute().returnResponse();
		List dataMap = checkSuccessResponseReturnType(response,List.class);
		assertNotNull(dataMap);
	}

	/**
	 * Gets the report test.
	 *
	 * @return the report test
	 * @throws Exception the exception
	 */
	@Test
	public void getReportTest() throws Exception {
		Long reportId = createTwoWeeksReport("createReportTest","TwoWeeksReports").getId();
		
		final HttpResponse response = Request.Get(getBaseUrl() + "reporting/rest/report/" + reportId)
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);
	}

		
	/**
	 * Creates the report test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void createReportTest() throws Exception {
		createTwoWeeksReport("createReportTest","TwoWeeksReports");
	}

	/**
	 * Creates the two weeks report.
	 *
	 * @param name the name
	 * @param description the description
	 * @return the report DTO
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClientProtocolException the client protocol exception
	 * @throws JsonProcessingException the json processing exception
	 * @throws JsonParseException the json parse exception
	 * @throws JsonMappingException the json mapping exception
	 */
	private ReportDTO createTwoWeeksReport(final String name,final String description) throws IOException, ClientProtocolException, JsonProcessingException,
			JsonParseException, JsonMappingException {
		ReportDTO reportDTO = new ReportDTO();
		long time = new Date().getTime();
		reportDTO.setDescription(name + time);
		reportDTO.setName(description + time);
		reportDTO.setReportTypeEnum(ReportTypeEnum.SUMMARY);
		reportDTO.setVisibility(VisibilityEnum.PRIVATE);
		
		
		
		CommonFilterDTO commonFilterDTO = new CommonFilterDTO();
		commonFilterDTO.setStartDate(new Date(new Date().getTime() - (60 * 1000 * 60 * 24 * 7)));
		commonFilterDTO.setEndDate(new Date(new Date().getTime() + (60 * 1000 * 60 * 24 * 7)));
		reportDTO.addFilter(commonFilterDTO);
		
		final HttpResponse response = Request.Post(getBaseUrl() + "reporting/rest/report?projection=DEFAULT")
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(reportDTO).getBytes()).execute().returnResponse();
		
		reportDTO.setId(checkSuccessResponseReturnType(response,Integer.class).longValue());
		
		return reportDTO;
	}
	

	/**
	 * Delete report test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void deleteReportTest() throws Exception {
		Long reportId = createTwoWeeksReport("deleteReportTest","TwoWeeksReports").getId();
		
		final HttpResponse response = Request.Delete(getBaseUrl() + "reporting/rest/report/" + reportId)
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}


	/**
	 * Update report test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void updateReportTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksReport("updateReportTest","TwoWeeksReports");
		twoWeeksReport.setDescription("new Description");

		final HttpResponse response = Request.Put(getBaseUrl() + "reporting/rest/report/" + twoWeeksReport.getId())
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(twoWeeksReport).getBytes()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	/**
	 * Share report test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void shareReportTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksReport("shareReportTest","TwoWeeksReports");
		twoWeeksReport.setDescription("new Description");

		final HttpResponse response = Request.Put(getBaseUrl() + "reporting/rest/report/share/" + twoWeeksReport.getId() + "/" + VisibilityEnum.PUBLIC)
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
	}

	/**
	 * Execute report with id test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	@Ignore
	public void executeReportWithIdTest() throws Exception {
		ReportDTO twoWeeksReport = createTwoWeeksReport("executeReportWithIdTest","TwoWeeksReports");

		DisplayFormat displayFormat = new DisplayFormat();
		displayFormat.setLengthType(LengthType.NM);
		displayFormat.setVelocityType(VelocityType.KTS);
		HashMap<String, Object> additionalProperties = new HashMap<String,Object>();
		HashMap<String, Object> valueMap = new HashMap<String,Object>();
		String timeStampValue = "2017-09-14T12:01:06";
		valueMap.put("timestamp", timeStampValue);
		additionalProperties.put("additionalProperties", valueMap);
		additionalProperties.put("timestamp", timeStampValue);
		
		displayFormat.setAdditionalProperties(additionalProperties);
		
		final HttpResponse response = Request.Post(getBaseUrl() + "reporting/rest/report/execute/" + twoWeeksReport.getId())
				.setHeader("Content-Type", "application/json").setHeader("scopeName", "All Reports")
				.setHeader("roleName", "AdminAll").setHeader("Authorization", getValidJwtToken())
				.bodyByteArray(writeValueAsString(displayFormat).getBytes()).execute().returnResponse();
		Map<String, Object> dataMap = checkSuccessResponseReturnMap(response);
		assertNotNull(dataMap);

	}

}

