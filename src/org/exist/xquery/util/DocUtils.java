begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|IOException
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
comment|//URLs
if|if
condition|(
name|path
operator|.
name|matches
argument_list|(
literal|"^[a-z]+://.*"
argument_list|)
condition|)
block|{
try|try
block|{
comment|//Basic tests on the URL
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|URLConnection
name|con
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
if|if
condition|(
name|con
operator|instanceof
name|HttpURLConnection
condition|)
block|{
name|HttpURLConnection
name|httpConnection
init|=
operator|(
name|HttpURLConnection
operator|)
name|con
decl_stmt|;
if|if
condition|(
name|httpConnection
operator|.
name|getResponseCode
argument_list|()
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
comment|//TODO : return another type
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Server returned code "
operator|+
name|httpConnection
operator|.
name|getResponseCode
argument_list|()
argument_list|)
throw|;
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
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//TODO : we should be able to cope with context.getBaseURI()
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|con
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
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
name|document
operator|=
name|memtreeDoc
expr_stmt|;
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
name|ParserConfigurationException
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
comment|//Database documents
block|}
else|else
block|{
comment|// relative collection Path: add the current base URI
comment|//TODO : use another strategy
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'/'
condition|)
name|path
operator|=
name|context
operator|.
name|getBaseURI
argument_list|()
operator|+
literal|"/"
operator|+
name|path
expr_stmt|;
comment|// check if the loaded documents should remain locked
name|boolean
name|lockOnLoad
init|=
name|context
operator|.
name|lockDocumentsOnLoad
argument_list|()
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// try to open the document and acquire a lock
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|path
argument_list|,
name|Lock
operator|.
name|READ_LOCK
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
name|Lock
operator|.
name|READ_LOCK
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
name|lockOnLoad
condition|)
block|{
comment|// add the document to the list of locked documents
name|context
operator|.
name|getLockedDocuments
argument_list|()
operator|.
name|add
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
name|Lock
operator|.
name|READ_LOCK
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

