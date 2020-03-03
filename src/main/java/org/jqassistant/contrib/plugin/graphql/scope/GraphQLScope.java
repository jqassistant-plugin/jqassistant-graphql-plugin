package org.jqassistant.contrib.plugin.graphql.scope;

import com.buschmais.jqassistant.core.scanner.api.Scope;

public enum GraphQLScope implements Scope {

    SCHEMA;

    @Override
    public String getPrefix() {
        return "graphql";
    }

    @Override
    public String getName() {
        return this.name();
    }
}
