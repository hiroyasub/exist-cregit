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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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

begin_comment
comment|/**  * Created by wolf on 15/08/15.  */
end_comment

begin_class
specifier|public
class|class
name|WhereClause
extends|extends
name|AbstractFLWORClause
block|{
specifier|protected
name|Expression
name|whereExpr
decl_stmt|;
specifier|protected
name|boolean
name|fastTrack
init|=
literal|false
decl_stmt|;
specifier|public
name|WhereClause
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|whereExpr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|whereExpr
operator|=
name|whereExpr
expr_stmt|;
block|}
annotation|@
name|Override
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
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|AnalyzeContextInfo
name|newContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContextInfo
operator|.
name|setFlags
argument_list|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator||
name|IN_PREDICATE
operator||
name|IN_WHERE_CLAUSE
argument_list|)
expr_stmt|;
name|newContextInfo
operator|.
name|setContextId
argument_list|(
name|getExpressionId
argument_list|()
argument_list|)
expr_stmt|;
name|whereExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
name|newContextInfo
operator|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|newContextInfo
operator|.
name|addFlag
argument_list|(
name|SINGLE_STEP_EXECUTION
argument_list|)
expr_stmt|;
name|returnExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|preEval
parameter_list|(
name|Sequence
name|in
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|in
operator|!=
literal|null
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|in
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
name|in
operator|.
name|isPersistentSet
argument_list|()
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|whereExpr
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
operator|&&
comment|//We might not be sure of the return type at this level
name|Type
operator|.
name|subTypeOf
argument_list|(
name|whereExpr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|isCached
argument_list|()
condition|)
block|{
name|BindingExpression
operator|.
name|setContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|Sequence
name|seq
init|=
name|whereExpr
operator|.
name|eval
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|//But *now*, we are ;-)
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|whereExpr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
specifier|final
name|NodeSet
name|nodes
init|=
name|seq
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
comment|// if the where expression returns a node set, check the context
comment|// node of each node in the set
specifier|final
name|NodeSet
name|contextSet
init|=
name|in
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|contextIsVirtual
init|=
name|contextSet
operator|instanceof
name|VirtualNodeSet
decl_stmt|;
specifier|final
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|lastDoc
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|NodeProxy
name|current
range|:
name|nodes
control|)
block|{
name|int
name|sizeHint
init|=
name|Constants
operator|.
name|NO_SIZE_HINT
decl_stmt|;
if|if
condition|(
name|lastDoc
operator|==
literal|null
operator|||
name|current
operator|.
name|getOwnerDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|current
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|sizeHint
operator|=
name|nodes
operator|.
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
name|ContextItem
name|context
init|=
name|current
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Internal evaluation error: context node is missing for node "
operator|+
name|current
operator|.
name|getNodeId
argument_list|()
operator|+
literal|"!"
argument_list|)
throw|;
block|}
comment|//				LOG.debug(current.debugContext());
while|while
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
comment|//TODO : Is this the context we want ? Not sure... would have prefered the LetExpr.
if|if
condition|(
name|context
operator|.
name|getContextId
argument_list|()
operator|==
name|whereExpr
operator|.
name|getContextId
argument_list|()
condition|)
block|{
specifier|final
name|NodeProxy
name|contextNode
init|=
name|context
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextIsVirtual
operator|||
name|contextSet
operator|.
name|contains
argument_list|(
name|contextNode
argument_list|)
condition|)
block|{
name|contextNode
operator|.
name|addMatches
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|contextNode
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|=
name|context
operator|.
name|getNextDirect
argument_list|()
expr_stmt|;
block|}
block|}
name|fastTrack
operator|=
literal|true
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|isCached
argument_list|()
condition|)
block|{
name|BindingExpression
operator|.
name|clearContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|preEval
argument_list|(
name|in
argument_list|)
return|;
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
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|applyWhereExpression
argument_list|()
condition|)
block|{
return|return
name|returnExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
return|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|postEval
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
name|fastTrack
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|returnExpr
operator|instanceof
name|FLWORClause
condition|)
block|{
name|seq
operator|=
operator|(
operator|(
name|FLWORClause
operator|)
name|returnExpr
operator|)
operator|.
name|postEval
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|postEval
argument_list|(
name|seq
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|applyWhereExpression
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|fastTrack
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|Sequence
name|innerSeq
init|=
name|whereExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
decl_stmt|;
return|return
name|innerSeq
operator|.
name|effectiveBooleanValue
argument_list|()
return|;
block|}
annotation|@
name|Override
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
literal|"where"
argument_list|,
name|whereExpr
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|whereExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
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
name|whereExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|returnExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

