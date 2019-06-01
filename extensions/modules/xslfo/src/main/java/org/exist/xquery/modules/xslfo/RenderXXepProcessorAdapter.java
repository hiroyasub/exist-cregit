begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|xslfo
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
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
name|dom
operator|.
name|DOMSource
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
name|NodeValue
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
name|Node
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
name|ContentHandler
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

begin_comment
comment|/**  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|RenderXXepProcessorAdapter
implements|implements
name|ProcessorAdapter
block|{
specifier|private
name|Object
name|formatter
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|NodeValue
name|configFile
parameter_list|,
name|Properties
name|parameters
parameter_list|,
name|String
name|mimeType
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
if|if
condition|(
name|configFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XEP requires a configuration file"
argument_list|)
throw|;
block|}
try|try
block|{
name|Class
name|formatterImplClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.renderx.xep.FormatterImpl"
argument_list|)
decl_stmt|;
if|if
condition|(
name|parameters
operator|==
literal|null
condition|)
block|{
name|Constructor
name|formatterImplCstr
init|=
name|formatterImplClazz
operator|.
name|getConstructor
argument_list|(
name|Source
operator|.
name|class
argument_list|)
decl_stmt|;
name|formatter
operator|=
name|formatterImplCstr
operator|.
name|newInstance
argument_list|(
operator|new
name|DOMSource
argument_list|(
operator|(
name|Node
operator|)
name|configFile
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Constructor
name|formatterImplCstr
init|=
name|formatterImplClazz
operator|.
name|getConstructor
argument_list|(
name|Source
operator|.
name|class
argument_list|,
name|Properties
operator|.
name|class
argument_list|)
decl_stmt|;
name|formatter
operator|=
name|formatterImplCstr
operator|.
name|newInstance
argument_list|(
operator|new
name|DOMSource
argument_list|(
operator|(
name|Node
operator|)
name|configFile
argument_list|)
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
name|String
name|backendType
init|=
name|mimeType
operator|.
name|substring
argument_list|(
name|mimeType
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
operator|.
name|toUpperCase
argument_list|()
decl_stmt|;
name|Class
name|foTargetClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.renderx.xep.FOTarget"
argument_list|)
decl_stmt|;
name|Constructor
name|foTargetCstr
init|=
name|foTargetClazz
operator|.
name|getConstructor
argument_list|(
name|OutputStream
operator|.
name|class
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|Object
name|foTarget
init|=
name|foTargetCstr
operator|.
name|newInstance
argument_list|(
name|os
argument_list|,
name|backendType
argument_list|)
decl_stmt|;
name|Method
name|createContentHandlerMethod
init|=
name|formatterImplClazz
operator|.
name|getMethod
argument_list|(
literal|"createContentHandler"
argument_list|,
name|String
operator|.
name|class
argument_list|,
name|foTargetClazz
argument_list|)
decl_stmt|;
return|return
operator|(
name|ContentHandler
operator|)
name|createContentHandlerMethod
operator|.
name|invoke
argument_list|(
name|formatter
argument_list|,
literal|null
argument_list|,
name|foTarget
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
if|if
condition|(
name|formatter
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Class
name|formatterImplClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"com.renderx.xep.FormatterImpl"
argument_list|)
decl_stmt|;
name|Method
name|cleanupMethod
init|=
name|formatterImplClazz
operator|.
name|getMethod
argument_list|(
literal|"cleanup"
argument_list|)
decl_stmt|;
name|cleanupMethod
operator|.
name|invoke
argument_list|(
name|formatter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
block|}
block|}
block|}
end_class

end_unit
