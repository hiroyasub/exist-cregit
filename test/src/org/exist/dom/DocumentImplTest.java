begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|BaseMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
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
name|PermissionFactory
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
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|expect
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|easymock
operator|.
name|classextension
operator|.
name|EasyMock
operator|.
name|verify
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|DocumentImplTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|copyOf_calls_getMetadata
parameter_list|()
block|{
name|SecurityManager
name|mockSecurityManager
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|PermissionFactory
operator|.
name|sm
operator|=
name|mockSecurityManager
expr_stmt|;
comment|//constructor logic expectations
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getDBAGroup
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getDBAGroup
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//test values
specifier|final
name|DocumentMetadata
name|otherMetadata
init|=
operator|new
name|DocumentMetadata
argument_list|()
decl_stmt|;
name|replay
argument_list|(
name|mockSecurityManager
argument_list|)
expr_stmt|;
comment|//test setup
name|TestableDocumentImpl
name|doc
init|=
operator|new
name|TestableDocumentImpl
argument_list|()
decl_stmt|;
name|DocumentImpl
name|other
init|=
operator|new
name|DocumentImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|other
operator|.
name|setMetadata
argument_list|(
name|otherMetadata
argument_list|)
expr_stmt|;
comment|//actions
name|doc
operator|.
name|copyOf
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockSecurityManager
argument_list|)
expr_stmt|;
comment|//assertions
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|doc
operator|.
name|getMetadata_invCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyOf_calls_metadata_copyOf
parameter_list|()
block|{
name|SecurityManager
name|mockSecurityManager
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|PermissionFactory
operator|.
name|sm
operator|=
name|mockSecurityManager
expr_stmt|;
comment|//constructor logic expectations
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getDBAGroup
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getDBAGroup
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//test values
specifier|final
name|TestableDocumentMetadata
name|docMetadata
init|=
operator|new
name|TestableDocumentMetadata
argument_list|()
decl_stmt|;
specifier|final
name|DocumentMetadata
name|otherMetadata
init|=
operator|new
name|DocumentMetadata
argument_list|()
decl_stmt|;
name|replay
argument_list|(
name|mockSecurityManager
argument_list|)
expr_stmt|;
comment|//test setup
name|DocumentImpl
name|doc
init|=
operator|new
name|DocumentImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setMetadata
argument_list|(
name|docMetadata
argument_list|)
expr_stmt|;
name|DocumentImpl
name|other
init|=
operator|new
name|DocumentImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|other
operator|.
name|setMetadata
argument_list|(
name|otherMetadata
argument_list|)
expr_stmt|;
comment|//actions
name|doc
operator|.
name|copyOf
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockSecurityManager
argument_list|)
expr_stmt|;
comment|//assertions
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|docMetadata
operator|.
name|getCopyOf_invCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|copyOf_updates_metadata_created_and_lastModified
parameter_list|()
block|{
name|SecurityManager
name|mockSecurityManager
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|PermissionFactory
operator|.
name|sm
operator|=
name|mockSecurityManager
expr_stmt|;
comment|//constructor logic expectations
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getDBAGroup
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockSecurityManager
operator|.
name|getDBAGroup
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|//test values
specifier|final
name|DocumentMetadata
name|docMetadata
init|=
operator|new
name|TestableDocumentMetadata
argument_list|()
decl_stmt|;
specifier|final
name|DocumentMetadata
name|otherMetadata
init|=
operator|new
name|DocumentMetadata
argument_list|()
decl_stmt|;
specifier|final
name|long
name|otherCreated
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|2000
decl_stmt|;
specifier|final
name|long
name|otherLastModified
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
literal|1000
decl_stmt|;
name|replay
argument_list|(
name|mockSecurityManager
argument_list|)
expr_stmt|;
comment|//test setup
name|DocumentImpl
name|doc
init|=
operator|new
name|DocumentImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setMetadata
argument_list|(
name|docMetadata
argument_list|)
expr_stmt|;
name|DocumentImpl
name|other
init|=
operator|new
name|DocumentImpl
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|other
operator|.
name|setMetadata
argument_list|(
name|otherMetadata
argument_list|)
expr_stmt|;
comment|//actions
name|doc
operator|.
name|copyOf
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockSecurityManager
argument_list|)
expr_stmt|;
comment|//assertions
name|assertThat
argument_list|(
name|otherCreated
argument_list|,
operator|new
name|LessThan
argument_list|(
name|docMetadata
operator|.
name|getCreated
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|otherLastModified
argument_list|,
operator|new
name|LessThan
argument_list|(
name|docMetadata
operator|.
name|getLastModified
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
class|class
name|TestableDocumentImpl
extends|extends
name|DocumentImpl
block|{
specifier|private
name|int
name|getMetadata_invCount
init|=
literal|0
decl_stmt|;
specifier|private
name|DocumentMetadata
name|meta
init|=
operator|new
name|DocumentMetadata
argument_list|()
decl_stmt|;
specifier|public
name|TestableDocumentImpl
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getMetadata_invCount
parameter_list|()
block|{
return|return
name|getMetadata_invCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentMetadata
name|getMetadata
parameter_list|()
block|{
name|getMetadata_invCount
operator|++
expr_stmt|;
return|return
name|meta
return|;
block|}
block|}
specifier|public
class|class
name|TestableDocumentMetadata
extends|extends
name|DocumentMetadata
block|{
specifier|private
name|int
name|copyOf_invCount
init|=
literal|0
decl_stmt|;
specifier|public
name|int
name|getCopyOf_invCount
parameter_list|()
block|{
return|return
name|copyOf_invCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copyOf
parameter_list|(
name|DocumentMetadata
name|other
parameter_list|)
block|{
name|copyOf_invCount
operator|++
expr_stmt|;
block|}
block|}
specifier|public
class|class
name|LessThan
extends|extends
name|BaseMatcher
argument_list|<
name|Long
argument_list|>
block|{
specifier|private
specifier|final
name|Long
name|actual
decl_stmt|;
specifier|public
name|LessThan
parameter_list|(
name|Long
name|actual
parameter_list|)
block|{
name|this
operator|.
name|actual
operator|=
name|actual
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|expected
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|expected
operator|instanceof
name|Long
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
operator|(
operator|(
name|Long
operator|)
name|expected
operator|)
operator|.
name|compareTo
argument_list|(
name|actual
argument_list|)
operator|<
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"Less than"
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
block|}
end_class

end_unit
