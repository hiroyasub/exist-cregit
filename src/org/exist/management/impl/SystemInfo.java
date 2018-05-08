begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2014 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
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
name|io
operator|.
name|InputStreamReader
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
name|org
operator|.
name|exist
operator|.
name|SystemProperties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|management
operator|.
name|ObjectName
import|;
end_import

begin_comment
comment|/**  * Class SystemInfo  *   * @author wessels  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|SystemInfo
implements|implements
name|SystemInfoMXBean
block|{
specifier|public
specifier|static
specifier|final
name|String
name|OBJECT_NAME
init|=
literal|"org.exist.management:type=SystemInfo"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getExistVersion
parameter_list|()
block|{
return|return
name|SystemProperties
operator|.
name|getInstance
argument_list|()
operator|.
name|getSystemProperty
argument_list|(
literal|"product-version"
argument_list|,
literal|"unknown"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getExistBuild
parameter_list|()
block|{
return|return
name|SystemProperties
operator|.
name|getInstance
argument_list|()
operator|.
name|getSystemProperty
argument_list|(
literal|"product-build"
argument_list|,
literal|"unknown"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSvnRevision
parameter_list|()
block|{
return|return
name|getGitCommit
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getGitCommit
parameter_list|()
block|{
return|return
name|SystemProperties
operator|.
name|getInstance
argument_list|()
operator|.
name|getSystemProperty
argument_list|(
literal|"git-commit"
argument_list|,
literal|"unknown Git commit ID"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultLocale
parameter_list|()
block|{
return|return
name|Locale
operator|.
name|getDefault
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultEncoding
parameter_list|()
block|{
try|try
init|(
specifier|final
name|InputStreamReader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
init|)
block|{
return|return
name|isr
operator|.
name|getEncoding
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOperatingSystem
parameter_list|()
block|{
return|return
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.version"
argument_list|)
operator|+
literal|" "
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.arch"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

