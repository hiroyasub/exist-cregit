begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|Observable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|JFrame
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
name|JScrollPane
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
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
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
name|SecurityManager
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
name|storage
operator|.
name|DBBroker
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
name|CollectionImpl
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
name|CollectionManagementServiceImpl
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
name|EXistResource
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
name|XPathException
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
name|value
operator|.
name|DateTimeValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|XMLReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|DefaultHandler
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
name|DatabaseManager
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
name|Resource
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

begin_comment
comment|/**  * Restore.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Restore
extends|extends
name|DefaultHandler
block|{
specifier|private
name|File
name|contents
decl_stmt|;
specifier|private
name|String
name|uri
decl_stmt|;
specifier|private
name|String
name|username
decl_stmt|;
specifier|private
name|String
name|pass
decl_stmt|;
specifier|private
name|String
name|adminPass
init|=
literal|null
decl_stmt|;
specifier|private
name|XMLReader
name|reader
decl_stmt|;
specifier|private
name|CollectionImpl
name|current
decl_stmt|;
specifier|private
name|Stack
name|stack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
name|RestoreDialog
name|dialog
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NS
init|=
literal|"http://exist.sourceforge.net/NS/exist"
decl_stmt|;
comment|/** 	 * Constructor for Restore. 	 * @throws XMLDBException  	 */
specifier|public
name|Restore
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|pass
parameter_list|,
name|String
name|newAdminPass
parameter_list|,
name|File
name|contents
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|XMLDBException
block|{
name|this
operator|.
name|username
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|pass
operator|=
name|pass
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
if|if
condition|(
name|newAdminPass
operator|!=
literal|null
condition|)
name|setAdminCredentials
argument_list|(
name|newAdminPass
argument_list|)
expr_stmt|;
name|SAXParserFactory
name|saxFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|SAXParser
name|sax
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|reader
operator|=
name|sax
operator|.
name|getXMLReader
argument_list|()
expr_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|contents
argument_list|)
expr_stmt|;
comment|// check if the system collection is in the backup. We have to process
comment|// this first to create users.
name|File
name|dir
init|=
name|contents
operator|.
name|getParentFile
argument_list|()
decl_stmt|;
comment|//TODO : find a way to make a corespondance with DBRoker's named constants
if|if
condition|(
name|dir
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|File
name|sys
init|=
operator|new
name|File
argument_list|(
name|dir
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"system"
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"__contents__.xml"
argument_list|)
decl_stmt|;
comment|// put the system collection on top of the stack
if|if
condition|(
name|sys
operator|.
name|canRead
argument_list|()
condition|)
block|{
comment|//TODO : find a way to make a corespondance with DBRoker's named constants
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"found 'db"
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"system'. It will be processed first."
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|sys
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|restore
parameter_list|(
name|boolean
name|showGUI
parameter_list|,
name|JFrame
name|parent
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|FileNotFoundException
throws|,
name|IOException
throws|,
name|SAXException
block|{
if|if
condition|(
name|showGUI
condition|)
block|{
name|dialog
operator|=
operator|new
name|RestoreDialog
argument_list|(
name|parent
argument_list|,
literal|"Restoring data ..."
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dialog
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Thread
name|restoreThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|contents
operator|=
operator|(
name|File
operator|)
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|contents
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|dialog
operator|.
name|displayMessage
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|dialog
operator|.
name|displayMessage
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|dialog
operator|.
name|displayMessage
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|dialog
operator|.
name|setVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|restoreThread
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
block|{
while|while
condition|(
name|restoreThread
operator|.
name|isAlive
argument_list|()
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
block|}
block|}
else|else
block|{
while|while
condition|(
operator|!
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|contents
operator|=
operator|(
name|File
operator|)
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|String
name|sysId
init|=
name|contents
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
decl_stmt|;
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|sysId
argument_list|)
decl_stmt|;
name|is
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"restoring "
operator|+
name|sysId
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes) 	 */
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|namespaceURI
operator|.
name|equals
argument_list|(
name|NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"collection"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|owner
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"owner"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|group
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"group"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|mode
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"mode"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|created
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"created"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"collection requires a name "
operator|+
literal|"attribute"
argument_list|)
throw|;
try|try
block|{
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
name|dialog
operator|.
name|displayMessage
argument_list|(
literal|"creating collection "
operator|+
name|name
argument_list|)
expr_stmt|;
name|Date
name|date_created
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
try|try
block|{
name|date_created
operator|=
operator|(
name|Date
operator|)
operator|(
operator|new
name|DateTimeValue
argument_list|(
name|created
argument_list|)
operator|)
operator|.
name|getDate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e2
parameter_list|)
block|{
block|}
name|current
operator|=
name|mkcol
argument_list|(
name|name
argument_list|,
name|date_created
argument_list|)
expr_stmt|;
name|UserManagementService
name|service
init|=
operator|(
name|UserManagementService
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|User
name|u
init|=
operator|new
name|User
argument_list|(
name|owner
argument_list|,
literal|null
argument_list|,
name|group
argument_list|)
decl_stmt|;
name|service
operator|.
name|chown
argument_list|(
name|u
argument_list|,
name|group
argument_list|)
expr_stmt|;
name|service
operator|.
name|chmod
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|mode
argument_list|,
literal|8
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
block|{
name|showErrorMessage
argument_list|(
literal|"An unrecoverable error occurred while restoring\ncollection '"
operator|+
name|name
operator|+
literal|"'. "
operator|+
literal|"Aborting restore!"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"An unrecoverable error occurred while restoring\ncollection '"
operator|+
name|name
operator|+
literal|"'. "
operator|+
literal|"Aborting restore!"
argument_list|)
expr_stmt|;
block|}
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
name|dialog
operator|.
name|setCollection
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"subcollection"
argument_list|)
condition|)
block|{
name|String
name|name
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"filename"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
name|name
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|fname
init|=
name|contents
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|name
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"__contents__.xml"
decl_stmt|;
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|fname
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|exists
argument_list|()
operator|&&
name|f
operator|.
name|canRead
argument_list|()
condition|)
name|stack
operator|.
name|push
argument_list|(
name|f
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" does not exist or is not readable."
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"resource"
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
name|type
operator|=
literal|"XMLResource"
expr_stmt|;
specifier|final
name|String
name|name
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|owner
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"owner"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|group
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"group"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|perms
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"mode"
argument_list|)
decl_stmt|;
name|String
name|filename
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"filename"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|mimetype
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"mimetype"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|created
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"created"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|modified
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"modified"
argument_list|)
decl_stmt|;
if|if
condition|(
name|filename
operator|==
literal|null
condition|)
name|filename
operator|=
name|name
expr_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
name|dialog
operator|.
name|displayMessage
argument_list|(
literal|"Wrong entry in backup descriptor: resource requires a name attribute."
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Wrong entry in backup descriptor: resource requires a name attribute."
argument_list|)
expr_stmt|;
block|}
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|contents
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|filename
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|dialog
operator|!=
literal|null
operator|&&
name|current
operator|instanceof
name|Observable
condition|)
block|{
operator|(
operator|(
name|Observable
operator|)
name|current
operator|)
operator|.
name|addObserver
argument_list|(
name|dialog
operator|.
name|getObserver
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
name|dialog
operator|.
name|setResource
argument_list|(
name|name
argument_list|)
expr_stmt|;
specifier|final
name|Resource
name|res
init|=
name|current
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|mimetype
operator|!=
literal|null
condition|)
operator|(
operator|(
name|EXistResource
operator|)
name|res
operator|)
operator|.
name|setMimeType
argument_list|(
name|mimetype
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|f
argument_list|)
expr_stmt|;
if|if
condition|(
name|dialog
operator|==
literal|null
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Restoring "
operator|+
name|name
argument_list|)
expr_stmt|;
name|Date
name|date_created
init|=
literal|null
decl_stmt|;
name|Date
name|date_modified
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
try|try
block|{
name|date_created
operator|=
operator|(
name|Date
operator|)
operator|(
operator|new
name|DateTimeValue
argument_list|(
name|created
argument_list|)
operator|)
operator|.
name|getDate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e2
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Illegal creation date. Skipping ..."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|modified
operator|!=
literal|null
condition|)
try|try
block|{
name|date_modified
operator|=
operator|(
name|Date
operator|)
operator|(
operator|new
name|DateTimeValue
argument_list|(
name|modified
argument_list|)
operator|)
operator|.
name|getDate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e2
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Illegal modification date. Skipping ..."
argument_list|)
expr_stmt|;
block|}
name|current
operator|.
name|storeResource
argument_list|(
name|res
argument_list|,
name|date_created
argument_list|,
name|date_modified
argument_list|)
expr_stmt|;
name|UserManagementService
name|service
init|=
operator|(
name|UserManagementService
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|User
name|u
init|=
operator|new
name|User
argument_list|(
name|owner
argument_list|,
literal|null
argument_list|,
name|group
argument_list|)
decl_stmt|;
try|try
block|{
name|service
operator|.
name|chown
argument_list|(
name|res
argument_list|,
name|u
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e1
parameter_list|)
block|{
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
block|{
name|dialog
operator|.
name|displayMessage
argument_list|(
literal|"Failed to change owner on document '"
operator|+
name|name
operator|+
literal|"'; skipping ..."
argument_list|)
expr_stmt|;
block|}
block|}
name|service
operator|.
name|chmod
argument_list|(
name|res
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|perms
argument_list|,
literal|8
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
name|dialog
operator|.
name|displayMessage
argument_list|(
literal|"restored "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|dialog
operator|!=
literal|null
condition|)
block|{
name|dialog
operator|.
name|displayMessage
argument_list|(
literal|"Failed to restore resource '"
operator|+
name|name
operator|+
literal|"'\nfrom file '"
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'.\nReason: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|showErrorMessage
argument_list|(
literal|"Failed to restore resource '"
operator|+
name|name
operator|+
literal|"' from file: '"
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'.\n\nReason: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to restore resource '"
operator|+
name|name
operator|+
literal|"' from file '"
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
specifier|final
name|CollectionImpl
name|mkcol
parameter_list|(
name|String
name|collPath
parameter_list|,
name|Date
name|created
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|URISyntaxException
block|{
if|if
condition|(
name|collPath
operator|.
name|startsWith
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
condition|)
name|collPath
operator|=
name|collPath
operator|.
name|substring
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|CollectionManagementServiceImpl
name|mgtService
decl_stmt|;
name|Collection
name|c
decl_stmt|;
name|Collection
name|current
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
name|username
argument_list|,
name|pass
argument_list|)
decl_stmt|;
name|String
name|p
init|=
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|,
name|token
decl_stmt|;
comment|//TODO : use dedicated shared code !
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|collPath
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|token
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|p
operator|=
name|p
operator|+
literal|'/'
operator|+
name|token
expr_stmt|;
name|XmldbURI
name|xmldbURI
init|=
operator|new
name|XmldbURI
argument_list|(
name|uri
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|c
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|xmldbURI
operator|.
name|toString
argument_list|()
argument_list|,
name|username
argument_list|,
name|pass
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|mgtService
operator|=
operator|(
name|CollectionManagementServiceImpl
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
comment|//current = mgtService.createCollection(token);
name|current
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|token
argument_list|,
name|created
argument_list|)
expr_stmt|;
block|}
else|else
name|current
operator|=
name|c
expr_stmt|;
block|}
return|return
operator|(
name|CollectionImpl
operator|)
name|current
return|;
block|}
specifier|private
name|void
name|setAdminCredentials
parameter_list|(
name|String
name|adminPassword
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
name|username
argument_list|,
name|pass
argument_list|)
decl_stmt|;
name|UserManagementService
name|mgmt
init|=
operator|(
name|UserManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|User
name|dba
init|=
name|mgmt
operator|.
name|getUser
argument_list|(
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
decl_stmt|;
name|dba
operator|.
name|setPassword
argument_list|(
name|adminPassword
argument_list|)
expr_stmt|;
name|mgmt
operator|.
name|updateUser
argument_list|(
name|dba
argument_list|)
expr_stmt|;
name|pass
operator|=
name|adminPassword
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|showErrorMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|JTextArea
name|msgArea
init|=
operator|new
name|JTextArea
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|msgArea
operator|.
name|setEditable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|msgArea
operator|.
name|setBackground
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|JScrollPane
name|scroll
init|=
operator|new
name|JScrollPane
argument_list|(
name|msgArea
argument_list|)
decl_stmt|;
name|JOptionPane
name|optionPane
init|=
operator|new
name|JOptionPane
argument_list|()
decl_stmt|;
name|optionPane
operator|.
name|setMessage
argument_list|(
operator|new
name|Object
index|[]
block|{
name|scroll
block|}
argument_list|)
expr_stmt|;
name|optionPane
operator|.
name|setMessageType
argument_list|(
name|JOptionPane
operator|.
name|ERROR_MESSAGE
argument_list|)
expr_stmt|;
name|JDialog
name|dialog
init|=
name|optionPane
operator|.
name|createDialog
argument_list|(
literal|null
argument_list|,
literal|"Error"
argument_list|)
decl_stmt|;
name|dialog
operator|.
name|setResizable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dialog
operator|.
name|pack
argument_list|()
expr_stmt|;
name|dialog
operator|.
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
end_class

end_unit

