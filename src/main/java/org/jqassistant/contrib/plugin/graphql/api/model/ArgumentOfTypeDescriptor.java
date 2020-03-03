package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("OF_TYPE")
public interface ArgumentOfTypeDescriptor extends RequiredTemplate, Descriptor {

    @Outgoing
    ArgumentDescriptor getArgument();

    @Incoming
    TypeDescriptor getType();

}
