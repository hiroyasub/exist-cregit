begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|dom
operator|.
name|ArraySet
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
name|dom
operator|.
name|SingleNodeSet
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

begin_class
specifier|public
class|class
name|FunId
extends|extends
name|Function
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
name|Function
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** 	 * Constructor for FunId. 	 */
specifier|public
name|FunId
parameter_list|()
block|{
name|super
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.xpath.Expression#eval(org.exist.dom.DocumentSet, org.exist.dom.NodeSet, org.exist.dom.NodeProxy) 	 */
specifier|public
name|Value
name|eval
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|NodeProxy
name|contextNode
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|<
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"function id requires one argument"
argument_list|)
throw|;
if|if
condition|(
name|contextNode
operator|!=
literal|null
condition|)
name|contextSet
operator|=
operator|new
name|SingleNodeSet
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
name|Expression
name|arg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Value
name|idval
init|=
name|arg
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|)
decl_stmt|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|5
argument_list|)
decl_stmt|;
if|if
condition|(
name|idval
operator|.
name|getType
argument_list|()
operator|==
name|Value
operator|.
name|isNodeList
condition|)
block|{
name|NodeSet
name|set
init|=
operator|(
name|NodeSet
operator|)
name|idval
operator|.
name|getNodeList
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
name|idval
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|QName
name|id
init|=
operator|new
name|QName
argument_list|(
literal|"&"
operator|+
name|set
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|getId
argument_list|(
name|context
argument_list|,
name|result
argument_list|,
name|docs
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|QName
name|id
init|=
operator|new
name|QName
argument_list|(
literal|"&"
operator|+
name|idval
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|getId
argument_list|(
name|context
argument_list|,
name|result
argument_list|,
name|docs
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|private
name|void
name|getId
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|NodeSet
name|result
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|id
parameter_list|)
block|{
name|NodeSet
name|attribs
init|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|findElementsByTagName
argument_list|(
name|docs
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|attribs
operator|.
name|getLength
argument_list|()
operator|+
literal|" attributes for id "
operator|+
name|id
argument_list|)
expr_stmt|;
name|NodeProxy
name|n
decl_stmt|,
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|attribs
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
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|p
operator|=
operator|new
name|NodeProxy
argument_list|(
name|n
operator|.
name|doc
argument_list|,
name|XMLUtil
operator|.
name|getParentId
argument_list|(
name|n
operator|.
name|doc
argument_list|,
name|n
operator|.
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * @see org.exist.xpath.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|TYPE_NODELIST
return|;
block|}
block|}
end_class

end_unit

