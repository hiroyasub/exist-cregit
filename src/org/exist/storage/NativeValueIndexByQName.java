begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    * Created on 25 mai 2005 $Id$ */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|exist
operator|.
name|dom
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
name|NodeImpl
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
name|SymbolTable
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
name|store
operator|.
name|BFile
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
name|ByteConversion
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
name|LongLinkedList
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
name|XPathException
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
name|AtomicValue
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
name|StringValue
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
name|Type
import|;
end_import

begin_comment
comment|/** The new index by QName that will make queries like<pre> / root [ key = 123 ]</pre> very quick.  It is used by an Xquery extension function with this signature :<pre> qname-index-lookup( $qname as xs:string,                      $key as xs:string ) as node*</pre>  that can be used this way :<pre> $key := qname-index-lookup( "key", "123") $user := $key / parent::root</pre>  The way of indexing is the same as current range indices {@link NativeValueIndex},  except that for each QName like<key> mentioned above, the QName will be stored .    * @author Jean-Marc Vanel http://jmvanel.free.fr/  */
end_comment

begin_class
specifier|public
class|class
name|NativeValueIndexByQName
extends|extends
name|NativeValueIndex
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
name|NativeValueIndexByQName
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|NativeValueIndexByQName
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|BFile
name|valuesDb
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|valuesDb
argument_list|)
expr_stmt|;
block|}
comment|/** @see org.exist.storage.NativeValueIndex#storeAttribute(org.exist.storage.ValueIndexSpec, org.exist.dom.AttrImpl) 	 */
specifier|public
name|void
name|storeAttribute
parameter_list|(
name|ValueIndexSpec
name|spec
parameter_list|,
name|AttrImpl
name|node
parameter_list|)
block|{
name|Indexable
name|indexable
init|=
name|computeTemporaryKey
argument_list|(
name|spec
operator|.
name|getType
argument_list|()
argument_list|,
name|node
operator|.
name|getValue
argument_list|()
argument_list|,
name|node
operator|.
name|getQName
argument_list|()
argument_list|)
decl_stmt|;
name|updatePendingIndexEntry
argument_list|(
name|node
argument_list|,
name|indexable
argument_list|)
expr_stmt|;
block|}
comment|/** @see org.exist.storage.NativeValueIndex#storeElement(int, org.exist.dom.ElementImpl, java.lang.String) 	 */
specifier|public
name|void
name|storeElement
parameter_list|(
name|int
name|xpathType
parameter_list|,
name|ElementImpl
name|node
parameter_list|,
name|String
name|content
parameter_list|)
block|{
name|Indexable
name|indexable
init|=
name|computeTemporaryKey
argument_list|(
name|xpathType
argument_list|,
name|content
argument_list|,
name|node
operator|.
name|getQName
argument_list|()
argument_list|)
decl_stmt|;
name|updatePendingIndexEntry
argument_list|(
name|node
argument_list|,
name|indexable
argument_list|)
expr_stmt|;
block|}
comment|/** adds or updates an entry in the {@link #pending} map 	 * @param node the DOM node 	 * @param indexable a {@link QNameIndexable} 	 */
specifier|private
name|void
name|updatePendingIndexEntry
parameter_list|(
name|NodeImpl
name|node
parameter_list|,
name|Indexable
name|indexable
parameter_list|)
block|{
if|if
condition|(
name|indexable
operator|==
literal|null
condition|)
return|return;
comment|// skip
name|LongLinkedList
name|buf
decl_stmt|;
if|if
condition|(
name|pending
operator|.
name|containsKey
argument_list|(
name|indexable
argument_list|)
condition|)
name|buf
operator|=
operator|(
name|LongLinkedList
operator|)
name|pending
operator|.
name|get
argument_list|(
name|indexable
argument_list|)
expr_stmt|;
else|else
block|{
name|buf
operator|=
operator|new
name|LongLinkedList
argument_list|()
expr_stmt|;
name|pending
operator|.
name|put
argument_list|(
name|indexable
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|add
argument_list|(
name|node
operator|.
name|getGID
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** compute a key for the {@link #pending} map */
specifier|private
name|Indexable
name|computeTemporaryKey
parameter_list|(
name|int
name|xpathType
parameter_list|,
name|String
name|value
parameter_list|,
name|QName
name|qname
parameter_list|)
block|{
specifier|final
name|StringValue
name|str
init|=
operator|new
name|StringValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|AtomicValue
name|atomic
init|=
literal|null
decl_stmt|;
name|QNameIndexable
name|ret
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|xpathType
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
name|atomic
operator|=
name|str
expr_stmt|;
else|else
block|{
try|try
block|{
name|atomic
operator|=
name|str
operator|.
name|convertTo
argument_list|(
name|xpathType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Node value: '"
operator|+
name|value
operator|+
literal|"' cannot be converted to type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|xpathType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|atomic
operator|instanceof
name|Indexable
condition|)
block|{
if|if
condition|(
name|atomic
operator|!=
literal|null
condition|)
name|ret
operator|=
operator|new
name|QNameIndexable
argument_list|(
operator|(
name|Indexable
operator|)
name|atomic
argument_list|,
name|qname
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The specified type: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|xpathType
argument_list|)
operator|+
literal|" cannot be used as index key. It does not implement interface Indexable."
argument_list|)
expr_stmt|;
name|atomic
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/** key for the {@link #pending} map ; the order is lexicographic on  	 * qname first, indexable second ; 	 * this class also provides through serialize() the persistant storage key : 	 * (collectionId, qname, indexType, indexData) 	 */
specifier|private
class|class
name|QNameIndexable
implements|implements
name|Indexable
block|{
specifier|private
name|QName
name|qname
decl_stmt|;
specifier|private
name|Indexable
name|indexable
decl_stmt|;
specifier|public
name|QNameIndexable
parameter_list|(
name|Indexable
name|indexable
parameter_list|,
name|QName
name|qname
parameter_list|)
block|{
name|this
operator|.
name|indexable
operator|=
name|indexable
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
block|}
comment|/** the one that is called from {@link NativeValueIndex} */
specifier|public
name|byte
index|[]
name|serialize
parameter_list|(
name|short
name|collectionId
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
comment|// key (collectionId, qname, indexType, indexData)
specifier|final
name|byte
index|[]
name|data
init|=
name|indexable
operator|.
name|serializeValue
argument_list|(
literal|4
argument_list|,
name|caseSensitive
argument_list|)
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|serializeQName
argument_list|(
name|data
argument_list|,
literal|2
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/** serialize the QName field on the persistant storage */
specifier|private
name|void
name|serializeQName
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|SymbolTable
name|symbols
init|=
name|broker
operator|.
name|getSymbols
argument_list|()
decl_stmt|;
name|short
name|namespaceId
init|=
name|symbols
operator|.
name|getNSSymbol
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|short
name|localNameId
init|=
name|symbols
operator|.
name|getSymbol
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
decl_stmt|;
name|data
index|[
name|offset
index|]
operator|=
operator|(
name|byte
operator|)
name|namespaceId
expr_stmt|;
name|data
index|[
name|offset
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
name|localNameId
expr_stmt|;
block|}
comment|/** @return negative value<==> this object is less than other */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
name|int
name|ret
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|other
operator|instanceof
name|QNameIndexable
condition|)
block|{
name|QNameIndexable
name|otherIndexable
init|=
operator|(
name|QNameIndexable
operator|)
name|other
decl_stmt|;
name|int
name|qnameComparison
init|=
name|qname
operator|.
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|otherIndexable
operator|.
name|qname
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|qnameComparison
operator|!=
literal|0
condition|)
block|{
name|ret
operator|=
name|qnameComparison
expr_stmt|;
block|}
else|else
block|{
name|ret
operator|=
name|indexable
operator|.
name|compareTo
argument_list|(
name|otherIndexable
operator|.
name|indexable
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ret
return|;
block|}
comment|/** unused */
specifier|public
name|byte
index|[]
name|serializeValue
parameter_list|(
name|int
name|offset
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

