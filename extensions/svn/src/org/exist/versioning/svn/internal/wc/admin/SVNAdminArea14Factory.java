begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ====================================================================  * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.  *  * This software is licensed as described in the file COPYING, which  * you should have received as part of this distribution.  The terms  * are also available at http://svnkit.com/license.html  * If newer versions of this license are posted there, you may use a  * newer version instead, at your option.  * ====================================================================  */
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
name|internal
operator|.
name|wc
operator|.
name|admin
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|IOException
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
name|util
operator|.
name|logging
operator|.
name|Level
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
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
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
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
operator|.
name|SVNFileType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
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
name|SVNDepth
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
name|util
operator|.
name|SVNLogType
import|;
end_import

begin_comment
comment|/**  * @version 1.3  * @author  TMate Software Ltd.  */
end_comment

begin_class
specifier|public
class|class
name|SVNAdminArea14Factory
extends|extends
name|SVNAdminAreaFactory
block|{
specifier|public
specifier|static
specifier|final
name|int
name|WC_FORMAT
init|=
name|SVNAdminAreaFactory
operator|.
name|WC_FORMAT_14
decl_stmt|;
specifier|protected
name|void
name|doCreateVersionedDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|String
name|url
parameter_list|,
name|String
name|rootURL
parameter_list|,
name|String
name|uuid
parameter_list|,
name|long
name|revNumber
parameter_list|,
name|SVNDepth
name|depth
parameter_list|)
throws|throws
name|SVNException
block|{
name|SVNAdminArea
name|adminArea
init|=
operator|new
name|SVNAdminArea14
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|adminArea
operator|.
name|createVersionedDirectory
argument_list|(
name|path
argument_list|,
name|url
argument_list|,
name|rootURL
argument_list|,
name|uuid
argument_list|,
name|revNumber
argument_list|,
literal|true
argument_list|,
name|depth
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SVNAdminArea
name|doOpen
parameter_list|(
name|File
name|path
parameter_list|,
name|int
name|version
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|version
operator|!=
name|getSupportedVersion
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SVNAdminArea14
argument_list|(
name|path
argument_list|)
return|;
block|}
specifier|protected
name|SVNAdminArea
name|doChangeWCFormat
parameter_list|(
name|SVNAdminArea
name|adminArea
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|adminArea
operator|==
literal|null
operator|||
name|adminArea
operator|.
name|getClass
argument_list|()
operator|==
name|SVNAdminArea14
operator|.
name|class
condition|)
block|{
return|return
name|adminArea
return|;
block|}
name|SVNAdminArea14
name|newestAdminArea
init|=
operator|new
name|SVNAdminArea14
argument_list|(
name|adminArea
operator|.
name|getRoot
argument_list|()
argument_list|)
decl_stmt|;
name|newestAdminArea
operator|.
name|setLocked
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|newestAdminArea
operator|.
name|formatWC
argument_list|(
name|adminArea
argument_list|)
return|;
block|}
specifier|public
name|int
name|getSupportedVersion
parameter_list|()
block|{
return|return
name|WC_FORMAT
return|;
block|}
specifier|protected
name|int
name|doCheckWC
parameter_list|(
name|File
name|path
parameter_list|,
name|Level
name|logLevel
parameter_list|)
throws|throws
name|SVNException
block|{
name|File
name|adminDir
init|=
operator|new
name|Resource
argument_list|(
name|path
argument_list|,
name|SVNFileUtil
operator|.
name|getAdminDirectoryName
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|entriesFile
init|=
operator|new
name|Resource
argument_list|(
name|adminDir
argument_list|,
literal|"entries"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|entriesFile
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|0
return|;
comment|//            SVNErrorMessage err = SVNErrorMessage.create(SVNErrorCode.IO_ERROR, "Cannot read entries file ''{0}'': {1}", new Object[] {entriesFile, "This resource does not exist."});
comment|//            throw new SVNException(err);
block|}
name|int
name|formatVersion
init|=
operator|-
literal|1
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|SVNFileUtil
operator|.
name|openFileForReading
argument_list|(
name|entriesFile
argument_list|,
name|logLevel
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
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
literal|"Cannot read entries file ''{0}'': {1}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|entriesFile
block|,
name|e
operator|.
name|getLocalizedMessage
argument_list|()
block|}
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
literal|"Cannot read entries file ''{0}'': {1}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|entriesFile
block|,
name|e
operator|.
name|getLocalizedMessage
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|e
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|svne
parameter_list|)
block|{
name|SVNFileType
name|type
init|=
name|SVNFileType
operator|.
name|getType
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
name|SVNFileType
operator|.
name|DIRECTORY
operator|||
operator|!
name|entriesFile
operator|.
name|exists
argument_list|()
condition|)
block|{
if|if
condition|(
name|type
operator|==
name|SVNFileType
operator|.
name|NONE
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
name|IO_ERROR
argument_list|,
literal|"''{0}'' does not exist"
argument_list|,
name|path
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
return|return
literal|0
return|;
block|}
throw|throw
name|svne
throw|;
block|}
finally|finally
block|{
name|SVNFileUtil
operator|.
name|closeFile
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|==
literal|null
operator|||
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
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
name|STREAM_UNEXPECTED_EOF
argument_list|,
literal|"Reading ''{0}''"
argument_list|,
name|entriesFile
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
name|formatVersion
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
name|BAD_VERSION_FILE_FORMAT
argument_list|,
literal|"First line of ''{0}'' contains non-digit"
argument_list|,
name|entriesFile
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
return|return
name|formatVersion
return|;
block|}
specifier|protected
name|int
name|getVersion
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|SVNException
block|{
name|File
name|adminDir
init|=
operator|new
name|Resource
argument_list|(
name|path
argument_list|,
name|SVNFileUtil
operator|.
name|getAdminDirectoryName
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|entriesFile
init|=
operator|new
name|Resource
argument_list|(
name|adminDir
argument_list|,
literal|"entries"
argument_list|)
decl_stmt|;
name|int
name|formatVersion
init|=
operator|-
literal|1
decl_stmt|;
name|BufferedReader
name|reader
init|=
literal|null
decl_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|SVNFileUtil
operator|.
name|openFileForReading
argument_list|(
name|entriesFile
argument_list|,
name|Level
operator|.
name|FINEST
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
expr_stmt|;
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
literal|"Cannot read entries file ''{0}'': {1}"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|entriesFile
block|,
name|e
operator|.
name|getLocalizedMessage
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|e
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|svne
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
name|WC_NOT_DIRECTORY
argument_list|,
literal|"''{0}'' is not a working copy"
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|err
operator|.
name|setChildErrorMessage
argument_list|(
name|svne
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err
argument_list|,
name|svne
argument_list|,
name|Level
operator|.
name|FINEST
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|SVNFileUtil
operator|.
name|closeFile
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|line
operator|==
literal|null
operator|||
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
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
name|STREAM_UNEXPECTED_EOF
argument_list|,
literal|"Reading ''{0}''"
argument_list|,
name|entriesFile
argument_list|)
decl_stmt|;
name|SVNErrorMessage
name|err1
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|WC_NOT_DIRECTORY
argument_list|,
literal|"''{0}'' is not a working copy"
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|err1
operator|.
name|setChildErrorMessage
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err1
argument_list|,
name|Level
operator|.
name|FINEST
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|formatVersion
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|line
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
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
name|BAD_VERSION_FILE_FORMAT
argument_list|,
literal|"First line of ''{0}'' contains non-digit"
argument_list|,
name|entriesFile
argument_list|)
decl_stmt|;
name|SVNErrorMessage
name|err1
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|WC_NOT_DIRECTORY
argument_list|,
literal|"''{0}'' is not a working copy"
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|err1
operator|.
name|setChildErrorMessage
argument_list|(
name|err
argument_list|)
expr_stmt|;
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|err1
argument_list|,
name|Level
operator|.
name|FINEST
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
block|}
return|return
name|formatVersion
return|;
block|}
block|}
end_class

end_unit

