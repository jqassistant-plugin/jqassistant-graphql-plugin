package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface DirectiveContainerTemplate {

    @Relation("DECLARES_DIRECTIVE")
    List<DirectiveValueDescriptor> getDeclaresDirectives();

}
