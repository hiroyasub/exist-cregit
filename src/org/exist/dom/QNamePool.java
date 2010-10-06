begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|util
operator|.
name|Iterator
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
name|AbstractHashtable
import|;
end_import

begin_comment
comment|/**  * A pool for QNames. This is a temporary pool for QName objects to avoid  * allocating the same QName multiple times. If the pool is full, it will just be  * cleared.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|QNamePool
extends|extends
name|AbstractHashtable
block|{
specifier|private
name|QName
index|[]
name|values
decl_stmt|;
specifier|private
name|QName
name|temp
init|=
operator|new
name|QName
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|public
name|QNamePool
parameter_list|()
block|{
name|super
argument_list|(
literal|512
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|QName
index|[
name|tabSize
index|]
expr_stmt|;
block|}
specifier|public
name|QNamePool
parameter_list|(
name|int
name|iSize
parameter_list|)
block|{
name|super
argument_list|(
name|iSize
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|QName
index|[
name|tabSize
index|]
expr_stmt|;
block|}
comment|/**      * Return a QName object for the given local name, namespace and      * prefix. Return null if the QName has not yet been added to the pool.      *      * @param type      * @param namespaceURI      * @param localName      * @param prefix      * @return QName object      */
specifier|public
name|QName
name|get
parameter_list|(
name|byte
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
name|temp
operator|.
name|setLocalName
argument_list|(
name|localName
argument_list|)
expr_stmt|;
name|temp
operator|.
name|setNamespaceURI
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
name|temp
operator|.
name|setPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|temp
operator|.
name|setNameType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|int
name|idx
init|=
name|temp
operator|.
name|hashCode
argument_list|()
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
return|return
literal|null
return|;
comment|// key does not exist
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|temp
argument_list|)
condition|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
name|int
name|rehashVal
init|=
name|rehash
argument_list|(
name|idx
argument_list|)
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
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|idx
operator|=
operator|(
name|idx
operator|+
name|rehashVal
operator|)
operator|%
name|tabSize
expr_stmt|;
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// key not found
block|}
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|temp
argument_list|)
condition|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Add a QName, consisting of namespace, local name and prefix, to the      * pool.      */
specifier|public
name|QName
name|add
parameter_list|(
name|byte
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
name|temp
operator|.
name|setLocalName
argument_list|(
name|localName
argument_list|)
expr_stmt|;
name|temp
operator|.
name|setNamespaceURI
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
name|temp
operator|.
name|setPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|temp
operator|.
name|setNameType
argument_list|(
name|type
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|insert
argument_list|(
name|temp
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|HashtableOverflowException
name|e
parameter_list|)
block|{
comment|// just clear the pool and try again
name|values
operator|=
operator|new
name|QName
index|[
name|tabSize
index|]
expr_stmt|;
name|items
operator|=
literal|0
expr_stmt|;
try|try
block|{
return|return
name|insert
argument_list|(
name|temp
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|HashtableOverflowException
name|e1
parameter_list|)
block|{
comment|//Doh ! Report something here !
block|}
comment|// should never happen, but just to be sure
return|return
operator|new
name|QName
argument_list|(
name|temp
argument_list|)
return|;
block|}
block|}
specifier|protected
name|QName
name|insert
parameter_list|(
name|QName
name|value
parameter_list|)
throws|throws
name|HashtableOverflowException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal value: null"
argument_list|)
throw|;
name|int
name|idx
init|=
name|value
operator|.
name|hashCode
argument_list|()
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
name|int
name|bucket
init|=
operator|-
literal|1
decl_stmt|;
comment|// look for an empty bucket
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|idx
index|]
operator|=
operator|new
name|QName
argument_list|(
name|value
argument_list|)
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
comment|// remember the bucket, but continue to check
comment|// for duplicate keys
name|bucket
operator|=
name|idx
expr_stmt|;
block|}
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// duplicate value
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
comment|//System.out.println("Hash collision: " + value + " with " + values[idx]);
name|int
name|rehashVal
init|=
name|rehash
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|int
name|rehashCnt
init|=
literal|1
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
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|idx
operator|=
operator|(
name|idx
operator|+
name|rehashVal
operator|)
operator|%
name|tabSize
expr_stmt|;
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
name|bucket
operator|=
name|idx
expr_stmt|;
block|}
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|bucket
operator|>
operator|-
literal|1
condition|)
block|{
comment|// store key into the empty bucket first found
name|idx
operator|=
name|bucket
expr_stmt|;
block|}
name|values
index|[
name|idx
index|]
operator|=
operator|new
name|QName
argument_list|(
name|value
argument_list|)
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// duplicate value
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
operator|++
name|rehashCnt
expr_stmt|;
block|}
comment|// should never happen, but just to be sure:
comment|// if the key has not been inserted yet, do it now
if|if
condition|(
name|bucket
operator|>
operator|-
literal|1
condition|)
block|{
name|values
index|[
name|bucket
index|]
operator|=
operator|new
name|QName
argument_list|(
name|value
argument_list|)
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
name|values
index|[
name|bucket
index|]
return|;
block|}
throw|throw
operator|new
name|HashtableOverflowException
argument_list|()
throw|;
block|}
specifier|protected
name|int
name|rehash
parameter_list|(
name|int
name|iVal
parameter_list|)
block|{
name|int
name|retVal
init|=
operator|(
name|iVal
operator|+
name|iVal
operator|/
literal|2
operator|)
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|retVal
operator|==
literal|0
condition|)
name|retVal
operator|=
literal|1
expr_stmt|;
return|return
name|retVal
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QName
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|QName
argument_list|>
name|valueIterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

