begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2020 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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

begin_comment
comment|/**  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
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
name|ModeDisplay
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
name|permission
operator|.
name|ownerRead
block|,
name|permission
operator|.
name|ownerWrite
block|,
name|permission
operator|.
name|ownerExecute
block|,
operator|new
name|LabelledBoolean
argument_list|(
literal|"SetUID"
argument_list|,
name|permission
operator|.
name|setUid
argument_list|)
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"Group"
block|,
name|permission
operator|.
name|groupRead
block|,
name|permission
operator|.
name|groupWrite
block|,
name|permission
operator|.
name|groupExecute
block|,
operator|new
name|LabelledBoolean
argument_list|(
literal|"SetGID"
argument_list|,
name|permission
operator|.
name|setGid
argument_list|)
block|}
block|,
operator|new
name|Object
index|[]
block|{
literal|"Other"
block|,
name|permission
operator|.
name|otherRead
block|,
name|permission
operator|.
name|otherWrite
block|,
name|permission
operator|.
name|otherExecute
block|,
operator|new
name|LabelledBoolean
argument_list|(
literal|"Sticky"
argument_list|,
name|permission
operator|.
name|sticky
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
specifier|final
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
name|ModeDisplay
name|getMode
parameter_list|()
block|{
specifier|final
name|ModeDisplay
name|modeDisplay
init|=
operator|new
name|ModeDisplay
argument_list|()
decl_stmt|;
name|modeDisplay
operator|.
name|ownerRead
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|ownerWrite
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|ownerExecute
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|setUid
operator|=
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
expr_stmt|;
name|modeDisplay
operator|.
name|groupRead
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|groupWrite
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|groupExecute
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|setGid
operator|=
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
expr_stmt|;
name|modeDisplay
operator|.
name|otherRead
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|otherWrite
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|otherExecute
operator|=
operator|(
name|Boolean
operator|)
name|getValueAt
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|modeDisplay
operator|.
name|sticky
operator|=
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
expr_stmt|;
return|return
name|modeDisplay
return|;
block|}
block|}
end_class

end_unit

