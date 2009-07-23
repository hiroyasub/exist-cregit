begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|memtree
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Indexer
import|;
end_import

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
name|QName
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
name|Constants
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
name|xml
operator|.
name|sax
operator|.
name|Attributes
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

begin_comment
comment|/**  * Use this class to build a new in-memory DOM document.  *   * @author Wolfgang<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|MemTreeBuilder
block|{
specifier|protected
name|DocumentImpl
name|doc
decl_stmt|;
specifier|protected
name|short
name|level
init|=
literal|1
decl_stmt|;
specifier|protected
name|int
index|[]
name|prevNodeInLevel
decl_stmt|;
specifier|protected
name|XQueryContext
name|context
init|=
literal|null
decl_stmt|;
specifier|public
name|MemTreeBuilder
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MemTreeBuilder
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|prevNodeInLevel
operator|=
operator|new
name|int
index|[
literal|15
index|]
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|prevNodeInLevel
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|prevNodeInLevel
index|[
literal|0
index|]
operator|=
literal|0
expr_stmt|;
block|}
comment|/** 	 * Returns the created document object. 	 *  	 */
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|doc
operator|.
name|getSize
argument_list|()
return|;
block|}
comment|/** 	 * Start building the document. 	 */
specifier|public
name|void
name|startDocument
parameter_list|()
block|{
name|this
operator|.
name|doc
operator|=
operator|new
name|DocumentImpl
argument_list|(
name|context
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Start building the document. 	 */
specifier|public
name|void
name|startDocument
parameter_list|(
name|boolean
name|explicitCreation
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
operator|new
name|DocumentImpl
argument_list|(
name|context
argument_list|,
name|explicitCreation
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * End building the document. 	 */
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
block|}
comment|/** 	 * Create a new element. 	 *  	 * @return the node number of the created element 	 */
specifier|public
name|int
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qname
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
block|{
name|int
name|p
init|=
name|qname
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
name|prefix
operator|=
name|context
operator|.
name|getPrefixForURI
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
name|prefix
operator|=
operator|(
name|p
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
operator|)
condition|?
name|qname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
else|:
literal|""
expr_stmt|;
name|QName
name|qn
init|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
return|return
name|startElement
argument_list|(
name|qn
argument_list|,
name|attributes
argument_list|)
return|;
block|}
comment|/** 	 * Create a new element. 	 *  	 * @return the node number of the created element 	 */
specifier|public
name|int
name|startElement
parameter_list|(
name|QName
name|qn
parameter_list|,
name|Attributes
name|attributes
parameter_list|)
block|{
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|Node
operator|.
name|ELEMENT_NODE
argument_list|,
name|level
argument_list|,
name|qn
argument_list|)
decl_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
comment|// parse attributes
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
name|String
name|attrNS
init|=
name|attributes
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrLocalName
init|=
name|attributes
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|attrQName
init|=
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// skip xmlns-attributes and attributes in eXist's namespace
if|if
condition|(
operator|!
operator|(
name|attrQName
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
operator|)
condition|)
block|{
comment|//					|| attrNS.equals(Namespaces.EXIST_NS))) {
name|int
name|p
init|=
name|attrQName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|attrPrefix
init|=
operator|(
name|p
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
operator|)
condition|?
name|attrQName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
else|:
literal|null
decl_stmt|;
name|QName
name|attrQn
init|=
operator|new
name|QName
argument_list|(
name|attrLocalName
argument_list|,
name|attrNS
argument_list|,
name|attrPrefix
argument_list|)
decl_stmt|;
name|int
name|type
init|=
name|getAttribType
argument_list|(
name|attrQn
argument_list|,
name|attributes
operator|.
name|getType
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addAttribute
argument_list|(
name|nodeNr
argument_list|,
name|attrQn
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// update links
if|if
condition|(
name|level
operator|+
literal|1
operator|>=
name|prevNodeInLevel
operator|.
name|length
condition|)
block|{
name|int
index|[]
name|t
init|=
operator|new
name|int
index|[
name|level
operator|+
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|prevNodeInLevel
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
name|prevNodeInLevel
operator|.
name|length
argument_list|)
expr_stmt|;
name|prevNodeInLevel
operator|=
name|t
expr_stmt|;
block|}
name|int
name|prevNr
init|=
name|prevNodeInLevel
index|[
name|level
index|]
decl_stmt|;
comment|// TODO: remove potential ArrayIndexOutOfBoundsException
if|if
condition|(
name|prevNr
operator|>
operator|-
literal|1
condition|)
name|doc
operator|.
name|next
index|[
name|prevNr
index|]
operator|=
name|nodeNr
expr_stmt|;
name|doc
operator|.
name|next
index|[
name|nodeNr
index|]
operator|=
name|prevNodeInLevel
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
name|prevNodeInLevel
index|[
name|level
index|]
operator|=
name|nodeNr
expr_stmt|;
operator|++
name|level
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|private
name|int
name|getAttribType
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
name|qname
operator|.
name|equalsSimple
argument_list|(
name|Namespaces
operator|.
name|XML_ID_QNAME
argument_list|)
condition|)
block|{
comment|// an xml:id attribute.
return|return
name|AttributeImpl
operator|.
name|ATTR_CDATA_TYPE
return|;
block|}
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|Indexer
operator|.
name|ATTR_ID_TYPE
argument_list|)
condition|)
return|return
name|AttributeImpl
operator|.
name|ATTR_ID_TYPE
return|;
if|else if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|Indexer
operator|.
name|ATTR_IDREF_TYPE
argument_list|)
condition|)
return|return
name|AttributeImpl
operator|.
name|ATTR_IDREF_TYPE
return|;
if|else if
condition|(
name|type
operator|.
name|equals
argument_list|(
name|Indexer
operator|.
name|ATTR_IDREFS_TYPE
argument_list|)
condition|)
return|return
name|AttributeImpl
operator|.
name|ATTR_IDREFS_TYPE
return|;
else|else
return|return
name|AttributeImpl
operator|.
name|ATTR_CDATA_TYPE
return|;
block|}
comment|/** 	 * Close the last element created. 	 */
specifier|public
name|void
name|endElement
parameter_list|()
block|{
comment|//		System.out.println("end-element: level = " + level);
name|prevNodeInLevel
index|[
name|level
index|]
operator|=
operator|-
literal|1
expr_stmt|;
operator|--
name|level
expr_stmt|;
block|}
specifier|public
name|int
name|addReferenceNode
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|int
name|lastNode
init|=
name|doc
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|lastNode
operator|&&
name|level
operator|==
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|lastNode
argument_list|)
condition|)
block|{
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|&&
name|proxy
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
comment|// if the last node is a text node, we have to append the
comment|// characters to this node. XML does not allow adjacent text nodes.
name|doc
operator|.
name|appendChars
argument_list|(
name|lastNode
argument_list|,
name|proxy
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
comment|// check if the previous node is a reference node. if yes, check if it is a text node
name|int
name|p
init|=
name|doc
operator|.
name|alpha
index|[
name|lastNode
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|&&
name|proxy
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
comment|// found a text node reference. create a new char sequence containing
comment|// the concatenated text of both nodes
name|String
name|s
init|=
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getStringValue
argument_list|()
operator|+
name|proxy
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|doc
operator|.
name|replaceReferenceNode
argument_list|(
name|lastNode
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
block|}
block|}
specifier|final
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|NodeImpl
operator|.
name|REFERENCE_NODE
argument_list|,
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addReferenceNode
argument_list|(
name|nodeNr
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
name|linkNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|public
name|int
name|addAttribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|int
name|lastNode
init|=
name|doc
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
comment|//if(0< lastNode&& doc.nodeKind[lastNode] != Node.ELEMENT_NODE) {
comment|//Definitely wrong !
comment|//lastNode = characters(value);
comment|//} else {
comment|//lastNode = doc.addAttribute(lastNode, qname, value);
comment|//}
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addAttribute
argument_list|(
name|lastNode
argument_list|,
name|qname
argument_list|,
name|value
argument_list|,
name|AttributeImpl
operator|.
name|ATTR_CDATA_TYPE
argument_list|)
decl_stmt|;
comment|//TODO :
comment|//1) call linkNode(nodeNr); ?
comment|//2) is there a relationship between lastNode and nodeNr ?
return|return
name|nodeNr
return|;
block|}
comment|/** 	 * Create a new text node. 	 *  	 * @return the node number of the created node 	 */
specifier|public
name|int
name|characters
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|lastNode
init|=
name|doc
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|lastNode
operator|&&
name|level
operator|==
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|lastNode
argument_list|)
condition|)
block|{
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
comment|// if the last node is a text node, we have to append the
comment|// characters to this node. XML does not allow adjacent text nodes.
name|doc
operator|.
name|appendChars
argument_list|(
name|lastNode
argument_list|,
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
comment|// check if the previous node is a reference node. if yes, check if it is a text node
name|int
name|p
init|=
name|doc
operator|.
name|alpha
index|[
name|lastNode
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
comment|// found a text node reference. create a new char sequence containing
comment|// the concatenated text of both nodes
name|StringBuilder
name|s
init|=
operator|new
name|StringBuilder
argument_list|(
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|s
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|doc
operator|.
name|replaceReferenceNode
argument_list|(
name|lastNode
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
comment|// fall through and add the node below
block|}
block|}
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|Node
operator|.
name|TEXT_NODE
argument_list|,
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addChars
argument_list|(
name|nodeNr
argument_list|,
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|linkNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
comment|/** 	 * Create a new text node. 	 *  	 * @return the node number of the created node 	 */
specifier|public
name|int
name|characters
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
name|int
name|lastNode
init|=
name|doc
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|lastNode
operator|&&
name|level
operator|==
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|lastNode
argument_list|)
condition|)
block|{
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|||
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
comment|// if the last node is a text node, we have to append the
comment|// characters to this node. XML does not allow adjacent text nodes.
name|doc
operator|.
name|appendChars
argument_list|(
name|lastNode
argument_list|,
name|s
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
comment|// check if the previous node is a reference node. if yes, check if it is a text node
name|int
name|p
init|=
name|doc
operator|.
name|alpha
index|[
name|lastNode
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|||
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
comment|// found a text node reference. create a new char sequence containing
comment|// the concatenated text of both nodes
name|doc
operator|.
name|replaceReferenceNode
argument_list|(
name|lastNode
argument_list|,
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getStringValue
argument_list|()
operator|+
name|s
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
comment|// fall through and add the node below
block|}
block|}
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|Node
operator|.
name|TEXT_NODE
argument_list|,
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addChars
argument_list|(
name|nodeNr
argument_list|,
name|s
argument_list|)
expr_stmt|;
name|linkNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|public
name|int
name|comment
parameter_list|(
name|CharSequence
name|data
parameter_list|)
block|{
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addChars
argument_list|(
name|nodeNr
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|linkNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|public
name|int
name|comment
parameter_list|(
name|char
name|ch
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|Node
operator|.
name|COMMENT_NODE
argument_list|,
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addChars
argument_list|(
name|nodeNr
argument_list|,
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|linkNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|public
name|int
name|cdataSection
parameter_list|(
name|CharSequence
name|data
parameter_list|)
block|{
name|int
name|lastNode
init|=
name|doc
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
if|if
condition|(
literal|0
operator|<
name|lastNode
operator|&&
name|level
operator|==
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|lastNode
argument_list|)
condition|)
block|{
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|||
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
comment|// if the last node is a text node, we have to append the
comment|// characters to this node. XML does not allow adjacent text nodes.
name|doc
operator|.
name|appendChars
argument_list|(
name|lastNode
argument_list|,
name|data
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
if|if
condition|(
name|doc
operator|.
name|getNodeType
argument_list|(
name|lastNode
argument_list|)
operator|==
name|NodeImpl
operator|.
name|REFERENCE_NODE
condition|)
block|{
comment|// check if the previous node is a reference node. if yes, check if it is a text node
name|int
name|p
init|=
name|doc
operator|.
name|alpha
index|[
name|lastNode
index|]
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|||
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|CDATA_SECTION_NODE
condition|)
block|{
comment|// found a text node reference. create a new char sequence containing
comment|// the concatenated text of both nodes
name|doc
operator|.
name|replaceReferenceNode
argument_list|(
name|lastNode
argument_list|,
name|doc
operator|.
name|references
index|[
name|p
index|]
operator|.
name|getStringValue
argument_list|()
operator|+
name|data
argument_list|)
expr_stmt|;
return|return
name|lastNode
return|;
block|}
comment|// fall through and add the node below
block|}
block|}
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|Node
operator|.
name|CDATA_SECTION_NODE
argument_list|,
name|level
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addChars
argument_list|(
name|nodeNr
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|linkNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|public
name|int
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|QName
name|qn
init|=
operator|new
name|QName
argument_list|(
name|target
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNode
argument_list|(
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
argument_list|,
name|level
argument_list|,
name|qn
argument_list|)
decl_stmt|;
name|doc
operator|.
name|addChars
argument_list|(
name|nodeNr
argument_list|,
name|data
operator|==
literal|null
condition|?
literal|""
else|:
name|data
argument_list|)
expr_stmt|;
name|linkNode
argument_list|(
name|nodeNr
argument_list|)
expr_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|public
name|int
name|namespaceNode
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
return|return
name|namespaceNode
argument_list|(
operator|new
name|QName
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|,
literal|"xmlns"
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|namespaceNode
parameter_list|(
name|QName
name|qn
parameter_list|)
block|{
name|int
name|lastNode
init|=
name|doc
operator|.
name|getLastNode
argument_list|()
decl_stmt|;
name|int
name|nodeNr
init|=
name|doc
operator|.
name|addNamespace
argument_list|(
name|lastNode
argument_list|,
name|qn
argument_list|)
decl_stmt|;
return|return
name|nodeNr
return|;
block|}
specifier|public
name|int
name|documentType
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
comment|//		int nodeNr = doc.addNode(Node.DOCUMENT_TYPE_NODE, level, null);
comment|//		doc.addChars(nodeNr, data);
comment|//		linkNode(nodeNr);
comment|//		return nodeNr;
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|void
name|documentType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
block|{
block|}
specifier|private
name|void
name|linkNode
parameter_list|(
name|int
name|nodeNr
parameter_list|)
block|{
name|int
name|prevNr
init|=
name|prevNodeInLevel
index|[
name|level
index|]
decl_stmt|;
if|if
condition|(
name|prevNr
operator|>
operator|-
literal|1
condition|)
name|doc
operator|.
name|next
index|[
name|prevNr
index|]
operator|=
name|nodeNr
expr_stmt|;
name|doc
operator|.
name|next
index|[
name|nodeNr
index|]
operator|=
name|prevNodeInLevel
index|[
name|level
operator|-
literal|1
index|]
expr_stmt|;
name|prevNodeInLevel
index|[
name|level
index|]
operator|=
name|nodeNr
expr_stmt|;
block|}
block|}
end_class

end_unit

