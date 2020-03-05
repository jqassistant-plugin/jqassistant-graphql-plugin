package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface DirectiveTypeDescriptor extends NamedTypeDescriptor, DirectiveDescriptor, InputValueContainerTemplate {

    @Relation("DECLARES_LOCATION")
    List<DirectiveLocationDescriptor> getDeclaresLocations();


}
