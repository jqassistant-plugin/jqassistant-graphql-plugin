package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface DirectiveTypeDescriptor extends DirectiveDescriptor, InputValueContainerTemplate, TypeDescriptor {

    @Relation("DECLARES_LOCATION")
    List<DirectiveLocationDescriptor> getDeclaresLocations();

}
