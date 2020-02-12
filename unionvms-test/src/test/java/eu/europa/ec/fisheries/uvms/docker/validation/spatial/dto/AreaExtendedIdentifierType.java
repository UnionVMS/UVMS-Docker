package eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto;

import eu.europa.ec.fisheries.uvms.spatial.model.schemas.AreaType;

public class AreaExtendedIdentifierType {

    protected String id;
    //@XmlElement(name = "AreaType", required = true)
    //@XmlSchemaType(name = "string")
    protected AreaType areaType;

    protected String code;
    //@XmlElement(name = "Name", required = true)
    protected String name;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AreaType getAreaType() {
        return areaType;
    }

    public void setAreaType(AreaType areaType) {
        this.areaType = areaType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
