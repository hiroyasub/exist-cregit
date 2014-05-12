begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|range
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|NodeSet
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
name|VirtualNodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|RangeIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|RangeIndexConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|RangeIndexConfigElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|RangeIndexWorker
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
name|ElementValue
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
name|IndexSpec
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
name|NodePath
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
name|Arrays
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

begin_class
specifier|public
class|class
name|Lookup
extends|extends
name|Function
implements|implements
name|Optimizable
block|{
specifier|private
specifier|final
specifier|static
name|SequenceType
index|[]
name|PARAMETER_TYPE
init|=
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set to search using a range index which is defined on those nodes"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"key"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The key to look up."
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DESCRIPTION
init|=
literal|"Search for nodes matching the given keys in the range "
operator|+
literal|"index. Normally this function will be called by the query optimizer."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"eq"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"gt"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"lt"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"le"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"ge"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"starts-with"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"ends-with"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"contains"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
name|PARAMETER_TYPE
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value is equal to the key."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"matches"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set to search using a range index which is defined on those nodes"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"regex"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The regular expression."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set whose node value matches the regular expression. Regular expression "
operator|+
literal|"syntax is limited to what Lucene supports. See http://lucene.apache.org/core/4_5_1/core/org/apache/lucene/util/automaton/RegExp.html"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|static
name|Lookup
name|create
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|RangeIndex
operator|.
name|Operator
name|operator
parameter_list|,
name|NodePath
name|contextPath
parameter_list|)
block|{
for|for
control|(
name|FunctionSignature
name|sig
range|:
name|signatures
control|)
block|{
if|if
condition|(
name|sig
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|operator
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|new
name|Lookup
argument_list|(
name|context
argument_list|,
name|sig
argument_list|,
name|contextPath
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|LocationStep
name|contextStep
init|=
literal|null
decl_stmt|;
specifier|protected
name|QName
name|contextQName
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|axis
init|=
name|Constants
operator|.
name|UNKNOWN_AXIS
decl_stmt|;
specifier|private
name|NodeSet
name|preselectResult
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|canOptimize
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|optimizeSelf
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|optimizeChild
init|=
literal|false
decl_stmt|;
specifier|protected
name|Expression
name|fallback
init|=
literal|null
decl_stmt|;
specifier|protected
name|NodePath
name|contextPath
init|=
literal|null
decl_stmt|;
specifier|public
name|Lookup
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|signature
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Lookup
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|,
name|NodePath
name|contextPath
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|contextPath
operator|=
name|contextPath
expr_stmt|;
block|}
specifier|public
name|void
name|setFallback
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|InternalFunctionCall
condition|)
block|{
name|expression
operator|=
operator|(
operator|(
name|InternalFunctionCall
operator|)
name|expression
operator|)
operator|.
name|getFunction
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|fallback
operator|=
name|expression
expr_stmt|;
block|}
specifier|public
name|Expression
name|getFallback
parameter_list|()
block|{
return|return
name|fallback
return|;
block|}
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|steps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Expression
name|path
init|=
name|arguments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Expression
name|arg
init|=
name|arguments
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|simplify
argument_list|()
decl_stmt|;
name|arg
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|arg
argument_list|)
expr_stmt|;
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|arg
argument_list|,
operator|new
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
operator|.
name|Error
argument_list|(
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
operator|.
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"2"
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|LocationStep
argument_list|>
name|steps
init|=
name|BasicExpressionVisitor
operator|.
name|findLocationSteps
argument_list|(
name|getArgument
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|steps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LocationStep
name|firstStep
init|=
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LocationStep
name|lastStep
init|=
name|steps
operator|.
name|get
argument_list|(
name|steps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstStep
operator|!=
literal|null
operator|&&
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|firstStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|)
block|{
name|Expression
name|outerExpr
init|=
name|contextInfo
operator|.
name|getContextStep
argument_list|()
decl_stmt|;
if|if
condition|(
name|outerExpr
operator|!=
literal|null
operator|&&
name|outerExpr
operator|instanceof
name|LocationStep
condition|)
block|{
name|LocationStep
name|outerStep
init|=
operator|(
name|LocationStep
operator|)
name|outerExpr
decl_stmt|;
name|NodeTest
name|test
init|=
name|outerStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|isWildcardTest
argument_list|()
condition|)
name|contextQName
operator|=
name|test
operator|.
name|getName
argument_list|()
expr_stmt|;
else|else
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|outerStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|outerStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|contextStep
operator|=
name|firstStep
expr_stmt|;
name|axis
operator|=
name|outerStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
name|optimizeSelf
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|lastStep
operator|!=
literal|null
operator|&&
name|firstStep
operator|!=
literal|null
condition|)
block|{
name|NodeTest
name|test
init|=
name|lastStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|isWildcardTest
argument_list|()
condition|)
name|contextQName
operator|=
name|test
operator|.
name|getName
argument_list|()
expr_stmt|;
else|else
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lastStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|lastStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|axis
operator|=
name|firstStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
name|optimizeChild
operator|=
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|axis
operator|==
name|Constants
operator|.
name|CHILD_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|)
expr_stmt|;
name|contextStep
operator|=
name|lastStep
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fallback
operator|!=
literal|null
condition|)
block|{
name|fallback
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|NodeSet
name|preSelect
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|boolean
name|useContext
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|canOptimize
condition|)
block|{
return|return
operator|(
operator|(
name|Optimizable
operator|)
name|fallback
operator|)
operator|.
name|preSelect
argument_list|(
name|contextSequence
argument_list|,
name|useContext
argument_list|)
return|;
block|}
if|if
condition|(
name|contextSequence
operator|!=
literal|null
operator|&&
operator|!
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
condition|)
comment|// in-memory docs won't have an index
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// the expression can be called multiple times, so we need to clear the previous preselectResult
name|preselectResult
operator|=
literal|null
expr_stmt|;
name|RangeIndexWorker
name|index
init|=
operator|(
name|RangeIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|RangeIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
name|DocumentSet
name|docs
init|=
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|AtomicValue
index|[]
name|keys
init|=
name|getKeys
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|keys
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
block|}
name|List
argument_list|<
name|QName
argument_list|>
name|qnames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextQName
operator|!=
literal|null
condition|)
block|{
name|qnames
operator|=
operator|new
name|ArrayList
argument_list|<
name|QName
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|qnames
operator|.
name|add
argument_list|(
name|contextQName
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RangeIndex
operator|.
name|Operator
name|operator
init|=
name|getOperator
argument_list|()
decl_stmt|;
try|try
block|{
name|preselectResult
operator|=
name|index
operator|.
name|query
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|qnames
argument_list|,
name|keys
argument_list|,
name|operator
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Error while querying full text index: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|//LOG.info("preselect for " + Arrays.toString(keys) + " on " + contextSequence.getItemCount() + "returned " + preselectResult.getItemCount() +
comment|//        " and took " + (System.currentTimeMillis() - start));
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceFunctions
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceIndexUsage
argument_list|(
name|context
argument_list|,
literal|"new-range"
argument_list|,
name|this
argument_list|,
name|PerformanceStats
operator|.
name|OPTIMIZED_INDEX
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|preselectResult
operator|==
literal|null
condition|)
block|{
name|preselectResult
operator|=
name|NodeSet
operator|.
name|EMPTY_SET
expr_stmt|;
block|}
return|return
name|preselectResult
return|;
block|}
specifier|private
name|RangeIndex
operator|.
name|Operator
name|getOperator
parameter_list|()
block|{
specifier|final
name|String
name|calledAs
init|=
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
return|return
name|RangeIndexModule
operator|.
name|OPERATOR_MAP
operator|.
name|get
argument_list|(
name|calledAs
argument_list|)
return|;
block|}
specifier|private
name|AtomicValue
index|[]
name|getKeys
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|keySeq
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
name|AtomicValue
index|[]
name|keys
init|=
operator|new
name|AtomicValue
index|[
name|keySeq
operator|.
name|getItemCount
argument_list|()
index|]
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
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|keys
index|[
name|i
index|]
operator|=
operator|(
name|AtomicValue
operator|)
name|keySeq
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|keys
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|canOptimize
operator|&&
name|fallback
operator|!=
literal|null
condition|)
block|{
return|return
name|fallback
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
operator|&&
operator|!
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
condition|)
block|{
comment|// in-memory docs won't have an index
if|if
condition|(
name|fallback
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
else|else
block|{
return|return
name|fallback
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
block|}
name|NodeSet
name|result
decl_stmt|;
if|if
condition|(
name|preselectResult
operator|==
literal|null
condition|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Sequence
name|input
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|input
operator|instanceof
name|VirtualNodeSet
operator|)
operator|&&
name|input
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|NodeSet
operator|.
name|EMPTY_SET
expr_stmt|;
else|else
block|{
name|RangeIndexWorker
name|index
init|=
operator|(
name|RangeIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|RangeIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
name|AtomicValue
index|[]
name|keys
init|=
name|getKeys
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|keys
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
block|}
name|List
argument_list|<
name|QName
argument_list|>
name|qnames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextQName
operator|!=
literal|null
condition|)
block|{
name|qnames
operator|=
operator|new
name|ArrayList
argument_list|<
name|QName
argument_list|>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|qnames
operator|.
name|add
argument_list|(
name|contextQName
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RangeIndex
operator|.
name|Operator
name|operator
init|=
name|getOperator
argument_list|()
decl_stmt|;
try|try
block|{
name|NodeSet
name|inNodes
init|=
name|input
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|DocumentSet
name|docs
init|=
name|inNodes
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|result
operator|=
name|index
operator|.
name|query
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|inNodes
argument_list|,
name|qnames
argument_list|,
name|keys
argument_list|,
name|operator
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceFunctions
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceIndexUsage
argument_list|(
name|context
argument_list|,
literal|"new-range"
argument_list|,
name|this
argument_list|,
name|PerformanceStats
operator|.
name|BASIC_INDEX
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
comment|//            LOG.info("eval plain took " + (System.currentTimeMillis() - start));
block|}
else|else
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|contextStep
operator|.
name|setPreloadedData
argument_list|(
name|preselectResult
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|preselectResult
argument_list|)
expr_stmt|;
name|result
operator|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
comment|//LOG.info("eval took " + (System.currentTimeMillis() - start));
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
name|fallback
operator|!=
literal|null
condition|)
block|{
name|fallback
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|postOptimization
condition|)
block|{
name|preselectResult
operator|=
literal|null
expr_stmt|;
name|canOptimize
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|canOptimize
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
if|if
condition|(
name|contextQName
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RangeIndexConfigElement
name|rice
init|=
name|findConfiguration
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|rice
operator|==
literal|null
condition|)
block|{
name|canOptimize
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|fallback
operator|instanceof
name|Optimizable
condition|)
block|{
return|return
operator|(
operator|(
name|Optimizable
operator|)
name|fallback
operator|)
operator|.
name|canOptimize
argument_list|(
name|contextSequence
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
name|canOptimize
operator|=
literal|true
expr_stmt|;
return|return
name|canOptimize
return|;
block|}
specifier|private
name|RangeIndexConfigElement
name|findConfiguration
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
name|NodePath
name|path
init|=
name|contextPath
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|contextQName
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|path
operator|=
operator|new
name|NodePath
argument_list|(
name|contextQName
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|i
init|=
name|contextSequence
operator|.
name|getCollectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Collection
name|collection
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|IndexSpec
name|idxConf
init|=
name|collection
operator|.
name|getIndexConfiguration
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxConf
operator|!=
literal|null
condition|)
block|{
name|RangeIndexConfig
name|config
init|=
operator|(
name|RangeIndexConfig
operator|)
name|idxConf
operator|.
name|getCustomIndexSpec
argument_list|(
name|RangeIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|RangeIndexConfigElement
name|rice
init|=
name|config
operator|.
name|find
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|rice
operator|!=
literal|null
operator|&&
operator|!
name|rice
operator|.
name|isComplex
argument_list|()
condition|)
block|{
return|return
name|rice
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|optimizeOnSelf
parameter_list|()
block|{
return|return
name|optimizeSelf
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|optimizeOnChild
parameter_list|()
block|{
return|return
name|optimizeChild
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOptimizeAxis
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|DESCENDANT_AXIS
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
specifier|final
name|Expression
name|stringArg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|stringArg
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
else|else
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
block|}
end_class

end_unit

