package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.api.annotation.ResultOf;
import com.buschmais.xo.neo4j.api.annotation.Cypher;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

public interface InputValueDescriptor extends GraphQLDescriptor, InputDescriptor, ValueDescriptor, NamedDescriptor, DescriptionTemplate, DirectiveContainerTemplate, SourceLocationTemplate {

    int getIndex();

    void setIndex(int index);

    @Relation
    @Outgoing
    InputValueOfTypeDescriptor getOfType();

    @Relation("HAS_DEFAULT_VALUE")
    ValueDescriptor getDefaultValue();

    void setDefaultValue(ValueDescriptor defaultValueDescriptor);

    @ResultOf
    @Cypher("MATCH (inputValue) WHERE id(inputValue)={this} MERGE (inputValue)-[:OF_TYPE]->(enumType:GraphQL:Type) SET enumType:Enum MERGE (enumType)-[:DECLARES_VALUE]->(enumValue:GraphQL:Enum:Value{name:{name}}) RETURN enumValue")
    EnumValueDescriptor resolveEnumValue(@ResultOf.Parameter("name") String name);

}
