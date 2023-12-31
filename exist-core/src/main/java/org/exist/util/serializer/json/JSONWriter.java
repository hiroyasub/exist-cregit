begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|json
package|;
end_package

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
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|QName
operator|.
name|IllegalQNameException
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
name|util
operator|.
name|serializer
operator|.
name|XMLWriter
import|;
end_import

begin_comment
comment|/**  * This class plugs into eXist's serialization to transform XML to JSON. It is used  * if the serialization property "method" is set to "json".  *   * The following rules apply for the mapping of XML to JSON:  *   *<ul>  *<li>The root element will be absorbed, i.e.&lt;root&gt;text&lt;/root&gt; becomes "root".</li>  *<li>Sibling elements with the same name are added to an array.</li>  *<li>If an element has attribute and text content, the text content becomes a  *      property, e.g. '#text': 'my text'.</li>  *<li>In mixed content nodes, text nodes will be dropped.</li>  *<li>An empty element becomes 'null', i.e.&lt;e/&gt; becomes {"e": null}.</li>  *<li>An element with a single text child becomes a property with the value of the text child, i.e.  *&lt;e&gt;text&lt;/e&gt; becomes {"e": "text"}<li>  *<li>An element with name "json:value" is serialized as a simple value, not an object, i.e.  *&lt;json:value&gt;value&lt;/json:value&gt; just becomes "value".</li>  *</ul>  *   * Namespace prefixes will be dropped from element and attribute names by default. If the serialization  * property {@link EXistOutputKeys#JSON_OUTPUT_NS_PREFIX} is set to "yes", namespace prefixes will be  * added to the resulting JSON property names, replacing the ":" with a "_", i.e.&lt;foo:node&gt; becomes  * "foo_node".  *   * If an attribute json:array is present on an element it will always be serialized as an array, even if there  * are no other sibling elements with the same name.  *   * The attribute json:literal indicates that the element's text content should be serialized literally. This is  * handy for writing boolean or numeric values. By default, text content is serialized as a Javascript string.  *    * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|JSONWriter
extends|extends
name|XMLWriter
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|JSONWriter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ARRAY
init|=
literal|"array"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|LITERAL
init|=
literal|"literal"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALUE
init|=
literal|"value"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|JSON_ARRAY
init|=
literal|"json:"
operator|+
name|ARRAY
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|JSON_LITERAL
init|=
literal|"json:"
operator|+
name|LITERAL
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|JSON_VALUE
init|=
literal|"json:"
operator|+
name|VALUE
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|JSON_NAME
init|=
literal|"json:"
operator|+
name|NAME
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|JASON_NS
init|=
literal|"http://www.json.org"
decl_stmt|;
specifier|protected
name|JSONNode
name|root
decl_stmt|;
specifier|protected
specifier|final
name|Deque
argument_list|<
name|JSONObject
argument_list|>
name|stack
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|useNSPrefix
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|prefixAttributes
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|ignoreWhitespaceTextNodes
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|jsonp
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|indent
init|=
literal|false
decl_stmt|;
specifier|public
name|JSONWriter
parameter_list|()
block|{
comment|// empty
block|}
specifier|public
name|JSONWriter
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|resetObjectState
parameter_list|()
block|{
name|super
operator|.
name|resetObjectState
argument_list|()
expr_stmt|;
name|stack
operator|.
name|clear
argument_list|()
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOutputProperties
parameter_list|(
specifier|final
name|Properties
name|properties
parameter_list|)
block|{
name|super
operator|.
name|setOutputProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
specifier|final
name|String
name|useNSPrefixProp
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|JSON_OUTPUT_NS_PREFIX
argument_list|,
literal|"no"
argument_list|)
decl_stmt|;
name|useNSPrefix
operator|=
name|useNSPrefixProp
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|prefixAttributesProp
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|JSON_PREFIX_ATTRIBUTES
argument_list|,
literal|"no"
argument_list|)
decl_stmt|;
name|prefixAttributes
operator|=
name|prefixAttributesProp
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|ignoreWhitespaceTextNodesProp
init|=
name|properties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|JSON_IGNORE_WHITESPACE_TEXT_NODES
argument_list|,
literal|"no"
argument_list|)
decl_stmt|;
name|ignoreWhitespaceTextNodes
operator|=
name|ignoreWhitespaceTextNodesProp
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
name|jsonp
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|JSONP
argument_list|)
expr_stmt|;
name|indent
operator|=
name|properties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
try|try
block|{
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|jsonp
operator|!=
literal|null
condition|)
block|{
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
name|jsonp
operator|+
literal|"("
argument_list|)
expr_stmt|;
block|}
name|root
operator|.
name|serialize
argument_list|(
name|getWriter
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|jsonp
operator|!=
literal|null
condition|)
block|{
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|qname
operator|.
name|equals
argument_list|(
name|JSON_VALUE
argument_list|)
condition|)
block|{
name|processStartValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|useNSPrefix
condition|)
block|{
name|processStartElement
argument_list|(
name|qname
operator|.
name|replace
argument_list|(
literal|':'
argument_list|,
literal|'_'
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|processStartElement
argument_list|(
name|QName
operator|.
name|extractLocalName
argument_list|(
name|qname
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|JASON_NS
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
name|VALUE
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|processStartValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|useNSPrefix
condition|)
block|{
name|processStartElement
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|+
literal|'_'
operator|+
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|processStartElement
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|processStartElement
parameter_list|(
specifier|final
name|String
name|localName
parameter_list|,
name|boolean
name|simpleValue
parameter_list|)
block|{
specifier|final
name|JSONObject
name|obj
init|=
operator|new
name|JSONObject
argument_list|(
name|localName
argument_list|)
decl_stmt|;
name|obj
operator|.
name|setIndent
argument_list|(
name|indent
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
name|obj
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|JSONObject
name|parent
init|=
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
name|parent
operator|.
name|addObject
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|processStartValue
parameter_list|()
throws|throws
name|TransformerException
block|{
comment|// a json:value is stored as an unnamed object
specifier|final
name|JSONObject
name|obj
init|=
operator|new
name|JSONObject
argument_list|()
decl_stmt|;
name|obj
operator|.
name|setIndent
argument_list|(
name|indent
argument_list|)
expr_stmt|;
if|if
condition|(
name|root
operator|==
literal|null
condition|)
block|{
name|root
operator|=
name|obj
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|JSONObject
name|parent
init|=
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
name|parent
operator|.
name|addObject
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|String
name|namespaceUri
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|stack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|namespace
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|nsURI
parameter_list|)
throws|throws
name|TransformerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
specifier|final
name|String
name|qname
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|TransformerException
block|{
specifier|final
name|JSONObject
name|parent
init|=
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|qname
condition|)
block|{
case|case
name|JSON_ARRAY
case|:
name|parent
operator|.
name|setSerializationType
argument_list|(
name|JSONNode
operator|.
name|SerializationType
operator|.
name|AS_ARRAY
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSON_LITERAL
case|:
name|parent
operator|.
name|setSerializationDataType
argument_list|(
name|JSONNode
operator|.
name|SerializationDataType
operator|.
name|AS_LITERAL
argument_list|)
expr_stmt|;
break|break;
case|case
name|JSON_NAME
case|:
name|parent
operator|.
name|setName
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
default|default:
specifier|final
name|String
name|name
init|=
name|prefixAttributes
condition|?
literal|"@"
operator|+
name|qname
else|:
name|qname
decl_stmt|;
specifier|final
name|JSONSimpleProperty
name|obj
init|=
operator|new
name|JSONSimpleProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|obj
operator|.
name|setIndent
argument_list|(
name|indent
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addObject
argument_list|(
name|obj
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|TransformerException
block|{
name|attribute
argument_list|(
name|qname
operator|.
name|toString
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
specifier|final
name|CharSequence
name|chars
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|ignoreWhitespaceTextNodes
condition|)
block|{
specifier|final
name|boolean
name|isWhitespace
init|=
name|chars
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
if|if
condition|(
name|isWhitespace
condition|)
block|{
return|return;
block|}
block|}
specifier|final
name|JSONObject
name|parent
init|=
name|stack
operator|.
name|peek
argument_list|()
decl_stmt|;
specifier|final
name|JSONNode
name|value
init|=
operator|new
name|JSONValue
argument_list|(
name|chars
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|value
operator|.
name|setIndent
argument_list|(
name|indent
argument_list|)
expr_stmt|;
name|value
operator|.
name|setSerializationType
argument_list|(
name|parent
operator|.
name|getSerializationType
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|.
name|setSerializationDataType
argument_list|(
name|parent
operator|.
name|getSerializationDataType
argument_list|()
argument_list|)
expr_stmt|;
name|parent
operator|.
name|addObject
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
name|characters
argument_list|(
operator|new
name|String
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|processingInstruction
parameter_list|(
specifier|final
name|String
name|target
parameter_list|,
specifier|final
name|String
name|data
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|// skip
block|}
annotation|@
name|Override
specifier|public
name|void
name|comment
parameter_list|(
specifier|final
name|CharSequence
name|data
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|// skip
block|}
annotation|@
name|Override
specifier|public
name|void
name|startCdataSection
parameter_list|()
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|public
name|void
name|endCdataSection
parameter_list|()
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|public
name|void
name|cdataSection
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|// treat as string content
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocumentType
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|publicId
parameter_list|,
specifier|final
name|String
name|systemId
parameter_list|)
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocumentType
parameter_list|()
block|{
comment|// empty
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentType
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|publicId
parameter_list|,
specifier|final
name|String
name|systemId
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|// skip
block|}
block|}
end_class

end_unit

