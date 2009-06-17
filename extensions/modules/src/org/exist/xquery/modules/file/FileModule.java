begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist SQL Module Extension  *  Copyright (C) 2006 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: SQLModule.java 3933 2006-09-18 21:08:38 +0000 (Mon, 18 Sep 2006) deliriumsky $  */
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
comment|/**  * eXist File Module Extension  *   * An extension module for the eXist Native XML Database that allows various file-oriented  * activities.  *   * @author Andrzej Taramina<andrzej@chaeron.com>  * @serial 2008-03-06  * @version 1.0  *  * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[])  */
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
name|DirectoryListFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|DirectoryListFunction
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
block|}
decl_stmt|;
specifier|public
name|FileModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
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
literal|"A module for performing various file-based operations."
operator|)
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

