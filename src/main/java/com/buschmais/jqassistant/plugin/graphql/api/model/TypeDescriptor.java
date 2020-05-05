package com.buschmais.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;

/* tag::doc[]

[[GraphQLType]]
== Type

Represents a GraphQL type.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Type]
ifndef::iov[| Used labels]
| `:GraphQL:Type`

end::labeloverview[]

|===

end::doc[] */
@Label("Type")
public interface TypeDescriptor extends GraphQLDescriptor, NameTemplate, DescriptionTemplate, DirectiveContainerTemplate, SourceLocationTemplate {

/* tag::doc[]

.Relations of a GraphQL Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node Type
| Cardinality
| Description

include::DirectiveContainerTemplate.java[tag=relations]

end::doc[] */

    @Relation
    @Incoming
    List<FieldOfTypeDescriptor> getFieldOfTypes();

    @Relation
    @Incoming
    List<OfElementTypeDescriptor> getOfElementTypes();

    @Relation
    @Incoming
    List<UnionDeclaresTypeDescriptor> getUnionDeclaresTypes();

    @Relation
    @Incoming
    List<InputValueOfTypeDescriptor> getInputValueOfTypes();

/* tag::doc[]
|===
end::doc[] */


/* tag::doc[]
.Properties of :GraphQL:Type
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

include::NameTemplate.java[tag=properties]
include::DescriptionTemplate.java[tag=properties]
include::SourceLocationTemplate.java[tag=properties]

end::doc[] */

/* tag::doc[]
|===
end::doc[] */
}
