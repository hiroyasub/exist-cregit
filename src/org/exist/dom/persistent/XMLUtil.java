begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|io
operator|.
name|FastByteArrayOutputStream
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
name|DocumentFragment
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
name|InputSource
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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

begin_comment
comment|/**  * Defines some static utility methods.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|XMLUtil
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|XMLUtil
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|XMLUtil
parameter_list|()
block|{
comment|//Utility class of static methods
block|}
specifier|public
specifier|static
specifier|final
name|String
name|dump
parameter_list|(
specifier|final
name|DocumentFragment
name|fragment
parameter_list|)
block|{
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|DOMSerializer
name|serializer
init|=
operator|new
name|DOMSerializer
argument_list|(
name|writer
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
specifier|final
name|TransformerException
name|e
parameter_list|)
block|{
comment|//Nothing to do ?
block|}
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
specifier|final
name|String
name|encodeAttrMarkup
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
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
block|{
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
block|{
break|break;
block|}
block|}
if|if
condition|(
name|isEntity
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
block|}
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
default|default:
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
specifier|final
name|String
name|decodeAttrMarkup
parameter_list|(
specifier|final
name|String
name|str
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
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
literal|"amp"
operator|.
name|equals
argument_list|(
name|ent
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"lt"
operator|.
name|equals
argument_list|(
name|ent
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"gt"
operator|.
name|equals
argument_list|(
name|ent
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"quot"
operator|.
name|equals
argument_list|(
name|ent
argument_list|)
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
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
specifier|static
specifier|final
name|Optional
argument_list|<
name|Charset
argument_list|>
name|getEncoding
parameter_list|(
specifier|final
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
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
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
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
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
block|{
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
block|{
continue|continue;
block|}
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
block|{
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
block|}
return|return
name|Optional
operator|.
name|of
argument_list|(
name|Charset
operator|.
name|forName
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
block|}
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
specifier|public
specifier|static
specifier|final
name|String
name|getXMLDecl
parameter_list|(
specifier|final
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
block|{
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
comment|/*                  * Need to gather the next 4 non-zero values and test them 				 * because greater than 8-bytes character encodings will be 				 * represented with two bits 				 */
name|boolean
name|foundQuestionMark
init|=
literal|false
decl_stmt|;
name|int
name|placeInDeclString
init|=
literal|0
decl_stmt|;
specifier|final
name|byte
index|[]
name|declString
init|=
operator|new
name|byte
index|[
literal|4
index|]
decl_stmt|;
name|int
name|x
init|=
operator|(
name|i
operator|+
literal|1
operator|)
decl_stmt|;
for|for
control|(
init|;
name|x
operator|<
name|data
operator|.
name|length
condition|;
name|x
operator|++
control|)
block|{
if|if
condition|(
name|data
index|[
name|x
index|]
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|foundQuestionMark
operator|&&
name|data
index|[
name|x
index|]
operator|!=
literal|'?'
condition|)
block|{
break|break;
block|}
else|else
block|{
name|foundQuestionMark
operator|=
literal|true
expr_stmt|;
block|}
name|declString
index|[
name|placeInDeclString
index|]
operator|=
name|data
index|[
name|x
index|]
expr_stmt|;
name|placeInDeclString
operator|++
expr_stmt|;
if|if
condition|(
name|placeInDeclString
operator|>=
literal|4
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|placeInDeclString
operator|==
literal|4
operator|&&
name|declString
index|[
literal|0
index|]
operator|==
literal|'?'
operator|&&
name|declString
index|[
literal|1
index|]
operator|==
literal|'x'
operator|&&
name|declString
index|[
literal|2
index|]
operator|==
literal|'m'
operator|&&
name|declString
index|[
literal|3
index|]
operator|==
literal|'l'
condition|)
block|{
specifier|final
name|FastByteArrayOutputStream
name|out
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|(
literal|150
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|declString
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
operator|(
name|x
operator|+
literal|1
operator|)
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
block|{
if|if
condition|(
name|data
index|[
name|j
index|]
operator|!=
literal|0
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|data
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|data
index|[
name|j
index|]
operator|==
literal|'?'
condition|)
block|{
name|j
operator|++
expr_stmt|;
comment|/* 							 * When we find this we have to start looking for the end tag 							 */
for|for
control|(
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
block|{
if|if
condition|(
name|data
index|[
name|j
index|]
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|out
operator|.
name|write
argument_list|(
name|data
index|[
name|j
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|data
index|[
name|j
index|]
operator|!=
literal|'>'
condition|)
block|{
break|break;
block|}
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
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Deprecated
specifier|public
specifier|static
specifier|final
name|String
name|readFile
parameter_list|(
specifier|final
name|Path
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
name|UTF_8
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
specifier|public
specifier|static
name|String
name|readFile
parameter_list|(
specifier|final
name|Path
name|file
parameter_list|,
specifier|final
name|Charset
name|defaultEncoding
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read the file into a string
return|return
name|readFile
argument_list|(
name|Files
operator|.
name|readAllBytes
argument_list|(
name|file
argument_list|)
argument_list|,
name|defaultEncoding
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
specifier|public
specifier|static
name|String
name|readFile
parameter_list|(
specifier|final
name|InputSource
name|inSrc
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read the file into a string
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|os
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|inSrc
operator|.
name|getByteStream
argument_list|()
init|)
block|{
name|os
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
return|return
name|readFile
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|Charset
operator|.
name|forName
argument_list|(
name|inSrc
operator|.
name|getEncoding
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|//TODO if needed, replace with a decent NIO implementation
annotation|@
name|Deprecated
specifier|public
specifier|static
name|String
name|readFile
parameter_list|(
specifier|final
name|byte
index|[]
name|in
parameter_list|,
specifier|final
name|Charset
name|defaultEncoding
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|xmlDecl
init|=
name|getXMLDecl
argument_list|(
name|in
argument_list|)
decl_stmt|;
specifier|final
name|Charset
name|enc
init|=
name|getEncoding
argument_list|(
name|xmlDecl
argument_list|)
operator|.
name|orElse
argument_list|(
name|defaultEncoding
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|in
argument_list|,
name|enc
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|parseValue
parameter_list|(
specifier|final
name|String
name|value
parameter_list|,
specifier|final
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
block|{
return|return
literal|null
return|;
block|}
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
specifier|final
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
block|{
comment|// Do nothing
block|}
if|if
condition|(
name|p
operator|==
name|value
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
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
block|{
comment|// Do nothing
block|}
if|if
condition|(
name|e
operator|==
name|value
operator|.
name|length
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
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

