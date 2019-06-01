begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2012 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|plugin
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Utils
block|{
specifier|public
specifier|static
name|boolean
name|fileCanContainClasses
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|boolean
name|can
init|=
literal|false
decl_stmt|;
specifier|final
name|String
name|fileName
init|=
name|file
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|can
operator|=
operator|(
name|isJar
argument_list|(
name|fileName
argument_list|)
operator|||
name|isZip
argument_list|(
name|fileName
argument_list|)
operator|||
name|file
operator|.
name|isDirectory
argument_list|()
operator|)
expr_stmt|;
block|}
return|return
name|can
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isJar
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
return|return
name|fileName
operator|.
name|toLowerCase
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".jar"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isZip
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
return|return
name|fileName
operator|.
name|toLowerCase
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
return|;
block|}
block|}
end_class

end_unit
