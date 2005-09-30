begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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
name|Iterator
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|parser
operator|.
name|XQueryAST
import|;
end_import

begin_class
specifier|public
class|class
name|XPathException
extends|extends
name|Exception
block|{
specifier|private
name|int
name|line
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|column
init|=
literal|0
decl_stmt|;
specifier|private
name|String
name|message
init|=
literal|null
decl_stmt|;
specifier|private
name|List
name|callStack
init|=
literal|null
decl_stmt|;
comment|/** 	 * @param message 	 */
specifier|public
name|XPathException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|XPathException
parameter_list|(
name|XQueryAST
name|ast
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XPathException
parameter_list|(
name|String
name|message
parameter_list|,
name|int
name|line
parameter_list|,
name|int
name|column
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|line
operator|=
name|line
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|column
expr_stmt|;
block|}
comment|/** 	 * @param cause 	 */
specifier|public
name|XPathException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param message 	 * @param cause 	 */
specifier|public
name|XPathException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
block|}
specifier|public
name|XPathException
parameter_list|(
name|XQueryAST
name|ast
parameter_list|,
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getLine
parameter_list|()
block|{
return|return
name|line
return|;
block|}
specifier|public
name|int
name|getColumn
parameter_list|()
block|{
return|return
name|column
return|;
block|}
specifier|public
name|void
name|setASTNode
parameter_list|(
name|XQueryAST
name|ast
parameter_list|)
block|{
if|if
condition|(
name|ast
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|line
operator|=
name|ast
operator|.
name|getLine
argument_list|()
expr_stmt|;
name|this
operator|.
name|column
operator|=
name|ast
operator|.
name|getColumn
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addFunctionCall
parameter_list|(
name|UserDefinedFunction
name|def
parameter_list|,
name|XQueryAST
name|ast
parameter_list|)
block|{
if|if
condition|(
name|callStack
operator|==
literal|null
condition|)
name|callStack
operator|=
operator|new
name|ArrayList
argument_list|()
expr_stmt|;
name|callStack
operator|.
name|add
argument_list|(
operator|new
name|FunctionStackElement
argument_list|(
name|def
argument_list|,
name|ast
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|prependMessage
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|message
operator|=
name|msg
operator|+
name|message
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Throwable#getMessage() 	 */
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
name|message
operator|=
literal|""
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|getLine
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" [at line "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|", column "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|callStack
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\nIn call to function:\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|callStack
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getMessageAsHTML
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
name|message
operator|=
literal|""
expr_stmt|;
name|message
operator|=
name|message
operator|.
name|replaceAll
argument_list|(
literal|"\r?\n"
argument_list|,
literal|"<br/>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<h2>"
argument_list|)
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|getLine
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" [at line "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|", column "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"</h2>"
argument_list|)
expr_stmt|;
if|if
condition|(
name|callStack
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"<table id=\"xquerytrace\">"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<caption>XQuery Stack Trace</caption>"
argument_list|)
expr_stmt|;
name|FunctionStackElement
name|e
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|callStack
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|e
operator|=
operator|(
name|FunctionStackElement
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<tr><td class=\"func\">"
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|function
argument_list|)
operator|.
name|append
argument_list|(
literal|"</td>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<td class=\"lineinfo\">"
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|ast
operator|.
name|getLine
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"</td>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</tr>"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"</table>"
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
class|class
name|FunctionStackElement
block|{
name|String
name|function
decl_stmt|;
name|XQueryAST
name|ast
decl_stmt|;
name|FunctionStackElement
parameter_list|(
name|UserDefinedFunction
name|func
parameter_list|,
name|XQueryAST
name|ast
parameter_list|)
block|{
name|this
operator|.
name|function
operator|=
name|func
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|ast
operator|=
name|ast
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|function
argument_list|)
operator|.
name|append
argument_list|(
literal|" ["
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|ast
operator|.
name|getLine
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

