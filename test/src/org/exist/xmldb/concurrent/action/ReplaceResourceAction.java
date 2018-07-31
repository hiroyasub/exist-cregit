begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-04 Wolfgang M. Meier (wolfgang@exist-db.org)  *  and others (see http://exist-db.org) * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *  *  $Id$ */
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
name|DBUtils
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
comment|/**  * Replace an existing resource.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ReplaceResourceAction
extends|extends
name|Action
block|{
specifier|public
specifier|static
specifier|final
name|String
name|XML
init|=
literal|"<config>"
operator|+
literal|"<user id=\"george\">"
operator|+
literal|"<phone>+49 69 888478</phone>"
operator|+
literal|"<email>george@email.com</email>"
operator|+
literal|"<customer-id>64534233</customer-id>"
operator|+
literal|"<bank-account>7466356</bank-account>"
operator|+
literal|"</user>"
operator|+
literal|"<user id=\"sam\">"
operator|+
literal|"<phone>+49 69 774345</phone>"
operator|+
literal|"<email>sam@email.com</email>"
operator|+
literal|"<customer-id>993834</customer-id>"
operator|+
literal|"<bank-account>364553</bank-account>"
operator|+
literal|"</user>"
operator|+
literal|"</config>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUERY1
init|=
literal|"//user[@id = 'george']/phone[contains(., '69')]/text()"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUERY2
init|=
literal|"//user[@id = 'sam']/customer-id[. = '993834']"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUERY3
init|=
literal|"//user[email = 'sam@email.com']"
decl_stmt|;
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|public
name|ReplaceResourceAction
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
name|String
name|xml
init|=
literal|"<data now=\""
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"\" count=\""
operator|+
operator|++
name|count
operator|+
literal|"\">"
operator|+
name|XML
operator|+
literal|"</data>"
decl_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|col
argument_list|,
name|resourceName
argument_list|,
name|xml
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|DBUtils
operator|.
name|queryResource
argument_list|(
name|col
argument_list|,
name|resourceName
argument_list|,
name|TEST_QUERY1
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
name|assertEquals
argument_list|(
literal|"+49 69 888478"
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
argument_list|)
expr_stmt|;
name|result
operator|=
name|DBUtils
operator|.
name|queryResource
argument_list|(
name|col
argument_list|,
name|resourceName
argument_list|,
name|TEST_QUERY2
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
name|result
operator|=
name|DBUtils
operator|.
name|queryResource
argument_list|(
name|col
argument_list|,
name|resourceName
argument_list|,
name|TEST_QUERY3
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
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

