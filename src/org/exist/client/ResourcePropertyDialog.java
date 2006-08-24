begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ResourcePropertyDialog.java - Jun 17, 2003  *   * @author wolf  */
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
name|Frame
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
name|HeadlessException
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
name|WindowAdapter
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
name|WindowEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|BorderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|Box
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
name|JCheckBox
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
name|JComponent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JDialog
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
name|border
operator|.
name|EtchedBorder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Permission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|UserManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|URIUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_class
specifier|public
class|class
name|ResourcePropertyDialog
extends|extends
name|JDialog
block|{
specifier|public
specifier|final
specifier|static
name|int
name|NO_OPTION
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|APPLY_OPTION
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|CANCEL_OPTION
init|=
literal|1
decl_stmt|;
name|Permission
name|permissions
decl_stmt|;
name|XmldbURI
name|resource
decl_stmt|;
name|UserManagementService
name|service
decl_stmt|;
name|Date
name|creationDate
decl_stmt|;
name|Date
name|modificationDate
decl_stmt|;
name|String
name|mimeType
decl_stmt|;
name|JComboBox
name|groups
decl_stmt|;
name|JComboBox
name|owners
decl_stmt|;
name|JCheckBox
index|[]
name|worldPerms
decl_stmt|;
name|JCheckBox
index|[]
name|groupPerms
decl_stmt|;
name|JCheckBox
index|[]
name|userPerms
decl_stmt|;
name|int
name|result
init|=
name|NO_OPTION
decl_stmt|;
comment|/** 	 * @param owner 	 * @param mgt 	 * @param res          * @param perm          * @param created          * @param modified          * @param mimeType 	 * @throws java.awt.HeadlessException 	 */
specifier|public
name|ResourcePropertyDialog
parameter_list|(
name|Frame
name|owner
parameter_list|,
name|UserManagementService
name|mgt
parameter_list|,
name|XmldbURI
name|res
parameter_list|,
name|Permission
name|perm
parameter_list|,
name|Date
name|created
parameter_list|,
name|Date
name|modified
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|HeadlessException
throws|,
name|XMLDBException
block|{
name|super
argument_list|(
name|owner
argument_list|,
literal|"Edit Properties"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|service
operator|=
name|mgt
expr_stmt|;
name|this
operator|.
name|permissions
operator|=
name|perm
expr_stmt|;
name|this
operator|.
name|creationDate
operator|=
name|created
expr_stmt|;
name|this
operator|.
name|modificationDate
operator|=
name|modified
expr_stmt|;
name|this
operator|.
name|resource
operator|=
name|res
expr_stmt|;
name|this
operator|.
name|mimeType
operator|=
name|mimeType
operator|==
literal|null
condition|?
literal|"N/A"
else|:
name|mimeType
expr_stmt|;
name|setupComponents
argument_list|()
expr_stmt|;
name|addWindowListener
argument_list|(
operator|new
name|WindowAdapter
argument_list|()
block|{
specifier|public
name|void
name|windowClosing
parameter_list|(
name|WindowEvent
name|ev
parameter_list|)
block|{
name|cancelAction
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
specifier|public
name|Permission
name|getPermissions
parameter_list|()
block|{
return|return
name|permissions
return|;
block|}
specifier|private
name|void
name|setupComponents
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|GridBagLayout
name|grid
init|=
operator|new
name|GridBagLayout
argument_list|()
decl_stmt|;
name|getContentPane
argument_list|()
operator|.
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
name|c
operator|.
name|insets
operator|=
operator|new
name|Insets
argument_list|(
literal|5
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
literal|"Resource:"
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|resource
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
literal|0
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
name|HORIZONTAL
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Mime:"
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
name|HORIZONTAL
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
name|mimeType
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
name|HORIZONTAL
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Created:"
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|String
name|date
init|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|()
operator|.
name|format
argument_list|(
name|creationDate
argument_list|)
decl_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
name|date
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
name|HORIZONTAL
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Last modified:"
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|date
operator|=
name|modificationDate
operator|!=
literal|null
condition|?
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|()
operator|.
name|format
argument_list|(
name|modificationDate
argument_list|)
else|:
literal|"not available"
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
name|date
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
name|HORIZONTAL
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Owner"
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|Vector
name|ol
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|User
name|users
index|[]
init|=
name|service
operator|.
name|getUsers
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
name|users
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ol
operator|.
name|addElement
argument_list|(
name|users
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|owners
operator|=
operator|new
name|JComboBox
argument_list|(
name|ol
argument_list|)
expr_stmt|;
name|owners
operator|.
name|setSelectedItem
argument_list|(
name|permissions
operator|.
name|getOwner
argument_list|()
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
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|owners
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|owners
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Group"
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
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|Vector
name|gl
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|String
name|allGroups
index|[]
init|=
name|service
operator|.
name|getGroups
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
name|allGroups
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|gl
operator|.
name|addElement
argument_list|(
name|allGroups
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|groups
operator|=
operator|new
name|JComboBox
argument_list|(
name|gl
argument_list|)
expr_stmt|;
name|groups
operator|.
name|setSelectedItem
argument_list|(
name|permissions
operator|.
name|getOwnerGroup
argument_list|()
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
name|HORIZONTAL
expr_stmt|;
name|grid
operator|.
name|setConstraints
argument_list|(
name|groups
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|groups
argument_list|)
expr_stmt|;
name|JComponent
name|pc
init|=
name|setupPermissions
argument_list|()
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
literal|6
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
name|EAST
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
name|pc
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|pc
argument_list|)
expr_stmt|;
name|Box
name|buttonBox
init|=
name|Box
operator|.
name|createHorizontalBox
argument_list|()
decl_stmt|;
name|JButton
name|button
init|=
operator|new
name|JButton
argument_list|(
literal|"Apply"
argument_list|)
decl_stmt|;
name|button
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
name|applyAction
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|buttonBox
operator|.
name|add
argument_list|(
name|button
argument_list|)
expr_stmt|;
name|button
operator|=
operator|new
name|JButton
argument_list|(
literal|"Cancel"
argument_list|)
expr_stmt|;
name|button
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
name|cancelAction
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|buttonBox
operator|.
name|add
argument_list|(
name|button
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
name|buttonBox
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|buttonBox
argument_list|)
expr_stmt|;
block|}
specifier|private
name|JComponent
name|setupPermissions
parameter_list|()
block|{
name|Box
name|hbox
init|=
name|Box
operator|.
name|createHorizontalBox
argument_list|()
decl_stmt|;
name|hbox
operator|.
name|setBorder
argument_list|(
name|BorderFactory
operator|.
name|createTitledBorder
argument_list|(
name|BorderFactory
operator|.
name|createEtchedBorder
argument_list|(
name|EtchedBorder
operator|.
name|RAISED
argument_list|)
argument_list|,
literal|"Permissions"
argument_list|)
argument_list|)
expr_stmt|;
name|userPerms
operator|=
operator|new
name|JCheckBox
index|[
literal|3
index|]
expr_stmt|;
name|JComponent
name|c
init|=
name|getPermissionsBox
argument_list|(
literal|"user"
argument_list|,
name|userPerms
argument_list|,
name|permissions
operator|.
name|getUserPermissions
argument_list|()
argument_list|)
decl_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|groupPerms
operator|=
operator|new
name|JCheckBox
index|[
literal|3
index|]
expr_stmt|;
name|c
operator|=
name|getPermissionsBox
argument_list|(
literal|"group"
argument_list|,
name|groupPerms
argument_list|,
name|permissions
operator|.
name|getGroupPermissions
argument_list|()
argument_list|)
expr_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|worldPerms
operator|=
operator|new
name|JCheckBox
index|[
literal|3
index|]
expr_stmt|;
name|c
operator|=
name|getPermissionsBox
argument_list|(
literal|"world"
argument_list|,
name|worldPerms
argument_list|,
name|permissions
operator|.
name|getPublicPermissions
argument_list|()
argument_list|)
expr_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|hbox
return|;
block|}
specifier|private
name|JComponent
name|getPermissionsBox
parameter_list|(
name|String
name|title
parameter_list|,
name|JCheckBox
index|[]
name|perms
parameter_list|,
name|int
name|current
parameter_list|)
block|{
name|Box
name|vbox
init|=
name|Box
operator|.
name|createVerticalBox
argument_list|()
decl_stmt|;
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
name|title
argument_list|)
decl_stmt|;
name|vbox
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|perms
index|[
literal|0
index|]
operator|=
operator|new
name|JCheckBox
argument_list|(
literal|"read"
argument_list|,
operator|(
name|current
operator|&
literal|0x4
operator|)
operator|==
literal|0x4
argument_list|)
expr_stmt|;
name|vbox
operator|.
name|add
argument_list|(
name|perms
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|perms
index|[
literal|1
index|]
operator|=
operator|new
name|JCheckBox
argument_list|(
literal|"write"
argument_list|,
operator|(
name|current
operator|&
literal|0x2
operator|)
operator|==
literal|0x2
argument_list|)
expr_stmt|;
name|vbox
operator|.
name|add
argument_list|(
name|perms
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|perms
index|[
literal|2
index|]
operator|=
operator|new
name|JCheckBox
argument_list|(
literal|"update"
argument_list|,
operator|(
name|current
operator|&
literal|0x1
operator|)
operator|==
literal|0x1
argument_list|)
expr_stmt|;
name|vbox
operator|.
name|add
argument_list|(
name|perms
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
return|return
name|vbox
return|;
block|}
specifier|private
name|int
name|checkPermissions
parameter_list|(
name|JCheckBox
name|cb
index|[]
parameter_list|)
block|{
name|int
name|perm
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|cb
index|[
literal|0
index|]
operator|.
name|isSelected
argument_list|()
condition|)
name|perm
operator||=
literal|4
expr_stmt|;
if|if
condition|(
name|cb
index|[
literal|1
index|]
operator|.
name|isSelected
argument_list|()
condition|)
name|perm
operator||=
literal|2
expr_stmt|;
if|if
condition|(
name|cb
index|[
literal|2
index|]
operator|.
name|isSelected
argument_list|()
condition|)
name|perm
operator||=
literal|1
expr_stmt|;
return|return
name|perm
return|;
block|}
specifier|private
name|void
name|cancelAction
parameter_list|()
block|{
name|this
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|result
operator|=
name|CANCEL_OPTION
expr_stmt|;
block|}
specifier|private
name|void
name|applyAction
parameter_list|()
block|{
name|permissions
operator|.
name|setOwner
argument_list|(
operator|(
name|String
operator|)
name|owners
operator|.
name|getSelectedItem
argument_list|()
argument_list|)
expr_stmt|;
name|permissions
operator|.
name|setGroup
argument_list|(
operator|(
name|String
operator|)
name|groups
operator|.
name|getSelectedItem
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|perms
init|=
operator|(
name|checkPermissions
argument_list|(
name|userPerms
argument_list|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|checkPermissions
argument_list|(
name|groupPerms
argument_list|)
operator|<<
literal|3
operator|)
operator||
name|checkPermissions
argument_list|(
name|worldPerms
argument_list|)
decl_stmt|;
name|permissions
operator|.
name|setPermissions
argument_list|(
name|perms
argument_list|)
expr_stmt|;
name|this
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|result
operator|=
name|APPLY_OPTION
expr_stmt|;
block|}
block|}
end_class

end_unit

