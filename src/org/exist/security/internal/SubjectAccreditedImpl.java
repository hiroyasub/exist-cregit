begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|AbstractSubject
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|AbstractAccount
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|SubjectAccreditedImpl
extends|extends
name|AbstractSubject
block|{
specifier|private
specifier|final
name|Object
name|letterOfCredit
decl_stmt|;
comment|/** 	 *  	 * @param account 	 * @param letterOfCredit the object the prove authentication 	 */
specifier|public
name|SubjectAccreditedImpl
parameter_list|(
name|AbstractAccount
name|account
parameter_list|,
name|Object
name|letterOfCredit
parameter_list|)
block|{
name|super
argument_list|(
name|account
argument_list|)
expr_stmt|;
name|this
operator|.
name|letterOfCredit
operator|=
name|letterOfCredit
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.Subject#authenticate(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|authenticate
parameter_list|(
name|Object
name|credentials
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.Subject#isAuthenticated() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isAuthenticated
parameter_list|()
block|{
return|return
operator|(
name|letterOfCredit
operator|!=
literal|null
operator|)
return|;
block|}
block|}
end_class

end_unit

