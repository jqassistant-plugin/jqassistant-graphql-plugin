package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.api.annotation.ResultOf.Parameter;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/* tag::doc[]

[[GraphQLEnumType]]
==  Enum Type

Represents a GraphQL Enum xref:Type[Type].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Enum Type]
ifndef::iov[| Used labels]
| `:GraphQL:Enum:Type`

end::labeloverview[]

|===

end::doc[] */
public interface EnumTypeDescriptor extends EnumDescriptor, TypeDescriptor {

/* tag::doc[]

.Relations of an Enum Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `DECLARES_VALUE`
| xref:GraphQLEnumValue[Enum Value]
| 1
| References an enum value declared by the type.
end::doc[] */
    @Relation("DECLARES_VALUE")
    List<EnumValueDescriptor> getDeclaresValues();

/* tag::doc[]
|===
end::doc[] */

    @ResultOf
    @Cypher("MATCH (type:GraphQL:Type:Enum) WHERE id(type)=$this MERGE (type)-[:DECLARES_VALUE]->(value:GraphQL:Value:Enum{name:$name}) RETURN value")
    EnumValueDescriptor resolveValue(@Parameter("name") String name);
}
