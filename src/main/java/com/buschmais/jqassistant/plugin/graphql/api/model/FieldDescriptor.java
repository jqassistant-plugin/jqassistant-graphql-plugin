package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

/* tag::doc[]

[[GraphQLField]]
==  A Field.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Field]
ifndef::iov[| Used labels]
| `:GraphQL:Field`

end::labeloverview[]

|===

end::doc[] */
@Label("Field")
public interface FieldDescriptor
        extends GraphQLDescriptor, NameTemplate, DescriptionTemplate, InputValueContainerTemplate, DirectiveContainerTemplate, SourceLocationTemplate {

    @Relation
    @Outgoing
    FieldOfTypeDescriptor getOfType();

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
