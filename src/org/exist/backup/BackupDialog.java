begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
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
name|JProgressBar
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

begin_class
specifier|public
class|class
name|BackupDialog
extends|extends
name|JDialog
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4960002499478536048L
decl_stmt|;
name|JTextField
name|currentCollection
decl_stmt|;
name|JTextField
name|currentFile
decl_stmt|;
name|JProgressBar
name|progress
decl_stmt|;
specifier|public
name|BackupDialog
parameter_list|()
throws|throws
name|HeadlessException
block|{
name|super
argument_list|()
expr_stmt|;
name|setupComponents
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param owner 	 * @param modal 	 * @throws java.awt.HeadlessException 	 */
specifier|public
name|BackupDialog
parameter_list|(
name|Frame
name|owner
parameter_list|,
name|boolean
name|modal
parameter_list|)
throws|throws
name|HeadlessException
block|{
name|super
argument_list|(
name|owner
argument_list|,
literal|"Backup"
argument_list|,
name|modal
argument_list|)
expr_stmt|;
name|setupComponents
argument_list|()
expr_stmt|;
name|pack
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setupComponents
parameter_list|()
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
literal|"Collection:"
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
name|currentCollection
operator|=
operator|new
name|JTextField
argument_list|(
literal|40
argument_list|)
expr_stmt|;
name|currentCollection
operator|.
name|setEditable
argument_list|(
literal|false
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
name|currentCollection
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|currentCollection
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Storing file:"
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
name|currentFile
operator|=
operator|new
name|JTextField
argument_list|(
literal|40
argument_list|)
expr_stmt|;
name|currentFile
operator|.
name|setEditable
argument_list|(
literal|false
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
name|currentFile
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|currentFile
argument_list|)
expr_stmt|;
name|label
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Collection Progress:"
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
name|progress
operator|=
operator|new
name|JProgressBar
argument_list|()
expr_stmt|;
name|progress
operator|.
name|setIndeterminate
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|progress
operator|.
name|setStringPainted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|progress
operator|.
name|setMinimumSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|200
argument_list|,
literal|30
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
name|progress
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|progress
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|currentCollection
operator|.
name|setText
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setResource
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|currentFile
operator|.
name|setText
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setResourceCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|progress
operator|.
name|setMaximum
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setProgress
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|progress
operator|.
name|setValue
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

