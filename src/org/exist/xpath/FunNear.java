begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id:  */
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|GlobCompiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|MalformedPatternException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|PatternCompiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|PatternMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|regex
operator|.
name|Perl5Matcher
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
name|ArraySet
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

begin_comment
comment|/**  *  near() function.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    July 31, 2002  */
end_comment

begin_class
specifier|public
class|class
name|FunNear
extends|extends
name|FunContains
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FunNear
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|max_distance
init|=
literal|1
decl_stmt|;
specifier|private
name|PatternCompiler
name|globCompiler
init|=
operator|new
name|GlobCompiler
argument_list|()
decl_stmt|;
specifier|public
name|FunNear
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|Constants
operator|.
name|FULLTEXT_AND
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FunNear
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|PathExpr
name|path
parameter_list|,
name|String
name|arg
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
name|path
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
specifier|public
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
block|{
name|NodeSet
name|nodes
init|=
operator|(
name|NodeSet
operator|)
name|path
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
name|contextNode
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
if|if
condition|(
name|hits
operator|==
literal|null
condition|)
name|processQuery
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|long
name|pid
decl_stmt|;
name|NodeProxy
name|current
decl_stmt|;
name|NodeProxy
name|parent
decl_stmt|;
name|NodeSet
name|temp
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|temp
operator|=
operator|new
name|ArraySet
argument_list|(
literal|100
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|hits
index|[
name|j
index|]
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
name|j
index|]
index|[
name|k
index|]
operator|==
literal|null
condition|)
continue|continue;
for|for
control|(
name|Iterator
name|i
init|=
name|hits
index|[
name|j
index|]
index|[
name|k
index|]
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
name|parent
operator|=
name|nodes
operator|.
name|parentWithChild
argument_list|(
name|current
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|temp
operator|.
name|contains
argument_list|(
name|parent
argument_list|)
condition|)
block|{
name|parent
operator|.
name|addMatches
argument_list|(
name|current
operator|.
name|matches
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|addMatches
argument_list|(
name|current
operator|.
name|matches
argument_list|)
expr_stmt|;
name|temp
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|hits
index|[
name|j
index|]
index|[
literal|0
index|]
operator|=
name|temp
expr_stmt|;
block|}
name|NodeSet
name|t0
init|=
literal|null
decl_stmt|;
name|NodeSet
name|t1
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|hits
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|t1
operator|=
name|hits
index|[
name|j
index|]
index|[
literal|0
index|]
expr_stmt|;
if|if
condition|(
name|t0
operator|==
literal|null
condition|)
name|t0
operator|=
name|t1
expr_stmt|;
else|else
name|t0
operator|=
name|t0
operator|.
name|intersection
argument_list|(
name|t1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|t0
operator|==
literal|null
condition|)
block|{
name|t0
operator|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|t0
argument_list|)
return|;
block|}
comment|// generate list of search term patterns
name|Pattern
name|terms
index|[]
init|=
operator|new
name|Pattern
index|[
name|containsExpr
operator|.
name|size
argument_list|()
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
name|terms
operator|.
name|length
condition|;
name|i
operator|++
control|)
try|try
block|{
name|terms
index|[
name|i
index|]
operator|=
name|globCompiler
operator|.
name|compile
argument_list|(
name|containsExpr
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|GlobCompiler
operator|.
name|CASE_INSENSITIVE_MASK
operator||
name|GlobCompiler
operator|.
name|QUESTION_MATCHES_ZERO_OR_ONE_MASK
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedPatternException
name|e
parameter_list|)
block|{
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
operator|new
name|ValueNodeSet
argument_list|(
name|NodeSet
operator|.
name|EMPTY_SET
argument_list|)
return|;
block|}
comment|// walk through hits and calculate term-distances
name|String
name|value
decl_stmt|;
name|Pattern
name|term
decl_stmt|;
name|String
name|word
decl_stmt|;
name|TextToken
name|token
decl_stmt|;
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|PatternMatcher
name|matcher
init|=
operator|new
name|Perl5Matcher
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
name|Tokenizer
name|tok
init|=
name|broker
operator|.
name|getTextEngine
argument_list|()
operator|.
name|getTokenizer
argument_list|()
decl_stmt|;
name|int
name|j
decl_stmt|;
name|int
name|distance
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|t0
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
name|value
operator|=
name|broker
operator|.
name|getNodeValue
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|tok
operator|.
name|setText
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|j
operator|=
literal|0
expr_stmt|;
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
name|distance
operator|=
operator|-
literal|1
expr_stmt|;
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
name|word
operator|=
name|token
operator|.
name|getText
argument_list|()
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
if|if
condition|(
name|distance
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
name|distance
operator|=
operator|-
literal|1
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|(
name|word
argument_list|,
name|term
argument_list|)
condition|)
block|{
name|distance
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
name|result
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
block|}
if|else if
condition|(
name|j
operator|>
literal|0
operator|&&
name|matcher
operator|.
name|matches
argument_list|(
name|word
argument_list|,
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
literal|0
expr_stmt|;
name|term
operator|=
name|terms
index|[
name|j
index|]
expr_stmt|;
name|distance
operator|=
operator|-
literal|1
expr_stmt|;
continue|continue;
block|}
if|else if
condition|(
operator|-
literal|1
operator|<
name|distance
condition|)
operator|++
name|distance
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
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
literal|"near("
argument_list|)
expr_stmt|;
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
literal|", "
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|containsExpr
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
name|buf
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
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
comment|/** 	 *  Sets the distance attribute of the FunNear object 	 * 	 *@param  distance  The new distance value 	 */
specifier|public
name|void
name|setDistance
parameter_list|(
name|int
name|distance
parameter_list|)
block|{
name|max_distance
operator|=
name|distance
expr_stmt|;
block|}
block|}
end_class

end_unit

