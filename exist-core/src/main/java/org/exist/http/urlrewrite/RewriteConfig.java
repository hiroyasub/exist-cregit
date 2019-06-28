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
name|EXistException
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
name|persistent
operator|.
name|LockedDocument
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
name|persistent
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
operator|.
name|LockMode
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
name|regex
operator|.
name|JDK15RegexTranslator
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
name|regex
operator|.
name|RegexSyntaxException
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
name|ServletConfig
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
name|Optional
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
name|RewriteConfig
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIG_FILE
init|=
literal|"controller-config.xml"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_ATTRIBUTE
init|=
literal|"pattern"
decl_stmt|;
comment|/**      * Adding server-name="www.example.com" to a root tag in the controller-config.xml file.      *      * i.e.      *      *&lt;root server-name="example1.com" pattern="/*" path="xmldb:exist:///db/org/example1/"/&gt;      *&lt;root server-name="example2.com" pattern="/*" path="xmldb:exist:///db/org/example2/"/&gt;      *      * Will redirect http://example1.com to /db/org/example1/      * and http://example2.com to /db/org/example2/      *      * If there is no server-name attribute on the root tag, then the server name is ignored while performing the URL rewriting.      */
specifier|public
specifier|static
specifier|final
name|String
name|SERVER_NAME_ATTRIBUTE
init|=
literal|"server-name"
decl_stmt|;
comment|// the list of established mappings
specifier|private
specifier|final
name|List
argument_list|<
name|Mapping
argument_list|>
name|mappings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// parent XQueryURLRewrite
specifier|private
specifier|final
name|XQueryURLRewrite
name|urlRewrite
decl_stmt|;
specifier|public
name|RewriteConfig
parameter_list|(
specifier|final
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
block|{
name|controllerConfig
operator|=
name|CONFIG_FILE
expr_stmt|;
block|}
name|configure
argument_list|(
name|controllerConfig
argument_list|)
expr_stmt|;
block|}
comment|/**      * Lookup the given path in the static mappings table.      *      * @param request use the path from this request      * @return the URLRewrite instance for the mapping or null if none was found      */
specifier|public
specifier|synchronized
name|URLRewrite
name|lookup
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|)
block|{
specifier|final
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
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Lookup the given path in the static mappings table.      *      * @param path          path to look up      * @param staticMapping don't return redirects to other controllers, just static mappings      *                      to servlets.      * @return the URLRewrite instance for the mapping or null if none was found      */
specifier|public
specifier|synchronized
name|URLRewrite
name|lookup
parameter_list|(
name|String
name|path
parameter_list|,
specifier|final
name|String
name|serverName
parameter_list|,
specifier|final
name|boolean
name|staticMapping
parameter_list|,
specifier|final
name|URLRewrite
name|copyFrom
parameter_list|)
block|{
specifier|final
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
block|{
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
block|}
for|for
control|(
specifier|final
name|Mapping
name|mapping
range|:
name|mappings
control|)
block|{
specifier|final
name|String
name|matchedString
init|=
name|mapping
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
specifier|final
name|URLRewrite
name|action
init|=
name|mapping
operator|.
name|action
operator|.
name|copy
argument_list|()
decl_stmt|;
if|if
condition|(
name|copyFrom
operator|!=
literal|null
condition|)
block|{
name|action
operator|.
name|copyFrom
argument_list|(
name|copyFrom
argument_list|)
expr_stmt|;
block|}
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
specifier|final
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
literal|"/"
operator|.
name|equals
argument_list|(
name|matchedString
argument_list|)
condition|)
block|{
name|action
operator|.
name|setPrefix
argument_list|(
name|matchedString
argument_list|)
expr_stmt|;
block|}
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
block|{
return|return
name|action
return|;
block|}
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
specifier|final
name|String
name|controllerConfig
parameter_list|)
throws|throws
name|ServletException
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
literal|"Loading XQueryURLRewrite configuration from "
operator|+
name|controllerConfig
argument_list|)
expr_stmt|;
block|}
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
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|urlRewrite
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|ofNullable
argument_list|(
name|urlRewrite
operator|.
name|getDefaultUser
argument_list|()
argument_list|)
argument_list|)
init|)
block|{
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDocument
init|=
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
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|;
init|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|lockedDocument
operator|==
literal|null
condition|?
literal|null
else|:
name|lockedDocument
operator|.
name|getDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|parse
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
decl||
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
block|}
else|else
block|{
try|try
block|{
specifier|final
name|Path
name|d
init|=
name|Paths
operator|.
name|get
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
literal|"/"
argument_list|)
argument_list|)
operator|.
name|normalize
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|configFile
init|=
name|d
operator|.
name|resolve
argument_list|(
name|controllerConfig
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|configFile
argument_list|)
condition|)
block|{
specifier|final
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
specifier|final
name|ParserConfigurationException
decl||
name|IOException
decl||
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
block|}
name|urlRewrite
operator|.
name|clearCaches
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|parse
parameter_list|(
specifier|final
name|Document
name|doc
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
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
specifier|final
name|String
name|ns
init|=
name|child
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
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
name|Namespaces
operator|.
name|EXIST_NS
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
specifier|final
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
block|{
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
block|}
specifier|final
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
block|{
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
block|}
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
specifier|final
name|ServletConfig
name|config
parameter_list|,
specifier|final
name|String
name|pattern
parameter_list|,
specifier|final
name|Element
name|action
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
name|URLRewrite
name|rewrite
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
specifier|final
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
comment|/*              * If there is a server-name attribute on the root tag, then add that              * as an attribute on the ControllerForward object.              */
specifier|final
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
else|else
block|{
name|rewrite
operator|=
literal|null
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
specifier|final
name|Path
name|file
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
specifier|final
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
decl_stmt|;
specifier|final
name|XMLReaderPool
name|parserPool
init|=
name|urlRewrite
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
decl_stmt|;
name|XMLReader
name|xr
init|=
literal|null
decl_stmt|;
try|try
block|{
name|xr
operator|=
name|parserPool
operator|.
name|borrowXMLReader
argument_list|()
expr_stmt|;
specifier|final
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
finally|finally
block|{
if|if
condition|(
name|xr
operator|!=
literal|null
condition|)
block|{
name|parserPool
operator|.
name|returnXMLReader
argument_list|(
name|xr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Maps a regular expression to an URLRewrite instance      */
specifier|private
specifier|static
specifier|final
class|class
name|Mapping
block|{
specifier|private
specifier|final
name|Pattern
name|pattern
decl_stmt|;
specifier|private
specifier|final
name|URLRewrite
name|action
decl_stmt|;
specifier|private
name|Matcher
name|matcher
decl_stmt|;
specifier|private
name|Mapping
parameter_list|(
name|String
name|regex
parameter_list|,
specifier|final
name|URLRewrite
name|action
parameter_list|)
throws|throws
name|ServletException
block|{
try|try
block|{
specifier|final
name|int
name|xmlVersion
init|=
literal|11
decl_stmt|;
specifier|final
name|boolean
name|ignoreWhitespace
init|=
literal|false
decl_stmt|;
specifier|final
name|boolean
name|caseBlind
init|=
literal|false
decl_stmt|;
name|regex
operator|=
name|JDK15RegexTranslator
operator|.
name|translate
argument_list|(
name|regex
argument_list|,
name|xmlVersion
argument_list|,
literal|true
argument_list|,
name|ignoreWhitespace
argument_list|,
name|caseBlind
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
name|this
operator|.
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
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
specifier|final
name|String
name|path
parameter_list|)
block|{
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
block|}
end_class

end_unit

