begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xqj
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQCommonHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQItemType
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  *  */
end_comment

begin_class
specifier|public
class|class
name|XQItem
implements|implements
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQItem
block|{
name|Item
name|item
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|XQItem
parameter_list|()
block|{
name|item
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|XQItem
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
name|this
operator|.
name|item
operator|=
name|item
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItem#close() 	 */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|XQException
block|{
name|item
operator|=
literal|null
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItem#isClosed() 	 */
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|item
operator|==
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getAtomicValue() 	 */
specifier|public
name|String
name|getAtomicValue
parameter_list|()
throws|throws
name|XQException
block|{
try|try
block|{
if|if
condition|(
name|item
operator|!=
literal|null
condition|)
return|return
name|item
operator|.
name|atomize
argument_list|()
operator|.
name|getStringValue
argument_list|()
return|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|XQException
argument_list|(
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getBoolean() 	 */
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getByte() 	 */
specifier|public
name|byte
name|getByte
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getDouble() 	 */
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getFloat() 	 */
specifier|public
name|float
name|getFloat
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getInt() 	 */
specifier|public
name|int
name|getInt
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getItemAsString() 	 */
specifier|public
name|String
name|getItemAsString
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getItemType() 	 */
specifier|public
name|XQItemType
name|getItemType
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getLong() 	 */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getNode() 	 */
specifier|public
name|Node
name|getNode
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getNodeUri() 	 */
specifier|public
name|URI
name|getNodeUri
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getObject() 	 */
specifier|public
name|Object
name|getObject
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getObject(javax.xml.xquery.XQCommonHandler) 	 */
specifier|public
name|Object
name|getObject
parameter_list|(
name|XQCommonHandler
name|handler
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#getShort() 	 */
specifier|public
name|short
name|getShort
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#instanceOf(javax.xml.xquery.XQItemType) 	 */
specifier|public
name|boolean
name|instanceOf
parameter_list|(
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#writeItem(java.io.OutputStream, java.util.Properties) 	 */
specifier|public
name|void
name|writeItem
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|Properties
name|props
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#writeItem(java.io.Writer, java.util.Properties) 	 */
specifier|public
name|void
name|writeItem
parameter_list|(
name|Writer
name|ow
parameter_list|,
name|Properties
name|props
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQItemAccessor#writeItemToSAX(org.xml.sax.ContentHandler) 	 */
specifier|public
name|void
name|writeItemToSAX
parameter_list|(
name|ContentHandler
name|saxHandler
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

