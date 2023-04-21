package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

/* tag::doc[]

[[GraphQLInputValueDefinition]]
==  Input Value Definition

Defines an input value.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Input Value Definition]
ifndef::iov[| Used labels]
| `:GraphQL:Input:ValueDefinition`

end::labeloverview[]

|===

end::doc[] */
@Label("ValueDefinition")
public interface InputValueDefinitionDescriptor
        extends GraphQLDescriptor, InputDescriptor, IndexTemplate, NameTemplate, DescriptionTemplate, DirectiveContainerTemplate, SourceLocationTemplate {

/* tag::doc[]

.Relations of an Input Value Definition
[options="header",cols="2,2,1,5"]
|===

| Relation Name
| Target Node
| Cardinality
| Description

end::doc[] */

/* tag::doc[]
| `OF_TYPE`
| xref:GraphQLOfType[Of Type (Relation)]
| 1
| The relation to the type.
end::doc[] */
    @Relation
    @Outgoing
    InputValueOfTypeDescriptor getOfType();

/* tag::doc[]
| `HAS_DEFAULT_VALUE`
| xref:GraphQLValue[Value]
| 0..1
| References the default value.
end::doc[] */
    @Relation("HAS_DEFAULT_VALUE")
    ValueDescriptor getDefaultValue();

    void setDefaultValue(ValueDescriptor defaultValueDescriptor);

/* tag::doc[]
include::DirectiveContainerTemplate.java[tag=relations]
/* end::doc[]

/* tag::doc[]
|===
end::doc[] */

/* tag::doc[]
.Properties of :GraphQL:Input:ValueDefinition
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

include::IndexTemplate.java[tag=properties]
include::NameTemplate.java[tag=properties]
include::DescriptionTemplate.java[tag=properties]
include::SourceLocationTemplate.java[tag=properties]

end::doc[] */

    @ResultOf
    @Cypher("MATCH (inputValue) WHERE id(inputValue)=$this MERGE (inputValue)-[:OF_TYPE]->(enumType:GraphQL:Type) SET enumType:Enum MERGE (enumType)-[:DECLARES_VALUE]->(enumValue:GraphQL:Enum:Value{name:$name}) RETURN enumValue")
    EnumValueDescriptor resolveEnumValue(@ResultOf.Parameter("name") String name);

/* tag::doc[]
|===
end::doc[] */
}
