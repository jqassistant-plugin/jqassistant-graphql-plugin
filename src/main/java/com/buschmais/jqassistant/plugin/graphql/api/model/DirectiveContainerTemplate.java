package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface DirectiveContainerTemplate {

    @Relation("DECLARES_DIRECTIVE")
    List<DirectiveValueDescriptor> getDeclaresDirectives();

}
