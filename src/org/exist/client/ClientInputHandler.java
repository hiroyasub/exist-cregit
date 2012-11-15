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
name|event
operator|.
name|ActionListener
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
name|KeyEvent
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
name|DefaultInputHandler
import|;
end_import

begin_comment
comment|/**  * A class to extend {@link org.jedit.syntax.DefaultInputHandler} to be a little  * more Mac friendly. This class doesn't pretend to be a robust cross-platform  * implementation of key bindings, but it is an incremental improvement over  * what came before it. To see just how involved cross-platform keyboard  * handling can become, check out<a href="http://jedit.org/">jEdit</a> from  * which the jEdit-syntax libraries were derived many years ago. Ideally, I  * suppose, someone should incorporate jEdit's much more robust solution back  * into eXist, but that's a pretty extensive overhaul.  */
end_comment

begin_class
specifier|public
class|class
name|ClientInputHandler
extends|extends
name|DefaultInputHandler
block|{
specifier|private
name|boolean
name|runningOnMac
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"mrj.version"
argument_list|)
operator|!=
literal|null
decl_stmt|;
comment|/* Listeners for actions not already defined in InputHandler */
specifier|public
specifier|static
specifier|final
name|ActionListener
name|SELECT_ALL
init|=
operator|new
name|select_all
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActionListener
name|CLIP_COPY
init|=
operator|new
name|clip_copy
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActionListener
name|CLIP_PASTE
init|=
operator|new
name|clip_paste
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ActionListener
name|CLIP_CUT
init|=
operator|new
name|clip_cut
argument_list|()
decl_stmt|;
comment|/** 	 * Sets up the default key bindings. 	 */
specifier|public
name|void
name|addDefaultKeyBindings
parameter_list|()
block|{
if|if
condition|(
name|runningOnMac
condition|)
block|{
comment|/* Bindings Mac users are accustomed to */
name|addKeyBinding
argument_list|(
literal|"BACK_SPACE"
argument_list|,
name|BACKSPACE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"M+BACK_SPACE"
argument_list|,
name|BACKSPACE_WORD
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"DELETE"
argument_list|,
name|DELETE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"M+DELETE"
argument_list|,
name|DELETE_WORD
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"ENTER"
argument_list|,
name|INSERT_BREAK
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"TAB"
argument_list|,
name|INSERT_TAB
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"HOME"
argument_list|,
name|DOCUMENT_HOME
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"END"
argument_list|,
name|DOCUMENT_END
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+HOME"
argument_list|,
name|SELECT_DOC_HOME
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+END"
argument_list|,
name|SELECT_DOC_END
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"M+A"
argument_list|,
name|SELECT_ALL
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+HOME"
argument_list|,
name|SELECT_HOME
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+END"
argument_list|,
name|SELECT_END
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"PAGE_UP"
argument_list|,
name|PREV_PAGE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"PAGE_DOWN"
argument_list|,
name|NEXT_PAGE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+PAGE_UP"
argument_list|,
name|SELECT_PREV_PAGE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+PAGE_DOWN"
argument_list|,
name|SELECT_NEXT_PAGE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"LEFT"
argument_list|,
name|PREV_CHAR
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+LEFT"
argument_list|,
name|SELECT_PREV_CHAR
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"A+LEFT"
argument_list|,
name|PREV_WORD
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"AS+LEFT"
argument_list|,
name|SELECT_PREV_WORD
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"RIGHT"
argument_list|,
name|NEXT_CHAR
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+RIGHT"
argument_list|,
name|SELECT_NEXT_CHAR
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"A+RIGHT"
argument_list|,
name|NEXT_WORD
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"AS+RIGHT"
argument_list|,
name|SELECT_NEXT_WORD
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"UP"
argument_list|,
name|PREV_LINE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+UP"
argument_list|,
name|SELECT_PREV_LINE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"DOWN"
argument_list|,
name|NEXT_LINE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"S+DOWN"
argument_list|,
name|SELECT_NEXT_LINE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"A+ENTER"
argument_list|,
name|REPEAT
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"M+C"
argument_list|,
name|CLIP_COPY
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"M+V"
argument_list|,
name|CLIP_PASTE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"M+X"
argument_list|,
name|CLIP_CUT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/* Bindings Windows users and others are accustomed to */
name|super
operator|.
name|addDefaultKeyBindings
argument_list|()
expr_stmt|;
comment|/* Plus a few extra DefaultInputHandler didn't include */
name|addKeyBinding
argument_list|(
literal|"C+A"
argument_list|,
name|SELECT_ALL
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"C+C"
argument_list|,
name|CLIP_COPY
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"C+V"
argument_list|,
name|CLIP_PASTE
argument_list|)
expr_stmt|;
name|addKeyBinding
argument_list|(
literal|"C+X"
argument_list|,
name|CLIP_CUT
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|keyTyped
parameter_list|(
name|KeyEvent
name|evt
parameter_list|)
block|{
if|if
condition|(
name|runningOnMac
condition|)
block|{
comment|/* 			 * Keys pressed with the command key shouldn't generate text. 			 */
name|int
name|modifiers
init|=
name|evt
operator|.
name|getModifiers
argument_list|()
decl_stmt|;
name|char
name|c
init|=
name|evt
operator|.
name|getKeyChar
argument_list|()
decl_stmt|;
comment|/* 			 * Default input handler filters out events with the ALT (option) 			 * key, but those are associated with valid characters on the Mac. 			 * This won't work in the general case, but it should get things 			 * working for many people for whom this was broken before. 			 */
if|if
condition|(
name|c
operator|!=
name|KeyEvent
operator|.
name|CHAR_UNDEFINED
operator|&&
operator|(
name|modifiers
operator|&
name|KeyEvent
operator|.
name|ALT_MASK
operator|)
operator|!=
literal|0
condition|)
block|{
name|executeAction
argument_list|(
name|INSERT_CHAR
argument_list|,
name|evt
operator|.
name|getSource
argument_list|()
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|c
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|(
name|modifiers
operator|&
name|KeyEvent
operator|.
name|META_MASK
operator|)
operator|==
literal|0
condition|)
block|{
name|super
operator|.
name|keyTyped
argument_list|(
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|keyTyped
argument_list|(
name|evt
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|select_all
implements|implements
name|ActionListener
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|JEditTextArea
name|textArea
init|=
name|getTextArea
argument_list|(
name|evt
argument_list|)
decl_stmt|;
name|textArea
operator|.
name|selectAll
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|clip_copy
implements|implements
name|ActionListener
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|JEditTextArea
name|textArea
init|=
name|getTextArea
argument_list|(
name|evt
argument_list|)
decl_stmt|;
name|textArea
operator|.
name|copy
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|clip_paste
implements|implements
name|ActionListener
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|JEditTextArea
name|textArea
init|=
name|getTextArea
argument_list|(
name|evt
argument_list|)
decl_stmt|;
name|textArea
operator|.
name|paste
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|clip_cut
implements|implements
name|ActionListener
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|evt
parameter_list|)
block|{
name|JEditTextArea
name|textArea
init|=
name|getTextArea
argument_list|(
name|evt
argument_list|)
decl_stmt|;
name|textArea
operator|.
name|cut
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

