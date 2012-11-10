begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|Component
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
name|JLabel
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
name|AbstractTableModel
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
name|DefaultTableCellRenderer
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
name|ClientFrame
operator|.
name|ResourceTableModel
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

begin_class
specifier|public
class|class
name|HighlightedTableCellRenderer
parameter_list|<
name|T
extends|extends
name|AbstractTableModel
parameter_list|>
extends|extends
name|DefaultTableCellRenderer
block|{
specifier|private
specifier|final
specifier|static
name|Color
name|collectionBackground
init|=
operator|new
name|Color
argument_list|(
literal|225
argument_list|,
literal|235
argument_list|,
literal|224
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Color
name|collectionForeground
init|=
name|Color
operator|.
name|black
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Color
name|highBackground
init|=
operator|new
name|Color
argument_list|(
literal|115
argument_list|,
literal|130
argument_list|,
literal|189
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Color
name|highForeground
init|=
name|Color
operator|.
name|white
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Color
name|altBackground
init|=
operator|new
name|Color
argument_list|(
literal|235
argument_list|,
literal|235
argument_list|,
literal|235
argument_list|)
decl_stmt|;
comment|/*      * (non-Javadoc)      *      * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,      *           java.lang.Object, boolean, boolean, int, int)      */
annotation|@
name|Override
specifier|public
name|Component
name|getTableCellRendererComponent
parameter_list|(
specifier|final
name|JTable
name|table
parameter_list|,
name|Object
name|value
parameter_list|,
specifier|final
name|boolean
name|isSelected
parameter_list|,
specifier|final
name|boolean
name|hasFocus
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
if|if
condition|(
name|value
operator|instanceof
name|XmldbURI
condition|)
block|{
name|value
operator|=
operator|new
name|PrettyXmldbURI
argument_list|(
operator|(
name|XmldbURI
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Component
name|renderer
init|=
name|super
operator|.
name|getTableCellRendererComponent
argument_list|(
name|table
argument_list|,
name|value
argument_list|,
name|isSelected
argument_list|,
name|hasFocus
argument_list|,
name|row
argument_list|,
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|renderer
operator|instanceof
name|JCheckBox
condition|)
block|{
operator|(
operator|(
name|JCheckBox
operator|)
name|renderer
operator|)
operator|.
name|setOpaque
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|renderer
operator|instanceof
name|JLabel
condition|)
block|{
operator|(
operator|(
name|JLabel
operator|)
name|renderer
operator|)
operator|.
name|setOpaque
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Color
name|foreground
decl_stmt|,
name|background
decl_stmt|;
specifier|final
name|T
name|resources
init|=
operator|(
name|T
operator|)
name|table
operator|.
name|getModel
argument_list|()
decl_stmt|;
if|if
condition|(
name|isSelected
condition|)
block|{
name|foreground
operator|=
name|highForeground
expr_stmt|;
name|background
operator|=
name|highBackground
expr_stmt|;
block|}
if|else if
condition|(
name|resources
operator|instanceof
name|ResourceTableModel
operator|&&
operator|(
operator|(
name|ResourceTableModel
operator|)
name|resources
operator|)
operator|.
name|getRow
argument_list|(
name|row
argument_list|)
operator|.
name|isCollection
argument_list|()
condition|)
block|{
name|foreground
operator|=
name|collectionForeground
expr_stmt|;
name|background
operator|=
name|collectionBackground
expr_stmt|;
block|}
if|else if
condition|(
name|row
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
name|background
operator|=
name|altBackground
expr_stmt|;
name|foreground
operator|=
name|Color
operator|.
name|black
expr_stmt|;
block|}
else|else
block|{
name|foreground
operator|=
name|Color
operator|.
name|black
expr_stmt|;
name|background
operator|=
name|Color
operator|.
name|white
expr_stmt|;
block|}
name|renderer
operator|.
name|setForeground
argument_list|(
name|foreground
argument_list|)
expr_stmt|;
name|renderer
operator|.
name|setBackground
argument_list|(
name|background
argument_list|)
expr_stmt|;
return|return
name|renderer
return|;
block|}
block|}
end_class

end_unit

