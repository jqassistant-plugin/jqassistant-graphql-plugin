package com.buschmais.jqassistant.plugin.graphql.api.model;

public interface OfTypeTemplate {

    TypeDescriptor getType();

    boolean isNonNull();

    void setNonNull(boolean nonNull);

}
