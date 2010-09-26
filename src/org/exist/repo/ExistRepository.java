begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/****************************************************************************/
end_comment

begin_comment
comment|/*  File:       ExistRepository.java                                        */
end_comment

begin_comment
comment|/*  Author:     F. Georges - H2O Consulting                                 */
end_comment

begin_comment
comment|/*  Date:       2010-09-22                                                  */
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
name|io
operator|.
name|File
import|;
end_import

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
name|net
operator|.
name|URISyntaxException
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
name|InternalModule
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
name|Module
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
name|Repository
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
comment|/**  * TODO: ...  *  * @author Florent Georges  * @date   2010-09-22  */
end_comment

begin_class
specifier|public
class|class
name|ExistRepository
block|{
specifier|public
name|ExistRepository
parameter_list|(
name|File
name|root
parameter_list|)
throws|throws
name|PackageException
block|{
name|this
argument_list|(
operator|new
name|Repository
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ExistRepository
parameter_list|(
name|Repository
name|parent
parameter_list|)
throws|throws
name|PackageException
block|{
name|myParent
operator|=
name|parent
expr_stmt|;
name|parent
operator|.
name|registerExtension
argument_list|(
operator|new
name|ExistPkgExtension
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Module
name|resolveJavaModule
parameter_list|(
name|String
name|namespace
parameter_list|,
name|XQueryContext
name|ctxt
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|Package
name|pkg
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
name|ExistPkgInfo
name|info
init|=
operator|(
name|ExistPkgInfo
operator|)
name|pkg
operator|.
name|getInfo
argument_list|(
literal|"exist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|String
name|clazz
init|=
name|info
operator|.
name|getJava
argument_list|(
name|namespace
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Class
name|c
init|=
name|Class
operator|.
name|forName
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|InternalModule
name|im
init|=
operator|(
name|InternalModule
operator|)
name|c
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|String
name|im_ns
init|=
name|im
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|im_ns
operator|.
name|equals
argument_list|(
name|namespace
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The namespace in the Java module does not match the namespace in the package descriptor: "
operator|+
name|namespace
operator|+
literal|" - "
operator|+
name|im_ns
argument_list|)
throw|;
block|}
return|return
name|ctxt
operator|.
name|loadBuiltInModule
argument_list|(
name|namespace
argument_list|,
name|clazz
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot find module from expath repository, but it should be there."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Problem instantiating module from expath repository."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot access expath repository directory"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Problem casting module from expath repository."
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|File
name|resolveXQueryModule
parameter_list|(
name|String
name|namespace
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|Package
name|pkg
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
name|ExistPkgInfo
name|info
init|=
operator|(
name|ExistPkgInfo
operator|)
name|pkg
operator|.
name|getInfo
argument_list|(
literal|"exist"
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|File
name|f
init|=
name|info
operator|.
name|getXQuery
argument_list|(
name|namespace
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
return|return
name|f
return|;
block|}
block|}
comment|// TODO: Should we really build URI objects?  Shouldn't the EXPath
comment|// repository use plain strings instead?
try|try
block|{
name|File
name|f
init|=
name|pkg
operator|.
name|resolveFile
argument_list|(
operator|new
name|URI
argument_list|(
name|namespace
argument_list|)
argument_list|,
name|URISpace
operator|.
name|XQUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|f
operator|!=
literal|null
condition|)
block|{
return|return
name|f
return|;
block|}
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Namespace URI is not correct URI"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** The wrapped EXPath repository. */
specifier|private
name|Repository
name|myParent
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

