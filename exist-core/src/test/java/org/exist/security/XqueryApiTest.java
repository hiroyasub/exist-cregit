begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
package|;
end_package

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
name|storage
operator|.
name|BrokerPool
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
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistEmbeddedServer
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
name|Base64Encoder
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
name|serializer
operator|.
name|XQuerySerializer
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
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
name|SAXException
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
name|StringWriter
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|XqueryApiTest
extends|extends
name|AbstractApiSecurityTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|server
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|createCol
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|Sequence
name|result
init|=
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"xmldb:create-collection('/db', '"
operator|+
name|collectionName
operator|+
literal|"')"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/db/"
operator|+
name|collectionName
argument_list|,
name|serialize
argument_list|(
name|result
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeCol
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"xmldb:remove('/db/"
operator|+
name|collectionName
operator|+
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chownCol
parameter_list|(
specifier|final
name|String
name|collectionUri
parameter_list|,
specifier|final
name|String
name|owner_uid
parameter_list|,
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"sm:chown(xs:anyURI('"
operator|+
name|collectionUri
operator|+
literal|"'), '"
operator|+
name|owner_uid
operator|+
literal|":"
operator|+
name|group_gid
operator|+
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chmodCol
parameter_list|(
specifier|final
name|String
name|collectionUri
parameter_list|,
specifier|final
name|String
name|mode
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"sm:chmod(xs:anyURI('"
operator|+
name|collectionUri
operator|+
literal|"'), '"
operator|+
name|mode
operator|+
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chmodRes
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|mode
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"sm:chmod(xs:anyURI('"
operator|+
name|resourceUri
operator|+
literal|"'), '"
operator|+
name|mode
operator|+
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|chownRes
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|owner_uid
parameter_list|,
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"sm:chown(xs:anyURI('"
operator|+
name|resourceUri
operator|+
literal|"'), '"
operator|+
name|owner_uid
operator|+
literal|":"
operator|+
name|group_gid
operator|+
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|addCollectionUserAce
parameter_list|(
specifier|final
name|String
name|collectionUri
parameter_list|,
specifier|final
name|String
name|user_uid
parameter_list|,
specifier|final
name|String
name|mode
parameter_list|,
specifier|final
name|boolean
name|allow
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"sm:add-user-ace(xs:anyURI('"
operator|+
name|collectionUri
operator|+
literal|"'), '"
operator|+
name|user_uid
operator|+
literal|"', "
operator|+
operator|(
name|allow
condition|?
literal|"true()"
else|:
literal|"false()"
operator|)
operator|+
literal|", '"
operator|+
name|mode
operator|+
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getXmlResourceContent
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|Sequence
name|result
init|=
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"fn:doc('"
operator|+
name|resourceUri
operator|+
literal|"')"
argument_list|)
decl_stmt|;
return|return
name|serialize
argument_list|(
name|result
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeAccount
parameter_list|(
specifier|final
name|String
name|account_uid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"if (sm:user-exists('"
operator|+
name|account_uid
operator|+
literal|"')) then sm:remove-account('"
operator|+
name|account_uid
operator|+
literal|"') else()"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|removeGroup
parameter_list|(
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"if (sm:group-exists('"
operator|+
name|group_gid
operator|+
literal|"')) then sm:remove-group('"
operator|+
name|group_gid
operator|+
literal|"') else ()"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createAccount
parameter_list|(
specifier|final
name|String
name|account_uid
parameter_list|,
specifier|final
name|String
name|account_pwd
parameter_list|,
specifier|final
name|String
name|group_uid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"sm:create-account('"
operator|+
name|account_uid
operator|+
literal|"', '"
operator|+
name|account_pwd
operator|+
literal|"', '"
operator|+
name|group_uid
operator|+
literal|"', ())"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createGroup
parameter_list|(
specifier|final
name|String
name|group_gid
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"sm:create-group('"
operator|+
name|group_gid
operator|+
literal|"')"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createXmlResource
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|String
name|content
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|int
name|resIdx
init|=
name|resourceUri
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|collectionUri
init|=
name|resourceUri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|resIdx
argument_list|)
decl_stmt|;
specifier|final
name|String
name|resourceName
init|=
name|resourceUri
operator|.
name|substring
argument_list|(
name|resIdx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"xmldb:store('"
operator|+
name|collectionUri
operator|+
literal|"', '"
operator|+
name|resourceName
operator|+
literal|"', fn:parse-xml('"
operator|+
name|content
operator|+
literal|"'))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|createBinResource
parameter_list|(
specifier|final
name|String
name|resourceUri
parameter_list|,
specifier|final
name|byte
index|[]
name|content
parameter_list|,
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|)
throws|throws
name|ApiException
block|{
specifier|final
name|int
name|resIdx
init|=
name|resourceUri
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
specifier|final
name|String
name|collectionUri
init|=
name|resourceUri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|resIdx
argument_list|)
decl_stmt|;
specifier|final
name|String
name|resourceName
init|=
name|resourceUri
operator|.
name|substring
argument_list|(
name|resIdx
operator|+
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Base64Encoder
name|base64Encoder
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|base64Encoder
operator|.
name|translate
argument_list|(
name|content
argument_list|)
expr_stmt|;
specifier|final
name|String
name|base64Content
init|=
operator|new
name|String
argument_list|(
name|base64Encoder
operator|.
name|getCharArray
argument_list|()
argument_list|)
decl_stmt|;
name|executeQuery
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|,
literal|"xmldb:store-as-binary('"
operator|+
name|collectionUri
operator|+
literal|"', '"
operator|+
name|resourceName
operator|+
literal|"', xs:base64Binary('"
operator|+
name|base64Content
operator|+
literal|"'))"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Sequence
name|executeQuery
parameter_list|(
specifier|final
name|String
name|uid
parameter_list|,
specifier|final
name|String
name|pwd
parameter_list|,
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|ApiException
block|{
try|try
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|server
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|uid
argument_list|,
name|pwd
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|)
block|{
return|return
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
decl||
name|EXistException
decl||
name|PermissionDeniedException
decl||
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
name|serialize
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|)
throws|throws
name|ApiException
block|{
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|server
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|;
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|XQuerySerializer
name|serializer
init|=
operator|new
name|XQuerySerializer
argument_list|(
name|broker
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
decl||
name|IOException
decl||
name|SAXException
decl||
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ApiException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

