begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2005 Wolfgang M. Meier  * wolfgang@exist-db.org  * http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation, Inc.,  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.  *  * $Id$  */
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
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
name|NativeTextEngine
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
name|util
operator|.
name|GlobToRegex
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
comment|/**  * text:near() function.  *   * @author Wolfgang Meier<wolfgang@exist-db.org> (July 31, 2002)  */
end_comment

begin_class
specifier|public
class|class
name|ExtNear
extends|extends
name|ExtFulltext
block|{
specifier|private
name|int
name|min_distance
init|=
literal|1
decl_stmt|;
specifier|private
name|int
name|max_distance
init|=
literal|1
decl_stmt|;
specifier|private
name|Expression
name|minDistance
init|=
literal|null
decl_stmt|;
specifier|private
name|Expression
name|maxDistance
init|=
literal|null
decl_stmt|;
specifier|public
name|ExtNear
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|FULLTEXT_AND
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.ExtFulltext#analyze(org.exist.xquery.AnalyzeContextInfo) 	 */
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
name|super
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxDistance
operator|!=
literal|null
condition|)
block|{
name|maxDistance
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|minDistance
operator|!=
literal|null
condition|)
block|{
name|minDistance
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|maxDistance
operator|!=
literal|null
condition|)
block|{
name|max_distance
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|maxDistance
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|minDistance
operator|!=
literal|null
condition|)
block|{
name|min_distance
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|minDistance
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
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
name|preselectResult
operator|.
name|deepIntersection
argument_list|(
name|hits
index|[
name|k
index|]
argument_list|)
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
if|if
condition|(
name|terms
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|boolean
name|hasWildcards
init|=
literal|false
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hasWildcards
operator||=
name|NativeTextEngine
operator|.
name|containsWildcards
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|preselectResult
operator|=
operator|(
name|NodeSet
operator|)
operator|(
name|hasWildcards
condition|?
name|patternMatch
argument_list|(
name|context
argument_list|,
name|terms
argument_list|,
name|preselectResult
argument_list|)
else|:
name|exactMatch
argument_list|(
name|context
argument_list|,
name|terms
argument_list|,
name|preselectResult
argument_list|)
operator|)
expr_stmt|;
block|}
return|return
name|preselectResult
return|;
block|}
specifier|public
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
if|if
condition|(
name|maxDistance
operator|!=
literal|null
condition|)
block|{
name|max_distance
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|maxDistance
operator|.
name|eval
argument_list|(
name|nodes
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|minDistance
operator|!=
literal|null
condition|)
block|{
name|min_distance
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|minDistance
operator|.
name|eval
argument_list|(
name|nodes
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
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
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
if|if
condition|(
name|terms
operator|.
name|length
operator|==
literal|1
condition|)
return|return
name|hits
return|;
name|boolean
name|hasWildcards
init|=
literal|false
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hasWildcards
operator||=
name|NativeTextEngine
operator|.
name|containsWildcards
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|hasWildcards
condition|?
name|patternMatch
argument_list|(
name|context
argument_list|,
name|terms
argument_list|,
name|hits
argument_list|)
else|:
name|exactMatch
argument_list|(
name|context
argument_list|,
name|terms
argument_list|,
name|hits
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|exactMatch
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
index|[]
name|terms
parameter_list|,
name|NodeSet
name|result
parameter_list|)
block|{
comment|// walk through hits and calculate term-distances
name|NodeSet
name|r
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
specifier|final
name|Tokenizer
name|tok
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
name|String
name|term
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|result
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
name|NodeProxy
name|current
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|current
operator|.
name|getNodeValueSeparated
argument_list|()
decl_stmt|;
name|tok
operator|.
name|setText
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|j
operator|<
name|terms
operator|.
name|length
condition|)
block|{
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
name|int
name|current_distance
init|=
operator|-
literal|1
decl_stmt|;
name|TextToken
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|word
init|=
name|token
operator|.
name|getText
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|current_distance
operator|>
name|max_distance
condition|)
block|{
comment|// reset
name|j
operator|=
literal|0
expr_stmt|;
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
name|current_distance
operator|=
operator|-
literal|1
expr_stmt|;
block|}
comment|// that else would cause some words to be ignored in the
comment|// matching
if|if
condition|(
name|word
operator|.
name|equalsIgnoreCase
argument_list|(
name|term
argument_list|)
condition|)
block|{
name|boolean
name|withIn
init|=
name|current_distance
operator|>=
name|min_distance
decl_stmt|;
name|current_distance
operator|=
literal|0
expr_stmt|;
name|j
operator|++
expr_stmt|;
if|if
condition|(
name|j
operator|==
name|terms
operator|.
name|length
condition|)
block|{
comment|// all terms found
if|if
condition|(
name|withIn
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|j
operator|>
literal|0
operator|&&
name|word
operator|.
name|equalsIgnoreCase
argument_list|(
name|terms
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
comment|// first search term found: start again
name|j
operator|=
literal|1
expr_stmt|;
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
name|current_distance
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
comment|// that else MAY cause the distance counts to be off by one
comment|// but i'm not sure
if|if
condition|(
operator|-
literal|1
operator|<
name|current_distance
condition|)
block|{
operator|++
name|current_distance
expr_stmt|;
block|}
block|}
block|}
comment|// LOG.debug("found " + r.getLength());
return|return
name|r
return|;
block|}
specifier|private
name|Sequence
name|patternMatch
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
index|[]
name|terms
parameter_list|,
name|NodeSet
name|result
parameter_list|)
block|{
comment|// generate list of search term patterns
name|Pattern
name|patterns
index|[]
init|=
operator|new
name|Pattern
index|[
name|terms
operator|.
name|length
index|]
decl_stmt|;
name|Matcher
name|matchers
index|[]
init|=
operator|new
name|Matcher
index|[
name|terms
operator|.
name|length
index|]
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
name|patterns
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|patterns
index|[
name|i
index|]
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|GlobToRegex
operator|.
name|globToRegexp
argument_list|(
name|terms
index|[
name|i
index|]
argument_list|)
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|UNICODE_CASE
argument_list|)
expr_stmt|;
name|matchers
index|[
name|i
index|]
operator|=
name|patterns
index|[
name|i
index|]
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
comment|//TODO : error ? -pb
name|LOG
operator|.
name|warn
argument_list|(
literal|"malformed pattern"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
comment|// walk through hits and calculate term-distances
name|ExtArrayNodeSet
name|r
init|=
operator|new
name|ExtArrayNodeSet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|Tokenizer
name|tok
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
name|Matcher
name|matcher
decl_stmt|;
name|TextToken
name|token
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|result
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
name|NodeProxy
name|current
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|current
operator|.
name|getNodeValueSeparated
argument_list|()
decl_stmt|;
name|tok
operator|.
name|setText
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|j
operator|<
name|patterns
operator|.
name|length
condition|)
block|{
comment|//Pattern term = patterns[j];
name|matcher
operator|=
name|matchers
index|[
name|j
index|]
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
name|int
name|current_distance
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|String
name|word
init|=
name|token
operator|.
name|getText
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|current_distance
operator|>
name|max_distance
condition|)
block|{
comment|// reset
name|j
operator|=
literal|0
expr_stmt|;
comment|//Pattern term = patterns[j];
name|matcher
operator|=
name|matchers
index|[
name|j
index|]
expr_stmt|;
name|current_distance
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|matcher
operator|.
name|reset
argument_list|(
name|word
argument_list|)
expr_stmt|;
name|matchers
index|[
literal|0
index|]
operator|.
name|reset
argument_list|(
name|word
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|boolean
name|withIn
init|=
name|current_distance
operator|>=
name|min_distance
condition|?
literal|true
else|:
literal|false
decl_stmt|;
name|current_distance
operator|=
literal|0
expr_stmt|;
name|j
operator|++
expr_stmt|;
if|if
condition|(
name|j
operator|==
name|patterns
operator|.
name|length
condition|)
block|{
comment|// all terms found
if|if
condition|(
name|withIn
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
else|else
block|{
comment|//Pattern term = patterns[j];
name|matcher
operator|=
name|matchers
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|j
operator|>
literal|0
operator|&&
name|matchers
index|[
literal|0
index|]
operator|.
name|matches
argument_list|()
condition|)
block|{
comment|// first search term found: start again
name|j
operator|=
literal|1
expr_stmt|;
comment|//Pattern term = patterns[j];
name|matcher
operator|=
name|matchers
index|[
name|j
index|]
expr_stmt|;
name|current_distance
operator|=
literal|0
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|-
literal|1
operator|<
name|current_distance
condition|)
block|{
operator|++
name|current_distance
expr_stmt|;
block|}
block|}
block|}
return|return
name|r
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.xquery.functions.ExtFulltext#dump(org.exist.xquery.util.ExpressionDumper) 	 */
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
literal|"near("
argument_list|)
expr_stmt|;
name|path
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|searchTerm
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
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
literal|"near("
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|searchTerm
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|setMaxDistance
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|maxDistance
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|void
name|setMinDistance
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|minDistance
operator|=
name|expr
expr_stmt|;
block|}
block|}
end_class

end_unit

