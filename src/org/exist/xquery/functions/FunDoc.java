begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|dom
operator|.
name|QName
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
name|NodeImpl
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
name|xquery
operator|.
name|Cardinality
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
name|Dependency
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
name|Function
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
name|FunctionSignature
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
name|Module
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
name|Item
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
name|SequenceType
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
name|w3c
operator|.
name|dom
operator|.
name|Node
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
comment|/**  * Implements the built-in fn:doc() function.  *   * This will be replaced by XQuery's fn:doc() function.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunDoc
extends|extends
name|Function
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"doc"
argument_list|,
name|Module
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Includes a document "
operator|+
literal|"into the input sequence. "
operator|+
literal|"eXist interprets the argument as a path pointing to a "
operator|+
literal|"document in the database, as for example, '/db/shakespeare/plays/hamlet.xml'. "
operator|+
literal|"If the path is relative, "
operator|+
literal|"it is resolved relative to the base URI property from the static context."
operator|+
literal|"Understands also standard URLs, starting with http:// , file:// , etc."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|NodeProxy
name|cachedNode
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|cachedPath
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|FunDoc
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.xquery.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
comment|/** 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|arg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|arg
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|String
name|path
init|=
name|arg
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Invalid argument to fn:doc function: empty string is not allowed here."
argument_list|)
throw|;
comment|//TODO : use a better pattern matcher (Java lacks a convenient method to achieve this)
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
comment|// === standard URL ===
comment|//TODO : process pseudo-protocols URLs differently.
try|try
block|{
return|return
name|processURL
argument_list|(
name|path
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// === document in the database ===
comment|// relative collection Path: add the current base URI
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
literal|'/'
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
name|Lock
name|dlock
init|=
literal|null
decl_stmt|;
comment|// if the expression occurs in a nested context, we might have cached the
comment|// document set
if|if
condition|(
name|path
operator|.
name|equals
argument_list|(
name|cachedPath
argument_list|)
operator|&&
name|cachedNode
operator|!=
literal|null
condition|)
block|{
name|dlock
operator|=
name|cachedNode
operator|.
name|getDocument
argument_list|()
operator|.
name|getUpdateLock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// wait for pending updates by acquiring a lock
name|dlock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
return|return
name|cachedNode
return|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Failed to acquire lock on document "
operator|+
name|path
argument_list|)
throw|;
block|}
finally|finally
block|{
name|dlock
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
name|openDocument
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
operator|==
literal|null
condition|)
block|{
comment|//return Sequence.EMPTY_SEQUENCE;
comment|//no reason to have a behaviour different from docs obtained via URLs
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Document not found "
operator|+
name|path
argument_list|)
throw|;
block|}
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
name|doc
operator|=
literal|null
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Insufficient privileges to read resource "
operator|+
name|path
argument_list|)
throw|;
block|}
name|cachedPath
operator|=
name|path
expr_stmt|;
name|cachedNode
operator|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
return|return
name|cachedNode
return|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Permission denied: unable to load document "
operator|+
name|path
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
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** process a standard URL :  http: , file: , ftp:   */
specifier|private
name|Sequence
name|processURL
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|XPathException
block|{
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
try|try
block|{
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
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|path
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
return|return
name|memtreeDoc
return|;
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
block|}
comment|/** 	 * @see org.exist.xquery.PathExpr#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|cachedNode
operator|=
literal|null
expr_stmt|;
name|cachedPath
operator|=
literal|null
expr_stmt|;
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

