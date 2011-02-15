begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/****************************************************************************/
end_comment

begin_comment
comment|/*  File:       ExistPkgInfo.java                                           */
end_comment

begin_comment
comment|/*  Author:     F. Georges - H2O Consulting                                 */
end_comment

begin_comment
comment|/*  Date:       2010-09-21                                                  */
end_comment

begin_comment
comment|/*  Tags:                                                                   */
end_comment

begin_comment
comment|/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
end_comment

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|repo
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
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
name|HashSet
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Package
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|PackageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|PackageInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|URISpace
import|;
end_import

begin_comment
comment|/**  * The extended package info, dedicated to eXist.  *  * @author Florent Georges  * @since  2010-09-21  */
end_comment

begin_class
specifier|public
class|class
name|ExistPkgInfo
extends|extends
name|PackageInfo
block|{
specifier|public
name|ExistPkgInfo
parameter_list|(
name|Package
name|pkg
parameter_list|)
block|{
name|super
argument_list|(
literal|"exist"
argument_list|,
name|pkg
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|StreamSource
name|resolve
parameter_list|(
name|String
name|href
parameter_list|,
name|URISpace
name|space
parameter_list|)
throws|throws
name|PackageException
block|{
comment|// TODO: Really?  Probably to refactor in accordance with ExistRepository...
return|return
literal|null
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getJars
parameter_list|()
block|{
return|return
name|myJars
return|;
block|}
specifier|public
name|String
name|getJava
parameter_list|(
name|URI
name|namespace
parameter_list|)
block|{
return|return
name|myJava
operator|.
name|get
argument_list|(
name|namespace
argument_list|)
return|;
block|}
specifier|public
name|String
name|getXQuery
parameter_list|(
name|URI
name|namespace
parameter_list|)
block|{
return|return
name|myXquery
operator|.
name|get
argument_list|(
name|namespace
argument_list|)
return|;
block|}
specifier|public
name|void
name|addJar
parameter_list|(
name|String
name|jar
parameter_list|)
block|{
name|myJars
operator|.
name|add
argument_list|(
name|jar
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addJava
parameter_list|(
name|URI
name|uri
parameter_list|,
name|String
name|fun
parameter_list|)
block|{
name|myJava
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|fun
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addXQuery
parameter_list|(
name|URI
name|uri
parameter_list|,
name|String
name|file
parameter_list|)
block|{
name|myXquery
operator|.
name|put
argument_list|(
name|uri
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|myJars
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|URI
argument_list|,
name|String
argument_list|>
name|myJava
init|=
operator|new
name|HashMap
argument_list|<
name|URI
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|URI
argument_list|,
name|String
argument_list|>
name|myXquery
init|=
operator|new
name|HashMap
argument_list|<
name|URI
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
block|}
end_class

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

begin_comment
comment|/*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.               */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The contents of this file are subject to the Mozilla Public License     */
end_comment

begin_comment
comment|/*  Version 1.0 (the "License"); you may not use this file except in        */
end_comment

begin_comment
comment|/*  compliance with the License. You may obtain a copy of the License at    */
end_comment

begin_comment
comment|/*  http://www.mozilla.org/MPL/.                                            */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  Software distributed under the License is distributed on an "AS IS"     */
end_comment

begin_comment
comment|/*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See    */
end_comment

begin_comment
comment|/*  the License for the specific language governing rights and limitations  */
end_comment

begin_comment
comment|/*  under the License.                                                      */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The Original Code is: all this file.                                    */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The Initial Developer of the Original Code is Florent Georges.          */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  Contributor(s): none.                                                   */
end_comment

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

end_unit

