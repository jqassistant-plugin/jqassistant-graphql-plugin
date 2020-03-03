package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface FieldContainerTemplate {

    @Relation("DECLARES_FIELD")
    List<FieldDescriptor> getFields();
}
