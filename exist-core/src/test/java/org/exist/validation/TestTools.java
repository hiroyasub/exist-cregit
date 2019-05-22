begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

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
name|TestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|IndexInfo
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
name|storage
operator|.
name|txn
operator|.
name|Txn
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|LockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|InputStreamUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
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

begin_comment
comment|/**  *  A set of helper methods for the validation tests.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|TestTools
block|{
specifier|public
specifier|final
specifier|static
name|String
name|VALIDATION_HOME_COLLECTION
init|=
literal|"validation"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|VALIDATION_DTD_COLLECTION
init|=
literal|"dtd"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|VALIDATION_XSD_COLLECTION
init|=
literal|"xsd"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|VALIDATION_TMP_COLLECTION
init|=
literal|"tmp"
decl_stmt|;
comment|/**      *      * @param document     File to be uploaded      * @param target  Target URL (e.g. xmldb:exist:///db/collection/document.xml)      * @throws java.lang.Exception  Oops.....      */
specifier|public
specifier|static
name|void
name|insertDocumentToURL
parameter_list|(
specifier|final
name|InputStream
name|document
parameter_list|,
specifier|final
name|String
name|target
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|target
argument_list|)
decl_stmt|;
specifier|final
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|OutputStream
name|os
init|=
name|connection
operator|.
name|getOutputStream
argument_list|()
init|)
block|{
name|InputStreamUtil
operator|.
name|copy
argument_list|(
name|document
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|storeDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Path
name|data
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
specifier|final
name|String
name|content
init|=
operator|new
name|String
argument_list|(
name|TestUtils
operator|.
name|readFile
argument_list|(
name|data
argument_list|)
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|storeDocument
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collection
argument_list|,
name|name
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|storeDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|content
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
specifier|final
name|XmldbURI
name|docUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|docUri
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|content
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|storeTextDocument
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Path
name|data
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
specifier|final
name|XmldbURI
name|docUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|data
argument_list|)
init|)
block|{
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|docUri
argument_list|,
name|is
argument_list|,
literal|"text/plain"
argument_list|,
name|Files
operator|.
name|size
argument_list|(
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|Sequence
name|executeQuery
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|XPathException
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|getBroker
argument_list|()
init|)
block|{
return|return
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

