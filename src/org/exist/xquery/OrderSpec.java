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
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_comment
comment|/**  * An XQuery order specifier as specified in an "order by" clause.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|OrderSpec
block|{
specifier|public
specifier|static
specifier|final
name|int
name|ASCENDING_ORDER
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DESCENDING_ORDER
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|EMPTY_GREATEST
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|EMPTY_LEAST
init|=
literal|4
decl_stmt|;
specifier|private
name|XQueryContext
name|context
decl_stmt|;
specifier|private
name|Expression
name|expression
decl_stmt|;
specifier|private
name|int
name|modifiers
init|=
literal|0
decl_stmt|;
specifier|private
name|Collator
name|collator
init|=
literal|null
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|OrderSpec
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|sortExpr
parameter_list|)
block|{
name|this
operator|.
name|expression
operator|=
name|sortExpr
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|void
name|setModifiers
parameter_list|(
name|int
name|modifiers
parameter_list|)
block|{
name|this
operator|.
name|modifiers
operator|=
name|modifiers
expr_stmt|;
block|}
specifier|public
name|void
name|setCollation
parameter_list|(
name|String
name|collationURI
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|collator
operator|=
name|context
operator|.
name|getCollator
argument_list|(
name|collationURI
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Expression
name|getSortExpression
parameter_list|()
block|{
return|return
name|expression
return|;
block|}
specifier|public
name|int
name|getModifiers
parameter_list|()
block|{
return|return
name|modifiers
return|;
block|}
specifier|public
name|Collator
name|getCollator
parameter_list|()
block|{
return|return
name|collator
operator|==
literal|null
condition|?
name|context
operator|.
name|getDefaultCollator
argument_list|()
else|:
name|collator
return|;
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
name|expression
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
operator|(
name|modifiers
operator|&
name|DESCENDING_ORDER
operator|)
operator|==
literal|0
condition|?
literal|"ascending"
else|:
literal|"descending"
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
name|resetState
parameter_list|()
block|{
name|expression
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

