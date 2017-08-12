begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
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
name|persistent
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
name|dom
operator|.
name|persistent
operator|.
name|NodeProxy
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
name|*
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|ValueSequenceTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|final
specifier|static
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|sortInDocumentOrder
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|AuthenticationException
block|{
specifier|final
name|ValueSequence
name|seq
init|=
operator|new
name|ValueSequence
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|seq
operator|.
name|keepUnOrdered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//in-memory doc
specifier|final
name|MemTreeBuilder
name|memtree
init|=
operator|new
name|MemTreeBuilder
argument_list|()
decl_stmt|;
name|memtree
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|memtree
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"m1"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|memtree
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"m2"
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|memtree
operator|.
name|characters
argument_list|(
literal|"test data"
argument_list|)
expr_stmt|;
name|memtree
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|memtree
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|memtree
operator|.
name|endDocument
argument_list|()
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|Subject
name|admin
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
literal|"admin"
argument_list|,
literal|""
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
name|admin
argument_list|)
argument_list|)
init|)
block|{
comment|//persistent doc
specifier|final
name|Collection
name|sysCollection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|SecurityManager
operator|.
name|SECURITY_COLLECTION_URI
argument_list|)
decl_stmt|;
specifier|final
name|DocumentImpl
name|doc
init|=
name|sysCollection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"config.xml"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|NodeProxy
name|docProxy
init|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|)
decl_stmt|;
specifier|final
name|NodeProxy
name|nodeProxy
init|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
operator|(
operator|(
name|NodeImpl
operator|)
name|doc
operator|.
name|getFirstChild
argument_list|()
operator|)
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|memtree
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|docProxy
argument_list|)
expr_stmt|;
name|seq
operator|.
name|add
argument_list|(
operator|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
operator|)
name|memtree
operator|.
name|getDocument
argument_list|()
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
name|seq
operator|.
name|add
argument_list|(
name|nodeProxy
argument_list|)
expr_stmt|;
comment|//call sort
name|seq
operator|.
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

