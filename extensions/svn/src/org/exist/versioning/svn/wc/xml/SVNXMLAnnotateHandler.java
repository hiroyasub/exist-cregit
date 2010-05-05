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
name|wc
operator|.
name|xml
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
name|Date
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
name|wc
operator|.
name|ISVNAnnotateHandler
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
name|ISVNDebugLog
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
name|ContentHandler
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
comment|/**  * This is an implementation of the<b>ISVNAnnotateHandler</b> interface   * that writes XML formatted annotation information to a specified   *<b>ContentHandler</b>.   *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  */
end_comment

begin_class
specifier|public
class|class
name|SVNXMLAnnotateHandler
extends|extends
name|AbstractXMLHandler
implements|implements
name|ISVNAnnotateHandler
block|{
comment|/**      *<code>'path'</code> attribute.      */
specifier|public
specifier|static
specifier|final
name|String
name|PATH_ATTR
init|=
literal|"path"
decl_stmt|;
comment|/**      *<code>'revision'</code> attribute.      */
specifier|public
specifier|static
specifier|final
name|String
name|REVISION_ATTR
init|=
literal|"revision"
decl_stmt|;
comment|/**      *<code>'date'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|DATE_TAG
init|=
literal|"date"
decl_stmt|;
comment|/**      *<code>'author'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|AUTHOR_TAG
init|=
literal|"author"
decl_stmt|;
comment|/**      *<code>'commit'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|COMMIT_TAG
init|=
literal|"commit"
decl_stmt|;
comment|/**      *<code>'entry'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|ENTRY_TAG
init|=
literal|"entry"
decl_stmt|;
comment|/**      *<code>'line-number'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|LINE_NUMBER_TAG
init|=
literal|"line-number"
decl_stmt|;
comment|/**      *<code>'target'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|TARGET_TAG
init|=
literal|"target"
decl_stmt|;
comment|/**      *<code>'blame'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|BLAME_TAG
init|=
literal|"blame"
decl_stmt|;
comment|/**      *<code>'merged'</code> tag.      */
specifier|public
specifier|static
specifier|final
name|String
name|MERGED_TAG
init|=
literal|"merged"
decl_stmt|;
specifier|private
name|long
name|myLineNumber
decl_stmt|;
specifier|private
name|boolean
name|myIsUseMergeHistory
decl_stmt|;
comment|/**      * Creates a new annotation handler.      *       * @param contentHandler a<b>ContentHandler</b> to form       *                       an XML tree      */
specifier|public
name|SVNXMLAnnotateHandler
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|)
block|{
name|this
argument_list|(
name|contentHandler
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new annotation handler.      *       * @param contentHandler a<b>ContentHandler</b> to form       *                       an XML tree      * @param log            a debug logger      */
specifier|public
name|SVNXMLAnnotateHandler
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|ISVNDebugLog
name|log
parameter_list|)
block|{
name|this
argument_list|(
name|contentHandler
argument_list|,
name|log
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new annotation handler.      *       * @param contentHandler     a<b>ContentHandler</b> to form       *                           an XML tree      * @param log                a debug logger      * @param isUseMergeHistory  whether merge history should be taken into account or not      */
specifier|public
name|SVNXMLAnnotateHandler
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|ISVNDebugLog
name|log
parameter_list|,
name|boolean
name|isUseMergeHistory
parameter_list|)
block|{
name|super
argument_list|(
name|contentHandler
argument_list|,
name|log
argument_list|)
expr_stmt|;
name|myIsUseMergeHistory
operator|=
name|isUseMergeHistory
expr_stmt|;
block|}
specifier|protected
name|String
name|getHeaderName
parameter_list|()
block|{
return|return
name|BLAME_TAG
return|;
block|}
comment|/**      * Begins an XML tree with the target path/URL for which       * annotating is run.      *        * @param pathOrURL a target file WC path or URL       */
specifier|public
name|void
name|startTarget
parameter_list|(
name|String
name|pathOrURL
parameter_list|)
block|{
name|myLineNumber
operator|=
literal|1
expr_stmt|;
try|try
block|{
name|addAttribute
argument_list|(
name|PATH_ATTR
argument_list|,
name|pathOrURL
argument_list|)
expr_stmt|;
name|openTag
argument_list|(
name|TARGET_TAG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|getDebugLog
argument_list|()
operator|.
name|logSevere
argument_list|(
name|SVNLogType
operator|.
name|DEFAULT
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Closes the formatted XML output.       *      */
specifier|public
name|void
name|endTarget
parameter_list|()
block|{
name|myLineNumber
operator|=
literal|1
expr_stmt|;
try|try
block|{
name|closeTag
argument_list|(
name|TARGET_TAG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|getDebugLog
argument_list|()
operator|.
name|logSevere
argument_list|(
name|SVNLogType
operator|.
name|DEFAULT
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Handles line annotation producing corresponding xml tags.      *       * @param date       * @param revision       * @param author       * @param line       * @throws SVNException       */
specifier|public
name|void
name|handleLine
parameter_list|(
name|Date
name|date
parameter_list|,
name|long
name|revision
parameter_list|,
name|String
name|author
parameter_list|,
name|String
name|line
parameter_list|)
throws|throws
name|SVNException
block|{
try|try
block|{
name|addAttribute
argument_list|(
name|LINE_NUMBER_TAG
argument_list|,
name|myLineNumber
operator|+
literal|""
argument_list|)
expr_stmt|;
name|openTag
argument_list|(
name|ENTRY_TAG
argument_list|)
expr_stmt|;
if|if
condition|(
name|revision
operator|>=
literal|0
condition|)
block|{
name|addAttribute
argument_list|(
name|REVISION_ATTR
argument_list|,
name|revision
operator|+
literal|""
argument_list|)
expr_stmt|;
name|openTag
argument_list|(
name|COMMIT_TAG
argument_list|)
expr_stmt|;
name|addTag
argument_list|(
name|AUTHOR_TAG
argument_list|,
name|author
argument_list|)
expr_stmt|;
name|addTag
argument_list|(
name|DATE_TAG
argument_list|,
name|SVNDate
operator|.
name|formatDate
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
name|closeTag
argument_list|(
name|COMMIT_TAG
argument_list|)
expr_stmt|;
block|}
name|closeTag
argument_list|(
name|ENTRY_TAG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|getDebugLog
argument_list|()
operator|.
name|logSevere
argument_list|(
name|SVNLogType
operator|.
name|DEFAULT
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|XML_MALFORMED
argument_list|,
name|e
operator|.
name|getLocalizedMessage
argument_list|()
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
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|myLineNumber
operator|++
expr_stmt|;
block|}
block|}
comment|/**      * Handles line annotation producing corresponding xml tags.      *       * @param date       * @param revision       * @param author       * @param line       * @param mergedDate       * @param mergedRevision       * @param mergedAuthor       * @param mergedPath       * @param lineNumber       * @throws SVNException       */
specifier|public
name|void
name|handleLine
parameter_list|(
name|Date
name|date
parameter_list|,
name|long
name|revision
parameter_list|,
name|String
name|author
parameter_list|,
name|String
name|line
parameter_list|,
name|Date
name|mergedDate
parameter_list|,
name|long
name|mergedRevision
parameter_list|,
name|String
name|mergedAuthor
parameter_list|,
name|String
name|mergedPath
parameter_list|,
name|int
name|lineNumber
parameter_list|)
throws|throws
name|SVNException
block|{
try|try
block|{
name|addAttribute
argument_list|(
name|LINE_NUMBER_TAG
argument_list|,
operator|++
name|lineNumber
operator|+
literal|""
argument_list|)
expr_stmt|;
name|openTag
argument_list|(
name|ENTRY_TAG
argument_list|)
expr_stmt|;
if|if
condition|(
name|revision
operator|>=
literal|0
condition|)
block|{
name|addAttribute
argument_list|(
name|REVISION_ATTR
argument_list|,
name|revision
operator|+
literal|""
argument_list|)
expr_stmt|;
name|openTag
argument_list|(
name|COMMIT_TAG
argument_list|)
expr_stmt|;
name|addTag
argument_list|(
name|AUTHOR_TAG
argument_list|,
name|author
argument_list|)
expr_stmt|;
name|addTag
argument_list|(
name|DATE_TAG
argument_list|,
name|SVNDate
operator|.
name|formatDate
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
name|closeTag
argument_list|(
name|COMMIT_TAG
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|myIsUseMergeHistory
operator|&&
name|mergedRevision
operator|>=
literal|0
condition|)
block|{
name|addAttribute
argument_list|(
name|PATH_ATTR
argument_list|,
name|mergedPath
argument_list|)
expr_stmt|;
name|openTag
argument_list|(
name|MERGED_TAG
argument_list|)
expr_stmt|;
name|addAttribute
argument_list|(
name|REVISION_ATTR
argument_list|,
name|mergedRevision
operator|+
literal|""
argument_list|)
expr_stmt|;
name|openTag
argument_list|(
name|COMMIT_TAG
argument_list|)
expr_stmt|;
name|addTag
argument_list|(
name|AUTHOR_TAG
argument_list|,
name|mergedAuthor
argument_list|)
expr_stmt|;
name|addTag
argument_list|(
name|DATE_TAG
argument_list|,
name|SVNDate
operator|.
name|formatDate
argument_list|(
name|mergedDate
argument_list|)
argument_list|)
expr_stmt|;
name|closeTag
argument_list|(
name|COMMIT_TAG
argument_list|)
expr_stmt|;
name|closeTag
argument_list|(
name|MERGED_TAG
argument_list|)
expr_stmt|;
block|}
name|closeTag
argument_list|(
name|ENTRY_TAG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|getDebugLog
argument_list|()
operator|.
name|logSevere
argument_list|(
name|SVNLogType
operator|.
name|DEFAULT
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|SVNErrorMessage
name|err
init|=
name|SVNErrorMessage
operator|.
name|create
argument_list|(
name|SVNErrorCode
operator|.
name|XML_MALFORMED
argument_list|,
name|e
operator|.
name|getLocalizedMessage
argument_list|()
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
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Just returns<span class="javakeyword">false</span>.      * @param date       * @param revision       * @param author       * @param contents       * @return<span class="javakeyword">false</span>      * @throws SVNException       */
specifier|public
name|boolean
name|handleRevision
parameter_list|(
name|Date
name|date
parameter_list|,
name|long
name|revision
parameter_list|,
name|String
name|author
parameter_list|,
name|File
name|contents
parameter_list|)
throws|throws
name|SVNException
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Does nothing.      */
specifier|public
name|void
name|handleEOF
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

