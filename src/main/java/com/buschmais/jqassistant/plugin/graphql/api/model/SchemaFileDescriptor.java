package com.buschmais.jqassistant.plugin.graphql.api.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;

/* tag::doc[]

==  A GraphQL SDL file

A file with the the extension `.graphql` or `.graphqls`.

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[| GraphQL Schema File (`.graphql` or `.graphqls` extension)]
ifndef::iov[| Used labels]
| `:GraphQL:Schema:File`

end::labeloverview[]

|===

end::doc[] */
public interface SchemaFileDescriptor extends SchemaDescriptor, FileDescriptor {
}


