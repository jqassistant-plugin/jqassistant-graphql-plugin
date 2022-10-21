package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;

/* tag::doc[]

==  Schema File

A file with the the extension `.graphql` or `.graphqls` containing a GraphQL xref:GraphQLSchema[Schema].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Schema File]
ifndef::iov[| Used labels]
| `:GraphQL:Schema:File`

end::labeloverview[]

|===

end::doc[] */
public interface SchemaFileDescriptor extends SchemaDescriptor, FileDescriptor {
}
