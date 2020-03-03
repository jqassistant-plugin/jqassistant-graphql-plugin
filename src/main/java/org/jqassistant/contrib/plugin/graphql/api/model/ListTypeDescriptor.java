package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label("List")
public interface ListTypeDescriptor extends TypeDescriptor {

    @Relation
    @Outgoing
    OfElementTypeDescriptor getOfElementType();

}
