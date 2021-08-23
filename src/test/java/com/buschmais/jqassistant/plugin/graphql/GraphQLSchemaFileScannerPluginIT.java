package com.buschmais.jqassistant.plugin.graphql;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.graphql.api.model.*;

import graphql.schema.GraphQLScalarType;
import graphql.schema.idl.ScalarInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

class GraphQLSchemaFileScannerPluginIT extends AbstractGraphQLSchemaScannerPluginIT<SchemaFileDescriptor> {

    @Override
    protected SchemaFileDescriptor scan() {
        File file = new File(getClassesDirectory(GraphQLSchemaFileScannerPluginIT.class), "schema.graphql");
        return getScanner().scan(file, "/schema.graphql", DefaultScope.NONE);
    }

    @Test
    void schemaFile() {
        store.beginTransaction();
        assertThat(schemaDescriptor).isNotNull();
        assertThat(schemaDescriptor.getFileName()).isEqualTo("/schema.graphql");
        store.commitTransaction();
    }

    @Test
    void schemaDeclaresDirectiveType() {
        store.beginTransaction();
        List<DirectiveTypeDescriptor> directives = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(directive:GraphQL:Directive:Type{name:'mine'}) RETURN directive").getColumn("directive");
        assertThat(directives).hasSize(1);
        DirectiveTypeDescriptor mineDirective = directives.get(0);
        assertThat(mineDirective.getDeclaresLocations().stream().map(location -> location.getName())).containsExactlyInAnyOrder("QUERY", "MUTATION", "FIELD",
                "FRAGMENT_DEFINITION", "FRAGMENT_SPREAD", "INLINE_FRAGMENT", "SCHEMA", "SCALAR", "OBJECT", "FIELD_DEFINITION", "ARGUMENT_DEFINITION",
                "INTERFACE", "UNION", "ENUM", "ENUM_VALUE", "INPUT_OBJECT", "INPUT_FIELD_DEFINITION");
        Map<String, InputValueDefinitionDescriptor> inputValueDescriptors = asMap(mineDirective.getInputValues());
        verifyInputValue(inputValueDescriptors.get("comment"), 0, false, "String");
        verifyInputValue(inputValueDescriptors.get("coolness"), 1, false, "Coolness");
        store.commitTransaction();
    }

    @Test
    void scalarTypeDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(:GraphQL:Scalar:Type{name:'Long'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive")
                        .getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "mine", argumentDescriptors -> {
            Assertions.assertThat(argumentDescriptors).hasSize(2);
            verifyArgument(argumentDescriptors.get(0), "comment", ScalarValueDescriptor.class,
                    scalarValueDescriptor -> assertThat(scalarValueDescriptor.getValue()).isEqualTo("My Long scalar"));
            verifyArgument(argumentDescriptors.get(1), "coolness", EnumValueDescriptor.class,
                    enumValueDescriptor -> assertThat(enumValueDescriptor.getName()).isEqualTo("HIGH"));
        });
        store.commitTransaction();
    }

    @Test
    void enumValueDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(:GraphQL:Enum:Type{name:'Coolness'})-[:DECLARES_VALUE]->(:GraphQL:Enum:Value{name:'LOW'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive")
                        .getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "mine", argumentDescriptors -> Assertions.assertThat(argumentDescriptors).isEmpty());
        store.commitTransaction();
    }

    @Test
    void fieldDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(:GraphQL:Object:Type{name:'Person'})-[:DECLARES_FIELD]->(:GraphQL:Field{name:'groups'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive")
                        .getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "relation", argumentDescriptors -> {
            verifyArgument(argumentDescriptors.get(0), "name", ScalarValueDescriptor.class, value -> assertThat(value.getValue()).isEqualTo("HAS_MEMBER"));
            verifyArgument(argumentDescriptors.get(1), "direction", EnumValueDescriptor.class, value -> assertThat(value.getName()).isEqualTo("IN"));
        });
        store.commitTransaction();
    }

    @Test
    void inputFieldDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(:GraphQL:Object:Type{name:'Query'})-[:DECLARES_FIELD]->(:GraphQL:Field{name:'personByName'})-[:DEFINES_INPUT_VALUE]->(:GraphQL:Input:ValueDefinition{name:'name'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive")
                        .getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "mine", argumentDescriptors -> Assertions.assertThat(argumentDescriptors).isEmpty());
        store.commitTransaction();
    }

    @Test
    void typeHasSourceLocation() {
        Set<String> standardScalarNames = ScalarInfo.STANDARD_SCALARS.stream().map(GraphQLScalarType::getName).collect(toSet());
        store.beginTransaction();
        List<TypeDescriptor> typeDescriptors = query("MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(type:GraphQL:Type) RETURN type").getColumn("type");
        typeDescriptors.stream().filter(typeDescriptor -> !standardScalarNames.contains(typeDescriptor.getName()))
                .forEach(typeDescriptor -> verifySourceLocation(typeDescriptor));
        store.commitTransaction();
    }

    @Test
    void fieldHasSourceLocation() {
        store.beginTransaction();
        List<FieldDescriptor> fieldDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(:GraphQL:Type)-[:DECLARES_FIELD]->(field:GraphQL:Field) RETURN field").getColumn("field");
        verifySourceLocations(fieldDescriptors);
        store.commitTransaction();
    }

    @Test
    void inputValueHasSourceLocation() {
        store.beginTransaction();
        List<InputValueDefinitionDescriptor> inputValueDefinitionDescriptors = query(
                "MATCH (:GraphQL:Schema)-[:DECLARES_TYPE]->(:GraphQL:Type)-[:DECLARES_FIELD]->(:GraphQL:Field)-[:DEFINES_INPUT_VALUE]->(inputValue:GraphQL:Input:ValueDefinition) RETURN inputValue")
                        .getColumn("inputValue");
        verifySourceLocations(inputValueDefinitionDescriptors);
        store.commitTransaction();
    }

    private void verifySourceLocations(List<? extends SourceLocationTemplate> sourceLocationTemplates) {
        sourceLocationTemplates.stream().forEach(sourceLocationTemplate -> verifySourceLocation(sourceLocationTemplate));
    }

    private void verifySourceLocation(SourceLocationTemplate sourceLocationTemplate) {
        assertThat(sourceLocationTemplate.getLine()).isNotNull();
        assertThat(sourceLocationTemplate.getColumn()).isNotNull();
    }
}
