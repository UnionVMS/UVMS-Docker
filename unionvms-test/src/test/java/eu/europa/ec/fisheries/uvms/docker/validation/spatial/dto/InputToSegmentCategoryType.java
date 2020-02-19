package eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto;

import eu.europa.ec.fisheries.schema.movement.v1.MovementPoint;

import java.util.Date;

//Copy with the relevant bits from movementType that getSegmentCategoryType in spatial needs to function
public class InputToSegmentCategoryType {
    protected MovementPoint position;
    protected Date positionTime;


    public MovementPoint getPosition() {
        return position;
    }

    public void setPosition(MovementPoint position) {
        this.position = position;
    }

    public Date getPositionTime() {
        return positionTime;
    }

    public void setPositionTime(Date positionTime) {
        this.positionTime = positionTime;
    }
}
