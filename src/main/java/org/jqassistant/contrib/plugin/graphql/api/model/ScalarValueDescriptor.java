package org.jqassistant.contrib.plugin.graphql.api.model;

public interface ScalarValueDescriptor extends ValueDescriptor, ScalarDescriptor {

    Object getValue();

    void setValue(Object object);

}
