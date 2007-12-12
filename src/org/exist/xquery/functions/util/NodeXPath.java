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
operator|.
name|util
package|;
end_package

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
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
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
name|NodeXPath
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
literal|"node-xpath"
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
literal|"Returns the XPath for a Node."
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
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|, 				}
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
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|NodeXPath
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
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|seq
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|NodeValue
name|nv
init|=
operator|(
name|NodeValue
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Node
name|n
init|=
name|nv
operator|.
name|getNode
argument_list|()
decl_stmt|;
comment|//if at the document level just return /
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
return|return
operator|new
name|StringValue
argument_list|(
literal|"/"
argument_list|)
return|;
comment|/* walk up the node hierarchy 		 * - node names become path names  		 * - attributes become predicates 		 */
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|nodeToXPath
argument_list|(
name|n
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|n
operator|.
name|getParentNode
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|buf
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|nodeToXPath
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|StringValue
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Creates an XPath for a Node 	 * The nodes attribute's become predicates 	 *  	 * @param n The Node to generate an XPath for 	 * @return StringBuffer containing the XPath 	 */
specifier|public
name|StringBuffer
name|nodeToXPath
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
name|StringBuffer
name|xpath
init|=
operator|new
name|StringBuffer
argument_list|(
literal|"/"
operator|+
name|getFullNodeName
argument_list|(
name|n
argument_list|)
argument_list|)
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|n
operator|.
name|getAttributes
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
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|attr
init|=
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|xpath
operator|.
name|append
argument_list|(
literal|"[@"
operator|+
name|getFullNodeName
argument_list|(
name|attr
argument_list|)
operator|+
literal|" eq \""
operator|+
name|attr
operator|.
name|getNodeValue
argument_list|()
operator|+
literal|"\"]"
argument_list|)
expr_stmt|;
block|}
return|return
name|xpath
return|;
block|}
comment|/** 	 * Returns the full node name including the prefix if present 	 *  	 * @param n The node to get the name for 	 * @return The full name of the node 	 */
specifier|public
name|String
name|getFullNodeName
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
return|return
name|n
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|n
operator|.
name|getPrefix
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|?
name|n
operator|.
name|getPrefix
argument_list|()
operator|+
literal|":"
operator|+
name|n
operator|.
name|getLocalName
argument_list|()
else|:
name|n
operator|.
name|getLocalName
argument_list|()
return|;
block|}
block|}
end_class

end_unit

