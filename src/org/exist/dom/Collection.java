begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Collection.java - eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id:  */
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
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|EOFException
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
name|ArrayList
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|Category
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
name|security
operator|.
name|User
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
name|*
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
name|SyntaxException
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

begin_class
specifier|public
class|class
name|Collection
implements|implements
name|Comparable
block|{
specifier|protected
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|Collection
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|short
name|collectionId
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|TreeMap
name|documents
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Permission
name|permissions
init|=
operator|new
name|Permission
argument_list|(
literal|0755
argument_list|)
decl_stmt|;
specifier|private
name|ArrayList
name|subcollections
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|Collection
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|Collection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** 	 *  Adds a feature to the Collection attribute of the Collection object 	 * 	 *@param  name  The feature to be added to the Collection attribute 	 */
specifier|public
name|void
name|addCollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|subcollections
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
name|subcollections
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Adds a feature to the Document attribute of the Collection object 	 * 	 *@param  doc  The feature to be added to the Document attribute 	 */
specifier|public
name|void
name|addDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|addDocument
argument_list|(
operator|new
name|User
argument_list|(
literal|"admin"
argument_list|,
literal|null
argument_list|,
literal|"dba"
argument_list|)
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Adds a feature to the Document attribute of the Collection object 	 * 	 *@param  user  The feature to be added to the Document attribute 	 *@param  doc   The feature to be added to the Document attribute 	 */
specifier|public
name|void
name|addDocument
parameter_list|(
name|User
name|user
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|)
block|{
if|if
condition|(
name|doc
operator|.
name|getDocId
argument_list|()
operator|<
literal|0
condition|)
name|doc
operator|.
name|setDocId
argument_list|(
name|broker
operator|.
name|getNextDocId
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|put
argument_list|(
name|doc
operator|.
name|getFileName
argument_list|()
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|Iterator
name|collectionIterator
parameter_list|()
block|{
return|return
name|subcollections
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** 	 * Load all collections being descendants of this collections 	 * and return them in a List. 	 *  	 * @return List 	 */
specifier|public
name|List
name|getDescendants
parameter_list|(
name|User
name|user
parameter_list|)
block|{
specifier|final
name|ArrayList
name|cl
init|=
operator|new
name|ArrayList
argument_list|(
name|subcollections
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
name|child
decl_stmt|;
name|String
name|childName
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|subcollections
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
name|childName
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|child
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|name
operator|+
literal|'/'
operator|+
name|childName
argument_list|)
expr_stmt|;
if|if
condition|(
name|permissions
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
block|{
name|cl
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|getChildCollectionCount
argument_list|()
operator|>
literal|0
condition|)
name|cl
operator|.
name|addAll
argument_list|(
name|child
operator|.
name|getDescendants
argument_list|(
name|user
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|cl
return|;
block|}
specifier|public
name|DocumentSet
name|allDocs
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|getDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|allDocs
argument_list|(
name|user
argument_list|,
name|docs
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
specifier|private
name|DocumentSet
name|allDocs
parameter_list|(
name|User
name|user
parameter_list|,
name|DocumentSet
name|docs
parameter_list|)
block|{
name|Collection
name|child
decl_stmt|;
name|String
name|childName
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|subcollections
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
name|childName
operator|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|child
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|name
operator|+
literal|'/'
operator|+
name|childName
argument_list|)
expr_stmt|;
if|if
condition|(
name|permissions
operator|.
name|validate
argument_list|(
name|user
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
condition|)
block|{
name|child
operator|.
name|getDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|getChildCollectionCount
argument_list|()
operator|>
literal|0
condition|)
name|child
operator|.
name|allDocs
argument_list|(
name|user
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|docs
return|;
block|}
specifier|public
name|void
name|getDocuments
parameter_list|(
name|DocumentSet
name|set
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|documents
operator|.
name|values
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
name|set
operator|.
name|add
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  obj  Description of the Parameter 	 *@return      Description of the Return Value 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|Collection
name|other
init|=
operator|(
name|Collection
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|collectionId
operator|==
name|other
operator|.
name|collectionId
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|collectionId
operator|<
name|other
operator|.
name|collectionId
condition|)
return|return
operator|-
literal|1
return|;
else|else
return|return
literal|1
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|Collection
operator|)
condition|)
return|return
literal|false
return|;
return|return
operator|(
operator|(
name|Collection
operator|)
name|obj
operator|)
operator|.
name|collectionId
operator|==
name|collectionId
return|;
block|}
comment|/** 	 *  Gets the childCollectionCount attribute of the Collection object 	 * 	 *@return    The childCollectionCount value 	 */
specifier|public
name|int
name|getChildCollectionCount
parameter_list|()
block|{
return|return
name|subcollections
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 *  Gets the document attribute of the Collection object 	 * 	 *@param  name  Description of the Parameter 	 *@return       The document value 	 */
specifier|public
name|DocumentImpl
name|getDocument
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|DocumentImpl
operator|)
name|documents
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 *  Gets the documentCount attribute of the Collection object 	 * 	 *@return    The documentCount value 	 */
specifier|public
name|int
name|getDocumentCount
parameter_list|()
block|{
return|return
name|documents
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 *  Gets the id attribute of the Collection object 	 * 	 *@return    The id value 	 */
specifier|public
name|short
name|getId
parameter_list|()
block|{
return|return
name|collectionId
return|;
block|}
comment|/** 	 *  Gets the name attribute of the Collection object 	 * 	 *@return    The name value 	 */
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/** 	 *  Gets the parent attribute of the Collection object 	 * 	 *@return    The parent value 	 */
specifier|public
name|Collection
name|getParent
parameter_list|()
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"/db"
argument_list|)
condition|)
return|return
literal|null
return|;
name|String
name|parent
init|=
operator|(
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|<
literal|1
condition|?
literal|"/"
else|:
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
argument_list|)
operator|)
decl_stmt|;
return|return
name|broker
operator|.
name|getCollection
argument_list|(
name|parent
argument_list|)
return|;
block|}
comment|/** 	 *  Gets the permissions attribute of the Collection object 	 * 	 *@return    The permissions value 	 */
specifier|public
name|Permission
name|getPermissions
parameter_list|()
block|{
return|return
name|permissions
return|;
block|}
comment|/** 	 *  Gets the symbols attribute of the Collection object 	 * 	 *@return    The symbols value 	 */
specifier|public
name|SymbolTable
name|getSymbols
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  name  Description of the Parameter 	 *@return       Description of the Return Value 	 */
specifier|public
name|boolean
name|hasDocument
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|documents
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  name  Description of the Parameter 	 *@return       Description of the Return Value 	 */
specifier|public
name|boolean
name|hasSubcollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|subcollections
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@return    Description of the Return Value 	 */
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
name|documents
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  istream          Description of the Parameter 	 *@exception  IOException  Description of the Exception 	 */
specifier|public
name|void
name|read
parameter_list|(
name|DataInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|collectionId
operator|=
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|name
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
name|int
name|collLen
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
name|collLen
condition|;
name|i
operator|++
control|)
name|subcollections
operator|.
name|add
argument_list|(
name|istream
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
name|permissions
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|doc
operator|=
operator|new
name|DocumentImpl
argument_list|(
name|broker
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|doc
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
block|}
block|}
specifier|public
name|void
name|read
parameter_list|(
name|VariableByteInputStream
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|collectionId
operator|=
name|istream
operator|.
name|readShort
argument_list|()
expr_stmt|;
name|name
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
specifier|final
name|int
name|collLen
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
name|collLen
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|sub
init|=
name|istream
operator|.
name|readUTF
argument_list|()
decl_stmt|;
name|subcollections
operator|.
name|add
argument_list|(
name|sub
argument_list|)
expr_stmt|;
block|}
name|permissions
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
operator|new
name|DocumentImpl
argument_list|(
name|broker
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|doc
operator|.
name|read
argument_list|(
name|istream
argument_list|)
expr_stmt|;
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EOFException
name|e
parameter_list|)
block|{
block|}
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  name  Description of the Parameter 	 */
specifier|public
name|void
name|removeCollection
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|subcollections
operator|.
name|remove
argument_list|(
name|subcollections
operator|.
name|indexOf
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  name  Description of the Parameter 	 */
specifier|public
name|void
name|removeDocument
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|documents
operator|.
name|remove
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"could not remove document "
operator|+
name|name
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  oldName  Description of the Parameter 	 *@param  newName  Description of the Parameter 	 *@return          Description of the Return Value 	 */
specifier|public
name|boolean
name|renameDocument
parameter_list|(
name|String
name|oldName
parameter_list|,
name|String
name|newName
parameter_list|)
block|{
name|DocumentImpl
name|doc
init|=
name|getDocument
argument_list|(
name|oldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"document "
operator|+
name|oldName
operator|+
literal|" not found"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|doc
operator|.
name|setFileName
argument_list|(
name|newName
argument_list|)
expr_stmt|;
name|documents
operator|.
name|remove
argument_list|(
name|oldName
argument_list|)
expr_stmt|;
name|documents
operator|.
name|put
argument_list|(
name|newName
argument_list|,
name|doc
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** 	 *  Sets the id attribute of the Collection object 	 * 	 *@param  id  The new id value 	 */
specifier|public
name|void
name|setId
parameter_list|(
name|short
name|id
parameter_list|)
block|{
name|this
operator|.
name|collectionId
operator|=
name|id
expr_stmt|;
block|}
comment|/** 	 *  Sets the name attribute of the Collection object 	 * 	 *@param  name  The new name value 	 */
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|/** 	 *  Sets the permissions attribute of the Collection object 	 * 	 *@param  mode  The new permissions value 	 */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|permissions
operator|.
name|setPermissions
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Sets the permissions attribute of the Collection object 	 * 	 *@param  mode                 The new permissions value 	 *@exception  SyntaxException  Description of the Exception 	 */
specifier|public
name|void
name|setPermissions
parameter_list|(
name|String
name|mode
parameter_list|)
throws|throws
name|SyntaxException
block|{
name|permissions
operator|.
name|setPermissions
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
comment|/** 	 *  Description of the Method 	 * 	 *@param  ostream          Description of the Parameter 	 *@exception  IOException  Description of the Exception 	 */
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|ostream
parameter_list|)
throws|throws
name|IOException
block|{
name|ostream
operator|.
name|writeShort
argument_list|(
name|collectionId
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|subcollections
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
name|collectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|ostream
operator|.
name|writeUTF
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
comment|//        symbols.write(ostream);
name|permissions
operator|.
name|write
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|documents
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
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
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|doc
operator|.
name|write
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|write
parameter_list|(
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
name|collectionId
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeInt
argument_list|(
name|subcollections
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
name|collectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|ostream
operator|.
name|writeUTF
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|permissions
operator|.
name|write
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
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
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|doc
operator|.
name|write
argument_list|(
name|ostream
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

