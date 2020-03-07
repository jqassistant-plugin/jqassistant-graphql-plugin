package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Label;

@Abstract
@Label("Schema")
public interface SchemaDescriptor extends GraphQLDescriptor {

    @ResultOf
    @Cypher("MATCH (schema:GraphQL:Schema) WHERE id(schema)={this} MERGE (schema)-[:DECLARES_ELEMENT]->(element:GraphQL:Named{name:{name}}) RETURN element")
    NamedElementDescriptor resolve(@Parameter("name") String name);

}
