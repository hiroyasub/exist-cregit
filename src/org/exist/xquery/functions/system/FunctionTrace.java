begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * \$Id\$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|system
package|;
end_package

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
name|xquery
operator|.
name|*
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
name|*
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
name|dom
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
name|BrokerPool
import|;
end_import

begin_class
specifier|public
class|class
name|FunctionTrace
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FunctionTrace
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"trace"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns function call statistics gathered by the trace log."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"call-statistics"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the call statistics gathered by the trace"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"enable-tracing"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Enable function tracing on the database instance."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"enable"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The boolean flag to enable/disable function tracing"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"enable-tracing"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Enable function tracing on the database instance."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"enable"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The enable boolean flag to enable/disable function tracing"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"tracelog"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The tracelog boolean flag: if set to true, entering/exiting a function will be logged to the logger 'xquery.profiling'"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"tracing-enabled"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns true if function tracing is currently enabled on the database instance."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"tracing-enabled"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true is tracing is enabled."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"clear-trace"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Clear the global trace log."
argument_list|,
literal|null
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunctionTrace
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Entering "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"clear-trace"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Entering the "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":clear-trace XQuery function"
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getPerformanceStats
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"enable-tracing"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Entering the "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":enable-tracing XQuery function"
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|enable
init|=
name|args
index|[
literal|0
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getPerformanceStats
argument_list|()
operator|.
name|setEnabled
argument_list|(
name|enable
argument_list|)
expr_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
block|{
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|Profiler
operator|.
name|CONFIG_PROPERTY_TRACELOG
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|Profiler
operator|.
name|CONFIG_PROPERTY_TRACELOG
argument_list|,
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"tracing-enabled"
argument_list|)
condition|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Entering the "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":tracing-enabled XQuery function"
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getPerformanceStats
argument_list|()
operator|.
name|isEnabled
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Entering the "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":trace XQuery function"
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|brokerPool
operator|.
name|getPerformanceStats
argument_list|()
operator|.
name|toXML
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"Exiting "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":"
operator|+
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

