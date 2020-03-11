package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

@Label("URL")
public interface SchemaUrlDescriptor extends SchemaDescriptor {

    String getURL();

    void setURL(String URL);

}
