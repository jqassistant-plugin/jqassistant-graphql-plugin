package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label("Field")
public interface FieldDescriptor extends GraphQLDescriptor, NamedElementDescriptor, DeprecatedTemplate, ArgumentContainerTemplate, DirectiveContainerTemplate {

    @Relation
    @Outgoing
    FieldOfTypeDescriptor getOfType();

}
