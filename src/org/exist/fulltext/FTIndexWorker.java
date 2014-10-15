begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fulltext
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
name|persistent
operator|.
name|AttrImpl
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
name|CharacterDataImpl
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
name|ElementImpl
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
name|DocumentSet
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
name|dom
operator|.
name|persistent
operator|.
name|IStoredNode
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
name|Match
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
name|NodeSet
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
name|indexing
operator|.
name|AbstractStreamListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexController
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|IndexWorker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|MatchListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|OrderedValuesIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|QNamedKeysIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|StreamListener
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
name|ElementValue
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
name|FulltextIndexSpec
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
name|IndexSpec
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
name|NativeTextEngine
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
name|NodePath
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
name|TextSearchEngine
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
name|btree
operator|.
name|DBException
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
name|DatabaseConfigurationException
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
name|Occurrences
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
name|QueryRewriter
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
name|Node
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
name|NodeList
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
name|Stack
import|;
end_import

begin_comment
comment|/**  * A legacy IndexWorker which wraps around {@link org.exist.storage.NativeTextEngine}. Right  * now, the fulltext index has only partly been moved into the new modularized indexing architecture  * and we thus need some glue classes to keep the old and new parts together. This class will become  * part of the new fulltext indexing module.  */
end_comment

begin_class
specifier|public
class|class
name|FTIndexWorker
implements|implements
name|OrderedValuesIndex
implements|,
name|QNamedKeysIndex
block|{
specifier|private
name|NativeTextEngine
name|engine
decl_stmt|;
specifier|private
name|FTIndex
name|index
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|DocumentImpl
name|document
decl_stmt|;
specifier|private
name|FulltextIndexSpec
name|config
decl_stmt|;
specifier|private
name|int
name|mode
init|=
name|StreamListener
operator|.
name|UNKNOWN
decl_stmt|;
specifier|private
name|FTStreamListener
name|listener
init|=
operator|new
name|FTStreamListener
argument_list|()
decl_stmt|;
specifier|private
name|FTMatchListener
name|matchListener
init|=
literal|null
decl_stmt|;
specifier|public
name|FTIndexWorker
parameter_list|(
name|FTIndex
name|index
parameter_list|,
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
try|try
block|{
name|this
operator|.
name|engine
operator|=
operator|new
name|NativeTextEngine
argument_list|(
name|broker
argument_list|,
name|index
operator|.
name|getBFile
argument_list|()
argument_list|,
name|broker
operator|.
name|getConfiguration
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
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
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|FTIndex
operator|.
name|ID
return|;
block|}
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
literal|"ft-index-old"
return|;
block|}
specifier|public
name|TextSearchEngine
name|getEngine
parameter_list|()
block|{
return|return
name|engine
return|;
block|}
annotation|@
name|Override
specifier|public
name|QueryRewriter
name|getQueryRewriter
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Object
name|configure
parameter_list|(
name|IndexController
name|controller
parameter_list|,
name|NodeList
name|configNodes
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
comment|// Not implemented
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|setDocument
argument_list|(
name|doc
argument_list|,
name|StreamListener
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
name|newMode
parameter_list|)
block|{
name|document
operator|=
name|doc
expr_stmt|;
name|mode
operator|=
name|newMode
expr_stmt|;
specifier|final
name|IndexSpec
name|indexConf
init|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getIndexConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexConf
operator|!=
literal|null
condition|)
block|{
name|config
operator|=
name|indexConf
operator|.
name|getFulltextIndexSpec
argument_list|()
expr_stmt|;
block|}
name|engine
operator|.
name|setDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setMode
parameter_list|(
name|int
name|newMode
parameter_list|)
block|{
name|mode
operator|=
name|newMode
expr_stmt|;
comment|// wolf: unnecessary call to setDocument?
comment|// setDocument(document, newMode);
block|}
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|()
block|{
return|return
name|document
return|;
block|}
specifier|public
name|int
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
extends|extends
name|IStoredNode
parameter_list|>
name|IStoredNode
name|getReindexRoot
parameter_list|(
name|IStoredNode
argument_list|<
name|T
argument_list|>
name|node
parameter_list|,
name|NodePath
name|path
parameter_list|,
name|boolean
name|insert
parameter_list|,
name|boolean
name|includeSelf
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|IndexSpec
name|indexConf
init|=
name|node
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getCollection
argument_list|()
operator|.
name|getIndexConfiguration
argument_list|(
name|broker
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexConf
operator|!=
literal|null
condition|)
block|{
specifier|final
name|FulltextIndexSpec
name|config
init|=
name|indexConf
operator|.
name|getFulltextIndexSpec
argument_list|()
decl_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|boolean
name|reindexRequired
init|=
literal|false
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
operator|!
name|includeSelf
condition|?
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
else|:
name|path
operator|.
name|length
argument_list|()
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
name|len
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|QName
name|qn
init|=
name|path
operator|.
name|getComponent
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|.
name|hasQNameIndex
argument_list|(
name|qn
argument_list|)
condition|)
block|{
name|reindexRequired
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|reindexRequired
condition|)
block|{
name|IStoredNode
name|topMost
init|=
literal|null
decl_stmt|;
name|IStoredNode
name|currentNode
init|=
name|node
decl_stmt|;
while|while
condition|(
name|currentNode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|config
operator|.
name|hasQNameIndex
argument_list|(
name|currentNode
operator|.
name|getQName
argument_list|()
argument_list|)
condition|)
block|{
name|topMost
operator|=
name|currentNode
expr_stmt|;
block|}
name|currentNode
operator|=
name|currentNode
operator|.
name|getParentStoredNode
argument_list|()
expr_stmt|;
block|}
return|return
name|topMost
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|StreamListener
name|getListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
specifier|public
name|MatchListener
name|getMatchListener
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|boolean
name|needToFilter
init|=
literal|false
decl_stmt|;
name|Match
name|nextMatch
init|=
name|proxy
operator|.
name|getMatches
argument_list|()
decl_stmt|;
while|while
condition|(
name|nextMatch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|nextMatch
operator|.
name|getIndexId
argument_list|()
operator|==
name|FTIndex
operator|.
name|ID
condition|)
block|{
name|needToFilter
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|nextMatch
operator|=
name|nextMatch
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|needToFilter
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|matchListener
operator|==
literal|null
condition|)
block|{
name|matchListener
operator|=
operator|new
name|FTMatchListener
argument_list|(
name|broker
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matchListener
operator|.
name|reset
argument_list|(
name|broker
argument_list|,
name|proxy
argument_list|)
expr_stmt|;
block|}
return|return
name|matchListener
return|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|StreamListener
operator|.
name|STORE
case|:
name|engine
operator|.
name|flush
argument_list|()
expr_stmt|;
break|break;
case|case
name|StreamListener
operator|.
name|REMOVE_ALL_NODES
case|:
name|engine
operator|.
name|dropIndex
argument_list|(
name|document
argument_list|)
expr_stmt|;
break|break;
case|case
name|StreamListener
operator|.
name|REMOVE_SOME_NODES
case|:
name|engine
operator|.
name|remove
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|boolean
name|reindex
parameter_list|)
block|{
name|engine
operator|.
name|dropIndex
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|checkIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
comment|// Not implemented
return|return
literal|false
return|;
block|}
specifier|public
name|Occurrences
index|[]
name|scanIndex
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|Map
name|hints
parameter_list|)
block|{
comment|// Not implemented
return|return
operator|new
name|Occurrences
index|[
literal|0
index|]
return|;
block|}
specifier|private
class|class
name|FTStreamListener
extends|extends
name|AbstractStreamListener
block|{
specifier|private
name|Stack
argument_list|<
name|ElementContent
argument_list|>
name|contentStack
init|=
operator|new
name|Stack
argument_list|<
name|ElementContent
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|FTStreamListener
parameter_list|()
block|{
comment|//Nothing to do
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
specifier|final
name|boolean
name|mixedContent
init|=
name|config
operator|.
name|matchMixedElement
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixedContent
operator|||
name|config
operator|.
name|hasQNameIndex
argument_list|(
name|element
operator|.
name|getQName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|ElementContent
name|contentBuf
init|=
operator|new
name|ElementContent
argument_list|(
name|element
operator|.
name|getQName
argument_list|()
argument_list|,
name|mixedContent
operator|||
name|config
operator|.
name|preserveMixedContent
argument_list|(
name|element
operator|.
name|getQName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|contentStack
operator|.
name|push
argument_list|(
name|contentBuf
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|startElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|ElementImpl
name|element
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
specifier|final
name|boolean
name|mixedContent
init|=
name|config
operator|.
name|matchMixedElement
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|mixedContent
operator|||
name|config
operator|.
name|hasQNameIndex
argument_list|(
name|element
operator|.
name|getQName
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|ElementContent
name|contentBuf
init|=
name|contentStack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|element
operator|.
name|setQName
argument_list|(
operator|new
name|QName
argument_list|(
name|element
operator|.
name|getQName
argument_list|()
argument_list|,
name|ElementValue
operator|.
name|ELEMENT
argument_list|)
argument_list|)
expr_stmt|;
name|engine
operator|.
name|storeText
argument_list|(
name|element
argument_list|,
name|contentBuf
argument_list|,
name|mixedContent
condition|?
name|NativeTextEngine
operator|.
name|FOURTH_OPTION
else|:
name|NativeTextEngine
operator|.
name|TEXT_BY_QNAME
argument_list|,
literal|null
argument_list|,
name|mode
operator|==
name|REMOVE_ALL_NODES
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|endElement
argument_list|(
name|transaction
argument_list|,
name|element
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**          *          * @param transaction          * @param text          * @param path          */
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|CharacterDataImpl
name|text
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
name|engine
operator|.
name|storeText
argument_list|(
name|text
argument_list|,
name|NativeTextEngine
operator|.
name|TOKENIZE
argument_list|,
name|config
argument_list|,
name|mode
operator|==
name|REMOVE_ALL_NODES
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|config
operator|.
name|match
argument_list|(
name|path
argument_list|)
condition|)
block|{
specifier|final
name|int
name|tokenize
init|=
name|config
operator|.
name|preserveContent
argument_list|(
name|path
argument_list|)
condition|?
name|NativeTextEngine
operator|.
name|DO_NOT_TOKENIZE
else|:
name|NativeTextEngine
operator|.
name|TOKENIZE
decl_stmt|;
name|engine
operator|.
name|storeText
argument_list|(
name|text
argument_list|,
name|tokenize
argument_list|,
name|config
argument_list|,
name|mode
operator|==
name|REMOVE_ALL_NODES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|contentStack
operator|.
name|isEmpty
argument_list|()
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
name|contentStack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|ElementContent
name|next
init|=
name|contentStack
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|next
operator|.
name|append
argument_list|(
name|text
operator|.
name|getXMLString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|characters
argument_list|(
name|transaction
argument_list|,
name|text
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|AttrImpl
name|attrib
parameter_list|,
name|NodePath
name|path
parameter_list|)
block|{
name|path
operator|.
name|addComponent
argument_list|(
name|attrib
operator|.
name|getQName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
operator|||
name|config
operator|.
name|matchAttribute
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|engine
operator|.
name|storeAttribute
argument_list|(
name|attrib
argument_list|,
literal|null
argument_list|,
name|NativeTextEngine
operator|.
name|ATTRIBUTE_NOT_BY_QNAME
argument_list|,
name|config
argument_list|,
name|mode
operator|==
name|REMOVE_ALL_NODES
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|hasQNameIndex
argument_list|(
name|attrib
operator|.
name|getQName
argument_list|()
argument_list|)
condition|)
block|{
name|engine
operator|.
name|storeAttribute
argument_list|(
name|attrib
argument_list|,
literal|null
argument_list|,
name|NativeTextEngine
operator|.
name|ATTRIBUTE_BY_QNAME
argument_list|,
name|config
argument_list|,
name|mode
operator|==
name|REMOVE_ALL_NODES
argument_list|)
expr_stmt|;
block|}
name|path
operator|.
name|removeLastComponent
argument_list|()
expr_stmt|;
name|super
operator|.
name|attribute
argument_list|(
name|transaction
argument_list|,
name|attrib
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexWorker
name|getWorker
parameter_list|()
block|{
return|return
name|FTIndexWorker
operator|.
name|this
return|;
block|}
block|}
block|}
end_class

end_unit

