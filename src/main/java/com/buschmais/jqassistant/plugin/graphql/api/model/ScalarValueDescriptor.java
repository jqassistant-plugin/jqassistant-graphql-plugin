package com.buschmais.jqassistant.plugin.graphql.api.model;

public interface ScalarValueDescriptor extends ValueDescriptor, ScalarDescriptor {

    Object getValue();

    void setValue(Object object);

}
