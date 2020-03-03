package org.jqassistant.contrib.plugin.graphql;

import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaUrlDescriptor;
import org.jqassistant.contrib.plugin.graphql.scope.GraphQLScope;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.MalformedURLException;
import java.net.URL;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class GraphQLSchemaURLScannerPluginIT extends AbstractPluginIT {

    @LocalServerPort
    private int port;

    @Test
    @TestStore
    public void scanURL() throws MalformedURLException {
        store.beginTransaction();
        String target = "http://localhost:" + port + "/graphql/";
        SchemaUrlDescriptor schemaUrlDescriptor = getScanner().scan(new URL(target), target, GraphQLScope.SCHEMA);
        store.commitTransaction();
    }
}


