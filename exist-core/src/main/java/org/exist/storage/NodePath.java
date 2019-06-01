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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|NodePath
implements|implements
name|Comparable
argument_list|<
name|NodePath
argument_list|>
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|NodePath
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * (Illegal) QNames used as a marker for arbitrary path steps.      */
specifier|public
specifier|final
specifier|static
name|QName
name|SKIP
init|=
operator|new
name|QName
argument_list|(
literal|"//"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
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
comment|//Nothing to do
block|}
specifier|public
name|NodePath
parameter_list|(
specifier|final
name|NodePath
name|other
parameter_list|)
block|{
name|this
argument_list|(
name|other
argument_list|,
name|other
operator|.
name|includeDescendants
argument_list|)
expr_stmt|;
block|}
specifier|public
name|NodePath
parameter_list|(
specifier|final
name|NodePath
name|other
parameter_list|,
specifier|final
name|boolean
name|includeDescendants
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
name|this
operator|.
name|includeDescendants
operator|=
name|includeDescendants
expr_stmt|;
block|}
comment|/**      *       */
specifier|public
name|NodePath
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
specifier|final
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
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
specifier|final
name|String
name|path
parameter_list|,
specifier|final
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
name|NodePath
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
block|{
name|addComponent
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setIncludeDescendants
parameter_list|(
specifier|final
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
block|}
specifier|public
name|boolean
name|includeDescendants
parameter_list|()
block|{
return|return
name|includeDescendants
return|;
block|}
specifier|public
name|void
name|append
parameter_list|(
specifier|final
name|NodePath
name|other
parameter_list|)
block|{
specifier|final
name|QName
index|[]
name|newComponents
init|=
operator|new
name|QName
index|[
name|length
argument_list|()
operator|+
name|other
operator|.
name|length
argument_list|()
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
name|newComponents
argument_list|,
literal|0
argument_list|,
name|pos
argument_list|)
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
name|newComponents
argument_list|,
name|pos
argument_list|,
name|other
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|pos
operator|=
name|newComponents
operator|.
name|length
expr_stmt|;
name|components
operator|=
name|newComponents
expr_stmt|;
block|}
specifier|public
name|void
name|addComponent
parameter_list|(
specifier|final
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
specifier|final
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
specifier|protected
name|void
name|reverseComponents
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
operator|/
literal|2
condition|;
operator|++
name|i
control|)
block|{
name|QName
name|tmp
init|=
name|components
index|[
name|i
index|]
decl_stmt|;
name|components
index|[
name|i
index|]
operator|=
name|components
index|[
name|pos
operator|-
literal|1
operator|-
name|i
index|]
expr_stmt|;
name|components
index|[
name|pos
operator|-
literal|1
operator|-
name|i
index|]
operator|=
name|tmp
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
block|{
name|components
index|[
operator|--
name|pos
index|]
operator|=
literal|null
expr_stmt|;
block|}
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
comment|/**      * @throws ArrayIndexOutOfBoundsException      */
specifier|public
name|QName
name|getComponent
parameter_list|(
specifier|final
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
block|{
throw|throw
operator|new
name|ArrayIndexOutOfBoundsException
argument_list|(
name|at
argument_list|)
throw|;
block|}
return|return
name|components
index|[
name|at
index|]
return|;
block|}
specifier|public
name|QName
name|getLastComponent
parameter_list|()
block|{
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
return|return
name|components
index|[
name|pos
operator|-
literal|1
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|hasWildcard
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
block|{
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
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|match
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
return|return
name|components
index|[
name|pos
operator|-
literal|1
index|]
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
specifier|final
name|boolean
name|match
parameter_list|(
specifier|final
name|NodePath
name|other
parameter_list|)
block|{
return|return
name|match
argument_list|(
name|other
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|boolean
name|match
parameter_list|(
specifier|final
name|NodePath
name|other
parameter_list|,
name|int
name|j
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
block|{
return|return
literal|true
return|;
block|}
return|return
name|j
operator|==
name|other
operator|.
name|pos
return|;
block|}
if|if
condition|(
name|components
index|[
name|i
index|]
operator|==
name|SKIP
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
operator|(
name|components
index|[
name|i
index|]
operator|==
name|WILDCARD
operator|||
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
operator|)
operator|&&
operator|(
operator|!
name|skip
operator|||
name|j
operator|+
literal|1
operator|==
name|other
operator|.
name|pos
operator|||
name|other
operator|.
name|components
index|[
name|j
operator|+
literal|1
index|]
operator|.
name|compareTo
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
operator|!=
literal|0
operator|)
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
block|{
return|return
literal|false
return|;
block|}
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
block|{
return|return
literal|true
return|;
block|}
return|return
name|j
operator|==
name|other
operator|.
name|pos
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
block|{
name|components
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|pos
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
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
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|addComponent
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
specifier|final
name|String
name|origComponent
parameter_list|)
block|{
name|String
name|component
init|=
name|origComponent
decl_stmt|;
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
try|try
block|{
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
specifier|final
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
literal|null
decl_stmt|;
comment|//TODO simplify this code (AR)
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
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
comment|//TODO : throw exception ? -pb
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
if|else if
condition|(
name|namespaces
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|namespaces
operator|.
name|get
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
expr_stmt|;
block|}
specifier|final
name|QName
name|qn
decl_stmt|;
if|if
condition|(
name|isAttribute
condition|)
block|{
name|qn
operator|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|,
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|qn
operator|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"URI = "
operator|+
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|addComponent
argument_list|(
name|qn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|init
parameter_list|(
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
specifier|final
name|String
name|path
parameter_list|)
block|{
comment|//TODO : compute better length
specifier|final
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
specifier|final
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
literal|'*'
case|:
name|addComponent
argument_list|(
name|WILDCARD
argument_list|)
expr_stmt|;
name|token
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|pos
operator|++
expr_stmt|;
break|break;
case|case
literal|'/'
case|:
specifier|final
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
block|{
name|addComponent
argument_list|(
name|namespaces
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
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
block|{
name|addComponent
argument_list|(
name|SKIP
argument_list|)
expr_stmt|;
block|}
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
block|{
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
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|!=
literal|null
operator|&&
name|obj
operator|instanceof
name|NodePath
condition|)
block|{
specifier|final
name|NodePath
name|nodePath
init|=
operator|(
name|NodePath
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|nodePath
operator|.
name|pos
operator|==
name|pos
condition|)
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
block|{
if|if
condition|(
operator|!
name|nodePath
operator|.
name|components
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
literal|0
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
name|h
operator|=
literal|31
operator|*
name|h
operator|+
name|components
index|[
name|i
index|]
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
return|return
name|h
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|NodePath
name|other
parameter_list|)
block|{
return|return
name|toString
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|toString
argument_list|()
argument_list|)
return|;
comment|//TODO: optimize
block|}
block|}
end_class

end_unit
