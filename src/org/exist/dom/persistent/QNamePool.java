begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
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
name|util
operator|.
name|hashtable
operator|.
name|AbstractHashSet
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * A pool for QNames. This is a temporary pool for QName objects to avoid  * allocating the same QName multiple times. If the pool is full, it will just be  * cleared.  *  * @author wolf  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|QNamePool
extends|extends
name|AbstractHashSet
argument_list|<
name|QName
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_POOL_SIZE
init|=
literal|512
decl_stmt|;
specifier|private
name|QName
index|[]
name|values
decl_stmt|;
specifier|public
name|QNamePool
parameter_list|()
block|{
name|super
argument_list|(
name|DEFAULT_POOL_SIZE
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
comment|/**      * @param size The size of the QName pool      */
specifier|public
name|QNamePool
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
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
specifier|final
name|QName
name|get
parameter_list|(
specifier|final
name|byte
name|type
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|)
block|{
name|int
name|idx
init|=
name|hashCode
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|,
name|type
argument_list|)
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
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
comment|// key does not exist
block|}
if|else if
condition|(
name|equals
argument_list|(
name|values
index|[
name|idx
index|]
argument_list|,
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|,
name|type
argument_list|)
condition|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
comment|//no hash-collision
block|}
else|else
block|{
comment|//hash-collision rehash
specifier|final
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
name|equals
argument_list|(
name|values
index|[
name|idx
index|]
argument_list|,
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|,
name|type
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
block|}
comment|/**      * Add a QName, consisting of namespace, local name and prefix, to the      * pool.      */
specifier|public
specifier|final
name|QName
name|add
parameter_list|(
specifier|final
name|byte
name|type
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|)
block|{
specifier|final
name|QName
name|qn
init|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|,
name|type
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|insert
argument_list|(
name|qn
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HashSetOverflowException
name|e
parameter_list|)
block|{
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|insert
argument_list|(
name|qn
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HashSetOverflowException
name|e1
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|clear
parameter_list|()
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
block|}
specifier|private
name|QName
name|insert
parameter_list|(
specifier|final
name|QName
name|value
parameter_list|)
throws|throws
name|HashSetOverflowException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal value: null"
argument_list|)
throw|;
block|}
name|int
name|idx
init|=
name|hashCode
argument_list|(
name|value
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|value
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|value
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|value
operator|.
name|getNameType
argument_list|()
argument_list|)
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
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
name|value
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
specifier|final
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
name|value
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
name|value
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
else|else
block|{
throw|throw
operator|new
name|HashSetOverflowException
argument_list|()
throw|;
block|}
block|}
specifier|private
name|int
name|rehash
parameter_list|(
specifier|final
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
block|{
name|retVal
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|retVal
return|;
block|}
comment|/**      * Used to calculate a hashCode for a QName      *<p/>      * This varies from {@see org.exist.dom.QName#hashCode()} in so far      * as it also includes the prefix in the hash calculation      *      * @param localPart      * @param namespaceURI      * @param prefix      * @param nameType      */
specifier|private
specifier|static
name|int
name|hashCode
parameter_list|(
specifier|final
name|String
name|localPart
parameter_list|,
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|byte
name|nameType
parameter_list|)
block|{
name|int
name|h
init|=
name|nameType
operator|+
literal|31
operator|+
name|localPart
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|+=
literal|31
operator|*
name|h
operator|+
name|namespaceURI
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|h
operator|+=
literal|31
operator|*
name|h
operator|+
operator|(
name|prefix
operator|==
literal|null
condition|?
literal|1
else|:
name|prefix
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|h
return|;
block|}
comment|/**      * Used to calculate equality for a QName and it's constituent components      *<p/>      * This varies from {@see org.exist.dom.QName#equals(Object)} in so far      * as it also includes the prefix in the equality test      *      * @param qname             The QName to check equality against the other*      * @param otherLocalPart      * @param otherNamespaceURI      * @param otherPrefix      * @param otherNameType      */
specifier|private
specifier|static
name|boolean
name|equals
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|String
name|otherLocalPart
parameter_list|,
specifier|final
name|String
name|otherNamespaceURI
parameter_list|,
specifier|final
name|String
name|otherPrefix
parameter_list|,
specifier|final
name|byte
name|otherNameType
parameter_list|)
block|{
return|return
name|qname
operator|.
name|getNameType
argument_list|()
operator|==
name|otherNameType
operator|&&
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|otherNamespaceURI
argument_list|)
operator|&&
name|qname
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|otherLocalPart
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|,
name|otherPrefix
argument_list|)
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit

