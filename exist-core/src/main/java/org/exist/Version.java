begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2010-2014 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Version
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NAME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VERSION
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BUILD
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GIT_BRANCH
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GIT_COMMIT
decl_stmt|;
static|static
block|{
specifier|final
name|SystemProperties
name|systemProperties
init|=
name|SystemProperties
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|NAME
operator|=
name|systemProperties
operator|.
name|getSystemProperty
argument_list|(
literal|"product-name"
argument_list|,
literal|"eXist"
argument_list|)
expr_stmt|;
name|VERSION
operator|=
name|systemProperties
operator|.
name|getSystemProperty
argument_list|(
literal|"product-version"
argument_list|)
expr_stmt|;
name|BUILD
operator|=
name|systemProperties
operator|.
name|getSystemProperty
argument_list|(
literal|"product-build"
argument_list|)
expr_stmt|;
name|GIT_BRANCH
operator|=
name|systemProperties
operator|.
name|getSystemProperty
argument_list|(
literal|"git-branch"
argument_list|)
expr_stmt|;
name|GIT_COMMIT
operator|=
name|systemProperties
operator|.
name|getSystemProperty
argument_list|(
literal|"git-commit"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getProductName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
specifier|public
specifier|static
name|String
name|getVersion
parameter_list|()
block|{
return|return
name|VERSION
return|;
block|}
specifier|public
specifier|static
name|String
name|getBuild
parameter_list|()
block|{
return|return
name|BUILD
return|;
block|}
comment|/** 	 * @deprecated Use {@link #getGitCommit()} 	 */
annotation|@
name|Deprecated
specifier|public
specifier|static
name|String
name|getSvnRevision
parameter_list|()
block|{
return|return
name|GIT_COMMIT
return|;
block|}
specifier|public
specifier|static
name|String
name|getGitBranch
parameter_list|()
block|{
return|return
name|GIT_BRANCH
return|;
block|}
specifier|public
specifier|static
name|String
name|getGitCommit
parameter_list|()
block|{
return|return
name|GIT_COMMIT
return|;
block|}
block|}
end_class

end_unit

