begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
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

begin_class
specifier|public
class|class
name|Union
extends|extends
name|PathExpr
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|Union
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|PathExpr
name|left
decl_stmt|,
name|right
decl_stmt|;
specifier|public
name|Union
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|PathExpr
name|left
parameter_list|,
name|PathExpr
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
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
comment|/** 	 * check relevant documents. if right operand is a string literal 	 * we check which documents contain it at all. in other cases 	 * do nothing. 	 */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
block|{
comment|//return in_docs;
name|DocumentSet
name|left_docs
init|=
name|left
operator|.
name|preselect
argument_list|(
name|in_docs
argument_list|)
decl_stmt|;
name|DocumentSet
name|right_docs
init|=
name|right
operator|.
name|preselect
argument_list|(
name|in_docs
argument_list|)
decl_stmt|;
return|return
name|left_docs
operator|.
name|union
argument_list|(
name|right_docs
argument_list|)
return|;
block|}
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
name|NodeSet
name|lval
init|=
operator|(
name|NodeSet
operator|)
name|left
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
name|getNodeList
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"left "
operator|+
name|left
operator|.
name|pprint
argument_list|()
operator|+
literal|" returned: "
operator|+
name|lval
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeSet
name|rval
init|=
operator|(
name|NodeSet
operator|)
name|right
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
name|getNodeList
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"right "
operator|+
name|right
operator|.
name|pprint
argument_list|()
operator|+
literal|" returned: "
operator|+
name|rval
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|NodeSet
name|result
init|=
name|lval
operator|.
name|union
argument_list|(
name|rval
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"union found "
operator|+
name|result
operator|.
name|getLength
argument_list|()
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
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
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
name|left
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"|"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|right
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#setInPredicate(boolean) 	 */
specifier|public
name|void
name|setInPredicate
parameter_list|(
name|boolean
name|inPredicate
parameter_list|)
block|{
name|super
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
name|left
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
name|right
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

