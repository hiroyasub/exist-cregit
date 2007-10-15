begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Document
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
name|Locator
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
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_comment
comment|/**  * Adapter class to build an internal, in-memory DOM from a SAX stream.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|SAXAdapter
implements|implements
name|ContentHandler
implements|,
name|LexicalHandler
block|{
specifier|private
name|MemTreeBuilder
name|builder
decl_stmt|;
specifier|private
name|HashMap
name|namespaces
init|=
literal|null
decl_stmt|;
specifier|public
name|SAXAdapter
parameter_list|()
block|{
name|builder
operator|=
operator|new
name|MemTreeBuilder
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SAXAdapter
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|builder
operator|=
operator|new
name|MemTreeBuilder
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#endDocument() 	 */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#startDocument() 	 */
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#characters(char[], int, int) 	 */
specifier|public
name|void
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
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int) 	 */
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String) 	 */
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String) 	 */
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator) 	 */
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|namespaces
operator|==
literal|null
condition|)
name|namespaces
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes) 	 */
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaces
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|namespaces
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|builder
operator|.
name|namespaceNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|atts
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|atts
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
block|{
name|String
name|prefix
init|=
name|atts
operator|.
name|getLocalName
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|namespaces
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|)
name|builder
operator|.
name|namespaceNode
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
name|namespaces
operator|=
literal|null
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.xml.sax.ext.LexicalHandler#endCDATA()      */
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc)      * @see org.xml.sax.ext.LexicalHandler#endDTD()      */
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc)      * @see org.xml.sax.ext.LexicalHandler#startCDATA()      */
specifier|public
name|void
name|startCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc)      * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)      */
specifier|public
name|void
name|comment
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|comment
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)      */
specifier|public
name|void
name|endEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc)      * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)      */
specifier|public
name|void
name|startEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc)      * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)      */
specifier|public
name|void
name|startDTD
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
throws|throws
name|SAXException
block|{
block|}
block|}
end_class

end_unit

