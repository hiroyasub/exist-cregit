begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
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
name|memtree
operator|.
name|NamespaceNode
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
name|InlineFunction
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
name|ArrayModule
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
name|AbstractMapType
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
name|MapModule
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
name|math
operator|.
name|MathModule
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
name|w3c
operator|.
name|dom
operator|.
name|Attr
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
name|text
operator|.
name|DecimalFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DecimalFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
comment|/**  * Implements adaptive serialization from the<a href="https://www.w3.org/TR/xslt-xquery-serialization-31/">XSLT and  * XQuery Serialization 3.1</a> specification.  *  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|AdaptiveSerializer
extends|extends
name|AbstractSerializer
block|{
specifier|private
specifier|final
specifier|static
name|String
name|DEFAULT_ITEM_SEPARATOR
init|=
literal|"\n"
decl_stmt|;
specifier|private
specifier|final
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|Writer
name|writer
decl_stmt|;
specifier|public
name|AdaptiveSerializer
parameter_list|(
specifier|final
name|DBBroker
name|broker
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOutput
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|Properties
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
block|{
name|outputProperties
operator|=
name|defaultProperties
expr_stmt|;
block|}
else|else
block|{
name|outputProperties
operator|=
name|properties
expr_stmt|;
block|}
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
for|for
control|(
name|XMLWriter
name|w
range|:
name|writers
control|)
block|{
name|w
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|w
operator|.
name|setOutputProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|serialize
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
specifier|final
name|String
name|itemSep
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|ITEM_SEPARATOR
argument_list|,
name|DEFAULT_ITEM_SEPARATOR
argument_list|)
decl_stmt|;
name|serialize
argument_list|(
name|sequence
argument_list|,
name|itemSep
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|,
specifier|final
name|String
name|itemSep
parameter_list|,
specifier|final
name|boolean
name|enclose
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
try|try
block|{
if|if
condition|(
name|enclose
operator|&&
name|sequence
operator|.
name|getItemCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|SequenceIterator
name|si
init|=
name|sequence
operator|.
name|iterate
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|item
init|=
name|si
operator|.
name|nextItem
argument_list|()
decl_stmt|;
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
name|DOCUMENT
case|:
case|case
name|Type
operator|.
name|ELEMENT
case|:
case|case
name|Type
operator|.
name|TEXT
case|:
case|case
name|Type
operator|.
name|COMMENT
case|:
case|case
name|Type
operator|.
name|CDATA_SECTION
case|:
case|case
name|Type
operator|.
name|PROCESSING_INSTRUCTION
case|:
name|serializeXML
argument_list|(
name|item
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|ATTRIBUTE
case|:
specifier|final
name|Attr
name|node
init|=
operator|(
name|Attr
operator|)
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|serializeText
argument_list|(
name|node
operator|.
name|getName
argument_list|()
operator|+
literal|"=\""
operator|+
name|node
operator|.
name|getValue
argument_list|()
operator|+
literal|'"'
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|NAMESPACE
case|:
specifier|final
name|NamespaceNode
name|ns
init|=
operator|(
name|NamespaceNode
operator|)
name|item
decl_stmt|;
name|serializeText
argument_list|(
name|ns
operator|.
name|getName
argument_list|()
operator|+
literal|"=\""
operator|+
name|ns
operator|.
name|getValue
argument_list|()
operator|+
literal|'"'
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|STRING
case|:
case|case
name|Type
operator|.
name|UNTYPED_ATOMIC
case|:
case|case
name|Type
operator|.
name|ANY_URI
case|:
specifier|final
name|String
name|v
init|=
name|item
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|serializeText
argument_list|(
literal|'"'
operator|+
name|escapeQuotes
argument_list|(
name|v
argument_list|)
operator|+
literal|'"'
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DOUBLE
case|:
name|serializeDouble
argument_list|(
operator|(
name|DoubleValue
operator|)
name|item
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|BOOLEAN
case|:
name|serializeText
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"()"
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|QNAME
case|:
specifier|final
name|QName
name|qn
init|=
operator|(
operator|(
name|QNameValue
operator|)
name|item
operator|)
operator|.
name|getQName
argument_list|()
decl_stmt|;
name|serializeText
argument_list|(
literal|"Q{"
operator|+
name|qn
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|'}'
operator|+
name|qn
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|ARRAY
case|:
name|serializeArray
argument_list|(
operator|(
name|ArrayType
operator|)
name|item
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|MAP
case|:
name|serializeMap
argument_list|(
operator|(
name|AbstractMapType
operator|)
name|item
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|FUNCTION_REFERENCE
case|:
name|serializeFunctionItem
argument_list|(
operator|(
name|FunctionReference
operator|)
name|item
argument_list|)
expr_stmt|;
break|break;
default|default:
name|serializeText
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|si
operator|.
name|hasNext
argument_list|()
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
name|itemSep
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
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|enclose
operator|&&
name|sequence
operator|.
name|getItemCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
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
name|serializeDouble
parameter_list|(
specifier|final
name|DoubleValue
name|item
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
specifier|final
name|DecimalFormatSymbols
name|symbols
init|=
name|DecimalFormatSymbols
operator|.
name|getInstance
argument_list|(
name|Locale
operator|.
name|US
argument_list|)
decl_stmt|;
name|symbols
operator|.
name|setExponentSeparator
argument_list|(
literal|"e"
argument_list|)
expr_stmt|;
specifier|final
name|DecimalFormat
name|df
init|=
operator|new
name|DecimalFormat
argument_list|(
literal|"0.0##########################E0"
argument_list|,
name|symbols
argument_list|)
decl_stmt|;
name|serializeText
argument_list|(
name|df
operator|.
name|format
argument_list|(
name|item
operator|.
name|getDouble
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|serializeArray
parameter_list|(
specifier|final
name|ArrayType
name|array
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'['
argument_list|)
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
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
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
name|serialize
argument_list|(
name|member
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|write
argument_list|(
literal|']'
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
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|serializeMap
parameter_list|(
specifier|final
name|AbstractMapType
name|map
parameter_list|)
throws|throws
name|SAXException
throws|,
name|XPathException
block|{
try|try
block|{
name|writer
operator|.
name|write
argument_list|(
literal|"map{"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|>
name|i
init|=
name|map
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|serialize
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|serialize
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|write
argument_list|(
literal|'}'
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
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|serializeFunctionItem
parameter_list|(
specifier|final
name|FunctionReference
name|item
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
specifier|final
name|FunctionReference
name|ref
init|=
name|item
decl_stmt|;
specifier|final
name|FunctionSignature
name|signature
init|=
name|ref
operator|.
name|getSignature
argument_list|()
decl_stmt|;
specifier|final
name|QName
name|fn
init|=
name|signature
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|name
decl_stmt|;
if|if
condition|(
name|fn
operator|==
name|InlineFunction
operator|.
name|INLINE_FUNCTION_QNAME
condition|)
block|{
name|name
operator|=
literal|"(anonymous-function)"
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|fn
operator|.
name|getNamespaceURI
argument_list|()
condition|)
block|{
case|case
name|Namespaces
operator|.
name|XPATH_FUNCTIONS_NS
case|:
name|name
operator|=
literal|"fn:"
operator|+
name|fn
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
break|break;
case|case
name|Namespaces
operator|.
name|XPATH_FUNCTIONS_MATH_NS
case|:
name|name
operator|=
name|MathModule
operator|.
name|PREFIX
operator|+
literal|':'
operator|+
name|fn
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
break|break;
case|case
name|MapModule
operator|.
name|NAMESPACE_URI
case|:
name|name
operator|=
name|MapModule
operator|.
name|PREFIX
operator|+
literal|':'
operator|+
name|fn
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
break|break;
case|case
name|ArrayModule
operator|.
name|NAMESPACE_URI
case|:
name|name
operator|=
name|ArrayModule
operator|.
name|PREFIX
operator|+
literal|':'
operator|+
name|fn
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
break|break;
case|case
name|Namespaces
operator|.
name|SCHEMA_NS
case|:
name|name
operator|=
literal|"xs:"
operator|+
name|fn
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
break|break;
default|default:
name|name
operator|=
literal|"Q{"
operator|+
name|fn
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|'}'
operator|+
name|fn
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
name|serializeText
argument_list|(
name|name
operator|+
literal|'#'
operator|+
name|signature
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|escapeQuotes
parameter_list|(
name|String
name|value
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|value
operator|.
name|length
argument_list|()
operator|+
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|ch
init|=
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'"'
case|:
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
default|default:
name|sb
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|serializeXML
parameter_list|(
specifier|final
name|Item
name|item
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|Properties
name|xmlProperties
init|=
operator|new
name|Properties
argument_list|(
name|outputProperties
argument_list|)
decl_stmt|;
name|xmlProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
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
name|SAXSerializer
name|sax
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
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|xmlProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|xmlProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|item
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
decl||
name|SAXNotRecognizedException
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
finally|finally
block|{
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
name|serializeText
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
throws|,
name|SAXException
block|{
specifier|final
name|XMLWriter
name|xmlWriter
init|=
name|writers
index|[
name|TEXT_WRITER
index|]
decl_stmt|;
try|try
block|{
name|xmlWriter
operator|.
name|characters
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
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
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

