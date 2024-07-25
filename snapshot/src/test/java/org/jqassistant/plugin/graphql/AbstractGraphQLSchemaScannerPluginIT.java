package org.jqassistant.plugin.graphql;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;

import org.assertj.core.api.Assertions;
import org.jqassistant.plugin.graphql.api.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractGraphQLSchemaScannerPluginIT<T extends SchemaDescriptor> extends AbstractPluginIT {

    protected T schemaDescriptor;

    @BeforeEach
    void setUp() throws IOException {
        schemaDescriptor = scan();
    }

    protected abstract T scan() throws IOException;

    @Test
    void validSchema() {
        store.beginTransaction();
        assertThat(schemaDescriptor.isValid()).isTrue();
        store.commitTransaction();
    }

    @Test
    void schemaDeclaresScalarType() {
        store.beginTransaction();
        List<ScalarTypeDescriptor> scalarTypeDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(scalar:GraphQL:Scalar:Type{name:'Long'}) RETURN scalar").getColumn("scalar");
        assertThat(scalarTypeDescriptors).hasSize(1);
        store.commitTransaction();
    }

    @Test
    void schemaDeclaresEnumType() {
        store.beginTransaction();
        List<EnumTypeDescriptor> enumTypeDescriptors = query("MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(enum:GraphQL:Enum:Type{name:'Coolness'}) RETURN enum")
                .getColumn("enum");
        assertThat(enumTypeDescriptors).hasSize(1);
        EnumTypeDescriptor enumTypeDescriptor = enumTypeDescriptors.get(0);
        List<EnumValueDescriptor> enumValueDescriptors = enumTypeDescriptor.getDeclaresValues();
        assertThat(enumValueDescriptors).hasSize(2);
        assertThat(enumValueDescriptors.stream().map(enumValueDescriptor -> enumValueDescriptor.getName()).collect(toList())).containsExactlyInAnyOrder("HIGH",
                "LOW");
        store.commitTransaction();
    }

    @Test
    void schemaDeclaresInterfaceType() {
        store.beginTransaction();
        List<InterfaceTypeDescriptor> interfaceTypeDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(interface:GraphQL:Interface:Type{name:'Versioned'}) RETURN interface").getColumn("interface");
        assertThat(interfaceTypeDescriptors).hasSize(1);
        InterfaceTypeDescriptor interfaceTypeDescriptor = interfaceTypeDescriptors.get(0);
        Map<String, FieldDescriptor> fieldDescriptors = asMap(interfaceTypeDescriptor.getFields());
        assertThat(fieldDescriptors.size()).isEqualTo(1);
        FieldDescriptor versionDescriptor = fieldDescriptors.get("version");
        verifyField(versionDescriptor, true, "Long");
        store.commitTransaction();
    }

    @Test
    void schemaDeclaresObjectType() {
        store.beginTransaction();
        List<ObjectTypeDescriptor> objectTypeDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(object:GraphQL:Object:Type{name:'Person'}) RETURN object").getColumn("object");
        assertThat(objectTypeDescriptors).hasSize(1);
        ObjectTypeDescriptor objectTypeDescriptor = objectTypeDescriptors.get(0);
        // implemented interfaces
        List<InterfaceTypeDescriptor> implementsInterfaceTypeDescriptors = objectTypeDescriptor.getImplements();
        assertThat(implementsInterfaceTypeDescriptors).hasSize(1);
        InterfaceTypeDescriptor implementsInterfaceTypeDescriptor = implementsInterfaceTypeDescriptors.get(0);
        assertThat(implementsInterfaceTypeDescriptor.getName()).isEqualTo("Versioned");
        // declared fields
        Map<String, FieldDescriptor> fieldDescriptors = asMap(objectTypeDescriptor.getFields());
        assertThat(fieldDescriptors).hasSize(5);
        verifyField(fieldDescriptors.get("name"), true, "ID");
        verifyField(fieldDescriptors.get("age"), false, "Int");
        verifyField(fieldDescriptors.get("gender"), false, "Gender");
        verifyField(fieldDescriptors.get("version"), true, "Long");
        verifyField(fieldDescriptors.get("groups"), true, ofElementType -> {
            verifyOfType(ofElementType, true, "Group");
        });
        store.commitTransaction();
    }

    @Test
    void schemaDeclaresInputObjectType() {
        store.beginTransaction();
        List<InputObjectTypeDescriptor> inputObjectTypeDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(inputObject:GraphQL:Input:Object:Type{name:'_Person'}) RETURN inputObject")
                        .getColumn("inputObject");
        Assertions.assertThat(inputObjectTypeDescriptors).hasSize(1);
        InputObjectTypeDescriptor inputObjectTypeDescriptor = inputObjectTypeDescriptors.get(0);
        // declared fields
        Map<String, FieldDescriptor> fieldDescriptors = asMap(inputObjectTypeDescriptor.getFields());
        assertThat(fieldDescriptors).hasSize(2);
        verifyField(fieldDescriptors.get("name"), false, "ID");
        verifyField(fieldDescriptors.get("name_in"), false, ofElementType -> {
            verifyOfType(ofElementType, true, "ID");
        });
        store.commitTransaction();
    }

    @Test
    void schemaDeclaresQueryTypeWithInputFields() {
        store.beginTransaction();
        List<ObjectTypeDescriptor> queryTypeDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(query:GraphQL:Object:Type{name:'Query'}) RETURN query").getColumn("query");
        assertThat(queryTypeDescriptors).hasSize(1);
        ObjectTypeDescriptor queryTypeDescriptor = queryTypeDescriptors.get(0);
        Map<String, FieldDescriptor> fieldDescriptors = asMap(queryTypeDescriptor.getFields());
        assertThat(fieldDescriptors).hasSize(4);

        FieldDescriptor personByAgeDescriptor = fieldDescriptors.get("personByAge");
        verifyField(personByAgeDescriptor, false, ofElementType -> {
            verifyOfType(ofElementType, true, "Person");
        });
        verifyInputValue(asMap(personByAgeDescriptor.getInputValues()).get("age"), 0, false, "Int", defaultValue -> {
            verifyValue(defaultValue, ScalarValueDescriptor.class, scalarValueDescriptor -> assertThat(scalarValueDescriptor.getValue()).isEqualTo(42L));
        });

        FieldDescriptor personByGenderDescriptor = fieldDescriptors.get("personByGender");
        verifyField(personByGenderDescriptor, false, ofElementType -> {
            verifyOfType(ofElementType, true, "Person");
        });
        verifyInputValue(asMap(personByGenderDescriptor.getInputValues()).get("gender"), 0, false, "Gender", defaultValue -> {
            verifyValue(defaultValue, EnumValueDescriptor.class, enumValueDescriptor -> assertThat(enumValueDescriptor.getName()).isEqualTo("FEMALE"));
        });

        store.commitTransaction();
    }

    @Test
    void schemaRequiresDirectiveType() {
        store.beginTransaction();
        List<DirectiveTypeDescriptor> directiveTypeDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:REQUIRES_TYPE]->(directive:GraphQL:Directive:Type{name:'deprecated'}) RETURN directive").getColumn("directive");
        assertThat(directiveTypeDescriptors).hasSize(1);
        DirectiveTypeDescriptor directiveTypeDescriptor = directiveTypeDescriptors.get(0);
        List<InputValueDefinitionDescriptor> inputValues = directiveTypeDescriptor.getInputValues();
        assertThat(inputValues).hasSize(1);
        InputValueDefinitionDescriptor inputValueDefinitionDescriptor = inputValues.get(0);
        assertThat(inputValueDefinitionDescriptor.getOfType()).isNull();
        assertThat(inputValueDefinitionDescriptor.getName()).isEqualTo("reason");
        store.commitTransaction();
    }

    protected <T extends NameTemplate> Map<String, T> asMap(Collection<T> collection) {
        return collection.stream().collect(toMap(nameTemplate -> nameTemplate.getName(), namedDescriptor -> namedDescriptor));
    }

    protected void verifyDirectiveValue(DirectiveValueDescriptor directiveValueDescriptor, String expectedName,
            Consumer<Map<Integer, ArgumentDescriptor>> argumentConsumer) {
        assertThat(directiveValueDescriptor.getOfType().getName()).isEqualTo(expectedName);
        Map<Integer, ArgumentDescriptor> argumentDescriptors = directiveValueDescriptor.getHasArguments().stream()
                .collect(toMap(argumentDescriptor -> argumentDescriptor.getIndex(), argumentDescriptor -> argumentDescriptor));
        argumentConsumer.accept(argumentDescriptors);
    }

    private void verifyField(FieldDescriptor fieldDescriptor, boolean expectedNonNull, Consumer<OfTypeTemplate> ofElementTypeConsumer) {
        FieldOfTypeDescriptor ofType = fieldDescriptor.getOfType();
        assertThat(ofType.isNonNull()).isEqualTo(expectedNonNull);
        TypeDescriptor typeDescriptor = ofType.getType();
        assertThat(typeDescriptor).isInstanceOf(ListTypeDescriptor.class);
        OfElementTypeDescriptor ofElementType = ((ListTypeDescriptor) typeDescriptor).getOfElementType();
        ofElementTypeConsumer.accept(ofElementType);
    }

    private void verifyField(FieldDescriptor fieldDescriptor, boolean expectedRequired, String expectedTypeName) {
        assertThat(fieldDescriptor).isNotNull();
        FieldOfTypeDescriptor ofType = fieldDescriptor.getOfType();
        verifyOfType(ofType, expectedRequired, expectedTypeName);
    }

    protected void verifyInputValue(InputValueDefinitionDescriptor inputValueDefinitionDescriptor, int expectedIndex, boolean expectedRequired, String expectedTypeName) {
        verifyInputValue(inputValueDefinitionDescriptor, expectedIndex, expectedRequired, expectedTypeName, defaultValue -> {
        });
    }

    private void verifyInputValue(InputValueDefinitionDescriptor inputValueDefinitionDescriptor, int expectedIndex, boolean expectedRequired, String expectedTypeName,
                                  Consumer<ValueDescriptor> defaultValueConsumer) {
        assertThat(inputValueDefinitionDescriptor).isNotNull();
        assertThat(inputValueDefinitionDescriptor.getIndex()).isEqualTo(expectedIndex);
        InputValueOfTypeDescriptor ofType = inputValueDefinitionDescriptor.getOfType();
        verifyOfType(ofType, expectedRequired, expectedTypeName);
        defaultValueConsumer.accept(inputValueDefinitionDescriptor.getDefaultValue());
    }

    private void verifyOfType(OfTypeTemplate ofType, boolean expectedNonNull, String expectedTypeName) {
        assertThat(ofType.isNonNull()).isEqualTo(expectedNonNull);
        TypeDescriptor typeDescriptor = ofType.getType();
        assertThat(typeDescriptor.getName()).isEqualTo(expectedTypeName);
    }

    protected <T extends ValueDescriptor> void verifyArgument(ArgumentDescriptor argumentDescriptor, String expectedName, Class<T> expectedValueType,
            Consumer<T> valueConsumer) {
        assertThat(argumentDescriptor.getInputValue().getName()).isEqualTo(expectedName);
        ValueDescriptor argumentValueDescriptor = argumentDescriptor.getValue();
        verifyValue(argumentValueDescriptor, expectedValueType, valueConsumer);
    }

    private <T extends ValueDescriptor> void verifyValue(ValueDescriptor valueDescriptor, Class<T> expectedValueType, Consumer<T> valueConsumer) {
        assertThat(valueDescriptor).isInstanceOf(expectedValueType);
        valueConsumer.accept(expectedValueType.cast(valueDescriptor));
    }

}
