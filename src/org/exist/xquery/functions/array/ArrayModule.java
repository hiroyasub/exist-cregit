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
name|array
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
comment|/**  * Module implementing functions that operate on arrays.  */
end_comment

begin_class
specifier|public
class|class
name|ArrayModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://www.w3.org/2005/xpath-functions/array"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"array"
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
name|ArrayFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|5
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|6
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|7
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|8
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|9
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|10
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|11
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|12
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|13
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|14
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|15
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|ArrayFunction
operator|.
name|signatures
index|[
literal|16
index|]
argument_list|,
name|ArrayFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|ArrayModule
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
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"Functions that operate on arrays"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
literal|"2.2.1"
return|;
block|}
block|}
end_class

end_unit
