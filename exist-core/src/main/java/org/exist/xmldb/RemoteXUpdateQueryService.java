begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

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
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
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
name|client
operator|.
name|XmlRpcClient
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
name|Collection
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
name|ErrorCodes
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XUpdateQueryService
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteXUpdateQueryService
implements|implements
name|XUpdateQueryService
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
name|RemoteXUpdateQueryService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|RemoteCollection
name|parent
decl_stmt|;
specifier|public
name|RemoteXUpdateQueryService
parameter_list|(
specifier|final
name|RemoteCollection
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"XUpdateQueryService"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"1.0"
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|update
parameter_list|(
specifier|final
name|String
name|commands
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"processing xupdate:\n"
operator|+
name|commands
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|xupdateData
init|=
name|commands
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|xupdateData
argument_list|)
expr_stmt|;
specifier|final
name|int
name|mods
init|=
operator|(
name|int
operator|)
name|parent
operator|.
name|execute
argument_list|(
literal|"xupdate"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"processed "
operator|+
name|mods
operator|+
literal|" modifications"
argument_list|)
expr_stmt|;
return|return
name|mods
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|updateResource
parameter_list|(
specifier|final
name|String
name|id
parameter_list|,
specifier|final
name|String
name|commands
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"processing xupdate:\n"
operator|+
name|commands
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|xupdateData
init|=
name|commands
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
comment|//TODO : use dedicated function in XmldbURI
name|params
operator|.
name|add
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|"/"
operator|+
name|id
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|xupdateData
argument_list|)
expr_stmt|;
specifier|final
name|int
name|mods
init|=
operator|(
name|int
operator|)
name|parent
operator|.
name|execute
argument_list|(
literal|"xupdateResource"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"processed "
operator|+
name|mods
operator|+
literal|" modifications"
argument_list|)
expr_stmt|;
return|return
name|mods
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCollection
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|parent
operator|=
operator|(
name|RemoteCollection
operator|)
name|collection
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
block|}
end_class

end_unit

