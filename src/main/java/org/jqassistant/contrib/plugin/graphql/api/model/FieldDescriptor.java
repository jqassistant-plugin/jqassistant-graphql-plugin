package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label("Field")
public interface FieldDescriptor extends GraphQLDescriptor, NamedDescriptor, DescriptionTemplate, InputValueContainerTemplate, DirectiveContainerTemplate {

    @Relation
    @Outgoing
    FieldOfTypeDescriptor getOfType();

}
