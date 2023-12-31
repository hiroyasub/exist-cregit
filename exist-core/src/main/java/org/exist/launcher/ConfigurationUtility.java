begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|launcher
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|DatabaseConfigurationException
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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLInputFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
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
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|*
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
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_class
specifier|public
class|class
name|ConfigurationUtility
block|{
specifier|public
specifier|static
specifier|final
name|String
name|LAUNCHER_PROPERTIES_FILE_NAME
init|=
literal|"launcher.properties"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LAUNCHER_PROPERTY_MAX_MEM
init|=
literal|"memory.max"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LAUNCHER_PROPERTY_MIN_MEM
init|=
literal|"memory.min"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LAUNCHER_PROPERTY_VMOPTIONS
init|=
literal|"vmoptions"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LAUNCHER_PROPERTY_NEVER_INSTALL_SERVICE
init|=
literal|"service.install.never"
decl_stmt|;
comment|/**      * We try to resolve any config file relative to an eXist-db      * config file indicated by the System Property {@link org.exist.util.ConfigurationHelper#PROP_EXIST_CONFIGURATION_FILE},      * if such a file does not exist, then we try and resolve it from the user.home or EXIST_HOME.      *      * @param configFileName the name/relative path of the config file to lookup      * @param shouldExist if the file should already exist      *      * @return the file path (may not exist!)      */
specifier|public
specifier|static
name|Path
name|lookup
parameter_list|(
specifier|final
name|String
name|configFileName
parameter_list|,
specifier|final
name|boolean
name|shouldExist
parameter_list|)
block|{
return|return
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ConfigurationHelper
operator|.
name|getFromSystemProperty
argument_list|()
operator|.
name|filter
argument_list|(
name|Files
operator|::
name|exists
argument_list|)
operator|.
name|map
argument_list|(
name|existConfigFile
lambda|->
name|existConfigFile
operator|.
name|resolveSibling
argument_list|(
name|configFileName
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|f
lambda|->
operator|!
name|shouldExist
operator|||
name|Files
operator|.
name|exists
argument_list|(
name|f
argument_list|)
argument_list|)
operator|.
name|orElseGet
argument_list|(
parameter_list|()
lambda|->
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
name|configFileName
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isFirstStart
parameter_list|()
block|{
specifier|final
name|Path
name|propFile
init|=
name|lookup
argument_list|(
name|LAUNCHER_PROPERTIES_FILE_NAME
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|propFile
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getJettyPorts
parameter_list|()
throws|throws
name|DatabaseConfigurationException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ports
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|jettyHttpConfig
init|=
name|lookup
argument_list|(
literal|"jetty/jetty-http.xml"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|jettyHttpsConfig
init|=
name|lookup
argument_list|(
literal|"jetty/jetty-ssl.xml"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|getJettyPorts
argument_list|(
name|ports
argument_list|,
name|jettyHttpConfig
argument_list|)
expr_stmt|;
name|getJettyPorts
argument_list|(
name|ports
argument_list|,
name|jettyHttpsConfig
argument_list|)
expr_stmt|;
return|return
name|ports
return|;
block|}
specifier|private
specifier|static
name|void
name|getJettyPorts
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ports
parameter_list|,
name|Path
name|jettyConfig
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|jettyConfig
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|XMLStreamReader
name|reader
init|=
name|XMLInputFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|createXMLStreamReader
argument_list|(
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|jettyConfig
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
operator|&&
literal|"SystemProperty"
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|null
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|(
name|name
operator|.
name|equals
argument_list|(
literal|"jetty.port"
argument_list|)
operator|||
name|name
operator|.
name|equals
argument_list|(
literal|"jetty.ssl.port"
argument_list|)
operator|)
condition|)
block|{
specifier|final
name|String
name|defaultValue
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|null
argument_list|,
literal|"default"
argument_list|)
decl_stmt|;
if|if
condition|(
name|defaultValue
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|ports
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|defaultValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
comment|// skip
block|}
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLStreamException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
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
specifier|public
specifier|static
name|Properties
name|loadProperties
parameter_list|()
block|{
specifier|final
name|Properties
name|launcherProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
name|propFile
init|=
name|lookup
argument_list|(
name|LAUNCHER_PROPERTIES_FILE_NAME
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|propFile
argument_list|)
condition|)
block|{
name|is
operator|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|propFile
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|is
operator|=
name|Launcher
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|LAUNCHER_PROPERTIES_FILE_NAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|launcherProperties
operator|.
name|load
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|LAUNCHER_PROPERTIES_FILE_NAME
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
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
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|launcherProperties
return|;
block|}
specifier|public
specifier|static
name|void
name|saveProperties
parameter_list|(
specifier|final
name|Properties
name|properties
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|propFile
init|=
name|lookup
argument_list|(
name|LAUNCHER_PROPERTIES_FILE_NAME
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|launcherProperties
init|=
name|loadProperties
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|key
range|:
name|properties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|launcherProperties
operator|.
name|setProperty
argument_list|(
name|key
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Launcher properties: "
operator|+
name|launcherProperties
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|key
range|:
name|launcherProperties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|key
operator|+
literal|"="
operator|+
name|launcherProperties
operator|.
name|getProperty
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|Writer
name|writer
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|propFile
argument_list|)
init|)
block|{
name|launcherProperties
operator|.
name|store
argument_list|(
name|writer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Path
name|backupOriginal
parameter_list|(
specifier|final
name|Path
name|propFile
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SimpleDateFormat
name|sdf
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyyMMddHHmmss"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|bakFileName
init|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|propFile
argument_list|)
operator|+
literal|".orig."
operator|+
name|sdf
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
name|bakFile
init|=
name|propFile
operator|.
name|resolveSibling
argument_list|(
name|bakFileName
argument_list|)
decl_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|propFile
argument_list|,
name|bakFile
argument_list|)
expr_stmt|;
return|return
name|bakFile
return|;
block|}
specifier|public
specifier|static
name|void
name|saveConfiguration
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|xsl
parameter_list|,
name|Properties
name|properties
parameter_list|)
throws|throws
name|IOException
throws|,
name|TransformerException
block|{
specifier|final
name|Path
name|config
init|=
name|lookup
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|applyXSL
argument_list|(
name|properties
argument_list|,
name|config
argument_list|,
name|xsl
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|applyXSL
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|Path
name|config
parameter_list|,
name|String
name|xsl
parameter_list|)
throws|throws
name|IOException
throws|,
name|TransformerException
block|{
specifier|final
name|Path
name|orig
init|=
name|backupOriginal
argument_list|(
name|config
argument_list|)
decl_stmt|;
specifier|final
name|TransformerFactory
name|factory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
specifier|final
name|StreamSource
name|xslSource
init|=
operator|new
name|StreamSource
argument_list|(
name|ConfigurationUtility
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|xsl
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Transformer
name|transformer
init|=
name|factory
operator|.
name|newTransformer
argument_list|(
name|xslSource
argument_list|)
decl_stmt|;
specifier|final
name|StreamSource
name|xmlSource
init|=
operator|new
name|StreamSource
argument_list|(
name|orig
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|StreamResult
name|output
init|=
operator|new
name|StreamResult
argument_list|(
name|config
operator|.
name|toFile
argument_list|()
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|setErrorListener
argument_list|(
operator|new
name|ErrorListener
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|warning
parameter_list|(
name|TransformerException
name|exception
parameter_list|)
throws|throws
name|TransformerException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|exception
operator|.
name|getMessageAndLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|error
parameter_list|(
name|TransformerException
name|exception
parameter_list|)
throws|throws
name|TransformerException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|exception
operator|.
name|getMessageAndLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|fatalError
parameter_list|(
name|TransformerException
name|exception
parameter_list|)
throws|throws
name|TransformerException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|exception
operator|.
name|getMessageAndLocation
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|properties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|transformer
operator|.
name|setParameter
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|transformer
operator|.
name|transform
argument_list|(
name|xmlSource
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

