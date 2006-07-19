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
name|util
operator|.
name|TimeZone
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
name|XQResultSequence
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
name|XQWarning
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
name|xacml
operator|.
name|AccessContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
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
name|storage
operator|.
name|XQueryPool
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
name|CompiledXQuery
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
name|XQuery
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
name|XQueryContext
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
name|Sequence
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
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  *  */
end_comment

begin_class
specifier|public
class|class
name|XQExpression
implements|implements
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQExpression
block|{
specifier|private
name|DBBroker
name|broker
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|XQExpression
parameter_list|()
block|{
name|broker
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|XQExpression
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#cancel() 	 */
specifier|public
name|void
name|cancel
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#clearWarnings() 	 */
specifier|public
name|void
name|clearWarnings
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#close() 	 */
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#executeCommand(java.io.Reader) 	 */
specifier|public
name|void
name|executeCommand
parameter_list|(
name|Reader
name|command
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#executeCommand(java.lang.String) 	 */
specifier|public
name|void
name|executeCommand
parameter_list|(
name|String
name|command
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#executeQuery(java.io.InputStream) 	 */
specifier|public
name|XQResultSequence
name|executeQuery
parameter_list|(
name|InputStream
name|query
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#executeQuery(java.io.Reader) 	 */
specifier|public
name|XQResultSequence
name|executeQuery
parameter_list|(
name|Reader
name|query
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#executeQuery(java.lang.String) 	 */
specifier|public
name|XQResultSequence
name|executeQuery
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XQException
block|{
comment|//prepare the source of the query
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|query
argument_list|)
decl_stmt|;
comment|//get an xquery and the xquery bool
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryPool
name|xqPool
init|=
name|xquery
operator|.
name|getXQueryPool
argument_list|()
decl_stmt|;
comment|//try and get a pre-compiled query from the pool
name|CompiledXQuery
name|compiled
init|=
name|xqPool
operator|.
name|borrowCompiledXQuery
argument_list|(
name|broker
argument_list|,
name|source
argument_list|)
decl_stmt|;
comment|//setup the context
name|XQueryContext
name|context
decl_stmt|;
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
name|context
operator|=
name|xquery
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|XQJ
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|=
name|compiled
operator|.
name|getContext
argument_list|()
expr_stmt|;
block|}
comment|//context.setStaticallyKnownDocuments(new XmldbURI[] { pathUri });
try|try
block|{
comment|//if there was no pre-compiled query then compile it
if|if
condition|(
name|compiled
operator|==
literal|null
condition|)
block|{
name|compiled
operator|=
name|xquery
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|)
expr_stmt|;
block|}
comment|//execute the query
name|Sequence
name|resultSequence
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|//return the result sequence
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|xqj
operator|.
name|XQResultSequence
argument_list|(
name|resultSequence
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
block|}
finally|finally
block|{
comment|//store the compiled query in the pool for re-use later
name|xqPool
operator|.
name|returnCompiledXQuery
argument_list|(
name|source
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#getQueryLanguageTypeAndVersion() 	 */
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#getQueryTimeout() 	 */
specifier|public
name|int
name|getQueryTimeout
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#getWarnings() 	 */
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#isClosed() 	 */
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQExpression#setQueryTimeout(int) 	 */
specifier|public
name|void
name|setQueryTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindAtomicValue(javax.xml.namespace.QName, java.lang.String, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindAtomicValue
parameter_list|(
name|QName
name|varname
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindBoolean(javax.xml.namespace.QName, boolean, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindBoolean
parameter_list|(
name|QName
name|varname
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindByte(javax.xml.namespace.QName, byte, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindByte
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindContextItem(javax.xml.xquery.XQItem) 	 */
specifier|public
name|void
name|bindContextItem
parameter_list|(
name|XQItem
name|contextitem
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindDocument(javax.xml.namespace.QName, org.xml.sax.InputSource) 	 */
specifier|public
name|void
name|bindDocument
parameter_list|(
name|QName
name|varname
parameter_list|,
name|InputSource
name|source
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindDouble(javax.xml.namespace.QName, double, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindDouble
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindFloat(javax.xml.namespace.QName, float, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindFloat
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindInt(javax.xml.namespace.QName, int, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindInt
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindItem(javax.xml.namespace.QName, javax.xml.xquery.XQItem) 	 */
specifier|public
name|void
name|bindItem
parameter_list|(
name|QName
name|varName
parameter_list|,
name|XQItem
name|value
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindLong(javax.xml.namespace.QName, long, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindLong
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindNode(javax.xml.namespace.QName, org.w3c.dom.Node, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindNode
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindObject(javax.xml.namespace.QName, java.lang.Object, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindObject
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindSequence(javax.xml.namespace.QName, javax.xml.xquery.XQSequence) 	 */
specifier|public
name|void
name|bindSequence
parameter_list|(
name|QName
name|varName
parameter_list|,
name|XQSequence
name|value
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#bindShort(javax.xml.namespace.QName, short, javax.xml.xquery.XQItemType) 	 */
specifier|public
name|void
name|bindShort
parameter_list|(
name|QName
name|varName
parameter_list|,
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
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#getImplicitTimeZone() 	 */
specifier|public
name|TimeZone
name|getImplicitTimeZone
parameter_list|()
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQDynamicContext#setImplicitTimeZone(java.util.TimeZone) 	 */
specifier|public
name|void
name|setImplicitTimeZone
parameter_list|(
name|TimeZone
name|implicitTimeZone
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

