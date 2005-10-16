begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
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
name|FileNotFoundException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|StringReader
import|;
end_import

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
name|security
operator|.
name|SecurityManager
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
name|Serializer
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
name|TransactionException
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
name|XQuery
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
name|Sequence
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
name|SequenceIterator
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
name|LockException
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
name|xml
operator|.
name|sax
operator|.
name|InputSource
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

begin_comment
comment|/**  *  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseResources
block|{
comment|/** Local reference to database  */
specifier|private
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
comment|/** Local logger */
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DatabaseResources
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Path to grammar in database  */
specifier|private
name|String
name|GRAMMERBASE
init|=
literal|"/db/system/grammar"
decl_stmt|;
specifier|private
name|String
name|XSDBASE
init|=
name|GRAMMERBASE
operator|+
literal|"/xsd"
decl_stmt|;
specifier|private
name|String
name|DTDBASE
init|=
name|GRAMMERBASE
operator|+
literal|"/dtd"
decl_stmt|;
specifier|private
name|String
name|DTDCATALOG
init|=
name|DTDBASE
operator|+
literal|"/catalog.xml"
decl_stmt|;
specifier|public
specifier|static
name|int
name|GRAMMAR_UNKNOWN
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
name|int
name|GRAMMAR_XSD
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
name|int
name|GRAMMAR_DTD
init|=
literal|2
decl_stmt|;
specifier|private
name|String
name|CATALOGCONTENT
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<catalog>\n"
operator|+
literal|"<!-- Warning this file is regenerated at every start -->\n"
operator|+
literal|"<!-- Will be fixed in the near future -->\n"
operator|+
literal|"<!--<public publicId=\"-//PLAY//EN\" uri=\"entities/play.dtd\"/> -->\n"
operator|+
literal|"</catalog>"
decl_stmt|;
comment|/**      * Creates a new instance of DatabaseResources      */
specifier|public
name|DatabaseResources
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing DatabaseResources"
argument_list|)
expr_stmt|;
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
comment|// TODO this must be performed once.... and earlier...
name|insertCollection
argument_list|(
name|GRAMMERBASE
argument_list|)
expr_stmt|;
name|insertCollection
argument_list|(
name|XSDBASE
argument_list|)
expr_stmt|;
name|insertCollection
argument_list|(
name|DTDBASE
argument_list|)
expr_stmt|;
name|insertGrammar
argument_list|(
operator|new
name|StringReader
argument_list|(
name|CATALOGCONTENT
argument_list|)
argument_list|,
name|GRAMMAR_DTD
argument_list|,
literal|"catalog.xml"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|insertCollection
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|boolean
name|insertIsSuccessfull
init|=
literal|false
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|TransactionManager
name|transact
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|insertIsSuccessfull
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|insertIsSuccessfull
return|;
block|}
specifier|public
name|boolean
name|insertGrammar
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|int
name|type
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|boolean
name|insertIsSuccessfull
init|=
literal|false
decl_stmt|;
try|try
block|{
name|File
name|tmpFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"InsertGrammar"
argument_list|,
literal|"tmp"
argument_list|)
decl_stmt|;
name|FileWriter
name|fw
init|=
operator|new
name|FileWriter
argument_list|(
name|tmpFile
argument_list|)
decl_stmt|;
comment|// Transfer bytes from in to out
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|reader
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|fw
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|fw
operator|.
name|close
argument_list|()
expr_stmt|;
name|insertIsSuccessfull
operator|=
name|insertGrammar
argument_list|(
name|tmpFile
argument_list|,
name|type
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|tmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|insertIsSuccessfull
return|;
block|}
specifier|public
name|boolean
name|insertGrammar
parameter_list|(
name|File
name|file
parameter_list|,
name|int
name|type
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|boolean
name|insertIsSuccesfull
init|=
literal|false
decl_stmt|;
comment|// Path = subpath/doc.xml
name|String
name|baseFolder
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|GRAMMAR_XSD
condition|)
block|{
name|baseFolder
operator|=
name|XSDBASE
expr_stmt|;
block|}
else|else
block|{
name|baseFolder
operator|=
name|DTDBASE
expr_stmt|;
block|}
name|String
name|collection
init|=
literal|null
decl_stmt|;
name|String
name|document
init|=
literal|null
decl_stmt|;
name|int
name|separatorPos
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|separatorPos
operator|==
operator|-
literal|1
condition|)
block|{
name|document
operator|=
name|path
expr_stmt|;
name|collection
operator|=
name|baseFolder
expr_stmt|;
block|}
else|else
block|{
name|document
operator|=
name|path
operator|.
name|substring
argument_list|(
name|separatorPos
operator|+
literal|1
argument_list|)
expr_stmt|;
name|collection
operator|=
name|baseFolder
operator|+
literal|"/"
operator|+
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|separatorPos
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
literal|"document="
operator|+
name|document
argument_list|)
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"collection="
operator|+
name|collection
argument_list|)
expr_stmt|;
return|return
name|insertDocumentInDatabase
argument_list|(
name|file
argument_list|,
name|collection
argument_list|,
name|document
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|insertDocumentInDatabase
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|collection
parameter_list|,
name|String
name|document
parameter_list|)
block|{
name|boolean
name|insertIsSuccesfull
init|=
literal|false
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|TransactionManager
name|transact
init|=
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|Collection
name|test
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|test
operator|.
name|validate
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|document
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|FileReader
argument_list|(
name|file
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|insertIsSuccesfull
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|insertIsSuccesfull
return|;
block|}
specifier|public
name|boolean
name|hasGrammar
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
operator|!
name|getGrammarPath
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"NONE"
argument_list|)
return|;
block|}
specifier|public
name|String
name|getGrammarPath
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Get path of '"
operator|+
name|id
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|String
name|result
init|=
literal|"EMPTY"
decl_stmt|;
name|String
name|query
init|=
name|getGrammarQuery
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error getting DBBroker"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
block|{
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"No xQuery result"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"XSD xQuery error: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|String
name|getGrammarQuery
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
comment|// TODO double
name|String
name|query
init|=
literal|"NOQUERY"
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|GRAMMAR_XSD
condition|)
block|{
name|query
operator|=
literal|"let $top := collection('"
operator|+
name|XSDBASE
operator|+
literal|"') "
operator|+
literal|"let $schemas := $top/xs:schema[ @targetNamespace = \""
operator|+
name|id
operator|+
literal|"\" ] "
operator|+
literal|"return if($schemas) then document-uri($schemas[1]) else \"NONE\" "
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|==
name|GRAMMAR_DTD
condition|)
block|{
name|query
operator|=
literal|"let $top := doc('"
operator|+
name|DTDCATALOG
operator|+
literal|"') "
operator|+
literal|"let $dtds := $top//public[@publicId = \""
operator|+
name|id
operator|+
literal|"\"]/@uri "
operator|+
literal|"return if($dtds) then document-uri($dtds[1]) else \"NONE\""
expr_stmt|;
block|}
name|logger
operator|.
name|info
argument_list|(
name|query
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
comment|/**      *  Get GRAMMAR resource specified by DB path      * @param path          Path in DB to resource.      * @param isBinary      Flag is resource binary?      * @return              Reader to the resource.      */
specifier|public
name|byte
index|[]
name|getGrammar
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
name|boolean
name|isBinary
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|GRAMMAR_DTD
condition|)
block|{
name|isBinary
operator|=
literal|true
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Get resource '"
operator|+
name|path
operator|+
literal|"' binary="
operator|+
name|isBinary
argument_list|)
expr_stmt|;
name|Reader
name|reader
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
name|broker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
if|if
condition|(
name|isBinary
condition|)
block|{
name|BinaryDocument
name|binDoc
init|=
operator|(
name|BinaryDocument
operator|)
name|broker
operator|.
name|openDocument
argument_list|(
name|path
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
name|data
operator|=
name|broker
operator|.
name|getBinaryResourceData
argument_list|(
name|binDoc
argument_list|)
expr_stmt|;
name|binDoc
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
else|else
block|{
name|DocumentImpl
name|doc
init|=
name|broker
operator|.
name|openDocument
argument_list|(
name|path
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
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
name|data
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
operator|.
name|getBytes
argument_list|()
expr_stmt|;
name|doc
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
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error opening document"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
literal|"Error serializing document"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
block|{
name|brokerPool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

