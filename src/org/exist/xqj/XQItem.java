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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|net
operator|.
name|URISyntaxException
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
name|BooleanValue
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
name|DoubleValue
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
name|FloatValue
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
name|IntegerValue
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|NodeValue
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
name|Document
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
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  *   */
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
specifier|public
name|XQItem
parameter_list|(
name|boolean
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
name|item
operator|=
operator|new
name|BooleanValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|item
operator|=
name|convertTo
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XQItem
parameter_list|(
name|byte
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
try|try
block|{
name|item
operator|=
operator|new
name|IntegerValue
argument_list|(
name|value
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
operator|.
name|BYTE
argument_list|)
expr_stmt|;
name|item
operator|=
name|convertTo
argument_list|(
name|type
argument_list|)
expr_stmt|;
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
literal|"Unable to create XQItem from byte: "
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|XQItem
parameter_list|(
name|double
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
name|item
operator|=
operator|new
name|DoubleValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|item
operator|=
name|convertTo
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XQItem
parameter_list|(
name|float
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
name|item
operator|=
operator|new
name|FloatValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|item
operator|=
name|convertTo
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItem#close() 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItem#isClosed() 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getAtomicValue() 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getBoolean() 	 */
specifier|public
name|boolean
name|getBoolean
parameter_list|()
throws|throws
name|XQException
block|{
name|BooleanValue
name|b
init|=
operator|(
name|BooleanValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
return|return
name|b
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getByte() 	 */
specifier|public
name|byte
name|getByte
parameter_list|()
throws|throws
name|XQException
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|BYTE
argument_list|)
decl_stmt|;
comment|// return v.getInt();
return|return
literal|0
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getDouble() 	 */
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|XQException
block|{
name|DoubleValue
name|d
init|=
operator|(
name|DoubleValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
return|return
name|d
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getFloat() 	 */
specifier|public
name|float
name|getFloat
parameter_list|()
throws|throws
name|XQException
block|{
name|FloatValue
name|f
init|=
operator|(
name|FloatValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|FLOAT
argument_list|)
decl_stmt|;
return|return
name|f
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getInt() 	 */
specifier|public
name|int
name|getInt
parameter_list|()
throws|throws
name|XQException
block|{
try|try
block|{
name|IntegerValue
name|i
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|INT
argument_list|)
decl_stmt|;
return|return
name|i
operator|.
name|getInt
argument_list|()
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getItemAsString() 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getItemType() 	 */
specifier|public
name|XQItemType
name|getItemType
parameter_list|()
throws|throws
name|XQException
block|{
return|return
literal|null
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getLong() 	 */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|XQException
block|{
name|IntegerValue
name|d
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
return|return
name|d
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getNode() 	 */
specifier|public
name|Node
name|getNode
parameter_list|()
throws|throws
name|XQException
block|{
try|try
block|{
name|NodeValue
name|n
init|=
operator|(
name|NodeValue
operator|)
name|item
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NODE
argument_list|)
decl_stmt|;
return|return
name|n
operator|.
name|getNode
argument_list|()
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getNodeUri() 	 */
specifier|public
name|URI
name|getNodeUri
parameter_list|()
throws|throws
name|XQException
block|{
try|try
block|{
name|NodeValue
name|n
init|=
operator|(
name|NodeValue
operator|)
name|item
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NODE
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|n
operator|.
name|getOwnerDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
comment|/* 				String documentURI = n.getOwnerDocument().getDocumentURI();                             */
name|String
name|documentURI
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Method
name|method
init|=
name|Document
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"getDocumentURI"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|documentURI
operator|=
operator|(
name|String
operator|)
name|method
operator|.
name|invoke
argument_list|(
name|n
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XQException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|documentURI
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|URI
argument_list|(
name|documentURI
argument_list|)
return|;
block|}
block|}
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
catch|catch
parameter_list|(
name|URISyntaxException
name|use
parameter_list|)
block|{
throw|throw
operator|new
name|XQException
argument_list|(
name|use
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getObject() 	 */
specifier|public
name|Object
name|getObject
parameter_list|()
throws|throws
name|XQException
block|{
return|return
literal|null
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getObject(javax.xml.xquery.XQCommonHandler) 	 */
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
return|return
name|handler
operator|.
name|toObject
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#getShort() 	 */
specifier|public
name|short
name|getShort
parameter_list|()
throws|throws
name|XQException
block|{
return|return
literal|0
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#instanceOf(javax.xml.xquery.XQItemType) 	 */
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
name|String
name|prefix
init|=
name|type
operator|.
name|getTypeName
argument_list|()
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|String
name|local
init|=
name|type
operator|.
name|getTypeName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|getType
argument_list|(
operator|new
name|QName
argument_list|(
name|local
argument_list|,
literal|null
argument_list|,
name|prefix
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
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
return|return
literal|false
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#writeItem(java.io.OutputStream, 	 *      java.util.Properties) 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#writeItem(java.io.Writer, 	 *      java.util.Properties) 	 */
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.xml.xquery.XQItemAccessor#writeItemToSAX(org.xml.sax.ContentHandler) 	 */
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
specifier|private
name|Item
name|convertTo
parameter_list|(
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
try|try
block|{
return|return
name|item
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|getType
argument_list|(
operator|new
name|QName
argument_list|(
name|type
operator|.
name|getTypeName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|,
literal|null
argument_list|,
name|type
operator|.
name|getTypeName
argument_list|()
operator|.
name|getPrefix
argument_list|()
argument_list|)
argument_list|)
argument_list|)
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
literal|"Could not convert value for item to: "
operator|+
name|type
operator|.
name|getTypeName
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Item
name|convertTo
parameter_list|(
name|int
name|type
parameter_list|)
throws|throws
name|XQException
block|{
try|try
block|{
return|return
name|item
operator|.
name|convertTo
argument_list|(
name|type
argument_list|)
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
literal|"Could not convert value for item to: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
operator|+
literal|" "
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

