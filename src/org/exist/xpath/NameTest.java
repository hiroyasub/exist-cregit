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
name|xpath
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

begin_class
specifier|public
class|class
name|NameTest
extends|extends
name|TypeTest
block|{
specifier|protected
name|QName
name|nodeName
decl_stmt|;
specifier|public
name|NameTest
parameter_list|(
name|int
name|type
parameter_list|,
name|QName
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|nodeName
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.NodeTest#matches(org.exist.dom.NodeProxy) 	 */
specifier|public
name|boolean
name|matches
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|Node
name|node
init|=
literal|null
decl_stmt|;
name|short
name|type
init|=
name|proxy
operator|.
name|nodeType
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
name|node
operator|=
name|proxy
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|type
operator|=
name|node
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isOfType
argument_list|(
name|type
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
name|node
operator|=
name|proxy
operator|.
name|getNode
argument_list|()
expr_stmt|;
return|return
name|matchesInternal
argument_list|(
name|node
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
operator|!
name|isOfType
argument_list|(
name|other
operator|.
name|getNodeType
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
name|matchesInternal
argument_list|(
name|other
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|matchesInternal
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
if|if
condition|(
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
if|if
condition|(
name|nodeName
operator|.
name|getLocalName
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|nodeName
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.NodeTest#isWildcardTest() 	 */
specifier|public
name|boolean
name|isWildcardTest
parameter_list|()
block|{
return|return
name|nodeName
operator|.
name|getLocalName
argument_list|()
operator|==
literal|null
operator|||
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#toString() 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|nodeName
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

