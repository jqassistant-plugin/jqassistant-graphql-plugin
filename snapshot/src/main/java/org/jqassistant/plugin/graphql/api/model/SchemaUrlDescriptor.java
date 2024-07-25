package org.jqassistant.plugin.graphql.api.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

/* tag::doc[]

==  Schema Url

A URL (i.e. endpoint) providing a GraphQL xref:GraphQLSchema[Schema].

.Used Combination of Labels
[cols="1h,2"]
|===

tag::labeloverview[]

ifdef::iov[|  Schema URL]
ifndef::iov[| Used labels]
| `:GraphQL:Schema:URL`

end::labeloverview[]

|===

end::doc[] */
@Label("URL")
public interface SchemaUrlDescriptor extends SchemaDescriptor {

/* tag::doc[]
.Properties of :GraphQL:Schema:URL
[options="header",cols="2,2,6"]
|===

| Property Name
| Existence
| Description

end::doc[] */

/* tag::doc[]
| `url`
| always
| The URL.
end::doc[] */
    String getURL();

    void setURL(String URL);

/* tag::doc[]
|===
end::doc[] */
}
