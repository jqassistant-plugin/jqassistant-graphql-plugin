package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.store.api.Store;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.jqassistant.contrib.plugin.graphql.api.model.NamedElementDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaDescriptor;

@RequiredArgsConstructor
public class NamedElementResolver {

    private final SchemaDescriptor schemaDescriptor;

    private final Store store;

    private Cache<String, NamedElementDescriptor> cache = Caffeine.newBuilder().maximumSize(256).build();

    public <N extends NamedElementDescriptor> N resolve(String name, Class<N> expectedType) {
        NamedElementDescriptor namedElementDescriptor = cache.get(name, key -> schemaDescriptor.resolve(key));
        if (namedElementDescriptor.getClass().isAssignableFrom(expectedType)) {
            return (N) namedElementDescriptor;
        }
        N descriptor = store.addDescriptorType(namedElementDescriptor, expectedType);
        cache.put(name, descriptor);
        return descriptor;

    }

}
