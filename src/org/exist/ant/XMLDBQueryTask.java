begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|PropertyHelper
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

begin_comment
comment|/**  * Ant task to execute an XQuery.  *   * The query is either passed as nested text in the element,  * or via an attribute "query".  *   * Please note that the task doesn't output the query results.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBQueryTask
extends|extends
name|AbstractXMLDBTask
block|{
specifier|private
name|String
name|query
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|text
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.apache.tools.ant.Task#execute() 	 */
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"you have to specify an XMLDB collection URI"
argument_list|)
throw|;
if|if
condition|(
name|text
operator|!=
literal|null
condition|)
block|{
name|PropertyHelper
name|helper
init|=
name|PropertyHelper
operator|.
name|getPropertyHelper
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|query
operator|=
name|helper
operator|.
name|replaceProperties
argument_list|(
literal|null
argument_list|,
name|text
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"you have to specify a query"
argument_list|)
throw|;
name|log
argument_list|(
literal|"XQuery is:\n"
operator|+
name|query
argument_list|,
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
operator|.
name|MSG_DEBUG
argument_list|)
expr_stmt|;
name|registerDatabase
argument_list|()
expr_stmt|;
try|try
block|{
name|Collection
name|collection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|results
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Found "
operator|+
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"XMLDB exception caught while executing query: "
operator|+
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
comment|/** 	 * @param query 	 */
specifier|public
name|void
name|setQuery
parameter_list|(
name|String
name|query
parameter_list|)
block|{
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
specifier|public
name|void
name|addText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
block|}
end_class

end_unit

