begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  FilteringTrigger.java - eXist Open Source Native XML Database  *  Copyright (C) 2003 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionConfigurationException
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
name|storage
operator|.
name|txn
operator|.
name|Txn
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
comment|/**  * Abstract default implementation of a Trigger. This implementation just forwards  * all SAX events to the output content handler.  *    * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|FilteringTrigger
implements|implements
name|DocumentTrigger
block|{
specifier|protected
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
comment|// The output handlers to which SAX events should be
comment|// forwarded
specifier|protected
name|ContentHandler
name|outputHandler
init|=
literal|null
decl_stmt|;
specifier|protected
name|LexicalHandler
name|lexicalOutputHandler
init|=
literal|null
decl_stmt|;
specifier|protected
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|validating
init|=
literal|true
decl_stmt|;
comment|/** 	 * Configure the trigger. The default implementation just stores the parent collection 	 * reference into the field {@link #collection collection}. Use method {@link #getCollection() getCollection} 	 * to later retrieve the collection.  	 */
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|parent
parameter_list|,
name|Map
name|parameters
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
name|this
operator|.
name|collection
operator|=
name|parent
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.collections.triggers.DocumentTrigger#finish(int, org.exist.storage.DBBroker, java.lang.String, org.w3c.dom.Document)      */
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setValidating
parameter_list|(
name|boolean
name|validating
parameter_list|)
block|{
name|this
operator|.
name|validating
operator|=
name|validating
expr_stmt|;
block|}
specifier|public
name|boolean
name|isValidating
parameter_list|()
block|{
return|return
name|validating
return|;
block|}
specifier|public
name|Collection
name|getCollection
parameter_list|()
block|{
return|return
name|collection
return|;
block|}
specifier|public
name|ContentHandler
name|getInputHandler
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|LexicalHandler
name|getLexicalInputHandler
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|ContentHandler
name|getOutputHandler
parameter_list|()
block|{
return|return
name|outputHandler
return|;
block|}
specifier|public
name|LexicalHandler
name|getLexicalOutputHandler
parameter_list|()
block|{
return|return
name|lexicalOutputHandler
return|;
block|}
specifier|public
name|void
name|setOutputHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|outputHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|void
name|setLexicalOutputHandler
parameter_list|(
name|LexicalHandler
name|handler
parameter_list|)
block|{
name|lexicalOutputHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|Logger
name|getLogger
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
name|outputHandler
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|outputHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|outputHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
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
name|outputHandler
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
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
name|outputHandler
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
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
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
name|outputHandler
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
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
name|outputHandler
operator|.
name|endElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|)
expr_stmt|;
block|}
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
name|outputHandler
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
name|outputHandler
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
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
name|outputHandler
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
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
name|outputHandler
operator|.
name|skippedEntity
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
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
name|lexicalOutputHandler
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
block|}
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
name|lexicalOutputHandler
operator|.
name|endDTD
argument_list|()
expr_stmt|;
block|}
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
name|lexicalOutputHandler
operator|.
name|startEntity
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
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
name|lexicalOutputHandler
operator|.
name|endEntity
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|lexicalOutputHandler
operator|.
name|startCDATA
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|lexicalOutputHandler
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
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
name|lexicalOutputHandler
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
end_class

end_unit

