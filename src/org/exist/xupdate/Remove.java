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
name|org
operator|.
name|apache
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
name|NodeImpl
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
name|util
operator|.
name|XMLUtil
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

begin_comment
comment|/**  * Remove.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Remove
extends|extends
name|Modification
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Remove
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** 	 * Constructor for Remove. 	 * @param pool 	 * @param user 	 * @param selectStmt 	 */
specifier|public
name|Remove
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|User
name|user
parameter_list|,
name|String
name|selectStmt
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|user
argument_list|,
name|selectStmt
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.xupdate.Modification#process(org.exist.dom.DocumentSet) 	 */
specifier|public
name|long
name|process
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|XMLUtil
operator|.
name|dump
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
name|ArrayList
name|qr
init|=
name|select
argument_list|(
name|docs
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"select found "
operator|+
name|qr
operator|.
name|size
argument_list|()
operator|+
literal|" nodes for remove"
argument_list|)
expr_stmt|;
name|NodeImpl
name|node
decl_stmt|;
name|Node
name|parent
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|qr
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|node
operator|=
operator|(
name|NodeImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
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
if|if
condition|(
operator|!
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getPermissions
argument_list|()
operator|.
name|validate
argument_list|(
name|user
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
literal|"write access to collection denied; user="
operator|+
name|user
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
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
name|user
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
literal|"permission to remove document denied"
argument_list|)
throw|;
name|parent
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"cannot remove the root node"
argument_list|)
expr_stmt|;
block|}
else|else
name|parent
operator|.
name|removeChild
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
name|qr
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 * @see org.exist.xupdate.Modification#getName() 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"remove"
return|;
block|}
block|}
end_class

end_unit

