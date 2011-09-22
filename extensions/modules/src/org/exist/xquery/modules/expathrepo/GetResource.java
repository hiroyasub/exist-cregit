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
name|IOException
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
name|ExistRepository
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
name|Base64BinaryDocument
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
name|BinaryValueFromFile
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
name|BinaryValueManager
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
name|Packages
import|;
end_import

begin_class
specifier|public
class|class
name|GetResource
extends|extends
name|BasicFunction
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
literal|"get-resource"
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
literal|"Retrieves the specified resource from an installed expath application package."
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
literal|"resource"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"resource path"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"<status result=\"ok\"/> if deployment was ok. Throws an error otherwise."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetResource
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
name|String
name|path
init|=
name|args
index|[
literal|1
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
name|Package
name|pkg
init|=
literal|null
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
name|pkg
operator|=
name|pp
operator|.
name|latest
argument_list|()
expr_stmt|;
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
name|StreamSource
name|source
init|=
name|pkg
operator|.
name|getResolver
argument_list|()
operator|.
name|resolveResource
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
return|return
name|Base64BinaryDocument
operator|.
name|getInstance
argument_list|(
name|context
argument_list|,
name|source
operator|.
name|getInputStream
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
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
name|ErrorCodes
operator|.
name|FOER0000
argument_list|,
literal|"Caught package error while reading expath package"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit
