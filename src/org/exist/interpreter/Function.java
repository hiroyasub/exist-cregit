begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|interpreter
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
name|Expression
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

begin_interface
specifier|public
interface|interface
name|Function
extends|extends
name|IPathExpr
block|{
comment|/** 	 * Set the parent expression of this function, i.e. the 	 * expression from which the function is called. 	 *  	 * @param parent 	 */
specifier|public
name|void
name|setParent
parameter_list|(
name|Expression
name|parent
parameter_list|)
function_decl|;
comment|/** 	 * Returns the expression from which this function 	 * gets called. 	 */
specifier|public
name|Expression
name|getParent
parameter_list|()
function_decl|;
comment|/** 	 * Set the (static) arguments for this function from a list of expressions. 	 *  	 * This will also check the type and cardinality of the 	 * passed argument expressions. 	 *  	 * @param arguments 	 * @throws XPathException 	 */
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|Sequence
index|[]
name|getArguments
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/** 	 * Get an argument expression by its position in the 	 * argument list. 	 *  	 * @param pos 	 */
specifier|public
name|Expression
name|getArgument
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
comment|/** 	 * Get the number of arguments passed to this function. 	 *  	 * @return number of arguments 	 */
specifier|public
name|int
name|getArgumentCount
parameter_list|()
function_decl|;
comment|/** 	 * Return the name of this function. 	 *  	 * @return name of this function 	 */
specifier|public
name|QName
name|getName
parameter_list|()
function_decl|;
comment|/** 	 * Get the signature of this function. 	 *  	 * @return signature of this function 	 */
specifier|public
name|FunctionSignature
name|getSignature
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isCalledAs
parameter_list|(
name|String
name|localName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

