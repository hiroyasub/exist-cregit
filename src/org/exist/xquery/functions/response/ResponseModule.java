begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2006-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|response
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
name|dom
operator|.
name|QName
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
name|*
import|;
end_import

begin_comment
comment|/**  * Module function definitions for xmldb module.  *  * @author  Adam Retter (adam.retter@devon.gov.uk)  * @author  ljo  * @author  JosÃ© MarÃ­a FernÃ¡ndez (jmfg@users.sourceforge.net)  */
end_comment

begin_class
specifier|public
class|class
name|ResponseModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/response"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"response"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2006-04-09"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|QName
name|RESPONSE_VAR
init|=
operator|new
name|QName
argument_list|(
literal|"response"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|RedirectTo
operator|.
name|signature
argument_list|,
name|RedirectTo
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetCookie
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|SetCookie
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetCookie
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|SetCookie
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetCookie
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|SetCookie
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetDateHeader
operator|.
name|signature
argument_list|,
name|SetDateHeader
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetHeader
operator|.
name|signature
argument_list|,
name|SetHeader
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetStatusCode
operator|.
name|signature
argument_list|,
name|SetStatusCode
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|StreamBinary
operator|.
name|signature
argument_list|,
name|StreamBinary
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Stream
operator|.
name|signature
argument_list|,
name|Stream
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetExists
operator|.
name|signature
argument_list|,
name|GetExists
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|final
name|Variable
name|responseVar
decl_stmt|;
specifier|public
name|ResponseModule
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
comment|// predefined module global variables:
name|this
operator|.
name|responseVar
operator|=
name|declareVariable
argument_list|(
name|RESPONSE_VAR
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#getDescription()      */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
operator|(
literal|"A module for dealing with HTTP responses."
operator|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#getNamespaceURI()      */
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#getDefaultPrefix()      */
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
name|getReleaseVersion
parameter_list|()
block|{
return|return
operator|(
name|RELEASED_IN_VERSION
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|,
name|boolean
name|keepGlobals
parameter_list|)
block|{
name|super
operator|.
name|reset
argument_list|(
name|xqueryContext
argument_list|,
name|keepGlobals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|keepGlobals
condition|)
block|{
name|responseVar
operator|.
name|setValue
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

