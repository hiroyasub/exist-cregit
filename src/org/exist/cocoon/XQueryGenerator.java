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
name|Enumeration
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
name|generation
operator|.
name|AbstractGenerator
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
name|XPathQueryServiceImpl
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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XQueryGenerator
extends|extends
name|AbstractGenerator
block|{
specifier|private
name|Source
name|inputSource
init|=
literal|null
decl_stmt|;
specifier|private
name|Request
name|request
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|mapRequestParams
init|=
literal|true
decl_stmt|;
specifier|private
name|String
name|collectionURI
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|user
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|password
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.apache.cocoon.generation.AbstractGenerator#setup(org.apache.cocoon.environment.SourceResolver, java.util.Map, java.lang.String, org.apache.avalon.framework.parameters.Parameters) 	 */
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
name|request
operator|=
name|ObjectModelHelper
operator|.
name|getRequest
argument_list|(
name|objectModel
argument_list|)
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
name|user
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
name|password
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
name|mapRequestParams
operator|=
name|parameters
operator|.
name|getParameterAsBoolean
argument_list|(
literal|"use-request-parameters"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.cocoon.generation.Generator#generate() 	 */
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
name|XPathQueryServiceImpl
name|service
init|=
operator|(
name|XPathQueryServiceImpl
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
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
name|String
name|xquery
init|=
name|readQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|getLogger
argument_list|()
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|getLogger
argument_list|()
operator|.
name|debug
argument_list|(
literal|"XQuery: "
operator|+
name|xquery
argument_list|)
expr_stmt|;
if|if
condition|(
name|mapRequestParams
condition|)
name|mapRequestParams
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|xquery
argument_list|)
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
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|resource
operator|.
name|getContent
argument_list|()
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
name|void
name|mapRequestParams
parameter_list|(
name|XPathQueryServiceImpl
name|service
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|param
decl_stmt|;
name|String
name|values
index|[]
decl_stmt|;
for|for
control|(
name|Enumeration
name|enum
type|=
name|request
operator|.
name|getParameterNames
decl|()
init|;
condition|enum
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|param
operator|=
operator|(
name|String
operator|)
expr|enum
operator|.
name|nextElement
argument_list|()
expr_stmt|;
name|values
operator|=
name|request
operator|.
name|getParameterValues
argument_list|(
name|param
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
name|service
operator|.
name|declareVariable
argument_list|(
name|param
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
else|else
name|service
operator|.
name|declareVariable
argument_list|(
name|param
argument_list|,
name|values
argument_list|)
expr_stmt|;
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
operator|new
name|String
argument_list|(
name|os
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

