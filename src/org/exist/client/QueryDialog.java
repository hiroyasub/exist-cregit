begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * QueryDialog.java - Aug 6, 2003  *   * @author wolf  */
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
name|BorderLayout
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|Box
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|ImageIcon
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
name|JComponent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JFrame
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
name|JScrollPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JSpinner
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JSplitPane
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
name|JTextArea
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|ListSelectionModel
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|SpinnerNumberModel
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
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XPathQueryServiceImpl
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
name|Collection
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
name|ResourceIterator
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
name|ResourceSet
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  * To change this generated comment go to   * Window>Preferences>Java>Code Generation>Code and Comments  */
end_comment

begin_class
specifier|public
class|class
name|QueryDialog
extends|extends
name|JFrame
block|{
specifier|private
name|Collection
name|collection
decl_stmt|;
specifier|private
name|Properties
name|properties
decl_stmt|;
specifier|private
name|JComboBox
name|query
decl_stmt|;
specifier|private
name|QueryResultTableModel
name|model
decl_stmt|;
specifier|private
name|JTable
name|resultDocs
decl_stmt|;
specifier|private
name|JTextArea
name|resultDisplay
decl_stmt|;
specifier|private
name|JComboBox
name|collections
init|=
literal|null
decl_stmt|;
specifier|private
name|SpinnerNumberModel
name|count
decl_stmt|;
specifier|public
name|QueryDialog
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|this
operator|.
name|properties
operator|=
name|properties
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
name|getContentPane
argument_list|()
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
name|JComponent
name|qbox
init|=
name|createQueryBox
argument_list|()
decl_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|qbox
argument_list|,
name|BorderLayout
operator|.
name|NORTH
argument_list|)
expr_stmt|;
name|model
operator|=
operator|new
name|QueryResultTableModel
argument_list|()
expr_stmt|;
name|resultDocs
operator|=
operator|new
name|JTable
argument_list|(
name|model
argument_list|)
expr_stmt|;
name|resultDocs
operator|.
name|setSelectionMode
argument_list|(
name|ListSelectionModel
operator|.
name|SINGLE_SELECTION
argument_list|)
expr_stmt|;
name|resultDocs
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
if|if
condition|(
name|e
operator|.
name|getClickCount
argument_list|()
operator|==
literal|1
condition|)
name|tableSelectAction
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|JScrollPane
name|tableScroll
init|=
operator|new
name|JScrollPane
argument_list|(
name|resultDocs
argument_list|)
decl_stmt|;
name|tableScroll
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|250
argument_list|,
literal|150
argument_list|)
argument_list|)
expr_stmt|;
name|resultDisplay
operator|=
operator|new
name|JTextArea
argument_list|(
literal|20
argument_list|,
literal|70
argument_list|)
expr_stmt|;
name|resultDisplay
operator|.
name|setLineWrap
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|JScrollPane
name|resultScroll
init|=
operator|new
name|JScrollPane
argument_list|(
name|resultDisplay
argument_list|)
decl_stmt|;
name|JSplitPane
name|split
init|=
operator|new
name|JSplitPane
argument_list|(
name|JSplitPane
operator|.
name|VERTICAL_SPLIT
argument_list|)
decl_stmt|;
name|split
operator|.
name|add
argument_list|(
name|tableScroll
argument_list|)
expr_stmt|;
name|split
operator|.
name|add
argument_list|(
name|resultScroll
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|split
argument_list|,
name|BorderLayout
operator|.
name|SOUTH
argument_list|)
expr_stmt|;
block|}
specifier|private
name|JComponent
name|createQueryBox
parameter_list|()
block|{
name|Box
name|vbox
init|=
name|Box
operator|.
name|createVerticalBox
argument_list|()
decl_stmt|;
name|Box
name|hbox
init|=
name|Box
operator|.
name|createHorizontalBox
argument_list|()
decl_stmt|;
name|Vector
name|data
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|data
operator|.
name|addElement
argument_list|(
literal|"*"
argument_list|)
expr_stmt|;
try|try
block|{
name|getCollections
argument_list|(
name|collection
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"An error occurred while retrieving collections list"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|collections
operator|=
operator|new
name|JComboBox
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|collections
argument_list|)
expr_stmt|;
name|query
operator|=
operator|new
name|JComboBox
argument_list|()
expr_stmt|;
name|query
operator|.
name|setEditable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|query
operator|.
name|setPreferredSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|350
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|URL
name|url
init|=
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"icons/Find24.gif"
argument_list|)
decl_stmt|;
name|JButton
name|button
init|=
operator|new
name|JButton
argument_list|(
literal|"Submit"
argument_list|,
operator|new
name|ImageIcon
argument_list|(
name|url
argument_list|)
argument_list|)
decl_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|button
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
name|doQuery
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|vbox
operator|.
name|add
argument_list|(
name|hbox
argument_list|)
expr_stmt|;
name|hbox
operator|=
name|Box
operator|.
name|createHorizontalBox
argument_list|()
expr_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|Box
operator|.
name|createHorizontalGlue
argument_list|()
argument_list|)
expr_stmt|;
name|JLabel
name|label
init|=
operator|new
name|JLabel
argument_list|(
literal|"Show max. results: "
argument_list|)
decl_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|label
argument_list|)
expr_stmt|;
name|count
operator|=
operator|new
name|SpinnerNumberModel
argument_list|(
literal|100
argument_list|,
literal|1
argument_list|,
literal|10000
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|JSpinner
name|spinner
init|=
operator|new
name|JSpinner
argument_list|(
name|count
argument_list|)
decl_stmt|;
name|spinner
operator|.
name|setMaximumSize
argument_list|(
operator|new
name|Dimension
argument_list|(
literal|160
argument_list|,
literal|25
argument_list|)
argument_list|)
expr_stmt|;
name|hbox
operator|.
name|add
argument_list|(
name|spinner
argument_list|)
expr_stmt|;
name|vbox
operator|.
name|add
argument_list|(
name|hbox
argument_list|)
expr_stmt|;
return|return
name|vbox
return|;
block|}
specifier|private
name|Vector
name|getCollections
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|Vector
name|collectionsList
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|collectionsList
operator|.
name|add
argument_list|(
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|String
index|[]
name|childCollections
init|=
name|collection
operator|.
name|listChildCollections
argument_list|()
decl_stmt|;
name|Collection
name|child
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
name|childCollections
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|child
operator|=
name|collection
operator|.
name|getChildCollection
argument_list|(
name|childCollections
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|getCollections
argument_list|(
name|child
argument_list|,
name|collectionsList
argument_list|)
expr_stmt|;
block|}
return|return
name|collectionsList
return|;
block|}
specifier|private
name|void
name|doQuery
parameter_list|()
block|{
name|String
name|xpath
init|=
operator|(
name|String
operator|)
name|query
operator|.
name|getSelectedItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|xpath
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|resultDisplay
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
name|XPathQueryServiceImpl
name|service
init|=
operator|(
name|XPathQueryServiceImpl
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
literal|"pretty"
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"indent"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"document("
argument_list|)
operator|||
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"collection("
argument_list|)
operator|||
name|xpath
operator|.
name|startsWith
argument_list|(
literal|"xcollection("
argument_list|)
operator|)
condition|)
block|{
name|String
name|collname
init|=
operator|(
name|String
operator|)
name|collections
operator|.
name|getSelectedItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|collname
operator|.
name|equals
argument_list|(
literal|"*"
argument_list|)
condition|)
name|xpath
operator|=
literal|"document()"
operator|+
name|xpath
expr_stmt|;
else|else
name|xpath
operator|=
literal|"collection(\""
operator|+
name|collname
operator|+
literal|"\")"
operator|+
name|xpath
expr_stmt|;
block|}
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|xpath
argument_list|)
decl_stmt|;
name|model
operator|.
name|setResourceSet
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"An exception occurred during query execution: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|addItem
argument_list|(
name|xpath
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|tableSelectAction
parameter_list|(
name|MouseEvent
name|ev
parameter_list|)
block|{
name|int
name|row
init|=
name|resultDocs
operator|.
name|rowAtPoint
argument_list|(
name|ev
operator|.
name|getPoint
argument_list|()
argument_list|)
decl_stmt|;
name|resultDisplay
operator|.
name|setText
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|ArrayList
name|results
init|=
name|model
operator|.
name|data
index|[
name|row
index|]
decl_stmt|;
name|XMLResource
name|resource
decl_stmt|;
name|int
name|howmany
init|=
name|count
operator|.
name|getNumber
argument_list|()
operator|.
name|intValue
argument_list|()
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|results
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
operator|&&
name|j
operator|<
name|howmany
condition|;
name|j
operator|++
control|)
block|{
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
try|try
block|{
name|resultDisplay
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|resource
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|resultDisplay
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|resultDisplay
operator|.
name|setCaretPosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
literal|"An error occurred while retrieving results: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
class|class
name|QueryResultTableModel
extends|extends
name|AbstractTableModel
block|{
name|ArrayList
name|data
index|[]
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|setResourceSet
parameter_list|(
name|ResourceSet
name|results
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|TreeMap
name|docs
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
name|XMLResource
name|current
decl_stmt|;
name|ArrayList
name|hits
decl_stmt|;
for|for
control|(
name|ResourceIterator
name|i
init|=
name|results
operator|.
name|getIterator
argument_list|()
init|;
name|i
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|current
operator|=
operator|(
name|XMLResource
operator|)
name|i
operator|.
name|nextResource
argument_list|()
expr_stmt|;
name|hits
operator|=
operator|(
name|ArrayList
operator|)
name|docs
operator|.
name|get
argument_list|(
name|current
operator|.
name|getDocumentId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|==
literal|null
condition|)
block|{
name|hits
operator|=
operator|new
name|ArrayList
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|docs
operator|.
name|put
argument_list|(
name|current
operator|.
name|getDocumentId
argument_list|()
argument_list|,
name|hits
argument_list|)
expr_stmt|;
block|}
name|hits
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
name|data
operator|=
operator|new
name|ArrayList
index|[
name|docs
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|docs
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|hits
operator|=
operator|(
name|ArrayList
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|data
index|[
name|j
index|]
operator|=
name|hits
expr_stmt|;
block|}
name|this
operator|.
name|fireTableDataChanged
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getColumnCount
parameter_list|()
block|{
return|return
literal|2
return|;
block|}
specifier|public
name|int
name|getRowCount
parameter_list|()
block|{
return|return
name|data
operator|==
literal|null
condition|?
literal|0
else|:
name|data
operator|.
name|length
return|;
block|}
specifier|public
name|String
name|getColumnName
parameter_list|(
name|int
name|column
parameter_list|)
block|{
switch|switch
condition|(
name|column
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|"Document"
return|;
default|default :
return|return
literal|"Hits"
return|;
block|}
block|}
specifier|public
name|Object
name|getValueAt
parameter_list|(
name|int
name|rowIndex
parameter_list|,
name|int
name|columnIndex
parameter_list|)
block|{
switch|switch
condition|(
name|columnIndex
condition|)
block|{
case|case
literal|0
case|:
try|try
block|{
return|return
operator|(
operator|(
name|XMLResource
operator|)
name|data
index|[
name|rowIndex
index|]
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getDocumentId
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
case|case
literal|1
case|:
return|return
operator|new
name|Integer
argument_list|(
name|data
index|[
name|rowIndex
index|]
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

