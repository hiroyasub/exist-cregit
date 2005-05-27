begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|PatternSyntaxException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|GlobToRegex
import|;
end_import

begin_comment
comment|/**  * A {@link org.exist.storage.TermMatcher} that matches index entries against a  * regular expression. Used by {@link org.exist.storage.NativeTextEngine} and  * {@link org.exist.storage.NativeValueIndex}.  *   * @author wolf  *  */
end_comment

begin_class
class|class
name|RegexMatcher
implements|implements
name|TermMatcher
block|{
specifier|private
name|Matcher
name|matcher
decl_stmt|;
specifier|public
name|RegexMatcher
parameter_list|(
name|String
name|expr
parameter_list|,
name|int
name|type
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|EXistException
block|{
try|try
block|{
comment|// if expr is a file glob, translate it to a regular expression first
if|if
condition|(
name|type
operator|==
name|DBBroker
operator|.
name|MATCH_WILDCARDS
condition|)
block|{
name|expr
operator|=
name|GlobToRegex
operator|.
name|globToRegexp
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|flags
operator|=
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|UNICODE_CASE
expr_stmt|;
block|}
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|expr
argument_list|,
name|flags
argument_list|)
decl_stmt|;
name|matcher
operator|=
name|pattern
operator|.
name|matcher
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PatternSyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Invalid regular expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see java.util.Comparator#equals(java.lang.Object) 	 */
specifier|public
name|boolean
name|matches
parameter_list|(
name|CharSequence
name|term
parameter_list|)
block|{
name|matcher
operator|.
name|reset
argument_list|(
name|term
argument_list|)
expr_stmt|;
return|return
name|matcher
operator|.
name|find
argument_list|()
return|;
block|}
block|}
end_class

end_unit

