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
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|NodeSet
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
name|AbstractSequence
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
name|IntegerValue
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
name|MemoryNodeSet
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
name|SequenceIterator
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

begin_class
specifier|public
class|class
name|RangeSequence
extends|extends
name|AbstractSequence
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|AbstractSequence
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|IntegerValue
name|start
decl_stmt|;
specifier|private
name|IntegerValue
name|end
decl_stmt|;
specifier|public
name|RangeSequence
parameter_list|(
name|IntegerValue
name|start
parameter_list|,
name|IntegerValue
name|end
parameter_list|)
block|{
name|this
operator|.
name|start
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|end
operator|=
name|end
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Internal error: adding to an immutable sequence"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Internal error: adding to an immutable sequence"
argument_list|)
throw|;
block|}
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|INTEGER
return|;
block|}
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|RangeSequenceIterator
argument_list|(
name|start
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|RangeSequenceIterator
argument_list|(
name|start
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
block|}
specifier|private
class|class
name|RangeSequenceIterator
implements|implements
name|SequenceIterator
block|{
name|long
name|current
decl_stmt|;
specifier|public
name|RangeSequenceIterator
parameter_list|(
name|long
name|start
parameter_list|)
block|{
name|this
operator|.
name|current
operator|=
name|start
expr_stmt|;
block|}
specifier|public
name|Item
name|nextItem
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|current
operator|<=
name|end
operator|.
name|getLong
argument_list|()
condition|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|current
operator|++
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected exception when processing result of range expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
try|try
block|{
return|return
name|current
operator|<=
name|end
operator|.
name|getLong
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected exception when processing result of range expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
specifier|public
name|int
name|getItemCount
parameter_list|()
block|{
if|if
condition|(
name|start
operator|.
name|compareTo
argument_list|(
name|end
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|0
return|;
block|}
try|try
block|{
return|return
operator|(
operator|(
name|IntegerValue
operator|)
name|end
operator|.
name|minus
argument_list|(
name|start
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
operator|+
literal|1
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected exception when processing result of range expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|getItemCount
argument_list|()
operator|==
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasOne
parameter_list|()
block|{
return|return
name|getItemCount
argument_list|()
operator|==
literal|1
return|;
block|}
specifier|public
name|boolean
name|hasMany
parameter_list|()
block|{
return|return
name|getItemCount
argument_list|()
operator|>
literal|1
return|;
block|}
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<=
name|getItemCount
argument_list|()
condition|)
try|try
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|start
operator|.
name|getLong
argument_list|()
operator|+
name|pos
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unexpected exception when processing result of range expression: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|NodeSet
name|toNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sequence cannot be converted into"
operator|+
literal|" a node set. Item type is xs:integer"
argument_list|)
throw|;
block|}
specifier|public
name|MemoryNodeSet
name|toMemNodeSet
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the sequence cannot be converted into"
operator|+
literal|" a node set. Item type is xs:integer"
argument_list|)
throw|;
block|}
specifier|public
name|void
name|removeDuplicates
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

