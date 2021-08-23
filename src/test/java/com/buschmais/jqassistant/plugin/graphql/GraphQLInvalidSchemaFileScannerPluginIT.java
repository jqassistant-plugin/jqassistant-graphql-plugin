package com.buschmais.jqassistant.plugin.graphql;

import java.io.File;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import com.buschmais.jqassistant.plugin.graphql.api.model.SchemaDescriptor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GraphQLInvalidSchemaFileScannerPluginIT extends AbstractPluginIT {

    @Test
    void invalidFile() {
        File file = new File(getClassesDirectory(GraphQLInvalidSchemaFileScannerPluginIT.class), "invalid-schema.graphql");
        SchemaDescriptor schemaDescriptor = getScanner().scan(file, "/invalid-schema.graphql", DefaultScope.NONE);
        store.beginTransaction();
        assertThat(schemaDescriptor.isValid()).isFalse();
        store.commitTransaction();
    }

}
