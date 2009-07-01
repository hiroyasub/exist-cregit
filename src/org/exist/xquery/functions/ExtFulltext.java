begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  * Copyright (C) 2001-04, Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
package|;
end_package

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
name|DBBroker
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
name|ElementValue
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
name|FulltextIndexSpec
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
name|AnalyzeContextInfo
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
name|BasicExpressionVisitor
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
name|CachedResult
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
name|Cardinality
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
name|Constants
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
name|Dependency
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
name|Function
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
name|FunctionSignature
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
name|LocationStep
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
name|NodeTest
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
name|Optimizable
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
name|PathExpr
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
name|SequenceIterator
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
name|SequenceType
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
comment|/**  * Implements the fulltext operators:&amp;= and |=.  *   * This is internally handled like a special function and thus inherits  * from {@link org.exist.xquery.Function}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ExtFulltext
extends|extends
name|Function
implements|implements
name|Optimizable
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
comment|//TODO : change this ! -pb
operator|new
name|QName
argument_list|(
literal|"contains"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|""
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
name|int
name|type
init|=
name|Constants
operator|.
name|FULLTEXT_AND
decl_stmt|;
specifier|protected
name|CachedResult
name|cached
init|=
literal|null
decl_stmt|;
specifier|private
name|LocationStep
name|contextStep
init|=
literal|null
decl_stmt|;
specifier|protected
name|QName
name|contextQName
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|axis
init|=
name|Constants
operator|.
name|UNKNOWN_AXIS
decl_stmt|;
specifier|protected
name|boolean
name|optimizeSelf
init|=
literal|false
decl_stmt|;
specifier|protected
name|NodeSet
name|preselectResult
init|=
literal|null
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#analyze(org.exist.xquery.AnalyzeContextInfo)      */
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
name|AnalyzeContextInfo
name|newContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|path
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
name|searchTerm
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
name|List
name|steps
init|=
name|BasicExpressionVisitor
operator|.
name|findLocationSteps
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|steps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LocationStep
name|firstStep
init|=
operator|(
name|LocationStep
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LocationStep
name|lastStep
init|=
operator|(
name|LocationStep
operator|)
name|steps
operator|.
name|get
argument_list|(
name|steps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|firstStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|)
block|{
name|Expression
name|outerExpr
init|=
name|contextInfo
operator|.
name|getContextStep
argument_list|()
decl_stmt|;
if|if
condition|(
name|outerExpr
operator|!=
literal|null
operator|&&
name|outerExpr
operator|instanceof
name|LocationStep
condition|)
block|{
name|LocationStep
name|outerStep
init|=
operator|(
name|LocationStep
operator|)
name|outerExpr
decl_stmt|;
name|NodeTest
name|test
init|=
name|outerStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|outerStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|outerStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|contextStep
operator|=
name|firstStep
expr_stmt|;
name|axis
operator|=
name|outerStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
name|optimizeSelf
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|NodeTest
name|test
init|=
name|lastStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|lastStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|contextStep
operator|=
name|lastStep
expr_stmt|;
name|axis
operator|=
name|firstStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|SELF_AXIS
operator|&&
name|steps
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
name|axis
operator|=
operator|(
operator|(
name|LocationStep
operator|)
name|steps
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getAxis
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|boolean
name|canOptimize
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
if|if
condition|(
name|contextQName
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|checkForQNameIndex
argument_list|(
name|contextSequence
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|optimizeOnSelf
parameter_list|()
block|{
return|return
name|optimizeSelf
return|;
block|}
specifier|public
name|int
name|getOptimizeAxis
parameter_list|()
block|{
return|return
name|axis
return|;
block|}
specifier|public
name|NodeSet
name|preSelect
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|boolean
name|useContext
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// the expression can be called multiple times, so we need to clear the previous preselectResult
name|preselectResult
operator|=
literal|null
expr_stmt|;
comment|// get the search terms
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
name|String
index|[]
name|terms
decl_stmt|;
try|try
block|{
name|terms
operator|=
name|getSearchTerms
argument_list|(
name|arg
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
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// lookup the terms in the fulltext index. returns one node set for each term
name|NodeSet
index|[]
name|hits
init|=
name|getMatches
argument_list|(
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|useContext
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
literal|null
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|contextQName
argument_list|,
name|terms
argument_list|)
decl_stmt|;
comment|// walk through the matches and compute the combined node set
name|preselectResult
operator|=
name|hits
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|preselectResult
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|1
init|;
name|k
operator|<
name|hits
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|hits
index|[
name|k
index|]
operator|!=
literal|null
condition|)
block|{
name|preselectResult
operator|=
operator|(
name|type
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
condition|?
name|preselectResult
operator|.
name|deepIntersection
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
else|:
name|preselectResult
operator|.
name|union
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
operator|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|preselectResult
operator|=
name|NodeSet
operator|.
name|EMPTY_SET
expr_stmt|;
block|}
return|return
name|preselectResult
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
comment|// if we were optimizing and the preselect did not return anything,
comment|// we won't have any matches and can return
if|if
condition|(
name|preselectResult
operator|!=
literal|null
operator|&&
name|preselectResult
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
name|preselectResult
operator|==
literal|null
operator|&&
operator|!
name|checkForQNameIndex
argument_list|(
name|contextSequence
argument_list|)
condition|)
name|contextQName
operator|=
literal|null
expr_stmt|;
name|NodeSet
name|result
decl_stmt|;
comment|// if the expression does not depend on the current context item,
comment|// we can evaluate it in one single step
if|if
condition|(
name|path
operator|==
literal|null
operator|||
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|path
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
block|{
name|boolean
name|canCache
init|=
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|searchTerm
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOnVar
argument_list|(
name|searchTerm
argument_list|)
decl_stmt|;
if|if
condition|(
name|canCache
operator|&&
name|cached
operator|!=
literal|null
operator|&&
name|cached
operator|.
name|isValid
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
condition|)
block|{
return|return
name|cached
operator|.
name|getResult
argument_list|()
return|;
block|}
comment|// do we optimize this expression?
if|if
condition|(
name|contextStep
operator|==
literal|null
operator|||
name|preselectResult
operator|==
literal|null
condition|)
block|{
comment|// no optimization: process the whole expression
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
name|result
operator|=
name|evalQuery
argument_list|(
name|arg
argument_list|,
name|nodes
argument_list|)
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|contextStep
operator|.
name|setPreloadedData
argument_list|(
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|preselectResult
argument_list|)
expr_stmt|;
name|result
operator|=
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
block|}
if|if
condition|(
name|canCache
operator|&&
name|contextSequence
operator|!=
literal|null
operator|&&
name|contextSequence
operator|.
name|isCacheable
argument_list|()
condition|)
name|cached
operator|=
operator|new
name|CachedResult
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|result
argument_list|)
expr_stmt|;
comment|// otherwise we have to walk through each item in the context
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
decl_stmt|;
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
name|Sequence
name|temp
decl_stmt|;
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
name|temp
operator|=
name|evalQuery
argument_list|(
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
block|}
block|}
name|preselectResult
operator|=
literal|null
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|boolean
name|checkForQNameIndex
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
name|contextQName
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|boolean
name|hasQNameIndex
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Iterator
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
name|Collection
name|collection
init|=
operator|(
name|Collection
operator|)
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
continue|continue;
name|FulltextIndexSpec
name|config
init|=
name|collection
operator|.
name|getFulltextIndexConfiguration
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
comment|//We have a fulltext index
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|hasQNameIndex
operator|=
name|config
operator|.
name|hasQNameIndex
argument_list|(
name|contextQName
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hasQNameIndex
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"cannot use index on QName: "
operator|+
name|contextQName
operator|+
literal|". Collection "
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|" does not define an index"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|hasQNameIndex
return|;
block|}
specifier|protected
name|Sequence
name|evalQuery
parameter_list|(
name|String
name|searchArg
parameter_list|,
name|NodeSet
name|nodes
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
index|[]
name|terms
decl_stmt|;
try|try
block|{
name|terms
operator|=
name|getSearchTerms
argument_list|(
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
name|this
argument_list|,
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
name|terms
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
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
condition|)
name|result
operator|.
name|append
argument_list|(
literal|"&= "
argument_list|)
expr_stmt|;
else|else
name|result
operator|.
name|append
argument_list|(
literal|" |= "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|searchTerm
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|path
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|"&= "
argument_list|)
expr_stmt|;
else|else
name|dumper
operator|.
name|display
argument_list|(
literal|" |= "
argument_list|)
expr_stmt|;
name|searchTerm
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|path
operator|.
name|getDependencies
argument_list|()
return|;
block|}
specifier|protected
name|String
index|[]
name|getSearchTerms
parameter_list|(
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
name|String
index|[]
name|terms
init|=
operator|new
name|String
index|[
name|tokens
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
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
return|;
block|}
specifier|protected
name|NodeSet
name|processQuery
parameter_list|(
name|String
index|[]
name|terms
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|)
throws|throws
name|XPathException
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
if|if
condition|(
name|terms
operator|.
name|length
operator|==
literal|0
condition|)
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
name|NodeSet
index|[]
name|hits
init|=
name|getMatches
argument_list|(
name|contextSet
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|contextSet
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
name|contextQName
argument_list|,
name|terms
argument_list|)
decl_stmt|;
name|NodeSet
name|result
init|=
name|hits
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|k
init|=
literal|1
init|;
name|k
operator|<
name|hits
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|hits
index|[
name|k
index|]
operator|!=
literal|null
condition|)
name|result
operator|=
operator|(
name|type
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
condition|?
name|result
operator|.
name|deepIntersection
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
else|:
name|result
operator|.
name|union
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
else|else
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
block|}
specifier|protected
name|NodeSet
index|[]
name|getMatches
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|axis
parameter_list|,
name|QName
name|qname
parameter_list|,
name|String
index|[]
name|terms
parameter_list|)
throws|throws
name|XPathException
block|{
name|NodeSet
name|hits
index|[]
init|=
operator|new
name|NodeSet
index|[
name|terms
operator|.
name|length
index|]
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
index|[
name|k
index|]
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
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
name|axis
argument_list|,
name|qname
argument_list|,
name|terms
index|[
name|k
index|]
argument_list|,
name|DBBroker
operator|.
name|MATCH_EXACT
argument_list|)
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
name|path
operator|.
name|setContextDocSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#resetState() 	 */
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
name|path
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|searchTerm
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|postOptimization
condition|)
block|{
name|preselectResult
operator|=
literal|null
expr_stmt|;
name|cached
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitFtExpression
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

