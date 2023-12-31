begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|protocols
operator|.
name|xmldb
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
name|io
operator|.
name|OutputStream
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
name|protocolhandler
operator|.
name|embedded
operator|.
name|InMemoryInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|embedded
operator|.
name|InMemoryOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmldb
operator|.
name|XmldbURL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmlrpc
operator|.
name|XmlrpcInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmlrpc
operator|.
name|XmlrpcOutputStream
import|;
end_import

begin_comment
comment|/**  *  A URLConnection object manages the translation of a URL object into a  * resource stream.  */
end_comment

begin_class
specifier|public
class|class
name|InMemoryURLConnection
extends|extends
name|URLConnection
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|InMemoryURLConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ThreadGroup
name|threadGroup
decl_stmt|;
comment|/**      * Constructs a URL connection to the specified URL.      * @param threadGroup Thread group      * @param url URL      */
specifier|protected
name|InMemoryURLConnection
parameter_list|(
specifier|final
name|ThreadGroup
name|threadGroup
parameter_list|,
specifier|final
name|URL
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadGroup
operator|=
name|threadGroup
expr_stmt|;
name|setDoInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|connect
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"connect: "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|XmldbURL
name|xmldbURL
init|=
operator|new
name|XmldbURL
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|xmldbURL
operator|.
name|isEmbedded
argument_list|()
condition|)
block|{
return|return
name|InMemoryInputStream
operator|.
name|stream
argument_list|(
name|xmldbURL
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|XmlrpcInputStream
argument_list|(
name|threadGroup
argument_list|,
name|xmldbURL
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|OutputStream
name|getOutputStream
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|XmldbURL
name|xmldbURL
init|=
operator|new
name|XmldbURL
argument_list|(
name|url
argument_list|)
decl_stmt|;
if|if
condition|(
name|xmldbURL
operator|.
name|isEmbedded
argument_list|()
condition|)
block|{
return|return
operator|new
name|InMemoryOutputStream
argument_list|(
name|xmldbURL
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|XmlrpcOutputStream
argument_list|(
name|threadGroup
argument_list|,
name|xmldbURL
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

