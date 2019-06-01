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
name|xquery
operator|.
name|pragmas
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|*
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
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_class
specifier|public
class|class
name|ProfilePragma
extends|extends
name|Pragma
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|PROFILING_PRAGMA
init|=
operator|new
name|QName
argument_list|(
literal|"profiling"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|public
name|ProfilePragma
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|contents
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|qname
argument_list|,
name|contents
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|after
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Profiler
name|profiler
init|=
name|context
operator|.
name|getProfiler
argument_list|()
decl_stmt|;
name|profiler
operator|.
name|setEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|before
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Profiler
name|profiler
init|=
name|context
operator|.
name|getProfiler
argument_list|()
decl_stmt|;
specifier|final
name|Option
name|pragma
init|=
operator|new
name|Option
argument_list|(
name|getQName
argument_list|()
argument_list|,
name|getContents
argument_list|()
argument_list|)
decl_stmt|;
name|profiler
operator|.
name|configure
argument_list|(
name|pragma
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
