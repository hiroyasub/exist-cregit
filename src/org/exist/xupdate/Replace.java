begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|dom
operator|.
name|AttrImpl
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
name|DocumentSet
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
name|StoredNode
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
name|TextImpl
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
name|NotificationService
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
name|UpdateListener
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
name|xquery
operator|.
name|XPathException
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * Implements xupdate:replace, an extension to the XUpdate standard.  * The modification replaces a node and its contents. It differs from xupdate:update  * which only replaces the contents of the node, not the node itself.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Replace
extends|extends
name|Modification
block|{
comment|/** 	 * @param broker 	 * @param docs 	 * @param selectStmt 	 * @param namespaces 	 * @param variables 	 */
specifier|public
name|Replace
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|selectStmt
parameter_list|,
name|Map
name|namespaces
parameter_list|,
name|Map
name|variables
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|selectStmt
argument_list|,
name|namespaces
argument_list|,
name|variables
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xupdate.Modification#process() 	 */
specifier|public
name|long
name|process
parameter_list|(
name|Txn
name|transaction
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
name|NodeList
name|children
init|=
name|content
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|children
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"xupdate:replace requires exactly one content node"
argument_list|)
throw|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"processing replace ..."
argument_list|)
expr_stmt|;
name|int
name|modifications
init|=
name|children
operator|.
name|getLength
argument_list|()
decl_stmt|;
try|try
block|{
name|StoredNode
name|ql
index|[]
init|=
name|selectAndLock
argument_list|()
decl_stmt|;
name|IndexListener
name|listener
init|=
operator|new
name|IndexListener
argument_list|(
name|ql
argument_list|)
decl_stmt|;
name|NotificationService
name|notifier
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNotificationService
argument_list|()
decl_stmt|;
name|NodeImpl
name|node
decl_stmt|;
name|Node
name|temp
decl_stmt|;
name|TextImpl
name|text
decl_stmt|;
name|AttrImpl
name|attribute
decl_stmt|;
name|ElementImpl
name|parent
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
name|DocumentSet
name|modifiedDocs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ql
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
name|ql
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"select "
operator|+
name|selectStmt
operator|+
literal|" returned empty node set"
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setIndexListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|modifiedDocs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
name|broker
operator|.
name|getUser
argument_list|()
argument_list|,
name|Permission
operator|.
name|UPDATE
argument_list|)
condition|)
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"permission to update document denied"
argument_list|)
throw|;
name|parent
operator|=
operator|(
name|ElementImpl
operator|)
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"The root element of a document can not be replaced with 'xu:replace'. "
operator|+
literal|"Please consider removing the document or use 'xu:update' to just replace the children of the root."
argument_list|)
throw|;
switch|switch
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
if|if
condition|(
name|modifications
operator|==
literal|0
condition|)
name|modifications
operator|=
literal|1
expr_stmt|;
name|temp
operator|=
name|children
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|parent
operator|.
name|replaceChild
argument_list|(
name|transaction
argument_list|,
name|temp
argument_list|,
name|node
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|temp
operator|=
name|children
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|text
operator|=
operator|new
name|TextImpl
argument_list|(
name|temp
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|modifications
operator|=
literal|1
expr_stmt|;
name|text
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|parent
operator|.
name|updateChild
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|text
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|AttrImpl
name|attr
init|=
operator|(
name|AttrImpl
operator|)
name|node
decl_stmt|;
name|temp
operator|=
name|children
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|attribute
operator|=
operator|new
name|AttrImpl
argument_list|(
name|attr
operator|.
name|getQName
argument_list|()
argument_list|,
name|temp
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|attribute
operator|.
name|setOwnerDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|parent
operator|.
name|updateChild
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|attribute
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"unsupported node-type"
argument_list|)
throw|;
block|}
name|doc
operator|.
name|setLastModified
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|broker
operator|.
name|storeDocument
argument_list|(
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|notifier
operator|.
name|notifyUpdate
argument_list|(
name|doc
argument_list|,
name|UpdateListener
operator|.
name|UPDATE
argument_list|)
expr_stmt|;
block|}
name|checkFragmentation
argument_list|(
name|transaction
argument_list|,
name|modifiedDocs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|unlockDocuments
argument_list|()
expr_stmt|;
block|}
return|return
name|modifications
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xupdate.Modification#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|XUpdateProcessor
operator|.
name|REPLACE
return|;
block|}
block|}
end_class

end_unit

