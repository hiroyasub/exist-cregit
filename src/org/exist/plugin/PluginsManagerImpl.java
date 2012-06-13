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
name|xmldb
operator|.
name|XmldbURI
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
name|PLUGINS_COLLETION_URI
init|=
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
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
name|Jack
argument_list|>
name|jacks
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Jack
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
name|PLUGINS_COLLETION_URI
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
name|PLUGINS_COLLETION_URI
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
name|Jack
argument_list|>
name|plugin
range|:
name|listServices
argument_list|(
name|Jack
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
name|Jack
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
name|Jack
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
name|Jack
argument_list|>
name|plugin
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|Jack
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
name|Jack
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
name|Jack
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
specifier|public
name|void
name|sync
parameter_list|()
block|{
for|for
control|(
name|Jack
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
name|Jack
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
block|}
end_class

end_unit

