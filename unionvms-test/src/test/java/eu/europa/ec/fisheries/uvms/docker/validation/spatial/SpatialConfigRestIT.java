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
package eu.europa.ec.fisheries.uvms.docker.validation.spatial;

import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.ConfigurationDto;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.MapConfigDto;
import eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.ProjectionDto;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class SpatialConfigRestIT extends AbstractRest {

	@Test
	public void getReportMapConfig() {
		String uid = "rep_power";
		String pwd = "abcd-1234";
		String scopeName = "EC";
		String roleName = "rep_power_role";

		String id = "1";

		ConfigResourceDto dto = new ConfigResourceDto();
		dto.setTimeStamp(new Date().toString());

		ResponseDto<MapConfigDto> response = getWebTarget()
		        .path("spatial/rest/config")
                .path(id)
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken(uid, pwd))
                .header(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
                .header(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
                .post(Entity.json(dto), new GenericType<ResponseDto<MapConfigDto>>() {});

		assertThat(response, is(notNullValue()));
		assertThat(response.getData(), is(notNullValue()));
		assertThat(response.getData().getMap(), is(notNullValue()));
		assertThat(response.getData().getMap().getProjection(), is(notNullValue()));
		assertThat(response.getData().getMap().getProjection().getEpsgCode(), is(3857));
	}

	@Test
	public void getBasicReportMapConfig() {
		String uid = "rep_power";
		String pwd = "abcd-1234";
		String scopeName = "EC";
		String roleName = "rep_power_role";

		ResponseDto<MapConfigDto> response = getWebTarget()
                .path("spatial/rest/config")
                .path("basic")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken(uid, pwd))
                .header(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
                .header(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
                .get(new GenericType<ResponseDto<MapConfigDto>>() {});

        assertThat(response, is(notNullValue()));
        assertThat(response.getData(), is(notNullValue()));
        assertThat(response.getData().getMap(), is(notNullValue()));
        assertThat(response.getData().getMap().getProjection(), is(notNullValue()));
        assertThat(response.getData().getMap().getProjection().getEpsgCode(), is(3857));
	}

	@Test
	public void getAllProjectionsTest() {
		String uid = "rep_power";
		String pwd = "abcd-1234";
		String scopeName = "EC";
		String roleName = "rep_power_role";

		ResponseDto<List<ProjectionDto>> response = getWebTarget()
                .path("spatial/rest/config")
                .path("projections")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken(uid, pwd))
                .header(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
                .header(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
                .get(new GenericType<ResponseDto<List<ProjectionDto>>>() {});

        assertThat(response, is(notNullValue()));
        assertThat(response.getData(), is(notNullValue()));
        assertThat(response.getData().size(), is(2));

        assertTrue(response.getData().stream().anyMatch(proj -> proj.getName().equals("Spherical Mercator")));
        assertTrue(response.getData().stream().anyMatch(proj -> proj.getName().equals("WGS 84")));
	}

	@Test
	public void getAdminPreferences() {
		String uid = "rep_power";
		String pwd = "abcd-1234";
		String scopeName = "EC";
		String roleName = "rep_power_role";

		ResponseDto<ConfigurationDto> response = getWebTarget()
		        .path("spatial/rest/config")
		        .path("admin")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken(uid, pwd))
                .header(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
                .header(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
                .get(new GenericType<ResponseDto<ConfigurationDto>>() {});

		assertThat(response, is(notNullValue()));
        assertThat(response.getData(), is(notNullValue()));

        assertThat(response.getData().getSystemSettings(), is(notNullValue()));
        assertThat(response.getData().getSystemSettings().getGeoserverUrl(), is("http://localhost:28080/geoserver/"));
	}

	@Test
	public void getUserPreferences() {
		String uid = "rep_power";
		String pwd = "abcd-1234";
		String scopeName = "EC";
		String roleName = "rep_power_role";

		ResponseDto<ConfigurationDto> response = getWebTarget()
				.path("spatial/rest/config")
				.path("user")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken(uid, pwd))
				.header(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.header(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.get(new GenericType<ResponseDto<ConfigurationDto>>() {});

		assertThat(response, is(notNullValue()));
		assertThat(response.getData(), is(notNullValue()));
	}

	@Test
	public void getReportConfig() {
		String uid = "rep_power";
		String pwd = "abcd-1234";
		String scopeName = "EC";
		String roleName = "rep_power_role";

		Response response = getWebTarget()
				.path("spatial/rest/config")
				.path("report")
				.request(MediaType.APPLICATION_JSON)
				.header(HttpHeaders.AUTHORIZATION, getValidJwtToken(uid, pwd))
				.header(AuthConstants.HTTP_HEADER_SCOPE_NAME, scopeName)
				.header(AuthConstants.HTTP_HEADER_ROLE_NAME, roleName)
				.get();

		assertNotNull(response);
		assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
	}
}
