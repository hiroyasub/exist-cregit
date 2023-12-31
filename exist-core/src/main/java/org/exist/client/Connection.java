begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
package|;
end_package

begin_comment
comment|/**  * Represents the Connection detail for  * connecting to either a local or remote eXist-db instance  *   * You can have either:  *  1) Remote Connection, provide a uri and ssl flag.  *  2) Embedded Connection, provide a configuration file path.  *   * The two settings are mutually exclusive, when using Remote,  * configuration is set to "". Likewise when using  * Embedded, uri is set to "" and ssl is set to false  * TODO subclass into RemoteConnection and EmbeddedConnection  */
end_comment

begin_class
specifier|public
class|class
name|Connection
block|{
specifier|private
specifier|final
name|String
name|username
decl_stmt|;
specifier|private
specifier|final
name|String
name|password
decl_stmt|;
comment|/* remote mode */
specifier|protected
name|String
name|uri
decl_stmt|;
specifier|protected
name|boolean
name|ssl
decl_stmt|;
comment|/** path to an alternate configuration file for embedded mode */
specifier|protected
name|String
name|configuration
decl_stmt|;
specifier|public
name|Connection
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|boolean
name|ssl
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
name|this
operator|.
name|ssl
operator|=
name|ssl
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
literal|""
expr_stmt|;
block|}
specifier|public
name|Connection
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|String
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|username
operator|=
name|username
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|uri
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|ssl
operator|=
literal|false
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
block|}
comment|/**      * Returns the username.      *      * @return the username      */
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|username
return|;
block|}
comment|/**      * Returns the password.      *      * @return the password      */
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
comment|/**      * Returns the uri.      *      * @return the uri      */
specifier|public
name|String
name|getUri
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
comment|/**      * Returns the configuration file path for emebeded mode.      *      * @return the url      */
specifier|public
name|String
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
comment|/**      *  Returns whether to use SSL or not      *      * @return true if SSL should be enabled.      */
specifier|public
name|boolean
name|isSsl
parameter_list|()
block|{
return|return
name|ssl
return|;
block|}
block|}
end_class

end_unit

