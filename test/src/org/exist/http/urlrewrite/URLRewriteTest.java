begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|urlrewrite
package|;
end_package

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
name|servlet
operator|.
name|FilterChain
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|easymock
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
name|EasyMock
operator|.
name|replay
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|URLRewriteTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|constructorAddsMultipleParameterValuesForSameParameterName
parameter_list|()
block|{
specifier|final
name|String
name|ELEMENT_ADD_PARAMETER
init|=
literal|"add-parameter"
decl_stmt|;
specifier|final
name|String
name|PARAM_NAME
init|=
literal|"param1"
decl_stmt|;
specifier|final
name|String
name|PARAM_VALUE_1
init|=
literal|"value1.1"
decl_stmt|;
specifier|final
name|String
name|PARAM_VALUE_2
init|=
literal|"value1.2"
decl_stmt|;
name|Element
name|mockConfig
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Element
operator|.
name|class
argument_list|)
decl_stmt|;
name|Element
name|mockParameter1
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Element
operator|.
name|class
argument_list|)
decl_stmt|;
name|Element
name|mockParameter2
init|=
name|EasyMock
operator|.
name|createMock
argument_list|(
name|Element
operator|.
name|class
argument_list|)
decl_stmt|;
name|expect
argument_list|(
name|mockConfig
operator|.
name|hasAttribute
argument_list|(
literal|"absolute"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockConfig
operator|.
name|hasAttribute
argument_list|(
literal|"method"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockConfig
operator|.
name|hasChildNodes
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockConfig
operator|.
name|getFirstChild
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockParameter1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter1
operator|.
name|getNodeType
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter1
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter1
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ELEMENT_ADD_PARAMETER
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter1
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|PARAM_NAME
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter1
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|PARAM_VALUE_1
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter1
operator|.
name|getNextSibling
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|mockParameter2
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter2
operator|.
name|getNodeType
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter2
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|Namespaces
operator|.
name|EXIST_NS
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter2
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
name|ELEMENT_ADD_PARAMETER
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter2
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|PARAM_NAME
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter2
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
argument_list|)
operator|.
name|andReturn
argument_list|(
name|PARAM_VALUE_2
argument_list|)
expr_stmt|;
name|expect
argument_list|(
name|mockParameter2
operator|.
name|getNextSibling
argument_list|()
argument_list|)
operator|.
name|andReturn
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|replay
argument_list|(
name|mockConfig
argument_list|,
name|mockParameter1
argument_list|,
name|mockParameter2
argument_list|)
expr_stmt|;
name|TestableURLRewrite
name|urlRewrite
init|=
operator|new
name|TestableURLRewrite
argument_list|(
name|mockConfig
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|verify
argument_list|(
name|mockConfig
argument_list|,
name|mockParameter1
argument_list|,
name|mockParameter2
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|testParameters
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|PARAM_VALUE_1
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|PARAM_VALUE_2
argument_list|)
expr_stmt|;
name|testParameters
operator|.
name|put
argument_list|(
name|PARAM_NAME
argument_list|,
name|values
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testParameters
operator|.
name|size
argument_list|()
argument_list|,
name|urlRewrite
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|paramName
range|:
name|testParameters
operator|.
name|keySet
argument_list|()
control|)
block|{
name|assertEquals
argument_list|(
name|testParameters
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
argument_list|,
name|urlRewrite
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
name|paramName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|TestableURLRewrite
extends|extends
name|URLRewrite
block|{
specifier|public
name|TestableURLRewrite
parameter_list|(
name|Element
name|config
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doRewrite
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|FilterChain
name|chain
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

