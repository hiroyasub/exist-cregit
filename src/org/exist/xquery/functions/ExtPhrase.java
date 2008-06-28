begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Vector
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
name|Match
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
name|numbering
operator|.
name|NodeId
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
name|Sequence
import|;
end_import

begin_comment
comment|/**  *  phrase() function : search an exact phrase in a NodeSet  *  *@author     Bruno Chatel<bcha@chadocs.com> (March 30, 2005)  */
end_comment

begin_class
specifier|public
class|class
name|ExtPhrase
extends|extends
name|ExtFulltext
block|{
comment|/** 	 *  	 * @param context 	 */
specifier|public
name|ExtPhrase
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
comment|/**      *       * @param searchArg      * @param nodes       */
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
name|getASTNode
argument_list|()
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
comment|/** 	 *  	 * @param context 	 * @param result 	 */
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
name|TextToken
name|token
decl_stmt|;
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
comment|// define search phrase for matches
name|String
name|matchTerm
init|=
literal|""
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
name|matchTerm
operator|=
name|matchTerm
operator|+
name|terms
index|[
name|k
index|]
expr_stmt|;
if|if
condition|(
name|k
operator|!=
name|terms
operator|.
name|length
operator|-
literal|1
condition|)
name|matchTerm
operator|=
name|matchTerm
operator|+
literal|"\\W*"
expr_stmt|;
block|}
comment|// iterate on results
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
name|Vector
name|matchGid
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
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
comment|// get first match
name|Match
name|nextMatch
init|=
name|current
operator|.
name|getMatches
argument_list|()
decl_stmt|;
comment|// remove previously found matches on current
name|current
operator|.
name|setMatches
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// iterate on attach matches, with unicity of related nodeproxy gid
name|String
name|term
decl_stmt|;
while|while
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
name|NodeId
name|nodeId
init|=
name|nextMatch
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
comment|// if current gid has not been previously processed
if|if
condition|(
operator|!
name|matchGid
operator|.
name|contains
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
name|NodeProxy
name|mcurrent
init|=
operator|new
name|NodeProxy
argument_list|(
name|current
operator|.
name|getDocument
argument_list|()
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
name|Match
name|match
init|=
literal|null
decl_stmt|;
name|int
name|firstOffset
init|=
operator|-
literal|1
decl_stmt|;
comment|// add it in gid array
name|matchGid
operator|.
name|add
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|mcurrent
operator|.
name|getNodeValue
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
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
else|else
break|break;
name|int
name|frequency
init|=
literal|0
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
name|word
operator|.
name|equalsIgnoreCase
argument_list|(
name|term
argument_list|)
condition|)
block|{
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
name|match
operator|==
literal|null
condition|)
name|match
operator|=
name|nextMatch
operator|.
name|createInstance
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|nodeId
argument_list|,
name|matchTerm
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstOffset
operator|<
literal|0
condition|)
name|firstOffset
operator|=
name|token
operator|.
name|startOffset
argument_list|()
expr_stmt|;
name|match
operator|.
name|addOffset
argument_list|(
name|firstOffset
argument_list|,
name|token
operator|.
name|endOffset
argument_list|()
operator|-
name|firstOffset
argument_list|)
expr_stmt|;
name|frequency
operator|++
expr_stmt|;
comment|// start again on fist term
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
continue|continue;
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
if|if
condition|(
name|firstOffset
operator|<
literal|0
condition|)
name|firstOffset
operator|=
name|token
operator|.
name|startOffset
argument_list|()
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
name|firstOffset
operator|=
name|token
operator|.
name|startOffset
argument_list|()
expr_stmt|;
continue|continue;
block|}
else|else
block|{
comment|//	reset
name|j
operator|=
literal|0
expr_stmt|;
name|firstOffset
operator|=
operator|-
literal|1
expr_stmt|;
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
block|}
block|}
comment|// if phrase found
if|if
condition|(
name|frequency
operator|!=
literal|0
condition|)
block|{
comment|// add new match to current
name|current
operator|.
name|addMatch
argument_list|(
name|match
argument_list|)
expr_stmt|;
comment|// add current to result
name|r
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
comment|// reset frequency
name|frequency
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|// process next match
name|nextMatch
operator|=
name|nextMatch
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
block|}
comment|//		LOG.debug("found " + r.getLength());
return|return
name|r
return|;
block|}
comment|/** 	 *  	 * @param context 	 * @param result 	 * @return 	 */
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
comment|// walk through hits
name|ExtArrayNodeSet
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
name|Matcher
name|matcher
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
name|Match
name|nextMatch
decl_stmt|;
name|Vector
name|matchGid
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
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
comment|// get first match
name|nextMatch
operator|=
name|current
operator|.
name|getMatches
argument_list|()
expr_stmt|;
comment|// remove previously found matches on current
name|current
operator|.
name|setMatches
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// iterate on attach matches, with unicity of related nodeproxy gid
while|while
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
name|Hashtable
name|matchTable
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|nextMatch
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
comment|// if current gid has not been previously processed
if|if
condition|(
operator|!
name|matchGid
operator|.
name|contains
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
name|NodeProxy
name|mcurrent
init|=
operator|new
name|NodeProxy
argument_list|(
name|current
operator|.
name|getDocument
argument_list|()
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
comment|// add it in gid array
name|matchGid
operator|.
name|add
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|mcurrent
operator|.
name|getNodeValue
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
break|break;
name|String
name|matchTerm
init|=
literal|null
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
name|j
operator|++
expr_stmt|;
if|if
condition|(
name|matchTerm
operator|==
literal|null
condition|)
name|matchTerm
operator|=
name|word
expr_stmt|;
else|else
name|matchTerm
operator|=
name|matchTerm
operator|+
literal|"\\W*"
operator|+
name|word
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
name|matchTable
operator|.
name|containsKey
argument_list|(
name|matchTerm
argument_list|)
condition|)
block|{
comment|// previously found matchTerm
name|Match
name|match
init|=
operator|(
name|Match
operator|)
operator|(
name|matchTable
operator|.
name|get
argument_list|(
name|matchTerm
argument_list|)
operator|)
decl_stmt|;
name|match
operator|.
name|addOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|matchTerm
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Match
name|match
init|=
name|nextMatch
operator|.
name|createInstance
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|nodeId
argument_list|,
name|matchTerm
argument_list|)
decl_stmt|;
name|match
operator|.
name|addOffset
argument_list|(
name|token
operator|.
name|startOffset
argument_list|()
argument_list|,
name|matchTerm
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|matchTable
operator|.
name|put
argument_list|(
name|matchTerm
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
comment|// start again on fist term
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
name|matchTerm
operator|=
literal|null
expr_stmt|;
continue|continue;
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
name|matchTerm
operator|=
name|word
expr_stmt|;
continue|continue;
block|}
else|else
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
name|matchTerm
operator|=
literal|null
expr_stmt|;
continue|continue;
block|}
block|}
comment|// one or more match found
if|if
condition|(
name|matchTable
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|Enumeration
name|eMatch
init|=
name|matchTable
operator|.
name|elements
argument_list|()
decl_stmt|;
while|while
condition|(
name|eMatch
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|Match
name|match
init|=
operator|(
name|Match
operator|)
operator|(
name|eMatch
operator|.
name|nextElement
argument_list|()
operator|)
decl_stmt|;
name|current
operator|.
name|addMatch
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
comment|// add current to result
name|r
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
comment|// process next match
name|nextMatch
operator|=
name|nextMatch
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.functions.ExtFulltext#dump(org.exist.xquery.util.ExpressionDumper)      */
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
literal|"phrase("
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
literal|"phrase("
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
block|}
end_class

end_unit

