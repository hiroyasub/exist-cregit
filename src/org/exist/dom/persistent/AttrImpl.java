begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|RangeIndexSpec
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
name|Signatures
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
name|ByteArrayPool
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
name|ByteConversion
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
name|UTF8
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
name|XMLString
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
name|pool
operator|.
name|NodePool
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
name|AttrList
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
name|w3c
operator|.
name|dom
operator|.
name|DOMException
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
name|Element
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
name|Node
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
name|TypeInfo
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
name|UserDataHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_class
specifier|public
class|class
name|AttrImpl
extends|extends
name|NamedNode
implements|implements
name|Attr
block|{
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_NS_ID
init|=
literal|2
decl_stmt|;
comment|//sizeof short
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_PREFIX_LENGTH
init|=
literal|2
decl_stmt|;
comment|//sizeof short
specifier|public
specifier|static
specifier|final
name|int
name|CDATA
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|ID
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|IDREF
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|IDREFS
init|=
literal|3
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_ATTRIBUTE_TYPE
init|=
name|CDATA
decl_stmt|;
specifier|private
name|int
name|attributeType
init|=
name|DEFAULT_ATTRIBUTE_TYPE
decl_stmt|;
specifier|private
name|int
name|indexType
init|=
name|RangeIndexSpec
operator|.
name|NO_INDEX
decl_stmt|;
specifier|private
name|XMLString
name|value
init|=
literal|null
decl_stmt|;
specifier|public
name|AttrImpl
parameter_list|()
block|{
name|super
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AttrImpl
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|SymbolTable
name|symbols
parameter_list|)
throws|throws
name|DOMException
block|{
name|super
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|symbols
operator|!=
literal|null
operator|&&
name|symbols
operator|.
name|getSymbol
argument_list|(
name|nodeName
operator|.
name|getLocalPart
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|INVALID_ACCESS_ERR
argument_list|,
literal|"Too many element/attribute names registered in the database. No of distinct names is limited to 16bit. Aborting store."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|AttrImpl
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|String
name|str
parameter_list|,
specifier|final
name|SymbolTable
name|symbols
parameter_list|)
throws|throws
name|DOMException
block|{
name|this
argument_list|(
name|name
argument_list|,
name|symbols
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
operator|new
name|XMLString
argument_list|(
name|str
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AttrImpl
parameter_list|(
specifier|final
name|AttrImpl
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|this
operator|.
name|attributeType
operator|=
name|other
operator|.
name|attributeType
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|other
operator|.
name|value
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|attributeType
operator|=
name|DEFAULT_ATTRIBUTE_TYPE
expr_stmt|;
name|this
operator|.
name|value
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Serializes a (persistent DOM) Attr to a byte array      *      * data = signature nodeIdUnitsLength nodeId localNameId namespace? value      *      * signature = [byte] 0x80 | localNameType | attrType | hasNamespace?      *      * localNameType = noContent OR intContent OR shortContent OR byteContent      * noContent = 0x0      * intContent = 0x1      * shortContent = 0x2      * byteContent = 0x3      *      * attrType = cdata OR id OR idref OR idrefs      * cdata = 0x0;      * id = 0x4      * idref = 0x8      * idrefs = 0xC      *      * hasNamespace = 0x10      *      * nodeIdUnitsLength = [short] (2 bytes) The number of units of the attr's NodeId      * nodeId = {@see org.exist.numbering.DLNBase#serialize(byte[], int)}      *      * localNameId = [int] (4 bytes) | [short] (2 bytes) | [byte] 1 byte. The Id of the attr's local name from SymbolTable (symbols.dbx)      *      * namespace = namespaceUriId namespacePrefixLength attrNamespacePrefix?      * namespaceUriId = [short] (2 bytes) The Id of the namespace URI from SymbolTable (symbols.dbx)      * namespacePrefixLength = [short] (2 bytes)      * attrNamespacePrefix = eUtf8      *      * value = eUtf8      *      * eUtf8 = {@see org.exist.util.UTF8#encode(java.lang.String, byte[], int)}      */
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
if|if
condition|(
name|nodeName
operator|.
name|getLocalPart
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Local name is null"
argument_list|)
throw|;
block|}
specifier|final
name|short
name|id
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getSymbol
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|final
name|byte
name|idSizeType
init|=
name|Signatures
operator|.
name|getSizeType
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|int
name|prefixLen
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|hasNamespace
argument_list|()
operator|&&
name|nodeName
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|nodeName
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|prefixLen
operator|=
name|UTF8
operator|.
name|encoded
argument_list|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|nodeIdLen
init|=
name|nodeId
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|ByteArrayPool
operator|.
name|getByteArray
argument_list|(
name|LENGTH_SIGNATURE_LENGTH
operator|+
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
operator|+
name|nodeIdLen
operator|+
name|Signatures
operator|.
name|getLength
argument_list|(
name|idSizeType
argument_list|)
operator|+
operator|(
name|nodeName
operator|.
name|hasNamespace
argument_list|()
condition|?
name|LENGTH_NS_ID
operator|+
name|LENGTH_PREFIX_LENGTH
operator|+
name|prefixLen
else|:
literal|0
operator|)
operator|+
name|value
operator|.
name|UTF8Size
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|data
index|[
name|pos
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
name|Signatures
operator|.
name|Attr
operator|<<
literal|0x5
operator|)
expr_stmt|;
name|data
index|[
name|pos
index|]
operator||=
name|idSizeType
expr_stmt|;
name|data
index|[
name|pos
index|]
operator||=
operator|(
name|byte
operator|)
operator|(
name|attributeType
operator|<<
literal|0x2
operator|)
expr_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|hasNamespace
argument_list|()
condition|)
block|{
name|data
index|[
name|pos
index|]
operator||=
literal|0x10
expr_stmt|;
block|}
name|pos
operator|+=
name|StoredNode
operator|.
name|LENGTH_SIGNATURE_LENGTH
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
operator|(
name|short
operator|)
name|nodeId
operator|.
name|units
argument_list|()
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
expr_stmt|;
name|nodeId
operator|.
name|serialize
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|nodeIdLen
expr_stmt|;
name|Signatures
operator|.
name|write
argument_list|(
name|idSizeType
argument_list|,
name|id
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|Signatures
operator|.
name|getLength
argument_list|(
name|idSizeType
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|hasNamespace
argument_list|()
condition|)
block|{
specifier|final
name|short
name|nsId
init|=
name|ownerDocument
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNSSymbol
argument_list|(
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|nsId
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|LENGTH_NS_ID
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
operator|(
name|short
operator|)
name|prefixLen
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|LENGTH_PREFIX_LENGTH
expr_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|nodeName
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|UTF8
operator|.
name|encode
argument_list|(
name|nodeName
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
name|pos
operator|+=
name|prefixLen
expr_stmt|;
block|}
name|value
operator|.
name|UTF8Encode
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|,
specifier|final
name|boolean
name|pooled
parameter_list|)
block|{
name|int
name|pos
init|=
name|start
decl_stmt|;
specifier|final
name|byte
name|idSizeType
init|=
operator|(
name|byte
operator|)
operator|(
name|data
index|[
name|pos
index|]
operator|&
literal|0x3
operator|)
decl_stmt|;
specifier|final
name|boolean
name|hasNamespace
init|=
operator|(
name|data
index|[
name|pos
index|]
operator|&
literal|0x10
operator|)
operator|==
literal|0x10
decl_stmt|;
specifier|final
name|int
name|attrType
init|=
operator|(
name|data
index|[
name|pos
index|]
operator|&
literal|0x4
operator|)
operator|>>
literal|0x2
decl_stmt|;
name|pos
operator|+=
name|StoredNode
operator|.
name|LENGTH_SIGNATURE_LENGTH
expr_stmt|;
specifier|final
name|int
name|dlnLen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
expr_stmt|;
specifier|final
name|NodeId
name|dln
init|=
name|doc
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromData
argument_list|(
name|dlnLen
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|dln
operator|.
name|size
argument_list|()
expr_stmt|;
specifier|final
name|short
name|id
init|=
operator|(
name|short
operator|)
name|Signatures
operator|.
name|read
argument_list|(
name|idSizeType
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|Signatures
operator|.
name|getLength
argument_list|(
name|idSizeType
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
name|doc
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getName
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no symbol for id "
operator|+
name|id
argument_list|)
throw|;
block|}
name|short
name|nsId
init|=
literal|0
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasNamespace
condition|)
block|{
name|nsId
operator|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|LENGTH_NS_ID
expr_stmt|;
name|int
name|prefixLen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|LENGTH_PREFIX_LENGTH
expr_stmt|;
if|if
condition|(
name|prefixLen
operator|>
literal|0
condition|)
block|{
name|prefix
operator|=
name|UTF8
operator|.
name|decode
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|prefixLen
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|pos
operator|+=
name|prefixLen
expr_stmt|;
block|}
specifier|final
name|String
name|namespace
init|=
name|nsId
operator|==
literal|0
condition|?
literal|""
else|:
name|doc
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNamespace
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
name|XMLString
name|value
init|=
name|UTF8
operator|.
name|decode
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|len
operator|-
operator|(
name|pos
operator|-
name|start
operator|)
argument_list|)
decl_stmt|;
comment|//OK : we have the necessary material to build the attribute
specifier|final
name|AttrImpl
name|attr
decl_stmt|;
if|if
condition|(
name|pooled
condition|)
block|{
name|attr
operator|=
operator|(
name|AttrImpl
operator|)
name|NodePool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowNode
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attr
operator|=
operator|new
name|AttrImpl
argument_list|()
expr_stmt|;
block|}
name|attr
operator|.
name|setNodeName
argument_list|(
name|doc
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getQName
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|,
name|namespace
argument_list|,
name|name
argument_list|,
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
name|attr
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|attr
operator|.
name|setNodeId
argument_list|(
name|dln
argument_list|)
expr_stmt|;
name|attr
operator|.
name|setType
argument_list|(
name|attrType
argument_list|)
expr_stmt|;
return|return
name|attr
return|;
block|}
specifier|public
specifier|static
name|void
name|addToList
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|,
specifier|final
name|AttrList
name|list
parameter_list|)
block|{
name|int
name|pos
init|=
name|start
decl_stmt|;
specifier|final
name|byte
name|idSizeType
init|=
operator|(
name|byte
operator|)
operator|(
name|data
index|[
name|pos
index|]
operator|&
literal|0x3
operator|)
decl_stmt|;
specifier|final
name|boolean
name|hasNamespace
init|=
operator|(
name|data
index|[
name|pos
index|]
operator|&
literal|0x10
operator|)
operator|==
literal|0x10
decl_stmt|;
specifier|final
name|int
name|attrType
init|=
operator|(
name|data
index|[
name|pos
index|]
operator|&
literal|0x4
operator|)
operator|>>
literal|0x2
decl_stmt|;
name|pos
operator|+=
name|StoredNode
operator|.
name|LENGTH_SIGNATURE_LENGTH
expr_stmt|;
specifier|final
name|int
name|dlnLen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
expr_stmt|;
specifier|final
name|NodeId
name|dln
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromData
argument_list|(
name|dlnLen
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|dln
operator|.
name|size
argument_list|()
expr_stmt|;
specifier|final
name|short
name|id
init|=
operator|(
name|short
operator|)
name|Signatures
operator|.
name|read
argument_list|(
name|idSizeType
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|Signatures
operator|.
name|getLength
argument_list|(
name|idSizeType
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getName
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no symbol for id "
operator|+
name|id
argument_list|)
throw|;
block|}
name|short
name|nsId
init|=
literal|0
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|hasNamespace
condition|)
block|{
name|nsId
operator|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|LENGTH_NS_ID
expr_stmt|;
name|int
name|prefixLen
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|LENGTH_PREFIX_LENGTH
expr_stmt|;
if|if
condition|(
name|prefixLen
operator|>
literal|0
condition|)
block|{
name|prefix
operator|=
name|UTF8
operator|.
name|decode
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|prefixLen
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|pos
operator|+=
name|prefixLen
expr_stmt|;
block|}
specifier|final
name|String
name|namespace
init|=
name|nsId
operator|==
literal|0
condition|?
name|XMLConstants
operator|.
name|NULL_NS_URI
else|:
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getNamespace
argument_list|(
name|nsId
argument_list|)
decl_stmt|;
specifier|final
name|String
name|value
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|len
operator|-
operator|(
name|pos
operator|-
name|start
operator|)
argument_list|,
name|UTF_8
argument_list|)
decl_stmt|;
name|list
operator|.
name|addAttribute
argument_list|(
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSymbols
argument_list|()
operator|.
name|getQName
argument_list|(
name|Node
operator|.
name|ATTRIBUTE_NODE
argument_list|,
name|namespace
argument_list|,
name|name
argument_list|,
name|prefix
argument_list|)
argument_list|,
name|value
argument_list|,
name|attrType
argument_list|,
name|dln
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|getNodeName
argument_list|()
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|attributeType
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
comment|//TODO : range check -pb
name|this
operator|.
name|attributeType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getAttributeType
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|AttrImpl
operator|.
name|ID
case|:
return|return
literal|"ID"
return|;
case|case
name|AttrImpl
operator|.
name|IDREF
case|:
return|return
literal|"IDREF"
return|;
case|case
name|AttrImpl
operator|.
name|IDREFS
case|:
return|return
literal|"IDREFS"
return|;
case|case
name|AttrImpl
operator|.
name|CDATA
case|:
return|return
literal|"CDATA"
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|void
name|setIndexType
parameter_list|(
specifier|final
name|int
name|idxType
parameter_list|)
block|{
name|this
operator|.
name|indexType
operator|=
name|idxType
expr_stmt|;
block|}
specifier|public
name|int
name|getIndexType
parameter_list|()
block|{
return|return
name|indexType
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getValue
parameter_list|()
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNodeValue
parameter_list|()
block|{
return|return
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|DOMException
block|{
name|this
operator|.
name|value
operator|=
operator|new
name|XMLString
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Element
name|getOwnerElement
parameter_list|()
block|{
return|return
operator|(
name|Element
operator|)
name|getOwnerDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeId
operator|.
name|getParentId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getSpecified
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|nodeName
argument_list|)
operator|+
literal|"=\""
operator|+
name|value
operator|+
literal|"\""
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|(
specifier|final
name|boolean
name|top
parameter_list|)
block|{
if|if
condition|(
name|top
condition|)
block|{
return|return
literal|"<exist:attribute "
operator|+
literal|"xmlns:exist=\""
operator|+
name|Namespaces
operator|.
name|EXIST_NS
operator|+
literal|"\" "
operator|+
literal|"exist:id=\""
operator|+
name|getNodeId
argument_list|()
operator|+
literal|"\" exist:source=\""
operator|+
name|getOwnerDocument
argument_list|()
operator|.
name|getFileURI
argument_list|()
operator|+
literal|"\" "
operator|+
name|getNodeName
argument_list|()
operator|+
literal|"=\""
operator|+
name|getValue
argument_list|()
operator|+
literal|"\"/>"
return|;
block|}
else|else
block|{
return|return
name|toString
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getChildCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|TypeInfo
name|getSchemaTypeInfo
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isId
parameter_list|()
block|{
return|return
name|this
operator|.
name|getType
argument_list|()
operator|==
name|ID
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
specifier|final
name|Element
name|e
init|=
name|getOwnerElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
return|return
name|e
operator|.
name|getBaseURI
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|compareDocumentPosition
parameter_list|(
specifier|final
name|Node
name|other
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getTextContent
parameter_list|()
throws|throws
name|DOMException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTextContent
parameter_list|(
specifier|final
name|String
name|textContent
parameter_list|)
throws|throws
name|DOMException
block|{
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isSameNode
parameter_list|(
specifier|final
name|Node
name|other
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|lookupPrefix
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isDefaultNamespace
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|lookupNamespaceURI
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEqualNode
parameter_list|(
specifier|final
name|Node
name|arg
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getFeature
parameter_list|(
specifier|final
name|String
name|feature
parameter_list|,
specifier|final
name|String
name|version
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|setUserData
parameter_list|(
specifier|final
name|String
name|key
parameter_list|,
specifier|final
name|Object
name|data
parameter_list|,
specifier|final
name|UserDataHandler
name|handler
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|getUserData
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

