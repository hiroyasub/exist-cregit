begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
package|;
end_package

begin_comment
comment|/**  * This class represents the context from which an access is made.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|AccessContext
block|{
comment|/** 	 * The postfix for all internal accesses. 	 */
specifier|public
specifier|static
specifier|final
name|String
name|INTERNAL
init|=
literal|"(internal)"
decl_stmt|;
comment|/** 	 * This represents when access is attempted as a result of a trigger. 	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|TRIGGER
init|=
operator|new
name|AccessContext
argument_list|(
literal|"Trigger"
argument_list|)
decl_stmt|;
comment|/** 	 * This represents when access is made through SOAP. 	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|SOAP
init|=
operator|new
name|AccessContext
argument_list|(
literal|"SOAP"
argument_list|)
decl_stmt|;
comment|/** 	 * This represents when access is made through XML:DB. 	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|XMLDB
init|=
operator|new
name|AccessContext
argument_list|(
literal|"XML:DB"
argument_list|)
decl_stmt|;
comment|/** 	 * The context for access through the REST-style interface.  	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|REST
init|=
operator|new
name|AccessContext
argument_list|(
literal|"REST"
argument_list|)
decl_stmt|;
comment|/** 	 * The context for remote access over XML-RPC. 	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|XMLRPC
init|=
operator|new
name|AccessContext
argument_list|(
literal|"XML-RPC"
argument_list|)
decl_stmt|;
comment|/** 	 * The context for access through WEBDAV 	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|WEBDAV
init|=
operator|new
name|AccessContext
argument_list|(
literal|"WebDAV"
argument_list|)
decl_stmt|;
comment|/** 	 * The context for access internally when the access is not made by any of the 	 * other contexts.  This should only be used if all actions 	 * are completely trusted, that is, no user input should be directly included 	 * in a query or any similar case.  	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|INTERNAL_PREFIX_LOOKUP
init|=
operator|new
name|AccessContext
argument_list|(
literal|"Prefix lookup "
operator|+
name|INTERNAL
argument_list|)
decl_stmt|;
comment|/** 	 * The context for trusted validation queries.  	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|VALIDATION_INTERNAL
init|=
operator|new
name|AccessContext
argument_list|(
literal|"Validation "
operator|+
name|INTERNAL
argument_list|)
decl_stmt|;
comment|/** 	 * The context for JUnit tests that directly make access not through the other 	 * contexts. 	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|TEST
init|=
operator|new
name|AccessContext
argument_list|(
literal|"JUnit test"
argument_list|)
decl_stmt|;
comment|/** 	 * The context for evaluating XInclude paths. 	 */
specifier|public
specifier|static
specifier|final
name|AccessContext
name|XINCLUDE
init|=
operator|new
name|AccessContext
argument_list|(
literal|"XInclude"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
name|AccessContext
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"The empty constructor is not supported."
argument_list|)
throw|;
block|}
specifier|private
name|AccessContext
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Access context value cannot be null"
argument_list|)
throw|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

