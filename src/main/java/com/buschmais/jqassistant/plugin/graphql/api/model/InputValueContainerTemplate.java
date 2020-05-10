package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface InputValueContainerTemplate {

/* tag::relations[]
| `DEFINES_INPUT_VALUE`
| xref:GraphQLInputValueDefinition[Input Value Definition]
| 0..n
| References the declared GraphQL input value definitions
end::relations[] */
    @Relation("DEFINES_INPUT_VALUE")
    List<InputValueDefinitionDescriptor> getInputValues();

    @ResultOf
    @Cypher("MATCH (container) WHERE id(container)=$this MERGE (container)-[:DEFINES_INPUT_VALUE]->(inputValue:GraphQL:Input:ValueDefinition{name:$name}) RETURN inputValue")
    InputValueDefinitionDescriptor resolveInputValue(@Parameter("name") String name);

}
