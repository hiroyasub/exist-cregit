begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|DebuggeeJoint
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
name|DocumentSet
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
name|QName
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
name|MemTreeBuilder
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
name|UpdateListener
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
name|FileUtils
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
name|value
operator|.
name|AnyURIValue
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
name|BinaryValue
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
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|XMLGregorianCalendar
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
name|Iterator
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
name|List
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|StringValue
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
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * Subclass of {@link org.exist.xquery.XQueryContext} for  * imported modules.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ModuleContext
extends|extends
name|XQueryContext
block|{
specifier|private
name|XQueryContext
name|parentContext
decl_stmt|;
specifier|private
name|String
name|modulePrefix
decl_stmt|;
specifier|private
name|String
name|moduleNamespace
decl_stmt|;
specifier|private
specifier|final
name|String
name|location
decl_stmt|;
comment|/** 	 * @param parentContext 	 */
specifier|public
name|ModuleContext
parameter_list|(
name|XQueryContext
name|parentContext
parameter_list|,
name|String
name|modulePrefix
parameter_list|,
name|String
name|moduleNamespace
parameter_list|,
name|String
name|location
parameter_list|)
block|{
name|super
argument_list|(
name|parentContext
operator|.
name|getAccessContext
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|moduleNamespace
operator|=
name|moduleNamespace
expr_stmt|;
name|this
operator|.
name|modulePrefix
operator|=
name|modulePrefix
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
expr_stmt|;
name|setParentContext
argument_list|(
name|parentContext
argument_list|)
expr_stmt|;
name|loadDefaults
argument_list|(
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|profiler
operator|=
operator|new
name|Profiler
argument_list|(
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|getLocation
parameter_list|()
block|{
return|return
name|location
return|;
block|}
name|String
name|getModuleNamespace
parameter_list|()
block|{
return|return
name|moduleNamespace
return|;
block|}
specifier|public
name|void
name|setModuleNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespaceURI
parameter_list|)
block|{
name|this
operator|.
name|modulePrefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|moduleNamespace
operator|=
name|namespaceURI
expr_stmt|;
block|}
name|void
name|setModulesChanged
parameter_list|()
block|{
name|parentContext
operator|.
name|setModulesChanged
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setParentContext
parameter_list|(
name|XQueryContext
name|parentContext
parameter_list|)
block|{
name|this
operator|.
name|parentContext
operator|=
name|parentContext
expr_stmt|;
comment|//XXX: raise error on null!
if|if
condition|(
name|parentContext
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|db
operator|=
name|parentContext
operator|.
name|db
expr_stmt|;
name|baseURI
operator|=
name|parentContext
operator|.
name|baseURI
expr_stmt|;
try|try
block|{
if|if
condition|(
name|location
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
operator|||
operator|(
name|location
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|<
literal|0
operator|&&
name|parentContext
operator|.
name|getModuleLoadPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
argument_list|)
operator|)
condition|)
block|{
comment|// use XmldbURI resolution - unfortunately these are not interpretable as URIs
comment|// because the scheme xmldb:exist: is not a valid URI scheme
specifier|final
name|XmldbURI
name|locationUri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|FileUtils
operator|.
name|dirname
argument_list|(
name|location
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|parentContext
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
condition|)
block|{
name|setModuleLoadPath
argument_list|(
name|locationUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
specifier|final
name|XmldbURI
name|parentLoadUri
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|parentContext
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|XmldbURI
name|moduleLoadUri
init|=
name|parentLoadUri
operator|.
name|resolveCollectionPath
argument_list|(
name|locationUri
argument_list|)
decl_stmt|;
name|setModuleLoadPath
argument_list|(
name|moduleLoadUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
name|setModuleLoadPath
argument_list|(
name|locationUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|String
name|dir
init|=
name|FileUtils
operator|.
name|dirname
argument_list|(
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|dir
operator|.
name|matches
argument_list|(
literal|"^[a-z]+:.*"
argument_list|)
condition|)
block|{
name|moduleLoadPath
operator|=
name|dir
expr_stmt|;
block|}
if|else if
condition|(
literal|"."
operator|.
name|equals
argument_list|(
name|parentContext
operator|.
name|moduleLoadPath
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
literal|"."
operator|.
name|equals
argument_list|(
name|dir
argument_list|)
condition|)
block|{
if|if
condition|(
name|dir
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|setModuleLoadPath
argument_list|(
literal|"."
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setModuleLoadPath
argument_list|(
literal|"./"
operator|+
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|dir
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|setModuleLoadPath
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setModuleLoadPath
argument_list|(
name|FileUtils
operator|.
name|addPaths
argument_list|(
name|parentContext
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|,
name|dir
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
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
block|}
specifier|public
name|void
name|setModule
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|Module
name|module
parameter_list|)
block|{
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
name|modules
operator|.
name|remove
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
comment|// unbind the module
block|}
else|else
block|{
name|modules
operator|.
name|put
argument_list|(
name|namespaceURI
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
name|setRootModule
argument_list|(
name|namespaceURI
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
name|XQueryContext
name|getParentContext
parameter_list|()
block|{
return|return
name|parentContext
return|;
block|}
specifier|public
name|boolean
name|hasParent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|XQueryContext
name|getRootContext
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getRootContext
argument_list|()
return|;
block|}
specifier|public
name|void
name|updateContext
parameter_list|(
name|XQueryContext
name|from
parameter_list|)
block|{
if|if
condition|(
name|from
operator|.
name|hasParent
argument_list|()
condition|)
block|{
comment|// TODO: shouldn't this call setParentContext ? - sokolov
name|this
operator|.
name|parentContext
operator|=
operator|(
operator|(
name|ModuleContext
operator|)
name|from
operator|)
operator|.
name|parentContext
expr_stmt|;
block|}
comment|//workaround for shared context issue, remove after fix
try|try
block|{
specifier|final
name|Variable
name|var
init|=
name|from
operator|.
name|getRootContext
argument_list|()
operator|.
name|resolveVariable
argument_list|(
name|ResponseModule
operator|.
name|PREFIX
operator|+
literal|":response"
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|!=
literal|null
condition|)
block|{
name|declareVariable
argument_list|(
name|ResponseModule
operator|.
name|PREFIX
operator|+
literal|":response"
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
comment|//ignore if not set
block|}
name|setModule
argument_list|(
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|from
operator|.
name|getRootContext
argument_list|()
operator|.
name|getModule
argument_list|(
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XQueryContext
name|copyContext
parameter_list|()
block|{
specifier|final
name|ModuleContext
name|ctx
init|=
operator|new
name|ModuleContext
argument_list|(
name|parentContext
argument_list|,
name|modulePrefix
argument_list|,
name|moduleNamespace
argument_list|,
name|location
argument_list|)
decl_stmt|;
name|copyFields
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
try|try
block|{
name|ctx
operator|.
name|declareNamespace
argument_list|(
name|modulePrefix
argument_list|,
name|moduleNamespace
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
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
return|return
name|ctx
return|;
block|}
specifier|public
name|void
name|addDynamicOption
parameter_list|(
name|String
name|qnameString
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|XPathException
block|{
name|parentContext
operator|.
name|addDynamicOption
argument_list|(
name|qnameString
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getStaticallyKnownDocuments() 	 */
specifier|public
name|DocumentSet
name|getStaticallyKnownDocuments
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|parentContext
operator|.
name|getStaticallyKnownDocuments
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getModule(java.lang.String) 	 */
specifier|public
name|Module
name|getModule
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
name|Module
name|module
init|=
name|super
operator|.
name|getModule
argument_list|(
name|namespaceURI
argument_list|)
decl_stmt|;
comment|// TODO: I don't think modules should be able to access their parent context's modules,
comment|// since that breaks lexical scoping.  However, it seems that some eXist modules rely on
comment|// this so let's leave it for now.  (pkaminsk2)
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
name|module
operator|=
name|parentContext
operator|.
name|getModule
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
return|return
name|module
return|;
block|}
specifier|protected
name|void
name|setRootModule
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|Module
name|module
parameter_list|)
block|{
name|allModules
operator|.
name|put
argument_list|(
name|namespaceURI
argument_list|,
name|module
argument_list|)
expr_stmt|;
name|parentContext
operator|.
name|setRootModule
argument_list|(
name|namespaceURI
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Module
argument_list|>
name|getRootModules
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getRootModules
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|Module
argument_list|>
name|getAllModules
parameter_list|()
block|{
return|return
name|allModules
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|Module
name|getRootModule
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
return|return
name|parentContext
operator|.
name|getRootModule
argument_list|(
name|namespaceURI
argument_list|)
return|;
block|}
comment|/**      * Overwritten method: the module will be loaded by the parent context, but      * we need to declare its namespace in the module context.       */
specifier|public
name|Module
name|loadBuiltInModule
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|moduleClass
parameter_list|)
block|{
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
name|module
operator|=
name|initBuiltInModule
argument_list|(
name|namespaceURI
argument_list|,
name|moduleClass
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|module
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|String
name|defaultPrefix
init|=
name|module
operator|.
name|getDefaultPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|defaultPrefix
argument_list|)
condition|)
block|{
name|declareNamespace
argument_list|(
name|defaultPrefix
argument_list|,
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error while loading builtin module class "
operator|+
name|moduleClass
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|module
return|;
block|}
specifier|public
name|void
name|updateModuleRefs
parameter_list|(
name|XQueryContext
name|rootContext
parameter_list|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|Module
argument_list|>
name|newModules
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Module
argument_list|>
argument_list|(
name|modules
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Module
name|module
range|:
name|modules
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|Module
name|updated
init|=
name|rootContext
operator|.
name|getModule
argument_list|(
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|updated
operator|==
literal|null
condition|)
block|{
name|updated
operator|=
name|module
expr_stmt|;
block|}
name|newModules
operator|.
name|put
argument_list|(
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|updated
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newModules
operator|.
name|put
argument_list|(
name|module
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
block|}
name|modules
operator|=
name|newModules
expr_stmt|;
block|}
annotation|@
name|Override
specifier|final
specifier|protected
name|XPathException
name|moduleLoadException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|String
name|moduleLocation
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|moduleLoadException
argument_list|(
name|message
argument_list|,
name|moduleLocation
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|final
specifier|protected
name|XPathException
name|moduleLoadException
parameter_list|(
specifier|final
name|String
name|message
parameter_list|,
specifier|final
name|String
name|moduleLocation
parameter_list|,
specifier|final
name|Exception
name|e
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//final String dependantModule = XmldbURI.create(moduleLoadPath).append(location).toString();
name|String
name|dependantModule
decl_stmt|;
try|try
block|{
name|dependantModule
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|getParentContext
argument_list|()
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|append
argument_list|(
name|location
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
name|dependantModule
operator|=
name|location
expr_stmt|;
block|}
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
name|message
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|moduleLocation
argument_list|)
argument_list|,
operator|new
name|StringValue
argument_list|(
name|dependantModule
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XQST0059
argument_list|,
name|message
argument_list|,
operator|new
name|ValueSequence
argument_list|(
operator|new
name|StringValue
argument_list|(
name|moduleLocation
argument_list|)
argument_list|,
operator|new
name|StringValue
argument_list|(
name|dependantModule
argument_list|)
argument_list|)
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getWatchDog() 	 */
specifier|public
name|XQueryWatchDog
name|getWatchDog
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getWatchDog
argument_list|()
return|;
block|}
specifier|public
name|Profiler
name|getProfiler
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getProfiler
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getCalendar() 	 */
specifier|public
name|XMLGregorianCalendar
name|getCalendar
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getCalendar
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getBaseURI() 	 */
specifier|public
name|AnyURIValue
name|getBaseURI
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|parentContext
operator|.
name|getBaseURI
argument_list|()
return|;
block|}
specifier|public
name|void
name|setBaseURI
parameter_list|(
name|AnyURIValue
name|uri
parameter_list|)
block|{
name|parentContext
operator|.
name|setBaseURI
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**      * Delegate to parent context      *       * @see org.exist.xquery.XQueryContext#setXQueryContextVar(String, Object)      */
specifier|public
name|void
name|setXQueryContextVar
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|XQvar
parameter_list|)
block|{
name|parentContext
operator|.
name|setXQueryContextVar
argument_list|(
name|name
argument_list|,
name|XQvar
argument_list|)
expr_stmt|;
block|}
comment|/**      * Delegate to parent context      *       * @see org.exist.xquery.XQueryContext#getXQueryContextVar(String)      */
specifier|public
name|Object
name|getXQueryContextVar
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|parentContext
operator|.
name|getXQueryContextVar
argument_list|(
name|name
argument_list|)
operator|)
return|;
block|}
comment|//    /* (non-Javadoc)
comment|//     * @see org.exist.xquery.XQueryContext#getBroker()
comment|//     */
comment|//    public DBBroker getBroker() {
comment|//        return parentContext.getBroker();
comment|//    }
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#getDocumentBuilder() 	 */
specifier|public
name|MemTreeBuilder
name|getDocumentBuilder
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getDocumentBuilder
argument_list|()
return|;
block|}
specifier|public
name|MemTreeBuilder
name|getDocumentBuilder
parameter_list|(
name|boolean
name|explicitCreation
parameter_list|)
block|{
return|return
name|parentContext
operator|.
name|getDocumentBuilder
argument_list|(
name|explicitCreation
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#pushDocumentContext() 	 */
specifier|public
name|void
name|pushDocumentContext
parameter_list|()
block|{
name|parentContext
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|LocalVariable
name|markLocalVariables
parameter_list|(
name|boolean
name|newContext
parameter_list|)
block|{
return|return
name|parentContext
operator|.
name|markLocalVariables
argument_list|(
name|newContext
argument_list|)
return|;
block|}
specifier|public
name|void
name|popLocalVariables
parameter_list|(
name|LocalVariable
name|var
parameter_list|)
block|{
name|parentContext
operator|.
name|popLocalVariables
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|popLocalVariables
parameter_list|(
name|LocalVariable
name|var
parameter_list|,
name|Sequence
name|resultSequence
parameter_list|)
block|{
name|parentContext
operator|.
name|popLocalVariables
argument_list|(
name|var
argument_list|,
name|resultSequence
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LocalVariable
name|declareVariableBinding
parameter_list|(
name|LocalVariable
name|var
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|parentContext
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
return|;
block|}
specifier|protected
name|Variable
name|resolveLocalVariable
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|parentContext
operator|.
name|resolveLocalVariable
argument_list|(
name|qname
argument_list|)
return|;
block|}
comment|/**      * Try to resolve a variable.      *      * @param qname the qualified name of the variable      * @return the declared Variable object      * @throws XPathException if the variable is unknown      */
specifier|public
name|Variable
name|resolveVariable
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
name|Variable
name|var
decl_stmt|;
comment|// check if the variable is declared local
name|var
operator|=
name|resolveLocalVariable
argument_list|(
name|qname
argument_list|)
expr_stmt|;
comment|// check if the variable is declared in a module
if|if
condition|(
name|var
operator|==
literal|null
condition|)
block|{
name|Module
name|module
decl_stmt|;
if|if
condition|(
name|moduleNamespace
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
name|module
operator|=
name|getRootModule
argument_list|(
name|moduleNamespace
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|module
operator|=
name|getModule
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|module
operator|!=
literal|null
condition|)
block|{
name|var
operator|=
name|module
operator|.
name|resolveVariable
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check if the variable is declared global
if|if
condition|(
name|var
operator|==
literal|null
condition|)
block|{
name|var
operator|=
name|globalVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
comment|//if (var == null)
comment|//	throw new XPathException("variable $" + qname + " is not bound");
return|return
name|var
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|getVariables
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getVariables
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|getLocalVariables
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getLocalVariables
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|Variable
argument_list|>
name|getLocalStack
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getLocalStack
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|getGlobalVariables
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getGlobalVariables
argument_list|()
return|;
block|}
specifier|public
name|void
name|restoreStack
parameter_list|(
name|List
argument_list|<
name|Variable
argument_list|>
name|stack
parameter_list|)
throws|throws
name|XPathException
block|{
name|parentContext
operator|.
name|restoreStack
argument_list|(
name|stack
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getCurrentStackSize
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getCurrentStackSize
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.XQueryContext#popDocumentContext() 	 */
specifier|public
name|void
name|popDocumentContext
parameter_list|()
block|{
name|parentContext
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
comment|/**      * First checks the parent context for in-scope namespaces,      * then the module's static context.      *      * @param prefix the prefix to look up      * @return the namespace currently mapped to that prefix      */
specifier|public
name|String
name|getURIForPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|String
name|uri
init|=
name|getInScopeNamespace
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
return|return
name|uri
return|;
block|}
comment|//TODO : test NS inheritance
name|uri
operator|=
name|getInheritedNamespace
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|uri
operator|!=
literal|null
condition|)
block|{
return|return
name|uri
return|;
block|}
comment|// Check global declarations
return|return
name|staticNamespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
return|;
block|}
comment|/**      * First checks the parent context for in-scope namespaces,      * then the module's static context.      *      * @param uri the URI to look up      * @return a prefix for the URI      */
specifier|public
name|String
name|getPrefixForURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|String
name|prefix
init|=
name|getInScopePrefix
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
return|return
name|prefix
return|;
block|}
comment|//TODO : test the NS inheritance
name|prefix
operator|=
name|getInheritedPrefix
argument_list|(
name|uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
return|return
name|prefix
return|;
block|}
return|return
name|staticPrefixes
operator|.
name|get
argument_list|(
name|uri
argument_list|)
return|;
block|}
specifier|public
name|String
name|getInScopeNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|parentContext
operator|.
name|getInScopeNamespace
argument_list|(
name|prefix
argument_list|)
return|;
block|}
specifier|public
name|String
name|getInScopePrefix
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
return|return
name|parentContext
operator|.
name|getInScopePrefix
argument_list|(
name|uri
argument_list|)
return|;
block|}
specifier|public
name|String
name|getInheritedNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
return|return
name|parentContext
operator|.
name|getInheritedNamespace
argument_list|(
name|prefix
argument_list|)
return|;
block|}
specifier|public
name|String
name|getInheritedPrefix
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
return|return
name|parentContext
operator|.
name|getInheritedPrefix
argument_list|(
name|uri
argument_list|)
return|;
block|}
specifier|public
name|void
name|declareInScopeNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
name|parentContext
operator|.
name|declareInScopeNamespace
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|pushInScopeNamespaces
parameter_list|(
name|boolean
name|inherit
parameter_list|)
block|{
name|parentContext
operator|.
name|pushInScopeNamespaces
argument_list|(
name|inherit
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|pushInScopeNamespaces
parameter_list|()
block|{
name|parentContext
operator|.
name|pushInScopeNamespaces
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|popInScopeNamespaces
parameter_list|()
block|{
name|parentContext
operator|.
name|popInScopeNamespaces
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|registerUpdateListener
parameter_list|(
name|UpdateListener
name|listener
parameter_list|)
block|{
name|parentContext
operator|.
name|registerUpdateListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|clearUpdateListeners
parameter_list|()
block|{
comment|// will be cleared by the parent context
block|}
specifier|public
name|DebuggeeJoint
name|getDebuggeeJoint
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|getDebuggeeJoint
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isDebugMode
parameter_list|()
block|{
return|return
name|parentContext
operator|.
name|isDebugMode
argument_list|()
return|;
block|}
specifier|public
name|void
name|expressionStart
parameter_list|(
name|Expression
name|expr
parameter_list|)
throws|throws
name|TerminatedException
block|{
name|parentContext
operator|.
name|expressionStart
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|expressionEnd
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|parentContext
operator|.
name|expressionEnd
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stackEnter
parameter_list|(
name|Expression
name|expr
parameter_list|)
throws|throws
name|TerminatedException
block|{
name|parentContext
operator|.
name|stackEnter
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stackLeave
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|parentContext
operator|.
name|stackLeave
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerBinaryValueInstance
parameter_list|(
name|BinaryValue
name|binaryValue
parameter_list|)
block|{
name|parentContext
operator|.
name|registerBinaryValueInstance
argument_list|(
name|binaryValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|saveState
parameter_list|()
block|{
name|super
operator|.
name|saveState
argument_list|()
expr_stmt|;
name|parentContext
operator|.
name|saveState
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

