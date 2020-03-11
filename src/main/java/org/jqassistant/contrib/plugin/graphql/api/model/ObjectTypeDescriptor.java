package org.jqassistant.contrib.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label("Object")
public interface ObjectTypeDescriptor extends FieldContainerTemplate, TypeDescriptor {

    @Relation("IMPLEMENTS")
    List<InterfaceTypeDescriptor> getImplements();

}
