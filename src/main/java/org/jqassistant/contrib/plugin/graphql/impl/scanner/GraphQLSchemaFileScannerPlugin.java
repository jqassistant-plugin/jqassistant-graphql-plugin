package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaDescriptor;

import java.io.IOException;

@ScannerPlugin.Requires(FileDescriptor.class)
public class GraphQLSchemaFileScannerPlugin extends AbstractGraphQLSchemaScannerPlugin<FileResource> {

    @Override
    protected <T> Class<T> getTypeParameter(Class<?> expectedSuperClass, int genericTypeParameterIndex) {
        return (Class<T>) FileResource.class;
    }

    @Override
    public boolean accepts(FileResource fileResource, String path, Scope scope) {
        return path.toLowerCase().endsWith(".graphql") || path.toLowerCase().endsWith(".graphqls");
    }

    @Override
    protected SchemaDescriptor getSchemaDescriptor(String path, ScannerContext context) {
        FileDescriptor fileDescriptor = context.peek(FileDescriptor.class);
        return context.getStore().addDescriptorType(fileDescriptor, SchemaDescriptor.class);
    }

    @Override
    public SchemaDescriptor scan(FileResource fileResource, String path, Scope scope, Scanner scanner) throws IOException {
        ScannerContext context = scanner.getContext();
        FileDescriptor fileDescriptor = context.peek(FileDescriptor.class);
        SchemaDescriptor schemaDescriptor = context.getStore().addDescriptorType(fileDescriptor, SchemaDescriptor.class);
        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry registry = schemaParser.parse(fileResource.createStream());
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        WiringFactory wiringFactory = new WiringFactoryImpl();
        RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring().wiringFactory(wiringFactory).build();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(registry, runtimeWiring);
        context.push(SchemaDescriptor.class, schemaDescriptor);
        try {
            return scanner.scan(graphQLSchema, path, scope);
        } finally {
            context.pop(SchemaDescriptor.class);
        }
    }

    @Override
    protected TypeDefinitionRegistry retrieveSchema(FileResource resource) throws IOException {
        SchemaParser schemaParser = new SchemaParser();
        return schemaParser.parse(resource.createStream());
    }

}
