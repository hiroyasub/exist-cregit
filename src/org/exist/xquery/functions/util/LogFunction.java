begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2009 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|util
package|;
end_package

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
name|dom
operator|.
name|persistent
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
name|storage
operator|.
name|serializers
operator|.
name|Serializer
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|FunctionParameterSequenceType
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
name|SequenceIterator
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
name|SequenceType
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author Andrzej Taramina (andrzej@chaeron.com)  */
end_comment

begin_class
specifier|public
class|class
name|LogFunction
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|PRIORITY_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"priority"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The logging priority: 'error', 'warn', 'debug', 'info', 'trace'"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|LOGGER_NAME_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"logger-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the logger, eg: my.app.log"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|MESSAGE_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"message"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The message to log"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|LogFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"log"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Logs the message to the current logger."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PRIORITY_PARAMETER
block|,
name|MESSAGE_PARAMETER
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"log-system-out"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Logs the message to System.out."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|MESSAGE_PARAMETER
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"log-system-err"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Logs the message to System.err."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|MESSAGE_PARAMETER
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"log-app"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Logs the message to the named logger"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PRIORITY_PARAMETER
block|,
name|LOGGER_NAME_PARAMETER
block|,
name|MESSAGE_PARAMETER
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|LogFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|SequenceIterator
name|i
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"log"
argument_list|)
condition|)
block|{
name|i
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|unorderedIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"log-app"
argument_list|)
condition|)
block|{
name|i
operator|=
name|args
index|[
literal|2
index|]
operator|.
name|unorderedIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
else|else
block|{
name|i
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|unorderedIterator
argument_list|()
expr_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
comment|// add line of the log statement
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"Line: "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|this
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
comment|//add the source to the log statement
if|if
condition|(
name|getSource
argument_list|()
operator|!=
literal|null
operator|&&
name|getSource
argument_list|()
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getSource
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
expr_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
specifier|final
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
block|{
name|buf
operator|.
name|append
argument_list|(
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|next
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An exception occurred while serializing node to log: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
operator|)
throw|;
block|}
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"log"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|priority
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"error"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"warn"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"info"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"trace"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"log-system-out"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"log-system-err"
argument_list|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"log-app"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|priority
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|logname
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Logger
name|logger
init|=
name|LOG
decl_stmt|;
if|if
condition|(
name|logname
operator|!=
literal|null
operator|&&
name|logname
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|logger
operator|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|logname
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"error"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"warn"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"info"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|priority
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"trace"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|trace
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
end_class

end_unit

