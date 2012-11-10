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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|FileSystemStorage
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
name|FileSystemStorage
operator|.
name|FileSystemResolver
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
name|Packages
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
comment|/**  * A repository as viewed by eXist.  *  * @author Florent Georges  * @since  2010-09-22  */
end_comment

begin_class
specifier|public
class|class
name|ExistRepository
block|{
specifier|public
specifier|final
specifier|static
name|String
name|EXPATH_REPO_DIR
init|=
literal|"expathrepo"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXPATH_REPO_DEFAULT
init|=
literal|"webapp/WEB-INF/"
operator|+
name|EXPATH_REPO_DIR
decl_stmt|;
specifier|public
name|ExistRepository
parameter_list|(
name|FileSystemStorage
name|storage
parameter_list|)
throws|throws
name|PackageException
block|{
name|myParent
operator|=
operator|new
name|Repository
argument_list|(
name|storage
argument_list|)
expr_stmt|;
name|myParent
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
name|Repository
name|getParentRepo
parameter_list|()
block|{
return|return
name|myParent
return|;
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
comment|// the URI
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
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
literal|"Invalid URI: "
operator|+
name|namespace
argument_list|,
name|ex
argument_list|)
throw|;
block|}
for|for
control|(
name|Packages
name|pp
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
name|Package
name|pkg
init|=
name|pp
operator|.
name|latest
argument_list|()
decl_stmt|;
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
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|clazz
operator|!=
literal|null
condition|)
block|{
return|return
name|getModule
argument_list|(
name|clazz
argument_list|,
name|namespace
argument_list|,
name|ctxt
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Load a module instance from its class name.  Check the namespace is consistent.      */
specifier|private
name|Module
name|getModule
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|namespace
parameter_list|,
name|XQueryContext
name|ctxt
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|Module
name|module
init|=
name|instantiateModule
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|String
name|ns
init|=
name|module
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|ns
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
literal|"The namespace in the Java module "
operator|+
literal|"does not match the namespace in the package descriptor: "
operator|+
name|namespace
operator|+
literal|" - "
operator|+
name|ns
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
name|name
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
literal|"Cannot find module class from EXPath repository: "
operator|+
name|name
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
literal|"Problem instantiating module class from EXPath repository: "
operator|+
name|name
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
literal|"Problem instantiating module class from EXPath repository: "
operator|+
name|name
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Problem instantiating module class from EXPath repository: "
operator|+
name|name
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
literal|"The class configured in EXPath repository is not a Module: "
operator|+
name|name
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Illegal argument passed to the module ctor"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**      * Try to instantiate the class using the constructor with a Map parameter,       * or the default constructor.      */
specifier|private
name|Module
name|instantiateModule
parameter_list|(
name|Class
name|clazz
parameter_list|)
throws|throws
name|XPathException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
throws|,
name|InvocationTargetException
block|{
try|try
block|{
name|Constructor
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
operator|(
name|Module
operator|)
name|ctor
operator|.
name|newInstance
argument_list|(
name|EMPTY_MAP
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
try|try
block|{
name|Constructor
name|ctor
init|=
name|clazz
operator|.
name|getConstructor
argument_list|()
decl_stmt|;
return|return
operator|(
name|Module
operator|)
name|ctor
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|exx
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot find suitable constructor "
operator|+
literal|"for module from expath repository"
argument_list|,
name|exx
argument_list|)
throw|;
block|}
block|}
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
comment|// the URI
name|URI
name|uri
decl_stmt|;
try|try
block|{
name|uri
operator|=
operator|new
name|URI
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
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
literal|"Invalid URI: "
operator|+
name|namespace
argument_list|,
name|ex
argument_list|)
throw|;
block|}
for|for
control|(
name|Packages
name|pp
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
name|Package
name|pkg
init|=
name|pp
operator|.
name|latest
argument_list|()
decl_stmt|;
comment|// FIXME: Rely on having a file system storage, that's probably a bad design!
name|FileSystemResolver
name|resolver
init|=
operator|(
name|FileSystemResolver
operator|)
name|pkg
operator|.
name|getResolver
argument_list|()
decl_stmt|;
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
name|f
init|=
name|info
operator|.
name|getXQuery
argument_list|(
name|uri
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
name|resolver
operator|.
name|resolveComponentAsFile
argument_list|(
name|f
argument_list|)
return|;
block|}
block|}
name|String
name|sysid
init|=
literal|null
decl_stmt|;
comment|// declared here to be used in catch
try|try
block|{
name|StreamSource
name|src
init|=
name|pkg
operator|.
name|resolve
argument_list|(
name|namespace
argument_list|,
name|URISpace
operator|.
name|XQUERY
argument_list|)
decl_stmt|;
if|if
condition|(
name|src
operator|!=
literal|null
condition|)
block|{
name|sysid
operator|=
name|src
operator|.
name|getSystemId
argument_list|()
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
operator|new
name|URI
argument_list|(
name|sysid
argument_list|)
argument_list|)
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
literal|"Error parsing the URI of the query library: "
operator|+
name|sysid
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PackageException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error resolving the query library: "
operator|+
name|namespace
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
specifier|public
name|List
argument_list|<
name|URI
argument_list|>
name|getJavaModules
parameter_list|()
block|{
name|List
argument_list|<
name|URI
argument_list|>
name|modules
init|=
operator|new
name|ArrayList
argument_list|<
name|URI
argument_list|>
argument_list|(
literal|13
argument_list|)
decl_stmt|;
for|for
control|(
name|Packages
name|pp
range|:
name|myParent
operator|.
name|listPackages
argument_list|()
control|)
block|{
name|Package
name|pkg
init|=
name|pp
operator|.
name|latest
argument_list|()
decl_stmt|;
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
name|modules
operator|.
name|addAll
argument_list|(
name|info
operator|.
name|getJavaModules
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|modules
return|;
block|}
specifier|public
specifier|static
name|ExistRepository
name|getRepository
parameter_list|(
name|File
name|home
parameter_list|)
throws|throws
name|PackageException
block|{
if|if
condition|(
name|home
operator|!=
literal|null
condition|)
block|{
name|File
name|repo_dir
init|=
operator|new
name|File
argument_list|(
name|home
argument_list|,
name|EXPATH_REPO_DEFAULT
argument_list|)
decl_stmt|;
comment|// ensure the dir exists
name|repo_dir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|FileSystemStorage
name|storage
init|=
operator|new
name|FileSystemStorage
argument_list|(
name|repo_dir
argument_list|)
decl_stmt|;
return|return
operator|new
name|ExistRepository
argument_list|(
name|storage
argument_list|)
return|;
block|}
else|else
block|{
name|File
name|repo_dir
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|,
name|EXPATH_REPO_DIR
argument_list|)
decl_stmt|;
comment|// ensure the dir exists
name|repo_dir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|FileSystemStorage
name|storage
init|=
operator|new
name|FileSystemStorage
argument_list|(
name|repo_dir
argument_list|)
decl_stmt|;
return|return
operator|new
name|ExistRepository
argument_list|(
name|storage
argument_list|)
return|;
block|}
block|}
comment|/** The wrapped EXPath repository. */
specifier|private
name|Repository
name|myParent
decl_stmt|;
comment|/** An empty map for constructors expecting a parameter map. */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
argument_list|>
name|EMPTY_MAP
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|Object
argument_list|>
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

