package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

import java.util.List;

public interface NamedTypeDescriptor extends TypeDescriptor, NamedElementDescriptor, DirectiveContainerTemplate {

    @Relation
    @Incoming
    List<InputValueOfTypeDescriptor> getInputValueOfTypes();

}
