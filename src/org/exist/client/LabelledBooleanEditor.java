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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Component
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
name|JTable
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
name|TableCellEditor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|AbstractCellEditor
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
name|SwingConstants
import|;
end_import

begin_comment
comment|/**  * Editor for a LabelledBoolean using a JCheckBox  *  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|LabelledBooleanEditor
extends|extends
name|AbstractCellEditor
implements|implements
name|TableCellEditor
block|{
specifier|private
name|LabelledBoolean
name|current
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Object
name|getCellEditorValue
parameter_list|()
block|{
return|return
name|current
return|;
block|}
annotation|@
name|Override
specifier|public
name|Component
name|getTableCellEditorComponent
parameter_list|(
specifier|final
name|JTable
name|table
parameter_list|,
specifier|final
name|Object
name|value
parameter_list|,
specifier|final
name|boolean
name|isSelected
parameter_list|,
specifier|final
name|int
name|row
parameter_list|,
specifier|final
name|int
name|column
parameter_list|)
block|{
specifier|final
name|LabelledBoolean
name|lb
init|=
operator|(
name|LabelledBoolean
operator|)
name|value
decl_stmt|;
specifier|final
name|JCheckBox
name|chkBox
init|=
operator|new
name|JCheckBox
argument_list|(
name|lb
operator|.
name|getLabel
argument_list|()
argument_list|,
name|lb
operator|.
name|isSet
argument_list|()
argument_list|)
decl_stmt|;
name|chkBox
operator|.
name|setHorizontalAlignment
argument_list|(
name|SwingConstants
operator|.
name|LEFT
argument_list|)
expr_stmt|;
name|chkBox
operator|.
name|setHorizontalTextPosition
argument_list|(
name|SwingConstants
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
name|chkBox
operator|.
name|addActionListener
argument_list|(
operator|new
name|ActionListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|actionPerformed
parameter_list|(
specifier|final
name|ActionEvent
name|e
parameter_list|)
block|{
name|current
operator|=
name|lb
operator|.
name|copy
argument_list|(
operator|!
name|lb
operator|.
name|isSet
argument_list|()
argument_list|)
expr_stmt|;
name|fireEditingStopped
argument_list|()
expr_stmt|;
comment|//notify that editing is done!
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|chkBox
return|;
block|}
block|}
end_class

end_unit

