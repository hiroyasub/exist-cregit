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
name|*
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
name|util
operator|.
name|ExpressionDumper
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
name|AtomicValue
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
name|Item
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
name|Type
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
name|List
import|;
end_import

begin_comment
comment|/**  * Implements the literal syntax for creating maps.  */
end_comment

begin_class
specifier|public
class|class
name|MapExpr
extends|extends
name|AbstractExpression
block|{
specifier|protected
name|List
argument_list|<
name|Mapping
argument_list|>
name|mappings
init|=
operator|new
name|ArrayList
argument_list|(
literal|13
argument_list|)
decl_stmt|;
specifier|public
name|MapExpr
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|map
parameter_list|(
name|PathExpr
name|key
parameter_list|,
name|PathExpr
name|value
parameter_list|)
block|{
specifier|final
name|Mapping
name|mapping
init|=
operator|new
name|Mapping
argument_list|(
name|key
operator|.
name|simplify
argument_list|()
argument_list|,
name|value
operator|.
name|simplify
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|mappings
operator|.
name|add
argument_list|(
name|mapping
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|Mapping
name|mapping
range|:
name|this
operator|.
name|mappings
control|)
block|{
name|mapping
operator|.
name|key
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|value
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
specifier|final
name|MapType
name|map
init|=
operator|new
name|MapType
argument_list|(
name|this
operator|.
name|context
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Mapping
name|mapping
range|:
name|this
operator|.
name|mappings
control|)
block|{
specifier|final
name|Sequence
name|key
init|=
name|mapping
operator|.
name|key
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|getItemCount
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|MapErrorCode
operator|.
name|EXMPDY001
argument_list|,
literal|"Expected single value for key, got "
operator|+
name|key
operator|.
name|getItemCount
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|AtomicValue
name|atomic
init|=
name|key
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|value
init|=
name|mapping
operator|.
name|value
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|atomic
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|MAP
return|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"map {"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Mapping
name|mapping
range|:
name|this
operator|.
name|mappings
control|)
block|{
name|mapping
operator|.
name|key
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" := "
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|value
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|Mapping
block|{
name|Expression
name|key
decl_stmt|;
name|Expression
name|value
decl_stmt|;
specifier|public
name|Mapping
parameter_list|(
name|Expression
name|key
parameter_list|,
name|Expression
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

