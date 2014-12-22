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
name|dom
operator|.
name|persistent
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
name|persistent
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
name|*
import|;
end_import

begin_comment
comment|/**  * Check a function parameter type at runtime.  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DynamicTypeCheck
extends|extends
name|AbstractExpression
block|{
specifier|final
specifier|private
name|Expression
name|expression
decl_stmt|;
specifier|final
specifier|private
name|int
name|requiredType
decl_stmt|;
specifier|public
name|DynamicTypeCheck
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|requiredType
parameter_list|,
name|Expression
name|expr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|requiredType
operator|=
name|requiredType
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expr
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.AnalyzeContextInfo)      */
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
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|expression
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
specifier|final
name|Sequence
name|seq
init|=
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|requiredType
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|seq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|requiredType
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|seq
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|check
argument_list|(
name|result
argument_list|,
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|check
argument_list|(
name|result
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|==
literal|null
condition|?
name|seq
else|:
name|result
return|;
block|}
specifier|private
name|void
name|check
parameter_list|(
name|Sequence
name|result
parameter_list|,
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|type
init|=
name|item
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|NODE
operator|&&
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|type
operator|=
operator|(
operator|(
name|NodeProxy
operator|)
name|item
operator|)
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|NodeProxy
operator|.
name|UNKNOWN_NODE_TYPE
condition|)
comment|//Retrieve the actual node
block|{
name|type
operator|=
operator|(
operator|(
name|NodeProxy
operator|)
name|item
operator|)
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|type
operator|!=
name|requiredType
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|requiredType
argument_list|)
condition|)
block|{
comment|//TODO : how to make this block more generic ? -pb
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|UNTYPED_ATOMIC
condition|)
block|{
try|try
block|{
name|item
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
expr_stmt|;
comment|//No way
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
literal|"Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
comment|//XDM: The dm:string-value accessor returns the string value of a node. It is defined on all seven node kinds.
block|}
if|else if
condition|(
name|requiredType
operator|==
name|Type
operator|.
name|STRING
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|item
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
comment|//Then, if numeric, try to refine the type
comment|//xs:decimal(3) treat as xs:integer
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|requiredType
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|requiredType
argument_list|)
condition|)
block|{
try|try
block|{
name|item
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
expr_stmt|;
comment|//No way
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
literal|"Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
comment|//Then, if duration, try to refine the type
comment|//No test on the type hierarchy ; this has to pass :
comment|//fn:months-from-duration(xs:duration("P1Y2M3DT10H30M"))
comment|//TODO : find a way to enforce the test (by making a difference between casting and treating as ?)
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|requiredType
argument_list|,
name|Type
operator|.
name|DURATION
argument_list|)
comment|/*&& Type.subTypeOf(type, requiredType)*/
condition|)
block|{
try|try
block|{
name|item
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
expr_stmt|;
comment|//No way
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
literal|"Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
comment|//Then, if date, try to refine the type
comment|//No test on the type hierarchy
comment|//TODO : find a way to enforce the test (by making a difference between casting and treating as ?)
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|requiredType
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
comment|/*&& Type.subTypeOf(type, requiredType)*/
condition|)
block|{
try|try
block|{
name|item
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
expr_stmt|;
comment|//No way
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
literal|"Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
comment|//URI type promotion: A value of type xs:anyURI (or any type derived
comment|//by restriction from xs:anyURI) can be promoted to the type xs:string.
comment|//The result of this promotion is created by casting the
comment|//original value to the type xs:string.
block|}
if|else if
condition|(
name|type
operator|==
name|Type
operator|.
name|ANY_URI
operator|&&
name|requiredType
operator|==
name|Type
operator|.
name|STRING
condition|)
block|{
name|item
operator|=
name|item
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|type
operator|=
name|Type
operator|.
name|STRING
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|requiredType
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|") is not a sub-type of "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|expression
argument_list|,
name|ErrorCodes
operator|.
name|FOCH0002
argument_list|,
literal|"Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)     */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
if|if
condition|(
name|dumper
operator|.
name|verbosity
argument_list|()
operator|>
literal|1
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"dynamic-type-check"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|expression
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
if|if
condition|(
name|dumper
operator|.
name|verbosity
argument_list|()
operator|>
literal|1
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|expression
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|requiredType
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getDependencies
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|expression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContextDocSet
parameter_list|(
name|DocumentSet
name|contextSet
parameter_list|)
block|{
name|super
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
name|expression
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getLine
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getLine
argument_list|()
return|;
block|}
specifier|public
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|expression
operator|.
name|getColumn
argument_list|()
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|expression
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getSubExpressionCount
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
specifier|public
name|Expression
name|getSubExpression
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
return|return
name|expression
return|;
block|}
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Index: "
operator|+
name|index
operator|+
literal|", Size: "
operator|+
name|getSubExpressionCount
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

