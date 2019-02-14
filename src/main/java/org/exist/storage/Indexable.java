begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
comment|/**  * This interface should be implemented by all basic types  * to be used as keys in a value index.  *   * @see org.exist.storage.NativeValueIndex  * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Indexable
extends|extends
name|Comparable
block|{
comment|/**      * Serialize the value plus collection and possibly element information      * to an array of bytes.       * The returned byte array has the following format:      *       * (short: collectionId, byte type, byte[] value)      *       * @param collectionId the collection id to use      */
comment|//TODO : better exception ?
comment|//public byte[] serialize(short collectionId) throws EXistException;
comment|/**      * Serialize the value plus collection and possibly element information      * to an array of bytes.      * @ deprecated use following function instead; this API should be local      * to value index class like {@link NativeValueIndex}      *       * The returned byte array has the following format:      *       * (short: collectionId, byte type, byte[] value)      *       * @param collectionId the collection id to use      * @param caseSensitive only relevant for string values: if set to false,      * strings should be serialized in lower case      */
comment|//TODO : better exception ?
comment|//public byte[] serialize(short collectionId, boolean caseSensitive) throws EXistException;
comment|/** Serialize the value to an array of bytes for the persistant storage.      *       * The returned byte array has the following format:      *       * (offset-1 free bytes, byte type, byte[] value)      *       * @ deprecated use following function instead; this API should be local      * to value index class like {@link NativeValueIndex}      * @param offset starting index for writing in array data      * @return the size actually writen in the array argument      */
comment|//TODO : better exception ?
comment|//public byte[] serializeValue(int offset, boolean caseSensitive) throws EXistException;
comment|/** Serialize the value to an array of bytes for the persistant storage.      *       * The returned byte array has the following format:      *       * (offset-1 free bytes, byte type, byte[] value)      *       * @param offset starting index for writing in array data      * @return the size actually writen in the array argument      */
comment|//TODO : better exception ?
specifier|public
name|byte
index|[]
name|serializeValue
parameter_list|(
name|int
name|offset
parameter_list|)
throws|throws
name|EXistException
function_decl|;
comment|/**      * Returns the type of the Indexable as one of the constants defined      * in {@link org.exist.xquery.value.Type}.      *       * @return Type of the Indexable      */
name|int
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

