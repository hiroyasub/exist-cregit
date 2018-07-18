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
name|*
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
name|annotation
operator|.
name|Nonnull
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
comment|/**  * Set's a HTTP Cookie on the HTTP Response.  *  * @author Adam Retter<adam.retter@devon.gov.uk>  * @author JosÃ© MarÃ­a FernÃ¡ndez (jmfg@users.sourceforge.net)  * @see org.exist.xquery.Function  */
end_comment

begin_class
specifier|public
class|class
name|SetCookie
extends|extends
name|StrictResponseFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SetCookie
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
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
specifier|private
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
specifier|private
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
name|ZERO_OR_ONE
argument_list|,
literal|"The xs:duration of the cookie"
argument_list|)
decl_stmt|;
specifier|private
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
specifier|private
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
specifier|private
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
name|EMPTY
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
name|EMPTY
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
name|EMPTY
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
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
annotation|@
name|Nonnull
specifier|final
name|ResponseWrapper
name|response
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//get parameters
specifier|final
name|String
name|name
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
name|value
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|int
name|maxAge
decl_stmt|;
specifier|final
name|Sequence
name|secureSeq
decl_stmt|,
name|domainSeq
decl_stmt|,
name|pathSeq
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|2
condition|)
block|{
specifier|final
name|Sequence
name|ageSeq
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
name|secureSeq
operator|=
name|args
index|[
literal|3
index|]
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
specifier|final
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
else|else
block|{
name|maxAge
operator|=
operator|-
literal|1
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
name|args
index|[
literal|4
index|]
expr_stmt|;
name|pathSeq
operator|=
name|args
index|[
literal|5
index|]
expr_stmt|;
block|}
else|else
block|{
name|domainSeq
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
name|pathSeq
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
else|else
block|{
name|secureSeq
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
name|domainSeq
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
name|pathSeq
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
name|maxAge
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|//set response header
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
name|response
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
name|response
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
name|response
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
name|response
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
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

