package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Abstract
@Label("Schema")
public interface SchemaDescriptor extends GraphQLDescriptor {

    @Relation("DECLARES_TYPE")
    List<TypeDescriptor> getDeclaresTypes();

    @Relation("REQUIRES_TYPE")
    List<TypeDescriptor> getRequiresTypes();
}
