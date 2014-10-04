begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|xquery
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
name|persistent
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
name|io
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|versioning
operator|.
name|svn
operator|.
name|WorkingCopy
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
name|Cardinality
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
name|FunctionSignature
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
name|xquery
operator|.
name|value
operator|.
name|FunctionReturnSequenceType
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
name|IntegerValue
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
name|SequenceType
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
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNCommitInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|tmatesoft
operator|.
name|svn
operator|.
name|core
operator|.
name|SVNException
import|;
end_import

begin_comment
comment|/**  * Commits files or directories into repository.  *   * @author<a href="mailto:amir.akhmedov@gmail.com">Amir Akhmedov</a>  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
specifier|public
class|class
name|SVNCommit
extends|extends
name|AbstractSVNFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"commit"
argument_list|,
name|SVNModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SVNModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Commits files or directories into repository."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|DB_PATH
block|,
name|MESSAGE
block|,
name|LOGIN
block|,
name|PASSWORD
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the revision number the repository was committed to"
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      *      * @param context      */
specifier|public
name|SVNCommit
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/**      * Process the function. All arguments are passed in the array args. The number of      * arguments, their type and cardinality have already been checked to match      * the function signature.      *      * @param args      * @param contextSequence      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|wcDir
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|comment
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|user
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|password
init|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|SVNCommitInfo
name|info
init|=
literal|null
decl_stmt|;
try|try
block|{
name|WorkingCopy
name|wc
init|=
operator|new
name|WorkingCopy
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|info
operator|=
name|wc
operator|.
name|commit
argument_list|(
operator|new
name|Resource
argument_list|(
name|wcDir
argument_list|)
argument_list|,
literal|false
argument_list|,
name|comment
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SVNException
name|svne
parameter_list|)
block|{
name|svne
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"error while commiting a working copy to the repository '"
operator|+
name|wcDir
operator|+
literal|"'"
argument_list|,
name|svne
argument_list|)
throw|;
block|}
if|if
condition|(
name|info
operator|==
literal|null
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
operator|-
literal|1
argument_list|)
return|;
return|return
operator|new
name|IntegerValue
argument_list|(
name|info
operator|.
name|getNewRevision
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

