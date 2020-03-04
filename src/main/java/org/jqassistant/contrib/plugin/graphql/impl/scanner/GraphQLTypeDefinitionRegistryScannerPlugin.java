package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import graphql.language.Argument;
import graphql.language.BooleanValue;
import graphql.language.Description;
import graphql.language.Directive;
import graphql.language.DirectiveDefinition;
import graphql.language.DirectivesContainer;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumValue;
import graphql.language.EnumValueDefinition;
import graphql.language.FieldDefinition;
import graphql.language.FloatValue;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.IntValue;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ListType;
import graphql.language.NamedNode;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.ScalarValue;
import graphql.language.StringValue;
import graphql.language.Type;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.language.UnionTypeDefinition;
import graphql.language.Value;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.contrib.plugin.graphql.api.model.ArgumentDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.DescriptionTemplate;
import org.jqassistant.contrib.plugin.graphql.api.model.DirectiveContainerTemplate;
import org.jqassistant.contrib.plugin.graphql.api.model.DirectiveTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.DirectiveValueDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.EnumTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.EnumValueDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.FieldContainerTemplate;
import org.jqassistant.contrib.plugin.graphql.api.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.FieldOfTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.InputFieldDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.InputObjectTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.InputValueContainerTemplate;
import org.jqassistant.contrib.plugin.graphql.api.model.InputValueDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.InputValueOfTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.ListTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.NamedElementDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.NamedTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.ObjectTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.OfElementTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.RequiredTemplate;
import org.jqassistant.contrib.plugin.graphql.api.model.ScalarTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.ScalarValueDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.SchemaDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.UnionDeclaresTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.UnionTypeDescriptor;
import org.jqassistant.contrib.plugin.graphql.api.model.ValueDescriptor;

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
            processDescription(directiveDefinition.getDescription(), directiveTypeDescriptor);
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

    private void processDescription(Description description, DescriptionTemplate descriptionTemplate) {
        if (description != null) {
            descriptionTemplate.setDescription(description.getContent());
        }
    }

    private void processDirectives(DirectivesContainer<?> directivesContainer, DirectiveContainerTemplate directiveContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        for (Directive directive : directivesContainer.getDirectives()) {
            DirectiveTypeDescriptor directiveTypeDescriptor = namedElementResolver.resolve(directive.getName(), DirectiveTypeDescriptor.class);
            DirectiveValueDescriptor directiveValueDescriptor = store.create(DirectiveValueDescriptor.class);
            directiveValueDescriptor.setOfType(directiveTypeDescriptor);
            resolveArguments(directive.getArguments(), directiveTypeDescriptor, directiveValueDescriptor, store);
            directiveContainerTemplate.getDeclaresDirectives().add(directiveValueDescriptor);
        }
    }

    private void resolveArguments(List<Argument> arguments, DirectiveTypeDescriptor directiveTypeDescriptor, DirectiveValueDescriptor directiveValueDescriptor, Store store) throws IOException {
        int index = 0;
        for (Argument argument : arguments) {
            ArgumentDescriptor argumentDescriptor = createNamedElement(argument, ArgumentDescriptor.class, store);
            InputValueDescriptor inputValueDescriptor = directiveTypeDescriptor.resolveInputValue(argument.getName());
            argumentDescriptor.setInputValue(inputValueDescriptor);
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

    private NamedTypeDescriptor process(ScalarTypeDefinition type, NamedElementResolver namedElementResolver, Store store) {
        ScalarTypeDescriptor scalarTypeDescriptor = resolveNamedSchemaElement(type, ScalarTypeDescriptor.class, namedElementResolver);
        processDescription(type.getDescription(), scalarTypeDescriptor);
        return scalarTypeDescriptor;
    }

    private NamedTypeDescriptor process(EnumTypeDefinition type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        EnumTypeDescriptor enumTypeDescriptor = resolveNamedSchemaElement(type, EnumTypeDescriptor.class, namedElementResolver);
        processDescription(type.getDescription(), enumTypeDescriptor);
        for (EnumValueDefinition value : type.getEnumValueDefinitions()) {
            EnumValueDescriptor enumValueDescriptor = enumTypeDescriptor.resolveValue(value.getName());
            processDescription(value.getDescription(), enumValueDescriptor);
            processDirectives(value, enumValueDescriptor, namedElementResolver, store);
        }
        return enumTypeDescriptor;
    }

    private NamedTypeDescriptor process(ObjectTypeDefinition type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        ObjectTypeDescriptor objectTypeDescriptor = resolveNamedSchemaElement(type, ObjectTypeDescriptor.class, namedElementResolver);
        processDescription(type.getDescription(), objectTypeDescriptor);
        for (Type interfaceType : type.getImplements()) {
            InterfaceTypeDescriptor interfaceTypeDescriptor = namedElementResolver.resolve(((TypeName) interfaceType).getName(), InterfaceTypeDescriptor.class);
            objectTypeDescriptor.getImplements().add(interfaceTypeDescriptor);
        }
        processFieldDefinitions(type.getFieldDefinitions(), objectTypeDescriptor, namedElementResolver, store);
        return objectTypeDescriptor;
    }

    private NamedTypeDescriptor process(InterfaceTypeDefinition type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        InterfaceTypeDescriptor interfaceTypeDescriptor = resolveNamedSchemaElement(type, InterfaceTypeDescriptor.class, namedElementResolver);
        processDescription(type.getDescription(), interfaceTypeDescriptor);
        processFieldDefinitions(type.getFieldDefinitions(), interfaceTypeDescriptor, namedElementResolver, store);
        return interfaceTypeDescriptor;
    }

    private NamedTypeDescriptor process(UnionTypeDefinition type, NamedElementResolver namedElementResolver, Store store) {
        UnionTypeDescriptor unionTypeDescriptor = resolveNamedSchemaElement(type, UnionTypeDescriptor.class, namedElementResolver);
        processDescription(type.getDescription(), unionTypeDescriptor);
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
            processDescription(fieldDefinition.getDescription(), fieldDescriptor);
            resolveFieldType(fieldDescriptor, FieldOfTypeDescriptor.class, fieldDefinition.getType(), namedElementResolver, store);
            resolveInputValues(fieldDefinition.getInputValueDefinitions(), fieldDescriptor, namedElementResolver, store);
            fieldContainerTemplate.getFields().add(fieldDescriptor);
            processDirectives(fieldDefinition, fieldDescriptor, namedElementResolver, store);
        }
    }

    private void resolveInputValues(List<InputValueDefinition> inputValueDefinitions, InputValueContainerTemplate inputValueContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        int index = 0;
        for (InputValueDefinition inputValueDefinition : inputValueDefinitions) {
            InputValueDescriptor inputValueDescriptor = createNamedElement(inputValueDefinition, InputValueDescriptor.class, store);
            processDescription(inputValueDefinition.getDescription(), inputValueDescriptor);
            inputValueDescriptor.setIndex(index);
            index++;
            Type type = inputValueDefinition.getType();
            InputValueOfTypeDescriptor inputValueOfTypeDescriptor = resolveFieldType(inputValueDescriptor, InputValueOfTypeDescriptor.class, type, namedElementResolver, store);
            inputValueDescriptor.setDefaultValue(createValue(inputValueOfTypeDescriptor.getType(), inputValueDefinition.getDefaultValue(), namedElementResolver, store));
            inputValueContainerTemplate.getInputValues().add(inputValueDescriptor);
            processDirectives(inputValueDefinition, inputValueDescriptor, namedElementResolver, store);
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
        processDescription(type.getDescription(), inputObjectTypeDescriptor);
        for (InputValueDefinition inputValueDefinition : type.getInputValueDefinitions()) {
            InputFieldDescriptor inputFieldDescriptor = createNamedElement(inputValueDefinition, InputFieldDescriptor.class, store);
            processDescription(inputValueDefinition.getDescription(), inputFieldDescriptor);
            resolveFieldType(inputFieldDescriptor, FieldOfTypeDescriptor.class, inputValueDefinition.getType(), namedElementResolver, store);
            inputObjectTypeDescriptor.getFields().add(inputFieldDescriptor);
        }
        return inputObjectTypeDescriptor;
    }

    private <T extends NamedElementDescriptor> T resolveNamedSchemaElement(NamedNode<?> namedNode, Class<T> descriptorType, NamedElementResolver namedElementResolver) {
        return namedElementResolver.resolve(namedNode.getName(), descriptorType);
    }


    private <T extends NamedElementDescriptor> T createNamedElement(NamedNode<?> namedNode, Class<T> type, Store store) {
        T namedElementDescriptor = store.create(type);
        namedElementDescriptor.setName(namedNode.getName());
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

