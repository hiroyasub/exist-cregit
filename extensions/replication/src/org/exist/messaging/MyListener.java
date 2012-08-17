begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|messaging
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
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
name|memtree
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|NodeImpl
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
name|FunctionReference
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|StringValue
import|;
end_import

begin_comment
comment|/**  * Handle incoming message by executing function with parameters  * (JMS config, Metadata, payload)  *   * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|MyListener
implements|implements
name|MessageListener
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
name|MyListener
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|XQueryContext
name|xqcontext
decl_stmt|;
specifier|private
name|FunctionReference
name|ref
init|=
literal|null
decl_stmt|;
specifier|public
name|MyListener
parameter_list|(
name|FunctionReference
name|ref
parameter_list|,
name|XQueryContext
name|xqcontext
parameter_list|)
block|{
name|this
operator|.
name|ref
operator|=
name|ref
expr_stmt|;
name|this
operator|.
name|xqcontext
operator|=
name|xqcontext
expr_stmt|;
name|this
operator|.
name|ref
operator|.
name|setContext
argument_list|(
name|this
operator|.
name|xqcontext
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
name|message
operator|.
name|getStringProperty
argument_list|(
literal|"name"
argument_list|)
operator|+
literal|" Id="
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
operator|+
literal|" type="
operator|+
name|message
operator|.
name|getJMSType
argument_list|()
argument_list|)
expr_stmt|;
name|NodeImpl
name|report
init|=
name|createReport
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|Item
name|content
init|=
literal|null
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"report created"
argument_list|)
expr_stmt|;
comment|// Get data from message
comment|// TODO switch based on supplied content-type e.g. element(),
comment|// document-node()etc
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"TextMessage"
argument_list|)
expr_stmt|;
name|String
name|txt
init|=
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|txt
argument_list|)
expr_stmt|;
name|content
operator|=
operator|new
name|StringValue
argument_list|(
name|txt
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|message
operator|instanceof
name|BytesMessage
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"BytesMessage"
argument_list|)
expr_stmt|;
name|BytesMessage
name|bm
init|=
operator|(
name|BytesMessage
operator|)
name|message
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"length="
operator|+
name|bm
operator|.
name|getBodyLength
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|bm
operator|.
name|getBodyLength
argument_list|()
index|]
decl_stmt|;
name|bm
operator|.
name|readBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|String
name|txt
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"to be converted '"
operator|+
name|txt
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|content
operator|=
operator|new
name|StringValue
argument_list|(
name|txt
argument_list|)
expr_stmt|;
block|}
comment|// Get Meta data from JMS
comment|// TODO wrap into node structure, flat, or element sequence.
name|Enumeration
name|names
init|=
name|message
operator|.
name|getPropertyNames
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
argument_list|<
name|?
argument_list|>
name|e
init|=
name|names
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|key
operator|+
literal|" == "
operator|+
name|message
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Call function
comment|// Construct parameters
name|Sequence
index|[]
name|params
init|=
operator|new
name|Sequence
index|[
literal|3
index|]
decl_stmt|;
name|params
index|[
literal|0
index|]
operator|=
operator|new
name|StringValue
argument_list|(
literal|".....0"
argument_list|)
expr_stmt|;
comment|// report; // report
name|params
index|[
literal|1
index|]
operator|=
operator|new
name|StringValue
argument_list|(
literal|".....1"
argument_list|)
expr_stmt|;
comment|//= report; // meta data
name|params
index|[
literal|2
index|]
operator|=
operator|new
name|StringValue
argument_list|(
literal|".....2"
argument_list|)
expr_stmt|;
comment|//= report; // content
comment|// Execute function
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"execute"
argument_list|)
expr_stmt|;
name|Sequence
name|ret
init|=
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
decl_stmt|;
comment|// Never reaches here, due to NPE.
name|LOG
operator|.
name|info
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// Catch all issues.
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Create messaging results report      *       * TODO shared code, except context (new copied)      */
specifier|private
name|NodeImpl
name|createReport
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|MemTreeBuilder
name|builder
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
comment|// start root element
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"JMS"
argument_list|,
literal|"JMS"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|txt
init|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
if|if
condition|(
name|txt
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"MessageID"
argument_list|,
literal|"MessageID"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|String
name|txt
init|=
name|message
operator|.
name|getJMSCorrelationID
argument_list|()
decl_stmt|;
if|if
condition|(
name|txt
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"CorrelationID"
argument_list|,
literal|"CorrelationID"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|message
operator|.
name|getJMSCorrelationID
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|String
name|txt
init|=
name|message
operator|.
name|getJMSType
argument_list|()
decl_stmt|;
if|if
condition|(
name|txt
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"Type"
argument_list|,
literal|"Type"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|message
operator|.
name|getJMSType
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
comment|// finish root element
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// return result
return|return
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
block|}
end_class

end_unit

