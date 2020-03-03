package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

import java.util.List;

@Label("Type")
public interface TypeDescriptor extends GraphQLDescriptor, NamedElementDescriptor, DirectiveContainerTemplate {

    @Relation
    @Incoming
    List<FieldOfTypeDescriptor> getFieldOfTypes();

    @Relation
    @Incoming
    List<InputValueOfTypeDescriptor> getArgumentOfTypes();

    @Relation
    @Incoming
    List<OfElementTypeDescriptor> getOfElementTypes();

    @Relation
    @Incoming
    List<UnionDeclaresTypeDescriptor> getUnionDeclaresTypes();
}
