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

begin_comment
comment|/** Factory for Keys for Value Indices;  * provides through serialize() the persistant storage key. */
end_comment

begin_interface
specifier|public
interface|interface
name|ValueIndexKeyFactory
extends|extends
name|Comparable
block|{
comment|/** this is called from {@link NativeValueIndex}  	 * @return the persistant storage key */
comment|//public byte[] serialize(short collectionId, boolean caseSensitive) throws EXistException;
block|}
end_interface

end_unit

