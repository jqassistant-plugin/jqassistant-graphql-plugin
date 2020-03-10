package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface DirectiveValueDescriptor extends ValueDescriptor, DirectiveDescriptor {

    @Relation("OF_TYPE")
    DirectiveTypeDescriptor getOfType();

    void setOfType(DirectiveTypeDescriptor directiveType);

    @Relation("HAS_ARGUMENT")
    List<ArgumentDescriptor> getHasArguments();

}
