begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * IndexedElement.java  *  * This file was auto-generated from WSDL  * by the Apache Axis 1.2.1 Jun 14, 2005 (09:15:57 EDT) WSDL2Java emitter.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
import|;
end_import

begin_class
specifier|public
class|class
name|IndexedElement
implements|implements
name|java
operator|.
name|io
operator|.
name|Serializable
block|{
specifier|private
name|java
operator|.
name|lang
operator|.
name|String
name|localName
decl_stmt|;
specifier|private
name|java
operator|.
name|lang
operator|.
name|String
name|namespaceURI
decl_stmt|;
specifier|private
name|java
operator|.
name|lang
operator|.
name|String
name|prefix
decl_stmt|;
specifier|private
name|int
name|occurences
decl_stmt|;
specifier|public
name|IndexedElement
parameter_list|()
block|{
block|}
specifier|public
name|IndexedElement
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|localName
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|namespaceURI
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|String
name|prefix
parameter_list|,
name|int
name|occurences
parameter_list|)
block|{
name|this
operator|.
name|localName
operator|=
name|localName
expr_stmt|;
name|this
operator|.
name|namespaceURI
operator|=
name|namespaceURI
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|occurences
operator|=
name|occurences
expr_stmt|;
block|}
comment|/**      * Gets the localName value for this IndexedElement.      *       * @return localName      */
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|localName
return|;
block|}
comment|/**      * Sets the localName value for this IndexedElement.      *       * @param localName      */
specifier|public
name|void
name|setLocalName
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|localName
parameter_list|)
block|{
name|this
operator|.
name|localName
operator|=
name|localName
expr_stmt|;
block|}
comment|/**      * Gets the namespaceURI value for this IndexedElement.      *       * @return namespaceURI      */
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|namespaceURI
return|;
block|}
comment|/**      * Sets the namespaceURI value for this IndexedElement.      *       * @param namespaceURI      */
specifier|public
name|void
name|setNamespaceURI
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|namespaceURI
parameter_list|)
block|{
name|this
operator|.
name|namespaceURI
operator|=
name|namespaceURI
expr_stmt|;
block|}
comment|/**      * Gets the prefix value for this IndexedElement.      *       * @return prefix      */
specifier|public
name|java
operator|.
name|lang
operator|.
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
comment|/**      * Sets the prefix value for this IndexedElement.      *       * @param prefix      */
specifier|public
name|void
name|setPrefix
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|prefix
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
comment|/**      * Gets the occurences value for this IndexedElement.      *       * @return occurences      */
specifier|public
name|int
name|getOccurences
parameter_list|()
block|{
return|return
name|occurences
return|;
block|}
comment|/**      * Sets the occurences value for this IndexedElement.      *       * @param occurences      */
specifier|public
name|void
name|setOccurences
parameter_list|(
name|int
name|occurences
parameter_list|)
block|{
name|this
operator|.
name|occurences
operator|=
name|occurences
expr_stmt|;
block|}
specifier|private
name|java
operator|.
name|lang
operator|.
name|Object
name|__equalsCalc
init|=
literal|null
decl_stmt|;
specifier|public
specifier|synchronized
name|boolean
name|equals
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|IndexedElement
operator|)
condition|)
return|return
literal|false
return|;
name|IndexedElement
name|other
init|=
operator|(
name|IndexedElement
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|__equalsCalc
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|__equalsCalc
operator|==
name|obj
operator|)
return|;
block|}
name|__equalsCalc
operator|=
name|obj
expr_stmt|;
name|boolean
name|_equals
decl_stmt|;
name|_equals
operator|=
literal|true
operator|&&
operator|(
operator|(
name|this
operator|.
name|localName
operator|==
literal|null
operator|&&
name|other
operator|.
name|getLocalName
argument_list|()
operator|==
literal|null
operator|)
operator|||
operator|(
name|this
operator|.
name|localName
operator|!=
literal|null
operator|&&
name|this
operator|.
name|localName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|)
operator|)
operator|&&
operator|(
operator|(
name|this
operator|.
name|namespaceURI
operator|==
literal|null
operator|&&
name|other
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
operator|)
operator|||
operator|(
name|this
operator|.
name|namespaceURI
operator|!=
literal|null
operator|&&
name|this
operator|.
name|namespaceURI
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|)
operator|)
operator|&&
operator|(
operator|(
name|this
operator|.
name|prefix
operator|==
literal|null
operator|&&
name|other
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
operator|)
operator|||
operator|(
name|this
operator|.
name|prefix
operator|!=
literal|null
operator|&&
name|this
operator|.
name|prefix
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getPrefix
argument_list|()
argument_list|)
operator|)
operator|)
operator|&&
name|this
operator|.
name|occurences
operator|==
name|other
operator|.
name|getOccurences
argument_list|()
expr_stmt|;
name|__equalsCalc
operator|=
literal|null
expr_stmt|;
return|return
name|_equals
return|;
block|}
specifier|private
name|boolean
name|__hashCodeCalc
init|=
literal|false
decl_stmt|;
specifier|public
specifier|synchronized
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|__hashCodeCalc
condition|)
block|{
return|return
literal|0
return|;
block|}
name|__hashCodeCalc
operator|=
literal|true
expr_stmt|;
name|int
name|_hashCode
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|getLocalName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|_hashCode
operator|+=
name|getLocalName
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|_hashCode
operator|+=
name|getNamespaceURI
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getPrefix
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|_hashCode
operator|+=
name|getPrefix
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
name|_hashCode
operator|+=
name|getOccurences
argument_list|()
expr_stmt|;
name|__hashCodeCalc
operator|=
literal|false
expr_stmt|;
return|return
name|_hashCode
return|;
block|}
comment|// Type metadata
specifier|private
specifier|static
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|TypeDesc
name|typeDesc
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|TypeDesc
argument_list|(
name|IndexedElement
operator|.
name|class
argument_list|,
literal|true
argument_list|)
decl_stmt|;
static|static
block|{
name|typeDesc
operator|.
name|setXmlType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"IndexedElement"
argument_list|)
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ElementDesc
name|elemField
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ElementDesc
argument_list|()
decl_stmt|;
name|elemField
operator|.
name|setFieldName
argument_list|(
literal|"localName"
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"localName"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
name|Namespaces
operator|.
name|SCHEMA_NS
argument_list|,
literal|"string"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setNillable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|typeDesc
operator|.
name|addFieldDesc
argument_list|(
name|elemField
argument_list|)
expr_stmt|;
name|elemField
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ElementDesc
argument_list|()
expr_stmt|;
name|elemField
operator|.
name|setFieldName
argument_list|(
literal|"namespaceURI"
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"namespaceURI"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
name|Namespaces
operator|.
name|SCHEMA_NS
argument_list|,
literal|"string"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setNillable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|typeDesc
operator|.
name|addFieldDesc
argument_list|(
name|elemField
argument_list|)
expr_stmt|;
name|elemField
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ElementDesc
argument_list|()
expr_stmt|;
name|elemField
operator|.
name|setFieldName
argument_list|(
literal|"prefix"
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"prefix"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
name|Namespaces
operator|.
name|SCHEMA_NS
argument_list|,
literal|"string"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setNillable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|typeDesc
operator|.
name|addFieldDesc
argument_list|(
name|elemField
argument_list|)
expr_stmt|;
name|elemField
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|ElementDesc
argument_list|()
expr_stmt|;
name|elemField
operator|.
name|setFieldName
argument_list|(
literal|"occurences"
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlName
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
literal|"urn:exist"
argument_list|,
literal|"occurences"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setXmlType
argument_list|(
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
name|Namespaces
operator|.
name|SCHEMA_NS
argument_list|,
literal|"int"
argument_list|)
argument_list|)
expr_stmt|;
name|elemField
operator|.
name|setNillable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|typeDesc
operator|.
name|addFieldDesc
argument_list|(
name|elemField
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return type metadata object      */
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|description
operator|.
name|TypeDesc
name|getTypeDesc
parameter_list|()
block|{
return|return
name|typeDesc
return|;
block|}
comment|/**      * Get Custom Serializer      */
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|encoding
operator|.
name|Serializer
name|getSerializer
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|mechType
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|Class
name|_javaType
parameter_list|,
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|_xmlType
parameter_list|)
block|{
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|encoding
operator|.
name|ser
operator|.
name|BeanSerializer
argument_list|(
name|_javaType
argument_list|,
name|_xmlType
argument_list|,
name|typeDesc
argument_list|)
return|;
block|}
comment|/**      * Get Custom Deserializer      */
specifier|public
specifier|static
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|encoding
operator|.
name|Deserializer
name|getDeserializer
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|String
name|mechType
parameter_list|,
name|java
operator|.
name|lang
operator|.
name|Class
name|_javaType
parameter_list|,
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|_xmlType
parameter_list|)
block|{
return|return
operator|new
name|org
operator|.
name|apache
operator|.
name|axis
operator|.
name|encoding
operator|.
name|ser
operator|.
name|BeanDeserializer
argument_list|(
name|_javaType
argument_list|,
name|_xmlType
argument_list|,
name|typeDesc
argument_list|)
return|;
block|}
block|}
end_class

end_unit

