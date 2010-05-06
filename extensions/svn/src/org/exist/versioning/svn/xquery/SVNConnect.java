begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|xquery
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
name|SessionWrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|old
operator|.
name|Subversion
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|functions
operator|.
name|session
operator|.
name|SessionModule
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNException
import|;
end_import

begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: lcahlander  * Date: Apr 22, 2010  * Time: 9:48:14 AM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_class
specifier|public
class|class
name|SVNConnect
extends|extends
name|BasicFunction
block|{
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
literal|"connect"
argument_list|,
name|SVNModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SVNModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Establishes a connection to a subversion repository.\n\nThis is a stub and currently does nothing."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"connection-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the connection"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection-uri"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The eXist collection URI"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"repository-url"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The subversion repository URL"
argument_list|)
block|,
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
name|ZERO_OR_ONE
argument_list|,
literal|"The subversion username"
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
literal|"The subversion password"
argument_list|)
block|,             }
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
literal|"true(), if successful."
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      *      * @param context      */
specifier|public
name|SVNConnect
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
comment|/**      * Process the function. All arguments are passed in the array args. The number of      * arguments, their type and cardinality have already been checked to match      * the function signature.      *      * @param args      * @param contextSequence      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|boolean
name|returnValue
init|=
literal|false
decl_stmt|;
comment|//        DAVRepositoryFactory.setup();
comment|//        SVNRepositoryFactoryImpl.setup();
name|String
name|connectionName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|AnyURIValue
name|collectionURI
init|=
operator|(
name|AnyURIValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|subversionPath
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|svnUsername
init|=
literal|"anonymous"
decl_stmt|;
name|String
name|svnPassword
init|=
literal|"anonymous"
decl_stmt|;
name|JavaObjectValue
name|session
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|svnUsername
operator|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|args
index|[
literal|4
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|svnPassword
operator|=
name|args
index|[
literal|4
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|XmldbURI
name|collection
init|=
name|collectionURI
operator|.
name|toXmldbURI
argument_list|()
decl_stmt|;
name|Subversion
name|subversion
init|=
operator|new
name|Subversion
argument_list|(
name|collection
argument_list|,
name|subversionPath
argument_list|,
name|svnUsername
argument_list|,
name|svnPassword
argument_list|)
decl_stmt|;
name|SessionModule
name|myModule
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
comment|// session object is read from global variable $session
name|Variable
name|var
init|=
name|myModule
operator|.
name|resolveVariable
argument_list|(
name|SessionModule
operator|.
name|SESSION_VAR
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
comment|// No saved session, so create one
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Type error: variable $session is not bound to a session object"
argument_list|)
operator|)
throw|;
comment|//            session = SessionModule.createSession( context, this );
block|}
if|else if
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
name|this
argument_list|,
literal|"Variable $session is not bound to a Java object."
argument_list|)
operator|)
throw|;
block|}
else|else
block|{
name|session
operator|=
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
expr_stmt|;
block|}
if|if
condition|(
name|session
operator|.
name|getObject
argument_list|()
operator|instanceof
name|SessionWrapper
condition|)
block|{
operator|(
operator|(
name|SessionWrapper
operator|)
name|session
operator|.
name|getObject
argument_list|()
operator|)
operator|.
name|setAttribute
argument_list|(
name|connectionName
argument_list|,
name|subversion
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
name|this
argument_list|,
literal|"Type error: variable $session is not bound to a session object"
argument_list|)
operator|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|SVNException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|(
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|returnValue
argument_list|)
operator|)
return|;
block|}
block|}
end_class

end_unit

