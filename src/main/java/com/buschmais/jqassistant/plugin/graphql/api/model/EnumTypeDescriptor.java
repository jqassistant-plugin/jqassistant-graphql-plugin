package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface EnumTypeDescriptor extends EnumDescriptor, TypeDescriptor {

    @Relation("DECLARES_VALUE")
    List<EnumValueDescriptor> getDeclaresValues();

    @ResultOf
    @Cypher("MATCH (type:GraphQL:Type:Enum) WHERE id(type)=$this MERGE (type)-[:DECLARES_VALUE]->(value:GraphQL:Value:Enum{name:$name}) RETURN value")
    EnumValueDescriptor resolveValue(@Parameter("name") String name);
}
