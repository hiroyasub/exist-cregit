begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_comment
comment|/**  * Base class to be implemented by an index module if it wants to rewrite  * certain query expressions. Subclasses should overwrite the rewriteXXX methods  * they are interested in.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|QueryRewriter
block|{
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|QueryRewriter
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
comment|/**      * Rewrite the expression to make use of indexes. The method may also return an additional      * pragma to be added to the extension expression which is inserted by the optimizer.      *      * @param locationStep the location step to rewrite      * @return a pragma expression to replace the step or null if not applicable      * @throws XPathException in case of a static error      */
specifier|public
name|Pragma
name|rewriteLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
literal|null
return|;
block|}
specifier|protected
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
block|}
end_class

end_unit

