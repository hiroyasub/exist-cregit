begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|runner
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
name|ExpressionVisitor
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
name|UserDefinedFunction
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
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|notification
operator|.
name|RunNotifier
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|functionSignature
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|returnsNothing
import|;
end_import

begin_comment
comment|/**  * Base class for XQuery functions that integrate with JUnit.  *  * @author Adam Retter  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|JUnitIntegrationFunction
extends|extends
name|UserDefinedFunction
block|{
specifier|protected
specifier|final
name|String
name|suiteName
decl_stmt|;
specifier|protected
specifier|final
name|RunNotifier
name|notifier
decl_stmt|;
specifier|public
name|JUnitIntegrationFunction
parameter_list|(
specifier|final
name|String
name|functionName
parameter_list|,
specifier|final
name|FunctionParameterSequenceType
index|[]
name|paramTypes
parameter_list|,
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|String
name|suiteName
parameter_list|,
specifier|final
name|RunNotifier
name|notifier
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|functionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|functionName
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
argument_list|,
literal|"External JUnit integration function"
argument_list|,
name|returnsNothing
argument_list|()
argument_list|,
name|paramTypes
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|suiteName
operator|=
name|suiteName
expr_stmt|;
name|this
operator|.
name|notifier
operator|=
name|notifier
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
if|if
condition|(
name|visited
condition|)
block|{
return|return;
block|}
name|visited
operator|=
literal|true
expr_stmt|;
block|}
block|}
end_class

end_unit

