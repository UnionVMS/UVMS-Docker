/*
 * ﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
 * © European Union, 2015-2016. This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM
 * Suite is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should
 * have received a copy of the GNU General Public License along with the IFDM Suite. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto;

import java.util.ArrayList;
import java.util.List;
import javax.json.bind.annotation.JsonbProperty;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaType;
import eu.europa.ec.fisheries.uvms.spatial.model.schemas.UnitType;

public class ClosestAreaSpatialRQ extends SpatialModuleRequest {

    @JsonbProperty("Point")
    protected PointType point;
    @JsonbProperty("AreaTypes")
    protected ClosestAreaSpatialRQ.AreaTypes areaTypes;
    @JsonbProperty("Unit")
    protected UnitType unit;

    public PointType getPoint() {
        return point;
    }

    public void setPoint(PointType value) {
        this.point = value;
    }

    public ClosestAreaSpatialRQ.AreaTypes getAreaTypes() {
        return areaTypes;
    }

    public void setAreaTypes(ClosestAreaSpatialRQ.AreaTypes value) {
        this.areaTypes = value;
    }

    public UnitType getUnit() {
        return unit;
    }

    public void setUnit(UnitType value) {
        this.unit = value;
    }

    public static class AreaTypes {

        @JsonbProperty("AreaType")
        protected List<AreaType> areaTypes;

        public List<AreaType> getAreaTypes() {
            if (areaTypes == null) {
                areaTypes = new ArrayList<AreaType>();
            }
            return this.areaTypes;
        }
    }

}
