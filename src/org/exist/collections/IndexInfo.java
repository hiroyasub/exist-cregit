begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
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
name|collections
operator|.
name|triggers
operator|.
name|DocumentTrigger
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
name|triggers
operator|.
name|TriggerException
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
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|DOMStreamer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|EntityResolver
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
name|InputSource
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
name|XMLReader
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
comment|/**  * Internal class used to track required fields between calls to  * {@link org.exist.collections.Collection#validateXMLResource(Txn, DBBroker, XmldbURI, InputSource)} and  * {@link org.exist.collections.Collection#store(Txn, DBBroker, IndexInfo, InputSource, boolean)}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|IndexInfo
block|{
specifier|private
name|Indexer
name|indexer
decl_stmt|;
specifier|private
name|DOMStreamer
name|streamer
decl_stmt|;
specifier|private
name|DocumentTrigger
name|trigger
decl_stmt|;
specifier|private
name|int
name|event
decl_stmt|;
name|IndexInfo
parameter_list|(
name|Indexer
name|indexer
parameter_list|)
block|{
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
block|}
specifier|public
name|Indexer
name|getIndexer
parameter_list|()
block|{
return|return
name|indexer
return|;
block|}
name|int
name|getEvent
parameter_list|()
block|{
return|return
name|event
return|;
block|}
name|void
name|setReader
parameter_list|(
name|XMLReader
name|reader
parameter_list|,
name|EntityResolver
name|entityResolver
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|entityResolver
operator|!=
literal|null
condition|)
name|reader
operator|.
name|setEntityResolver
argument_list|(
name|entityResolver
argument_list|)
expr_stmt|;
name|LexicalHandler
name|lexicalHandler
init|=
name|trigger
operator|==
literal|null
condition|?
name|indexer
else|:
name|trigger
operator|.
name|getLexicalInputHandler
argument_list|()
decl_stmt|;
name|ContentHandler
name|contentHandler
init|=
name|trigger
operator|==
literal|null
condition|?
name|indexer
else|:
name|trigger
operator|.
name|getInputHandler
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|contentHandler
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setErrorHandler
argument_list|(
name|indexer
argument_list|)
expr_stmt|;
block|}
name|void
name|setDOMStreamer
parameter_list|(
name|DOMStreamer
name|streamer
parameter_list|)
block|{
name|this
operator|.
name|streamer
operator|=
name|streamer
expr_stmt|;
if|if
condition|(
name|trigger
operator|==
literal|null
condition|)
block|{
name|streamer
operator|.
name|setContentHandler
argument_list|(
name|indexer
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|setLexicalHandler
argument_list|(
name|indexer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|streamer
operator|.
name|setContentHandler
argument_list|(
name|trigger
operator|.
name|getInputHandler
argument_list|()
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|setLexicalHandler
argument_list|(
name|trigger
operator|.
name|getLexicalInputHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|DOMStreamer
name|getDOMStreamer
parameter_list|()
block|{
return|return
name|this
operator|.
name|streamer
return|;
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|indexer
operator|.
name|getDocument
argument_list|()
return|;
block|}
name|void
name|setTrigger
parameter_list|(
name|DocumentTrigger
name|trigger
parameter_list|,
name|int
name|event
parameter_list|)
block|{
name|this
operator|.
name|trigger
operator|=
name|trigger
expr_stmt|;
name|this
operator|.
name|event
operator|=
name|event
expr_stmt|;
block|}
name|DocumentTrigger
name|getTrigger
parameter_list|()
block|{
return|return
name|trigger
return|;
block|}
name|void
name|prepareTrigger
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|XmldbURI
name|docUri
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
throws|throws
name|TriggerException
block|{
if|if
condition|(
name|trigger
operator|==
literal|null
condition|)
return|return;
name|trigger
operator|.
name|setOutputHandler
argument_list|(
name|indexer
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|setLexicalOutputHandler
argument_list|(
name|indexer
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|setValidating
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|prepare
argument_list|(
name|event
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|,
name|docUri
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|void
name|postValidateTrigger
parameter_list|()
block|{
if|if
condition|(
name|trigger
operator|==
literal|null
condition|)
return|return;
name|trigger
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|void
name|finishTrigger
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
if|if
condition|(
name|trigger
operator|==
literal|null
condition|)
return|return;
name|trigger
operator|.
name|finish
argument_list|(
name|event
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

