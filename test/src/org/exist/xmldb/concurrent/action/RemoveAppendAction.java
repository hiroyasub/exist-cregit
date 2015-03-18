begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
operator|.
name|XMLGenerator
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

begin_comment
comment|/**  * Removes the 10 last elements from the resource and inserts 10   * new elements at the top.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|RemoveAppendAction
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
literal|"<xu:remove select=\"//ELEMENT[last()]\">"
operator|+
literal|"</xu:remove>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
specifier|protected
name|XMLGenerator
name|xmlGenerator
decl_stmt|;
specifier|public
name|RemoveAppendAction
parameter_list|(
name|String
name|collectionPath
parameter_list|,
name|String
name|resourceName
parameter_list|,
name|String
index|[]
name|wordList
parameter_list|)
block|{
name|super
argument_list|(
name|collectionPath
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
name|xmlGenerator
operator|=
operator|new
name|XMLGenerator
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|,
name|wordList
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xmldb.test.concurrent.ConcurrentXUpdateTest.Action#execute() 	 */
specifier|public
name|boolean
name|execute
parameter_list|()
throws|throws
name|Exception
block|{
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
literal|null
argument_list|)
decl_stmt|;
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
name|append
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|remove
argument_list|(
name|service
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|remove
parameter_list|(
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
name|service
operator|.
name|update
argument_list|(
name|REMOVE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|append
parameter_list|(
name|XUpdateQueryService
name|service
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|updateOpen
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:append select=\"/ROOT-ELEMENT\" child=\"1\">"
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
name|String
name|update
init|=
name|updateOpen
operator|+
name|xmlGenerator
operator|.
name|generateElement
argument_list|()
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
block|}
end_class

end_unit

