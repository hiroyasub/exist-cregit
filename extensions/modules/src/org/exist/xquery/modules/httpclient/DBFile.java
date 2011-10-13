begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|httpclient
package|;
end_package

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
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URLConnection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|methods
operator|.
name|multipart
operator|.
name|PartSource
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DBFile
implements|implements
name|PartSource
block|{
specifier|private
name|XmldbURI
name|uri
decl_stmt|;
specifier|private
name|URLConnection
name|connection
init|=
literal|null
decl_stmt|;
specifier|public
name|DBFile
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|private
name|URLConnection
name|getConnection
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|connection
operator|=
name|url
operator|.
name|openConnection
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
return|return
name|connection
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.commons.httpclient.methods.multipart.PartSource#createInputStream() 	 */
specifier|public
name|InputStream
name|createInputStream
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getInputStream
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.commons.httpclient.methods.multipart.PartSource#getFileName() 	 */
specifier|public
name|String
name|getFileName
parameter_list|()
block|{
return|return
name|uri
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.commons.httpclient.methods.multipart.PartSource#getLength() 	 */
specifier|public
name|long
name|getLength
parameter_list|()
block|{
try|try
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getContentLength
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
block|}
block|}
end_class

end_unit

