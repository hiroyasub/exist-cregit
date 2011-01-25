begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

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
name|eclipse
operator|.
name|jetty
operator|.
name|util
operator|.
name|log
operator|.
name|Log
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
name|management
operator|.
name|impl
operator|.
name|SanityReport
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
name|BrokerPool
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
name|io
operator|.
name|VariableByteInput
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
name|io
operator|.
name|VariableByteInputStream
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
name|io
operator|.
name|VariableByteOutputStream
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
name|Configuration
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
name|hashtable
operator|.
name|Int2ObjectHashMap
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
name|hashtable
operator|.
name|Object2IntHashMap
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
name|sanity
operator|.
name|SanityCheck
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
name|Attr
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
name|Element
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Maintains a global symbol table shared by a database instance. The symbol  * table maps namespace URIs and node names to unique, numeric ids. Internally,  * the db does not store node QNames in clear text. Instead, it uses the numeric ids  * maintained here.  *   * The global SymbolTable singleton can be retrieved from {@link org.exist.storage.BrokerPool#getSymbols()}.  * It is saved into the database file "symbols.dbx".  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|SymbolTable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|FILE_NAME
init|=
literal|"symbols.dbx"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|short
name|FILE_FORMAT_VERSION_ID
init|=
literal|8
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|short
name|LEGACY_FILE_FORMAT_VERSION_ID
init|=
literal|7
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|NAME_ID_TYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|NAMESPACE_ID_TYPE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|MIME_ID_TYPE
init|=
literal|2
decl_stmt|;
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
name|SymbolTable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|int
name|LENGTH_LOCAL_NAME
init|=
literal|2
decl_stmt|;
comment|//sizeof short
specifier|public
specifier|static
name|int
name|LENGTH_NS_URI
init|=
literal|2
decl_stmt|;
comment|//sizeof short
comment|/** Maps local node names to an integer id */
specifier|protected
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
name|nameSymbols
init|=
operator|new
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|/** Maps int ids to local node names */
specifier|protected
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
literal|200
index|]
decl_stmt|;
comment|/** Maps namespace URIs to an integer id */
specifier|protected
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
name|nsSymbols
init|=
operator|new
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|/** Maps int ids to namespace URIs */
specifier|protected
name|String
index|[]
name|namespaces
init|=
operator|new
name|String
index|[
literal|200
index|]
decl_stmt|;
comment|/**      * Temporary name pool to share QName instances during indexing.      */
specifier|protected
name|QNamePool
name|namePool
init|=
operator|new
name|QNamePool
argument_list|()
decl_stmt|;
specifier|protected
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
name|mimeTypeByName
init|=
operator|new
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
argument_list|(
literal|32
argument_list|)
decl_stmt|;
specifier|protected
name|String
index|[]
name|mimeTypeById
init|=
operator|new
name|String
index|[
literal|32
index|]
decl_stmt|;
comment|/** contains the next mime type id to be used */
specifier|protected
name|short
name|maxMime
init|=
literal|0
decl_stmt|;
comment|/** contains the next local name id to be used */
specifier|protected
name|short
name|max
init|=
literal|0
decl_stmt|;
comment|/** contains the next namespace URI id to be used */
specifier|protected
name|short
name|nsMax
init|=
literal|0
decl_stmt|;
comment|/** set to true if the symbol table needs to be saved */
specifier|protected
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
comment|/** the underlying symbols.dbx file */
specifier|protected
name|File
name|file
decl_stmt|;
specifier|protected
name|VariableByteOutputStream
name|outBuffer
init|=
operator|new
name|VariableByteOutputStream
argument_list|(
literal|512
argument_list|)
decl_stmt|;
specifier|protected
name|OutputStream
name|outStream
init|=
literal|null
decl_stmt|;
specifier|public
name|SymbolTable
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|File
name|dataDir
parameter_list|)
throws|throws
name|EXistException
block|{
name|file
operator|=
operator|new
name|File
argument_list|(
name|dataDir
argument_list|,
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|saveSymbols
argument_list|()
expr_stmt|;
block|}
else|else
name|loadSymbols
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SymbolTable
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Configuration
name|config
parameter_list|)
throws|throws
name|EXistException
block|{
name|this
argument_list|(
name|pool
argument_list|,
operator|new
name|File
argument_list|(
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|FILE_NAME
return|;
block|}
comment|/**      * Retrieve a shared QName instance from the temporary pool.      *      * TODO: make the namePool thread-local to avoid synchronization.      *      * @param namespaceURI      * @param localName      * @param prefix      */
specifier|public
specifier|synchronized
name|QName
name|getQName
parameter_list|(
name|short
name|type
parameter_list|,
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|byte
name|itype
init|=
name|type
operator|==
name|Node
operator|.
name|ATTRIBUTE_NODE
condition|?
name|ElementValue
operator|.
name|ATTRIBUTE
else|:
name|ElementValue
operator|.
name|ELEMENT
decl_stmt|;
name|QName
name|qn
init|=
name|namePool
operator|.
name|get
argument_list|(
name|itype
argument_list|,
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|qn
operator|==
literal|null
condition|)
block|{
name|qn
operator|=
name|namePool
operator|.
name|add
argument_list|(
name|itype
argument_list|,
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
return|return
name|qn
return|;
block|}
comment|/**      * Return a unique id for the local node name of the specified element.      *       * @param element      */
specifier|public
specifier|synchronized
name|short
name|getSymbol
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
name|short
name|id
init|=
operator|(
name|short
operator|)
name|nameSymbols
operator|.
name|get
argument_list|(
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
operator|-
literal|1
condition|)
return|return
name|id
return|;
name|id
operator|=
operator|++
name|max
expr_stmt|;
name|nameSymbols
operator|.
name|put
argument_list|(
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ensureCapacity
argument_list|()
expr_stmt|;
name|names
index|[
name|id
index|]
operator|=
name|element
operator|.
name|getLocalName
argument_list|()
expr_stmt|;
name|write
argument_list|(
name|NAME_ID_TYPE
argument_list|,
name|id
argument_list|,
name|names
index|[
name|id
index|]
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**      * Return a unique id for the local node name of the specified attribute.      *       * @param attr      */
specifier|public
specifier|synchronized
name|short
name|getSymbol
parameter_list|(
name|Attr
name|attr
parameter_list|)
block|{
name|String
name|localName
init|=
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|key
init|=
literal|'@'
operator|+
name|localName
decl_stmt|;
name|short
name|id
init|=
operator|(
name|short
operator|)
name|nameSymbols
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
operator|-
literal|1
condition|)
return|return
name|id
return|;
name|id
operator|=
operator|++
name|max
expr_stmt|;
name|nameSymbols
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ensureCapacity
argument_list|()
expr_stmt|;
name|names
index|[
name|id
index|]
operator|=
name|localName
expr_stmt|;
name|write
argument_list|(
name|NAME_ID_TYPE
argument_list|,
name|id
argument_list|,
name|localName
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**      * Returns a unique id for the specified local name. If the name is      * the local name of an attribute, it should start with a '@' character.      *       * @param name      */
specifier|public
specifier|synchronized
name|short
name|getSymbol
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name is empty"
argument_list|)
throw|;
name|short
name|id
init|=
operator|(
name|short
operator|)
name|nameSymbols
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
operator|-
literal|1
condition|)
return|return
name|id
return|;
name|id
operator|=
operator|++
name|max
expr_stmt|;
name|nameSymbols
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ensureCapacity
argument_list|()
expr_stmt|;
name|names
index|[
name|id
index|]
operator|=
name|name
expr_stmt|;
name|write
argument_list|(
name|NAME_ID_TYPE
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**      * Returns a unique id for the specified namespace URI.      *       * @param ns      */
specifier|public
specifier|synchronized
name|short
name|getNSSymbol
parameter_list|(
name|String
name|ns
parameter_list|)
block|{
if|if
condition|(
name|ns
operator|==
literal|null
operator|||
name|ns
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
name|short
name|id
init|=
operator|(
name|short
operator|)
name|nsSymbols
operator|.
name|get
argument_list|(
name|ns
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
operator|-
literal|1
condition|)
return|return
name|id
return|;
name|id
operator|=
operator|++
name|nsMax
expr_stmt|;
name|nsSymbols
operator|.
name|put
argument_list|(
name|ns
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|ensureCapacity
argument_list|()
expr_stmt|;
name|namespaces
index|[
name|id
index|]
operator|=
name|ns
expr_stmt|;
name|write
argument_list|(
name|NAMESPACE_ID_TYPE
argument_list|,
name|id
argument_list|,
name|ns
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**      * Returns the namespace URI registered for the id or null      * if the namespace URI is not known. Returns the empty string      * if the namespace is empty.      *       * @param id      */
specifier|public
specifier|synchronized
name|String
name|getNamespace
parameter_list|(
name|short
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|<=
literal|0
operator|||
name|id
operator|>
name|nsMax
condition|)
return|return
literal|""
return|;
return|return
name|namespaces
index|[
name|id
index|]
return|;
block|}
comment|/**      * Returns true if the symbol table needs to be saved      * to persistent storage.      *       */
specifier|public
specifier|synchronized
name|boolean
name|hasChanged
parameter_list|()
block|{
return|return
name|changed
return|;
block|}
comment|/**      * Returns the local name registered for the id or      * null if the name is not known.      *       * @param id      */
specifier|public
specifier|synchronized
name|String
name|getName
parameter_list|(
name|short
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|<=
literal|0
operator|||
name|id
operator|>
name|max
condition|)
return|return
literal|""
return|;
return|return
name|names
index|[
name|id
index|]
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|getMimeTypeId
parameter_list|(
name|String
name|mimeType
parameter_list|)
block|{
name|int
name|id
init|=
name|mimeTypeByName
operator|.
name|get
argument_list|(
name|mimeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
operator|-
literal|1
condition|)
block|{
name|id
operator|=
operator|++
name|maxMime
expr_stmt|;
name|ensureCapacity
argument_list|()
expr_stmt|;
name|mimeTypeByName
operator|.
name|put
argument_list|(
name|mimeType
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|mimeTypeById
index|[
name|id
index|]
operator|=
name|mimeType
expr_stmt|;
name|write
argument_list|(
name|MIME_ID_TYPE
argument_list|,
name|id
argument_list|,
name|mimeType
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
specifier|public
specifier|synchronized
name|String
name|getMimeType
parameter_list|(
name|int
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|<
literal|0
operator|||
name|id
operator|>=
name|maxMime
condition|)
return|return
literal|""
return|;
return|return
name|mimeTypeById
index|[
name|id
index|]
return|;
block|}
comment|/**      * Append a new entry to the .dbx file      *       * @param type      * @param id      * @param key      */
specifier|protected
name|void
name|write
parameter_list|(
name|byte
name|type
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|outBuffer
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|writeEntry
argument_list|(
name|type
argument_list|,
name|id
argument_list|,
name|key
argument_list|,
name|outBuffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|outStream
operator|==
literal|null
condition|)
name|outStream
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|this
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|outStream
operator|.
name|write
argument_list|(
name|outBuffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|outStream
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Symbol table: file not found!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Symbol table: caught exception while writing!"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Write the symbol table to persistent storage. Only called when upgrading      * a .dbx file from previous versions.      *       * @param ostream      * @throws IOException      */
specifier|protected
specifier|synchronized
name|void
name|writeAll
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|writeFixedInt
argument_list|(
name|FILE_FORMAT_VERSION_ID
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|nameSymbols
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|entry
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|short
name|id
init|=
operator|(
name|short
operator|)
name|nameSymbols
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|<
literal|0
condition|)
name|LOG
operator|.
name|error
argument_list|(
literal|"symbol table: name id for "
operator|+
name|entry
operator|+
literal|"< 0"
argument_list|)
expr_stmt|;
name|writeEntry
argument_list|(
name|NAME_ID_TYPE
argument_list|,
name|id
argument_list|,
name|entry
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|nsSymbols
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|entry
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|short
name|id
init|=
operator|(
name|short
operator|)
name|nsSymbols
operator|.
name|get
argument_list|(
name|entry
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|<
literal|0
condition|)
name|LOG
operator|.
name|error
argument_list|(
literal|"symbol table: namespace id for "
operator|+
name|entry
operator|+
literal|"< 0"
argument_list|)
expr_stmt|;
name|writeEntry
argument_list|(
name|NAMESPACE_ID_TYPE
argument_list|,
name|id
argument_list|,
name|entry
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
name|String
name|mime
decl_stmt|;
name|int
name|mimeId
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|mimeTypeByName
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|mime
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|mimeId
operator|=
name|mimeTypeByName
operator|.
name|get
argument_list|(
name|mime
argument_list|)
expr_stmt|;
name|writeEntry
argument_list|(
name|MIME_ID_TYPE
argument_list|,
name|mimeId
argument_list|,
name|mime
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
name|changed
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
name|void
name|writeEntry
parameter_list|(
name|byte
name|type
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|key
parameter_list|,
name|VariableByteOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|os
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeUTF
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Read the symbol table from disk. 	 *  	 * @param is 	 * @throws IOException 	 */
specifier|protected
name|void
name|read
parameter_list|(
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|max
operator|=
literal|0
expr_stmt|;
name|nsMax
operator|=
literal|0
expr_stmt|;
name|maxMime
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|is
operator|.
name|available
argument_list|()
operator|>
literal|0
condition|)
block|{
name|byte
name|type
init|=
name|is
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|int
name|id
init|=
name|is
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|String
name|key
init|=
name|is
operator|.
name|readUTF
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|NAME_ID_TYPE
case|:
name|names
operator|=
name|ensureCapacity
argument_list|(
name|names
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'@'
condition|)
name|names
index|[
name|id
index|]
operator|=
name|key
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
else|else
name|names
index|[
name|id
index|]
operator|=
name|key
expr_stmt|;
name|nameSymbols
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|>
name|max
condition|)
name|max
operator|=
operator|(
name|short
operator|)
name|id
expr_stmt|;
break|break;
case|case
name|NAMESPACE_ID_TYPE
case|:
name|namespaces
operator|=
name|ensureCapacity
argument_list|(
name|namespaces
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|namespaces
index|[
name|id
index|]
operator|=
name|key
expr_stmt|;
name|nsSymbols
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|>
name|nsMax
condition|)
name|nsMax
operator|=
operator|(
name|short
operator|)
name|id
expr_stmt|;
break|break;
case|case
name|MIME_ID_TYPE
case|:
name|mimeTypeById
operator|=
name|ensureCapacity
argument_list|(
name|mimeTypeById
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|mimeTypeById
index|[
name|id
index|]
operator|=
name|key
expr_stmt|;
name|mimeTypeByName
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|>
name|maxMime
condition|)
name|maxMime
operator|=
operator|(
name|short
operator|)
name|id
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|/**      * Legacy method: read a symbol table written by a previous eXist version.      *       * @param istream      * @throws IOException      */
specifier|protected
name|void
name|readLegacy
parameter_list|(
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|max
operator|=
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|nsMax
operator|=
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|names
operator|=
operator|new
name|String
index|[
operator|(
name|max
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
expr_stmt|;
name|namespaces
operator|=
operator|new
name|String
index|[
operator|(
name|nsMax
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
expr_stmt|;
name|int
name|count
init|=
name|istream
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|String
name|name
decl_stmt|;
name|short
name|id
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|name
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|id
operator|=
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|nameSymbols
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'@'
condition|)
name|names
index|[
name|id
index|]
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
else|else
name|names
index|[
name|id
index|]
operator|=
name|name
expr_stmt|;
block|}
name|count
operator|=
name|istream
operator|.
name|readInt
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|name
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|id
operator|=
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|nsSymbols
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|namespaces
index|[
name|id
index|]
operator|=
name|name
expr_stmt|;
block|}
comment|// default mappings have been removed
comment|// read them for backwards compatibility
name|count
operator|=
name|istream
operator|.
name|readInt
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
block|}
name|count
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|maxMime
operator|=
operator|(
name|short
operator|)
name|count
expr_stmt|;
name|mimeTypeById
operator|=
operator|new
name|String
index|[
operator|(
name|maxMime
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
expr_stmt|;
name|String
name|mime
decl_stmt|;
name|int
name|mimeId
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|mime
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|mimeId
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|mimeTypeByName
operator|.
name|put
argument_list|(
name|mime
argument_list|,
name|mimeId
argument_list|)
expr_stmt|;
name|mimeTypeById
index|[
name|mimeId
index|]
operator|=
name|mime
expr_stmt|;
block|}
name|changed
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
comment|/** 	 * Save the entire symbol table. Will only be called when initializing an 	 * empty database or when upgrading an older dbx file. 	 *  	 * @throws EXistException 	 */
specifier|protected
name|void
name|saveSymbols
parameter_list|()
throws|throws
name|EXistException
block|{
try|try
block|{
name|VariableByteOutputStream
name|os
init|=
operator|new
name|VariableByteOutputStream
argument_list|(
literal|256
argument_list|)
decl_stmt|;
name|writeAll
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"file not found: "
operator|+
name|this
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
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
name|EXistException
argument_list|(
literal|"io error occurred while creating "
operator|+
name|this
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Read the global symbol table. The global symbol table stores QNames and 	 * namespace/prefix mappings. 	 *  	 * @throws EXistException 	 */
specifier|protected
specifier|synchronized
name|void
name|loadSymbols
parameter_list|()
throws|throws
name|EXistException
block|{
try|try
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|this
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|VariableByteInput
name|is
init|=
operator|new
name|VariableByteInputStream
argument_list|(
name|fis
argument_list|)
decl_stmt|;
name|int
name|magic
init|=
name|is
operator|.
name|readFixedInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|magic
operator|==
name|LEGACY_FILE_FORMAT_VERSION_ID
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Converting legacy symbols.dbx to new format..."
argument_list|)
expr_stmt|;
name|readLegacy
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|saveSymbols
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|magic
operator|!=
name|FILE_FORMAT_VERSION_ID
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Symbol table was created by an older or newer version of eXist"
operator|+
literal|" (file id: "
operator|+
name|magic
operator|+
literal|"). "
operator|+
literal|"To avoid damage, the database will stop."
argument_list|)
throw|;
else|else
name|read
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"could not read "
operator|+
name|this
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
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
name|EXistException
argument_list|(
literal|"io error occurred while reading "
operator|+
name|this
operator|.
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
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
name|backupSymbolsTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|this
operator|.
name|getFile
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|EXistException
block|{
block|}
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|outStream
operator|!=
literal|null
condition|)
name|outStream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|ensureCapacity
parameter_list|()
block|{
if|if
condition|(
name|max
operator|==
name|names
operator|.
name|length
condition|)
block|{
name|String
index|[]
name|newNames
init|=
operator|new
name|String
index|[
operator|(
name|max
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|names
argument_list|,
literal|0
argument_list|,
name|newNames
argument_list|,
literal|0
argument_list|,
name|max
argument_list|)
expr_stmt|;
name|names
operator|=
name|newNames
expr_stmt|;
block|}
if|if
condition|(
name|nsMax
operator|==
name|namespaces
operator|.
name|length
condition|)
block|{
name|String
index|[]
name|newNamespaces
init|=
operator|new
name|String
index|[
operator|(
name|nsMax
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|namespaces
argument_list|,
literal|0
argument_list|,
name|newNamespaces
argument_list|,
literal|0
argument_list|,
name|nsMax
argument_list|)
expr_stmt|;
name|namespaces
operator|=
name|newNamespaces
expr_stmt|;
block|}
if|if
condition|(
name|maxMime
operator|==
name|mimeTypeById
operator|.
name|length
condition|)
block|{
name|String
index|[]
name|newMime
init|=
operator|new
name|String
index|[
operator|(
name|maxMime
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|mimeTypeById
argument_list|,
literal|0
argument_list|,
name|newMime
argument_list|,
literal|0
argument_list|,
name|maxMime
argument_list|)
expr_stmt|;
name|mimeTypeById
operator|=
name|newMime
expr_stmt|;
block|}
block|}
specifier|private
name|String
index|[]
name|ensureCapacity
parameter_list|(
name|String
index|[]
name|array
parameter_list|,
name|int
name|max
parameter_list|)
block|{
if|if
condition|(
name|array
operator|.
name|length
operator|<=
name|max
condition|)
block|{
name|String
index|[]
name|newArray
init|=
operator|new
name|String
index|[
operator|(
name|max
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|array
argument_list|,
literal|0
argument_list|,
name|newArray
argument_list|,
literal|0
argument_list|,
name|array
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
return|return
name|array
return|;
block|}
block|}
end_class

end_unit

