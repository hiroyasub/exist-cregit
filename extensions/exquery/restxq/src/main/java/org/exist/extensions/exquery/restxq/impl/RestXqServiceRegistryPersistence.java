begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2012, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
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
name|LineNumberReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|StandardCopyOption
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
name|StandardOpenOption
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
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
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
name|storage
operator|.
name|BrokerPool
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
name|CompiledXQuery
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

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqServiceRegistryListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery3
operator|.
name|FunctionSignature
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|RestXqServiceRegistryPersistence
implements|implements
name|RestXqServiceRegistryListener
block|{
specifier|public
specifier|final
specifier|static
name|int
name|REGISTRY_FILE_VERSION
init|=
literal|0x1
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VERSION_LABEL
init|=
literal|"version"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|LABEL_SEP
init|=
literal|": "
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|FIELD_SEP
init|=
literal|","
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ARITY_SEP
init|=
literal|"#"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|REGISTRY_FILENAME
init|=
literal|"restxq.registry"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|REGISTRY_FILENAME_TMP
init|=
name|REGISTRY_FILENAME
operator|+
literal|".tmp"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
specifier|final
name|RestXqServiceRegistry
name|registry
decl_stmt|;
specifier|public
name|RestXqServiceRegistryPersistence
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|RestXqServiceRegistry
name|registry
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|registry
operator|=
name|registry
expr_stmt|;
block|}
specifier|private
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
specifier|private
name|RestXqServiceRegistry
name|getRegistry
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
specifier|public
name|void
name|loadRegistry
parameter_list|()
block|{
comment|//only load the registry if a serialized registry exists on disk
specifier|final
name|Path
name|fRegistry
init|=
name|getRegistryFile
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|fRegistry
operator|!=
literal|null
operator|&&
name|Files
operator|.
name|exists
argument_list|(
name|fRegistry
argument_list|)
operator|&&
name|Files
operator|.
name|isRegularFile
argument_list|(
name|fRegistry
argument_list|)
condition|)
block|{
name|loadRegistry
argument_list|(
name|fRegistry
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|loadRegistry
parameter_list|(
specifier|final
name|Path
name|fRegistry
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Loading RESTXQ registry from: "
operator|+
name|fRegistry
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|LineNumberReader
name|reader
init|=
operator|new
name|LineNumberReader
argument_list|(
name|Files
operator|.
name|newBufferedReader
argument_list|(
name|fRegistry
argument_list|)
argument_list|)
init|;
specifier|final
name|DBBroker
name|broker
init|=
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
name|String
name|line
init|=
literal|null
decl_stmt|;
comment|//read version line first
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
specifier|final
name|String
name|versionStr
init|=
name|line
operator|.
name|substring
argument_list|(
name|line
operator|.
name|indexOf
argument_list|(
name|VERSION_LABEL
argument_list|)
operator|+
name|VERSION_LABEL
operator|.
name|length
argument_list|()
operator|+
name|LABEL_SEP
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|REGISTRY_FILE_VERSION
operator|!=
name|Integer
operator|.
name|parseInt
argument_list|(
name|versionStr
argument_list|)
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Unable to load RESTXQ registry file: "
operator|+
name|fRegistry
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|". Expected version: "
operator|+
name|REGISTRY_FILE_VERSION
operator|+
literal|" but saw version: "
operator|+
name|versionStr
argument_list|)
expr_stmt|;
block|}
else|else
block|{
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|xqueryLocation
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|line
operator|.
name|indexOf
argument_list|(
name|FIELD_SEP
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|CompiledXQuery
name|xquery
init|=
name|XQueryCompiler
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
operator|new
name|URI
argument_list|(
name|xqueryLocation
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|services
init|=
name|XQueryInspector
operator|.
name|findServices
argument_list|(
name|xquery
argument_list|)
decl_stmt|;
name|getRegistry
argument_list|()
operator|.
name|register
argument_list|(
name|services
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|ExQueryException
decl||
name|IOException
decl||
name|EXistException
decl||
name|URISyntaxException
name|eqe
parameter_list|)
block|{
name|log
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
name|log
operator|.
name|info
argument_list|(
literal|"RESTXQ registry loaded."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registered
parameter_list|(
specifier|final
name|RestXqService
name|service
parameter_list|)
block|{
comment|//TODO consider a pause before writting to disk of maybe 1 second or so
comment|//to allow updates to batched together i.e. when one xquery has many resource functions
name|updateRegistryOnDisk
argument_list|(
name|service
argument_list|,
name|UpdateAction
operator|.
name|ADD
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|deregistered
parameter_list|(
specifier|final
name|RestXqService
name|service
parameter_list|)
block|{
comment|//TODO consider a pause before writting to disk of maybe 1 second or so
comment|//to allow updates to batched together i.e. when one xquery has many resource functions
name|updateRegistryOnDisk
argument_list|(
name|service
argument_list|,
name|UpdateAction
operator|.
name|REMOVE
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|void
name|updateRegistryOnDisk
parameter_list|(
specifier|final
name|RestXqService
name|restXqService
parameter_list|,
specifier|final
name|UpdateAction
name|updateAction
parameter_list|)
block|{
comment|//we can ignore the change in service provided to this function as args, as we just write the details of all
comment|//services to disk, overwritting the old registry
specifier|final
name|Path
name|fNewRegistry
init|=
name|getRegistryFile
argument_list|(
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|fNewRegistry
operator|==
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not save RESTXQ Registry to disk!"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Updating new RESTXQ registry on disk: "
operator|+
name|fNewRegistry
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|fNewRegistry
argument_list|,
name|StandardOpenOption
operator|.
name|TRUNCATE_EXISTING
argument_list|)
argument_list|)
init|)
block|{
name|writer
operator|.
name|println
argument_list|(
name|VERSION_LABEL
operator|+
name|LABEL_SEP
operator|+
name|REGISTRY_FILE_VERSION
argument_list|)
expr_stmt|;
comment|//get details of RESTXQ functions in XQuery modules
specifier|final
name|Map
argument_list|<
name|URI
argument_list|,
name|List
argument_list|<
name|FunctionSignature
argument_list|>
argument_list|>
name|xqueryServices
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|RestXqService
name|service
range|:
name|getRegistry
argument_list|()
control|)
block|{
name|List
argument_list|<
name|FunctionSignature
argument_list|>
name|fnNames
init|=
name|xqueryServices
operator|.
name|get
argument_list|(
name|service
operator|.
name|getResourceFunction
argument_list|()
operator|.
name|getXQueryLocation
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fnNames
operator|==
literal|null
condition|)
block|{
name|fnNames
operator|=
operator|new
name|ArrayList
argument_list|<
name|FunctionSignature
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|fnNames
operator|.
name|add
argument_list|(
name|service
operator|.
name|getResourceFunction
argument_list|()
operator|.
name|getFunctionSignature
argument_list|()
argument_list|)
expr_stmt|;
name|xqueryServices
operator|.
name|put
argument_list|(
name|service
operator|.
name|getResourceFunction
argument_list|()
operator|.
name|getXQueryLocation
argument_list|()
argument_list|,
name|fnNames
argument_list|)
expr_stmt|;
block|}
comment|//iterate and save to disk
for|for
control|(
specifier|final
name|Entry
argument_list|<
name|URI
argument_list|,
name|List
argument_list|<
name|FunctionSignature
argument_list|>
argument_list|>
name|xqueryServiceFunctions
range|:
name|xqueryServices
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writer
operator|.
name|print
argument_list|(
name|xqueryServiceFunctions
operator|.
name|getKey
argument_list|()
operator|+
name|FIELD_SEP
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|FunctionSignature
argument_list|>
name|fnSigs
init|=
name|xqueryServiceFunctions
operator|.
name|getValue
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
name|fnSigs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FunctionSignature
name|fnSig
init|=
name|fnSigs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|qnameToClarkNotation
argument_list|(
name|fnSig
operator|.
name|getName
argument_list|()
argument_list|)
operator|+
name|ARITY_SEP
operator|+
name|fnSig
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|Path
name|fRegistry
init|=
name|getRegistryFile
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|//replace the original reistry with the new registry
name|Files
operator|.
name|move
argument_list|(
name|fNewRegistry
argument_list|,
name|fRegistry
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|,
name|StandardCopyOption
operator|.
name|ATOMIC_MOVE
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Replaced RESTXQ registry with new registry: "
operator|+
name|fRegistry
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Could not replace RESTXQ registry with updated registry: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|String
name|qnameToClarkNotation
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
block|{
if|if
condition|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|qname
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|"{"
operator|+
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|"}"
operator|+
name|qname
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
block|}
specifier|private
enum|enum
name|UpdateAction
block|{
name|ADD
block|,
name|REMOVE
block|;     }
specifier|private
name|Path
name|getRegistryFile
parameter_list|(
specifier|final
name|boolean
name|temp
parameter_list|)
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
specifier|final
name|Configuration
name|configuration
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|dataDir
init|=
operator|(
name|Path
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
decl_stmt|;
return|return
name|dataDir
operator|.
name|resolve
argument_list|(
name|temp
operator|!=
literal|true
condition|?
name|REGISTRY_FILENAME
else|:
name|REGISTRY_FILENAME_TMP
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

