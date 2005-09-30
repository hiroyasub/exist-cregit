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

begin_comment
comment|/**  * Used to track fulltext matches throughout the query.  *   * {@link org.exist.storage.TextSearchEngine} will add a  * match object to every {@link org.exist.dom.NodeProxy}  * that triggered a fulltext match for every term matched. The   * Match object contains the nodeId of the text node that triggered the  * match, the string value of the matching term and a frequency count,  * indicating the frequency of the matching term string within the corresponding  * single text node.  *   * All path operations copy existing match objects, i.e. the match objects  * are copied to the selected descendant or child nodes. This means that  * every NodeProxy being the direct or indirect result of a fulltext  * selection will have one or more match objects, indicating which text nodes  * among its descendant nodes contained a fulltext match.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Match
implements|implements
name|Comparable
block|{
specifier|public
specifier|final
specifier|static
class|class
name|Offset
implements|implements
name|Comparable
block|{
specifier|private
name|int
name|offset
decl_stmt|;
specifier|private
name|int
name|length
decl_stmt|;
specifier|private
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
name|Object
name|other
parameter_list|)
block|{
specifier|final
name|int
name|otherOffset
init|=
operator|(
operator|(
name|Offset
operator|)
name|other
operator|)
operator|.
name|offset
decl_stmt|;
return|return
name|offset
operator|==
name|otherOffset
condition|?
literal|0
else|:
operator|(
name|offset
operator|>
name|otherOffset
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
return|;
block|}
block|}
specifier|private
name|long
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
name|prevMatch
init|=
literal|null
decl_stmt|;
specifier|public
name|Match
parameter_list|(
name|long
name|nodeId
parameter_list|,
name|String
name|matchTerm
parameter_list|)
block|{
name|this
argument_list|(
name|nodeId
argument_list|,
name|matchTerm
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Match
parameter_list|(
name|long
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
specifier|public
name|Match
parameter_list|(
name|Match
name|match
parameter_list|)
block|{
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
name|long
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
return|return
operator|(
operator|(
name|Match
operator|)
name|other
operator|)
operator|.
name|matchTerm
operator|.
name|equals
argument_list|(
name|matchTerm
argument_list|)
operator|&&
operator|(
operator|(
name|Match
operator|)
name|other
operator|)
operator|.
name|nodeId
operator|==
name|nodeId
return|;
block|}
comment|/** 	 * Used to sort matches. Terms are compared by their string  	 * length to have the longest string first. 	 *  	 * @see java.lang.Comparable#compareTo(java.lang.Object) 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Match
name|other
init|=
operator|(
name|Match
operator|)
name|o
decl_stmt|;
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
block|}
end_class

end_unit

