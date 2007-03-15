begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Created on 27 mai 2005 $Id$ */
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
name|xquery
operator|.
name|Constants
import|;
end_import

begin_comment
comment|/** Simple wrapper around an Indexable object, that adds the collectionId  * to the srailization of the indexable.  * TODO "ValueIndexKeyFactory" refactoring: use this class in NativeValueIndex */
end_comment

begin_class
specifier|public
class|class
name|ValueIndexKeyFactorySimple
implements|implements
name|ValueIndexKeyFactory
block|{
specifier|public
specifier|static
name|int
name|OFFSET_COLLECTION_ID
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
name|int
name|OFFSET_VALUE
init|=
name|OFFSET_COLLECTION_ID
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
decl_stmt|;
comment|//2
specifier|private
name|Indexable
name|indexable
decl_stmt|;
specifier|public
name|ValueIndexKeyFactorySimple
parameter_list|(
name|Indexable
name|indexable
parameter_list|)
block|{
name|this
operator|.
name|indexable
operator|=
name|indexable
expr_stmt|;
block|}
comment|/* provides the persistent storage key : collectionId + qname + indexType + indexData 	 * @deprecated 	 * @see org.exist.storage.ValueIndexKeyFactory#serialize(short, boolean) 	 */
comment|/* 	public byte[] serialize(short collectionId, boolean caseSensitive)  throws EXistException {         final byte[] data = indexable.serializeValue(OFFSET_VALUE, caseSensitive);         ByteConversion.shortToByte(collectionId, data, OFFSET_COLLECTION_ID); 		return data; 	} 	*/
comment|/* provides the persistent storage key : collectionId + qname + indexType + indexData 	 * @see org.exist.storage.ValueIndexKeyFactory#serialize(short, boolean) 	 */
specifier|public
name|byte
index|[]
name|serialize
parameter_list|(
name|short
name|collectionId
parameter_list|)
throws|throws
name|EXistException
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|indexable
operator|.
name|serializeValue
argument_list|(
name|OFFSET_VALUE
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
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
return|return
name|data
return|;
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
name|Constants
operator|.
name|EQUAL
decl_stmt|;
if|if
condition|(
name|other
operator|instanceof
name|ValueIndexKeyFactorySimple
condition|)
block|{
name|ValueIndexKeyFactorySimple
name|otherIndexable
init|=
operator|(
name|ValueIndexKeyFactorySimple
operator|)
name|other
decl_stmt|;
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
return|return
name|ret
return|;
block|}
block|}
end_class

end_unit

