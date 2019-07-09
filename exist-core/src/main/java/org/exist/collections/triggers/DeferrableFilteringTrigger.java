begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|sax
operator|.
name|event
operator|.
name|SAXEvent
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
name|sax
operator|.
name|event
operator|.
name|contenthandler
operator|.
name|*
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
name|sax
operator|.
name|event
operator|.
name|lexicalhandler
operator|.
name|*
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
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_comment
comment|/**  * DeferrableFilteringTrigger decorates a FilteringTrigger with the  * ability to capture and defer the processing of events.  *  * By default all events are dispatched to 'super' unless  * we are deferring events and then they are queued.  * When events are realised from the deferred queue  * they will then be dispatched to 'super', you may override  * either {@link #applyDeferredEvents()} or one or more of the  * _deferred methods to change this behaviour.  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DeferrableFilteringTrigger
extends|extends
name|FilteringTrigger
block|{
specifier|private
name|boolean
name|defer
init|=
literal|false
decl_stmt|;
specifier|protected
name|Deque
argument_list|<
name|SAXEvent
argument_list|>
name|deferred
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|boolean
name|isDeferring
parameter_list|()
block|{
return|return
name|defer
return|;
block|}
comment|/**      * Controls the deferral of FilteringTrigger      * event processing.      *      * If we are deferring events and this function is called      * with 'false' then deferred events will be applied      * by calling {@link #applyDeferredEvents()}.      *      * @param defer Should we defer the processing of events?      * @throws SAXException in case of an Error      */
specifier|public
name|void
name|defer
parameter_list|(
specifier|final
name|boolean
name|defer
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|this
operator|.
name|defer
operator|&&
operator|!
name|defer
condition|)
block|{
name|applyDeferredEvents
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|defer
operator|=
name|defer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
specifier|final
name|Locator
name|locator
parameter_list|)
block|{
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|SetDocumentLocator
argument_list|(
name|locator
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
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
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
name|StartDocument
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
name|EndDocument
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
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
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|StartPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
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
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|EndPrefixMapping
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
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
name|qname
parameter_list|,
specifier|final
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|StartElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|,
name|attributes
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
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
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|EndElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
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
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|Characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
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
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|IgnorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|ProcessingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|SkippedEntity
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|skippedEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|StartDTD
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
name|EndDTD
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|endDTD
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|StartEntity
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|startEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|EndEntity
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|endEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
name|StartCDATA
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|startCDATA
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
name|EndCDATA
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
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
if|if
condition|(
name|defer
condition|)
block|{
name|deferred
operator|.
name|add
argument_list|(
operator|new
name|Comment
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
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
comment|/**      * Applies any deferred events      * by dispatching to the appropriate _deferred method      * @throws SAXException in case of an error      */
specifier|protected
name|void
name|applyDeferredEvents
parameter_list|()
throws|throws
name|SAXException
block|{
name|SAXEvent
name|event
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|event
operator|=
name|deferred
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|event
operator|instanceof
name|SetDocumentLocator
condition|)
block|{
specifier|final
name|SetDocumentLocator
name|setDocumentLocator
init|=
operator|(
name|SetDocumentLocator
operator|)
name|event
decl_stmt|;
name|setDocumentLocator_deferred
argument_list|(
name|setDocumentLocator
operator|.
name|locator
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|StartDocument
condition|)
block|{
name|startDocument_deferred
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|EndDocument
condition|)
block|{
name|endDocument_deferred
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|StartPrefixMapping
condition|)
block|{
specifier|final
name|StartPrefixMapping
name|startPrefixMapping
init|=
operator|(
name|StartPrefixMapping
operator|)
name|event
decl_stmt|;
name|startPrefixMapping_deferred
argument_list|(
name|startPrefixMapping
operator|.
name|prefix
argument_list|,
name|startPrefixMapping
operator|.
name|uri
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|EndPrefixMapping
condition|)
block|{
specifier|final
name|EndPrefixMapping
name|endPrefixMapping
init|=
operator|(
name|EndPrefixMapping
operator|)
name|event
decl_stmt|;
name|endPrefixMapping_deferred
argument_list|(
name|endPrefixMapping
operator|.
name|prefix
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|StartElement
condition|)
block|{
specifier|final
name|StartElement
name|startElement
init|=
operator|(
name|StartElement
operator|)
name|event
decl_stmt|;
name|startElement_deferred
argument_list|(
name|startElement
operator|.
name|namespaceURI
argument_list|,
name|startElement
operator|.
name|localName
argument_list|,
name|startElement
operator|.
name|qname
argument_list|,
name|startElement
operator|.
name|attributes
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|EndElement
condition|)
block|{
specifier|final
name|EndElement
name|endElement
init|=
operator|(
name|EndElement
operator|)
name|event
decl_stmt|;
name|endElement_deferred
argument_list|(
name|endElement
operator|.
name|namespaceURI
argument_list|,
name|endElement
operator|.
name|localName
argument_list|,
name|endElement
operator|.
name|qname
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|Characters
condition|)
block|{
specifier|final
name|Characters
name|characters
init|=
operator|(
name|Characters
operator|)
name|event
decl_stmt|;
name|characters_deferred
argument_list|(
name|characters
operator|.
name|ch
argument_list|,
literal|0
argument_list|,
name|characters
operator|.
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|IgnorableWhitespace
condition|)
block|{
specifier|final
name|IgnorableWhitespace
name|ignorableWhitespace
init|=
operator|(
name|IgnorableWhitespace
operator|)
name|event
decl_stmt|;
name|ignorableWhitespace_deferred
argument_list|(
name|ignorableWhitespace
operator|.
name|ch
argument_list|,
literal|0
argument_list|,
name|ignorableWhitespace
operator|.
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|ProcessingInstruction
condition|)
block|{
specifier|final
name|ProcessingInstruction
name|processingInstruction
init|=
operator|(
name|ProcessingInstruction
operator|)
name|event
decl_stmt|;
name|processingInstruction_deferred
argument_list|(
name|processingInstruction
operator|.
name|target
argument_list|,
name|processingInstruction
operator|.
name|data
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|SkippedEntity
condition|)
block|{
specifier|final
name|SkippedEntity
name|skippedEntity
init|=
operator|(
name|SkippedEntity
operator|)
name|event
decl_stmt|;
name|skippedEntity_deferred
argument_list|(
name|skippedEntity
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|StartDTD
condition|)
block|{
specifier|final
name|StartDTD
name|startDTD
init|=
operator|(
name|StartDTD
operator|)
name|event
decl_stmt|;
name|startDTD_deferred
argument_list|(
name|startDTD
operator|.
name|name
argument_list|,
name|startDTD
operator|.
name|publicId
argument_list|,
name|startDTD
operator|.
name|systemId
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|EndDTD
condition|)
block|{
name|endDTD_deferred
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|StartEntity
condition|)
block|{
specifier|final
name|StartEntity
name|startEntity
init|=
operator|(
name|StartEntity
operator|)
name|event
decl_stmt|;
name|startEntity_deferred
argument_list|(
name|startEntity
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|EndEntity
condition|)
block|{
specifier|final
name|EndEntity
name|endEntity
init|=
operator|(
name|EndEntity
operator|)
name|event
decl_stmt|;
name|endEntity_deferred
argument_list|(
name|endEntity
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|StartCDATA
condition|)
block|{
name|startCDATA_deferred
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|EndCDATA
condition|)
block|{
name|endCDATA_deferred
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|event
operator|instanceof
name|Comment
condition|)
block|{
specifier|final
name|Comment
name|comment
init|=
operator|(
name|Comment
operator|)
name|event
decl_stmt|;
name|comment_deferred
argument_list|(
name|comment
operator|.
name|ch
argument_list|,
literal|0
argument_list|,
name|comment
operator|.
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//<editor-fold desc="Deferred ContentHandler">
specifier|protected
name|void
name|setDocumentLocator_deferred
parameter_list|(
specifier|final
name|Locator
name|locator
parameter_list|)
block|{
name|super
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|startDocument_deferred
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|endDocument_deferred
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|startPrefixMapping_deferred
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|endPrefixMapping_deferred
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|startElement_deferred
parameter_list|(
specifier|final
name|String
name|namespaceUri
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
name|super
operator|.
name|startElement
argument_list|(
name|namespaceUri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|endElement_deferred
parameter_list|(
specifier|final
name|String
name|namespaceUri
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
name|super
operator|.
name|endElement
argument_list|(
name|namespaceUri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|characters_deferred
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
name|super
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
specifier|protected
name|void
name|ignorableWhitespace_deferred
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
name|super
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
specifier|protected
name|void
name|processingInstruction_deferred
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
name|super
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|skippedEntity_deferred
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|skippedEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|//</editor-fold>
comment|//<editor-fold desc="Deferred Lexical">
specifier|protected
name|void
name|startDTD_deferred
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
name|super
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
specifier|protected
name|void
name|endDTD_deferred
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endDTD
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|startEntity_deferred
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|endEntity_deferred
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|startCDATA_deferred
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|startCDATA
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|endCDATA_deferred
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|comment_deferred
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
name|super
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
comment|//</editor-fold>
block|}
end_class

end_unit

