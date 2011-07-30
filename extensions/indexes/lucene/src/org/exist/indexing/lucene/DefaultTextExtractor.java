begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: DefaultTextExtractor.java 11737 2010-05-02 21:25:21Z ixitar $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
package|;
end_package

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
name|XMLString
import|;
end_import

begin_class
specifier|public
class|class
name|DefaultTextExtractor
extends|extends
name|AbstractTextExtractor
block|{
specifier|private
name|int
name|stack
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|addSpaceBeforeNext
init|=
literal|false
decl_stmt|;
specifier|public
name|int
name|startElement
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|isIgnoredNode
argument_list|(
name|name
argument_list|)
operator|||
operator|(
name|idxConfig
operator|!=
literal|null
operator|&&
name|idxConfig
operator|.
name|isIgnoredNode
argument_list|(
name|name
argument_list|)
operator|)
condition|)
name|stack
operator|++
expr_stmt|;
if|else if
condition|(
operator|!
name|isInlineNode
argument_list|(
name|name
argument_list|)
operator|&&
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|' '
condition|)
block|{
comment|// separate the current element's text from preceding text
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|private
name|boolean
name|isInlineNode
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
return|return
operator|(
name|config
operator|.
name|isInlineNode
argument_list|(
name|name
argument_list|)
operator|||
operator|(
name|idxConfig
operator|!=
literal|null
operator|&&
name|idxConfig
operator|.
name|isInlineNode
argument_list|(
name|name
argument_list|)
operator|)
operator|)
return|;
block|}
specifier|public
name|int
name|endElement
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
if|if
condition|(
name|config
operator|.
name|isIgnoredNode
argument_list|(
name|name
argument_list|)
operator|||
operator|(
name|idxConfig
operator|!=
literal|null
operator|&&
name|idxConfig
operator|.
name|isIgnoredNode
argument_list|(
name|name
argument_list|)
operator|)
condition|)
name|stack
operator|--
expr_stmt|;
if|else if
condition|(
operator|!
name|isInlineNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// add space before following text
name|addSpaceBeforeNext
operator|=
literal|true
expr_stmt|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|beforeCharacters
parameter_list|()
block|{
if|if
condition|(
name|addSpaceBeforeNext
operator|&&
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|' '
condition|)
block|{
comment|// separate the previous element's text from following text
name|buffer
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|addSpaceBeforeNext
operator|=
literal|false
expr_stmt|;
return|return
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|characters
parameter_list|(
name|XMLString
name|text
parameter_list|)
block|{
if|if
condition|(
name|stack
operator|==
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|text
argument_list|)
expr_stmt|;
return|return
name|text
operator|.
name|length
argument_list|()
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

