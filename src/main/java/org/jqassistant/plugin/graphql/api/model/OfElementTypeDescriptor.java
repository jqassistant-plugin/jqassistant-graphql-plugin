package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

/* tag::doc[]

[[GraphQLOfElementType]]
==  Of Element Type Relation

References a GraphQL type indicating if this reference is nullable.

.Relation to a GraphQL Type
[options="header",cols="2,4"]
|===

| Relation Name
| Target Node Type


| `OF_ELEMENT_TYPE`
| xref:GraphQLType[Type]

|===
end::doc[] */
@Relation("OF_ELEMENT_TYPE")
public interface OfElementTypeDescriptor extends OfTypeTemplate, Descriptor {

/* tag::doc[]
.Properties of :OF_TYPE
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

end::doc[] */

/* tag::doc[]
| `nonNull`
| always
| If `true` the reference is not nullable.
end::doc[] */

/* tag::doc[]
|===
end::doc[] */
    @Outgoing
    ListTypeDescriptor getListType();

    @Incoming
    @Override
    TypeDescriptor getType();

}
