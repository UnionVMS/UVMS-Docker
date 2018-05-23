/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.system.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.ActionType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.AvailabilityType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.ConditionType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CriteriaType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CustomRuleActionType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CustomRuleSegmentType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.CustomRuleType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.LogicOperatorType;
import eu.europa.ec.fisheries.schema.rules.customrule.v1.SubCriteriaType;
import eu.europa.ec.fisheries.uvms.docker.validation.common.AbstractRestServiceTest;

public class CustomRuleHelper extends AbstractRestServiceTest {

    private static final String userName = "vms_admin_com";
    
    public static CustomRuleType sendAllFSToFLUXEndpoint(String flagState, String endpoint) throws Exception {
        CustomRuleType customRule = new CustomRuleType();

        customRule.setName("Flag " + flagState + " => Send to " + endpoint + " (" + System.currentTimeMillis() + ")");
        customRule.setAvailability(AvailabilityType.PRIVATE);
        customRule.setUpdatedBy(userName);
        customRule.setActive(true);
        customRule.setArchived(false);

        // Flag state
        CustomRuleSegmentType flagStateRule = new CustomRuleSegmentType();
        flagStateRule.setStartOperator("(");
        flagStateRule.setCriteria(CriteriaType.ASSET);
        flagStateRule.setSubCriteria(SubCriteriaType.FLAG_STATE);
        flagStateRule.setCondition(ConditionType.EQ);
        flagStateRule.setValue(flagState);
        flagStateRule.setEndOperator(")");
        flagStateRule.setLogicBoolOperator(LogicOperatorType.NONE);
        flagStateRule.setOrder("0");
        customRule.getDefinitions().add(flagStateRule);

        // Send to FLUX
        CustomRuleActionType action = new CustomRuleActionType();
        action.setAction(ActionType.SEND_TO_FLUX);
        action.setValue(endpoint);
        action.setOrder("0");

        customRule.getActions().add(action);

        final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/customrules")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(customRule).getBytes()).execute().returnResponse();

        return checkSuccessResponseReturnObject(response, CustomRuleType.class);
    }
    
    public static CustomRuleType sendAllFSInAreaToFLUXEndpoint(String flagState, String areaCode, String endpoint) throws Exception {
        CustomRuleType customRule = new CustomRuleType();

        customRule.setName("Flag SWE && area DNK => Send to DNK" + " (" + System.currentTimeMillis() + ")");
        customRule.setAvailability(AvailabilityType.PRIVATE);
        customRule.setUpdatedBy(userName);
        customRule.setActive(true);
        customRule.setArchived(false);

        // Flag state
        CustomRuleSegmentType flagStateRule = new CustomRuleSegmentType();
        flagStateRule.setStartOperator("(");
        flagStateRule.setCriteria(CriteriaType.ASSET);
        flagStateRule.setSubCriteria(SubCriteriaType.FLAG_STATE);
        flagStateRule.setCondition(ConditionType.EQ);
        flagStateRule.setValue(flagState);
        flagStateRule.setEndOperator(")");
        flagStateRule.setLogicBoolOperator(LogicOperatorType.AND);
        flagStateRule.setOrder("0");
        customRule.getDefinitions().add(flagStateRule);

        // Area
        CustomRuleSegmentType areaRule = new CustomRuleSegmentType();
        areaRule.setStartOperator("(");
        areaRule.setCriteria(CriteriaType.AREA);
        areaRule.setSubCriteria(SubCriteriaType.AREA_CODE);
        areaRule.setCondition(ConditionType.EQ);
        areaRule.setValue(areaCode);
        areaRule.setEndOperator(")");
        areaRule.setLogicBoolOperator(LogicOperatorType.NONE);
        areaRule.setOrder("1");
        customRule.getDefinitions().add(areaRule);

        // Send to FLUX
        CustomRuleActionType action = new CustomRuleActionType();
        action.setAction(ActionType.SEND_TO_FLUX);
        action.setValue(endpoint);
        action.setOrder("0");

        customRule.getActions().add(action);

        final HttpResponse response = Request.Post(getBaseUrl() + "rules/rest/customrules")
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .bodyByteArray(writeValueAsString(customRule).getBytes()).execute().returnResponse();

        return checkSuccessResponseReturnObject(response, CustomRuleType.class);
    }
    
    public static void removeCustomRule(String guid) throws Exception {
        HttpResponse response = Request.Delete(getBaseUrl() + "rules/rest/customrules/" + guid)
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken())
                .execute().returnResponse();
        
        checkSuccessResponseReturnDataMap(response);
    }

    public static void assertRuleTriggered(CustomRuleType rule, Date dateFrom) throws Exception {
        HttpResponse response = Request.Get(getBaseUrl() + "rules/rest/customrules/" + rule.getGuid())
                .setHeader("Content-Type", "application/json").setHeader("Authorization", getValidJwtToken()).execute()
                .returnResponse();

        CustomRuleType fetchedCustomRule = checkSuccessResponseReturnObject(response, CustomRuleType.class);
        
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss X");
        Date lastTriggered = formatter.parse(fetchedCustomRule.getLastTriggered());
        
        assertTrue(lastTriggered.getTime() > dateFrom.getTime());
    }
}
