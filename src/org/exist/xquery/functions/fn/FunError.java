begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|fn
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|ErrorCodes
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
name|ErrorCodes
operator|.
name|ErrorCode
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
name|Function
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
name|QNameValue
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

begin_class
specifier|public
class|class
name|FunError
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FunError
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"error"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Indicates that an irrecoverable error has occurred. "
operator|+
literal|"The script will terminate immediately with an exception using "
operator|+
literal|"the default qname, 'http://www.w3.org/2004/07/xqt-errors#err:FOER0000', "
operator|+
literal|"and the default error message, 'An error has been raised by the query'."
argument_list|,
literal|null
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"error"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Indicates that an irrecoverable error has occurred. "
operator|+
literal|"The script will terminate immediately with an exception using "
operator|+
literal|"$qname and the default message, 'An error has been raised by the query'."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"qname"
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The qname"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"error"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Indicates that an irrecoverable error has occurred. "
operator|+
literal|"The script will terminate immediately with an exception using "
operator|+
literal|"$qname and $message."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"qname"
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The qname"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"message"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The message"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"error"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Indicates that an irrecoverable error has occurred. "
operator|+
literal|"The script will terminate immediately with an exception using "
operator|+
literal|"$qname and $message with $error-object appended."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"qname"
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The qname"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"message"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The message"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"error-object"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The error object"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
block|,     }
decl_stmt|;
specifier|public
name|FunError
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
specifier|public
specifier|final
specifier|static
name|ErrorCode
name|DEFAULT_ERROR
init|=
name|ErrorCodes
operator|.
name|FOER0000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_DESCRIPTION
init|=
literal|"An error has been raised by the query"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|EMPTY
return|;
block|}
annotation|@
name|Override
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
comment|// Define default values
name|ErrorCode
name|errorCode
init|=
name|DEFAULT_ERROR
decl_stmt|;
name|String
name|errorDesc
init|=
name|DEFAULT_DESCRIPTION
decl_stmt|;
name|Sequence
name|errorVal
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
comment|// Enter if one or more parameters are supplied
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
comment|// If there are 2 arguments or more supplied
comment|// use 2nd argument for error description
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|errorDesc
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
comment|// If first argument is not empty, get qname from argument
comment|// and construct error code
if|if
condition|(
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|QName
name|errorQName
init|=
operator|(
operator|(
name|QNameValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getQName
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|errorQName
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|ns
init|=
name|errorQName
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|prefix
operator|=
name|getContext
argument_list|()
operator|.
name|getPrefixForURI
argument_list|(
name|ns
argument_list|)
expr_stmt|;
name|errorQName
operator|=
operator|new
name|QName
argument_list|(
name|errorQName
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|errorQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
name|errorCode
operator|=
operator|new
name|ErrorCode
argument_list|(
name|errorQName
argument_list|,
name|errorDesc
argument_list|)
expr_stmt|;
block|}
comment|// If there is a third argument, use it.
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|errorVal
operator|=
name|args
index|[
literal|2
index|]
expr_stmt|;
block|}
block|}
name|logger
operator|.
name|error
argument_list|(
name|errorDesc
operator|+
literal|": "
operator|+
name|errorCode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|errorCode
argument_list|,
name|errorDesc
argument_list|,
name|errorVal
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

