begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|KeyManagementException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|HostnameVerifier
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|HttpsURLConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|TrustManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|X509TrustManager
import|;
end_import

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

begin_comment
comment|/**  *  Helper class for accepting self-signed SSL certificates.  *   * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|SSLHelper
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SSLHelper
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|TrustManager
index|[]
name|nonvalidatingTrustManager
init|=
literal|null
decl_stmt|;
specifier|private
name|HostnameVerifier
name|dummyHostnameVerifier
init|=
literal|null
decl_stmt|;
comment|/**      * Initializing constructor.      */
specifier|public
name|SSLHelper
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initialize"
argument_list|)
expr_stmt|;
comment|// Create trust manager that does not validate certificate chains
name|nonvalidatingTrustManager
operator|=
operator|new
name|TrustManager
index|[]
block|{
operator|new
name|X509TrustManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|X509Certificate
index|[]
name|getAcceptedIssuers
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
expr|@
name|Override
specifier|public
name|void
name|checkClientTrusted
argument_list|(
name|X509Certificate
index|[]
name|certs
argument_list|,
name|String
name|authType
argument_list|)
block|{
comment|// Always trust
block|}
expr|@
name|Override
specifier|public
name|void
name|checkServerTrusted
argument_list|(
name|X509Certificate
index|[]
name|certs
argument_list|,
name|String
name|authType
argument_list|)
block|{
comment|// Alway trust
block|}
block|}
block|}
end_class

begin_empty_stmt
empty_stmt|;
end_empty_stmt

begin_comment
comment|// Create dummy HostnameVerifier
end_comment

begin_expr_stmt
name|dummyHostnameVerifier
operator|=
operator|new
name|HostnameVerifier
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|verify
parameter_list|(
name|String
name|hostname
parameter_list|,
name|SSLSession
name|session
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
expr_stmt|;
end_expr_stmt

begin_comment
unit|}
comment|/**      *  Initialize HttpsURLConnection with a non validating SSL trust manager and a      * dummy hostname verifier.      *       * @param sslAllowSelfsigned    Set to TRUE to allow selfsigned certificates      * @param sslVerifyHostname     Set to FALSE for not verifying hostnames.      * @return       */
end_comment

begin_function
unit|public
name|boolean
name|initialize
parameter_list|(
name|boolean
name|sslAllowSelfsigned
parameter_list|,
name|boolean
name|sslVerifyHostname
parameter_list|)
block|{
name|SSLContext
name|sc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|sc
operator|=
name|SSLContext
operator|.
name|getInstance
argument_list|(
literal|"SSL"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to initialize SSL."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Set accept of selfsigned certificates
if|if
condition|(
name|sslAllowSelfsigned
condition|)
block|{
try|try
block|{
comment|// Install the all-trusting trust manager
name|LOG
operator|.
name|debug
argument_list|(
literal|"Installing SSL trust manager"
argument_list|)
expr_stmt|;
name|sc
operator|.
name|init
argument_list|(
literal|null
argument_list|,
name|nonvalidatingTrustManager
argument_list|,
operator|new
name|java
operator|.
name|security
operator|.
name|SecureRandom
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|KeyManagementException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to initialize keychain validation."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|//
name|HttpsURLConnection
operator|.
name|setDefaultSSLSocketFactory
argument_list|(
name|sc
operator|.
name|getSocketFactory
argument_list|()
argument_list|)
expr_stmt|;
comment|// Set dummy hostname verifier
if|if
condition|(
operator|!
name|sslVerifyHostname
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Registering hostname verifier"
argument_list|)
expr_stmt|;
name|HttpsURLConnection
operator|.
name|setDefaultHostnameVerifier
argument_list|(
name|dummyHostnameVerifier
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
end_function

unit|}
end_unit

