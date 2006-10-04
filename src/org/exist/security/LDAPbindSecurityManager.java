begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * LDAPbindSecurityManager.java  *  * Created on September 11, 2006, 9:00 AM  *  * (C) Andrew Hart  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|InitialDirContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
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
comment|/**  * Code to use LDAP's bind to authenticate technology  * @author Andrew Hart  */
end_comment

begin_class
specifier|public
class|class
name|LDAPbindSecurityManager
extends|extends
name|LDAPSecurityManager
implements|implements
name|SecurityManager
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
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|boolean
name|bind
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|passwd
parameter_list|)
block|{
comment|/**        * @param user         * @param passwd        */
name|Hashtable
name|env
init|=
name|getDirectoryEnvironment
argument_list|()
decl_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_CREDENTIALS
argument_list|,
name|passwd
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|SECURITY_PRINCIPAL
argument_list|,
literal|"uid="
operator|+
name|user
operator|+
literal|","
operator|+
name|userBase
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Create initial context
name|DirContext
name|ctx
init|=
operator|new
name|InitialDirContext
argument_list|(
name|env
argument_list|)
decl_stmt|;
comment|// Check that the credentials work
name|LOG
operator|.
name|info
argument_list|(
name|ctx
operator|.
name|lookup
argument_list|(
literal|"uid="
operator|+
name|user
operator|+
literal|","
operator|+
name|userBase
argument_list|)
argument_list|)
expr_stmt|;
name|ctx
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid Credentials for user: uid="
operator|+
name|user
operator|+
literal|","
operator|+
name|userBase
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"User "
operator|+
name|user
operator|+
literal|", bind successful."
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

