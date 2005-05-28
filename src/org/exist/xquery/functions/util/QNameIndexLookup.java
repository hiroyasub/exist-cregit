begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|util
package|;
end_package

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
name|storage
operator|.
name|Indexable
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
name|NativeValueIndexByQName
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
name|Cardinality
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
name|Dependency
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
name|DynamicCardinalityCheck
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
name|Expression
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
name|Function
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
name|FunctionSignature
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
name|RootNode
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
name|util
operator|.
name|Error
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
name|util
operator|.
name|ExpressionDumper
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
name|util
operator|.
name|Messages
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
name|AtomicValue
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
name|QNameValue
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
name|SequenceType
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

begin_comment
comment|/**  * @author J.M. Vanel  */
end_comment

begin_class
specifier|public
class|class
name|QNameIndexLookup
extends|extends
name|Function
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"qname-index-lookup"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Fast retrieval of nodes by node name and content, "
operator|+
literal|"using the new value index by QName's"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|QNameIndexLookup
parameter_list|(
name|XQueryContext
name|context
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
comment|/**      * Overwritten: function can process the whole context sequence at once.      *       * @see org.exist.xquery.Expression#getDependencies()      */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
comment|/**      * Overwritten to disable automatic type checks. We check manually.      *       * @see org.exist.xquery.Function#setArguments(java.util.List)      */
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// wrap arguments into a cardinality check, so an error will be generated if
comment|// an empty sequence is passed to the function
name|Expression
name|arg
init|=
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|arg
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"1"
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
name|arg
operator|=
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|1
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
name|ONE_OR_MORE
argument_list|,
name|arg
argument_list|,
operator|new
name|Error
argument_list|(
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
name|contextSequence
operator|==
literal|null
operator|||
name|contextSequence
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// if the context sequence is empty, we create a default context
name|RootNode
name|rootNode
init|=
operator|new
name|RootNode
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|contextSequence
operator|=
name|rootNode
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|Sequence
index|[]
name|args
init|=
name|getArguments
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Item
name|item
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|QNameValue
name|qval
decl_stmt|;
try|try
block|{
comment|// attempt to convert the first argument to a QName
name|qval
operator|=
operator|(
name|QNameValue
operator|)
name|item
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|QNAME
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|// wrong type: generate a diagnostic error
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|Messages
operator|.
name|formatMessage
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_TYPE
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|"1"
block|,
name|mySignature
operator|.
name|toString
argument_list|()
block|,
literal|null
block|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|QNAME
argument_list|)
block|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
block|}
argument_list|)
argument_list|)
throw|;
block|}
name|QName
name|qname
init|=
name|qval
operator|.
name|getQName
argument_list|()
decl_stmt|;
name|AtomicValue
name|comparisonCriterium
init|=
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
decl_stmt|;
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
if|if
condition|(
name|comparisonCriterium
operator|instanceof
name|Indexable
condition|)
block|{
name|NativeValueIndexByQName
name|valueIndex
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getQNameValueIndex
argument_list|()
decl_stmt|;
name|result
operator|=
name|valueIndex
operator|.
name|findByQName
argument_list|(
name|qname
argument_list|,
name|comparisonCriterium
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO error message& log :
comment|// "The comparison Criterium must be an Indexable: boolean, numeric, string, and not ...
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

