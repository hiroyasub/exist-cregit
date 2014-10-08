begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Namespaces
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
name|IndentingXMLWriter
block|{
specifier|private
specifier|final
specifier|static
name|ObjectHashSet
argument_list|<
name|String
argument_list|>
name|emptyTags
init|=
operator|new
name|ObjectHashSet
argument_list|<
name|String
argument_list|>
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
specifier|final
specifier|static
name|ObjectHashSet
argument_list|<
name|String
argument_list|>
name|inlineTags
init|=
operator|new
name|ObjectHashSet
argument_list|<
name|String
argument_list|>
argument_list|(
literal|31
argument_list|)
decl_stmt|;
static|static
block|{
name|inlineTags
operator|.
name|add
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"abbr"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"acronym"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"bdo"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"big"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"br"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"button"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"cite"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"code"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"del"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"dfn"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"em"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"i"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"img"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"input"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"kbd"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"label"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"samp"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"select"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"small"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"span"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"strong"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"sub"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"sup"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"textarea"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"tt"
argument_list|)
expr_stmt|;
name|inlineTags
operator|.
name|add
argument_list|(
literal|"var"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|boolean
name|isEmptyTag
parameter_list|(
specifier|final
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
specifier|protected
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
specifier|final
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
name|boolean
name|haveCollapsedXhtmlPrefix
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
specifier|final
name|QName
name|xhtmlQName
init|=
name|removeXhtmlPrefix
argument_list|(
name|qname
argument_list|)
decl_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|xhtmlQName
argument_list|)
expr_stmt|;
name|currentTag
operator|=
name|xhtmlQName
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
specifier|final
name|QName
name|xhtmlQName
init|=
name|removeXhtmlPrefix
argument_list|(
name|qname
argument_list|)
decl_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|xhtmlQName
argument_list|)
expr_stmt|;
name|haveCollapsedXhtmlPrefix
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
name|QName
name|removeXhtmlPrefix
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
block|{
specifier|final
name|String
name|prefix
init|=
name|qname
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
specifier|final
name|String
name|namespaceURI
init|=
name|qname
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XHTML_NS
argument_list|)
condition|)
block|{
name|haveCollapsedXhtmlPrefix
operator|=
literal|true
expr_stmt|;
return|return
operator|new
name|QName
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|namespaceURI
argument_list|)
return|;
block|}
return|return
name|qname
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
specifier|final
name|String
name|xhtmlQName
init|=
name|removeXhtmlPrefix
argument_list|(
name|namespaceURI
argument_list|,
name|qname
argument_list|)
decl_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|xhtmlQName
argument_list|)
expr_stmt|;
name|currentTag
operator|=
name|xhtmlQName
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
specifier|final
name|String
name|xhtmlQName
init|=
name|removeXhtmlPrefix
argument_list|(
name|namespaceURI
argument_list|,
name|qname
argument_list|)
decl_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|xhtmlQName
argument_list|)
expr_stmt|;
name|haveCollapsedXhtmlPrefix
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
name|String
name|removeXhtmlPrefix
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|qname
parameter_list|)
block|{
specifier|final
name|int
name|pos
init|=
name|qname
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|>
literal|0
operator|&&
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XHTML_NS
argument_list|)
condition|)
block|{
name|haveCollapsedXhtmlPrefix
operator|=
literal|true
expr_stmt|;
return|return
name|qname
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
name|qname
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|namespace
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|nsURI
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|haveCollapsedXhtmlPrefix
operator|&&
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|nsURI
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XHTML_NS
argument_list|)
condition|)
block|{
return|return;
comment|//dont output the xmlns:prefix for the collapsed nodes prefix
block|}
name|super
operator|.
name|namespace
argument_list|(
name|prefix
argument_list|,
name|nsURI
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|closeStartTag
parameter_list|(
specifier|final
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
block|{
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|" />"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|"</"
argument_list|)
expr_stmt|;
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
name|currentTag
argument_list|)
expr_stmt|;
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|getWriter
argument_list|()
operator|.
name|write
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
block|}
name|tagIsOpen
operator|=
literal|false
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isInlineTag
parameter_list|(
specifier|final
name|String
name|namespaceURI
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|)
block|{
return|return
operator|(
name|namespaceURI
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
operator|||
name|Namespaces
operator|.
name|XHTML_NS
operator|.
name|equals
argument_list|(
name|namespaceURI
argument_list|)
operator|)
operator|&&
name|inlineTags
operator|.
name|contains
argument_list|(
name|localName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

