begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Native XML Database  *  Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|DocumentSet
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
name|NodeProxy
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
name|NodeSet
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
name|BrokerPool
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<wolfgang@exist-db.org>  *@created    7. Oktober 2002  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Function
extends|extends
name|PathExpr
block|{
specifier|protected
name|String
name|name
decl_stmt|;
comment|/**      *  Constructor for the Function object      *      *@param  name  Description of the Parameter      */
specifier|public
name|Function
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/**  Constructor for the Function object */
specifier|public
name|Function
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  name    Description of the Parameter      *@return         Description of the Return Value      */
specifier|public
specifier|static
name|Function
name|createFunction
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|name
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"insufficient arguments"
argument_list|)
throw|;
name|Class
name|constructorArgs
index|[]
init|=
block|{
name|BrokerPool
operator|.
name|class
block|}
decl_stmt|;
name|Class
name|fclass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fclass
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"class not found"
argument_list|)
throw|;
name|Constructor
name|construct
init|=
name|fclass
operator|.
name|getConstructor
argument_list|(
name|constructorArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|construct
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"constructor not found"
argument_list|)
throw|;
name|Object
name|initArgs
index|[]
init|=
operator|new
name|Object
index|[
literal|1
index|]
decl_stmt|;
name|initArgs
index|[
literal|0
index|]
operator|=
name|pool
expr_stmt|;
name|Object
name|obj
init|=
name|construct
operator|.
name|newInstance
argument_list|(
name|initArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Function
condition|)
return|return
operator|(
name|Function
operator|)
name|obj
return|;
else|else
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"function object does not implement interface function"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"function not found"
argument_list|)
throw|;
block|}
block|}
comment|/**      *  Adds a feature to the Argument attribute of the Function object      *      *@param  expr  The feature to be added to the Argument attribute      */
specifier|public
name|void
name|addArgument
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
return|return;
name|steps
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  docs     Description of the Parameter      *@param  context  Description of the Parameter      *@param  node     Description of the Parameter      *@return          Description of the Return Value      */
specifier|public
specifier|abstract
name|Value
name|eval
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|NodeProxy
name|contextNode
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      *  Gets the argument attribute of the Function object      *      *@param  pos  Description of the Parameter      *@return      The argument value      */
specifier|public
name|Expression
name|getArgument
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|getExpression
argument_list|(
name|pos
argument_list|)
return|;
block|}
comment|/**      *  Gets the argumentCount attribute of the Function object      *      *@return    The argumentCount value      */
specifier|public
name|int
name|getArgumentCount
parameter_list|()
block|{
return|return
name|steps
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      *  Gets the name attribute of the Function object      *      *@return    The name value      */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      *  Description of the Method      *      *@return    Description of the Return Value      */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|steps
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Expression
name|e
init|=
operator|(
name|Expression
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|e
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|deleteCharAt
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

