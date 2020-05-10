package com.buschmais.jqassistant.plugin.graphql.api.model;

/* tag::doc[]

[[GraphQLOfType]]
==  Of Type Relation

References a GraphQL type indicating if this reference is nullable.

.Relation to a GraphQL Type
[options="header",cols="2,4"]
|===

| Relation Name
| Target Node Type


| `OF_TYPE`
| xref:GraphQLType[Type]

|===
end::doc[] */
public interface OfTypeTemplate {

    TypeDescriptor getType();

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
    boolean isNonNull();

    void setNonNull(boolean nonNull);

/* tag::doc[]
|===
end::doc[] */

}
