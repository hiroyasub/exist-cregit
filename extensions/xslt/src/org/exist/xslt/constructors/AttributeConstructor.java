begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|constructors
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|AnalyzeContextInfo
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
name|PathExpr
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
name|XPathException
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
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|pattern
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|AttributeConstructor
extends|extends
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AttributeConstructor
block|{
specifier|public
name|AttributeConstructor
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|newDocumentContext
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_NODE_CONSTRUCTOR
operator|)
operator|==
literal|0
expr_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|newContents
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Object
argument_list|>
name|i
init|=
name|contents
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|obj
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|String
condition|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
operator|&&
name|value
operator|.
name|startsWith
argument_list|(
literal|"}"
argument_list|)
condition|)
block|{
name|PathExpr
name|expr
init|=
operator|new
name|PathExpr
argument_list|(
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
name|Pattern
operator|.
name|parse
argument_list|(
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|value
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|newContents
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
name|newContents
operator|.
name|add
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
name|contents
operator|.
name|clear
argument_list|()
expr_stmt|;
name|contents
operator|.
name|add
argument_list|(
name|newContents
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

