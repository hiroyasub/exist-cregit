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
name|modules
operator|.
name|example
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|ExampleModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/examples"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"example"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2005-04-20"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.2"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|EchoFunction
operator|.
name|signature
argument_list|,
name|EchoFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|ExampleModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for showing good examples of module usage"
return|;
block|}
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
block|}
end_class

end_unit

