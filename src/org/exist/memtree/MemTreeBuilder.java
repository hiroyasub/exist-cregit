begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|xpath
operator|.
name|StaticContext
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
name|xml
operator|.
name|sax
operator|.
name|Attributes
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
name|StaticContext
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
name|StaticContext
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
comment|/** 	 * Returns the created document object. 	 *  	 * @return 	 */
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
return|return
name|doc
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
literal|500
argument_list|,
literal|50
argument_list|,
literal|1000
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|prefix
operator|+
literal|" = "
operator|+
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
name|p
operator|>
operator|-
literal|1
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
comment|// parse attributes
name|String
name|attrPrefix
decl_stmt|;
name|String
name|attrLocalName
decl_stmt|;
name|String
name|attrNS
decl_stmt|;
name|String
name|attrQName
decl_stmt|;
name|int
name|p
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
name|attrNS
operator|=
name|attributes
operator|.
name|getURI
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrLocalName
operator|=
name|attributes
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|attrQName
operator|=
name|attributes
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
expr_stmt|;
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
operator|||
name|attrNS
operator|.
name|equals
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|)
operator|)
condition|)
block|{
name|p
operator|=
name|attrQName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|attrPrefix
operator|=
operator|(
name|p
operator|>
operator|-
literal|1
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
expr_stmt|;
name|p
operator|=
name|doc
operator|.
name|addAttribute
argument_list|(
name|nodeNr
argument_list|,
operator|new
name|QName
argument_list|(
name|attrLocalName
argument_list|,
name|attrNS
argument_list|,
name|attrPrefix
argument_list|)
argument_list|,
name|attributes
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
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
name|level
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
comment|/** 	 * Close the last element created. 	 */
specifier|public
name|void
name|endElement
parameter_list|()
block|{
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
argument_list|)
expr_stmt|;
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
return|return
name|nodeNr
return|;
block|}
block|}
end_class

end_unit

