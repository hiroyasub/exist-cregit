begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JMenuItem
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JPopupMenu
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTextField
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
implements|implements
name|ActionListener
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CUT
init|=
literal|"Cut"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|COPY
init|=
literal|"Copy"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PASTE
init|=
literal|"Paste"
decl_stmt|;
specifier|private
name|JTextField
name|txtPositionOutput
init|=
literal|null
decl_stmt|;
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
literal|10
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
name|this
operator|.
name|addCaretListener
argument_list|(
operator|new
name|CaretListener
argument_list|()
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
name|ClientInputHandler
name|inputHandler
init|=
operator|new
name|ClientInputHandler
argument_list|()
decl_stmt|;
name|inputHandler
operator|.
name|addDefaultKeyBindings
argument_list|()
expr_stmt|;
name|setInputHandler
argument_list|(
name|inputHandler
argument_list|)
expr_stmt|;
name|popup
operator|=
operator|new
name|JPopupMenu
argument_list|(
literal|"Edit Menu"
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
operator|new
name|JMenuItem
argument_list|(
name|CUT
argument_list|)
argument_list|)
operator|.
name|addActionListener
argument_list|(
name|ClientInputHandler
operator|.
name|CLIP_CUT
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
operator|new
name|JMenuItem
argument_list|(
name|COPY
argument_list|)
argument_list|)
operator|.
name|addActionListener
argument_list|(
name|ClientInputHandler
operator|.
name|CLIP_COPY
argument_list|)
expr_stmt|;
name|popup
operator|.
name|add
argument_list|(
operator|new
name|JMenuItem
argument_list|(
name|PASTE
argument_list|)
argument_list|)
operator|.
name|addActionListener
argument_list|(
name|ClientInputHandler
operator|.
name|CLIP_PASTE
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
literal|true
argument_list|)
expr_stmt|;
name|painter
operator|.
name|setBracketHighlightEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setPositionOutputTextArea
parameter_list|(
name|JTextField
name|txtPositionOutput
parameter_list|)
block|{
name|this
operator|.
name|txtPositionOutput
operator|=
name|txtPositionOutput
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent) 	 */
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"event: "
operator|+
name|e
operator|.
name|getActionCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|CaretListener
implements|implements
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|CaretListener
block|{
specifier|public
name|void
name|caretUpdate
parameter_list|(
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|CaretEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|txtPositionOutput
operator|!=
literal|null
condition|)
block|{
name|ClientTextArea
name|txt
init|=
operator|(
name|ClientTextArea
operator|)
name|e
operator|.
name|getSource
argument_list|()
decl_stmt|;
name|txtPositionOutput
operator|.
name|setText
argument_list|(
literal|"Line: "
operator|+
operator|(
name|txt
operator|.
name|getCaretLine
argument_list|()
operator|+
literal|1
operator|)
operator|+
literal|" Column:"
operator|+
operator|(
operator|(
name|txt
operator|.
name|getCaretPosition
argument_list|()
operator|-
name|txt
operator|.
name|getLineStartOffset
argument_list|(
name|txt
operator|.
name|getCaretLine
argument_list|()
argument_list|)
operator|)
operator|+
literal|1
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

