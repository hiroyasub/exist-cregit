begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|ref
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|*
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
name|triggers
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
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_comment
comment|/**  * Internal class not for public use; needs to be public due to external instantiation requirements.  * Mediates between native eXist triggers and db listeners.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|ListenerManager
block|{
specifier|static
name|String
name|getTriggerConfigXml
parameter_list|()
block|{
return|return
literal|"<triggers><trigger event='store update remove create-collection rename-collection delete-collection' class='org.exist.fluent.ListenerManager$TriggerDispatcher'/></triggers>"
return|;
block|}
specifier|static
class|class
name|EventKey
implements|implements
name|Comparable
argument_list|<
name|EventKey
argument_list|>
block|{
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|Trigger
name|trigger
decl_stmt|;
name|EventKey
parameter_list|(
name|String
name|path
parameter_list|,
name|Trigger
name|trigger
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|Database
operator|.
name|normalizePath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|trigger
operator|=
name|trigger
expr_stmt|;
block|}
name|EventKey
parameter_list|(
name|String
name|path
parameter_list|,
name|int
name|existEventCode
parameter_list|,
name|boolean
name|before
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|toTrigger
argument_list|(
name|existEventCode
argument_list|,
name|before
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|Trigger
name|toTrigger
parameter_list|(
name|int
name|code
parameter_list|,
name|boolean
name|before
parameter_list|)
block|{
switch|switch
condition|(
name|code
condition|)
block|{
case|case
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|Trigger
operator|.
name|STORE_DOCUMENT_EVENT
case|:
case|case
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|Trigger
operator|.
name|CREATE_COLLECTION_EVENT
case|:
return|return
name|before
condition|?
name|Trigger
operator|.
name|BEFORE_CREATE
else|:
name|Trigger
operator|.
name|AFTER_CREATE
return|;
case|case
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|Trigger
operator|.
name|UPDATE_DOCUMENT_EVENT
case|:
case|case
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|Trigger
operator|.
name|RENAME_COLLECTION_EVENT
case|:
return|return
name|before
condition|?
name|Trigger
operator|.
name|BEFORE_UPDATE
else|:
name|Trigger
operator|.
name|AFTER_UPDATE
return|;
case|case
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|Trigger
operator|.
name|REMOVE_DOCUMENT_EVENT
case|:
case|case
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|Trigger
operator|.
name|DELETE_COLLECTION_EVENT
case|:
return|return
name|before
condition|?
name|Trigger
operator|.
name|BEFORE_DELETE
else|:
name|Trigger
operator|.
name|AFTER_DELETE
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown exist trigger code "
operator|+
name|code
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|EventKey
condition|)
block|{
name|EventKey
name|that
init|=
operator|(
name|EventKey
operator|)
name|o
decl_stmt|;
return|return
name|path
operator|.
name|equals
argument_list|(
name|that
operator|.
name|path
argument_list|)
operator|&&
name|trigger
operator|==
name|that
operator|.
name|trigger
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|path
operator|.
name|hashCode
argument_list|()
operator|*
literal|37
operator|+
name|trigger
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|EventKey
name|that
parameter_list|)
block|{
name|int
name|r
init|=
name|this
operator|.
name|trigger
operator|.
name|compareTo
argument_list|(
name|that
operator|.
name|trigger
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
literal|0
condition|)
name|r
operator|=
name|that
operator|.
name|path
operator|.
name|compareTo
argument_list|(
name|this
operator|.
name|path
argument_list|)
expr_stmt|;
comment|// reverse order on purpose
return|return
name|r
return|;
block|}
name|boolean
name|matchesAsPrefix
parameter_list|(
name|EventKey
name|that
parameter_list|)
block|{
return|return
name|this
operator|.
name|trigger
operator|==
name|that
operator|.
name|trigger
operator|&&
name|that
operator|.
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
operator|&&
operator|(
name|this
operator|.
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
operator|||
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
operator|==
name|that
operator|.
name|path
operator|.
name|length
argument_list|()
operator|||
name|that
operator|.
name|path
operator|.
name|charAt
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
operator|==
literal|'/'
operator|)
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|ListenerWrapper
block|{
specifier|final
name|Reference
argument_list|<
name|Listener
argument_list|>
name|refListener
decl_stmt|;
specifier|final
name|Resource
name|origin
decl_stmt|;
name|ListenerWrapper
parameter_list|(
name|Listener
name|listener
parameter_list|,
name|Resource
name|origin
parameter_list|)
block|{
name|this
operator|.
name|refListener
operator|=
operator|new
name|WeakReference
argument_list|<
name|Listener
argument_list|>
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|this
operator|.
name|origin
operator|=
name|origin
expr_stmt|;
block|}
specifier|private
name|Document
name|wrap
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
return|return
name|doc
operator|==
literal|null
condition|?
literal|null
else|:
name|Document
operator|.
name|newInstance
argument_list|(
name|doc
argument_list|,
name|origin
argument_list|)
return|;
block|}
specifier|private
name|Folder
name|wrap
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|col
parameter_list|)
block|{
if|if
condition|(
name|col
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|Folder
argument_list|(
name|col
operator|.
name|getURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
argument_list|,
literal|false
argument_list|,
name|origin
argument_list|)
return|;
block|}
name|boolean
name|sameOrNull
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
name|Listener
name|x
init|=
name|refListener
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|x
operator|==
literal|null
operator|||
name|x
operator|==
name|listener
return|;
block|}
name|boolean
name|isAlive
parameter_list|()
block|{
return|return
name|refListener
operator|.
name|get
argument_list|()
operator|!=
literal|null
return|;
block|}
name|void
name|fireDocumentEvent
parameter_list|(
name|EventKey
name|key
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|Listener
name|listener
init|=
name|refListener
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|listener
operator|instanceof
name|Document
operator|.
name|Listener
condition|)
operator|(
operator|(
name|Document
operator|.
name|Listener
operator|)
name|listener
operator|)
operator|.
name|handle
argument_list|(
operator|new
name|Document
operator|.
name|Event
argument_list|(
name|key
argument_list|,
name|wrap
argument_list|(
name|doc
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|void
name|fireFolderEvent
parameter_list|(
name|EventKey
name|key
parameter_list|,
name|Collection
name|col
parameter_list|)
block|{
name|Listener
name|listener
init|=
name|refListener
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|listener
operator|instanceof
name|Folder
operator|.
name|Listener
condition|)
operator|(
operator|(
name|Folder
operator|.
name|Listener
operator|)
name|listener
operator|)
operator|.
name|handle
argument_list|(
operator|new
name|Folder
operator|.
name|Event
argument_list|(
name|key
argument_list|,
name|wrap
argument_list|(
name|col
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
enum|enum
name|Depth
block|{
comment|/** 		 * Targets matching the given path exactly. 		 */
name|ZERO
block|,
comment|/** 		 * Targets one level below the given path (i.e. inside the given folder). 		 */
name|ONE
block|,
comment|/** 		 * Targets matching or at any level below the given path. 		 */
name|MANY
block|}
specifier|private
specifier|final
name|Map
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
index|[]
name|listenerMaps
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|private
name|ListenerManager
parameter_list|()
block|{
name|listenerMaps
operator|=
operator|new
name|Map
index|[
name|Depth
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
expr_stmt|;
name|listenerMaps
index|[
name|Depth
operator|.
name|ZERO
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|listenerMaps
index|[
name|Depth
operator|.
name|ONE
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|listenerMaps
index|[
name|Depth
operator|.
name|MANY
operator|.
name|ordinal
argument_list|()
index|]
operator|=
name|Collections
operator|.
name|synchronizedSortedMap
argument_list|(
operator|new
name|TreeMap
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkListenerType
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|listener
operator|instanceof
name|Document
operator|.
name|Listener
operator|||
name|listener
operator|instanceof
name|Folder
operator|.
name|Listener
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid listener type "
operator|+
name|listener
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
name|void
name|add
parameter_list|(
name|String
name|pathPrefix
parameter_list|,
name|Depth
name|depth
parameter_list|,
name|Set
argument_list|<
name|Trigger
argument_list|>
name|triggers
parameter_list|,
name|Listener
name|listener
parameter_list|,
name|Resource
name|origin
parameter_list|)
block|{
name|checkListenerType
argument_list|(
name|listener
argument_list|)
expr_stmt|;
if|if
condition|(
name|triggers
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot add listener with empty set of triggers"
argument_list|)
throw|;
for|for
control|(
name|Trigger
name|trigger
range|:
name|triggers
control|)
block|{
name|EventKey
name|key
init|=
operator|new
name|EventKey
argument_list|(
name|pathPrefix
argument_list|,
name|trigger
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
name|list
decl_stmt|;
synchronized|synchronized
init|(
name|listenerMaps
index|[
name|depth
operator|.
name|ordinal
argument_list|()
index|]
init|)
block|{
name|list
operator|=
name|listenerMaps
index|[
name|depth
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|LinkedList
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|()
expr_stmt|;
name|listenerMaps
index|[
name|depth
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
block|}
synchronized|synchronized
init|(
name|list
init|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|ListenerWrapper
argument_list|(
name|listener
argument_list|,
name|origin
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|remove
parameter_list|(
name|String
name|pathPrefix
parameter_list|,
name|Depth
name|depth
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
name|remove
argument_list|(
name|pathPrefix
argument_list|,
name|listenerMaps
index|[
name|depth
operator|.
name|ordinal
argument_list|()
index|]
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
name|void
name|remove
parameter_list|(
name|Listener
name|listener
parameter_list|)
block|{
for|for
control|(
name|Map
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
name|map
range|:
name|listenerMaps
control|)
name|remove
argument_list|(
literal|null
argument_list|,
name|map
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
name|map
parameter_list|,
name|Listener
name|listener
parameter_list|)
block|{
name|checkListenerType
argument_list|(
name|listener
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|map
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
argument_list|>
name|it
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
name|entry
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
operator|&&
operator|!
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|path
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
continue|continue;
synchronized|synchronized
init|(
name|entry
operator|.
name|getValue
argument_list|()
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|ListenerWrapper
argument_list|>
name|it2
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|it2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|it2
operator|.
name|next
argument_list|()
operator|.
name|sameOrNull
argument_list|(
name|listener
argument_list|)
condition|)
name|it2
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|void
name|fire
parameter_list|(
name|EventKey
name|key
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|fire
argument_list|(
name|key
argument_list|,
name|doc
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|void
name|fire
parameter_list|(
name|EventKey
name|key
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|col
parameter_list|)
block|{
name|fire
argument_list|(
name|key
argument_list|,
literal|null
argument_list|,
name|col
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|fire
parameter_list|(
name|EventKey
name|key
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|col
parameter_list|,
name|boolean
name|documentEvent
parameter_list|)
block|{
name|fire
argument_list|(
name|listenerMaps
index|[
name|Depth
operator|.
name|ZERO
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|key
argument_list|,
name|doc
argument_list|,
name|col
argument_list|,
name|documentEvent
argument_list|)
expr_stmt|;
name|int
name|k
init|=
name|key
operator|.
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
assert|assert
name|k
operator|!=
operator|-
literal|1
assert|;
if|if
condition|(
name|k
operator|>
literal|0
condition|)
block|{
name|EventKey
name|trimmedKey
init|=
operator|new
name|EventKey
argument_list|(
name|key
operator|.
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|k
argument_list|)
argument_list|,
name|key
operator|.
name|trigger
argument_list|)
decl_stmt|;
name|fire
argument_list|(
name|listenerMaps
index|[
name|Depth
operator|.
name|ONE
operator|.
name|ordinal
argument_list|()
index|]
operator|.
name|get
argument_list|(
name|trimmedKey
argument_list|)
argument_list|,
name|key
argument_list|,
name|doc
argument_list|,
name|col
argument_list|,
name|documentEvent
argument_list|)
expr_stmt|;
block|}
name|SortedMap
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
name|map
init|=
operator|(
name|SortedMap
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
operator|)
name|listenerMaps
index|[
name|Depth
operator|.
name|MANY
operator|.
name|ordinal
argument_list|()
index|]
decl_stmt|;
name|SortedMap
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
name|tailMap
decl_stmt|;
synchronized|synchronized
init|(
name|map
init|)
block|{
name|tailMap
operator|=
operator|new
name|TreeMap
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
argument_list|(
name|map
operator|.
name|tailMap
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|EventKey
argument_list|,
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|>
name|entry
range|:
name|tailMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|EventKey
name|target
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|target
operator|.
name|matchesAsPrefix
argument_list|(
name|key
argument_list|)
condition|)
break|break;
name|fire
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|key
argument_list|,
name|doc
argument_list|,
name|col
argument_list|,
name|documentEvent
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fire
parameter_list|(
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
name|list
parameter_list|,
name|EventKey
name|key
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|col
parameter_list|,
name|boolean
name|documentEvent
parameter_list|)
block|{
if|if
condition|(
name|list
operator|==
literal|null
condition|)
return|return;
name|List
argument_list|<
name|ListenerWrapper
argument_list|>
name|listCopy
decl_stmt|;
synchronized|synchronized
init|(
name|list
init|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|ListenerWrapper
argument_list|>
name|it
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|it
operator|.
name|next
argument_list|()
operator|.
name|isAlive
argument_list|()
condition|)
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|listCopy
operator|=
operator|new
name|ArrayList
argument_list|<
name|ListenerWrapper
argument_list|>
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|documentEvent
condition|)
block|{
for|for
control|(
name|ListenerWrapper
name|wrap
range|:
name|listCopy
control|)
name|wrap
operator|.
name|fireDocumentEvent
argument_list|(
name|key
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|ListenerWrapper
name|wrap
range|:
name|listCopy
control|)
name|wrap
operator|.
name|fireFolderEvent
argument_list|(
name|key
argument_list|,
name|col
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
specifier|final
name|ListenerManager
name|INSTANCE
init|=
operator|new
name|ListenerManager
argument_list|()
decl_stmt|;
comment|/** 	 * A centralized trigger listener for eXist that dispatches back to the singleton 	 *<code>ListenerManager</code>.  Public only because it needs to be instantiated 	 * via reflection; for internal use only. 	 * 	 * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a> 	 */
specifier|public
specifier|static
class|class
name|TriggerDispatcher
implements|implements
name|DocumentTrigger
implements|,
name|CollectionTrigger
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|TriggerDispatcher
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|validating
decl_stmt|;
specifier|private
name|ContentHandler
name|contentHandler
decl_stmt|;
specifier|private
name|LexicalHandler
name|lexicalHandler
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|parent
parameter_list|,
name|Map
name|parameters
parameter_list|)
throws|throws
name|CollectionConfigurationException
block|{
comment|// nothing to do
block|}
specifier|public
name|void
name|prepare
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
block|{
name|EventKey
name|key
init|=
operator|new
name|EventKey
argument_list|(
name|documentPath
operator|.
name|getCollectionPath
argument_list|()
argument_list|,
name|event
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|INSTANCE
operator|.
name|fire
argument_list|(
name|key
argument_list|,
name|existingDocument
argument_list|)
expr_stmt|;
block|}
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
name|txn
parameter_list|,
name|XmldbURI
name|documentPath
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
block|{
name|EventKey
name|key
init|=
operator|new
name|EventKey
argument_list|(
name|documentPath
operator|.
name|getCollectionPath
argument_list|()
argument_list|,
name|event
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|INSTANCE
operator|.
name|fire
argument_list|(
name|key
argument_list|,
name|key
operator|.
name|trigger
operator|==
name|Trigger
operator|.
name|AFTER_DELETE
condition|?
literal|null
else|:
name|document
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|prepare
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|TriggerException
block|{
name|EventKey
name|key
init|=
operator|new
name|EventKey
argument_list|(
name|newName
argument_list|,
name|event
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|INSTANCE
operator|.
name|fire
argument_list|(
name|key
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
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
name|txn
parameter_list|,
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
name|EventKey
name|key
init|=
operator|new
name|EventKey
argument_list|(
name|newName
argument_list|,
name|event
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|INSTANCE
operator|.
name|fire
argument_list|(
name|key
argument_list|,
name|key
operator|.
name|trigger
operator|==
name|Trigger
operator|.
name|AFTER_DELETE
condition|?
literal|null
else|:
name|collection
argument_list|)
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
name|void
name|setOutputHandler
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
name|void
name|setLexicalOutputHandler
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
name|ContentHandler
name|getOutputHandler
parameter_list|()
block|{
return|return
name|contentHandler
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
name|getLexicalOutputHandler
parameter_list|()
block|{
return|return
name|lexicalHandler
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
name|contentHandler
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
name|contentHandler
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
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
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
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
name|contentHandler
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
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
name|contentHandler
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
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
name|contentHandler
operator|.
name|skippedEntity
argument_list|(
name|name
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
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
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
name|contentHandler
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
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
name|uri
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
name|uri
argument_list|)
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
specifier|public
name|void
name|endCDATA
parameter_list|()
throws|throws
name|SAXException
block|{
name|lexicalHandler
operator|.
name|endCDATA
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|endDTD
parameter_list|()
throws|throws
name|SAXException
block|{
name|lexicalHandler
operator|.
name|endDTD
argument_list|()
expr_stmt|;
block|}
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
name|lexicalHandler
operator|.
name|endEntity
argument_list|(
name|name
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
name|lexicalHandler
operator|.
name|startCDATA
argument_list|()
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
block|}
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
name|lexicalHandler
operator|.
name|startEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

