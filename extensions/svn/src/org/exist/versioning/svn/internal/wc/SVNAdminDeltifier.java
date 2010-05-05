begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ====================================================================  * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.  *  * This software is licensed as described in the file COPYING, which  * you should have received as part of this distribution.  The terms  * are also available at http://svnkit.com/license.html.  * If newer versions of this license are posted there, you may use a  * newer version instead, at your option.  * ====================================================================  */
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
package|;
end_package

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
name|Map
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
name|SVNRevisionProperty
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
name|delta
operator|.
name|SVNDeltaCombiner
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
name|io
operator|.
name|fs
operator|.
name|FSEntry
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
name|io
operator|.
name|fs
operator|.
name|FSFS
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
name|io
operator|.
name|fs
operator|.
name|FSRepositoryUtil
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
name|io
operator|.
name|fs
operator|.
name|FSRevisionNode
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
name|io
operator|.
name|fs
operator|.
name|FSRevisionRoot
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
name|SVNDeltaGenerator
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
name|SVNRevision
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
name|SVNAdminDeltifier
block|{
specifier|private
name|FSFS
name|myFSFS
decl_stmt|;
specifier|private
name|SVNDepth
name|myDepth
decl_stmt|;
specifier|private
name|boolean
name|myIsIncludeEntryProperties
decl_stmt|;
specifier|private
name|boolean
name|myIsIgnoreAncestry
decl_stmt|;
specifier|private
name|boolean
name|myIsSendTextDeltas
decl_stmt|;
specifier|private
name|ISVNEditor
name|myEditor
decl_stmt|;
specifier|private
name|SVNDeltaCombiner
name|myDeltaCombiner
decl_stmt|;
specifier|private
name|SVNDeltaGenerator
name|myDeltaGenerator
decl_stmt|;
specifier|public
name|SVNAdminDeltifier
parameter_list|(
name|FSFS
name|fsfs
parameter_list|,
name|SVNDepth
name|depth
parameter_list|,
name|boolean
name|includeEntryProperties
parameter_list|,
name|boolean
name|ignoreAncestry
parameter_list|,
name|boolean
name|sendTextDeltas
parameter_list|,
name|ISVNEditor
name|editor
parameter_list|)
block|{
name|myFSFS
operator|=
name|fsfs
expr_stmt|;
name|myDepth
operator|=
name|depth
expr_stmt|;
name|myIsIncludeEntryProperties
operator|=
name|includeEntryProperties
expr_stmt|;
name|myIsIgnoreAncestry
operator|=
name|ignoreAncestry
expr_stmt|;
name|myIsSendTextDeltas
operator|=
name|sendTextDeltas
expr_stmt|;
name|myEditor
operator|=
name|editor
expr_stmt|;
name|myDeltaCombiner
operator|=
operator|new
name|SVNDeltaCombiner
argument_list|()
expr_stmt|;
name|myDeltaGenerator
operator|=
operator|new
name|SVNDeltaGenerator
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setEditor
parameter_list|(
name|ISVNEditor
name|editor
parameter_list|)
block|{
name|myEditor
operator|=
name|editor
expr_stmt|;
block|}
specifier|public
name|void
name|deltifyDir
parameter_list|(
name|FSRevisionRoot
name|srcRoot
parameter_list|,
name|String
name|srcParentDir
parameter_list|,
name|String
name|srcEntry
parameter_list|,
name|FSRevisionRoot
name|tgtRoot
parameter_list|,
name|String
name|tgtFullPath
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|srcParentDir
operator|==
literal|null
condition|)
block|{
name|generateNotADirError
argument_list|(
literal|"source parent"
argument_list|,
name|srcParentDir
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tgtFullPath
operator|==
literal|null
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
name|FS_PATH_SYNTAX
argument_list|,
literal|"Invalid target path"
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
name|FSFS
argument_list|)
expr_stmt|;
block|}
name|String
name|srcFullPath
init|=
name|SVNPathUtil
operator|.
name|getAbsolutePath
argument_list|(
name|SVNPathUtil
operator|.
name|append
argument_list|(
name|srcParentDir
argument_list|,
name|srcEntry
argument_list|)
argument_list|)
decl_stmt|;
name|SVNNodeKind
name|tgtKind
init|=
name|tgtRoot
operator|.
name|checkNodeKind
argument_list|(
name|tgtFullPath
argument_list|)
decl_stmt|;
name|SVNNodeKind
name|srcKind
init|=
name|srcRoot
operator|.
name|checkNodeKind
argument_list|(
name|srcFullPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|tgtKind
operator|==
name|SVNNodeKind
operator|.
name|NONE
operator|&&
name|srcKind
operator|==
name|SVNNodeKind
operator|.
name|NONE
condition|)
block|{
name|myEditor
operator|.
name|closeEdit
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|srcEntry
operator|==
literal|null
operator|&&
operator|(
name|srcKind
operator|!=
name|SVNNodeKind
operator|.
name|DIR
operator|||
name|tgtKind
operator|!=
name|SVNNodeKind
operator|.
name|DIR
operator|)
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
name|FS_PATH_SYNTAX
argument_list|,
literal|"Invalid editor anchoring; at least one "
operator|+
literal|"of the input paths is not a directory "
operator|+
literal|"and there was no source entry"
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
name|FSFS
argument_list|)
expr_stmt|;
block|}
name|myEditor
operator|.
name|targetRevision
argument_list|(
name|tgtRoot
operator|.
name|getRevision
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|rootRevision
init|=
name|srcRoot
operator|.
name|getRevision
argument_list|()
decl_stmt|;
if|if
condition|(
name|tgtKind
operator|==
name|SVNNodeKind
operator|.
name|NONE
condition|)
block|{
name|myEditor
operator|.
name|openRoot
argument_list|(
name|rootRevision
argument_list|)
expr_stmt|;
name|myEditor
operator|.
name|deleteEntry
argument_list|(
name|srcEntry
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|myEditor
operator|.
name|closeDir
argument_list|()
expr_stmt|;
name|myEditor
operator|.
name|closeEdit
argument_list|()
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|srcKind
operator|==
name|SVNNodeKind
operator|.
name|NONE
condition|)
block|{
name|myEditor
operator|.
name|openRoot
argument_list|(
name|rootRevision
argument_list|)
expr_stmt|;
name|addFileOrDir
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|tgtFullPath
argument_list|,
name|srcEntry
argument_list|,
name|tgtKind
argument_list|)
expr_stmt|;
name|myEditor
operator|.
name|closeDir
argument_list|()
expr_stmt|;
name|myEditor
operator|.
name|closeEdit
argument_list|()
expr_stmt|;
return|return;
block|}
name|FSRevisionNode
name|srcNode
init|=
name|srcRoot
operator|.
name|getRevisionNode
argument_list|(
name|srcFullPath
argument_list|)
decl_stmt|;
name|FSRevisionNode
name|tgtNode
init|=
name|tgtRoot
operator|.
name|getRevisionNode
argument_list|(
name|tgtFullPath
argument_list|)
decl_stmt|;
name|int
name|distance
init|=
name|srcNode
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|tgtNode
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|distance
operator|==
literal|0
condition|)
block|{
name|myEditor
operator|.
name|closeEdit
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|srcEntry
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|srcKind
operator|!=
name|tgtKind
operator|||
name|distance
operator|==
operator|-
literal|1
condition|)
block|{
name|myEditor
operator|.
name|openRoot
argument_list|(
name|rootRevision
argument_list|)
expr_stmt|;
name|myEditor
operator|.
name|deleteEntry
argument_list|(
name|srcEntry
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|addFileOrDir
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|tgtFullPath
argument_list|,
name|srcEntry
argument_list|,
name|tgtKind
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|openRoot
argument_list|(
name|rootRevision
argument_list|)
expr_stmt|;
name|replaceFileOrDir
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|srcFullPath
argument_list|,
name|tgtFullPath
argument_list|,
name|srcEntry
argument_list|,
name|tgtKind
argument_list|)
expr_stmt|;
block|}
name|myEditor
operator|.
name|closeDir
argument_list|()
expr_stmt|;
name|myEditor
operator|.
name|closeEdit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|openRoot
argument_list|(
name|rootRevision
argument_list|)
expr_stmt|;
name|deltifyDirs
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|srcFullPath
argument_list|,
name|tgtFullPath
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|myEditor
operator|.
name|closeDir
argument_list|()
expr_stmt|;
name|myEditor
operator|.
name|closeEdit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addFileOrDir
parameter_list|(
name|FSRevisionRoot
name|srcRoot
parameter_list|,
name|FSRevisionRoot
name|tgtRoot
parameter_list|,
name|String
name|tgtPath
parameter_list|,
name|String
name|editPath
parameter_list|,
name|SVNNodeKind
name|tgtKind
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|tgtKind
operator|==
name|SVNNodeKind
operator|.
name|DIR
condition|)
block|{
name|myEditor
operator|.
name|addDir
argument_list|(
name|editPath
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|deltifyDirs
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
literal|null
argument_list|,
name|tgtPath
argument_list|,
name|editPath
argument_list|)
expr_stmt|;
name|myEditor
operator|.
name|closeDir
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|addFile
argument_list|(
name|editPath
argument_list|,
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|deltifyFiles
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
literal|null
argument_list|,
name|tgtPath
argument_list|,
name|editPath
argument_list|)
expr_stmt|;
name|FSRevisionNode
name|tgtNode
init|=
name|tgtRoot
operator|.
name|getRevisionNode
argument_list|(
name|tgtPath
argument_list|)
decl_stmt|;
name|myEditor
operator|.
name|closeFile
argument_list|(
name|editPath
argument_list|,
name|tgtNode
operator|.
name|getFileMD5Checksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|deltifyDirs
parameter_list|(
name|FSRevisionRoot
name|srcRoot
parameter_list|,
name|FSRevisionRoot
name|tgtRoot
parameter_list|,
name|String
name|srcPath
parameter_list|,
name|String
name|tgtPath
parameter_list|,
name|String
name|editPath
parameter_list|)
throws|throws
name|SVNException
block|{
name|deltifyProperties
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|srcPath
argument_list|,
name|tgtPath
argument_list|,
name|editPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSRevisionNode
name|targetNode
init|=
name|tgtRoot
operator|.
name|getRevisionNode
argument_list|(
name|tgtPath
argument_list|)
decl_stmt|;
name|Map
name|targetEntries
init|=
name|targetNode
operator|.
name|getDirEntries
argument_list|(
name|myFSFS
argument_list|)
decl_stmt|;
name|Map
name|sourceEntries
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|srcPath
operator|!=
literal|null
condition|)
block|{
name|FSRevisionNode
name|sourceNode
init|=
name|srcRoot
operator|.
name|getRevisionNode
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|sourceEntries
operator|=
name|sourceNode
operator|.
name|getDirEntries
argument_list|(
name|myFSFS
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|tgtEntries
init|=
name|targetEntries
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|tgtEntries
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|tgtEntries
operator|.
name|next
argument_list|()
decl_stmt|;
name|FSEntry
name|tgtEntry
init|=
operator|(
name|FSEntry
operator|)
name|targetEntries
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|SVNNodeKind
name|tgtKind
init|=
name|tgtEntry
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|targetFullPath
init|=
name|SVNPathUtil
operator|.
name|getAbsolutePath
argument_list|(
name|SVNPathUtil
operator|.
name|append
argument_list|(
name|tgtPath
argument_list|,
name|tgtEntry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|editFullPath
init|=
name|SVNPathUtil
operator|.
name|getAbsolutePath
argument_list|(
name|SVNPathUtil
operator|.
name|append
argument_list|(
name|editPath
argument_list|,
name|tgtEntry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|sourceEntries
operator|!=
literal|null
operator|&&
name|sourceEntries
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|FSEntry
name|srcEntry
init|=
operator|(
name|FSEntry
operator|)
name|sourceEntries
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|sourceFullPath
init|=
name|SVNPathUtil
operator|.
name|getAbsolutePath
argument_list|(
name|SVNPathUtil
operator|.
name|append
argument_list|(
name|srcPath
argument_list|,
name|tgtEntry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|SVNNodeKind
name|srcKind
init|=
name|srcEntry
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|myDepth
operator|==
name|SVNDepth
operator|.
name|INFINITY
operator|||
name|srcKind
operator|!=
name|SVNNodeKind
operator|.
name|DIR
condition|)
block|{
name|int
name|distance
init|=
name|srcEntry
operator|.
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|tgtEntry
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcKind
operator|!=
name|tgtKind
operator|||
operator|(
name|distance
operator|==
operator|-
literal|1
operator|&&
operator|!
name|myIsIgnoreAncestry
operator|)
condition|)
block|{
name|myEditor
operator|.
name|deleteEntry
argument_list|(
name|editFullPath
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|addFileOrDir
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|targetFullPath
argument_list|,
name|editFullPath
argument_list|,
name|tgtKind
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|distance
operator|!=
literal|0
condition|)
block|{
name|replaceFileOrDir
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|sourceFullPath
argument_list|,
name|targetFullPath
argument_list|,
name|editFullPath
argument_list|,
name|tgtKind
argument_list|)
expr_stmt|;
block|}
block|}
name|sourceEntries
operator|.
name|remove
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|myDepth
operator|==
name|SVNDepth
operator|.
name|INFINITY
operator|||
name|tgtKind
operator|!=
name|SVNNodeKind
operator|.
name|DIR
condition|)
block|{
name|addFileOrDir
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|targetFullPath
argument_list|,
name|editFullPath
argument_list|,
name|tgtKind
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|sourceEntries
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|srcEntries
init|=
name|sourceEntries
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|srcEntries
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|srcEntries
operator|.
name|next
argument_list|()
decl_stmt|;
name|FSEntry
name|srcEntry
init|=
operator|(
name|FSEntry
operator|)
name|sourceEntries
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|editFullPath
init|=
name|SVNPathUtil
operator|.
name|getAbsolutePath
argument_list|(
name|SVNPathUtil
operator|.
name|append
argument_list|(
name|editPath
argument_list|,
name|srcEntry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|myDepth
operator|==
name|SVNDepth
operator|.
name|INFINITY
operator|||
name|srcEntry
operator|.
name|getType
argument_list|()
operator|!=
name|SVNNodeKind
operator|.
name|DIR
condition|)
block|{
name|myEditor
operator|.
name|deleteEntry
argument_list|(
name|editFullPath
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|replaceFileOrDir
parameter_list|(
name|FSRevisionRoot
name|srcRoot
parameter_list|,
name|FSRevisionRoot
name|tgtRoot
parameter_list|,
name|String
name|srcPath
parameter_list|,
name|String
name|tgtPath
parameter_list|,
name|String
name|editPath
parameter_list|,
name|SVNNodeKind
name|tgtKind
parameter_list|)
throws|throws
name|SVNException
block|{
name|long
name|baseRevision
init|=
name|srcRoot
operator|.
name|getRevision
argument_list|()
decl_stmt|;
if|if
condition|(
name|tgtKind
operator|==
name|SVNNodeKind
operator|.
name|DIR
condition|)
block|{
name|myEditor
operator|.
name|openDir
argument_list|(
name|editPath
argument_list|,
name|baseRevision
argument_list|)
expr_stmt|;
name|deltifyDirs
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|srcPath
argument_list|,
name|tgtPath
argument_list|,
name|editPath
argument_list|)
expr_stmt|;
name|myEditor
operator|.
name|closeDir
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|openFile
argument_list|(
name|editPath
argument_list|,
name|baseRevision
argument_list|)
expr_stmt|;
name|deltifyFiles
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|srcPath
argument_list|,
name|tgtPath
argument_list|,
name|editPath
argument_list|)
expr_stmt|;
name|FSRevisionNode
name|tgtNode
init|=
name|tgtRoot
operator|.
name|getRevisionNode
argument_list|(
name|tgtPath
argument_list|)
decl_stmt|;
name|myEditor
operator|.
name|closeFile
argument_list|(
name|editPath
argument_list|,
name|tgtNode
operator|.
name|getFileMD5Checksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|deltifyFiles
parameter_list|(
name|FSRevisionRoot
name|srcRoot
parameter_list|,
name|FSRevisionRoot
name|tgtRoot
parameter_list|,
name|String
name|srcPath
parameter_list|,
name|String
name|tgtPath
parameter_list|,
name|String
name|editPath
parameter_list|)
throws|throws
name|SVNException
block|{
name|deltifyProperties
argument_list|(
name|srcRoot
argument_list|,
name|tgtRoot
argument_list|,
name|srcPath
argument_list|,
name|tgtPath
argument_list|,
name|editPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|boolean
name|changed
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|srcPath
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|myIsIgnoreAncestry
condition|)
block|{
name|changed
operator|=
name|FSRepositoryUtil
operator|.
name|checkFilesDifferent
argument_list|(
name|srcRoot
argument_list|,
name|srcPath
argument_list|,
name|tgtRoot
argument_list|,
name|tgtPath
argument_list|,
name|myDeltaCombiner
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|changed
operator|=
name|FSRepositoryUtil
operator|.
name|areFileContentsChanged
argument_list|(
name|srcRoot
argument_list|,
name|srcPath
argument_list|,
name|tgtRoot
argument_list|,
name|tgtPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|changed
condition|)
block|{
name|String
name|srcHexDigest
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|srcPath
operator|!=
literal|null
condition|)
block|{
name|FSRevisionNode
name|srcNode
init|=
name|srcRoot
operator|.
name|getRevisionNode
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|srcHexDigest
operator|=
name|srcNode
operator|.
name|getFileMD5Checksum
argument_list|()
expr_stmt|;
block|}
name|FSRepositoryUtil
operator|.
name|sendTextDelta
argument_list|(
name|myEditor
argument_list|,
name|editPath
argument_list|,
name|srcPath
argument_list|,
name|srcHexDigest
argument_list|,
name|srcRoot
argument_list|,
name|tgtPath
argument_list|,
name|tgtRoot
argument_list|,
name|myIsSendTextDeltas
argument_list|,
name|myDeltaCombiner
argument_list|,
name|myDeltaGenerator
argument_list|,
name|myFSFS
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|deltifyProperties
parameter_list|(
name|FSRevisionRoot
name|srcRoot
parameter_list|,
name|FSRevisionRoot
name|tgtRoot
parameter_list|,
name|String
name|srcPath
parameter_list|,
name|String
name|tgtPath
parameter_list|,
name|String
name|editPath
parameter_list|,
name|boolean
name|isDir
parameter_list|)
throws|throws
name|SVNException
block|{
if|if
condition|(
name|myIsIncludeEntryProperties
condition|)
block|{
name|FSRevisionNode
name|node
init|=
name|tgtRoot
operator|.
name|getRevisionNode
argument_list|(
name|tgtPath
argument_list|)
decl_stmt|;
name|long
name|committedRevision
init|=
name|node
operator|.
name|getCreatedRevision
argument_list|()
decl_stmt|;
if|if
condition|(
name|SVNRevision
operator|.
name|isValidRevisionNumber
argument_list|(
name|committedRevision
argument_list|)
condition|)
block|{
if|if
condition|(
name|isDir
condition|)
block|{
name|myEditor
operator|.
name|changeDirProperty
argument_list|(
name|SVNProperty
operator|.
name|COMMITTED_REVISION
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|committedRevision
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|changeFileProperty
argument_list|(
name|editPath
argument_list|,
name|SVNProperty
operator|.
name|COMMITTED_REVISION
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|committedRevision
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|SVNProperties
name|revisionProps
init|=
name|myFSFS
operator|.
name|getRevisionProperties
argument_list|(
name|committedRevision
argument_list|)
decl_stmt|;
name|String
name|committedDateStr
init|=
name|revisionProps
operator|.
name|getStringValue
argument_list|(
name|SVNRevisionProperty
operator|.
name|DATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|committedDateStr
operator|!=
literal|null
operator|||
name|srcPath
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isDir
condition|)
block|{
name|myEditor
operator|.
name|changeDirProperty
argument_list|(
name|SVNProperty
operator|.
name|COMMITTED_DATE
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|committedDateStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|changeFileProperty
argument_list|(
name|editPath
argument_list|,
name|SVNProperty
operator|.
name|COMMITTED_DATE
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|committedDateStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|lastAuthor
init|=
name|revisionProps
operator|.
name|getStringValue
argument_list|(
name|SVNRevisionProperty
operator|.
name|AUTHOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastAuthor
operator|!=
literal|null
operator|||
name|srcPath
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isDir
condition|)
block|{
name|myEditor
operator|.
name|changeDirProperty
argument_list|(
name|SVNProperty
operator|.
name|LAST_AUTHOR
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|lastAuthor
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|changeFileProperty
argument_list|(
name|editPath
argument_list|,
name|SVNProperty
operator|.
name|LAST_AUTHOR
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|lastAuthor
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|uuid
init|=
name|myFSFS
operator|.
name|getUUID
argument_list|()
decl_stmt|;
if|if
condition|(
name|isDir
condition|)
block|{
name|myEditor
operator|.
name|changeDirProperty
argument_list|(
name|SVNProperty
operator|.
name|UUID
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|changeFileProperty
argument_list|(
name|editPath
argument_list|,
name|SVNProperty
operator|.
name|UUID
argument_list|,
name|SVNPropertyValue
operator|.
name|create
argument_list|(
name|uuid
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|FSRevisionNode
name|targetNode
init|=
name|tgtRoot
operator|.
name|getRevisionNode
argument_list|(
name|tgtPath
argument_list|)
decl_stmt|;
name|SVNProperties
name|sourceProps
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|srcPath
operator|!=
literal|null
condition|)
block|{
name|FSRevisionNode
name|sourceNode
init|=
name|srcRoot
operator|.
name|getRevisionNode
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|boolean
name|propsChanged
init|=
operator|!
name|FSRepositoryUtil
operator|.
name|arePropertiesEqual
argument_list|(
name|sourceNode
argument_list|,
name|targetNode
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|propsChanged
condition|)
block|{
return|return;
block|}
name|sourceProps
operator|=
name|sourceNode
operator|.
name|getProperties
argument_list|(
name|myFSFS
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sourceProps
operator|=
operator|new
name|SVNProperties
argument_list|()
expr_stmt|;
block|}
name|SVNProperties
name|targetProps
init|=
name|targetNode
operator|.
name|getProperties
argument_list|(
name|myFSFS
argument_list|)
decl_stmt|;
name|SVNProperties
name|propsDiffs
init|=
name|FSRepositoryUtil
operator|.
name|getPropsDiffs
argument_list|(
name|sourceProps
argument_list|,
name|targetProps
argument_list|)
decl_stmt|;
name|Object
index|[]
name|names
init|=
name|propsDiffs
operator|.
name|nameSet
argument_list|()
operator|.
name|toArray
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|propName
init|=
operator|(
name|String
operator|)
name|names
index|[
name|i
index|]
decl_stmt|;
name|SVNPropertyValue
name|propValue
init|=
name|propsDiffs
operator|.
name|getSVNPropertyValue
argument_list|(
name|propName
argument_list|)
decl_stmt|;
if|if
condition|(
name|isDir
condition|)
block|{
name|myEditor
operator|.
name|changeDirProperty
argument_list|(
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|myEditor
operator|.
name|changeFileProperty
argument_list|(
name|editPath
argument_list|,
name|propName
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|generateNotADirError
parameter_list|(
name|String
name|role
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|SVNException
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
name|FS_NOT_DIRECTORY
argument_list|,
literal|"Invalid {0} directory ''{1}''"
argument_list|,
operator|new
name|Object
index|[]
block|{
name|role
block|,
name|path
operator|!=
literal|null
condition|?
name|path
else|:
literal|"(null)"
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
name|FSFS
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

