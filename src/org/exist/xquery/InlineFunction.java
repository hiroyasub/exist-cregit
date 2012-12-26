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
name|xquery
package|;
end_package

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
name|util
operator|.
name|ExpressionDumper
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
name|Type
import|;
end_import

begin_comment
comment|/**  * An XQuery 3.0 inline function expression.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|InlineFunction
extends|extends
name|AbstractExpression
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|INLINE_FUNCTION_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
name|UserDefinedFunction
name|function
decl_stmt|;
specifier|private
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|InlineFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|UserDefinedFunction
name|function
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|cachedContextInfo
operator|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|AnalyzeContextInfo
name|info
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|info
operator|.
name|addFlag
argument_list|(
name|SINGLE_STEP_EXECUTION
argument_list|)
expr_stmt|;
comment|// local variable context is known within inline function:
name|List
argument_list|<
name|Variable
argument_list|>
name|closureVars
init|=
name|context
operator|.
name|getLocalStack
argument_list|()
decl_stmt|;
name|function
operator|.
name|setClosureVariables
argument_list|(
name|closureVars
argument_list|)
expr_stmt|;
name|function
operator|.
name|analyze
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"function"
argument_list|)
expr_stmt|;
name|function
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Wraps a function call around the function and returns a 	 * reference to it. Make sure local variables in the context 	 * are visible. 	 */
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
comment|// local variable context is known within inline function
name|List
argument_list|<
name|Variable
argument_list|>
name|closureVars
init|=
name|context
operator|.
name|getLocalStack
argument_list|()
decl_stmt|;
name|function
operator|.
name|setClosureVariables
argument_list|(
name|closureVars
argument_list|)
expr_stmt|;
name|FunctionCall
name|call
init|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|function
argument_list|)
decl_stmt|;
name|call
operator|.
name|setLocation
argument_list|(
name|function
operator|.
name|getLine
argument_list|()
argument_list|,
name|function
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|function
operator|.
name|setCaller
argument_list|(
name|call
argument_list|)
expr_stmt|;
name|function
operator|.
name|analyze
argument_list|(
name|cachedContextInfo
argument_list|)
expr_stmt|;
return|return
operator|new
name|FunctionReference
argument_list|(
name|call
argument_list|)
return|;
block|}
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
name|FUNCTION_REFERENCE
return|;
block|}
block|}
end_class

end_unit

