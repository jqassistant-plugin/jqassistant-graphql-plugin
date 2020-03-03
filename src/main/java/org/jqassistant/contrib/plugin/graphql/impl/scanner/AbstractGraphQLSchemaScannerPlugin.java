package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaDescriptor;

import java.io.IOException;

public abstract class AbstractGraphQLSchemaScannerPlugin<R> extends AbstractScannerPlugin<R, SchemaDescriptor> {

    private final SchemaGenerator schemaGenerator = new SchemaGenerator();
    private final RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().wiringFactory(new WiringFactoryImpl()).build();

    @Override
    public SchemaDescriptor scan(R resource, String path, Scope scope, Scanner scanner) throws IOException {
        ScannerContext context = scanner.getContext();
        SchemaDescriptor schemaDescriptor = getSchemaDescriptor(path, context);
        TypeDefinitionRegistry registry = retrieveSchema(resource);
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(registry, runtimeWiring);
        context.push(SchemaDescriptor.class, schemaDescriptor);
        try {
            return scanner.scan(graphQLSchema, path, scope);
        } finally {
            context.pop(SchemaDescriptor.class);
        }
    }

    protected abstract SchemaDescriptor getSchemaDescriptor(String path, ScannerContext context);

    protected abstract TypeDefinitionRegistry retrieveSchema(R resource) throws IOException;

}
