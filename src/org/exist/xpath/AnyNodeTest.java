begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * AnyNodeTest.java - Aug 30, 2003  *   * @author wolf  */
end_comment

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
name|AnyNodeTest
implements|implements
name|NodeTest
block|{
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.NodeTest#getName() 	 */
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.NodeTest#isWildcardTest() 	 */
specifier|public
name|boolean
name|isWildcardTest
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.NodeTest#matches(org.w3c.dom.Node) 	 */
specifier|public
name|boolean
name|matches
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
return|return
literal|true
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
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#toString() 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"node()"
return|;
block|}
block|}
end_class

end_unit

