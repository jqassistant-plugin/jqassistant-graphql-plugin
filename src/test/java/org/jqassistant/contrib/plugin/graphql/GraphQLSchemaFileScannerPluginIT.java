package org.jqassistant.contrib.plugin.graphql;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import org.jqassistant.contrib.plugin.graphql.api.model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class GraphQLSchemaFileScannerPluginIT extends AbstractGraphQLSchemaScannerPluginIT<SchemaFileDescriptor> {

    @Override
    protected SchemaFileDescriptor scan() {
        File file = new File(getClassesDirectory(GraphQLSchemaFileScannerPluginIT.class), "schema.graphql");
        return getScanner().scan(file, "/schema.graphql", DefaultScope.NONE);
    }

    @Test
    public void schemaFile() {
        store.beginTransaction();
        assertThat(schemaDescriptor).isNotNull();
        assertThat(schemaDescriptor.getFileName()).isEqualTo("/schema.graphql");
        store.commitTransaction();
    }

    @Test
    public void schemaDeclaresDirectiveType() {
        store.beginTransaction();
        List<DirectiveTypeDescriptor> directives = query("MATCH (:GraphQL:Schema)-[:DECLARES]->(directive:GraphQL:Directive:Type:Named{name:'mine'}) RETURN directive").getColumn("directive");
        assertThat(directives).hasSize(1);
        DirectiveTypeDescriptor mineDirective = directives.get(0);
        assertThat(mineDirective.getDeclaresLocations().stream().map(location -> location.getName())).containsExactlyInAnyOrder("QUERY",
            "MUTATION",
            "FIELD",
            "FRAGMENT_DEFINITION",
            "FRAGMENT_SPREAD",
            "INLINE_FRAGMENT",
            "SCHEMA",
            "SCALAR",
            "OBJECT",
            "FIELD_DEFINITION",
            "ARGUMENT_DEFINITION",
            "INTERFACE",
            "UNION",
            "ENUM",
            "ENUM_VALUE",
            "INPUT_OBJECT",
            "INPUT_FIELD_DEFINITION");
        Map<String, InputValueDescriptor> inputValueDescriptors = asMap(mineDirective.getInputValues());
        verifyInputValue(inputValueDescriptors.get("comment"), 0, false, "String");
        verifyInputValue(inputValueDescriptors.get("coolness"), 1, false, "Coolness");
        store.commitTransaction();
    }

    @Test
    public void typeDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query("MATCH (:GraphQL:Schema)-[:DECLARES]->(:GraphQL:Scalar:Type:Named{name:'Long'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive").getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "mine", argumentDescriptors -> {
            assertThat(argumentDescriptors).hasSize(2);
            verifyArgument(argumentDescriptors.get(0), "comment", ScalarValueDescriptor.class, scalarValueDescriptor -> assertThat(scalarValueDescriptor.getValue()).isEqualTo("My Long scalar"));
            verifyArgument(argumentDescriptors.get(1), "coolness", EnumValueDescriptor.class, enumValueDescriptor -> assertThat(enumValueDescriptor.getName()).isEqualTo("HIGH"));
        });
        store.commitTransaction();
    }

    @Test
    public void enumValueDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query("MATCH (:GraphQL:Schema)-[:DECLARES]->(:GraphQL:Enum:Type:Named{name:'Coolness'})-[:DECLARES_VALUE]->(:GraphQL:Enum:Value{name:'LOW'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive").getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "mine", argumentDescriptors -> assertThat(argumentDescriptors).isEmpty());
        store.commitTransaction();
    }

    @Test
    public void fieldDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query("MATCH (:GraphQL:Schema)-[:DECLARES]->(:GraphQL:Object:Type:Named{name:'Person'})-[:DECLARES_FIELD]->(:GraphQL:Field{name:'groups'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive").getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "relation", argumentDescriptors -> {
            verifyArgument(argumentDescriptors.get(0), "name", ScalarValueDescriptor.class, value -> assertThat(value.getValue()).isEqualTo("HAS_MEMBER"));
            verifyArgument(argumentDescriptors.get(1), "direction", EnumValueDescriptor.class, value -> assertThat(value.getName()).isEqualTo("IN"));
        });
        store.commitTransaction();
    }

    @Test
    public void inputFieldDeclaresDirective() {
        store.beginTransaction();
        List<DirectiveValueDescriptor> directiveValueDescriptors = query("MATCH (:GraphQL:Schema)-[:DECLARES]->(:GraphQL:Object:Type:Named{name:'Query'})-[:DECLARES_FIELD]->(:GraphQL:Field{name:'personByName'})-[:DECLARES_INPUT_VALUE]->(:GraphQL:Input:Value{name:'name'})-[:DECLARES_DIRECTIVE]->(directive:GraphQL:Directive:Value) RETURN directive").getColumn("directive");
        assertThat(directiveValueDescriptors).hasSize(1);
        verifyDirectiveValue(directiveValueDescriptors.get(0), "mine", argumentDescriptors -> assertThat(argumentDescriptors).isEmpty());
        store.commitTransaction();
    }

}
