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
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|IMap
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

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|map
operator|.
name|MapType
operator|.
name|newLinearMap
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
specifier|private
specifier|final
name|List
argument_list|<
name|Mapping
argument_list|>
name|mappings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|13
argument_list|)
decl_stmt|;
specifier|public
name|MapExpr
parameter_list|(
specifier|final
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
specifier|final
name|PathExpr
name|key
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
specifier|final
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|getContext
argument_list|()
operator|.
name|getXQueryVersion
argument_list|()
operator|<
literal|30
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0003
argument_list|,
literal|"Map is not available before XQuery 3.0"
argument_list|)
throw|;
block|}
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
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
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
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
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|map
init|=
name|newLinearMap
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|boolean
name|firstType
init|=
literal|true
decl_stmt|;
name|int
name|prevType
init|=
name|AbstractMapType
operator|.
name|UNKNOWN_KEY_TYPE
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
name|this
argument_list|,
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
if|if
condition|(
name|map
operator|.
name|contains
argument_list|(
name|atomic
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQDY0137
argument_list|,
literal|"Key \""
operator|+
name|atomic
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"\" already exists in map."
argument_list|)
throw|;
block|}
name|map
operator|.
name|put
argument_list|(
name|atomic
argument_list|,
name|value
argument_list|)
expr_stmt|;
specifier|final
name|int
name|thisType
init|=
name|atomic
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|firstType
condition|)
block|{
name|prevType
operator|=
name|thisType
expr_stmt|;
name|firstType
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|thisType
operator|!=
name|prevType
condition|)
block|{
name|prevType
operator|=
name|AbstractMapType
operator|.
name|MIXED_KEY_TYPES
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
name|map
operator|.
name|forked
argument_list|()
argument_list|,
name|prevType
argument_list|)
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|super
operator|.
name|accept
argument_list|(
name|visitor
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
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|value
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
specifier|final
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|mappings
operator|.
name|forEach
argument_list|(
name|m
lambda|->
name|m
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|Mapping
block|{
specifier|final
name|Expression
name|key
decl_stmt|;
specifier|final
name|Expression
name|value
decl_stmt|;
specifier|public
name|Mapping
parameter_list|(
specifier|final
name|Expression
name|key
parameter_list|,
specifier|final
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
specifier|private
name|void
name|resetState
parameter_list|(
specifier|final
name|boolean
name|postOptimization
parameter_list|)
block|{
name|key
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|value
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

