begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
specifier|public
class|class
name|XMLString
implements|implements
name|CharSequence
implements|,
name|Comparable
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
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
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
specifier|final
name|XMLString
name|append
parameter_list|(
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
name|char
index|[]
name|ch
parameter_list|,
name|int
name|offset
parameter_list|,
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
name|ensureCapacity
argument_list|(
name|length_
operator|+
literal|1
argument_list|)
expr_stmt|;
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
name|char
index|[]
name|ch
parameter_list|,
name|int
name|offset
parameter_list|,
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
return|return
name|this
return|;
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
literal|0
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
operator|++
name|start_
expr_stmt|;
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
literal|0
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
return|return
name|this
return|;
block|}
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
return|return
literal|"null"
return|;
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
name|String
name|substring
parameter_list|(
name|int
name|start
parameter_list|,
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
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|()
throw|;
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
name|int
name|start
parameter_list|,
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
name|int
name|offset
parameter_list|,
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
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|,
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
throw|throw
operator|new
name|StringIndexOutOfBoundsException
argument_list|()
throw|;
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
specifier|private
specifier|final
name|void
name|ensureCapacity
parameter_list|(
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
comment|//value_ = new char[capacity];
name|value_
operator|=
name|CharArrayPool
operator|.
name|getCharArray
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
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
name|newCapacity
operator|=
name|capacity
expr_stmt|;
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
specifier|private
specifier|final
specifier|static
name|boolean
name|isWhiteSpace
parameter_list|(
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
comment|/* (non-Javadoc) 	 * @see java.lang.Object#finalize() 	 */
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
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
comment|/* (non-Javadoc) 	 * @see java.lang.CharSequence#subSequence(int, int) 	 */
specifier|public
specifier|final
name|CharSequence
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
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
name|byte
index|[]
name|b
parameter_list|,
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
comment|/* (non-Javadoc) 	 * @see java.lang.Comparable#compareTo(java.lang.Object) 	 */
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|CharSequence
name|cs
init|=
operator|(
name|CharSequence
operator|)
name|o
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
return|return
operator|-
literal|1
return|;
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
return|return
literal|1
return|;
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
return|return
operator|-
literal|1
return|;
if|else if
condition|(
name|length_
operator|>
name|cs
operator|.
name|length
argument_list|()
condition|)
return|return
literal|1
return|;
else|else
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

