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
name|apache
operator|.
name|commons
operator|.
name|io
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
name|util
operator|.
name|ConfigurationHelper
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
name|ErrorListener
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
name|Transformer
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
name|TransformerException
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
name|TransformerFactory
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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

begin_class
specifier|public
class|class
name|ConfigurationUtility
block|{
specifier|public
specifier|static
name|boolean
name|isFirstStart
parameter_list|()
block|{
specifier|final
name|File
name|propFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"vm.properties"
argument_list|)
decl_stmt|;
return|return
operator|!
name|propFile
operator|.
name|exists
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|saveProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|File
name|propFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"vm.properties"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|vmProperties
init|=
name|LauncherWrapper
operator|.
name|getVMProperties
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"system properties: "
operator|+
name|vmProperties
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
name|entry
range|:
name|vmProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|userProperty
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|userProperty
operator|==
literal|null
condition|)
block|{
name|properties
operator|.
name|setProperty
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
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|propFile
argument_list|)
decl_stmt|;
name|properties
operator|.
name|store
argument_list|(
name|os
argument_list|,
literal|"This file contains a list of VM parameters to be passed to Java\n"
operator|+
literal|"when eXist is started by double clicking on start.jar (or calling\n"
operator|+
literal|"\"java -jar start.jar\" without parameters on the shell)."
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|saveConfiguration
parameter_list|(
name|Properties
name|properties
parameter_list|)
throws|throws
name|IOException
throws|,
name|TransformerException
block|{
specifier|final
name|File
name|config
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"conf.xml"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|bakFile
init|=
operator|new
name|File
argument_list|(
name|config
operator|.
name|getParent
argument_list|()
argument_list|,
literal|"conf.xml.orig"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bakFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|config
argument_list|,
name|bakFile
argument_list|)
expr_stmt|;
block|}
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
literal|"conf.xsl"
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
name|config
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
