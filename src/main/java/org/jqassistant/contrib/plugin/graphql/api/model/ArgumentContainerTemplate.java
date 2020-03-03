package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface ArgumentContainerTemplate {

    @Relation("DECLARES_ARGUMENT")
    List<ArgumentDescriptor> getArguments();

}
