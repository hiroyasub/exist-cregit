begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|filter
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
name|javax
operator|.
name|servlet
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSession
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
import|;
end_import

begin_comment
comment|/**  * Created by IntelliJ IDEA.  * User: lcahlander  * Date: Aug 24, 2010  * Time: 2:06:13 PM  * To change this template use File | Settings | File Templates.  */
end_comment

begin_class
specifier|public
class|class
name|GuestFilter
implements|implements
name|Filter
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
name|GuestFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|String
name|sslPort
init|=
literal|null
decl_stmt|;
specifier|private
name|FilterConfig
name|filterConfig
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|init
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
throws|throws
name|ServletException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting GuestFilter"
argument_list|)
expr_stmt|;
name|setFilterConfig
argument_list|(
name|filterConfig
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|doFilter
parameter_list|(
name|ServletRequest
name|servletRequest
parameter_list|,
name|ServletResponse
name|servletResponse
parameter_list|,
name|FilterChain
name|filterChain
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
name|HttpServletRequest
name|httpServletRequest
decl_stmt|;
name|HttpServletResponse
name|httpServletResponse
decl_stmt|;
if|if
condition|(
name|servletRequest
operator|instanceof
name|HttpServletRequest
condition|)
block|{
name|httpServletRequest
operator|=
operator|(
name|HttpServletRequest
operator|)
name|servletRequest
expr_stmt|;
name|httpServletResponse
operator|=
operator|(
name|HttpServletResponse
operator|)
name|servletResponse
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"HTTP Servlet Request confirmed"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Servlet Request confirmed"
argument_list|)
expr_stmt|;
name|filterChain
operator|.
name|doFilter
argument_list|(
name|servletRequest
argument_list|,
name|servletResponse
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|username
init|=
name|httpServletRequest
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
name|String
name|requestURI
init|=
name|httpServletRequest
operator|.
name|getRequestURI
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|sessionID
init|=
name|httpServletRequest
operator|.
name|getRequestedSessionId
argument_list|()
decl_stmt|;
name|HttpSession
name|session
init|=
name|httpServletRequest
operator|.
name|getSession
argument_list|(
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"session: "
operator|+
name|session
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Enumeration
name|enumeration
init|=
name|session
operator|.
name|getAttributeNames
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumeration
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|enumeration
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|session
operator|.
name|getAttribute
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"session attribute ["
operator|+
name|key
operator|+
literal|"]["
operator|+
name|value
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|key
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"_eXist_xmldb_user"
argument_list|)
condition|)
block|{
name|username
operator|=
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
operator|.
name|SubjectImpl
operator|)
name|value
operator|)
operator|.
name|getUsername
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"username ["
operator|+
name|username
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No valid session"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"username ["
operator|+
name|username
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"requestURI ["
operator|+
name|requestURI
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|requestURI
operator|.
name|startsWith
argument_list|(
literal|"/webdav"
argument_list|)
operator|||
name|requestURI
operator|.
name|startsWith
argument_list|(
literal|"/xmlrpc"
argument_list|)
condition|)
block|{
if|if
condition|(
name|username
operator|!=
literal|null
operator|&&
name|username
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"guest"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Permission denied to : "
operator|+
name|requestURI
argument_list|)
expr_stmt|;
name|httpServletResponse
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
operator|!
name|httpServletRequest
operator|.
name|isSecure
argument_list|()
condition|)
block|{
name|String
name|serverName
init|=
name|httpServletRequest
operator|.
name|getServerName
argument_list|()
decl_stmt|;
name|String
name|path
init|=
name|httpServletRequest
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|String
name|newpath
init|=
literal|"https://"
operator|+
name|serverName
operator|+
literal|":"
operator|+
name|sslPort
operator|+
name|path
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Redirecting to SSL: "
operator|+
name|newpath
argument_list|)
expr_stmt|;
name|httpServletResponse
operator|.
name|sendRedirect
argument_list|(
name|newpath
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|httpServletRequest
operator|.
name|isSecure
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Request is appropriate"
argument_list|)
expr_stmt|;
name|filterChain
operator|.
name|doFilter
argument_list|(
name|servletRequest
argument_list|,
name|servletResponse
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
name|filterChain
operator|.
name|doFilter
argument_list|(
name|servletRequest
argument_list|,
name|servletResponse
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Ending GuestFilter"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FilterConfig
name|getFilterConfig
parameter_list|()
block|{
return|return
name|filterConfig
return|;
block|}
specifier|public
name|void
name|setFilterConfig
parameter_list|(
name|FilterConfig
name|filterConfig
parameter_list|)
block|{
name|this
operator|.
name|filterConfig
operator|=
name|filterConfig
expr_stmt|;
name|Enumeration
name|initParams
init|=
name|filterConfig
operator|.
name|getInitParameterNames
argument_list|()
decl_stmt|;
comment|// no initial parameters, so invoke the next filter in the chain
if|if
condition|(
name|initParams
operator|!=
literal|null
condition|)
block|{
name|sslPort
operator|=
literal|"443"
expr_stmt|;
while|while
condition|(
name|initParams
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|initParams
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|filterConfig
operator|.
name|getInitParameter
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Parameter ["
operator|+
name|name
operator|+
literal|"]["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"sslport"
argument_list|)
condition|)
block|{
name|sslPort
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

