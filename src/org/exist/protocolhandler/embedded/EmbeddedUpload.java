begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: EmbeddedUpload.java 223 2007-04-21 22:13:05Z dizzzz $  */
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
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|collections
operator|.
name|IndexInfo
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
name|txn
operator|.
name|TransactionManager
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
name|MimeTable
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
name|MimeType
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_comment
comment|/**  *   Read a document from a (input)stream and write it into database.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|EmbeddedUpload
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|EmbeddedUpload
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      *   Read document from stream and write data to database.      *      * @param xmldbURL Location in database.      * @param is Stream containing document.      * @throws IOException      */
specifier|public
name|void
name|stream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
argument_list|(
name|xmldbURL
argument_list|,
name|is
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Read document from stream and write data to database with specified user.      *      * @param user Effective user for operation. If NULL the user information      * is distilled from the URL.      * @param xmldbURL Location in database.      * @param is Stream containing document.      * @throws IOException      */
specifier|public
name|void
name|stream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|,
name|InputStream
name|is
parameter_list|,
name|Subject
name|user
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|tmp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|tmp
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"EMBEDDED"
argument_list|,
literal|"tmp"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
name|is
argument_list|,
name|tmp
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
comment|// Let database read file
name|stream
argument_list|(
name|xmldbURL
argument_list|,
name|tmp
operator|.
name|toFile
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
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
finally|finally
block|{
if|if
condition|(
name|tmp
operator|!=
literal|null
condition|)
block|{
name|Files
operator|.
name|delete
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      *  Read document and write data to database.      *      * @param xmldbURL Location in database.      * @param tmp Document that is inserted.      * @throws IOException      */
specifier|public
name|void
name|stream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|,
name|File
name|tmp
parameter_list|)
throws|throws
name|IOException
block|{
name|stream
argument_list|(
name|xmldbURL
argument_list|,
name|tmp
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Read document and write data to database.      *      * @param user  Effective user for operation. If NULL the user information      * is distilled from the URL.      * @param xmldbURL Location in database.      * @param tmp Document that is inserted.      * @throws IOException      */
specifier|public
name|void
name|stream
parameter_list|(
name|XmldbURL
name|xmldbURL
parameter_list|,
name|File
name|tmp
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
literal|"Begin document upload"
argument_list|)
expr_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
name|boolean
name|collectionLocked
init|=
literal|true
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unauthorized user "
operator|+
name|xmldbURL
operator|.
name|getUsername
argument_list|()
argument_list|)
expr_stmt|;
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
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|)
block|{
specifier|final
name|XmldbURI
name|collectionUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|xmldbURL
operator|.
name|getCollection
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|XmldbURI
name|documentUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|xmldbURL
operator|.
name|getDocumentName
argument_list|()
argument_list|)
decl_stmt|;
name|collection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collectionUri
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
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|collectionUri
operator|.
name|toString
argument_list|()
operator|+
literal|" is not a collection."
argument_list|)
throw|;
block|}
if|if
condition|(
name|collection
operator|.
name|hasChildCollection
argument_list|(
name|broker
argument_list|,
name|documentUri
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Resource "
operator|+
name|documentUri
operator|.
name|toString
argument_list|()
operator|+
literal|" is a collection."
argument_list|)
throw|;
block|}
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|documentUri
argument_list|)
decl_stmt|;
name|String
name|contentType
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
name|contentType
operator|=
name|mime
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|mime
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
if|if
condition|(
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing XML resource"
argument_list|)
expr_stmt|;
specifier|final
name|InputSource
name|inputsource
init|=
operator|new
name|InputSource
argument_list|(
name|tmp
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|documentUri
argument_list|,
name|inputsource
argument_list|)
decl_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|info
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|collection
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|collectionLocked
operator|=
literal|false
expr_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|inputsource
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"storing Binary resource"
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|tmp
argument_list|)
init|)
block|{
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|documentUri
argument_list|,
name|is
argument_list|,
name|contentType
argument_list|,
name|tmp
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"done"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"commit"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
comment|//ex.printStackTrace();
name|LOG
operator|.
name|debug
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
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
comment|//ex.printStackTrace();
name|LOG
operator|.
name|debug
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Done."
argument_list|)
expr_stmt|;
if|if
condition|(
name|collectionLocked
operator|&&
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
block|}
block|}
block|}
end_class

end_unit

