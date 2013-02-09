begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
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
name|Properties
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
name|functions
operator|.
name|system
operator|.
name|GetVersion
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Version
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"eXist"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VERSION
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BUILD
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SVN_REVISION
decl_stmt|;
static|static
block|{
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|properties
operator|.
name|load
argument_list|(
name|GetVersion
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"org/exist/system.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
block|}
name|VERSION
operator|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
literal|"product-version"
argument_list|)
expr_stmt|;
name|BUILD
operator|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
literal|"product-build"
argument_list|)
expr_stmt|;
name|SVN_REVISION
operator|=
operator|(
name|String
operator|)
name|properties
operator|.
name|get
argument_list|(
literal|"svn-revision"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getProductName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|VERSION
return|;
block|}
specifier|public
specifier|static
name|String
name|getBuild
parameter_list|()
block|{
return|return
name|BUILD
return|;
block|}
specifier|public
specifier|static
name|String
name|getSvnRevision
parameter_list|()
block|{
return|return
name|SVN_REVISION
return|;
block|}
block|}
end_class

end_unit

