begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  * Copyright (C) 2001-03, Wolfgang M. Meier (meier@ifs. tu- darmstadt. de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|functions
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|storage
operator|.
name|analysis
operator|.
name|Tokenizer
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
name|Cardinality
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
name|Constants
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
name|Dependency
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
name|Expression
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
name|Function
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
name|FunctionSignature
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
name|PathExpr
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
name|XQueryContext
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
name|XPathException
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
name|SequenceType
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

begin_class
specifier|public
class|class
name|ExtFulltext
extends|extends
name|Function
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
literal|"contains"
argument_list|,
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
name|PathExpr
name|path
decl_stmt|;
specifier|protected
name|Expression
name|searchTerm
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|terms
index|[]
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|type
init|=
name|Constants
operator|.
name|FULLTEXT_AND
decl_stmt|;
specifier|public
name|ExtFulltext
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|ExtFulltext
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|PathExpr
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|void
name|addTerm
parameter_list|(
name|Expression
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|instanceof
name|PathExpr
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|PathExpr
operator|)
name|term
operator|)
operator|.
name|getLength
argument_list|()
operator|==
literal|1
condition|)
name|term
operator|=
operator|(
operator|(
name|PathExpr
operator|)
name|term
operator|)
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|searchTerm
operator|=
name|term
expr_stmt|;
block|}
specifier|protected
name|void
name|getSearchTerms
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|searchString
parameter_list|)
throws|throws
name|EXistException
block|{
name|List
name|tokens
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|Tokenizer
name|tokenizer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getTextEngine
argument_list|()
operator|.
name|getTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setText
argument_list|(
name|searchString
argument_list|)
expr_stmt|;
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|analysis
operator|.
name|TextToken
name|token
decl_stmt|;
name|String
name|word
decl_stmt|;
while|while
condition|(
literal|null
operator|!=
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
condition|)
block|{
name|word
operator|=
name|token
operator|.
name|getText
argument_list|()
expr_stmt|;
name|tokens
operator|.
name|add
argument_list|(
name|word
argument_list|)
expr_stmt|;
block|}
name|terms
operator|=
operator|new
name|String
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|terms
operator|=
operator|(
name|String
index|[]
operator|)
name|tokens
operator|.
name|toArray
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|countTerms
parameter_list|()
block|{
return|return
name|terms
operator|.
name|length
return|;
block|}
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
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
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
name|Dependency
operator|.
name|NO_DEPENDENCY
condition|)
block|{
name|NodeSet
name|nodes
init|=
name|path
operator|==
literal|null
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
name|path
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|String
name|arg
init|=
name|searchTerm
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
return|return
name|evalQuery
argument_list|(
name|context
argument_list|,
name|arg
argument_list|,
name|nodes
argument_list|)
return|;
block|}
else|else
block|{
name|Item
name|current
decl_stmt|;
name|String
name|arg
decl_stmt|;
name|NodeSet
name|nodes
init|=
literal|null
decl_stmt|;
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|Sequence
name|temp
decl_stmt|;
name|boolean
name|haveNodes
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|(
name|path
operator|.
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
name|Dependency
operator|.
name|NO_DEPENDENCY
condition|)
block|{
name|nodes
operator|=
name|path
operator|==
literal|null
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
name|path
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
name|haveNodes
operator|=
literal|true
expr_stmt|;
block|}
for|for
control|(
name|SequenceIterator
name|i
init|=
name|contextSequence
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
name|current
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|arg
operator|=
name|searchTerm
operator|.
name|eval
argument_list|(
name|current
operator|.
name|toSequence
argument_list|()
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|haveNodes
condition|)
block|{
name|nodes
operator|=
name|path
operator|==
literal|null
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
name|path
operator|.
name|eval
argument_list|(
name|current
operator|.
name|toSequence
argument_list|()
argument_list|)
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
block|}
name|temp
operator|=
name|evalQuery
argument_list|(
name|context
argument_list|,
name|arg
argument_list|,
name|nodes
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|temp
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|temp
operator|.
name|getLength
argument_list|()
operator|+
literal|" for "
operator|+
name|arg
operator|+
literal|" in "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
specifier|public
name|Sequence
name|evalQuery
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|searchArg
parameter_list|,
name|NodeSet
name|nodes
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|getSearchTerms
argument_list|(
name|context
argument_list|,
name|searchArg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|NodeSet
name|hits
init|=
name|processQuery
argument_list|(
name|context
argument_list|,
name|nodes
argument_list|)
decl_stmt|;
if|if
condition|(
name|hits
operator|==
literal|null
condition|)
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
return|return
name|hits
return|;
block|}
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
name|path
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"&= \""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|searchTerm
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.functions.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
name|int
name|deps
init|=
name|Dependency
operator|.
name|NO_DEPENDENCY
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
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
name|deps
operator|=
name|deps
operator||
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|getDependencies
argument_list|()
expr_stmt|;
return|return
name|deps
return|;
block|}
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
block|{
return|return
name|in_docs
return|;
block|}
specifier|protected
name|NodeSet
name|processQuery
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
block|{
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no search terms"
argument_list|)
throw|;
name|NodeSet
name|hits
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|terms
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|hits
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getTextEngine
argument_list|()
operator|.
name|getNodesContaining
argument_list|(
name|contextSet
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|contextSet
argument_list|,
name|terms
index|[
name|k
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|==
literal|null
condition|)
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
if|if
condition|(
name|type
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
condition|)
name|contextSet
operator|=
name|hits
expr_stmt|;
block|}
return|return
name|hits
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
specifier|public
name|void
name|setPath
parameter_list|(
name|PathExpr
name|path
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#setInPredicate(boolean) 	 */
specifier|public
name|void
name|setInPredicate
parameter_list|(
name|boolean
name|inPredicate
parameter_list|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
name|path
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.PathExpr#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|path
operator|.
name|resetState
argument_list|()
expr_stmt|;
name|searchTerm
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

