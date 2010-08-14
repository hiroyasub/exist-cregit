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
name|StoredNode
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
name|serializers
operator|.
name|EXistOutputKeys
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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

begin_class
specifier|public
class|class
name|SAXSerializer
implements|implements
name|ContentHandler
implements|,
name|LexicalHandler
implements|,
name|Receiver
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XHTML_NS
init|=
literal|"http://www.w3.org/1999/xhtml"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|defaultProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|int
name|XML_WRITER
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|XHTML_WRITER
init|=
literal|1
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|TEXT_WRITER
init|=
literal|2
decl_stmt|;
specifier|private
name|XMLWriter
name|writers
index|[]
init|=
block|{
operator|new
name|IndentingXMLWriter
argument_list|()
block|,
operator|new
name|XHTMLWriter
argument_list|()
block|,
operator|new
name|TEXTWriter
argument_list|()
block|}
decl_stmt|;
specifier|protected
name|XMLWriter
name|receiver
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
init|=
name|defaultProperties
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
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaceDecls
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|optionalNamespaceDecls
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|enforceXHTML
init|=
literal|false
decl_stmt|;
specifier|public
name|SAXSerializer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|receiver
operator|=
name|writers
index|[
name|XML_WRITER
index|]
expr_stmt|;
block|}
specifier|public
name|SAXSerializer
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
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setOutput
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|Properties
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
name|outputProperties
operator|=
name|defaultProperties
expr_stmt|;
else|else
name|outputProperties
operator|=
name|properties
expr_stmt|;
name|String
name|method
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
literal|"method"
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"xhtml"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
name|receiver
operator|=
name|writers
index|[
name|XHTML_WRITER
index|]
expr_stmt|;
if|else if
condition|(
literal|"text"
operator|.
name|equalsIgnoreCase
argument_list|(
name|method
argument_list|)
condition|)
name|receiver
operator|=
name|writers
index|[
name|TEXT_WRITER
index|]
expr_stmt|;
else|else
name|receiver
operator|=
name|writers
index|[
name|XML_WRITER
index|]
expr_stmt|;
comment|// if set, enforce XHTML namespace on elements with no namespace
name|String
name|xhtml
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|ENFORCE_XHTML
argument_list|,
literal|"no"
argument_list|)
decl_stmt|;
name|enforceXHTML
operator|=
name|xhtml
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
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
name|Writer
name|getWriter
parameter_list|()
block|{
return|return
name|receiver
operator|.
name|writer
return|;
block|}
specifier|public
name|void
name|setReceiver
parameter_list|(
name|XMLWriter
name|receiver
parameter_list|)
block|{
name|this
operator|.
name|receiver
operator|=
name|receiver
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
name|optionalNamespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|writers
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|writers
index|[
name|i
index|]
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator) 	 */
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|arg0
parameter_list|)
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#startDocument() 	 */
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
try|try
block|{
name|receiver
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#endDocument() 	 */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
try|try
block|{
name|receiver
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|namespaceURI
parameter_list|)
throws|throws
name|SAXException
block|{
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
name|String
name|ns
init|=
name|nsSupport
operator|.
name|getURI
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|ns
operator|==
literal|null
operator|||
operator|(
operator|!
name|ns
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
operator|)
condition|)
block|{
name|optionalNamespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
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
name|optionalNamespaceDecls
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
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
name|qname
parameter_list|,
name|Attributes
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
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
name|qname
argument_list|)
expr_stmt|;
name|String
name|elemPrefix
init|=
literal|""
decl_stmt|;
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
if|if
condition|(
name|p
operator|>
literal|0
condition|)
name|elemPrefix
operator|=
name|qname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
name|namespaceURI
operator|=
literal|""
expr_stmt|;
if|if
condition|(
name|enforceXHTML
operator|&&
name|elemPrefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|namespaceURI
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|namespaceURI
operator|=
name|XHTML_NS
expr_stmt|;
if|if
condition|(
name|nsSupport
operator|.
name|getURI
argument_list|(
name|elemPrefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|namespaceDecls
operator|.
name|put
argument_list|(
name|elemPrefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|elemPrefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
comment|// check attributes for required namespace declarations
name|String
name|attrName
decl_stmt|;
name|String
name|uri
decl_stmt|;
if|if
condition|(
name|attribs
operator|!=
literal|null
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
name|attribs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|attrName
operator|=
name|attribs
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
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
name|attribs
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
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
name|String
name|prefix
init|=
name|attrName
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
decl_stmt|;
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
name|attribs
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
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
operator|(
name|p
operator|=
name|attrName
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|String
name|prefix
init|=
name|attrName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|uri
operator|=
name|attribs
operator|.
name|getURI
argument_list|(
name|i
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
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsEntry
range|:
name|optionalNamespaceDecls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|prefix
init|=
name|nsEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|uri
operator|=
name|nsEntry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|namespace
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
comment|//nsSupport.declarePrefix(prefix, namespaceURI);
block|}
comment|// output all namespace declarations
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsEntry
range|:
name|namespaceDecls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|prefix
init|=
name|nsEntry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|uri
operator|=
name|nsEntry
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|optionalNamespaceDecls
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|receiver
operator|.
name|namespace
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
comment|//cancels current xmlns if relevant
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|elemPrefix
argument_list|)
operator|&&
operator|!
name|namespaceURI
operator|.
name|equals
argument_list|(
name|receiver
operator|.
name|getDefaultNamespace
argument_list|()
argument_list|)
condition|)
block|{
name|receiver
operator|.
name|namespace
argument_list|(
literal|""
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
literal|""
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
name|optionalNamespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|attribs
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
operator|!
name|attribs
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
name|receiver
operator|.
name|attribute
argument_list|(
name|attribs
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
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
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.Receiver#startElement(org.exist.dom.QName) 	 */
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
try|try
block|{
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
name|String
name|prefix
init|=
name|qname
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|String
name|namespaceURI
init|=
name|qname
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
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
name|namespaceURI
operator|==
literal|null
condition|)
name|namespaceURI
operator|=
literal|""
expr_stmt|;
if|if
condition|(
name|enforceXHTML
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|namespaceURI
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|namespaceURI
operator|=
name|XHTML_NS
expr_stmt|;
name|qname
operator|.
name|setNamespaceURI
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|startElement
argument_list|(
name|qname
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
name|namespaceDecls
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
comment|// check attributes for required namespace declarations
name|QName
name|attrQName
decl_stmt|;
name|String
name|uri
decl_stmt|;
if|if
condition|(
name|attribs
operator|!=
literal|null
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
if|if
condition|(
name|attrQName
operator|.
name|getLocalName
argument_list|()
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
name|attribs
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
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
name|attrQName
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|attrQName
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|prefix
operator|=
name|attrQName
operator|.
name|getPrefix
argument_list|()
expr_stmt|;
if|if
condition|(
name|prefix
operator|.
name|equals
argument_list|(
literal|"xmlns:"
argument_list|)
condition|)
block|{
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
name|attribs
operator|.
name|getValue
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|prefix
operator|=
name|attrQName
operator|.
name|getLocalName
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
else|else
block|{
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
name|attrQName
operator|.
name|getNamespaceURI
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
block|}
block|}
block|}
name|String
name|optPrefix
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsEntry
range|:
name|optionalNamespaceDecls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|optPrefix
operator|=
name|nsEntry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|uri
operator|=
name|nsEntry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|namespace
argument_list|(
name|optPrefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
name|optPrefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|// output all namespace declarations
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nsEntry
range|:
name|namespaceDecls
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|optPrefix
operator|=
name|nsEntry
operator|.
name|getKey
argument_list|()
expr_stmt|;
if|if
condition|(
name|optPrefix
operator|.
name|equals
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|uri
operator|=
name|nsEntry
operator|.
name|getValue
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|optionalNamespaceDecls
operator|.
name|containsKey
argument_list|(
name|optPrefix
argument_list|)
condition|)
block|{
name|receiver
operator|.
name|namespace
argument_list|(
name|optPrefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
name|optionalNamespaceDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//cancels current xmlns if relevant
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|prefix
argument_list|)
operator|&&
operator|!
name|namespaceURI
operator|.
name|equals
argument_list|(
name|receiver
operator|.
name|getDefaultNamespace
argument_list|()
argument_list|)
condition|)
block|{
name|receiver
operator|.
name|namespace
argument_list|(
literal|""
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
name|nsSupport
operator|.
name|declarePrefix
argument_list|(
literal|""
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attribs
operator|!=
literal|null
condition|)
block|{
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
name|attribs
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
operator|!
name|attribs
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
operator|.
name|getLocalName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
name|receiver
operator|.
name|attribute
argument_list|(
name|attribs
operator|.
name|getQName
argument_list|(
name|i
argument_list|)
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
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
name|nsSupport
operator|.
name|popContext
argument_list|()
expr_stmt|;
name|receiver
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|setDefaultNamespace
argument_list|(
name|nsSupport
operator|.
name|getURI
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|nsSupport
operator|.
name|popContext
argument_list|()
expr_stmt|;
name|String
name|prefix
init|=
name|qname
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|String
name|namespaceURI
init|=
name|qname
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
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
name|namespaceURI
operator|==
literal|null
condition|)
name|namespaceURI
operator|=
literal|""
expr_stmt|;
if|if
condition|(
name|enforceXHTML
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|&&
name|namespaceURI
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|namespaceURI
operator|=
name|XHTML_NS
expr_stmt|;
name|qname
operator|.
name|setNamespaceURI
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
name|receiver
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|receiver
operator|.
name|setDefaultNamespace
argument_list|(
name|nsSupport
operator|.
name|getURI
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
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
comment|// ignore namespace declaration attributes
if|if
condition|(
operator|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
name|qname
operator|.
name|getPrefix
argument_list|()
operator|.
name|equals
argument_list|(
literal|"xmlns"
argument_list|)
operator|)
operator|||
name|qname
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
return|return;
try|try
block|{
name|receiver
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
name|receiver
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
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
try|try
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
name|receiver
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
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
try|try
block|{
name|receiver
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
try|try
block|{
name|receiver
operator|.
name|cdataSection
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String) 	 */
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|startDTD
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|,
name|String
name|arg2
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.LexicalHandler#endDTD() 	 */
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
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
try|try
block|{
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
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
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
comment|/* (non-Javadoc)       * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)       */
specifier|public
name|void
name|startEntity
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String) 	 */
specifier|public
name|void
name|endEntity
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.LexicalHandler#startCDATA() 	 */
specifier|public
name|void
name|startCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.LexicalHandler#endCDATA() 	 */
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int) 	 */
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
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
name|receiver
operator|.
name|comment
argument_list|(
operator|new
name|XMLString
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|setCurrentNode
parameter_list|(
name|StoredNode
name|node
parameter_list|)
block|{
comment|// just ignore.
block|}
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
comment|//just ignore.
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

