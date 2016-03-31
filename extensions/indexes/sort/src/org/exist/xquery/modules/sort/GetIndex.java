begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|sort
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

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
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|sort
operator|.
name|SortIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|sort
operator|.
name|SortIndexWorker
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
name|LockException
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
name|value
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|GetIndex
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index"
argument_list|,
name|SortModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|SortModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Look up a node in the sort index and return a number (&gt; 0) corresponding to the "
operator|+
literal|"position of that node in the ordered set which was created by a previous call to "
operator|+
literal|"the sort:create-index function. The function returns the empty sequence if the node "
operator|+
literal|"cannot be found in the index."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"id"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the index."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The node to look up."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"A number&gt; 0 or the empty "
operator|+
literal|"sequence if the $node argument was empty or the node could not be found in the index."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetIndex
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
specifier|final
name|String
name|id
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|NodeProxy
name|node
init|=
operator|(
name|NodeProxy
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SortIndexWorker
name|index
init|=
operator|(
name|SortIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|SortIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
name|long
name|pos
init|=
literal|0
decl_stmt|;
try|try
block|{
name|pos
operator|=
name|index
operator|.
name|getIndex
argument_list|(
name|id
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|LockException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Caught lock error while searching index. Giving up."
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|pos
operator|<
literal|0
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
operator|new
name|IntegerValue
argument_list|(
name|pos
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
return|;
block|}
block|}
end_class

end_unit

