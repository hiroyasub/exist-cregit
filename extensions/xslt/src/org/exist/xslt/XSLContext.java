begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|interpreter
operator|.
name|ContextAtExist
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
name|xacml
operator|.
name|AccessContext
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
name|Configuration
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
name|Module
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
name|XQueryWatchDog
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|functions
operator|.
name|XSLModule
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSLContext
extends|extends
name|XQueryContext
implements|implements
name|ContextAtExist
block|{
specifier|private
name|XSLStylesheet
name|xslStylesheet
decl_stmt|;
specifier|public
name|XSLContext
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|AccessContext
operator|.
name|XSLT
argument_list|)
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|init
parameter_list|()
block|{
name|setWatchDog
argument_list|(
operator|new
name|XQueryWatchDog
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|loadDefaultNS
argument_list|()
expr_stmt|;
name|Configuration
name|config
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
comment|// Get map of built-in modules
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|Module
argument_list|>
argument_list|>
name|builtInModules
init|=
operator|(
name|Map
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_BUILT_IN_MODULES
argument_list|)
decl_stmt|;
if|if
condition|(
name|builtInModules
operator|!=
literal|null
condition|)
block|{
comment|// Iterate on all map entries
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|Module
argument_list|>
argument_list|>
name|entry
range|:
name|builtInModules
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// Get URI and class
name|String
name|namespaceURI
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Class
argument_list|<
name|Module
argument_list|>
name|moduleClass
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// first check if the module has already been loaded in the parent context
name|Module
name|module
init|=
name|getModule
argument_list|(
name|namespaceURI
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
comment|// Module does not exist yet, instantiate
name|instantiateModule
argument_list|(
name|namespaceURI
argument_list|,
name|moduleClass
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|(
name|getPrefixForURI
argument_list|(
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|==
literal|null
operator|)
operator|&&
operator|(
name|module
operator|.
name|getDefaultPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
comment|// make sure the namespaces of default modules are known,
comment|// even if they were imported in a parent context
try|try
block|{
name|declareNamespace
argument_list|(
name|module
operator|.
name|getDefaultPrefix
argument_list|()
argument_list|,
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Internal error while loading default modules: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//UNDERSTAND: what to do?
try|try
block|{
name|importModule
argument_list|(
name|XSLModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XSLModule
operator|.
name|PREFIX
argument_list|,
literal|"java:org.exist.xslt.functions.XSLModule"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|loadDefaultNS
parameter_list|()
block|{
name|super
operator|.
name|loadDefaultNS
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setXSLStylesheet
parameter_list|(
name|XSLStylesheet
name|xsl
parameter_list|)
block|{
name|this
operator|.
name|xslStylesheet
operator|=
name|xsl
expr_stmt|;
block|}
specifier|public
name|XSLStylesheet
name|getXSLStylesheet
parameter_list|()
block|{
return|return
name|xslStylesheet
return|;
block|}
specifier|public
name|Transformer
name|getTransformer
parameter_list|()
block|{
return|return
name|xslStylesheet
operator|.
name|getTransformer
argument_list|()
return|;
block|}
block|}
end_class

end_unit

