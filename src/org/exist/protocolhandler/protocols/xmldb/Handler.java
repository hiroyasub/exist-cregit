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
name|java
operator|.
name|net
operator|.
name|URLStreamHandler
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
name|Mode
import|;
end_import

begin_comment
comment|/**  *  A stream protocol handler knows how to make a connection for a particular  * protocol type. This handler deals with "xmldb:"  *  * @author Dannes Wessels  *  * @see<A HREF="http://java.sun.com/developer/onlineTraining/protocolhandlers/"  *>A New Era for Java Protocol Handlers</A>  * @see java.net.URLStreamHandler  */
end_comment

begin_class
specifier|public
class|class
name|Handler
extends|extends
name|URLStreamHandler
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
name|Handler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadGroup
name|threadGroup
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"exist.url-stream-handler"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|XMLDB_EXIST
init|=
literal|"xmldb:exist:"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|XMLDB
init|=
literal|"xmldb:"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN
init|=
literal|"xmldb:[\\w]+:\\/\\/.*"
decl_stmt|;
specifier|private
specifier|final
name|Mode
name|mode
decl_stmt|;
comment|/**      * Creates a new instance of Handler      */
specifier|public
name|Handler
parameter_list|(
specifier|final
name|Mode
name|mode
parameter_list|)
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
literal|"Setup \"xmldb:\" handler"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
comment|/**      * @see java.net.URLStreamHandler#parseURL(java.net.URL,java.lang.String,int,int)      *      * TODO: exist instance names must be supported. The idea is to pass      * this information as a parameter to the url, format __instance=XXXXX      * Should we clean all other params? remove #?      */
annotation|@
name|Override
specifier|protected
name|void
name|parseURL
parameter_list|(
specifier|final
name|URL
name|url
parameter_list|,
specifier|final
name|String
name|spec
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|limit
parameter_list|)
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
name|spec
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|spec
operator|.
name|startsWith
argument_list|(
name|XMLDB_EXIST
operator|+
literal|"//"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing xmldb:exist:// URL."
argument_list|)
expr_stmt|;
name|super
operator|.
name|parseURL
argument_list|(
name|url
argument_list|,
name|spec
argument_list|,
name|XMLDB_EXIST
operator|.
name|length
argument_list|()
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|spec
operator|.
name|startsWith
argument_list|(
name|XMLDB
operator|+
literal|"//"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing xmldb:// URL."
argument_list|)
expr_stmt|;
name|super
operator|.
name|parseURL
argument_list|(
name|url
argument_list|,
name|spec
argument_list|,
name|XMLDB
operator|.
name|length
argument_list|()
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|spec
operator|.
name|startsWith
argument_list|(
name|XMLDB
operator|+
literal|"/"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing xmldb:/ URL."
argument_list|)
expr_stmt|;
name|super
operator|.
name|parseURL
argument_list|(
name|url
argument_list|,
name|spec
argument_list|,
name|XMLDB
operator|.
name|length
argument_list|()
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|spec
operator|.
name|matches
argument_list|(
name|PATTERN
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing URL with custom exist instance"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|separator
init|=
name|spec
operator|.
name|indexOf
argument_list|(
literal|"//"
argument_list|)
decl_stmt|;
name|super
operator|.
name|parseURL
argument_list|(
name|url
argument_list|,
name|spec
argument_list|,
name|separator
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|spec
operator|.
name|startsWith
argument_list|(
literal|"xmldb:://"
argument_list|)
condition|)
block|{
comment|// very dirty
specifier|final
name|int
name|separator
init|=
name|spec
operator|.
name|indexOf
argument_list|(
literal|"//"
argument_list|)
decl_stmt|;
name|super
operator|.
name|parseURL
argument_list|(
name|url
argument_list|,
name|spec
argument_list|,
name|separator
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|spec
operator|.
name|startsWith
argument_list|(
literal|"xmldb:/"
argument_list|)
condition|)
block|{
name|super
operator|.
name|parseURL
argument_list|(
name|url
argument_list|,
name|spec
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Expected 'xmldb:'-like URL, found "
operator|+
name|spec
argument_list|)
expr_stmt|;
name|super
operator|.
name|parseURL
argument_list|(
name|url
argument_list|,
name|spec
argument_list|,
name|start
argument_list|,
name|limit
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|URLConnection
name|openConnection
parameter_list|(
specifier|final
name|URL
name|u
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|mode
condition|)
block|{
case|case
name|THREADS
case|:
case|case
name|DISK
case|:
return|return
operator|new
name|EmbeddedURLConnection
argument_list|(
name|threadGroup
argument_list|,
name|u
argument_list|)
return|;
case|case
name|MEMORY
case|:
return|return
operator|new
name|InMemoryURLConnection
argument_list|(
name|threadGroup
argument_list|,
name|u
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IOException
argument_list|(
literal|"unsupported mode "
operator|+
name|mode
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

