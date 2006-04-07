begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|cocoon
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
name|io
operator|.
name|Serializable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|TreeMap
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
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|configuration
operator|.
name|Configurable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|configuration
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|configuration
operator|.
name|ConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|parameters
operator|.
name|ParameterException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|parameters
operator|.
name|Parameterizable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|parameters
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|ProcessingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|caching
operator|.
name|CacheableProcessingComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|ObjectModelHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|SourceResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|http
operator|.
name|HttpEnvironment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|generation
operator|.
name|ServiceableGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|xml
operator|.
name|IncludeXMLConsumer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|source
operator|.
name|SourceValidity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|source
operator|.
name|impl
operator|.
name|validity
operator|.
name|AggregatedValidity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|source
operator|.
name|impl
operator|.
name|validity
operator|.
name|ExpiresValidity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|Descriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|CocoonSource
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|serializers
operator|.
name|Serializer
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
name|CollectionImpl
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
name|XQueryService
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
name|functions
operator|.
name|request
operator|.
name|RequestModule
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
name|functions
operator|.
name|response
operator|.
name|ResponseModule
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
name|functions
operator|.
name|session
operator|.
name|SessionModule
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
name|Sequence
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
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
name|Collection
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
name|Database
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
name|ResourceSet
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * A generator for Cocoon which reads an XQuery script, executes it and passes  * the results into the Cocoon pipeline.  *   * The following optional attributes are accepted on the component declaration as default eXist settings:  *<li><tt>collection</tt>: identifies the XML:DB root collection used to process  * the request</li>  *<li><tt>user</tt></li>  *<li><tt>password</tt></li>  *<li><tt>create-session</tt>: if set to "true", indicates that an  * HTTP session should be created upon the first invocation.</li>  *<li><tt>expand-xincludes</tt></li>  *<li><tt>cache-validity</tt>: if specified, the XQuery content is  * cached until the specified delay expressed in milliseconds is elapsed  * or until the XQuery file is modified.  The identity of the cached content is  * computed using the XQuery file URI and the list of all parameters passed to  * the XQuery.</li>  *   * The component also accept default parameters that will be declared as implicit variables in the XQuery.  * See below an example declaration of the XQueryGenerator component with default eXist settings, and an extra user-defined parameter:  *   *<map:generator logger="xmldb" name="xquery"  * 		collection="xmldb:exist:///db/"  * 		user="guest"  * 		password="guest"  *		create-session="false"  * 		expand-xincludes="false"  *		cache-validity="-1"  *		src="org.exist.cocoon.XQueryGenerator">  *<parameter name="myProjectURI" value="/db/myproject"/>  *</map:generator>  *   * These settings and parameters can be overriden on a per-pipeline basis with sitemap parameters, see below with default values and the extra user-defined parameter:  *   *<pre>  *&lt;map:parameter name=&quot;collection&quot; value=&quot;xmldb:exist:///db&quot;/&gt;  *&lt;map:parameter name=&quot;user&quot; value=&quot;guest&quot;/&gt;  *&lt;map:parameter name=&quot;password&quot; value=&quot;guest&quot;/&gt;  *&lt;map:parameter name=&quot;create-session&quot; value=&quot;false&quot;/&gt;  *&lt;map:parameter name=&quot;expand-xincludes&quot; value=&quot;false&quot;/&gt;  *&lt;map:parameter name=&quot;cache-validity&quot; value=&quot;-1quot;/&gt;  *&lt;map:parameter name=&quot;myProjectURI&quot; value=&quot;/db/myproject&quot;/&gt;  *</pre>  *   * The last sitemap parameter overrides the value of the XQuery variable defined in the component parameters,  * whereas others override the default eXist settings defined on the component attributes.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XQueryGenerator
extends|extends
name|ServiceableGenerator
implements|implements
name|Configurable
implements|,
name|Parameterizable
implements|,
name|CacheableProcessingComponent
block|{
specifier|public
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
name|Source
name|inputSource
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
name|objectModel
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|createSession
decl_stmt|;
specifier|private
name|boolean
name|defaultCreateSession
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CREATE_SESSION
init|=
literal|"create-session"
decl_stmt|;
specifier|private
name|boolean
name|expandXIncludes
decl_stmt|;
specifier|private
name|boolean
name|defaultExpandXIncludes
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EXPAND_XINCLUDES
init|=
literal|"expand-xincludes"
decl_stmt|;
specifier|private
name|String
name|collectionURI
decl_stmt|;
specifier|private
name|String
name|defaultCollectionURI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COLLECTION_URI
init|=
literal|"collection"
decl_stmt|;
specifier|private
name|long
name|cacheValidity
decl_stmt|;
specifier|private
name|long
name|defaultCacheValidity
init|=
name|SourceValidity
operator|.
name|INVALID
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CACHE_VALIDITY
init|=
literal|"cache-validity"
decl_stmt|;
specifier|private
name|String
name|user
decl_stmt|;
specifier|private
name|String
name|defaultUser
init|=
literal|"guest"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|USER
init|=
literal|"user"
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|String
name|defaultPassword
init|=
literal|"guest"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PASSWORD
init|=
literal|"password"
decl_stmt|;
specifier|private
name|Map
name|optionalParameters
decl_stmt|;
specifier|private
name|Parameters
name|componentParams
decl_stmt|;
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.apache.cocoon.generation.AbstractGenerator#setup(org.apache.cocoon.environment.SourceResolver, 	 *         java.util.Map, java.lang.String, 	 *         org.apache.avalon.framework.parameters.Parameters) 	 */
specifier|public
name|void
name|setup
parameter_list|(
name|SourceResolver
name|resolver
parameter_list|,
name|Map
name|objectModel
parameter_list|,
name|String
name|source
parameter_list|,
name|Parameters
name|parameters
parameter_list|)
throws|throws
name|ProcessingException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|super
operator|.
name|setup
argument_list|(
name|resolver
argument_list|,
name|objectModel
argument_list|,
name|source
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
comment|/* 		 * We don't do this directly in parameterize() because setup() can be 		 * called multiple times and optionalParameters needs resetting to forget 		 * sitemap parameters that may have been removed inbetween 		 *  		 * The map must be sorted so that getKey() always returns the same 		 * object for any given oder of parameters. 		 */
name|this
operator|.
name|optionalParameters
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
name|String
name|paramNames
index|[]
init|=
name|componentParams
operator|.
name|getNames
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paramNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|param
init|=
name|paramNames
index|[
name|i
index|]
decl_stmt|;
try|try
block|{
name|optionalParameters
operator|.
name|put
argument_list|(
name|param
argument_list|,
name|componentParams
operator|.
name|getParameter
argument_list|(
name|param
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParameterException
name|e1
parameter_list|)
block|{
comment|// Cannot happen as we iterate through existing parameters
block|}
block|}
name|this
operator|.
name|objectModel
operator|=
name|objectModel
expr_stmt|;
name|this
operator|.
name|inputSource
operator|=
name|resolver
operator|.
name|resolveURI
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|collectionURI
operator|=
name|parameters
operator|.
name|getParameter
argument_list|(
name|COLLECTION_URI
argument_list|,
name|this
operator|.
name|defaultCollectionURI
argument_list|)
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|parameters
operator|.
name|getParameter
argument_list|(
name|USER
argument_list|,
name|this
operator|.
name|defaultUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|parameters
operator|.
name|getParameter
argument_list|(
name|PASSWORD
argument_list|,
name|this
operator|.
name|defaultPassword
argument_list|)
expr_stmt|;
name|this
operator|.
name|createSession
operator|=
name|parameters
operator|.
name|getParameterAsBoolean
argument_list|(
name|CREATE_SESSION
argument_list|,
name|this
operator|.
name|defaultCreateSession
argument_list|)
expr_stmt|;
name|this
operator|.
name|expandXIncludes
operator|=
name|parameters
operator|.
name|getParameterAsBoolean
argument_list|(
name|EXPAND_XINCLUDES
argument_list|,
name|this
operator|.
name|defaultExpandXIncludes
argument_list|)
expr_stmt|;
name|this
operator|.
name|cacheValidity
operator|=
name|parameters
operator|.
name|getParameterAsLong
argument_list|(
name|CACHE_VALIDITY
argument_list|,
name|defaultCacheValidity
argument_list|)
expr_stmt|;
name|paramNames
operator|=
name|parameters
operator|.
name|getNames
argument_list|()
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
name|paramNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|param
init|=
name|paramNames
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|param
operator|.
name|equals
argument_list|(
name|COLLECTION_URI
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
name|USER
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
name|PASSWORD
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
name|CREATE_SESSION
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
name|EXPAND_XINCLUDES
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
name|CACHE_VALIDITY
argument_list|)
operator|)
condition|)
block|{
name|this
operator|.
name|optionalParameters
operator|.
name|put
argument_list|(
name|param
argument_list|,
name|parameters
operator|.
name|getParameter
argument_list|(
name|param
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|Context
name|context
init|=
name|ObjectModelHelper
operator|.
name|getContext
argument_list|(
name|objectModel
argument_list|)
decl_stmt|;
name|String
name|dbHome
init|=
name|context
operator|.
name|getRealPath
argument_list|(
literal|"WEB-INF"
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
name|driver
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|driver
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"configuration"
argument_list|,
name|dbHome
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"conf.xml"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProcessingException
argument_list|(
literal|"Failed to initialize database driver: "
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.apache.cocoon.generation.AbstractGenerator#recycle() 	 */
specifier|public
name|void
name|recycle
parameter_list|()
block|{
if|if
condition|(
name|resolver
operator|!=
literal|null
condition|)
name|resolver
operator|.
name|release
argument_list|(
name|inputSource
argument_list|)
expr_stmt|;
name|inputSource
operator|=
literal|null
expr_stmt|;
name|super
operator|.
name|recycle
argument_list|()
expr_stmt|;
block|}
comment|/** @see org.apache.cocoon.generation.Generator#generate() */
specifier|public
name|void
name|generate
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ProcessingException
block|{
name|ContentHandler
name|includeContentHandler
decl_stmt|;
if|if
condition|(
name|inputSource
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ProcessingException
argument_list|(
literal|"No input source"
argument_list|)
throw|;
name|Request
name|request
init|=
name|ObjectModelHelper
operator|.
name|getRequest
argument_list|(
name|objectModel
argument_list|)
decl_stmt|;
name|Response
name|response
init|=
name|ObjectModelHelper
operator|.
name|getResponse
argument_list|(
name|objectModel
argument_list|)
decl_stmt|;
name|Context
name|context
init|=
name|ObjectModelHelper
operator|.
name|getContext
argument_list|(
name|objectModel
argument_list|)
decl_stmt|;
name|Session
name|session
init|=
name|request
operator|.
name|getSession
argument_list|(
name|createSession
argument_list|)
decl_stmt|;
specifier|final
name|String
name|servletPath
init|=
name|request
operator|.
name|getServletPath
argument_list|()
decl_stmt|;
specifier|final
name|String
name|pathInfo
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
name|StringBuffer
name|baseURIBuffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|servletPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathInfo
operator|!=
literal|null
condition|)
name|baseURIBuffer
operator|.
name|append
argument_list|(
name|pathInfo
argument_list|)
expr_stmt|;
name|int
name|p
init|=
name|baseURIBuffer
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
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
name|baseURIBuffer
operator|.
name|delete
argument_list|(
name|p
argument_list|,
name|baseURIBuffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|baseURI
init|=
name|context
operator|.
name|getRealPath
argument_list|(
name|baseURIBuffer
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// check if user and password can be read from the session
if|if
condition|(
name|session
operator|!=
literal|null
operator|&&
name|request
operator|.
name|isRequestedSessionIdValid
argument_list|()
condition|)
block|{
name|String
name|actualUser
init|=
name|getSessionAttribute
argument_list|(
name|session
argument_list|,
literal|"user"
argument_list|)
decl_stmt|;
name|String
name|actualPass
init|=
name|getSessionAttribute
argument_list|(
name|session
argument_list|,
literal|"password"
argument_list|)
decl_stmt|;
name|user
operator|=
name|actualUser
operator|==
literal|null
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|actualUser
argument_list|)
expr_stmt|;
name|password
operator|=
name|actualPass
operator|==
literal|null
condition|?
literal|null
else|:
name|String
operator|.
name|valueOf
argument_list|(
name|actualPass
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|user
operator|==
literal|null
condition|)
name|user
operator|=
name|defaultUser
expr_stmt|;
if|if
condition|(
name|password
operator|==
literal|null
condition|)
name|password
operator|=
name|defaultPassword
expr_stmt|;
try|try
block|{
name|Collection
name|collection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionURI
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|getLogger
argument_list|()
operator|.
name|isErrorEnabled
argument_list|()
condition|)
name|getLogger
argument_list|()
operator|.
name|error
argument_list|(
literal|"Collection "
operator|+
name|collectionURI
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ProcessingException
argument_list|(
literal|"Collection "
operator|+
name|collectionURI
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
name|Serializer
operator|.
name|GENERATE_DOC_EVENTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
name|expandXIncludes
condition|?
literal|"yes"
else|:
literal|"no"
argument_list|)
expr_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
literal|"base-uri"
argument_list|,
name|baseURI
argument_list|)
expr_stmt|;
comment|//service.setNamespace(RequestModule.PREFIX, RequestModule.NAMESPACE_URI);
name|service
operator|.
name|setModuleLoadPath
argument_list|(
name|baseURI
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|CollectionImpl
operator|)
name|collection
operator|)
operator|.
name|isRemoteCollection
argument_list|()
condition|)
block|{
name|HttpServletRequest
name|httpRequest
init|=
operator|(
name|HttpServletRequest
operator|)
name|objectModel
operator|.
name|get
argument_list|(
name|HttpEnvironment
operator|.
name|HTTP_REQUEST_OBJECT
argument_list|)
decl_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
name|RequestModule
operator|.
name|PREFIX
operator|+
literal|":request"
argument_list|,
operator|new
name|CocoonRequestWrapper
argument_list|(
name|request
argument_list|,
name|httpRequest
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
name|ResponseModule
operator|.
name|PREFIX
operator|+
literal|":response"
argument_list|,
operator|new
name|CocoonResponseWrapper
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
name|service
operator|.
name|declareVariable
argument_list|(
name|SessionModule
operator|.
name|PREFIX
operator|+
literal|":session"
argument_list|,
operator|new
name|CocoonSessionWrapper
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|includeContentHandler
operator|=
name|this
operator|.
name|contentHandler
expr_stmt|;
block|}
else|else
block|{
name|includeContentHandler
operator|=
operator|new
name|IncludeXMLConsumer
argument_list|(
name|this
operator|.
name|contentHandler
argument_list|)
expr_stmt|;
block|}
name|declareParameters
argument_list|(
name|service
argument_list|)
expr_stmt|;
comment|// String uri = inputSource.getURI();
name|log
argument_list|(
name|request
argument_list|,
name|service
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
operator|new
name|CocoonSource
argument_list|(
name|inputSource
argument_list|,
literal|true
argument_list|)
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
decl_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|resource
operator|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|includeContentHandler
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ProcessingException
argument_list|(
literal|"XMLDBException occurred: "
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
name|declareParameters
parameter_list|(
name|XQueryService
name|service
parameter_list|)
throws|throws
name|XMLDBException
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|optionalParameters
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getSessionAttribute
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|attribute
parameter_list|)
block|{
name|Object
name|obj
init|=
name|session
operator|.
name|getAttribute
argument_list|(
name|attribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|obj
operator|instanceof
name|Sequence
condition|)
try|try
block|{
return|return
operator|(
operator|(
name|Sequence
operator|)
name|obj
operator|)
operator|.
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|obj
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** 	 * @see org.apache.avalon.framework.configuration.Configurable#configure(org.apache.avalon.framework.configuration.Configuration) 	 */
specifier|public
name|void
name|configure
parameter_list|(
name|Configuration
name|config
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
operator|.
name|defaultCollectionURI
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
name|COLLECTION_URI
argument_list|,
name|this
operator|.
name|defaultCollectionURI
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultCreateSession
operator|=
name|config
operator|.
name|getAttributeAsBoolean
argument_list|(
name|CREATE_SESSION
argument_list|,
name|this
operator|.
name|defaultCreateSession
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultExpandXIncludes
operator|=
name|config
operator|.
name|getAttributeAsBoolean
argument_list|(
name|EXPAND_XINCLUDES
argument_list|,
name|this
operator|.
name|defaultExpandXIncludes
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultPassword
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
name|PASSWORD
argument_list|,
name|this
operator|.
name|defaultPassword
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultUser
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
name|USER
argument_list|,
name|this
operator|.
name|defaultUser
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultCacheValidity
operator|=
name|config
operator|.
name|getAttributeAsLong
argument_list|(
name|CACHE_VALIDITY
argument_list|,
name|this
operator|.
name|defaultCacheValidity
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.apache.avalon.framework.parameters.Parameterizable#parameterize(org.apache.avalon.framework.parameters.Parameters) 	 */
specifier|public
name|void
name|parameterize
parameter_list|(
name|Parameters
name|params
parameter_list|)
throws|throws
name|ParameterException
block|{
name|this
operator|.
name|componentParams
operator|=
name|params
expr_stmt|;
block|}
specifier|public
name|Serializable
name|getKey
parameter_list|()
block|{
name|StringBuffer
name|key
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|key
operator|.
name|append
argument_list|(
name|optionalParameters
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|key
operator|.
name|append
argument_list|(
name|inputSource
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|key
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|SourceValidity
name|getValidity
parameter_list|()
block|{
if|if
condition|(
name|cacheValidity
operator|!=
name|SourceValidity
operator|.
name|INVALID
condition|)
block|{
name|AggregatedValidity
name|v
init|=
operator|new
name|AggregatedValidity
argument_list|()
decl_stmt|;
if|if
condition|(
name|inputSource
operator|.
name|getValidity
argument_list|()
operator|!=
literal|null
condition|)
name|v
operator|.
name|add
argument_list|(
name|inputSource
operator|.
name|getValidity
argument_list|()
argument_list|)
expr_stmt|;
name|v
operator|.
name|add
argument_list|(
operator|new
name|ExpiresValidity
argument_list|(
name|cacheValidity
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|v
return|;
block|}
return|return
literal|null
return|;
block|}
name|HttpServletRequest
name|getRequest
parameter_list|()
block|{
return|return
operator|(
name|HttpServletRequest
operator|)
name|objectModel
operator|.
name|get
argument_list|(
literal|"httprequest"
argument_list|)
return|;
block|}
comment|/** Static method to log the HTTP requests received by the {@link XQueryGenerator} */
specifier|private
specifier|static
name|void
name|log
parameter_list|(
name|Request
name|request
parameter_list|,
name|XQueryService
name|service
parameter_list|,
name|XQueryGenerator
name|generator
parameter_list|)
block|{
name|Descriptor
name|descriptor
init|=
name|Descriptor
operator|.
name|getDescriptorSingleton
argument_list|()
decl_stmt|;
if|if
condition|(
name|descriptor
operator|.
name|allowRequestLogging
argument_list|()
condition|)
block|{
name|HttpServletRequest
name|servletRequest
init|=
name|generator
operator|.
name|getRequest
argument_list|()
decl_stmt|;
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|servletRequest
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

