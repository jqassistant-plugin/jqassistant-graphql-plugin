package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

/* tag::doc[]

[[GraphQLField]]
==  Field

Represents a field of an object or interface type.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Field]
ifndef::iov[| Used labels]
| `:GraphQL:Field`

end::labeloverview[]

|===

end::doc[] */
@Label("Field")
public interface FieldDescriptor
        extends GraphQLDescriptor, NameTemplate, DescriptionTemplate, InputValueContainerTemplate, DirectiveContainerTemplate, SourceLocationTemplate {

/* tag::doc[]

.Relations of a Field
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `OF_TYPE`
| xref:GraphQLOfType[Of Type (Relation)]
| 1
| References the field type
end::doc[] */
    @Relation
    @Outgoing
    FieldOfTypeDescriptor getOfType();

/* tag::doc[]
include::InputValueContainerTemplate.java[tag=relations]
end::doc[] */

/* tag::doc[]
|===
end::doc[] */

/* tag::doc[]
.Properties of :GraphQL:Field
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
