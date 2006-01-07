begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|jsp
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|jsp
operator|.
name|JspException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|jsp
operator|.
name|tagext
operator|.
name|TagSupport
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

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  *  */
end_comment

begin_class
specifier|public
class|class
name|CollectionTag
extends|extends
name|TagSupport
block|{
specifier|public
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
name|String
name|varName
decl_stmt|;
specifier|private
name|String
name|uri
decl_stmt|;
specifier|private
name|String
name|user
init|=
literal|"guest"
decl_stmt|;
specifier|private
name|String
name|password
init|=
literal|"guest"
decl_stmt|;
specifier|private
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see javax.servlet.jsp.tagext.TagSupport#doStartTag() 	 */
specifier|public
name|int
name|doStartTag
parameter_list|()
throws|throws
name|JspException
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
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
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|collection
operator|=
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
name|JspException
argument_list|(
literal|"Database driver class not found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JspException
argument_list|(
literal|"Failed to initialize database driver"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JspException
argument_list|(
literal|"Failed to initialize database driver"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|JspException
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
name|pageContext
operator|.
name|setAttribute
argument_list|(
name|varName
argument_list|,
name|collection
argument_list|)
expr_stmt|;
return|return
name|EVAL_BODY_INCLUDE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.servlet.jsp.tagext.TagSupport#doEndTag() 	 */
specifier|public
name|int
name|doEndTag
parameter_list|()
throws|throws
name|JspException
block|{
return|return
name|EVAL_PAGE
return|;
block|}
specifier|public
name|Collection
name|getCollection
parameter_list|()
block|{
return|return
name|collection
return|;
block|}
specifier|public
name|void
name|setVar
parameter_list|(
name|String
name|var
parameter_list|)
block|{
name|this
operator|.
name|varName
operator|=
name|var
expr_stmt|;
block|}
specifier|public
name|String
name|getVar
parameter_list|()
block|{
return|return
name|varName
return|;
block|}
comment|/** 	 * @return Returns the password. 	 */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
comment|/** 	 * @param password The password to set. 	 */
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
block|}
comment|/** 	 * @return Returns the uri. 	 */
specifier|public
name|String
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
comment|/** 	 * @param uri The uri to set. 	 */
specifier|public
name|void
name|setUri
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
comment|/** 	 * @return Returns the user. 	 */
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
comment|/** 	 * @param user The user to set. 	 */
specifier|public
name|void
name|setUser
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
block|}
end_class

end_unit

