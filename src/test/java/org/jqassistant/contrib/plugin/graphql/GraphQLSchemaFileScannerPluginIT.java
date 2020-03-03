package org.jqassistant.contrib.plugin.graphql;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.junit.jupiter.api.Test;

import java.io.File;

public class GraphQLSchemaFileScannerPluginIT extends AbstractPluginIT {

    @Test
    @TestStore
    public void testSchemaFile() {
        store.beginTransaction();
        File file = new File(getClassesDirectory(GraphQLSchemaFileScannerPluginIT.class), "schema.graphql");
        getScanner().scan(file, file.getAbsolutePath(), DefaultScope.NONE);
        store.commitTransaction();
    }

}
