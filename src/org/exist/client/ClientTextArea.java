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
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Color
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Dimension
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Font
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|text
operator|.
name|PlainDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jedit
operator|.
name|syntax
operator|.
name|JEditTextArea
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jedit
operator|.
name|syntax
operator|.
name|SyntaxDocument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jedit
operator|.
name|syntax
operator|.
name|SyntaxStyle
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jedit
operator|.
name|syntax
operator|.
name|TextAreaPainter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jedit
operator|.
name|syntax
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jedit
operator|.
name|syntax
operator|.
name|XMLTokenMarker
import|;
end_import

begin_class
specifier|public
class|class
name|ClientTextArea
extends|extends
name|JEditTextArea
block|{
specifier|protected
name|Font
name|textFont
init|=
operator|new
name|Font
argument_list|(
literal|"Monospaced"
argument_list|,
name|Font
operator|.
name|PLAIN
argument_list|,
literal|12
argument_list|)
decl_stmt|;
specifier|public
name|ClientTextArea
parameter_list|(
name|boolean
name|editable
parameter_list|,
name|String
name|mode
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|setFont
argument_list|(
name|textFont
argument_list|)
expr_stmt|;
name|setEditable
argument_list|(
name|editable
argument_list|)
expr_stmt|;
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|300
argument_list|,
literal|200
argument_list|)
argument_list|)
expr_stmt|;
name|SyntaxDocument
name|doc
init|=
operator|new
name|SyntaxDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|putProperty
argument_list|(
name|PlainDocument
operator|.
name|tabSizeAttribute
argument_list|,
operator|new
name|Integer
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|setElectricScroll
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|.
name|equals
argument_list|(
literal|"XML"
argument_list|)
condition|)
name|setTokenMarker
argument_list|(
operator|new
name|XMLTokenMarker
argument_list|()
argument_list|)
expr_stmt|;
name|TextAreaPainter
name|painter
init|=
name|getPainter
argument_list|()
decl_stmt|;
name|SyntaxStyle
index|[]
name|styles
init|=
name|painter
operator|.
name|getStyles
argument_list|()
decl_stmt|;
name|styles
index|[
name|Token
operator|.
name|KEYWORD1
index|]
operator|=
operator|new
name|SyntaxStyle
argument_list|(
operator|new
name|Color
argument_list|(
literal|0
argument_list|,
literal|102
argument_list|,
literal|153
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|styles
index|[
name|Token
operator|.
name|KEYWORD2
index|]
operator|=
operator|new
name|SyntaxStyle
argument_list|(
operator|new
name|Color
argument_list|(
literal|0
argument_list|,
literal|153
argument_list|,
literal|102
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|styles
index|[
name|Token
operator|.
name|KEYWORD3
index|]
operator|=
operator|new
name|SyntaxStyle
argument_list|(
operator|new
name|Color
argument_list|(
literal|0
argument_list|,
literal|153
argument_list|,
literal|255
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|styles
index|[
name|Token
operator|.
name|LITERAL1
index|]
operator|=
operator|new
name|SyntaxStyle
argument_list|(
operator|new
name|Color
argument_list|(
literal|255
argument_list|,
literal|0
argument_list|,
literal|204
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|styles
index|[
name|Token
operator|.
name|LITERAL2
index|]
operator|=
operator|new
name|SyntaxStyle
argument_list|(
operator|new
name|Color
argument_list|(
literal|204
argument_list|,
literal|0
argument_list|,
literal|204
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|painter
operator|.
name|setStyles
argument_list|(
name|styles
argument_list|)
expr_stmt|;
name|painter
operator|.
name|setEOLMarkersPainted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

