begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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
name|Node
import|;
end_import

begin_comment
comment|/**  * Tests if a node is of a given node type.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|TypeTest
implements|implements
name|NodeTest
block|{
specifier|protected
name|int
name|nodeType
init|=
literal|0
decl_stmt|;
specifier|public
name|TypeTest
parameter_list|(
name|int
name|nodeType
parameter_list|)
block|{
name|setType
argument_list|(
name|nodeType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|int
name|nodeType
parameter_list|)
block|{
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|protected
name|boolean
name|isOfType
parameter_list|(
name|short
name|type
parameter_list|)
block|{
name|int
name|domType
decl_stmt|;
switch|switch
condition|(
name|nodeType
condition|)
block|{
case|case
name|Type
operator|.
name|ELEMENT
case|:
name|domType
operator|=
name|Node
operator|.
name|ELEMENT_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|TEXT
case|:
name|domType
operator|=
name|Node
operator|.
name|TEXT_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|ATTRIBUTE
case|:
name|domType
operator|=
name|Node
operator|.
name|ATTRIBUTE_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|COMMENT
case|:
name|domType
operator|=
name|Node
operator|.
name|COMMENT_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|PROCESSING_INSTRUCTION
case|:
name|domType
operator|=
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|NODE
case|:
default|default :
return|return
literal|true
return|;
block|}
return|return
operator|(
name|type
operator|==
name|domType
operator|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|nodeType
operator|==
operator|-
literal|1
condition|?
literal|"node()"
else|:
name|Type
operator|.
name|NODETYPES
index|[
name|nodeType
index|]
operator|+
literal|"()"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.NodeTest#matches(org.exist.dom.NodeProxy) 	 */
specifier|public
name|boolean
name|matches
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
if|if
condition|(
name|proxy
operator|.
name|getNodeType
argument_list|()
operator|==
name|Type
operator|.
name|ITEM
operator|||
name|proxy
operator|.
name|getNodeType
argument_list|()
operator|==
name|Type
operator|.
name|NODE
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|nodeType
operator|==
name|Type
operator|.
name|NODE
condition|)
return|return
literal|true
return|;
name|Node
name|node
init|=
name|proxy
operator|.
name|getNode
argument_list|()
decl_stmt|;
return|return
name|matches
argument_list|(
name|node
argument_list|)
return|;
block|}
else|else
return|return
name|isOfType
argument_list|(
name|proxy
operator|.
name|getNodeType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|isOfType
argument_list|(
name|other
operator|.
name|getNodeType
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.NodeTest#isWildcardTest() 	 */
specifier|public
name|boolean
name|isWildcardTest
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

