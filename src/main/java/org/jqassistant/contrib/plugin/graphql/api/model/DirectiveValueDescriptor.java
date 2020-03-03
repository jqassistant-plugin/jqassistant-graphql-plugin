package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface DirectiveValueDescriptor extends ValueDescriptor, DirectiveDescriptor, ArgumentContainerTemplate {

    @Relation("OF_TYPE")
    DirectiveTypeDescriptor getOfType();

    void setOfType(DirectiveTypeDescriptor directiveType);

}
