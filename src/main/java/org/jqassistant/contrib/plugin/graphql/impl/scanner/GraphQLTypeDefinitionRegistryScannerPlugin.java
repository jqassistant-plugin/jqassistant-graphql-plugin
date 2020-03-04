package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import graphql.language.*;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.contrib.plugin.graphql.api.model.*;

import java.io.IOException;
import java.util.List;

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
        NamedElementResolver namedElementResolver = new NamedElementResolver(schema, store);
        for (DirectiveDefinition directiveDefinition : typeDefinitionRegistry.getDirectiveDefinitions().values()) {
            DirectiveTypeDescriptor directiveTypeDescriptor = resolveNamedSchemaElement(directiveDefinition, DirectiveTypeDescriptor.class, namedElementResolver);
            resolveInputValues(directiveDefinition.getInputValueDefinitions(), directiveTypeDescriptor, namedElementResolver, store);
        }
        for (TypeDefinition<?> typeDefinition : typeDefinitionRegistry.types().values()) {
            NamedTypeDescriptor namedTypeDescriptor;
            if (typeDefinition instanceof EnumTypeDefinition) {
                namedTypeDescriptor = process((EnumTypeDefinition) typeDefinition, namedElementResolver, store);
            } else if (typeDefinition instanceof ScalarTypeDefinition) {
                namedTypeDescriptor = process((ScalarTypeDefinition) typeDefinition, namedElementResolver, store);
            } else if (typeDefinition instanceof ObjectTypeDefinition) {
                namedTypeDescriptor = process((ObjectTypeDefinition) typeDefinition, namedElementResolver, store);
            } else if (typeDefinition instanceof InputObjectTypeDefinition) {
                namedTypeDescriptor = process((InputObjectTypeDefinition) typeDefinition, namedElementResolver, store);
            } else if (typeDefinition instanceof InterfaceTypeDefinition) {
                namedTypeDescriptor = process((InterfaceTypeDefinition) typeDefinition, namedElementResolver, store);
            } else if (typeDefinition instanceof UnionTypeDefinition) {
                namedTypeDescriptor = process((UnionTypeDefinition) typeDefinition, namedElementResolver, store);
            } else {
                throw new IOException("Unsupported GraphQL type " + typeDefinition);
            }
            processDirectives(typeDefinition, namedTypeDescriptor, namedElementResolver, store);
        }
        return schema;
    }

    private void processDirectives(DirectivesContainer<?> directivesContainer, DirectiveContainerTemplate directiveContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        for (Directive directive : directivesContainer.getDirectives()) {
            DirectiveTypeDescriptor directiveTypeDescriptor = namedElementResolver.resolve(directive.getName(), DirectiveTypeDescriptor.class);
            DirectiveValueDescriptor valueDescriptor = store.create(DirectiveValueDescriptor.class);
            valueDescriptor.setOfType(directiveTypeDescriptor);
            resolveArguments(directive.getArguments(), valueDescriptor, store);
            directiveContainerTemplate.getDeclaresDirectives().add(valueDescriptor);
        }
    }

    private NamedTypeDescriptor process(ScalarTypeDefinition type, NamedElementResolver namedElementResolver, Store store) {
        return resolveNamedSchemaElement(type, ScalarTypeDescriptor.class, namedElementResolver);
    }

    private NamedTypeDescriptor process(EnumTypeDefinition type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        EnumTypeDescriptor enumTypeDescriptor = resolveNamedSchemaElement(type, EnumTypeDescriptor.class, namedElementResolver);
        for (EnumValueDefinition value : type.getEnumValueDefinitions()) {
            EnumValueDescriptor enumValueDescriptor = enumTypeDescriptor.resolveValue(value.getName());
            // enumValueDescriptor.setDescription(value.getDescription().getContent());
            // enumValueDescriptor.setDeprecated(value.isDeprecated());
            // enumValueDescriptor.setDeprecationReason(value.getDeprecationReason());
            processDirectives(value, enumValueDescriptor, namedElementResolver, store);
        }
        return enumTypeDescriptor;
    }

    private NamedTypeDescriptor process(ObjectTypeDefinition type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        ObjectTypeDescriptor objectTypeDescriptor = resolveNamedSchemaElement(type, ObjectTypeDescriptor.class, namedElementResolver);
        for (Type interfaceType : type.getImplements()) {
            InterfaceTypeDescriptor interfaceTypeDescriptor = namedElementResolver.resolve(((TypeName) interfaceType).getName(), InterfaceTypeDescriptor.class);
            objectTypeDescriptor.getImplements().add(interfaceTypeDescriptor);
        }
        processFieldDefinitions(type.getFieldDefinitions(), objectTypeDescriptor, namedElementResolver, store);
        return objectTypeDescriptor;
    }

    private NamedTypeDescriptor process(InterfaceTypeDefinition type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        InterfaceTypeDescriptor interfaceTypeDescriptor = resolveNamedSchemaElement(type, InterfaceTypeDescriptor.class, namedElementResolver);
        processFieldDefinitions(type.getFieldDefinitions(), interfaceTypeDescriptor, namedElementResolver, store);
        return interfaceTypeDescriptor;
    }

    private NamedTypeDescriptor process(UnionTypeDefinition type, NamedElementResolver namedElementResolver, Store store) {
        UnionTypeDescriptor unionTypeDescriptor = resolveNamedSchemaElement(type, UnionTypeDescriptor.class, namedElementResolver);
        int index = 0;
        for (Type memberType : type.getMemberTypes()) {
            NamedTypeDescriptor namedTypeDescriptor = namedElementResolver.resolve(((TypeName) memberType).getName(), NamedTypeDescriptor.class);
            UnionDeclaresTypeDescriptor unionDeclaresType = store.create(unionTypeDescriptor, UnionDeclaresTypeDescriptor.class, namedTypeDescriptor);
            unionDeclaresType.setIndex(index);
            index++;
        }
        return unionTypeDescriptor;
    }

    private void processFieldDefinitions(List<FieldDefinition> fieldDefinitions, FieldContainerTemplate fieldContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            FieldDescriptor fieldDescriptor = store.create(FieldDescriptor.class);
            fieldDescriptor.setName(fieldDefinition.getName());
            // fieldDescriptor.setDescription(fieldDefinition.getDescription().getContent());
            // fieldDescriptor.setDeprecated(fieldDefinition.gisDeprecated());
            // fieldDescriptor.setDeprecationReason(fieldDefinition.getDeprecationReason());
            resolveFieldType(fieldDescriptor, FieldOfTypeDescriptor.class, fieldDefinition.getType(), namedElementResolver, store);
            resolveInputValues(fieldDefinition.getInputValueDefinitions(), fieldDescriptor, namedElementResolver, store);
            fieldContainerTemplate.getFields().add(fieldDescriptor);
            processDirectives(fieldDefinition, fieldDescriptor, namedElementResolver, store);
        }
    }

    private void resolveArguments(List<Argument> arguments, DirectiveValueDescriptor directiveValueDescriptor, Store store) throws IOException {
        int index = 0;
        for (Argument argument : arguments) {
            ArgumentDescriptor argumentDescriptor = createNamedElement(argument, ArgumentDescriptor.class, store);
            argumentDescriptor.setIndex(index);
            index++;
            Value value = argument.getValue();
            Object argumentValue;
            if (value instanceof ScalarValue<?>) {
                argumentValue = getScalarValue((ScalarValue) argument.getValue());
            } else if (value instanceof EnumValue) {
                argumentValue = ((EnumValue) value).getName();
            } else {
                throw new IOException("Unsupported argument value type " + value);
            }
            argumentDescriptor.setValue(argumentValue);
            directiveValueDescriptor.getHasArguments().add(argumentDescriptor);
        }
    }

    private void resolveInputValues(List<InputValueDefinition> inputValueDefinitions, InputValueContainerTemplate inputValueContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        int index = 0;
        for (InputValueDefinition inputValueDefinition : inputValueDefinitions) {
            InputValueDescriptor inputValueDescriptor = createNamedElement(inputValueDefinition, InputValueDescriptor.class, store);
            inputValueDescriptor.setIndex(index);
            Type type = inputValueDefinition.getType();
            InputValueOfTypeDescriptor inputValueOfTypeDescriptor = resolveFieldType(inputValueDescriptor, InputValueOfTypeDescriptor.class, type, namedElementResolver, store);
            inputValueDescriptor.setDefaultValue(createValue(inputValueOfTypeDescriptor.getType(), inputValueDefinition.getDefaultValue(), namedElementResolver, store));
            inputValueContainerTemplate.getInputValues().add(inputValueDescriptor);
            processDirectives(inputValueDefinition, inputValueDescriptor, namedElementResolver, store);
            index++;
        }
    }

    private ValueDescriptor createValue(NamedTypeDescriptor type, Object value, NamedElementResolver namedElementResolver, Store store) throws IOException {
        if (value == null) {
            return null;
        } else if (value instanceof ScalarValue<?>) {
            Object scalarValue = getScalarValue((ScalarValue) value);
            ScalarValueDescriptor scalarValueDescriptor = store.create(ScalarValueDescriptor.class);
            scalarValueDescriptor.setValue(scalarValue);
            return scalarValueDescriptor;
        } else if (value instanceof EnumValue) {
            EnumTypeDescriptor enumTypeDescriptor = namedElementResolver.resolve(type.getName(), EnumTypeDescriptor.class);
            return enumTypeDescriptor.resolveValue(((EnumValue) value).getName());
        }
        throw new IOException("Unsupported value type " + value);
    }


    private NamedTypeDescriptor process(InputObjectTypeDefinition type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        InputObjectTypeDescriptor inputObjectTypeDescriptor = resolveNamedSchemaElement(type, InputObjectTypeDescriptor.class, namedElementResolver);
        for (InputValueDefinition inputValueDefinition : type.getInputValueDefinitions()) {
            InputFieldDescriptor inputFieldDescriptor = createNamedElement(inputValueDefinition, InputFieldDescriptor.class, store);
            resolveFieldType(inputFieldDescriptor, FieldOfTypeDescriptor.class, inputValueDefinition.getType(), namedElementResolver, store);
            inputObjectTypeDescriptor.getFields().add(inputFieldDescriptor);
        }
        return inputObjectTypeDescriptor;
    }

    private <T extends NamedElementDescriptor> T resolveNamedSchemaElement(NamedNode<?> namedNode, Class<T> descriptorType, NamedElementResolver namedElementResolver) {
        T namedElement = namedElementResolver.resolve(namedNode.getName(), descriptorType);
        // namedElement.setDescription(namedNode.getDescription());
        return namedElement;
    }


    private <T extends NamedElementDescriptor> T createNamedElement(NamedNode<?> namedNode, Class<T> type, Store store) {
        T namedElementDescriptor = store.create(type);
        namedElementDescriptor.setName(namedNode.getName());
        //        namedElementDescriptor.setDescription(namedNode.getDescription());
        return namedElementDescriptor;
    }

    /**
     * (:Field)-[:OF_TYPE{required:true}]->(:List)-[:OF_ELEMENT_TYPE{required:true}]->(:Type)
     */
    private <R extends RequiredTemplate & Descriptor> R resolveFieldType(Descriptor from, Class<R> relationType, Type type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        if (type instanceof NonNullType) {
            NonNullType nonNullType = (NonNullType) type;
            Type wrappedType = nonNullType.getType();
            R relation = resolveFieldType(from, relationType, wrappedType, namedElementResolver, store);
            relation.setRequired(true);
            return relation;
        } else if (type instanceof ListType) {
            ListType listType = (ListType) type;
            Type elementType = listType.getType();
            ListTypeDescriptor listTypeDescriptor = store.create(ListTypeDescriptor.class);
            resolveFieldType(listTypeDescriptor, OfElementTypeDescriptor.class, elementType, namedElementResolver, store);
            return store.create(from, relationType, listTypeDescriptor);
        } else if (type instanceof TypeName) {
            NamedTypeDescriptor to = namedElementResolver.resolve(((TypeName) type).getName(), NamedTypeDescriptor.class);
            return store.create(from, relationType, to);
        }
        throw new IOException("Unsupported field type " + type);
    }

    private Object getScalarValue(ScalarValue value) throws IOException {
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

