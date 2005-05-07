begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
package|;
end_package

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
name|Writer
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
name|util
operator|.
name|hashtable
operator|.
name|ObjectHashSet
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|XHTMLWriter
extends|extends
name|XMLIndenter
block|{
specifier|private
specifier|static
name|ObjectHashSet
name|emptyTags
init|=
operator|new
name|ObjectHashSet
argument_list|(
literal|31
argument_list|)
decl_stmt|;
static|static
block|{
name|emptyTags
operator|.
name|add
argument_list|(
literal|"area"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"base"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"br"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"col"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"hr"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"img"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"input"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"link"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"meta"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"basefont"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"frame"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"isindex"
argument_list|)
expr_stmt|;
name|emptyTags
operator|.
name|add
argument_list|(
literal|"param"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|isEmptyTag
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
return|return
name|emptyTags
operator|.
name|contains
argument_list|(
name|tag
argument_list|)
return|;
block|}
specifier|private
name|String
name|currentTag
decl_stmt|;
comment|/**      *       */
specifier|public
name|XHTMLWriter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param writer      */
specifier|public
name|XHTMLWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|startElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|currentTag
operator|=
name|qname
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|startElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|currentTag
operator|=
name|qname
expr_stmt|;
block|}
specifier|protected
name|void
name|closeStartTag
parameter_list|(
name|boolean
name|isEmpty
parameter_list|)
throws|throws
name|TransformerException
block|{
try|try
block|{
if|if
condition|(
name|tagIsOpen
condition|)
block|{
if|if
condition|(
name|isEmpty
condition|)
block|{
if|if
condition|(
name|isEmptyTag
argument_list|(
name|currentTag
argument_list|)
condition|)
name|writer
operator|.
name|write
argument_list|(
literal|" />"
argument_list|)
expr_stmt|;
else|else
block|{
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|"</"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|currentTag
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|writer
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|tagIsOpen
operator|=
literal|false
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

