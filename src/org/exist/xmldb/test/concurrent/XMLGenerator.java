begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|test
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLGenerator
block|{
name|String
index|[]
name|words
decl_stmt|;
name|int
name|elementCnt
decl_stmt|;
name|int
name|attrCnt
decl_stmt|;
name|int
name|depth
decl_stmt|;
name|Random
name|random
decl_stmt|;
specifier|public
name|XMLGenerator
parameter_list|(
name|int
name|elementCnt
parameter_list|,
name|int
name|attrCnt
parameter_list|,
name|int
name|depth
parameter_list|,
name|String
index|[]
name|words
parameter_list|)
block|{
name|this
operator|.
name|elementCnt
operator|=
name|elementCnt
expr_stmt|;
name|this
operator|.
name|attrCnt
operator|=
name|attrCnt
expr_stmt|;
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
name|this
operator|.
name|words
operator|=
name|words
expr_stmt|;
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|generateXML
parameter_list|(
name|Writer
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"<ROOT-ELEMENT>"
argument_list|)
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
name|elementCnt
condition|;
name|i
operator|++
control|)
block|{
name|writeElement
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|"</ROOT-ELEMENT>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|generateElement
parameter_list|()
throws|throws
name|IOException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|writeElement
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|void
name|writeElement
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|int
name|level
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"<ELEMENT"
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|level
argument_list|)
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
name|attrCnt
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|" attribute-"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|generateText
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|<
name|depth
operator|-
literal|1
condition|)
name|writeElement
argument_list|(
name|writer
argument_list|,
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
else|else
name|writer
operator|.
name|write
argument_list|(
name|generateText
argument_list|(
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"\n</ELEMENT"
argument_list|)
expr_stmt|;
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|">\n"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|generateText
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
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
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|words
operator|.
name|length
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|words
index|[
name|n
index|]
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

