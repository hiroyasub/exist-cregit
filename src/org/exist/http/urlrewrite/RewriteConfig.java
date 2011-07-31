begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|urlrewrite
package|;
end_package

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
name|Namespaces
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
name|security
operator|.
name|PermissionDeniedException
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
name|DocumentImpl
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
name|lock
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|SAXAdapter
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
name|Constants
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
name|RegexTranslator
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
name|RegexTranslator
operator|.
name|RegexSyntaxException
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
name|Document
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
name|Element
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
name|javax
operator|.
name|servlet
operator|.
name|FilterConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|FileInputStream
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * Handles static mapping configuration for the @link XQueryURLRewrite filter,  * defined in controller-config.xml. The static mapping is used to map  * base paths to base controllers or servlets.  */
end_comment

begin_class
specifier|public
class|class
name|RewriteConfig
block|{
specifier|public
specifier|final
specifier|static
name|String
name|CONFIG_FILE
init|=
literal|"controller-config.xml"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|MAP_ELEMENT
init|=
literal|"map"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PATTERN_ATTRIBUTE
init|=
literal|"pattern"
decl_stmt|;
comment|/**      * Adding server-name="www.example.com" to a root tag in the controller-config.xml file.<br/>      *<br/>      *  i.e.<br/>       *<br/>      *&lt;root server-name="example1.com" pattern="/*" path="xmldb:exist:///db/org/example1/"/&gt;<br/>      *&lt;root server-name="example2.com" pattern="/*" path="xmldb:exist:///db/org/example2/"/&gt;<br/>      *<br/>      *  Will redirect http://example1.com to /db/org/example1/<br/>      *  and http://example2.com to /db/org/example2/<br/>      *<br/>      *  If there is no server-name attribute on the root tag, then the server name is ignored while performing the URL rewriting.      *        */
specifier|public
specifier|final
specifier|static
name|String
name|SERVER_NAME_ATTRIBUTE
init|=
literal|"server-name"
decl_stmt|;
comment|/**      * Maps a regular expression to an URLRewrite instance      */
specifier|private
specifier|final
specifier|static
class|class
name|Mapping
block|{
name|Pattern
name|pattern
decl_stmt|;
name|Matcher
name|matcher
init|=
literal|null
decl_stmt|;
name|URLRewrite
name|action
decl_stmt|;
specifier|private
name|Mapping
parameter_list|(
name|String
name|regex
parameter_list|,
name|URLRewrite
name|action
parameter_list|)
throws|throws
name|ServletException
block|{
try|try
block|{
name|regex
operator|=
name|RegexTranslator
operator|.
name|translate
argument_list|(
name|regex
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|pattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regex
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RegexSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Syntax error in regular expression specified for path. "
operator|+
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
name|String
name|match
parameter_list|(
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|matcher
operator|==
literal|null
condition|)
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
name|path
argument_list|)
expr_stmt|;
else|else
name|matcher
operator|.
name|reset
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|lookingAt
argument_list|()
condition|)
block|{
return|return
name|path
operator|.
name|substring
argument_list|(
name|matcher
operator|.
name|start
argument_list|()
argument_list|,
name|matcher
operator|.
name|end
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
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
name|RewriteConfig
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// the list of established mappings
specifier|private
name|List
argument_list|<
name|Mapping
argument_list|>
name|mappings
init|=
operator|new
name|ArrayList
argument_list|<
name|Mapping
argument_list|>
argument_list|()
decl_stmt|;
comment|// parent XQueryURLRewrite
specifier|private
name|XQueryURLRewrite
name|urlRewrite
decl_stmt|;
specifier|public
name|RewriteConfig
parameter_list|(
name|XQueryURLRewrite
name|urlRewrite
parameter_list|)
throws|throws
name|ServletException
block|{
name|this
operator|.
name|urlRewrite
operator|=
name|urlRewrite
expr_stmt|;
name|String
name|controllerConfig
init|=
name|urlRewrite
operator|.
name|getConfig
argument_list|()
operator|.
name|getInitParameter
argument_list|(
literal|"config"
argument_list|)
decl_stmt|;
if|if
condition|(
name|controllerConfig
operator|==
literal|null
condition|)
name|controllerConfig
operator|=
name|CONFIG_FILE
expr_stmt|;
name|configure
argument_list|(
name|controllerConfig
argument_list|)
expr_stmt|;
block|}
comment|/**      * Lookup the given path in the static mappings table.      *      * @param request use the path from this request      * @return the URLRewrite instance for the mapping or null if none was found      * @throws ServletException      */
specifier|public
specifier|synchronized
name|URLRewrite
name|lookup
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|ServletException
block|{
name|String
name|path
init|=
name|request
operator|.
name|getRequestURI
argument_list|()
operator|.
name|substring
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|lookup
argument_list|(
name|path
argument_list|,
name|request
operator|.
name|getServerName
argument_list|()
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Lookup the given path in the static mappings table.      *      * @param path path to look up      * @param staticMapping don't return redirects to other controllers, just static mappings      *  to servlets.      * @return the URLRewrite instance for the mapping or null if none was found      * @throws ServletException      */
specifier|public
specifier|synchronized
name|URLRewrite
name|lookup
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|serverName
parameter_list|,
name|boolean
name|staticMapping
parameter_list|)
throws|throws
name|ServletException
block|{
name|int
name|p
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|mappings
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Mapping
name|m
init|=
name|mappings
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|matchedString
init|=
name|m
operator|.
name|match
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchedString
operator|!=
literal|null
condition|)
block|{
name|URLRewrite
name|action
init|=
name|m
operator|.
name|action
decl_stmt|;
comment|/*                  * If the URLRewrite is a ControllerForward, then test to see if there is a condition                  * on the server name.  If there is a condition on the server name and the names do not                  * match, then ignore this ControllerForward.                  */
if|if
condition|(
name|action
operator|instanceof
name|ControllerForward
condition|)
block|{
if|if
condition|(
name|serverName
operator|!=
literal|null
condition|)
block|{
name|String
name|controllerServerName
init|=
operator|(
operator|(
name|ControllerForward
operator|)
name|action
operator|)
operator|.
name|getServerName
argument_list|()
decl_stmt|;
if|if
condition|(
name|controllerServerName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|serverName
operator|.
name|equalsIgnoreCase
argument_list|(
name|controllerServerName
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
block|}
block|}
comment|// if the mapping matches a part of the URI only, set the prefix to the
comment|// matched string. This will later be stripped from the URI.
if|if
condition|(
name|matchedString
operator|.
name|length
argument_list|()
operator|!=
name|path
operator|.
name|length
argument_list|()
operator|&&
operator|!
name|matchedString
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
name|action
operator|.
name|setPrefix
argument_list|(
name|matchedString
argument_list|)
expr_stmt|;
name|action
operator|.
name|setURI
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|staticMapping
operator|||
operator|!
operator|(
name|action
operator|instanceof
name|ControllerForward
operator|)
condition|)
return|return
name|action
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|configure
parameter_list|(
name|String
name|controllerConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading XQueryURLRewrite configuration from "
operator|+
name|controllerConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|controllerConfig
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
condition|)
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|urlRewrite
operator|.
name|pool
operator|.
name|get
argument_list|(
name|urlRewrite
operator|.
name|defaultUser
argument_list|)
expr_stmt|;
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|controllerConfig
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|parse
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to parse controller.xml: "
operator|+
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
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to parse controller.xml: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|urlRewrite
operator|.
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
block|{
name|File
name|d
init|=
operator|new
name|File
argument_list|(
name|urlRewrite
operator|.
name|getConfig
argument_list|()
operator|.
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
literal|"."
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|d
argument_list|,
name|controllerConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|configFile
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|Document
name|doc
init|=
name|parseConfig
argument_list|(
name|configFile
argument_list|)
decl_stmt|;
name|parse
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to parse controller.xml: "
operator|+
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
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to parse controller.xml: "
operator|+
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
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to parse controller.xml: "
operator|+
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
try|try
block|{
name|urlRewrite
operator|.
name|clearCaches
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Failed to update controller.xml: "
operator|+
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
specifier|private
name|void
name|parse
parameter_list|(
name|Document
name|doc
parameter_list|)
throws|throws
name|ServletException
block|{
name|Element
name|root
init|=
name|doc
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|Node
name|child
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|child
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|)
condition|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
name|String
name|pattern
init|=
name|elem
operator|.
name|getAttribute
argument_list|(
name|PATTERN_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|pattern
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Action in controller-config.xml has no pattern: "
operator|+
name|elem
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
name|URLRewrite
name|urw
init|=
name|parseAction
argument_list|(
name|urlRewrite
operator|.
name|getConfig
argument_list|()
argument_list|,
name|pattern
argument_list|,
name|elem
argument_list|)
decl_stmt|;
if|if
condition|(
name|urw
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Unknown action in controller-config.xml: "
operator|+
name|elem
operator|.
name|getNodeName
argument_list|()
argument_list|)
throw|;
name|mappings
operator|.
name|add
argument_list|(
operator|new
name|Mapping
argument_list|(
name|pattern
argument_list|,
name|urw
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|URLRewrite
name|parseAction
parameter_list|(
name|FilterConfig
name|config
parameter_list|,
name|String
name|pattern
parameter_list|,
name|Element
name|action
parameter_list|)
throws|throws
name|ServletException
block|{
name|URLRewrite
name|rewrite
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"forward"
operator|.
name|equals
argument_list|(
name|action
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|rewrite
operator|=
operator|new
name|PathForward
argument_list|(
name|config
argument_list|,
name|action
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"redirect"
operator|.
name|equals
argument_list|(
name|action
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|rewrite
operator|=
operator|new
name|Redirect
argument_list|(
name|action
argument_list|,
name|pattern
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"root"
operator|.
name|equals
argument_list|(
name|action
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|ControllerForward
name|cf
init|=
operator|new
name|ControllerForward
argument_list|(
name|action
argument_list|,
name|pattern
argument_list|)
decl_stmt|;
comment|/*         	 * If there is a server-name attribute on the root tag, then add that         	 * as an attribute on the ControllerForward object.         	 */
name|String
name|serverName
init|=
name|action
operator|.
name|getAttribute
argument_list|(
name|SERVER_NAME_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
name|serverName
operator|!=
literal|null
operator|&&
name|serverName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|cf
operator|.
name|setServerName
argument_list|(
name|serverName
argument_list|)
expr_stmt|;
block|}
name|rewrite
operator|=
name|cf
expr_stmt|;
block|}
return|return
name|rewrite
return|;
block|}
specifier|private
name|Document
name|parseConfig
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|xr
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|xr
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|adapter
argument_list|)
expr_stmt|;
name|xr
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
return|return
name|adapter
operator|.
name|getDocument
argument_list|()
return|;
block|}
block|}
end_class

end_unit

