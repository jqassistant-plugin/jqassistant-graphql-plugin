package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("Argument")
public interface ArgumentDescriptor extends GraphQLDescriptor, NamedElementDescriptor, DirectiveContainerTemplate {

    int getIndex();

    void setIndex(int index);

    @Relation
    @Relation.Outgoing
    ArgumentOfTypeDescriptor getOfType();

    @Relation("HAS_VALUE")
    ValueDescriptor getValue();

    void setValue(ValueDescriptor valueDescriptor);

    @Relation("HAS_DEFAULT_VALUE")
    ValueDescriptor getDefaultValue();

    void setDefaultValue(ValueDescriptor defaultValueDescriptor);
}
