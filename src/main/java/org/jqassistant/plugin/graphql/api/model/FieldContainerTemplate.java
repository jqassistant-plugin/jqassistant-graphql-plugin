package org.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface FieldContainerTemplate {

/* tag::relations[]
| `DECLARES_FIELD`
| xref:GraphQLField[]
| 0..n
| References the declared GraphQL fields
end::relations[] */
    @Relation("DECLARES_FIELD")
    List<FieldDescriptor> getFields();
}
