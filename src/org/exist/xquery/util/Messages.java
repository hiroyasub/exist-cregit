begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|MissingResourceException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ResourceBundle
import|;
end_import

begin_comment
comment|/**  * Reads error messages from a {@link java.util.ResourceBundle} and  * provides shorthand methods to format the error message using the  * message arguments passed.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Messages
block|{
comment|/**      * The base name of the messages file.      */
specifier|public
specifier|static
specifier|final
name|String
name|BASE_NAME
init|=
literal|"org.exist.xquery.util.messages"
decl_stmt|;
specifier|public
specifier|static
name|String
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|)
block|{
return|return
name|formatMessage
argument_list|(
name|messageId
argument_list|,
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|,
name|Object
name|arg0
parameter_list|)
block|{
return|return
name|formatMessage
argument_list|(
name|messageId
argument_list|,
operator|new
name|Object
index|[]
block|{
name|arg0
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{
return|return
name|formatMessage
argument_list|(
name|messageId
argument_list|,
operator|new
name|Object
index|[]
block|{
name|arg0
block|,
name|arg1
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|)
block|{
return|return
name|formatMessage
argument_list|(
name|messageId
argument_list|,
operator|new
name|Object
index|[]
block|{
name|arg0
block|,
name|arg1
block|,
name|arg2
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getMessage
parameter_list|(
name|String
name|messageId
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|,
name|Object
name|arg3
parameter_list|)
block|{
return|return
name|formatMessage
argument_list|(
name|messageId
argument_list|,
operator|new
name|Object
index|[]
block|{
name|arg0
block|,
name|arg1
block|,
name|arg2
block|,
name|arg3
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|formatMessage
parameter_list|(
name|String
name|messageId
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
name|args
index|[
name|i
index|]
operator|=
name|args
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|args
index|[
name|i
index|]
operator|=
literal|""
expr_stmt|;
block|}
block|}
specifier|final
name|ResourceBundle
name|bundle
init|=
name|getBundle
argument_list|(
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|String
name|message
init|=
name|bundle
operator|.
name|getString
argument_list|(
name|messageId
argument_list|)
decl_stmt|;
return|return
name|MessageFormat
operator|.
name|format
argument_list|(
name|message
argument_list|,
name|args
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|ResourceBundle
name|getBundle
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
try|try
block|{
return|return
name|ResourceBundle
operator|.
name|getBundle
argument_list|(
name|BASE_NAME
argument_list|,
name|locale
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MissingResourceException
name|e
parameter_list|)
block|{
return|return
name|ResourceBundle
operator|.
name|getBundle
argument_list|(
name|BASE_NAME
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

