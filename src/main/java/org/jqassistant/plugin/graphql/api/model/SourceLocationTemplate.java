package org.jqassistant.plugin.graphql.api.model;

public interface SourceLocationTemplate {

/* tag::properties[]
| `line`
| not always
| The optional line number of the source location within the GraphQL schema.
end::properties[] */
    Integer getLine();

    void setLine(Integer line);

/* tag::properties[]
| `column`
| not always
| The optional column of the source location within the GraphQL schema.
end::properties[] */
    Integer getColumn();

    void setColumn(Integer column);

}
