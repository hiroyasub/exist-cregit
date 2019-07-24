begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
operator|.
name|function
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
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
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
name|storage
operator|.
name|DBBroker
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
name|txn
operator|.
name|Txn
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|TriFunction2E
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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

begin_comment
comment|/**  * Specialisation of FunctionE which deals with  * XML-RPC server operations; Predominantly converts exceptions  * from the database into EXistException types  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|XmlRpcDocumentFunction
parameter_list|<
name|R
parameter_list|>
extends|extends
name|TriFunction2E
argument_list|<
name|DocumentImpl
argument_list|,
name|DBBroker
argument_list|,
name|Txn
argument_list|,
name|R
argument_list|,
name|EXistException
argument_list|,
name|PermissionDeniedException
argument_list|>
block|{
annotation|@
name|Override
specifier|default
name|R
name|apply
parameter_list|(
specifier|final
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
try|try
block|{
return|return
name|applyXmlRpc
argument_list|(
name|document
argument_list|,
name|broker
argument_list|,
name|transaction
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Signature for lambda function which takes a document      *      * @param document The database collection      * @param broker the database broker      * @param transaction the database transaction       * @return the result of the function      *      * @throws EXistException if an error occurs with the database      * @throws PermissionDeniedException if the caller has insufficient priviledges      * @throws SAXException if a SAX error occurs      * @throws IOException if an I/O error occurs      */
name|R
name|applyXmlRpc
parameter_list|(
specifier|final
name|DocumentImpl
name|document
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|IOException
function_decl|;
block|}
end_interface

end_unit

