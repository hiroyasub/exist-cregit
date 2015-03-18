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
name|management
package|;
end_package

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

begin_class
specifier|public
class|class
name|AgentFactory
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
name|AgentFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|Agent
name|instance
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
name|Agent
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
literal|"exist.jmxagent"
argument_list|,
literal|"org.exist.management.impl.JMXAgent"
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
name|Agent
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
literal|" does not implement interface Agent. Using fallback."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|instance
operator|=
operator|(
name|Agent
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
literal|"Class not found for JMX agent: "
operator|+
name|className
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalAccessException
decl||
name|InstantiationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to instantiate class for JMX agent: "
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
name|DummyAgent
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|instance
return|;
block|}
block|}
end_class

end_unit

