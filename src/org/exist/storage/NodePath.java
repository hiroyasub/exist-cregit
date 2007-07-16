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
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|FastStringBuffer
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|NodePath
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|NodePath
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * (Illegal) QName used as a marker for arbitrary path steps.      */
specifier|public
specifier|final
specifier|static
name|QName
name|WILDCARD
init|=
operator|new
name|QName
argument_list|(
literal|"*"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|private
name|QName
index|[]
name|components
init|=
operator|new
name|QName
index|[
literal|5
index|]
decl_stmt|;
specifier|private
name|int
name|pos
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|includeDescendants
init|=
literal|true
decl_stmt|;
specifier|public
name|NodePath
parameter_list|()
block|{
block|}
specifier|public
name|NodePath
parameter_list|(
name|NodePath
name|other
parameter_list|)
block|{
name|components
operator|=
operator|new
name|QName
index|[
name|other
operator|.
name|components
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|components
argument_list|,
literal|0
argument_list|,
name|components
argument_list|,
literal|0
argument_list|,
name|other
operator|.
name|components
operator|.
name|length
argument_list|)
expr_stmt|;
name|pos
operator|=
name|other
operator|.
name|pos
expr_stmt|;
name|includeDescendants
operator|=
name|other
operator|.
name|includeDescendants
expr_stmt|;
block|}
comment|/**      *       */
specifier|public
name|NodePath
parameter_list|(
name|Map
name|namespaces
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|init
argument_list|(
name|namespaces
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodePath
parameter_list|(
name|Map
name|namespaces
parameter_list|,
name|String
name|path
parameter_list|,
name|boolean
name|includeDescendants
parameter_list|)
block|{
name|this
operator|.
name|includeDescendants
operator|=
name|includeDescendants
expr_stmt|;
name|init
argument_list|(
name|namespaces
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addComponent
parameter_list|(
name|QName
name|component
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|==
name|components
operator|.
name|length
condition|)
block|{
name|QName
index|[]
name|t
init|=
operator|new
name|QName
index|[
name|pos
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|components
operator|=
name|t
expr_stmt|;
block|}
name|components
index|[
name|pos
operator|++
index|]
operator|=
name|component
expr_stmt|;
block|}
specifier|public
name|void
name|addComponentAtStart
parameter_list|(
name|QName
name|component
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|==
name|components
operator|.
name|length
condition|)
block|{
name|QName
index|[]
name|t
init|=
operator|new
name|QName
index|[
name|pos
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
name|t
argument_list|,
literal|1
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|components
operator|=
name|t
expr_stmt|;
name|components
index|[
literal|0
index|]
operator|=
name|component
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
name|components
argument_list|,
literal|1
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|components
index|[
literal|0
index|]
operator|=
name|component
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeLastComponent
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
name|components
index|[
operator|--
name|pos
index|]
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|pos
return|;
block|}
specifier|public
name|QName
name|getComponent
parameter_list|(
name|int
name|at
parameter_list|)
block|{
if|if
condition|(
name|at
operator|<
literal|0
operator|||
name|at
operator|>=
name|pos
condition|)
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
name|at
argument_list|)
throw|;
return|return
name|components
index|[
name|at
index|]
return|;
block|}
specifier|public
specifier|final
name|boolean
name|match
parameter_list|(
name|NodePath
name|other
parameter_list|)
block|{
name|boolean
name|skip
init|=
literal|false
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|,
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|j
operator|<
name|other
operator|.
name|pos
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|==
name|pos
condition|)
block|{
if|if
condition|(
name|includeDescendants
condition|)
return|return
literal|true
return|;
return|return
name|j
operator|==
name|other
operator|.
name|pos
condition|?
literal|true
else|:
literal|false
return|;
block|}
if|if
condition|(
name|components
index|[
name|i
index|]
operator|==
name|WILDCARD
condition|)
block|{
operator|++
name|i
expr_stmt|;
name|skip
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|other
operator|.
name|components
index|[
name|j
index|]
operator|.
name|compareTo
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
operator|==
literal|0
condition|)
block|{
operator|++
name|i
expr_stmt|;
name|skip
operator|=
literal|false
expr_stmt|;
block|}
if|else if
condition|(
name|skip
condition|)
block|{
continue|continue;
block|}
else|else
return|return
literal|false
return|;
block|}
if|if
condition|(
name|i
operator|==
name|pos
condition|)
block|{
if|if
condition|(
name|includeDescendants
condition|)
return|return
literal|true
return|;
return|return
name|j
operator|==
name|other
operator|.
name|pos
condition|?
literal|true
else|:
literal|false
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
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
name|pos
condition|;
name|i
operator|++
control|)
name|components
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|pos
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
if|if
condition|(
name|components
index|[
name|i
index|]
operator|.
name|getNameType
argument_list|()
operator|==
name|ElementValue
operator|.
name|ATTRIBUTE
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|components
index|[
name|i
index|]
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
specifier|private
name|void
name|addComponent
parameter_list|(
name|Map
name|namespaces
parameter_list|,
name|String
name|component
parameter_list|)
block|{
name|boolean
name|isAttribute
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|component
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|isAttribute
operator|=
literal|true
expr_stmt|;
name|component
operator|=
name|component
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|String
name|prefix
init|=
name|QName
operator|.
name|extractPrefix
argument_list|(
name|component
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|component
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
operator|(
name|String
operator|)
name|namespaces
operator|.
name|get
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
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"No namespace URI defined for prefix: "
operator|+
name|prefix
argument_list|)
expr_stmt|;
name|prefix
operator|=
literal|null
expr_stmt|;
name|namespaceURI
operator|=
literal|""
expr_stmt|;
block|}
block|}
name|QName
name|qn
init|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|isAttribute
condition|)
name|qn
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|addComponent
argument_list|(
name|qn
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|init
parameter_list|(
name|Map
name|namespaces
parameter_list|,
name|String
name|path
parameter_list|)
block|{
comment|//TODO : compute better length
name|FastStringBuffer
name|token
init|=
operator|new
name|FastStringBuffer
argument_list|(
name|path
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|path
operator|.
name|length
argument_list|()
condition|)
block|{
name|char
name|ch
init|=
name|path
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'/'
case|:
name|String
name|next
init|=
name|token
operator|.
name|toString
argument_list|()
decl_stmt|;
name|token
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|addComponent
argument_list|(
name|namespaces
argument_list|,
name|next
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
operator|++
name|pos
argument_list|)
operator|==
literal|'/'
condition|)
name|addComponent
argument_list|(
name|WILDCARD
argument_list|)
expr_stmt|;
break|break;
default|default:
name|token
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|pos
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|addComponent
argument_list|(
name|namespaces
argument_list|,
name|token
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

