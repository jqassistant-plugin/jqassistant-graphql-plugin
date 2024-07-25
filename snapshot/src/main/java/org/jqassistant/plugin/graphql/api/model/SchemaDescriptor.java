package org.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.jqassistant.plugin.common.api.model.ValidDescriptor;
import com.buschmais.xo.api.annotation.Abstract;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/* tag::doc[]

[[GraphQLSchema]]
== Schema

Represents a GraphQL schema.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Schema]
ifndef::iov[| Used labels]
| `:GraphQL:Schema`

end::labeloverview[]

|===

end::doc[] */
@Abstract
@Label("Schema")
public interface SchemaDescriptor extends GraphQLDescriptor, ValidDescriptor {

/* tag::doc[]

.Relations of a GraphQL Schema
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `DECLARES_TYPE`
| xref:GraphQLType[Type]
| 0..n
| References all GraphQL types which are declared in the schema
end::doc[] */
    @Relation("DECLARES_TYPE")
    List<TypeDescriptor> getDeclaresTypes();

/* tag::doc[]
| `REQUIRES_TYPE`
| xref:GraphQLType[Type]
| 0..n
| References all GraphQL types which are required by the schema (i.e. referenced but not declared)
end::doc[] */
    @Relation("REQUIRES_TYPE")
    List<TypeDescriptor> getRequiresTypes();

/* tag::doc[]
|===
end::doc[]
*/
}

