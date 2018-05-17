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
name|DocumentMetadata
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
name|BinaryDocument
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
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
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_comment
comment|/**  * A document from the database, either binary or XML.  Note that querying a non-XML  * document is harmless, but will never return any results.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|Document
extends|extends
name|NamedResource
block|{
comment|/** 	 * Listener for events affecting documents.  The three possible actions are document 	 * creation, update (modification), and deletion. 	 * 	 * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a> 	 */
specifier|public
interface|interface
name|Listener
extends|extends
name|org
operator|.
name|exist
operator|.
name|fluent
operator|.
name|Listener
block|{
comment|/** 		 * Respond to a document event.  		 * 		 * @param ev the details of the event 		 */
name|void
name|handle
parameter_list|(
name|Document
operator|.
name|Event
name|ev
parameter_list|)
function_decl|;
block|}
comment|/** 	 * An event that concerns a document. 	 * 	 * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a> 	 */
specifier|public
specifier|static
class|class
name|Event
extends|extends
name|org
operator|.
name|exist
operator|.
name|fluent
operator|.
name|Listener
operator|.
name|Event
block|{
comment|/** 		 * The document that's the subject of this event. 		 * Note that for some timing/action combinations, this field might be<code>null</code>. 		 */
specifier|public
specifier|final
name|Document
name|document
decl_stmt|;
name|Event
parameter_list|(
name|Trigger
name|trigger
parameter_list|,
name|String
name|path
parameter_list|,
name|Document
name|document
parameter_list|)
block|{
name|super
argument_list|(
name|trigger
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
block|}
name|Event
parameter_list|(
name|ListenerManager
operator|.
name|EventKey
name|key
parameter_list|,
name|Document
name|document
parameter_list|)
block|{
name|super
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|this
operator|.
name|document
operator|=
name|document
expr_stmt|;
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
return|return
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
operator|&&
operator|(
name|document
operator|==
literal|null
condition|?
operator|(
operator|(
name|Event
operator|)
name|o
operator|)
operator|.
name|document
operator|==
literal|null
else|:
name|document
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Event
operator|)
name|o
operator|)
operator|.
name|document
argument_list|)
operator|)
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
name|super
operator|.
name|hashCode
argument_list|()
operator|*
literal|37
operator|+
operator|(
name|document
operator|==
literal|null
condition|?
literal|0
else|:
name|document
operator|.
name|hashCode
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|buf
operator|.
name|insert
argument_list|(
literal|3
argument_list|,
literal|"Document."
argument_list|)
expr_stmt|;
name|buf
operator|.
name|insert
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
literal|", "
operator|+
name|document
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** 	 * The facet that gives access to a document's listeners. 	 * 	 * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a> 	 */
specifier|public
class|class
name|ListenersFacet
block|{
comment|/** 		 * Add a listener for this document.  Equivalent to<code>add(EnumSet.of(trigger), listener)</code>. 		 * 		 * @see #add(Set, Document.Listener) 		 * @param trigger the kind of event the listener should be notified of 		 * @param listener the listener to notify of events 		 */
specifier|public
name|void
name|add
parameter_list|(
name|Trigger
name|trigger
parameter_list|,
name|Document
operator|.
name|Listener
name|listener
parameter_list|)
block|{
name|add
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|trigger
argument_list|)
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/** 		 * Add a listener for this document. 		 *  		 * @param triggers the kinds of events the listener should be notified of; the set must not be empty 		 * @param listener the listener to notify of events  		 */
specifier|public
name|void
name|add
parameter_list|(
name|Set
argument_list|<
name|Trigger
argument_list|>
name|triggers
parameter_list|,
name|Document
operator|.
name|Listener
name|listener
parameter_list|)
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
name|ListenerManager
operator|.
name|INSTANCE
operator|.
name|add
argument_list|(
name|path
argument_list|()
argument_list|,
name|ListenerManager
operator|.
name|Depth
operator|.
name|ZERO
argument_list|,
name|triggers
argument_list|,
name|listener
argument_list|,
name|Document
operator|.
name|this
argument_list|)
expr_stmt|;
block|}
comment|/** 		 * Remove a listener previously added through this facet.  This will remove the listener from 		 * all combinations of timing and action for this document, even if added via multiple invocations 		 * of the<code>add</code> methods.  However, it will not remove the listener from combinations 		 * added through other facets. 		 * 		 * @param listener the listener to remove 		 */
specifier|public
name|void
name|remove
parameter_list|(
name|Document
operator|.
name|Listener
name|listener
parameter_list|)
block|{
comment|// don't check for staleness here, might still want to remove listeners after doc is gone
name|ListenerManager
operator|.
name|INSTANCE
operator|.
name|remove
argument_list|(
name|path
argument_list|()
argument_list|,
name|ListenerManager
operator|.
name|Depth
operator|.
name|ZERO
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * The metadata facet for this document.  Allows access to and manipulation of various aspects 	 * of the document's metadata, including its permissions and various timestamps. 	 * NOTE:  The interface is fairly bare-bones right now, until I figure out the use cases and flesh 	 * it out a bit. 	 */
specifier|public
specifier|static
class|class
name|MetadataFacet
extends|extends
name|NamedResource
operator|.
name|MetadataFacet
block|{
specifier|private
specifier|final
name|DocumentMetadata
name|docMetadata
decl_stmt|;
specifier|private
name|MetadataFacet
parameter_list|(
specifier|final
name|DocumentImpl
name|doc
parameter_list|,
specifier|final
name|Database
name|db
parameter_list|)
block|{
name|super
argument_list|(
name|doc
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|this
operator|.
name|docMetadata
operator|=
name|doc
operator|.
name|getMetadata
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Date
name|creationDate
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|docMetadata
operator|.
name|getCreated
argument_list|()
argument_list|)
return|;
block|}
comment|/** 		 * Return the time at which this document was last modified. 		 * 		 * @return the date of the last modification 		 */
specifier|public
name|Date
name|lastModificationDate
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|docMetadata
operator|.
name|getLastModified
argument_list|()
argument_list|)
return|;
block|}
comment|/** 		 * Return the recorded MIME type of this document. 		 *  		 * @return this document's MIME type 		 */
specifier|public
name|String
name|mimeType
parameter_list|()
block|{
return|return
name|docMetadata
operator|.
name|getMimeType
argument_list|()
return|;
block|}
comment|/** 		 * Set the MIME type of this document. 		 * 		 * @param mimeType this document's desired MIME type 		 */
specifier|public
name|void
name|setMimeType
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
name|docMetadata
operator|.
name|setMimeType
argument_list|(
name|mimeType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|DocumentImpl
name|doc
decl_stmt|;
specifier|protected
name|StaleMarker
name|staleMarker
decl_stmt|;
specifier|private
name|ListenersFacet
name|listeners
decl_stmt|;
specifier|private
name|MetadataFacet
name|metadata
decl_stmt|;
name|Document
parameter_list|(
name|DocumentImpl
name|dimpl
parameter_list|,
name|NamespaceMap
name|namespaceBindings
parameter_list|,
name|Database
name|db
parameter_list|)
block|{
name|super
argument_list|(
name|namespaceBindings
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|changeDoc
argument_list|(
name|dimpl
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|changeDoc
parameter_list|(
name|DocumentImpl
name|dimpl
parameter_list|)
block|{
if|if
condition|(
name|dimpl
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"no such document"
argument_list|)
throw|;
assert|assert
name|getClass
argument_list|()
operator|==
operator|(
name|dimpl
operator|instanceof
name|BinaryDocument
condition|?
name|Document
operator|.
name|class
else|:
name|XMLDocument
operator|.
name|class
operator|)
assert|;
name|this
operator|.
name|doc
operator|=
name|dimpl
expr_stmt|;
name|String
name|path
init|=
name|dimpl
operator|.
name|getURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
decl_stmt|;
name|staleMarker
operator|=
operator|new
name|StaleMarker
argument_list|()
expr_stmt|;
name|staleMarker
operator|.
name|track
argument_list|(
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// folder
name|staleMarker
operator|.
name|track
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// document
block|}
specifier|static
name|Document
name|newInstance
parameter_list|(
name|DocumentImpl
name|dimpl
parameter_list|,
name|Resource
name|origin
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|dimpl
argument_list|,
name|origin
operator|.
name|namespaceBindings
argument_list|()
operator|.
name|extend
argument_list|()
argument_list|,
name|origin
operator|.
name|database
argument_list|()
argument_list|)
return|;
block|}
specifier|static
name|Document
name|newInstance
parameter_list|(
name|DocumentImpl
name|dimpl
parameter_list|,
name|NamespaceMap
name|namespaceBindings
parameter_list|,
name|Database
name|db
parameter_list|)
block|{
return|return
name|dimpl
operator|instanceof
name|BinaryDocument
condition|?
operator|new
name|Document
argument_list|(
name|dimpl
argument_list|,
name|namespaceBindings
argument_list|,
name|db
argument_list|)
else|:
operator|new
name|XMLDocument
argument_list|(
name|dimpl
argument_list|,
name|namespaceBindings
argument_list|,
name|db
argument_list|)
return|;
block|}
annotation|@
name|Override
name|Sequence
name|convertToSequence
parameter_list|()
block|{
comment|// TODO: figure out if binary documents can be converted after all
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"binary resources are not convertible"
argument_list|)
throw|;
block|}
comment|/** 	 * Return the listeners facet for this document, used for adding and removing document listeners. 	 * 	 * @return the listeners facet for this document 	 */
specifier|public
name|ListenersFacet
name|listeners
parameter_list|()
block|{
if|if
condition|(
name|listeners
operator|==
literal|null
condition|)
name|listeners
operator|=
operator|new
name|ListenersFacet
argument_list|()
expr_stmt|;
return|return
name|listeners
return|;
block|}
annotation|@
name|Override
specifier|public
name|MetadataFacet
name|metadata
parameter_list|()
block|{
if|if
condition|(
name|metadata
operator|==
literal|null
condition|)
name|metadata
operator|=
operator|new
name|MetadataFacet
argument_list|(
name|doc
argument_list|,
name|db
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
comment|/** 	 * Cast this document to an {@link XMLDocument}, if possible. 	 * 	 * @return this document cast as an XML document 	 * @throws DatabaseException if this document is not an XML document 	 */
specifier|public
name|XMLDocument
name|xml
parameter_list|()
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
literal|"document is not XML"
argument_list|)
throw|;
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
name|Document
condition|)
return|return
name|doc
operator|.
name|getDocId
argument_list|()
operator|==
operator|(
operator|(
name|Document
operator|)
name|o
operator|)
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
return|;
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
name|doc
operator|.
name|getDocId
argument_list|()
return|;
block|}
comment|/** 	 * Return a string representation of the reference to this document.  The representation will 	 * list the document's path, but will not include its contents. 	 *  	 * @return a string representation of this document 	 */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"document '"
operator|+
name|path
argument_list|()
operator|+
literal|"'"
return|;
block|}
comment|/** 	 * Return the local filename of this document. This name will never contain 	 * slashes ('/'). 	 *  	 * @return the local filename of this document 	 */
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|doc
operator|.
name|getFileURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
return|;
block|}
comment|/** 	 * Return the full path of this document.  This is the path of its parent folder plus its 	 * filename. 	 * 	 * @return the full path of this document 	 */
annotation|@
name|Override
specifier|public
name|String
name|path
parameter_list|()
block|{
comment|// TODO:  is this check necessary?
comment|// if (doc.getURI() == null) throw new DatabaseException("handle invalid, document may have been deleted");
return|return
name|Database
operator|.
name|normalizePath
argument_list|(
name|doc
operator|.
name|getURI
argument_list|()
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Return the folder that contains this document. 	 * 	 * @return the folder that contains this document 	 */
specifier|public
name|Folder
name|folder
parameter_list|()
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
name|String
name|path
init|=
name|path
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
assert|assert
name|i
operator|!=
operator|-
literal|1
assert|;
return|return
operator|new
name|Folder
argument_list|(
name|i
operator|==
literal|0
condition|?
literal|"/"
else|:
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|,
literal|false
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/** 	 * Return the length of this document, in bytes.  For binary documents, this is the actual 	 * size of the file; for XML documents, this is the approximate amount of space that the 	 * document occupies in the database, and is unrelated to its serialized length. 	 * 	 * @return the length of this document, in bytes 	 */
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|doc
operator|.
name|getContentLength
argument_list|()
return|;
block|}
comment|/** 	 * Delete this document from the database. 	 */
annotation|@
name|Override
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
name|folder
argument_list|()
operator|.
name|removeDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Copy this document to another collection, potentially changing the copy's name in the process. 	 * @see Name 	 * 	 * @param destination the destination folder for the copy 	 * @param name the desired name for the copy 	 * @return the new copy of the document 	 */
annotation|@
name|Override
specifier|public
name|Document
name|copy
parameter_list|(
name|Folder
name|destination
parameter_list|,
name|Name
name|name
parameter_list|)
block|{
return|return
name|newInstance
argument_list|(
name|moveOrCopy
argument_list|(
name|destination
argument_list|,
name|name
argument_list|,
literal|true
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/** 	 * Move this document to another collection, potentially changing its name in the process. 	 * This document will refer to the document in its new location after this method returns. 	 * You can easily use this method to move a document without changing its name 	 * (<code>doc.move(newFolder, Name.keepCreate())</code>) or to rename a document 	 * without changing its location (<code>doc.move(doc.folder(), Name.create(newName))</code>). 	 * @see Name 	 * 	 * @param destination the destination folder for the move 	 * @param name the desired name for the moved document 	 */
annotation|@
name|Override
specifier|public
name|void
name|move
parameter_list|(
name|Folder
name|destination
parameter_list|,
name|Name
name|name
parameter_list|)
block|{
name|changeDoc
argument_list|(
name|moveOrCopy
argument_list|(
name|destination
argument_list|,
name|name
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|DocumentImpl
name|moveOrCopy
parameter_list|(
name|Folder
name|destination
parameter_list|,
name|Name
name|name
parameter_list|,
name|boolean
name|copy
parameter_list|)
block|{
name|db
operator|.
name|checkSame
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|staleMarker
operator|.
name|check
argument_list|()
expr_stmt|;
name|name
operator|.
name|setOldName
argument_list|(
name|name
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|destination
operator|.
name|moveOrCopyDocument
argument_list|(
name|doc
argument_list|,
name|name
argument_list|,
name|copy
argument_list|)
return|;
block|}
comment|/** 	 * Return the contents of this document interpreted as a string.  Binary documents are 	 * decoded using the default character encoding specified for the database. 	 *  	 * @see Database#setDefaultCharacterEncoding(String) 	 * @return the contents of this document 	 * @throws DatabaseException if the encoding is not supported or some other unexpected IOException occurs 	 */
specifier|public
name|String
name|contentsAsString
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
name|db
operator|.
name|acquireBroker
argument_list|()
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|)
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|broker
operator|.
name|getBinaryResourceSize
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|)
index|]
decl_stmt|;
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|db
operator|.
name|defaultCharacterEncoding
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|db
operator|.
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Export this document to the given file, overwriting it if it already exists. 	 * 	 * @param destination the file to export to 	 * @throws IOException if the export failed due to an I/O error 	 */
specifier|public
name|void
name|export
parameter_list|(
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|stream
init|=
operator|new
name|BufferedOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|destination
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|write
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Copy the contents of the document to the given stream.  XML documents will use 	 * the default character encoding set for the database. 	 * @see Database#setDefaultCharacterEncoding(String) 	 * 	 * @param stream the output stream to copy the document to 	 * @throws IOException in case of I/O problems; 	 * 		WARNING: I/O exceptions are currently logged and eaten by eXist, so they won't propagate to this layer! 	 */
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|DBBroker
name|broker
init|=
name|db
operator|.
name|acquireBroker
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|.
name|readBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|,
name|stream
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|db
operator|.
name|releaseBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
name|QueryService
name|createQueryService
parameter_list|()
block|{
return|return
name|QueryService
operator|.
name|NULL
return|;
block|}
block|}
end_class

end_unit

