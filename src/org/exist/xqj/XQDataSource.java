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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|sql
operator|.
name|Connection
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
name|namespace
operator|.
name|QName
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
name|XQConnection
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
name|XQItem
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
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQSequence
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
name|XQSequenceType
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
name|storage
operator|.
name|DBBroker
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
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ErrorCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  *  */
end_comment

begin_class
specifier|public
class|class
name|XQDataSource
implements|implements
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQDataSource
block|{
specifier|private
specifier|final
specifier|static
name|String
index|[]
name|strPropertyNames
init|=
block|{
literal|"javax.xml.xquery.property.UserName"
block|,
literal|"javax.xml.xquery.property.Password"
block|,
literal|"javax.xml.xquery.property.MaxConnections"
block|}
decl_stmt|;
specifier|private
name|Properties
name|properties
decl_stmt|;
specifier|private
name|int
name|iLoginTimeout
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|PrintWriter
name|pwLogWriter
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|XQDataSource
parameter_list|()
block|{
comment|//setup initial property values
name|Properties
name|propsDefaults
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|propsDefaults
operator|.
name|setProperty
argument_list|(
name|strPropertyNames
index|[
literal|0
index|]
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
comment|//javax.xml.xquery.property.UserName
name|propsDefaults
operator|.
name|setProperty
argument_list|(
name|strPropertyNames
index|[
literal|0
index|]
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
comment|//javax.xml.xquery.property.Password
name|propsDefaults
operator|.
name|setProperty
argument_list|(
name|strPropertyNames
index|[
literal|0
index|]
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
comment|//javax.xml.xquery.property.MaxConnections
name|properties
operator|=
operator|new
name|Properties
argument_list|(
name|propsDefaults
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#getConnection() 	 */
specifier|public
name|XQConnection
name|getConnection
parameter_list|()
throws|throws
name|XQException
block|{
return|return
name|getConnection
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"javax.xml.xquery.property.UserName"
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"javax.xml.xquery.property.Password"
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#getConnection(java.sql.Connection) 	 */
specifier|public
name|XQConnection
name|getConnection
parameter_list|(
name|Connection
name|con
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#getConnection(java.lang.String, java.lang.String) 	 */
specifier|public
name|XQConnection
name|getConnection
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|XQException
block|{
name|BrokerPool
name|pool
decl_stmt|;
try|try
block|{
comment|//get the broker pool instance
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
comment|//get the user
name|User
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XQException
argument_list|(
literal|"User '"
operator|+
name|username
operator|+
literal|"' does not exist"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|user
operator|.
name|validate
argument_list|(
name|password
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XQException
argument_list|(
literal|"Invalid password for user '"
operator|+
name|username
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|//get a broker for the user
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
comment|//return the connection object
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|xqj
operator|.
name|XQConnection
argument_list|(
name|broker
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
throw|throw
operator|new
name|XQException
argument_list|(
literal|"Can not access local database instance: "
operator|+
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#getLoginTimeout() 	 */
specifier|public
name|int
name|getLoginTimeout
parameter_list|()
block|{
return|return
name|iLoginTimeout
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#getLogWriter() 	 */
specifier|public
name|PrintWriter
name|getLogWriter
parameter_list|()
block|{
return|return
name|pwLogWriter
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#getProperty(java.lang.String) 	 */
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|XQException
block|{
comment|//check for a valid property name
if|if
condition|(
name|validPropertyName
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|properties
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
throw|throw
operator|new
name|XQException
argument_list|(
literal|"Invalid Property Name"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#getSupportedPropertyNames() 	 */
specifier|public
name|String
index|[]
name|getSupportedPropertyNames
parameter_list|()
block|{
return|return
name|strPropertyNames
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#setCommonHandler(javax.xml.xquery.XQCommonHandler) 	 */
specifier|public
name|void
name|setCommonHandler
parameter_list|(
name|XQCommonHandler
name|handler
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#setLoginTimeout(int) 	 */
specifier|public
name|void
name|setLoginTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|XQException
block|{
name|iLoginTimeout
operator|=
name|seconds
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#setLogWriter(java.io.PrintWriter) 	 */
specifier|public
name|void
name|setLogWriter
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|XQException
block|{
name|pwLogWriter
operator|=
name|out
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#setProperties(java.util.Properties) 	 */
specifier|public
name|void
name|setProperties
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|XQException
block|{
comment|//copy the valid properties accross
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|strPropertyNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|strPropertyNames
index|[
name|i
index|]
argument_list|,
name|props
operator|.
name|getProperty
argument_list|(
name|strPropertyNames
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataSource#setProperty(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XQException
block|{
comment|//check for a valid property name
if|if
condition|(
name|validPropertyName
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|//set the property
name|properties
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XQException
argument_list|(
literal|"Invalid Property Name"
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createAtomicItemType(int) 	 */
specifier|public
name|XQItemType
name|createAtomicItemType
parameter_list|(
name|int
name|baseType
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItem(javax.xml.xquery.XQItem) 	 */
specifier|public
name|XQItem
name|createItem
parameter_list|(
name|XQItem
name|item
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromAtomicValue(java.lang.String, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromAtomicValue
parameter_list|(
name|String
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromBoolean(boolean, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromBoolean
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromByte(byte, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromByte
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromDocument(org.xml.sax.InputSource) 	 */
specifier|public
name|XQItem
name|createItemFromDocument
parameter_list|(
name|InputSource
name|value
parameter_list|)
throws|throws
name|XQException
throws|,
name|IOException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromDouble(double, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromDouble
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromFloat(float, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromFloat
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromInt(int, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromInt
parameter_list|(
name|int
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromLong(long, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromLong
parameter_list|(
name|long
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromNode(org.w3c.dom.Node, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromNode
parameter_list|(
name|Node
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromObject(java.lang.Object, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromObject
parameter_list|(
name|Object
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemFromShort(short, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQItem
name|createItemFromShort
parameter_list|(
name|short
name|value
parameter_list|,
name|XQItemType
name|type
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemType(int, int, javax.xml.namespace.QName) 	 */
specifier|public
name|XQItemType
name|createItemType
parameter_list|(
name|int
name|itemkind
parameter_list|,
name|int
name|basetype
parameter_list|,
name|QName
name|nodename
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createItemType(int, int, javax.xml.namespace.QName, javax.xml.namespace.QName, java.net.URI, boolean) 	 */
specifier|public
name|XQItemType
name|createItemType
parameter_list|(
name|int
name|itemkind
parameter_list|,
name|int
name|basetype
parameter_list|,
name|QName
name|nodename
parameter_list|,
name|QName
name|typename
parameter_list|,
name|URI
name|schemaURI
parameter_list|,
name|boolean
name|nillable
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createSequence(java.util.Iterator) 	 */
specifier|public
name|XQSequence
name|createSequence
parameter_list|(
name|Iterator
name|i
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createSequence(javax.xml.xquery.XQSequence) 	 */
specifier|public
name|XQSequence
name|createSequence
parameter_list|(
name|XQSequence
name|s
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDataFactory#createSequenceType(javax.xml.xquery.XQItemType, int) 	 */
specifier|public
name|XQSequenceType
name|createSequenceType
parameter_list|(
name|XQItemType
name|item
parameter_list|,
name|int
name|occurrence
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/** 	 * Determines if the property name is valid in strPropertyNames 	 *  	 * @param name	The property name 	 * @return True or False indicating the validitity of the supplied name  	 */
specifier|private
name|boolean
name|validPropertyName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|//iterate through strPropertyNames
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|strPropertyNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|strPropertyNames
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|//found the property name
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

