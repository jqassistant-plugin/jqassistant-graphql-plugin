package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/* tag::doc[]

[[GraphQLDirectiveLocation]]
==  Directive Location

Represents the allowed locations for a directive.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Directive Location]
ifndef::iov[| Used labels]
| `:GraphQL:Directive:Location`

end::labeloverview[]

|===

end::doc[] */
@Label("Location")
public interface DirectiveLocationDescriptor extends DirectiveDescriptor, NameTemplate {

/* tag::doc[]
.Properties of :GraphQL:Directive:Location
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

include::NameTemplate.java[tag=properties]

end::doc[] */

/* tag::doc[]
|===
 end::doc[] */
}
