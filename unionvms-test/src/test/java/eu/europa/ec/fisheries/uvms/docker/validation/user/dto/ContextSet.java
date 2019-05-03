package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * Holds a set of Contexts.
 */
public class ContextSet implements Serializable {
    private static final long serialVersionUID = 1L;
    private Set<Context> contexts;

    /**
     * Creates a new instance
     */
    public ContextSet() {}

    /**
     * Get the value of contexts
     *
     * @return the value of contexts
     */
    public Set<Context> getContexts() {
        return contexts;
    }

    /**
     * Set the value of contexts
     *
     * @param contexts new value of contexts
     */
    public void setContexts(Set<Context> contexts) {
        this.contexts = contexts;
    }

    /**
     * Formats a human-readable view of this instance.
     * 
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "ContextSet{" + "contexts=" + contexts + '}';
    }
}
