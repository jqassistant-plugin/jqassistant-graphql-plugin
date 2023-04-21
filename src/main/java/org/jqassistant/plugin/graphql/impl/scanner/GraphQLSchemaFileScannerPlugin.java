package org.jqassistant.plugin.graphql.impl.scanner;

import java.io.IOException;

import com.buschmais.jqassistant.core.scanner.api.ScannerContext;
import com.buschmais.jqassistant.core.scanner.api.ScannerPlugin;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.jqassistant.plugin.graphql.api.model.SchemaDescriptor;
import org.jqassistant.plugin.graphql.api.model.SchemaFileDescriptor;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

@ScannerPlugin.Requires(FileDescriptor.class)
public class GraphQLSchemaFileScannerPlugin extends AbstractGraphQLSchemaScannerPlugin<FileResource> {

    private final SchemaParser schemaParser = new SchemaParser();

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
        return context.getStore().addDescriptorType(fileDescriptor, SchemaFileDescriptor.class);
    }

    @Override
    protected TypeDefinitionRegistry createTypeDefinitionRegistry(FileResource resource) throws IOException {
        return schemaParser.parse(resource.createStream());
    }

}
