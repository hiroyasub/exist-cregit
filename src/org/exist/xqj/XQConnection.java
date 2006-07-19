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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Iterator
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
name|XQExpression
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
name|XQMetaData
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
name|XQPreparedExpression
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
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQWarning
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

begin_comment
comment|/**  * @author adam  *  */
end_comment

begin_class
specifier|public
class|class
name|XQConnection
implements|implements
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQConnection
block|{
comment|/** 	 *  	 */
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|public
name|XQConnection
parameter_list|()
block|{
name|broker
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|XQConnection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#clearWarnings() 	 */
specifier|public
name|void
name|clearWarnings
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#close() 	 */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|XQException
block|{
try|try
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|broker
operator|=
literal|null
expr_stmt|;
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
literal|"Unable to return broker to pool"
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#commit() 	 */
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#createExpression() 	 */
specifier|public
name|XQExpression
name|createExpression
parameter_list|()
throws|throws
name|XQException
block|{
name|XQExpression
name|expr
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|xqj
operator|.
name|XQExpression
argument_list|(
name|broker
argument_list|)
decl_stmt|;
return|return
name|expr
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getHoldability() 	 */
specifier|public
name|int
name|getHoldability
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getMetaData() 	 */
specifier|public
name|XQMetaData
name|getMetaData
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getMetaDataProperty(java.lang.String) 	 */
specifier|public
name|String
name|getMetaDataProperty
parameter_list|(
name|String
name|key
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getQueryLanguageTypeAndVersion() 	 */
specifier|public
name|int
name|getQueryLanguageTypeAndVersion
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getScrollability() 	 */
specifier|public
name|int
name|getScrollability
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getSupportedMetaDataPropertyNames() 	 */
specifier|public
name|String
index|[]
name|getSupportedMetaDataPropertyNames
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getUpdatability() 	 */
specifier|public
name|int
name|getUpdatability
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#getWarnings() 	 */
specifier|public
name|XQWarning
name|getWarnings
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#isClosed() 	 */
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
operator|(
name|broker
operator|==
literal|null
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#prepareExpression(java.io.InputStream) 	 */
specifier|public
name|XQPreparedExpression
name|prepareExpression
parameter_list|(
name|InputStream
name|xquery
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#prepareExpression(java.io.InputStream, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQPreparedExpression
name|prepareExpression
parameter_list|(
name|InputStream
name|xquery
parameter_list|,
name|XQItemType
name|contextItemType
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#prepareExpression(java.io.Reader) 	 */
specifier|public
name|XQPreparedExpression
name|prepareExpression
parameter_list|(
name|Reader
name|xquery
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#prepareExpression(java.io.Reader, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQPreparedExpression
name|prepareExpression
parameter_list|(
name|Reader
name|xquery
parameter_list|,
name|XQItemType
name|contextItemType
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#prepareExpression(java.lang.String) 	 */
specifier|public
name|XQPreparedExpression
name|prepareExpression
parameter_list|(
name|String
name|xquery
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#prepareExpression(java.lang.String, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|XQPreparedExpression
name|prepareExpression
parameter_list|(
name|String
name|xquery
parameter_list|,
name|XQItemType
name|contextItemType
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#rollback() 	 */
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#setCommonHandler(javax.xml.xquery.XQCommonHandler) 	 */
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#setHoldability(int) 	 */
specifier|public
name|void
name|setHoldability
parameter_list|(
name|int
name|holdability
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#setQueryLanguageTypeAndVersion(int) 	 */
specifier|public
name|void
name|setQueryLanguageTypeAndVersion
parameter_list|(
name|int
name|langtype
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#setScrollability(int) 	 */
specifier|public
name|void
name|setScrollability
parameter_list|(
name|int
name|scrollability
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQConnection#setUpdatability(int) 	 */
specifier|public
name|void
name|setUpdatability
parameter_list|(
name|int
name|updatability
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getBaseURI() 	 */
specifier|public
name|String
name|getBaseURI
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getBoundarySpacePolicy() 	 */
specifier|public
name|int
name|getBoundarySpacePolicy
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getConstructionMode() 	 */
specifier|public
name|int
name|getConstructionMode
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getCopyNamespacesModeInherit() 	 */
specifier|public
name|int
name|getCopyNamespacesModeInherit
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getCopyNamespacesModePreserve() 	 */
specifier|public
name|int
name|getCopyNamespacesModePreserve
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getDefaultCollation() 	 */
specifier|public
name|String
name|getDefaultCollation
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getDefaultElementTypeNamespace() 	 */
specifier|public
name|String
name|getDefaultElementTypeNamespace
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getDefaultFunctionNamespace() 	 */
specifier|public
name|String
name|getDefaultFunctionNamespace
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getDefaultOrderForEmptySequences() 	 */
specifier|public
name|int
name|getDefaultOrderForEmptySequences
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getInScopeNamespacePrefixes() 	 */
specifier|public
name|String
index|[]
name|getInScopeNamespacePrefixes
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getNamespaceURI(java.lang.String) 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getOrderingMode() 	 */
specifier|public
name|int
name|getOrderingMode
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getStaticInScopeVariableNames() 	 */
specifier|public
name|QName
index|[]
name|getStaticInScopeVariableNames
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQStaticContext#getStaticInScopeVariableType(javax.xml.namespace.QName) 	 */
specifier|public
name|XQSequenceType
name|getStaticInScopeVariableType
parameter_list|(
name|QName
name|varname
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

