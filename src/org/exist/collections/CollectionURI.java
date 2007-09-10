begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
package|;
end_package

begin_comment
comment|/**  * URI to represent a Collection path internally in eXist  * */
end_comment

begin_class
specifier|public
class|class
name|CollectionURI
block|{
specifier|public
specifier|final
specifier|static
name|char
name|FRAGMENT_SEPARATOR
init|=
literal|'/'
decl_stmt|;
specifier|private
name|char
index|[]
name|uri
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|length
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|hash
decl_stmt|;
comment|// Default to 0
specifier|public
name|CollectionURI
parameter_list|(
name|String
name|path
parameter_list|)
block|{
comment|//    	uri = new char[path.length()];
comment|//        path.getChars(0, path.length(), uri, 0);
comment|//        length = path.length();
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CollectionURI
parameter_list|(
name|CollectionURI
name|other
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
operator|new
name|char
index|[
name|other
operator|.
name|uri
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|uri
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|uri
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|other
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|hash
operator|=
name|other
operator|.
name|hash
expr_stmt|;
block|}
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|String
name|segment
parameter_list|)
block|{
name|int
name|startOffset
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|segment
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|FRAGMENT_SEPARATOR
condition|)
block|{
name|startOffset
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|uri
operator|==
literal|null
condition|)
block|{
name|uri
operator|=
operator|new
name|char
index|[
name|segment
operator|.
name|length
argument_list|()
operator|+
literal|1
operator|-
name|startOffset
index|]
expr_stmt|;
name|uri
index|[
literal|0
index|]
operator|=
name|FRAGMENT_SEPARATOR
expr_stmt|;
name|segment
operator|.
name|getChars
argument_list|(
name|startOffset
argument_list|,
name|segment
operator|.
name|length
argument_list|()
argument_list|,
name|uri
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|char
name|newURI
index|[]
init|=
operator|new
name|char
index|[
name|length
operator|+
literal|1
operator|+
name|segment
operator|.
name|length
argument_list|()
operator|-
name|startOffset
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|uri
argument_list|,
literal|0
argument_list|,
name|newURI
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|newURI
index|[
name|length
index|]
operator|=
name|FRAGMENT_SEPARATOR
expr_stmt|;
name|segment
operator|.
name|getChars
argument_list|(
name|startOffset
argument_list|,
name|segment
operator|.
name|length
argument_list|()
argument_list|,
name|newURI
argument_list|,
name|length
operator|+
literal|1
argument_list|)
expr_stmt|;
name|uri
operator|=
name|newURI
expr_stmt|;
block|}
name|length
operator|+=
name|segment
operator|.
name|length
argument_list|()
operator|+
literal|1
operator|-
name|startOffset
expr_stmt|;
comment|//reset the cache
name|hash
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|void
name|removeLastSegment
parameter_list|()
block|{
name|char
name|c
decl_stmt|;
name|int
name|pos
init|=
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|uri
index|[
name|pos
index|]
operator|)
operator|!=
name|FRAGMENT_SEPARATOR
condition|)
block|{
name|pos
operator|--
expr_stmt|;
block|}
name|length
operator|=
name|pos
expr_stmt|;
comment|//reset the cache
name|hash
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|uri
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**      * Copied from java.lang.String.hashCode();      *       * Returns a hash code for this string. The hash code for a      *<code>String</code> object is computed as      *<blockquote><pre>      * s[0]*31^(n-1) + s[1]*31^(n-2) + ... + s[n-1]      *</pre></blockquote>      * using<code>int</code> arithmetic, where<code>s[i]</code> is the      *<i>i</i>th character of the string,<code>n</code> is the length of      * the string, and<code>^</code> indicates exponentiation.      * (The hash value of the empty string is zero.)      *      * @return  a hash code value for this object.      */
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|hash
decl_stmt|;
if|if
condition|(
name|h
operator|==
literal|0
condition|)
block|{
name|int
name|off
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|uri
index|[
name|off
operator|++
index|]
expr_stmt|;
block|}
name|hash
operator|=
name|h
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|instanceof
name|CollectionURI
condition|)
block|{
name|CollectionURI
name|otherCollectionURI
init|=
operator|(
name|CollectionURI
operator|)
name|object
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|length
operator|==
name|otherCollectionURI
operator|.
name|length
condition|)
block|{
name|int
name|pos
init|=
name|length
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|pos
operator|>
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|uri
index|[
name|pos
index|]
operator|!=
name|otherCollectionURI
operator|.
name|uri
index|[
name|pos
operator|--
index|]
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

