begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|RawDataBackup
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
name|Object2IntHashMap
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
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Maintains a global symbol table shared by a database instance. The symbol  * table maps namespace URIs and node names to unique, numeric ids. Internally,  * the db does not store node QNames in clear text. Instead, it uses the numeric ids  * maintained here.  *   * The global SymbolTable singleton can be retrieved from {@link org.exist.storage.BrokerPool#getSymbols()}.  * It is saved into the database file "symbols.dbx".  *   * @author wolf  * @author Adam Retter<adam@exist-db.org>  *  */
end_comment

begin_class
specifier|public
class|class
name|SymbolTable
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
name|SymbolTable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
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
comment|//TODO the bytes used by these types could be replaced by single bits in an EnumSet
comment|//if we can get bit level storage operations working
specifier|public
enum|enum
name|SymbolType
block|{
name|NAME
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
name|NAMESPACE
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
name|MIMETYPE
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|;
specifier|private
specifier|final
name|byte
name|type_id
decl_stmt|;
specifier|private
name|SymbolType
parameter_list|(
name|byte
name|type_id
parameter_list|)
block|{
name|this
operator|.
name|type_id
operator|=
name|type_id
expr_stmt|;
block|}
specifier|public
specifier|final
name|byte
name|getTypeId
parameter_list|()
block|{
return|return
name|type_id
return|;
block|}
specifier|public
specifier|static
name|SymbolType
name|valueOf
parameter_list|(
name|byte
name|type_id
parameter_list|)
block|{
for|for
control|(
name|SymbolType
name|symbolType
range|:
name|SymbolType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|symbolType
operator|.
name|getTypeId
argument_list|()
operator|==
name|type_id
condition|)
block|{
return|return
name|symbolType
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No such enumerated value for type_id:"
operator|+
name|type_id
argument_list|)
throw|;
block|}
block|}
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
specifier|public
specifier|final
specifier|static
name|char
name|ATTR_NAME_PREFIX
init|=
literal|'@'
decl_stmt|;
specifier|protected
specifier|final
name|SymbolCollection
name|localNameSymbols
init|=
operator|new
name|LocalNameSymbolCollection
argument_list|(
name|SymbolType
operator|.
name|NAME
argument_list|,
literal|200
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|SymbolCollection
name|namespaceSymbols
init|=
operator|new
name|SymbolCollection
argument_list|(
name|SymbolType
operator|.
name|NAMESPACE
argument_list|,
literal|200
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|SymbolCollection
name|mimeTypeSymbols
init|=
operator|new
name|SymbolCollection
argument_list|(
name|SymbolType
operator|.
name|MIMETYPE
argument_list|,
literal|32
argument_list|)
decl_stmt|;
comment|/**      * Temporary name pool to share QName instances during indexing.      */
specifier|private
name|QNamePool
name|namePool
init|=
operator|new
name|QNamePool
argument_list|()
decl_stmt|;
comment|/** set to true if the symbol table needs to be saved */
specifier|private
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
comment|/** the underlying symbols.dbx file */
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|VariableByteOutputStream
name|outBuffer
init|=
operator|new
name|VariableByteOutputStream
argument_list|(
literal|512
argument_list|)
decl_stmt|;
specifier|private
name|OutputStream
name|os
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
block|{
name|loadSymbols
argument_list|()
expr_stmt|;
block|}
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
comment|//TODO the (short) cast is nasty - should consider using either short or int end to end
specifier|public
specifier|synchronized
name|short
name|getSymbol
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
name|localNameSymbols
operator|.
name|getId
argument_list|(
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Return a unique id for the local node name of the specified attribute.      *       * @param attr      */
comment|//TODO the (short) cast is nasty - should consider using either short or int end to end
specifier|public
specifier|synchronized
name|short
name|getSymbol
parameter_list|(
name|Attr
name|attr
parameter_list|)
block|{
specifier|final
name|String
name|key
init|=
name|ATTR_NAME_PREFIX
operator|+
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
return|return
operator|(
name|short
operator|)
name|localNameSymbols
operator|.
name|getId
argument_list|(
name|key
argument_list|)
return|;
block|}
comment|/**      * Returns a unique id for the specified local name. If the name is      * the local name of an attribute, it should start with a '@' character.      *       * @param name      */
comment|//TODO the (short) cast is nasty - should consider using either short or int end to end
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
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"name is empty"
argument_list|)
throw|;
block|}
return|return
operator|(
name|short
operator|)
name|localNameSymbols
operator|.
name|getId
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Returns a unique id for the specified namespace URI.      *       * @param ns      */
comment|//TODO the (short) cast is nasty - should consider using either short or int end to end
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
return|return
operator|(
name|short
operator|)
name|namespaceSymbols
operator|.
name|getId
argument_list|(
name|ns
argument_list|)
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
return|return
name|mimeTypeSymbols
operator|.
name|getId
argument_list|(
name|mimeType
argument_list|)
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
return|return
name|localNameSymbols
operator|.
name|getSymbol
argument_list|(
name|id
argument_list|)
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
return|return
name|mimeTypeSymbols
operator|.
name|getSymbol
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Returns the namespace URI registered for the id or null      * if the namespace URI is not known. Returns the empty string      * if the namespace is empty.      *      * @param id      */
specifier|public
specifier|synchronized
name|String
name|getNamespace
parameter_list|(
name|short
name|id
parameter_list|)
block|{
return|return
name|namespaceSymbols
operator|.
name|getSymbol
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Write the symbol table to persistent storage. Only called when upgrading      * a .dbx file from previous versions.      *       * @param os outputstream      * @throws IOException      */
specifier|private
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
name|localNameSymbols
operator|.
name|write
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|namespaceSymbols
operator|.
name|write
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|mimeTypeSymbols
operator|.
name|write
argument_list|(
name|os
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Read the symbol table from disk.      *      * @param is      * @throws IOException      */
specifier|protected
specifier|final
name|void
name|read
parameter_list|(
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|localNameSymbols
operator|.
name|clear
argument_list|()
expr_stmt|;
name|namespaceSymbols
operator|.
name|clear
argument_list|()
expr_stmt|;
name|mimeTypeSymbols
operator|.
name|clear
argument_list|()
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
name|readEntry
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|readEntry
parameter_list|(
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
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
comment|//symbol types can be written in any order by SymbolCollection.getById()->SymbolCollection.write()
switch|switch
condition|(
name|SymbolType
operator|.
name|valueOf
argument_list|(
name|type
argument_list|)
condition|)
block|{
case|case
name|NAME
case|:
name|localNameSymbols
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|key
argument_list|)
expr_stmt|;
break|break;
case|case
name|NAMESPACE
case|:
name|namespaceSymbols
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|key
argument_list|)
expr_stmt|;
break|break;
case|case
name|MIMETYPE
case|:
name|mimeTypeSymbols
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|key
argument_list|)
expr_stmt|;
break|break;
comment|//Removed default clause
block|}
block|}
comment|/**      * Legacy method: read a symbol table written by a previous eXist version.      *       * @param istream      * @throws IOException      */
specifier|protected
specifier|final
name|void
name|readLegacy
parameter_list|(
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
comment|//read max, not needed anymore
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
comment|//read nsMax not needed anymore
name|String
name|key
decl_stmt|;
name|short
name|id
decl_stmt|;
comment|//read local names
name|int
name|count
init|=
name|istream
operator|.
name|readInt
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|key
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
name|localNameSymbols
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|key
argument_list|)
expr_stmt|;
block|}
comment|//read namespaces
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
name|key
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
name|namespaceSymbols
operator|.
name|add
argument_list|(
name|id
argument_list|,
name|key
argument_list|)
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
comment|//read namespaces
name|count
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
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
name|key
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
name|mimeTypeSymbols
operator|.
name|add
argument_list|(
name|mimeId
argument_list|,
name|key
argument_list|)
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
comment|/**      * Save the entire symbol table. Will only be called when initializing an      * empty database or when upgrading an older dbx file.      *      * @throws EXistException      */
specifier|private
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
literal|"File not found: "
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
literal|"IO error occurred while creating "
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
comment|/**      * Read the global symbol table. The global symbol table stores QNames and      * namespace/prefix mappings.      *      * @throws EXistException      */
specifier|private
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
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Symbol table was created by an older"
operator|+
literal|"or newer version of eXist"
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
block|}
else|else
block|{
name|read
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
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
literal|"Could not read "
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
literal|"IO error occurred while reading "
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
name|backupToArchive
parameter_list|(
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
block|{
name|OutputStream
name|os
init|=
name|backup
operator|.
name|newEntry
argument_list|(
name|getFile
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|backupSymbolsTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
throws|throws
name|EXistException
block|{
comment|//Noting to do ? -pb
block|}
specifier|private
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|FileNotFoundException
block|{
if|if
condition|(
name|os
operator|==
literal|null
condition|)
block|{
name|os
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|getFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
name|os
return|;
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
name|os
operator|!=
literal|null
condition|)
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Represents a distinct collection of symbols      *      * @author wolf      * @author Adam Retter<adam@exist-db.org>      */
specifier|protected
class|class
name|SymbolCollection
block|{
specifier|private
specifier|final
name|SymbolType
name|symbolType
decl_stmt|;
comment|/** Maps mimetype names to an integer id (persisted to disk) */
specifier|private
specifier|final
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
name|symbolsByName
decl_stmt|;
comment|/** Maps int ids to mimetype names (transient map for fast reverse lookup of symbolsByName) */
specifier|private
name|String
index|[]
name|symbolsById
decl_stmt|;
comment|/** contains the offset of the last symbol */
specifier|protected
name|short
name|offset
init|=
literal|0
decl_stmt|;
specifier|public
name|SymbolCollection
parameter_list|(
name|SymbolType
name|symbolType
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|this
operator|.
name|symbolType
operator|=
name|symbolType
expr_stmt|;
name|symbolsByName
operator|=
operator|new
name|Object2IntHashMap
argument_list|<
name|String
argument_list|>
argument_list|(
name|initialSize
argument_list|)
expr_stmt|;
name|symbolsById
operator|=
operator|new
name|String
index|[
name|initialSize
index|]
expr_stmt|;
block|}
specifier|private
name|SymbolType
name|getSymbolType
parameter_list|()
block|{
return|return
name|symbolType
return|;
block|}
specifier|private
name|int
name|add
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|symbolsById
operator|=
name|ensureCapacity
argument_list|(
name|symbolsById
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|addSymbolById
argument_list|(
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|addSymbolByName
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|>
name|offset
condition|)
block|{
name|offset
operator|=
operator|(
name|short
operator|)
name|id
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
specifier|protected
name|void
name|addSymbolById
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|symbolsById
index|[
name|id
index|]
operator|=
name|name
expr_stmt|;
block|}
specifier|protected
name|void
name|addSymbolByName
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|)
block|{
name|symbolsByName
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
comment|/* Apparently unused. Commented out -pb         private void ensureCapacity() {             if(offset == symbolsById.length) {                 String[] newSymbolsById = new String[(offset * 3) / 2];                 System.arraycopy(symbolsById, 0, newSymbolsById, 0, offset);                 symbolsById = newSymbolsById;             }         }         */
specifier|protected
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
specifier|private
name|void
name|clear
parameter_list|()
block|{
name|offset
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|String
name|getSymbol
parameter_list|(
name|int
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
name|offset
condition|)
block|{
return|return
literal|""
return|;
comment|//TODO : raise an exception ? -pb
block|}
return|return
name|symbolsById
index|[
name|id
index|]
return|;
block|}
specifier|public
specifier|synchronized
name|int
name|getId
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|int
name|id
init|=
name|symbolsByName
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
block|{
return|return
name|id
return|;
block|}
name|id
operator|=
name|add
argument_list|(
operator|++
name|offset
argument_list|,
name|name
argument_list|)
expr_stmt|;
comment|//we use "++offset" here instead of "offset++",
comment|//because the system expects id's to start at 1, not 0
name|write
argument_list|(
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
specifier|protected
specifier|final
name|void
name|write
parameter_list|(
name|VariableByteOutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|symbol
decl_stmt|;
name|int
name|id
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|i
init|=
name|symbolsByName
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
name|symbol
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|id
operator|=
name|symbolsByName
operator|.
name|get
argument_list|(
name|symbol
argument_list|)
expr_stmt|;
if|if
condition|(
name|id
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Symbol Table: symbolTypeId="
operator|+
name|getSymbolType
argument_list|()
operator|+
literal|", symbol='"
operator|+
name|symbol
operator|+
literal|"', id="
operator|+
name|id
argument_list|)
expr_stmt|;
comment|//TODO : raise exception ? -pb
block|}
name|writeEntry
argument_list|(
name|id
argument_list|,
name|symbol
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Append a new entry to the .dbx file          *          * @param id          * @param key          */
specifier|private
name|void
name|write
parameter_list|(
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
name|id
argument_list|,
name|key
argument_list|,
name|outBuffer
argument_list|)
expr_stmt|;
name|getOutputStream
argument_list|()
operator|.
name|write
argument_list|(
name|outBuffer
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|getOutputStream
argument_list|()
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
comment|//TODO :throw exception -pb
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
comment|//TODO : throw exception -pb
block|}
block|}
specifier|private
name|void
name|writeEntry
parameter_list|(
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
name|getSymbolType
argument_list|()
operator|.
name|getTypeId
argument_list|()
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
block|}
comment|/**      * Local name storage is used by both element names and attribute names      *      * Attributes behave slightly differently to element names      * For the persistent map symbolsByName, the attribute name is prefixed with      * an '@' symbol to differentiate the attribute name from a similar element name      * However, for the in-memory reverse map symbolsById, the attribute name      * should not be prefixed.      *      * @author Adam Retter<adam@exist-db.org>      */
specifier|private
class|class
name|LocalNameSymbolCollection
extends|extends
name|SymbolCollection
block|{
specifier|public
name|LocalNameSymbolCollection
parameter_list|(
name|SymbolType
name|symbolType
parameter_list|,
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|symbolType
argument_list|,
name|initialSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addSymbolById
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
comment|/*              For attributes, Don't store '@' in in-memory mapping of id -> attrName              enables faster retrieval              */
if|if
condition|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|ATTR_NAME_PREFIX
condition|)
block|{
name|super
operator|.
name|addSymbolById
argument_list|(
name|id
argument_list|,
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|addSymbolById
argument_list|(
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

