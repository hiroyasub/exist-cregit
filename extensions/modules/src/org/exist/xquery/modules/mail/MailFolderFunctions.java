begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Mail Module Extension MailFolderFunctions  *  Copyright (C) 2006-09 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|mail
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|mail
operator|.
name|Folder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|mail
operator|.
name|MessagingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|mail
operator|.
name|Store
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
name|dom
operator|.
name|QName
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XQueryContext
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
name|BooleanValue
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|IntegerValue
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
name|SequenceType
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
name|Type
import|;
end_import

begin_comment
comment|/**  * eXist Mail Module Extension GetFolder  *   * Get a mail store  *   * @author Andrzej Taramina<andrzej@chaeron.com>  * @serial 2009-03-12  * @version 1.3  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|MailFolderFunctions
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|MailFolderFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-mail-folder"
argument_list|,
name|MailModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MailModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Opens a mail folder."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"mail-store-handle"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The mail store handle retrieved from mail:get-mail-store()"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"foldername"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the folder to open"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"an xs:long representing the folder handle."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"close-mail-folder"
argument_list|,
name|MailModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MailModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Closes a mail folder."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"mail-folder-handle"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The mail folder handle retrieved from mail:get-mail-folder()"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"expunge"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A boolean that specifies whether to expunge the folder on close."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * MailFolderFunctions Constructor 	 *  	 * @param context	The Context of the calling XQuery 	 */
specifier|public
name|MailFolderFunctions
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * evaluate the call to the xquery get-folder function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the get-folder() function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		A sequence representing the result of the get-folder() function call 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"get-mail-folder"
argument_list|)
condition|)
block|{
return|return
name|getMailFolder
argument_list|(
name|args
argument_list|,
name|contextSequence
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"close-mail-folder"
argument_list|)
condition|)
block|{
return|return
name|closeMailFolder
argument_list|(
name|args
argument_list|,
name|contextSequence
argument_list|)
return|;
block|}
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Invalid function name"
argument_list|)
operator|)
throw|;
block|}
specifier|private
name|Sequence
name|getMailFolder
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Folder
name|folder
decl_stmt|;
comment|// was a store handle specified?
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
operator|||
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Store handle and/or folder name not specified"
argument_list|)
operator|)
throw|;
block|}
comment|// get the Store
name|long
name|storeHandle
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|Store
name|store
init|=
name|MailModule
operator|.
name|retrieveStore
argument_list|(
name|context
argument_list|,
name|storeHandle
argument_list|)
decl_stmt|;
if|if
condition|(
name|store
operator|==
literal|null
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Invalid Store handle specified"
argument_list|)
operator|)
throw|;
block|}
comment|// get the Folder Name
name|String
name|name
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
name|folder
operator|=
name|store
operator|.
name|getFolder
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|folder
operator|.
name|open
argument_list|(
name|Folder
operator|.
name|READ_WRITE
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessagingException
name|me
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to open mail folder"
argument_list|,
name|me
argument_list|)
operator|)
throw|;
block|}
comment|// save the folder and return the handle of the folder
return|return
operator|(
operator|new
name|IntegerValue
argument_list|(
name|MailModule
operator|.
name|storeFolder
argument_list|(
name|context
argument_list|,
name|folder
argument_list|)
argument_list|)
operator|)
return|;
block|}
specifier|private
name|Sequence
name|closeMailFolder
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// was a folder handle specified?
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Folder handle not specified"
argument_list|)
operator|)
throw|;
block|}
name|boolean
name|expunge
init|=
operator|(
operator|(
name|BooleanValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
comment|// get the Folder
name|long
name|folderHandle
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
decl_stmt|;
name|Folder
name|folder
init|=
name|MailModule
operator|.
name|retrieveFolder
argument_list|(
name|context
argument_list|,
name|folderHandle
argument_list|)
decl_stmt|;
if|if
condition|(
name|folder
operator|==
literal|null
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Invalid Folder handle specified"
argument_list|)
operator|)
throw|;
block|}
try|try
block|{
name|folder
operator|.
name|close
argument_list|(
name|expunge
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MessagingException
name|me
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to close mail folder"
argument_list|,
name|me
argument_list|)
operator|)
throw|;
block|}
finally|finally
block|{
name|MailModule
operator|.
name|removeFolder
argument_list|(
name|context
argument_list|,
name|folderHandle
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
end_class

end_unit

