begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or modify it  * under the terms of the GNU Lesser General Public License as published by the  * Free Software Foundation; either version 2 of the License, or (at your  * option) any later version.  *  * This program is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or  * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License  * for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation,  * Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
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
name|Dimension
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|GridBagConstraints
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|GridBagLayout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Insets
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
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|MouseAdapter
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
name|MouseEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|prefs
operator|.
name|BackingStoreException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|prefs
operator|.
name|Preferences
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|DefaultListModel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JButton
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

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JLabel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JList
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JOptionPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JPanel
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
name|JScrollPane
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
name|ListModel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|ScrollPaneConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|DocumentEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|DocumentListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|ListSelectionEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|ListSelectionListener
import|;
end_import

begin_comment
comment|/**  * This class implements the graphical login panel used to log into  * local and remote eXist database instances.  *  * @author Wolfgang M. Meier<wolfgang@exist-db.org>  * @author Tobias Wunden<tobias.wunden@o2it.ch>  */
end_comment

begin_class
specifier|public
class|class
name|LoginPanel
extends|extends
name|JPanel
block|{
specifier|public
specifier|static
specifier|final
name|int
name|TYPE_REMOTE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|TYPE_LOCAL
init|=
literal|1
decl_stmt|;
comment|/** Uri for local connections */
specifier|public
specifier|static
specifier|final
name|String
name|URI_LOCAL
init|=
literal|"xmldb:exist://"
decl_stmt|;
comment|/** Default uri for remote connections */
specifier|public
specifier|static
specifier|final
name|String
name|URI_REMOTE
init|=
literal|"xmldb:exist://localhost:8080/exist/xmlrpc"
decl_stmt|;
comment|/** Name of Preference node containing favourites */
specifier|public
specifier|static
specifier|final
name|String
name|FAVOURITES_NODE
init|=
literal|"favourites"
decl_stmt|;
comment|/** Ui components */
name|JTextField
name|username
decl_stmt|;
name|JPasswordField
name|password
decl_stmt|;
name|JTextField
name|cur_url
decl_stmt|;
name|JComboBox
name|type
decl_stmt|;
name|JList
name|favourites
decl_stmt|;
name|DefaultListModel
name|favouritesModel
decl_stmt|;
name|JTextField
name|title
decl_stmt|;
name|JButton
name|btnAddFavourite
decl_stmt|;
name|JButton
name|btnRemoveFavourite
decl_stmt|;
name|JButton
name|btnLoadFavourite
decl_stmt|;
comment|/**      * Creates a new login panel with the given user and uri.      *      * @param defaultUser the initial user      * @param uri the uri to connect to      */
specifier|public
name|LoginPanel
parameter_list|(
name|String
name|defaultUser
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|setupComponents
argument_list|(
name|defaultUser
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets up the graphical components.      *      * @param defaultUser the initial user      * @param uri the uri to connect to      */
specifier|private
name|void
name|setupComponents
parameter_list|(
specifier|final
name|String
name|defaultUser
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|)
block|{
name|GridBagLayout
name|grid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|setLayout
argument_list|(
name|grid
argument_list|)
expr_stmt|;
name|GridBagConstraints
name|c
init|=
operator|new
name|GridBagConstraints
argument_list|()
decl_stmt|;
specifier|final
name|int
name|inset
init|=
literal|5
decl_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|)
expr_stmt|;
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
literal|"Username"
argument_list|)
decl_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|username
operator|=
operator|new
name|JTextField
argument_list|(
name|defaultUser
argument_list|,
literal|12
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|username
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Password"
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|password
operator|=
operator|new
name|JPasswordField
argument_list|(
literal|12
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|password
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Type"
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|type
operator|=
operator|new
name|JComboBox
argument_list|()
expr_stmt|;
name|type
operator|.
name|addItem
argument_list|(
literal|"Remote"
argument_list|)
expr_stmt|;
name|type
operator|.
name|addItem
argument_list|(
literal|"Local"
argument_list|)
expr_stmt|;
name|type
operator|.
name|setSelectedIndex
argument_list|(
name|uri
operator|.
name|equals
argument_list|(
name|URI_LOCAL
argument_list|)
condition|?
name|TYPE_LOCAL
else|:
name|TYPE_REMOTE
argument_list|)
expr_stmt|;
name|type
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
switch|switch
condition|(
name|type
operator|.
name|getSelectedIndex
argument_list|()
condition|)
block|{
case|case
name|TYPE_LOCAL
case|:
name|cur_url
operator|.
name|setText
argument_list|(
name|URI_LOCAL
argument_list|)
expr_stmt|;
name|cur_url
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
break|break;
case|case
name|TYPE_REMOTE
case|:
name|cur_url
operator|.
name|setText
argument_list|(
operator|!
name|uri
operator|.
name|equals
argument_list|(
name|URI_LOCAL
argument_list|)
condition|?
name|uri
else|:
name|URI_REMOTE
argument_list|)
expr_stmt|;
name|cur_url
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|type
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"URL"
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|3
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|cur_url
operator|=
operator|new
name|JTextField
argument_list|(
name|uri
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|cur_url
operator|.
name|setEnabled
argument_list|(
operator|!
name|uri
operator|.
name|equals
argument_list|(
name|URI_LOCAL
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|3
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|cur_url
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|cur_url
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Title"
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|4
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
literal|20
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|)
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|EAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|title
operator|=
operator|new
name|JTextField
argument_list|()
expr_stmt|;
name|title
operator|.
name|getDocument
argument_list|()
operator|.
name|addDocumentListener
argument_list|(
operator|new
name|DocumentListener
argument_list|()
block|{
specifier|public
name|void
name|insertUpdate
parameter_list|(
name|DocumentEvent
name|arg0
parameter_list|)
block|{
name|btnAddFavourite
operator|.
name|setEnabled
argument_list|(
name|title
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeUpdate
parameter_list|(
name|DocumentEvent
name|arg0
parameter_list|)
block|{
name|btnAddFavourite
operator|.
name|setEnabled
argument_list|(
name|title
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|changedUpdate
parameter_list|(
name|DocumentEvent
name|arg0
parameter_list|)
block|{
name|btnAddFavourite
operator|.
name|setEnabled
argument_list|(
name|title
operator|.
name|getText
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|4
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
literal|20
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|)
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|title
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|title
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Favourites"
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|0
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|5
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridheight
operator|=
literal|4
expr_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|)
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|NORTHEAST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|NONE
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|label
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|favouritesModel
operator|=
operator|new
name|DefaultListModel
argument_list|()
expr_stmt|;
name|Favourite
index|[]
name|f
init|=
name|loadFavourites
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
name|f
operator|.
name|length
condition|;
name|favouritesModel
operator|.
name|addElement
argument_list|(
name|f
index|[
name|i
operator|++
index|]
argument_list|)
control|)
empty_stmt|;
name|favourites
operator|=
operator|new
name|JList
argument_list|(
name|favouritesModel
argument_list|)
expr_stmt|;
name|favourites
operator|.
name|addListSelectionListener
argument_list|(
operator|new
name|ListSelectionListener
argument_list|()
block|{
specifier|public
name|void
name|valueChanged
parameter_list|(
name|ListSelectionEvent
name|e
parameter_list|)
block|{
name|boolean
name|selection
init|=
name|favourites
operator|.
name|getSelectedIndex
argument_list|()
operator|>=
literal|0
decl_stmt|;
name|btnLoadFavourite
operator|.
name|setEnabled
argument_list|(
name|selection
argument_list|)
expr_stmt|;
name|btnRemoveFavourite
operator|.
name|setEnabled
argument_list|(
name|selection
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|favourites
operator|.
name|addMouseListener
argument_list|(
operator|new
name|MouseAdapter
argument_list|()
block|{
specifier|public
name|void
name|mouseClicked
parameter_list|(
name|MouseEvent
name|e
parameter_list|)
block|{
name|super
operator|.
name|mouseClicked
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getClickCount
argument_list|()
operator|==
literal|2
operator|&&
name|favourites
operator|.
name|getSelectedIndex
argument_list|()
operator|>=
literal|0
condition|)
block|{
name|Favourite
name|f
init|=
operator|(
name|Favourite
operator|)
name|favourites
operator|.
name|getSelectedValue
argument_list|()
decl_stmt|;
name|title
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|username
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|password
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|type
operator|.
name|setSelectedIndex
argument_list|(
name|URI_LOCAL
operator|.
name|equals
argument_list|(
name|f
operator|.
name|getUrl
argument_list|()
argument_list|)
condition|?
name|TYPE_LOCAL
else|:
name|TYPE_REMOTE
argument_list|)
expr_stmt|;
name|cur_url
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|JScrollPane
name|scroll
init|=
operator|new
name|JScrollPane
argument_list|(
name|favourites
argument_list|)
decl_stmt|;
name|scroll
operator|.
name|setHorizontalScrollBarPolicy
argument_list|(
name|ScrollPaneConstants
operator|.
name|HORIZONTAL_SCROLLBAR_NEVER
argument_list|)
expr_stmt|;
name|scroll
operator|.
name|setVerticalScrollBarPolicy
argument_list|(
name|ScrollPaneConstants
operator|.
name|VERTICAL_SCROLLBAR_AS_NEEDED
argument_list|)
expr_stmt|;
name|scroll
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|200
argument_list|,
literal|130
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|5
expr_stmt|;
name|c
operator|.
name|gridheight
operator|=
literal|4
expr_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|)
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|NORTHWEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|scroll
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|scroll
argument_list|)
expr_stmt|;
name|btnLoadFavourite
operator|=
operator|new
name|JButton
argument_list|(
literal|"Load"
argument_list|)
expr_stmt|;
name|btnLoadFavourite
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|btnLoadFavourite
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
name|Favourite
name|f
init|=
operator|(
name|Favourite
operator|)
name|favourites
operator|.
name|getSelectedValue
argument_list|()
decl_stmt|;
name|title
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|username
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|password
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|type
operator|.
name|setSelectedIndex
argument_list|(
name|URI_LOCAL
operator|.
name|equals
argument_list|(
name|f
operator|.
name|getUrl
argument_list|()
argument_list|)
condition|?
name|TYPE_LOCAL
else|:
name|TYPE_REMOTE
argument_list|)
expr_stmt|;
name|cur_url
operator|.
name|setText
argument_list|(
name|f
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|5
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridheight
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
name|inset
argument_list|,
name|inset
argument_list|,
literal|15
argument_list|,
name|inset
argument_list|)
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|btnLoadFavourite
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|btnLoadFavourite
argument_list|)
expr_stmt|;
name|btnAddFavourite
operator|=
operator|new
name|JButton
argument_list|(
literal|"Save..."
argument_list|)
expr_stmt|;
name|btnAddFavourite
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|btnAddFavourite
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
name|String
name|t
init|=
name|title
operator|.
name|getText
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
name|favouritesModel
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|favouritesModel
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
operator|.
name|equals
argument_list|(
name|t
argument_list|)
condition|)
block|{
name|int
name|result
init|=
name|JOptionPane
operator|.
name|showConfirmDialog
argument_list|(
name|LoginPanel
operator|.
name|this
argument_list|,
literal|"A connection with this name already exists. Ok to overwrite?"
argument_list|,
literal|"Conflict"
argument_list|,
name|JOptionPane
operator|.
name|YES_NO_OPTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
name|JOptionPane
operator|.
name|NO_OPTION
condition|)
block|{
return|return;
block|}
name|favouritesModel
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|Favourite
name|f
init|=
operator|new
name|Favourite
argument_list|(
name|title
operator|.
name|getText
argument_list|()
argument_list|,
name|username
operator|.
name|getText
argument_list|()
argument_list|,
operator|new
name|String
argument_list|(
name|password
operator|.
name|getPassword
argument_list|()
argument_list|)
argument_list|,
name|cur_url
operator|.
name|getText
argument_list|()
argument_list|)
decl_stmt|;
name|favouritesModel
operator|.
name|addElement
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|storeFavourites
argument_list|(
name|favouritesModel
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|6
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridheight
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|,
name|inset
argument_list|)
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|btnAddFavourite
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|btnAddFavourite
argument_list|)
expr_stmt|;
name|btnRemoveFavourite
operator|=
operator|new
name|JButton
argument_list|(
literal|"Remove"
argument_list|)
expr_stmt|;
name|btnRemoveFavourite
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|btnRemoveFavourite
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
name|favouritesModel
operator|.
name|remove
argument_list|(
name|favourites
operator|.
name|getSelectedIndex
argument_list|()
argument_list|)
expr_stmt|;
name|btnRemoveFavourite
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|btnLoadFavourite
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|storeFavourites
argument_list|(
name|favourites
operator|.
name|getModel
argument_list|()
argument_list|)
expr_stmt|;
name|repaint
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|7
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridheight
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|anchor
operator|=
name|GridBagConstraints
operator|.
name|WEST
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|btnRemoveFavourite
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|btnRemoveFavourite
argument_list|)
expr_stmt|;
name|JPanel
name|spacer
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|c
operator|.
name|gridx
operator|=
literal|2
expr_stmt|;
name|c
operator|.
name|gridy
operator|=
literal|8
expr_stmt|;
name|c
operator|.
name|gridwidth
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|gridheight
operator|=
literal|1
expr_stmt|;
name|c
operator|.
name|fill
operator|=
name|GridBagConstraints
operator|.
name|BOTH
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|spacer
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|spacer
argument_list|)
expr_stmt|;
block|}
comment|/**      * Loads the connection favourites using the Preferences API.      *      * @return the favourites      */
specifier|private
name|Favourite
index|[]
name|loadFavourites
parameter_list|()
block|{
name|Preferences
name|prefs
init|=
name|Preferences
operator|.
name|userNodeForPackage
argument_list|(
name|LoginPanel
operator|.
name|class
argument_list|)
decl_stmt|;
name|Preferences
name|favouritesNode
init|=
name|prefs
operator|.
name|node
argument_list|(
name|FAVOURITES_NODE
argument_list|)
decl_stmt|;
comment|// Get all favourites
name|String
name|favouriteNodeNames
index|[]
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
try|try
block|{
name|favouriteNodeNames
operator|=
name|favouritesNode
operator|.
name|childrenNames
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BackingStoreException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Copy for each connection data into Favourite array
name|Favourite
index|[]
name|favourites
init|=
operator|new
name|Favourite
index|[
name|favouriteNodeNames
operator|.
name|length
index|]
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
name|favouriteNodeNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Preferences
name|node
init|=
name|favouritesNode
operator|.
name|node
argument_list|(
name|favouriteNodeNames
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Favourite
name|favourite
init|=
operator|new
name|Favourite
argument_list|(
name|node
operator|.
name|get
argument_list|(
name|Favourite
operator|.
name|NAME
argument_list|,
literal|""
argument_list|)
argument_list|,
name|node
operator|.
name|get
argument_list|(
name|Favourite
operator|.
name|USERNAME
argument_list|,
literal|""
argument_list|)
argument_list|,
name|node
operator|.
name|get
argument_list|(
name|Favourite
operator|.
name|PASSWORD
argument_list|,
literal|""
argument_list|)
argument_list|,
name|node
operator|.
name|get
argument_list|(
name|Favourite
operator|.
name|URL
argument_list|,
literal|""
argument_list|)
argument_list|)
decl_stmt|;
name|favourites
index|[
name|i
index|]
operator|=
name|favourite
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|favourites
argument_list|)
expr_stmt|;
return|return
name|favourites
return|;
block|}
comment|/**      * Saves the connections favourites using the Preferences API.      *      * @param model the list model      */
specifier|private
name|void
name|storeFavourites
parameter_list|(
name|ListModel
name|model
parameter_list|)
block|{
name|Preferences
name|prefs
init|=
name|Preferences
operator|.
name|userNodeForPackage
argument_list|(
name|LoginPanel
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Clear connection node
name|Preferences
name|favouritesNode
init|=
name|prefs
operator|.
name|node
argument_list|(
name|FAVOURITES_NODE
argument_list|)
decl_stmt|;
try|try
block|{
name|favouritesNode
operator|.
name|removeNode
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BackingStoreException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|// Recreate connection node
name|favouritesNode
operator|=
name|prefs
operator|.
name|node
argument_list|(
name|FAVOURITES_NODE
argument_list|)
expr_stmt|;
comment|// Write a node for each item in model.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|model
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Favourite
name|f
init|=
operator|(
name|Favourite
operator|)
name|model
operator|.
name|getElementAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Preferences
name|favouriteNode
init|=
name|favouritesNode
operator|.
name|node
argument_list|(
literal|""
operator|+
name|i
argument_list|)
decl_stmt|;
name|favouriteNode
operator|.
name|put
argument_list|(
name|Favourite
operator|.
name|NAME
argument_list|,
name|f
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|favouriteNode
operator|.
name|put
argument_list|(
name|Favourite
operator|.
name|USERNAME
argument_list|,
name|f
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
name|favouriteNode
operator|.
name|put
argument_list|(
name|Favourite
operator|.
name|PASSWORD
argument_list|,
name|f
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|favouriteNode
operator|.
name|put
argument_list|(
name|Favourite
operator|.
name|URL
argument_list|,
name|f
operator|.
name|getUrl
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the username that is used to connect to the database.      *      * @return the username      */
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
operator|.
name|getText
argument_list|()
return|;
block|}
comment|/**      * Returns the password that is used to connect to the database.      *      * @return the password      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|password
operator|.
name|getPassword
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Returns the database uri.      *      * @return the uri      */
specifier|public
name|String
name|getUri
parameter_list|()
block|{
return|return
name|cur_url
operator|.
name|getText
argument_list|()
return|;
block|}
comment|/**      * Wrapper used to hold a favourite's connection information.      *      * @author Tobias Wunden      */
specifier|static
class|class
name|Favourite
implements|implements
name|Comparable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USERNAME
init|=
literal|"username"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|URL
init|=
literal|"url"
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|username
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|String
name|url
decl_stmt|;
comment|/**          * Creates a new connection favourite from the given parameters.          *          * @param name the favourite's name          * @param username the username          * @param password the password          * @param url the url          */
specifier|public
name|Favourite
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
comment|/**          * Returns the connection name.          *          * @return the connection name          */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**          * Returns the username.          *          * @return the username          */
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
comment|/**          * Returns the password.          *          * @return the password          */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
comment|/**          * Returns the url.          *          * @return the url          */
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
comment|/**          * Compares<code>o</code> to this favourite by comparing the          * connection names to the object's toString() output.          *          * @see java.util.Comparator#compareTo(Object)          */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|name
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Returns the favourite's hashcode.          *          * @see java.lang.Object#hashCode()          */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**          * Returns<code>true</code> if this favourite equals the given object.          *          * @see java.lang.Object#equals(Object)          */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Returns the connection name.          *          * @return the connection name          */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
block|}
end_class

end_unit

