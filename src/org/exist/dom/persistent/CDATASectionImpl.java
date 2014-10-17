begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|w3c
operator|.
name|dom
operator|.
name|CDATASection
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
name|Text
import|;
end_import

begin_class
specifier|public
class|class
name|CDATASectionImpl
extends|extends
name|AbstractCharacterData
implements|implements
name|CDATASection
block|{
specifier|public
name|CDATASectionImpl
parameter_list|()
block|{
name|super
argument_list|(
name|Node
operator|.
name|CDATA_SECTION_NODE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CDATASectionImpl
parameter_list|(
specifier|final
name|NodeId
name|nodeId
parameter_list|,
specifier|final
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|CDATA_SECTION_NODE
argument_list|,
name|nodeId
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CDATASectionImpl
parameter_list|(
specifier|final
name|NodeId
name|nodeId
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|CDATA_SECTION_NODE
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CDATASectionImpl
parameter_list|(
specifier|final
name|XMLString
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|CDATA_SECTION_NODE
argument_list|)
expr_stmt|;
name|this
operator|.
name|cdata
operator|=
name|data
expr_stmt|;
block|}
annotation|@
name|Override
comment|/**      * Serializes a (persistent DOM) CDATA Section to a byte array      *      * data = signature nodeIdUnitsLength nodeId cdata      *      * signature = [byte] 0xA0      *      * nodeIdUnitsLength = [short] (2 bytes) The number of units of the cdata section's NodeId      * nodeId = {@see org.exist.numbering.DLNBase#serialize(byte[], int)}      *      * cdata = eUtf8      *      * eUtf8 = {@see org.exist.util.UTF8#encode(java.lang.String, byte[], int)}      */
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
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
name|cdata
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
name|Cdata
operator|<<
literal|0x5
operator|)
expr_stmt|;
name|pos
operator|+=
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
name|cdata
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
name|pos
operator|+=
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
name|int
name|nodeIdLen
init|=
name|dln
operator|.
name|size
argument_list|()
decl_stmt|;
name|pos
operator|+=
name|nodeIdLen
expr_stmt|;
specifier|final
name|CDATASectionImpl
name|cdata
init|=
operator|new
name|CDATASectionImpl
argument_list|(
name|dln
argument_list|)
decl_stmt|;
name|cdata
operator|.
name|cdata
operator|=
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
expr_stmt|;
return|return
name|cdata
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getWholeText
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
name|isElementContentWhitespace
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Text
name|replaceWholeText
parameter_list|(
specifier|final
name|String
name|content
parameter_list|)
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
name|Text
name|splitText
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

