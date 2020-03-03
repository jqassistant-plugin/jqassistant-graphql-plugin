package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("Argument")
public interface ArgumentDescriptor extends GraphQLDescriptor, NamedElementDescriptor {

    int getIndex();

    void setIndex(int index);

    Object getValue();

    void setValue(Object value);

}
