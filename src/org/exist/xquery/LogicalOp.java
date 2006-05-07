begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
comment|/**  * Base class for the boolean operators "and" and "or".  *   * @author Wolfgang<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|LogicalOp
extends|extends
name|BinaryOp
block|{
comment|/** 	 * If set to true, the boolean operation is processed as 	 * a set operation on two node sets. This is only possible 	 * within a predicate expression and if both operands return 	 * nodes. The predicate class can then filter out the matching 	 * nodes from the context set. 	 */
specifier|protected
name|boolean
name|optimize
init|=
literal|false
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|LogicalOp
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
specifier|public
specifier|abstract
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
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BinaryOp#analyze(org.exist.xquery.Expression, int) 	 */
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
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|getLeft
argument_list|()
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|getRight
argument_list|()
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|getLeft
argument_list|()
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
operator|&&
comment|//TODO : use Dependency.VARS ?
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|getLeft
argument_list|()
argument_list|,
name|Dependency
operator|.
name|LOCAL_VARS
argument_list|)
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|getRight
argument_list|()
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
operator|&&
comment|//TODO : use Dependency.VARS ?
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|getRight
argument_list|()
argument_list|,
name|Dependency
operator|.
name|LOCAL_VARS
argument_list|)
comment|//TODO: is this accurate ? -pb
comment|/*&& contextInfo.getContextId() != -1*/
condition|)
name|optimize
operator|=
literal|true
expr_stmt|;
else|else
name|optimize
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|optimize
condition|?
name|Type
operator|.
name|NODE
else|:
name|Type
operator|.
name|BOOLEAN
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
if|if
condition|(
operator|!
name|optimize
condition|)
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
else|else
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
block|}
end_class

end_unit

