begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|persistent
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
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
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
name|Error
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
name|Messages
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
name|VariableImpl
implements|implements
name|Variable
block|{
comment|// the name of the variable
specifier|private
specifier|final
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
specifier|private
name|int
name|staticType
init|=
name|Type
operator|.
name|ITEM
decl_stmt|;
specifier|private
name|boolean
name|initialized
init|=
literal|true
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|VariableImpl
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
name|VariableImpl
parameter_list|(
name|VariableImpl
name|var
parameter_list|)
block|{
name|this
argument_list|(
name|var
operator|.
name|qname
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|var
operator|.
name|value
expr_stmt|;
name|this
operator|.
name|contextDocs
operator|=
name|var
operator|.
name|contextDocs
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|var
operator|.
name|type
expr_stmt|;
name|this
operator|.
name|staticType
operator|=
name|var
operator|.
name|staticType
expr_stmt|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|Sequence
name|val
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|val
expr_stmt|;
if|if
condition|(
name|val
operator|instanceof
name|NodeImpl
condition|)
block|{
name|ValueSequence
name|newSeq
init|=
operator|new
name|ValueSequence
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|newSeq
operator|.
name|add
argument_list|(
operator|(
name|Item
operator|)
name|val
argument_list|)
expr_stmt|;
name|newSeq
operator|.
name|setHolderVariable
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|newSeq
expr_stmt|;
block|}
if|else if
condition|(
name|val
operator|instanceof
name|ValueSequence
condition|)
block|{
operator|(
operator|(
name|ValueSequence
operator|)
name|this
operator|.
name|value
operator|)
operator|.
name|setHolderVariable
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
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
block|{
return|return
name|type
operator|.
name|getPrimaryType
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|ITEM
return|;
block|}
block|}
specifier|public
name|void
name|setSequenceType
parameter_list|(
name|SequenceType
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
comment|//Check the value's type if it is already assigned : happens with external variables
if|if
condition|(
name|getValue
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|getSequenceType
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|int
name|actualCardinality
decl_stmt|;
if|if
condition|(
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|EMPTY
expr_stmt|;
block|}
if|else if
condition|(
name|getValue
argument_list|()
operator|.
name|hasMany
argument_list|()
condition|)
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|MANY
expr_stmt|;
block|}
else|else
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|ONE
expr_stmt|;
block|}
comment|//Type.EMPTY is *not* a subtype of other types ; checking cardinality first
if|if
condition|(
operator|!
name|Cardinality
operator|.
name|checkCardinality
argument_list|(
name|getSequenceType
argument_list|()
operator|.
name|getCardinality
argument_list|()
argument_list|,
name|actualCardinality
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XPTY0004: Invalid cardinality for variable $"
operator|+
name|getQName
argument_list|()
operator|+
literal|". Expected "
operator|+
name|Cardinality
operator|.
name|getDescription
argument_list|(
name|getSequenceType
argument_list|()
operator|.
name|getCardinality
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Cardinality
operator|.
name|getDescription
argument_list|(
name|actualCardinality
argument_list|)
argument_list|)
throw|;
block|}
comment|//TODO : ignore nodes right now ; they are returned as xs:untypedAtomicType
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|getSequenceType
argument_list|()
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|,
name|getSequenceType
argument_list|()
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XPTY0004: Invalid type for variable $"
operator|+
name|getQName
argument_list|()
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getSequenceType
argument_list|()
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|//Here is an attempt to process the nodes correctly
block|}
else|else
block|{
comment|//Same as above : we probably may factorize
if|if
condition|(
operator|!
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|,
name|getSequenceType
argument_list|()
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XPTY0004: Invalid type for variable $"
operator|+
name|getQName
argument_list|()
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getSequenceType
argument_list|()
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
block|}
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
name|void
name|setStaticType
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|staticType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|int
name|getStaticType
parameter_list|()
block|{
return|return
name|staticType
return|;
block|}
specifier|public
name|boolean
name|isInitialized
parameter_list|()
block|{
return|return
name|initialized
return|;
block|}
specifier|public
name|void
name|setIsInitialized
parameter_list|(
name|boolean
name|initialized
parameter_list|)
block|{
name|this
operator|.
name|initialized
operator|=
name|initialized
expr_stmt|;
block|}
specifier|public
name|void
name|destroy
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|value
operator|.
name|destroy
argument_list|(
name|context
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"$"
operator|+
name|qname
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|Cardinality
operator|.
name|toString
argument_list|(
name|getCardinality
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"[not set]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
literal|":= "
argument_list|)
operator|.
name|append
argument_list|(
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
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
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_VARS
return|;
block|}
else|else
block|{
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
block|{
return|return;
block|}
name|type
operator|.
name|checkCardinality
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
specifier|final
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
block|{
name|value
operator|=
name|Atomize
operator|.
name|atomize
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|//TODO : we should recheck the dependencies of this method
comment|//and remove that conversion !
if|if
condition|(
name|requiredType
operator|!=
name|Type
operator|.
name|ATOMIC
condition|)
block|{
name|value
operator|=
name|convert
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|type
operator|.
name|checkType
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|Messages
operator|.
name|getMessage
argument_list|(
name|Error
operator|.
name|VAR_TYPE_MISMATCH
argument_list|,
name|toString
argument_list|()
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|value
operator|.
name|getItemType
argument_list|()
argument_list|,
name|value
operator|.
name|getCardinality
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
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
specifier|final
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
specifier|final
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

