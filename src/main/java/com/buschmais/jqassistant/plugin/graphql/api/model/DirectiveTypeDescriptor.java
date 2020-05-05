package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

/* tag::doc[]

[[GraphQLDirectiveType]]
==  Directive Type

Represents a directive type that is defined within a GraphQL schema.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Directive Type]
ifndef::iov[| Used labels]
| `:GraphQL:Directive:Type`

end::labeloverview[]

|===

end::doc[] */
public interface DirectiveTypeDescriptor extends TypeDescriptor, DirectiveDescriptor, InputValueContainerTemplate {

/* tag::doc[]

.Relations of a GraphQL Directive Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `DECLARES_LOCATION`
| xref:GraphQLDirectiveLocation[Directive Location]
| 1..n
| References the allowed locations the directive can be applied to.
end::doc[] */
    @Relation("DECLARES_LOCATION")
    List<DirectiveLocationDescriptor> getDeclaresLocations();

/* tag::doc[]
|===
end::doc[]
*/
}
