package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * Holds a Role based on a set a features from any applications. The goal is to define the actions that can be performed
 * on data;.
 */
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;
    private String roleName;
    private Set<Feature> features;

    /**
     * Creates a new instance
     */
    public Role() {}

    /**
     * Get the value of roleName
     *
     * @return the value of roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * Set the value of roleName
     *
     * @param roleName new value of roleName
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * Get the value of features
     *
     * @return the value of features
     */
    public Set<Feature> getFeatures() {
        return features;
    }

    /**
     * Set the value of features
     *
     * @param features new value of features
     */
    public void setFeatures(Set<Feature> features) {
        this.features = features;
    }

    /**
     * Formats a human-readable view of this instance.
     * 
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "Role{" + "roleName=" + roleName + ", features=" + features + '}';
    }
}
