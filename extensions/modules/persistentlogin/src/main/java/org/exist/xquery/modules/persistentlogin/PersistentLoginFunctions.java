begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|persistentlogin
package|;
end_package

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
name|*
import|;
end_import

begin_comment
comment|/**  * Functions to access the persistent login module.  */
end_comment

begin_class
specifier|public
class|class
name|PersistentLoginFunctions
extends|extends
name|UserSwitchingBasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"register"
argument_list|,
name|PersistentLoginModule
operator|.
name|NAMESPACE
argument_list|,
name|PersistentLoginModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Try to log in the user and create a one-time login token. The token can be stored to a cookie and used to log in "
operator|+
literal|"(via the login function) as the same user without "
operator|+
literal|"providing credentials. However, for security reasons the token will be valid only for "
operator|+
literal|"the next request to the login function and is deleted afterwards. "
operator|+
literal|"If the user is valid and the token could be generated, the "
operator|+
literal|"supplied callback function is called with 4 arguments: $token as xs:string, $user as xs:string, $password as xs:string, "
operator|+
literal|"$timeToLive as xs:duration."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"user"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"user name"
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
literal|"password"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"timeToLive"
argument_list|,
name|Type
operator|.
name|DURATION
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"duration for which the user is remembered"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"onLogin"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"callback function to be called when the login succeeds"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"result of the callback function or the empty sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"login"
argument_list|,
name|PersistentLoginModule
operator|.
name|NAMESPACE
argument_list|,
name|PersistentLoginModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Try to log in the user based on the supplied token. If the login succeeds, the provided callback function "
operator|+
literal|"is called with 4 arguments: $token as xs:string, $user as xs:string, $password as xs:string, $timeToLive as duration. "
operator|+
literal|"$token will be a new token which can be used for the next request. The old token is deleted."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"token"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"a valid one-time token"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"onLogin"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"callback function to be called when the login succeeds"
argument_list|)
block|,                     }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"result of the callback function or the empty sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"invalidate"
argument_list|,
name|PersistentLoginModule
operator|.
name|NAMESPACE
argument_list|,
name|PersistentLoginModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Invalidate the supplied one-time token, so it can no longer be used to log in."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"token"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"a valid one-time token"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"empty sequence"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|private
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|PersistentLoginFunctions
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
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
name|void
name|analyze
parameter_list|(
specifier|final
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|this
operator|.
name|cachedContextInfo
operator|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
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
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"register"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|user
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
name|pass
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|pass
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|pass
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|DurationValue
name|timeToLive
init|=
operator|(
name|DurationValue
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|FunctionReference
name|callback
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
name|callback
operator|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|callback
operator|=
literal|null
expr_stmt|;
block|}
try|try
block|{
return|return
name|register
argument_list|(
name|user
argument_list|,
name|pass
argument_list|,
name|timeToLive
argument_list|,
name|callback
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"login"
argument_list|)
condition|)
block|{
specifier|final
name|String
name|token
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
name|FunctionReference
name|callback
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|callback
operator|=
operator|(
name|FunctionReference
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
expr_stmt|;
block|}
else|else
block|{
name|callback
operator|=
literal|null
expr_stmt|;
block|}
try|try
block|{
return|return
name|authenticate
argument_list|(
name|token
argument_list|,
name|callback
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|callback
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|PersistentLogin
operator|.
name|getInstance
argument_list|()
operator|.
name|invalidate
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
specifier|private
name|Sequence
name|register
parameter_list|(
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|pass
parameter_list|,
specifier|final
name|DurationValue
name|timeToLive
parameter_list|,
specifier|final
name|FunctionReference
name|callback
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|login
argument_list|(
name|user
argument_list|,
name|pass
argument_list|)
condition|)
block|{
specifier|final
name|PersistentLogin
operator|.
name|LoginDetails
name|details
init|=
name|PersistentLogin
operator|.
name|getInstance
argument_list|()
operator|.
name|register
argument_list|(
name|user
argument_list|,
name|pass
argument_list|,
name|timeToLive
argument_list|)
decl_stmt|;
return|return
name|callback
argument_list|(
name|callback
argument_list|,
literal|null
argument_list|,
name|details
argument_list|)
return|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|Sequence
name|authenticate
parameter_list|(
specifier|final
name|String
name|token
parameter_list|,
specifier|final
name|FunctionReference
name|callback
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|PersistentLogin
operator|.
name|LoginDetails
name|data
init|=
name|PersistentLogin
operator|.
name|getInstance
argument_list|()
operator|.
name|lookup
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
name|login
argument_list|(
name|data
operator|.
name|getUser
argument_list|()
argument_list|,
name|data
operator|.
name|getPassword
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|callback
argument_list|(
name|callback
argument_list|,
name|token
argument_list|,
name|data
argument_list|)
return|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|boolean
name|login
parameter_list|(
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|pass
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|SecurityManager
name|sm
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|subject
init|=
name|sm
operator|.
name|authenticate
argument_list|(
name|user
argument_list|,
name|pass
argument_list|)
decl_stmt|;
comment|//switch the user of the current broker
name|switchUser
argument_list|(
name|subject
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
decl||
name|EXistException
name|e
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|Sequence
name|callback
parameter_list|(
specifier|final
name|FunctionReference
name|func
parameter_list|,
specifier|final
name|String
name|oldToken
parameter_list|,
specifier|final
name|PersistentLogin
operator|.
name|LoginDetails
name|details
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
index|[]
name|args
init|=
operator|new
name|Sequence
index|[
literal|4
index|]
decl_stmt|;
specifier|final
name|String
name|newToken
init|=
name|details
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|oldToken
operator|!=
literal|null
operator|&&
name|oldToken
operator|.
name|equals
argument_list|(
name|newToken
argument_list|)
condition|)
block|{
name|args
index|[
literal|0
index|]
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
name|args
index|[
literal|0
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|newToken
argument_list|)
expr_stmt|;
block|}
name|args
index|[
literal|1
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|details
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|args
index|[
literal|2
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|details
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|args
index|[
literal|3
index|]
operator|=
name|details
operator|.
name|getTimeToLive
argument_list|()
expr_stmt|;
name|func
operator|.
name|analyze
argument_list|(
name|cachedContextInfo
argument_list|)
expr_stmt|;
return|return
name|func
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|args
argument_list|)
return|;
block|}
block|}
end_class

end_unit
