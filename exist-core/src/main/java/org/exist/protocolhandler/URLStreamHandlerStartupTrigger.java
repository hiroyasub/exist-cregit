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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|StartupTrigger
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
name|txn
operator|.
name|Txn
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Startup Trigger to register eXists URL Stream Handler  *  * @author Adam Retter<adam@exist-db.org>  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|URLStreamHandlerStartupTrigger
implements|implements
name|StartupTrigger
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
name|URLStreamHandlerStartupTrigger
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|JAVA_PROTOCOL_HANDLER_PKGS
init|=
literal|"java.protocol.handler.pkgs"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXIST_PROTOCOL_HANDLER
init|=
literal|"org.exist.protocolhandler.protocols"
decl_stmt|;
comment|/*     eXist may be started and stopped multiple times within the same JVM,     for example when running the test suite. This guard ensures that     we only attempt the registration once per JVM session     */
specifier|private
specifier|final
specifier|static
name|AtomicBoolean
name|registered
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
specifier|final
name|DBBroker
name|sysBroker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|params
parameter_list|)
block|{
name|String
name|mode
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|?
argument_list|>
name|list
init|=
name|params
operator|.
name|get
argument_list|(
literal|"mode"
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
operator|&&
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|mode
operator|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
name|registerStreamHandlerFactory
argument_list|(
name|mode
operator|==
literal|null
condition|?
name|Mode
operator|.
name|DISK
else|:
name|Mode
operator|.
name|valueOf
argument_list|(
name|mode
operator|.
name|toUpperCase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|registerStreamHandlerFactory
parameter_list|(
name|Mode
name|mode
parameter_list|)
block|{
if|if
condition|(
name|registered
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
try|try
block|{
name|URL
operator|.
name|setURLStreamHandlerFactory
argument_list|(
operator|new
name|eXistURLStreamHandlerFactory
argument_list|(
name|mode
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully registered eXistURLStreamHandlerFactory."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Error
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The JVM already has a URLStreamHandlerFactory registered, skipping..."
argument_list|)
expr_stmt|;
name|String
name|currentSystemProperty
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|JAVA_PROTOCOL_HANDLER_PKGS
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentSystemProperty
operator|==
literal|null
condition|)
block|{
comment|// Nothing setup yet
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting "
operator|+
name|JAVA_PROTOCOL_HANDLER_PKGS
operator|+
literal|" to "
operator|+
name|EXIST_PROTOCOL_HANDLER
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|JAVA_PROTOCOL_HANDLER_PKGS
argument_list|,
name|EXIST_PROTOCOL_HANDLER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// java.protocol.handler.pkgs is already setup, preserving settings
if|if
condition|(
operator|!
name|currentSystemProperty
operator|.
name|contains
argument_list|(
name|EXIST_PROTOCOL_HANDLER
argument_list|)
condition|)
block|{
comment|// eXist handler is not setup yet
name|currentSystemProperty
operator|=
name|currentSystemProperty
operator|+
literal|"|"
operator|+
name|EXIST_PROTOCOL_HANDLER
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting "
operator|+
name|JAVA_PROTOCOL_HANDLER_PKGS
operator|+
literal|" to "
operator|+
name|currentSystemProperty
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
name|JAVA_PROTOCOL_HANDLER_PKGS
argument_list|,
name|currentSystemProperty
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"System property "
operator|+
name|JAVA_PROTOCOL_HANDLER_PKGS
operator|+
literal|" has not been updated."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
end_class

end_unit
