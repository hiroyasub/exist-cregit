begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|contentextraction
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|memtree
operator|.
name|DocumentBuilderReceiver
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|dom
operator|.
name|INodeHandle
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
name|storage
operator|.
name|NodePath
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
name|serializer
operator|.
name|AttrList
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
name|serializer
operator|.
name|Receiver
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
name|FunctionReference
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
name|ValueSequence
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
name|SAXException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:dulip.withanage@gmail.com">Dulip Withanage</a>  * @author<a href="mailto:dannes@exist-db.org">Dannes Wessels</a>  *   * @version 1.1  */
end_comment

begin_class
specifier|public
class|class
name|ContentReceiver
implements|implements
name|Receiver
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ContentReceiver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|FunctionReference
name|ref
decl_stmt|;
specifier|private
specifier|final
name|NodePath
name|currentElementPath
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NodePath
index|[]
name|paths
decl_stmt|;
specifier|private
name|DocumentBuilderReceiver
name|docBuilderReceiver
init|=
literal|null
decl_stmt|;
specifier|private
name|NodePath
name|startElementPath
init|=
literal|null
decl_stmt|;
specifier|private
name|Sequence
name|userData
init|=
literal|null
decl_stmt|;
specifier|private
name|Sequence
name|prevReturnData
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|private
name|boolean
name|sendDataToCB
init|=
literal|false
decl_stmt|;
comment|/**      *  Receiver constructor      *       * @param context The XQuery context      * @param paths   Paths that must be extracted from the TIKA XHTML document      * @param ref     Reference to callback function      * @param userData Additional user supplied datas      */
specifier|public
name|ContentReceiver
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodePath
index|[]
name|paths
parameter_list|,
name|FunctionReference
name|ref
parameter_list|,
name|Sequence
name|userData
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|paths
operator|=
name|paths
expr_stmt|;
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|userData
operator|=
name|userData
expr_stmt|;
block|}
comment|/**      * Get the result of the content extraction.      *       * @return the result sequence.      */
specifier|public
name|Sequence
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
comment|/**      * Check if content of current (node) path should be retrieved.      *      * @param path Xpath to current node      *      * @return TRUE if path is in to-be-retrieved paths      */
specifier|private
name|boolean
name|matches
parameter_list|(
name|NodePath
name|path
parameter_list|)
block|{
for|for
control|(
name|NodePath
name|p
range|:
name|paths
control|)
block|{
if|if
condition|(
name|p
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespaceURI
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|,
name|AttrList
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// Calculate path to current element
name|currentElementPath
operator|.
name|addComponent
argument_list|(
name|qname
argument_list|)
expr_stmt|;
comment|// Current path matches wanted path
if|if
condition|(
name|matches
argument_list|(
name|currentElementPath
argument_list|)
condition|)
block|{
if|if
condition|(
name|sendDataToCB
condition|)
block|{
comment|// Data is already sent to callback, ignore
block|}
else|else
block|{
comment|// New element match, new data
comment|// Save reference to current path
name|startElementPath
operator|=
operator|new
name|NodePath
argument_list|(
name|currentElementPath
argument_list|)
expr_stmt|;
comment|// Store old fragment in stack
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
comment|// Create new receiver
name|MemTreeBuilder
name|memBuilder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|docBuilderReceiver
operator|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|memBuilder
argument_list|)
expr_stmt|;
comment|// Switch on retrievel
name|sendDataToCB
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sendDataToCB
condition|)
block|{
name|docBuilderReceiver
operator|.
name|startElement
argument_list|(
name|qname
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// Send end element to result
if|if
condition|(
name|sendDataToCB
condition|)
block|{
name|docBuilderReceiver
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
comment|// If path was to be matched path
if|if
condition|(
name|sendDataToCB
operator|&&
name|currentElementPath
operator|.
name|match
argument_list|(
name|startElementPath
argument_list|)
condition|)
block|{
comment|// flush the collected data
name|sendDataToCallback
argument_list|()
expr_stmt|;
comment|// get back from stack
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
comment|// Switch off retrieval
name|sendDataToCB
operator|=
literal|false
expr_stmt|;
name|docBuilderReceiver
operator|=
literal|null
expr_stmt|;
block|}
comment|// calculate new path
name|currentElementPath
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|sendDataToCB
condition|)
block|{
name|docBuilderReceiver
operator|.
name|characters
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|sendDataToCB
condition|)
block|{
name|docBuilderReceiver
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|comment
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|cdataSection
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|highlightText
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentNode
parameter_list|(
name|INodeHandle
name|node
parameter_list|)
block|{
block|}
comment|/**      * Does not return anything.      *       * @return NULL      */
annotation|@
name|Override
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Send data to callback handler      */
specifier|private
name|void
name|sendDataToCallback
parameter_list|()
block|{
comment|// Retrieve result as document
name|Document
name|doc
init|=
name|docBuilderReceiver
operator|.
name|getDocument
argument_list|()
decl_stmt|;
comment|// Get the root
name|NodeImpl
name|root
init|=
operator|(
name|NodeImpl
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
comment|// Construct parameters
name|Sequence
index|[]
name|params
init|=
operator|new
name|Sequence
index|[
literal|3
index|]
decl_stmt|;
name|params
index|[
literal|0
index|]
operator|=
name|root
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
name|userData
expr_stmt|;
name|params
index|[
literal|2
index|]
operator|=
name|prevReturnData
expr_stmt|;
try|try
block|{
comment|// Send data to callback function
name|Sequence
name|ret
init|=
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|prevReturnData
operator|=
name|ret
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|ret
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

