package com.buschmais.jqassistant.plugin.graphql.api.model;

/* tag::doc[]

[[GraphQLInputField]]
==  Input Field

Represents a GraphQL Input xref:GraphQLField[Field], having similar properties and relations.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Input Field]
ifndef::iov[| Used labels]
| `:GraphQL:Input:Field`

end::labeloverview[]

|===

end::doc[] */
public interface InputFieldDescriptor extends FieldDescriptor, InputDescriptor {
}
