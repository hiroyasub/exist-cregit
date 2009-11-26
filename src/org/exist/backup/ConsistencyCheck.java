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
name|backup
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|dom
operator|.
name|BinaryDocument
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
name|ElementImpl
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
name|StoredNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|Agent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|AgentFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|EmbeddedXMLStreamReader
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
name|NativeBroker
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
name|btree
operator|.
name|BTreeCallback
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
name|btree
operator|.
name|Value
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
name|dom
operator|.
name|DOMFile
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
name|dom
operator|.
name|DOMTransaction
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
name|index
operator|.
name|CollectionStore
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
name|io
operator|.
name|VariableByteInput
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
name|TerminatedException
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

begin_class
specifier|public
class|class
name|ConsistencyCheck
block|{
specifier|private
name|Stack
argument_list|<
name|ElementNode
argument_list|>
name|elementStack
init|=
operator|new
name|Stack
argument_list|<
name|ElementNode
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|documentCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|static
class|class
name|ElementNode
block|{
name|ElementImpl
name|elem
decl_stmt|;
name|int
name|childCount
init|=
literal|0
decl_stmt|;
name|NodeId
name|prevSibling
init|=
literal|null
decl_stmt|;
name|ElementNode
parameter_list|(
name|ElementImpl
name|element
parameter_list|)
block|{
name|this
operator|.
name|elem
operator|=
name|element
expr_stmt|;
block|}
block|}
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|int
name|defaultIndexDepth
decl_stmt|;
specifier|private
name|boolean
name|directAccess
init|=
literal|false
decl_stmt|;
specifier|public
name|ConsistencyCheck
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|boolean
name|directAccess
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|defaultIndexDepth
operator|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getDefaultIndexDepth
argument_list|()
expr_stmt|;
name|this
operator|.
name|directAccess
operator|=
name|directAccess
expr_stmt|;
block|}
comment|/**      * Combines      * {@link #checkCollectionTree(org.exist.backup.ConsistencyCheck.ProgressCallback)}      * and      * {@link #checkDocuments(org.exist.backup.ConsistencyCheck.ProgressCallback)}.      *       * @param callback      *            the callback object to report to      * @return a list of {@link ErrorReport} objects or an empty list if no      *         errors were found      */
specifier|public
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|checkAll
parameter_list|(
name|ProgressCallback
name|callback
parameter_list|)
block|{
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
init|=
name|checkCollectionTree
argument_list|(
name|callback
argument_list|)
decl_stmt|;
name|checkDocuments
argument_list|(
name|callback
argument_list|,
name|errors
argument_list|)
expr_stmt|;
return|return
name|errors
return|;
block|}
comment|/**      * Run some tests on the collection hierarchy, starting at the root      * collection /db.      *       * @param callback      *            callback object      * @return a list of {@link ErrorReport} instances describing the errors      *         found      */
specifier|public
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|checkCollectionTree
parameter_list|(
name|ProgressCallback
name|callback
parameter_list|)
block|{
name|User
operator|.
name|enablePasswordChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|ErrorReport
argument_list|>
argument_list|()
decl_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|)
decl_stmt|;
name|checkCollection
argument_list|(
name|root
argument_list|,
name|errors
argument_list|,
name|callback
argument_list|)
expr_stmt|;
return|return
name|errors
return|;
block|}
finally|finally
block|{
name|User
operator|.
name|enablePasswordChecks
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
parameter_list|,
name|ProgressCallback
name|callback
parameter_list|)
block|{
name|XmldbURI
name|uri
init|=
name|collection
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|callback
operator|.
name|startCollection
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|collection
operator|.
name|collectionIteratorNoLock
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|XmldbURI
name|childUri
init|=
operator|(
name|XmldbURI
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|Collection
name|child
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|uri
operator|.
name|append
argument_list|(
name|childUri
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
name|ErrorReport
operator|.
name|CollectionError
name|error
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|CollectionError
argument_list|(
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|CHILD_COLLECTION
argument_list|,
literal|"Child collection not found: "
operator|+
name|childUri
operator|+
literal|", parent is "
operator|+
name|uri
argument_list|)
decl_stmt|;
name|error
operator|.
name|setCollectionId
argument_list|(
name|collection
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|error
operator|.
name|setCollectionURI
argument_list|(
name|childUri
argument_list|)
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|callback
operator|.
name|error
argument_list|(
name|error
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|checkCollection
argument_list|(
name|child
argument_list|,
name|errors
argument_list|,
name|callback
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|ErrorReport
operator|.
name|CollectionError
name|error
init|=
operator|new
name|ErrorReport
operator|.
name|CollectionError
argument_list|(
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|CHILD_COLLECTION
argument_list|,
literal|"Error while loading child collection: "
operator|+
name|childUri
operator|+
literal|", parent is "
operator|+
name|uri
argument_list|)
decl_stmt|;
name|error
operator|.
name|setCollectionId
argument_list|(
name|collection
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|error
operator|.
name|setCollectionURI
argument_list|(
name|childUri
argument_list|)
expr_stmt|;
name|errors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|callback
operator|.
name|error
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|int
name|getDocumentCount
parameter_list|()
block|{
if|if
condition|(
name|documentCount
operator|==
operator|-
literal|1
condition|)
block|{
name|User
operator|.
name|enablePasswordChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|DocumentCallback
name|cb
init|=
operator|new
name|DocumentCallback
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|broker
operator|.
name|getResourcesFailsafe
argument_list|(
name|cb
argument_list|,
name|directAccess
argument_list|)
expr_stmt|;
name|documentCount
operator|=
name|cb
operator|.
name|docCount
expr_stmt|;
block|}
finally|finally
block|{
name|User
operator|.
name|enablePasswordChecks
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|documentCount
return|;
block|}
comment|/**      * Run some tests on all documents stored in the database. The method checks      * if a document is readable and if its DOM representation is consistent.      *       * @param progress      *            progress callback      * @return a list of {@link ErrorReport} instances describing the errors      *         found      */
specifier|public
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|checkDocuments
parameter_list|(
name|ProgressCallback
name|progress
parameter_list|)
block|{
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|ErrorReport
argument_list|>
argument_list|()
decl_stmt|;
name|checkDocuments
argument_list|(
name|progress
argument_list|,
name|errors
argument_list|)
expr_stmt|;
return|return
name|errors
return|;
block|}
comment|/**      * Run some tests on all documents stored in the database. The method checks      * if a document is readable and if its DOM representation is consistent.      *       * @param progress      *            progress callback      * @param errorList      *            error reports will be added to this list, using instances of      *            class {@link ErrorReport}.      */
specifier|public
name|void
name|checkDocuments
parameter_list|(
name|ProgressCallback
name|progress
parameter_list|,
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errorList
parameter_list|)
block|{
name|User
operator|.
name|enablePasswordChecks
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|DocumentCallback
name|cb
init|=
operator|new
name|DocumentCallback
argument_list|(
name|errorList
argument_list|,
name|progress
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|broker
operator|.
name|getResourcesFailsafe
argument_list|(
name|cb
argument_list|,
name|directAccess
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|User
operator|.
name|enablePasswordChecks
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Check the persistent DOM of a document. The method traverses the entire      * node tree and checks it for consistency, including node relationships,      * child and attribute counts etc.      *       * @param doc      *            the document to check      * @return null if the document is consistent, an error report otherwise.      */
specifier|public
name|ErrorReport
name|checkXMLTree
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
specifier|final
name|DOMFile
name|domDb
init|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getDOMFile
argument_list|()
decl_stmt|;
return|return
operator|(
name|ErrorReport
operator|)
operator|new
name|DOMTransaction
argument_list|(
name|this
argument_list|,
name|domDb
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|,
name|doc
argument_list|)
block|{
specifier|public
name|Object
name|start
parameter_list|()
block|{
try|try
block|{
name|ElementImpl
name|root
init|=
operator|(
name|ElementImpl
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|EmbeddedXMLStreamReader
name|reader
init|=
name|broker
operator|.
name|getXMLStreamReader
argument_list|(
name|root
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
decl_stmt|;
name|boolean
name|attribsAllowed
init|=
literal|false
decl_stmt|;
name|int
name|expectedAttribs
init|=
literal|0
decl_stmt|;
name|int
name|attributeCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
name|nodeId
operator|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|EmbeddedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
expr_stmt|;
name|ElementNode
name|parent
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|status
operator|!=
name|XMLStreamReader
operator|.
name|END_ELEMENT
operator|&&
operator|!
name|elementStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|parent
operator|=
name|elementStack
operator|.
name|peek
argument_list|()
expr_stmt|;
name|parent
operator|.
name|childCount
operator|++
expr_stmt|;
comment|// test parent-child relation
if|if
condition|(
operator|!
name|nodeId
operator|.
name|isChildOf
argument_list|(
name|parent
operator|.
name|elem
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
return|return
operator|new
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|ErrorReport
operator|.
name|NODE_HIERARCHY
argument_list|,
literal|"Node "
operator|+
name|nodeId
operator|+
literal|" is not a child of "
operator|+
name|parent
operator|.
name|elem
operator|.
name|getNodeId
argument_list|()
argument_list|)
return|;
comment|// test sibling relation
if|if
condition|(
name|parent
operator|.
name|prevSibling
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|nodeId
operator|.
name|isSiblingOf
argument_list|(
name|parent
operator|.
name|prevSibling
argument_list|)
operator|&&
name|nodeId
operator|.
name|compareTo
argument_list|(
name|parent
operator|.
name|prevSibling
argument_list|)
operator|>
literal|0
operator|)
condition|)
block|{
return|return
operator|new
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|ErrorReport
operator|.
name|INCORRECT_NODE_ID
argument_list|,
literal|"Node "
operator|+
name|nodeId
operator|+
literal|" is not a sibling of "
operator|+
name|parent
operator|.
name|prevSibling
argument_list|)
return|;
block|}
name|parent
operator|.
name|prevSibling
operator|=
name|nodeId
expr_stmt|;
block|}
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|XMLStreamReader
operator|.
name|ATTRIBUTE
case|:
name|attributeCount
operator|++
expr_stmt|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
name|elementStack
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|ErrorReport
operator|.
name|NODE_HIERARCHY
argument_list|,
literal|"Error in node hierarchy: received END_ELEMENT event "
operator|+
literal|"but stack was empty!"
argument_list|)
return|;
name|ElementNode
name|lastElem
init|=
name|elementStack
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastElem
operator|.
name|childCount
operator|!=
name|lastElem
operator|.
name|elem
operator|.
name|getChildCount
argument_list|()
condition|)
return|return
operator|new
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|NODE_HIERARCHY
argument_list|,
literal|"Element reports incorrect child count: expected "
operator|+
name|lastElem
operator|.
name|elem
operator|.
name|getChildCount
argument_list|()
operator|+
literal|" but found "
operator|+
name|lastElem
operator|.
name|childCount
argument_list|)
return|;
break|break;
case|case
name|XMLStreamReader
operator|.
name|START_ELEMENT
case|:
if|if
condition|(
name|nodeId
operator|.
name|getTreeLevel
argument_list|()
operator|<=
name|defaultIndexDepth
condition|)
block|{
comment|// check dom.dbx btree, which maps the node
comment|// id to the node's storage address
comment|// look up the node id and check if the
comment|// returned storage address is correct
name|NativeBroker
operator|.
name|NodeRef
name|nodeRef
init|=
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|p
init|=
name|domDb
operator|.
name|findValue
argument_list|(
name|nodeRef
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
name|reader
operator|.
name|getCurrentPosition
argument_list|()
condition|)
block|{
name|Value
name|v
init|=
name|domDb
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
return|return
operator|new
name|ErrorReport
operator|.
name|IndexError
argument_list|(
name|ErrorReport
operator|.
name|DOM_INDEX
argument_list|,
literal|"Failed to access node "
operator|+
name|nodeId
operator|+
literal|" through dom.dbx index. Wrong storage address. Expected: "
operator|+
name|p
operator|+
literal|"; got: "
operator|+
name|reader
operator|.
name|getCurrentPosition
argument_list|()
operator|+
literal|" - "
argument_list|,
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
operator|new
name|ErrorReport
operator|.
name|IndexError
argument_list|(
name|ErrorReport
operator|.
name|DOM_INDEX
argument_list|,
literal|"Failed to access node "
operator|+
name|nodeId
operator|+
literal|" through dom.dbx index."
argument_list|,
name|e
argument_list|,
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
return|;
block|}
block|}
name|StoredNode
name|node
init|=
name|reader
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|ErrorReport
operator|.
name|INCORRECT_NODE_TYPE
argument_list|,
literal|"Expected an element node, received node of type "
operator|+
name|node
operator|.
name|getNodeType
argument_list|()
argument_list|)
return|;
name|elementStack
operator|.
name|push
argument_list|(
operator|new
name|ElementNode
argument_list|(
operator|(
name|ElementImpl
operator|)
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|attribsAllowed
operator|=
literal|true
expr_stmt|;
name|attributeCount
operator|=
literal|0
expr_stmt|;
name|expectedAttribs
operator|=
name|reader
operator|.
name|getAttributeCount
argument_list|()
expr_stmt|;
break|break;
default|default:
if|if
condition|(
name|attribsAllowed
condition|)
block|{
if|if
condition|(
name|attributeCount
operator|!=
name|expectedAttribs
condition|)
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|ErrorReport
operator|.
name|INCORRECT_NODE_TYPE
argument_list|,
literal|"Wrong number of attributes. Expected: "
operator|+
name|expectedAttribs
operator|+
literal|"; found: "
operator|+
name|attributeCount
argument_list|)
return|;
block|}
name|attribsAllowed
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|elementStack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|ErrorReport
operator|.
name|NODE_HIERARCHY
argument_list|,
literal|"Error in node hierarchy: reached end of tree but "
operator|+
literal|"stack was not empty!"
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|ErrorReport
operator|.
name|RESOURCE_ACCESS_FAILED
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
operator|new
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|RESOURCE_ACCESS_FAILED
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
return|;
block|}
finally|finally
block|{
name|elementStack
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
block|}
operator|.
name|run
argument_list|()
return|;
block|}
specifier|private
class|class
name|DocumentCallback
implements|implements
name|BTreeCallback
block|{
specifier|private
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
decl_stmt|;
specifier|private
name|ProgressCallback
name|progress
decl_stmt|;
specifier|private
name|int
name|docCount
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|checkDocs
decl_stmt|;
specifier|private
name|int
name|lastPercentage
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|Agent
name|jmxAgent
init|=
name|AgentFactory
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|private
name|DocumentCallback
parameter_list|(
name|List
argument_list|<
name|ErrorReport
argument_list|>
name|errors
parameter_list|,
name|ProgressCallback
name|progress
parameter_list|,
name|boolean
name|checkDocs
parameter_list|)
block|{
name|this
operator|.
name|errors
operator|=
name|errors
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|progress
expr_stmt|;
name|this
operator|.
name|checkDocs
operator|=
name|checkDocs
expr_stmt|;
block|}
specifier|public
name|boolean
name|indexInfo
parameter_list|(
name|Value
name|key
parameter_list|,
name|long
name|pointer
parameter_list|)
throws|throws
name|TerminatedException
block|{
name|CollectionStore
name|store
init|=
operator|(
name|CollectionStore
operator|)
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getStorage
argument_list|(
name|NativeBroker
operator|.
name|COLLECTIONS_DBX_ID
argument_list|)
decl_stmt|;
name|int
name|collectionId
init|=
name|CollectionStore
operator|.
name|DocumentKey
operator|.
name|getCollectionId
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|int
name|docId
init|=
name|CollectionStore
operator|.
name|DocumentKey
operator|.
name|getDocumentId
argument_list|(
name|key
argument_list|)
decl_stmt|;
try|try
block|{
name|byte
name|type
init|=
name|key
operator|.
name|data
argument_list|()
index|[
name|key
operator|.
name|start
argument_list|()
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|DocumentImpl
operator|.
name|LENGTH_DOCUMENT_TYPE
index|]
decl_stmt|;
name|VariableByteInput
name|istream
init|=
name|store
operator|.
name|getAsStream
argument_list|(
name|pointer
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
name|doc
operator|=
operator|new
name|BinaryDocument
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|doc
operator|=
operator|new
name|DocumentImpl
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
expr_stmt|;
name|doc
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
name|docCount
operator|++
expr_stmt|;
if|if
condition|(
name|checkDocs
condition|)
block|{
if|if
condition|(
name|progress
operator|!=
literal|null
condition|)
name|progress
operator|.
name|startDocument
argument_list|(
name|doc
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|docCount
argument_list|,
name|getDocumentCount
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|percentage
init|=
literal|100
operator|*
operator|(
name|docCount
operator|+
literal|1
operator|)
operator|/
operator|(
name|getDocumentCount
argument_list|()
operator|+
literal|1
operator|)
decl_stmt|;
if|if
condition|(
operator|(
name|jmxAgent
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|percentage
operator|!=
name|lastPercentage
operator|)
condition|)
block|{
name|lastPercentage
operator|=
name|percentage
expr_stmt|;
name|jmxAgent
operator|.
name|updateStatus
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|percentage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
operator|&&
operator|!
name|directAccess
condition|)
block|{
name|ErrorReport
name|report
init|=
name|checkXMLTree
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|report
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|report
operator|instanceof
name|ErrorReport
operator|.
name|ResourceError
condition|)
operator|(
operator|(
name|ErrorReport
operator|.
name|ResourceError
operator|)
name|report
operator|)
operator|.
name|setDocumentId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
if|if
condition|(
name|errors
operator|!=
literal|null
condition|)
name|errors
operator|.
name|add
argument_list|(
name|report
argument_list|)
expr_stmt|;
if|if
condition|(
name|progress
operator|!=
literal|null
condition|)
name|progress
operator|.
name|error
argument_list|(
name|report
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|ResourceError
name|error
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|ResourceError
argument_list|(
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
operator|.
name|RESOURCE_ACCESS_FAILED
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
decl_stmt|;
name|error
operator|.
name|setDocumentId
argument_list|(
name|docId
argument_list|)
expr_stmt|;
if|if
condition|(
name|errors
operator|!=
literal|null
condition|)
name|errors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
if|if
condition|(
name|progress
operator|!=
literal|null
condition|)
name|progress
operator|.
name|error
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|public
interface|interface
name|ProgressCallback
block|{
specifier|public
name|void
name|startDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|current
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
specifier|public
name|void
name|startCollection
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
specifier|public
name|void
name|error
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ErrorReport
name|error
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

