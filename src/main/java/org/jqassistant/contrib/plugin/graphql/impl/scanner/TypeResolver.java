package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.store.api.Store;
import graphql.language.NamedNode;
import lombok.RequiredArgsConstructor;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.TypeDescriptor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class TypeResolver implements AutoCloseable {

    private final SchemaDescriptor schemaDescriptor;

    private final Store store;

    private Map<String, TypeDescriptor> requiresTypes = new HashMap<>();

    private Map<String, TypeDescriptor> declaresTypes = new HashMap<>();

    public <T extends TypeDescriptor> T require(NamedNode<?> namedNode, Class<T> expectedType) {
        String name = namedNode.getName();
        TypeDescriptor typeDescriptor = declaresTypes.get(name);
        if (typeDescriptor == null) {
            typeDescriptor = requiresTypes.computeIfAbsent(name, key -> create(expectedType, key));
        }
        return migrate(typeDescriptor, expectedType);
    }

    public <T extends TypeDescriptor> T declare(NamedNode<?> namedNode, Class<T> expectedType) {
        String name = namedNode.getName();
        TypeDescriptor resolvedDescriptor = requiresTypes.remove(name);
        if (resolvedDescriptor == null) {
            resolvedDescriptor = create(expectedType, name);
        }
        T namedElementDescriptor = migrate(resolvedDescriptor, expectedType);
        declaresTypes.put(name, namedElementDescriptor);
        return namedElementDescriptor;
    }

    private <T extends TypeDescriptor> T create(Class<T> expectedType, String name) {
        return store.create(expectedType, d -> d.setName(name));
    }

    private <T extends TypeDescriptor> T migrate(TypeDescriptor typeDescriptor, Class<T> expectedType) {
        if (typeDescriptor.getClass().isAssignableFrom(expectedType)) {
            return (T) typeDescriptor;
        }
        T descriptor = store.addDescriptorType(typeDescriptor, expectedType);
        return descriptor;
    }

    @Override
    public void close() {
        schemaDescriptor.getDeclaresTypes().addAll(declaresTypes.values());
        schemaDescriptor.getRequiresTypes().addAll(requiresTypes.values());
    }
}
