begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|SimpleStep
extends|extends
name|Step
block|{
specifier|private
name|Expression
name|expression
decl_stmt|;
comment|/** 	 * @param context 	 * @param axis 	 */
specifier|public
name|SimpleStep
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|axis
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|axis
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|expression
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|NodeSet
name|set
init|=
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|set
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|set
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
switch|switch
condition|(
name|axis
condition|)
block|{
case|case
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
case|:
name|set
operator|=
name|set
operator|.
name|selectAncestorDescendant
argument_list|(
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
literal|true
argument_list|,
name|inPredicate
argument_list|)
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|CHILD_AXIS
case|:
name|set
operator|=
name|set
operator|.
name|selectParentChild
argument_list|(
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|inPredicate
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Wrong axis specified"
argument_list|)
throw|;
block|}
return|return
name|set
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Step#pprint() 	 */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|"simple: "
operator|+
operator|(
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
condition|?
literal|"//"
operator|+
name|expression
operator|.
name|pprint
argument_list|()
else|:
literal|'/'
operator|+
name|expression
operator|.
name|pprint
argument_list|()
operator|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Step#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|super
operator|.
name|resetState
argument_list|()
expr_stmt|;
name|expression
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Step#setInPredicate(boolean) 	 */
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
name|expression
operator|.
name|setInPredicate
argument_list|(
name|inPredicate
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#setPrimaryAxis(int) 	 */
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
name|expression
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

