begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist-db project  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
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
name|DOMException
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
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
name|HashMap
import|;
end_import

begin_comment
comment|/**  * Builds an in-memory DOM tree from SAX {@link org.exist.util.serializer.Receiver} events.  *  * @author Wolfgang<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|DocumentBuilderReceiver
implements|implements
name|ContentHandler
implements|,
name|LexicalHandler
implements|,
name|Receiver
block|{
specifier|private
name|MemTreeBuilder
name|builder
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|explicitNSDecl
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|checkNS
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|suppressWhitespace
init|=
literal|true
decl_stmt|;
specifier|public
name|DocumentBuilderReceiver
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentBuilderReceiver
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
name|this
argument_list|(
name|builder
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DocumentBuilderReceiver
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|,
specifier|final
name|boolean
name|declareNamespaces
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|explicitNSDecl
operator|=
name|declareNamespaces
expr_stmt|;
block|}
specifier|public
name|void
name|setCheckNS
parameter_list|(
specifier|final
name|boolean
name|checkNS
parameter_list|)
block|{
name|this
operator|.
name|checkNS
operator|=
name|checkNS
expr_stmt|;
block|}
specifier|public
name|void
name|setSuppressWhitespace
parameter_list|(
name|boolean
name|flag
parameter_list|)
block|{
name|this
operator|.
name|suppressWhitespace
operator|=
name|flag
expr_stmt|;
block|}
annotation|@
name|Override
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
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|builder
operator|.
name|getContext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
operator|new
name|MemTreeBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
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
operator|||
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|setDefaultNamespace
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|explicitNSDecl
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|namespaces
operator|==
literal|null
condition|)
block|{
name|namespaces
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|prefix
operator|==
literal|null
operator|||
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|builder
operator|.
name|setDefaultNamespace
argument_list|(
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qName
parameter_list|,
specifier|final
name|Attributes
name|attrs
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
name|attrs
argument_list|)
expr_stmt|;
name|declareNamespaces
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|declareNamespaces
parameter_list|()
block|{
if|if
condition|(
name|explicitNSDecl
operator|&&
name|namespaces
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|namespaces
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|builder
operator|.
name|namespaceNode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|namespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|AttrList
name|attribs
parameter_list|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|checkNS
argument_list|(
literal|true
argument_list|,
name|qname
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|declareNamespaces
argument_list|()
expr_stmt|;
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
name|builder
operator|.
name|addAttribute
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
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|QName
name|qname
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
specifier|public
name|void
name|addReferenceNode
parameter_list|(
specifier|final
name|NodeProxy
name|proxy
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|addReferenceNode
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addNamespaceNode
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|namespaceNode
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
specifier|final
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|characters
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
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
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
name|builder
operator|.
name|addAttribute
argument_list|(
name|checkNS
argument_list|(
literal|false
argument_list|,
name|qname
argument_list|)
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DOMException
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
annotation|@
name|Override
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|suppressWhitespace
condition|)
block|{
name|builder
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|processingInstruction
parameter_list|(
specifier|final
name|String
name|target
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|cdataSection
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
operator|.
name|cdataSection
argument_list|(
operator|new
name|String
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
annotation|@
name|Override
specifier|public
name|void
name|skippedEntity
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentType
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|publicId
parameter_list|,
specifier|final
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
name|builder
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
annotation|@
name|Override
specifier|public
name|void
name|comment
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|endEntity
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startEntity
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDTD
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|publicId
parameter_list|,
specifier|final
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|highlightText
parameter_list|(
specifier|final
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
specifier|final
name|INodeHandle
name|node
parameter_list|)
block|{
comment|// ignored
block|}
specifier|private
name|QName
name|checkNS
parameter_list|(
name|boolean
name|isElement
parameter_list|,
specifier|final
name|QName
name|qname
parameter_list|)
block|{
if|if
condition|(
name|checkNS
condition|)
block|{
specifier|final
name|XQueryContext
name|context
init|=
name|builder
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|qname
operator|.
name|hasNamespace
argument_list|()
condition|)
block|{
return|return
name|qname
return|;
block|}
if|else if
condition|(
name|isElement
condition|)
block|{
return|return
name|qname
return|;
block|}
else|else
block|{
specifier|final
name|String
name|prefix
init|=
name|generatePrefix
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|getInScopePrefix
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|prefix
argument_list|,
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|QName
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|prefix
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|qname
operator|.
name|getPrefix
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|qname
return|;
block|}
specifier|final
name|String
name|inScopeNamespace
init|=
name|context
operator|.
name|getInScopeNamespace
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|inScopeNamespace
operator|==
literal|null
condition|)
block|{
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|inScopeNamespace
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|prefix
init|=
name|generatePrefix
argument_list|(
name|context
argument_list|,
name|context
operator|.
name|getInScopePrefix
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|prefix
argument_list|,
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|QName
argument_list|(
name|qname
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|prefix
argument_list|)
return|;
block|}
block|}
return|return
name|qname
return|;
block|}
specifier|private
name|String
name|generatePrefix
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
literal|"XXX"
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|prefix
operator|+=
name|String
operator|.
name|valueOf
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|getInScopeNamespace
argument_list|(
name|prefix
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|prefix
operator|=
literal|null
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
return|return
name|prefix
return|;
block|}
block|}
end_class

end_unit

