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
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|cglib
operator|.
name|proxy
operator|.
name|Callback
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|cglib
operator|.
name|proxy
operator|.
name|CallbackFilter
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|cglib
operator|.
name|proxy
operator|.
name|Dispatcher
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|cglib
operator|.
name|proxy
operator|.
name|Enhancer
import|;
end_import

begin_import
import|import
name|net
operator|.
name|sf
operator|.
name|cglib
operator|.
name|proxy
operator|.
name|NoOp
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
name|ContextItem
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
name|Match
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
name|NodeHandle
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
name|xquery
operator|.
name|value
operator|.
name|Type
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
name|Attr
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
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|Node
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
name|Text
import|;
end_import

begin_comment
comment|/**  * A NodeProxy Proxy which enhances NodeProxy  * with a W3C DOM implementation by proxying to  * its underlying typed node which is available  * through NodeProxy.getNode()  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|DomEnhancingNodeProxyAdapter
block|{
specifier|public
specifier|final
specifier|static
name|NodeProxy
name|create
parameter_list|(
specifier|final
name|NodeProxy
name|nodeProxy
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Node
argument_list|>
name|clazzes
index|[]
init|=
name|getNodeClasses
argument_list|(
name|nodeProxy
argument_list|)
decl_stmt|;
comment|// NoOp Callback is for NodeProxy calls
comment|// NodeDispatched Callback is for the underlying Node calls
specifier|final
name|Callback
index|[]
name|callbacks
init|=
block|{
name|NoOp
operator|.
name|INSTANCE
block|,
operator|new
name|NodeDispatcher
argument_list|(
name|nodeProxy
argument_list|)
block|}
decl_stmt|;
specifier|final
name|CallbackFilter
name|callbackFilter
init|=
operator|new
name|CallbackFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|accept
parameter_list|(
specifier|final
name|Method
name|method
parameter_list|)
block|{
specifier|final
name|Class
name|declaringClass
init|=
name|method
operator|.
name|getDeclaringClass
argument_list|()
decl_stmt|;
comment|//look for nodes
name|boolean
name|isMethodOnNode
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|declaringClass
operator|.
name|equals
argument_list|(
name|Node
operator|.
name|class
argument_list|)
condition|)
block|{
name|isMethodOnNode
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
comment|//search parent interfaces
for|for
control|(
specifier|final
name|Class
name|iface
range|:
name|declaringClass
operator|.
name|getInterfaces
argument_list|()
control|)
block|{
if|if
condition|(
name|iface
operator|.
name|equals
argument_list|(
name|Node
operator|.
name|class
argument_list|)
condition|)
block|{
name|isMethodOnNode
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|isMethodOnNode
condition|)
block|{
return|return
literal|1
return|;
comment|//The NodeDispatcher
block|}
else|else
block|{
return|return
literal|0
return|;
comment|//The NoOp passthrough
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|Enhancer
name|enhancer
init|=
operator|new
name|Enhancer
argument_list|()
decl_stmt|;
name|enhancer
operator|.
name|setSuperclass
argument_list|(
name|NodeProxy
operator|.
name|class
argument_list|)
expr_stmt|;
name|enhancer
operator|.
name|setInterfaces
argument_list|(
name|clazzes
argument_list|)
expr_stmt|;
name|enhancer
operator|.
name|setCallbackFilter
argument_list|(
name|callbackFilter
argument_list|)
expr_stmt|;
name|enhancer
operator|.
name|setCallbacks
argument_list|(
name|callbacks
argument_list|)
expr_stmt|;
specifier|final
name|NodeProxy
name|enhancedNodeProxy
init|=
operator|(
name|NodeProxy
operator|)
name|enhancer
operator|.
name|create
argument_list|(
operator|new
name|Class
index|[]
block|{
name|NodeHandle
operator|.
name|class
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|nodeProxy
block|}
argument_list|)
decl_stmt|;
return|return
name|enhancedNodeProxy
return|;
block|}
specifier|private
specifier|final
specifier|static
name|Class
argument_list|<
name|?
extends|extends
name|Node
argument_list|>
index|[]
name|getNodeClasses
parameter_list|(
specifier|final
name|NodeProxy
name|nodeProxy
parameter_list|)
block|{
switch|switch
condition|(
name|nodeProxy
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|DOCUMENT
case|:
return|return
operator|new
name|Class
index|[]
block|{
name|Document
operator|.
name|class
block|,
name|Node
operator|.
name|class
block|}
return|;
case|case
name|Type
operator|.
name|ELEMENT
case|:
return|return
operator|new
name|Class
index|[]
block|{
name|Element
operator|.
name|class
block|,
name|Node
operator|.
name|class
block|}
return|;
case|case
name|Type
operator|.
name|ATTRIBUTE
case|:
return|return
operator|new
name|Class
index|[]
block|{
name|Attr
operator|.
name|class
block|,
name|Node
operator|.
name|class
block|}
return|;
case|case
name|Type
operator|.
name|TEXT
case|:
return|return
operator|new
name|Class
index|[]
block|{
name|Text
operator|.
name|class
block|,
name|Node
operator|.
name|class
block|}
return|;
default|default:
return|return
operator|new
name|Class
index|[]
block|{
name|Node
operator|.
name|class
block|}
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|NodeDispatcher
implements|implements
name|Dispatcher
block|{
specifier|private
specifier|final
name|NodeProxy
name|nodeProxy
decl_stmt|;
specifier|public
name|NodeDispatcher
parameter_list|(
specifier|final
name|NodeProxy
name|nodeProxy
parameter_list|)
block|{
name|this
operator|.
name|nodeProxy
operator|=
name|nodeProxy
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|loadObject
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|nodeProxy
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

