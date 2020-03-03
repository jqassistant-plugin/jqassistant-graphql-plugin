package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("DECLARES_TYPE")
public interface UnionDeclaresTypeDescriptor extends Descriptor {

    int getIndex();

    void setIndex(int index);

    @Outgoing
    UnionTypeDescriptor getUntUnionType();

    @Incoming
    TypeDescriptor getType();

}
