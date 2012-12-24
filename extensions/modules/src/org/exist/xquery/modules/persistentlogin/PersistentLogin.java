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
name|util
operator|.
name|Base64Encoder
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
name|XPathException
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
name|DateTimeValue
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
name|DurationValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|SecureRandom
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A persistent login feature ("remember me") similar to the implementation in<a href="https://github.com/SpringSource/spring-security">Spring Security</a>,  * which is based on<a href="http://jaspan.com/improved_persistent_login_cookie_best_practice">Improved Persistent Login Cookie  * Best Practice</a> .  *  * The one-time tokens generated by this class are purely random and do not contain a user name or other information. For security reasons,  * tokens and user information are not stored anywhere, so if the database is shut down, registered tokens will be gone.  *  * The one-time token approach has the negative effect that requests need to be made in sequence, which is sometimes difficult if an app uses  * concurrent AJAX requests. Unfortunately, this is the price we have to pay for a sufficiently secure protection against  * cookie stealing attacks.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|PersistentLogin
block|{
specifier|private
specifier|final
specifier|static
name|PersistentLogin
name|instance
init|=
operator|new
name|PersistentLogin
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|PersistentLogin
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
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
name|PersistentLogin
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_SERIES_LENGTH
init|=
literal|16
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_TOKEN_LENGTH
init|=
literal|16
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|INVALIDATION_TIMEOUT
init|=
literal|20000
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|LoginDetails
argument_list|>
name|seriesMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|LoginDetails
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|SecureRandom
name|random
decl_stmt|;
specifier|public
name|PersistentLogin
parameter_list|()
block|{
name|random
operator|=
operator|new
name|SecureRandom
argument_list|()
expr_stmt|;
block|}
comment|/**      * Register the user and generate a first login token which will be valid for the next      * call to {@link #lookup(String)}.      *      * The generated token will have the format base64(series-hash):base64(token-hash).      *      * @param user the user name      * @param password the password      * @param timeToLive timeout of the token      * @return a first login token      * @throws XPathException      */
specifier|public
name|LoginDetails
name|register
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|DurationValue
name|timeToLive
parameter_list|)
throws|throws
name|XPathException
block|{
name|DateTimeValue
name|now
init|=
operator|new
name|DateTimeValue
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
decl_stmt|;
name|DateTimeValue
name|expires
init|=
operator|(
name|DateTimeValue
operator|)
name|now
operator|.
name|plus
argument_list|(
name|timeToLive
argument_list|)
decl_stmt|;
name|LoginDetails
name|login
init|=
operator|new
name|LoginDetails
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|timeToLive
argument_list|,
name|expires
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
decl_stmt|;
name|seriesMap
operator|.
name|put
argument_list|(
name|login
operator|.
name|getSeries
argument_list|()
argument_list|,
name|login
argument_list|)
expr_stmt|;
return|return
name|login
return|;
block|}
comment|/**      * Look up the given token and return login details. If the token is found, it will be updated      * with a new hash before returning and the old hash is removed.      *      * @param token the token string provided by the user      * @return login details for the user or null if no session was found or it was expired      * @throws XPathException series matched but the token not. may indicate a cookie theft attack      * or an out-of-sequence request.      */
specifier|public
name|LoginDetails
name|lookup
parameter_list|(
name|String
name|token
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
index|[]
name|tokens
init|=
name|token
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|LoginDetails
name|data
init|=
name|seriesMap
operator|.
name|get
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No session found for series "
operator|+
name|tokens
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|>
name|data
operator|.
name|expires
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Persistent session expired"
argument_list|)
expr_stmt|;
name|seriesMap
operator|.
name|remove
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|data
operator|.
name|checkAndUpdateToken
argument_list|(
name|tokens
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Token mismatch. This may indicate an out-of-sequence request (likely) or a cookie theft attack.  "
operator|+
literal|"Session is deleted for security reasons."
argument_list|)
throw|;
block|}
return|return
name|data
return|;
block|}
comment|/**      * Invalidate the session associated with the token string. Looks up the series hash      * and deletes it.      *      * @param token token string provided by the user      */
specifier|public
name|void
name|invalidate
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|String
index|[]
name|tokens
init|=
name|token
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|seriesMap
operator|.
name|remove
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|generateSeriesToken
parameter_list|()
block|{
name|byte
index|[]
name|newSeries
init|=
operator|new
name|byte
index|[
name|DEFAULT_SERIES_LENGTH
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|newSeries
argument_list|)
expr_stmt|;
name|Base64Encoder
name|encoder
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|encoder
operator|.
name|translate
argument_list|(
name|newSeries
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|encoder
operator|.
name|getCharArray
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|generateToken
parameter_list|()
block|{
name|byte
index|[]
name|newSeries
init|=
operator|new
name|byte
index|[
name|DEFAULT_TOKEN_LENGTH
index|]
decl_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|newSeries
argument_list|)
expr_stmt|;
name|Base64Encoder
name|encoder
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|encoder
operator|.
name|translate
argument_list|(
name|newSeries
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|encoder
operator|.
name|getCharArray
argument_list|()
argument_list|)
return|;
block|}
specifier|public
class|class
name|LoginDetails
block|{
specifier|private
name|String
name|userName
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|String
name|token
decl_stmt|;
specifier|private
name|String
name|series
decl_stmt|;
specifier|private
name|long
name|expires
decl_stmt|;
specifier|private
name|DurationValue
name|timeToLive
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|invalidatedTokens
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|LoginDetails
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|DurationValue
name|timeToLive
parameter_list|,
name|long
name|expires
parameter_list|)
block|{
name|this
operator|.
name|userName
operator|=
name|user
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|timeToLive
operator|=
name|timeToLive
expr_stmt|;
name|this
operator|.
name|expires
operator|=
name|expires
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|generateToken
argument_list|()
expr_stmt|;
name|this
operator|.
name|series
operator|=
name|generateSeriesToken
argument_list|()
expr_stmt|;
block|}
specifier|public
name|String
name|getToken
parameter_list|()
block|{
return|return
name|this
operator|.
name|token
return|;
block|}
specifier|public
name|String
name|getSeries
parameter_list|()
block|{
return|return
name|this
operator|.
name|series
return|;
block|}
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|this
operator|.
name|userName
return|;
block|}
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|this
operator|.
name|password
return|;
block|}
specifier|public
name|DurationValue
name|getTimeToLive
parameter_list|()
block|{
return|return
name|timeToLive
return|;
block|}
specifier|public
name|boolean
name|checkAndUpdateToken
parameter_list|(
name|String
name|token
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|token
operator|.
name|equals
argument_list|(
name|token
argument_list|)
condition|)
block|{
name|update
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|// check map of invalidating tokens
name|Long
name|timeout
init|=
name|invalidatedTokens
operator|.
name|get
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|timeout
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// timed out: remove
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|>
name|timeout
condition|)
block|{
name|invalidatedTokens
operator|.
name|remove
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// found invalidating token: return true but do not replace token
return|return
literal|true
return|;
block|}
specifier|public
name|String
name|update
parameter_list|()
block|{
name|timeoutCheck
argument_list|()
expr_stmt|;
comment|// leave a small time window until previous token is deleted
comment|// to allow for concurrent requests
name|invalidatedTokens
operator|.
name|put
argument_list|(
name|this
operator|.
name|token
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|INVALIDATION_TIMEOUT
argument_list|)
expr_stmt|;
name|this
operator|.
name|token
operator|=
name|generateToken
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|token
return|;
block|}
specifier|private
name|void
name|timeoutCheck
parameter_list|()
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|i
init|=
name|invalidatedTokens
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|entry
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|>
name|now
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|series
operator|+
literal|":"
operator|+
name|this
operator|.
name|token
return|;
block|}
block|}
block|}
end_class

end_unit

