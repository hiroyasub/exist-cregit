begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id:  */
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
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|Int2ObjectOpenHashMap
import|;
end_import

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|Object2IntMap
import|;
end_import

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|Object2IntOpenHashMap
import|;
end_import

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|Object2ShortOpenHashMap
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
name|util
operator|.
name|Iterator
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
name|util
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
name|util
operator|.
name|VariableByteOutputStream
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

begin_class
specifier|public
class|class
name|SymbolTable
block|{
specifier|private
specifier|final
specifier|static
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
specifier|protected
name|Object2IntOpenHashMap
name|nameSymbols
init|=
operator|new
name|Object2IntOpenHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Int2ObjectOpenHashMap
name|names
init|=
operator|new
name|Int2ObjectOpenHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Object2IntOpenHashMap
name|nsSymbols
init|=
operator|new
name|Object2IntOpenHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Int2ObjectOpenHashMap
name|namespaces
init|=
operator|new
name|Int2ObjectOpenHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Object2ShortOpenHashMap
name|defaultMappings
init|=
operator|new
name|Object2ShortOpenHashMap
argument_list|()
decl_stmt|;
specifier|protected
name|short
name|max
init|=
literal|0
decl_stmt|;
specifier|protected
name|short
name|nsMax
init|=
literal|0
decl_stmt|;
specifier|protected
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
specifier|public
name|SymbolTable
parameter_list|()
block|{
block|}
specifier|public
specifier|synchronized
name|short
name|getSymbol
parameter_list|(
name|Element
name|element
parameter_list|)
block|{
if|if
condition|(
name|nameSymbols
operator|.
name|containsKey
argument_list|(
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
return|return
operator|(
name|short
operator|)
name|nameSymbols
operator|.
name|getInt
argument_list|(
name|element
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
name|short
name|id
init|=
operator|++
name|max
decl_stmt|;
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
if|if
condition|(
name|nameSymbols
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
return|return
operator|(
name|short
operator|)
name|nameSymbols
operator|.
name|getInt
argument_list|(
name|key
argument_list|)
return|;
name|short
name|id
init|=
operator|++
name|max
decl_stmt|;
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
if|if
condition|(
name|nameSymbols
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
return|return
operator|(
name|short
operator|)
name|nameSymbols
operator|.
name|getInt
argument_list|(
name|name
argument_list|)
return|;
name|short
name|id
init|=
operator|++
name|max
decl_stmt|;
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
if|if
condition|(
name|nsSymbols
operator|.
name|containsKey
argument_list|(
name|ns
argument_list|)
condition|)
return|return
operator|(
name|short
operator|)
name|nsSymbols
operator|.
name|getInt
argument_list|(
name|ns
argument_list|)
return|;
name|short
name|id
init|=
operator|++
name|nsMax
decl_stmt|;
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
specifier|public
name|String
index|[]
name|getSymbols
parameter_list|()
block|{
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|nameSymbols
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|Object
index|[]
name|keys
init|=
name|nameSymbols
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
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
name|keys
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|result
index|[
name|i
index|]
operator|=
operator|(
name|String
operator|)
name|keys
index|[
name|i
index|]
expr_stmt|;
return|return
name|result
return|;
block|}
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
name|defaultMappings
operator|.
name|getShort
argument_list|(
name|prefix
argument_list|)
argument_list|)
return|;
return|return
literal|null
return|;
block|}
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
name|keySet
argument_list|()
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
name|entrySet
argument_list|()
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
name|Object2IntMap
operator|.
name|Entry
name|entry
init|=
operator|(
name|Object2IntMap
operator|.
name|Entry
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
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|entry
operator|.
name|getIntValue
argument_list|()
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
name|entrySet
argument_list|()
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
name|Object2IntMap
operator|.
name|Entry
name|entry
init|=
operator|(
name|Object2IntMap
operator|.
name|Entry
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
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeShort
argument_list|(
operator|(
name|short
operator|)
name|entry
operator|.
name|getIntValue
argument_list|()
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
name|keySet
argument_list|()
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
name|defaultMappings
operator|.
name|getShort
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
name|changed
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|read
parameter_list|(
name|VariableByteInputStream
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
name|changed
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

