begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|plugin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|*
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
name|Database
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
name|BackupHandler
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
name|RestoreHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|*
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
name|DocumentAtExist
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
name|storage
operator|.
name|txn
operator|.
name|TransactionManager
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
name|serializer
operator|.
name|SAXSerializer
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
name|Locator
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
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * Plugins manager.   * It control search procedure, activation and de-actication (including runtime).  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"plugin-manager"
argument_list|)
specifier|public
class|class
name|PluginsManagerImpl
implements|implements
name|Configurable
implements|,
name|PluginsManager
implements|,
name|Startable
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|PluginsManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|COLLETION_URI
init|=
name|XmldbURI
operator|.
name|SYSTEM
operator|.
name|append
argument_list|(
literal|"plugins"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|CONFIG_FILE_URI
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"config.xml"
argument_list|)
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"version"
argument_list|)
specifier|private
name|String
name|version
init|=
literal|"1.0"
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"plugin"
argument_list|)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|runPlugins
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//	@ConfigurationFieldAsElement("search-path")
comment|//	private Map<String, File> placesToSearch = new LinkedHashMap<String, File>();
comment|//	private Map<String, PluginInfo> foundClasses = new LinkedHashMap<String, PluginInfo>();
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Plug
argument_list|>
name|jacks
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Plug
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
specifier|private
name|Collection
name|collection
decl_stmt|;
specifier|private
name|Database
name|db
decl_stmt|;
specifier|public
name|PluginsManagerImpl
parameter_list|(
name|Database
name|db
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
comment|//Temporary for testing
name|addPlugin
argument_list|(
literal|"org.exist.scheduler.SchedulerManager"
argument_list|)
expr_stmt|;
name|addPlugin
argument_list|(
literal|"org.exist.storage.md.MDStorageManager"
argument_list|)
expr_stmt|;
name|addPlugin
argument_list|(
literal|"org.exist.monitoring.MonitoringManager"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startUp
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|TransactionManager
name|transaction
init|=
name|db
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|COLLETION_URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|txn
operator|=
name|transaction
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|collection
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|COLLETION_URI
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
return|return;
comment|//if db corrupted it can lead to unrunnable issue
comment|//throw new ConfigurationException("Collection '/db/system/plugins' can't be created.");
name|collection
operator|.
name|setPermissions
argument_list|(
literal|0770
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|transaction
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading configuration failed: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|_config_
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|broker
argument_list|,
name|collection
argument_list|,
name|CONFIG_FILE_URI
argument_list|)
decl_stmt|;
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|_config_
argument_list|)
expr_stmt|;
comment|//load plugins by META-INF/services/
try|try
block|{
comment|//			File libFolder = new File(((BrokerPool)db).getConfiguration().getExistHome(), "lib");
comment|//			File pluginsFolder = new File(libFolder, "plugins");
comment|//			placesToSearch.put(pluginsFolder.getAbsolutePath(), pluginsFolder);
for|for
control|(
name|Class
argument_list|<
name|?
extends|extends
name|Plug
argument_list|>
name|plugin
range|:
name|listServices
argument_list|(
name|Plug
operator|.
name|class
argument_list|)
control|)
block|{
comment|//System.out.println("found plugin "+plugin);
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|Plug
argument_list|>
name|ctor
init|=
name|plugin
operator|.
name|getConstructor
argument_list|(
name|PluginsManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|Plug
name|plgn
init|=
name|ctor
operator|.
name|newInstance
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|jacks
operator|.
name|put
argument_list|(
name|plugin
operator|.
name|getName
argument_list|()
argument_list|,
name|plgn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//UNDERSTAND: call save?
comment|//		try {
comment|//			configuration.save(broker);
comment|//		} catch (PermissionDeniedException e) {
comment|//			//LOG?
comment|//		}
for|for
control|(
name|Plug
name|jack
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|jack
operator|instanceof
name|Startable
condition|)
block|{
operator|(
operator|(
name|Startable
operator|)
name|jack
operator|)
operator|.
name|startUp
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|String
name|version
parameter_list|()
block|{
return|return
name|version
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|addPlugin
parameter_list|(
name|String
name|className
parameter_list|)
block|{
comment|//check if already run
if|if
condition|(
name|jacks
operator|.
name|containsKey
argument_list|(
name|className
argument_list|)
condition|)
return|return;
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Plug
argument_list|>
name|plugin
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Plug
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|Plug
argument_list|>
name|ctor
init|=
name|plugin
operator|.
name|getConstructor
argument_list|(
name|PluginsManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|Plug
name|plgn
init|=
name|ctor
operator|.
name|newInstance
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|jacks
operator|.
name|put
argument_list|(
name|plugin
operator|.
name|getName
argument_list|()
argument_list|,
name|plgn
argument_list|)
expr_stmt|;
name|runPlugins
operator|.
name|add
argument_list|(
name|className
argument_list|)
expr_stmt|;
comment|//TODO: if (jack instanceof Startable) { ((Startable) jack).startUp(broker); }
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|//			e.printStackTrace();
block|}
block|}
specifier|public
name|void
name|sync
parameter_list|()
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|plugin
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
try|try
block|{
name|plugin
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Database
name|getDatabase
parameter_list|()
block|{
return|return
name|db
return|;
block|}
comment|/* 	 * Generate list of service implementations  	 */
specifier|private
parameter_list|<
name|S
parameter_list|>
name|Iterable
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
name|listServices
parameter_list|(
name|Class
argument_list|<
name|S
argument_list|>
name|ifc
parameter_list|)
throws|throws
name|Exception
block|{
name|ClassLoader
name|ldr
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|URL
argument_list|>
name|e
init|=
name|ldr
operator|.
name|getResources
argument_list|(
literal|"META-INF/services/"
operator|+
name|ifc
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
name|services
init|=
operator|new
name|HashSet
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|URL
name|url
init|=
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|url
operator|.
name|openStream
argument_list|()
decl_stmt|;
try|try
block|{
name|BufferedReader
name|r
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|String
name|line
init|=
name|r
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
break|break;
name|int
name|comment
init|=
name|line
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
decl_stmt|;
if|if
condition|(
name|comment
operator|>=
literal|0
condition|)
name|line
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|comment
argument_list|)
expr_stmt|;
name|String
name|name
init|=
name|line
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
continue|continue;
name|Class
argument_list|<
name|?
argument_list|>
name|clz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|,
literal|true
argument_list|,
name|ldr
argument_list|)
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|S
argument_list|>
name|impl
init|=
name|clz
operator|.
name|asSubclass
argument_list|(
name|ifc
argument_list|)
decl_stmt|;
name|services
operator|.
name|add
argument_list|(
name|impl
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|services
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
name|configuration
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
specifier|private
name|BackupHandler
name|bh
init|=
operator|new
name|BH
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|BackupHandler
name|getBackupHandler
parameter_list|()
block|{
return|return
name|bh
return|;
block|}
class|class
name|BH
implements|implements
name|BackupHandler
block|{
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|Collection
name|colection
parameter_list|,
name|AttributesImpl
name|attrs
parameter_list|)
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|BackupHandler
condition|)
block|{
operator|(
operator|(
name|BackupHandler
operator|)
name|plugin
operator|)
operator|.
name|backup
argument_list|(
name|colection
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|Collection
name|colection
parameter_list|,
name|SAXSerializer
name|serializer
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|BackupHandler
condition|)
block|{
operator|(
operator|(
name|BackupHandler
operator|)
name|plugin
operator|)
operator|.
name|backup
argument_list|(
name|colection
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|,
name|AttributesImpl
name|attrs
parameter_list|)
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|BackupHandler
condition|)
block|{
operator|(
operator|(
name|BackupHandler
operator|)
name|plugin
operator|)
operator|.
name|backup
argument_list|(
name|document
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|,
name|SAXSerializer
name|serializer
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|BackupHandler
condition|)
block|{
operator|(
operator|(
name|BackupHandler
operator|)
name|plugin
operator|)
operator|.
name|backup
argument_list|(
name|document
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|RestoreHandler
name|rh
init|=
operator|new
name|RH
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|RestoreHandler
name|getRestoreHandler
parameter_list|()
block|{
return|return
name|rh
return|;
block|}
class|class
name|RH
implements|implements
name|RestoreHandler
block|{
annotation|@
name|Override
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|skippedEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startCollectionRestore
parameter_list|(
name|Collection
name|colection
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|startCollectionRestore
argument_list|(
name|colection
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endCollectionRestore
parameter_list|(
name|Collection
name|colection
parameter_list|)
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|endCollectionRestore
argument_list|(
name|colection
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocumentRestore
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|startDocumentRestore
argument_list|(
name|document
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocumentRestore
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|)
block|{
for|for
control|(
name|Plug
name|plugin
range|:
name|jacks
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|plugin
operator|instanceof
name|RestoreHandler
condition|)
block|{
operator|(
operator|(
name|RestoreHandler
operator|)
name|plugin
operator|)
operator|.
name|endDocumentRestore
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

