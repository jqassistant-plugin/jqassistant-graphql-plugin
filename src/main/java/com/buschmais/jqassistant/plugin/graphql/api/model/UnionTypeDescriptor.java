package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Label
public interface UnionTypeDescriptor extends TypeDescriptor {

    @Relation
    @Outgoing
    List<UnionDeclaresTypeDescriptor> getUnionDeclaresType();
}
