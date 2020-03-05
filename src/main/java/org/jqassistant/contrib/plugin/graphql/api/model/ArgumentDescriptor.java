package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@Label("Argument")
public interface ArgumentDescriptor extends GraphQLDescriptor {

    int getIndex();

    void setIndex(int index);

    @Relation("HAS_VALUE")
    ValueDescriptor getValue();

    void setValue(ValueDescriptor value);

    @Relation("OF_INPUT_VALUE")
    InputValueDescriptor getInputValue();

    void setInputValue(InputValueDescriptor inputValueDescriptor);

}
