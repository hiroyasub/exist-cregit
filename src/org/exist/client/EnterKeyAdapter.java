begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2013 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|KeyAdapter
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
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|KeyListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|AbstractButton
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JPasswordField
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
name|JComboBox
import|;
end_import

begin_comment
comment|/**  * The class EnterKeyAdapter listens for VK_ENTER key events   * for buttons, JPasswordFields, JTextFields and JComboBoxes,  * whereby it sends doClick() to the affected or specified source.  *  * @author ljo<ljo@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|EnterKeyAdapter
extends|extends
name|KeyAdapter
block|{
specifier|private
name|AbstractButton
name|button
decl_stmt|;
comment|/**      * Creates a new<code>EnterKeyAdapter</code> instance.      *      */
specifier|public
name|EnterKeyAdapter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * Creates a new<code>EnterKeyAdapter</code> instance.      *      * @param button an<code>AbstractButton</code> value      */
specifier|public
name|EnterKeyAdapter
parameter_list|(
specifier|final
name|AbstractButton
name|button
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|button
operator|=
name|button
expr_stmt|;
block|}
specifier|public
name|void
name|keyPressed
parameter_list|(
name|KeyEvent
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getKeyCode
argument_list|()
operator|==
name|KeyEvent
operator|.
name|VK_ENTER
operator|&&
name|e
operator|.
name|getSource
argument_list|()
operator|instanceof
name|AbstractButton
condition|)
block|{
operator|(
operator|(
name|AbstractButton
operator|)
name|e
operator|.
name|getSource
argument_list|()
operator|)
operator|.
name|doClick
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|e
operator|.
name|getKeyCode
argument_list|()
operator|==
name|KeyEvent
operator|.
name|VK_ENTER
operator|&&
operator|(
name|e
operator|.
name|getSource
argument_list|()
operator|instanceof
name|JPasswordField
operator|||
name|e
operator|.
name|getSource
argument_list|()
operator|instanceof
name|JTextField
operator|||
name|e
operator|.
name|getSource
argument_list|()
operator|instanceof
name|JComboBox
operator|)
condition|)
block|{
if|if
condition|(
name|button
operator|!=
literal|null
condition|)
block|{
name|button
operator|.
name|doClick
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

