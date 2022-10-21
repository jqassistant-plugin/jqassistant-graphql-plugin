package org.jqassistant.plugin.graphql.api.model;

import java.util.List;

import com.buschmais.xo.neo4j.api.annotation.Relation;

public interface DirectiveContainerTemplate {

/* tag::relations[]
| `DECLARES_DIRECTIVE`
| xref:GraphQLDirectiveValue[Directive Value]
| 0..n
| References to the declared directive values
end::relations[] */
    @Relation("DECLARES_DIRECTIVE")
    List<DirectiveValueDescriptor> getDeclaresDirectives();

}
