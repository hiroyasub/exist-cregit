begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|security
operator|.
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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
name|BooleanValue
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|Sequence
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
name|SequenceType
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
name|StringValue
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier  * @author Loren Cahlander  */
end_comment

begin_class
specifier|public
class|class
name|SetCurrentUser
extends|extends
name|UserSwitchingBasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SetCurrentUser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"set-current-user"
argument_list|,
name|SessionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SessionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Change the user identity for the current HTTP session. Subsequent XQueries in the session will run with the "
operator|+
literal|"new user identity."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"user-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The user name"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"password"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The password"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true if the user name and password represent a valid user"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|SetCurrentUser
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//get the username and password parameters
specifier|final
name|String
name|userName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|passwd
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|//try and validate the user and password
specifier|final
name|SecurityManager
name|security
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|user
decl_stmt|;
try|try
block|{
name|user
operator|=
name|security
operator|.
name|authenticate
argument_list|(
name|userName
argument_list|,
name|passwd
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Could not validate user "
operator|+
name|userName
operator|+
literal|" ["
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
comment|//switch the user of the current broker
name|switchUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
comment|//validated user, store in session
specifier|final
name|SessionWrapper
name|session
init|=
name|SessionFunction
operator|.
name|getOrCreateSession
argument_list|(
name|this
argument_list|,
name|context
argument_list|,
name|Optional
operator|.
name|ofNullable
argument_list|(
name|context
operator|.
name|getHttpContext
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|XQueryContext
operator|.
name|HttpContext
operator|::
name|getSession
argument_list|)
argument_list|)
decl_stmt|;
name|session
operator|.
name|setAttribute
argument_list|(
literal|"user"
argument_list|,
name|userName
argument_list|)
expr_stmt|;
name|session
operator|.
name|setAttribute
argument_list|(
literal|"password"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|passwd
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
block|}
block|}
end_class

end_unit

