begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2020 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|repo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|PackageException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|URIResolver
import|;
end_import

begin_comment
comment|/**  * URI Resolver which attempts to resolve XSLT Modules  * from EXPath Packages.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|PkgXsltModuleURIResolver
implements|implements
name|URIResolver
block|{
specifier|private
specifier|final
name|ExistRepository
name|existRepository
decl_stmt|;
specifier|public
name|PkgXsltModuleURIResolver
parameter_list|(
specifier|final
name|ExistRepository
name|existRepository
parameter_list|)
block|{
name|this
operator|.
name|existRepository
operator|=
name|existRepository
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Source
name|resolve
parameter_list|(
specifier|final
name|String
name|href
parameter_list|,
specifier|final
name|String
name|base
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
return|return
name|existRepository
operator|.
name|resolveXSLTModule
argument_list|(
name|href
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PackageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
