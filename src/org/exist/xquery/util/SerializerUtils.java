begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|ExtendedXMLStreamReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
operator|.
name|EXistOutputKeys
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
name|ErrorCodes
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
name|Expression
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
name|functions
operator|.
name|fn
operator|.
name|FnModule
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
name|functions
operator|.
name|map
operator|.
name|MapType
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
name|*
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
name|XMLStreamException
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Serializer utilities used by several XQuery functions.  */
end_comment

begin_class
specifier|public
class|class
name|SerializerUtils
block|{
comment|/**      * See https://www.w3.org/TR/xpath-functions-31/#func-serialize      */
specifier|public
enum|enum
name|ParameterConvention
block|{
name|ALLOW_DUPLICATE_NAMES
argument_list|(
literal|"allow-duplicate-names"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|BooleanValue
operator|.
name|FALSE
argument_list|)
block|,
name|BYTE_ORDER_MARK
argument_list|(
literal|"byte-order-mark"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|BooleanValue
operator|.
name|FALSE
argument_list|)
block|,
name|CDATA_SECTION_ELEMENTS
argument_list|(
name|OutputKeys
operator|.
name|CDATA_SECTION_ELEMENTS
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
name|DOCTYPE_PUBLIC
argument_list|(
name|OutputKeys
operator|.
name|DOCTYPE_PUBLIC
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
comment|//default: () means "absent"
name|DOCTYPE_SYSTEM
argument_list|(
name|OutputKeys
operator|.
name|DOCTYPE_SYSTEM
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
comment|//default: () means "absent"
name|ENCODING
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"utf-8"
argument_list|)
argument_list|)
block|,
name|ESCAPE_URI_ATTRIBUTES
argument_list|(
literal|"escape-uri-attributes"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|BooleanValue
operator|.
name|TRUE
argument_list|)
block|,
name|HTML_VERSION
argument_list|(
name|EXistOutputKeys
operator|.
name|HTML_VERSION
argument_list|,
name|Type
operator|.
name|DECIMAL
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
operator|new
name|DecimalValue
argument_list|(
literal|5
argument_list|)
argument_list|)
block|,
name|INCLUDE_CONTENT_TYPE
argument_list|(
literal|"include-content-type"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|BooleanValue
operator|.
name|TRUE
argument_list|)
block|,
name|INDENT
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|BooleanValue
operator|.
name|FALSE
argument_list|)
block|,
name|ITEM_SEPARATOR
argument_list|(
name|EXistOutputKeys
operator|.
name|ITEM_SEPARATOR
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
comment|//default: () means "absent"
name|JSON_NODE_OUTPUT_METHOD
argument_list|(
name|EXistOutputKeys
operator|.
name|JSON_NODE_OUTPUT_METHOD
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"xml"
argument_list|)
argument_list|)
block|,
name|MEDIA_TYPE
argument_list|(
name|OutputKeys
operator|.
name|MEDIA_TYPE
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
comment|// default: a media type suitable for the chosen method
name|METHOD
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"xml"
argument_list|)
argument_list|)
block|,
name|NORMALIZATION_FORM
argument_list|(
literal|"normalization-form"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"none"
argument_list|)
argument_list|)
block|,
name|OMIT_XML_DECLARATION
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|BooleanValue
operator|.
name|TRUE
argument_list|)
block|,
name|STANDALONE
argument_list|(
name|OutputKeys
operator|.
name|STANDALONE
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
comment|//default: () means "omit"
name|SUPPRESS_INDENTATION
argument_list|(
literal|"suppress-indentation"
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
name|UNDECLARE_PREFIXES
argument_list|(
literal|"undeclare-prefixes"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|BooleanValue
operator|.
name|FALSE
argument_list|)
block|,
name|USE_CHARACTER_MAPS
argument_list|(
literal|"use-character-maps"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
block|,
name|VERSION
argument_list|(
name|OutputKeys
operator|.
name|VERSION
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
operator|new
name|StringValue
argument_list|(
literal|"1.0"
argument_list|)
argument_list|)
block|;
specifier|final
name|String
name|parameterName
decl_stmt|;
specifier|final
name|int
name|type
decl_stmt|;
specifier|final
name|int
name|cardinality
decl_stmt|;
specifier|final
name|Sequence
name|defaultValue
decl_stmt|;
name|ParameterConvention
parameter_list|(
specifier|final
name|String
name|parameterName
parameter_list|,
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|int
name|cardinality
parameter_list|,
specifier|final
name|Sequence
name|defaultValue
parameter_list|)
block|{
name|this
operator|.
name|parameterName
operator|=
name|parameterName
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|cardinality
operator|=
name|cardinality
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|ParameterConvention
argument_list|>
name|PARAMETER_CONVENTIONS_BY_NAME
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
specifier|final
name|ParameterConvention
name|parameterConvention
range|:
name|ParameterConvention
operator|.
name|values
argument_list|()
control|)
block|{
name|PARAMETER_CONVENTIONS_BY_NAME
operator|.
name|put
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
name|parameterConvention
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Parse output:serialization-parameters XML fragment into serialization      * properties as defined by the fn:serialize function.      *      * @param parent     the parent expression calling this method      * @param parameters root node of the XML fragment      * @param properties parameters are added to the given properties      */
specifier|public
specifier|static
name|void
name|getSerializationOptions
parameter_list|(
specifier|final
name|Expression
name|parent
parameter_list|,
specifier|final
name|NodeValue
name|parameters
parameter_list|,
specifier|final
name|Properties
name|properties
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|XMLStreamReader
name|reader
init|=
name|parent
operator|.
name|getContext
argument_list|()
operator|.
name|getXMLStreamReader
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
operator|&&
operator|(
name|reader
operator|.
name|next
argument_list|()
operator|!=
name|XMLStreamReader
operator|.
name|START_ELEMENT
operator|)
condition|)
block|{
block|}
if|if
condition|(
operator|!
name|Namespaces
operator|.
name|XSLT_XQUERY_SERIALIZATION_NS
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|FnModule
operator|.
name|SENR0001
argument_list|,
literal|"serialization parameter elements should be in the output namespace"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|thisLevel
init|=
operator|(
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
operator|)
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
block|{
specifier|final
name|String
name|key
init|=
name|reader
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|properties
operator|.
name|contains
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|FnModule
operator|.
name|SEPM0019
argument_list|,
literal|"serialization parameter specified twice: "
operator|+
name|key
argument_list|)
throw|;
block|}
name|String
name|value
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
comment|// backwards compatibility: use element text as value
name|value
operator|=
name|reader
operator|.
name|getElementText
argument_list|()
expr_stmt|;
block|}
name|properties
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
condition|)
block|{
specifier|final
name|NodeId
name|otherId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|otherLevel
init|=
name|otherId
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherLevel
operator|==
name|thisLevel
condition|)
block|{
comment|// finished `optRoot` element...
break|break;
comment|// exit-while
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|Properties
name|getSerializationOptions
parameter_list|(
specifier|final
name|Expression
name|parent
parameter_list|,
specifier|final
name|MapType
name|entries
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|ParameterConvention
name|parameterConvention
range|:
name|ParameterConvention
operator|.
name|values
argument_list|()
control|)
block|{
specifier|final
name|Sequence
name|providedParameterValue
init|=
name|entries
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|parameterValue
decl_stmt|;
comment|// should we use the default value
if|if
condition|(
name|providedParameterValue
operator|==
literal|null
operator|||
name|providedParameterValue
operator|.
name|isEmpty
argument_list|()
operator|||
operator|(
name|parameterConvention
operator|.
name|type
operator|==
name|Type
operator|.
name|STRING
operator|&&
name|isEmptyStringValue
argument_list|(
name|providedParameterValue
argument_list|)
operator|)
condition|)
block|{
comment|// use default value
if|if
condition|(
name|ParameterConvention
operator|.
name|MEDIA_TYPE
operator|==
name|parameterConvention
condition|)
block|{
comment|// the default value of MEDIA_TYPE is dependent on the METHOD
name|parameterValue
operator|=
name|getDefaultMediaType
argument_list|(
name|entries
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
name|ParameterConvention
operator|.
name|METHOD
operator|.
name|parameterName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parameterValue
operator|=
name|parameterConvention
operator|.
name|defaultValue
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// use provided value
if|if
condition|(
name|checkTypes
argument_list|(
name|parameterConvention
argument_list|,
name|providedParameterValue
argument_list|)
condition|)
block|{
name|parameterValue
operator|=
name|providedParameterValue
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"The supplied value is of the wrong type for the particular parameter: "
operator|+
name|parameterConvention
operator|.
name|parameterName
argument_list|)
throw|;
block|}
block|}
name|setPropertyForMap
argument_list|(
name|properties
argument_list|,
name|parameterConvention
argument_list|,
name|parameterValue
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|FnModule
operator|.
name|SENR0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Sequence
name|getDefaultMediaType
parameter_list|(
specifier|final
name|Sequence
name|providedMethod
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|methodValue
decl_stmt|;
comment|// should we use the default method
if|if
condition|(
name|providedMethod
operator|==
literal|null
operator|||
name|providedMethod
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//use default
name|methodValue
operator|=
name|ParameterConvention
operator|.
name|METHOD
operator|.
name|defaultValue
expr_stmt|;
block|}
else|else
block|{
comment|//use provided
name|methodValue
operator|=
name|providedMethod
expr_stmt|;
block|}
specifier|final
name|String
name|method
init|=
name|methodValue
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|method
condition|)
block|{
case|case
literal|"xml"
case|:
case|case
literal|"microxml"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"application/xml"
argument_list|)
return|;
case|case
literal|"xhtml"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"application/xhtml+xml"
argument_list|)
return|;
case|case
literal|"adaptive"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"text/plain"
argument_list|)
return|;
case|case
literal|"json"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"application/json"
argument_list|)
return|;
case|case
literal|"jsonp"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"application/javascript"
argument_list|)
return|;
case|case
literal|"html"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"text/html"
argument_list|)
return|;
case|case
literal|"text"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"text/plain"
argument_list|)
return|;
case|case
literal|"binary"
case|:
return|return
operator|new
name|StringValue
argument_list|(
literal|"application/octet-stream"
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Unrecognised serialization method: "
operator|+
name|method
argument_list|)
throw|;
block|}
block|}
comment|/**      * Checks that the types of the items in the sequence match the parameter convention.      *      * @param parameterConvention The parameter convention to check against      * @param sequence The sequence to check the types of      *      * @return true if the types are suitable, false otherwise      */
specifier|private
specifier|static
name|boolean
name|checkTypes
parameter_list|(
specifier|final
name|ParameterConvention
name|parameterConvention
parameter_list|,
specifier|final
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|Cardinality
operator|.
name|checkCardinality
argument_list|(
name|parameterConvention
operator|.
name|cardinality
argument_list|,
name|sequence
operator|.
name|getCardinality
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|SequenceIterator
name|iterator
init|=
name|sequence
operator|.
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|item
init|=
name|iterator
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameterConvention
operator|.
name|type
operator|!=
name|item
operator|.
name|getType
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|void
name|setPropertyForMap
parameter_list|(
specifier|final
name|Properties
name|properties
parameter_list|,
specifier|final
name|ParameterConvention
name|parameterConvention
parameter_list|,
specifier|final
name|Sequence
name|parameterValue
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|Type
operator|.
name|BOOLEAN
operator|==
name|parameterConvention
operator|.
name|type
condition|)
block|{
comment|// ignore "admit" i.e. "standalone" empty sequence
if|if
condition|(
operator|!
name|parameterValue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|BooleanValue
operator|)
name|parameterValue
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
condition|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|Type
operator|.
name|STRING
operator|==
name|parameterConvention
operator|.
name|type
condition|)
block|{
comment|// ignore "absent" i.e. empty sequence
if|if
condition|(
operator|!
name|parameterValue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
operator|(
operator|(
name|StringValue
operator|)
name|parameterValue
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|Type
operator|.
name|DECIMAL
operator|==
name|parameterConvention
operator|.
name|type
condition|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
operator|(
operator|(
name|DecimalValue
operator|)
name|parameterValue
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|Type
operator|.
name|QNAME
operator|==
name|parameterConvention
operator|.
name|type
condition|)
block|{
if|if
condition|(
operator|!
name|parameterValue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|Cardinality
operator|.
name|checkCardinality
argument_list|(
name|Cardinality
operator|.
name|MANY
argument_list|,
name|parameterConvention
operator|.
name|cardinality
argument_list|)
condition|)
block|{
specifier|final
name|String
name|existingValue
init|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|existingValue
operator|==
literal|null
operator|||
name|existingValue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
operator|(
operator|(
name|QNameValue
operator|)
name|parameterValue
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
name|existingValue
operator|+
literal|" "
operator|+
operator|(
operator|(
name|QNameValue
operator|)
name|parameterValue
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|parameterConvention
operator|.
name|parameterName
argument_list|,
operator|(
operator|(
name|QNameValue
operator|)
name|parameterValue
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|Type
operator|.
name|MAP
operator|==
name|parameterConvention
operator|.
name|type
condition|)
block|{
if|if
condition|(
operator|!
name|parameterValue
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//TODO(AR) implement `use-character-maps`
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not yet implemented support for the map serialization parameter: "
operator|+
name|parameterConvention
operator|.
name|parameterName
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Determines if the provided sequence contains a single empty string      *      * @param sequence The sequence to test      *      * @return if the sequence is a single empty string      */
specifier|private
specifier|static
name|boolean
name|isEmptyStringValue
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|)
block|{
if|if
condition|(
name|sequence
operator|!=
literal|null
operator|&&
name|sequence
operator|.
name|getItemCount
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|Item
name|firstItem
init|=
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|Type
operator|.
name|STRING
operator|==
name|firstItem
operator|.
name|getType
argument_list|()
operator|&&
operator|(
operator|(
name|StringValue
operator|)
name|firstItem
operator|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

