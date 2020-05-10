package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

/* tag::doc[]

[[GraphQLListType]]
== List Type

Represents a GraphQL list xref:GraphQLType[Type] that declares an element xref:GraphQLType[Type].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  List Type]
ifndef::iov[| Used labels]
| `:GraphQL:List:Type`

end::labeloverview[]

|===

end::doc[] */
@Label("List")
public interface ListTypeDescriptor extends TypeDescriptor {

/* tag::doc[]

.Relations of a GraphQL List Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node Type
| Cardinality
| Description
end::doc[] */

/* tag::doc[]
| `OF_ELEMENT_TYPE`
| xref:GraphQLOfElementType[Of Type (Relation)]
| 1
| References the field type
end::doc[] */
    @Relation
    @Outgoing
    OfElementTypeDescriptor getOfElementType();

/* tag::doc[]
|===
end::doc[] */

}
