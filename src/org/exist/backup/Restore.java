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
name|org
operator|.
name|exist
operator|.
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentTypeImpl
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
name|util
operator|.
name|EXistInputSource
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
name|util
operator|.
name|URIUtils
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
name|w3c
operator|.
name|dom
operator|.
name|DocumentType
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
name|modules
operator|.
name|CollectionManagementService
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

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|*
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
name|Properties
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
name|BackupDescriptor
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
specifier|private
name|int
name|version
init|=
literal|0
decl_stmt|;
specifier|private
name|RestoreListener
name|listener
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|strictUriVersion
init|=
literal|1
decl_stmt|;
comment|/** 	 * Constructor for Restore. 	 * @throws XMLDBException  	 * @throws URISyntaxException  	 */
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
throws|,
name|URISyntaxException
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
name|this
operator|.
name|listener
operator|=
operator|new
name|DefaultListener
argument_list|()
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
do|do
block|{
name|BackupDescriptor
name|bd
init|=
literal|null
decl_stmt|;
name|Properties
name|properties
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|contents
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|bd
operator|=
operator|new
name|FileSystemBackupDescriptor
argument_list|(
operator|new
name|File
argument_list|(
name|contents
argument_list|,
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|contents
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
operator|||
name|contents
operator|.
name|getName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".ZIP"
argument_list|)
condition|)
block|{
name|bd
operator|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|contents
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bd
operator|=
operator|new
name|FileSystemBackupDescriptor
argument_list|(
name|contents
argument_list|)
expr_stmt|;
block|}
name|properties
operator|=
name|bd
operator|.
name|getProperties
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Unable to create backup descriptor object from "
operator|+
name|contents
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|stack
operator|.
name|push
argument_list|(
name|bd
argument_list|)
expr_stmt|;
comment|// check if the system collection is in the backup. We have to process
comment|// this first to create users.
comment|//TODO : find a way to make a corespondance with DBRoker's named constants
name|BackupDescriptor
name|sysbd
init|=
name|bd
operator|.
name|getChildBackupDescriptor
argument_list|(
literal|"system"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sysbd
operator|!=
literal|null
condition|)
block|{
name|stack
operator|.
name|push
argument_list|(
name|sysbd
argument_list|)
expr_stmt|;
block|}
name|contents
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|properties
operator|!=
literal|null
operator|&&
name|properties
operator|.
name|getProperty
argument_list|(
literal|"incremental"
argument_list|,
literal|"no"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
name|String
name|previous
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"previous"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|contents
operator|=
operator|new
name|File
argument_list|(
name|bd
operator|.
name|getParentDir
argument_list|()
argument_list|,
name|previous
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|contents
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Required part of incremental backup not found: "
operator|+
name|contents
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
do|while
condition|(
name|contents
operator|!=
literal|null
condition|)
do|;
block|}
specifier|public
name|void
name|setListener
parameter_list|(
name|RestoreListener
name|listener
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
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
name|BackupDescriptor
operator|)
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|dialog
operator|.
name|setBackup
argument_list|(
name|contents
operator|.
name|getSymbolicPath
argument_list|()
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|contents
operator|.
name|getInputSource
argument_list|()
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
name|BackupDescriptor
operator|)
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
name|EXistInputSource
name|is
init|=
name|contents
operator|.
name|getInputSource
argument_list|()
decl_stmt|;
name|is
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
comment|//restoring sysId
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
name|Namespaces
operator|.
name|EXIST_NS
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
name|String
name|strVersion
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
if|if
condition|(
name|strVersion
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|this
operator|.
name|version
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|strVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
literal|0
expr_stmt|;
block|}
block|}
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
name|listener
operator|.
name|createCollection
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|XmldbURI
name|collUri
decl_stmt|;
if|if
condition|(
name|version
operator|>=
name|strictUriVersion
condition|)
block|{
name|collUri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|collUri
operator|=
name|URIUtils
operator|.
name|encodeXmldbUriFor
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|listener
operator|.
name|warn
argument_list|(
literal|"Could not parse document name into a URI: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
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
operator|new
name|DateTimeValue
argument_list|(
name|created
argument_list|)
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
name|collUri
argument_list|,
name|date_created
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Collection not found: "
operator|+
name|collUri
argument_list|)
throw|;
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
name|listener
operator|.
name|warn
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
block|{
name|name
operator|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
expr_stmt|;
block|}
name|BackupDescriptor
name|subbd
init|=
name|contents
operator|.
name|getChildBackupDescriptor
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|subbd
operator|!=
literal|null
condition|)
name|stack
operator|.
name|push
argument_list|(
name|subbd
argument_list|)
expr_stmt|;
else|else
name|listener
operator|.
name|warn
argument_list|(
name|name
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
name|skip
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"skip"
argument_list|)
decl_stmt|;
if|if
condition|(
name|skip
operator|==
literal|null
operator|||
name|skip
operator|.
name|equals
argument_list|(
literal|"no"
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
specifier|final
name|String
name|publicid
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"publicid"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|systemid
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"systemid"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|namedoctype
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"namedoctype"
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
name|listener
operator|.
name|warn
argument_list|(
literal|"Wrong entry in backup descriptor: resource requires a name attribute."
argument_list|)
expr_stmt|;
block|}
name|XmldbURI
name|docUri
decl_stmt|;
if|if
condition|(
name|version
operator|>=
name|strictUriVersion
condition|)
block|{
name|docUri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|docUri
operator|=
name|URIUtils
operator|.
name|encodeXmldbUriFor
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
name|listener
operator|.
name|warn
argument_list|(
literal|"Could not parse document name into a URI: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|EXistInputSource
name|is
init|=
name|contents
operator|.
name|getInputSource
argument_list|(
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
name|docUri
operator|.
name|toString
argument_list|()
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
if|if
condition|(
name|is
operator|.
name|getByteStreamLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|res
operator|.
name|setContent
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|setContent
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
comment|// Restoring name
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
name|listener
operator|.
name|warn
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
name|listener
operator|.
name|warn
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
if|if
condition|(
name|publicid
operator|!=
literal|null
operator|||
name|systemid
operator|!=
literal|null
condition|)
block|{
name|DocumentType
name|doctype
init|=
operator|new
name|DocumentTypeImpl
argument_list|(
name|namedoctype
argument_list|,
name|publicid
argument_list|,
name|systemid
argument_list|)
decl_stmt|;
try|try
block|{
operator|(
operator|(
name|EXistResource
operator|)
name|res
operator|)
operator|.
name|setDocType
argument_list|(
name|doctype
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
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
name|listener
operator|.
name|warn
argument_list|(
literal|"Failed to change owner on document '"
operator|+
name|name
operator|+
literal|"'; skipping ..."
argument_list|)
expr_stmt|;
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
name|listener
operator|.
name|restored
argument_list|(
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|listener
operator|.
name|warn
argument_list|(
literal|"Failed to restore resource '"
operator|+
name|name
operator|+
literal|"'\nfrom file '"
operator|+
name|contents
operator|.
name|getSymbolicPath
argument_list|(
name|name
argument_list|,
literal|false
argument_list|)
operator|+
literal|"'.\nReason: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"deleted"
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
operator|.
name|equals
argument_list|(
literal|"collection"
argument_list|)
condition|)
block|{
try|try
block|{
name|Collection
name|child
init|=
name|current
operator|.
name|getChildCollection
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
name|CollectionManagementService
name|cmgt
init|=
operator|(
name|CollectionManagementService
operator|)
name|current
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|cmgt
operator|.
name|removeCollection
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|listener
operator|.
name|warn
argument_list|(
literal|"Failed to remove deleted collection: "
operator|+
name|name
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"resource"
argument_list|)
condition|)
block|{
try|try
block|{
name|Resource
name|resource
init|=
name|current
operator|.
name|getResource
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
name|current
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|listener
operator|.
name|warn
argument_list|(
literal|"Failed to remove deleted resource: "
operator|+
name|name
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
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
name|XmldbURI
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
name|XmldbURI
index|[]
name|segments
init|=
name|collPath
operator|.
name|getPathSegments
argument_list|()
decl_stmt|;
name|CollectionManagementServiceImpl
name|mgtService
decl_stmt|;
name|Collection
name|c
decl_stmt|;
name|XmldbURI
name|dbUri
decl_stmt|;
if|if
condition|(
operator|!
name|uri
operator|.
name|endsWith
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
condition|)
name|dbUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
expr_stmt|;
else|else
name|dbUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|Collection
name|current
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|dbUri
operator|.
name|toString
argument_list|()
argument_list|,
name|username
argument_list|,
name|pass
argument_list|)
decl_stmt|;
name|XmldbURI
name|p
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|segments
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
name|p
operator|.
name|append
argument_list|(
name|segments
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|XmldbURI
name|xmldbURI
init|=
name|dbUri
operator|.
name|resolveCollectionPath
argument_list|(
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
name|segments
index|[
name|i
index|]
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
throws|,
name|URISyntaxException
block|{
name|XmldbURI
name|dbUri
decl_stmt|;
if|if
condition|(
operator|!
name|uri
operator|.
name|endsWith
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
condition|)
name|dbUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
expr_stmt|;
else|else
name|dbUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|dbUri
operator|.
name|toString
argument_list|()
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
specifier|public
interface|interface
name|RestoreListener
block|{
name|void
name|createCollection
parameter_list|(
name|String
name|collection
parameter_list|)
function_decl|;
name|void
name|restored
parameter_list|(
name|String
name|resource
parameter_list|)
function_decl|;
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
block|}
specifier|private
class|class
name|DefaultListener
implements|implements
name|RestoreListener
block|{
specifier|public
name|void
name|createCollection
parameter_list|(
name|String
name|collection
parameter_list|)
block|{
name|info
argument_list|(
literal|"creating collection "
operator|+
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|restored
parameter_list|(
name|String
name|resource
parameter_list|)
block|{
name|info
argument_list|(
literal|"restored "
operator|+
name|resource
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|info
parameter_list|(
name|String
name|message
parameter_list|)
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
name|message
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|message
parameter_list|)
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
name|message
argument_list|)
expr_stmt|;
else|else
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

