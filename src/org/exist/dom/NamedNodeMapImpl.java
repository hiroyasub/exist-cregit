begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  * $Id:  */
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
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ListIterator
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
name|NamedNodeMap
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

begin_class
specifier|public
class|class
name|NamedNodeMapImpl
extends|extends
name|LinkedList
implements|implements
name|NamedNodeMap
block|{
specifier|public
name|NamedNodeMapImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|size
argument_list|()
return|;
block|}
specifier|public
name|Node
name|setNamedItem
parameter_list|(
name|Node
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
name|arg
return|;
block|}
specifier|public
name|Node
name|setNamedItemNS
parameter_list|(
name|Node
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
return|return
name|setNamedItem
argument_list|(
name|arg
argument_list|)
return|;
block|}
specifier|public
name|Node
name|item
parameter_list|(
name|int
name|index
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
name|size
argument_list|()
condition|)
return|return
operator|(
name|Node
operator|)
name|get
argument_list|(
name|index
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|Node
name|getNamedItem
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|i
init|=
name|indexOf
argument_list|(
operator|new
name|QName
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
operator|(
name|i
operator|<
literal|0
operator|)
condition|?
literal|null
else|:
operator|(
name|Node
operator|)
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
specifier|public
name|Node
name|getNamedItemNS
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|int
name|i
init|=
name|indexOf
argument_list|(
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|namespaceURI
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|(
name|i
operator|<
literal|0
operator|)
condition|?
literal|null
else|:
operator|(
name|Node
operator|)
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
specifier|public
name|Node
name|removeNamedItem
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|DOMException
block|{
name|int
name|i
init|=
name|indexOf
argument_list|(
operator|new
name|QName
argument_list|(
name|name
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|Node
name|removeNamedItemNS
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|DOMException
block|{
name|int
name|i
init|=
name|indexOf
argument_list|(
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|namespaceURI
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|private
name|int
name|indexOf
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
name|ListIterator
name|i
init|=
name|this
operator|.
name|listIterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|temp
init|=
operator|(
name|Node
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|temp
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|&&
name|temp
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|name
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
return|return
name|i
operator|.
name|previousIndex
argument_list|()
return|;
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

