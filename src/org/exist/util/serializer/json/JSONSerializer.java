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
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonGenerator
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
name|DBBroker
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
name|storage
operator|.
name|serializers
operator|.
name|Serializer
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
name|array
operator|.
name|ArrayType
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXNotRecognizedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXNotSupportedException
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
comment|/**  * Called by {@link org.exist.util.serializer.XQuerySerializer} to serialize an XQuery sequence  * to JSON. The JSON serializer differs from other serialization methods because it maps XQuery  * data items to JSON.  *  * @author Wolf  */
end_comment

begin_class
specifier|public
class|class
name|JSONSerializer
block|{
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
specifier|final
name|Properties
name|outputProperties
decl_stmt|;
specifier|public
name|JSONSerializer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Properties
name|outputProperties
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|outputProperties
operator|=
name|outputProperties
expr_stmt|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|Sequence
name|sequence
parameter_list|,
name|Writer
name|writer
parameter_list|)
throws|throws
name|SAXException
block|{
name|JsonFactory
name|factory
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
try|try
block|{
name|JsonGenerator
name|generator
init|=
name|factory
operator|.
name|createGenerator
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|generator
operator|.
name|disable
argument_list|(
name|JsonGenerator
operator|.
name|Feature
operator|.
name|AUTO_CLOSE_TARGET
argument_list|)
expr_stmt|;
if|if
condition|(
literal|"yes"
operator|.
name|equals
argument_list|(
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
argument_list|)
condition|)
block|{
name|generator
operator|.
name|useDefaultPrettyPrinter
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
literal|"yes"
operator|.
name|equals
argument_list|(
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|ALLOW_DUPLICATE_NAMES
argument_list|,
literal|"yes"
argument_list|)
argument_list|)
condition|)
block|{
name|generator
operator|.
name|enable
argument_list|(
name|JsonGenerator
operator|.
name|Feature
operator|.
name|STRICT_DUPLICATE_DETECTION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|generator
operator|.
name|disable
argument_list|(
name|JsonGenerator
operator|.
name|Feature
operator|.
name|STRICT_DUPLICATE_DETECTION
argument_list|)
expr_stmt|;
block|}
name|serializeSequence
argument_list|(
name|sequence
argument_list|,
name|generator
argument_list|)
expr_stmt|;
name|generator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|serializeSequence
parameter_list|(
name|Sequence
name|sequence
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
throws|,
name|SAXException
block|{
if|if
condition|(
name|sequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|generator
operator|.
name|writeNull
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|sequence
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|serializeItem
argument_list|(
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|sequence
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|serializeItem
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|serializeItem
parameter_list|(
name|Item
name|item
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
throws|,
name|SAXException
block|{
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
condition|)
block|{
name|serializeArray
argument_list|(
operator|(
name|ArrayType
operator|)
name|item
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|MAP
condition|)
block|{
name|serializeMap
argument_list|(
operator|(
name|MapType
operator|)
name|item
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
block|{
name|generator
operator|.
name|writeNumber
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|item
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|BOOLEAN
case|:
name|generator
operator|.
name|writeBoolean
argument_list|(
operator|(
operator|(
name|AtomicValue
operator|)
name|item
operator|)
operator|.
name|effectiveBooleanValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
name|generator
operator|.
name|writeString
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|serializeNode
argument_list|(
name|item
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|serializeNode
parameter_list|(
name|Item
name|item
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
specifier|final
name|Properties
name|xmlOutput
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|xmlOutput
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|JSON_NODE_OUTPUT_METHOD
argument_list|,
literal|"xml"
argument_list|)
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|xmlOutput
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|serializer
operator|.
name|setProperties
argument_list|(
name|xmlOutput
argument_list|)
expr_stmt|;
name|generator
operator|.
name|writeString
argument_list|(
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|item
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|serializeArray
parameter_list|(
name|ArrayType
name|array
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
throws|,
name|SAXException
block|{
name|generator
operator|.
name|writeStartArray
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|array
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Sequence
name|member
init|=
name|array
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|serializeSequence
argument_list|(
name|member
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndArray
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|serializeMap
parameter_list|(
name|MapType
name|map
parameter_list|,
name|JsonGenerator
name|generator
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
throws|,
name|SAXException
block|{
name|generator
operator|.
name|writeStartObject
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
range|:
name|map
control|)
block|{
name|generator
operator|.
name|writeFieldName
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|serializeSequence
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|generator
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|writeEndObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

