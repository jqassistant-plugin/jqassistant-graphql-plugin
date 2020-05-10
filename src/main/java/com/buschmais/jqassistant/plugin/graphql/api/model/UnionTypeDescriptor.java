package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

/* tag::doc[]

[[GraphQLInterfaceType]]
==  Union Type

Represents a GraphQL Union xref:GraphQLType[Type].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Union Type]
ifndef::iov[| Used labels]
| `:GraphQL:Union:Type`

end::labeloverview[]

|===

end::doc[] */
@Label
public interface UnionTypeDescriptor extends TypeDescriptor {

/* tag::doc[]

.Relations of an Union Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `DECLARES_TYPE`
| xref:GraphQLUnionDeclaresType[]
| 1..n
| References the declared GraphQL fields
end::doc[] */
    @Relation
    @Outgoing
    List<UnionDeclaresTypeDescriptor> getUnionDeclaresType();

/* tag::doc[]
|===
end::doc[] */

}
