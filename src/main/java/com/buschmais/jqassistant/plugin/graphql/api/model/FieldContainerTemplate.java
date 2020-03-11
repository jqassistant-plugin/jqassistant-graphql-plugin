package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface FieldContainerTemplate {

    @Relation("DECLARES_FIELD")
    List<FieldDescriptor> getFields();
}
