begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Update.java - Apr 29, 2003  *   * @author wolf  */
end_comment

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

begin_class
specifier|public
class|class
name|Update
extends|extends
name|Modification
block|{
comment|/** 	 * @param pool 	 * @param user 	 * @param selectStmt 	 */
specifier|public
name|Update
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
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xupdate.Modification#process(org.exist.dom.DocumentSet) 	 */
specifier|public
name|long
name|process
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
name|NodeImpl
index|[]
name|qr
init|=
name|select
argument_list|(
name|docs
argument_list|)
decl_stmt|;
name|NodeList
name|children
init|=
name|content
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|qr
operator|==
literal|null
condition|)
return|return
literal|0
return|;
name|IndexListener
name|listener
init|=
operator|new
name|IndexListener
argument_list|(
name|qr
argument_list|)
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
name|Collection
name|collection
init|=
literal|null
decl_stmt|,
name|prevCollection
init|=
literal|null
decl_stmt|;
name|int
name|result
init|=
name|children
operator|.
name|getLength
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
name|qr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
name|qr
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
literal|" returned empty node"
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
name|collection
operator|=
name|doc
operator|.
name|getCollection
argument_list|()
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
if|if
condition|(
name|prevCollection
operator|!=
literal|null
operator|&&
name|collection
operator|!=
name|prevCollection
condition|)
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|saveCollection
argument_list|(
name|prevCollection
argument_list|)
expr_stmt|;
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
name|result
operator|==
literal|0
condition|)
name|result
operator|=
literal|1
expr_stmt|;
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|update
argument_list|(
name|children
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
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
name|children
operator|.
name|getLength
argument_list|()
operator|!=
literal|0
condition|)
block|{
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
block|}
else|else
block|{
name|result
operator|=
literal|1
expr_stmt|;
name|text
operator|=
operator|new
name|TextImpl
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
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
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"parent node not found for "
operator|+
name|node
operator|.
name|getGID
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|AttrImpl
name|attr
init|=
operator|(
name|AttrImpl
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|children
operator|.
name|getLength
argument_list|()
operator|!=
literal|0
condition|)
block|{
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
block|}
else|else
block|{
name|result
operator|=
literal|1
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
literal|""
argument_list|)
expr_stmt|;
block|}
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
name|node
argument_list|,
name|attribute
argument_list|)
expr_stmt|;
break|break;
default|default :
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"unsupported node-type"
argument_list|)
throw|;
block|}
name|prevCollection
operator|=
name|collection
expr_stmt|;
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
block|}
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getBroker
argument_list|()
operator|.
name|saveCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xupdate.Modification#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"update"
return|;
block|}
block|}
end_class

end_unit

