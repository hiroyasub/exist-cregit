begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
comment|/**  * Install Function: Install package into repository  *  * @author James Fuller<jim.fuller@exist-db.org>  * @author Wolfgang Meier  * @version 1.0  */
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
literal|"text"
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
name|ExistRepository
name|repo
init|=
name|getContext
argument_list|()
operator|.
name|getRepository
argument_list|()
decl_stmt|;
name|Repository
name|parent_repo
init|=
name|repo
operator|.
name|getParentRepo
argument_list|()
decl_stmt|;
try|try
block|{
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
name|BinaryDocument
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|_getDocument
argument_list|(
name|pkgOrPath
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Installing file: "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|pkg
operator|=
name|parent_repo
operator|.
name|installPackage
argument_list|(
name|file
argument_list|,
name|force
argument_list|,
name|interact
argument_list|)
expr_stmt|;
name|repo
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
catch|catch
parameter_list|(
name|PackageException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
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
name|BinaryDocument
name|_getDocument
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
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
name|DocumentImpl
name|doc
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
name|Lock
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
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
return|return
operator|(
name|BinaryDocument
operator|)
name|doc
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

