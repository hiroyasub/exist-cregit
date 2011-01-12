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
name|Constants
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
name|SequenceType
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DeprecatedExtRegexpOr
extends|extends
name|DeprecatedExtRegexp
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"match-any"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Tries to match each of the regular expression "
operator|+
literal|"strings passed in $regular-expression and all following parameters against the keywords contained in "
operator|+
literal|"the old fulltext index. The keywords found are then compared to the node set in $nodes. Every "
operator|+
literal|"node containing any of the keywords is copied to the result sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|SOURCE_PARAM
block|,
name|REGEX_PARAM
block|}
argument_list|,
name|RETURN_TYPE
argument_list|,
literal|true
argument_list|,
literal|"This function is eXist-specific and should not be in the standard functions namespace. Please "
operator|+
literal|"use text:match-any() instead."
argument_list|)
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|DeprecatedExtRegexpOr
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|FULLTEXT_OR
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

