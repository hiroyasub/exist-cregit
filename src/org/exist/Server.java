begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id$  */
end_comment

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|secure
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmlrpc
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  Main class to start the stand-alone server. By default,  *  an XML-RPC listener is started at port 8081. The HTTP server  *  will be available at port 8088. Use command-line options to  *  change this.  *    *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    22 May 2002  */
end_comment

begin_class
specifier|public
class|class
name|Server
block|{
comment|// command-line options
specifier|private
specifier|final
specifier|static
name|int
name|HELP_OPT
init|=
literal|'h'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|DEBUG_OPT
init|=
literal|'d'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|HTTP_PORT_OPT
init|=
literal|'p'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|XMLRPC_PORT_OPT
init|=
literal|'x'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|THREADS_OPT
init|=
literal|'t'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|CLOptionDescriptor
name|OPTIONS
index|[]
init|=
operator|new
name|CLOptionDescriptor
index|[]
block|{
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"help"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|HELP_OPT
argument_list|,
literal|"print help on command line options and exit."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"debug"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|DEBUG_OPT
argument_list|,
literal|"debug XMLRPC calls."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"http-port"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|HTTP_PORT_OPT
argument_list|,
literal|"set HTTP port."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"xmlrpc-port"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|XMLRPC_PORT_OPT
argument_list|,
literal|"set XMLRPC port."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"threads"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|THREADS_OPT
argument_list|,
literal|"set max. number of parallel threads allowed by the db."
argument_list|)
block|}
decl_stmt|;
comment|/**      *  Main method to start the stand-alone server.      *      *@param  args           Description of the Parameter      *@exception  Exception  Description of the Exception      */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|Exception
block|{
name|InteractiveClient
operator|.
name|printNotice
argument_list|()
expr_stmt|;
name|CLArgsParser
name|optParser
init|=
operator|new
name|CLArgsParser
argument_list|(
name|args
argument_list|,
name|OPTIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|optParser
operator|.
name|getErrorString
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|optParser
operator|.
name|getErrorString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|List
name|opt
init|=
name|optParser
operator|.
name|getArguments
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|opt
operator|.
name|size
argument_list|()
decl_stmt|;
name|CLOption
name|option
decl_stmt|;
name|int
name|httpPort
init|=
literal|8088
decl_stmt|;
name|int
name|rpcPort
init|=
literal|8081
decl_stmt|;
name|int
name|threads
init|=
literal|5
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|option
operator|=
operator|(
name|CLOption
operator|)
name|opt
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|option
operator|.
name|getId
argument_list|()
condition|)
block|{
case|case
name|HELP_OPT
case|:
name|printHelp
argument_list|()
expr_stmt|;
return|return;
case|case
name|DEBUG_OPT
case|:
name|XmlRpc
operator|.
name|debug
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|HTTP_PORT_OPT
case|:
try|try
block|{
name|httpPort
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"option -p requires a numeric argument"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
name|XMLRPC_PORT_OPT
case|:
try|try
block|{
name|rpcPort
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"option -x requires a numeric argument"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
case|case
name|THREADS_OPT
case|:
try|try
block|{
name|threads
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"option -t requires a numeric argument"
argument_list|)
expr_stmt|;
return|return;
block|}
break|break;
block|}
block|}
name|String
name|pathSep
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|home
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"loading configuration from "
operator|+
name|home
operator|+
name|pathSep
operator|+
literal|"conf.xml"
argument_list|)
expr_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
literal|"conf.xml"
argument_list|,
name|home
argument_list|)
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
name|threads
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"starting HTTP listener at port "
operator|+
name|httpPort
argument_list|)
expr_stmt|;
name|HttpServer
name|http
init|=
operator|new
name|HttpServer
argument_list|(
name|config
argument_list|,
name|httpPort
argument_list|,
literal|1
argument_list|,
name|threads
argument_list|)
decl_stmt|;
name|http
operator|.
name|start
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"starting XMLRPC listener at port "
operator|+
name|rpcPort
argument_list|)
expr_stmt|;
name|XmlRpc
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|WebServer
name|webServer
init|=
operator|new
name|WebServer
argument_list|(
name|rpcPort
argument_list|)
decl_stmt|;
name|AuthenticatedHandler
name|handler
init|=
operator|new
name|AuthenticatedHandler
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|webServer
operator|.
name|addHandler
argument_list|(
literal|"$default"
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"waiting for connections ..."
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|printHelp
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Options:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-t x      Maximum number of server threads (default=5)."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-p x      Port number for HTTP listener (default=8088)."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-x x      Port number for XML-RPC listener (default=8081)."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"-d        Turn on XML-RPC debugging output."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

