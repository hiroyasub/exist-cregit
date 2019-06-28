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
name|xmldb
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
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|LockException
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
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|TriFunctionE
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
name|ErrorCodes
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
comment|/**  * Specialisation of FunctionE which deals with  * local XMLDB operations; Predominantly converts exceptions  * from the database into XMLDBException types  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|LocalXmldbCollectionFunction
parameter_list|<
name|R
parameter_list|>
extends|extends
name|TriFunctionE
argument_list|<
name|Collection
argument_list|,
name|DBBroker
argument_list|,
name|Txn
argument_list|,
name|R
argument_list|,
name|XMLDBException
argument_list|>
block|{
annotation|@
name|Override
specifier|default
name|R
name|apply
parameter_list|(
specifier|final
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
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
name|XMLDBException
block|{
try|try
block|{
return|return
name|applyXmldb
argument_list|(
name|collection
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
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|COLLECTION_CLOSED
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TriggerException
decl||
name|IOException
decl||
name|SyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Signature for lambda function which takes a collection      *      * @param collection The database collection      * @param broker The database broker for the XMLDB function      * @param transaction The transaction for the XMLDB function      *      * @return the result of apply the function.      *      * @throws XMLDBException if an error occurs whilst applying the function      * @throws PermissionDeniedException if the user has insufficient permissions      * @throws LockException if an error occurs whilst locking a collection or document      * @throws TriggerException if a  trigger raises an error      * @throws IOException if an IO error occurs      * @throws SyntaxException if a syntax error occurs      */
name|R
name|applyXmldb
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|collection
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
name|XMLDBException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|TriggerException
throws|,
name|IOException
throws|,
name|SyntaxException
function_decl|;
block|}
end_interface

end_unit

