begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
package|;
end_package

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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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
name|Templates
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
name|TransformerConfigurationException
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
name|URIResolver
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
name|sax
operator|.
name|SAXTransformerFactory
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
name|sax
operator|.
name|TemplatesHandler
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
name|sax
operator|.
name|TransformerHandler
import|;
end_import

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|ThreadSafe
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|PkgXsltModuleURIResolver
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
name|util
operator|.
name|EXistURISchemeURIResolver
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
name|URIResolverHierarchy
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
name|Document
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
import|import static
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|XsltURIResolverHelper
operator|.
name|getXsltURIResolver
import|;
end_import

begin_comment
comment|/**  * Factory for stylesheet resolver and compiler instances  * and if instance is safe for reuse then it cached.  *  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
annotation|@
name|ThreadSafe
specifier|public
class|class
name|TemplatesFactory
block|{
specifier|private
specifier|final
specifier|static
name|ConcurrentMap
argument_list|<
name|String
argument_list|,
name|StylesheetResolverAndCompiler
argument_list|>
name|cache
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|Stylesheet
name|stylesheet
parameter_list|(
name|String
name|stylesheet
parameter_list|,
name|String
name|baseUri
parameter_list|,
name|Properties
name|properties
parameter_list|,
name|boolean
name|useCache
parameter_list|)
block|{
if|if
condition|(
name|useCache
operator|&&
name|properties
operator|==
literal|null
condition|)
block|{
return|return
name|stylesheet
argument_list|(
name|stylesheet
argument_list|,
name|baseUri
argument_list|)
return|;
block|}
name|String
name|uri
init|=
name|uri
argument_list|(
name|stylesheet
argument_list|,
name|baseUri
argument_list|)
decl_stmt|;
return|return
operator|new
name|StylesheetResolverAndCompiler
argument_list|(
name|uri
argument_list|,
name|properties
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Stylesheet
name|stylesheet
parameter_list|(
name|String
name|stylesheet
parameter_list|,
name|String
name|baseUri
parameter_list|)
block|{
name|String
name|uri
init|=
name|uri
argument_list|(
name|stylesheet
argument_list|,
name|baseUri
argument_list|)
decl_stmt|;
return|return
name|cache
operator|.
name|computeIfAbsent
argument_list|(
name|uri
argument_list|,
name|StylesheetResolverAndCompiler
operator|::
operator|new
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Stylesheet
name|stylesheet
parameter_list|(
name|String
name|stylesheet
parameter_list|,
name|String
name|baseUri
parameter_list|,
name|boolean
name|useCache
parameter_list|)
block|{
if|if
condition|(
name|useCache
condition|)
block|{
return|return
name|stylesheet
argument_list|(
name|stylesheet
argument_list|,
name|baseUri
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|StylesheetResolverAndCompiler
argument_list|(
name|uri
argument_list|(
name|stylesheet
argument_list|,
name|baseUri
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|uri
parameter_list|(
name|String
name|stylesheet
parameter_list|,
name|String
name|baseUri
parameter_list|)
block|{
name|String
name|uri
init|=
name|stylesheet
decl_stmt|;
if|if
condition|(
name|stylesheet
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|Path
name|f
init|=
name|Paths
operator|.
name|get
argument_list|(
name|stylesheet
argument_list|)
operator|.
name|normalize
argument_list|()
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|uri
operator|=
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
name|Paths
operator|.
name|get
argument_list|(
name|baseUri
argument_list|,
name|stylesheet
argument_list|)
operator|.
name|normalize
argument_list|()
expr_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|uri
operator|=
name|f
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
return|return
name|uri
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|Stylesheet
name|stylesheet
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|NodeValue
name|node
parameter_list|,
name|String
name|baseUri
parameter_list|)
throws|throws
name|E
throws|,
name|TransformerConfigurationException
block|{
name|SAXTransformerFactory
name|factory
init|=
name|TransformerFactoryAllocator
operator|.
name|getTransformerFactory
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|base
init|=
name|baseUri
decl_stmt|;
name|Document
name|doc
init|=
name|node
operator|.
name|getOwnerDocument
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|String
name|uri
init|=
name|doc
operator|.
name|getDocumentURI
argument_list|()
decl_stmt|;
comment|/*        * This must be checked because in the event the stylesheet is        * an in-memory document, it will cause an NPE        */
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
name|base
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|uri
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// setup any URI resolvers
specifier|final
name|URIResolver
name|uriResolver
init|=
name|getXsltURIResolver
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|factory
operator|.
name|getURIResolver
argument_list|()
argument_list|,
name|base
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setURIResolver
argument_list|(
name|uriResolver
argument_list|)
expr_stmt|;
return|return
operator|new
name|Stylesheet
argument_list|()
block|{
annotation|@
name|Override
specifier|public
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|Templates
name|templates
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XSLTErrorsListener
argument_list|<
name|E
argument_list|>
name|errorListener
parameter_list|)
throws|throws
name|E
throws|,
name|TransformerConfigurationException
throws|,
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
block|{
specifier|final
name|TemplatesHandler
name|handler
init|=
name|factory
operator|.
name|newTemplatesHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|node
operator|.
name|toSAX
argument_list|(
name|broker
argument_list|,
name|handler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
specifier|final
name|Templates
name|t
init|=
name|handler
operator|.
name|getTemplates
argument_list|()
decl_stmt|;
comment|//check for errors
name|errorListener
operator|.
name|checkForErrors
argument_list|()
expr_stmt|;
return|return
name|t
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|E
extends|extends
name|Exception
parameter_list|>
name|TransformerHandler
name|newTransformerHandler
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XSLTErrorsListener
argument_list|<
name|E
argument_list|>
name|errorListener
parameter_list|)
throws|throws
name|E
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|TransformerConfigurationException
throws|,
name|IOException
block|{
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|(
name|templates
argument_list|(
name|broker
argument_list|,
name|errorListener
argument_list|)
argument_list|)
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setErrorListener
argument_list|(
name|errorListener
argument_list|)
expr_stmt|;
return|return
name|handler
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

