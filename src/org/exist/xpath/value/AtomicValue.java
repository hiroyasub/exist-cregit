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
name|xpath
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|Receiver
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
name|xpath
operator|.
name|XPathException
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
name|ContentHandler
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

begin_class
specifier|public
specifier|abstract
class|class
name|AtomicValue
implements|implements
name|Item
implements|,
name|Sequence
block|{
specifier|public
specifier|final
specifier|static
name|AtomicValue
name|EMPTY_VALUE
init|=
operator|new
name|EmptyValue
argument_list|()
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ATOMIC
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getStringValue() 	 */
specifier|public
specifier|abstract
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|compareTo
parameter_list|(
name|int
name|operator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|int
name|compareTo
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|AtomicValue
name|max
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|AtomicValue
name|min
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
block|{
return|return
operator|new
name|SingleItemIterator
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#getItemType() 	 */
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|getType
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#itemAt(int) 	 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|>
literal|0
condition|?
literal|null
else|:
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#toSequence() 	 */
specifier|public
name|Sequence
name|toSequence
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#toSAX(org.exist.storage.DBBroker, org.xml.sax.ContentHandler) 	 */
specifier|public
name|void
name|toSAX
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
specifier|final
name|String
name|s
init|=
name|getStringValue
argument_list|()
decl_stmt|;
name|handler
operator|.
name|characters
argument_list|(
name|s
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|copyTo
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
specifier|final
name|String
name|s
init|=
name|getStringValue
argument_list|()
decl_stmt|;
name|receiver
operator|.
name|characters
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#add(org.exist.xpath.value.Item) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
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
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#atomize() 	 */
specifier|public
name|AtomicValue
name|atomize
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|getStringValue
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#toNodeSet() 	 */
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert value of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to a node set"
argument_list|)
throw|;
block|}
specifier|public
name|String
name|pprint
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
literal|""
return|;
block|}
block|}
specifier|private
specifier|final
specifier|static
class|class
name|EmptyValue
extends|extends
name|AtomicValue
block|{
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.AtomicValue#getStringValue() 		 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.AtomicValue#convertTo(int) 		 */
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert empty value to "
operator|+
name|requiredType
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.AtomicValue#compareTo(java.lang.Object) 		 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|instanceof
name|EmptyValue
condition|)
return|return
literal|0
return|;
else|else
return|return
operator|-
literal|1
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.AtomicValue#itemAt(int) 		 */
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.Item#toSequence() 		 */
specifier|public
name|Sequence
name|toSequence
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.AtomicValue#max(org.exist.xpath.value.AtomicValue) 		 */
specifier|public
name|AtomicValue
name|max
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.Sequence#add(org.exist.xpath.value.Item) 		 */
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.AtomicValue#compareTo(int, org.exist.xpath.value.AtomicValue) 		 */
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|int
name|operator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot compare operand to empty value"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xpath.value.AtomicValue#min(org.exist.xpath.value.AtomicValue) 		 */
specifier|public
name|AtomicValue
name|min
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

