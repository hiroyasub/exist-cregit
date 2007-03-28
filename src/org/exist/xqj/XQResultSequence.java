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
name|XQWarning
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
name|ContentHandler
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@devon.gov.uk>  *  */
end_comment

begin_class
specifier|public
class|class
name|XQResultSequence
implements|implements
name|javax
operator|.
name|xml
operator|.
name|xquery
operator|.
name|XQResultSequence
block|{
specifier|private
name|Sequence
name|resultSequence
decl_stmt|;
comment|//NB First Item is 1, Java Arrays start at 0!!! (before first is 0)
specifier|private
name|int
name|iLength
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|iCurrent
init|=
literal|0
decl_stmt|;
specifier|public
name|XQResultSequence
parameter_list|()
block|{
name|resultSequence
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|XQResultSequence
parameter_list|(
name|Sequence
name|resultSequence
parameter_list|)
block|{
name|this
operator|.
name|resultSequence
operator|=
name|resultSequence
expr_stmt|;
name|iLength
operator|=
name|resultSequence
operator|.
name|getItemCount
argument_list|()
expr_stmt|;
comment|//do this once here as getLength() is expensive
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQResultSequence#clearWarnings() 	 */
specifier|public
name|void
name|clearWarnings
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQResultSequence#getConnection() 	 */
specifier|public
name|XQConnection
name|getConnection
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQResultSequence#getWarnings() 	 */
specifier|public
name|XQWarning
name|getWarnings
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
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
name|Item
name|item
init|=
name|resultSequence
operator|.
name|itemAt
argument_list|(
name|iCurrent
argument_list|)
decl_stmt|;
return|return
name|item
operator|.
name|atomize
argument_list|()
operator|.
name|toString
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#absolute(int) 	 */
specifier|public
name|boolean
name|absolute
parameter_list|(
name|int
name|itempos
parameter_list|)
throws|throws
name|XQException
block|{
if|if
condition|(
name|itempos
operator|>
literal|0
operator|&&
name|itempos
operator|<=
name|iLength
condition|)
block|{
name|iCurrent
operator|=
name|itempos
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#afterLast() 	 */
specifier|public
name|void
name|afterLast
parameter_list|()
throws|throws
name|XQException
block|{
if|if
condition|(
name|resultSequence
operator|!=
literal|null
condition|)
name|iCurrent
operator|=
name|iLength
operator|+
literal|1
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#beforeFirst() 	 */
specifier|public
name|void
name|beforeFirst
parameter_list|()
throws|throws
name|XQException
block|{
if|if
condition|(
name|resultSequence
operator|!=
literal|null
condition|)
name|iCurrent
operator|=
literal|0
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#close() 	 */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|XQException
block|{
name|iLength
operator|=
literal|0
expr_stmt|;
name|iCurrent
operator|=
literal|0
expr_stmt|;
name|resultSequence
operator|=
literal|null
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#count() 	 */
specifier|public
name|int
name|count
parameter_list|()
throws|throws
name|XQException
block|{
return|return
name|iLength
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#first() 	 */
specifier|public
name|boolean
name|first
parameter_list|()
throws|throws
name|XQException
block|{
name|iCurrent
operator|=
literal|1
expr_stmt|;
return|return
operator|(
name|resultSequence
operator|!=
literal|null
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#getItem() 	 */
specifier|public
name|XQItem
name|getItem
parameter_list|()
throws|throws
name|XQException
block|{
name|Item
name|item
init|=
name|resultSequence
operator|.
name|itemAt
argument_list|(
name|iCurrent
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|org
operator|.
name|exist
operator|.
name|xqj
operator|.
name|XQItem
argument_list|(
name|item
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#getPosition() 	 */
specifier|public
name|int
name|getPosition
parameter_list|()
throws|throws
name|XQException
block|{
return|return
name|iCurrent
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#getSequenceAsString(java.util.Properties) 	 */
specifier|public
name|String
name|getSequenceAsString
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#isAfterLast() 	 */
specifier|public
name|boolean
name|isAfterLast
parameter_list|()
throws|throws
name|XQException
block|{
return|return
operator|(
name|iCurrent
operator|>
name|iLength
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#isBeforeFirst() 	 */
specifier|public
name|boolean
name|isBeforeFirst
parameter_list|()
throws|throws
name|XQException
block|{
return|return
operator|(
name|iCurrent
operator|==
literal|0
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#isClosed() 	 */
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
operator|(
name|resultSequence
operator|==
literal|null
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#isFirst() 	 */
specifier|public
name|boolean
name|isFirst
parameter_list|()
throws|throws
name|XQException
block|{
return|return
operator|(
name|iCurrent
operator|==
literal|1
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#isLast() 	 */
specifier|public
name|boolean
name|isLast
parameter_list|()
throws|throws
name|XQException
block|{
return|return
operator|(
name|iCurrent
operator|==
name|iLength
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#isOnItem() 	 */
specifier|public
name|boolean
name|isOnItem
parameter_list|()
throws|throws
name|XQException
block|{
return|return
operator|(
name|iCurrent
operator|>
literal|0
operator|&&
name|iCurrent
operator|<=
name|iLength
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#isScrollable() 	 */
specifier|public
name|boolean
name|isScrollable
parameter_list|()
throws|throws
name|XQException
block|{
return|return
operator|(
name|resultSequence
operator|!=
literal|null
operator|&&
name|iLength
operator|>
literal|0
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#last() 	 */
specifier|public
name|boolean
name|last
parameter_list|()
throws|throws
name|XQException
block|{
name|iCurrent
operator|=
name|iLength
expr_stmt|;
return|return
operator|(
name|resultSequence
operator|!=
literal|null
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#next() 	 */
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|XQException
block|{
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|iCurrent
operator|++
expr_stmt|;
return|return
operator|(
name|iCurrent
operator|<=
name|iLength
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#previous() 	 */
specifier|public
name|boolean
name|previous
parameter_list|()
throws|throws
name|XQException
block|{
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|iCurrent
operator|>
literal|0
condition|)
comment|//dont ever go lower than 0 (0 is before first)
name|iCurrent
operator|--
expr_stmt|;
return|return
operator|(
name|iCurrent
operator|>
literal|0
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#relative(int) 	 */
specifier|public
name|boolean
name|relative
parameter_list|(
name|int
name|itempos
parameter_list|)
throws|throws
name|XQException
block|{
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|//positive number
if|if
condition|(
name|itempos
operator|>
literal|0
condition|)
block|{
comment|//call next() itempos number of times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|itempos
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|next
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|//negative number
if|else if
condition|(
name|itempos
operator|<
literal|0
condition|)
block|{
comment|//call previous() itempos number of times
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|itempos
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|previous
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#writeSequence(java.io.OutputStream, java.util.Properties) 	 */
specifier|public
name|void
name|writeSequence
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#writeSequence(java.io.Writer, java.util.Properties) 	 */
specifier|public
name|void
name|writeSequence
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
comment|/* (non-Javadoc) 	 * @see javax.xml.xquery.XQSequence#writeSequenceToSAX(org.xml.sax.ContentHandler) 	 */
specifier|public
name|void
name|writeSequenceToSAX
parameter_list|(
name|ContentHandler
name|saxhdlr
parameter_list|)
throws|throws
name|XQException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

