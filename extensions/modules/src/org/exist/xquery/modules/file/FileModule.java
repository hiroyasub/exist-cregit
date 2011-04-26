begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|file
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_comment
comment|/**  * eXist File Module Extension  *   * An extension module for the eXist Native XML Database that allows various file-oriented  * activities.  *   * @author Andrzej Taramina<andrzej@chaeron.com>  * @author ljo  * @serial 2008-03-06  * @version 1.0  *  * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[], java.util.Map)   */
end_comment

begin_class
specifier|public
class|class
name|FileModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/file"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"file"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2008-03-07"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.4"
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
name|Directory
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Directory
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DirectoryList
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|DirectoryList
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileRead
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileRead
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileRead
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FileRead
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileReadBinary
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileReadBinary
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileReadUnicode
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileReadUnicode
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileReadUnicode
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FileReadUnicode
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SerializeToFile
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|SerializeToFile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SerializeToFile
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|SerializeToFile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SerializeToFile
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|SerializeToFile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SerializeToFile
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|SerializeToFile
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileExists
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileExists
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileIsReadable
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileIsReadable
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileIsWriteable
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileIsWriteable
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileIsDirectory
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileIsDirectory
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileDelete
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileDelete
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FileMove
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FileMove
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DirectoryCreate
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|DirectoryCreate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DirectoryCreate
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|DirectoryCreate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Sync
operator|.
name|signature
argument_list|,
name|Sync
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FileModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
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
operator|(
name|NAMESPACE_URI
operator|)
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
operator|(
name|PREFIX
operator|)
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
operator|(
literal|"A module for performing various operations on files and directories stored in the server file system."
operator|)
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
comment|/** 	 * Resets the Module Context  	 *  	 * @param xqueryContext The XQueryContext 	 */
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
name|super
operator|.
name|reset
argument_list|(
name|xqueryContext
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

