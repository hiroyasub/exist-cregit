begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|xquery
operator|.
name|AtomicToString
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
name|Atomize
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
name|UntypedValueCheck
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

begin_comment
comment|/**  * @author wolf  */
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
specifier|public
name|SequenceType
parameter_list|()
block|{
block|}
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
name|int
name|items
init|=
name|seq
operator|.
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|items
operator|>
literal|0
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
name|items
argument_list|)
throw|;
if|if
condition|(
name|items
operator|==
literal|0
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
name|items
operator|>
literal|1
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
literal|"empty()"
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
name|display
argument_list|(
name|cardinality
argument_list|)
return|;
block|}
block|}
end_class

end_unit

