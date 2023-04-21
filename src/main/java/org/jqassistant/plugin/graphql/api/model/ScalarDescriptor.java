package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Abstract
@Label("Scalar")
public interface ScalarDescriptor extends GraphQLDescriptor {
}
