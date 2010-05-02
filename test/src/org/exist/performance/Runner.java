begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|performance
package|;
end_package

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
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|performance
operator|.
name|actions
operator|.
name|Action
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
name|CollectionImpl
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
name|DatabaseInstanceManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
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
name|Map
import|;
end_import

begin_class
specifier|public
class|class
name|Runner
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Connection
argument_list|>
name|connections
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Connection
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Connection
name|firstConnection
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|Action
argument_list|>
argument_list|>
name|classes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|Action
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
name|groups
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|TestResultWriter
name|resultWriter
decl_stmt|;
specifier|private
name|int
name|nextId
init|=
literal|0
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Runner
parameter_list|(
name|Element
name|root
parameter_list|,
name|TestResultWriter
name|reporter
parameter_list|)
throws|throws
name|EXistException
throws|,
name|XMLDBException
block|{
name|this
operator|.
name|resultWriter
operator|=
name|reporter
expr_stmt|;
name|initDb
argument_list|()
expr_stmt|;
name|NodeList
name|nl
init|=
name|root
operator|.
name|getElementsByTagNameNS
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"configuration"
argument_list|)
decl_stmt|;
if|if
condition|(
name|nl
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"no configuration element found"
argument_list|)
throw|;
if|if
condition|(
name|nl
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"found more than one configuration element"
argument_list|)
throw|;
name|Element
name|config
init|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|nl
operator|=
name|config
operator|.
name|getElementsByTagNameNS
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"action"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|Class
argument_list|<
name|Action
argument_list|>
name|clazz
init|=
operator|(
name|Class
argument_list|<
name|Action
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
argument_list|)
decl_stmt|;
name|classes
operator|.
name|put
argument_list|(
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Class not found: "
operator|+
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
argument_list|)
throw|;
block|}
block|}
name|nl
operator|=
name|config
operator|.
name|getElementsByTagNameNS
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"connection"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Connection
name|con
init|=
operator|new
name|Connection
argument_list|(
name|elem
argument_list|)
decl_stmt|;
name|connections
operator|.
name|put
argument_list|(
name|con
operator|.
name|getId
argument_list|()
argument_list|,
name|con
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstConnection
operator|==
literal|null
condition|)
name|firstConnection
operator|=
name|con
expr_stmt|;
block|}
name|nl
operator|=
name|root
operator|.
name|getElementsByTagNameNS
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"group"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Group
name|group
init|=
operator|new
name|Group
argument_list|(
name|this
argument_list|,
name|elem
argument_list|)
decl_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|run
parameter_list|(
name|String
name|groupToRun
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|EXistException
block|{
if|if
condition|(
name|groupToRun
operator|==
literal|null
condition|)
block|{
for|for
control|(
name|Group
name|group
range|:
name|groups
operator|.
name|values
argument_list|()
control|)
block|{
name|group
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|Group
name|group
init|=
name|groups
operator|.
name|get
argument_list|(
name|groupToRun
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Test group not found: "
operator|+
name|groupToRun
argument_list|)
throw|;
name|group
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|(
name|String
name|connection
parameter_list|)
block|{
return|return
name|connections
operator|.
name|get
argument_list|(
name|connection
argument_list|)
return|;
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|()
block|{
return|return
name|firstConnection
return|;
block|}
specifier|public
name|Class
argument_list|<
name|Action
argument_list|>
name|getClassForAction
parameter_list|(
name|String
name|action
parameter_list|)
block|{
return|return
name|classes
operator|.
name|get
argument_list|(
name|action
argument_list|)
return|;
block|}
specifier|public
name|TestResultWriter
name|getResults
parameter_list|()
block|{
return|return
name|resultWriter
return|;
block|}
specifier|public
name|int
name|getNextId
parameter_list|()
block|{
return|return
operator|++
name|nextId
return|;
block|}
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|resultWriter
operator|!=
literal|null
condition|)
name|resultWriter
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|shutdownDb
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|initDb
parameter_list|()
throws|throws
name|EXistException
block|{
try|try
block|{
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
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|shutdownDb
parameter_list|()
throws|throws
name|XMLDBException
block|{
for|for
control|(
name|Connection
name|connection
range|:
name|connections
operator|.
name|values
argument_list|()
control|)
block|{
name|CollectionImpl
name|collection
init|=
operator|(
name|CollectionImpl
operator|)
name|connection
operator|.
name|getCollection
argument_list|(
literal|"/db"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|collection
operator|.
name|isRemoteCollection
argument_list|()
condition|)
block|{
name|DatabaseInstanceManager
name|mgr
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

