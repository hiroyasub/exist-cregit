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
literal|7
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
name|nameSymbols
init|=
operator|new
name|Object2IntHashMap
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|/** Maps int ids to local node names */
specifier|protected
name|Int2ObjectHashMap
name|names
init|=
operator|new
name|Int2ObjectHashMap
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|/** Maps namespace URIs to an integer id */
specifier|protected
name|Object2IntHashMap
name|nsSymbols
init|=
operator|new
name|Object2IntHashMap
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|/** Maps int ids to namespace URIs */
specifier|protected
name|Int2ObjectHashMap
name|namespaces
init|=
operator|new
name|Int2ObjectHashMap
argument_list|(
literal|200
argument_list|)
decl_stmt|;
comment|/**      * Contains default prefix-to-namespace mappings. For convenience, eXist tracks      * the first prefix-to-namespace mapping it finds in a document. If an undefined prefix      * is found in a query, the query engine will first look up the prefix in this table before      * throwing an error.      */
specifier|protected
name|Object2IntHashMap
name|defaultMappings
init|=
operator|new
name|Object2IntHashMap
argument_list|(
literal|200
argument_list|)
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
name|mimeTypeByName
init|=
operator|new
name|Object2IntHashMap
argument_list|(
literal|32
argument_list|)
decl_stmt|;
specifier|protected
name|Int2ObjectHashMap
name|mimeTypeById
init|=
operator|new
name|Int2ObjectHashMap
argument_list|(
literal|32
argument_list|)
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
name|String
name|dataDir
init|=
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
decl_stmt|;
name|file
operator|=
operator|new
name|File
argument_list|(
name|dataDir
operator|+
name|File
operator|.
name|separatorChar
operator|+
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
name|names
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
comment|// remember the prefix=namespace mapping for querying
name|String
name|prefix
init|=
name|element
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|(
operator|!
name|defaultMappings
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
operator|)
condition|)
block|{
specifier|final
name|short
name|nsId
init|=
name|getNSSymbol
argument_list|(
name|element
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|defaultMappings
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|String
name|key
init|=
literal|'@'
operator|+
name|attr
operator|.
name|getLocalName
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
name|names
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|attr
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
name|changed
operator|=
literal|true
expr_stmt|;
comment|//		remember the prefix=namespace mapping for querying
name|String
name|prefix
init|=
name|attr
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
operator|(
operator|!
name|defaultMappings
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
operator|)
condition|)
block|{
specifier|final
name|short
name|nsId
init|=
name|getNSSymbol
argument_list|(
name|attr
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|defaultMappings
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
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
comment|// DW: can cause NPE
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
name|names
operator|.
name|put
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
name|namespaces
operator|.
name|put
argument_list|(
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
return|return
name|id
operator|==
literal|0
condition|?
literal|""
else|:
operator|(
name|String
operator|)
name|namespaces
operator|.
name|get
argument_list|(
name|id
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
operator|(
name|String
operator|)
name|names
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Returns a namespace URI for the given prefix if there's      * a default mapping.      *       * @param prefix      */
specifier|public
specifier|synchronized
name|String
name|getDefaultNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|defaultMappings
operator|.
name|containsKey
argument_list|(
name|prefix
argument_list|)
condition|)
return|return
name|getNamespace
argument_list|(
operator|(
name|short
operator|)
name|defaultMappings
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
argument_list|)
return|;
return|return
literal|null
return|;
block|}
comment|/**      * Returns a list of default prefixes registered.      *       */
specifier|public
specifier|synchronized
name|String
index|[]
name|defaultPrefixList
parameter_list|()
block|{
name|String
index|[]
name|prefixes
init|=
operator|new
name|String
index|[
name|defaultMappings
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|defaultMappings
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
name|i
operator|++
control|)
name|prefixes
index|[
name|i
index|]
operator|=
operator|(
name|String
operator|)
name|j
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|prefixes
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
name|int
name|maxId
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|mimeTypeById
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
name|Integer
name|val
init|=
operator|(
name|Integer
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|maxId
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxId
argument_list|,
name|val
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|id
operator|=
operator|++
name|maxId
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
operator|.
name|put
argument_list|(
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
return|return
operator|(
name|String
operator|)
name|mimeTypeById
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Write the symbol table to persistent storage.      *       * @param ostream      * @throws IOException      */
specifier|public
specifier|synchronized
name|void
name|write
parameter_list|(
specifier|final
name|VariableByteOutputStream
name|ostream
parameter_list|)
throws|throws
name|IOException
block|{
name|ostream
operator|.
name|writeFixedInt
argument_list|(
name|FILE_FORMAT_VERSION_ID
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeShort
argument_list|(
name|max
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeShort
argument_list|(
name|nsMax
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|nameSymbols
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
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
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|entry
argument_list|)
expr_stmt|;
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
name|Thread
operator|.
name|dumpStack
argument_list|()
expr_stmt|;
name|ostream
operator|.
name|writeShort
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|ostream
operator|.
name|writeInt
argument_list|(
name|nsSymbols
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
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
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|entry
argument_list|)
expr_stmt|;
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
name|Thread
operator|.
name|dumpStack
argument_list|()
expr_stmt|;
name|ostream
operator|.
name|writeShort
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|ostream
operator|.
name|writeInt
argument_list|(
name|defaultMappings
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|prefix
decl_stmt|;
name|short
name|nsId
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|defaultMappings
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
name|prefix
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|nsId
operator|=
operator|(
name|short
operator|)
name|defaultMappings
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeShort
argument_list|(
name|nsId
argument_list|)
expr_stmt|;
block|}
name|ostream
operator|.
name|writeInt
argument_list|(
name|mimeTypeByName
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|mime
decl_stmt|;
name|int
name|mimeId
decl_stmt|;
for|for
control|(
name|Iterator
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
operator|(
name|String
operator|)
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
name|ostream
operator|.
name|writeUTF
argument_list|(
name|mime
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|mimeId
argument_list|)
expr_stmt|;
block|}
name|changed
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Read the symbol table.      *       * @param istream      * @throws IOException      */
specifier|public
specifier|synchronized
name|void
name|read
parameter_list|(
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|magic
init|=
name|istream
operator|.
name|readFixedInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|magic
operator|!=
name|FILE_FORMAT_VERSION_ID
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Database file symbols.dbx has a storage format incompatible with this "
operator|+
literal|"version of eXist. Please do a backup/restore of your data first."
argument_list|)
throw|;
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
operator|.
name|put
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
else|else
name|names
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|name
argument_list|)
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
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
name|count
operator|=
name|istream
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|String
name|prefix
decl_stmt|;
name|short
name|nsId
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
name|prefix
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|nsId
operator|=
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|defaultMappings
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
block|}
name|count
operator|=
name|istream
operator|.
name|readInt
argument_list|()
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
operator|.
name|put
argument_list|(
name|mimeId
argument_list|,
name|mime
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
comment|/** 	 * Save the global symbol table. The global symbol table stores QNames and 	 * namespace/prefix mappings. 	 *  	 * @throws EXistException 	 */
specifier|public
name|void
name|saveSymbols
parameter_list|()
throws|throws
name|EXistException
block|{
synchronized|synchronized
init|(
name|this
init|)
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
name|this
operator|.
name|write
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
name|this
operator|.
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
block|}
comment|/** 	 * Read the global symbol table. The global symbol table stores QNames and 	 * namespace/prefix mappings. 	 *  	 * @throws EXistException 	 */
specifier|public
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
name|this
operator|.
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
if|if
condition|(
name|hasChanged
argument_list|()
condition|)
name|saveSymbols
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

