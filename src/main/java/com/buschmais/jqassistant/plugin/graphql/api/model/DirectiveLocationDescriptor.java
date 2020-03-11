package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Location")
public interface DirectiveLocationDescriptor extends DirectiveDescriptor, NamedDescriptor {
}
