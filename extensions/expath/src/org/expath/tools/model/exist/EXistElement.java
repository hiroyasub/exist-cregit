begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist EXPath  *  Copyright (C) 2011 Adam Retter<adam@existsolutions.com>  *  www.existsolutions.com  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|XQueryContext
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
name|NodeValue
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

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|ToolsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|tools
operator|.
name|model
operator|.
name|Sequence
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
name|Attr
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
import|import
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|EXistElement
implements|implements
name|Element
block|{
specifier|private
specifier|final
name|NodeValue
name|element
decl_stmt|;
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|EXistElement
parameter_list|(
specifier|final
name|NodeValue
name|element
parameter_list|,
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|element
operator|=
name|element
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Attribute
argument_list|>
name|attributes
parameter_list|()
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Attribute
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Attribute
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Attribute
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|int
name|length
init|=
name|attrs
operator|.
name|getLength
argument_list|()
decl_stmt|;
specifier|private
name|int
name|position
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|position
operator|<
name|length
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Attribute
name|next
parameter_list|()
block|{
if|if
condition|(
name|position
operator|>=
name|length
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
operator|new
name|EXistAttribute
argument_list|(
operator|(
name|Attr
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|position
operator|++
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Element
argument_list|>
name|children
parameter_list|()
block|{
specifier|final
name|Node
name|node
init|=
name|element
operator|.
name|getNode
argument_list|()
decl_stmt|;
return|return
operator|new
name|IterableElement
argument_list|(
name|node
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAttribute
parameter_list|(
specifier|final
name|String
name|local_name
parameter_list|)
block|{
name|String
name|attrValue
init|=
literal|null
decl_stmt|;
specifier|final
name|NamedNodeMap
name|attrs
init|=
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
specifier|final
name|Node
name|attr
init|=
name|attrs
operator|.
name|getNamedItem
argument_list|(
name|local_name
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|!=
literal|null
condition|)
block|{
name|attrValue
operator|=
operator|(
operator|(
name|Attr
operator|)
name|attr
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
return|return
name|attrValue
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|getContent
parameter_list|()
block|{
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|valueSequence
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|NodeList
name|children
init|=
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|child
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|valueSequence
operator|.
name|add
argument_list|(
operator|(
name|NodeValue
operator|)
name|child
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|EXistSequence
argument_list|(
name|valueSequence
argument_list|,
name|context
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDisplayName
parameter_list|()
block|{
return|return
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeName
argument_list|()
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
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getLocalName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceUri
parameter_list|()
block|{
return|return
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNoNsChild
parameter_list|()
block|{
specifier|final
name|NodeList
name|children
init|=
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|child
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
operator|&&
name|child
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
operator|||
name|child
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Element
argument_list|>
name|children
parameter_list|(
specifier|final
name|String
name|ns
parameter_list|)
block|{
specifier|final
name|Node
name|node
init|=
name|element
operator|.
name|getNode
argument_list|()
decl_stmt|;
return|return
operator|new
name|IterableElement
argument_list|(
name|node
argument_list|,
name|ns
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|noOtherNCNameAttribute
parameter_list|(
specifier|final
name|String
index|[]
name|names
parameter_list|,
name|String
index|[]
name|forbidden_ns
parameter_list|)
throws|throws
name|ToolsException
block|{
if|if
condition|(
name|forbidden_ns
operator|==
literal|null
condition|)
block|{
name|forbidden_ns
operator|=
operator|new
name|String
index|[]
block|{ }
expr_stmt|;
block|}
specifier|final
name|String
index|[]
name|sorted_names
init|=
name|sortCopy
argument_list|(
name|names
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|sorted_ns
init|=
name|sortCopy
argument_list|(
name|forbidden_ns
argument_list|)
decl_stmt|;
specifier|final
name|NamedNodeMap
name|attributes
init|=
name|element
operator|.
name|getNode
argument_list|()
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attributes
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|attr
init|=
name|attributes
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|attr_name
init|=
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|ns
init|=
name|attr
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|sorted_ns
argument_list|,
name|ns
argument_list|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|ns
operator|!=
literal|null
operator|&&
name|ns
operator|.
name|equals
argument_list|(
name|HttpConstants
operator|.
name|HTTP_CLIENT_NS_URI
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ToolsException
argument_list|(
literal|"@"
operator|+
name|attr_name
operator|+
literal|" in namespace "
operator|+
name|ns
operator|+
literal|" not allowed on "
operator|+
name|getDisplayName
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
block|{
comment|// ignore other-namespace-attributes
block|}
if|else if
condition|(
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|sorted_names
argument_list|,
name|attr
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|ToolsException
argument_list|(
literal|"@"
operator|+
name|attr_name
operator|+
literal|" not allowed on "
operator|+
name|getDisplayName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|String
index|[]
name|sortCopy
parameter_list|(
specifier|final
name|String
index|[]
name|array
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|sorted
init|=
operator|new
name|String
index|[
name|array
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|sorted
argument_list|,
literal|0
argument_list|,
name|sorted
operator|.
name|length
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|sorted
argument_list|)
expr_stmt|;
return|return
name|sorted
return|;
block|}
annotation|@
name|Override
specifier|public
name|QName
name|parseQName
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|ToolsException
block|{
try|try
block|{
specifier|final
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
name|qn
init|=
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|value
argument_list|,
name|element
operator|.
name|getQName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|qn
operator|.
name|toJavaQName
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ToolsException
argument_list|(
literal|"Error parsing the literal QName: "
operator|+
name|value
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
specifier|public
class|class
name|IterableElement
implements|implements
name|Iterable
argument_list|<
name|Element
argument_list|>
block|{
specifier|private
specifier|final
name|Node
name|node
decl_stmt|;
specifier|private
name|String
name|inNamespaceURI
init|=
literal|null
decl_stmt|;
specifier|public
name|IterableElement
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
specifier|public
name|IterableElement
parameter_list|(
name|Node
name|node
parameter_list|,
name|String
name|inNamespaceURI
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|inNamespaceURI
operator|=
name|inNamespaceURI
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Element
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ElementIterator
argument_list|(
name|node
argument_list|,
name|inNamespaceURI
argument_list|)
return|;
block|}
block|}
specifier|public
class|class
name|ElementIterator
implements|implements
name|Iterator
argument_list|<
name|Element
argument_list|>
block|{
specifier|private
specifier|final
name|Node
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|inNamespaceURI
decl_stmt|;
specifier|private
name|List
argument_list|<
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
argument_list|>
name|elements
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|position
init|=
literal|0
decl_stmt|;
specifier|public
name|ElementIterator
parameter_list|(
name|Node
name|parent
parameter_list|,
name|String
name|inNamespaceURI
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|inNamespaceURI
operator|=
name|inNamespaceURI
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|position
operator|<
name|getLength
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Element
name|next
parameter_list|()
block|{
if|if
condition|(
name|position
operator|>=
name|getLength
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
operator|new
name|EXistElement
argument_list|(
operator|(
name|NodeValue
operator|)
name|getElements
argument_list|()
operator|.
name|get
argument_list|(
name|position
operator|++
argument_list|)
argument_list|,
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
specifier|private
name|int
name|getLength
parameter_list|()
block|{
return|return
name|getElements
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**          * lazy initialised          */
specifier|private
name|List
argument_list|<
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
argument_list|>
name|getElements
parameter_list|()
block|{
if|if
condition|(
name|elements
operator|==
literal|null
condition|)
block|{
name|elements
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
specifier|final
name|NodeList
name|children
init|=
name|parent
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Node
name|child
init|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
if|if
condition|(
name|inNamespaceURI
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|ns
init|=
name|child
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|ns
operator|!=
literal|null
operator|&&
name|inNamespaceURI
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
block|{
name|elements
operator|.
name|add
argument_list|(
operator|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
operator|)
name|child
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|elements
operator|.
name|add
argument_list|(
operator|(
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
operator|)
name|child
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|elements
return|;
block|}
block|}
block|}
end_class

end_unit

