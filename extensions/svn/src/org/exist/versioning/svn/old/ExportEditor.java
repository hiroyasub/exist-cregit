begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|old
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
name|FileInputStream
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
name|IOException
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
name|InputStreamReader
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
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|SecurityManager
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
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNCommitInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNErrorCode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNErrorMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNProperty
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNPropertyValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|util
operator|.
name|SVNDate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|util
operator|.
name|SVNEncodingUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|util
operator|.
name|SVNHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|util
operator|.
name|SVNPathUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|wc
operator|.
name|DefaultSVNOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|wc
operator|.
name|SVNErrorManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|wc
operator|.
name|SVNFileUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|wc
operator|.
name|admin
operator|.
name|SVNTranslator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|io
operator|.
name|ISVNEditor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|io
operator|.
name|diff
operator|.
name|SVNDeltaProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|io
operator|.
name|diff
operator|.
name|SVNDiffWindow
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|wc
operator|.
name|ISVNOptions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|util
operator|.
name|SVNLogType
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|ExportEditor
implements|implements
name|ISVNEditor
block|{
specifier|private
name|XmldbURI
name|rootPath
decl_stmt|;
specifier|private
name|Collection
name|myRootDirectory
decl_stmt|;
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
init|=
literal|null
decl_stmt|;
name|Txn
name|transaction
decl_stmt|;
specifier|private
name|SVNProperties
name|fileProperties
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|dirProperties
decl_stmt|;
specifier|private
name|File
name|currentTmpFile
decl_stmt|;
specifier|private
name|SVNDeltaProcessor
name|deltaProcessor
decl_stmt|;
specifier|private
name|String
name|currentPath
decl_stmt|;
specifier|private
name|Collection
name|currentDirectory
decl_stmt|;
specifier|private
name|File
name|currentFile
decl_stmt|;
specifier|private
name|ISVNOptions
name|options
decl_stmt|;
specifier|private
name|String
name|eolStyle
init|=
name|SVNProperty
operator|.
name|EOL_STYLE_NATIVE
decl_stmt|;
specifier|public
name|ExportEditor
parameter_list|(
name|XmldbURI
name|path
parameter_list|)
throws|throws
name|EXistException
block|{
name|rootPath
operator|=
name|path
expr_stmt|;
name|deltaProcessor
operator|=
operator|new
name|SVNDeltaProcessor
argument_list|()
expr_stmt|;
name|options
operator|=
operator|new
name|DefaultSVNOptions
argument_list|()
expr_stmt|;
name|dirProperties
operator|=
operator|new
name|SVNHashMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|targetRevision
parameter_list|(
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"targetRevision"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|openRoot
parameter_list|(
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"openRoot"
argument_list|)
expr_stmt|;
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
comment|//BUG: need to be released!!! where???
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemAccount
argument_list|()
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|myRootDirectory
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|currentDirectory
operator|=
name|myRootDirectory
expr_stmt|;
name|transaction
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: failed to initialize database."
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
name|currentPath
operator|=
literal|""
expr_stmt|;
block|}
specifier|public
name|void
name|addDir
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|copyFromPath
parameter_list|,
name|long
name|copyFromRevision
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"addDir"
argument_list|)
expr_stmt|;
name|currentPath
operator|=
name|path
expr_stmt|;
name|Collection
name|child
decl_stmt|;
try|try
block|{
name|child
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|myRootDirectory
operator|.
name|getURI
argument_list|()
operator|.
name|append
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|currentDirectory
operator|=
name|child
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: failed on permission."
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: failed on IO."
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|openDir
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"openDir"
argument_list|)
expr_stmt|;
name|currentPath
operator|=
name|path
expr_stmt|;
name|currentDirectory
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|myRootDirectory
operator|.
name|getURI
argument_list|()
operator|.
name|append
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|changeDirProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|SVNPropertyValue
name|property
parameter_list|)
throws|throws
name|SVNException
block|{
comment|// UNDERSTAND: should check path?
if|if
condition|(
name|SVNProperty
operator|.
name|EXTERNALS
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
name|property
operator|!=
literal|null
condition|)
block|{
name|dirProperties
operator|.
name|put
argument_list|(
name|currentPath
argument_list|,
name|property
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addFile
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|copyFromPath
parameter_list|,
name|long
name|copyFromRevision
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"addFile path = "
operator|+
name|path
argument_list|)
expr_stmt|;
name|path
operator|=
name|SVNEncodingUtil
operator|.
name|uriEncode
argument_list|(
name|path
argument_list|)
expr_stmt|;
comment|// TODO: check parent for that resource.
comment|// create child resource.
name|currentFile
operator|=
name|SVNFileUtil
operator|.
name|createTempFile
argument_list|(
literal|""
argument_list|,
literal|".tmp"
argument_list|)
expr_stmt|;
comment|// prefix???
comment|// TODO: "COPY"
name|fileProperties
operator|=
operator|new
name|SVNProperties
argument_list|()
expr_stmt|;
name|checksum
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|openFile
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"openFile"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|changeFileProperty
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|name
parameter_list|,
name|SVNPropertyValue
name|property
parameter_list|)
throws|throws
name|SVNException
block|{
comment|// UNDERSTAND: should check path?
name|fileProperties
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
comment|/* ************************************* 	 * ************ text part ************** 	 * ************************************* 	 */
specifier|private
name|String
name|checksum
decl_stmt|;
specifier|public
name|void
name|applyTextDelta
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|baseChecksum
parameter_list|)
throws|throws
name|SVNException
block|{
name|String
name|name
init|=
name|SVNPathUtil
operator|.
name|tail
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|currentTmpFile
operator|=
name|SVNFileUtil
operator|.
name|createTempFile
argument_list|(
name|name
argument_list|,
literal|".tmp"
argument_list|)
expr_stmt|;
name|deltaProcessor
operator|.
name|applyTextDelta
argument_list|(
operator|(
name|File
operator|)
literal|null
argument_list|,
name|currentTmpFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|OutputStream
name|textDeltaChunk
parameter_list|(
name|String
name|path
parameter_list|,
name|SVNDiffWindow
name|diffWindow
parameter_list|)
throws|throws
name|SVNException
block|{
return|return
name|deltaProcessor
operator|.
name|textDeltaChunk
argument_list|(
name|diffWindow
argument_list|)
return|;
block|}
specifier|public
name|void
name|textDeltaEnd
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|checksum
operator|=
name|deltaProcessor
operator|.
name|textDeltaEnd
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|closeFile
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|textChecksum
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" closeFile path = "
operator|+
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|textChecksum
operator|==
literal|null
condition|)
block|{
name|textChecksum
operator|=
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|CHECKSUM
argument_list|)
expr_stmt|;
block|}
name|String
name|realChecksum
init|=
name|checksum
operator|!=
literal|null
condition|?
name|checksum
else|:
name|SVNFileUtil
operator|.
name|computeChecksum
argument_list|(
name|currentTmpFile
argument_list|)
decl_stmt|;
name|checksum
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|textChecksum
operator|!=
literal|null
operator|&&
operator|!
name|textChecksum
operator|.
name|equals
argument_list|(
name|realChecksum
argument_list|)
condition|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|CHECKSUM_MISMATCH
argument_list|,
literal|"Checksum mismatch for ''{0}''; expected: ''{1}'', actual: ''{2}''"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|currentFile
block|,
name|textChecksum
block|,
name|realChecksum
block|}
argument_list|)
decl_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|String
name|date
init|=
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|COMMITTED_DATE
argument_list|)
decl_stmt|;
name|boolean
name|special
init|=
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|SPECIAL
argument_list|)
operator|!=
literal|null
decl_stmt|;
name|boolean
name|binary
init|=
name|SVNProperty
operator|.
name|isBinaryMimeType
argument_list|(
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|MIME_TYPE
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|keywords
init|=
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|KEYWORDS
argument_list|)
decl_stmt|;
name|Map
name|keywordsMap
init|=
literal|null
decl_stmt|;
comment|// if (keywords != null) {
comment|// String url = SVNPathUtil.append(myURL,
comment|// SVNEncodingUtil.uriEncode(currentPath));
comment|// url = SVNPathUtil.append(url,
comment|// SVNEncodingUtil.uriEncode(currentFile.getName()));
comment|// String author =
comment|// fileProperties.getStringValue(SVNProperty.LAST_AUTHOR);
comment|// String revStr =
comment|// fileProperties.getStringValue(SVNProperty.COMMITTED_REVISION);
comment|// keywordsMap = SVNTranslator.computeKeywords(keywords, url,
comment|// author, date, revStr, options);
comment|// }
name|String
name|charset
init|=
name|SVNTranslator
operator|.
name|getCharset
argument_list|(
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|CHARSET
argument_list|)
argument_list|,
name|currentFile
operator|.
name|getPath
argument_list|()
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|byte
index|[]
name|eolBytes
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|SVNProperty
operator|.
name|EOL_STYLE_NATIVE
operator|.
name|equals
argument_list|(
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|EOL_STYLE
argument_list|)
argument_list|)
condition|)
block|{
name|eolBytes
operator|=
name|SVNTranslator
operator|.
name|getEOL
argument_list|(
name|eolStyle
operator|!=
literal|null
condition|?
name|eolStyle
else|:
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|EOL_STYLE
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|fileProperties
operator|.
name|containsName
argument_list|(
name|SVNProperty
operator|.
name|EOL_STYLE
argument_list|)
condition|)
block|{
name|eolBytes
operator|=
name|SVNTranslator
operator|.
name|getEOL
argument_list|(
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|EOL_STYLE
argument_list|)
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|binary
condition|)
block|{
comment|// no translation unless 'special'.
name|charset
operator|=
literal|null
expr_stmt|;
name|eolBytes
operator|=
literal|null
expr_stmt|;
name|keywordsMap
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|charset
operator|!=
literal|null
operator|||
name|eolBytes
operator|!=
literal|null
operator|||
operator|(
name|keywordsMap
operator|!=
literal|null
operator|&&
operator|!
name|keywordsMap
operator|.
name|isEmpty
argument_list|()
operator|)
operator|||
name|special
condition|)
block|{
name|SVNTranslator
operator|.
name|translate
argument_list|(
name|currentTmpFile
argument_list|,
name|currentFile
argument_list|,
name|charset
argument_list|,
name|eolBytes
argument_list|,
name|keywordsMap
argument_list|,
name|special
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SVNFileUtil
operator|.
name|rename
argument_list|(
name|currentTmpFile
argument_list|,
name|currentFile
argument_list|)
expr_stmt|;
block|}
name|boolean
name|executable
init|=
name|fileProperties
operator|.
name|getStringValue
argument_list|(
name|SVNProperty
operator|.
name|EXECUTABLE
argument_list|)
operator|!=
literal|null
decl_stmt|;
if|if
condition|(
name|executable
condition|)
block|{
name|SVNFileUtil
operator|.
name|setExecutable
argument_list|(
name|currentFile
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|special
operator|&&
name|date
operator|!=
literal|null
condition|)
block|{
name|currentFile
operator|.
name|setLastModified
argument_list|(
name|SVNDate
operator|.
name|parseDate
argument_list|(
name|date
argument_list|)
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|XmldbURI
name|fileName
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|path
argument_list|)
operator|.
name|lastSegment
argument_list|()
decl_stmt|;
name|MimeType
name|mimeType
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
comment|// unknown mime type, here preferred is to do nothing
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
comment|// TODO: report error? path +
comment|// " - unknown suffix. No matching mime-type found in : " +
comment|// MimeTable.getInstance().getSrc());
comment|// if some one prefers to store it as binary by default, but
comment|// dangerous
name|mimeType
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|mimeType
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
comment|// store as xml resource
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|currentFile
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|currentDirectory
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|fileName
argument_list|,
operator|new
name|InputSource
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|//									new InputStreamReader(is, charset)));
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|info
operator|.
name|getDocument
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|mimeType
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|currentFile
argument_list|)
expr_stmt|;
name|currentDirectory
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
name|InputStreamReader
argument_list|(
name|is
argument_list|)
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//									new InputStreamReader(is, charset)), false);
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// store as binary resource
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|currentFile
argument_list|)
expr_stmt|;
name|BinaryDocument
name|doc
init|=
name|currentDirectory
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|fileName
argument_list|,
name|is
argument_list|,
name|mimeType
operator|.
name|getName
argument_list|()
argument_list|,
operator|(
name|int
operator|)
name|currentFile
operator|.
name|length
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|,
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: ."
argument_list|)
decl_stmt|;
comment|// TODO: error
comment|// description
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TriggerException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: ."
argument_list|)
decl_stmt|;
comment|// TODO: error
comment|// description
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: ."
argument_list|)
decl_stmt|;
comment|// TODO: error
comment|// description
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: ."
argument_list|)
decl_stmt|;
comment|// TODO: error
comment|// description
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: ."
argument_list|)
decl_stmt|;
comment|// TODO: error
comment|// description
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: ."
argument_list|)
decl_stmt|;
comment|// TODO: error
comment|// description
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: ."
argument_list|)
decl_stmt|;
comment|// TODO: error
comment|// description
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
block|}
name|currentFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|currentTmpFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|closeDir
parameter_list|()
throws|throws
name|SVNException
block|{
name|currentDirectory
operator|=
name|broker
operator|.
name|getCollection
argument_list|(
name|currentDirectory
operator|.
name|getParentURI
argument_list|()
argument_list|)
expr_stmt|;
name|currentPath
operator|=
name|SVNPathUtil
operator|.
name|removeTail
argument_list|(
name|currentPath
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|deleteEntry
parameter_list|(
name|String
name|path
parameter_list|,
name|long
name|revision
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"deleteEntry"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|absentDir
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"absentDir"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|absentFile
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"absentFile"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SVNCommitInfo
name|closeEdit
parameter_list|()
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"closeEdit"
argument_list|)
expr_stmt|;
try|try
block|{
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransactionException
name|e
parameter_list|)
block|{
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|IO_ERROR
argument_list|,
literal|"error: failed on transaction's commit."
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|SVNException
argument_list|(
name|err
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|abortEdit
parameter_list|()
throws|throws
name|SVNException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"abortEdit"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

