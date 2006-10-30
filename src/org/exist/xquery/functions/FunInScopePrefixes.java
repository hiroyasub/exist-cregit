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
name|HashMap
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
name|Map
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
name|ElementImpl
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
name|QName
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
name|BasicFunction
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
name|NodeValue
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
name|StringValue
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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

begin_class
specifier|public
class|class
name|FunInScopePrefixes
extends|extends
name|BasicFunction
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
literal|"in-scope-prefixes"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the prefixes of the in-scope namespaces for $a. For namespaces that have "
operator|+
literal|"a prefix, it returns the prefix as an xs:NCName. For the default namespace, which has "
operator|+
literal|"no prefix, it returns the zero-length string."
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
name|ELEMENT
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
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunInScopePrefixes
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
block|}
name|Map
name|prefixes
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
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
if|if
condition|(
name|node
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
name|NodeProxy
name|proxy
init|=
operator|(
name|NodeProxy
operator|)
name|node
decl_stmt|;
name|NodeSet
name|ancestors
init|=
name|proxy
operator|.
name|getAncestors
argument_list|(
name|contextId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|ancestors
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
name|proxy
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|collectNamespacePrefixes
argument_list|(
operator|(
name|ElementImpl
operator|)
name|proxy
operator|.
name|getNode
argument_list|()
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|Node
name|next
init|=
operator|(
name|Node
operator|)
name|node
decl_stmt|;
do|do
block|{
name|collectNamespacePrefixes
argument_list|(
operator|(
name|Element
operator|)
name|next
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
do|;
block|}
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|String
name|prefix
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|prefixes
operator|.
name|keySet
argument_list|()
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
name|prefix
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
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
specifier|public
specifier|static
name|void
name|collectNamespacePrefixes
parameter_list|(
name|ElementImpl
name|element
parameter_list|,
name|Map
name|prefixes
parameter_list|)
block|{
name|String
name|namespaceURI
init|=
name|element
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|String
name|prefix
decl_stmt|;
if|if
condition|(
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|prefix
operator|=
name|element
operator|.
name|getPrefix
argument_list|()
expr_stmt|;
name|prefixes
operator|.
name|put
argument_list|(
name|prefix
operator|==
literal|null
condition|?
literal|""
else|:
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|element
operator|.
name|declaresNamespacePrefixes
argument_list|()
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|element
operator|.
name|getPrefixes
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|prefix
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|prefixes
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|element
operator|.
name|getNamespaceForPrefix
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|collectNamespacePrefixes
parameter_list|(
name|Element
name|element
parameter_list|,
name|Map
name|prefixes
parameter_list|)
block|{
name|String
name|namespaceURI
init|=
name|element
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|String
name|prefix
decl_stmt|;
if|if
condition|(
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|prefix
operator|=
name|element
operator|.
name|getPrefix
argument_list|()
expr_stmt|;
name|prefixes
operator|.
name|put
argument_list|(
name|prefix
operator|==
literal|null
condition|?
literal|""
else|:
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

