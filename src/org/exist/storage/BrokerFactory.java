begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|Configuration
import|;
end_import

begin_class
specifier|public
class|class
name|BrokerFactory
block|{
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_DATABASE
init|=
literal|"database"
decl_stmt|;
specifier|private
specifier|static
name|Class
argument_list|<
name|?
argument_list|>
name|constructorArgs
index|[]
init|=
block|{
name|BrokerPool
operator|.
name|class
block|,
name|Configuration
operator|.
name|class
block|}
decl_stmt|;
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
argument_list|>
name|objClasses
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|void
name|plug
parameter_list|(
name|String
name|id
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
name|clazz
parameter_list|)
block|{
name|objClasses
operator|.
name|put
argument_list|(
name|id
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
static|static
block|{
name|plug
argument_list|(
literal|"NATIVE"
argument_list|,
name|NativeBroker
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|DBBroker
name|getInstance
parameter_list|(
name|BrokerPool
name|database
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|EXistException
block|{
name|String
name|brokerID
init|=
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_DATABASE
argument_list|)
decl_stmt|;
if|if
condition|(
name|brokerID
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no database defined"
argument_list|)
throw|;
block|}
comment|// Repair name ; https://sourceforge.net/p/exist/bugs/810/
name|brokerID
operator|=
name|brokerID
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|objClasses
operator|.
name|containsKey
argument_list|(
name|brokerID
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no database backend found for "
operator|+
name|brokerID
argument_list|)
throw|;
block|}
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
name|clazz
init|=
name|objClasses
operator|.
name|get
argument_list|(
name|brokerID
argument_list|)
decl_stmt|;
specifier|final
name|Constructor
argument_list|<
name|?
extends|extends
name|DBBroker
argument_list|>
name|constructor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|constructorArgs
argument_list|)
decl_stmt|;
return|return
name|constructor
operator|.
name|newInstance
argument_list|(
name|database
argument_list|,
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"can't get database backend "
operator|+
name|brokerID
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

