begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|interpreter
operator|.
name|ContextAtExist
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
name|AnalyzeContextInfo
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
name|Expression
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
name|functions
operator|.
name|fn
operator|.
name|FunDistinctValues
operator|.
name|ValueComparator
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
name|AtomicValue
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
name|NumericValue
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
name|Type
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
name|ErrorCodes
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
comment|/**  *<!-- Category: instruction -->  *<xsl:for-each-group  *   select = expression  *   group-by? = expression  *   group-adjacent? = expression  *   group-starting-with? = pattern  *   group-ending-with? = pattern  *   collation? = { uri }>  *<!-- Content: (xsl:sort*, sequence-constructor) -->  *</xsl:for-each-group>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ForEachGroup
extends|extends
name|SimpleConstructor
block|{
specifier|private
name|String
name|attr_select
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|attr_group_by
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|attr_group_adjacent
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|attr_group_starting_with
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|attr_group_ending_with
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|attr_collation
init|=
literal|null
decl_stmt|;
specifier|private
name|XSLPathExpr
name|select
init|=
literal|null
decl_stmt|;
specifier|private
name|XSLPathExpr
name|group_by
init|=
literal|null
decl_stmt|;
specifier|private
name|PathExpr
name|group_adjacent
init|=
literal|null
decl_stmt|;
specifier|private
name|PathExpr
name|group_starting_with
init|=
literal|null
decl_stmt|;
specifier|private
name|PathExpr
name|group_ending_with
init|=
literal|null
decl_stmt|;
specifier|private
name|XSLPathExpr
name|collator
init|=
literal|null
decl_stmt|;
specifier|public
name|ForEachGroup
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
name|attr_select
operator|=
literal|null
expr_stmt|;
name|attr_group_by
operator|=
literal|null
expr_stmt|;
name|attr_group_adjacent
operator|=
literal|null
expr_stmt|;
name|attr_group_starting_with
operator|=
literal|null
expr_stmt|;
name|attr_group_ending_with
operator|=
literal|null
expr_stmt|;
name|attr_collation
operator|=
literal|null
expr_stmt|;
name|select
operator|=
literal|null
expr_stmt|;
name|group_by
operator|=
literal|null
expr_stmt|;
name|group_adjacent
operator|=
literal|null
expr_stmt|;
name|group_starting_with
operator|=
literal|null
expr_stmt|;
name|group_ending_with
operator|=
literal|null
expr_stmt|;
name|collator
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|ContextAtExist
name|context
parameter_list|,
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
name|getNodeName
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
name|attr_select
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
name|GROUP_BY
argument_list|)
condition|)
block|{
name|attr_group_by
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
name|GROUP_ADJACENT
argument_list|)
condition|)
block|{
name|attr_group_adjacent
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
name|GROUP_STARTING_WITH
argument_list|)
condition|)
block|{
name|attr_group_starting_with
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
name|GROUP_ENDING_WITH
argument_list|)
condition|)
block|{
name|attr_group_ending_with
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
name|COLLATION
argument_list|)
condition|)
block|{
name|attr_collation
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
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|boolean
name|atRootCall
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|attr_collation
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|attr_group_by
operator|==
literal|null
operator|&&
name|attr_group_adjacent
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTSE1090
argument_list|,
literal|""
argument_list|)
throw|;
if|if
condition|(
name|attr_collation
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
operator|&&
name|attr_collation
operator|.
name|endsWith
argument_list|(
literal|"}"
argument_list|)
condition|)
block|{
name|collator
operator|=
operator|new
name|XSLPathExpr
argument_list|(
name|getXSLContext
argument_list|()
argument_list|)
expr_stmt|;
name|Pattern
operator|.
name|parse
argument_list|(
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|attr_collation
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|attr_collation
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|collator
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|attr_select
operator|!=
literal|null
condition|)
block|{
name|select
operator|=
operator|new
name|XSLPathExpr
argument_list|(
name|getXSLContext
argument_list|()
argument_list|)
expr_stmt|;
name|Pattern
operator|.
name|parse
argument_list|(
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|attr_select
argument_list|,
name|select
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|DOT_TEST
operator|)
operator|!=
literal|0
condition|)
block|{
name|atRootCall
operator|=
literal|true
expr_stmt|;
name|_check_
argument_list|(
name|select
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|removeFlag
argument_list|(
name|DOT_TEST
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|attr_group_by
operator|!=
literal|null
condition|)
block|{
name|group_by
operator|=
operator|new
name|XSLPathExpr
argument_list|(
name|getXSLContext
argument_list|()
argument_list|)
expr_stmt|;
name|Pattern
operator|.
name|parse
argument_list|(
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|attr_group_by
argument_list|,
name|group_by
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|DOT_TEST
operator|)
operator|!=
literal|0
condition|)
block|{
name|atRootCall
operator|=
literal|true
expr_stmt|;
name|_check_
argument_list|(
name|group_by
argument_list|)
expr_stmt|;
name|contextInfo
operator|.
name|removeFlag
argument_list|(
name|DOT_TEST
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|atRootCall
condition|)
name|contextInfo
operator|.
name|addFlag
argument_list|(
name|DOT_TEST
argument_list|)
expr_stmt|;
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
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Sequence
name|selected
init|=
name|select
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Collator
name|collator
init|=
name|getCollator
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|TreeMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|(
operator|new
name|ValueComparator
argument_list|(
name|collator
argument_list|)
argument_list|)
decl_stmt|;
name|Item
name|item
decl_stmt|;
name|AtomicValue
name|value
decl_stmt|;
name|NumericValue
name|firstNaN
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|selected
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|item
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|value
operator|=
name|group_by
operator|.
name|eval
argument_list|(
name|selected
argument_list|,
name|item
argument_list|)
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
expr_stmt|;
comment|//UNDERSTAND: is it correct?
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|value
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
block|{
comment|//although NaN does not equal itself, if $arg contains multiple NaN values a single NaN is returned.
if|if
condition|(
name|firstNaN
operator|==
literal|null
condition|)
block|{
name|Sequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|firstNaN
operator|=
operator|(
name|NumericValue
operator|)
name|value
expr_stmt|;
block|}
else|else
block|{
name|Sequence
name|seq
init|=
name|map
operator|.
name|get
argument_list|(
name|firstNaN
argument_list|)
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
block|}
name|Sequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|value
argument_list|,
name|seq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Sequence
name|seq
init|=
name|map
operator|.
name|get
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|SequenceIterator
name|iterInner
init|=
name|entry
operator|.
name|getValue
argument_list|()
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
name|each
init|=
name|iterInner
operator|.
name|nextItem
argument_list|()
decl_stmt|;
comment|//Sequence seq = childNodes.eval(contextSequence, each);
name|Sequence
name|answer
init|=
name|super
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|each
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|answer
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|Collator
name|getCollator
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|int
name|arg
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|attr_collation
operator|!=
literal|null
condition|)
block|{
name|String
name|collationURI
init|=
name|attr_collation
decl_stmt|;
if|if
condition|(
name|collator
operator|!=
literal|null
condition|)
name|collationURI
operator|=
name|collator
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
return|return
name|context
operator|.
name|getCollator
argument_list|(
name|collationURI
argument_list|)
return|;
block|}
else|else
return|return
name|context
operator|.
name|getDefaultCollator
argument_list|()
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
literal|"<xsl:for-each-group"
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
name|select
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|group_by
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" group_by = "
argument_list|)
expr_stmt|;
name|group_by
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|group_adjacent
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" group_adjacent = "
argument_list|)
expr_stmt|;
name|group_adjacent
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|group_starting_with
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" group_starting_with = "
argument_list|)
expr_stmt|;
name|group_starting_with
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|group_ending_with
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" group_ending_with = "
argument_list|)
expr_stmt|;
name|group_ending_with
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attr_collation
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" collation = "
operator|+
name|attr_collation
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
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
literal|"</xsl:for-each-group>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<xsl:for-each-group"
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
name|group_by
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" group_by = "
operator|+
name|group_by
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|group_adjacent
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" group_adjacent = "
operator|+
name|group_adjacent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|group_starting_with
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" group_starting_with = "
operator|+
name|group_starting_with
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|group_ending_with
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" group_ending_with = "
operator|+
name|group_ending_with
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|attr_collation
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" collation = "
operator|+
name|attr_collation
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
literal|"</xsl:for-each-group>"
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

