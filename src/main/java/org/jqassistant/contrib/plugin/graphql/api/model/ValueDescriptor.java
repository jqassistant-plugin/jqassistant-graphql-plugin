package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Abstract
@Label("Value")
public interface ValueDescriptor extends GraphQLDescriptor {
}
