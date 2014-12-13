begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
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
name|pool
operator|.
name|NodePool
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
name|ProcessingInstruction
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

begin_comment
comment|/**  * Persistent implementation of a DOM processing-instruction node.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ProcessingInstructionImpl
extends|extends
name|StoredNode
implements|implements
name|ProcessingInstruction
block|{
specifier|public
specifier|static
specifier|final
name|int
name|LENGTH_TARGET_DATA
init|=
literal|4
decl_stmt|;
comment|//Sizeof int;
specifier|protected
name|String
name|target
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|data
init|=
literal|null
decl_stmt|;
specifier|public
name|ProcessingInstructionImpl
parameter_list|()
block|{
name|super
argument_list|(
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ProcessingInstructionImpl
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
argument_list|,
name|nodeId
argument_list|)
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
specifier|public
name|ProcessingInstructionImpl
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|target
argument_list|,
name|data
argument_list|)
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
name|target
operator|=
literal|null
expr_stmt|;
name|data
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      *  Gets the target attribute of the ProcessingInstructionImpl object      *      *@return    The target value      */
specifier|public
name|String
name|getTarget
parameter_list|()
block|{
return|return
name|target
return|;
block|}
comment|/**      *  Sets the target attribute of the ProcessingInstructionImpl object      *      *@param  target  The new target value      */
specifier|public
name|void
name|setTarget
parameter_list|(
specifier|final
name|String
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.w3c.dom.Node#getNodeName()      */
annotation|@
name|Override
specifier|public
name|String
name|getNodeName
parameter_list|()
block|{
return|return
name|target
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|target
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/**      *  Gets the data attribute of the ProcessingInstructionImpl object      *      *@return    The data value      */
specifier|public
name|String
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
comment|/**      *  Sets the data attribute of the ProcessingInstructionImpl object      *      *@param  data  The new data value      */
specifier|public
name|void
name|setData
parameter_list|(
specifier|final
name|String
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
comment|/** ? @see org.w3c.dom.Node#getBaseURI()      */
annotation|@
name|Override
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
specifier|final
name|StoredNode
name|parent
init|=
name|getParentStoredNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
return|return
name|parent
operator|.
name|getBaseURI
argument_list|()
return|;
block|}
return|return
name|getDocument
argument_list|()
operator|.
name|getBaseURI
argument_list|()
return|;
block|}
comment|/**      *  Description of the Method      *      *@return    Description of the Return Value      */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<?"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" ?>"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Serializes a (persistent DOM) Processing Instruction to a byte array      *      * data = signature nodeIdUnitsLength nodeId targetLength target contentLength content      *      * signature = [byte] 0x40      *      * nodeIdUnitsLength = [short] (2 bytes) The number of units of the processing instruction's NodeId      * nodeId = {@see org.exist.numbering.DLNBase#serialize(byte[], int)}      *      * targetLength = [int] (4 bytes) The length of the target string in bytes      * target = jUtf8      *      * contentLength = [int] (4 bytes) The length of the data string in bytes      * content = jUtf8      *      * jUtf8 = {@see java.io.DataOutputStream#writeUTF(java.lang.String)}      */
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
specifier|final
name|byte
index|[]
name|td
init|=
name|target
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|dd
init|=
name|data
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
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
name|d
init|=
operator|new
name|byte
index|[
name|td
operator|.
name|length
operator|+
name|dd
operator|.
name|length
operator|+
name|nodeIdLen
operator|+
literal|7
index|]
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
name|d
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
name|Proc
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
name|d
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
name|d
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|nodeIdLen
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|td
operator|.
name|length
argument_list|,
name|d
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|LENGTH_TARGET_DATA
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|td
argument_list|,
literal|0
argument_list|,
name|d
argument_list|,
name|pos
argument_list|,
name|td
operator|.
name|length
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|td
operator|.
name|length
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|dd
argument_list|,
literal|0
argument_list|,
name|d
argument_list|,
name|pos
argument_list|,
name|dd
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|d
return|;
block|}
specifier|public
specifier|static
name|StoredNode
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
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
name|int
name|l
init|=
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|pos
operator|+=
name|LENGTH_TARGET_DATA
expr_stmt|;
name|String
name|target
decl_stmt|;
name|target
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|pos
argument_list|,
name|l
argument_list|,
name|UTF_8
argument_list|)
expr_stmt|;
name|pos
operator|+=
name|l
expr_stmt|;
name|String
name|cdata
decl_stmt|;
name|cdata
operator|=
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
expr_stmt|;
comment|//OK : we have the necessary material to build the processing instruction
name|ProcessingInstructionImpl
name|pi
decl_stmt|;
if|if
condition|(
name|pooled
condition|)
block|{
name|pi
operator|=
operator|(
name|ProcessingInstructionImpl
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
name|PROCESSING_INSTRUCTION_NODE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|pi
operator|=
operator|new
name|ProcessingInstructionImpl
argument_list|()
expr_stmt|;
block|}
name|pi
operator|.
name|setTarget
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|pi
operator|.
name|data
operator|=
name|cdata
expr_stmt|;
name|pi
operator|.
name|setNodeId
argument_list|(
name|dln
argument_list|)
expr_stmt|;
return|return
name|pi
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChildNodes
parameter_list|()
block|{
return|return
literal|false
return|;
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
comment|//bad implementations don't call hasChildNodes before
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

