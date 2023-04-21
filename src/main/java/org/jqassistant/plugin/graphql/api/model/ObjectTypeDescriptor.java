package org.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/* tag::doc[]

[[GraphQLObjectType]]
==  Object Type

Represents a GraphQL Object xref:GraphQLType[Type].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Object Type]
ifndef::iov[| Used labels]
| `:GraphQL:Object:Type`

end::labeloverview[]

|===

end::doc[] */
@Label("Object")
public interface ObjectTypeDescriptor extends FieldContainerTemplate, TypeDescriptor {

/* tag::doc[]

.Relations of an Object Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `IMPLEMENTS`
| xref:GraphQLInterfaceType[Interface Type]
| 0..n
| References the implemented interface types.
end::doc[] */
    @Relation("IMPLEMENTS")
    List<InterfaceTypeDescriptor> getImplements();

/* tag::doc[]
include::FieldContainerTemplate.java[tag=relations]
/* end::doc[]

/* tag::doc[]
|===
end::doc[] */
}
