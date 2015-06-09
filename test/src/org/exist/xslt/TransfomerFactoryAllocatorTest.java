begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Configuration
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
name|sax
operator|.
name|SAXTransformerFactory
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
name|junit
operator|.
name|Test
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
name|verify
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
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameter
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|value
operator|=
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|TransfomerFactoryAllocatorTest
block|{
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
index|[]
block|{
block|{
literal|"net.sf.saxon.TransformerFactoryImpl"
block|}
block|,
block|{
literal|"org.apache.xalan.processor.TransformerFactoryImpl"
block|}
block|,
block|{
literal|"org.exist.xslt.TransformerFactoryImpl"
block|}
block|}
argument_list|)
return|;
block|}
annotation|@
name|Parameter
specifier|public
name|String
name|transformerFactoryClass
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|getTransformerFactory
parameter_list|()
block|{
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|testAttributes
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
name|BrokerPool
name|mockBrokerPool
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|BrokerPool
operator|.
name|class
argument_list|)
decl_stmt|;
name|Configuration
name|mockConfiguration
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|mockBrokerPool
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockConfiguration
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockConfiguration
operator|.
name|getProperty
argument_list|(
name|TransformerFactoryAllocator
operator|.
name|PROPERTY_TRANSFORMER_CLASS
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|transformerFactoryClass
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockBrokerPool
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockConfiguration
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockConfiguration
operator|.
name|getProperty
argument_list|(
name|TransformerFactoryAllocator
operator|.
name|PROPERTY_TRANSFORMER_ATTRIBUTES
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|testAttributes
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockBrokerPool
argument_list|,
name|mockConfiguration
argument_list|)
expr_stmt|;
name|SAXTransformerFactory
name|transformerFactory
init|=
name|TransformerFactoryAllocator
operator|.
name|getTransformerFactory
argument_list|(
name|mockBrokerPool
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|transformerFactoryClass
argument_list|,
name|transformerFactory
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|mockBrokerPool
argument_list|,
name|mockConfiguration
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|resetTransformerFactoryAllocatorSingleton
parameter_list|()
throws|throws
name|NoSuchFieldException
throws|,
name|IllegalArgumentException
throws|,
name|IllegalAccessException
block|{
name|Field
name|field
init|=
name|TransformerFactoryAllocator
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
literal|"saxTransformerFactory"
argument_list|)
decl_stmt|;
name|field
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|field
operator|.
name|set
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

