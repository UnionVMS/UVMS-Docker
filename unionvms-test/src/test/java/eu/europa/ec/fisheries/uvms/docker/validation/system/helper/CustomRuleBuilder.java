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

import eu.europa.ec.fisheries.schema.movementrules.customrule.v1.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class CustomRuleBuilder {

    protected static final String DEFAULT_USER = "vms_admin_se";

    private int segmentOrder = 0;
    private int actionOrder = 0;
    
    private CustomRuleType customRule;
    
    private CustomRuleBuilder() {
        customRule = new CustomRuleType();
        customRule.setName("Rule: " + System.currentTimeMillis());
        customRule.setAvailability(AvailabilityType.PRIVATE);
        customRule.setUpdatedBy(DEFAULT_USER);
        customRule.setActive(true);
        customRule.setArchived(false);
    }
    
    public static CustomRuleBuilder getBuilder() {
        return new CustomRuleBuilder();
    }
    
    public CustomRuleBuilder setName(String name) {
        customRule.setName(name + " " + System.currentTimeMillis());
        return this;
    }
    
    public CustomRuleBuilder setUser(String username) {
        customRule.setUpdatedBy(username);
        return this;
    }
    
    public CustomRuleBuilder setAvailability(AvailabilityType availabilityType) {
        customRule.setAvailability(availabilityType);
        return this;
    }

    public CustomRuleBuilder rule(CriteriaType criteriaType, SubCriteriaType subCriteriaType, ConditionType conditionType, String value) {
        CustomRuleSegmentType segment = new CustomRuleSegmentType();
        segment.setStartOperator("");
        segment.setCriteria(criteriaType);
        segment.setSubCriteria(subCriteriaType);
        segment.setCondition(conditionType);
        segment.setValue(value);
        segment.setEndOperator("");
        segment.setLogicBoolOperator(LogicOperatorType.NONE);
        segment.setOrder(String.valueOf(segmentOrder));
        segmentOrder++;
        customRule.getDefinitions().add(segment);
        return this;
    }
   
    public CustomRuleBuilder and(CriteriaType criteriaType, SubCriteriaType subCriteriaType, ConditionType conditionType, String value) {
        List<CustomRuleSegmentType> definitions = customRule.getDefinitions();
        if (!definitions.isEmpty()) {
            CustomRuleSegmentType previousSegment = definitions.get(definitions.size() - 1);
            previousSegment.setLogicBoolOperator(LogicOperatorType.AND);
        }
        return rule(criteriaType, subCriteriaType, conditionType, value);
    }
    
    public CustomRuleBuilder or(CriteriaType criteriaType, SubCriteriaType subCriteriaType, ConditionType conditionType, String value) {
        List<CustomRuleSegmentType> definitions = customRule.getDefinitions();
        if (!definitions.isEmpty()) {
            CustomRuleSegmentType previousSegment = definitions.get(definitions.size() - 1);
            previousSegment.setLogicBoolOperator(LogicOperatorType.OR);
        }
        return rule(criteriaType, subCriteriaType, conditionType, value);
    }
    
    public CustomRuleBuilder interval(Date start, Date end) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        CustomRuleIntervalType customRuleIntervalType = new CustomRuleIntervalType();
        customRuleIntervalType.setStart(formatter.format(start));
        customRuleIntervalType.setEnd(formatter.format(end));
        customRule.getTimeIntervals().add(customRuleIntervalType);
        return this;
    }
    
    public CustomRuleBuilder action(ActionType actionType, String value) {
        CustomRuleActionType action = new CustomRuleActionType();
        action.setAction(actionType);
        action.setValue(value);
        action.setOrder(String.valueOf(actionOrder));
        actionOrder++;
        customRule.getActions().add(action);
        return this;
    }
    
    public CustomRuleType build() {
        return customRule;
    }
}