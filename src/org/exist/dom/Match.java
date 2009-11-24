begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org)  *   *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

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
name|xquery
operator|.
name|Constants
import|;
end_import

begin_comment
comment|/**  * Used to track fulltext matches throughout the query.  *   * {@link org.exist.storage.TextSearchEngine} will add a  * match object to every {@link org.exist.dom.NodeProxy}  * that triggered a fulltext match for every term matched. The   * Match object contains the nodeId of the text node that triggered the  * match, the string value of the matching term and a frequency count,  * indicating the frequency of the matching term string within the corresponding  * single text node.  *   * All path operations copy existing match objects, i.e. the match objects  * are copied to the selected descendant or child nodes. This means that  * every NodeProxy being the direct or indirect result of a fulltext  * selection will have one or more match objects, indicating which text nodes  * among its descendant nodes contained a fulltext match.  *   * @author wolf  */
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
specifier|final
specifier|static
class|class
name|Offset
implements|implements
name|Comparable
argument_list|<
name|Offset
argument_list|>
block|{
specifier|private
name|int
name|offset
decl_stmt|;
specifier|private
name|int
name|length
decl_stmt|;
specifier|public
name|Offset
parameter_list|(
name|int
name|offset
parameter_list|,
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
name|void
name|setOffset
parameter_list|(
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
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
specifier|public
name|int
name|compareTo
parameter_list|(
name|Offset
name|other
parameter_list|)
block|{
specifier|final
name|int
name|otherOffset
init|=
name|other
operator|.
name|offset
decl_stmt|;
return|return
name|offset
operator|==
name|otherOffset
condition|?
name|Constants
operator|.
name|EQUAL
else|:
operator|(
name|offset
operator|<
name|otherOffset
condition|?
name|Constants
operator|.
name|INFERIOR
else|:
name|Constants
operator|.
name|SUPERIOR
operator|)
return|;
block|}
block|}
specifier|private
name|int
name|context
decl_stmt|;
specifier|protected
name|NodeId
name|nodeId
decl_stmt|;
specifier|private
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
name|int
name|contextId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
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
name|int
name|contextId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|String
name|matchTerm
parameter_list|,
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
name|String
name|getMatchTerm
parameter_list|()
block|{
return|return
name|matchTerm
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
name|int
name|contextId
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
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
name|int
name|offset
parameter_list|,
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
specifier|public
name|Offset
name|getOffset
parameter_list|(
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
name|Match
name|isAfter
parameter_list|(
name|Match
name|other
parameter_list|)
block|{
name|Match
name|m
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
if|if
condition|(
name|other
operator|.
name|offsets
index|[
name|j
index|]
operator|>
name|offsets
index|[
name|i
index|]
operator|&&
name|other
operator|.
name|offsets
index|[
name|j
index|]
operator|<=
name|offsets
index|[
name|i
index|]
operator|+
name|lengths
index|[
name|i
index|]
condition|)
block|{
if|if
condition|(
name|m
operator|==
literal|null
condition|)
name|m
operator|=
name|createInstance
argument_list|(
name|context
argument_list|,
name|nodeId
argument_list|,
name|matchTerm
operator|+
name|other
operator|.
name|matchTerm
argument_list|)
expr_stmt|;
name|m
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
name|other
operator|.
name|lengths
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|m
return|;
block|}
comment|/**      * Return true if there's a match starting at the given      * character position.      *      * @param pos the position      * @return true if a match starts at the given position      */
specifier|public
name|boolean
name|hasMatchAt
parameter_list|(
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
return|return
literal|true
return|;
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
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|mergeOffsets
parameter_list|(
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
name|boolean
name|equals
parameter_list|(
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
return|return
literal|false
return|;
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
return|return
literal|true
return|;
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
comment|/** 	 * Used to sort matches. Terms are compared by their string  	 * length to have the longest string first. 	 *  	 * @see java.lang.Comparable#compareTo(java.lang.Object) 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
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

