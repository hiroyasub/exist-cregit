begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *   *  Copyright (C) 2000-03, Wolfgang M. Meier (meier@ifs. tu- darmstadt. de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|Map
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
name|ContextItem
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
name|DocumentImpl
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
name|ExtArrayNodeSet
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
name|IndexPaths
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
name|analysis
operator|.
name|SimpleTokenizer
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
name|analysis
operator|.
name|TextToken
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|functions
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
name|xpath
operator|.
name|value
operator|.
name|AtomicValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|BooleanValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|SequenceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|StringValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * A general XQuery/XPath2 comparison expression.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|GeneralComparison
extends|extends
name|BinaryOp
block|{
specifier|protected
name|int
name|relation
init|=
name|Constants
operator|.
name|EQ
decl_stmt|;
specifier|public
name|GeneralComparison
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|int
name|relation
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|relation
operator|=
name|relation
expr_stmt|;
block|}
specifier|public
name|GeneralComparison
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|int
name|relation
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|relation
operator|=
name|relation
expr_stmt|;
comment|// simplify arguments
if|if
condition|(
name|left
operator|instanceof
name|PathExpr
operator|&&
operator|(
operator|(
name|PathExpr
operator|)
name|left
operator|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
name|add
argument_list|(
operator|(
operator|(
name|PathExpr
operator|)
name|left
operator|)
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|left
argument_list|)
expr_stmt|;
if|if
condition|(
name|right
operator|instanceof
name|PathExpr
operator|&&
operator|(
operator|(
name|PathExpr
operator|)
name|right
operator|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
name|add
argument_list|(
operator|(
operator|(
name|PathExpr
operator|)
name|right
operator|)
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
else|else
name|add
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.BinaryOp#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
comment|// TODO: Assumes that context sequence is a node set
if|if
condition|(
name|inPredicate
operator|&&
operator|(
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
block|{
comment|/* If one argument is a node set we directly 			 * return the matching nodes from the context set. This works 			 * only inside predicates. 			 */
return|return
name|Type
operator|.
name|NODE
return|;
block|}
comment|// In all other cases, we return boolean
return|return
name|Type
operator|.
name|BOOLEAN
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
name|int
name|leftDeps
init|=
name|getLeft
argument_list|()
operator|.
name|getDependencies
argument_list|()
decl_stmt|;
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
comment|// left expression returns node set
operator|&&
operator|(
name|leftDeps
operator|&
name|Dependency
operator|.
name|GLOBAL_VARS
operator|)
operator|==
literal|0
comment|// and has no dependency on global vars
operator|&&
operator|(
name|leftDeps
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
comment|// and does not depend on the context item
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
else|else
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#preselect(org.exist.dom.DocumentSet, org.exist.xpath.StaticContext) 	 */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|in_docs
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#eval(org.exist.xpath.StaticContext, org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
comment|/*  		 * If we are inside a predicate and one of the arguments is a node set,  		 * we try to speed up the query by returning nodes from the context set. 		 * This works only inside a predicate. 		 */
if|if
condition|(
name|inPredicate
condition|)
block|{
name|int
name|rightDeps
init|=
name|getRight
argument_list|()
operator|.
name|getDependencies
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
condition|)
block|{
if|if
condition|(
operator|(
name|rightDeps
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
operator|&&
operator|(
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
name|STRING
argument_list|)
operator|||
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
operator|)
operator|&&
operator|(
name|getRight
argument_list|()
operator|.
name|getCardinality
argument_list|()
operator|&
name|Cardinality
operator|.
name|MANY
operator|)
operator|==
literal|0
condition|)
block|{
comment|// lookup search terms in the fulltext index
return|return
name|quickNodeSetCompare
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|nodeSetCompare
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|)
return|;
block|}
block|}
block|}
comment|// Fall back to the generic compare process
return|return
name|genericCompare
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
specifier|protected
name|BooleanValue
name|genericCompare
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
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
name|ls
init|=
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|rs
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|AtomicValue
name|lv
decl_stmt|,
name|rv
decl_stmt|;
if|if
condition|(
name|ls
operator|.
name|getLength
argument_list|()
operator|==
literal|1
operator|&&
name|rs
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
block|{
name|lv
operator|=
name|ls
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
expr_stmt|;
name|rv
operator|=
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
expr_stmt|;
return|return
operator|new
name|BooleanValue
argument_list|(
name|compareValues
argument_list|(
name|context
argument_list|,
name|lv
argument_list|,
name|rv
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
for|for
control|(
name|SequenceIterator
name|i1
init|=
name|ls
operator|.
name|iterate
argument_list|()
init|;
name|i1
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|lv
operator|=
name|i1
operator|.
name|nextItem
argument_list|()
operator|.
name|atomize
argument_list|()
expr_stmt|;
if|if
condition|(
name|rs
operator|.
name|getLength
argument_list|()
operator|==
literal|1
operator|&&
name|compareValues
argument_list|(
name|context
argument_list|,
name|lv
argument_list|,
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
argument_list|)
condition|)
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
else|else
block|{
for|for
control|(
name|SequenceIterator
name|i2
init|=
name|rs
operator|.
name|iterate
argument_list|()
init|;
name|i2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|rv
operator|=
name|i2
operator|.
name|nextItem
argument_list|()
operator|.
name|atomize
argument_list|()
expr_stmt|;
if|if
condition|(
name|compareValues
argument_list|(
name|context
argument_list|,
name|lv
argument_list|,
name|rv
argument_list|)
condition|)
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
block|}
block|}
block|}
block|}
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
comment|/** 	 * Optimized implementation, which can be applied if the left operand 	 * returns a node set. In this case, the left expression is executed first. 	 * All matching context nodes are then passed to the right expression. 	 */
specifier|protected
name|Sequence
name|nodeSetCompare
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// evaluate left expression (returning node set)
name|NodeSet
name|nodes
init|=
operator|(
name|NodeSet
operator|)
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
return|return
name|nodeSetCompare
argument_list|(
name|nodes
argument_list|,
name|docs
argument_list|,
name|contextSequence
argument_list|)
return|;
block|}
specifier|protected
name|Sequence
name|nodeSetCompare
parameter_list|(
name|NodeSet
name|nodes
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|NodeProxy
name|current
decl_stmt|;
name|ContextItem
name|c
decl_stmt|;
name|Sequence
name|rs
decl_stmt|;
name|AtomicValue
name|lv
decl_stmt|,
name|rv
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|nodes
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
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|c
operator|=
name|current
operator|.
name|getContext
argument_list|()
expr_stmt|;
do|do
block|{
name|lv
operator|=
name|current
operator|.
name|atomize
argument_list|()
expr_stmt|;
name|rs
operator|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|c
operator|.
name|getNode
argument_list|()
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|si
init|=
name|rs
operator|.
name|iterate
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|compareValues
argument_list|(
name|context
argument_list|,
name|lv
argument_list|,
name|si
operator|.
name|nextItem
argument_list|()
operator|.
name|atomize
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
operator|(
name|c
operator|=
name|c
operator|.
name|getNextItem
argument_list|()
operator|)
operator|!=
literal|null
condition|)
do|;
block|}
return|return
name|result
return|;
block|}
comment|/** 	 * Optimized implementation, which uses the fulltext index to look up 	 * matching string sequences. Applies to comparisons where the left 	 * operand returns a node set and the right operand is a string literal. 	 */
specifier|protected
name|Sequence
name|quickNodeSetCompare
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//	evaluate left expression
name|NodeSet
name|nodes
init|=
operator|(
name|NodeSet
operator|)
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
name|Sequence
name|rightSeq
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|rightSeq
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
comment|// fall back to nodeSetCompare
return|return
name|nodeSetCompare
argument_list|(
name|nodes
argument_list|,
name|docs
argument_list|,
name|contextSequence
argument_list|)
return|;
name|String
name|cmp
init|=
name|rightSeq
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|getLeft
argument_list|()
operator|.
name|returnsType
argument_list|()
operator|==
name|Type
operator|.
name|NODE
operator|&&
name|relation
operator|==
name|Constants
operator|.
name|EQ
operator|&&
name|nodes
operator|.
name|hasIndex
argument_list|()
operator|&&
name|cmp
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|cmpCopy
init|=
name|cmp
decl_stmt|;
name|cmp
operator|=
name|maskWildcards
argument_list|(
name|cmp
argument_list|)
expr_stmt|;
comment|// try to use a fulltext search expression to reduce the number
comment|// of potential nodes to scan through
name|SimpleTokenizer
name|tokenizer
init|=
operator|new
name|SimpleTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setText
argument_list|(
name|cmp
argument_list|)
expr_stmt|;
name|TextToken
name|token
decl_stmt|;
name|String
name|term
decl_stmt|;
name|boolean
name|foundNumeric
init|=
literal|false
decl_stmt|;
comment|// setup up an&= expression using the fulltext index
name|ExtFulltext
name|containsExpr
init|=
operator|new
name|ExtFulltext
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|FULLTEXT_AND
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
literal|5
operator|&&
operator|(
name|token
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|(
literal|true
argument_list|)
operator|)
operator|!=
literal|null
condition|;
name|i
operator|++
control|)
block|{
comment|// remember if we find an alphanumeric token
if|if
condition|(
name|token
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|ALPHANUM
condition|)
name|foundNumeric
operator|=
literal|true
expr_stmt|;
block|}
comment|// check if all elements are indexed. If not, we can't use the
comment|// fulltext index.
if|if
condition|(
name|foundNumeric
condition|)
name|foundNumeric
operator|=
name|checkArgumentTypes
argument_list|(
name|context
argument_list|,
name|docs
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
operator|!
name|foundNumeric
operator|)
operator|&&
name|i
operator|>
literal|0
condition|)
block|{
comment|// all elements are indexed: use the fulltext index
name|containsExpr
operator|.
name|addTerm
argument_list|(
operator|new
name|LiteralValue
argument_list|(
name|context
argument_list|,
operator|new
name|StringValue
argument_list|(
name|cmp
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//LOG.debug("using shortcut: " + cmp);
name|nodes
operator|=
operator|(
name|NodeSet
operator|)
name|containsExpr
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|nodes
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|cmp
operator|=
name|cmpCopy
expr_stmt|;
block|}
comment|// now compare the input node set to the search expression
name|NodeSet
name|r
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getNodesEqualTo
argument_list|(
name|nodes
argument_list|,
name|docs
argument_list|,
name|relation
argument_list|,
name|cmp
argument_list|)
decl_stmt|;
return|return
name|r
return|;
block|}
comment|/** 	 * Cast the atomic operands into a comparable type 	 * and compare them. 	 */
specifier|protected
name|boolean
name|compareValues
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|AtomicValue
name|lv
parameter_list|,
name|AtomicValue
name|rv
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|ltype
init|=
name|lv
operator|.
name|getType
argument_list|()
decl_stmt|;
name|int
name|rtype
init|=
name|rv
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|ltype
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
name|lv
operator|=
name|lv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
if|else if
condition|(
name|rtype
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
name|lv
operator|=
name|lv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|rv
operator|=
name|rv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
else|else
name|lv
operator|=
name|lv
operator|.
name|convertTo
argument_list|(
name|rv
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|rtype
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
name|rv
operator|=
name|rv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
if|else if
condition|(
name|rtype
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
name|lv
operator|=
name|lv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|rv
operator|=
name|rv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
block|}
else|else
name|rv
operator|=
name|rv
operator|.
name|convertTo
argument_list|(
name|lv
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|isBackwardsCompatible
argument_list|()
condition|)
block|{
comment|// in XPath 1.0 compatible mode, if one of the operands is a number, cast
comment|// both operands to xs:double
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|ltype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|rtype
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|lv
operator|=
name|lv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
name|rv
operator|=
name|rv
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
block|}
block|}
comment|//		System.out.println(
comment|//			lv.getStringValue() + Constants.OPS[relation] + rv.getStringValue());
return|return
name|lv
operator|.
name|compareTo
argument_list|(
name|relation
argument_list|,
name|rv
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|checkArgumentTypes
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|XPathException
block|{
name|Configuration
name|config
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Map
name|idxPathMap
init|=
operator|(
name|Map
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"indexer.map"
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
name|IndexPaths
name|idx
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|docs
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
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|idx
operator|=
operator|(
name|IndexPaths
operator|)
name|idxPathMap
operator|.
name|get
argument_list|(
name|doc
operator|.
name|getDoctype
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|idx
operator|!=
literal|null
operator|&&
name|idx
operator|.
name|isSelective
argument_list|()
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|idx
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|idx
operator|.
name|getIncludeAlphaNum
argument_list|()
operator|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|String
name|maskWildcards
parameter_list|(
name|String
name|expr
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|char
name|ch
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expr
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ch
operator|=
name|expr
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'*'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"\\*"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'%'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
break|break;
default|default :
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#pprint() 	 */
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
name|getLeft
argument_list|()
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Constants
operator|.
name|OPS
index|[
name|relation
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getRight
argument_list|()
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|switchOperands
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"switching operands"
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|Constants
operator|.
name|GT
case|:
name|relation
operator|=
name|Constants
operator|.
name|LT
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|LT
case|:
name|relation
operator|=
name|Constants
operator|.
name|GT
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|LTEQ
case|:
name|relation
operator|=
name|Constants
operator|.
name|GTEQ
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|GTEQ
case|:
name|relation
operator|=
name|Constants
operator|.
name|LTEQ
expr_stmt|;
break|break;
block|}
name|Expression
name|right
init|=
name|getRight
argument_list|()
decl_stmt|;
name|setRight
argument_list|(
name|getLeft
argument_list|()
argument_list|)
expr_stmt|;
name|setLeft
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|simplify
parameter_list|()
block|{
comment|// switch operands to simplify execution
if|if
condition|(
operator|(
operator|!
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
operator|)
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
condition|)
name|switchOperands
argument_list|()
expr_stmt|;
if|else if
condition|(
operator|(
name|getLeft
argument_list|()
operator|.
name|getCardinality
argument_list|()
operator|&
name|Cardinality
operator|.
name|MANY
operator|)
operator|!=
literal|0
operator|&&
operator|(
name|getRight
argument_list|()
operator|.
name|getCardinality
argument_list|()
operator|&
name|Cardinality
operator|.
name|MANY
operator|)
operator|==
literal|0
condition|)
name|switchOperands
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

