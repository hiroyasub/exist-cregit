begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|InputVerifier
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JComponent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTextField
import|;
end_import

begin_comment
comment|/**  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|RegExpInputVerifier
extends|extends
name|InputVerifier
block|{
specifier|final
name|Matcher
name|matcher
decl_stmt|;
specifier|public
name|RegExpInputVerifier
parameter_list|(
specifier|final
name|Pattern
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|verify
parameter_list|(
specifier|final
name|JComponent
name|input
parameter_list|)
block|{
if|if
condition|(
name|input
operator|instanceof
name|JTextField
condition|)
block|{
name|matcher
operator|.
name|reset
argument_list|(
operator|(
operator|(
name|JTextField
operator|)
name|input
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|matcher
operator|.
name|matches
argument_list|()
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

