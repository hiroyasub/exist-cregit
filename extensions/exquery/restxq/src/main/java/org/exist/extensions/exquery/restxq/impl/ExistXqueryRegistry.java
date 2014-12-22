begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2013, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
package|;
end_package

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
name|ArrayList
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|dom
operator|.
name|persistent
operator|.
name|BinaryDocument
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
name|ErrorCodes
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
name|value
operator|.
name|Item
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
name|exquery
operator|.
name|ExQueryException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqServiceRegistry
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|ExistXqueryRegistry
block|{
comment|//singleton
specifier|private
specifier|final
specifier|static
name|ExistXqueryRegistry
name|instance
init|=
operator|new
name|ExistXqueryRegistry
argument_list|()
decl_stmt|;
specifier|private
name|ExistXqueryRegistry
parameter_list|()
block|{
block|}
specifier|public
specifier|final
specifier|static
name|ExistXqueryRegistry
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ExistXqueryRegistry
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Key is XQuery Module URI      * Value is set of XQuery Module URIs on which the Module indicated by the Key depends on      */
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|dependenciesTree
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Key is the missing Module URI      * Value is the Set of XQuery Module URIs that require the missing Module indicated by the Key      */
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|missingDependencies
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * The list of XQuerys that could not be compiled      * for reasons other than missing dependencies      */
specifier|final
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|invalidQueries
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|isXquery
parameter_list|(
specifier|final
name|DocumentImpl
name|document
parameter_list|)
block|{
return|return
name|document
operator|instanceof
name|BinaryDocument
operator|&&
name|document
operator|.
name|getMetadata
argument_list|()
operator|.
name|getMimeType
argument_list|()
operator|.
name|equals
argument_list|(
name|XQueryCompiler
operator|.
name|XQUERY_MIME_TYPE
argument_list|)
return|;
block|}
specifier|public
name|void
name|registerServices
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|services
parameter_list|)
block|{
name|getRegistry
argument_list|(
name|broker
argument_list|)
operator|.
name|register
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deregisterServices
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|XmldbURI
name|xqueryLocation
parameter_list|)
block|{
name|getRegistry
argument_list|(
name|broker
argument_list|)
operator|.
name|deregister
argument_list|(
name|xqueryLocation
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
comment|//find and remove services from modules that depend on this one
for|for
control|(
specifier|final
name|String
name|dependant
range|:
name|getDependants
argument_list|(
name|xqueryLocation
argument_list|)
control|)
block|{
try|try
block|{
comment|//TODO This null check is a temporary workaround
comment|//as a NPE in the URI class was reported by Wolf
comment|//where dependant was null. I can only imagine
comment|//that another thread interrupted and removed it
comment|//from the hashmap that it comes from.
comment|//its quite possible the use of synchronized around
comment|//the various maps in this class is not sufficient in scope
comment|//and we should move to some locks and operating over closures
comment|//on the maps.
if|if
condition|(
name|dependant
operator|!=
literal|null
condition|)
block|{
name|getRegistry
argument_list|(
name|broker
argument_list|)
operator|.
name|deregister
argument_list|(
operator|new
name|URI
argument_list|(
name|dependant
argument_list|)
argument_list|)
expr_stmt|;
comment|//record the now missing dependency
name|recordMissingDependency
argument_list|(
name|xqueryLocation
operator|.
name|toString
argument_list|()
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|dependant
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|urise
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|urise
operator|.
name|getMessage
argument_list|()
argument_list|,
name|urise
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*          * update the missingDependencies??          * Probaly not needed as this will be done in find services          */
block|}
specifier|public
name|void
name|deregisterService
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|RestXqService
name|service
parameter_list|)
block|{
name|getRegistry
argument_list|(
name|broker
argument_list|)
operator|.
name|deregister
argument_list|(
name|service
argument_list|)
expr_stmt|;
comment|//TODO below is not needed as we are not removing the module just a single service
comment|//find and remove services from modules that depend on this one
comment|/*for(final String dependant : getDependants(xqueryLocation)) {             try {                                  //TODO This null check is a temporary workaround                 //as a NPE in the URI class was reported by Wolf                 //where dependant was null. I can only imagine                 //that another thread interrupted and removed it                 //from the hashmap that it comes from.                 //its quite possible the use of synchronized around                 //the various maps in this class is not sufficient in scope                 //and we should move to some locks and operating over closures                 //on the maps.                 if(dependant != null) {                     getRegistry(broker).deregister(new URI(dependant));                      //record the now missing dependency                     recordMissingDependency(xqueryLocation.toString(), XmldbURI.create(dependant));                 }             } catch(final URISyntaxException urise) {                 LOG.error(urise.getMessage(), urise);             }         }*/
comment|/*          * update the missingDependencies??          * Probaly not needed as this will be done in find services          */
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|getDependants
parameter_list|(
specifier|final
name|XmldbURI
name|xqueryLocation
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|dependants
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|//make a copy of the dependenciesTree into depTree
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|depTree
decl_stmt|;
synchronized|synchronized
init|(
name|dependenciesTree
init|)
block|{
name|depTree
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|(
name|dependenciesTree
argument_list|)
expr_stmt|;
block|}
comment|//find all modules that have a dependency on this one
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|depTreeEntry
range|:
name|depTree
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|String
name|dependency
range|:
name|depTreeEntry
operator|.
name|getValue
argument_list|()
control|)
block|{
if|if
condition|(
name|dependency
operator|.
name|equals
argument_list|(
name|xqueryLocation
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|dependants
operator|.
name|add
argument_list|(
name|depTreeEntry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
block|}
return|return
name|dependants
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|RestXqService
argument_list|>
name|registered
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|getRegistry
argument_list|(
name|broker
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|RestXqService
argument_list|>
name|findServices
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|ExQueryException
block|{
try|try
block|{
specifier|final
name|CompiledXQuery
name|compiled
init|=
name|XQueryCompiler
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
name|document
argument_list|)
decl_stmt|;
comment|/*              * examine the compiled query, record all modules and modules of modules.              * Keep a dependencies list so that we can act on it if a module is deleted.              */
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|queryDependenciesTree
init|=
name|XQueryInspector
operator|.
name|getDependencies
argument_list|(
name|compiled
argument_list|)
decl_stmt|;
name|recordQueryDependenciesTree
argument_list|(
name|queryDependenciesTree
argument_list|)
expr_stmt|;
comment|/*              * A compiled query may be a missing dependency for another query              * so reexamine queries with missing dependencies              */
name|reexamineModulesWithResolvedDependencies
argument_list|(
name|broker
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|/*              * remove any potentially re-compiled query from the              * invalid queries list              */
name|removeInvalidQuery
argument_list|(
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|XQueryInspector
operator|.
name|findServices
argument_list|(
name|compiled
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|RestXqServiceCompilationException
name|rxsce
parameter_list|)
block|{
comment|//if there was a missing dependency then record it
specifier|final
name|MissingModuleHint
name|missingModuleHint
init|=
name|extractMissingModuleHint
argument_list|(
name|rxsce
argument_list|)
decl_stmt|;
if|if
condition|(
name|missingModuleHint
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|missingModuleHint
operator|.
name|dependantModule
operator|==
literal|null
condition|)
block|{
name|recordMissingDependency
argument_list|(
name|missingModuleHint
operator|.
name|moduleHint
argument_list|,
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//avoids wrong missing dependency dependant being recorded for a complex module tree
try|try
block|{
name|recordMissingDependency
argument_list|(
name|missingModuleHint
operator|.
name|moduleHint
argument_list|,
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|missingModuleHint
operator|.
name|dependantModule
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|use
parameter_list|)
block|{
name|recordInvalidQuery
argument_list|(
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"XQuery '"
operator|+
name|document
operator|.
name|getURI
argument_list|()
operator|+
literal|"' could not be compiled! "
operator|+
name|rxsce
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|recordInvalidQuery
argument_list|(
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"XQuery '"
operator|+
name|document
operator|.
name|getURI
argument_list|()
operator|+
literal|"' could not be compiled! "
operator|+
name|rxsce
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*              * This may be the recompilation of a query              * so we should unregister any of its missing              * services. Luckily this is taken care of in              * the before{EVENT} trigger functions              */
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|RestXqService
argument_list|>
argument_list|()
return|;
block|}
comment|/**      * Gets the modules that have a missing dependency      * on the module indicated by compiledModuleURI      * and attempts to re-compile them and register their      * services      */
specifier|private
name|void
name|reexamineModulesWithResolvedDependencies
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|String
name|compiledModuleUri
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|dependants
decl_stmt|;
synchronized|synchronized
init|(
name|missingDependencies
init|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|deps
init|=
name|missingDependencies
operator|.
name|get
argument_list|(
name|compiledModuleUri
argument_list|)
decl_stmt|;
if|if
condition|(
name|deps
operator|!=
literal|null
condition|)
block|{
name|dependants
operator|=
operator|new
name|HashSet
argument_list|(
name|deps
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dependants
operator|=
operator|new
name|HashSet
argument_list|()
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|String
name|dependant
range|:
name|dependants
control|)
block|{
try|try
block|{
specifier|final
name|DocumentImpl
name|dependantModule
init|=
name|broker
operator|.
name|getResource
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|dependant
argument_list|)
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
decl_stmt|;
comment|/**                  * This null check is needed, as a dependency module may have been renamed,                  * and so is no longer accessible under its old URI.                  *                  * However if its dependant module (compiledModuleUri) compiles                  * (which it must have for this function to be invoked)                  * then we can assume that the dependant module references the new                  * module dependency (in the case of a module move/rename)                  * or the dependency has been removed                  */
if|if
condition|(
name|dependantModule
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Missing dependency '"
operator|+
name|compiledModuleUri
operator|+
literal|"' has been added to the database, re-examining '"
operator|+
name|dependant
operator|+
literal|"'..."
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|services
init|=
name|findServices
argument_list|(
name|broker
argument_list|,
name|dependantModule
argument_list|)
decl_stmt|;
name|registerServices
argument_list|(
name|broker
argument_list|,
name|services
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Dependant '"
operator|+
name|compiledModuleUri
operator|+
literal|"' has been resolved. Dependency on: "
operator|+
name|dependant
operator|+
literal|"was removed"
argument_list|)
expr_stmt|;
comment|//we need to remove dependant from the dependenciesTree of dependant
name|removeDependency
argument_list|(
name|dependant
argument_list|,
name|compiledModuleUri
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ExQueryException
name|eqe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|eqe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|eqe
argument_list|)
expr_stmt|;
block|}
comment|//remove the resolve dependecies from the missing dependencies
name|removeMissingDependency
argument_list|(
name|compiledModuleUri
argument_list|,
name|dependant
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeMissingDependency
parameter_list|(
specifier|final
name|String
name|dependency
parameter_list|,
specifier|final
name|String
name|dependant
parameter_list|)
block|{
synchronized|synchronized
init|(
name|missingDependencies
init|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|missingDependants
init|=
name|missingDependencies
operator|.
name|get
argument_list|(
name|dependency
argument_list|)
decl_stmt|;
name|missingDependants
operator|.
name|remove
argument_list|(
name|dependant
argument_list|)
expr_stmt|;
if|if
condition|(
name|missingDependants
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|missingDependencies
operator|.
name|remove
argument_list|(
name|dependency
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|recordQueryDependenciesTree
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|queryDependenciesTree
parameter_list|)
block|{
synchronized|synchronized
init|(
name|dependenciesTree
init|)
block|{
comment|//Its not a merge its an ovewrite!
name|dependenciesTree
operator|.
name|putAll
argument_list|(
name|queryDependenciesTree
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|removeInvalidQuery
parameter_list|(
specifier|final
name|XmldbURI
name|xqueryUri
parameter_list|)
block|{
synchronized|synchronized
init|(
name|invalidQueries
init|)
block|{
name|invalidQueries
operator|.
name|remove
argument_list|(
name|xqueryUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|recordInvalidQuery
parameter_list|(
specifier|final
name|XmldbURI
name|xqueryUri
parameter_list|)
block|{
synchronized|synchronized
init|(
name|invalidQueries
init|)
block|{
name|invalidQueries
operator|.
name|add
argument_list|(
name|xqueryUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|MissingModuleHint
block|{
specifier|public
name|String
name|moduleHint
init|=
literal|null
decl_stmt|;
specifier|public
name|String
name|dependantModule
init|=
literal|null
decl_stmt|;
block|}
specifier|private
name|MissingModuleHint
name|extractMissingModuleHint
parameter_list|(
specifier|final
name|RestXqServiceCompilationException
name|rxsce
parameter_list|)
block|{
name|MissingModuleHint
name|missingModuleHint
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|rxsce
operator|.
name|getCause
argument_list|()
operator|instanceof
name|XPathException
condition|)
block|{
specifier|final
name|XPathException
name|xpe
init|=
operator|(
name|XPathException
operator|)
name|rxsce
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|xpe
operator|.
name|getErrorCode
argument_list|()
operator|==
name|ErrorCodes
operator|.
name|XQST0059
condition|)
block|{
specifier|final
name|Sequence
name|errorVals
init|=
name|xpe
operator|.
name|getErrorVal
argument_list|()
decl_stmt|;
if|if
condition|(
name|errorVals
operator|!=
literal|null
operator|&&
name|errorVals
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|Item
name|errorVal1
init|=
name|errorVals
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|errorVal1
operator|instanceof
name|StringValue
condition|)
block|{
if|if
condition|(
name|missingModuleHint
operator|==
literal|null
condition|)
block|{
name|missingModuleHint
operator|=
operator|new
name|MissingModuleHint
argument_list|()
expr_stmt|;
block|}
name|missingModuleHint
operator|.
name|moduleHint
operator|=
operator|(
operator|(
name|StringValue
operator|)
name|errorVal1
operator|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|errorVals
operator|.
name|getItemCount
argument_list|()
operator|==
literal|2
condition|)
block|{
specifier|final
name|Item
name|errorVal2
init|=
name|errorVals
operator|.
name|itemAt
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|errorVal2
operator|instanceof
name|StringValue
condition|)
block|{
if|if
condition|(
name|missingModuleHint
operator|==
literal|null
condition|)
block|{
name|missingModuleHint
operator|=
operator|new
name|MissingModuleHint
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|dependantModuleUri
init|=
operator|(
operator|(
name|StringValue
operator|)
name|errorVal2
operator|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|//path will be of xmldb:exist:///db/a/c/1.xqm form so change it to /db/a/c/1.xqm form
name|missingModuleHint
operator|.
name|dependantModule
operator|=
name|makeDbAbsolutePath
argument_list|(
name|dependantModuleUri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|missingModuleHint
return|;
block|}
specifier|private
name|void
name|recordMissingDependency
parameter_list|(
specifier|final
name|String
name|moduleHint
parameter_list|,
specifier|final
name|XmldbURI
name|xqueryUri
parameter_list|)
block|{
specifier|final
name|String
name|moduleUri
init|=
name|getAbsoluteModuleHint
argument_list|(
name|moduleHint
argument_list|,
name|xqueryUri
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|missingDependencies
init|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|dependants
decl_stmt|;
if|if
condition|(
name|missingDependencies
operator|.
name|containsKey
argument_list|(
name|moduleUri
argument_list|)
condition|)
block|{
name|dependants
operator|=
name|missingDependencies
operator|.
name|get
argument_list|(
name|moduleUri
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dependants
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|dependants
operator|.
name|add
argument_list|(
name|xqueryUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|missingDependencies
operator|.
name|put
argument_list|(
name|moduleUri
argument_list|,
name|dependants
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Module '"
operator|+
name|xqueryUri
operator|+
literal|"' has a missing dependency on '"
operator|+
name|moduleUri
operator|+
literal|"'. Will re-examine if the missing module is added."
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|removeDependency
parameter_list|(
specifier|final
name|String
name|dependant
parameter_list|,
specifier|final
name|String
name|dependency
parameter_list|)
block|{
synchronized|synchronized
init|(
name|dependenciesTree
init|)
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|dependencies
init|=
name|dependenciesTree
operator|.
name|get
argument_list|(
name|dependant
argument_list|)
decl_stmt|;
if|if
condition|(
name|dependencies
operator|!=
literal|null
condition|)
block|{
name|dependencies
operator|.
name|remove
argument_list|(
name|dependency
argument_list|)
expr_stmt|;
if|if
condition|(
name|dependencies
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|dependenciesTree
operator|.
name|remove
argument_list|(
name|dependant
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
name|String
name|getAbsoluteModuleHint
parameter_list|(
specifier|final
name|String
name|moduleHint
parameter_list|,
specifier|final
name|XmldbURI
name|xqueryUri
parameter_list|)
block|{
if|if
condition|(
name|moduleHint
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
condition|)
block|{
comment|//absolute simple path
return|return
name|moduleHint
return|;
block|}
if|else if
condition|(
name|moduleHint
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|moduleHint
operator|.
name|replace
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|toString
argument_list|()
argument_list|,
literal|""
argument_list|)
return|;
block|}
if|else if
condition|(
name|moduleHint
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
argument_list|)
condition|)
block|{
return|return
name|moduleHint
operator|.
name|replace
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
argument_list|,
literal|""
argument_list|)
return|;
block|}
else|else
block|{
comment|//relative to the xqueryUri
specifier|final
name|XmldbURI
name|xqueryPath
init|=
name|xqueryUri
operator|.
name|removeLastSegment
argument_list|()
decl_stmt|;
return|return
name|xqueryPath
operator|.
name|append
argument_list|(
name|moduleHint
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Converts an xmldb:exist:// path to an Absolute DB path      *       * e.g. path  xmldb:exist:///db/a/c/1.xqm form will to /db/a/c/1.xqm form      */
specifier|private
name|String
name|makeDbAbsolutePath
parameter_list|(
name|String
name|dependantModuleUri
parameter_list|)
block|{
name|dependantModuleUri
operator|=
name|dependantModuleUri
operator|.
name|replace
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|toString
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|dependantModuleUri
operator|=
name|dependantModuleUri
operator|.
name|replace
argument_list|(
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
argument_list|,
literal|""
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dependantModuleUri
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|dependantModuleUri
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|dependantModuleUri
operator|=
name|dependantModuleUri
operator|.
name|substring
argument_list|(
name|dependantModuleUri
operator|.
name|indexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|dependantModuleUri
return|;
block|}
specifier|private
name|RestXqServiceRegistry
name|getRegistry
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|RestXqServiceRegistryManager
operator|.
name|getRegistry
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

