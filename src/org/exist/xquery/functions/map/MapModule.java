begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|map
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
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
name|FunctionDef
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

begin_comment
comment|/**  * Implements the XQuery extension for maps as proposed by Michael Kay:  *  * http://dev.saxonica.com/blog/mike/2012/01/#000188  */
end_comment

begin_class
specifier|public
class|class
name|MapModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://www.w3.org/2005/xpath-functions/map"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"map"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|5
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|6
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|7
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|8
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|,
comment|/* Deprecated below */
operator|new
name|FunctionDef
argument_list|(
name|MapFunction
operator|.
name|signatures
index|[
literal|9
index|]
argument_list|,
name|MapFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|MapModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
literal|"http://www.w3.org/2005/xpath-functions/map"
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
literal|"map"
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Functions that operate on maps"
return|;
block|}
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
literal|"eXist-2.0.x"
return|;
block|}
block|}
end_class

end_unit

