package com.buschmais.jqassistant.plugin.graphql.api.model;

public interface OfTypeTemplate {

    TypeDescriptor getType();

    boolean isRequired();

    void setRequired(boolean required);

}
