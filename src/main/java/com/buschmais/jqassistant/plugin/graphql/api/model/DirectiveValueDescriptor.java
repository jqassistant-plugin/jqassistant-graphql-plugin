package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface DirectiveValueDescriptor extends ValueDescriptor, DirectiveDescriptor {

    @Relation("OF_TYPE")
    DirectiveTypeDescriptor getOfType();

    void setOfType(DirectiveTypeDescriptor directiveType);

    @Relation("HAS_ARGUMENT")
    List<ArgumentDescriptor> getHasArguments();

}
