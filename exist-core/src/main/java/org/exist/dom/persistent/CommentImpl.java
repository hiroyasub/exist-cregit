begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|value
operator|.
name|StringValue
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
name|Comment
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
name|CommentImpl
extends|extends
name|AbstractCharacterData
implements|implements
name|Comment
block|{
specifier|public
name|CommentImpl
parameter_list|()
block|{
name|super
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CommentImpl
parameter_list|(
specifier|final
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CommentImpl
parameter_list|(
specifier|final
name|char
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|howmany
parameter_list|)
block|{
name|super
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"<!-- "
operator|+
name|cdata
operator|.
name|toString
argument_list|()
operator|+
literal|" -->"
return|;
block|}
comment|/**      * Serializes a (persistent DOM) Comment to a byte array      *      * data = signature nodeIdUnitsLength nodeId cdata      *      * signature = [byte] 0x60      *      * nodeIdUnitsLength = [short] (2 bytes) The number of units of the comment's NodeId      * nodeId = {@link org.exist.numbering.DLNBase#serialize(byte[], int)}      *      * cdata = jUtf8      *      * jUtf8 = {@link java.io.DataOutputStream#writeUTF(java.lang.String)}      */
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|serialize
parameter_list|()
block|{
specifier|final
name|String
name|s
init|=
name|cdata
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|cd
init|=
name|s
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
name|data
init|=
operator|new
name|byte
index|[
name|StoredNode
operator|.
name|LENGTH_SIGNATURE_LENGTH
operator|+
name|NodeId
operator|.
name|LENGTH_NODE_ID_UNITS
operator|+
operator|+
name|nodeIdLen
operator|+
name|cd
operator|.
name|length
index|]
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
name|Comm
operator|<<
literal|0x5
operator|)
expr_stmt|;
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
name|System
operator|.
name|arraycopy
argument_list|(
name|cd
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
name|pos
argument_list|,
name|cd
operator|.
name|length
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
name|String
name|cdata
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
comment|//OK : we have the necessary material to build the comment
specifier|final
name|CommentImpl
name|comment
decl_stmt|;
if|if
condition|(
name|pooled
condition|)
block|{
name|comment
operator|=
operator|(
name|CommentImpl
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
name|COMMENT_NODE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|comment
operator|=
operator|new
name|CommentImpl
argument_list|()
expr_stmt|;
block|}
name|comment
operator|.
name|setNodeId
argument_list|(
name|dln
argument_list|)
expr_stmt|;
name|comment
operator|.
name|appendData
argument_list|(
name|cdata
argument_list|)
expr_stmt|;
return|return
name|comment
return|;
block|}
block|}
end_class

end_unit

