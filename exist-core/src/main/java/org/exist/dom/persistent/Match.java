begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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

begin_comment
comment|/**  * Used to track matches throughout the query.  *<p/>  * Index may add a match object to every {@link org.exist.dom.persistent.NodeProxy}  * that triggered a match for every term matched. The  * Match object contains the nodeId of the text node that triggered the  * match, the string value of the matching term and a frequency count,  * indicating the frequency of the matching term string within the corresponding  * single text node.  *<p/>  * All path operations copy existing match objects, i.e. the match objects  * are copied to the selected descendant or child nodes. This means that  * every NodeProxy being the direct or indirect result of a  * selection will have one or more match objects, indicating which text nodes  * among its descendant nodes contained a match.  *  * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Match
implements|implements
name|Comparable
argument_list|<
name|Match
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
class|class
name|Offset
implements|implements
name|Comparable
argument_list|<
name|Offset
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
specifier|public
name|Offset
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|Offset
name|other
parameter_list|)
block|{
return|return
name|this
operator|.
name|offset
operator|-
name|other
operator|.
name|offset
return|;
block|}
specifier|public
name|boolean
name|overlaps
parameter_list|(
specifier|final
name|Offset
name|other
parameter_list|)
block|{
return|return
operator|(
name|other
operator|.
name|offset
operator|>=
name|offset
operator|&&
name|other
operator|.
name|offset
operator|<
name|offset
operator|+
name|length
operator|)
operator|||
operator|(
name|offset
operator|>=
name|other
operator|.
name|offset
operator|&&
name|offset
operator|<
name|other
operator|.
name|offset
operator|+
name|other
operator|.
name|length
operator|)
return|;
block|}
block|}
specifier|private
specifier|final
name|int
name|context
decl_stmt|;
specifier|protected
specifier|final
name|NodeId
name|nodeId
decl_stmt|;
specifier|private
specifier|final
name|String
name|matchTerm
decl_stmt|;
specifier|private
name|int
index|[]
name|offsets
decl_stmt|;
specifier|private
name|int
index|[]
name|lengths
decl_stmt|;
specifier|private
name|int
name|currentOffset
init|=
literal|0
decl_stmt|;
specifier|protected
name|Match
name|nextMatch
init|=
literal|null
decl_stmt|;
specifier|protected
name|Match
parameter_list|(
specifier|final
name|int
name|contextId
parameter_list|,
specifier|final
name|NodeId
name|nodeId
parameter_list|,
specifier|final
name|String
name|matchTerm
parameter_list|)
block|{
name|this
argument_list|(
name|contextId
argument_list|,
name|nodeId
argument_list|,
name|matchTerm
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Match
parameter_list|(
specifier|final
name|int
name|contextId
parameter_list|,
specifier|final
name|NodeId
name|nodeId
parameter_list|,
specifier|final
name|String
name|matchTerm
parameter_list|,
specifier|final
name|int
name|frequency
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|contextId
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|nodeId
expr_stmt|;
name|this
operator|.
name|matchTerm
operator|=
name|matchTerm
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
operator|new
name|int
index|[
name|frequency
index|]
expr_stmt|;
name|this
operator|.
name|lengths
operator|=
operator|new
name|int
index|[
name|frequency
index|]
expr_stmt|;
block|}
specifier|protected
name|Match
parameter_list|(
specifier|final
name|Match
name|match
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|match
operator|.
name|context
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|match
operator|.
name|nodeId
expr_stmt|;
name|this
operator|.
name|matchTerm
operator|=
name|match
operator|.
name|matchTerm
expr_stmt|;
name|this
operator|.
name|offsets
operator|=
name|match
operator|.
name|offsets
expr_stmt|;
name|this
operator|.
name|lengths
operator|=
name|match
operator|.
name|lengths
expr_stmt|;
name|this
operator|.
name|currentOffset
operator|=
name|match
operator|.
name|currentOffset
expr_stmt|;
block|}
specifier|public
name|NodeId
name|getNodeId
parameter_list|()
block|{
return|return
name|nodeId
return|;
block|}
specifier|public
name|int
name|getFrequency
parameter_list|()
block|{
return|return
name|currentOffset
return|;
block|}
specifier|public
name|int
name|getContextId
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
specifier|abstract
name|Match
name|createInstance
parameter_list|(
specifier|final
name|int
name|contextId
parameter_list|,
specifier|final
name|NodeId
name|nodeId
parameter_list|,
specifier|final
name|String
name|matchTerm
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|Match
name|newCopy
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|String
name|getIndexId
parameter_list|()
function_decl|;
specifier|public
name|void
name|addOffset
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|currentOffset
operator|==
name|offsets
operator|.
name|length
condition|)
block|{
specifier|final
name|int
name|noffsets
index|[]
init|=
operator|new
name|int
index|[
name|currentOffset
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|offsets
argument_list|,
literal|0
argument_list|,
name|noffsets
argument_list|,
literal|0
argument_list|,
name|currentOffset
argument_list|)
expr_stmt|;
name|offsets
operator|=
name|noffsets
expr_stmt|;
specifier|final
name|int
name|nlengths
index|[]
init|=
operator|new
name|int
index|[
name|currentOffset
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|lengths
argument_list|,
literal|0
argument_list|,
name|nlengths
argument_list|,
literal|0
argument_list|,
name|currentOffset
argument_list|)
expr_stmt|;
name|lengths
operator|=
name|nlengths
expr_stmt|;
block|}
name|offsets
index|[
name|currentOffset
index|]
operator|=
name|offset
expr_stmt|;
name|lengths
index|[
name|currentOffset
operator|++
index|]
operator|=
name|length
expr_stmt|;
block|}
specifier|private
name|void
name|addOffset
parameter_list|(
specifier|final
name|Offset
name|offset
parameter_list|)
block|{
name|addOffset
argument_list|(
name|offset
operator|.
name|offset
argument_list|,
name|offset
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addOffsets
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|Offset
argument_list|>
name|offsets
parameter_list|)
block|{
name|offsets
operator|.
name|forEach
argument_list|(
name|this
operator|::
name|addOffset
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Offset
name|getOffset
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
return|return
operator|new
name|Offset
argument_list|(
name|offsets
index|[
name|pos
index|]
argument_list|,
name|lengths
index|[
name|pos
index|]
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Offset
argument_list|>
name|getOffsets
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Offset
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|currentOffset
argument_list|)
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
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|add
argument_list|(
name|getOffset
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * Constructs a match starting with this match and continued by the other match if possible      *      * @param other a match continuing this match      * @return a match starting with this match and continued by the other match      * if such a match exists or null if no continuous match found      */
specifier|public
name|Match
name|continuedBy
parameter_list|(
specifier|final
name|Match
name|other
parameter_list|)
block|{
return|return
name|followedBy
argument_list|(
name|other
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**      * Constructs a match starting with this match and followed by the other match if possible      *      * @param other       a match following this match      * @param minDistance the minimum distance between this and the other match      * @param maxDistance the maximum distance between this and the other match      * @return a match starting with this match and followed by      * the other match in the specified distance range if such      * a match exists or null if no such match found      */
specifier|public
name|Match
name|followedBy
parameter_list|(
specifier|final
name|Match
name|other
parameter_list|,
specifier|final
name|int
name|minDistance
parameter_list|,
specifier|final
name|int
name|maxDistance
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Offset
argument_list|>
name|newMatchOffsets
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
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
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|other
operator|.
name|currentOffset
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|distance
init|=
name|other
operator|.
name|offsets
index|[
name|j
index|]
operator|-
operator|(
name|offsets
index|[
name|i
index|]
operator|+
name|lengths
index|[
name|i
index|]
operator|)
decl_stmt|;
if|if
condition|(
name|distance
operator|>=
name|minDistance
operator|&&
name|distance
operator|<=
name|maxDistance
condition|)
block|{
name|newMatchOffsets
operator|.
name|add
argument_list|(
operator|new
name|Offset
argument_list|(
name|offsets
index|[
name|i
index|]
argument_list|,
name|lengths
index|[
name|i
index|]
operator|+
name|distance
operator|+
name|other
operator|.
name|lengths
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|newMatchOffsets
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|int
name|wildCardSize
init|=
name|newMatchOffsets
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|length
operator|-
name|matchTerm
operator|.
name|length
argument_list|()
operator|-
name|other
operator|.
name|matchTerm
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|matched
init|=
operator|new
name|StringBuilder
argument_list|(
name|matchTerm
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|wildCardSize
condition|;
name|ii
operator|++
control|)
block|{
name|matched
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
name|matched
operator|.
name|append
argument_list|(
name|other
operator|.
name|matchTerm
argument_list|)
expr_stmt|;
specifier|final
name|Match
name|result
init|=
name|createInstance
argument_list|(
name|context
argument_list|,
name|nodeId
argument_list|,
name|matched
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|result
operator|.
name|addOffsets
argument_list|(
name|newMatchOffsets
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Expand the match backwards by at least minExpand up to maxExpand characters.      * The match is expanded as much as possible.      *      * @param minExpand The minimum number of characters to expand this match by      * @param maxExpand The maximum number of characters to expand this match by      * @return The expanded match if possible, or null if no offset is far enough from the start.      */
specifier|public
name|Match
name|expandBackward
parameter_list|(
specifier|final
name|int
name|minExpand
parameter_list|,
specifier|final
name|int
name|maxExpand
parameter_list|)
block|{
name|Match
name|result
init|=
literal|null
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
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|offsets
index|[
name|i
index|]
operator|-
name|minExpand
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
specifier|final
name|StringBuilder
name|matched
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|minExpand
condition|;
name|ii
operator|++
control|)
block|{
name|matched
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
name|matched
operator|.
name|append
argument_list|(
name|matchTerm
argument_list|)
expr_stmt|;
name|result
operator|=
name|createInstance
argument_list|(
name|context
argument_list|,
name|nodeId
argument_list|,
name|matched
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|expand
init|=
name|Math
operator|.
name|min
argument_list|(
name|offsets
index|[
name|i
index|]
argument_list|,
name|maxExpand
argument_list|)
decl_stmt|;
name|result
operator|.
name|addOffset
argument_list|(
name|offsets
index|[
name|i
index|]
operator|-
name|expand
argument_list|,
name|lengths
index|[
name|i
index|]
operator|+
name|expand
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
comment|/**      * Expand the match forward by at least minExpand up to maxExpand characters.      * The match is expanded as much as possible.      *      * @param minExpand  The minimum number of characters to expand this match by      * @param maxExpand  The maximum number of characters to expand this match by      * @param dataLength The length of the valued of the node, limiting the expansion      * @return The expanded match if possible, or null if no offset is far enough from the end.      */
specifier|public
name|Match
name|expandForward
parameter_list|(
specifier|final
name|int
name|minExpand
parameter_list|,
specifier|final
name|int
name|maxExpand
parameter_list|,
specifier|final
name|int
name|dataLength
parameter_list|)
block|{
name|Match
name|result
init|=
literal|null
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
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|offsets
index|[
name|i
index|]
operator|+
name|lengths
index|[
name|i
index|]
operator|+
name|minExpand
operator|<=
name|dataLength
condition|)
block|{
specifier|final
name|int
name|expand
init|=
name|Math
operator|.
name|min
argument_list|(
name|dataLength
operator|-
name|offsets
index|[
name|i
index|]
operator|-
name|lengths
index|[
name|i
index|]
argument_list|,
name|maxExpand
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
specifier|final
name|StringBuilder
name|matched
init|=
operator|new
name|StringBuilder
argument_list|(
name|matchTerm
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|<
name|expand
condition|;
name|ii
operator|++
control|)
block|{
name|matched
operator|.
name|append
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
name|createInstance
argument_list|(
name|context
argument_list|,
name|nodeId
argument_list|,
name|matched
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|addOffset
argument_list|(
name|offsets
index|[
name|i
index|]
argument_list|,
name|lengths
index|[
name|i
index|]
operator|+
name|expand
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Match
name|filterOffsets
parameter_list|(
specifier|final
name|Predicate
argument_list|<
name|Offset
argument_list|>
name|predicate
parameter_list|)
block|{
specifier|final
name|Match
name|result
init|=
name|createInstance
argument_list|(
name|context
argument_list|,
name|nodeId
argument_list|,
name|matchTerm
argument_list|)
decl_stmt|;
name|getOffsets
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|predicate
argument_list|)
operator|.
name|forEach
argument_list|(
name|result
operator|::
name|addOffset
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|currentOffset
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|result
return|;
block|}
block|}
comment|/**      * Creates a match containing only those offsets starting at the given position.      *      * @param pos Required offset      * @return a match containing only offsets starting at the given position,      * or null if no such offset exists.      */
specifier|public
name|Match
name|filterOffsetsStartingAt
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
return|return
name|filterOffsets
argument_list|(
name|offset
lambda|->
name|offset
operator|.
name|offset
operator|==
name|pos
argument_list|)
return|;
block|}
comment|/**      * Creates a match containing only those offsets ending at the given position.      *      * @param pos Required position of the end of the matches      * @return A match containing only offsets ending at the given position,      * or null if no such offset exists.      */
specifier|public
name|Match
name|filterOffsetsEndingAt
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
return|return
name|filterOffsets
argument_list|(
name|offset
lambda|->
name|offset
operator|.
name|offset
operator|+
name|offset
operator|.
name|length
operator|==
name|pos
argument_list|)
return|;
block|}
comment|/**      * Creates a match containing only non-overlapping offsets,      * preferring longer matches, and then matches from left to right.      *      * @return a match containing only non-overlapping offsets      */
specifier|public
name|Match
name|filterOutOverlappingOffsets
parameter_list|()
block|{
if|if
condition|(
name|currentOffset
operator|==
literal|0
condition|)
block|{
return|return
name|newCopy
argument_list|()
return|;
block|}
specifier|final
name|List
argument_list|<
name|Offset
argument_list|>
name|newMatchOffsets
init|=
name|getOffsets
argument_list|()
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|newMatchOffsets
argument_list|,
parameter_list|(
name|o1
parameter_list|,
name|o2
parameter_list|)
lambda|->
block|{
specifier|final
name|int
name|lengthDiff
init|=
name|o2
operator|.
name|length
operator|-
name|o1
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|lengthDiff
operator|!=
literal|0
condition|)
block|{
return|return
name|lengthDiff
return|;
block|}
else|else
block|{
return|return
name|o1
operator|.
name|offset
operator|-
name|o2
operator|.
name|offset
return|;
block|}
block|}
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Offset
argument_list|>
name|nonOverlappingMatchOffsets
init|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
decl_stmt|;
name|nonOverlappingMatchOffsets
operator|.
name|add
argument_list|(
name|newMatchOffsets
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Offset
name|o
range|:
name|newMatchOffsets
control|)
block|{
name|boolean
name|overlapsExistingOffset
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|Offset
name|eo
range|:
name|nonOverlappingMatchOffsets
control|)
block|{
if|if
condition|(
name|eo
operator|.
name|overlaps
argument_list|(
name|o
argument_list|)
condition|)
block|{
name|overlapsExistingOffset
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|overlapsExistingOffset
condition|)
block|{
name|nonOverlappingMatchOffsets
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Match
name|result
init|=
name|createInstance
argument_list|(
name|context
argument_list|,
name|nodeId
argument_list|,
name|matchTerm
argument_list|)
decl_stmt|;
name|result
operator|.
name|addOffsets
argument_list|(
name|nonOverlappingMatchOffsets
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Return true if there's a match starting at the given      * character position.      *      * @param pos the position      * @return true if a match starts at the given position      */
specifier|public
name|boolean
name|hasMatchAt
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|offsets
index|[
name|i
index|]
operator|==
name|pos
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**      * Returns true if the given position is within a match.      *      * @param pos the position      * @return true if the given position is within a match      */
specifier|public
name|boolean
name|hasMatchAround
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|offsets
index|[
name|i
index|]
operator|+
name|lengths
index|[
name|i
index|]
operator|>=
name|pos
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|mergeOffsets
parameter_list|(
specifier|final
name|Match
name|other
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|other
operator|.
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|hasMatchAt
argument_list|(
name|other
operator|.
name|offsets
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|addOffset
argument_list|(
name|other
operator|.
name|offsets
index|[
name|i
index|]
argument_list|,
name|other
operator|.
name|lengths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Match
name|getNextMatch
parameter_list|()
block|{
return|return
name|nextMatch
return|;
block|}
specifier|public
specifier|static
name|boolean
name|matchListEquals
parameter_list|(
specifier|final
name|Match
name|m1
parameter_list|,
specifier|final
name|Match
name|m2
parameter_list|)
block|{
name|Match
name|n1
init|=
name|m1
decl_stmt|;
name|Match
name|n2
init|=
name|m2
decl_stmt|;
while|while
condition|(
name|n1
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|n2
operator|==
literal|null
operator|||
name|n1
operator|!=
name|n2
condition|)
block|{
return|return
literal|false
return|;
block|}
name|n1
operator|=
name|n1
operator|.
name|nextMatch
expr_stmt|;
name|n2
operator|=
name|n2
operator|.
name|nextMatch
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|Match
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|Match
name|om
init|=
operator|(
name|Match
operator|)
name|other
decl_stmt|;
return|return
name|om
operator|.
name|matchTerm
operator|!=
literal|null
operator|&&
name|om
operator|.
name|matchTerm
operator|.
name|equals
argument_list|(
name|matchTerm
argument_list|)
operator|&&
name|om
operator|.
name|nodeId
operator|.
name|equals
argument_list|(
name|nodeId
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matchEquals
parameter_list|(
specifier|final
name|Match
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
operator|(
name|nodeId
operator|==
name|other
operator|.
name|nodeId
operator|||
name|nodeId
operator|.
name|equals
argument_list|(
name|other
operator|.
name|nodeId
argument_list|)
operator|)
operator|&&
name|matchTerm
operator|.
name|equals
argument_list|(
name|other
operator|.
name|matchTerm
argument_list|)
return|;
block|}
comment|/**      * Used to sort matches. Terms are compared by their string      * length to have the longest string first.      *      * @see java.lang.Comparable#compareTo(java.lang.Object)      */
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|Match
name|other
parameter_list|)
block|{
return|return
name|matchTerm
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|matchTerm
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|matchTerm
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|matchTerm
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|currentOffset
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" ["
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|offsets
index|[
name|i
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|lengths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|nextMatch
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
