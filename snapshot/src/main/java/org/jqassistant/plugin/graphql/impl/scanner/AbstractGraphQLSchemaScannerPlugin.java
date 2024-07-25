package org.jqassistant.plugin.graphql.impl.scanner;

import java.io.IOException;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import org.jqassistant.plugin.graphql.api.model.SchemaDescriptor;

import graphql.schema.idl.TypeDefinitionRegistry;
import graphql.schema.idl.errors.SchemaProblem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractGraphQLSchemaScannerPlugin<R> extends AbstractScannerPlugin<R, SchemaDescriptor> {

    @Override
    public final SchemaDescriptor scan(R resource, String path, Scope scope, Scanner scanner) throws IOException {
        ScannerContext context = scanner.getContext();
        SchemaDescriptor schemaDescriptor = getSchemaDescriptor(path, context);
        TypeDefinitionRegistry registry;
        try {
            registry = createTypeDefinitionRegistry(resource);
        } catch (SchemaProblem schemaProblem) {
            log.info("Cannot read GraphQL schema from {}: {}", path, schemaProblem.getMessage());
            schemaDescriptor.setValid(false);
            return schemaDescriptor;
        }
        schemaDescriptor.setValid(true);
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
