begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|FastQSort
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
name|AnyNodeTest
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
name|LocationStep
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
name|OrderSpec
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
name|PathExpr
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
name|PreorderedValueSequence
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
name|SequenceIterator
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
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|XSLContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|pattern
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_comment
comment|/**  *<xsl:sort  *   select? = expression  *   lang? = { nmtoken }  *   order? = { "ascending" | "descending" }  *   collation? = { uri }  *   stable? = { "yes" | "no" }  *   case-order? = { "upper-first" | "lower-first" }  *   data-type? = { "text" | "number" | qname-but-not-ncname }>  *<!-- Content: sequence-constructor -->  *</xsl:sort>  *   * @author shabanovd  *  */
end_comment

begin_class
specifier|public
class|class
name|Sort
extends|extends
name|Declaration
block|{
class|class
name|SortItem
implements|implements
name|Comparable
argument_list|<
name|SortItem
argument_list|>
block|{
specifier|private
name|Item
name|item
decl_stmt|;
specifier|private
name|String
name|value
decl_stmt|;
specifier|private
name|int
name|pos
decl_stmt|;
specifier|public
name|SortItem
parameter_list|(
name|Item
name|item
parameter_list|,
name|int
name|pos
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|item
operator|=
name|item
expr_stmt|;
name|value
operator|=
name|select
operator|.
name|eval
argument_list|(
name|item
operator|.
name|toSequence
argument_list|()
argument_list|,
name|item
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
block|}
specifier|public
name|Item
name|getItem
parameter_list|()
block|{
return|return
name|item
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|SortItem
name|o
parameter_list|)
block|{
name|int
name|compare
init|=
name|value
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|==
literal|0
condition|)
return|return
name|order
operator|*
operator|(
name|pos
operator|<
name|o
operator|.
name|pos
condition|?
operator|-
literal|1
else|:
operator|(
name|pos
operator|==
name|o
operator|.
name|pos
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
return|return
name|order
operator|*
name|compare
return|;
block|}
block|}
specifier|private
name|String
name|attr_order
init|=
literal|null
decl_stmt|;
specifier|private
name|PathExpr
name|select
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|lang
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|order
init|=
literal|1
decl_stmt|;
comment|//ascending
specifier|private
name|String
name|collation
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|stable
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|case_order
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|data_type
init|=
literal|null
decl_stmt|;
specifier|public
name|Sort
parameter_list|(
name|XSLContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setToDefaults
parameter_list|()
block|{
name|attr_order
operator|=
literal|null
expr_stmt|;
name|select
operator|=
literal|null
expr_stmt|;
name|lang
operator|=
literal|null
expr_stmt|;
name|order
operator|=
literal|1
expr_stmt|;
name|collation
operator|=
literal|null
expr_stmt|;
name|stable
operator|=
literal|null
expr_stmt|;
name|case_order
operator|=
literal|null
expr_stmt|;
name|data_type
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|Attr
name|attr
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|attr_name
init|=
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|SELECT
argument_list|)
condition|)
block|{
name|select
operator|=
operator|new
name|PathExpr
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|Pattern
operator|.
name|parse
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
name|select
argument_list|)
expr_stmt|;
name|_check_
argument_list|(
name|select
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|LANG
argument_list|)
condition|)
block|{
name|lang
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|ORDER
argument_list|)
condition|)
block|{
name|attr_order
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|attr
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"ascending"
argument_list|)
condition|)
name|order
operator|=
literal|1
expr_stmt|;
if|else if
condition|(
name|attr
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"descending"
argument_list|)
condition|)
name|order
operator|=
operator|-
literal|1
expr_stmt|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"wrong order"
argument_list|)
throw|;
comment|//TODO: error?
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|COLLATION
argument_list|)
condition|)
block|{
name|collation
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|STABLE
argument_list|)
condition|)
block|{
name|stable
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|CASE_ORDER
argument_list|)
condition|)
block|{
name|case_order
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|DATA_TYPE
argument_list|)
condition|)
block|{
name|data_type
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|select
operator|==
literal|null
condition|)
block|{
name|select
operator|=
operator|new
name|PathExpr
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|select
operator|.
name|add
argument_list|(
operator|new
name|LocationStep
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|Constants
operator|.
name|SELF_AXIS
argument_list|,
operator|new
name|AnyNodeTest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
name|SortItem
index|[]
name|items
init|=
operator|new
name|SortItem
index|[
name|contextSequence
operator|.
name|getItemCount
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
comment|//		for (Item item : contextSequence) {
for|for
control|(
name|SequenceIterator
name|iterInner
init|=
name|contextSequence
operator|.
name|iterate
argument_list|()
init|;
name|iterInner
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Item
name|item
init|=
name|iterInner
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|items
index|[
name|i
index|]
operator|=
operator|new
name|SortItem
argument_list|(
name|item
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
name|FastQSort
operator|.
name|sort
argument_list|(
name|items
argument_list|,
literal|0
argument_list|,
name|contextSequence
operator|.
name|getItemCount
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|items
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|result
operator|.
name|add
argument_list|(
name|items
index|[
name|i
index|]
operator|.
name|getItem
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
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
literal|"<xsl:sort"
argument_list|)
expr_stmt|;
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" select = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|select
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|lang
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" lang = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|lang
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attr_order
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" order = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|attr_order
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collation
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" collation = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stable
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" stable = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|stable
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|case_order
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" case_order = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|case_order
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|data_type
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" data_type = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|data_type
argument_list|)
expr_stmt|;
block|}
name|super
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
literal|"</xsl:sort>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<xsl:sort"
argument_list|)
expr_stmt|;
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" select = "
operator|+
name|select
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lang
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" lang = "
operator|+
name|lang
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|attr_order
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" order = "
operator|+
name|attr_order
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|collation
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" collation = "
operator|+
name|collation
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stable
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" stable = "
operator|+
name|stable
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|case_order
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" case_order = "
operator|+
name|case_order
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|data_type
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" data_type = "
operator|+
name|data_type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"</xsl:sort> "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

