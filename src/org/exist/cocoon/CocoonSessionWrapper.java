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
name|util
operator|.
name|Enumeration
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
name|Session
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
name|SessionWrapper
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|CocoonSessionWrapper
implements|implements
name|SessionWrapper
block|{
specifier|private
name|Session
name|session
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|CocoonSessionWrapper
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @return 	 */
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|session
operator|.
name|getAttribute
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|Enumeration
name|getAttributeNames
parameter_list|()
block|{
return|return
name|session
operator|.
name|getAttributeNames
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|session
operator|.
name|getCreationTime
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|session
operator|.
name|getId
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|long
name|getLastAccessedTime
parameter_list|()
block|{
return|return
name|session
operator|.
name|getLastAccessedTime
argument_list|()
return|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|int
name|getMaxInactiveInterval
parameter_list|()
block|{
return|return
name|session
operator|.
name|getMaxInactiveInterval
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#hashCode() 	 */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|session
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/** 	 *  	 */
specifier|public
name|void
name|invalidate
parameter_list|()
block|{
name|session
operator|.
name|invalidate
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @return 	 */
specifier|public
name|boolean
name|isNew
parameter_list|()
block|{
return|return
name|session
operator|.
name|isNew
argument_list|()
return|;
block|}
comment|/** 	 * @param arg0 	 */
specifier|public
name|void
name|removeAttribute
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|session
operator|.
name|removeAttribute
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param arg0 	 * @param arg1 	 */
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{
name|session
operator|.
name|setAttribute
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
name|setMaxInactiveInterval
parameter_list|(
name|int
name|arg0
parameter_list|)
block|{
name|session
operator|.
name|setMaxInactiveInterval
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

