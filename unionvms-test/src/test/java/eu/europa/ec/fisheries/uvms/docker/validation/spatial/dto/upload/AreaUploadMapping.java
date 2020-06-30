/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.upload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaUploadMapping {

    @JsonProperty("mapping")
    @Valid
    private List<AreaUploadMappingProperty> mapping = new ArrayList<AreaUploadMappingProperty>();
    //@JsonAnySetter
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public AreaUploadMapping() {
    }

    /**
     * 
     * @param mapping
     */
    public AreaUploadMapping(List<AreaUploadMappingProperty> mapping) {
        this.mapping = mapping;
    }

    /**
     * 
     * @return
     *     The mapping
     */
    @JsonProperty("mapping")
    public List<AreaUploadMappingProperty> getMapping() {
        return mapping;
    }

    /**
     * 
     * @param mapping
     *     The mapping
     */
    @JsonProperty("mapping")
    public void setMapping(List<AreaUploadMappingProperty> mapping) {
        this.mapping = mapping;
    }

    public AreaUploadMapping withMapping(List<AreaUploadMappingProperty> mapping) {
        this.mapping = mapping;
        return this;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    //@JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public AreaUploadMapping withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(mapping).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof AreaUploadMapping) == false) {
            return false;
        }
        AreaUploadMapping rhs = ((AreaUploadMapping) other);
        return new EqualsBuilder().append(mapping, rhs.mapping).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}