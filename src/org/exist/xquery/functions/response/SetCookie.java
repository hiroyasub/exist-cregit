begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2009 The eXist team  *  http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|response
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|ResponseWrapper
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
name|Dependency
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
name|Profiler
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
name|Variable
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
name|DurationValue
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
name|JavaObjectValue
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|Duration
import|;
end_import

begin_comment
comment|/**  * Set's a HTTP Cookie on the HTTP Response.  *  * @author  Adam Retter<adam.retter@devon.gov.uk>  * @author  JosÃ© MarÃ­a FernÃ¡ndez (jmfg@users.sourceforge.net)  * @see     org.exist.xquery.Function  */
end_comment

begin_class
specifier|public
class|class
name|SetCookie
extends|extends
name|Function
block|{
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
name|SetCookie
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|NAME_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The cookie name"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|VALUE_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The cookie value"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|MAX_AGE_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"max-age"
argument_list|,
name|Type
operator|.
name|DURATION
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The xs:duration of the cookie"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|SECURE_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"secure-flag"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The flag for whether the cookie is to be secure (i.e., only transferred using HTTPS)"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|DOMAIN_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"domain"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The cookie domain"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionParameterSequenceType
name|PATH_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The cookie path"
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
literal|"set-cookie"
argument_list|,
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ResponseModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Sets a HTTP Cookie on the HTTP Response."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAME_PARAM
block|,
name|VALUE_PARAM
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
literal|"set-cookie"
argument_list|,
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ResponseModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Sets a HTTP Cookie on the HTTP Response."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAME_PARAM
block|,
name|VALUE_PARAM
block|,
name|MAX_AGE_PARAM
block|,
name|SECURE_PARAM
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
literal|"set-cookie"
argument_list|,
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ResponseModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Sets a HTTP Cookie on the HTTP Response."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NAME_PARAM
block|,
name|VALUE_PARAM
block|,
name|MAX_AGE_PARAM
block|,
name|SECURE_PARAM
block|,
name|DOMAIN_PARAM
block|,
name|PATH_PARAM
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
name|SetCookie
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item)      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|ResponseModule
name|myModule
init|=
operator|(
name|ResponseModule
operator|)
name|context
operator|.
name|getModule
argument_list|(
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
comment|// response object is read from global variable $response
name|Variable
name|var
init|=
name|myModule
operator|.
name|resolveVariable
argument_list|(
name|ResponseModule
operator|.
name|RESPONSE_VAR
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|var
operator|==
literal|null
operator|)
operator|||
operator|(
name|var
operator|.
name|getValue
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Response not set"
argument_list|)
operator|)
throw|;
block|}
if|if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|JAVA_OBJECT
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Variable $response is not bound to a Java object."
argument_list|)
operator|)
throw|;
block|}
name|JavaObjectValue
name|response
init|=
operator|(
name|JavaObjectValue
operator|)
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//get parameters
name|String
name|name
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Sequence
name|ageSeq
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|Sequence
name|secureSeq
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|Sequence
name|domainSeq
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|Sequence
name|pathSeq
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|int
name|maxAge
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|2
condition|)
block|{
name|ageSeq
operator|=
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
name|secureSeq
operator|=
name|getArgument
argument_list|(
literal|3
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ageSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Duration
name|duration
init|=
operator|(
operator|(
name|DurationValue
operator|)
name|ageSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getCanonicalDuration
argument_list|()
decl_stmt|;
name|maxAge
operator|=
operator|(
name|int
operator|)
operator|(
name|duration
operator|.
name|getTimeInMillis
argument_list|(
operator|new
name|Date
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
operator|/
literal|1000L
operator|)
expr_stmt|;
block|}
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|4
condition|)
block|{
name|domainSeq
operator|=
name|getArgument
argument_list|(
literal|4
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
name|pathSeq
operator|=
name|getArgument
argument_list|(
literal|5
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
block|}
comment|//set response header
if|if
condition|(
name|response
operator|.
name|getObject
argument_list|()
operator|instanceof
name|ResponseWrapper
condition|)
block|{
switch|switch
condition|(
name|getArgumentCount
argument_list|()
condition|)
block|{
case|case
literal|2
case|:
block|{
operator|(
operator|(
name|ResponseWrapper
operator|)
name|response
operator|.
name|getObject
argument_list|()
operator|)
operator|.
name|addCookie
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
literal|4
case|:
block|{
if|if
condition|(
name|secureSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
operator|(
operator|(
name|ResponseWrapper
operator|)
name|response
operator|.
name|getObject
argument_list|()
operator|)
operator|.
name|addCookie
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|maxAge
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|ResponseWrapper
operator|)
name|response
operator|.
name|getObject
argument_list|()
operator|)
operator|.
name|addCookie
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|maxAge
argument_list|,
operator|(
operator|(
name|BooleanValue
operator|)
name|secureSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|effectiveBooleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
case|case
literal|6
case|:
block|{
name|boolean
name|secure
init|=
literal|false
decl_stmt|;
name|String
name|domain
init|=
literal|null
decl_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|secureSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|secure
operator|=
operator|(
operator|(
name|BooleanValue
operator|)
name|secureSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|domainSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|domain
operator|=
name|domainSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|pathSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|path
operator|=
name|pathSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
operator|(
operator|(
name|ResponseWrapper
operator|)
name|response
operator|.
name|getObject
argument_list|()
operator|)
operator|.
name|addCookie
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|maxAge
argument_list|,
name|secure
argument_list|,
name|domain
argument_list|,
name|path
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Type error: variable $response is not bound to a response object"
argument_list|)
operator|)
throw|;
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

