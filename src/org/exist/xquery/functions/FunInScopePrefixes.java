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
name|LinkedHashMap
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
name|Namespaces
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
name|LinkedHashMap
argument_list|()
decl_stmt|;
name|NodeValue
name|nodeValue
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
name|nodeValue
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|PERSISTENT_NODE
condition|)
block|{
comment|//NodeProxy proxy = (NodeProxy) node;
name|Node
name|node
init|=
name|nodeValue
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|preserveNamespaces
argument_list|()
condition|)
block|{
comment|//Horrible hacks to work-around bad in-scope NS : we reconstruct a NS context !
if|if
condition|(
name|context
operator|.
name|inheritNamespaces
argument_list|()
condition|)
block|{
comment|//Grab ancestors' NS
do|do
block|{
name|collectNamespacePrefixes
argument_list|(
operator|(
name|ElementImpl
operator|)
name|node
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
do|;
comment|/* 					NodeSet ancestors = nodeValue.getAncestors(contextId, true); 					for (Iterator i = ancestors.iterator(); i.hasNext(); ) { 						proxy = (NodeProxy) i.next(); 						collectNamespacePrefixes((ElementImpl)node, prefixes); 					} 					*/
block|}
else|else
block|{
comment|//Grab self's NS
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
name|collectNamespacePrefixes
argument_list|(
operator|(
name|ElementImpl
operator|)
name|node
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//untested : copied from below
if|if
condition|(
name|context
operator|.
name|inheritNamespaces
argument_list|()
condition|)
block|{
comment|//get the top-most ancestor
do|do
block|{
if|if
condition|(
name|node
operator|.
name|getParentNode
argument_list|()
operator|==
literal|null
operator|||
name|node
operator|.
name|getParentNode
argument_list|()
operator|instanceof
name|DocumentImpl
condition|)
name|collectNamespacePrefixes
argument_list|(
operator|(
name|ElementImpl
operator|)
name|node
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
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
block|}
comment|// Add xmlNS to all in-memory constructs. /ljo
name|prefixes
operator|.
name|put
argument_list|(
literal|"xml"
argument_list|,
name|Namespaces
operator|.
name|XML_NS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// In-memory node
comment|//NodeImpl nodeImpl = (NodeImpl) node;
name|Node
name|node
init|=
name|nodeValue
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|preserveNamespaces
argument_list|()
condition|)
block|{
comment|//Horrible hacks to work-around bad in-scope NS : we reconstruct a NS context !
if|if
condition|(
name|context
operator|.
name|inheritNamespaces
argument_list|()
condition|)
block|{
comment|//Grab ancestors' NS
do|do
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
name|collectNamespacePrefixes
argument_list|(
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
operator|)
name|node
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
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
else|else
block|{
comment|//Grab self's NS
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
name|collectNamespacePrefixes
argument_list|(
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
operator|)
name|node
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|context
operator|.
name|inheritNamespaces
argument_list|()
condition|)
block|{
comment|//get the top-most ancestor
do|do
block|{
if|if
condition|(
name|node
operator|.
name|getParentNode
argument_list|()
operator|==
literal|null
operator|||
name|node
operator|.
name|getParentNode
argument_list|()
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
condition|)
name|collectNamespacePrefixes
argument_list|(
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
operator|)
name|node
argument_list|,
name|prefixes
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
do|while
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
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
block|}
comment|// Add xmlNS to all in-memory constructs. /ljo
name|prefixes
operator|.
name|put
argument_list|(
literal|"xml"
argument_list|,
name|Namespaces
operator|.
name|XML_NS
argument_list|)
expr_stmt|;
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
comment|//The predefined namespaces (e.g. "exist" for temporary nodes) could have been removed from the static context
if|if
condition|(
operator|!
operator|(
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
operator|&&
operator|(
literal|"exist"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|||
literal|"xs"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|||
literal|"xsi"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|||
literal|"wdt"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|||
literal|"fn"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|||
literal|"local"
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|)
operator|)
condition|)
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
name|String
name|prefix
init|=
name|element
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
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
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
condition|)
block|{
name|prefixes
operator|.
name|putAll
argument_list|(
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|ElementImpl
operator|)
name|element
operator|)
operator|.
name|getNamespaceMap
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ElementImpl
name|elementImpl
init|=
operator|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|ElementImpl
operator|)
name|element
decl_stmt|;
if|if
condition|(
name|elementImpl
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
name|elementImpl
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
name|String
name|prefix
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|prefixes
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|elementImpl
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
block|}
block|}
end_class

end_unit

