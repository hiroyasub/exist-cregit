begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|XMLGregorianCalendar
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
name|*
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
name|io
operator|.
name|VariableByteOutputStream
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
name|*
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
name|DOMException
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
name|NodeList
import|;
end_import

begin_comment
comment|/**  * A node in the database.  Nodes are most often contained in XML documents, but can also  * be transient in-memory nodes created by a query.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|Node
extends|extends
name|Item
block|{
specifier|private
name|XMLDocument
name|document
decl_stmt|;
specifier|private
specifier|final
name|StaleMarker
name|staleMarker
init|=
operator|new
name|StaleMarker
argument_list|()
decl_stmt|;
specifier|private
name|Node
parameter_list|()
block|{
block|}
name|Node
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
name|item
parameter_list|,
name|NamespaceMap
name|namespaceBindings
parameter_list|,
name|Database
name|db
parameter_list|)
block|{
name|super
argument_list|(
name|item
argument_list|,
name|namespaceBindings
argument_list|,
name|db
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|item
operator|instanceof
name|NodeValue
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"item is not a node"
argument_list|)
throw|;
if|if
condition|(
name|item
operator|instanceof
name|NodeProxy
condition|)
block|{
name|NodeProxy
name|proxy
init|=
operator|(
name|NodeProxy
operator|)
name|item
decl_stmt|;
name|String
name|docPath
init|=
name|proxy
operator|.
name|getDocument
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
decl_stmt|;
name|staleMarker
operator|.
name|track
argument_list|(
name|docPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|docPath
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// folder
name|staleMarker
operator|.
name|track
argument_list|(
name|docPath
argument_list|)
expr_stmt|;
comment|// document
name|staleMarker
operator|.
name|track
argument_list|(
name|docPath
operator|+
literal|"#"
operator|+
name|proxy
operator|.
name|getNodeId
argument_list|()
argument_list|)
expr_stmt|;
comment|// node
block|}
block|}
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|getDOMNode
parameter_list|()
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|domNode
init|=
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|domNode
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"unable to load node data"
argument_list|)
throw|;
return|return
name|domNode
return|;
block|}
comment|/** 	 * Return this node. 	 *  	 * @return this node 	 */
annotation|@
name|Override
specifier|public
name|Node
name|node
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/** 	 * Return whether this node represents the same node in the database as the given object. 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|Node
operator|)
condition|)
return|return
literal|false
return|;
name|Node
name|that
init|=
operator|(
name|Node
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|item
operator|==
name|that
operator|.
name|item
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|this
operator|.
name|item
operator|instanceof
name|NodeProxy
operator|&&
name|that
operator|.
name|item
operator|instanceof
name|NodeProxy
condition|)
block|{
name|NodeProxy
name|thisProxy
init|=
operator|(
name|NodeProxy
operator|)
name|this
operator|.
name|item
decl_stmt|,
name|thatProxy
init|=
operator|(
name|NodeProxy
operator|)
name|that
operator|.
name|item
decl_stmt|;
return|return
name|thisProxy
operator|.
name|getDocument
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|equals
argument_list|(
name|thatProxy
operator|.
name|getDocument
argument_list|()
operator|.
name|getURI
argument_list|()
argument_list|)
operator|&&
name|thisProxy
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|thatProxy
operator|.
name|getNodeId
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/** 	 * Warning:  computing a node's hash code is surprisingly expensive, and the value is not cached. 	 * You should not use nodes in situations where they might get hashed. 	 */
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|computeHashCode
argument_list|()
return|;
block|}
specifier|private
name|int
name|computeHashCode
parameter_list|()
block|{
if|if
condition|(
name|item
operator|instanceof
name|NodeProxy
condition|)
block|{
name|NodeProxy
name|proxy
init|=
operator|(
name|NodeProxy
operator|)
name|item
decl_stmt|;
name|VariableByteOutputStream
name|buf
init|=
operator|new
name|VariableByteOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|proxy
operator|.
name|getNodeId
argument_list|()
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unable to serialize node's id to compute hashCode"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|proxy
operator|.
name|getDocument
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|Arrays
operator|.
name|hashCode
argument_list|(
name|buf
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|item
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/** 	 * Return the document to which this node belongs. 	 *  	 * @return the document to which this node belongs 	 * @throws UnsupportedOperationException if this node does not belong to a document 	 */
specifier|public
name|XMLDocument
name|document
parameter_list|()
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
if|if
condition|(
name|document
operator|==
literal|null
condition|)
try|try
block|{
name|document
operator|=
name|Document
operator|.
name|newInstance
argument_list|(
operator|(
operator|(
name|NodeProxy
operator|)
name|item
operator|)
operator|.
name|getDocument
argument_list|()
argument_list|,
name|this
argument_list|)
operator|.
name|xml
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"node is not part of a document in the database"
argument_list|)
throw|;
block|}
return|return
name|document
return|;
block|}
comment|/** 	 * Return a builder that will append elements to this node's children.  The builder will return the 	 * appended node if a single node was appended, otherwise<code>null</code>. 	 * 	 * @return a builder that will append nodes to this node 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|Node
argument_list|>
name|append
parameter_list|()
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
comment|// do an early check to fail-fast, we'll check again on completion
try|try
block|{
specifier|final
name|StoredNode
name|node
init|=
operator|(
name|StoredNode
operator|)
name|getDOMNode
argument_list|()
decl_stmt|;
return|return
operator|new
name|ElementBuilder
argument_list|<
name|Node
argument_list|>
argument_list|(
name|namespaceBindings
argument_list|,
literal|true
argument_list|,
operator|new
name|ElementBuilder
operator|.
name|CompletedCallback
argument_list|<
name|Node
argument_list|>
argument_list|()
block|{
specifier|public
name|Node
name|completed
parameter_list|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
index|[]
name|nodes
parameter_list|)
block|{
name|Transaction
name|tx
init|=
name|Database
operator|.
name|requireTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|StoredNode
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nodes
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|result
operator|=
operator|(
name|StoredNode
operator|)
name|node
operator|.
name|appendChild
argument_list|(
name|nodes
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|appendChildren
argument_list|(
name|tx
operator|.
name|tx
argument_list|,
name|toNodeList
argument_list|(
name|nodes
argument_list|)
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|defrag
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|NodeProxy
name|proxy
init|=
operator|new
name|NodeProxy
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|result
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|result
operator|.
name|getNodeId
argument_list|()
argument_list|,
name|result
operator|.
name|getNodeType
argument_list|()
argument_list|,
name|result
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
decl_stmt|;
name|Database
operator|.
name|trackNode
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
return|return
operator|new
name|Node
argument_list|(
name|proxy
argument_list|,
name|namespaceBindings
operator|.
name|extend
argument_list|()
argument_list|,
name|db
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DOMException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|tx
operator|.
name|abortIfIncomplete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
if|if
condition|(
name|getDOMNode
argument_list|()
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"updates on in-memory nodes are not yet supported, but calling query().single(\"self::*\").node() on the node will implicitly materialize the result in a temporary area of the database"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot update attributes on a "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** 	 * Delete this node from its parent.  This can delete an element from a document, 	 * or an attribute from an element, etc.  Trying to delete the root element of a 	 * document will delete the document instead.  If the node cannot be found, assume 	 * it's already been deleted and return silently. 	 */
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|child
decl_stmt|;
try|try
block|{
name|child
operator|=
name|getDOMNode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DatabaseException
name|e
parameter_list|)
block|{
return|return;
block|}
name|NodeImpl
name|parent
init|=
operator|(
name|NodeImpl
operator|)
name|child
operator|.
name|getParentNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|child
operator|instanceof
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
operator|||
name|parent
operator|instanceof
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
condition|)
block|{
name|document
argument_list|()
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"cannot delete node with no parent"
argument_list|)
throw|;
block|}
else|else
block|{
name|Transaction
name|tx
init|=
name|Database
operator|.
name|requireTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|parent
operator|.
name|removeChild
argument_list|(
name|tx
operator|.
name|tx
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DOMException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|tx
operator|.
name|abortIfIncomplete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Return the name of this node, in the "prefix:localName" form. 	 * 	 * @return the name of this node 	 */
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|getDOMNode
argument_list|()
operator|.
name|getNodeName
argument_list|()
return|;
block|}
comment|/** 	 * Return the qualified name of this node, including its namespace URI, local name and prefix. 	 * 	 * @return the qname of this node 	 */
specifier|public
name|QName
name|qname
parameter_list|()
block|{
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|node
init|=
name|getDOMNode
argument_list|()
decl_stmt|;
name|String
name|localName
init|=
name|node
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|localName
operator|==
literal|null
condition|)
name|localName
operator|=
name|node
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
return|return
operator|new
name|QName
argument_list|(
name|node
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|localName
argument_list|,
name|node
operator|.
name|getPrefix
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Return a builder that will replace this node.  The builder returns<code>null</code>. 	 * 	 * @return a builder that will replace this node 	 * @throws UnsupportedOperationException if the node does not have a parent 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|?
argument_list|>
name|replace
parameter_list|()
block|{
comment|// TODO: right now, can only replace an element; what about other nodes?
comment|// TODO: right now, can only replace with a single node, investigate multiple replace
try|try
block|{
specifier|final
name|NodeImpl
name|oldNode
init|=
operator|(
name|NodeImpl
operator|)
name|getDOMNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldNode
operator|.
name|getParentNode
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot replace a "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" with no parent"
argument_list|)
throw|;
return|return
operator|new
name|ElementBuilder
argument_list|<
name|Object
argument_list|>
argument_list|(
name|namespaceBindings
argument_list|,
literal|false
argument_list|,
operator|new
name|ElementBuilder
operator|.
name|CompletedCallback
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|completed
parameter_list|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
index|[]
name|nodes
parameter_list|)
block|{
assert|assert
name|nodes
operator|.
name|length
operator|==
literal|1
assert|;
name|Transaction
name|tx
init|=
name|Database
operator|.
name|requireTransaction
argument_list|()
decl_stmt|;
try|try
block|{
operator|(
operator|(
name|NodeImpl
operator|)
name|oldNode
operator|.
name|getParentNode
argument_list|()
operator|)
operator|.
name|replaceChild
argument_list|(
name|tx
operator|.
name|tx
argument_list|,
name|nodes
index|[
literal|0
index|]
argument_list|,
name|oldNode
argument_list|)
expr_stmt|;
name|defrag
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// no point in returning the old node; we'd rather return the newly inserted one,
comment|// but it's not easily available
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|DOMException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|tx
operator|.
name|abortIfIncomplete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
if|if
condition|(
name|getDOMNode
argument_list|()
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"updates on in-memory nodes are not yet supported, but calling query().single(\"self::*\").node() on the node will implicitly materialize the result in a temporary area of the database"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot update attributes on a "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** 	 * Return a builder for updating the attribute values of this element. 	 * 	 * @return an attribute builder for this element 	 * @throws UnsupportedOperationException if this node is not an element 	 */
specifier|public
name|AttributeBuilder
name|update
parameter_list|()
block|{
try|try
block|{
specifier|final
name|ElementImpl
name|elem
init|=
operator|(
name|ElementImpl
operator|)
name|getDOMNode
argument_list|()
decl_stmt|;
return|return
operator|new
name|AttributeBuilder
argument_list|(
name|elem
argument_list|,
name|namespaceBindings
argument_list|,
operator|new
name|AttributeBuilder
operator|.
name|CompletedCallback
argument_list|()
block|{
specifier|public
name|void
name|completed
parameter_list|(
name|NodeList
name|removeList
parameter_list|,
name|NodeList
name|addList
parameter_list|)
block|{
name|Transaction
name|tx
init|=
name|Database
operator|.
name|requireTransaction
argument_list|()
decl_stmt|;
try|try
block|{
name|elem
operator|.
name|removeAppendAttributes
argument_list|(
name|tx
operator|.
name|tx
argument_list|,
name|removeList
argument_list|,
name|addList
argument_list|)
expr_stmt|;
name|defrag
argument_list|(
name|tx
argument_list|)
expr_stmt|;
name|tx
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|tx
operator|.
name|abortIfIncomplete
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
if|if
condition|(
name|getDOMNode
argument_list|()
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"updates on in-memory nodes are not yet supported, but calling query().single(\"self::*\").node() on the node will implicitly materialize the result in a temporary area of the database"
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot update attributes on a "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|defrag
parameter_list|(
name|Transaction
name|tx
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|item
operator|instanceof
name|NodeProxy
operator|)
condition|)
return|return;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|db
operator|.
name|acquireBroker
argument_list|()
expr_stmt|;
name|Integer
name|fragmentationLimit
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getInteger
argument_list|(
name|DBBroker
operator|.
name|PROPERTY_XUPDATE_FRAGMENTATION_FACTOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragmentationLimit
operator|==
literal|null
condition|)
name|fragmentationLimit
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
operator|(
operator|(
name|NodeProxy
operator|)
name|item
operator|)
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getSplitCount
argument_list|()
operator|>
name|fragmentationLimit
condition|)
name|broker
operator|.
name|defragXMLResource
argument_list|(
name|tx
operator|.
name|tx
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|NodeList
name|toNodeList
parameter_list|(
specifier|final
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
index|[]
name|nodes
parameter_list|)
block|{
return|return
operator|new
name|NodeList
argument_list|()
block|{
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|nodes
operator|.
name|length
return|;
block|}
specifier|public
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|item
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|nodes
index|[
name|index
index|]
return|;
block|}
block|}
return|;
block|}
comment|/** 	 * A null node, used as a placeholder where an actual<code>null</code> would be inappropriate. 	 */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"hiding"
argument_list|)
specifier|static
specifier|final
name|Node
name|NULL
init|=
operator|new
name|Node
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|ElementBuilder
argument_list|<
name|Node
argument_list|>
name|append
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot append to a null resource"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|XMLDocument
name|document
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"null resource does not have a document"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"null resource does not have a name"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|QName
name|qname
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"null resource does not have a qname"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|ElementBuilder
argument_list|<
name|?
argument_list|>
name|replace
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot replace a null resource"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|AttributeBuilder
name|update
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"cannot update a null resource"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|booleanValue
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|booleanValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|doubleValue
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|doubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|intValue
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|intValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|longValue
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|longValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Duration
name|durationValue
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|durationValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|XMLGregorianCalendar
name|dateTimeValue
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|dateTimeValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Date
name|instantValue
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|instantValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|node
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|node
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|extant
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|extant
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryService
name|query
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|query
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|value
argument_list|()
return|;
block|}
annotation|@
name|Override
name|Sequence
name|convertToSequence
parameter_list|()
block|{
return|return
name|Item
operator|.
name|NULL
operator|.
name|convertToSequence
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NULL Node"
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

