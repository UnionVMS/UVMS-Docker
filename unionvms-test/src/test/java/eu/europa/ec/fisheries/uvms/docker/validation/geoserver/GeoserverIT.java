package eu.europa.ec.fisheries.uvms.docker.validation.geoserver;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.junit.Test;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;

/**
 * The Class GeoserverTest.
 */
public class GeoserverIT extends AbstractRest {

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
	
	/**
	 * Verify access control allow origin test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifyAccessControlAllowOriginTest() throws Exception {
		HttpResponse httpResponse = Request.Get("http://localhost:28080/geoserver/web/").setHeader("Authorization", getValidJwtToken()).setHeader("Origin", "http://www.example.com").execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		assertEquals("SAMEORIGIN", httpResponse.getFirstHeader("X-Frame-Options").getValue());
		assertEquals("http://www.example.com", httpResponse.getFirstHeader("Access-Control-Allow-Origin").getValue());
		assertEquals("true", httpResponse.getFirstHeader("Access-Control-Allow-Credentials").getValue());
				
	}

	/**
	 * Verify options allow test.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void verifyOptionsAllowTest() throws Exception {
		HttpResponse httpResponse = Request.Options("http://localhost:28080/geoserver/web/").setHeader("Authorization", getValidJwtToken()).setHeader("Origin", "http://www.example.com").setHeader("Access-Control-Request-Method","POST").setHeader("Access-Control-Request-Headers", "content-type,accept").execute().returnResponse();
		assertEquals(HttpStatus.SC_OK, httpResponse.getStatusLine().getStatusCode());
		
		assertEquals("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH", httpResponse.getFirstHeader("Allow").getValue());
		assertEquals("http://www.example.com", httpResponse.getFirstHeader("Access-Control-Allow-Origin").getValue());
		assertEquals("true", httpResponse.getFirstHeader("Access-Control-Allow-Credentials").getValue());
		assertEquals("GET,POST,HEAD",httpResponse.getFirstHeader("Access-Control-Allow-Methods").getValue());
		assertEquals("X-Requested-With,Content-Type,Accept,Origin",httpResponse.getFirstHeader("Access-Control-Allow-Headers").getValue());
	}
}
