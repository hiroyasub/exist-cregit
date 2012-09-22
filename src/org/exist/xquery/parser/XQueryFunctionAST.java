begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|antlr
operator|.
name|collections
operator|.
name|AST
import|;
end_import

begin_comment
comment|/**  * AST for XQuery function declarations. Preserves XQDoc comments.  */
end_comment

begin_class
specifier|public
class|class
name|XQueryFunctionAST
extends|extends
name|XQueryAST
block|{
specifier|private
name|String
name|doc
init|=
literal|null
decl_stmt|;
specifier|public
name|XQueryFunctionAST
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|XQueryFunctionAST
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|text
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|text
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XQueryFunctionAST
parameter_list|(
name|AST
name|ast
parameter_list|)
block|{
name|super
argument_list|(
name|ast
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setDoc
parameter_list|(
name|String
name|xqdoc
parameter_list|)
block|{
name|this
operator|.
name|doc
operator|=
name|xqdoc
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDoc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
block|}
end_class

end_unit

