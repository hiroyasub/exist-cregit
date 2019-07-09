begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|security
package|;
end_package

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
name|table
operator|.
name|DefaultTableModel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|LabelledBoolean
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

begin_comment
comment|/**  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|BasicPermissionsTableModel
extends|extends
name|DefaultTableModel
block|{
specifier|public
name|BasicPermissionsTableModel
parameter_list|(
specifier|final
name|Permission
name|permission
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
operator|new
name|Object
index|[]
block|{
literal|"User"
block|,
operator|(
name|permission
operator|.
name|getOwnerMode
argument_list|()
operator|&
name|Permission
operator|.
name|READ
operator|)
operator|==
name|Permission
operator|.
name|READ
block|,
operator|(
name|permission
operator|.
name|getOwnerMode
argument_list|()
operator|&
name|Permission
operator|.
name|WRITE
operator|)
operator|==
name|Permission
operator|.
name|WRITE
block|,
operator|(
name|permission
operator|.
name|getOwnerMode
argument_list|()
operator|&
name|Permission
operator|.
name|EXECUTE
operator|)
operator|==
name|Permission
operator|.
name|EXECUTE
block|,
operator|new
name|LabelledBoolean
argument_list|(
literal|"SetUID"
argument_list|,
name|permission
operator|.
name|isSetUid
argument_list|()
argument_list|)
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"Group"
block|,
operator|(
name|permission
operator|.
name|getGroupMode
argument_list|()
operator|&
name|Permission
operator|.
name|READ
operator|)
operator|==
name|Permission
operator|.
name|READ
block|,
operator|(
name|permission
operator|.
name|getGroupMode
argument_list|()
operator|&
name|Permission
operator|.
name|WRITE
operator|)
operator|==
name|Permission
operator|.
name|WRITE
block|,
operator|(
name|permission
operator|.
name|getGroupMode
argument_list|()
operator|&
name|Permission
operator|.
name|EXECUTE
operator|)
operator|==
name|Permission
operator|.
name|EXECUTE
block|,
operator|new
name|LabelledBoolean
argument_list|(
literal|"SetGID"
argument_list|,
name|permission
operator|.
name|isSetGid
argument_list|()
argument_list|)
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"Other"
block|,
operator|(
name|permission
operator|.
name|getOtherMode
argument_list|()
operator|&
name|Permission
operator|.
name|READ
operator|)
operator|==
name|Permission
operator|.
name|READ
block|,
operator|(
name|permission
operator|.
name|getOtherMode
argument_list|()
operator|&
name|Permission
operator|.
name|WRITE
operator|)
operator|==
name|Permission
operator|.
name|WRITE
block|,
operator|(
name|permission
operator|.
name|getOtherMode
argument_list|()
operator|&
name|Permission
operator|.
name|EXECUTE
operator|)
operator|==
name|Permission
operator|.
name|EXECUTE
block|,
operator|new
name|LabelledBoolean
argument_list|(
literal|"Sticky"
argument_list|,
name|permission
operator|.
name|isSticky
argument_list|()
argument_list|)
block|}
block|}
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"Permission"
block|,
literal|"Read"
block|,
literal|"Write"
block|,
literal|"Execute"
block|,
literal|"Special"
block|}
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Class
index|[]
name|types
init|=
operator|new
name|Class
index|[]
block|{
name|java
operator|.
name|lang
operator|.
name|String
operator|.
name|class
block|,
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|class
block|,
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|class
block|,
name|java
operator|.
name|lang
operator|.
name|Boolean
operator|.
name|class
block|,
name|LabelledBoolean
operator|.
name|class
block|}
decl_stmt|;
name|boolean
index|[]
name|canEdit
init|=
operator|new
name|boolean
index|[]
block|{
literal|false
block|,
literal|true
block|,
literal|true
block|,
literal|true
block|,
literal|true
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Class
name|getColumnClass
parameter_list|(
name|int
name|columnIndex
parameter_list|)
block|{
return|return
name|types
index|[
name|columnIndex
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCellEditable
parameter_list|(
name|int
name|rowIndex
parameter_list|,
name|int
name|columnIndex
parameter_list|)
block|{
return|return
name|canEdit
index|[
name|columnIndex
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setValueAt
parameter_list|(
name|Object
name|aValue
parameter_list|,
name|int
name|row
parameter_list|,
name|int
name|column
parameter_list|)
block|{
name|super
operator|.
name|setValueAt
argument_list|(
name|aValue
argument_list|,
name|row
argument_list|,
name|column
argument_list|)
expr_stmt|;
comment|//To change body of generated methods, choose Tools | Templates.
block|}
comment|/**      * Get the Mode described by the table model      *       * @return The Unix mode of the permissions      */
specifier|public
name|int
name|getMode
parameter_list|()
block|{
name|int
name|mode
init|=
literal|0
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
name|getRowCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
name|i
argument_list|,
literal|1
argument_list|)
condition|)
block|{
name|mode
operator||=
name|Permission
operator|.
name|READ
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
name|i
argument_list|,
literal|2
argument_list|)
condition|)
block|{
name|mode
operator||=
name|Permission
operator|.
name|WRITE
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
name|i
argument_list|,
literal|3
argument_list|)
condition|)
block|{
name|mode
operator||=
name|Permission
operator|.
name|EXECUTE
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|!=
name|getRowCount
argument_list|()
operator|-
literal|1
condition|)
block|{
name|mode
operator|<<=
literal|3
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
operator|(
name|LabelledBoolean
operator|)
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|4
argument_list|)
operator|)
operator|.
name|isSet
argument_list|()
condition|)
block|{
name|mode
operator||=
operator|(
name|Permission
operator|.
name|SET_UID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|LabelledBoolean
operator|)
name|getValueAt
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
operator|)
operator|.
name|isSet
argument_list|()
condition|)
block|{
name|mode
operator||=
operator|(
name|Permission
operator|.
name|SET_GID
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
operator|(
name|LabelledBoolean
operator|)
name|getValueAt
argument_list|(
literal|2
argument_list|,
literal|4
argument_list|)
operator|)
operator|.
name|isSet
argument_list|()
condition|)
block|{
name|mode
operator||=
operator|(
name|Permission
operator|.
name|STICKY
operator|<<
literal|9
operator|)
expr_stmt|;
block|}
return|return
name|mode
return|;
block|}
block|}
end_class

end_unit

