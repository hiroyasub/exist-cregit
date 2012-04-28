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
name|examples
operator|.
name|soap
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Admin
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|AdminService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|AdminServiceLocator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QueryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|soap
operator|.
name|QueryServiceLocator
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
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xupdate
operator|.
name|XUpdateProcessor
import|;
end_import

begin_comment
comment|/**  * Execute xupdate via SOAP. First create /db/test collection in database.  *  * Execute: bin\run.bat org.exist.examples.soap.XUpdateExample<query file>  */
end_comment

begin_class
specifier|public
class|class
name|XUpdateExample
block|{
specifier|private
specifier|final
specifier|static
name|String
name|document
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<notes>"
operator|+
literal|"<note id=\"1\">Complete documentation.</note>"
operator|+
literal|"</notes>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|xupdate
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\""
operator|+
name|XUpdateProcessor
operator|.
name|XUPDATE_NS
operator|+
literal|"\">"
operator|+
literal|"<xu:insert-after select=\"//note[1]\">"
operator|+
literal|"<xu:element name=\"note\">"
operator|+
literal|"<xu:attribute name=\"id\">2</xu:attribute>"
operator|+
literal|"Complete change log."
operator|+
literal|"</xu:element>"
operator|+
literal|"</xu:insert-after>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|AdminService
name|adminService
init|=
operator|new
name|AdminServiceLocator
argument_list|()
decl_stmt|;
name|Admin
name|admin
init|=
name|adminService
operator|.
name|getAdmin
argument_list|()
decl_stmt|;
name|QueryService
name|queryService
init|=
operator|new
name|QueryServiceLocator
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
name|queryService
operator|.
name|getQuery
argument_list|()
decl_stmt|;
name|String
name|session
init|=
name|admin
operator|.
name|connect
argument_list|(
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|admin
operator|.
name|store
argument_list|(
name|session
argument_list|,
name|document
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/notes.xml"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|admin
operator|.
name|xupdateResource
argument_list|(
name|session
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/notes.xml"
argument_list|,
name|xupdate
argument_list|)
expr_stmt|;
name|String
name|data
init|=
name|query
operator|.
name|getResource
argument_list|(
name|session
argument_list|,
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/notes.xml"
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|admin
operator|.
name|disconnect
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

