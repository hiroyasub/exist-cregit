begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2006-09 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|session
package|;
end_package

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
name|http
operator|.
name|servlets
operator|.
name|RequestWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|SessionWrapper
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
name|Function
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
name|Variable
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
name|request
operator|.
name|RequestModule
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
name|value
operator|.
name|JavaObjectValue
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
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * Module function definitions for transform module.  *  * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author Adam Retter (adam.retter@devon.gov.uk)  * @author Loren Cahlander  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|SessionModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/session"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"session"
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
name|SESSION_VAR
init|=
operator|new
name|QName
argument_list|(
literal|"session"
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
name|Create
operator|.
name|signature
argument_list|,
name|Create
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Clear
operator|.
name|signature
argument_list|,
name|Clear
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|EncodeURL
operator|.
name|signature
argument_list|,
name|EncodeURL
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetID
operator|.
name|signature
argument_list|,
name|GetID
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetAttribute
operator|.
name|signature
argument_list|,
name|GetAttribute
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|RemoveAttribute
operator|.
name|signature
argument_list|,
name|RemoveAttribute
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetAttributeNames
operator|.
name|signature
argument_list|,
name|GetAttributeNames
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Invalidate
operator|.
name|signature
argument_list|,
name|Invalidate
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetAttribute
operator|.
name|signature
argument_list|,
name|SetAttribute
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetCurrentUser
operator|.
name|signature
argument_list|,
name|SetCurrentUser
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
specifier|public
name|SessionModule
parameter_list|()
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
comment|// predefined module global variables:
name|declareVariable
argument_list|(
name|SESSION_VAR
argument_list|,
literal|null
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
literal|"A module for dealing with the HTTP session."
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getNamespaceURI() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Module#getDefaultPrefix() 	 */
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
name|RELEASED_IN_VERSION
return|;
block|}
comment|/** 	 * Utility method to create a session and store it in the context as a variable 	  	 * @param context 	 */
specifier|static
name|JavaObjectValue
name|createSession
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Function
name|fn
parameter_list|)
throws|throws
name|XPathException
block|{
name|JavaObjectValue
name|ret
init|=
literal|null
decl_stmt|;
name|RequestModule
name|myModule
init|=
operator|(
name|RequestModule
operator|)
name|context
operator|.
name|getModule
argument_list|(
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
comment|// request object is read from global variable $request
name|Variable
name|var
init|=
name|myModule
operator|.
name|resolveVariable
argument_list|(
name|RequestModule
operator|.
name|REQUEST_VAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|==
literal|null
operator|||
name|var
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|fn
argument_list|,
literal|"No request object found in the current XQuery context."
argument_list|)
operator|)
throw|;
block|}
if|if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|JAVA_OBJECT
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|fn
argument_list|,
literal|"Variable $request is not bound to an Java object."
argument_list|)
operator|)
throw|;
block|}
name|JavaObjectValue
name|value
init|=
operator|(
name|JavaObjectValue
operator|)
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getObject
argument_list|()
operator|instanceof
name|RequestWrapper
condition|)
block|{
name|SessionModule
name|sessionModule
init|=
operator|(
name|SessionModule
operator|)
name|context
operator|.
name|getModule
argument_list|(
name|SessionModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
name|SessionWrapper
name|session
init|=
operator|(
operator|(
name|RequestWrapper
operator|)
name|value
operator|.
name|getObject
argument_list|()
operator|)
operator|.
name|getSession
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|sessionModule
operator|.
name|declareVariable
argument_list|(
name|SessionModule
operator|.
name|SESSION_VAR
argument_list|,
name|session
argument_list|)
expr_stmt|;
name|ret
operator|=
operator|(
name|JavaObjectValue
operator|)
name|sessionModule
operator|.
name|resolveVariable
argument_list|(
name|SessionModule
operator|.
name|SESSION_VAR
argument_list|)
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|fn
argument_list|,
literal|"Variable $request is not bound to a Request object."
argument_list|)
operator|)
throw|;
block|}
return|return
operator|(
name|ret
operator|)
return|;
block|}
block|}
end_class

end_unit

