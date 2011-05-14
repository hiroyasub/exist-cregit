begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: EmbeddedDownload.java 223 2007-04-21 22:13:05Z dizzzz $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|embedded
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

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
name|dom
operator|.
name|BinaryDocument
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
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmldb
operator|.
name|XmldbURL
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
name|BrokerPool
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
name|lock
operator|.
name|Lock
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|serializers
operator|.
name|Serializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  *   Read document from an embedded database and write the data into an  * output stream.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedDownload
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
name|EmbeddedDownload
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|/**      * Set brokerpool for in database resolve of resource.      * @param brokerPool       */
specifier|public
name|void
name|setBrokerPool
parameter_list|(
name|BrokerPool
name|brokerPool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|brokerPool
expr_stmt|;
block|}
comment|/**      *   Write document referred by URL to an (output)stream.      *      * @param xmldbURL Document location in database.      * @param os Stream to which the document is written.      * @throws IOException      */
specifier|public
name|void
name|stream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
argument_list|(
name|xmldbURL
argument_list|,
name|os
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      *   Write document referred by URL to an (output)stream as specified user.      *      * @param user Effective user for operation. If NULL the user information      * is distilled from the URL.      * @param xmldbURL Document location in database.      * @param os Stream to which the document is written.      * @throws IOException      */
specifier|public
name|void
name|stream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|,
name|OutputStream
name|os
parameter_list|,
name|Subject
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Begin document download"
argument_list|)
expr_stmt|;
name|DocumentImpl
name|resource
init|=
literal|null
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|XmldbURI
name|path
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|xmldbURL
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|pool
operator|==
literal|null
condition|)
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|xmldbURL
operator|.
name|hasUserInfo
argument_list|()
condition|)
block|{
name|user
operator|=
name|EmbeddedUser
operator|.
name|authenticate
argument_list|(
name|xmldbURL
argument_list|,
name|pool
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unauthorized user "
operator|+
name|xmldbURL
operator|.
name|getUsername
argument_list|()
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|user
operator|=
name|EmbeddedUser
operator|.
name|getUserGuest
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
block|}
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|resource
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|path
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
block|{
comment|// Test for collection
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|path
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
comment|// No collection, no document
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|xmldbURL
operator|.
name|getPath
argument_list|()
operator|+
literal|" not found."
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Collection
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|xmldbURL
operator|.
name|getPath
argument_list|()
operator|+
literal|" is a collection."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|resource
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
block|{
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|// Preserve doctype
name|serializer
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|OUTPUT_DOCTYPE
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|Writer
name|w
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|os
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|resource
argument_list|,
name|w
argument_list|)
expr_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|broker
operator|.
name|readBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|resource
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
comment|//ex.printStackTrace();
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
name|ex
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|//ex.printStackTrace();
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|resource
operator|!=
literal|null
condition|)
block|{
name|resource
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"End document download"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

