begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|BackupHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|RestoreHandler
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
name|config
operator|.
name|Configuration
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
name|DocumentAtExist
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
name|plugin
operator|.
name|Plug
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|plugin
operator|.
name|PluginsManager
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
name|PermissionDeniedException
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
name|util
operator|.
name|serializer
operator|.
name|SAXSerializer
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|MDStorageManager
implements|implements
name|Plug
implements|,
name|BackupHandler
implements|,
name|RestoreHandler
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|MDStorageManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"md"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/metadata"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|UUID
init|=
literal|"uuid"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|META
init|=
literal|"meta"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|KEY
init|=
literal|"key"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|VALUE
init|=
literal|"value"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|VALUE_IS_DOCUMENT
init|=
literal|"value-is-document"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX_UUID
init|=
name|PREFIX
operator|+
literal|":"
operator|+
name|UUID
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX_KEY
init|=
name|PREFIX
operator|+
literal|":"
operator|+
name|KEY
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX_META
init|=
name|PREFIX
operator|+
literal|":"
operator|+
name|META
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX_VALUE
init|=
name|PREFIX
operator|+
literal|":"
operator|+
name|VALUE
decl_stmt|;
specifier|protected
specifier|static
name|MDStorageManager
name|_
init|=
literal|null
decl_stmt|;
name|MetaData
name|md
decl_stmt|;
specifier|public
name|MDStorageManager
parameter_list|(
name|PluginsManager
name|manager
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|Class
argument_list|<
name|?
extends|extends
name|MetaData
argument_list|>
name|backend
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|MetaData
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.storage.md.MetaDataImpl"
argument_list|)
decl_stmt|;
name|Constructor
argument_list|<
name|?
extends|extends
name|MetaData
argument_list|>
name|ctor
init|=
name|backend
operator|.
name|getConstructor
argument_list|(
name|Database
operator|.
name|class
argument_list|)
decl_stmt|;
name|md
operator|=
name|ctor
operator|.
name|newInstance
argument_list|(
name|manager
operator|.
name|getDatabase
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|_
operator|=
name|this
expr_stmt|;
name|manager
operator|.
name|getDatabase
argument_list|()
operator|.
name|getDocumentTriggers
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|DocumentEvents
argument_list|()
argument_list|)
expr_stmt|;
name|manager
operator|.
name|getDatabase
argument_list|()
operator|.
name|getCollectionTriggers
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|CollectionEvents
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|start
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|sync
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|md
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|stop
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
name|md
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|//backup methods
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|Collection
name|colection
parameter_list|,
name|AttributesImpl
name|attrs
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|Collection
name|colection
parameter_list|,
name|SAXSerializer
name|serializer
parameter_list|)
throws|throws
name|SAXException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|,
name|AttributesImpl
name|attrs
parameter_list|)
block|{
name|Metas
name|ms
init|=
name|md
operator|.
name|getMetas
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|UUID
argument_list|,
name|PREFIX_UUID
argument_list|,
literal|"CDATA"
argument_list|,
name|ms
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|backup
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|,
name|SAXSerializer
name|serializer
parameter_list|)
throws|throws
name|SAXException
block|{
name|Metas
name|ms
init|=
name|md
operator|.
name|getMetas
argument_list|(
name|document
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Meta
argument_list|>
name|sub
init|=
name|ms
operator|.
name|metas
argument_list|()
decl_stmt|;
for|for
control|(
name|Meta
name|m
range|:
name|sub
control|)
block|{
name|AttributesImpl
name|attr
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|UUID
argument_list|,
name|PREFIX_UUID
argument_list|,
literal|"CDATA"
argument_list|,
name|m
operator|.
name|getUUID
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|KEY
argument_list|,
name|PREFIX_KEY
argument_list|,
literal|"CDATA"
argument_list|,
name|m
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Object
name|value
init|=
name|m
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|DocumentImpl
condition|)
block|{
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|value
decl_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|VALUE
argument_list|,
name|PREFIX_VALUE
argument_list|,
literal|"CDATA"
argument_list|,
name|doc
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|VALUE_IS_DOCUMENT
argument_list|,
name|PREFIX_VALUE
argument_list|,
literal|"CDATA"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attr
operator|.
name|addAttribute
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|VALUE
argument_list|,
name|PREFIX_VALUE
argument_list|,
literal|"CDATA"
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|serializer
operator|.
name|startElement
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|META
argument_list|,
name|PREFIX_META
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|endElement
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|META
argument_list|,
name|PREFIX_META
argument_list|)
expr_stmt|;
block|}
block|}
comment|//restore methods
specifier|private
name|Metas
name|currentMetas
init|=
literal|null
decl_stmt|;
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
block|}
annotation|@
name|Override
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
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
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
if|if
condition|(
name|META
operator|.
name|equals
argument_list|(
name|localName
argument_list|)
operator|&&
name|NAMESPACE_URI
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|String
name|uuid
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|UUID
argument_list|)
decl_stmt|;
name|String
name|key
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|KEY
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|VALUE
argument_list|)
decl_stmt|;
name|md
operator|.
name|_addMeta
argument_list|(
name|currentMetas
argument_list|,
name|uuid
argument_list|,
name|key
argument_list|,
name|value
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
name|String
name|uri
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
block|}
annotation|@
name|Override
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
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|startCollectionRestore
parameter_list|(
name|Collection
name|colection
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|endCollectionRestore
parameter_list|(
name|Collection
name|colection
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocumentRestore
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"startDocument "
operator|+
name|document
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|uuid
init|=
name|atts
operator|.
name|getValue
argument_list|(
name|NAMESPACE_URI
argument_list|,
name|UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuid
operator|!=
literal|null
condition|)
name|currentMetas
operator|=
name|md
operator|.
name|replaceMetas
argument_list|(
name|document
operator|.
name|getURI
argument_list|()
argument_list|,
name|uuid
argument_list|)
expr_stmt|;
else|else
name|currentMetas
operator|=
name|md
operator|.
name|addMetas
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocumentRestore
parameter_list|(
name|DocumentAtExist
name|document
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

