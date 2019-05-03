package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;

/**
 * Holds a Feature.
 * <ul>
 * <li>It can be optionally associated to a group to identify rapidly all features acting on a same business entity: ex:
 * feature "create organisation" to the group "organisation";</li>
 * <li>It is attached to the application in which it has been defined;</li>
 * <li>A feature is defined in the configuration file of that application and delivered to USM at deployment time;</li>
 * <li>A feature is not managed (CRUD) by USM but by the application exposed it;</li>
 * </ul>
 */
public class Feature implements Serializable {
    private static final long serialVersionUID = 1L;
    private String applicationName;
    private String featureName;

    /**
     * Creates a new instance
     */
    public Feature() {}

    /**
     * Get the value of applicationName
     *
     * @return the value of applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Set the value of applicationName
     *
     * @param applicationName new value of applicationName
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Get the value of featureName
     *
     * @return the value of featureName
     */
    public String getFeatureName() {
        return featureName;
    }

    /**
     * Set the value of featureName
     *
     * @param featureName new value of featureName
     */
    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    /**
     * Formats a human-readable view of this instance.
     * 
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "Feature{" + "applicationName=" + applicationName + ", featureName=" + featureName + '}';
    }

}
