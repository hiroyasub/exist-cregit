begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|Cookie
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggeeFactory
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DebuggeeFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Debuggee
name|instance
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
name|Debuggee
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|className
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.debuggee"
argument_list|,
literal|"org.exist.debuggee.DebuggeeImpl"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Debuggee
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Class "
operator|+
name|className
operator|+
literal|" does not implement interface Debuggee. Using fallback."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|instance
operator|=
operator|(
name|Debuggee
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Class not found for debuggee: "
operator|+
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to instantiate class for debuggee: "
operator|+
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InstantiationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to instantiate class for debuggee: "
operator|+
name|className
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
block|{
name|instance
operator|=
operator|new
name|DummyDebuggee
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|instance
return|;
block|}
specifier|public
specifier|static
name|void
name|checkForDebugRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//TODO: XDEBUG_SESSION_STOP_NO_EXEC
comment|//TODO: XDEBUG_SESSION_STOP
comment|//if get "start new debug session" request
name|String
name|xdebug
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"XDEBUG_SESSION_START"
argument_list|)
decl_stmt|;
if|if
condition|(
name|xdebug
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|declareVariable
argument_list|(
name|Debuggee
operator|.
name|SESSION
argument_list|,
name|xdebug
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//if have session
name|xdebug
operator|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"XDEBUG_SESSION"
argument_list|)
expr_stmt|;
if|if
condition|(
name|xdebug
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|declareVariable
argument_list|(
name|Debuggee
operator|.
name|SESSION
argument_list|,
name|xdebug
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//looking for session in cookies (FF XDebug Helper add-ons as example)
specifier|final
name|Cookie
index|[]
name|cookies
init|=
name|request
operator|.
name|getCookies
argument_list|()
decl_stmt|;
if|if
condition|(
name|cookies
operator|!=
literal|null
condition|)
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
name|cookies
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
literal|"XDEBUG_SESSION"
operator|.
name|equals
argument_list|(
name|cookies
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
comment|//TODO: check for value?? ("eXistDB_XDebug" ? or leave "default") -shabanovd
name|context
operator|.
name|declareVariable
argument_list|(
name|Debuggee
operator|.
name|SESSION
argument_list|,
name|cookies
index|[
name|i
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|context
operator|.
name|requireDebugMode
argument_list|()
condition|)
block|{
specifier|final
name|String
name|idekey
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"KEY"
argument_list|)
decl_stmt|;
if|if
condition|(
name|idekey
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|declareVariable
argument_list|(
name|Debuggee
operator|.
name|IDEKEY
argument_list|,
name|idekey
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
