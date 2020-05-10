package com.buschmais.jqassistant.plugin.graphql.api.model;

/* tag::doc[]

[[GraphQLScalarValue]]
==  Scalar Value

Represents a GraphQL Scalar value.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Scalar Value]
ifndef::iov[| Used labels]
| `:GraphQL:Scalar:Value`

end::labeloverview[]

|===

end::doc[] */
public interface ScalarValueDescriptor extends ValueDescriptor, ScalarDescriptor {

/* tag::doc[]
.Properties of :GraphQL:Scalar:Value
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

end::doc[] */

/* tag::doc[]
| `value`
| always
| The scalar value.
end::doc[] */
    Object getValue();

    void setValue(Object object);

/* tag::doc[]
|===
 end::doc[] */

}
