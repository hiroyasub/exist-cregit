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
operator|.
name|adapters
package|;
end_package

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
name|NodeProxy
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
name|SequenceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|TypedValue
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|SequenceAdapter
implements|implements
name|Sequence
argument_list|<
name|Item
argument_list|>
block|{
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
name|SequenceAdapter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|sequence
decl_stmt|;
specifier|public
name|SequenceAdapter
parameter_list|(
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|TypedValue
argument_list|<
name|Item
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|TypedValue
argument_list|<
name|Item
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|private
name|SequenceIterator
name|iterator
decl_stmt|;
specifier|private
name|SequenceIterator
name|getIterator
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|iterator
operator|=
name|sequence
operator|.
name|iterate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|xpe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to extract the underlying Sequence Iterator: "
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
operator|+
literal|". Falling back to EMPTY_ITERATOR"
argument_list|,
name|xpe
argument_list|)
expr_stmt|;
name|iterator
operator|=
name|SequenceIterator
operator|.
name|EMPTY_ITERATOR
expr_stmt|;
block|}
block|}
return|return
name|iterator
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|getIterator
argument_list|()
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TypedValue
argument_list|<
name|Item
argument_list|>
name|next
parameter_list|()
block|{
return|return
operator|new
name|TypedValue
argument_list|<
name|Item
argument_list|>
argument_list|()
block|{
specifier|final
name|Item
name|item
init|=
name|getIterator
argument_list|()
operator|.
name|nextItem
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|TypeAdapter
operator|.
name|toExQueryType
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Item
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|item
operator|instanceof
name|NodeProxy
condition|)
block|{
return|return
name|DomEnhancingNodeProxyAdapter
operator|.
name|create
argument_list|(
operator|(
name|NodeProxy
operator|)
name|item
argument_list|)
return|;
comment|//RESTXQ expects to find DOM Nodes not NodeProxys
block|}
else|else
block|{
return|return
name|item
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
comment|//do nothing
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
argument_list|<
name|Item
argument_list|>
name|tail
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|SequenceAdapter
argument_list|(
name|sequence
operator|.
name|tail
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|xpe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
expr_stmt|;
return|return
operator|new
name|SequenceAdapter
argument_list|(
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
return|;
block|}
block|}
specifier|public
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|getExistSequence
parameter_list|()
block|{
return|return
name|sequence
return|;
block|}
block|}
end_class

end_unit

