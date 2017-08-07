begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  */
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
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
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
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Faster string implementation which uses a CharArrayPool to  * pool the backing char arrays.  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
specifier|final
class|class
name|XMLString
implements|implements
name|CharSequence
implements|,
name|Comparable
argument_list|<
name|CharSequence
argument_list|>
block|{
specifier|public
specifier|final
specifier|static
name|int
name|SUPPRESS_NONE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SUPPRESS_LEADING_WS
init|=
literal|0x01
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SUPPRESS_TRAILING_WS
init|=
literal|0x02
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|COLLAPSE_WS
init|=
literal|0x04
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SUPPRESS_BOTH
init|=
name|SUPPRESS_LEADING_WS
operator||
name|SUPPRESS_TRAILING_WS
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NORMALIZE
init|=
name|SUPPRESS_LEADING_WS
operator||
name|SUPPRESS_TRAILING_WS
operator||
name|COLLAPSE_WS
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DEFAULT_CAPACITY
init|=
literal|16
decl_stmt|;
specifier|private
name|char
index|[]
name|value_
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|start_
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|length_
init|=
literal|0
decl_stmt|;
specifier|public
name|XMLString
parameter_list|()
block|{
name|value_
operator|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|DEFAULT_CAPACITY
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XMLString
parameter_list|(
specifier|final
name|int
name|capacity
parameter_list|)
block|{
name|value_
operator|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XMLString
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|)
block|{
name|value_
operator|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|value_
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
name|length_
operator|=
name|ch
operator|.
name|length
expr_stmt|;
block|}
specifier|public
name|XMLString
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
block|{
name|value_
operator|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|value_
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|length_
operator|=
name|length
expr_stmt|;
block|}
specifier|public
name|XMLString
parameter_list|(
specifier|final
name|XMLString
name|other
parameter_list|)
block|{
name|value_
operator|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|other
operator|.
name|length_
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|value_
argument_list|,
name|other
operator|.
name|start_
argument_list|,
name|value_
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|length_
argument_list|)
expr_stmt|;
name|length_
operator|=
name|other
operator|.
name|length_
expr_stmt|;
block|}
specifier|public
specifier|final
name|XMLString
name|append
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
name|append
argument_list|(
name|str
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|append
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|)
block|{
name|append
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|append
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
name|ensureCapacity
argument_list|(
name|length_
operator|+
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ch
argument_list|,
name|offset
argument_list|,
name|value_
argument_list|,
name|length_
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|length_
operator|+=
name|len
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|append
parameter_list|(
specifier|final
name|XMLString
name|other
parameter_list|)
block|{
name|ensureCapacity
argument_list|(
name|length_
operator|+
name|other
operator|.
name|length_
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|value_
argument_list|,
name|other
operator|.
name|start_
argument_list|,
name|value_
argument_list|,
name|length_
argument_list|,
name|other
operator|.
name|length_
argument_list|)
expr_stmt|;
name|length_
operator|+=
name|other
operator|.
name|length_
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|append
parameter_list|(
specifier|final
name|char
name|ch
parameter_list|)
block|{
if|if
condition|(
name|value_
operator|.
name|length
operator|<
name|length_
operator|+
literal|2
condition|)
block|{
name|ensureCapacity
argument_list|(
name|length_
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|value_
index|[
name|length_
operator|++
index|]
operator|=
name|ch
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|void
name|setData
parameter_list|(
specifier|final
name|char
index|[]
name|ch
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
name|length_
operator|=
literal|0
expr_stmt|;
name|start_
operator|=
literal|0
expr_stmt|;
name|append
argument_list|(
name|ch
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|XMLString
name|normalize
parameter_list|(
specifier|final
name|int
name|mode
parameter_list|)
block|{
if|if
condition|(
name|length_
operator|==
literal|0
condition|)
block|{
return|return
name|this
return|;
block|}
if|if
condition|(
operator|(
name|mode
operator|&
name|SUPPRESS_LEADING_WS
operator|)
operator|!=
literal|0
condition|)
block|{
while|while
condition|(
name|length_
operator|>
literal|1
operator|&&
name|isWhiteSpace
argument_list|(
name|value_
index|[
name|start_
index|]
argument_list|)
condition|)
block|{
operator|--
name|length_
expr_stmt|;
if|if
condition|(
name|length_
operator|>
literal|0
condition|)
block|{
operator|++
name|start_
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|(
name|mode
operator|&
name|SUPPRESS_TRAILING_WS
operator|)
operator|!=
literal|0
condition|)
block|{
while|while
condition|(
name|length_
operator|>
literal|1
operator|&&
name|isWhiteSpace
argument_list|(
name|value_
index|[
name|start_
operator|+
name|length_
operator|-
literal|1
index|]
argument_list|)
condition|)
block|{
operator|--
name|length_
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|(
name|mode
operator|&
name|COLLAPSE_WS
operator|)
operator|!=
literal|0
condition|)
block|{
specifier|final
name|XMLString
name|copy
init|=
operator|new
name|XMLString
argument_list|(
name|length_
argument_list|)
decl_stmt|;
name|boolean
name|inWhitespace
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start_
init|;
name|i
operator|<
name|start_
operator|+
name|length_
condition|;
name|i
operator|++
control|)
block|{
switch|switch
condition|(
name|value_
index|[
name|i
index|]
condition|)
block|{
case|case
literal|'\n'
case|:
case|case
literal|'\r'
case|:
case|case
literal|'\t'
case|:
case|case
literal|' '
case|:
if|if
condition|(
name|inWhitespace
operator|&&
name|i
operator|!=
name|start_
operator|+
name|length_
operator|-
literal|1
condition|)
block|{
comment|// remove the whitespace
block|}
else|else
block|{
name|copy
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|inWhitespace
operator|=
literal|true
expr_stmt|;
block|}
break|break;
default|default:
name|copy
operator|.
name|append
argument_list|(
name|value_
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|inWhitespace
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
return|return
name|copy
return|;
block|}
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|boolean
name|isWhitespaceOnly
parameter_list|()
block|{
if|if
condition|(
name|length_
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|length_
operator|&&
name|isWhiteSpace
argument_list|(
name|value_
index|[
name|start_
operator|+
name|i
index|]
argument_list|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
return|return
name|i
operator|==
name|length_
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|value_
operator|==
literal|null
condition|)
block|{
return|return
literal|"null"
return|;
block|}
return|return
operator|new
name|String
argument_list|(
name|value_
argument_list|,
name|start_
argument_list|,
name|length_
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|length
parameter_list|()
block|{
return|return
name|length_
return|;
block|}
specifier|public
specifier|final
name|int
name|startOffset
parameter_list|()
block|{
return|return
name|start_
return|;
block|}
specifier|public
specifier|final
name|String
name|substring
parameter_list|(
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
if|if
condition|(
name|start
operator|<
literal|0
operator|||
name|count
operator|<
literal|0
operator|||
name|start
operator|>=
name|length_
operator|||
name|start
operator|+
name|count
operator|>
name|length_
condition|)
block|{
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|()
throw|;
block|}
return|return
operator|new
name|String
argument_list|(
name|value_
argument_list|,
name|start_
operator|+
name|start
argument_list|,
name|count
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|delete
parameter_list|(
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|count
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|value_
argument_list|,
name|start
operator|+
name|count
operator|+
name|start_
argument_list|,
name|value_
argument_list|,
name|start
argument_list|,
name|length_
operator|-
operator|(
name|start
operator|+
name|count
operator|)
argument_list|)
expr_stmt|;
name|start_
operator|=
literal|0
expr_stmt|;
name|length_
operator|=
name|length_
operator|-
name|count
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|insert
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|String
name|data
parameter_list|)
block|{
name|ensureCapacity
argument_list|(
name|length_
operator|+
name|data
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|value_
argument_list|,
name|offset
argument_list|,
name|value_
argument_list|,
name|offset
operator|+
name|data
operator|.
name|length
argument_list|()
argument_list|,
name|length_
operator|-
name|offset
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value_
argument_list|,
name|offset
argument_list|,
name|data
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|length_
operator|+=
name|data
operator|.
name|length
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|replace
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|count
parameter_list|,
specifier|final
name|String
name|data
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|<
literal|0
operator|||
name|count
operator|<
literal|0
operator|||
name|offset
operator|>=
name|length_
operator|||
name|offset
operator|+
name|count
operator|>
name|length_
condition|)
block|{
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|()
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|data
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value_
argument_list|,
name|start_
operator|+
name|offset
argument_list|,
name|count
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|char
name|charAt
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
return|return
name|value_
index|[
name|start_
operator|+
name|pos
index|]
return|;
block|}
specifier|public
specifier|final
name|void
name|reset
parameter_list|()
block|{
name|CharArrayPool
operator|.
name|releaseCharArray
argument_list|(
name|value_
argument_list|)
expr_stmt|;
name|value_
operator|=
literal|null
expr_stmt|;
name|start_
operator|=
literal|0
expr_stmt|;
name|length_
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|reuse
parameter_list|()
block|{
name|start_
operator|=
literal|0
expr_stmt|;
name|length_
operator|=
literal|0
expr_stmt|;
block|}
specifier|private
name|void
name|ensureCapacity
parameter_list|(
specifier|final
name|int
name|capacity
parameter_list|)
block|{
if|if
condition|(
name|value_
operator|==
literal|null
condition|)
block|{
name|value_
operator|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value_
operator|.
name|length
operator|-
name|start_
operator|<
name|capacity
condition|)
block|{
name|int
name|newCapacity
init|=
operator|(
name|length_
operator|+
literal|1
operator|)
operator|*
literal|2
decl_stmt|;
if|if
condition|(
name|newCapacity
operator|<
name|capacity
condition|)
block|{
name|newCapacity
operator|=
name|capacity
expr_stmt|;
block|}
specifier|final
name|char
index|[]
name|temp
init|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|newCapacity
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|value_
argument_list|,
name|start_
argument_list|,
name|temp
argument_list|,
literal|0
argument_list|,
name|length_
argument_list|)
expr_stmt|;
name|CharArrayPool
operator|.
name|releaseCharArray
argument_list|(
name|value_
argument_list|)
expr_stmt|;
name|value_
operator|=
name|temp
expr_stmt|;
name|start_
operator|=
literal|0
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|boolean
name|isWhiteSpace
parameter_list|(
specifier|final
name|char
name|ch
parameter_list|)
block|{
return|return
operator|(
name|ch
operator|==
literal|0x20
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0x09
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0xD
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0xA
operator|)
return|;
block|}
comment|/**      * Release all resources hold by this XMLString.      */
specifier|public
specifier|final
name|void
name|release
parameter_list|()
block|{
name|CharArrayPool
operator|.
name|releaseCharArray
argument_list|(
name|value_
argument_list|)
expr_stmt|;
name|value_
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|CharSequence
name|subSequence
parameter_list|(
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|XMLString
argument_list|(
name|value_
argument_list|,
name|start_
operator|+
name|start
argument_list|,
name|end
operator|-
name|start
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|XMLString
name|transformToLower
parameter_list|()
block|{
specifier|final
name|int
name|end
init|=
name|start_
operator|+
name|length_
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start_
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|value_
index|[
name|i
index|]
operator|=
name|Character
operator|.
name|toLowerCase
argument_list|(
name|value_
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
specifier|final
name|int
name|UTF8Size
parameter_list|()
block|{
return|return
name|UTF8
operator|.
name|encoded
argument_list|(
name|value_
argument_list|,
name|start_
argument_list|,
name|length_
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|byte
index|[]
name|UTF8Encode
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
return|return
name|UTF8
operator|.
name|encode
argument_list|(
name|value_
argument_list|,
name|start_
argument_list|,
name|length_
argument_list|,
name|b
argument_list|,
name|offset
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|void
name|toSAX
parameter_list|(
specifier|final
name|ContentHandler
name|ch
parameter_list|)
throws|throws
name|SAXException
block|{
name|ch
operator|.
name|characters
argument_list|(
name|value_
argument_list|,
name|start_
argument_list|,
name|length_
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
specifier|final
name|CharSequence
name|cs
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length_
operator|&&
name|i
operator|<
name|cs
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|value_
index|[
name|start_
operator|+
name|i
index|]
operator|<
name|cs
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
name|Constants
operator|.
name|INFERIOR
return|;
block|}
if|else if
condition|(
name|value_
index|[
name|start_
operator|+
name|i
index|]
operator|>
name|cs
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
name|Constants
operator|.
name|SUPERIOR
return|;
block|}
block|}
if|if
condition|(
name|length_
operator|<
name|cs
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|Constants
operator|.
name|INFERIOR
return|;
block|}
if|else if
condition|(
name|length_
operator|>
name|cs
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
name|Constants
operator|.
name|SUPERIOR
return|;
block|}
else|else
block|{
return|return
name|Constants
operator|.
name|EQUAL
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|anObject
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|anObject
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|anObject
operator|instanceof
name|XMLString
condition|)
block|{
specifier|final
name|XMLString
name|anotherString
init|=
operator|(
name|XMLString
operator|)
name|anObject
decl_stmt|;
if|if
condition|(
name|length_
operator|==
name|anotherString
operator|.
name|length_
condition|)
block|{
name|int
name|n
init|=
name|length_
decl_stmt|;
specifier|final
name|char
name|v1
index|[]
init|=
name|value_
decl_stmt|;
specifier|final
name|char
name|v2
index|[]
init|=
name|anotherString
operator|.
name|value_
decl_stmt|;
name|int
name|i
init|=
name|start_
decl_stmt|;
name|int
name|j
init|=
name|anotherString
operator|.
name|start_
decl_stmt|;
while|while
condition|(
name|n
operator|--
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|v1
index|[
name|i
operator|++
index|]
operator|!=
name|v2
index|[
name|j
operator|++
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
specifier|final
name|String
name|anotherString
init|=
name|anObject
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|length_
operator|==
name|anotherString
operator|.
name|length
argument_list|()
condition|)
block|{
name|int
name|j
init|=
name|start_
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
name|length_
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|value_
index|[
name|j
operator|++
index|]
operator|!=
name|anotherString
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|off
init|=
name|start_
decl_stmt|;
name|int
name|h
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
name|length_
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
name|value_
index|[
name|off
operator|++
index|]
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
block|}
end_class

end_unit

