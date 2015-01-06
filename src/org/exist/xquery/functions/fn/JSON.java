begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
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
name|JsonParseException
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
name|JsonParser
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
name|JsonToken
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
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|SourceFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|*
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
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Functions related to JSON parsing.  *  * @author Wolf  */
end_comment

begin_class
specifier|public
class|class
name|JSON
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|static
specifier|final
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parse-json"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Parses a string supplied in the form of a JSON text, returning the results typically in the form of a map or array."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"json-text"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"JSON string"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The parsed data, typically a map, array or atomic value"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parse-json"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Parses a string supplied in the form of a JSON text, returning the results typically in the form of a map or array."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"json-text"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"JSON string"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"options"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Parsing options"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The parsed data, typically a map, array or atomic value"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"json-doc"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Reads an external (or database) resource containing JSON, and returns the results of parsing the resource as JSON. An URL parameter "
operator|+
literal|"without scheme or scheme 'xmldb:' is considered to point to a database resource."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"href"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"URL pointing to a JSON resource"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The parsed data, typically a map, array or atomic value"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"json-doc"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Reads an external (or database) resource containing JSON, and returns the results of parsing the resource as JSON. An URL parameter "
operator|+
literal|"without scheme or scheme 'xmldb:' is considered to point to a database resource."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"href"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"URL pointing to a JSON resource"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"options"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Parsing options"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The parsed data, typically a map, array or atomic value"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OPTION_DUPLICATES
init|=
literal|"duplicates"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OPTION_DUPLICATES_REJECT
init|=
literal|"reject"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OPTION_DUPLICATES_USE_FIRST
init|=
literal|"use-first"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OPTION_DUPLICATES_USE_LAST
init|=
literal|"use-last"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OPTION_LIBERAL
init|=
literal|"liberal"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OPTION_UNESCAPE
init|=
literal|"unescape"
decl_stmt|;
specifier|public
name|JSON
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// process options if present
comment|// TODO: jackson does not allow access to raw string, so option "unescape" is not supported
name|boolean
name|liberal
init|=
literal|false
decl_stmt|;
name|String
name|handleDuplicates
init|=
name|OPTION_DUPLICATES_USE_LAST
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
specifier|final
name|MapType
name|options
init|=
operator|(
name|MapType
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Sequence
name|liberalOpt
init|=
name|options
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
name|OPTION_LIBERAL
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|liberalOpt
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|liberal
operator|=
name|liberalOpt
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Sequence
name|duplicateOpt
init|=
name|options
operator|.
name|get
argument_list|(
operator|new
name|StringValue
argument_list|(
name|OPTION_DUPLICATES
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|duplicateOpt
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|handleDuplicates
operator|=
name|duplicateOpt
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
name|JsonFactory
name|factory
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
name|factory
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_NON_NUMERIC_NUMBERS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// duplicates are handled in readValue
name|factory
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|STRICT_DUPLICATE_DETECTION
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|liberal
condition|)
block|{
name|factory
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_COMMENTS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_SINGLE_QUOTES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_NUMERIC_LEADING_ZEROS
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_UNQUOTED_FIELD_NAMES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|factory
operator|.
name|configure
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"parse-json"
argument_list|)
condition|)
block|{
return|return
name|parse
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|handleDuplicates
argument_list|,
name|factory
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|parseResource
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|handleDuplicates
argument_list|,
name|factory
argument_list|)
return|;
block|}
block|}
specifier|private
name|Sequence
name|parse
parameter_list|(
name|Sequence
name|json
parameter_list|,
name|String
name|handleDuplicates
parameter_list|,
name|JsonFactory
name|factory
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|json
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
try|try
block|{
specifier|final
name|JsonParser
name|parser
init|=
name|factory
operator|.
name|createParser
argument_list|(
name|json
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Item
name|result
init|=
name|readValue
argument_list|(
name|context
argument_list|,
name|parser
argument_list|,
name|handleDuplicates
argument_list|)
decl_stmt|;
return|return
name|result
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
name|result
operator|.
name|toSequence
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOJS0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|setLocation
argument_list|(
name|getLine
argument_list|()
argument_list|,
name|getColumn
argument_list|()
argument_list|,
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
specifier|private
name|Sequence
name|parseResource
parameter_list|(
name|Sequence
name|href
parameter_list|,
name|String
name|handleDuplicates
parameter_list|,
name|JsonFactory
name|factory
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|href
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
try|try
block|{
name|String
name|url
init|=
name|href
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|url
operator|=
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
operator|+
name|url
expr_stmt|;
block|}
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
literal|""
argument_list|,
name|url
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
name|source
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
specifier|final
name|JsonParser
name|parser
init|=
name|factory
operator|.
name|createParser
argument_list|(
name|is
argument_list|)
decl_stmt|;
specifier|final
name|Item
name|result
init|=
name|readValue
argument_list|(
name|context
argument_list|,
name|parser
argument_list|,
name|handleDuplicates
argument_list|)
decl_stmt|;
return|return
name|result
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
name|result
operator|.
name|toSequence
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
decl||
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOUT1170
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Generate an XDM from the tokens delivered by the JSON parser.      *      * @param context the XQueryContext      * @param parser parser to use      * @param handleDuplicates string indicating how to handle duplicate property names      * @return the top item read      * @throws IOException      * @throws XPathException      */
specifier|public
specifier|static
name|Item
name|readValue
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|JsonParser
name|parser
parameter_list|,
name|String
name|handleDuplicates
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
return|return
name|readValue
argument_list|(
name|context
argument_list|,
name|parser
argument_list|,
literal|null
argument_list|,
name|handleDuplicates
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Item
name|readValue
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|JsonParser
name|parser
parameter_list|,
name|Item
name|parent
parameter_list|,
name|String
name|handleDuplicates
parameter_list|)
throws|throws
name|IOException
throws|,
name|XPathException
block|{
name|JsonToken
name|token
decl_stmt|;
name|Item
name|next
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextValue
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|JsonToken
operator|.
name|END_OBJECT
operator|||
name|token
operator|==
name|JsonToken
operator|.
name|END_ARRAY
condition|)
block|{
return|return
name|parent
return|;
block|}
switch|switch
condition|(
name|token
condition|)
block|{
case|case
name|START_OBJECT
case|:
name|next
operator|=
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|readValue
argument_list|(
name|context
argument_list|,
name|parser
argument_list|,
name|next
argument_list|,
name|handleDuplicates
argument_list|)
expr_stmt|;
break|break;
case|case
name|START_ARRAY
case|:
name|next
operator|=
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
name|readValue
argument_list|(
name|context
argument_list|,
name|parser
argument_list|,
name|next
argument_list|,
name|handleDuplicates
argument_list|)
expr_stmt|;
break|break;
case|case
name|VALUE_FALSE
case|:
name|next
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
break|break;
case|case
name|VALUE_TRUE
case|:
name|next
operator|=
name|BooleanValue
operator|.
name|TRUE
expr_stmt|;
break|break;
case|case
name|VALUE_NUMBER_FLOAT
case|:
case|case
name|VALUE_NUMBER_INT
case|:
comment|// according to spec, all numbers are converted to double
name|next
operator|=
operator|new
name|StringValue
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
expr_stmt|;
break|break;
case|case
name|VALUE_NULL
case|:
name|next
operator|=
literal|null
expr_stmt|;
break|break;
default|default:
name|next
operator|=
operator|new
name|StringValue
argument_list|(
name|parser
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|parent
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|ARRAY
case|:
operator|(
operator|(
name|ArrayType
operator|)
name|parent
operator|)
operator|.
name|add
argument_list|(
name|next
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
name|next
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|MAP
case|:
specifier|final
name|String
name|currentName
init|=
name|parser
operator|.
name|getCurrentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOJS0001
argument_list|,
literal|"Invalid JSON object"
argument_list|)
throw|;
block|}
specifier|final
name|StringValue
name|name
init|=
operator|new
name|StringValue
argument_list|(
name|currentName
argument_list|)
decl_stmt|;
specifier|final
name|MapType
name|map
init|=
operator|(
name|MapType
operator|)
name|parent
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// handle duplicate keys
if|if
condition|(
name|handleDuplicates
operator|.
name|equals
argument_list|(
name|OPTION_DUPLICATES_REJECT
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOJS0003
argument_list|,
literal|"Duplicate key: "
operator|+
name|currentName
argument_list|)
throw|;
block|}
if|if
condition|(
name|handleDuplicates
operator|.
name|equals
argument_list|(
name|OPTION_DUPLICATES_USE_LAST
argument_list|)
condition|)
block|{
name|map
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|next
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
name|next
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|map
operator|.
name|add
argument_list|(
name|name
argument_list|,
name|next
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
name|next
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
return|return
name|next
return|;
block|}
block|}
end_class

end_unit

