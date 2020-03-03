package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface InputValueContainerTemplate {

    @Relation("DECLARES_INPUT_VALUE")
    List<InputValueDescriptor> getInputValues();

}
