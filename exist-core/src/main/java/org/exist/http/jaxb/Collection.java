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
name|XmlSchemaType
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
name|datatype
operator|.
name|XMLGregorianCalendar
import|;
end_import

begin_comment
comment|/**  * Java class for anonymous complex type.  *   * The following schema fragment specifies the expected content contained within this class.  *   *<pre>  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;sequence&gt;  *&lt;element ref="{http://exist.sourceforge.net/NS/exist}collection" maxOccurs="unbounded" minOccurs="0"/&gt;  *&lt;/sequence&gt;  *&lt;attGroup ref="{http://exist.sourceforge.net/NS/exist}collectionAttrs"/&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *</pre>  */
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
literal|"collection"
block|}
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"collection"
argument_list|)
specifier|public
class|class
name|Collection
block|{
specifier|protected
name|List
argument_list|<
name|Collection
argument_list|>
name|collection
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"name"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|String
name|name
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"created"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
annotation|@
name|XmlSchemaType
argument_list|(
name|name
operator|=
literal|"dateTime"
argument_list|)
specifier|protected
name|XMLGregorianCalendar
name|created
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"owner"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|String
name|owner
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"group"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|String
name|group
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"permissions"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|String
name|permissions
decl_stmt|;
comment|/**      * Gets the value of the collection property.      *       *      * This accessor method returns a reference to the live list,      * not a snapshot. Therefore any modification you make to the      * returned list will be present inside the JAXB object.      * This is why there is not a<CODE>set</CODE> method for the collection property.      *       *      * For example, to add a new item, do as follows:      *<pre>      *    getCollection().add(newItem);      *</pre>      *       *       *      * Objects of the following type(s) are allowed in the list      * {@link Collection }      *       * @return the collections.      */
specifier|public
name|List
argument_list|<
name|Collection
argument_list|>
name|getCollection
parameter_list|()
block|{
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|collection
operator|=
operator|new
name|ArrayList
argument_list|<
name|Collection
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|collection
return|;
block|}
comment|/**      * Gets the value of the name property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**      * Sets the value of the name property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the created property.      *       * @return      *     possible object is      *     {@link XMLGregorianCalendar }      *           */
specifier|public
name|XMLGregorianCalendar
name|getCreated
parameter_list|()
block|{
return|return
name|created
return|;
block|}
comment|/**      * Sets the value of the created property.      *       * @param value      *     allowed object is      *     {@link XMLGregorianCalendar }      *           */
specifier|public
name|void
name|setCreated
parameter_list|(
name|XMLGregorianCalendar
name|value
parameter_list|)
block|{
name|this
operator|.
name|created
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the owner property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
comment|/**      * Sets the value of the owner property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the group property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
comment|/**      * Sets the value of the group property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setGroup
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|group
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the permissions property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getPermissions
parameter_list|()
block|{
return|return
name|permissions
return|;
block|}
comment|/**      * Sets the value of the permissions property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|permissions
operator|=
name|value
expr_stmt|;
block|}
block|}
end_class

end_unit

