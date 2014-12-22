begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|range
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|NodeSet
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
name|indexing
operator|.
name|range
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
name|storage
operator|.
name|IndexSpec
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
name|NodePath
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|pragmas
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A pragma which checks if an XPath expression could be replaced with a range field lookup.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|OptimizeFieldPragma
extends|extends
name|Pragma
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|OPTIMIZE_RANGE_PRAGMA
init|=
operator|new
name|QName
argument_list|(
literal|"optimize-field"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|private
name|Expression
name|rewritten
init|=
literal|null
decl_stmt|;
specifier|private
name|AnalyzeContextInfo
name|contextInfo
decl_stmt|;
specifier|private
name|int
name|axis
decl_stmt|;
specifier|public
name|OptimizeFieldPragma
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|contents
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|qname
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
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
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|contextInfo
operator|=
name|contextInfo
expr_stmt|;
block|}
annotation|@
name|Override
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
if|if
condition|(
name|rewritten
operator|!=
literal|null
condition|)
block|{
name|rewritten
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
return|return
name|rewritten
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|before
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|LocationStep
name|locationStep
init|=
operator|(
name|LocationStep
operator|)
name|expression
decl_stmt|;
if|if
condition|(
name|locationStep
operator|.
name|hasPredicates
argument_list|()
condition|)
block|{
name|Expression
name|parentExpr
init|=
name|locationStep
operator|.
name|getParentExpression
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|parentExpr
operator|instanceof
name|RewritableExpression
operator|)
condition|)
block|{
return|return;
block|}
specifier|final
name|List
argument_list|<
name|Predicate
argument_list|>
name|preds
init|=
name|locationStep
operator|.
name|getPredicates
argument_list|()
decl_stmt|;
comment|// get path of path expression before the predicates
name|NodePath
name|contextPath
init|=
name|RangeQueryRewriter
operator|.
name|toNodePath
argument_list|(
name|RangeQueryRewriter
operator|.
name|getPrecedingSteps
argument_list|(
name|locationStep
argument_list|)
argument_list|)
decl_stmt|;
name|rewritten
operator|=
name|tryRewriteToFields
argument_list|(
name|locationStep
argument_list|,
name|preds
argument_list|,
name|contextPath
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
name|axis
operator|=
name|locationStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|after
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
specifier|private
name|Expression
name|tryRewriteToFields
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|,
name|List
argument_list|<
name|Predicate
argument_list|>
name|preds
parameter_list|,
name|NodePath
name|contextPath
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// without context path, we cannot rewrite the entire query
if|if
condition|(
name|contextPath
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|Expression
argument_list|>
name|args
init|=
literal|null
decl_stmt|;
name|SequenceConstructor
name|arg0
init|=
literal|null
decl_stmt|;
name|SequenceConstructor
name|arg1
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Predicate
argument_list|>
name|notOptimizable
init|=
operator|new
name|ArrayList
argument_list|<
name|Predicate
argument_list|>
argument_list|(
name|preds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RangeIndexConfig
argument_list|>
name|configs
init|=
name|getConfigurations
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
comment|// walk through the predicates attached to the current location step
comment|// check if expression can be optimized
for|for
control|(
specifier|final
name|Predicate
name|pred
range|:
name|preds
control|)
block|{
if|if
condition|(
name|pred
operator|.
name|getLength
argument_list|()
operator|!=
literal|1
condition|)
block|{
comment|// can only optimize predicates with one expression
name|notOptimizable
operator|.
name|add
argument_list|(
name|pred
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|Expression
name|innerExpr
init|=
name|pred
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocationStep
argument_list|>
name|steps
init|=
name|RangeQueryRewriter
operator|.
name|getStepsToOptimize
argument_list|(
name|innerExpr
argument_list|)
decl_stmt|;
if|if
condition|(
name|steps
operator|==
literal|null
condition|)
block|{
name|notOptimizable
operator|.
name|add
argument_list|(
name|pred
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// compute left hand path
name|NodePath
name|innerPath
init|=
name|RangeQueryRewriter
operator|.
name|toNodePath
argument_list|(
name|steps
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerPath
operator|==
literal|null
condition|)
block|{
name|notOptimizable
operator|.
name|add
argument_list|(
name|pred
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|(
name|contextPath
argument_list|)
decl_stmt|;
name|path
operator|.
name|append
argument_list|(
name|innerPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// find a range index configuration matching the full path to the predicate expression
name|RangeIndexConfigElement
name|rice
init|=
name|findConfiguration
argument_list|(
name|path
argument_list|,
literal|true
argument_list|,
name|configs
argument_list|)
decl_stmt|;
comment|// found index configuration with sub-fields
if|if
condition|(
name|rice
operator|!=
literal|null
operator|&&
name|rice
operator|.
name|isComplex
argument_list|()
operator|&&
name|rice
operator|.
name|getNodePath
argument_list|()
operator|.
name|match
argument_list|(
name|contextPath
argument_list|)
operator|&&
name|findConfiguration
argument_list|(
name|path
argument_list|,
literal|false
argument_list|,
name|configs
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// check for a matching sub-path and retrieve field information
name|RangeIndexConfigField
name|field
init|=
operator|(
operator|(
name|ComplexRangeIndexConfigElement
operator|)
name|rice
operator|)
operator|.
name|getField
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
comment|// initialize args
name|args
operator|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|arg0
operator|=
operator|new
name|SequenceConstructor
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
name|arg1
operator|=
operator|new
name|SequenceConstructor
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|// field is added to the sequence in first parameter
name|arg0
operator|.
name|add
argument_list|(
operator|new
name|LiteralValue
argument_list|(
name|context
argument_list|,
operator|new
name|StringValue
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// operator
name|arg1
operator|.
name|add
argument_list|(
operator|new
name|LiteralValue
argument_list|(
name|context
argument_list|,
operator|new
name|StringValue
argument_list|(
name|RangeQueryRewriter
operator|.
name|getOperator
argument_list|(
name|innerExpr
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// append right hand expression as additional parameter
name|args
operator|.
name|add
argument_list|(
name|getKeyArg
argument_list|(
name|innerExpr
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|notOptimizable
operator|.
name|add
argument_list|(
name|pred
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
else|else
block|{
name|notOptimizable
operator|.
name|add
argument_list|(
name|pred
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
else|else
block|{
name|notOptimizable
operator|.
name|add
argument_list|(
name|pred
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
comment|// the entire filter expression can be replaced
comment|// create range:field-equals function
name|FieldLookup
name|func
init|=
operator|new
name|FieldLookup
argument_list|(
name|context
argument_list|,
name|FieldLookup
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|func
operator|.
name|setFallback
argument_list|(
name|locationStep
argument_list|)
expr_stmt|;
name|func
operator|.
name|setLocation
argument_list|(
name|locationStep
operator|.
name|getLine
argument_list|()
argument_list|,
name|locationStep
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|func
operator|.
name|setArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|Expression
name|optimizedExpr
init|=
operator|new
name|InternalFunctionCall
argument_list|(
name|func
argument_list|)
decl_stmt|;
if|if
condition|(
name|notOptimizable
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|FilteredExpression
name|filtered
init|=
operator|new
name|FilteredExpression
argument_list|(
name|context
argument_list|,
name|optimizedExpr
argument_list|)
decl_stmt|;
for|for
control|(
name|Predicate
name|pred
range|:
name|notOptimizable
control|)
block|{
name|filtered
operator|.
name|addPredicate
argument_list|(
name|pred
argument_list|)
expr_stmt|;
block|}
name|optimizedExpr
operator|=
name|filtered
expr_stmt|;
block|}
return|return
name|optimizedExpr
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Expression
name|getKeyArg
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|GeneralComparison
condition|)
block|{
return|return
operator|(
operator|(
name|GeneralComparison
operator|)
name|expression
operator|)
operator|.
name|getRight
argument_list|()
return|;
block|}
if|else if
condition|(
name|expression
operator|instanceof
name|InternalFunctionCall
condition|)
block|{
name|InternalFunctionCall
name|fcall
init|=
operator|(
name|InternalFunctionCall
operator|)
name|expression
decl_stmt|;
name|Function
name|function
init|=
name|fcall
operator|.
name|getFunction
argument_list|()
decl_stmt|;
if|if
condition|(
name|function
operator|instanceof
name|Lookup
condition|)
block|{
return|return
name|function
operator|.
name|getArgument
argument_list|(
literal|1
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Scan all index configurations to find one matching path.      */
specifier|private
name|RangeIndexConfigElement
name|findConfiguration
parameter_list|(
name|NodePath
name|path
parameter_list|,
name|boolean
name|complex
parameter_list|,
name|List
argument_list|<
name|RangeIndexConfig
argument_list|>
name|configs
parameter_list|)
block|{
for|for
control|(
name|RangeIndexConfig
name|config
range|:
name|configs
control|)
block|{
specifier|final
name|RangeIndexConfigElement
name|rice
init|=
name|config
operator|.
name|find
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|rice
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|complex
operator|&&
name|rice
operator|.
name|isComplex
argument_list|()
operator|)
operator|||
operator|(
operator|!
name|complex
operator|&&
operator|!
name|rice
operator|.
name|isComplex
argument_list|()
operator|)
operator|)
condition|)
block|{
return|return
name|rice
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|List
argument_list|<
name|RangeIndexConfig
argument_list|>
name|getConfigurations
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
name|List
argument_list|<
name|RangeIndexConfig
argument_list|>
name|configs
init|=
operator|new
name|ArrayList
argument_list|<
name|RangeIndexConfig
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|i
init|=
name|contextSequence
operator|.
name|getCollectionIterator
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
name|Collection
name|collection
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|IndexSpec
name|idxConf
init|=
name|collection
operator|.
name|getIndexConfiguration
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxConf
operator|!=
literal|null
condition|)
block|{
specifier|final
name|RangeIndexConfig
name|config
init|=
operator|(
name|RangeIndexConfig
operator|)
name|idxConf
operator|.
name|getCustomIndexSpec
argument_list|(
name|RangeIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|configs
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|configs
return|;
block|}
block|}
end_class

end_unit

