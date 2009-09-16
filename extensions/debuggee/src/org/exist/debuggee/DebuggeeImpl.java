begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
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
name|util
operator|.
name|HashMap
import|;
end_import

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
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggeeImpl
implements|implements
name|Debuggee
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Debuggee
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|FEATURES
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|FEATURES
operator|.
name|put
argument_list|(
literal|"language_supports_threads"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"language_name"
argument_list|,
literal|"XQuery"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"language_version"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"encoding"
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"protocol_version"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"supports_async"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"breakpoint_types"
argument_list|,
literal|"line"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"multiple_sessions"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"max_children"
argument_list|,
literal|"32"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"max_data"
argument_list|,
literal|"1024"
argument_list|)
expr_stmt|;
name|FEATURES
operator|.
name|put
argument_list|(
literal|"max_depth"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
block|}
name|DebuggeeConnectionTCP
name|connection
init|=
literal|null
decl_stmt|;
specifier|public
name|DebuggeeImpl
parameter_list|()
block|{
block|}
specifier|public
name|DebuggeeJoint
name|joint
parameter_list|()
block|{
name|DebuggeeJoint
name|joint
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|connection
operator|=
operator|new
name|DebuggeeConnectionTCP
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|connection
operator|.
name|connect
argument_list|()
condition|)
name|joint
operator|=
operator|new
name|DebuggeeJointImpl
argument_list|()
expr_stmt|;
block|}
return|return
name|joint
return|;
block|}
block|}
end_class

end_unit

