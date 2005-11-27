begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
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
name|Arrays
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|AttrImpl
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
name|CommentImpl
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
name|DocumentImpl
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
name|DocumentTypeImpl
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
name|ElementImpl
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
name|Match
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
name|NodeImpl
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
name|dom
operator|.
name|ProcessingInstructionImpl
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
name|TextImpl
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
name|XMLUtil
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
name|util
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|FastQSort
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|XMLString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|AttrList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
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
comment|/**  * Serializer implementation for the native database backend.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|NativeSerializer
extends|extends
name|Serializer
block|{
specifier|public
specifier|final
specifier|static
name|int
name|EXIST_ID_NONE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EXIST_ID_ELEMENT
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EXIST_ID_ALL
init|=
literal|2
decl_stmt|;
comment|// private final static AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
specifier|private
specifier|final
specifier|static
name|QName
name|MATCH_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"match"
argument_list|,
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|TEXT_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"text"
argument_list|,
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|ATTRIB_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"attribute"
argument_list|,
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|SOURCE_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"source"
argument_list|,
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|ID_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"id"
argument_list|,
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
name|int
name|showId
init|=
name|EXIST_ID_ELEMENT
decl_stmt|;
specifier|public
name|NativeSerializer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|String
name|showIdParam
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
literal|"serialization.add-exist-id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|showIdParam
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|showIdParam
operator|.
name|equals
argument_list|(
literal|"element"
argument_list|)
condition|)
name|showId
operator|=
name|EXIST_ID_ELEMENT
expr_stmt|;
if|else if
condition|(
name|showIdParam
operator|.
name|equals
argument_list|(
literal|"all"
argument_list|)
condition|)
name|showId
operator|=
name|EXIST_ID_ALL
expr_stmt|;
else|else
name|showId
operator|=
name|EXIST_ID_NONE
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|serializeToReceiver
parameter_list|(
name|NodeProxy
name|p
parameter_list|,
name|boolean
name|generateDocEvent
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|p
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|DOCUMENT
argument_list|)
operator|||
name|p
operator|.
name|getGID
argument_list|()
operator|==
name|NodeProxy
operator|.
name|DOCUMENT_NODE_GID
condition|)
block|{
name|serializeToReceiver
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
argument_list|,
name|generateDocEvent
argument_list|)
expr_stmt|;
return|return;
block|}
name|setDocument
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|generateDocEvent
condition|)
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|Iterator
name|domIter
init|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|serializeToReceiver
argument_list|(
literal|null
argument_list|,
name|domIter
argument_list|,
name|p
operator|.
name|getDocument
argument_list|()
argument_list|,
name|p
operator|.
name|getGID
argument_list|()
argument_list|,
literal|true
argument_list|,
name|p
operator|.
name|getMatches
argument_list|()
argument_list|,
operator|new
name|TreeSet
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|generateDocEvent
condition|)
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|serializeToReceiver
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|generateDocEvent
parameter_list|)
throws|throws
name|SAXException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|NodeList
name|children
init|=
name|doc
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|generateDocEvent
condition|)
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|getDoctype
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|OUTPUT_DOCTYPE
argument_list|,
literal|"no"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
specifier|final
name|NodeImpl
name|n
init|=
operator|(
name|NodeImpl
operator|)
name|doc
operator|.
name|getDoctype
argument_list|()
decl_stmt|;
name|serializeToReceiver
argument_list|(
name|n
argument_list|,
literal|null
argument_list|,
operator|(
name|DocumentImpl
operator|)
name|n
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|n
operator|.
name|getGID
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
operator|new
name|TreeSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// iterate through children
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
name|NodeImpl
name|n
init|=
operator|(
name|NodeImpl
operator|)
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|n
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|n
operator|.
name|getGID
argument_list|()
argument_list|,
name|n
operator|.
name|getInternalAddress
argument_list|()
argument_list|)
decl_stmt|;
name|Iterator
name|domIter
init|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|domIter
operator|.
name|next
argument_list|()
expr_stmt|;
name|serializeToReceiver
argument_list|(
name|n
argument_list|,
name|domIter
argument_list|,
operator|(
name|DocumentImpl
operator|)
name|n
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|n
operator|.
name|getGID
argument_list|()
argument_list|,
literal|true
argument_list|,
name|p
operator|.
name|getMatches
argument_list|()
argument_list|,
operator|new
name|TreeSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|DocumentImpl
name|documentImpl
init|=
operator|(
name|DocumentImpl
operator|)
name|doc
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"serializing document "
operator|+
name|documentImpl
operator|.
name|getDocId
argument_list|()
operator|+
literal|" ("
operator|+
name|documentImpl
operator|.
name|getName
argument_list|()
operator|+
literal|")"
operator|+
literal|" to SAX took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|generateDocEvent
condition|)
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|serializeToReceiver
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|Iterator
name|iter
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|,
name|boolean
name|first
parameter_list|,
name|Match
name|match
parameter_list|,
name|Set
name|namespaces
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
name|node
operator|=
operator|(
name|NodeImpl
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
return|return;
comment|// char ch[];
name|String
name|cdata
decl_stmt|;
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
name|ELEMENT_NODE
case|:
name|String
name|defaultNS
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|declaresNamespacePrefixes
argument_list|()
condition|)
block|{
comment|// declare namespaces used by this element
name|String
name|prefix
decl_stmt|,
name|uri
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|getPrefixes
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|prefix
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|defaultNS
operator|=
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|getNamespaceForPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|startPrefixMapping
argument_list|(
literal|""
argument_list|,
name|defaultNS
argument_list|)
expr_stmt|;
name|namespaces
operator|.
name|add
argument_list|(
name|defaultNS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|uri
operator|=
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|getNamespaceForPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|namespaces
operator|.
name|add
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|String
name|ns
init|=
name|defaultNS
operator|==
literal|null
condition|?
name|node
operator|.
name|getNamespaceURI
argument_list|()
else|:
name|defaultNS
decl_stmt|;
if|if
condition|(
name|ns
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|(
operator|!
name|namespaces
operator|.
name|contains
argument_list|(
name|ns
argument_list|)
operator|)
condition|)
name|receiver
operator|.
name|startPrefixMapping
argument_list|(
name|node
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|AttrList
name|attribs
init|=
operator|new
name|AttrList
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|first
operator|&&
name|showId
operator|==
name|EXIST_ID_ELEMENT
operator|)
operator|||
name|showId
operator|==
name|EXIST_ID_ALL
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
name|ID_ATTRIB
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|first
operator|&&
name|showId
operator|>
literal|0
condition|)
block|{
comment|// String src = doc.getCollection().getName() + '/' + doc.getFileName();
name|attribs
operator|.
name|addAttribute
argument_list|(
name|SOURCE_ATTRIB
argument_list|,
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|int
name|children
init|=
name|node
operator|.
name|getChildCount
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|// int childLen;
name|NodeImpl
name|child
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|children
operator|>
literal|0
condition|)
name|gid
operator|=
name|XMLUtil
operator|.
name|getFirstChildId
argument_list|(
name|doc
argument_list|,
name|gid
argument_list|)
expr_stmt|;
while|while
condition|(
name|count
operator|<
name|children
condition|)
block|{
name|child
operator|=
operator|(
name|NodeImpl
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|)
block|{
if|if
condition|(
operator|(
name|getHighlightingMode
argument_list|()
operator|&
name|TAG_ATTRIBUTE_MATCHES
operator|)
operator|>
literal|0
condition|)
name|cdata
operator|=
name|processAttribute
argument_list|(
operator|(
operator|(
name|AttrImpl
operator|)
name|child
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|gid
argument_list|,
name|match
argument_list|)
expr_stmt|;
else|else
name|cdata
operator|=
operator|(
operator|(
name|AttrImpl
operator|)
name|child
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|child
operator|.
name|getQName
argument_list|()
argument_list|,
name|cdata
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
name|gid
operator|++
expr_stmt|;
name|child
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
else|else
break|break;
block|}
name|receiver
operator|.
name|startElement
argument_list|(
name|node
operator|.
name|getQName
argument_list|()
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
while|while
condition|(
name|count
operator|<
name|children
condition|)
block|{
name|serializeToReceiver
argument_list|(
name|child
argument_list|,
name|iter
argument_list|,
name|doc
argument_list|,
name|gid
operator|++
argument_list|,
literal|false
argument_list|,
name|match
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
if|if
condition|(
operator|++
name|count
operator|<
name|children
condition|)
block|{
name|child
operator|=
operator|(
name|NodeImpl
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
break|break;
block|}
name|receiver
operator|.
name|endElement
argument_list|(
name|node
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|declaresNamespacePrefixes
argument_list|()
condition|)
block|{
name|String
name|prefix
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|ElementImpl
operator|)
name|node
operator|)
operator|.
name|getPrefixes
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|prefix
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ns
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|(
operator|!
name|namespaces
operator|.
name|contains
argument_list|(
name|ns
argument_list|)
operator|)
condition|)
name|receiver
operator|.
name|endPrefixMapping
argument_list|(
name|node
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|release
argument_list|()
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
if|if
condition|(
name|first
operator|&&
name|createContainerElements
condition|)
block|{
name|AttrList
name|tattribs
init|=
operator|new
name|AttrList
argument_list|()
decl_stmt|;
if|if
condition|(
name|showId
operator|>
literal|0
condition|)
block|{
name|tattribs
operator|.
name|addAttribute
argument_list|(
name|ID_ATTRIB
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|tattribs
operator|.
name|addAttribute
argument_list|(
name|SOURCE_ATTRIB
argument_list|,
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|startElement
argument_list|(
name|TEXT_ELEMENT
argument_list|,
name|tattribs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|getHighlightingMode
argument_list|()
operator|&
name|TAG_ELEMENT_MATCHES
operator|)
operator|==
name|TAG_ELEMENT_MATCHES
condition|)
name|textToReceiver
argument_list|(
operator|(
name|TextImpl
operator|)
name|node
argument_list|,
name|gid
argument_list|,
name|match
argument_list|)
expr_stmt|;
else|else
block|{
name|receiver
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|TextImpl
operator|)
name|node
operator|)
operator|.
name|getXMLString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|first
operator|&&
name|createContainerElements
condition|)
name|receiver
operator|.
name|endElement
argument_list|(
name|TEXT_ELEMENT
argument_list|)
expr_stmt|;
name|node
operator|.
name|release
argument_list|()
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
if|if
condition|(
operator|(
name|getHighlightingMode
argument_list|()
operator|&
name|TAG_ATTRIBUTE_MATCHES
operator|)
operator|==
name|TAG_ATTRIBUTE_MATCHES
condition|)
name|cdata
operator|=
name|processAttribute
argument_list|(
operator|(
operator|(
name|AttrImpl
operator|)
name|node
operator|)
operator|.
name|getValue
argument_list|()
argument_list|,
name|gid
argument_list|,
name|match
argument_list|)
expr_stmt|;
else|else
name|cdata
operator|=
operator|(
operator|(
name|AttrImpl
operator|)
name|node
operator|)
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|first
operator|&&
name|createContainerElements
condition|)
block|{
name|AttrList
name|tattribs
init|=
operator|new
name|AttrList
argument_list|()
decl_stmt|;
if|if
condition|(
name|showId
operator|>
literal|0
condition|)
block|{
name|tattribs
operator|.
name|addAttribute
argument_list|(
name|ID_ATTRIB
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|tattribs
operator|.
name|addAttribute
argument_list|(
name|SOURCE_ATTRIB
argument_list|,
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tattribs
operator|.
name|addAttribute
argument_list|(
operator|(
operator|(
name|AttrImpl
operator|)
name|node
operator|)
operator|.
name|getQName
argument_list|()
argument_list|,
name|cdata
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|startElement
argument_list|(
name|ATTRIB_ELEMENT
argument_list|,
name|tattribs
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|endElement
argument_list|(
name|ATTRIB_ELEMENT
argument_list|)
expr_stmt|;
block|}
else|else
name|receiver
operator|.
name|attribute
argument_list|(
name|node
operator|.
name|getQName
argument_list|()
argument_list|,
name|cdata
argument_list|)
expr_stmt|;
name|node
operator|.
name|release
argument_list|()
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|DOCUMENT_TYPE_NODE
case|:
name|String
name|systemId
init|=
operator|(
operator|(
name|DocumentTypeImpl
operator|)
name|node
operator|)
operator|.
name|getSystemId
argument_list|()
decl_stmt|;
name|String
name|publicId
init|=
operator|(
operator|(
name|DocumentTypeImpl
operator|)
name|node
operator|)
operator|.
name|getPublicId
argument_list|()
decl_stmt|;
name|String
name|name
init|=
operator|(
operator|(
name|DocumentTypeImpl
operator|)
name|node
operator|)
operator|.
name|getName
argument_list|()
decl_stmt|;
name|receiver
operator|.
name|documentType
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
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
name|ProcessingInstructionImpl
operator|)
name|node
operator|)
operator|.
name|getTarget
argument_list|()
argument_list|,
operator|(
operator|(
name|ProcessingInstructionImpl
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|.
name|release
argument_list|()
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
name|String
name|comment
init|=
operator|(
operator|(
name|CommentImpl
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
decl_stmt|;
name|char
name|data
index|[]
init|=
operator|new
name|char
index|[
name|comment
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|comment
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|comment
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|node
operator|.
name|release
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
specifier|private
specifier|final
name|String
name|processAttribute
parameter_list|(
name|String
name|data
parameter_list|,
name|long
name|gid
parameter_list|,
name|Match
name|match
parameter_list|)
block|{
if|if
condition|(
name|match
operator|==
literal|null
condition|)
return|return
name|data
return|;
comment|// prepare a regular expression to mark match-terms
name|StringBuffer
name|expr
init|=
literal|null
decl_stmt|;
name|Match
name|next
init|=
name|match
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getNodeId
argument_list|()
operator|==
name|gid
condition|)
block|{
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
block|{
name|expr
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|expr
operator|.
name|append
argument_list|(
literal|"\\b("
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expr
operator|.
name|length
argument_list|()
operator|>
literal|5
condition|)
name|expr
operator|.
name|append
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
name|expr
operator|.
name|append
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
name|next
operator|=
name|next
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|expr
operator|!=
literal|null
condition|)
block|{
name|expr
operator|.
name|append
argument_list|(
literal|")\\b"
argument_list|)
expr_stmt|;
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|UNICODE_CASE
argument_list|)
decl_stmt|;
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|data
argument_list|)
decl_stmt|;
return|return
name|matcher
operator|.
name|replaceAll
argument_list|(
literal|"||$1||"
argument_list|)
return|;
block|}
return|return
name|data
return|;
block|}
specifier|private
specifier|final
name|void
name|textToReceiver
parameter_list|(
name|TextImpl
name|text
parameter_list|,
name|long
name|gid
parameter_list|,
name|Match
name|match
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|match
operator|==
literal|null
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|text
operator|.
name|getXMLString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
name|offsets
init|=
literal|null
decl_stmt|;
name|Match
name|next
init|=
name|match
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getNodeId
argument_list|()
operator|==
name|gid
condition|)
block|{
if|if
condition|(
name|offsets
operator|==
literal|null
condition|)
name|offsets
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|int
name|freq
init|=
name|next
operator|.
name|getFrequency
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
name|freq
condition|;
name|i
operator|++
control|)
block|{
name|offsets
operator|.
name|add
argument_list|(
name|next
operator|.
name|getOffset
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|next
operator|=
name|next
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|offsets
operator|!=
literal|null
condition|)
block|{
name|FastQSort
operator|.
name|sort
argument_list|(
name|offsets
argument_list|,
literal|0
argument_list|,
name|offsets
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|XMLString
name|str
init|=
name|text
operator|.
name|getXMLString
argument_list|()
decl_stmt|;
name|Match
operator|.
name|Offset
name|offset
decl_stmt|;
name|int
name|pos
init|=
literal|0
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
name|offsets
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|offset
operator|=
operator|(
name|Match
operator|.
name|Offset
operator|)
name|offsets
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|offset
operator|.
name|getOffset
argument_list|()
operator|>
name|pos
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|offset
operator|.
name|getOffset
argument_list|()
operator|-
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|startElement
argument_list|(
name|MATCH_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|characters
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|offset
operator|.
name|getOffset
argument_list|()
argument_list|,
name|offset
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|endElement
argument_list|(
name|MATCH_ELEMENT
argument_list|)
expr_stmt|;
name|pos
operator|=
name|offset
operator|.
name|getOffset
argument_list|()
operator|+
name|offset
operator|.
name|getLength
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|pos
operator|<
name|str
operator|.
name|length
argument_list|()
condition|)
name|receiver
operator|.
name|characters
argument_list|(
name|str
operator|.
name|substring
argument_list|(
name|pos
argument_list|,
name|str
operator|.
name|length
argument_list|()
operator|-
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|text
operator|.
name|getXMLString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|final
name|void
name|textToReceiver
parameter_list|(
name|String
name|data
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|SAXException
block|{
name|int
name|p0
init|=
literal|0
decl_stmt|,
name|p1
decl_stmt|;
name|boolean
name|inTerm
init|=
literal|false
decl_stmt|;
while|while
condition|(
name|p0
operator|<
name|data
operator|.
name|length
argument_list|()
condition|)
block|{
name|p1
operator|=
name|data
operator|.
name|indexOf
argument_list|(
literal|"||"
argument_list|,
name|p0
argument_list|)
expr_stmt|;
if|if
condition|(
name|p1
operator|<
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|data
operator|.
name|substring
argument_list|(
name|p0
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|inTerm
condition|)
block|{
name|receiver
operator|.
name|startElement
argument_list|(
name|MATCH_ELEMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|characters
argument_list|(
name|data
operator|.
name|substring
argument_list|(
name|p0
argument_list|,
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|endElement
argument_list|(
name|MATCH_ELEMENT
argument_list|)
expr_stmt|;
name|inTerm
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|inTerm
operator|=
literal|true
expr_stmt|;
name|receiver
operator|.
name|characters
argument_list|(
name|data
operator|.
name|substring
argument_list|(
name|p0
argument_list|,
name|p1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|p0
operator|=
name|p1
operator|+
literal|2
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

