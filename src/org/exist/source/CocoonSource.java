begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|source
operator|.
name|SourceValidity
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
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * A source that wraps around a Cocoon source object.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CocoonSource
extends|extends
name|AbstractSource
block|{
specifier|private
name|Source
name|inputSource
decl_stmt|;
specifier|private
name|SourceValidity
name|validity
decl_stmt|;
specifier|private
name|boolean
name|checkEncoding
init|=
literal|false
decl_stmt|;
specifier|private
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
comment|/**      *       */
specifier|public
name|CocoonSource
parameter_list|(
name|Source
name|source
parameter_list|,
name|boolean
name|checkXQEncoding
parameter_list|)
block|{
name|inputSource
operator|=
name|source
expr_stmt|;
name|validity
operator|=
name|inputSource
operator|.
name|getValidity
argument_list|()
expr_stmt|;
name|checkEncoding
operator|=
name|checkXQEncoding
expr_stmt|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.source.Source#isValid()      */
specifier|public
name|int
name|isValid
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
if|if
condition|(
name|validity
operator|==
literal|null
condition|)
block|{
return|return
name|UNKNOWN
return|;
block|}
name|int
name|valid
init|=
name|validity
operator|.
name|isValid
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|valid
condition|)
block|{
case|case
name|SourceValidity
operator|.
name|UNKNOWN
case|:
return|return
name|UNKNOWN
return|;
case|case
name|SourceValidity
operator|.
name|VALID
case|:
return|return
name|VALID
return|;
default|default:
return|return
name|INVALID
return|;
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.source.Source#isValid(org.exist.source.Source)      */
specifier|public
name|int
name|isValid
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
name|other
parameter_list|)
block|{
name|SourceValidity
name|validityOther
init|=
operator|(
operator|(
name|CocoonSource
operator|)
name|other
operator|)
operator|.
name|inputSource
operator|.
name|getValidity
argument_list|()
decl_stmt|;
if|if
condition|(
name|validity
operator|==
literal|null
operator|||
name|validityOther
operator|==
literal|null
condition|)
block|{
comment|// if one of the validity objects is null, we fall back to comparing the content
try|try
block|{
if|if
condition|(
name|getContent
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|CocoonSource
operator|)
name|other
operator|)
operator|.
name|getContent
argument_list|()
argument_list|)
condition|)
return|return
name|VALID
return|;
else|else
return|return
name|INVALID
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|UNKNOWN
return|;
block|}
block|}
else|else
block|{
name|int
name|valid
init|=
name|validity
operator|.
name|isValid
argument_list|(
name|validityOther
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|valid
condition|)
block|{
case|case
name|SourceValidity
operator|.
name|UNKNOWN
case|:
return|return
name|UNKNOWN
return|;
case|case
name|SourceValidity
operator|.
name|VALID
case|:
return|return
name|VALID
return|;
default|default:
return|return
name|INVALID
return|;
block|}
block|}
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.source.Source#getReader()      */
specifier|public
name|Reader
name|getReader
parameter_list|()
throws|throws
name|IOException
block|{
name|checkEncoding
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|inputSource
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
return|return
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|encoding
argument_list|)
return|;
block|}
specifier|public
name|String
name|getContent
parameter_list|()
throws|throws
name|IOException
block|{
name|checkEncoding
argument_list|()
expr_stmt|;
name|int
name|len
init|=
operator|(
name|int
operator|)
name|inputSource
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|os
decl_stmt|;
if|if
condition|(
name|len
operator|==
operator|-
literal|1
condition|)
name|os
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|()
expr_stmt|;
else|else
name|os
operator|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|byte
index|[]
name|t
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|InputStream
name|is
init|=
name|inputSource
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|t
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|t
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
return|return
name|os
operator|.
name|toString
argument_list|(
name|encoding
argument_list|)
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see org.exist.source.Source#getKey()      */
specifier|public
name|Object
name|getKey
parameter_list|()
block|{
return|return
name|inputSource
operator|.
name|getURI
argument_list|()
return|;
block|}
specifier|private
name|void
name|checkEncoding
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|checkEncoding
condition|)
block|{
name|String
name|checkedEnc
init|=
name|guessXQueryEncoding
argument_list|(
name|inputSource
operator|.
name|getInputStream
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkedEnc
operator|!=
literal|null
condition|)
name|encoding
operator|=
name|checkedEnc
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

