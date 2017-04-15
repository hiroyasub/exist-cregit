begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|DocumentTriggers
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
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Permission
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
comment|/**  * Internal class used to track required fields between calls to  * {@link org.exist.collections.Collection#validateXMLResource(Txn, DBBroker, XmldbURI, InputSource)} and  * {@link org.exist.collections.Collection#store(Txn, DBBroker, IndexInfo, InputSource)}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|IndexInfo
block|{
specifier|private
specifier|final
name|Indexer
name|indexer
decl_stmt|;
specifier|private
specifier|final
name|CollectionConfiguration
name|collectionConfig
decl_stmt|;
specifier|private
name|DOMStreamer
name|streamer
decl_stmt|;
specifier|private
name|DocumentTriggers
name|docTriggers
decl_stmt|;
specifier|private
name|boolean
name|creating
init|=
literal|false
decl_stmt|;
specifier|private
name|Permission
name|oldDocPermissions
init|=
literal|null
decl_stmt|;
name|IndexInfo
parameter_list|(
specifier|final
name|Indexer
name|indexer
parameter_list|,
specifier|final
name|CollectionConfiguration
name|collectionConfig
parameter_list|)
block|{
name|this
operator|.
name|indexer
operator|=
name|indexer
expr_stmt|;
name|this
operator|.
name|collectionConfig
operator|=
name|collectionConfig
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
comment|//XXX: make protected
specifier|public
name|void
name|setTriggers
parameter_list|(
specifier|final
name|DocumentTriggers
name|triggersVisitor
parameter_list|)
block|{
name|this
operator|.
name|docTriggers
operator|=
name|triggersVisitor
expr_stmt|;
block|}
comment|//XXX: make protected
specifier|public
name|DocumentTriggers
name|getTriggers
parameter_list|()
block|{
return|return
name|docTriggers
return|;
block|}
specifier|public
name|void
name|setCreating
parameter_list|(
specifier|final
name|boolean
name|creating
parameter_list|)
block|{
name|this
operator|.
name|creating
operator|=
name|creating
expr_stmt|;
block|}
specifier|public
name|boolean
name|isCreating
parameter_list|()
block|{
return|return
name|creating
return|;
block|}
specifier|public
name|void
name|setOldDocPermissions
parameter_list|(
specifier|final
name|Permission
name|oldDocPermissions
parameter_list|)
block|{
name|this
operator|.
name|oldDocPermissions
operator|=
name|oldDocPermissions
expr_stmt|;
block|}
specifier|public
name|Permission
name|getOldDocPermissions
parameter_list|()
block|{
return|return
name|oldDocPermissions
return|;
block|}
name|void
name|setReader
parameter_list|(
specifier|final
name|XMLReader
name|reader
parameter_list|,
specifier|final
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
block|{
name|reader
operator|.
name|setEntityResolver
argument_list|(
name|entityResolver
argument_list|)
expr_stmt|;
block|}
specifier|final
name|LexicalHandler
name|lexicalHandler
init|=
name|docTriggers
operator|==
literal|null
condition|?
name|indexer
else|:
name|docTriggers
decl_stmt|;
specifier|final
name|ContentHandler
name|contentHandler
init|=
name|docTriggers
operator|==
literal|null
condition|?
name|indexer
else|:
name|docTriggers
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
specifier|final
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
name|docTriggers
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
name|docTriggers
argument_list|)
expr_stmt|;
name|streamer
operator|.
name|setLexicalHandler
argument_list|(
name|docTriggers
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
specifier|public
name|CollectionConfiguration
name|getCollectionConfig
parameter_list|()
block|{
return|return
name|collectionConfig
return|;
block|}
block|}
end_class

end_unit

