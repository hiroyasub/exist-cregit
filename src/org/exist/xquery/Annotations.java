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
name|xquery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Annotations
block|{
specifier|private
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Annotations
argument_list|>
name|ns
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Annotations
argument_list|>
argument_list|()
decl_stmt|;
comment|//workaround
static|static
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xquery.test.Annotations"
argument_list|)
decl_stmt|;
name|clazz
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
block|}
specifier|public
specifier|static
name|void
name|register
parameter_list|(
name|String
name|namespace
parameter_list|,
name|Annotations
name|anns
parameter_list|)
block|{
name|ns
operator|.
name|put
argument_list|(
name|namespace
argument_list|,
name|anns
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|AnnotationTrigger
name|getTrigger
parameter_list|(
name|Annotation
name|ann
parameter_list|)
block|{
name|Annotations
name|anns
init|=
name|ns
operator|.
name|get
argument_list|(
name|ann
operator|.
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|anns
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|anns
operator|.
name|getTrigger
argument_list|(
name|ann
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|ann
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|AnnotationTrigger
name|getTrigger
parameter_list|(
name|String
name|name
parameter_list|,
name|Annotation
name|ann
parameter_list|)
function_decl|;
block|}
end_class

end_unit

