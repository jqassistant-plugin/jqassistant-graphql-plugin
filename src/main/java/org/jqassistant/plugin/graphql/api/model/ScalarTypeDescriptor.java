package org.jqassistant.plugin.graphql.api.model;

/* tag::doc[]

[[GraphQLScalarType]]
==  Scalar Type

Represents a GraphQL Scalar xref:GraphQLType[Type].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Scalar Type]
ifndef::iov[| Used labels]
| `:GraphQL:Scalar:Type`

end::labeloverview[]

|===

end::doc[] */
public interface ScalarTypeDescriptor extends ScalarDescriptor, TypeDescriptor {
}
