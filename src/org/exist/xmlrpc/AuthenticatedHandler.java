begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|Category
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|AuthenticatedXmlRpcHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpc
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Configuration
import|;
end_import

begin_class
specifier|public
class|class
name|AuthenticatedHandler
implements|implements
name|AuthenticatedXmlRpcHandler
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|AuthenticatedXmlRpcHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|RpcAPI
name|handler
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
comment|/**      *  Constructor for the AuthenticatedHandler object      *      *@param  conf                 Description of the Parameter      *@exception  XmlRpcException  Description of the Exception      */
specifier|public
name|AuthenticatedHandler
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|XmlRpcException
block|{
try|try
block|{
name|handler
operator|=
operator|new
name|RpcServer
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XmlRpcException
argument_list|(
literal|0
argument_list|,
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Object
name|execute
parameter_list|(
name|String
name|method
parameter_list|,
name|Vector
name|v
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|Exception
block|{
comment|// assume guest user if no user is specified
comment|// set a password for admin to permit this
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|user
operator|=
literal|"guest"
expr_stmt|;
name|password
operator|=
literal|"guest"
expr_stmt|;
block|}
comment|// check user
name|User
name|u
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|u
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XmlRpcException
argument_list|(
literal|0
argument_list|,
literal|"User "
operator|+
name|user
operator|+
literal|" unknown"
argument_list|)
throw|;
if|if
condition|(
operator|!
name|u
operator|.
name|validate
argument_list|(
name|password
argument_list|)
condition|)
block|{
if|if
condition|(
name|XmlRpc
operator|.
name|debug
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"login denied for user "
operator|+
name|user
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XmlRpcException
argument_list|(
literal|0
argument_list|,
literal|"Invalid password for user "
operator|+
name|user
argument_list|)
throw|;
block|}
if|if
condition|(
name|XmlRpc
operator|.
name|debug
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"user "
operator|+
name|user
operator|+
literal|" logged in"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"calling "
operator|+
name|method
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|v
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"argument "
operator|+
name|i
operator|+
literal|": "
operator|+
name|v
operator|.
name|elementAt
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|execute
argument_list|(
name|u
argument_list|,
name|method
argument_list|,
name|v
argument_list|)
return|;
block|}
specifier|private
name|Object
name|execute
parameter_list|(
name|User
name|user
parameter_list|,
name|String
name|methodName
parameter_list|,
name|Vector
name|params
parameter_list|)
throws|throws
name|Exception
block|{
name|Class
index|[]
name|argClasses
init|=
literal|null
decl_stmt|;
name|Object
index|[]
name|argValues
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
name|argClasses
operator|=
operator|new
name|Class
index|[
name|params
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
expr_stmt|;
name|argValues
operator|=
operator|new
name|Object
index|[
name|params
operator|.
name|size
argument_list|()
operator|+
literal|1
index|]
expr_stmt|;
name|argValues
index|[
literal|0
index|]
operator|=
name|user
expr_stmt|;
name|argClasses
index|[
literal|0
index|]
operator|=
name|User
operator|.
name|class
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|params
operator|.
name|size
argument_list|()
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|argValues
index|[
name|i
index|]
operator|=
name|params
operator|.
name|elementAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|argValues
index|[
name|i
index|]
operator|instanceof
name|Integer
condition|)
name|argClasses
index|[
name|i
index|]
operator|=
name|Integer
operator|.
name|TYPE
expr_stmt|;
if|else if
condition|(
name|argValues
index|[
name|i
index|]
operator|instanceof
name|Double
condition|)
name|argClasses
index|[
name|i
index|]
operator|=
name|Double
operator|.
name|TYPE
expr_stmt|;
if|else if
condition|(
name|argValues
index|[
name|i
index|]
operator|instanceof
name|Boolean
condition|)
name|argClasses
index|[
name|i
index|]
operator|=
name|Boolean
operator|.
name|TYPE
expr_stmt|;
else|else
name|argClasses
index|[
name|i
index|]
operator|=
name|argValues
index|[
name|i
index|]
operator|.
name|getClass
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|argClasses
operator|=
operator|new
name|Class
index|[
literal|1
index|]
expr_stmt|;
name|argValues
operator|=
operator|new
name|Class
index|[
literal|1
index|]
expr_stmt|;
name|argValues
index|[
literal|0
index|]
operator|=
name|user
expr_stmt|;
name|argClasses
index|[
literal|0
index|]
operator|=
name|User
operator|.
name|class
expr_stmt|;
block|}
name|Method
name|method
init|=
literal|null
decl_stmt|;
try|try
block|{
name|method
operator|=
name|RpcAPI
operator|.
name|class
operator|.
name|getMethod
argument_list|(
name|methodName
argument_list|,
name|argClasses
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|nsm_e
parameter_list|)
block|{
throw|throw
name|nsm_e
throw|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|s_e
parameter_list|)
block|{
throw|throw
name|s_e
throw|;
block|}
comment|// Our policy is to make all public methods callable except
comment|// the ones defined in java.lang.Object.
if|if
condition|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
operator|==
name|Object
operator|.
name|class
condition|)
throw|throw
operator|new
name|XmlRpcException
argument_list|(
literal|0
argument_list|,
literal|"Invoker can't call methods "
operator|+
literal|"defined in java.lang.Object"
argument_list|)
throw|;
comment|// invoke
name|Object
name|returnValue
init|=
literal|null
decl_stmt|;
try|try
block|{
name|returnValue
operator|=
name|method
operator|.
name|invoke
argument_list|(
name|handler
argument_list|,
name|argValues
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|iacc_e
parameter_list|)
block|{
throw|throw
name|iacc_e
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iarg_e
parameter_list|)
block|{
throw|throw
name|iarg_e
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|it_e
parameter_list|)
block|{
comment|// check whether the thrown exception is XmlRpcException
name|Throwable
name|t
init|=
name|getCause
argument_list|(
name|it_e
argument_list|)
decl_stmt|;
if|if
condition|(
name|XmlRpc
operator|.
name|debug
condition|)
name|t
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|Exception
condition|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|t
throw|;
block|}
else|else
throw|throw
operator|new
name|Exception
argument_list|(
name|t
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Throwable
name|t
init|=
name|getCause
argument_list|(
name|e
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|Exception
condition|)
throw|throw
operator|(
name|Exception
operator|)
name|t
throw|;
else|else
throw|throw
name|e
throw|;
block|}
return|return
name|returnValue
return|;
block|}
specifier|private
specifier|final
specifier|static
name|Throwable
name|getCause
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|Throwable
name|t
decl_stmt|;
while|while
condition|(
operator|(
name|t
operator|=
name|e
operator|.
name|getCause
argument_list|()
operator|)
operator|!=
literal|null
condition|)
name|e
operator|=
name|t
expr_stmt|;
return|return
name|e
return|;
block|}
block|}
end_class

end_unit

