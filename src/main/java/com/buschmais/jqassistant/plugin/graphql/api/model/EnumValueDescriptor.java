package com.buschmais.jqassistant.plugin.graphql.api.model;

/* tag::doc[]

[[GraphQLEnumValue]]
==  Enum Value

Represents a GraphQL Enum value.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| Enum Value]
ifndef::iov[| Used labels]
| `:GraphQL:Enum:Value`

end::labeloverview[]

|===

end::doc[] */
public interface EnumValueDescriptor extends ValueDescriptor, EnumDescriptor, NameTemplate, DescriptionTemplate, DirectiveContainerTemplate {

/* tag::doc[]

.Relations of an Enum Value
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
include::DirectiveContainerTemplate.java[tag=relations]
/* end::doc[]

/* tag::doc[]
|===
end::doc[] */

/* tag::doc[]
.Properties of :GraphQL:Enum:Value
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

include::NameTemplate.java[tag=properties]
include::DescriptionTemplate.java[tag=properties]

end::doc[] */

/* tag::doc[]
|===
 end::doc[] */

}
