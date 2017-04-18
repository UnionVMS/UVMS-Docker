package eu.europa.ec.fisheries.uvms.docker.validation;

import org.apache.http.client.fluent.Request;
import org.junit.Assert;
import org.junit.Test;

public class ModuleAvailableSystemTest extends Assert {

	private final String BASE_URL = "http://localhost:28080/";

	@Test
	public void checkUnionVmsWebAccessTest() throws Exception {
		assertEquals(200,Request.Get(BASE_URL + "unionvms/").execute().returnResponse().getStatusLine().getStatusCode());
	}

	@Test
	public void checkMovementAccessTest() throws Exception {
		assertEquals(403,Request.Get(BASE_URL + "movement/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(403,Request.Get(BASE_URL + "movement/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	@Test
	public void checkSpatialAccessTest() throws Exception {
		assertEquals(403,Request.Get(BASE_URL + "spatial/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(403,Request.Get(BASE_URL + "spatial/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	@Test
	public void checkRulesAccessTest() throws Exception {
		assertEquals(403,Request.Get(BASE_URL + "rules/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(403,Request.Get(BASE_URL + "rules/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}
	
	@Test
	public void checkAuditAccessTest() throws Exception {
		assertEquals(403,Request.Get(BASE_URL + "audit/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(403,Request.Get(BASE_URL + "audit/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	@Test
	public void checkAssetAccessTest() throws Exception {
		assertEquals(403,Request.Get(BASE_URL + "asset/").execute().returnResponse().getStatusLine().getStatusCode());
		assertEquals(403,Request.Get(BASE_URL + "asset/rest").execute().returnResponse().getStatusLine().getStatusCode());		
	}

	
	
}
