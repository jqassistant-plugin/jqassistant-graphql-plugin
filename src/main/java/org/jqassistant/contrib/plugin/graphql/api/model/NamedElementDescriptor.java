package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Named")
public interface NamedElementDescriptor extends GraphQLDescriptor, NamedDescriptor, DescriptionTemplate {

}
