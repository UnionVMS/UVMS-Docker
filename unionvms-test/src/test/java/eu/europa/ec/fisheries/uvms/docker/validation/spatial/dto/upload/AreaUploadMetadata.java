/*
Developed by the European Commission - Directorate General for Maritime Affairs and Fisheries @ European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can redistribute it 
and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of 
the License, or any later version. The IFDM Suite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more 
details. You should have received a copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.

 */
package eu.europa.ec.fisheries.uvms.docker.validation.spatial.dto.upload;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreaUploadMetadata {

    @Valid
    private List<AreaUploadProperty> domain = new ArrayList<>();
    @Valid
    private List<AreaUploadProperty> file = new ArrayList<>();
    //@JsonIgnore
    //@JsonAnySetter
    private Map<String, Object> additionalProperties = new HashMap<>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public AreaUploadMetadata() {
    }

    /**
     * 
     * @param domain
     * @param file
     */
    public AreaUploadMetadata(List<AreaUploadProperty> domain, List<AreaUploadProperty> file) {
        this.domain = domain;
        this.file = file;
    }

    /**
     * 
     * @return
     *     The domain
     */
    public List<AreaUploadProperty> getDomain() {
        return domain;
    }

    /**
     * 
     * @param domain
     *     The database-properties
     */
    public void setDomain(List<AreaUploadProperty> domain) {
        this.domain = domain;
    }

    public AreaUploadMetadata withDatabaseProperties(List<AreaUploadProperty> databaseProperties) {
        this.domain = databaseProperties;
        return this;
    }

    /**
     * 
     * @return
     *     The file
     */
    public List<AreaUploadProperty> getFile() {
        return file;
    }

    /**
     * 
     * @param file
     *     The file
     */
    public void setFile(List<AreaUploadProperty> file) {
        this.file = file;
    }

    public AreaUploadMetadata withFileProperties(List<AreaUploadProperty> file) {
        this.file = file;
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

    public void setAdditionalProperty(String name, String value) {
        this.additionalProperties.put(name, value);
    }

    public AreaUploadMetadata withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(domain).append(file).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AreaUploadMetadata)) {
            return false;
        }
        AreaUploadMetadata rhs = ((AreaUploadMetadata) other);
        return new EqualsBuilder().append(domain, rhs.domain).append(file, rhs.file).append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}