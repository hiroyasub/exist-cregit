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
name|exist
operator|.
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
operator|.
name|adapters
operator|.
name|AnnotationAdapter
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
name|DBSource
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
name|Annotation
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
name|ExternalModule
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
name|UserDefinedFunction
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
name|annotation
operator|.
name|AnnotationException
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
name|ResourceFunction
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
name|impl
operator|.
name|ResourceFunctionFactory
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
name|impl
operator|.
name|annotation
operator|.
name|RestAnnotationFactory
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|XQueryInspector
block|{
specifier|public
specifier|static
name|List
argument_list|<
name|RestXqService
argument_list|>
name|findServices
parameter_list|(
specifier|final
name|CompiledXQuery
name|compiled
parameter_list|)
throws|throws
name|ExQueryException
block|{
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|services
init|=
operator|new
name|ArrayList
argument_list|<
name|RestXqService
argument_list|>
argument_list|()
decl_stmt|;
try|try
block|{
comment|//look at each function
specifier|final
name|Iterator
argument_list|<
name|UserDefinedFunction
argument_list|>
name|itFunctions
init|=
name|compiled
operator|.
name|getContext
argument_list|()
operator|.
name|localFunctions
argument_list|()
decl_stmt|;
while|while
condition|(
name|itFunctions
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|UserDefinedFunction
name|function
init|=
name|itFunctions
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|Annotation
name|annotations
index|[]
init|=
name|function
operator|.
name|getSignature
argument_list|()
operator|.
name|getAnnotations
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|org
operator|.
name|exquery
operator|.
name|xquery3
operator|.
name|Annotation
argument_list|>
name|functionRestAnnotations
init|=
literal|null
decl_stmt|;
comment|//process the function annotations
for|for
control|(
specifier|final
name|Annotation
name|annotation
range|:
name|annotations
control|)
block|{
if|if
condition|(
name|RestAnnotationFactory
operator|.
name|isRestXqAnnotation
argument_list|(
name|annotation
operator|.
name|getName
argument_list|()
operator|.
name|toJavaQName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|org
operator|.
name|exquery
operator|.
name|xquery3
operator|.
name|Annotation
name|restAnnotation
init|=
name|RestAnnotationFactory
operator|.
name|getAnnotation
argument_list|(
operator|new
name|AnnotationAdapter
argument_list|(
name|annotation
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|functionRestAnnotations
operator|==
literal|null
condition|)
block|{
name|functionRestAnnotations
operator|=
operator|new
name|HashSet
argument_list|<
name|org
operator|.
name|exquery
operator|.
name|xquery3
operator|.
name|Annotation
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|functionRestAnnotations
operator|.
name|add
argument_list|(
name|restAnnotation
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|functionRestAnnotations
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ResourceFunction
name|resourceFunction
init|=
name|ResourceFunctionFactory
operator|.
name|create
argument_list|(
operator|new
name|URI
argument_list|(
name|compiled
operator|.
name|getSource
argument_list|()
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|functionRestAnnotations
argument_list|)
decl_stmt|;
specifier|final
name|RestXqService
name|service
init|=
operator|new
name|RestXqServiceImpl
argument_list|(
name|resourceFunction
argument_list|,
name|compiled
operator|.
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
comment|//add service and compiled query to the cache
name|RestXqServiceCompiledXQueryCacheImpl
operator|.
name|getInstance
argument_list|()
operator|.
name|returnCompiledQuery
argument_list|(
name|resourceFunction
operator|.
name|getXQueryLocation
argument_list|()
argument_list|,
name|compiled
argument_list|)
expr_stmt|;
comment|//add the service to the list of services for this query
name|services
operator|.
name|add
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|use
parameter_list|)
block|{
throw|throw
operator|new
name|ExQueryException
argument_list|(
name|use
operator|.
name|getMessage
argument_list|()
argument_list|,
name|use
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AnnotationException
name|ae
parameter_list|)
block|{
throw|throw
operator|new
name|ExQueryException
argument_list|(
name|ae
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ae
argument_list|)
throw|;
block|}
return|return
name|services
return|;
block|}
specifier|public
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
name|getDependencies
parameter_list|(
specifier|final
name|CompiledXQuery
name|compiled
parameter_list|)
block|{
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
name|dependencies
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
name|getDependencies
argument_list|(
name|compiled
operator|.
name|getContext
argument_list|()
argument_list|,
name|dependencies
argument_list|)
expr_stmt|;
return|return
name|dependencies
return|;
block|}
specifier|private
specifier|static
name|void
name|getDependencies
parameter_list|(
specifier|final
name|XQueryContext
name|xqyCtx
parameter_list|,
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
name|dependencies
parameter_list|)
block|{
specifier|final
name|String
name|xqueryUri
init|=
name|getDbUri
argument_list|(
name|xqyCtx
operator|.
name|getSource
argument_list|()
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|depSet
init|=
name|dependencies
operator|.
name|get
argument_list|(
name|xqueryUri
argument_list|)
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|Module
argument_list|>
name|itModule
init|=
name|xqyCtx
operator|.
name|getModules
argument_list|()
decl_stmt|;
while|while
condition|(
name|itModule
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Module
name|module
init|=
name|itModule
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|module
operator|instanceof
name|ExternalModule
condition|)
block|{
specifier|final
name|ExternalModule
name|extModule
init|=
operator|(
name|ExternalModule
operator|)
name|module
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|extModule
operator|.
name|getSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|instanceof
name|DBSource
condition|)
block|{
specifier|final
name|String
name|moduleUri
init|=
name|getDbUri
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|depSet
operator|==
literal|null
condition|)
block|{
name|depSet
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|depSet
operator|.
name|add
argument_list|(
name|moduleUri
argument_list|)
expr_stmt|;
block|}
name|getDependencies
argument_list|(
name|extModule
operator|.
name|getContext
argument_list|()
argument_list|,
name|dependencies
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|depSet
operator|!=
literal|null
condition|)
block|{
name|dependencies
operator|.
name|put
argument_list|(
name|xqueryUri
argument_list|,
name|depSet
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getDbUri
parameter_list|(
name|Source
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|!=
literal|null
operator|&&
name|source
operator|instanceof
name|DBSource
condition|)
block|{
return|return
operator|(
operator|(
name|XmldbURI
operator|)
name|source
operator|.
name|getKey
argument_list|()
operator|)
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

