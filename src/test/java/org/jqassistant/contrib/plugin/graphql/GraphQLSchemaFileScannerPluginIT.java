package org.jqassistant.contrib.plugin.graphql;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.jqassistant.contrib.plugin.graphql.api.model.DirectiveTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaFileDescriptor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphQLSchemaFileScannerPluginIT extends AbstractPluginIT {

    @Test
    @TestStore
    public void testSchemaFile() {
        store.beginTransaction();
        File file = new File(getClassesDirectory(GraphQLSchemaFileScannerPluginIT.class), "schema.graphql");
        SchemaFileDescriptor schemaFileDescriptor = getScanner().scan(file, "/schema.graphql", DefaultScope.NONE);
        assertThat(schemaFileDescriptor).isNotNull();
        assertThat(schemaFileDescriptor.getFileName()).isEqualTo("/schema.graphql");

        List<DirectiveTypeDescriptor> directives = query("MATCH (schema:GraphQL:Schema)-[:DECLARES]->(directive:GraphQL:Directive:Type:Named{name:'mine'}) RETURN directive").getColumn("directive");
        assertThat(directives).hasSize(1);

        DirectiveTypeDescriptor mineDirective = directives.get(0);
        assertThat(mineDirective.getDeclaresLocations().stream().map(location -> location.getName())).isNotEmpty();
        store.commitTransaction();
    }

}
