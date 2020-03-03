package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface InputValueDescriptor extends GraphQLDescriptor, InputDescriptor, ValueDescriptor, NamedElementDescriptor, DirectiveContainerTemplate {

    int getIndex();

    void setIndex(int index);

    @Relation
    @Relation.Outgoing
    InputValueOfTypeDescriptor getOfType();

    @Relation("HAS_VALUE")
    ValueDescriptor getValue();

    void setValue(ValueDescriptor valueDescriptor);

    @Relation("HAS_DEFAULT_VALUE")
    ValueDescriptor getDefaultValue();

    void setDefaultValue(ValueDescriptor defaultValueDescriptor);
}
