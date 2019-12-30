begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|ParsedArguments
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * Utility functions for working with Jargo  */
end_comment

begin_class
specifier|public
class|class
name|ArgumentUtil
block|{
comment|/**      * Get the value of an optional argument.      *      * @param<T> the type of the argument.      *      * @param parsedArguments The arguments which have been parsed      * @param argument The argument that we are looking for      *      * @return Some value or {@link Optional#empty()} if the      *     argument was not supplied      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Optional
argument_list|<
name|T
argument_list|>
name|getOpt
parameter_list|(
specifier|final
name|ParsedArguments
name|parsedArguments
parameter_list|,
specifier|final
name|Argument
argument_list|<
name|T
argument_list|>
name|argument
parameter_list|)
block|{
if|if
condition|(
name|parsedArguments
operator|.
name|wasGiven
argument_list|(
name|argument
argument_list|)
condition|)
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|parsedArguments
operator|.
name|get
argument_list|(
name|argument
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
comment|/**      * Get the values of an optional argument.      *      * @param<T> the type of the argument.      *      * @param parsedArguments The arguments which have been parsed      * @param argument The argument that we are looking for      *      * @return A list of the provided argument values, or      *     an empty list if the argument was not supplied      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|List
argument_list|<
name|T
argument_list|>
name|getListOpt
parameter_list|(
specifier|final
name|ParsedArguments
name|parsedArguments
parameter_list|,
specifier|final
name|Argument
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|argument
parameter_list|)
block|{
return|return
name|getOpt
argument_list|(
name|parsedArguments
argument_list|,
name|argument
argument_list|)
operator|.
name|orElseGet
argument_list|(
name|Collections
operator|::
name|emptyList
argument_list|)
return|;
block|}
comment|/**      * Get the value of an optional file argument      *      * @param parsedArguments The arguments which have been parsed      * @param argument The argument that we are looking for      *      * @return Some {@link java.nio.file.Path} or      *     {@link Optional#empty()} if the argument was not supplied      */
specifier|public
specifier|static
name|Optional
argument_list|<
name|Path
argument_list|>
name|getPathOpt
parameter_list|(
specifier|final
name|ParsedArguments
name|parsedArguments
parameter_list|,
specifier|final
name|Argument
argument_list|<
name|File
argument_list|>
name|argument
parameter_list|)
block|{
return|return
name|getOpt
argument_list|(
name|parsedArguments
argument_list|,
name|argument
argument_list|)
operator|.
name|map
argument_list|(
name|File
operator|::
name|toPath
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|Path
argument_list|>
name|getPathsOpt
parameter_list|(
specifier|final
name|ParsedArguments
name|parsedArguments
parameter_list|,
specifier|final
name|Argument
argument_list|<
name|List
argument_list|<
name|File
argument_list|>
argument_list|>
name|argument
parameter_list|)
block|{
try|try
init|(
specifier|final
name|Stream
argument_list|<
name|File
argument_list|>
name|files
init|=
name|getListOpt
argument_list|(
name|parsedArguments
argument_list|,
name|argument
argument_list|)
operator|.
name|stream
argument_list|()
init|)
block|{
return|return
name|files
operator|.
name|map
argument_list|(
name|File
operator|::
name|toPath
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Get the value of an option argument      *      * @param parsedArguments The arguments which have been parsed      * @param argument The option argument that we are looking for      *      * @return true if the option was set, false otherwise      */
specifier|public
specifier|static
name|boolean
name|getBool
parameter_list|(
specifier|final
name|ParsedArguments
name|parsedArguments
parameter_list|,
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|argument
parameter_list|)
block|{
return|return
name|getOpt
argument_list|(
name|parsedArguments
argument_list|,
name|argument
argument_list|)
operator|.
name|flatMap
argument_list|(
name|Optional
operator|::
name|ofNullable
argument_list|)
operator|.
name|orElse
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
end_class

end_unit

