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
name|NodeSet
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
comment|/**  * Append.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Append
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
name|Append
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** 	 * Constructor for Append. 	 * @param selectStmt 	 */
specifier|public
name|Append
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
comment|/** 	 * @see org.exist.xupdate.Modification#process() 	 */
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
name|NodeSet
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
name|getLength
argument_list|()
operator|+
literal|" nodes for append"
argument_list|)
expr_stmt|;
name|NodeProxy
name|proxy
decl_stmt|;
name|Node
name|node
decl_stmt|;
name|NodeList
name|children
init|=
name|content
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|children
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|len
operator|+
literal|" nodes to append"
argument_list|)
expr_stmt|;
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
name|proxy
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|node
operator|=
name|proxy
operator|.
name|getNode
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
name|node
operator|.
name|appendChild
argument_list|(
name|children
operator|.
name|item
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|qr
operator|.
name|getLength
argument_list|()
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"append"
return|;
block|}
block|}
end_class

end_unit

