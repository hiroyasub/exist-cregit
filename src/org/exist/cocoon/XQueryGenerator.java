begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|ByteArrayOutputStream
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
name|CompiledExpression
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
comment|/**  * A generator for Cocoon which reads an XQuery script, executes it and passes  * the results into the Cocoon pipeline.  *   * The following sitemap parameters (with default values) are accepted:  *   *<pre>  *&lt;map:parameter name=&quot;collection&quot; value=&quot;xmldb:exist:///db&quot;/&gt;  *&lt;map:parameter name=&quot;user&quot; value=&quot;guest&quot;/&gt;  *&lt;map:parameter name=&quot;password&quot; value=&quot;guest&quot;/&gt;  *&lt;map:parameter name=&quot;create-session&quot; value=&quot;false&quot;/&gt;  *&lt;map:parameter name=&quot;expand-xincludes&quot; value=&quot;false&quot;/&gt;  *</pre>  *   * Parameter collection identifies the XML:DB root collection used to process  * the request. If set to "true", parameter create-session indicates that an  * HTTP session should be created upon the first invocation.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XQueryGenerator
extends|extends
name|ServiceableGenerator
block|{
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
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|expandXIncludes
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|collectionURI
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|defaultUser
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|defaultPassword
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
name|optionalParameters
decl_stmt|;
specifier|private
name|ThreadLocal
name|cache
init|=
operator|new
name|ThreadLocal
argument_list|()
block|{
comment|/* (non-Javadoc) 		 * @see java.lang.ThreadLocal#initialValue() 		 */
specifier|protected
name|Object
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|private
class|class
name|CachedExpression
block|{
name|SourceValidity
name|validity
decl_stmt|;
name|CompiledExpression
name|expr
decl_stmt|;
specifier|public
name|CachedExpression
parameter_list|(
name|SourceValidity
name|validity
parameter_list|,
name|CompiledExpression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|validity
operator|=
name|validity
expr_stmt|;
name|this
operator|.
name|expr
operator|=
name|expr
expr_stmt|;
block|}
block|}
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
literal|"collection"
argument_list|,
literal|"xmldb:exist:///db"
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultUser
operator|=
name|parameters
operator|.
name|getParameter
argument_list|(
literal|"user"
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
name|this
operator|.
name|defaultPassword
operator|=
name|parameters
operator|.
name|getParameter
argument_list|(
literal|"password"
argument_list|,
literal|"guest"
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
literal|"create-session"
argument_list|,
literal|false
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
literal|"expand-xincludes"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|optionalParameters
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|String
name|paramNames
index|[]
init|=
name|parameters
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
if|if
condition|(
operator|!
operator|(
name|param
operator|.
name|equals
argument_list|(
literal|"collection"
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
literal|"user"
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
literal|"password"
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
literal|"create-session"
argument_list|)
operator|||
name|param
operator|.
name|equals
argument_list|(
literal|"expand-xincludes"
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
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.apache.cocoon.generation.Generator#generate() 	 */
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
name|String
name|baseURI
init|=
name|request
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|int
name|p
init|=
name|baseURI
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
name|baseURI
operator|=
name|baseURI
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|baseURI
operator|=
name|context
operator|.
name|getRealPath
argument_list|(
name|baseURI
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
argument_list|)
expr_stmt|;
name|String
name|user
init|=
literal|null
decl_stmt|;
name|String
name|password
init|=
literal|null
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
name|user
operator|=
operator|(
name|String
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|password
operator|=
operator|(
name|String
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
literal|"password"
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
name|String
name|prefix
init|=
name|RequestModule
operator|.
name|PREFIX
decl_stmt|;
name|service
operator|.
name|setNamespace
argument_list|(
name|prefix
argument_list|,
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|)
expr_stmt|;
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
name|prefix
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
name|prefix
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
name|service
operator|.
name|declareVariable
argument_list|(
name|prefix
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
block|}
name|declareParameters
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|inputSource
operator|.
name|getURI
argument_list|()
decl_stmt|;
name|CompiledExpression
name|expr
decl_stmt|;
name|CachedExpression
name|cached
decl_stmt|;
name|cached
operator|=
operator|(
name|CachedExpression
operator|)
operator|(
operator|(
name|Map
operator|)
name|cache
operator|.
name|get
argument_list|()
operator|)
operator|.
name|get
argument_list|(
name|uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|cached
operator|!=
literal|null
condition|)
block|{
comment|// check if source is valid or should be reloaded
name|int
name|valid
init|=
name|cached
operator|.
name|validity
operator|.
name|isValid
argument_list|()
decl_stmt|;
if|if
condition|(
name|valid
operator|==
name|SourceValidity
operator|.
name|UNKNOWN
condition|)
name|valid
operator|=
name|cached
operator|.
name|validity
operator|.
name|isValid
argument_list|(
name|inputSource
operator|.
name|getValidity
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|valid
operator|!=
name|SourceValidity
operator|.
name|VALID
condition|)
block|{
operator|(
operator|(
name|Map
operator|)
name|cache
operator|.
name|get
argument_list|()
operator|)
operator|.
name|remove
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|cached
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|cached
operator|==
literal|null
condition|)
block|{
name|String
name|xquery
init|=
name|readQuery
argument_list|()
decl_stmt|;
name|expr
operator|=
name|service
operator|.
name|compile
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|cached
operator|=
operator|new
name|CachedExpression
argument_list|(
name|inputSource
operator|.
name|getValidity
argument_list|()
argument_list|,
name|expr
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Map
operator|)
name|cache
operator|.
name|get
argument_list|()
operator|)
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|cached
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|expr
operator|=
name|cached
operator|.
name|expr
expr_stmt|;
block|}
name|ResourceSet
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
name|expr
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
name|this
operator|.
name|contentHandler
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
name|String
name|readQuery
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
operator|(
name|int
operator|)
name|inputSource
operator|.
name|getContentLength
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|t
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|InputStream
name|is
init|=
name|inputSource
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|t
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|t
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
return|return
name|os
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
return|;
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
block|}
end_class

end_unit

