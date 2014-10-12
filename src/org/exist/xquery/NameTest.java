begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|NodeImpl
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
name|ReferenceNode
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_class
specifier|public
class|class
name|NameTest
extends|extends
name|TypeTest
block|{
specifier|protected
specifier|final
name|QName
name|nodeName
decl_stmt|;
specifier|public
name|NameTest
parameter_list|(
name|int
name|type
parameter_list|,
name|QName
name|name
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|name
operator|.
name|isValid
argument_list|()
expr_stmt|;
name|nodeName
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.NodeTest#matches(org.exist.dom.persistent.NodeProxy) 	 */
specifier|public
name|boolean
name|matches
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|Node
name|node
init|=
literal|null
decl_stmt|;
name|short
name|type
init|=
name|proxy
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|proxy
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
name|node
operator|=
name|proxy
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|type
operator|=
name|node
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isOfType
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|node
operator|=
name|proxy
operator|.
name|getNode
argument_list|()
expr_stmt|;
block|}
return|return
name|matchesName
argument_list|(
name|node
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|getNodeType
argument_list|()
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
return|return
name|matches
argument_list|(
operator|(
operator|(
name|ReferenceNode
operator|)
name|other
operator|)
operator|.
name|getReference
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|isOfType
argument_list|(
name|other
operator|.
name|getNodeType
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|matchesName
argument_list|(
name|other
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardNamespaceURIQName
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|name
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardLocalPartQName
operator|)
condition|)
block|{
return|return
name|nodeName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|name
operator|.
name|getLocalPart
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|matchesName
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|getNodeType
argument_list|()
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
return|return
name|matchesName
argument_list|(
operator|(
operator|(
name|ReferenceNode
operator|)
name|other
operator|)
operator|.
name|getReference
argument_list|()
operator|.
name|getNode
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardNamespaceURIQName
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardLocalPartQName
operator|)
condition|)
block|{
return|return
name|nodeName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|matches
parameter_list|(
name|XMLStreamReader
name|reader
parameter_list|)
block|{
specifier|final
name|int
name|ev
init|=
name|reader
operator|.
name|getEventType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|isOfEventType
argument_list|(
name|ev
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
switch|switch
condition|(
name|ev
condition|)
block|{
case|case
name|XMLStreamReader
operator|.
name|START_ELEMENT
case|:
if|if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardNamespaceURIQName
operator|)
condition|)
block|{
if|if
condition|(
operator|!
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardLocalPartQName
operator|)
condition|)
block|{
return|return
name|nodeName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
break|break;
case|case
name|XMLStreamReader
operator|.
name|PROCESSING_INSTRUCTION
case|:
if|if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardLocalPartQName
operator|)
condition|)
block|{
return|return
name|nodeName
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|reader
operator|.
name|getPITarget
argument_list|()
argument_list|)
return|;
block|}
break|break;
block|}
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.NodeTest#isWildcardTest() 	 */
specifier|public
name|boolean
name|isWildcardTest
parameter_list|()
block|{
return|return
name|nodeName
operator|instanceof
name|QName
operator|.
name|PartialQName
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|NameTest
condition|)
block|{
specifier|final
name|NameTest
name|other
init|=
operator|(
name|NameTest
operator|)
name|obj
decl_stmt|;
return|return
name|other
operator|.
name|nodeType
operator|==
name|nodeType
operator|&&
name|other
operator|.
name|nodeName
operator|.
name|equals
argument_list|(
name|nodeName
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
name|nodeName
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#toString() 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
operator|(
name|nodeName
operator|instanceof
name|QName
operator|.
name|WildcardNamespaceURIQName
operator|)
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
name|nodeName
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

