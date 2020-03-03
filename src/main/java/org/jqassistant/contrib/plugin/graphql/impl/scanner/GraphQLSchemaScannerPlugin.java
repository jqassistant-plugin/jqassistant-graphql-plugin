package org.jqassistant.contrib.plugin.graphql.impl.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.core.store.api.Store;
import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import graphql.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.jqassistant.contrib.plugin.graphql.api.model.*;

import java.io.IOException;
import java.util.List;

@Slf4j
public class GraphQLSchemaScannerPlugin extends AbstractScannerPlugin<GraphQLSchema, SchemaDescriptor> {

    @Override
    public boolean accepts(GraphQLSchema graphQLSchema, String s, Scope scope) {
        return true;
    }

    @Override
    public SchemaDescriptor scan(GraphQLSchema graphQLSchema, String path, Scope scope, Scanner scanner) throws IOException {
        SchemaDescriptor schema = scanner.getContext().peek(SchemaDescriptor.class);
        Store store = scanner.getContext().getStore();
        NamedElementResolver namedElementResolver = new NamedElementResolver(schema, store);
        for (GraphQLDirective schemaDirective : graphQLSchema.getDirectives()) {
            DirectiveTypeDescriptor directiveTypeDescriptor = resolveNamedSchemaElement(schemaDirective, DirectiveTypeDescriptor.class, namedElementResolver, store);
            resolveArguments(schemaDirective.getArguments(), directiveTypeDescriptor, namedElementResolver, store);
        }
        for (GraphQLNamedType namedType : graphQLSchema.getAllTypesAsList()) {
            String typeName = namedType.getName();
            GraphQLType graphQLType = graphQLSchema.getType(typeName);
            TypeDescriptor typeDescriptor;
            if (graphQLType instanceof GraphQLEnumType) {
                typeDescriptor = process((GraphQLEnumType) graphQLType, namedElementResolver, store);
            } else if (graphQLType instanceof GraphQLScalarType) {
                typeDescriptor = process((GraphQLScalarType) graphQLType, namedElementResolver, store);
            } else if (graphQLType instanceof GraphQLObjectType) {
                typeDescriptor = process((GraphQLObjectType) graphQLType, namedElementResolver, store);
            } else if (graphQLType instanceof GraphQLInputObjectType) {
                typeDescriptor = process((GraphQLInputObjectType) graphQLType, namedElementResolver, store);
            } else if (graphQLType instanceof GraphQLInterfaceType) {
                typeDescriptor = process((GraphQLInterfaceType) graphQLType, namedElementResolver, store);
            } else if (graphQLType instanceof GraphQLUnionType) {
                typeDescriptor = process((GraphQLUnionType) graphQLType, namedElementResolver, store);
            } else {
                throw new IOException("Unsupported GraphQL type " + namedType);
            }
            if (graphQLType instanceof GraphQLDirectiveContainer) {
                processDirectives((GraphQLDirectiveContainer) graphQLType, typeDescriptor, namedElementResolver, store);
            }
        }
        return schema;
    }

    private void processDirectives(GraphQLDirectiveContainer directiveContainer, DirectiveContainerTemplate directiveContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        for (GraphQLDirective directive : directiveContainer.getDirectives()) {
            DirectiveTypeDescriptor ofTypeDescriptor = namedElementResolver.resolve(directive.getName(), DirectiveTypeDescriptor.class);
            DirectiveValueDescriptor valueDescriptor = store.create(DirectiveValueDescriptor.class);
            valueDescriptor.setOfType(ofTypeDescriptor);
            resolveArguments(directive.getArguments(), valueDescriptor, namedElementResolver, store);
            directiveContainerTemplate.getDeclaresDirectives().add(valueDescriptor);
        }
    }

    private TypeDescriptor process(GraphQLScalarType type, NamedElementResolver namedElementResolver, Store store) {
        return resolveNamedSchemaElement(type, ScalarTypeDescriptor.class, namedElementResolver, store);
    }

    private TypeDescriptor process(GraphQLEnumType type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        EnumTypeDescriptor enumTypeDescriptor = resolveNamedSchemaElement(type, EnumTypeDescriptor.class, namedElementResolver, store);
        for (GraphQLEnumValueDefinition value : type.getValues()) {
            EnumValueDescriptor enumValueDescriptor = enumTypeDescriptor.resolveValue(value.getName());
            enumValueDescriptor.setDescription(value.getDescription());
            enumValueDescriptor.setDeprecated(value.isDeprecated());
            enumValueDescriptor.setDeprecationReason(value.getDeprecationReason());
            processDirectives(value, enumValueDescriptor, namedElementResolver, store);
        }
        return enumTypeDescriptor;
    }

    private TypeDescriptor process(GraphQLObjectType type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        ObjectTypeDescriptor objectTypeDescriptor = resolveNamedSchemaElement(type, ObjectTypeDescriptor.class, namedElementResolver, store);
        for (GraphQLNamedOutputType interfaceType : type.getInterfaces()) {
            InterfaceTypeDescriptor interfaceTypeDescriptor = namedElementResolver.resolve(interfaceType.getName(), InterfaceTypeDescriptor.class);
            objectTypeDescriptor.getImplements().add(interfaceTypeDescriptor);
        }
        processFieldDefinitions(type, objectTypeDescriptor, namedElementResolver, store);
        return objectTypeDescriptor;
    }

    private TypeDescriptor process(GraphQLInterfaceType type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        InterfaceTypeDescriptor interfaceTypeDescriptor = resolveNamedSchemaElement(type, InterfaceTypeDescriptor.class, namedElementResolver, store);
        processFieldDefinitions(type, interfaceTypeDescriptor, namedElementResolver, store);
        return interfaceTypeDescriptor;
    }

    private TypeDescriptor process(GraphQLUnionType type, NamedElementResolver namedElementResolver, Store store) {
        UnionTypeDescriptor unionTypeDescriptor = resolveNamedSchemaElement(type, UnionTypeDescriptor.class, namedElementResolver, store);
        int index = 0;
        for (GraphQLNamedOutputType outputType : type.getTypes()) {
            TypeDescriptor typeDescriptor = namedElementResolver.resolve(outputType.getName(), TypeDescriptor.class);
            UnionDeclaresTypeDescriptor unionDeclaresType = store.create(unionTypeDescriptor, UnionDeclaresTypeDescriptor.class, typeDescriptor);
            unionDeclaresType.setIndex(index);
            index++;
        }
        return unionTypeDescriptor;
    }

    private void processFieldDefinitions(GraphQLFieldsContainer type, FieldContainerTemplate fieldContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        for (GraphQLFieldDefinition fieldDefinition : type.getFieldDefinitions()) {
            FieldDescriptor fieldDescriptor = store.create(FieldDescriptor.class);
            fieldDescriptor.setName(fieldDefinition.getName());
            fieldDescriptor.setDescription(fieldDefinition.getDescription());
            fieldDescriptor.setDeprecated(fieldDefinition.isDeprecated());
            fieldDescriptor.setDeprecationReason(fieldDefinition.getDeprecationReason());
            resolveFieldType(fieldDescriptor, FieldOfTypeDescriptor.class, fieldDefinition.getType(), namedElementResolver, store);
            resolveArguments(fieldDefinition.getArguments(), fieldDescriptor, namedElementResolver, store);
            fieldContainerTemplate.getFields().add(fieldDescriptor);
            processDirectives(fieldDefinition, fieldDescriptor, namedElementResolver, store);
        }
    }

    private void resolveArguments(List<GraphQLArgument> arguments, ArgumentContainerTemplate argumentContainerTemplate, NamedElementResolver namedElementResolver, Store store) throws IOException {
        int index = 0;
        for (GraphQLArgument argument : arguments) {
            ArgumentDescriptor argumentDescriptor = createNamedElement(argument, ArgumentDescriptor.class, store);
            argumentDescriptor.setIndex(index);
            index++;
            GraphQLInputType type = argument.getType();
            ArgumentOfTypeDescriptor argumentOfTypeDescriptor = resolveFieldType(argumentDescriptor, ArgumentOfTypeDescriptor.class, type, namedElementResolver, store);
            argumentDescriptor.setValue(createValue(argumentOfTypeDescriptor.getType(), argument.getValue(), namedElementResolver, store));
            argumentDescriptor.setDefaultValue(createValue(argumentOfTypeDescriptor.getType(), argument.getDefaultValue(), namedElementResolver, store));
            argumentContainerTemplate.getArguments().add(argumentDescriptor);
            processDirectives(argument, argumentDescriptor, namedElementResolver, store);
        }
    }

    private ValueDescriptor createValue(TypeDescriptor type, Object value, NamedElementResolver namedElementResolver, Store store) throws IOException {
        if (value == null) {
            return null;
        } else if (type instanceof ScalarTypeDescriptor) {
            ScalarValueDescriptor scalarValueDescriptor = store.create(ScalarValueDescriptor.class);
            scalarValueDescriptor.setValue(value);
            return scalarValueDescriptor;
        } else if (type instanceof EnumTypeDescriptor) {
            EnumTypeDescriptor enumTypeDescriptor = namedElementResolver.resolve(type.getName(), EnumTypeDescriptor.class);
            return enumTypeDescriptor.resolveValue((String) value);
        }
        throw new IOException("Unsupported value type " + type);
    }

    private TypeDescriptor process(GraphQLInputObjectType type, NamedElementResolver namedElementResolver, Store store) throws IOException {
        InputObjectTypeDescriptor inputObjectTypeDescriptor = resolveNamedSchemaElement(type, InputObjectTypeDescriptor.class, namedElementResolver, store);
        for (GraphQLInputObjectField inputFieldDefinition : type.getFieldDefinitions()) {
            InputFieldDescriptor inputFieldDescriptor = createNamedElement(inputFieldDefinition, InputFieldDescriptor.class, store);
            resolveFieldType(inputFieldDescriptor, FieldOfTypeDescriptor.class, inputFieldDefinition.getType(), namedElementResolver, store);
            inputObjectTypeDescriptor.getFields().add(inputFieldDescriptor);
        }
        return inputObjectTypeDescriptor;
    }

    private <T extends NamedElementDescriptor> T resolveNamedSchemaElement(GraphQLNamedSchemaElement namedSchemaElement, Class<T> descriptorType, NamedElementResolver namedElementResolver, Store store) {
        T namedElement = namedElementResolver.resolve(namedSchemaElement.getName(), descriptorType);
        namedElement.setDescription(namedSchemaElement.getDescription());
        return namedElement;
    }


    private <T extends NamedElementDescriptor> T createNamedElement(GraphQLNamedSchemaElement namedSchemaElement, Class<T> type, Store store) {
        T namedElementDescriptor = store.create(type);
        namedElementDescriptor.setName(namedSchemaElement.getName());
        namedElementDescriptor.setDescription(namedSchemaElement.getDescription());
        return namedElementDescriptor;
    }

    /**
     * (:Field)-[:OF_TYPE{required:true}]->(:List)-[:OF_ELEMENT_TYPE{required:true}]->(:Type)
     */
    private <R extends RequiredTemplate & Descriptor> R resolveFieldType(Descriptor from, Class<R> relationType, GraphQLType graphQLType, NamedElementResolver namedElementResolver, Store store) throws IOException {
        if (graphQLType instanceof GraphQLNonNull) {
            GraphQLNonNull graphQLNonNull = (GraphQLNonNull) graphQLType;
            GraphQLType wrappedType = graphQLNonNull.getWrappedType();
            R relation = resolveFieldType(from, relationType, wrappedType, namedElementResolver, store);
            relation.setRequired(true);
            return relation;
        } else if (graphQLType instanceof GraphQLList) {
            GraphQLList graphQLList = (GraphQLList) graphQLType;
            GraphQLType elementType = graphQLList.getWrappedType();
            ListTypeDescriptor listTypeDescriptor = store.create(ListTypeDescriptor.class);
            resolveFieldType(listTypeDescriptor, OfElementTypeDescriptor.class, elementType, namedElementResolver, store);
            return store.create(from, relationType, listTypeDescriptor);
        } else if (graphQLType instanceof GraphQLNamedType) {
            Class<? extends TypeDescriptor> descriptorType;
            if (graphQLType instanceof GraphQLScalarType) {
                descriptorType = ScalarTypeDescriptor.class;
            } else if (graphQLType instanceof GraphQLEnumType) {
                descriptorType = EnumTypeDescriptor.class;
            } else if (graphQLType instanceof GraphQLObjectType) {
                descriptorType = ObjectTypeDescriptor.class;
            } else if (graphQLType instanceof GraphQLInterfaceType) {
                descriptorType = InterfaceTypeDescriptor.class;
            } else if (graphQLType instanceof GraphQLUnionType) {
                descriptorType = UnionTypeDescriptor.class;
            } else if (graphQLType instanceof GraphQLInputObjectType) {
                descriptorType = InputObjectTypeDescriptor.class;
            } else {
                throw new IOException("Unsupported field type: " + graphQLType);
            }
            TypeDescriptor to = namedElementResolver.resolve(((GraphQLNamedType) graphQLType).getName(), descriptorType);
            return store.create(from, relationType, to);
        }
        throw new IOException("Unsupported field type " + graphQLType);
    }
}

