package com.buschmais.jqassistant.plugin.graphql.impl.scanner;

import java.io.IOException;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.graphql.api.model.SchemaDescriptor;

import graphql.schema.idl.TypeDefinitionRegistry;

public abstract class AbstractGraphQLSchemaScannerPlugin<R> extends AbstractScannerPlugin<R, SchemaDescriptor> {

    @Override
    public final SchemaDescriptor scan(R resource, String path, Scope scope, Scanner scanner) throws IOException {
        ScannerContext context = scanner.getContext();
        SchemaDescriptor schemaDescriptor = getSchemaDescriptor(path, context);
        TypeDefinitionRegistry registry = createTypeDefinitionRegistry(resource);
        context.push(SchemaDescriptor.class, schemaDescriptor);
        try {
            return scanner.scan(registry, path, scope);
        } finally {
            context.pop(SchemaDescriptor.class);
        }
    }

    protected abstract SchemaDescriptor getSchemaDescriptor(String path, ScannerContext context);

    protected abstract TypeDefinitionRegistry createTypeDefinitionRegistry(R resource) throws IOException;

}
