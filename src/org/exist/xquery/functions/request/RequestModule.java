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
name|xquery
operator|.
name|functions
operator|.
name|request
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|RequestModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/request"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"request"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|REQUEST_VAR
init|=
operator|new
name|QName
argument_list|(
literal|"request"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|RESPONSE_VAR
init|=
operator|new
name|QName
argument_list|(
literal|"response"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|SESSION_VAR
init|=
operator|new
name|QName
argument_list|(
literal|"session"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|SessionAttributes
operator|.
name|signature
argument_list|,
name|SessionAttributes
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetSessionAttribute
operator|.
name|signature
argument_list|,
name|GetSessionAttribute
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetSessionAttribute
operator|.
name|signature
argument_list|,
name|SetSessionAttribute
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|InvalidateSession
operator|.
name|signature
argument_list|,
name|InvalidateSession
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RequestParameter
operator|.
name|signature
argument_list|,
name|RequestParameter
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RequestParameterNames
operator|.
name|signature
argument_list|,
name|RequestParameterNames
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetRequestData
operator|.
name|signature
argument_list|,
name|GetRequestData
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|CreateSession
operator|.
name|signature
argument_list|,
name|CreateSession
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RequestURI
operator|.
name|signature
argument_list|,
name|RequestURI
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RedirectTo
operator|.
name|signature
argument_list|,
name|RedirectTo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|EncodeURL
operator|.
name|signature
argument_list|,
name|EncodeURL
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetUploadedFile
operator|.
name|signature
argument_list|,
name|GetUploadedFile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetUploadedFileName
operator|.
name|signature
argument_list|,
name|GetUploadedFileName
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|RequestModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDescription() 	 */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Functions dealing with HTTP requests/responses"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDefaultPrefix() 	 */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
block|}
end_class

end_unit

