begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|MemTreeBuilder
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xpath
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Represents an enclosed expression<code>{expr}</code> inside element  * content. Enclosed expressions within attribute values are processed by  * {@link org.exist.xpath.AttributeConstructor}.  *    * @author Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|EnclosedExpr
extends|extends
name|PathExpr
block|{
comment|/** 	 *  	 */
specifier|public
name|EnclosedExpr
parameter_list|(
name|StaticContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.AbstractExpression#eval(org.exist.xpath.StaticContext, org.exist.dom.DocumentSet, org.exist.xpath.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|Sequence
name|result
init|=
name|super
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|Receiver
name|receiver
init|=
operator|new
name|Receiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
try|try
block|{
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|boolean
name|readNext
init|=
literal|true
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
comment|// if item is an atomic value, collect the string values of all
comment|// following atomic values and seperate them by a space.
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
name|ATOMIC
argument_list|)
condition|)
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
block|}
if|else if
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
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|next
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Encountered SAX exception while serializing enclosed expression: "
operator|+
name|pprint
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.PathExpr#pprint() 	 */
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|'{'
operator|+
name|super
operator|.
name|pprint
argument_list|()
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

