begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|encodings
package|;
end_package

begin_class
specifier|public
specifier|abstract
class|class
name|CharacterSet
block|{
specifier|public
specifier|abstract
name|boolean
name|inCharacterSet
parameter_list|(
name|char
name|ch
parameter_list|)
function_decl|;
specifier|public
specifier|static
name|CharacterSet
name|getCharacterSet
parameter_list|(
name|String
name|encoding
parameter_list|)
block|{
if|if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ASCII"
argument_list|)
condition|)
block|{
return|return
name|ASCIICharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"US-ASCII"
argument_list|)
condition|)
block|{
return|return
name|ASCIICharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ISO-8859-1"
argument_list|)
condition|)
block|{
return|return
name|Latin1CharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ISO8859_1"
argument_list|)
condition|)
block|{
return|return
name|Latin1CharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"UTF-8"
argument_list|)
condition|)
block|{
return|return
name|UnicodeCharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"UTF8"
argument_list|)
condition|)
block|{
return|return
name|UnicodeCharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"utf-16"
argument_list|)
condition|)
block|{
return|return
name|UnicodeCharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"utf16"
argument_list|)
condition|)
block|{
return|return
name|UnicodeCharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"iso-8859-2"
argument_list|)
condition|)
block|{
return|return
name|Latin2CharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"ISO8859_2"
argument_list|)
condition|)
block|{
return|return
name|Latin2CharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"KOI8-R"
argument_list|)
condition|)
block|{
return|return
name|KOI8RCharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|ASCIICharSet
operator|.
name|getInstance
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

