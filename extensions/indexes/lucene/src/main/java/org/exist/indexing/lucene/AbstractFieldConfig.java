begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
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
name|lang3
operator|.
name|StringUtils
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|CollectionConfigurationManager
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
name|dom
operator|.
name|persistent
operator|.
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|xquery
operator|.
name|CompiledXQuery
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
name|XQuery
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
name|XQueryContext
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
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|Optional
import|;
end_import

begin_comment
comment|/**  * Abstract configuration corresponding to either a field or facet element nested inside  * a index definition 'text' element. Adds the possibility to create index content based  * on an arbitrary XQuery expression.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractFieldConfig
block|{
specifier|public
specifier|final
specifier|static
name|String
name|XPATH_ATTR
init|=
literal|"expression"
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|AbstractFieldConfig
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Optional
argument_list|<
name|String
argument_list|>
name|expression
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|isValid
init|=
literal|true
decl_stmt|;
specifier|private
name|CompiledXQuery
name|compiled
init|=
literal|null
decl_stmt|;
specifier|public
name|AbstractFieldConfig
parameter_list|(
name|LuceneConfig
name|config
parameter_list|,
name|Element
name|configElement
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
block|{
specifier|final
name|String
name|xpath
init|=
name|configElement
operator|.
name|getAttribute
argument_list|(
name|XPATH_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|xpath
argument_list|)
condition|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|namespaces
operator|.
name|forEach
argument_list|(
parameter_list|(
name|prefix
parameter_list|,
name|uri
parameter_list|)
lambda|->
block|{
if|if
condition|(
operator|!
literal|"xml"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"declare namespace "
argument_list|)
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
operator|.
name|append
argument_list|(
name|uri
argument_list|)
operator|.
name|append
argument_list|(
literal|"\";\n"
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|config
operator|.
name|getImports
argument_list|()
operator|.
name|ifPresent
argument_list|(
name|moduleImports
lambda|->
block|{
name|moduleImports
operator|.
name|forEach
argument_list|(
operator|(
name|moduleImport
lambda|->
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"import module namespace "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|moduleImport
operator|.
name|prefix
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"=\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|moduleImport
operator|.
name|uri
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\" at \""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|resolveURI
argument_list|(
name|configElement
operator|.
name|getBaseURI
argument_list|()
argument_list|,
name|moduleImport
operator|.
name|at
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\";\n"
argument_list|)
expr_stmt|;
block|}
operator|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|xpath
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nullable
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|protected
specifier|abstract
name|void
name|processResult
parameter_list|(
name|Sequence
name|result
parameter_list|,
name|Document
name|luceneDoc
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|processText
parameter_list|(
name|CharSequence
name|text
parameter_list|,
name|Document
name|luceneDoc
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|void
name|build
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|Document
name|luceneDoc
parameter_list|,
name|CharSequence
name|text
parameter_list|)
function_decl|;
specifier|protected
name|void
name|doBuild
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|document
parameter_list|,
name|NodeId
name|nodeId
parameter_list|,
name|Document
name|luceneDoc
parameter_list|,
name|CharSequence
name|text
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|XPathException
block|{
if|if
condition|(
operator|!
name|expression
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|processText
argument_list|(
name|text
argument_list|,
name|luceneDoc
argument_list|)
expr_stmt|;
return|return;
block|}
name|compile
argument_list|(
name|broker
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isValid
condition|)
block|{
return|return;
block|}
specifier|final
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|NodeProxy
name|currentNode
init|=
operator|new
name|NodeProxy
argument_list|(
name|document
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
try|try
block|{
name|Sequence
name|result
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
name|currentNode
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|processResult
argument_list|(
name|result
argument_list|,
name|luceneDoc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
decl||
name|XPathException
name|e
parameter_list|)
block|{
name|isValid
operator|=
literal|false
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|compiled
operator|.
name|reset
argument_list|()
expr_stmt|;
name|compiled
operator|.
name|getContext
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|compile
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
if|if
condition|(
name|compiled
operator|==
literal|null
operator|&&
name|isValid
condition|)
block|{
name|expression
operator|.
name|ifPresent
argument_list|(
parameter_list|(
name|code
parameter_list|)
lambda|->
name|compiled
operator|=
name|compile
argument_list|(
name|broker
argument_list|,
name|code
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|CompiledXQuery
name|compile
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|code
parameter_list|)
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|xquery
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|context
argument_list|,
name|code
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
decl||
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to compile expression: "
operator|+
name|code
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|isValid
operator|=
literal|false
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|String
name|resolveURI
parameter_list|(
name|String
name|baseURI
parameter_list|,
name|String
name|location
parameter_list|)
block|{
try|try
block|{
specifier|final
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|uri
operator|.
name|isAbsolute
argument_list|()
operator|&&
name|baseURI
operator|!=
literal|null
operator|&&
name|baseURI
operator|.
name|startsWith
argument_list|(
name|CollectionConfigurationManager
operator|.
name|CONFIG_COLLECTION
argument_list|)
condition|)
block|{
name|String
name|base
init|=
name|baseURI
operator|.
name|substring
argument_list|(
name|CollectionConfigurationManager
operator|.
name|CONFIG_COLLECTION
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|lastSlash
init|=
name|base
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastSlash
operator|>
operator|-
literal|1
condition|)
block|{
name|base
operator|=
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|lastSlash
argument_list|)
expr_stmt|;
block|}
return|return
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
operator|+
name|base
operator|+
literal|'/'
operator|+
name|location
return|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|// ignore and return location
block|}
return|return
name|location
return|;
block|}
block|}
end_class

end_unit

