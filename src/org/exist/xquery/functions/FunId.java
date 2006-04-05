begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
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
name|java
operator|.
name|util
operator|.
name|StringTokenizer
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
name|ExtArrayNodeSet
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
name|NodeSetHelper
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
name|ElementValue
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
name|XMLChar
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
name|Constants
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
name|Profiler
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
name|SequenceIterator
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

begin_class
specifier|public
class|class
name|FunId
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
literal|"id"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
comment|/** 	 * Constructor for FunId. 	 */
specifier|public
name|FunId
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
comment|/** 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.dom.NodeSet, org.exist.dom.NodeProxy) 	 */
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
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|<
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"function id requires one argument"
argument_list|)
throw|;
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
name|Sequence
name|result
decl_stmt|;
name|Expression
name|arg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Sequence
name|idval
init|=
name|arg
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|idval
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
else|else
block|{
name|result
operator|=
operator|new
name|ExtArrayNodeSet
argument_list|()
expr_stmt|;
name|String
name|nextId
decl_stmt|;
name|DocumentSet
name|docs
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
operator|!
operator|(
name|contextSequence
operator|instanceof
name|NodeSet
operator|)
condition|)
name|docs
operator|=
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
expr_stmt|;
else|else
name|docs
operator|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|idval
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|nextId
operator|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextId
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
comment|// parse idrefs
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|nextId
argument_list|,
literal|" "
argument_list|)
decl_stmt|;
while|while
condition|(
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|nextId
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|nextId
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|nextId
operator|+
literal|" is not a valid NCName"
argument_list|)
throw|;
name|QName
name|id
init|=
operator|new
name|QName
argument_list|(
name|nextId
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|getId
argument_list|(
operator|(
name|NodeSet
operator|)
name|result
argument_list|,
name|docs
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|nextId
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|nextId
operator|+
literal|" is not a valid NCName"
argument_list|)
throw|;
name|QName
name|id
init|=
operator|new
name|QName
argument_list|(
name|nextId
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|getId
argument_list|(
operator|(
name|NodeSet
operator|)
name|result
argument_list|,
name|docs
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|void
name|getId
parameter_list|(
name|NodeSet
name|result
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|QName
name|id
parameter_list|)
block|{
name|NodeSet
name|attribs
init|=
operator|(
name|NodeSet
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getElementIndex
argument_list|()
operator|.
name|findElementsByTagName
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE_ID
argument_list|,
name|docs
argument_list|,
name|id
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeProxy
name|n
decl_stmt|,
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|attribs
operator|.
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
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|p
operator|=
operator|new
name|NodeProxy
argument_list|(
name|n
operator|.
name|getDocument
argument_list|()
argument_list|,
name|NodeSetHelper
operator|.
name|getParentId
argument_list|(
name|n
argument_list|)
argument_list|,
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

