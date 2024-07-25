package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

/* tag::doc[]

[[GraphQLArgument]]
== Argument

Represents an argument of a GraphQL directive.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| Argument]
ifndef::iov[| Used labels]
| `:GraphQL:Argument`

end::labeloverview[]

|===

end::doc[] */
@Label("Argument")
public interface ArgumentDescriptor extends GraphQLDescriptor, IndexTemplate {

/* tag::doc[]

.Relations of a GraphQL Argument
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `HAS_VALUE`
| xref:GraphQLValue[Value]
| 1
| References the argument value.
end::doc[] */
    @Relation("HAS_VALUE")
    ValueDescriptor getValue();

    void setValue(ValueDescriptor value);

/* tag::doc[]
| `OF_INPUT_VALUE`
| xref:GraphQLInputValueDefinition[Input Value Definition]
| 1
| References the input value defined for this argument.
end::doc[] */
    @Relation("OF_INPUT_VALUE")
    InputValueDefinitionDescriptor getInputValue();

    void setInputValue(InputValueDefinitionDescriptor inputValueDefinitionDescriptor);

/* tag::doc[]
|===
end::doc[]
*/

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
