begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|TerminatedException
extends|extends
name|XPathException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|6055587317214098592L
decl_stmt|;
specifier|public
name|TerminatedException
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|column
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|static
class|class
name|TimeoutException
extends|extends
name|TerminatedException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1193758368058763151L
decl_stmt|;
specifier|public
name|TimeoutException
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|column
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|static
class|class
name|SizeLimitException
extends|extends
name|TerminatedException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|697205233217384556L
decl_stmt|;
specifier|public
name|SizeLimitException
parameter_list|(
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|line
argument_list|,
name|column
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

