begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|file
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
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
name|org
operator|.
name|apache
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
name|persistent
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
name|util
operator|.
name|serializer
operator|.
name|SAXSerializer
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
name|SerializerPool
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
name|BasicFunction
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
name|FunctionSignature
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
name|Option
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
name|XQueryContext
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
name|util
operator|.
name|SerializerUtils
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
name|BinaryValue
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
name|BooleanValue
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|NodeValue
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
name|Sequence
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
name|SequenceIterator
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
name|SequenceType
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
name|Type
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

begin_class
specifier|public
class|class
name|SerializeToFile
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SerializeToFile
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|FN_SERIALIZE_LN
init|=
literal|"serialize"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|FN_SERIALIZE_BINARY_LN
init|=
literal|"serialize-binary"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|FN_SERIALIZE_LN
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|FileModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Writes the node set into a file on the file system. $parameters contains a "
operator|+
literal|"sequence of zero or more serialization parameters specified as key=value pairs. The "
operator|+
literal|"serialization options are the same as those recognized by \"declare option exist:serialize\". "
operator|+
literal|"The function does NOT automatically inherit the serialization options of the XQuery it is "
operator|+
literal|"called from.  This method is only available to the DBA role."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node-set"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The contents to write to the file system."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full path or URI to the file"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The serialization parameters: either a sequence of key=value pairs or an output:serialization-parameters "
operator|+
literal|"element as defined by the standard fn:serialize function."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"true on success - false if the specified file can not be "
operator|+
literal|"created or is not writable.  The empty sequence is returned if the argument sequence is empty."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|FN_SERIALIZE_LN
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|FileModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Writes the node set into a file on the file system, optionally appending to it. "
operator|+
literal|"$parameters contains a sequence of zero or more serialization parameters specified as "
operator|+
literal|"key=value pairs. The serialization options are the same as those recognized by "
operator|+
literal|"\"declare option exist:serialize\". "
operator|+
literal|"The function does NOT automatically inherit the serialization options of the XQuery it is "
operator|+
literal|"called from.  This method is only available to the DBA role."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node-set"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The contents to write to the file system."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full path or URI to the file"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The serialization parameters: either a sequence of key=value pairs or an output:serialization-parameters "
operator|+
literal|"element as defined by the standard fn:serialize function."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"append"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Should content be appended?"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"true on success - false if the specified file can "
operator|+
literal|"not be created or is not writable.  The empty sequence is returned if the argument sequence is empty."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|FN_SERIALIZE_BINARY_LN
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|FileModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Writes binary data into a file on the file system.  This method is only available to the DBA role."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binarydata"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The contents to write to the file system."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full path or URI to the file"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true on success - false if the specified file can not be created or is not writable"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|FN_SERIALIZE_BINARY_LN
argument_list|,
name|FileModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|FileModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Writes binary data into a file on the file system, optionally appending the content.  This method is only available to the DBA role."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"binarydata"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The contents to write to the file system."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The full path or URI to the file"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"append"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Should content be appended?"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true on success - false if the specified file can not be created or is not writable"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|SerializeToFile
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
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
if|if
condition|(
operator|!
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
specifier|final
name|XPathException
name|xPathException
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied, calling user '"
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' must be a DBA to call this function."
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid user"
argument_list|,
name|xPathException
argument_list|)
expr_stmt|;
throw|throw
name|xPathException
throw|;
block|}
comment|//check the file output path
specifier|final
name|String
name|inputPath
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|File
name|file
init|=
name|FileModuleHelper
operator|.
name|getFile
argument_list|(
name|inputPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Cannot serialize file. Output file is a directory: "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|file
operator|.
name|canWrite
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Cannot serialize file. Cannot write to file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
name|FN_SERIALIZE_LN
argument_list|)
condition|)
block|{
comment|//parse serialization options from third argument to function
specifier|final
name|Properties
name|outputProperties
init|=
name|parseXMLSerializationOptions
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|doAppend
init|=
operator|(
name|args
operator|.
name|length
operator|>
literal|3
operator|)
operator|&&
literal|"true"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|3
index|]
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
comment|//do the serialization
name|serializeXML
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|iterate
argument_list|()
argument_list|,
name|outputProperties
argument_list|,
name|file
argument_list|,
name|doAppend
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|FN_SERIALIZE_BINARY_LN
argument_list|)
condition|)
block|{
specifier|final
name|boolean
name|doAppend
init|=
operator|(
name|args
operator|.
name|length
operator|>
literal|2
operator|)
operator|&&
literal|"true"
operator|.
name|equals
argument_list|(
name|args
index|[
literal|2
index|]
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
name|serializeBinary
argument_list|(
operator|(
name|BinaryValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|file
argument_list|,
name|doAppend
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unknown function name"
argument_list|)
throw|;
block|}
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
block|}
specifier|private
name|Properties
name|parseXMLSerializationOptions
parameter_list|(
specifier|final
name|Sequence
name|sSerializeParams
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//parse serialization options
specifier|final
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|outputProperties
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
if|if
condition|(
name|sSerializeParams
operator|.
name|hasOne
argument_list|()
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|sSerializeParams
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|SerializerUtils
operator|.
name|getSerializationOptions
argument_list|(
name|this
argument_list|,
operator|(
name|NodeValue
operator|)
name|sSerializeParams
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SequenceIterator
name|siSerializeParams
init|=
name|sSerializeParams
operator|.
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|siSerializeParams
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|String
name|serializeParam
init|=
name|siSerializeParams
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|opt
index|[]
init|=
name|Option
operator|.
name|parseKeyValuePair
argument_list|(
name|serializeParam
argument_list|)
decl_stmt|;
if|if
condition|(
name|opt
operator|!=
literal|null
operator|&&
name|opt
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|opt
index|[
literal|0
index|]
argument_list|,
name|opt
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|outputProperties
return|;
block|}
specifier|private
name|void
name|serializeXML
parameter_list|(
specifier|final
name|SequenceIterator
name|siNode
parameter_list|,
specifier|final
name|Properties
name|outputProperties
parameter_list|,
specifier|final
name|File
name|file
parameter_list|,
specifier|final
name|boolean
name|doAppend
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// serialize the node set
name|SAXSerializer
name|sax
init|=
literal|null
decl_stmt|;
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sax
operator|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|,
name|doAppend
argument_list|)
decl_stmt|;
specifier|final
name|String
name|encoding
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
name|encoding
argument_list|)
expr_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
specifier|final
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setReceiver
argument_list|(
name|sax
argument_list|)
expr_stmt|;
name|sax
operator|.
name|startDocument
argument_list|()
expr_stmt|;
while|while
condition|(
name|siNode
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|NodeValue
name|next
init|=
operator|(
name|NodeValue
operator|)
name|siNode
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|sax
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Cannot serialize file. A problem occurred while serializing the node set: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
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
literal|"Cannot serialize file. A problem occurred while serializing the node set: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Cannot serialize file '"
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" ': "
operator|+
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
if|if
condition|(
name|sax
operator|!=
literal|null
condition|)
block|{
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|serializeBinary
parameter_list|(
specifier|final
name|BinaryValue
name|binary
parameter_list|,
specifier|final
name|File
name|file
parameter_list|,
specifier|final
name|boolean
name|doAppend
parameter_list|)
throws|throws
name|XPathException
block|{
name|OutputStream
name|os
init|=
literal|null
decl_stmt|;
try|try
block|{
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|,
name|doAppend
argument_list|)
expr_stmt|;
name|binary
operator|.
name|streamBinaryTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Cannot serialize file. A problem occurred while serializing the binary data: "
operator|+
name|fnfe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|fnfe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Cannot serialize file. A problem occurred while serializing the binary data: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|os
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Cannot serialize file '"
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" ': "
operator|+
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
block|}
block|}
block|}
end_class

end_unit

