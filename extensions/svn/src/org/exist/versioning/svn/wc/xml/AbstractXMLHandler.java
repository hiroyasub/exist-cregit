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
name|SVNDebugLog
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
name|Locator
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  *<b>AbstractXMLLogger</b> is a basic XML formatter for all   * XML handler classes which are provided in this package. All   * XML output is written to a specified<b>ContentHandler</b>.  *   * @version 1.3  * @author  TMate Software Ltd.  * @since   1.2  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractXMLHandler
implements|implements
name|Locator
block|{
specifier|private
name|AttributesImpl
name|mySharedAttributes
decl_stmt|;
specifier|private
name|ContentHandler
name|myHandler
decl_stmt|;
specifier|private
name|ISVNDebugLog
name|myLog
decl_stmt|;
specifier|protected
name|AbstractXMLHandler
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|ISVNDebugLog
name|log
parameter_list|)
block|{
name|myHandler
operator|=
name|contentHandler
expr_stmt|;
name|myLog
operator|=
name|log
operator|==
literal|null
condition|?
name|SVNDebugLog
operator|.
name|getDefaultLog
argument_list|()
else|:
name|log
expr_stmt|;
block|}
specifier|protected
name|ISVNDebugLog
name|getDebugLog
parameter_list|()
block|{
return|return
name|myLog
return|;
block|}
comment|/**      * Starts logging.       *      */
specifier|public
name|void
name|startDocument
parameter_list|()
block|{
try|try
block|{
name|getHandler
argument_list|()
operator|.
name|setDocumentLocator
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|getHandler
argument_list|()
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|openTag
argument_list|(
name|getHeaderName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
block|}
block|}
comment|/**      * Stops logging.      *      */
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
try|try
block|{
name|closeTag
argument_list|(
name|getHeaderName
argument_list|()
argument_list|)
expr_stmt|;
name|getHandler
argument_list|()
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
block|}
block|}
specifier|private
name|ContentHandler
name|getHandler
parameter_list|()
block|{
return|return
name|myHandler
return|;
block|}
specifier|protected
specifier|abstract
name|String
name|getHeaderName
parameter_list|()
function_decl|;
specifier|protected
name|void
name|openTag
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|mySharedAttributes
operator|==
literal|null
condition|)
block|{
name|mySharedAttributes
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
block|}
name|getHandler
argument_list|()
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|name
argument_list|,
name|mySharedAttributes
argument_list|)
expr_stmt|;
name|mySharedAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|closeTag
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|getHandler
argument_list|()
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addTag
parameter_list|(
name|String
name|tagName
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|mySharedAttributes
operator|==
literal|null
condition|)
block|{
name|mySharedAttributes
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
block|}
name|getHandler
argument_list|()
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|tagName
argument_list|,
name|mySharedAttributes
argument_list|)
expr_stmt|;
name|mySharedAttributes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|value
operator|=
name|value
operator|==
literal|null
condition|?
literal|""
else|:
name|value
expr_stmt|;
name|getHandler
argument_list|()
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|getHandler
argument_list|()
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|tagName
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|addAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|mySharedAttributes
operator|==
literal|null
condition|)
block|{
name|mySharedAttributes
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
block|}
name|mySharedAttributes
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
name|name
argument_list|,
literal|"CDATA"
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return<code>0</code>      */
specifier|public
name|int
name|getColumnNumber
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * @return<code>0</code>      */
specifier|public
name|int
name|getLineNumber
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/**      * @return<span class="javakeyword">null</span>      */
specifier|public
name|String
name|getPublicId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * @return<span class="javakeyword">null</span>      */
specifier|public
name|String
name|getSystemId
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

