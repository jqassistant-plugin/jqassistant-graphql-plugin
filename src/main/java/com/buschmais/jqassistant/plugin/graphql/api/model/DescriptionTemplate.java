package com.buschmais.jqassistant.plugin.graphql.api.model;

public interface DescriptionTemplate {

/* tag::properties[]
| `description`
| not always
| The optional description as provided in the GraphQL schema.
end::properties[] */
    String getDescription();

    void setDescription(String description);

}
