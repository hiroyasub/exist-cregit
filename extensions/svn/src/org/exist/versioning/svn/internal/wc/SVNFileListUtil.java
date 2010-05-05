begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ====================================================================  * Copyright (c) 2004-2010 TMate Software Ltd.  All rights reserved.  *  * This software is licensed as described in the file COPYING, which  * you should have received as part of this distribution.  The terms  * are also available at http://svnkit.com/license.html  * If newer versions of this license are posted there, you may use a  * newer version instead, at your option.  * ====================================================================  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|internal
operator|.
name|wc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|internal
operator|.
name|util
operator|.
name|SVNHashMap
import|;
end_import

begin_comment
comment|/**  * @version 1.3  * @author  TMate Software Ltd.  */
end_comment

begin_class
specifier|public
class|class
name|SVNFileListUtil
block|{
comment|/**      * This method is a replacement for file.list(), which composes decomposed file names (e.g. umlauts in file names on the Mac).      */
specifier|private
specifier|static
name|String
index|[]
name|list
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
return|return
name|directory
operator|.
name|list
argument_list|()
return|;
block|}
comment|/**      * This method is a replacement for file.listFiles(), which composes decomposed file names (e.g. umlauts in file names on the Mac).      */
specifier|public
specifier|static
name|File
index|[]
name|listFiles
parameter_list|(
name|File
name|directory
parameter_list|)
block|{
specifier|final
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|()
decl_stmt|;
return|return
name|files
operator|!=
literal|null
condition|?
name|sort
argument_list|(
name|files
argument_list|)
else|:
literal|null
return|;
block|}
specifier|private
specifier|static
name|File
index|[]
name|sort
parameter_list|(
name|File
index|[]
name|files
parameter_list|)
block|{
name|Map
name|map
init|=
operator|new
name|SVNHashMap
argument_list|()
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
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|map
operator|.
name|put
argument_list|(
name|files
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|files
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|(
name|File
index|[]
operator|)
name|map
operator|.
name|values
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|File
index|[
name|map
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|String
name|compose
parameter_list|(
name|String
name|decomposedString
parameter_list|)
block|{
if|if
condition|(
name|decomposedString
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|StringBuffer
name|buffer
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|,
name|length
init|=
name|decomposedString
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|chr
init|=
name|decomposedString
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|chr
operator|==
literal|'\u0300'
condition|)
block|{
comment|// grave `
name|buffer
operator|=
name|compose
argument_list|(
name|i
argument_list|,
literal|"AaEeIiOoUu"
argument_list|,
literal|"\u00C0\u00E0\u00C8\u00E8\u00CC\u00EC\u00D2\u00F2\u00D9\u00F9"
argument_list|,
name|decomposedString
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|chr
operator|==
literal|'\u0301'
condition|)
block|{
comment|// acute '
name|buffer
operator|=
name|compose
argument_list|(
name|i
argument_list|,
literal|"AaEeIiOoUuYy"
argument_list|,
literal|"\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00DD\u00FD"
argument_list|,
name|decomposedString
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|chr
operator|==
literal|'\u0302'
condition|)
block|{
comment|// circumflex ^
name|buffer
operator|=
name|compose
argument_list|(
name|i
argument_list|,
literal|"AaEeIiOoUuYy"
argument_list|,
literal|"\u00C2\u00E2\u00CA\u00EA\u00CE\u00EE\u00D4\u00F4\u00DB\u00FB\u0176\u0177"
argument_list|,
name|decomposedString
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|chr
operator|==
literal|'\u0303'
condition|)
block|{
comment|// tilde ~
name|buffer
operator|=
name|compose
argument_list|(
name|i
argument_list|,
literal|"AaNnOoUu"
argument_list|,
literal|"\u00C3\u00E3\u00D1\u00F1\u00D5\u00F5\u0168\u0169"
argument_list|,
name|decomposedString
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|chr
operator|==
literal|'\u0308'
condition|)
block|{
comment|// umlaut/dieresis (two dots above)
name|buffer
operator|=
name|compose
argument_list|(
name|i
argument_list|,
literal|"AaEeIiOoUuYy"
argument_list|,
literal|"\u00C4\u00E4\u00CB\u00EB\u00CF\u00EF\u00D6\u00F6\u00DC\u00FC\u0178\u00FF"
argument_list|,
name|decomposedString
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|chr
operator|==
literal|'\u030A'
condition|)
block|{
comment|// ring above (as in Angstrom)
name|buffer
operator|=
name|compose
argument_list|(
name|i
argument_list|,
literal|"Aa"
argument_list|,
literal|"\u00C5\u00E5"
argument_list|,
name|decomposedString
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|chr
operator|==
literal|'\u0327'
condition|)
block|{
comment|// cedilla ,
name|buffer
operator|=
name|compose
argument_list|(
name|i
argument_list|,
literal|"Cc"
argument_list|,
literal|"\u00C7\u00E7"
argument_list|,
name|decomposedString
argument_list|,
name|buffer
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|buffer
operator|!=
literal|null
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|chr
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
return|return
name|decomposedString
return|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// Utils ==================================================================
specifier|private
specifier|static
name|StringBuffer
name|compose
parameter_list|(
name|int
name|i
parameter_list|,
name|String
name|decomposedChars
parameter_list|,
name|String
name|composedChars
parameter_list|,
name|String
name|decomposedString
parameter_list|,
name|StringBuffer
name|buffer
parameter_list|)
block|{
specifier|final
name|char
name|previousChar
init|=
name|decomposedString
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|int
name|decomposedIndex
init|=
name|decomposedChars
operator|.
name|indexOf
argument_list|(
name|previousChar
argument_list|)
decl_stmt|;
if|if
condition|(
name|decomposedIndex
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
operator|new
name|StringBuffer
argument_list|(
name|decomposedString
operator|.
name|length
argument_list|()
operator|+
literal|2
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|decomposedString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|delete
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|composedChars
operator|.
name|charAt
argument_list|(
name|decomposedIndex
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|buffer
operator|==
literal|null
condition|)
block|{
name|buffer
operator|=
operator|new
name|StringBuffer
argument_list|(
name|decomposedString
operator|.
name|length
argument_list|()
operator|+
literal|2
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|decomposedString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buffer
return|;
block|}
block|}
end_class

end_unit

