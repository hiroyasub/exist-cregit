begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-08 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * An extension to BinaryResource interface, which adds the  * common methods needed by LocalBinaryResource and RemoteBinaryResource,  * so they can be streamlined.  * @author jmfernandez  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExtendedResource
block|{
comment|/** 	 * It returns an object representing the content, in the representation 	 * which needs less memory. 	 */
specifier|public
name|Object
name|getExtendedContent
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * It returns an stream to the content, whichever it is its origin 	 */
specifier|public
name|InputStream
name|getStreamContent
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * It returns the length of the content, whichever it is its origin 	 */
specifier|public
name|long
name|getStreamLength
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * It saves the resource to the local file given as input parameter. 	 * Do NOT confuse with set content. 	 */
specifier|public
name|void
name|getContentIntoAFile
parameter_list|(
name|File
name|localfile
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * It saves the resource to the local stream given as input parameter. 	 * Do NOT confuse with set content. 	 */
specifier|public
name|void
name|getContentIntoAStream
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

