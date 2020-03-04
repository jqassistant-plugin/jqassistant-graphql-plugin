package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;

@Label
public interface UnionTypeDescriptor extends NamedTypeDescriptor {

    @Relation
    @Outgoing
    List<UnionDeclaresTypeDescriptor> getUnionDeclaresType();
}
