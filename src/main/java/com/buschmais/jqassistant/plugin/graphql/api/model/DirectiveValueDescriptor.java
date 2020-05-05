package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

/* tag::doc[]

[[GraphQLDirectiveValue]]
== Directive Value

Represents a directive that is present at an element of the GraphQL schema.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Directive Value]
ifndef::iov[| Used labels]
| `:GraphQL:Directive:Value`

end::labeloverview[]

|===

end::doc[] */
public interface DirectiveValueDescriptor extends ValueDescriptor, DirectiveDescriptor {

/* tag::doc[]

.Relations of a GraphQL Directive Value
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `OF_TYPE`
| xref:GraphQLDirectiveType[Directive Type]
| 1
| References the type of the directive
end::doc[] */
    @Relation("OF_TYPE")
    DirectiveTypeDescriptor getOfType();

    void setOfType(DirectiveTypeDescriptor directiveType);

/* tag::doc[]
| `HAS_ARGUMENT`
| xref:GraphQLArgument[Argument]
| 0..n
| References arguments of the directive
end::doc[] */
    @Relation("HAS_ARGUMENT")
    List<ArgumentDescriptor> getHasArguments();

/* tag::doc[]
|===
end::doc[]
*/
}
