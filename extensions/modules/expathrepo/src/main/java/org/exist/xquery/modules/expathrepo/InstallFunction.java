begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2017 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|persistent
operator|.
name|LockedDocument
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
name|ExistPkgInfo
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
name|ExistRepository
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
name|repo
operator|.
name|ClasspathHelper
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
operator|.
name|LockMode
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
name|txn
operator|.
name|TransactionException
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
name|txn
operator|.
name|Txn
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
name|BasicFunction
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
name|BooleanValue
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
name|FunctionParameterSequenceType
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
name|StringValue
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
name|expath
operator|.
name|pkg
operator|.
name|repo
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
name|tui
operator|.
name|BatchUserInteraction
import|;
end_import

begin_comment
comment|/**  * Install Function: Install package into repository  *  * @author<a href="mailto:jim.fuller@exist-db.org">James Fuller</a>  * @author Wolfgang Meier  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|InstallFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|InstallFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatureInstall
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"install"
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
literal|"Install package from repository."
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
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true if successful, false otherwise"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatureInstallFromDB
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"install-from-db"
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
literal|"Install package stored in database."
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
literal|"database path to the package archive (.xar file)"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"true if successful, false otherwise"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|InstallFunction
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
name|Sequence
name|removed
init|=
name|BooleanValue
operator|.
name|FALSE
decl_stmt|;
name|boolean
name|force
init|=
literal|true
decl_stmt|;
name|UserInteractionStrategy
name|interact
init|=
operator|new
name|BatchUserInteraction
argument_list|()
decl_stmt|;
name|String
name|pkgOrPath
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Optional
argument_list|<
name|ExistRepository
argument_list|>
name|repo
init|=
name|getContext
argument_list|()
operator|.
name|getRepository
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|repo
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|Repository
name|parent_repo
init|=
name|repo
operator|.
name|get
argument_list|()
operator|.
name|getParentRepo
argument_list|()
decl_stmt|;
name|Package
name|pkg
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"install"
argument_list|)
condition|)
block|{
comment|// download .xar from a URI
name|URI
name|uri
init|=
name|_getURI
argument_list|(
name|pkgOrPath
argument_list|)
decl_stmt|;
name|pkg
operator|=
name|parent_repo
operator|.
name|installPackage
argument_list|(
name|uri
argument_list|,
name|force
argument_list|,
name|interact
argument_list|)
expr_stmt|;
name|repo
operator|.
name|get
argument_list|()
operator|.
name|reportAction
argument_list|(
name|ExistRepository
operator|.
name|Action
operator|.
name|INSTALL
argument_list|,
name|pkg
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// .xar is stored as a binary resource
try|try
init|(
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|getBinaryDoc
argument_list|(
name|pkgOrPath
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|continueOrBeginTransaction
argument_list|()
init|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|lockedDoc
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Installing file: "
operator|+
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|pkg
operator|=
name|parent_repo
operator|.
name|installPackage
argument_list|(
operator|new
name|BinaryDocumentXarSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|transaction
argument_list|,
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|)
argument_list|,
name|force
argument_list|,
name|interact
argument_list|)
expr_stmt|;
name|repo
operator|.
name|get
argument_list|()
operator|.
name|reportAction
argument_list|(
name|ExistRepository
operator|.
name|Action
operator|.
name|INSTALL
argument_list|,
name|pkg
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
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
operator|&&
operator|!
name|info
operator|.
name|getJars
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
name|ClasspathHelper
operator|.
name|updateClasspath
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|pkg
argument_list|)
expr_stmt|;
comment|// TODO: expath libs do not provide a way to see if there were any XQuery modules installed at all
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryPool
argument_list|()
operator|.
name|clear
argument_list|()
expr_stmt|;
name|removed
operator|=
name|BooleanValue
operator|.
name|TRUE
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"expath repository not available"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|PackageException
decl||
name|TransactionException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
name|removed
return|;
comment|// /TODO: _repo.removePackage seems to throw PackageException
comment|//throw new XPathException("Problem installing package " + pkg + " in expath repository, check that eXist-db has access permissions to expath repository file directory  ", ex);
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|removed
return|;
block|}
return|return
name|removed
return|;
block|}
specifier|private
name|URI
name|_getURI
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|XPathException
block|{
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
name|s
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
name|this
argument_list|,
name|EXPathErrorCode
operator|.
name|EXPDY001
argument_list|,
name|s
operator|+
literal|" is not a valid URI: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
operator|new
name|StringValue
argument_list|(
name|s
argument_list|)
argument_list|,
name|ex
argument_list|)
throw|;
block|}
if|if
condition|(
name|uri
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
return|return
name|uri
return|;
block|}
else|else
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
name|s
operator|+
literal|" must be an absolute URI"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|s
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|private
name|LockedDocument
name|getBinaryDoc
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|XmldbURI
name|uri
init|=
name|XmldbURI
operator|.
name|createInternal
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|uri
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
if|if
condition|(
name|lockedDoc
operator|==
literal|null
condition|)
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
name|path
operator|+
literal|" is not .xar resource"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|path
argument_list|)
argument_list|)
throw|;
block|}
if|else if
condition|(
name|lockedDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getResourceType
argument_list|()
operator|!=
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
name|lockedDoc
operator|.
name|close
argument_list|()
expr_stmt|;
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
literal|" is not a valid .xar, it's not a binary resource"
argument_list|,
operator|new
name|StringValue
argument_list|(
name|path
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|lockedDoc
return|;
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
name|EXPDY003
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
operator|new
name|StringValue
argument_list|(
name|path
argument_list|)
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

