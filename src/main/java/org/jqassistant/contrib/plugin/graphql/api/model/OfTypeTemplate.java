package org.jqassistant.contrib.plugin.graphql.api.model;

public interface OfTypeTemplate {

    TypeDescriptor getType();

    boolean isRequired();

    void setRequired(boolean required);

}
