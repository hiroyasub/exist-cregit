begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|Namespaces
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
name|BackupDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|Deployment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|ExistRepository
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
name|XMLReaderPool
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
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Package
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|PackageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Packages
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|deps
operator|.
name|Semver
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Utility to compare the applications contained in a backup with the already  * installed applications in the package repo.  *  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|AppRestoreUtils
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AppRestoreUtils
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PKG_NAMESPACE
init|=
literal|"http://expath.org/ns/pkg"
decl_stmt|;
comment|/**      * Inspects the apps contained in the backup against installed apps in the database      * and return a set of symbolic backup paths pointing to the collection of those      * apps for which newer versions are installed within the database. The returned      * paths may then be ignored during a restore.      *      * The method attempts to be fail safe to make sure even bad backups can be restored. Errors      * reading package descriptors are thus only logged and should not abort the process.      *      * @param broker the broker used for reading the backup and retrieving the expath repo      * @param descriptors a queue of backup descriptors to inspect      * @return a set of paths for which newer versions exist in the database. may be empty.      */
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|checkApps
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Deque
argument_list|<
name|BackupDescriptor
argument_list|>
name|descriptors
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|AppDetail
argument_list|>
name|apps
init|=
name|getAppsFromBackup
argument_list|(
name|broker
argument_list|,
name|descriptors
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|ExistRepository
argument_list|>
name|repo
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getExpathRepo
argument_list|()
decl_stmt|;
if|if
condition|(
name|repo
operator|.
name|isPresent
argument_list|()
condition|)
block|{
for|for
control|(
specifier|final
name|AppDetail
name|app
range|:
name|apps
control|)
block|{
specifier|final
name|Packages
name|packages
init|=
name|repo
operator|.
name|get
argument_list|()
operator|.
name|getParentRepo
argument_list|()
operator|.
name|getPackages
argument_list|(
name|app
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|packages
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Package
name|latest
init|=
name|packages
operator|.
name|latest
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Semver
name|version
init|=
name|Semver
operator|.
name|parse
argument_list|(
name|latest
operator|.
name|getVersion
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|.
name|compareTo
argument_list|(
name|app
operator|.
name|version
argument_list|)
operator|>
literal|0
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|app
operator|.
name|path
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PackageException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid semver in expath repository for "
operator|+
name|app
operator|.
name|name
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|paths
return|;
block|}
comment|/**      * Inspect all collections which may belong to apps in the backup descriptor. Return a list      * of {@link AppDetail} objects containing the symbolic path, name and version of every app      * found.      *      * The method attempts to be fail safe to make sure even bad backups can be restored. Errors      * reading package descriptors are thus only logged and should not abort the process.      *      * @param broker the broker to use for parsing the descriptor and obtaining the app root      * @param descriptors a queue of backup descriptors to inspect      * @return list of application details      */
specifier|private
specifier|static
name|List
argument_list|<
name|AppDetail
argument_list|>
name|getAppsFromBackup
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Deque
argument_list|<
name|BackupDescriptor
argument_list|>
name|descriptors
parameter_list|)
block|{
specifier|final
name|String
name|appRoot
init|=
name|getAppRoot
argument_list|(
name|broker
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|AppDetail
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|17
argument_list|)
decl_stmt|;
specifier|final
name|XMLReaderPool
name|parserPool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|BackupDescriptor
name|descriptor
range|:
name|descriptors
control|)
block|{
specifier|final
name|BackupDescriptor
name|apps
init|=
name|descriptor
operator|.
name|getChildBackupDescriptor
argument_list|(
name|appRoot
argument_list|)
decl_stmt|;
if|if
condition|(
name|apps
operator|!=
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|collections
init|=
name|getSubcollectionNames
argument_list|(
name|parserPool
argument_list|,
name|apps
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|collection
range|:
name|collections
control|)
block|{
specifier|final
name|BackupDescriptor
name|app
init|=
name|apps
operator|.
name|getChildBackupDescriptor
argument_list|(
name|collection
argument_list|)
decl_stmt|;
specifier|final
name|InputSource
name|is
init|=
name|app
operator|.
name|getInputSource
argument_list|(
literal|"expath-pkg.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|parserPool
operator|.
name|borrowXMLReader
argument_list|()
expr_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
operator|new
name|DefaultHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|PKG_NAMESPACE
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|&&
literal|"package"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
specifier|final
name|String
name|version
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"version"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|name
init|=
name|attributes
operator|.
name|getValue
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|version
argument_list|)
operator|||
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid package descriptor for "
operator|+
name|app
operator|.
name|getSymbolicPath
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
try|try
block|{
specifier|final
name|AppDetail
name|detail
init|=
operator|new
name|AppDetail
argument_list|(
name|app
operator|.
name|getSymbolicPath
argument_list|()
argument_list|,
name|name
argument_list|,
name|Semver
operator|.
name|parse
argument_list|(
name|version
argument_list|)
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|detail
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PackageException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid semver found while parsing "
operator|+
name|app
operator|.
name|getSymbolicPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
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
catch|catch
parameter_list|(
name|IOException
decl||
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Parse exception while parsing "
operator|+
name|app
operator|.
name|getSymbolicPath
argument_list|(
literal|"expath-pkg.xml"
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|parserPool
operator|.
name|returnXMLReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|getSubcollectionNames
parameter_list|(
name|XMLReaderPool
name|parserPool
parameter_list|,
name|BackupDescriptor
name|apps
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|collections
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
try|try
block|{
name|apps
operator|.
name|parse
argument_list|(
name|parserPool
argument_list|,
operator|new
name|DefaultHandler
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|Namespaces
operator|.
name|EXIST_NS
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|&&
literal|"subcollection"
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|collections
operator|.
name|add
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
literal|"filename"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"SAX error while parsing backup descriptor "
operator|+
name|apps
operator|.
name|getSymbolicPath
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|collections
return|;
block|}
comment|/**      * Get the database root path for applications, removing /db and trailing slash.      * @param broker the broker to get the configuration from      * @return the root path for applications      */
specifier|private
specifier|static
name|String
name|getAppRoot
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|String
name|appRoot
init|=
operator|(
name|String
operator|)
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|Deployment
operator|.
name|PROPERTY_APP_ROOT
argument_list|)
decl_stmt|;
if|if
condition|(
name|appRoot
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|'/'
argument_list|)
condition|)
block|{
name|appRoot
operator|=
name|appRoot
operator|.
name|substring
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|appRoot
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|appRoot
operator|=
name|appRoot
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|appRoot
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|appRoot
return|;
block|}
specifier|final
specifier|static
class|class
name|AppDetail
block|{
specifier|protected
specifier|final
name|String
name|path
decl_stmt|;
specifier|protected
specifier|final
name|String
name|name
decl_stmt|;
specifier|protected
specifier|final
name|Semver
name|version
decl_stmt|;
name|AppDetail
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|Semver
name|version
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

