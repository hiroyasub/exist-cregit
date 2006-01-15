begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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
name|Range
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
name|serializer
operator|.
name|DOMSerializer
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
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DocumentFragment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  *  Defines some static utility methods.   *  */
end_comment

begin_class
specifier|public
class|class
name|XMLUtil
block|{
specifier|public
specifier|final
specifier|static
name|String
name|dump
parameter_list|(
name|DocumentFragment
name|fragment
parameter_list|)
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|DOMSerializer
name|serializer
init|=
operator|new
name|DOMSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
try|try
block|{
name|serializer
operator|.
name|serialize
argument_list|(
name|fragment
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
block|}
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|encodeAttrMarkup
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|char
name|ch
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
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
switch|switch
condition|(
name|ch
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
block|{
case|case
literal|'&'
case|:
name|boolean
name|isEntity
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|str
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|str
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
operator|==
literal|';'
condition|)
block|{
name|isEntity
operator|=
literal|true
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetter
argument_list|(
name|str
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
condition|)
break|break;
block|}
if|if
condition|(
name|isEntity
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'<'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"&lt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'>'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'"'
case|:
name|buf
operator|.
name|append
argument_list|(
literal|"&quot;"
argument_list|)
expr_stmt|;
break|break;
default|default :
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|decodeAttrMarkup
parameter_list|(
name|String
name|str
parameter_list|)
block|{
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|(
name|str
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|char
name|ch
decl_stmt|;
name|String
name|ent
decl_stmt|;
name|int
name|p
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
name|str
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ch
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'&'
condition|)
block|{
name|p
operator|=
name|str
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|,
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|ent
operator|=
name|str
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|,
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|ent
operator|.
name|equals
argument_list|(
literal|"amp"
argument_list|)
condition|)
name|out
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
if|else if
condition|(
name|ent
operator|.
name|equals
argument_list|(
literal|"lt"
argument_list|)
condition|)
name|out
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
if|else if
condition|(
name|ent
operator|.
name|equals
argument_list|(
literal|"gt"
argument_list|)
condition|)
name|out
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
if|else if
condition|(
name|ent
operator|.
name|equals
argument_list|(
literal|"quot"
argument_list|)
condition|)
name|out
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|i
operator|=
name|p
expr_stmt|;
continue|continue;
block|}
block|}
name|out
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|getEncoding
parameter_list|(
name|String
name|xmlDecl
parameter_list|)
block|{
if|if
condition|(
name|xmlDecl
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|p0
init|=
name|xmlDecl
operator|.
name|indexOf
argument_list|(
literal|"encoding"
argument_list|)
decl_stmt|;
if|if
condition|(
name|p0
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
return|return
literal|null
return|;
for|for
control|(
name|int
name|i
init|=
name|p0
operator|+
literal|8
init|;
name|i
operator|<
name|xmlDecl
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|xmlDecl
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
operator|||
name|xmlDecl
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'='
condition|)
continue|continue;
if|else if
condition|(
name|xmlDecl
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'"'
condition|)
block|{
while|while
condition|(
name|xmlDecl
operator|.
name|charAt
argument_list|(
operator|++
name|i
argument_list|)
operator|!=
literal|'"'
operator|&&
name|i
operator|<
name|xmlDecl
operator|.
name|length
argument_list|()
condition|)
name|buf
operator|.
name|append
argument_list|(
name|xmlDecl
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
return|return
literal|null
return|;
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|getXMLDecl
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|boolean
name|foundTag
init|=
literal|false
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
name|data
operator|.
name|length
operator|&&
operator|!
name|foundTag
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|data
index|[
name|i
index|]
operator|==
literal|'<'
condition|)
block|{
name|foundTag
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|data
index|[
name|i
operator|+
literal|1
index|]
operator|==
literal|'?'
operator|&&
name|data
index|[
name|i
operator|+
literal|2
index|]
operator|==
literal|'x'
operator|&&
name|data
index|[
name|i
operator|+
literal|3
index|]
operator|==
literal|'m'
operator|&&
name|data
index|[
name|i
operator|+
literal|4
index|]
operator|==
literal|'l'
condition|)
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|5
init|;
name|j
operator|<
name|data
operator|.
name|length
condition|;
name|j
operator|++
control|)
if|if
condition|(
name|data
index|[
name|j
index|]
operator|==
literal|'?'
operator|&&
name|data
index|[
name|j
operator|+
literal|1
index|]
operator|==
literal|'>'
condition|)
block|{
name|String
name|xmlDecl
init|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|i
argument_list|,
name|j
operator|-
name|i
operator|+
literal|2
argument_list|)
decl_stmt|;
return|return
name|xmlDecl
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|readFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|readFile
argument_list|(
name|file
argument_list|,
literal|"ISO-8859-1"
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|readFile
parameter_list|(
name|File
name|file
parameter_list|,
name|String
name|defaultEncoding
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read the file into a string
return|return
name|readFile
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
argument_list|,
name|defaultEncoding
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|readFile
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|String
name|defaultEncoding
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|l
decl_stmt|;
do|do
block|{
name|l
operator|=
name|in
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
if|if
condition|(
name|l
operator|>
literal|0
condition|)
name|out
operator|.
name|write
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|l
operator|>
operator|-
literal|1
condition|)
do|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|String
name|xmlDecl
init|=
name|getXMLDecl
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|String
name|enc
init|=
name|getEncoding
argument_list|(
name|xmlDecl
argument_list|)
decl_stmt|;
if|if
condition|(
name|enc
operator|==
literal|null
condition|)
name|enc
operator|=
name|defaultEncoding
expr_stmt|;
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|enc
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|out
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|parseValue
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|key
parameter_list|)
block|{
name|int
name|p
init|=
name|value
operator|.
name|indexOf
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
return|return
literal|null
return|;
return|return
name|parseValue
argument_list|(
name|value
argument_list|,
name|p
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|parseValue
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|p
parameter_list|)
block|{
while|while
condition|(
operator|(
name|p
operator|<
name|value
operator|.
name|length
argument_list|()
operator|)
operator|&&
operator|(
name|value
operator|.
name|charAt
argument_list|(
operator|++
name|p
argument_list|)
operator|!=
literal|'"'
operator|)
condition|)
empty_stmt|;
if|if
condition|(
name|p
operator|==
name|value
operator|.
name|length
argument_list|()
condition|)
return|return
literal|null
return|;
name|int
name|e
init|=
operator|++
name|p
decl_stmt|;
while|while
condition|(
operator|(
name|e
operator|<
name|value
operator|.
name|length
argument_list|()
operator|)
operator|&&
operator|(
name|value
operator|.
name|charAt
argument_list|(
operator|++
name|e
argument_list|)
operator|!=
literal|'"'
operator|)
condition|)
empty_stmt|;
if|if
condition|(
name|e
operator|==
name|value
operator|.
name|length
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
name|value
operator|.
name|substring
argument_list|(
name|p
argument_list|,
name|e
argument_list|)
return|;
block|}
block|}
end_class

end_unit

