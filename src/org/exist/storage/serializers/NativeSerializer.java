begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id:  */
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
name|Iterator
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
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|perl
operator|.
name|Perl5Util
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
name|NodeSet
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    13. April 2002  */
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
specifier|private
name|int
name|showId
init|=
name|EXIST_ID_ELEMENT
decl_stmt|;
specifier|private
name|Perl5Util
name|reutil
init|=
operator|new
name|Perl5Util
argument_list|()
decl_stmt|;
comment|/** 	 *  Constructor for the NativeSerializer object 	 * 	 *@param  broker  Description of the Parameter 	 *@param  pool    Description of the Parameter 	 */
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
comment|/** 	 *  Description of the Method 	 * 	 *@param  set               Description of the Parameter 	 *@param  start             Description of the Parameter 	 *@param  howmany           Description of the Parameter 	 *@param  queryTime         Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|NodeSet
name|set
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|,
name|long
name|queryTime
parameter_list|)
throws|throws
name|SAXException
block|{
name|Iterator
name|iter
init|=
name|set
operator|.
name|iterator
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
name|start
operator|-
literal|1
condition|;
name|i
operator|++
control|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
return|return;
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
literal|"exist"
argument_list|,
name|EXIST_NS
argument_list|)
expr_stmt|;
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"hitCount"
argument_list|,
literal|"hitCount"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|set
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryTime
operator|>=
literal|0
condition|)
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"queryTime"
argument_list|,
literal|"queryTime"
argument_list|,
literal|"CDATA"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|queryTime
argument_list|)
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|startElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"result"
argument_list|,
literal|"exist:result"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Iterator
name|domIter
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
name|howmany
operator|&&
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
continue|continue;
name|domIter
operator|=
name|broker
operator|.
name|getNodeIterator
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|domIter
operator|==
literal|null
condition|)
continue|continue;
name|serializeToSAX
argument_list|(
literal|null
argument_list|,
name|domIter
argument_list|,
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|,
literal|true
argument_list|,
name|p
operator|.
name|match
argument_list|)
expr_stmt|;
block|}
name|contentHandler
operator|.
name|endElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"result"
argument_list|,
literal|"exist:result"
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  doc               Description of the Parameter 	 *@param  generateDocEvent  Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|Document
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
operator|(
name|DocumentImpl
operator|)
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
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
literal|"exist"
argument_list|,
name|EXIST_NS
argument_list|)
expr_stmt|;
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
name|serializeToSAX
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
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"serializing document "
operator|+
operator|(
operator|(
name|DocumentImpl
operator|)
name|doc
operator|)
operator|.
name|getDocId
argument_list|()
operator|+
literal|"to SAX took "
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
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
literal|"exist"
argument_list|)
expr_stmt|;
if|if
condition|(
name|generateDocEvent
condition|)
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  n                 Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|Node
name|n
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
operator|(
name|n
operator|instanceof
name|NodeImpl
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"wrong implementation"
argument_list|)
throw|;
name|serializeToSAX
argument_list|(
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
operator|(
operator|(
name|NodeImpl
operator|)
name|n
operator|)
operator|.
name|getGID
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  p                 Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|NodeProxy
name|p
parameter_list|)
throws|throws
name|SAXException
block|{
name|serializeToSAX
argument_list|(
name|p
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  p                  Description of the Parameter 	 *@param  generateDocEvents  Description of the Parameter 	 *@exception  SAXException   Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|NodeProxy
name|p
parameter_list|,
name|boolean
name|generateDocEvents
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|generateDocEvents
condition|)
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
literal|"exist"
argument_list|,
name|EXIST_NS
argument_list|)
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
name|serializeToSAX
argument_list|(
literal|null
argument_list|,
name|domIter
argument_list|,
name|p
operator|.
name|doc
argument_list|,
name|p
operator|.
name|gid
argument_list|,
literal|true
argument_list|,
name|p
operator|.
name|match
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
literal|"exist"
argument_list|)
expr_stmt|;
if|if
condition|(
name|generateDocEvents
condition|)
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  iter              Description of the Parameter 	 *@param  doc               Description of the Parameter 	 *@param  gid               Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
parameter_list|(
name|Iterator
name|iter
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|gid
parameter_list|)
throws|throws
name|SAXException
block|{
name|serializeToSAX
argument_list|(
literal|null
argument_list|,
name|iter
argument_list|,
name|doc
argument_list|,
name|gid
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  node              Description of the Parameter 	 *@param  iter              Description of the Parameter 	 *@param  doc               Description of the Parameter 	 *@param  gid               Description of the Parameter 	 *@param  first             Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
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
parameter_list|)
throws|throws
name|SAXException
block|{
name|serializeToSAX
argument_list|(
name|node
argument_list|,
name|iter
argument_list|,
name|doc
argument_list|,
name|gid
argument_list|,
name|first
argument_list|,
operator|new
name|TreeSet
argument_list|()
argument_list|,
name|match
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  node              Description of the Parameter 	 *@param  iter              Description of the Parameter 	 *@param  doc               Description of the Parameter 	 *@param  gid               Description of the Parameter 	 *@param  first             Description of the Parameter 	 *@param  prefixes          Description of the Parameter 	 *@exception  SAXException  Description of the Exception 	 */
specifier|protected
name|void
name|serializeToSAX
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
name|Set
name|namespaces
parameter_list|,
name|Match
name|match
parameter_list|)
throws|throws
name|SAXException
block|{
name|setDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
name|char
name|ch
index|[]
decl_stmt|;
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
name|int
name|childLen
decl_stmt|;
name|NodeImpl
name|child
init|=
literal|null
decl_stmt|;
name|AttributesImpl
name|attributes
init|=
operator|new
name|AttributesImpl
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
name|attributes
operator|.
name|addAttribute
argument_list|(
name|EXIST_NS
argument_list|,
literal|"id"
argument_list|,
literal|"exist:id"
argument_list|,
literal|"CDATA"
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
name|attributes
operator|.
name|addAttribute
argument_list|(
name|EXIST_NS
argument_list|,
literal|"source"
argument_list|,
literal|"exist:source"
argument_list|,
literal|"CDATA"
argument_list|,
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|highlightMatches
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
name|attributes
operator|.
name|addAttribute
argument_list|(
name|child
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|child
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|child
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"CDATA"
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
block|}
else|else
break|break;
block|}
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
name|contentHandler
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
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|EXIST_NS
argument_list|)
condition|)
continue|continue;
name|contentHandler
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
name|contentHandler
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
name|contentHandler
operator|.
name|startElement
argument_list|(
name|ns
argument_list|,
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
while|while
condition|(
name|count
operator|<
name|children
condition|)
block|{
name|serializeToSAX
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
name|namespaces
argument_list|,
name|match
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
name|contentHandler
operator|.
name|endElement
argument_list|(
name|ns
argument_list|,
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|node
operator|.
name|getNodeName
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
name|contentHandler
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
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
name|node
operator|.
name|getPrefix
argument_list|()
argument_list|)
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
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|showId
operator|>
literal|0
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
name|EXIST_NS
argument_list|,
literal|"id"
argument_list|,
literal|"exist:id"
argument_list|,
literal|"CDATA"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|EXIST_NS
argument_list|,
literal|"source"
argument_list|,
literal|"exist:source"
argument_list|,
literal|"CDATA"
argument_list|,
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|contentHandler
operator|.
name|startElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"text"
argument_list|,
literal|"exist:text"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|highlightMatches
operator|&
name|TAG_ELEMENT_MATCHES
operator|)
operator|==
name|TAG_ELEMENT_MATCHES
operator|&&
operator|(
name|cdata
operator|=
name|processText
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
operator|)
operator|!=
literal|null
condition|)
name|scanText
argument_list|(
name|cdata
argument_list|)
expr_stmt|;
else|else
block|{
operator|(
operator|(
name|TextImpl
operator|)
name|node
operator|)
operator|.
name|getXMLString
argument_list|()
operator|.
name|toSAX
argument_list|(
name|contentHandler
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|first
operator|&&
name|createContainerElements
condition|)
name|contentHandler
operator|.
name|endElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"text"
argument_list|,
literal|"exist:text"
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
if|if
condition|(
name|first
operator|&&
name|createContainerElements
condition|)
block|{
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|showId
operator|>
literal|0
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
name|EXIST_NS
argument_list|,
literal|"id"
argument_list|,
literal|"exist:id"
argument_list|,
literal|"CDATA"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
name|EXIST_NS
argument_list|,
literal|"source"
argument_list|,
literal|"exist:source"
argument_list|,
literal|"CDATA"
argument_list|,
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|highlightMatches
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
name|attribs
operator|.
name|addAttribute
argument_list|(
name|node
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|node
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"CDATA"
argument_list|,
name|cdata
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|startElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"attribute"
argument_list|,
literal|"exist:attribute"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|endElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"attribute"
argument_list|,
literal|"exist:attribute"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|(
name|highlightMatches
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
name|ch
operator|=
operator|new
name|char
index|[
name|cdata
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
name|cdata
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|,
name|ch
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
name|contentHandler
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
break|break;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
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
operator|-
literal|1
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|lexicalHandler
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
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
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
literal|"s/\\b("
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
name|next
operator|.
name|getMatchingTerm
argument_list|()
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
literal|")\\b/||$1||/gi"
argument_list|)
expr_stmt|;
name|data
operator|=
name|reutil
operator|.
name|substitute
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
specifier|private
specifier|final
name|String
name|processText
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
block|{
if|if
condition|(
name|match
operator|==
literal|null
condition|)
return|return
literal|null
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
literal|"s/\\b("
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
name|next
operator|.
name|getMatchingTerm
argument_list|()
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
literal|")\\b/||$1||/gi"
argument_list|)
expr_stmt|;
return|return
name|reutil
operator|.
name|substitute
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|,
name|text
operator|.
name|getData
argument_list|()
argument_list|)
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
specifier|private
specifier|final
name|void
name|scanText
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|AttributesImpl
name|atts
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
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
name|outputText
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
name|contentHandler
operator|.
name|startElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"match"
argument_list|,
literal|"exist:match"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|outputText
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
name|contentHandler
operator|.
name|endElement
argument_list|(
name|EXIST_NS
argument_list|,
literal|"match"
argument_list|,
literal|"exist:match"
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
name|outputText
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
specifier|private
specifier|final
name|void
name|outputText
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|char
name|ch
index|[]
init|=
operator|new
name|char
index|[
name|data
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|data
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|,
name|ch
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

