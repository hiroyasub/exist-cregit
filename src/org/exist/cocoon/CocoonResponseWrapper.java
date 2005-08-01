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
name|cocoon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Cookie
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|environment
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|ResponseWrapper
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|CocoonResponseWrapper
implements|implements
name|ResponseWrapper
block|{
specifier|private
name|Response
name|response
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|CocoonResponseWrapper
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
name|this
operator|.
name|response
operator|=
name|response
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 */
specifier|public
name|void
name|addCookie
parameter_list|(
name|Cookie
name|arg0
parameter_list|)
block|{
name|response
operator|.
name|addCookie
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|addDateHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|long
name|arg1
parameter_list|)
block|{
name|response
operator|.
name|addDateHeader
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|addHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
name|response
operator|.
name|addHeader
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|addIntHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
block|{
name|response
operator|.
name|addIntHeader
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|boolean
name|containsHeader
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|response
operator|.
name|containsHeader
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 * @return 	 */
specifier|public
name|Cookie
name|createCookie
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
return|return
name|response
operator|.
name|createCookie
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
return|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|String
name|encodeURL
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|response
operator|.
name|encodeURL
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|response
operator|.
name|getCharacterEncoding
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Locale
name|getLocale
parameter_list|()
block|{
return|return
name|response
operator|.
name|getLocale
argument_list|()
return|;
block|}
comment|/** Note: all this is pasted from class HttpResponseWrapper, 	 * but response is from a different class; no simple re-use of code possible.  :-( */
specifier|private
name|Map
name|dateHeaders
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
comment|/** 	 * @param name 	 * @param arg1 	 */
specifier|public
name|void
name|setDateHeader
parameter_list|(
name|String
name|name
parameter_list|,
name|long
name|arg1
parameter_list|)
block|{
name|dateHeaders
operator|.
name|put
argument_list|(
name|name
argument_list|,
operator|new
name|Long
argument_list|(
name|arg1
argument_list|)
argument_list|)
expr_stmt|;
name|response
operator|.
name|setDateHeader
argument_list|(
name|name
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/** @return the value of Date Header corresponding to given name, 	 * 0 if none has been set. */
specifier|public
name|long
name|getDateHeader
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|long
name|ret
init|=
literal|0
decl_stmt|;
name|Long
name|val
init|=
operator|(
name|Long
operator|)
name|dateHeaders
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|!=
literal|null
condition|)
name|ret
operator|=
name|val
operator|.
name|longValue
argument_list|()
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|setHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|setIntHeader
parameter_list|(
name|String
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
block|{
name|response
operator|.
name|setIntHeader
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 */
specifier|public
name|void
name|setLocale
parameter_list|(
name|Locale
name|arg0
parameter_list|)
block|{
name|response
operator|.
name|setLocale
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.http.ResponseWrapper#sendRedirect(java.lang.String) 	 */
specifier|public
name|void
name|sendRedirect
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|IOException
block|{
block|}
block|}
end_class

end_unit

