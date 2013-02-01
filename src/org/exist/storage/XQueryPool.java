begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

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
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsAttribute
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
name|Permission
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
name|util
operator|.
name|hashtable
operator|.
name|Object2ObjectHashMap
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
name|*
import|;
end_import

begin_comment
comment|/**  * Global pool for pre-compiled XQuery expressions. Expressions are stored and  * retrieved from the pool by comparing the {@link org.exist.source.Source}  * objects from which they were created. For each XQuery, a maximum of  * {@link #MAX_STACK_SIZE} compiled expressions are kept in the pool. An XQuery  * expression will be removed from the pool if it has not been used for a  * pre-defined timeout. These settings can be configured in conf.xml.  *   * @author wolf  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"query-pool"
argument_list|)
specifier|public
class|class
name|XQueryPool
extends|extends
name|Object2ObjectHashMap
block|{
specifier|public
specifier|final
specifier|static
name|int
name|MAX_POOL_SIZE
init|=
literal|128
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MAX_STACK_SIZE
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|TIMEOUT
init|=
literal|120000L
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|long
name|TIMEOUT_CHECK_INTERVAL
init|=
literal|30000L
decl_stmt|;
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
name|XQueryPool
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|long
name|lastTimeOutCheck
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"size"
argument_list|)
specifier|private
name|int
name|maxPoolSize
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"max-stack-size"
argument_list|)
specifier|private
name|int
name|maxStackSize
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"timeout"
argument_list|)
specifier|private
name|long
name|timeout
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"timeout-check-interval"
argument_list|)
specifier|private
name|long
name|timeoutCheckInterval
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"query-pool"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MAX_STACK_SIZE_ATTRIBUTE
init|=
literal|"max-stack-size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|POOL_SIZE_ATTTRIBUTE
init|=
literal|"size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TIMEOUT_ATTRIBUTE
init|=
literal|"timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TIMEOUT_CHECK_INTERVAL_ATTRIBUTE
init|=
literal|"timeout-check-interval"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_MAX_STACK_SIZE
init|=
literal|"db-connection.query-pool.max-stack-size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_POOL_SIZE
init|=
literal|"db-connection.query-pool.size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_TIMEOUT
init|=
literal|"db-connection.query-pool.timeout"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_TIMEOUT_CHECK_INTERVAL
init|=
literal|"db-connection.query-pool.timeout-check-interval"
decl_stmt|;
comment|/** 	 * @param conf 	 */
specifier|public
name|XQueryPool
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
literal|27
argument_list|)
expr_stmt|;
name|lastTimeOutCheck
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|Integer
name|maxStSz
init|=
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_MAX_STACK_SIZE
argument_list|)
decl_stmt|;
name|Integer
name|maxPoolSz
init|=
operator|(
name|Integer
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_POOL_SIZE
argument_list|)
decl_stmt|;
name|Long
name|t
init|=
operator|(
name|Long
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TIMEOUT
argument_list|)
decl_stmt|;
name|Long
name|tci
init|=
operator|(
name|Long
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_TIMEOUT_CHECK_INTERVAL
argument_list|)
decl_stmt|;
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|maxPoolSz
operator|!=
literal|null
condition|)
name|maxPoolSize
operator|=
name|maxPoolSz
operator|.
name|intValue
argument_list|()
expr_stmt|;
else|else
name|maxPoolSize
operator|=
name|MAX_POOL_SIZE
expr_stmt|;
if|if
condition|(
name|maxStSz
operator|!=
literal|null
condition|)
name|maxStackSize
operator|=
name|maxStSz
operator|.
name|intValue
argument_list|()
expr_stmt|;
else|else
name|maxStackSize
operator|=
name|MAX_STACK_SIZE
expr_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
name|timeout
operator|=
name|t
operator|.
name|longValue
argument_list|()
expr_stmt|;
else|else
name|timeout
operator|=
name|TIMEOUT
expr_stmt|;
comment|// TODO : check that it is inferior to t
if|if
condition|(
name|tci
operator|!=
literal|null
condition|)
name|timeoutCheckInterval
operator|=
name|tci
operator|.
name|longValue
argument_list|()
expr_stmt|;
else|else
name|timeoutCheckInterval
operator|=
name|TIMEOUT_CHECK_INTERVAL
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"QueryPool: "
operator|+
literal|"size = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxPoolSize
argument_list|)
operator|+
literal|"; "
operator|+
literal|"maxStackSize = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxStackSize
argument_list|)
operator|+
literal|"; "
operator|+
literal|"timeout = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|timeout
argument_list|)
operator|+
literal|"; "
operator|+
literal|"timeoutCheckInterval = "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|timeoutCheckInterval
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|returnCompiledXQuery
parameter_list|(
name|Source
name|source
parameter_list|,
name|CompiledXQuery
name|xquery
parameter_list|)
block|{
comment|// returnModules(xquery.getContext(), null);
name|returnObject
argument_list|(
name|source
argument_list|,
name|xquery
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|returnModules
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|ExternalModule
name|self
parameter_list|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Module
argument_list|>
name|it
init|=
name|context
operator|.
name|getRootModules
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Module
name|module
init|=
operator|(
name|Module
operator|)
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|module
operator|!=
name|self
operator|&&
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|ExternalModule
name|extModule
init|=
operator|(
name|ExternalModule
operator|)
name|module
decl_stmt|;
comment|// ((ModuleContext)extModule.getContext()).setParentContext(null);
comment|// Don't return recursively, since all modules are listed in the
comment|// top-level context
name|returnObject
argument_list|(
name|extModule
operator|.
name|getSource
argument_list|()
argument_list|,
name|extModule
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|returnObject
parameter_list|(
name|Source
name|source
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|>=
name|maxPoolSize
condition|)
name|timeoutCheck
argument_list|()
expr_stmt|;
if|if
condition|(
name|size
argument_list|()
operator|<
name|maxPoolSize
condition|)
block|{
name|Stack
name|stack
init|=
operator|(
name|Stack
operator|)
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|stack
operator|==
literal|null
condition|)
block|{
name|stack
operator|=
operator|new
name|Stack
argument_list|()
expr_stmt|;
name|source
operator|.
name|setCacheTimestamp
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|source
argument_list|,
name|stack
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|<
name|maxStackSize
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|stack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|stack
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
name|o
condition|)
comment|// query already in pool. may happen for modules.
comment|// don't add it a second time.
return|return;
block|}
name|stack
operator|.
name|push
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|synchronized
name|Object
name|borrowObject
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Source
name|source
parameter_list|)
block|{
name|int
name|idx
init|=
name|getIndex
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Source
name|key
init|=
operator|(
name|Source
operator|)
name|keys
index|[
name|idx
index|]
decl_stmt|;
name|int
name|validity
init|=
name|key
operator|.
name|isValid
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|validity
operator|==
name|Source
operator|.
name|UNKNOWN
condition|)
name|validity
operator|=
name|key
operator|.
name|isValid
argument_list|(
name|source
argument_list|)
expr_stmt|;
if|if
condition|(
name|validity
operator|==
name|Source
operator|.
name|INVALID
operator|||
name|validity
operator|==
name|Source
operator|.
name|UNKNOWN
condition|)
block|{
name|keys
index|[
name|idx
index|]
operator|=
name|REMOVED
expr_stmt|;
name|values
index|[
name|idx
index|]
operator|=
literal|null
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
name|source
operator|.
name|getKey
argument_list|()
operator|+
literal|" is invalid"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Stack
name|stack
init|=
operator|(
name|Stack
operator|)
name|values
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
name|stack
operator|==
literal|null
operator|||
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
comment|// now check if the compiled expression is valid
comment|// it might become invalid if an imported module has changed.
name|CompiledXQuery
name|query
init|=
operator|(
name|CompiledXQuery
operator|)
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
init|=
name|query
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|//context.setBroker(broker);
if|if
condition|(
operator|!
name|query
operator|.
name|isValid
argument_list|()
condition|)
block|{
comment|// the compiled query is no longer valid: one of the imported
comment|// modules may have changed
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
return|return
name|query
return|;
block|}
specifier|public
specifier|synchronized
name|CompiledXQuery
name|borrowCompiledXQuery
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Source
name|source
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|CompiledXQuery
name|query
init|=
operator|(
name|CompiledXQuery
operator|)
name|borrowObject
argument_list|(
name|broker
argument_list|,
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|//check execution permission
name|source
operator|.
name|validate
argument_list|(
name|broker
operator|.
name|getSubject
argument_list|()
argument_list|,
name|Permission
operator|.
name|EXECUTE
argument_list|)
expr_stmt|;
comment|// now check if the compiled expression is valid
comment|// it might become invalid if an imported module has changed.
name|XQueryContext
name|context
init|=
name|query
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|//context.setBroker(broker);
return|return
name|query
return|;
comment|// if (!borrowModules(broker, context)) {
comment|// // the compiled query is no longer valid: one of the imported
comment|// // modules may have changed
comment|// remove(source);
comment|// return null;
comment|// } else {
comment|// if (query instanceof PathExpr) try {
comment|// // This is necessary because eXist performs whole-expression
comment|// analysis, so a function
comment|// // can only be analyzed as part of the expression it's called from.
comment|// It might be better
comment|// // to make module functions more stand-alone, so they only need to be
comment|// analyzed
comment|// // once.
comment|// context.analyzeAndOptimizeIfModulesChanged((PathExpr) query);
comment|// } catch (XPathException e) {
comment|// remove(source);
comment|// return null;
comment|// }
comment|// return query;
comment|// }
block|}
specifier|private
specifier|synchronized
name|boolean
name|borrowModules
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Module
argument_list|>
name|borrowedModules
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Module
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Module
argument_list|>
name|it
init|=
name|context
operator|.
name|getAllModules
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Module
name|module
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
operator|||
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|ExternalModule
name|extModule
init|=
operator|(
name|ExternalModule
operator|)
name|module
decl_stmt|;
name|ExternalModule
name|borrowedModule
init|=
name|borrowModule
argument_list|(
name|broker
argument_list|,
name|extModule
operator|.
name|getSource
argument_list|()
argument_list|,
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|borrowedModule
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Module
argument_list|>
name|it2
init|=
name|borrowedModules
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ExternalModule
name|moduleToReturn
init|=
operator|(
name|ExternalModule
operator|)
name|it2
operator|.
name|next
argument_list|()
decl_stmt|;
name|returnObject
argument_list|(
name|moduleToReturn
operator|.
name|getSource
argument_list|()
argument_list|,
name|moduleToReturn
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
name|borrowedModules
operator|.
name|put
argument_list|(
name|extModule
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|borrowedModule
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Iterator
name|it
init|=
name|borrowedModules
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
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
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|moduleNamespace
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|ExternalModule
name|module
init|=
operator|(
name|ExternalModule
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Modules that don't appear in the root context will be set in
comment|// context.allModules by
comment|// calling setModule below on the module that does import them
comment|// directly.
if|if
condition|(
name|context
operator|.
name|getModule
argument_list|(
name|moduleNamespace
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|setModule
argument_list|(
name|moduleNamespace
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|importedModuleNamespaceUris
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Module
argument_list|>
name|it2
init|=
name|module
operator|.
name|getContext
argument_list|()
operator|.
name|getModules
argument_list|()
init|;
name|it2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Module
name|nestedModule
init|=
name|it2
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|nestedModule
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|importedModuleNamespaceUris
operator|.
name|add
argument_list|(
name|nestedModule
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|it2
init|=
name|importedModuleNamespaceUris
operator|.
name|iterator
argument_list|()
init|;
name|it2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|namespaceUri
init|=
operator|(
name|String
operator|)
name|it2
operator|.
name|next
argument_list|()
decl_stmt|;
name|Module
name|imported
init|=
operator|(
name|Module
operator|)
name|borrowedModules
operator|.
name|get
argument_list|(
name|namespaceUri
argument_list|)
decl_stmt|;
name|module
operator|.
name|getContext
argument_list|()
operator|.
name|setModule
argument_list|(
name|namespaceUri
argument_list|,
name|imported
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
specifier|synchronized
name|ExternalModule
name|borrowModule
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Source
name|source
parameter_list|,
name|XQueryContext
name|rootContext
parameter_list|)
block|{
name|ExternalModule
name|module
init|=
operator|(
name|ExternalModule
operator|)
name|borrowObject
argument_list|(
name|broker
argument_list|,
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|XQueryContext
name|context
init|=
name|module
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|//context.setBroker(broker);
if|if
condition|(
operator|!
name|module
operator|.
name|moduleIsValid
argument_list|(
name|broker
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Module with URI "
operator|+
name|module
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|" has changed and needs to be reloaded"
argument_list|)
expr_stmt|;
name|remove
argument_list|(
name|source
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
comment|// check all modules imported by the borrowed module and update them
if|if
condition|(
operator|!
name|borrowModules
argument_list|(
name|broker
argument_list|,
name|context
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
operator|(
operator|(
name|ModuleContext
operator|)
name|module
operator|.
name|getContext
argument_list|()
operator|)
operator|.
name|updateModuleRefs
argument_list|(
name|rootContext
argument_list|)
expr_stmt|;
try|try
block|{
name|module
operator|.
name|analyzeGlobalVars
argument_list|()
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|module
return|;
block|}
block|}
specifier|public
specifier|synchronized
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|i
init|=
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
name|Source
name|next
init|=
operator|(
name|Source
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|remove
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|timeoutCheck
parameter_list|()
block|{
specifier|final
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|timeoutCheckInterval
operator|<
literal|0L
condition|)
return|return;
if|if
condition|(
name|currentTime
operator|-
name|lastTimeOutCheck
operator|<
name|timeoutCheckInterval
condition|)
return|return;
for|for
control|(
name|Iterator
name|i
init|=
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
name|Source
name|next
init|=
operator|(
name|Source
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentTime
operator|-
name|next
operator|.
name|getCacheTimestamp
argument_list|()
operator|>
name|timeout
condition|)
block|{
name|remove
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

