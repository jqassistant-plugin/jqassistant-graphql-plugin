package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

public interface EnumTypeDescriptor extends NamedTypeDescriptor, EnumDescriptor {

    @Relation("DECLARES_VALUE")
    List<EnumValueDescriptor> getDeclaresValues();

    @ResultOf
    @Cypher("MATCH (type:GraphQL:Type:Enum) WHERE id(type)={this} MERGE (type)-[:DECLARES_VALUE]->(value:GraphQL:Value:Enum{name:{name}}) RETURN value")
    EnumValueDescriptor resolveValue(@Parameter("name") String name);
}
