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
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|selectStmt
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|,
name|selectStmt
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.xupdate.Modification#process(org.exist.dom.DocumentSet) 	 */
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
literal|"permission to remove document denied"
argument_list|)
throw|;
name|collection
operator|=
name|doc
operator|.
name|getCollection
argument_list|()
expr_stmt|;
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
name|doc
operator|.
name|setIndexListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
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
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"you cannot remove the document element. Use update "
operator|+
literal|"instead"
argument_list|)
throw|;
block|}
else|else
name|parent
operator|.
name|removeChild
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|doc
operator|.
name|clearIndexListener
argument_list|()
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
name|prevCollection
operator|=
name|collection
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
name|qr
operator|.
name|length
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

