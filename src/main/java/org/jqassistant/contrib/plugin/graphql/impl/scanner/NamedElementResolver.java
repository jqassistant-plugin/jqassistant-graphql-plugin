package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.store.api.Store;
import graphql.language.NamedNode;
import lombok.RequiredArgsConstructor;
import org.jqassistant.contrib.plugin.graphql.api.model.NamedElementDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaDescriptor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class NamedElementResolver implements AutoCloseable {

    private final SchemaDescriptor schemaDescriptor;

    private final Store store;

    private Map<String, NamedElementDescriptor> requiresElements = new HashMap<>();

    private Map<String, NamedElementDescriptor> declaresElements = new HashMap<>();

    public <N extends NamedElementDescriptor> N require(NamedNode<?> namedNode, Class<N> expectedType) {
        String name = namedNode.getName();
        NamedElementDescriptor namedElementDescriptor = declaresElements.get(name);
        if (namedElementDescriptor == null) {
            namedElementDescriptor = requiresElements.computeIfAbsent(name, key -> create(expectedType, key));
        }
        return migrate(namedElementDescriptor, expectedType);
    }

    public <N extends NamedElementDescriptor> N declare(NamedNode<?> namedNode, Class<N> expectedType) {
        String name = namedNode.getName();
        NamedElementDescriptor resolvedDescriptor = requiresElements.remove(name);
        if (resolvedDescriptor == null) {
            resolvedDescriptor = create(expectedType, name);
        }
        N namedElementDescriptor = migrate(resolvedDescriptor, expectedType);
        declaresElements.put(name, namedElementDescriptor);
        return namedElementDescriptor;
    }

    private <N extends NamedElementDescriptor> N create(Class<N> expectedType, String name) {
        return store.create(expectedType, d -> d.setName(name));
    }

    private <N extends NamedElementDescriptor> N migrate(NamedElementDescriptor namedElementDescriptor, Class<N> expectedType) {
        if (namedElementDescriptor.getClass().isAssignableFrom(expectedType)) {
            return (N) namedElementDescriptor;
        }
        N descriptor = store.addDescriptorType(namedElementDescriptor, expectedType);
        return descriptor;
    }

    @Override
    public void close() {
        schemaDescriptor.getDeclares().addAll(declaresElements.values());
        schemaDescriptor.getRequires().addAll(requiresElements.values());
    }
}
