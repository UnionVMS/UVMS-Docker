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
package eu.europa.ec.fisheries.uvms.docker.validation.user;

import java.io.IOException;
import java.util.Map;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuthenticationRequest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuthenticationResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.ChallengeResponse;

/**
 * UserHelper
 */
public class UserHelper extends AbstractRest {

	public static AuthenticationResponse authenticate(String user, String password) throws ClientProtocolException, IOException {
	    AuthenticationRequest userPwd = new AuthenticationRequest(user,password);
		
	    return getWebTarget()
                .path("usm-administration/rest/authenticate")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(userPwd), AuthenticationResponse.class);
	}
	
	
	public static ChallengeResponse getChallenge(String jwtoken) throws ClientProtocolException, IOException{
		ChallengeResponse challengeResponse = getWebTarget()
                .path("usm-administration/rest/challenge")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, jwtoken)
                .get(ChallengeResponse.class);
		
		assertNotNull(challengeResponse.getChallenge());
		assertEquals("vms_admin_com", challengeResponse.getUserName());
		return challengeResponse;
	}
	

}
