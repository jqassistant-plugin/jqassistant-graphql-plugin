package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/* tag::doc[]

[[GraphQLArgument]]
==  An Argument.

Represents an argument of a GraphQL directive

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Argument]
ifndef::iov[| Used labels]
| `:GraphQL:Argument`

end::labeloverview[]

|===

end::doc[] */
@Label("Argument")
public interface ArgumentDescriptor extends GraphQLDescriptor, IndexTemplate {

    @Relation("HAS_VALUE")
    ValueDescriptor getValue();

    void setValue(ValueDescriptor value);

    @Relation("OF_INPUT_VALUE")
    InputValueDescriptor getInputValue();

    void setInputValue(InputValueDescriptor inputValueDescriptor);

/* tag::doc[]
.Properties of :GraphQL:Argument
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

include::IndexTemplate.java[tag=properties]

end::doc[] */

/* tag::doc[]
|===
 end::doc[] */

}
