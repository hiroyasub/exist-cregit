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
name|util
operator|.
name|serializer
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

begin_comment
comment|/**  * Represents a list of attributes. Each attribute is defined by  * a {@link org.exist.dom.QName} and a value. Instances  * of this class can be passed to   * {@link org.exist.util.serializer.Receiver#startElement(QName, AttrList)}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|AttrList
block|{
specifier|protected
name|QName
name|names
index|[]
init|=
operator|new
name|QName
index|[
literal|4
index|]
decl_stmt|;
specifier|protected
name|String
name|values
index|[]
init|=
operator|new
name|String
index|[
literal|4
index|]
decl_stmt|;
specifier|protected
name|int
name|size
init|=
literal|0
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|AttrList
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|addAttribute
parameter_list|(
name|QName
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|ensureCapacity
argument_list|()
expr_stmt|;
name|names
index|[
name|size
index|]
operator|=
name|name
expr_stmt|;
name|values
index|[
name|size
index|]
operator|=
name|value
expr_stmt|;
name|size
operator|++
expr_stmt|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|names
index|[
name|pos
index|]
return|;
block|}
specifier|public
name|String
name|getValue
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|values
index|[
name|pos
index|]
return|;
block|}
specifier|public
name|String
name|getValue
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|names
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|values
index|[
name|i
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|ensureCapacity
parameter_list|()
block|{
if|if
condition|(
name|size
operator|==
name|names
operator|.
name|length
condition|)
block|{
comment|// resize
specifier|final
name|int
name|newSize
init|=
name|names
operator|.
name|length
operator|*
literal|3
operator|/
literal|2
decl_stmt|;
name|QName
name|tnames
index|[]
init|=
operator|new
name|QName
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|names
argument_list|,
literal|0
argument_list|,
name|tnames
argument_list|,
literal|0
argument_list|,
name|names
operator|.
name|length
argument_list|)
expr_stmt|;
name|String
name|tvalues
index|[]
init|=
operator|new
name|String
index|[
name|newSize
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|tvalues
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
name|names
operator|=
name|tnames
expr_stmt|;
name|values
operator|=
name|tvalues
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

