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
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|persistent
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
name|persistent
operator|.
name|CDATASectionImpl
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
name|persistent
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
name|persistent
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
name|persistent
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
name|persistent
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
name|persistent
operator|.
name|IStoredNode
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
name|persistent
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
name|persistent
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
name|persistent
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
name|persistent
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
name|numbering
operator|.
name|NodeId
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|storage
operator|.
name|dom
operator|.
name|INodeIterator
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
comment|// private final static AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
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
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
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
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
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
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
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
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|MATCHES_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"matches"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|MATCHES_OFFSET_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"matches-offset"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|MATCHES_LENGTH_ATTRIB
init|=
operator|new
name|QName
argument_list|(
literal|"matches-length"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS_PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Pattern
name|P_ZERO_VALUES
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"0(,0)?"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Matcher
name|M_ZERO_VALUES
init|=
name|P_ZERO_VALUES
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
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
name|this
argument_list|(
name|broker
argument_list|,
name|config
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NativeSerializer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|chainOfReceivers
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|config
argument_list|,
name|chainOfReceivers
argument_list|)
expr_stmt|;
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
parameter_list|,
name|boolean
name|checkAttributes
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
name|getNodeId
argument_list|()
operator|==
name|NodeId
operator|.
name|DOCUMENT_NODE
condition|)
block|{
name|serializeToReceiver
argument_list|(
name|p
operator|.
name|getOwnerDocument
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
name|getOwnerDocument
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|generateDocEvent
condition|)
block|{
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|INodeIterator
name|domIter
init|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|p
argument_list|)
init|)
block|{
name|serializeToReceiver
argument_list|(
literal|null
argument_list|,
name|domIter
argument_list|,
name|p
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
name|checkAttributes
argument_list|,
name|p
operator|.
name|getMatches
argument_list|()
argument_list|,
operator|new
name|TreeSet
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to close node iterator"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|generateDocEvent
condition|)
block|{
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
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
specifier|final
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
specifier|final
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
block|{
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
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
literal|"yes"
operator|.
name|equals
argument_list|(
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|OUTPUT_DOCTYPE
argument_list|,
literal|"no"
argument_list|)
argument_list|)
condition|)
block|{
specifier|final
name|DocumentTypeImpl
name|docType
init|=
operator|(
name|DocumentTypeImpl
operator|)
name|doc
operator|.
name|getDoctype
argument_list|()
decl_stmt|;
name|serializeToReceiver
argument_list|(
name|docType
argument_list|,
literal|null
argument_list|,
name|docType
operator|.
name|getOwnerDocument
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
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
name|IStoredNode
argument_list|<
name|?
argument_list|>
name|node
init|=
operator|(
name|IStoredNode
argument_list|<
name|?
argument_list|>
operator|)
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|INodeIterator
name|domIter
init|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|node
argument_list|)
init|)
block|{
name|domIter
operator|.
name|next
argument_list|()
expr_stmt|;
specifier|final
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|serializeToReceiver
argument_list|(
name|node
argument_list|,
name|domIter
argument_list|,
operator|(
name|DocumentImpl
operator|)
name|node
operator|.
name|getOwnerDocument
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
argument_list|<
name|String
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to close node iterator"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|generateDocEvent
condition|)
block|{
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"serializing document "
operator|+
name|doc
operator|.
name|getDocId
argument_list|()
operator|+
literal|" ("
operator|+
name|doc
operator|.
name|getURI
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
operator|+
literal|" msec"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|serializeToReceiver
parameter_list|(
name|IStoredNode
name|node
parameter_list|,
name|INodeIterator
name|iter
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|first
parameter_list|,
name|Match
name|match
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
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
operator|&&
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|node
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
name|receiver
operator|.
name|setCurrentNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
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
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
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
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefix
operator|.
name|isEmpty
argument_list|()
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
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
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
specifier|final
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
operator|!=
literal|null
operator|&&
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
block|{
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
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
expr_stmt|;
block|}
name|receiver
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|ns
argument_list|)
expr_stmt|;
block|}
specifier|final
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
name|node
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|/*               * This is a proposed fix-up that the serializer could do              * to make sure elements always have the namespace declarations              *             } else {                // This is fix-up for when the node has a namespace but there is no                // namespace declaration.                String elementNS = node.getNamespaceURI();                Node parent = node.getParentNode();                if (parent instanceof ElementImpl) {                   ElementImpl parentElement = (ElementImpl)parent;                   String declaredNS = parentElement.getNamespaceForPrefix(node.getPrefix());                   if (elementNS!=null&& declaredNS==null) {                      // We need to declare the prefix as it was missed somehow                      receiver.startPrefixMapping(node.getPrefix(), elementNS);                   } else if (elementNS==null&& declaredNS!=null) {                      // We need to declare the default namespace to be the no namespace                      receiver.startPrefixMapping(node.getPrefix(), elementNS);                   } else if (!elementNS.equals(defaultNS)) {                      // Same prefix but different namespace                      receiver.startPrefixMapping(node.getPrefix(), elementNS);                   }                } else if (elementNS!=null) {                   // If the parent is the document, we must have a namespace                   // declaration when there is a namespace URI.                   receiver.startPrefixMapping(node.getPrefix(), elementNS);                }              */
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
comment|// String src = doc.getCollection().getName() + "/" + doc.getFileName();
name|attribs
operator|.
name|addAttribute
argument_list|(
name|SOURCE_ATTRIB
argument_list|,
name|doc
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
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
name|IStoredNode
name|child
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|matchAttrCdata
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|matchAttrOffsetsCdata
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|matchAttrLengthsCdata
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|children
condition|)
block|{
name|child
operator|=
name|iter
operator|.
name|hasNext
argument_list|()
condition|?
name|iter
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
if|if
condition|(
name|child
operator|!=
literal|null
operator|&&
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
operator|==
name|TAG_ATTRIBUTE_MATCHES
operator|&&
name|match
operator|!=
literal|null
operator|&&
name|child
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|match
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|matchAttrCdata
operator|==
literal|null
condition|)
block|{
name|matchAttrCdata
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|matchAttrOffsetsCdata
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
name|matchAttrLengthsCdata
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|matchAttrCdata
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|matchAttrOffsetsCdata
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|matchAttrLengthsCdata
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|matchAttrCdata
operator|.
name|append
argument_list|(
name|child
operator|.
name|getQName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|matchAttrOffsetsCdata
operator|.
name|append
argument_list|(
name|match
operator|.
name|getOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|matchAttrLengthsCdata
operator|.
name|append
argument_list|(
name|match
operator|.
name|getOffset
argument_list|(
literal|0
argument_list|)
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|match
operator|=
name|match
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
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
name|child
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|matchAttrCdata
operator|!=
literal|null
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
name|MATCHES_ATTRIB
argument_list|,
name|matchAttrCdata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|//mask the full-text index which doesn't provide offset and length
name|M_ZERO_VALUES
operator|.
name|reset
argument_list|(
name|matchAttrOffsetsCdata
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|offsetsIsZero
init|=
name|M_ZERO_VALUES
operator|.
name|matches
argument_list|()
decl_stmt|;
name|M_ZERO_VALUES
operator|.
name|reset
argument_list|(
name|matchAttrLengthsCdata
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|lengthsIsZero
init|=
name|M_ZERO_VALUES
operator|.
name|matches
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|offsetsIsZero
operator|&&
operator|!
name|lengthsIsZero
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
name|MATCHES_OFFSET_ATTRIB
argument_list|,
name|matchAttrOffsetsCdata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|MATCHES_LENGTH_ATTRIB
argument_list|,
name|matchAttrLengthsCdata
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|receiver
operator|.
name|setCurrentNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
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
name|iter
operator|.
name|hasNext
argument_list|()
condition|?
name|iter
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
name|receiver
operator|.
name|setCurrentNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
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
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|String
argument_list|>
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
specifier|final
name|String
name|prefix
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
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
operator|!=
literal|null
operator|&&
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
block|{
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
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
expr_stmt|;
block|}
name|receiver
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
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
specifier|final
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
name|node
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
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
name|getFileURI
argument_list|()
operator|.
name|toString
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
name|receiver
operator|.
name|setCurrentNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|first
operator|&&
name|createContainerElements
condition|)
block|{
name|receiver
operator|.
name|endElement
argument_list|(
name|TEXT_ELEMENT
argument_list|)
expr_stmt|;
block|}
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
operator|&&
name|match
operator|!=
literal|null
operator|&&
name|node
operator|.
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|match
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
block|{
comment|//TODO(AR) do we need to expand attribute matches here also? see {@code matchAttrCdata} above
block|}
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
condition|)
block|{
if|if
condition|(
name|createContainerElements
condition|)
block|{
specifier|final
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
name|node
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
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
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tattribs
operator|.
name|addAttribute
argument_list|(
name|node
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
block|{
if|if
condition|(
name|this
operator|.
name|outputProperties
operator|.
name|getProperty
argument_list|(
literal|"output-method"
argument_list|)
operator|!=
literal|null
operator|&&
literal|"text"
operator|.
name|equals
argument_list|(
name|this
operator|.
name|outputProperties
operator|.
name|getProperty
argument_list|(
literal|"output-method"
argument_list|)
argument_list|)
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|node
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error SENR0001: attribute '"
operator|+
name|node
operator|.
name|getQName
argument_list|()
operator|+
literal|"' has no parent element. "
operator|+
literal|"While serializing document "
operator|+
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Error SENR0001: attribute '"
operator|+
name|node
operator|.
name|getQName
argument_list|()
operator|+
literal|"' has no parent element"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
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
block|}
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
case|case
name|Node
operator|.
name|CDATA_SECTION_NODE
case|:
specifier|final
name|String
name|str
init|=
operator|(
operator|(
name|CDATASectionImpl
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|str
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|data
operator|=
operator|new
name|char
index|[
name|str
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
name|str
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|str
operator|.
name|length
argument_list|()
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|cdataSection
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
block|}
break|break;
comment|//TODO : how to process other types ? -pb
block|}
block|}
block|}
end_class

end_unit

