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
name|IOException
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
name|Date
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
name|org
operator|.
name|apache
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

begin_comment
comment|/**  * BackupSystemTask creates an XML backup of the current database into a directory  * or zip file. Running the backup as a system task guarantees a consistent backup. No  * other transactions will be allowed while the backup is in progress.  *  * The following properties can be used to configure the backup task if passed to the  * {@link #configure(org.exist.util.Configuration, java.util.Properties)} method:  *  *<table>  *<tr>  *<td>collection</td>  *<td>the collection to backup, specified as an absolute path into the db, e.g. /db/back-me-up</td>  *</tr>  *<tr>  *<td>user</td>  *<td>a valid user for writing the backup. Usually, this needs to be a user in the dba  *          database admin group.</td>  *</tr>  *<tr>  *<td>password</td>  *<td>the password for the user</td>  *</tr>  *<tr>  *<td>dir</td>  *<td>the output directory where the backup will be written</td>  *</tr>  *<tr>  *<td>prefix</td>  *<td>a prefix for the generated file name. the final file name will consist of  *          prefix + current-dateTime + suffix</td>  *</tr>  *<tr>  *<td>suffix</td>  *<td>a suffix for the generated file name. If it ends with .zip, BackupSystemTask will  *          directly write the backup into a zip file. Otherwise, it will write into a plain directory.</td>  *</tr>  *</table>  */
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
name|Logger
operator|.
name|getLogger
argument_list|(
name|BackupSystemTask
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|DateFormat
name|df
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd'T'HHmm"
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
name|File
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
name|String
name|prefix
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|Configuration
name|config
parameter_list|,
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
name|collName
operator|=
literal|"xmldb:exist://"
operator|+
name|collName
expr_stmt|;
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
operator|new
name|File
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
name|dir
operator|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|dir
expr_stmt|;
name|directory
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|directory
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|String
name|dateTime
init|=
name|df
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|dest
init|=
name|directory
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|prefix
operator|+
name|dateTime
operator|+
name|suffix
decl_stmt|;
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
name|XMLDBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
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
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
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
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
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
block|}
end_class

end_unit

