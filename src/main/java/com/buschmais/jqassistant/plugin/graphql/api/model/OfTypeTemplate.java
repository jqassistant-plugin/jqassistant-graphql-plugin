package com.buschmais.jqassistant.plugin.graphql.api.model;

/* tag::doc[]

[[GraphQLOfType]]
==  Of Type Relation

References a GraphQL type indicating of this reference may be nullable.

.Relation to a GraphQL Type
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node Type
| Description


| `OF_TYPE`
| xref:GraphQLType[Type]
| The relation to the type.

|===
end::doc[] */
public interface OfTypeTemplate {

    TypeDescriptor getType();

/* tag::doc[]
.Properties of OF_TYPE
[options="header",cols="2,2,6"]
|===
end::doc[] */

/* tag::doc[]
| `nonNull`
| always
| If `true` the reference is not nullable.
end::doc[] */
    boolean isNonNull();

    void setNonNull(boolean nonNull);

/* tag::doc[]
|===
end::doc[] */

}
