begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|Backup
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
name|txn
operator|.
name|Txn
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
name|Configuration
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
name|FileUtils
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
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * BackupSystemTask creates an XML backup of the current database into a directory  * or zip file. Running the backup as a system task guarantees a consistent backup. No  * other transactions will be allowed while the backup is in progress.  *  * The following properties can be used to configure the backup task if passed to the  * {@link #configure(org.exist.util.Configuration, java.util.Properties)} method:  *  *<table>  *<caption>Properties</caption>  *<tr>  *<td>collection</td>  *<td>the collection to backup, specified as an absolute path into the db, e.g. /db/back-me-up</td>  *</tr>  *<tr>  *<td>user</td>  *<td>a valid user for writing the backup. Usually, this needs to be a user in the dba  *          database admin group.</td>  *</tr>  *<tr>  *<td>password</td>  *<td>the password for the user</td>  *</tr>  *<tr>  *<td>dir</td>  *<td>the output directory where the backup will be written</td>  *</tr>  *<tr>  *<td>prefix</td>  *<td>a prefix for the generated file name. the final file name will consist of  *          prefix + current-dateTime + suffix</td>  *</tr>  *<tr>  *<td>suffix</td>  *<td>a suffix for the generated file name. If it ends with .zip, BackupSystemTask will  *          directly write the backup into a zip file. Otherwise, it will write into a plain directory.</td>  *</tr>  *</table>  */
end_comment

begin_class
specifier|public
class|class
name|BackupSystemTask
implements|implements
name|SystemTask
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|BackupSystemTask
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SimpleDateFormat
name|creationDateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
name|DataBackup
operator|.
name|DATE_FORMAT_PICTURE
argument_list|)
decl_stmt|;
specifier|private
name|String
name|user
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|Path
name|directory
decl_stmt|;
specifier|private
name|String
name|suffix
decl_stmt|;
specifier|private
name|XmldbURI
name|collection
decl_stmt|;
specifier|private
name|boolean
name|deduplicateBlobs
decl_stmt|;
specifier|private
name|String
name|prefix
decl_stmt|;
comment|// purge old zip backup files
specifier|private
name|int
name|zipFilesMax
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"Backup Task"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
specifier|final
name|Configuration
name|config
parameter_list|,
specifier|final
name|Properties
name|properties
parameter_list|)
throws|throws
name|EXistException
block|{
name|user
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
name|password
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
name|String
name|collName
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"collection"
argument_list|,
literal|"xmldb:exist:///db"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|collName
operator|.
name|startsWith
argument_list|(
literal|"xmldb:exist:"
argument_list|)
condition|)
block|{
name|collName
operator|=
literal|"xmldb:exist://"
operator|+
name|collName
expr_stmt|;
block|}
name|collection
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|collName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Collection to backup: "
operator|+
name|collection
operator|.
name|toString
argument_list|()
operator|+
literal|". User: "
operator|+
name|user
argument_list|)
expr_stmt|;
name|deduplicateBlobs
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"deduplucate-blobs"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|suffix
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"suffix"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|prefix
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"prefix"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dir
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"dir"
argument_list|,
literal|"backup"
argument_list|)
decl_stmt|;
name|directory
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|dir
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|directory
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|directory
operator|=
operator|(
operator|(
name|Path
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
operator|)
operator|.
name|resolve
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|directory
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Unable to create backup directory: "
operator|+
name|directory
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
comment|// check for max zip files
specifier|final
name|String
name|filesMaxStr
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"zip-files-max"
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"zip-files-max: "
operator|+
name|filesMaxStr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|null
operator|!=
name|filesMaxStr
condition|)
block|{
try|try
block|{
name|zipFilesMax
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|filesMaxStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NumberFormatException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"zip-files-max property error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|EXistException
block|{
comment|// see if old zip files need to be purged
if|if
condition|(
name|zipFilesMax
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|purgeZipFiles
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Unable to purge zip files"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
specifier|final
name|String
name|dateTime
init|=
name|creationDateFormat
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|dest
init|=
name|directory
operator|.
name|resolve
argument_list|(
name|prefix
operator|+
name|dateTime
operator|+
name|suffix
argument_list|)
decl_stmt|;
specifier|final
name|Backup
name|backup
init|=
operator|new
name|Backup
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|dest
argument_list|,
name|collection
argument_list|,
literal|null
argument_list|,
name|deduplicateBlobs
argument_list|)
decl_stmt|;
try|try
block|{
name|backup
operator|.
name|backup
argument_list|(
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
decl||
name|SAXException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
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
block|}
specifier|public
name|void
name|purgeZipFiles
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"starting purgeZipFiles()"
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|Path
argument_list|>
name|entriesPaths
init|=
name|FileUtils
operator|.
name|list
argument_list|(
name|directory
argument_list|,
name|FileUtils
operator|.
name|getPrefixSuffixFilter
argument_list|(
name|prefix
argument_list|,
name|suffix
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|entriesNumber
init|=
name|entriesPaths
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|numberOfEntriesToBeDeleted
init|=
name|entriesNumber
operator|-
name|zipFilesMax
operator|+
literal|1
decl_stmt|;
name|Comparator
argument_list|<
name|Path
argument_list|>
name|timestampComparator
init|=
parameter_list|(
name|path1
parameter_list|,
name|path2
parameter_list|)
lambda|->
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
try|try
block|{
name|result
operator|=
name|Files
operator|.
name|getLastModifiedTime
argument_list|(
name|path1
argument_list|)
operator|.
name|compareTo
argument_list|(
name|Files
operator|.
name|getLastModifiedTime
argument_list|(
name|path2
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot compare files by timestamp: "
operator|+
name|path1
operator|+
literal|", "
operator|+
name|path2
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
decl_stmt|;
if|if
condition|(
name|numberOfEntriesToBeDeleted
operator|>
literal|0
condition|)
block|{
name|entriesPaths
operator|.
name|stream
argument_list|()
operator|.
name|sorted
argument_list|(
name|timestampComparator
argument_list|)
operator|.
name|limit
argument_list|(
name|numberOfEntriesToBeDeleted
argument_list|)
operator|.
name|forEach
argument_list|(
name|path
lambda|->
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Purging backup : "
operator|+
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|afterCheckpoint
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

