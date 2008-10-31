begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
name|*
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

begin_comment
comment|/**  * A qualified name, consisting of a namespace and a local name.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|QName
extends|extends
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
implements|implements
name|Comparable
argument_list|<
name|QName
argument_list|>
block|{
specifier|private
specifier|final
name|String
name|tag
decl_stmt|;
comment|/** 	 * Create a qualified name. 	 * 	 * @param namespace the namespace of the qualified name,<code>null</code> if none 	 * @param localName the local part of the qualified name, must not be<code>null</code> or empty 	 * @param prefix the prefix to use for the qualified name,<code>null</code> if default (empty) prefix 	 */
specifier|public
name|QName
parameter_list|(
name|String
name|namespace
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|super
argument_list|(
name|namespace
operator|==
literal|null
condition|?
name|XMLConstants
operator|.
name|NULL_NS_URI
else|:
name|namespace
argument_list|,
name|localName
argument_list|,
name|prefix
operator|==
literal|null
condition|?
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
else|:
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|localName
operator|==
literal|null
operator|||
name|localName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null or empty local name"
argument_list|)
throw|;
if|if
condition|(
name|prefix
operator|==
literal|null
operator|||
name|prefix
operator|.
name|equals
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
condition|)
block|{
name|tag
operator|=
name|localName
expr_stmt|;
block|}
else|else
block|{
name|tag
operator|=
name|prefix
operator|+
literal|":"
operator|+
name|localName
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|QName
name|o
parameter_list|)
block|{
return|return
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Return whether this qualified name is actually qualified by a namespace or not. 	 * 	 * @return<code>true</code> if the qualified name has a namespace set,<code>false</code> if it's just a local name 	 */
specifier|public
name|boolean
name|hasNamespace
parameter_list|()
block|{
return|return
operator|!
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
return|;
block|}
comment|/** 	 * Create an element in the given document whose tag is this qualified name.  Correctly calls 	 *<code>createElement</code> or<code>createElementNS</code> depending on whether 	 * this name is actually qualified or not. 	 *  	 * @param doc the document to use to create the element 	 * @return a new element whose tag is this qualified name 	 */
specifier|public
name|Element
name|createElement
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
if|if
condition|(
name|hasNamespace
argument_list|()
condition|)
return|return
name|doc
operator|.
name|createElementNS
argument_list|(
name|getNamespaceURI
argument_list|()
argument_list|,
name|tag
argument_list|)
return|;
return|return
name|doc
operator|.
name|createElement
argument_list|(
name|tag
argument_list|)
return|;
block|}
comment|/** 	 * Create an attribute in the given document whose name is this qualified name.  Correctly calls 	 *<code>createAttribute</code> or<code>createAttributeNS</code> depending on whether 	 * this name is actually qualified or not. 	 *  	 * @param doc the document to use to create the attribute 	 * @return a new attribute whose name is this qualified name 	 */
specifier|public
name|Attr
name|createAttribute
parameter_list|(
name|Document
name|doc
parameter_list|)
block|{
if|if
condition|(
name|hasNamespace
argument_list|()
condition|)
return|return
name|doc
operator|.
name|createAttributeNS
argument_list|(
name|getNamespaceURI
argument_list|()
argument_list|,
name|tag
argument_list|)
return|;
return|return
name|doc
operator|.
name|createAttribute
argument_list|(
name|tag
argument_list|)
return|;
block|}
comment|/** 	 * Set an attribute value on the given element, where the attribute's name is this qualified name. 	 * Correctly calls<code>setAttribute</code> or<code>setAttributeNS</code> depending on whether 	 * this name is actually qualified or not. 	 * 	 * @param elem the element on which to set the attribute 	 * @param value the value of the attribute 	 */
specifier|public
name|void
name|setAttribute
parameter_list|(
name|Element
name|elem
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|hasNamespace
argument_list|()
condition|)
block|{
name|elem
operator|.
name|setAttributeNS
argument_list|(
name|getNamespaceURI
argument_list|()
argument_list|,
name|tag
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|elem
operator|.
name|setAttribute
argument_list|(
name|tag
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Get the attribute with this qualified name from the given element.  Correctly calls 	 *<code>getAttributeNode</code> or<code>getAttributeNodeNS</code> depending on whether 	 * this name is actually qualified or not. 	 * 	 * @param elem the element to read the attribute from 	 * @return the attribute node with this qualified name 	 */
specifier|public
name|Attr
name|getAttributeNode
parameter_list|(
name|Element
name|elem
parameter_list|)
block|{
if|if
condition|(
name|hasNamespace
argument_list|()
condition|)
return|return
name|elem
operator|.
name|getAttributeNodeNS
argument_list|(
name|getNamespaceURI
argument_list|()
argument_list|,
name|getLocalPart
argument_list|()
argument_list|)
return|;
return|return
name|elem
operator|.
name|getAttributeNode
argument_list|(
name|tag
argument_list|)
return|;
block|}
comment|/** 	 * Return the qualified name of the given node. 	 * 	 * @param node the target node 	 * @return the node's qualified name 	 */
specifier|public
specifier|static
name|QName
name|of
parameter_list|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
name|node
parameter_list|)
block|{
name|String
name|localName
init|=
name|node
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|localName
operator|==
literal|null
condition|)
name|localName
operator|=
name|node
operator|.
name|getNodeName
argument_list|()
expr_stmt|;
return|return
operator|new
name|QName
argument_list|(
name|node
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|localName
argument_list|,
name|node
operator|.
name|getPrefix
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Parse the given tag into a qualified name within the context of the given namespace bindings. 	 * 	 * @param tag the tag to parse, in standard XML format 	 * @param namespaces the namespace bindings to use 	 * @return the qualified name of the given tag 	 */
specifier|public
specifier|static
name|QName
name|parse
parameter_list|(
name|String
name|tag
parameter_list|,
name|NamespaceMap
name|namespaces
parameter_list|)
block|{
return|return
name|parse
argument_list|(
name|tag
argument_list|,
name|namespaces
argument_list|,
name|namespaces
operator|.
name|get
argument_list|(
literal|""
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 * Parse the given tag into a qualified name within the context of the given namespace bindings, 	 * overriding the default namespace binding with the given one.  This is useful for parsing 	 * attribute names, where a lack of prefix should be interpreted as no namespace rather 	 * than the default namespace currently in effect. 	 * 	 * @param tag the tag to parse, in standard XML format 	 * @param namespaces the namespace bindings to use 	 * @param defaultNamespace the URI to use as the default namespace, in preference to any specified in the namespace bindings 	 * @return the qualified name of the given tag 	 */
specifier|public
specifier|static
name|QName
name|parse
parameter_list|(
name|String
name|tag
parameter_list|,
name|NamespaceMap
name|namespaces
parameter_list|,
name|String
name|defaultNamespace
parameter_list|)
block|{
name|int
name|colonIndex
init|=
name|tag
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|colonIndex
operator|==
literal|0
operator|||
name|colonIndex
operator|==
name|tag
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"illegal tag syntax '"
operator|+
name|tag
operator|+
literal|"'"
argument_list|)
throw|;
name|String
name|prefix
init|=
name|colonIndex
operator|==
operator|-
literal|1
condition|?
literal|""
else|:
name|tag
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|colonIndex
argument_list|)
decl_stmt|;
name|String
name|ns
init|=
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|namespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
else|:
name|defaultNamespace
decl_stmt|;
if|if
condition|(
name|ns
operator|==
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no namespace registered for tag prefix '"
operator|+
name|prefix
operator|+
literal|"'"
argument_list|)
throw|;
return|return
operator|new
name|QName
argument_list|(
name|ns
argument_list|,
name|tag
operator|.
name|substring
argument_list|(
name|colonIndex
operator|+
literal|1
argument_list|)
argument_list|,
name|prefix
argument_list|)
return|;
block|}
block|}
end_class

end_unit

