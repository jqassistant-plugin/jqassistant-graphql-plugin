package org.jqassistant.plugin.graphql.api.model;

/* tag::doc[]

[[GraphQLInputObjectType]]
==  Input Object Type

Represents a GraphQL Input Object xref:GraphQLType[Type].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Input Object Type]
ifndef::iov[| Used labels]
| `:GraphQL:Input:Object:Type`

end::labeloverview[]

|===

end::doc[] */
public interface InputObjectTypeDescriptor extends ObjectTypeDescriptor, InputDescriptor {

/* tag::doc[]

.Relations of an Input Object Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `DECLARES_FIELD`
| xref:GraphQLInputField[Input Field]
| 1..n
| References the declared input fields.
end::doc[] */

/* tag::doc[]
|===
end::doc[] */
}
