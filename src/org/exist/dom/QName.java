begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  meier@ifs.tu-darmstadt.de  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id:  */
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
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|StaticContext
import|;
end_import

begin_class
specifier|public
class|class
name|QName
implements|implements
name|Comparable
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|TEXT_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"#text"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|COMMENT_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"#comment"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|DOCTYPE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"#doctype"
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|private
name|String
name|localName_
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|namespaceURI_
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|prefix_
init|=
literal|null
decl_stmt|;
specifier|public
name|QName
parameter_list|(
name|String
name|localName
parameter_list|,
name|String
name|namespaceURI
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|localName_
operator|=
name|localName
expr_stmt|;
name|namespaceURI_
operator|=
name|namespaceURI
expr_stmt|;
name|prefix_
operator|=
name|prefix
expr_stmt|;
block|}
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
name|localName_
return|;
block|}
specifier|public
name|void
name|setLocalName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|localName_
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|namespaceURI_
return|;
block|}
specifier|public
name|void
name|setNamespaceURI
parameter_list|(
name|String
name|namespaceURI
parameter_list|)
block|{
name|namespaceURI_
operator|=
name|namespaceURI
expr_stmt|;
block|}
specifier|public
name|boolean
name|needsNamespaceDecl
parameter_list|()
block|{
return|return
name|namespaceURI_
operator|!=
literal|null
operator|&&
name|namespaceURI_
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix_
return|;
block|}
specifier|public
name|void
name|setPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|prefix_
operator|=
name|prefix
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|prefix_
operator|!=
literal|null
operator|&&
name|prefix_
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
return|return
name|prefix_
operator|+
literal|':'
operator|+
name|localName_
return|;
else|else
return|return
name|localName_
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Comparable#compareTo(java.lang.Object) 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|QName
name|other
init|=
operator|(
name|QName
operator|)
name|o
decl_stmt|;
name|int
name|c
decl_stmt|;
if|if
condition|(
name|namespaceURI_
operator|==
literal|null
condition|)
name|c
operator|=
name|other
operator|.
name|namespaceURI_
operator|==
literal|null
condition|?
literal|0
else|:
operator|-
literal|1
expr_stmt|;
if|else if
condition|(
name|other
operator|.
name|namespaceURI_
operator|==
literal|null
condition|)
name|c
operator|=
literal|1
expr_stmt|;
else|else
name|c
operator|=
name|namespaceURI_
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|namespaceURI_
argument_list|)
expr_stmt|;
return|return
name|c
operator|==
literal|0
condition|?
name|localName_
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|localName_
argument_list|)
else|:
name|c
return|;
block|}
specifier|public
specifier|static
name|String
name|extractPrefix
parameter_list|(
name|String
name|qname
parameter_list|)
block|{
name|int
name|p
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
name|p
operator|<
literal|0
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|p
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: starts with a :"
argument_list|)
throw|;
return|return
name|qname
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|extractLocalName
parameter_list|(
name|String
name|qname
parameter_list|)
block|{
name|int
name|p
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
name|p
operator|<
literal|0
condition|)
return|return
name|qname
return|;
if|if
condition|(
name|p
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: starts with a :"
argument_list|)
throw|;
if|if
condition|(
name|p
operator|==
name|qname
operator|.
name|length
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal QName: ends with a :"
argument_list|)
throw|;
return|return
name|qname
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|QName
name|parse
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|String
name|qname
parameter_list|)
block|{
name|String
name|prefix
init|=
name|extractPrefix
argument_list|(
name|qname
argument_list|)
decl_stmt|;
name|String
name|namespaceURI
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No namespace defined for prefix "
operator|+
name|prefix
argument_list|)
throw|;
block|}
return|return
operator|new
name|QName
argument_list|(
name|extractLocalName
argument_list|(
name|qname
argument_list|)
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
return|;
block|}
block|}
end_class

end_unit

