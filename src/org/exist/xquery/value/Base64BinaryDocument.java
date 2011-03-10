begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|XPathException
import|;
end_import

begin_comment
comment|/**  * Wrapper around Base64Binary.  * @author dizzzzz  */
end_comment

begin_class
specifier|public
class|class
name|Base64BinaryDocument
extends|extends
name|BinaryValueFromInputStream
block|{
specifier|private
name|String
name|url
init|=
literal|null
decl_stmt|;
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
specifier|public
name|void
name|setUrl
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
specifier|private
name|Base64BinaryDocument
parameter_list|(
name|BinaryValueManager
name|manager
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|manager
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
name|is
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Base64BinaryDocument
name|getInstance
parameter_list|(
name|BinaryValueManager
name|manager
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|XPathException
block|{
name|Base64BinaryDocument
name|b64BinaryDocument
init|=
operator|new
name|Base64BinaryDocument
argument_list|(
name|manager
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|manager
operator|.
name|registerBinaryValueInstance
argument_list|(
name|b64BinaryDocument
argument_list|)
expr_stmt|;
return|return
name|b64BinaryDocument
return|;
block|}
block|}
end_class

end_unit

