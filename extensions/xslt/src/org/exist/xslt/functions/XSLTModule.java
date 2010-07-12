begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|functions
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSLTModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/xslt"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"xslt"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.5.0"
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
name|Current
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Current
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Document
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Document
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Document
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Document
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_date
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Format_date
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_date
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Format_date
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_dateTime
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Format_dateTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_dateTime
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Format_dateTime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_number
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Format_number
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_number
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Format_number
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_time
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Format_time
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Format_time
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Format_time
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Generate_id
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Generate_id
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Key
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Key
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Key
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Key
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|System_property
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|System_property
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Unparsed_entity_public_id
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Unparsed_entity_public_id
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Unparsed_entity_uri
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Unparsed_entity_uri
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Unparsed_text_available
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Unparsed_text_available
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Unparsed_text_available
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Unparsed_text_available
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Unparsed_text
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Unparsed_text
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Unparsed_text
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Unparsed_text
operator|.
name|class
argument_list|)
block|, 		}
decl_stmt|;
comment|/** 	 * @param functions 	 */
specifier|public
name|XSLTModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractInternalModule#getDefaultPrefix() 	 */
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractInternalModule#getNamespaceURI() 	 */
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDescription() 	 */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"XSLT Module"
return|;
block|}
annotation|@
name|Override
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

