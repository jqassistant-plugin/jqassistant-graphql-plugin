package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/* tag::doc[]

[[GraphQLInterfaceType]]
==  Interface Type

Represents a GraphQL Interface xref:GraphQLType[Type].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Interface Type]
ifndef::iov[| Used labels]
| `:GraphQL:Interface:Type`

end::labeloverview[]

|===

end::doc[] */
@Label("Interface")
public interface InterfaceTypeDescriptor extends FieldContainerTemplate, TypeDescriptor {

/* tag::doc[]

.Relations of an Interface Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
include::FieldContainerTemplate.java[tag=relations]
/* end::doc[]

/* tag::doc[]
|===
end::doc[] */

}
