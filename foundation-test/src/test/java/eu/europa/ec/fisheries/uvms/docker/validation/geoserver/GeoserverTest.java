package eu.europa.ec.fisheries.uvms.docker.validation.geoserver;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

/**
 * The Class GeoserverTest.
 */
public class GeoserverTest extends AbstractRestServiceTest {
	
	/**
	 * Verify protected uvms layers test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifyProtectedUvmsLayersTest() throws Exception {
		HttpResponse httpResponse = Request.Get("http://localhost:28080/geoserver/uvms/wms?service=WMS&version=1.1.0&request=GetMap&layers=uvms:port&styles=&bbox=-180.0,-90.0,180.0,90.0&width=768&height=384&srs=EPSG:4326&format=application/openlayers").execute().returnResponse();
		assertEquals(HttpStatus.SC_FORBIDDEN, httpResponse.getStatusLine().getStatusCode());
	}

	/**
	 * Verify protected uvms layers access with jwt header test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifyProtectedUvmsLayersAccessWithJwtHeaderTest() throws Exception {
		HttpResponse httpResponse = Request.Get("http://localhost:28080/geoserver/uvms/wms?service=WMS&version=1.1.0&request=GetMap&layers=uvms:port&styles=&bbox=-180.0,-90.0,180.0,90.0&width=768&height=384&srs=EPSG:4326&format=application/openlayers").setHeader("Authorization", getValidJwtToken()).execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		assertEquals("SAMEORIGIN", httpResponse.getFirstHeader("X-Frame-Options").getValue());
						
	}
}
