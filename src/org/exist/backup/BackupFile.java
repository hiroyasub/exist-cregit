begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
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
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_class
specifier|public
class|class
name|BackupFile
block|{
specifier|private
name|File
name|file
decl_stmt|;
specifier|private
name|Date
name|date
decl_stmt|;
specifier|public
name|BackupFile
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|dateTime
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
try|try
block|{
name|date
operator|=
name|BackupDirectory
operator|.
name|DATE_FORMAT
operator|.
name|parse
argument_list|(
name|dateTime
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
block|}
block|}
specifier|public
name|boolean
name|after
parameter_list|(
name|BackupFile
name|other
parameter_list|)
block|{
return|return
name|date
operator|.
name|after
argument_list|(
name|other
operator|.
name|date
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|after
parameter_list|(
name|long
name|time
parameter_list|)
block|{
return|return
name|date
operator|.
name|getTime
argument_list|()
operator|>
name|time
return|;
block|}
specifier|public
name|boolean
name|before
parameter_list|(
name|BackupFile
name|other
parameter_list|)
block|{
return|return
name|date
operator|.
name|before
argument_list|(
name|other
operator|.
name|date
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|before
parameter_list|(
name|long
name|time
parameter_list|)
block|{
return|return
name|time
operator|>
name|date
operator|.
name|getTime
argument_list|()
return|;
block|}
specifier|public
name|File
name|getFile
parameter_list|()
block|{
return|return
name|file
return|;
block|}
specifier|public
name|Date
name|getDate
parameter_list|()
block|{
return|return
name|date
return|;
block|}
block|}
end_class

end_unit

