package org.jqassistant.plugin.graphql.api.model;

public interface IndexTemplate {

/* tag::properties[]
| `index`
| always
| The index number (starting with 0).
end::properties[] */
    int getIndex();

    void setIndex(int index);

}
