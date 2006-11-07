begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * DOM.java  *  * Created on June 20, 2006, 12:31 PM  *  * (C) R. Alexander Milowski alex@milowski.com  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|util
package|;
end_package

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

begin_comment
comment|/**  *  * @author R. Alexander Milowski  */
end_comment

begin_class
specifier|public
class|class
name|DOM
block|{
comment|/** Creates a new instance of DOM */
specifier|private
name|DOM
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|void
name|forEachChild
parameter_list|(
name|Element
name|parent
parameter_list|,
name|NodeHandler
name|filter
parameter_list|)
block|{
name|Node
name|current
init|=
name|parent
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|Node
name|toProcess
init|=
name|current
decl_stmt|;
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|filter
operator|.
name|process
argument_list|(
name|parent
argument_list|,
name|toProcess
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|findChildren
parameter_list|(
name|Element
name|parent
parameter_list|,
name|String
name|namespaceName
parameter_list|,
name|String
name|localName
parameter_list|,
name|NodeHandler
name|filter
parameter_list|)
block|{
name|Node
name|current
init|=
name|parent
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|(
name|namespaceName
operator|==
literal|null
operator|&&
name|current
operator|.
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
operator|)
operator|||
operator|(
name|namespaceName
operator|!=
literal|null
operator|&&
operator|!
name|namespaceName
operator|.
name|equals
argument_list|(
name|current
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|current
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
name|Node
name|toProcess
init|=
name|current
decl_stmt|;
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|filter
operator|.
name|process
argument_list|(
name|parent
argument_list|,
name|toProcess
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|Element
name|findChild
parameter_list|(
name|Element
name|parent
parameter_list|,
name|String
name|namespaceName
parameter_list|,
name|String
name|localName
parameter_list|)
block|{
name|Node
name|current
init|=
name|parent
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|current
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|(
name|namespaceName
operator|==
literal|null
operator|&&
name|current
operator|.
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
operator|)
operator|||
operator|(
name|namespaceName
operator|!=
literal|null
operator|&&
operator|!
name|namespaceName
operator|.
name|equals
argument_list|(
name|current
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|current
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
condition|)
block|{
return|return
operator|(
name|Element
operator|)
name|current
return|;
block|}
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|Element
name|replaceTextElement
parameter_list|(
name|Element
name|parent
parameter_list|,
name|String
name|namespaceName
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|firstChild
parameter_list|)
block|{
return|return
name|DOM
operator|.
name|replaceTextElement
argument_list|(
name|parent
argument_list|,
name|namespaceName
argument_list|,
name|localName
argument_list|,
name|value
argument_list|,
name|firstChild
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Element
name|replaceTextElement
parameter_list|(
name|Element
name|parent
parameter_list|,
name|String
name|namespaceName
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|firstChild
parameter_list|,
name|boolean
name|wrap
parameter_list|)
block|{
name|Element
name|textE
init|=
name|DOM
operator|.
name|findChild
argument_list|(
name|parent
argument_list|,
name|namespaceName
argument_list|,
name|localName
argument_list|)
decl_stmt|;
if|if
condition|(
name|textE
operator|==
literal|null
condition|)
block|{
name|textE
operator|=
name|parent
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createElementNS
argument_list|(
name|namespaceName
argument_list|,
name|localName
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstChild
condition|)
block|{
if|if
condition|(
name|wrap
condition|)
block|{
name|parent
operator|.
name|insertBefore
argument_list|(
name|parent
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createTextNode
argument_list|(
literal|"\n"
argument_list|)
argument_list|,
name|parent
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|insertBefore
argument_list|(
name|textE
argument_list|,
name|parent
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|appendChild
argument_list|(
name|textE
argument_list|)
expr_stmt|;
if|if
condition|(
name|wrap
condition|)
block|{
name|parent
operator|.
name|appendChild
argument_list|(
name|parent
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createTextNode
argument_list|(
literal|"\n"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|DOM
operator|.
name|removeChildren
argument_list|(
name|textE
argument_list|)
expr_stmt|;
name|textE
operator|.
name|appendChild
argument_list|(
name|parent
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createTextNode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|textE
return|;
block|}
specifier|public
specifier|static
name|void
name|replaceText
parameter_list|(
name|Element
name|textE
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|DOM
operator|.
name|removeChildren
argument_list|(
name|textE
argument_list|)
expr_stmt|;
name|textE
operator|.
name|appendChild
argument_list|(
name|textE
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createTextNode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|removeChildren
parameter_list|(
name|Element
name|parent
parameter_list|)
block|{
name|Node
name|current
init|=
name|parent
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|Node
name|toRemove
init|=
name|current
decl_stmt|;
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|parent
operator|.
name|removeChild
argument_list|(
name|toRemove
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|String
name|textContent
parameter_list|(
name|Node
name|n
parameter_list|)
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
name|StringBuffer
name|builder
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|Node
name|current
init|=
name|n
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
name|int
name|type
init|=
name|current
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|current
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|n
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

