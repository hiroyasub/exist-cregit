begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001/2002 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
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

begin_comment
comment|/**  *  xpath-library function: match-keywords(XPATH, arg1, arg2 ...)  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    7. Oktober 2002  */
end_comment

begin_class
specifier|public
class|class
name|FunKeywordMatch
extends|extends
name|Function
block|{
comment|/**  Constructor for the FunKeywordMatch object */
specifier|public
name|FunKeywordMatch
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|"match-keywords"
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  docs     Description of the Parameter      *@param  context  Description of the Parameter      *@param  node     Description of the Parameter      *@return          Description of the Return Value      */
specifier|public
name|Value
name|eval
parameter_list|(
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
name|Expression
name|path
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|docs
argument_list|,
name|context
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
name|String
index|[]
name|terms
init|=
operator|new
name|String
index|[
name|getArgumentCount
argument_list|()
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
name|terms
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|eval
argument_list|(
name|docs
argument_list|,
name|context
argument_list|,
name|node
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|NodeSet
index|[]
index|[]
name|hits
init|=
operator|new
name|NodeSet
index|[
name|terms
operator|.
name|length
index|]
index|[]
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
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|terms
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|String
name|t
index|[]
init|=
block|{
name|terms
index|[
name|j
index|]
block|}
decl_stmt|;
name|hits
index|[
name|j
index|]
operator|=
name|broker
operator|.
name|getNodesContaining
argument_list|(
name|docs
argument_list|,
name|t
argument_list|,
name|DBBroker
operator|.
name|MATCH_REGEXP
argument_list|)
expr_stmt|;
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
operator|&&
operator|(
operator|!
name|temp
operator|.
name|contains
argument_list|(
name|current
operator|.
name|doc
argument_list|,
name|parent
operator|.
name|gid
argument_list|)
operator|)
condition|)
name|temp
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
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
comment|// merge results
name|NodeSet
name|t0
init|=
literal|null
decl_stmt|;
comment|// merge results
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
comment|/*              *  for(int k = 1; k< hits[j].length; k++)              *  t1 = t1.union(hits[j][k]);              */
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
operator|(
name|getOperatorType
argument_list|()
operator|==
name|Constants
operator|.
name|FULLTEXT_AND
operator|)
condition|?
name|t0
operator|.
name|intersection
argument_list|(
name|t1
argument_list|)
else|:
name|t0
operator|.
name|union
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
comment|/**      *  Gets the operatorType attribute of the FunKeywordMatch object      *      *@return    The operatorType value      */
specifier|protected
name|int
name|getOperatorType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|FULLTEXT_AND
return|;
block|}
comment|/**      *  Description of the Method      *      *@return    Description of the Return Value      */
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
literal|"match-keywords("
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
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
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|pprint
argument_list|()
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
comment|/**      *  Description of the Method      *      *@param  in_docs  Description of the Parameter      *@return          Description of the Return Value      */
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
comment|/**      *  Description of the Method      *      *@return    Description of the Return Value      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|TYPE_NODELIST
return|;
block|}
block|}
end_class

end_unit

