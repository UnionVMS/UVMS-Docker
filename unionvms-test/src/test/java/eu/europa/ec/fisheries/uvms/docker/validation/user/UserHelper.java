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
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.http.client.ClientProtocolException;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuthenticationRequest;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AuthenticationResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.ChallengeResponse;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Channel;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.EndPoint;
import eu.europa.ec.fisheries.uvms.docker.validation.user.dto.Organisation;

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
	
	public static Organisation createOrganisation(Organisation organisation) {
	    return getWebTarget()
                .path("usm-administration/rest/organisations")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .header("roleName", "AdminAllUVMS")
                .header("scopeName", "All Vessels")
                .post(Entity.json(organisation), Organisation.class);
	}

	public static Organisation getOrganisation(Long organisationId) {
        return getWebTarget()
                .path("usm-administration/rest/organisations")
                .path(organisationId.toString())
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .header("roleName", "AdminAllUVMS")
                .header("scopeName", "All Vessels")
                .get(Organisation.class);
    }

	public static EndPoint createEndpoint(EndPoint endpoint) {
        return getWebTarget()
                .path("usm-administration/rest/endpoint")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
                .header("roleName", "AdminAllUVMS")
                .header("scopeName", "All Vessels")
                .post(Entity.json(endpoint), EndPoint.class);
    }

	public static Channel createChannel(Channel channel) {
        return getWebTarget()
                .path("usm-administration/rest/channel")
                .request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken("usm_bootstrap", "password"))
                .header("roleName", "AdminAllUVMS")
                .header("scopeName", "All Vessels")
                .post(Entity.json(channel), Channel.class);
    }

    public static Organisation getBasicOrganisation() {
        Organisation organisation = new Organisation();
        organisation.setName("Name " + generateARandomStringWithMaxLength(10));
        organisation.setNation("SWE");
        organisation.setStatus("E");
        return organisation;
    }
}
