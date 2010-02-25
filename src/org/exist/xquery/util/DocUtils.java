begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2005-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|net
operator|.
name|ConnectException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|SAXAdapter
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
name|Permission
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
name|lock
operator|.
name|Lock
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
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|SourceFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|URLSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|InputSource
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_comment
comment|/**  * Utilities for XPath doc related functions  *   * @author wolf  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_comment
comment|//TODO : many more improvements to handle efficiently any URI
end_comment

begin_class
specifier|public
class|class
name|DocUtils
block|{
specifier|public
specifier|static
name|Sequence
name|getDocument
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|XPathException
throws|,
name|PermissionDeniedException
block|{
return|return
name|getDocumentByPath
argument_list|(
name|context
argument_list|,
name|path
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isDocumentAvailable
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|Sequence
name|seq
init|=
name|getDocumentByPath
argument_list|(
name|context
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
operator|(
name|seq
operator|!=
literal|null
operator|&&
name|seq
operator|.
name|effectiveBooleanValue
argument_list|()
operator|)
return|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
specifier|static
name|Sequence
name|getDocumentByPath
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|XPathException
throws|,
name|PermissionDeniedException
block|{
name|Sequence
name|document
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|matches
argument_list|(
literal|"^[a-z]+:.*"
argument_list|)
operator|&&
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"xmldb:"
argument_list|)
condition|)
block|{
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
comment|/* URL */
try|try
block|{
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|""
argument_list|,
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|InputStream
name|istream
init|=
name|source
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|instanceof
name|URLSource
condition|)
block|{
name|int
name|responseCode
init|=
operator|(
operator|(
name|URLSource
operator|)
name|source
operator|)
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseCode
operator|==
name|HttpURLConnection
operator|.
name|HTTP_NOT_FOUND
condition|)
block|{
comment|// Special case: '404'
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|else if
condition|(
name|responseCode
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Server returned code "
operator|+
name|responseCode
argument_list|)
throw|;
block|}
block|}
comment|//TODO : process pseudo-protocols URLs more efficiently.
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
name|memtreeDoc
init|=
literal|null
decl_stmt|;
comment|// we use eXist's in-memory DOM implementation
name|reader
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
operator|.
name|borrowXMLReader
argument_list|()
expr_stmt|;
comment|//TODO : we should be able to cope with context.getBaseURI()
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|istream
argument_list|)
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|adapter
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|memtreeDoc
operator|=
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
operator|)
name|doc
expr_stmt|;
name|memtreeDoc
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|memtreeDoc
operator|.
name|setDocumentURI
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|document
operator|=
name|memtreeDoc
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ConnectException
name|e
parameter_list|)
block|{
comment|// prevent long stacktraces
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|" ("
operator|+
name|path
operator|+
literal|")"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"An error occurred while parsing "
operator|+
name|path
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Special case: FileNotFoundException
if|if
condition|(
name|e
operator|instanceof
name|FileNotFoundException
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"An error occurred while parsing "
operator|+
name|path
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
operator|.
name|returnXMLReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|/* Database documents */
comment|// check if the loaded documents should remain locked
name|boolean
name|lockOnLoad
init|=
name|context
operator|.
name|lockDocumentsOnLoad
argument_list|()
decl_stmt|;
name|int
name|lockType
init|=
name|lockOnLoad
condition|?
name|Lock
operator|.
name|WRITE_LOCK
else|:
name|Lock
operator|.
name|READ_LOCK
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|XmldbURI
name|pathUri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// relative collection Path: add the current base URI
name|pathUri
operator|=
name|context
operator|.
name|getBaseURI
argument_list|()
operator|.
name|toXmldbURI
argument_list|()
operator|.
name|resolveCollectionPath
argument_list|(
name|pathUri
argument_list|)
expr_stmt|;
comment|// try to open the document and acquire a lock
name|doc
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|pathUri
argument_list|,
name|lockType
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|doc
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|context
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
block|{
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|lockType
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Insufficient privileges to read resource "
operator|+
name|path
argument_list|)
throw|;
block|}
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Document "
operator|+
name|path
operator|+
literal|" is a binary resource, not an XML document. Please consider using the function util:binary-doc() to retrieve a reference to it."
argument_list|)
throw|;
block|}
if|if
condition|(
name|lockOnLoad
condition|)
block|{
comment|// add the document to the list of locked documents
name|context
operator|.
name|addLockedDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|document
operator|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|// release all locks unless lockOnLoad is true
if|if
condition|(
operator|!
name|lockOnLoad
operator|&&
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|lockType
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|document
return|;
block|}
block|}
end_class

end_unit

