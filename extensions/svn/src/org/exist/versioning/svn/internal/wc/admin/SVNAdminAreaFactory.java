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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|versioning
operator|.
name|svn
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
name|SVNEventFactory
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
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|wc
operator|.
name|ISVNEventHandler
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
name|wc
operator|.
name|SVNEvent
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
name|wc
operator|.
name|SVNEventAction
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
name|core
operator|.
name|SVNNodeKind
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
name|SVNURL
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
name|ISVNAdminAreaFactorySelector
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
name|SVNRepository
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
specifier|abstract
class|class
name|SVNAdminAreaFactory
implements|implements
name|Comparable
block|{
specifier|public
specifier|static
specifier|final
name|int
name|WC_FORMAT_13
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|WC_FORMAT_14
init|=
literal|8
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|WC_FORMAT_15
init|=
literal|9
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|WC_FORMAT_16
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Collection
name|ourFactories
init|=
operator|new
name|TreeSet
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|ourIsUpgradeEnabled
init|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"svnkit.upgradeWC"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"javasvn.upgradeWC"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|booleanValue
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|ISVNAdminAreaFactorySelector
name|ourSelector
decl_stmt|;
specifier|private
specifier|static
name|ISVNAdminAreaFactorySelector
name|ourDefaultSelector
init|=
operator|new
name|DefaultSelector
argument_list|()
decl_stmt|;
static|static
block|{
name|SVNAdminAreaFactory
operator|.
name|registerFactory
argument_list|(
operator|new
name|SVNAdminArea16Factory
argument_list|()
argument_list|)
expr_stmt|;
comment|//        SVNAdminAreaFactory.registerFactory(new SVNAdminArea15Factory());
comment|//        SVNAdminAreaFactory.registerFactory(new SVNAdminArea14Factory());
comment|//        SVNAdminAreaFactory.registerFactory(new SVNXMLAdminAreaFactory());
block|}
specifier|public
specifier|static
name|void
name|setUpgradeEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|ourIsUpgradeEnabled
operator|=
name|enabled
expr_stmt|;
block|}
specifier|public
specifier|static
name|boolean
name|isUpgradeEnabled
parameter_list|()
block|{
return|return
name|ourIsUpgradeEnabled
return|;
block|}
specifier|public
specifier|static
name|void
name|setSelector
parameter_list|(
name|ISVNAdminAreaFactorySelector
name|selector
parameter_list|)
block|{
name|ourSelector
operator|=
name|selector
expr_stmt|;
block|}
specifier|public
specifier|static
name|ISVNAdminAreaFactorySelector
name|getSelector
parameter_list|()
block|{
return|return
name|ourSelector
operator|!=
literal|null
condition|?
name|ourSelector
else|:
name|ourDefaultSelector
return|;
block|}
specifier|public
specifier|static
name|int
name|checkWC
parameter_list|(
name|File
name|path
parameter_list|,
name|boolean
name|useSelector
parameter_list|)
throws|throws
name|SVNException
block|{
return|return
name|checkWC
argument_list|(
name|path
argument_list|,
name|useSelector
argument_list|,
name|Level
operator|.
name|FINE
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|int
name|checkWC
parameter_list|(
name|File
name|path
parameter_list|,
name|boolean
name|useSelector
parameter_list|,
name|Level
name|logLevel
parameter_list|)
throws|throws
name|SVNException
block|{
name|Collection
name|enabledFactories
init|=
name|ourFactories
decl_stmt|;
if|if
condition|(
name|useSelector
condition|)
block|{
name|enabledFactories
operator|=
name|getSelector
argument_list|()
operator|.
name|getEnabledFactories
argument_list|(
name|path
argument_list|,
name|enabledFactories
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
name|SVNErrorMessage
name|error
init|=
literal|null
decl_stmt|;
name|int
name|version
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|Iterator
name|factories
init|=
name|enabledFactories
operator|.
name|iterator
argument_list|()
init|;
name|factories
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SVNAdminAreaFactory
name|factory
init|=
operator|(
name|SVNAdminAreaFactory
operator|)
name|factories
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|version
operator|=
name|factory
operator|.
name|doCheckWC
argument_list|(
name|path
argument_list|,
name|logLevel
argument_list|)
expr_stmt|;
if|if
condition|(
name|version
operator|==
literal|0
condition|)
block|{
return|return
name|version
return|;
block|}
if|if
condition|(
name|version
operator|>
name|factory
operator|.
name|getSupportedVersion
argument_list|()
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
name|WC_UNSUPPORTED_FORMAT
argument_list|,
literal|"The path ''{0}'' appears to be part of a Subversion 1.7 or greater\n"
operator|+
literal|"working copy.  Please upgrade your Subversion client to use this\n"
operator|+
literal|"working copy."
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
if|else if
condition|(
name|version
operator|<
name|factory
operator|.
name|getSupportedVersion
argument_list|()
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
name|WC_UNSUPPORTED_FORMAT
argument_list|,
literal|"Working copy format of {0} is too old ({1}); please check out your working copy again"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|path
block|,
operator|new
name|Integer
argument_list|(
name|version
argument_list|)
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
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|error
operator|.
name|setChildErrorMessage
argument_list|(
name|e
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|error
operator|=
name|e
operator|.
name|getErrorMessage
argument_list|()
expr_stmt|;
block|}
continue|continue;
block|}
return|return
name|version
return|;
block|}
if|if
condition|(
name|error
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|checkWCNG
argument_list|(
name|path
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
name|error
operator|=
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
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|.
name|getErrorCode
argument_list|()
operator|==
name|SVNErrorCode
operator|.
name|WC_UNSUPPORTED_FORMAT
condition|)
block|{
name|error
operator|.
name|setChildErrorMessage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|error
argument_list|,
name|logLevel
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|private
specifier|static
name|void
name|checkWCNG
parameter_list|(
name|File
name|path
parameter_list|,
name|File
name|targetPath
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|File
name|dbFile
init|=
operator|new
name|Resource
argument_list|(
name|path
argument_list|,
literal|".svn/wc.db"
argument_list|)
decl_stmt|;
name|SVNFileType
name|type
init|=
name|SVNFileType
operator|.
name|getType
argument_list|(
name|dbFile
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|SVNFileType
operator|.
name|FILE
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
name|WC_UNSUPPORTED_FORMAT
argument_list|,
literal|"The path ''{0}'' appears to be part of Subversion 1.7 (SVNKit 1.4) or greater\n"
operator|+
literal|"working copy rooted at ''{1}''.\n"
operator|+
literal|"Please upgrade your Subversion (SVNKit) client to use this working copy."
argument_list|,
operator|new
name|Object
index|[]
block|{
name|targetPath
block|,
name|path
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
name|checkWCNG
argument_list|(
name|path
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|targetPath
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|SVNAdminArea
name|open
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
name|SVNErrorMessage
name|error
init|=
literal|null
decl_stmt|;
name|int
name|wcFormatVersion
init|=
operator|-
literal|1
decl_stmt|;
name|Collection
name|enabledFactories
init|=
name|getSelector
argument_list|()
operator|.
name|getEnabledFactories
argument_list|(
name|path
argument_list|,
name|ourFactories
argument_list|,
literal|false
argument_list|)
decl_stmt|;
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
name|adminDir
operator|.
name|isDirectory
argument_list|()
operator|&&
name|entriesFile
operator|.
name|isFile
argument_list|()
condition|)
block|{
for|for
control|(
name|Iterator
name|factories
init|=
name|enabledFactories
operator|.
name|iterator
argument_list|()
init|;
name|factories
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SVNAdminAreaFactory
name|factory
init|=
operator|(
name|SVNAdminAreaFactory
operator|)
name|factories
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|wcFormatVersion
operator|=
name|factory
operator|.
name|getVersion
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|wcFormatVersion
operator|>
name|factory
operator|.
name|getSupportedVersion
argument_list|()
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
name|WC_UNSUPPORTED_FORMAT
argument_list|,
literal|"The path ''{0}'' appears to be part of a Subversion 1.7 or greater\n"
operator|+
literal|"working copy.  Please upgrade your Subversion client to use this\n"
operator|+
literal|"working copy."
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
if|else if
condition|(
name|wcFormatVersion
operator|<
name|factory
operator|.
name|getSupportedVersion
argument_list|()
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
name|WC_UNSUPPORTED_FORMAT
argument_list|,
literal|"Working copy format of {0} is too old ({1}); please check out your working copy again"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|path
block|,
operator|new
name|Integer
argument_list|(
name|wcFormatVersion
argument_list|)
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
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|error
operator|.
name|setChildErrorMessage
argument_list|(
name|e
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|error
operator|=
name|e
operator|.
name|getErrorMessage
argument_list|()
expr_stmt|;
block|}
continue|continue;
block|}
name|SVNAdminArea
name|adminArea
init|=
name|factory
operator|.
name|doOpen
argument_list|(
name|path
argument_list|,
name|wcFormatVersion
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminArea
operator|!=
literal|null
condition|)
block|{
name|adminArea
operator|.
name|setWorkingCopyFormatVersion
argument_list|(
name|wcFormatVersion
argument_list|)
expr_stmt|;
return|return
name|adminArea
return|;
block|}
block|}
block|}
if|if
condition|(
name|error
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|checkWCNG
argument_list|(
name|path
operator|.
name|getAbsoluteFile
argument_list|()
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
name|error
operator|=
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
expr_stmt|;
block|}
if|if
condition|(
name|error
operator|.
name|getErrorCode
argument_list|()
operator|==
name|SVNErrorCode
operator|.
name|WC_UNSUPPORTED_FORMAT
condition|)
block|{
name|error
operator|.
name|setChildErrorMessage
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|error
argument_list|,
name|logLevel
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|SVNAdminArea
name|upgrade
parameter_list|(
name|SVNAdminArea
name|area
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|isUpgradeEnabled
argument_list|()
operator|&&
operator|!
name|ourFactories
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|Collection
name|enabledFactories
init|=
name|getSelector
argument_list|()
operator|.
name|getEnabledFactories
argument_list|(
name|area
operator|.
name|getRoot
argument_list|()
argument_list|,
name|ourFactories
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|enabledFactories
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SVNAdminAreaFactory
name|newestFactory
init|=
operator|(
name|SVNAdminAreaFactory
operator|)
name|enabledFactories
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|SVNAdminArea
name|newArea
init|=
name|newestFactory
operator|.
name|doChangeWCFormat
argument_list|(
name|area
argument_list|)
decl_stmt|;
if|if
condition|(
name|newArea
operator|!=
literal|null
operator|&&
name|newArea
operator|!=
name|area
operator|&&
name|newArea
operator|.
name|getWCAccess
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SVNEvent
name|event
init|=
name|SVNEventFactory
operator|.
name|createSVNEvent
argument_list|(
name|newArea
operator|.
name|getRoot
argument_list|()
argument_list|,
name|SVNNodeKind
operator|.
name|DIR
argument_list|,
literal|null
argument_list|,
name|SVNRepository
operator|.
name|INVALID_REVISION
argument_list|,
name|SVNEventAction
operator|.
name|UPGRADE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|newArea
operator|.
name|getWCAccess
argument_list|()
operator|.
name|handleEvent
argument_list|(
name|event
argument_list|,
name|ISVNEventHandler
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
name|area
operator|=
name|newArea
expr_stmt|;
block|}
block|}
return|return
name|area
return|;
block|}
specifier|public
specifier|static
name|SVNAdminArea
name|changeWCFormat
parameter_list|(
name|SVNAdminArea
name|adminArea
parameter_list|,
name|int
name|format
parameter_list|)
throws|throws
name|SVNException
block|{
name|SVNAdminAreaFactory
name|factory
init|=
name|getAdminAreaFactory
argument_list|(
name|format
argument_list|)
decl_stmt|;
name|SVNAdminArea
name|newArea
init|=
name|factory
operator|.
name|doChangeWCFormat
argument_list|(
name|adminArea
argument_list|)
decl_stmt|;
if|if
condition|(
name|newArea
operator|!=
literal|null
operator|&&
name|newArea
operator|!=
name|adminArea
operator|&&
name|newArea
operator|.
name|getWCAccess
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|SVNEvent
name|event
init|=
name|SVNEventFactory
operator|.
name|createSVNEvent
argument_list|(
name|newArea
operator|.
name|getRoot
argument_list|()
argument_list|,
name|SVNNodeKind
operator|.
name|DIR
argument_list|,
literal|null
argument_list|,
name|SVNRepository
operator|.
name|INVALID_REVISION
argument_list|,
name|SVNEventAction
operator|.
name|UPGRADE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|newArea
operator|.
name|getWCAccess
argument_list|()
operator|.
name|handleEvent
argument_list|(
name|event
argument_list|,
name|ISVNEventHandler
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
block|}
name|adminArea
operator|=
name|newArea
expr_stmt|;
return|return
name|adminArea
return|;
block|}
specifier|private
specifier|static
name|SVNAdminAreaFactory
name|getAdminAreaFactory
parameter_list|(
name|int
name|wcFormat
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|wcFormat
operator|==
name|SVNXMLAdminAreaFactory
operator|.
name|WC_FORMAT
condition|)
block|{
return|return
operator|new
name|SVNXMLAdminAreaFactory
argument_list|()
return|;
block|}
if|if
condition|(
name|wcFormat
operator|==
name|SVNAdminArea14Factory
operator|.
name|WC_FORMAT
condition|)
block|{
return|return
operator|new
name|SVNAdminArea14Factory
argument_list|()
return|;
block|}
if|if
condition|(
name|wcFormat
operator|==
name|SVNAdminArea15Factory
operator|.
name|WC_FORMAT
condition|)
block|{
return|return
operator|new
name|SVNAdminArea15Factory
argument_list|()
return|;
block|}
if|if
condition|(
name|wcFormat
operator|==
name|SVNAdminArea16Factory
operator|.
name|WC_FORMAT
condition|)
block|{
return|return
operator|new
name|SVNAdminArea16Factory
argument_list|()
return|;
block|}
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|WC_UNSUPPORTED_FORMAT
argument_list|)
argument_list|,
name|SVNLogType
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|private
specifier|static
name|int
name|readFormatVersion
parameter_list|(
name|File
name|adminDir
parameter_list|)
throws|throws
name|SVNException
block|{
name|SVNErrorMessage
name|error
init|=
literal|null
decl_stmt|;
name|int
name|version
init|=
operator|-
literal|1
decl_stmt|;
name|Collection
name|enabledFactories
init|=
name|getSelector
argument_list|()
operator|.
name|getEnabledFactories
argument_list|(
name|adminDir
operator|.
name|getParentFile
argument_list|()
argument_list|,
name|ourFactories
argument_list|,
literal|false
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|factories
init|=
name|enabledFactories
operator|.
name|iterator
argument_list|()
init|;
name|factories
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SVNAdminAreaFactory
name|factory
init|=
operator|(
name|SVNAdminAreaFactory
operator|)
name|factories
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|version
operator|=
name|factory
operator|.
name|getVersion
argument_list|(
name|adminDir
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
name|error
operator|=
name|e
operator|.
name|getErrorMessage
argument_list|()
expr_stmt|;
continue|continue;
block|}
return|return
name|version
return|;
block|}
if|if
condition|(
name|error
operator|==
literal|null
condition|)
block|{
name|error
operator|=
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
name|adminDir
argument_list|)
expr_stmt|;
block|}
name|SVNErrorManager
operator|.
name|error
argument_list|(
name|error
argument_list|,
name|SVNLogType
operator|.
name|WC
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
specifier|public
specifier|static
name|void
name|createVersionedDirectory
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
if|if
condition|(
operator|!
name|ourFactories
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|checkAdminAreaExists
argument_list|(
name|path
argument_list|,
name|url
argument_list|,
name|revNumber
argument_list|)
condition|)
block|{
name|Collection
name|enabledFactories
init|=
name|getSelector
argument_list|()
operator|.
name|getEnabledFactories
argument_list|(
name|path
argument_list|,
name|ourFactories
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|enabledFactories
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SVNAdminAreaFactory
name|newestFactory
init|=
operator|(
name|SVNAdminAreaFactory
operator|)
name|enabledFactories
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
name|newestFactory
operator|.
name|doCreateVersionedDirectory
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
name|depth
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
specifier|static
name|void
name|createVersionedDirectory
parameter_list|(
name|File
name|path
parameter_list|,
name|SVNURL
name|url
parameter_list|,
name|SVNURL
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
name|createVersionedDirectory
argument_list|(
name|path
argument_list|,
name|url
operator|!=
literal|null
condition|?
name|url
operator|.
name|toString
argument_list|()
else|:
literal|null
argument_list|,
name|rootURL
operator|!=
literal|null
condition|?
name|rootURL
operator|.
name|toString
argument_list|()
else|:
literal|null
argument_list|,
name|uuid
argument_list|,
name|revNumber
argument_list|,
name|depth
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|checkAdminAreaExists
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|url
parameter_list|,
name|long
name|revision
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
name|dir
argument_list|,
name|SVNFileUtil
operator|.
name|getAdminDirectoryName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|adminDir
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|adminDir
operator|.
name|isDirectory
argument_list|()
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
name|WC_OBSTRUCTED_UPDATE
argument_list|,
literal|"''{0}'' is not a directory"
argument_list|,
name|dir
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
if|else if
condition|(
operator|!
name|adminDir
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|boolean
name|wcExists
init|=
literal|false
decl_stmt|;
try|try
block|{
name|readFormatVersion
argument_list|(
name|adminDir
argument_list|)
expr_stmt|;
name|wcExists
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|svne
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|wcExists
condition|)
block|{
name|SVNWCAccess
name|wcAccess
init|=
name|SVNWCAccess
operator|.
name|newInstance
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|SVNAdminArea
name|adminArea
init|=
literal|null
decl_stmt|;
name|SVNEntry
name|entry
init|=
literal|null
decl_stmt|;
try|try
block|{
name|adminArea
operator|=
name|wcAccess
operator|.
name|open
argument_list|(
name|dir
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|entry
operator|=
name|adminArea
operator|.
name|getVersionedEntry
argument_list|(
name|adminArea
operator|.
name|getThisDirName
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|wcAccess
operator|.
name|closeAdminArea
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|entry
operator|.
name|isScheduledForDeletion
argument_list|()
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|getRevision
argument_list|()
operator|!=
name|revision
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
name|WC_OBSTRUCTED_UPDATE
argument_list|,
literal|"Revision {0} doesn''t match existing revision {1} in ''{2}''"
argument_list|,
operator|new
name|Object
index|[]
block|{
operator|new
name|Long
argument_list|(
name|revision
argument_list|)
block|,
operator|new
name|Long
argument_list|(
name|entry
operator|.
name|getRevision
argument_list|()
argument_list|)
block|,
name|dir
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
if|if
condition|(
operator|!
name|url
operator|.
name|equals
argument_list|(
name|entry
operator|.
name|getURL
argument_list|()
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
name|WC_OBSTRUCTED_UPDATE
argument_list|,
literal|"URL ''{0}'' doesn''t match existing URL ''{1}'' in ''{2}''"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|url
block|,
name|entry
operator|.
name|getURL
argument_list|()
block|,
name|dir
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
block|}
block|}
return|return
name|wcExists
return|;
block|}
specifier|public
specifier|abstract
name|int
name|getSupportedVersion
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|int
name|getVersion
parameter_list|(
name|File
name|path
parameter_list|)
throws|throws
name|SVNException
function_decl|;
specifier|protected
specifier|abstract
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
function_decl|;
specifier|protected
specifier|abstract
name|SVNAdminArea
name|doChangeWCFormat
parameter_list|(
name|SVNAdminArea
name|area
parameter_list|)
throws|throws
name|SVNException
function_decl|;
specifier|protected
specifier|abstract
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
function_decl|;
specifier|protected
specifier|abstract
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
function_decl|;
specifier|protected
specifier|static
name|void
name|registerFactory
parameter_list|(
name|SVNAdminAreaFactory
name|factory
parameter_list|)
block|{
if|if
condition|(
name|factory
operator|!=
literal|null
condition|)
block|{
name|ourFactories
operator|.
name|add
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
operator|||
operator|!
operator|(
name|o
operator|instanceof
name|SVNAdminAreaFactory
operator|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|version
init|=
operator|(
operator|(
name|SVNAdminAreaFactory
operator|)
name|o
operator|)
operator|.
name|getSupportedVersion
argument_list|()
decl_stmt|;
return|return
name|getSupportedVersion
argument_list|()
operator|>
name|version
condition|?
operator|-
literal|1
else|:
operator|(
name|getSupportedVersion
argument_list|()
operator|<
name|version
operator|)
condition|?
literal|1
else|:
literal|0
return|;
block|}
specifier|private
specifier|static
class|class
name|DefaultSelector
implements|implements
name|ISVNAdminAreaFactorySelector
block|{
specifier|public
name|Collection
name|getEnabledFactories
parameter_list|(
name|File
name|path
parameter_list|,
name|Collection
name|factories
parameter_list|,
name|boolean
name|writeAccess
parameter_list|)
throws|throws
name|SVNException
block|{
return|return
name|factories
return|;
block|}
block|}
block|}
end_class

end_unit

