begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|NodeProxy
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

begin_comment
comment|/**  * An abstract implementation of {@link org.exist.xquery.value.Sequence} with  * default implementations for some methods.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSequence
implements|implements
name|Sequence
block|{
specifier|protected
name|AbstractSequence
parameter_list|()
block|{
block|}
specifier|public
specifier|abstract
name|int
name|getItemType
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|SequenceIterator
name|iterate
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|SequenceIterator
name|unorderedIterator
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|int
name|getLength
parameter_list|()
function_decl|;
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
switch|switch
condition|(
name|getLength
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|Cardinality
operator|.
name|EMPTY
return|;
case|case
literal|1
case|:
return|return
name|Cardinality
operator|.
name|EXACTLY_ONE
return|;
default|default:
return|return
name|Cardinality
operator|.
name|ONE_OR_MORE
return|;
block|}
block|}
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
name|Item
name|first
init|=
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|first
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
return|return
operator|(
operator|(
name|AtomicValue
operator|)
name|first
operator|)
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
return|;
else|else
return|return
operator|new
name|StringValue
argument_list|(
name|first
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
return|;
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|Item
name|first
init|=
name|iterate
argument_list|()
operator|.
name|nextItem
argument_list|()
decl_stmt|;
return|return
name|first
operator|.
name|getStringValue
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
return|return
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#add(org.exist.xquery.value.Item) 	 */
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|SequenceIterator
name|i
init|=
name|other
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
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#itemAt(int) 	 */
specifier|public
specifier|abstract
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|int
name|len
init|=
name|getLength
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|len
operator|>
literal|1
condition|)
return|return
literal|true
return|;
name|Item
name|first
init|=
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|instanceof
name|StringValue
condition|)
return|return
name|first
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|?
literal|true
else|:
literal|false
return|;
if|else if
condition|(
name|first
operator|instanceof
name|BooleanValue
condition|)
return|return
operator|(
operator|(
name|BooleanValue
operator|)
name|first
operator|)
operator|.
name|getValue
argument_list|()
return|;
if|else if
condition|(
name|first
operator|instanceof
name|NumericValue
condition|)
return|return
operator|(
operator|(
name|BooleanValue
operator|)
name|first
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
return|;
else|else
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#conversionPreference(java.lang.Class) 	 */
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|Sequence
operator|.
name|class
argument_list|)
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|List
operator|.
name|class
argument_list|)
condition|)
return|return
literal|1
return|;
if|else if
condition|(
name|javaClass
operator|==
name|Object
operator|.
name|class
condition|)
return|return
literal|20
return|;
if|if
condition|(
name|getLength
argument_list|()
operator|>
literal|0
condition|)
return|return
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|conversionPreference
argument_list|(
name|javaClass
argument_list|)
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#toJavaObject(java.lang.Class) 	 */
specifier|public
name|Object
name|toJavaObject
parameter_list|(
name|Class
name|target
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|Sequence
operator|.
name|class
argument_list|)
condition|)
return|return
name|this
return|;
if|else if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|List
operator|.
name|class
argument_list|)
condition|)
block|{
name|List
name|l
init|=
operator|new
name|ArrayList
argument_list|(
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
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
name|l
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
if|if
condition|(
name|getLength
argument_list|()
operator|>
literal|0
condition|)
return|return
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|target
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setSelfAsContext
parameter_list|()
block|{
name|Item
name|next
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|unorderedIterator
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
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
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
name|next
operator|instanceof
name|NodeProxy
condition|)
block|{
name|NodeProxy
name|n
init|=
operator|(
name|NodeProxy
operator|)
name|next
decl_stmt|;
name|n
operator|.
name|addContextNode
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

