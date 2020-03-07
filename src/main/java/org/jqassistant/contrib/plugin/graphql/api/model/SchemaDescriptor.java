package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Abstract
@Label("Schema")
public interface SchemaDescriptor extends GraphQLDescriptor {

    @Relation("DECLARES")
    List<NamedElementDescriptor> getDeclares();

    @Relation("REQUIRES")
    List<NamedElementDescriptor> getRequires();
}
