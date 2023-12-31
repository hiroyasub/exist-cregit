begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ExpressionDumper
block|{
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_INDENT_AMOUNT
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
name|String
name|dump
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|ExpressionDumper
name|dumper
init|=
operator|new
name|ExpressionDumper
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|expr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|PrintWriter
name|out
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
name|int
name|indentAmount
decl_stmt|;
specifier|private
name|String
name|spaces
decl_stmt|;
specifier|private
name|int
name|verbosity
decl_stmt|;
specifier|private
name|int
name|indent
init|=
literal|0
decl_stmt|;
specifier|public
name|ExpressionDumper
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|this
argument_list|(
name|writer
argument_list|,
name|DEFAULT_INDENT_AMOUNT
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExpressionDumper
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|int
name|indentAmount
parameter_list|)
block|{
name|this
argument_list|(
name|writer
argument_list|,
name|indentAmount
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExpressionDumper
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|int
name|indentAmount
parameter_list|,
name|int
name|verbosity
parameter_list|)
block|{
if|if
condition|(
name|writer
operator|instanceof
name|PrintWriter
condition|)
block|{
name|this
operator|.
name|out
operator|=
operator|(
name|PrintWriter
operator|)
name|writer
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|out
operator|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|indentAmount
operator|=
name|indentAmount
expr_stmt|;
name|this
operator|.
name|spaces
operator|=
literal|""
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|indentAmount
condition|;
name|i
operator|++
control|)
name|this
operator|.
name|spaces
operator|+=
literal|" "
expr_stmt|;
name|this
operator|.
name|verbosity
operator|=
name|verbosity
expr_stmt|;
block|}
specifier|private
name|void
name|indent
parameter_list|()
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
name|indent
condition|;
name|i
operator|++
control|)
name|out
operator|.
name|print
argument_list|(
name|spaces
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|verbosity
parameter_list|()
block|{
return|return
name|verbosity
return|;
block|}
specifier|public
name|ExpressionDumper
name|display
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
return|return
name|display
argument_list|(
name|object
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ExpressionDumper
name|display
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionDumper
name|display
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|line
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
if|if
condition|(
name|line
operator|>
operator|-
literal|1
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionDumper
name|display
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
name|ch
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionDumper
name|startIndent
parameter_list|()
block|{
operator|++
name|indent
expr_stmt|;
name|nl
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionDumper
name|endIndent
parameter_list|()
block|{
if|if
condition|(
name|indent
operator|>
literal|0
condition|)
block|{
operator|--
name|indent
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|ExpressionDumper
name|nl
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|indent
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

