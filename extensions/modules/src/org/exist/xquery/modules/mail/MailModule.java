begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Mail Module Extension  *  Copyright (C) 2006 Adam Retter<adam.retter@devon.gov.uk>  *  www.adamretter.co.uk  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|mail
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
comment|/**  * eXist Mail Module Extension  *   * An extension module for the eXist Native XML Database that allows email to  * be sent from XQuery using either SMTP or Sendmail.    *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-03-01  * @version 1.2  *  * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[])  */
end_comment

begin_class
specifier|public
class|class
name|MailModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/mail"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"mail"
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
name|SendEmailFunction
operator|.
name|signature
argument_list|,
name|SendEmailFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|MailModule
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
name|NAMESPACE_URI
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for performing email related functions"
return|;
block|}
block|}
end_class

end_unit

