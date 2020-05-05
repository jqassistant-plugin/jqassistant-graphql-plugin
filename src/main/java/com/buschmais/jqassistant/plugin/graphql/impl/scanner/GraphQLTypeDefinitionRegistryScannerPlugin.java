package com.buschmais.jqassistant.plugin.graphql.impl.scanner;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.graphql.api.model.*;

import graphql.language.*;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GraphQLTypeDefinitionRegistryScannerPlugin extends AbstractScannerPlugin<TypeDefinitionRegistry, SchemaDescriptor> {

    @Override
    public boolean accepts(TypeDefinitionRegistry typeDefinitionRegistry, String s, Scope scope) {
        return true;
    }

    @Override
    public SchemaDescriptor scan(TypeDefinitionRegistry typeDefinitionRegistry, String path, Scope scope, Scanner scanner) throws IOException {
        SchemaDescriptor schema = scanner.getContext().peek(SchemaDescriptor.class);
        Store store = scanner.getContext().getStore();
        try (TypeResolver typeResolver = new TypeResolver(schema, store)) {
            processDirectiveDefinitions(typeDefinitionRegistry.getDirectiveDefinitions().values(), typeResolver, store);
            for (ScalarTypeDefinition scalarTypeDefinition : typeDefinitionRegistry.scalars().values()) {
                TypeDescriptor typeDescriptor = process(scalarTypeDefinition, typeResolver);
                processDirectives(scalarTypeDefinition, typeDescriptor, typeResolver, store);
                processSourceLocation(scalarTypeDefinition, typeDescriptor);
            }
            for (TypeDefinition<?> typeDefinition : typeDefinitionRegistry.types().values()) {
                TypeDescriptor typeDescriptor;
                if (typeDefinition instanceof EnumTypeDefinition) {
                    typeDescriptor = process((EnumTypeDefinition) typeDefinition, typeResolver, store);
                } else if (typeDefinition instanceof ObjectTypeDefinition) {
                    typeDescriptor = process((ObjectTypeDefinition) typeDefinition, typeResolver, store);
                } else if (typeDefinition instanceof InputObjectTypeDefinition) {
                    typeDescriptor = process((InputObjectTypeDefinition) typeDefinition, typeResolver, store);
                } else if (typeDefinition instanceof InterfaceTypeDefinition) {
                    typeDescriptor = process((InterfaceTypeDefinition) typeDefinition, typeResolver, store);
                } else if (typeDefinition instanceof UnionTypeDefinition) {
                    typeDescriptor = process((UnionTypeDefinition) typeDefinition, typeResolver, store);
                } else {
                    throw new IOException("Unsupported GraphQL type " + typeDefinition);
                }
                processSourceLocation(typeDefinition, typeDescriptor);
                processDirectives(typeDefinition, typeDescriptor, typeResolver, store);
            }
        }
        return schema;
    }

    private void processDirectiveDefinitions(Collection<DirectiveDefinition> directiveDefinitions, TypeResolver typeResolver, Store store) throws IOException {
        Map<String, DirectiveLocationDescriptor> directiveLocations = new HashMap<>();
        for (DirectiveDefinition directiveDefinition : directiveDefinitions) {
            DirectiveTypeDescriptor directiveTypeDescriptor = typeResolver.declare(directiveDefinition, DirectiveTypeDescriptor.class);
            resolveInputValues(directiveDefinition.getInputValueDefinitions(), directiveTypeDescriptor, typeResolver, store);
            for (DirectiveLocation directiveLocation : directiveDefinition.getDirectiveLocations()) {
                String name = directiveLocation.getName();
                DirectiveLocationDescriptor locationDescriptor = directiveLocations.computeIfAbsent(name,
                        key -> store.create(DirectiveLocationDescriptor.class, d -> d.setName(name)));
                directiveTypeDescriptor.getDeclaresLocations().add(locationDescriptor);
            }
            processSourceLocation(directiveDefinition, directiveTypeDescriptor);
            processDescription(directiveDefinition.getDescription(), directiveTypeDescriptor);
        }
    }

    private void processDescription(Description description, DescriptionTemplate descriptionTemplate) {
        if (description != null) {
            descriptionTemplate.setDescription(description.getContent());
        }
    }

    private void processDirectives(DirectivesContainer<?> directivesContainer, DirectiveContainerTemplate directiveContainerTemplate, TypeResolver typeResolver,
            Store store) throws IOException {
        for (Directive directive : directivesContainer.getDirectives()) {
            DirectiveTypeDescriptor directiveTypeDescriptor = typeResolver.require(directive, DirectiveTypeDescriptor.class);
            DirectiveValueDescriptor directiveValueDescriptor = store.create(DirectiveValueDescriptor.class);
            directiveValueDescriptor.setOfType(directiveTypeDescriptor);
            resolveArguments(directive.getArguments(), directiveTypeDescriptor, directiveValueDescriptor, store);
            directiveContainerTemplate.getDeclaresDirectives().add(directiveValueDescriptor);
        }
    }

    private void resolveArguments(List<Argument> arguments, DirectiveTypeDescriptor directiveTypeDescriptor, DirectiveValueDescriptor directiveValueDescriptor,
            Store store) throws IOException {
        int index = 0;
        for (Argument argument : arguments) {
            ArgumentDescriptor argumentDescriptor = store.create(ArgumentDescriptor.class);
            InputValueDefinitionDescriptor inputValueDefinitionDescriptor = directiveTypeDescriptor.resolveInputValue(argument.getName());
            argumentDescriptor.setInputValue(inputValueDefinitionDescriptor);
            argumentDescriptor.setIndex(index);
            index++;
            ValueDescriptor valueDescriptor = resolveValue(inputValueDefinitionDescriptor, argument.getValue(), store);
            argumentDescriptor.setValue(valueDescriptor);
            directiveValueDescriptor.getHasArguments().add(argumentDescriptor);
        }
    }

    private TypeDescriptor process(ScalarTypeDefinition type, TypeResolver typeResolver) {
        ScalarTypeDescriptor scalarTypeDescriptor = typeResolver.declare(type, ScalarTypeDescriptor.class);
        processDescription(type.getDescription(), scalarTypeDescriptor);
        return scalarTypeDescriptor;
    }

    private TypeDescriptor process(EnumTypeDefinition type, TypeResolver typeResolver, Store store) throws IOException {
        EnumTypeDescriptor enumTypeDescriptor = typeResolver.declare(type, EnumTypeDescriptor.class);
        processDescription(type.getDescription(), enumTypeDescriptor);
        for (EnumValueDefinition value : type.getEnumValueDefinitions()) {
            EnumValueDescriptor enumValueDescriptor = enumTypeDescriptor.resolveValue(value.getName());
            processDescription(value.getDescription(), enumValueDescriptor);
            processDirectives(value, enumValueDescriptor, typeResolver, store);
        }
        return enumTypeDescriptor;
    }

    private TypeDescriptor process(ObjectTypeDefinition type, TypeResolver typeResolver, Store store) throws IOException {
        ObjectTypeDescriptor objectTypeDescriptor = typeResolver.declare(type, ObjectTypeDescriptor.class);
        processDescription(type.getDescription(), objectTypeDescriptor);
        for (Type interfaceType : type.getImplements()) {
            InterfaceTypeDescriptor interfaceTypeDescriptor = typeResolver.require((TypeName) interfaceType, InterfaceTypeDescriptor.class);
            objectTypeDescriptor.getImplements().add(interfaceTypeDescriptor);
        }
        processFieldDefinitions(type.getFieldDefinitions(), objectTypeDescriptor, typeResolver, store);
        return objectTypeDescriptor;
    }

    private TypeDescriptor process(InterfaceTypeDefinition type, TypeResolver typeResolver, Store store) throws IOException {
        InterfaceTypeDescriptor interfaceTypeDescriptor = typeResolver.declare(type, InterfaceTypeDescriptor.class);
        processDescription(type.getDescription(), interfaceTypeDescriptor);
        processFieldDefinitions(type.getFieldDefinitions(), interfaceTypeDescriptor, typeResolver, store);
        return interfaceTypeDescriptor;
    }

    private TypeDescriptor process(UnionTypeDefinition type, TypeResolver typeResolver, Store store) {
        UnionTypeDescriptor unionTypeDescriptor = typeResolver.declare(type, UnionTypeDescriptor.class);
        processDescription(type.getDescription(), unionTypeDescriptor);
        int index = 0;
        for (Type memberType : type.getMemberTypes()) {
            TypeDescriptor typeDescriptor = typeResolver.require((TypeName) memberType, TypeDescriptor.class);
            UnionDeclaresTypeDescriptor unionDeclaresType = store.create(unionTypeDescriptor, UnionDeclaresTypeDescriptor.class, typeDescriptor);
            unionDeclaresType.setIndex(index);
            index++;
        }
        return unionTypeDescriptor;
    }

    private void processFieldDefinitions(List<FieldDefinition> fieldDefinitions, FieldContainerTemplate fieldContainerTemplate, TypeResolver typeResolver,
            Store store) throws IOException {
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            FieldDescriptor fieldDescriptor = store.create(FieldDescriptor.class);
            fieldDescriptor.setName(fieldDefinition.getName());
            processDescription(fieldDefinition.getDescription(), fieldDescriptor);
            resolveFieldType(fieldDescriptor, FieldOfTypeDescriptor.class, fieldDefinition.getType(), typeResolver, store);
            resolveInputValues(fieldDefinition.getInputValueDefinitions(), fieldDescriptor, typeResolver, store);
            fieldContainerTemplate.getFields().add(fieldDescriptor);
            processSourceLocation(fieldDefinition, fieldDescriptor);
            processDirectives(fieldDefinition, fieldDescriptor, typeResolver, store);
        }
    }

    private void resolveInputValues(List<InputValueDefinition> inputValueDefinitions, InputValueContainerTemplate inputValueContainerTemplate,
            TypeResolver typeResolver, Store store) throws IOException {
        int index = 0;
        for (InputValueDefinition inputValueDefinition : inputValueDefinitions) {
            InputValueDefinitionDescriptor inputValueDefinitionDescriptor = createInputDescriptor(inputValueDefinition, InputValueDefinitionDescriptor.class, store);
            processDescription(inputValueDefinition.getDescription(), inputValueDefinitionDescriptor);
            inputValueDefinitionDescriptor.setIndex(index);
            index++;
            Type type = inputValueDefinition.getType();
            resolveFieldType(inputValueDefinitionDescriptor, InputValueOfTypeDescriptor.class, type, typeResolver, store);
            inputValueDefinitionDescriptor.setDefaultValue(resolveValue(inputValueDefinitionDescriptor, inputValueDefinition.getDefaultValue(), store));
            inputValueContainerTemplate.getInputValues().add(inputValueDefinitionDescriptor);
            processDirectives(inputValueDefinition, inputValueDefinitionDescriptor, typeResolver, store);
        }
    }

    private ValueDescriptor resolveValue(InputValueDefinitionDescriptor inputValueDefinitionDescriptor, Value<?> value, Store store) throws IOException {
        if (value == null) {
            return null;
        } else if (value instanceof ScalarValue<?>) {
            Object scalarValue = getScalarValue((ScalarValue) value);
            ScalarValueDescriptor scalarValueDescriptor = store.create(ScalarValueDescriptor.class);
            scalarValueDescriptor.setValue(scalarValue);
            return scalarValueDescriptor;
        } else if (value instanceof EnumValue) {
            return inputValueDefinitionDescriptor.resolveEnumValue(((EnumValue) value).getName());
        }
        throw new IOException("Unsupported value type " + value);
    }

    private TypeDescriptor process(InputObjectTypeDefinition type, TypeResolver typeResolver, Store store) throws IOException {
        InputObjectTypeDescriptor inputObjectTypeDescriptor = typeResolver.declare(type, InputObjectTypeDescriptor.class);
        processDescription(type.getDescription(), inputObjectTypeDescriptor);
        for (InputValueDefinition inputValueDefinition : type.getInputValueDefinitions()) {
            InputFieldDescriptor inputFieldDescriptor = createInputDescriptor(inputValueDefinition, InputFieldDescriptor.class, store);
            processDescription(inputValueDefinition.getDescription(), inputFieldDescriptor);
            resolveFieldType(inputFieldDescriptor, FieldOfTypeDescriptor.class, inputValueDefinition.getType(), typeResolver, store);
            inputObjectTypeDescriptor.getFields().add(inputFieldDescriptor);
        }
        return inputObjectTypeDescriptor;
    }

    private <T extends InputDescriptor & NameTemplate & SourceLocationTemplate> T createInputDescriptor(NamedNode<?> namedNode, Class<T> type, Store store) {
        return store.create(type, t -> {
            t.setName(namedNode.getName());
            processSourceLocation(namedNode, t);
        });
    }

    private void processSourceLocation(NamedNode<?> namedNode, SourceLocationTemplate sourceLocationTemplate) {
        if (namedNode.getSourceLocation() != null) {
            sourceLocationTemplate.setLine(namedNode.getSourceLocation().getLine());
            sourceLocationTemplate.setColumn(namedNode.getSourceLocation().getColumn());
        }
    }

    /**
     * (:Field)-[:OF_TYPE{required:true}]->(:List)-[:OF_ELEMENT_TYPE{required:true}]->(:Type)
     */
    private <R extends OfTypeTemplate & Descriptor> R resolveFieldType(Descriptor from, Class<R> relationType, Type type, TypeResolver typeResolver,
            Store store) throws IOException {
        if (type instanceof NonNullType) {
            NonNullType nonNullType = (NonNullType) type;
            Type wrappedType = nonNullType.getType();
            R relation = resolveFieldType(from, relationType, wrappedType, typeResolver, store);
            relation.setNonNull(true);
            return relation;
        } else if (type instanceof ListType) {
            ListType listType = (ListType) type;
            Type elementType = listType.getType();
            ListTypeDescriptor listTypeDescriptor = store.create(ListTypeDescriptor.class);
            resolveFieldType(listTypeDescriptor, OfElementTypeDescriptor.class, elementType, typeResolver, store);
            return store.create(from, relationType, listTypeDescriptor);
        } else if (type instanceof TypeName) {
            TypeDescriptor to = typeResolver.require((TypeName) type, TypeDescriptor.class);
            return store.create(from, relationType, to);
        }
        throw new IOException("Unsupported field type " + type);
    }

    /**
     * Converts a {@link ScalarValue} to a property value.
     *
     * @param value
     *            The {@link ScalarValue}.
     * @return The property value.
     * @throws IOException
     *             If an unknown scalar value type is provided.
     */
    private Object getScalarValue(ScalarValue<?> value) throws IOException {
        Object scalarValue;
        if (value instanceof IntValue) {
            scalarValue = ((IntValue) value).getValue().longValue();
        } else if (value instanceof FloatValue) {
            scalarValue = ((FloatValue) value).getValue();
        } else if (value instanceof StringValue) {
            scalarValue = ((StringValue) value).getValue();
        } else if (value instanceof BooleanValue) {
            scalarValue = ((BooleanValue) value).isValue();
        } else {
            throw new IOException("Unsupported scalar value type " + value.getClass());
        }
        return scalarValue;
    }
}
