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
name|source
package|;
end_package

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
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|security
operator|.
name|PermissionDeniedException
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
name|Subject
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
name|DBBroker
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * A general interface for access to external or internal sources.  * This is mainly used as an abstraction for loading XQuery scripts  * and modules, but can also be applied to other use cases.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Source
block|{
enum|enum
name|Validity
block|{
name|VALID
block|,
name|INVALID
block|,
name|UNKNOWN
block|}
name|String
name|path
parameter_list|()
function_decl|;
name|String
name|type
parameter_list|()
function_decl|;
comment|/**      * Returns a unique key to identify the source, usually      * an URI.      * @return unique key to identify the source, usually an URI      */
name|Object
name|getKey
parameter_list|()
function_decl|;
comment|/**      * Is this source object still valid?      *       * Returns {@link Validity#UNKNOWN} if the validity of      * the source cannot be determined.      *       * The {@link DBBroker} parameter is required by      * some implementations as they have to read      * resources from the database.      *       * @param broker eXist-db broker      * @return Validity of the source object      */
name|Validity
name|isValid
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
comment|/**      * Checks if the source object is still valid      * by comparing it to another version of the      * same source. It depends on the concrete      * implementation how the sources are compared.      *       * Use this method if {@link #isValid(DBBroker)}      * return {@link Validity#UNKNOWN}.      *       * @param other source      * @return Validity of the other source object      */
name|Validity
name|isValid
parameter_list|(
name|Source
name|other
parameter_list|)
function_decl|;
comment|/**      * Returns a {@link Reader} to read the contents      * of the source.      * @return Reader to read the contents of the source      * @throws IOException in case of an I/O error      */
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
function_decl|;
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Returns the character encoding of the underlying source or null if unknown.      *      * @return the character encoding      * @throws IOException in case of an I/O error      */
annotation|@
name|Nullable
name|Charset
name|getEncoding
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Set a timestamp for this source. This is used      * by {@link org.exist.storage.XQueryPool} to      * check if a source has timed out.      *       * @param timestamp for the source      */
name|void
name|setCacheTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
function_decl|;
name|long
name|getCacheTimestamp
parameter_list|()
function_decl|;
comment|/**      * Check: has subject requested permissions for this resource?      *      * @param  subject The subject      * @param  perm The requested permissions      * @throws PermissionDeniedException if user has not sufficient rights      */
name|void
name|validate
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|int
name|perm
parameter_list|)
throws|throws
name|PermissionDeniedException
function_decl|;
name|QName
name|isModule
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

