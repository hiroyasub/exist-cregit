begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|system
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
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
name|security
operator|.
name|User
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
name|Item
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
name|Type
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
specifier|public
class|class
name|AsUser
extends|extends
name|Function
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AsUser
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
literal|"as-user"
argument_list|,
name|SystemModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SystemModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"A pseudo-function to execute a limited block of code as a different "
operator|+
literal|"user. The first argument is the name of the user, the second is the "
operator|+
literal|"password. If the user can be authenticated, the function will execute the "
operator|+
literal|"code block given in the third argument with the permissions of that user and"
operator|+
literal|"returns the result of the execution. Before the function completes, it switches "
operator|+
literal|"the current user back to the old user."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"username"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The username of the user to run the code against"
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
name|ZERO_OR_ONE
argument_list|,
literal|"The password of the user to run the code against"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"code-block"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The code block to run as the identified user"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the results of the code block executed"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|AsUser
parameter_list|(
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
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Entering the "
operator|+
name|SystemModule
operator|.
name|PREFIX
operator|+
literal|":as-user XQuery function"
argument_list|)
expr_stmt|;
name|Sequence
name|userSeq
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|passwdSeq
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|userSeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|XPathException
name|exception
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"No user specified"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"No user specified, throwing an exception!"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
throw|throw
name|exception
throw|;
block|}
name|String
name|userName
init|=
name|userSeq
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|passwd
init|=
name|passwdSeq
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|security
operator|.
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
name|User
name|user
init|=
name|security
operator|.
name|getUser
argument_list|(
name|userName
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|XPathException
name|exception
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Authentication failed"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Authentication failed for setting the user to ["
operator|+
name|userName
operator|+
literal|"] because user does not exist, throwing an exception!"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
throw|throw
name|exception
throw|;
block|}
if|if
condition|(
name|user
operator|.
name|validate
argument_list|(
name|passwd
argument_list|)
condition|)
block|{
name|User
name|oldUser
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getUser
argument_list|()
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Setting the authenticated user to: ["
operator|+
name|userName
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|setUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
return|return
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
finally|finally
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Returning the user to the original user: ["
operator|+
name|oldUser
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|setUser
argument_list|(
name|oldUser
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|XPathException
name|exception
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Authentication failed"
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Authentication failed for setting the user to ["
operator|+
name|userName
operator|+
literal|"] because of bad password, throwing an exception!"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
throw|throw
name|exception
throw|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#getDependencies()      */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|getDependencies
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#returnsType()      */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsType
argument_list|()
return|;
block|}
block|}
end_class

end_unit

