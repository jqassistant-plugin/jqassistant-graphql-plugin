package org.jqassistant.contrib.plugin.graphql.api.model;

public interface DeprecatedTemplate {

    boolean isDeprecated();

    void setDeprecated(boolean deprecated);

    String getDeprecationReason();

    void setDeprecationReason(String deprecationReason);

}
