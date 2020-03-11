package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface InputValueContainerTemplate {

    @Relation("DECLARES_INPUT_VALUE")
    List<InputValueDescriptor> getInputValues();

    @ResultOf
    @Cypher("MATCH (container) WHERE id(container)={this} MERGE (container)-[:DECLARES_INPUT_VALUE]->(inputValue:GraphQL:Input:Value{name:{name}}) RETURN inputValue")
    InputValueDescriptor resolveInputValue(@Parameter("name") String name);

}
