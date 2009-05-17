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
name|IntegerValue
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
name|NumericValue
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
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * An XQuery range expression, like "1 to 10".  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|RangeExpression
extends|extends
name|PathExpr
block|{
name|Expression
name|start
decl_stmt|;
name|Expression
name|end
decl_stmt|;
comment|/** 	 * @param context 	 */
comment|//TODO : RangeExpression(XQueryContext context, Expressoin start, Expression end)
comment|//Needs parser refactoring
specifier|public
name|RangeExpression
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
comment|//TODO : remove and use the other constructor
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|start
operator|=
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|end
operator|=
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
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
comment|//TODO : static checks ?
comment|/*     	if (!Cardinality.checkCardinality(Cardinality.ZERO_OR_ONE, start.getCardinality())) 		    throw new XPathException(this, "Invalid cardinality for 1st argument");     	if (!Cardinality.checkCardinality(Cardinality.ZERO_OR_ONE, end.getCardinality())) 		    throw new XPathException(this, "Invalid cardinality for 2nd argument");     	if (start.returnsType() != Type.INTEGER) 		    throw new XPathException(this, "Invalid type for 1st argument");     	if (end.returnsType() != Type.INTEGER) 		    throw new XPathException(this, "Invalid type for 2nd argument");         */
name|inPredicate
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
expr_stmt|;
name|contextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
expr_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|start
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|end
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
name|Sequence
name|startSeq
init|=
name|start
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|endSeq
init|=
name|end
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|startSeq
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
if|else if
condition|(
name|endSeq
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
if|else if
condition|(
name|startSeq
operator|.
name|hasMany
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: the first operand must have at most one item"
argument_list|)
throw|;
if|else if
condition|(
name|endSeq
operator|.
name|hasMany
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XPTY0004: the second operand must have at most one item"
argument_list|)
throw|;
else|else
block|{
if|if
condition|(
name|context
operator|.
name|isBackwardsCompatible
argument_list|()
condition|)
block|{
name|NumericValue
name|valueStart
decl_stmt|;
try|try
block|{
comment|//Currently breaks 1e3 to 3
name|valueStart
operator|=
operator|(
name|NumericValue
operator|)
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
name|NumericValue
name|valueEnd
decl_stmt|;
try|try
block|{
comment|//Currently breaks 3 to 1e3
name|valueEnd
operator|=
operator|(
name|NumericValue
operator|)
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
comment|//Implied by previous conversion
if|if
condition|(
name|valueStart
operator|.
name|hasFractionalPart
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
comment|//Implied by previous conversion
if|if
condition|(
name|valueEnd
operator|.
name|hasFractionalPart
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
block|}
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|valueStart
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
init|;
name|i
operator|<=
operator|(
operator|(
name|IntegerValue
operator|)
name|valueEnd
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//Quite unusual test : we accept integers but no other *typed* type
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|UNTYPED_ATOMIC
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
comment|//Quite unusual test : we accept integers but no other *typed* type
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|UNTYPED_ATOMIC
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"FORG0006: Required type is "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|+
literal|" but got '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|"("
operator|+
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|+
literal|")'"
argument_list|)
throw|;
name|IntegerValue
name|valueStart
init|=
operator|(
name|IntegerValue
operator|)
name|startSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
name|IntegerValue
name|valueEnd
init|=
operator|(
name|IntegerValue
operator|)
name|endSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
name|valueStart
operator|.
name|getLong
argument_list|()
init|;
name|i
operator|<=
name|valueEnd
operator|.
name|getLong
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
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
name|start
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" to "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|end
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|INTEGER
return|;
block|}
block|}
end_class

end_unit

