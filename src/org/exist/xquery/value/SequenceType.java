begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

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
name|XPathException
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

begin_comment
comment|/**  * Represents an XQuery SequenceType and provides methods to check  * sequences and items against this type.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|SequenceType
block|{
specifier|private
name|int
name|primaryType
init|=
name|Type
operator|.
name|ITEM
decl_stmt|;
specifier|private
name|int
name|cardinality
init|=
name|Cardinality
operator|.
name|EXACTLY_ONE
decl_stmt|;
specifier|private
name|QName
name|nodeName
init|=
literal|null
decl_stmt|;
specifier|public
name|SequenceType
parameter_list|()
block|{
block|}
comment|/**      * Construct a new SequenceType using the specified      * primary type and cardinality constants.      *       * @param primaryType      * @param cardinality      */
specifier|public
name|SequenceType
parameter_list|(
name|int
name|primaryType
parameter_list|,
name|int
name|cardinality
parameter_list|)
block|{
name|this
operator|.
name|primaryType
operator|=
name|primaryType
expr_stmt|;
name|this
operator|.
name|cardinality
operator|=
name|cardinality
expr_stmt|;
block|}
comment|/**      * Returns the primary type as one of the      * constants defined in {@link Type}.      */
specifier|public
name|int
name|getPrimaryType
parameter_list|()
block|{
return|return
name|primaryType
return|;
block|}
specifier|public
name|void
name|setPrimaryType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|this
operator|.
name|primaryType
operator|=
name|type
expr_stmt|;
block|}
comment|/**      * Returns the expected cardinality. See the constants       * defined in {@link Cardinality}.      *       */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|cardinality
return|;
block|}
specifier|public
name|void
name|setCardinality
parameter_list|(
name|int
name|cardinality
parameter_list|)
block|{
name|this
operator|.
name|cardinality
operator|=
name|cardinality
expr_stmt|;
block|}
specifier|public
name|QName
name|getNodeName
parameter_list|()
block|{
return|return
name|nodeName
return|;
block|}
specifier|public
name|void
name|setNodeName
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|this
operator|.
name|nodeName
operator|=
name|qname
expr_stmt|;
block|}
comment|/**      * Check the specified sequence against this SequenceType.      *        * @param seq      * @throws XPathException       * @throws XPathException       */
specifier|public
name|boolean
name|checkType
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|nodeName
operator|!=
literal|null
condition|)
block|{
name|Item
name|next
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|seq
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
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|checkType
argument_list|(
name|next
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|subTypeOf
argument_list|(
name|seq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|primaryType
argument_list|)
return|;
block|}
block|}
comment|/**      * Check a single item against this SequenceType.      *       * @param item      */
specifier|public
name|boolean
name|checkType
parameter_list|(
name|Item
name|item
parameter_list|)
block|{
name|Node
name|realNode
init|=
literal|null
decl_stmt|;
name|int
name|type
init|=
name|item
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|NODE
condition|)
block|{
name|realNode
operator|=
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|type
operator|=
name|realNode
operator|.
name|getNodeType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|primaryType
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|nodeName
operator|!=
literal|null
condition|)
block|{
comment|//TODO : how to improve performance ?
if|if
condition|(
name|realNode
operator|==
literal|null
condition|)
name|realNode
operator|=
operator|(
operator|(
name|NodeValue
operator|)
name|item
operator|)
operator|.
name|getNode
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|nodeName
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|realNode
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
if|if
condition|(
name|nodeName
operator|.
name|getLocalName
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|nodeName
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|realNode
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Check the given type against the primary type      * declared in this SequenceType.      *       * @param type      * @throws XPathException      */
specifier|public
name|void
name|checkType
parameter_list|(
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|EMPTY
operator|||
name|type
operator|==
name|Type
operator|.
name|ITEM
condition|)
return|return;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|primaryType
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: expected type: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|primaryType
argument_list|)
operator|+
literal|"; got: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
argument_list|)
throw|;
block|}
comment|/**      * Check if the given sequence has the cardinality required      * by this sequence type.      *       * @param seq      * @throws XPathException      */
specifier|public
name|void
name|checkCardinality
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|seq
operator|.
name|isEmpty
argument_list|()
operator|&&
name|cardinality
operator|==
name|Cardinality
operator|.
name|EMPTY
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Empty sequence expected; got "
operator|+
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
throw|;
if|if
condition|(
name|seq
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|(
name|cardinality
operator|&
name|Cardinality
operator|.
name|ZERO
operator|)
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Empty sequence is not allowed here"
argument_list|)
throw|;
if|else if
condition|(
name|seq
operator|.
name|hasMany
argument_list|()
operator|&&
operator|(
name|cardinality
operator|&
name|Cardinality
operator|.
name|MANY
operator|)
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Sequence with more than one item is not allowed here"
argument_list|)
throw|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|cardinality
operator|==
name|Cardinality
operator|.
name|EMPTY
condition|)
return|return
name|Cardinality
operator|.
name|toString
argument_list|(
name|cardinality
argument_list|)
return|;
return|return
name|Type
operator|.
name|getTypeName
argument_list|(
name|primaryType
argument_list|)
operator|+
name|Cardinality
operator|.
name|toString
argument_list|(
name|cardinality
argument_list|)
return|;
block|}
block|}
end_class

end_unit

