package org.jqassistant.plugin.graphql.impl.scanner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import org.jqassistant.plugin.graphql.api.model.SchemaDescriptor;
import org.jqassistant.plugin.graphql.api.model.SchemaUrlDescriptor;
import org.jqassistant.plugin.graphql.scope.GraphQLScope;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static graphql.introspection.IntrospectionQuery.INTROSPECTION_QUERY;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

@Slf4j
public class GraphQLSchemaURLScannerPlugin extends AbstractGraphQLSchemaScannerPlugin<URL> {

    @Override
    protected <T> Class<T> getTypeParameter(Class<?> expectedSuperClass, int genericTypeParameterIndex) {
        return (Class<T>) URL.class;
    }

    @Override
    public boolean accepts(URL url, String path, Scope scope) {
        return GraphQLScope.SCHEMA.equals(scope);
    }

    @Override
    protected SchemaDescriptor getSchemaDescriptor(String path, ScannerContext context) {
        SchemaUrlDescriptor schemaUrlDescriptor = context.getStore().create(SchemaUrlDescriptor.class);
        schemaUrlDescriptor.setURL(path);
        return schemaUrlDescriptor;
    }

    @Override
    protected TypeDefinitionRegistry createTypeDefinitionRegistry(URL resource) throws IOException {
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = Client.create(clientConfig);

        WebResource.Builder requestBuilder = null;
        try {
            requestBuilder = client.resource(resource.toURI()).getRequestBuilder();
        } catch (URISyntaxException e) {
            throw new IOException("Cannot convert URL " + resource + " to URI.", e);
        }

        log.info("Executing introspection query.");
        GraphQLResponse response = requestBuilder.type(APPLICATION_JSON_TYPE).accept(APPLICATION_JSON_TYPE).post(GraphQLResponse.class,
                GraphQLRequest.builder().query(INTROSPECTION_QUERY).build());
        log.info("Introspection query finished successfully.");
        Map<String, Object> data = response.getData();

        IntrospectionResultToSchema schema = new IntrospectionResultToSchema();
        Document schemaDefinition = schema.createSchemaDefinition(data);
        return new SchemaParser().buildRegistry(schemaDefinition);
    }

    @Getter
    @Builder
    @ToString
    private static class GraphQLRequest {

        private String query;

    }

    @Setter
    @Getter
    @ToString
    private static class GraphQLResponse {

        private Map<String, Object> data;

    }

}
