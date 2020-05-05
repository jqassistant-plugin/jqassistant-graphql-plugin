package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;

/* tag::doc[]

[[GraphQLValue]]
== Value

Represents a value (e.g. a scalar or enum).

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Value]
ifndef::iov[| Used labels]
| `:GraphQL:Value`

end::labeloverview[]

|===

end::doc[] */
@Abstract
@Label("Value")
public interface ValueDescriptor extends GraphQLDescriptor {
}
