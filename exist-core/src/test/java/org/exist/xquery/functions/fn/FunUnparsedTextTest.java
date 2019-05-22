begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistXmldbEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|CompiledXQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|AnyURIValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|FunUnparsedTextTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|unparsedText_dynamicallyAvailableDocument_absoluteUri
parameter_list|()
throws|throws
name|XPathException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|String
name|text
init|=
literal|"hello, the time is: "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|String
name|textUri
init|=
literal|"http://from-dynamic-context/doc1"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"fn:unparsed-text('"
operator|+
name|textUri
operator|+
literal|"')"
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|context
operator|.
name|addDynamicallyAvailableTextResource
argument_list|(
name|textUri
argument_list|,
name|UTF_8
argument_list|,
parameter_list|(
name|broker2
parameter_list|,
name|transaction
parameter_list|,
name|uri
parameter_list|,
name|charset
parameter_list|)
lambda|->
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
name|charset
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|xqueryService
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|query
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|text
argument_list|,
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|unparsedText_dynamicallyAvailableDocument_relativeUri
parameter_list|()
throws|throws
name|XPathException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|String
name|text
init|=
literal|"hello, the time is: "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|String
name|baseUri
init|=
literal|"http://from-dynamic-context/"
decl_stmt|;
specifier|final
name|String
name|textRelativeUri
init|=
literal|"doc1"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"fn:unparsed-text('"
operator|+
name|textRelativeUri
operator|+
literal|"')"
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|context
operator|.
name|setBaseURI
argument_list|(
operator|new
name|AnyURIValue
argument_list|(
operator|new
name|URI
argument_list|(
name|baseUri
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|addDynamicallyAvailableTextResource
argument_list|(
name|baseUri
operator|+
name|textRelativeUri
argument_list|,
name|UTF_8
argument_list|,
parameter_list|(
name|broker2
parameter_list|,
name|transaction
parameter_list|,
name|uri
parameter_list|,
name|charset
parameter_list|)
lambda|->
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
name|charset
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|xqueryService
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|query
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|text
argument_list|,
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|unparsedTextAvailable_dynamicallyAvailableDocument_absoluteUri
parameter_list|()
throws|throws
name|XPathException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|String
name|text
init|=
literal|"hello, the time is: "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|String
name|textUri
init|=
literal|"http://from-dynamic-context/doc1"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"fn:unparsed-text-available('"
operator|+
name|textUri
operator|+
literal|"')"
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|context
operator|.
name|addDynamicallyAvailableTextResource
argument_list|(
name|textUri
argument_list|,
name|UTF_8
argument_list|,
parameter_list|(
name|broker2
parameter_list|,
name|transaction
parameter_list|,
name|uri
parameter_list|,
name|charset
parameter_list|)
lambda|->
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
name|charset
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|xqueryService
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|query
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|unparsedTextAvailable_dynamicallyAvailableDocument_relativeUri
parameter_list|()
throws|throws
name|XPathException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|URISyntaxException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|String
name|text
init|=
literal|"hello, the time is: "
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|final
name|String
name|baseUri
init|=
literal|"http://from-dynamic-context/"
decl_stmt|;
specifier|final
name|String
name|textRelativeUri
init|=
literal|"doc1"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"fn:unparsed-text-available('"
operator|+
name|textRelativeUri
operator|+
literal|"')"
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|context
operator|.
name|setBaseURI
argument_list|(
operator|new
name|AnyURIValue
argument_list|(
operator|new
name|URI
argument_list|(
name|baseUri
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|addDynamicallyAvailableTextResource
argument_list|(
name|baseUri
operator|+
name|textRelativeUri
argument_list|,
name|UTF_8
argument_list|,
parameter_list|(
name|broker2
parameter_list|,
name|transaction
parameter_list|,
name|uri
parameter_list|,
name|charset
parameter_list|)
lambda|->
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|text
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|,
name|charset
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|xqueryService
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|query
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|result
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|Boolean
operator|.
name|class
argument_list|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

