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
name|bind
operator|.
name|annotation
operator|.
name|adapters
operator|.
name|CollapsedStringAdapter
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
name|adapters
operator|.
name|XmlJavaTypeAdapter
import|;
end_import

begin_comment
comment|/**  * Java class for anonymous complex type.  *   * The following schema fragment specifies the expected content contained within this class.  *   *<pre>  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;sequence&gt;  *&lt;element name="text" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;  *&lt;element name="variables" minOccurs="0"&gt;  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;sequence&gt;  *&lt;element name="variable"&gt;  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;sequence&gt;  *&lt;element name="qname"&gt;  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;sequence&gt;  *&lt;element name="localname" type="{http://www.w3.org/2001/XMLSchema}NCName"/&gt;  *&lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;  *&lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}NCName" minOccurs="0"/&gt;  *&lt;element ref="{http://exist-db.org/xquery/types/serialized}sequence"/&gt;  *&lt;/sequence&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *&lt;/element&gt;  *&lt;/sequence&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *&lt;/element&gt;  *&lt;/sequence&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *&lt;/element&gt;  *&lt;element name="properties" minOccurs="0"&gt;  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;sequence&gt;  *&lt;element name="property" maxOccurs="unbounded"&gt;  *&lt;complexType&gt;  *&lt;complexContent&gt;  *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;  *&lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;  *&lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *&lt;/element&gt;  *&lt;/sequence&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *&lt;/element&gt;  *&lt;/sequence&gt;  *&lt;attribute name="start" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;  *&lt;attribute name="max" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;  *&lt;attribute name="enclose" type="{http://exist.sourceforge.net/NS/exist}yesNo" /&gt;  *&lt;attribute name="wrap" type="{http://exist.sourceforge.net/NS/exist}yesNo" /&gt;  *&lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;  *&lt;attribute name="typed" type="{http://exist.sourceforge.net/NS/exist}yesNo" /&gt;  *&lt;attribute name="mime" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;  *&lt;attribute name="cache" type="{http://exist.sourceforge.net/NS/exist}yesNo" /&gt;  *&lt;attribute name="session" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;  *&lt;/restriction&gt;  *&lt;/complexContent&gt;  *&lt;/complexType&gt;  *</pre>  *   *   */
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
literal|"text"
block|,
literal|"variables"
block|,
literal|"properties"
block|}
argument_list|)
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"query"
argument_list|)
specifier|public
class|class
name|Query
block|{
annotation|@
name|XmlElement
argument_list|(
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|String
name|text
decl_stmt|;
specifier|protected
name|Query
operator|.
name|Variables
name|variables
decl_stmt|;
specifier|protected
name|Query
operator|.
name|Properties
name|properties
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"start"
argument_list|)
specifier|protected
name|Integer
name|start
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"max"
argument_list|)
specifier|protected
name|Integer
name|max
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"enclose"
argument_list|)
specifier|protected
name|YesNo
name|enclose
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"wrap"
argument_list|)
specifier|protected
name|YesNo
name|wrap
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"method"
argument_list|)
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|CollapsedStringAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlSchemaType
argument_list|(
name|name
operator|=
literal|"NCName"
argument_list|)
specifier|protected
name|String
name|method
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"typed"
argument_list|)
specifier|protected
name|YesNo
name|typed
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"mime"
argument_list|)
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|CollapsedStringAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlSchemaType
argument_list|(
name|name
operator|=
literal|"NCName"
argument_list|)
specifier|protected
name|String
name|mime
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"cache"
argument_list|)
specifier|protected
name|YesNo
name|cache
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"session"
argument_list|)
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|CollapsedStringAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlSchemaType
argument_list|(
name|name
operator|=
literal|"NCName"
argument_list|)
specifier|protected
name|String
name|session
decl_stmt|;
comment|/**      * Gets the value of the text property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
return|;
block|}
comment|/**      * Sets the value of the text property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setText
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the variables property.      *       * @return      *     possible object is      *     {@link Query.Variables }      *           */
specifier|public
name|Query
operator|.
name|Variables
name|getVariables
parameter_list|()
block|{
return|return
name|variables
return|;
block|}
comment|/**      * Sets the value of the variables property.      *       * @param value      *     allowed object is      *     {@link Query.Variables }      *           */
specifier|public
name|void
name|setVariables
parameter_list|(
name|Query
operator|.
name|Variables
name|value
parameter_list|)
block|{
name|this
operator|.
name|variables
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the properties property.      *       * @return      *     possible object is      *     {@link Query.Properties }      *           */
specifier|public
name|Query
operator|.
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|properties
return|;
block|}
comment|/**      * Sets the value of the properties property.      *       * @param value      *     allowed object is      *     {@link Query.Properties }      *           */
specifier|public
name|void
name|setProperties
parameter_list|(
name|Query
operator|.
name|Properties
name|value
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the start property.      *       * @return      *     possible object is      *     {@link Integer }      *           */
specifier|public
name|Integer
name|getStart
parameter_list|()
block|{
return|return
name|start
return|;
block|}
comment|/**      * Sets the value of the start property.      *       * @param value      *     allowed object is      *     {@link Integer }      *           */
specifier|public
name|void
name|setStart
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the max property.      *       * @return      *     possible object is      *     {@link Integer }      *           */
specifier|public
name|Integer
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
comment|/**      * Sets the value of the max property.      *       * @param value      *     allowed object is      *     {@link Integer }      *           */
specifier|public
name|void
name|setMax
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
name|this
operator|.
name|max
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the enclose property.      *       * @return      *     possible object is      *     {@link YesNo }      *           */
specifier|public
name|YesNo
name|getEnclose
parameter_list|()
block|{
return|return
name|enclose
return|;
block|}
comment|/**      * Sets the value of the enclose property.      *       * @param value      *     allowed object is      *     {@link YesNo }      *           */
specifier|public
name|void
name|setEnclose
parameter_list|(
name|YesNo
name|value
parameter_list|)
block|{
name|this
operator|.
name|enclose
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the wrap property.      *       * @return      *     possible object is      *     {@link YesNo }      *           */
specifier|public
name|YesNo
name|getWrap
parameter_list|()
block|{
return|return
name|wrap
return|;
block|}
comment|/**      * Sets the value of the wrap property.      *       * @param value      *     allowed object is      *     {@link YesNo }      *           */
specifier|public
name|void
name|setWrap
parameter_list|(
name|YesNo
name|value
parameter_list|)
block|{
name|this
operator|.
name|wrap
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the method property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|method
return|;
block|}
comment|/**      * Sets the value of the method property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setMethod
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|method
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the typed property.      *       * @return      *     possible object is      *     {@link YesNo }      *           */
specifier|public
name|YesNo
name|getTyped
parameter_list|()
block|{
return|return
name|typed
return|;
block|}
comment|/**      * Sets the value of the typed property.      *       * @param value      *     allowed object is      *     {@link YesNo }      *           */
specifier|public
name|void
name|setTyped
parameter_list|(
name|YesNo
name|value
parameter_list|)
block|{
name|this
operator|.
name|typed
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the mime property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getMime
parameter_list|()
block|{
return|return
name|mime
return|;
block|}
comment|/**      * Sets the value of the mime property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setMime
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|mime
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the cache property.      *       * @return      *     possible object is      *     {@link YesNo }      *           */
specifier|public
name|YesNo
name|getCache
parameter_list|()
block|{
return|return
name|cache
return|;
block|}
comment|/**      * Sets the value of the cache property.      *       * @param value      *     allowed object is      *     {@link YesNo }      *           */
specifier|public
name|void
name|setCache
parameter_list|(
name|YesNo
name|value
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Gets the value of the session property.      *       * @return      *     possible object is      *     {@link String }      *           */
specifier|public
name|String
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
comment|/**      * Sets the value of the session property.      *       * @param value      *     allowed object is      *     {@link String }      *           */
specifier|public
name|void
name|setSession
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Java class for anonymous complex type.      *       * The following schema fragment specifies the expected content contained within this class.      *       *<pre>      *&lt;complexType&gt;      *&lt;complexContent&gt;      *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;      *&lt;sequence&gt;      *&lt;element name="property" maxOccurs="unbounded"&gt;      *&lt;complexType&gt;      *&lt;complexContent&gt;      *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;      *&lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;      *&lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;      *&lt;/restriction&gt;      *&lt;/complexContent&gt;      *&lt;/complexType&gt;      *&lt;/element&gt;      *&lt;/sequence&gt;      *&lt;/restriction&gt;      *&lt;/complexContent&gt;      *&lt;/complexType&gt;      *</pre>      *       *       */
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
literal|"property"
block|}
argument_list|)
specifier|public
specifier|static
class|class
name|Properties
block|{
annotation|@
name|XmlElement
argument_list|(
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|List
argument_list|<
name|Query
operator|.
name|Properties
operator|.
name|Property
argument_list|>
name|property
decl_stmt|;
comment|/**          * Gets the value of the property property.          *          * This accessor method returns a reference to the live list,          * not a snapshot. Therefore any modification you make to the          * returned list will be present inside the JAXB object.          * This is why there is not a<CODE>set</CODE> method for the property property.          *          * For example, to add a new item, do as follows:          *<pre>          *    getProperty().add(newItem);          *</pre>          *           *          * Objects of the following type(s) are allowed in the list          * {@link Query.Properties.Property }          *          * @return the list of properties          *           */
specifier|public
name|List
argument_list|<
name|Query
operator|.
name|Properties
operator|.
name|Property
argument_list|>
name|getProperty
parameter_list|()
block|{
if|if
condition|(
name|property
operator|==
literal|null
condition|)
block|{
name|property
operator|=
operator|new
name|ArrayList
argument_list|<
name|Query
operator|.
name|Properties
operator|.
name|Property
argument_list|>
argument_list|()
expr_stmt|;
block|}
return|return
name|this
operator|.
name|property
return|;
block|}
comment|/**          * Java class for anonymous complex type.          *           * The following schema fragment specifies the expected content contained within this class.          *           *<pre>          *&lt;complexType&gt;          *&lt;complexContent&gt;          *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;          *&lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;          *&lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;          *&lt;/restriction&gt;          *&lt;/complexContent&gt;          *&lt;/complexType&gt;          *</pre>          *           *           */
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
argument_list|)
specifier|public
specifier|static
class|class
name|Property
block|{
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
literal|"value"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|String
name|value
decl_stmt|;
comment|/**              * Gets the value of the name property.              *               * @return              *     possible object is              *     {@link String }              *                   */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**              * Sets the value of the name property.              *               * @param value              *     allowed object is              *     {@link String }              *                   */
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
comment|/**              * Gets the value of the value property.              *               * @return              *     possible object is              *     {@link String }              *                   */
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/**              * Sets the value of the value property.              *               * @param value              *     allowed object is              *     {@link String }              *                   */
specifier|public
name|void
name|setValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Java class for anonymous complex type.      *       * The following schema fragment specifies the expected content contained within this class.      *       *<pre>      *&lt;complexType&gt;      *&lt;complexContent&gt;      *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;      *&lt;sequence&gt;      *&lt;element name="variable"&gt;      *&lt;complexType&gt;      *&lt;complexContent&gt;      *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;      *&lt;sequence&gt;      *&lt;element name="qname"&gt;      *&lt;complexType&gt;      *&lt;complexContent&gt;      *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;      *&lt;sequence&gt;      *&lt;element name="localname" type="{http://www.w3.org/2001/XMLSchema}NCName"/&gt;      *&lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;      *&lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}NCName" minOccurs="0"/&gt;      *&lt;element ref="{http://exist-db.org/xquery/types/serialized}sequence"/&gt;      *&lt;/sequence&gt;      *&lt;/restriction&gt;      *&lt;/complexContent&gt;      *&lt;/complexType&gt;      *&lt;/element&gt;      *&lt;/sequence&gt;      *&lt;/restriction&gt;      *&lt;/complexContent&gt;      *&lt;/complexType&gt;      *&lt;/element&gt;      *&lt;/sequence&gt;      *&lt;/restriction&gt;      *&lt;/complexContent&gt;      *&lt;/complexType&gt;      *</pre>      *       *       */
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
literal|"variable"
block|}
argument_list|)
specifier|public
specifier|static
class|class
name|Variables
block|{
annotation|@
name|XmlElement
argument_list|(
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|Query
operator|.
name|Variables
operator|.
name|Variable
name|variable
decl_stmt|;
comment|/**          * Gets the value of the variable property.          *           * @return          *     possible object is          *     {@link Query.Variables.Variable }          *               */
specifier|public
name|Query
operator|.
name|Variables
operator|.
name|Variable
name|getVariable
parameter_list|()
block|{
return|return
name|variable
return|;
block|}
comment|/**          * Sets the value of the variable property.          *           * @param value          *     allowed object is          *     {@link Query.Variables.Variable }          *               */
specifier|public
name|void
name|setVariable
parameter_list|(
name|Query
operator|.
name|Variables
operator|.
name|Variable
name|value
parameter_list|)
block|{
name|this
operator|.
name|variable
operator|=
name|value
expr_stmt|;
block|}
comment|/**          * Java class for anonymous complex type.          *           * The following schema fragment specifies the expected content contained within this class.          *           *<pre>          *&lt;complexType&gt;          *&lt;complexContent&gt;          *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;          *&lt;sequence&gt;          *&lt;element name="qname"&gt;          *&lt;complexType&gt;          *&lt;complexContent&gt;          *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;          *&lt;sequence&gt;          *&lt;element name="localname" type="{http://www.w3.org/2001/XMLSchema}NCName"/&gt;          *&lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;          *&lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}NCName" minOccurs="0"/&gt;          *&lt;element ref="{http://exist-db.org/xquery/types/serialized}sequence"/&gt;          *&lt;/sequence&gt;          *&lt;/restriction&gt;          *&lt;/complexContent&gt;          *&lt;/complexType&gt;          *&lt;/element&gt;          *&lt;/sequence&gt;          *&lt;/restriction&gt;          *&lt;/complexContent&gt;          *&lt;/complexType&gt;          *</pre>          *           *           */
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
literal|"qname"
block|}
argument_list|)
specifier|public
specifier|static
class|class
name|Variable
block|{
annotation|@
name|XmlElement
argument_list|(
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|Query
operator|.
name|Variables
operator|.
name|Variable
operator|.
name|Qname
name|qname
decl_stmt|;
comment|/**              * Gets the value of the qname property.              *               * @return              *     possible object is              *     {@link Query.Variables.Variable.Qname }              *                   */
specifier|public
name|Query
operator|.
name|Variables
operator|.
name|Variable
operator|.
name|Qname
name|getQname
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
comment|/**              * Sets the value of the qname property.              *               * @param value              *     allowed object is              *     {@link Query.Variables.Variable.Qname }              *                   */
specifier|public
name|void
name|setQname
parameter_list|(
name|Query
operator|.
name|Variables
operator|.
name|Variable
operator|.
name|Qname
name|value
parameter_list|)
block|{
name|this
operator|.
name|qname
operator|=
name|value
expr_stmt|;
block|}
comment|/**              * Java class for anonymous complex type.              *               * The following schema fragment specifies the expected content contained within this class.              *               *<pre>              *&lt;complexType&gt;              *&lt;complexContent&gt;              *&lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;              *&lt;sequence&gt;              *&lt;element name="localname" type="{http://www.w3.org/2001/XMLSchema}NCName"/&gt;              *&lt;element name="namespace" type="{http://www.w3.org/2001/XMLSchema}anyURI"/&gt;              *&lt;element name="prefix" type="{http://www.w3.org/2001/XMLSchema}NCName" minOccurs="0"/&gt;              *&lt;element ref="{http://exist-db.org/xquery/types/serialized}sequence"/&gt;              *&lt;/sequence&gt;              *&lt;/restriction&gt;              *&lt;/complexContent&gt;              *&lt;/complexType&gt;              *</pre>              *               *               */
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
literal|"localname"
block|,
literal|"namespace"
block|,
literal|"prefix"
block|,
literal|"sequence"
block|}
argument_list|)
specifier|public
specifier|static
class|class
name|Qname
block|{
annotation|@
name|XmlElement
argument_list|(
name|required
operator|=
literal|true
argument_list|)
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|CollapsedStringAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlSchemaType
argument_list|(
name|name
operator|=
literal|"NCName"
argument_list|)
specifier|protected
name|String
name|localname
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|required
operator|=
literal|true
argument_list|)
annotation|@
name|XmlSchemaType
argument_list|(
name|name
operator|=
literal|"anyURI"
argument_list|)
specifier|protected
name|String
name|namespace
decl_stmt|;
annotation|@
name|XmlJavaTypeAdapter
argument_list|(
name|CollapsedStringAdapter
operator|.
name|class
argument_list|)
annotation|@
name|XmlSchemaType
argument_list|(
name|name
operator|=
literal|"NCName"
argument_list|)
specifier|protected
name|String
name|prefix
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|namespace
operator|=
literal|"http://exist-db.org/xquery/types/serialized"
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|protected
name|Sequence
name|sequence
decl_stmt|;
comment|/**                  * Gets the value of the localname property.                  *                   * @return                  *     possible object is                  *     {@link String }                  *                       */
specifier|public
name|String
name|getLocalname
parameter_list|()
block|{
return|return
name|localname
return|;
block|}
comment|/**                  * Sets the value of the localname property.                  *                   * @param value                  *     allowed object is                  *     {@link String }                  *                       */
specifier|public
name|void
name|setLocalname
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|localname
operator|=
name|value
expr_stmt|;
block|}
comment|/**                  * Gets the value of the namespace property.                  *                   * @return                  *     possible object is                  *     {@link String }                  *                       */
specifier|public
name|String
name|getNamespace
parameter_list|()
block|{
return|return
name|namespace
return|;
block|}
comment|/**                  * Sets the value of the namespace property.                  *                   * @param value                  *     allowed object is                  *     {@link String }                  *                       */
specifier|public
name|void
name|setNamespace
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|namespace
operator|=
name|value
expr_stmt|;
block|}
comment|/**                  * Gets the value of the prefix property.                  *                   * @return                  *     possible object is                  *     {@link String }                  *                       */
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
comment|/**                  * Sets the value of the prefix property.                  *                   * @param value                  *     allowed object is                  *     {@link String }                  *                       */
specifier|public
name|void
name|setPrefix
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|prefix
operator|=
name|value
expr_stmt|;
block|}
comment|/**                  * Gets the value of the sequence property.                  *                   * @return                  *     possible object is                  *     {@link Sequence }                  *                       */
specifier|public
name|Sequence
name|getSequence
parameter_list|()
block|{
return|return
name|sequence
return|;
block|}
comment|/**                  * Sets the value of the sequence property.                  *                   * @param value                  *     allowed object is                  *     {@link Sequence }                  *                       */
specifier|public
name|void
name|setSequence
parameter_list|(
name|Sequence
name|value
parameter_list|)
block|{
name|this
operator|.
name|sequence
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

