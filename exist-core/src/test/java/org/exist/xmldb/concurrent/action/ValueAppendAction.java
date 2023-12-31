begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
operator|.
name|action
package|;
end_package

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
name|ResourceSet
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
name|XPathQueryService
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ValueAppendAction
extends|extends
name|Action
block|{
specifier|private
specifier|static
specifier|final
name|String
name|REMOVE
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:remove select=\"//item[last()]\">"
operator|+
literal|"</xu:remove>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
specifier|public
name|ValueAppendAction
parameter_list|(
specifier|final
name|String
name|collectionPath
parameter_list|,
specifier|final
name|String
name|resourceName
parameter_list|)
block|{
name|super
argument_list|(
name|collectionPath
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|execute
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|col
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionPath
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|XPathQueryService
name|query
init|=
operator|(
name|XPathQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|append
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|remove
argument_list|(
name|service
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|remove
parameter_list|(
specifier|final
name|XUpdateQueryService
name|service
parameter_list|)
throws|throws
name|XMLDBException
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|service
operator|.
name|update
argument_list|(
name|REMOVE
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|append
parameter_list|(
specifier|final
name|XUpdateQueryService
name|service
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|updateOpen
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:append select=\"/items\" child=\"1\">"
decl_stmt|;
specifier|final
name|String
name|updateClose
init|=
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|update
init|=
name|updateOpen
operator|+
literal|"<item id=\""
operator|+
name|i
operator|+
literal|"\"><name>abcdefg</name>"
operator|+
literal|"<value>"
operator|+
operator|(
literal|44.53
operator|+
name|i
operator|)
operator|+
literal|"</value></item>"
operator|+
name|updateClose
decl_stmt|;
name|service
operator|.
name|update
argument_list|(
name|update
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|query
parameter_list|(
specifier|final
name|XPathQueryService
name|service
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|queryResource
argument_list|(
name|resourceName
argument_list|,
literal|"/items/item[value = 44.53]"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|queryResource
argument_list|(
name|resourceName
argument_list|,
literal|"/items/item[@id=1]/name[.='abcdefg']/text()"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abcdefg"
argument_list|,
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

