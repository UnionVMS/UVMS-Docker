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

import static org.hamcrest.CoreMatchers.is;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListCriteria;
import eu.europa.ec.fisheries.schema.movement.search.v1.ListPagination;
import eu.europa.ec.fisheries.schema.movement.search.v1.MovementQuery;
import eu.europa.ec.fisheries.schema.movement.search.v1.SearchKey;
import eu.europa.ec.fisheries.schema.movement.v1.MovementType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.AvailabilityType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.asset.client.model.AssetDTO;
import eu.europa.ec.fisheries.uvms.commons.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.docker.validation.asset.AssetTestHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRest;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.LatLong;
import eu.europa.ec.fisheries.uvms.docker.validation.movement.MovementHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleBuilder;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.CustomRuleHelper;
import eu.europa.ec.fisheries.uvms.docker.validation.system.helper.NAFHelper;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.rules.AlarmMovement;
import eu.europa.ec.fisheries.uvms.reporting.service.dto.rules.AlarmMovementList;

/**
 * The Class AlarmRestIT.
 */

public class AlarmRestIT extends AbstractRest {

	@Test
	public void getAlarmsTest() throws Exception {
		AlarmMovementList alarmMovementList = new AlarmMovementList();
		ArrayList<AlarmMovement> alarmMovementListContent = new ArrayList<AlarmMovement>();
		AlarmMovement alarmMovement = new AlarmMovement();
		alarmMovement.setMovementId("movementId");
		alarmMovement.setxCoordinate("57.715434");
		alarmMovement.setyCoordinate("11.970012");
		alarmMovementListContent.add(alarmMovement);	
		
		alarmMovementList.setAlarmMovementList(alarmMovementListContent);

		Response response = getWebTarget()
		        .path("reporting/rest/alarms")
		        .request(MediaType.APPLICATION_JSON)
		        .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
		        .post(Entity.json(alarmMovementList));
		assertThat(response.getStatus(), CoreMatchers.is(Status.OK.getStatusCode()));
	}
	
	@Test
    public void getTicketForMovementTest() throws Exception {
		try {
			// Create ticket
			AssetDTO asset = AssetTestHelper.createTestAsset();
			CustomRuleType customRule = CustomRuleBuilder.getBuilder()
					.rule(CriteriaType.ASSET, SubCriteriaType.FLAG_STATE,
							ConditionType.EQ, asset.getFlagStateCode())
					.setAvailability(AvailabilityType.GLOBAL)
					.build();
			CustomRuleType createdCustomRule = CustomRuleHelper.createCustomRule(customRule);
			NAFHelper.sendPositionToNAFPlugin(new LatLong(1, 1, new Date()), asset);

			MovementHelper.pollMovementCreated();
			// TODO find an alternative to Thread.sleep
			Thread.sleep(10000);

			// Get movement guid
			MovementQuery query = new MovementQuery();
			ListPagination pagination = new ListPagination();
			pagination.setListSize(BigInteger.TEN);
			pagination.setPage(BigInteger.ONE);
			query.setPagination(pagination);
			ListCriteria criteria = new ListCriteria();
			criteria.setKey(SearchKey.CONNECT_ID);
			criteria.setValue(asset.getId().toString());
			query.getMovementSearchCriteria().add(criteria);
			List<MovementType> movements = MovementHelper.getListByQuery(query);
			assertThat(movements.size(), is(1));

			// Query reporting
			AlarmMovementList alarmMovementList = new AlarmMovementList();
			ArrayList<AlarmMovement> alarmMovementListContent = new ArrayList<AlarmMovement>();
			AlarmMovement alarmMovement = new AlarmMovement();
			alarmMovement.setMovementId(movements.get(0).getGuid());
			alarmMovement.setxCoordinate("1");
			alarmMovement.setyCoordinate("1");
			alarmMovementListContent.add(alarmMovement);
			alarmMovementList.setAlarmMovementList(alarmMovementListContent);
			
			ResponseDto<ObjectNode> response = getWebTarget()
	                .path("reporting/rest/alarms")
	                .request(MediaType.APPLICATION_JSON)
	                .header(HttpHeaders.AUTHORIZATION, getValidJwtToken())
	                .post(Entity.json(alarmMovementList), new GenericType<ResponseDto<ObjectNode>>() {});

			JsonNode data = response.getData();
			JsonNode alarms = data.get("alarms");
			JsonNode features = alarms.get("features");
			assertThat(features.size(), is(1));
		} finally {
			CustomRuleHelper.removeCustomRulesByDefaultUser();
		}
    }
}
