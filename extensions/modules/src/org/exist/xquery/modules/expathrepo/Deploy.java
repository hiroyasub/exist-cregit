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
name|io
operator|.
name|FileInputStream
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
name|util
operator|.
name|jar
operator|.
name|JarEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|jar
operator|.
name|JarInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|IndexInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|ConfigurationException
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
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
name|memtree
operator|.
name|ElementImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|InMemoryNodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
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
name|memtree
operator|.
name|NodeImpl
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
name|Permission
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
name|security
operator|.
name|SecurityManager
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
name|UUIDGenerator
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
name|internal
operator|.
name|aider
operator|.
name|GroupAider
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
name|internal
operator|.
name|aider
operator|.
name|UserAider
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
name|xacml
operator|.
name|AccessContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|FileSource
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
name|storage
operator|.
name|txn
operator|.
name|TransactionManager
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
name|util
operator|.
name|LockException
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
name|MimeTable
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
name|MimeType
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
name|CompiledXQuery
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
name|ErrorCodes
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
name|NameTest
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
name|XQuery
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
name|util
operator|.
name|DocUtils
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
name|xml
operator|.
name|sax
operator|.
name|InputSource
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
name|Logger
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
name|signature
init|=
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
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|SETUP_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"setup"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|PRE_SETUP_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"prepare"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|POST_SETUP_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"finish"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|TARGET_COLL_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"target"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|PERMISSIONS_ELEMENT
init|=
operator|new
name|QName
argument_list|(
literal|"permissions"
argument_list|,
name|ExpathPackageModule
operator|.
name|NAMESPACE_URI
argument_list|)
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
specifier|private
name|String
name|user
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|password
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|group
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|perms
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|Deploy
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
name|File
name|packageDir
init|=
literal|null
decl_stmt|;
name|ExistRepository
name|repo
init|=
name|context
operator|.
name|getRepository
argument_list|()
decl_stmt|;
for|for
control|(
name|Packages
name|pp
range|:
name|repo
operator|.
name|getParentRepo
argument_list|()
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
if|if
condition|(
name|pkg
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|pkgName
argument_list|)
condition|)
block|{
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
name|packageDir
operator|=
name|resolver
operator|.
name|resolveResourceAsFile
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|packageDir
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Package "
operator|+
name|pkgName
operator|+
literal|" not found"
argument_list|)
throw|;
comment|// find and parse the repo.xml descriptor
name|File
name|repoFile
init|=
operator|new
name|File
argument_list|(
name|packageDir
argument_list|,
literal|"repo.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|repoFile
operator|.
name|canRead
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|DocumentImpl
name|repoXML
init|=
name|DocUtils
operator|.
name|parse
argument_list|(
name|context
argument_list|,
operator|new
name|FileInputStream
argument_list|(
name|repoFile
argument_list|)
argument_list|)
decl_stmt|;
comment|// if there's a<setup> element, run the query it points to
name|ElementImpl
name|setup
init|=
name|findElement
argument_list|(
name|repoXML
argument_list|,
name|SETUP_ELEMENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|setup
operator|!=
literal|null
condition|)
block|{
name|runQuery
argument_list|(
name|packageDir
argument_list|,
name|setup
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|statusReport
argument_list|(
literal|null
argument_list|)
return|;
block|}
else|else
block|{
comment|// otherwise copy all child directories to the target collection
name|XmldbURI
name|targetCollection
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
decl_stmt|;
name|ElementImpl
name|target
init|=
name|findElement
argument_list|(
name|repoXML
argument_list|,
name|TARGET_COLL_ELEMENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
block|{
comment|// determine target collection
try|try
block|{
name|targetCollection
operator|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|target
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Bad collection URI for<target> element: "
operator|+
name|target
operator|.
name|getStringValue
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|ElementImpl
name|permissions
init|=
name|findElement
argument_list|(
name|repoXML
argument_list|,
name|PERMISSIONS_ELEMENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|permissions
operator|!=
literal|null
condition|)
block|{
comment|// get user, group and default permissions
name|user
operator|=
name|permissions
operator|.
name|getAttribute
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
name|group
operator|=
name|permissions
operator|.
name|getAttribute
argument_list|(
literal|"group"
argument_list|)
expr_stmt|;
name|password
operator|=
name|permissions
operator|.
name|getAttribute
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
name|String
name|mode
init|=
name|permissions
operator|.
name|getAttribute
argument_list|(
literal|"mode"
argument_list|)
decl_stmt|;
try|try
block|{
name|perms
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|mode
argument_list|,
literal|8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Bad format for mode attribute in<permissions>: "
operator|+
name|mode
argument_list|)
throw|;
block|}
block|}
comment|// run the pre-setup query if present
name|ElementImpl
name|preSetup
init|=
name|findElement
argument_list|(
name|repoXML
argument_list|,
name|PRE_SETUP_ELEMENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|preSetup
operator|!=
literal|null
condition|)
name|runQuery
argument_list|(
name|packageDir
argument_list|,
name|preSetup
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// any required users and group should have been created by the pre-setup query.
comment|// check for invalid users now.
name|checkUserSettings
argument_list|()
expr_stmt|;
comment|// install
name|scanDirectory
argument_list|(
name|packageDir
argument_list|,
name|targetCollection
argument_list|)
expr_stmt|;
comment|// run the post-setup query if present
name|ElementImpl
name|postSetup
init|=
name|findElement
argument_list|(
name|repoXML
argument_list|,
name|POST_SETUP_ELEMENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|postSetup
operator|!=
literal|null
condition|)
name|runQuery
argument_list|(
name|packageDir
argument_list|,
name|postSetup
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|statusReport
argument_list|(
name|targetCollection
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
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
name|Sequence
name|statusReport
parameter_list|(
name|String
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
if|if
condition|(
name|target
operator|!=
literal|null
condition|)
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
argument_list|)
expr_stmt|;
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
specifier|private
name|void
name|checkUserSettings
parameter_list|()
throws|throws
name|XPathException
block|{
name|SecurityManager
name|secman
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|group
operator|!=
literal|null
operator|&&
operator|!
name|secman
operator|.
name|hasGroup
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|GroupAider
name|aider
init|=
operator|new
name|GroupAider
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|secman
operator|.
name|addGroup
argument_list|(
name|aider
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
operator|!
name|secman
operator|.
name|hasAccount
argument_list|(
name|user
argument_list|)
condition|)
block|{
name|UserAider
name|aider
init|=
operator|new
name|UserAider
argument_list|(
name|user
argument_list|)
decl_stmt|;
name|aider
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
name|aider
operator|.
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|secman
operator|.
name|addAccount
argument_list|(
name|aider
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to create user: "
operator|+
name|user
argument_list|,
name|e
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
literal|"Failed to create user: "
operator|+
name|user
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to create user: "
operator|+
name|user
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Sequence
name|runQuery
parameter_list|(
name|File
name|tempDir
parameter_list|,
name|String
name|fileName
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
name|File
name|xquery
init|=
operator|new
name|File
argument_list|(
name|tempDir
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|xquery
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"The XQuery resource specified in the<setup> element was not found"
argument_list|)
throw|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Calling XQuery "
operator|+
name|xquery
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|XQuery
name|xqs
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|XQueryContext
name|ctx
init|=
name|xqs
operator|.
name|newContext
argument_list|(
name|AccessContext
operator|.
name|REST
argument_list|)
decl_stmt|;
name|ctx
operator|.
name|declareVariable
argument_list|(
literal|"dir"
argument_list|,
name|tempDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|home
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|ctx
operator|.
name|declareVariable
argument_list|(
literal|"home"
argument_list|,
name|home
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|xqs
operator|.
name|compile
argument_list|(
name|ctx
argument_list|,
operator|new
name|FileSource
argument_list|(
name|xquery
argument_list|,
literal|"UTF-8"
argument_list|,
literal|false
argument_list|)
argument_list|)
decl_stmt|;
name|Sequence
name|setupResult
init|=
name|xqs
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|setupResult
return|;
block|}
comment|/** 	 * Scan a directory and import all files and sub directories into the target 	 * collection. 	 *  	 * @param directory 	 * @param target 	 * @param includeFiles 	 */
specifier|private
name|void
name|scanDirectory
parameter_list|(
name|File
name|directory
parameter_list|,
name|XmldbURI
name|target
parameter_list|)
block|{
name|TransactionManager
name|mgr
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|txn
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|target
argument_list|)
expr_stmt|;
name|setPermissions
argument_list|(
name|collection
operator|.
name|getPermissions
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|mgr
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// lock the collection while we store the files
comment|// TODO: could be released after each operation
name|collection
operator|.
name|getLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|storeFiles
argument_list|(
name|directory
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|collection
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
comment|// scan sub directories
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|scanDirectory
argument_list|(
name|file
argument_list|,
name|target
operator|.
name|append
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Import all files in the given directory into the target collection 	 *  	 * @param directory 	 * @param targetCollection 	 */
specifier|private
name|void
name|storeFiles
parameter_list|(
name|File
name|directory
parameter_list|,
name|Collection
name|targetCollection
parameter_list|)
block|{
name|File
index|[]
name|files
init|=
name|directory
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|MimeTable
name|mimeTab
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|TransactionManager
name|mgr
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|files
control|)
block|{
if|if
condition|(
literal|"repo.xml"
operator|.
name|equals
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
operator|||
literal|"expath-pkg.xml"
operator|.
name|equals
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
continue|continue;
if|if
condition|(
operator|!
name|file
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|MimeType
name|mime
init|=
name|mimeTab
operator|.
name|getContentTypeFor
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|XmldbURI
name|name
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|Txn
name|txn
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
name|InputSource
name|is
init|=
operator|new
name|InputSource
argument_list|(
name|file
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
decl_stmt|;
name|IndexInfo
name|info
init|=
name|targetCollection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|name
argument_list|,
name|is
argument_list|)
decl_stmt|;
name|info
operator|.
name|getDocument
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|mime
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Permission
name|permission
init|=
name|info
operator|.
name|getDocument
argument_list|()
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
name|setPermissions
argument_list|(
name|permission
argument_list|)
expr_stmt|;
name|targetCollection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|info
argument_list|,
name|is
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|long
name|size
init|=
name|file
operator|.
name|length
argument_list|()
decl_stmt|;
name|FileInputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|BinaryDocument
name|doc
init|=
name|targetCollection
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|name
argument_list|,
name|is
argument_list|,
name|mime
operator|.
name|getName
argument_list|()
argument_list|,
name|size
argument_list|)
decl_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|Permission
name|permission
init|=
name|doc
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
name|setPermissions
argument_list|(
name|permission
argument_list|)
expr_stmt|;
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|setMimeType
argument_list|(
name|mime
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|storeXMLResource
argument_list|(
name|txn
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|mgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|mgr
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|setPermissions
parameter_list|(
name|Permission
name|permission
parameter_list|)
block|{
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
name|permission
operator|.
name|setOwner
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
name|permission
operator|.
name|setGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
if|if
condition|(
name|perms
operator|>
operator|-
literal|1
condition|)
name|permission
operator|.
name|setPermissions
argument_list|(
name|perms
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ElementImpl
name|findElement
parameter_list|(
name|NodeImpl
name|root
parameter_list|,
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
name|InMemoryNodeSet
name|setupNodes
init|=
operator|new
name|InMemoryNodeSet
argument_list|()
decl_stmt|;
name|root
operator|.
name|selectDescendants
argument_list|(
literal|false
argument_list|,
operator|new
name|NameTest
argument_list|(
name|Type
operator|.
name|ELEMENT
argument_list|,
name|qname
argument_list|)
argument_list|,
name|setupNodes
argument_list|)
expr_stmt|;
if|if
condition|(
name|setupNodes
operator|.
name|getItemCount
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|ElementImpl
operator|)
name|setupNodes
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|// Unused
specifier|private
name|void
name|unpack
parameter_list|(
name|File
name|outputDir
parameter_list|,
name|InputStream
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|JarInputStream
name|jis
init|=
operator|new
name|JarInputStream
argument_list|(
name|istream
argument_list|)
decl_stmt|;
name|JarEntry
name|entry
decl_stmt|;
while|while
condition|(
operator|(
name|entry
operator|=
name|jis
operator|.
name|getNextJarEntry
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|File
name|targetFile
init|=
operator|new
name|File
argument_list|(
name|outputDir
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|targetFile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|targetFile
argument_list|)
decl_stmt|;
name|int
name|c
decl_stmt|;
name|byte
index|[]
name|b
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|jis
operator|.
name|read
argument_list|(
name|b
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
name|jis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// Unused
specifier|private
name|File
name|createTempDir
parameter_list|()
throws|throws
name|XPathException
block|{
name|File
name|sysTempDir
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
argument_list|)
decl_stmt|;
name|String
name|uuid
init|=
name|UUIDGenerator
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|File
name|tempDir
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|maxAttempts
init|=
literal|9
decl_stmt|;
name|int
name|attemptCount
init|=
literal|0
decl_stmt|;
do|do
block|{
name|attemptCount
operator|++
expr_stmt|;
if|if
condition|(
name|attemptCount
operator|>
name|maxAttempts
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Failed to create a unique temporary directory. Giving up."
argument_list|)
throw|;
block|}
name|String
name|dirName
init|=
name|UUIDGenerator
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|tempDir
operator|=
operator|new
name|File
argument_list|(
name|sysTempDir
argument_list|,
name|dirName
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|tempDir
operator|.
name|exists
argument_list|()
condition|)
do|;
return|return
name|tempDir
return|;
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
if|if
condition|(
operator|!
name|postOptimization
condition|)
block|{
name|user
operator|=
literal|null
expr_stmt|;
name|group
operator|=
literal|null
expr_stmt|;
name|perms
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

