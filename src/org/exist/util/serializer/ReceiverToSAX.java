begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org) * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *  *  $Id$ */
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|INodeHandle
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
comment|/**  * A wrapper class that forwards the method calls defined in the  * {@link org.exist.util.serializer.Receiver} interface to a  * SAX content handler and lexical handler.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ReceiverToSAX
implements|implements
name|Receiver
block|{
specifier|private
name|ContentHandler
name|contentHandler
decl_stmt|;
specifier|private
name|LexicalHandler
name|lexicalHandler
init|=
literal|null
decl_stmt|;
specifier|private
name|char
index|[]
name|charBuf
init|=
operator|new
name|char
index|[
literal|2048
index|]
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|ReceiverToSAX
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|=
name|handler
expr_stmt|;
if|if
condition|(
name|handler
operator|instanceof
name|LexicalHandler
condition|)
block|{
name|lexicalHandler
operator|=
operator|(
name|LexicalHandler
operator|)
name|handler
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
name|LexicalHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|lexicalHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|void
name|setContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|contentHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
block|{
return|return
name|contentHandler
return|;
block|}
specifier|public
name|LexicalHandler
name|getLexicalHandler
parameter_list|()
block|{
return|return
name|lexicalHandler
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#startDocument() 	 */
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#endDocument() 	 */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#startPrefixMapping(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespaceURI
parameter_list|)
throws|throws
name|SAXException
block|{
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#endPrefixMapping(java.lang.String) 	 */
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
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#startElement(org.exist.dom.QName, org.exist.util.serializer.AttrList) 	 */
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|,
name|AttrList
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|AttributesImpl
name|a
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|attribs
operator|!=
literal|null
condition|)
block|{
name|QName
name|attrQName
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
name|attribs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrQName
operator|=
name|attribs
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|a
operator|.
name|addAttribute
argument_list|(
name|attrQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|attrQName
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|attrQName
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"CDATA"
argument_list|,
name|attribs
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|contentHandler
operator|.
name|startElement
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|qname
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#endElement(org.exist.dom.QName) 	 */
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
name|contentHandler
operator|.
name|endElement
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|qname
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#characters(java.lang.CharSequence) 	 */
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|int
name|len
init|=
name|seq
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|<
name|charBuf
operator|.
name|length
condition|)
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
name|len
condition|;
name|i
operator|++
control|)
name|charBuf
index|[
name|i
index|]
operator|=
name|seq
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|characters
argument_list|(
name|charBuf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contentHandler
operator|.
name|characters
argument_list|(
name|seq
operator|.
name|toString
argument_list|()
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|seq
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#attribute(org.exist.dom.QName, java.lang.String) 	 */
specifier|public
name|void
name|attribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
name|contentHandler
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#comment(char[], int, int) 	 */
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
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|lexicalHandler
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
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#processingInstruction(java.lang.String, java.lang.String) 	 */
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
name|contentHandler
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.util.serializer.Receiver#cdataSection(char[], int, int)      */
specifier|public
name|void
name|cdataSection
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
throws|throws
name|SAXException
block|{
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|lexicalHandler
operator|.
name|startCDATA
argument_list|()
expr_stmt|;
block|}
name|contentHandler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|lexicalHandler
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
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
throws|throws
name|SAXException
block|{
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|lexicalHandler
operator|.
name|startDTD
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
name|lexicalHandler
operator|.
name|endDTD
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|highlightText
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
block|{
comment|// not supported with this receiver
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentNode
parameter_list|(
name|INodeHandle
name|node
parameter_list|)
block|{
comment|// just ignore
block|}
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
comment|//just ignore
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

