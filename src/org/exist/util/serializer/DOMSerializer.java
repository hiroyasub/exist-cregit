begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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
name|CharacterData
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
name|ProcessingInstruction
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
name|helpers
operator|.
name|NamespaceSupport
import|;
end_import

begin_class
specifier|public
class|class
name|DOMSerializer
block|{
specifier|protected
name|XMLWriter
name|receiver
decl_stmt|;
specifier|protected
name|NamespaceSupport
name|nsSupport
init|=
operator|new
name|NamespaceSupport
argument_list|()
decl_stmt|;
specifier|protected
name|HashMap
name|namespaceDecls
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
decl_stmt|;
specifier|public
name|DOMSerializer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|receiver
operator|=
operator|new
name|XMLIndenter
argument_list|()
expr_stmt|;
block|}
specifier|public
name|DOMSerializer
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|Properties
name|outputProperties
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|outputProperties
operator|=
name|outputProperties
expr_stmt|;
if|if
condition|(
name|outputProperties
operator|==
literal|null
condition|)
block|{
name|outputProperties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|receiver
operator|=
operator|new
name|XMLIndenter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|this
operator|.
name|receiver
operator|.
name|setOutputProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setOutputProperties
parameter_list|(
name|Properties
name|outputProperties
parameter_list|)
block|{
name|this
operator|.
name|outputProperties
operator|=
name|outputProperties
expr_stmt|;
name|receiver
operator|.
name|setOutputProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|receiver
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|nsSupport
operator|.
name|reset
argument_list|()
expr_stmt|;
name|namespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|TransformerException
block|{
name|Node
name|top
init|=
name|node
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|startNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|Node
name|nextNode
init|=
name|node
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextNode
operator|==
literal|null
condition|)
block|{
name|endNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|equals
argument_list|(
name|node
argument_list|)
condition|)
break|break;
name|nextNode
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextNode
operator|==
literal|null
condition|)
block|{
name|node
operator|=
name|node
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
operator|||
operator|(
name|top
operator|!=
literal|null
operator|&&
name|top
operator|.
name|equals
argument_list|(
name|node
argument_list|)
operator|)
condition|)
block|{
name|endNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|nextNode
operator|=
literal|null
expr_stmt|;
break|break;
block|}
block|}
block|}
name|node
operator|=
name|nextNode
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|startNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|TransformerException
block|{
switch|switch
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
case|case
name|Node
operator|.
name|DOCUMENT_FRAGMENT_NODE
case|:
break|break;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|namespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
name|nsSupport
operator|.
name|pushContext
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|startElement
argument_list|(
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|uri
init|=
name|node
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|node
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
name|uri
operator|=
literal|""
expr_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
name|prefix
operator|=
literal|""
expr_stmt|;
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|namespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|// check attributes for required namespace declarations
name|NamedNodeMap
name|attrs
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Attr
name|nextAttr
decl_stmt|;
name|String
name|attrName
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
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|nextAttr
operator|=
operator|(
name|Attr
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrName
operator|=
name|nextAttr
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|attrName
operator|.
name|equals
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
block|{
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
literal|""
argument_list|)
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
name|nextAttr
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|namespaceDecls
operator|.
name|put
argument_list|(
literal|""
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
literal|""
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|attrName
operator|.
name|startsWith
argument_list|(
literal|"xmlns:"
argument_list|)
condition|)
block|{
name|prefix
operator|=
name|attrName
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
expr_stmt|;
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
name|nextAttr
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|namespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|attrName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|>
literal|0
condition|)
block|{
name|prefix
operator|=
name|nextAttr
operator|.
name|getPrefix
argument_list|()
expr_stmt|;
name|uri
operator|=
name|nextAttr
operator|.
name|getNamespaceURI
argument_list|()
expr_stmt|;
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|namespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// output all namespace declarations
name|Map
operator|.
name|Entry
name|nsEntry
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|namespaceDecls
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|nsEntry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|namespace
argument_list|(
operator|(
name|String
operator|)
name|nsEntry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|nsEntry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// output attributes
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|nextAttr
operator|=
operator|(
name|Attr
operator|)
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|attribute
argument_list|(
name|nextAttr
operator|.
name|getName
argument_list|()
argument_list|,
name|nextAttr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
name|receiver
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|CharacterData
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
break|break;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
name|receiver
operator|.
name|processingInstruction
argument_list|(
operator|(
operator|(
name|ProcessingInstruction
operator|)
name|node
operator|)
operator|.
name|getTarget
argument_list|()
argument_list|,
operator|(
operator|(
name|ProcessingInstruction
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
name|receiver
operator|.
name|comment
argument_list|(
operator|(
operator|(
name|Comment
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default :
break|break;
block|}
block|}
specifier|protected
name|void
name|endNode
parameter_list|(
name|Node
name|node
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|nsSupport
operator|.
name|popContext
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

