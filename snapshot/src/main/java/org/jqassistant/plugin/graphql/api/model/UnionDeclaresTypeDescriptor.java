package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

/* tag::doc[]

[[GraphQLUnionDeclaresType]]
==  Union Declares Type

References a GraphQL type indicating the index of the relation.

.Relation to a GraphQL Type
[options="header",cols="2,4"]
|===

| Relation Name
| Target Node Type

| `DECLARES_TYPE`
| xref:GraphQLType[Type]

|===
end::doc[] */
@Relation("DECLARES_TYPE")
public interface UnionDeclaresTypeDescriptor extends Descriptor {

/* tag::doc[]
.Properties of :DECLARES_TYPE
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

include::IndexTemplate.java[tag=properties]
end::doc[] */
    int getIndex();

    void setIndex(int index);

    @Outgoing
    UnionTypeDescriptor getUnionType();

    @Incoming
    TypeDescriptor getType();

/* tag::doc[]
|===
 end::doc[] */
}
