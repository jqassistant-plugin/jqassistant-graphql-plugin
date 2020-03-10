package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("OF_ELEMENT_TYPE")
public interface OfElementTypeDescriptor extends OfTypeTemplate, Descriptor {

    @Outgoing
    ListTypeDescriptor getListType();

    @Incoming
    @Override
    TypeDescriptor getType();

}
