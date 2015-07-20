begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2010-2015 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|expathrepo
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
name|io
operator|.
name|FileOutputStream
import|;
end_import

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
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|dom
operator|.
name|persistent
operator|.
name|BinaryDocument
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
name|persistent
operator|.
name|DocumentImpl
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
name|dom
operator|.
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|Deployment
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|repo
operator|.
name|PackageLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|NativeBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|*
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
name|*
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_class
specifier|public
class|class
name|Deploy
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Deploy
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"deploy"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Deploy an application package. Installs package contents to the specified target collection, using the permissions "
operator|+
literal|"defined by the&lt;permissions&gt; element in repo.xml. Pre- and post-install XQuery scripts can be specified "
operator|+
literal|"via the&lt;prepare&gt; and&lt;finish&gt; elements."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"pkgName"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"package name"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"deploy"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Deploy an application package. Installs package contents to the specified target collection, using the permissions "
operator|+
literal|"defined by the&lt;permissions&gt; element in repo.xml. Pre- and post-install XQuery scripts can be specified "
operator|+
literal|"via the&lt;prepare&gt; and&lt;finish&gt; elements."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"pkgName"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"package name"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"targetCollection"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the target "
operator|+
literal|"collection into which the package will be stored"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"install-and-deploy"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Downloads, installs and deploys a package from the public repository at $publicRepoURL. Dependencies are resolved "
operator|+
literal|"automatically. For downloading the package, the package name is appended to the repository URL as "
operator|+
literal|"parameter 'name'."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"pkgName"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Unique name of the package to install."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"publicRepoURL"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URL of the public repo."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"install-and-deploy"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Downloads, installs and deploys a package from the public repository at $publicRepoURL. Dependencies are resolved "
operator|+
literal|"automatically. For downloading the package, the package name and version are appended to the repository URL as "
operator|+
literal|"parameters 'name' and 'version'."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"pkgName"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Unique name of the package to install."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"version"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Version to install."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"publicRepoURL"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URL of the public repo."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"install-and-deploy-from-db"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Installs and deploys a package from a .xar archive file stored in the database. Dependencies are not "
operator|+
literal|"resolved and will just be ignored."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Database path to the package archive (.xar file)"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"install-and-deploy-from-db"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Installs and deploys a package from a .xar archive file stored in the database. Dependencies will be downloaded "
operator|+
literal|"from the public repo and installed automatically."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Database path to the package archive (.xar file)"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"publicRepoURL"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URL of the public repo."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"undeploy"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExpathPackageModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Uninstall the resources belonging to a package from the db. Calls cleanup scripts if defined."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"pkgName"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"package name"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|STATUS_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"status"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
specifier|public
name|Deploy
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
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
annotation|@
name|Override
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
if|if
condition|(
operator|!
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY003
argument_list|,
literal|"Permission denied. You need to be a member "
operator|+
literal|"of the dba group to use repo:deploy/undeploy"
argument_list|)
throw|;
name|String
name|pkgName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
try|try
block|{
name|Deployment
name|deployment
init|=
operator|new
name|Deployment
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
name|Optional
argument_list|<
name|String
argument_list|>
name|target
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"deploy"
argument_list|)
condition|)
block|{
name|String
name|userTarget
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
name|userTarget
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|target
operator|=
name|deployment
operator|.
name|deploy
argument_list|(
name|pkgName
argument_list|,
name|context
operator|.
name|getRepository
argument_list|()
argument_list|,
name|userTarget
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"install-and-deploy"
argument_list|)
condition|)
block|{
name|String
name|version
init|=
literal|null
decl_stmt|;
name|String
name|repoURI
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
name|version
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|repoURI
operator|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|repoURI
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
name|target
operator|=
name|installAndDeploy
argument_list|(
name|pkgName
argument_list|,
name|version
argument_list|,
name|repoURI
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"install-and-deploy-from-db"
argument_list|)
condition|)
block|{
name|String
name|repoURI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
name|repoURI
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|target
operator|=
name|installAndDeployFromDb
argument_list|(
name|pkgName
argument_list|,
name|repoURI
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|=
name|deployment
operator|.
name|undeploy
argument_list|(
name|pkgName
argument_list|,
name|context
operator|.
name|getRepository
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|target
operator|.
name|orElseThrow
argument_list|(
parameter_list|()
lambda|->
operator|new
name|XPathException
argument_list|(
literal|"expath repository is not available."
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|statusReport
argument_list|(
name|target
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PackageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOER0000
argument_list|,
literal|"Caught IO error while deploying expath archive"
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Optional
argument_list|<
name|String
argument_list|>
name|installAndDeploy
parameter_list|(
name|String
name|pkgName
parameter_list|,
name|String
name|version
parameter_list|,
name|String
name|repoURI
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|RepoPackageLoader
name|loader
init|=
operator|new
name|RepoPackageLoader
argument_list|(
name|repoURI
argument_list|)
decl_stmt|;
name|Deployment
name|deployment
init|=
operator|new
name|Deployment
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|xar
init|=
name|loader
operator|.
name|load
argument_list|(
name|pkgName
argument_list|,
operator|new
name|PackageLoader
operator|.
name|Version
argument_list|(
name|version
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|xar
operator|!=
literal|null
condition|)
return|return
name|deployment
operator|.
name|installAndDeploy
argument_list|(
name|xar
argument_list|,
name|loader
argument_list|)
return|;
return|return
name|Optional
operator|.
name|empty
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY005
argument_list|,
literal|"Malformed URL: "
operator|+
name|repoURI
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PackageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY007
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY007
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Optional
argument_list|<
name|String
argument_list|>
name|installAndDeployFromDb
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|repoURI
parameter_list|)
throws|throws
name|XPathException
block|{
name|XmldbURI
name|docPath
init|=
name|XmldbURI
operator|.
name|createInternal
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|docPath
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY001
argument_list|,
name|path
operator|+
literal|" is not a valid .xar"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|path
argument_list|)
argument_list|)
throw|;
name|File
name|file
init|=
operator|(
operator|(
name|NativeBroker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|)
operator|.
name|getCollectionBinaryFileFsPath
argument_list|(
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
decl_stmt|;
name|RepoPackageLoader
name|loader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|repoURI
operator|!=
literal|null
condition|)
name|loader
operator|=
operator|new
name|RepoPackageLoader
argument_list|(
name|repoURI
argument_list|)
expr_stmt|;
name|Deployment
name|deployment
init|=
operator|new
name|Deployment
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|deployment
operator|.
name|installAndDeploy
argument_list|(
name|file
argument_list|,
name|loader
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|PackageException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY007
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY007
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY007
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|Sequence
name|statusReport
parameter_list|(
name|Optional
argument_list|<
name|String
argument_list|>
name|target
parameter_list|)
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"result"
argument_list|,
literal|"result"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"ok"
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"target"
argument_list|,
literal|"target"
argument_list|,
literal|"CDATA"
argument_list|,
name|target
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"result"
argument_list|,
literal|"result"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"fail"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|STATUS_ELEMENT
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
literal|1
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|RepoPackageLoader
implements|implements
name|PackageLoader
block|{
specifier|private
name|String
name|repoURL
decl_stmt|;
specifier|public
name|RepoPackageLoader
parameter_list|(
name|String
name|repoURL
parameter_list|)
block|{
name|this
operator|.
name|repoURL
operator|=
name|repoURL
expr_stmt|;
block|}
specifier|public
name|File
name|load
parameter_list|(
name|String
name|name
parameter_list|,
name|Version
name|version
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|pkgURL
init|=
name|repoURL
operator|+
literal|"?name="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|name
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|version
operator|.
name|getMin
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pkgURL
operator|+=
literal|"&semver-min="
operator|+
name|version
operator|.
name|getMin
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|.
name|getMax
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pkgURL
operator|+=
literal|"&semver-max="
operator|+
name|version
operator|.
name|getMax
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|.
name|getSemVer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pkgURL
operator|+=
literal|"&semver="
operator|+
name|version
operator|.
name|getSemVer
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|.
name|getVersion
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|pkgURL
operator|+=
literal|"&version="
operator|+
name|URLEncoder
operator|.
name|encode
argument_list|(
name|version
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Retrieving package from "
operator|+
name|pkgURL
argument_list|)
expr_stmt|;
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
operator|new
name|URL
argument_list|(
name|pkgURL
argument_list|)
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setConnectTimeout
argument_list|(
literal|15
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setReadTimeout
argument_list|(
literal|15
operator|*
literal|1000
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setRequestProperty
argument_list|(
literal|"User-Agent"
argument_list|,
literal|"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) "
operator|+
literal|"Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|connect
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|File
name|outFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"deploy"
argument_list|,
literal|"xar"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|fos
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|outFile
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|int
name|count
decl_stmt|;
while|while
condition|(
operator|(
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|fos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
return|return
name|outFile
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|fos
operator|!=
literal|null
condition|)
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

