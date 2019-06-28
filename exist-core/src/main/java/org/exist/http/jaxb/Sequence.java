begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|//
end_comment

begin_comment
comment|// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802
end_comment

begin_comment
comment|// See<a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
end_comment

begin_comment
comment|// Any modifications to this file will be lost upon recompilation of the source schema.
end_comment

begin_comment
comment|// Generated on: 2017.12.01 at 08:56:54 PM GMT
end_comment

begin_comment
comment|//
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|jaxb
package|;
end_package

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
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAttribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlValue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_comment
comment|/**  * Java class for anonymous complex type.  *   * The following schema fragment specifies the expected content contained within this class.  *   *<pre>  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;sequence&gt;  *&lt;element name="value" maxOccurs="unbounded" minOccurs="0"&gt;  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" /&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *&lt;/element&gt;  *&lt;/sequence&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *</pre>  *   *   */
end_comment

begin_class
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlType
argument_list|(
name|name
operator|=
literal|""
argument_list|,
name|propOrder
operator|=
block|{
literal|"value"
block|}
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"sequence"
argument_list|,
name|namespace
operator|=
literal|"http://exist-db.org/xquery/types/serialized"
argument_list|)
specifier|public
class|class
name|Sequence
block|{
annotation|@
name|XmlElement
argument_list|(
name|namespace
operator|=
literal|"http://exist-db.org/xquery/types/serialized"
argument_list|)
specifier|protected
name|List
argument_list|<
name|Sequence
operator|.
name|Value
argument_list|>
name|value
decl_stmt|;
comment|/**      * Gets the value of the value property.      *      * This accessor method returns a reference to the live list,      * not a snapshot. Therefore any modification you make to the      * returned list will be present inside the JAXB object.      * This is why there is not a<CODE>set</CODE> method for the value property.      *      * For example, to add a new item, do as follows:      *<pre>      *    getValue().add(newItem);      *</pre>      *       *      * Objects of the following type(s) are allowed in the list      * {@link Sequence.Value }      *       *       */
specifier|public
name|List
argument_list|<
name|Sequence
operator|.
name|Value
argument_list|>
name|getValue
parameter_list|()
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|ArrayList
argument_list|<
name|Sequence
operator|.
name|Value
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|value
return|;
block|}
comment|/**      * Java class for anonymous complex type.      *       * The following schema fragment specifies the expected content contained within this class.      *       *<pre>      *&lt;complexType&gt;      *&lt;complexContent&gt;      *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;      *&lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" /&gt;      *&lt;/restriction&gt;      *&lt;/complexContent&gt;      *&lt;/complexType&gt;      *</pre>      *       *       */
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
annotation|@
name|XmlType
argument_list|(
name|name
operator|=
literal|""
argument_list|,
name|propOrder
operator|=
block|{
literal|"content"
block|}
argument_list|)
specifier|public
specifier|static
class|class
name|Value
block|{
annotation|@
name|XmlValue
specifier|protected
name|String
name|content
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"type"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|QName
name|type
decl_stmt|;
comment|/**          * Gets the value of the content property.          *           * @return          *     possible object is          *     {@link String }          *               */
specifier|public
name|String
name|getContent
parameter_list|()
block|{
return|return
name|content
return|;
block|}
comment|/**          * Sets the value of the content property.          *           * @param value          *     allowed object is          *     {@link String }          *               */
specifier|public
name|void
name|setContent
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|value
expr_stmt|;
block|}
comment|/**          * Gets the value of the type property.          *           * @return          *     possible object is          *     {@link QName }          *               */
specifier|public
name|QName
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**          * Sets the value of the type property.          *           * @param value          *     allowed object is          *     {@link QName }          *               */
specifier|public
name|void
name|setType
parameter_list|(
name|QName
name|value
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

