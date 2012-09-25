begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
operator|.
name|xquery
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
name|storage
operator|.
name|md
operator|.
name|MDStorageManager
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
name|XPathException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|MetadataModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2012-04-01"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-2.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FunctionDef
index|[]
name|functions
init|=
block|{
comment|//		new FunctionDef( Check.signature, Check.class ),
operator|new
name|FunctionDef
argument_list|(
name|DocumentByPair
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|DocumentByPair
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|DocumentByUUID
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|DocumentByUUID
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Keys
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Keys
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Keys
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Keys
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PairGet
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|PairGet
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PairGet
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|PairGet
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PairGet
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|PairGet
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PairSet
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|PairSet
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|PairSet
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|PairSet
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|UUID
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|UUID
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|UUID
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|UUID
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|MetadataModule
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
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDescription() 	 */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Metadata storage xquery interface"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getNamespaceURI() 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|MDStorageManager
operator|.
name|NAMESPACE_URI
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDefaultPrefix() 	 */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|MDStorageManager
operator|.
name|PREFIX
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

