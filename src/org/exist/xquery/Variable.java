begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|DocumentSet
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
name|xquery
operator|.
name|value
operator|.
name|Item
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * An XQuery/XPath variable, consisting of a QName and a value.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Variable
block|{
comment|// the name of the variable
specifier|private
name|QName
name|qname
decl_stmt|;
comment|// the current value assigned to the variable
specifier|private
name|Sequence
name|value
init|=
literal|null
decl_stmt|;
comment|// the context position of this variable in the local variable stack
comment|// this can be used to determine if a variable has been declared
comment|// before another
specifier|private
name|int
name|positionInStack
init|=
literal|0
decl_stmt|;
comment|// the context document set
specifier|private
name|DocumentSet
name|contextDocs
init|=
literal|null
decl_stmt|;
comment|// the sequence type of this variable if known
specifier|private
name|SequenceType
name|type
init|=
literal|null
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|Variable
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|Sequence
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|Sequence
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
return|return
name|type
operator|.
name|getPrimaryType
argument_list|()
return|;
else|else
return|return
name|Type
operator|.
name|ITEM
return|;
block|}
specifier|public
name|void
name|setSequenceType
parameter_list|(
name|SequenceType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|SequenceType
name|getSequenceType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"$"
operator|+
name|qname
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|getDependencies
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
comment|//		if(context.getCurrentStackSize()> positionInStack)
comment|//			return Dependency.CONTEXT_SET + Dependency.GLOBAL_VARS+ Dependency.CONTEXT_ITEM;
comment|//		else
comment|//			return Dependency.CONTEXT_SET + Dependency.LOCAL_VARS;
if|if
condition|(
name|context
operator|.
name|getCurrentStackSize
argument_list|()
operator|>
name|positionInStack
condition|)
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_VARS
return|;
else|else
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|LOCAL_VARS
return|;
block|}
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|ZERO_OR_MORE
return|;
block|}
specifier|public
name|void
name|setStackPosition
parameter_list|(
name|int
name|position
parameter_list|)
block|{
name|positionInStack
operator|=
name|position
expr_stmt|;
block|}
specifier|public
name|DocumentSet
name|getContextDocs
parameter_list|()
block|{
return|return
name|contextDocs
return|;
block|}
specifier|public
name|void
name|setContextDocs
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
block|{
name|this
operator|.
name|contextDocs
operator|=
name|docs
expr_stmt|;
block|}
specifier|public
name|void
name|checkType
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
return|return;
name|type
operator|.
name|checkCardinality
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|int
name|requiredType
init|=
name|type
operator|.
name|getPrimaryType
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|requiredType
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
name|value
operator|=
name|Atomize
operator|.
name|atomize
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|requiredType
operator|!=
name|Type
operator|.
name|ATOMIC
condition|)
name|value
operator|=
name|convert
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|,
name|requiredType
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The type of variable "
operator|+
name|toString
argument_list|()
operator|+
literal|" does not match the declared type: "
operator|+
name|type
argument_list|)
throw|;
block|}
specifier|private
name|Sequence
name|convert
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Item
name|item
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
name|item
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|item
operator|.
name|convertTo
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

