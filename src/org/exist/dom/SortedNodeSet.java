begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Category
import|;
end_import

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
name|parser
operator|.
name|XPathLexer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|parser
operator|.
name|XPathParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|OrderedLinkedList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|PathExpr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|ValueSet
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_class
specifier|public
class|class
name|SortedNodeSet
extends|extends
name|NodeSet
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|SortedNodeSet
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|PathExpr
name|expr
decl_stmt|;
specifier|private
name|OrderedLinkedList
name|list
init|=
operator|new
name|OrderedLinkedList
argument_list|()
decl_stmt|;
specifier|private
name|DocumentSet
name|ndocs
decl_stmt|;
specifier|private
name|String
name|sortExpr
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|User
name|user
init|=
literal|null
decl_stmt|;
specifier|public
name|SortedNodeSet
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|User
name|user
parameter_list|,
name|String
name|sortExpr
parameter_list|)
block|{
name|this
operator|.
name|sortExpr
operator|=
name|sortExpr
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeSet
name|other
parameter_list|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|Item
name|item
decl_stmt|;
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|other
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|p
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|XPathLexer
name|lexer
init|=
operator|new
name|XPathLexer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|sortExpr
argument_list|)
argument_list|)
decl_stmt|;
name|XPathParser
name|parser
init|=
operator|new
name|XPathParser
argument_list|(
name|pool
argument_list|,
name|user
argument_list|,
name|lexer
argument_list|)
decl_stmt|;
name|expr
operator|=
operator|new
name|PathExpr
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|parser
operator|.
name|expr
argument_list|(
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|foundErrors
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
name|parser
operator|.
name|getErrorMsg
argument_list|()
argument_list|)
expr_stmt|;
name|ndocs
operator|=
name|expr
operator|.
name|preselect
argument_list|(
name|docs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|antlr
operator|.
name|RecognitionException
name|re
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|re
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|antlr
operator|.
name|TokenStreamException
name|tse
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|tse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|()
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|other
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|item
operator|=
operator|new
name|Item
argument_list|(
name|broker
argument_list|,
name|p
argument_list|,
name|expr
argument_list|,
name|ndocs
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"exception during sort"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"sort-expression found "
operator|+
name|list
operator|.
name|size
argument_list|()
operator|+
literal|" in "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"ms."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addAll
parameter_list|(
name|NodeList
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|NodeSet
operator|)
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented!"
argument_list|)
throw|;
name|addAll
argument_list|(
operator|(
name|NodeSet
operator|)
name|other
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
return|return
name|contains
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
name|p
operator|=
operator|(
operator|(
name|Item
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|proxy
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|compareTo
argument_list|(
name|proxy
argument_list|)
operator|==
literal|0
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
specifier|final
name|Item
name|item
init|=
operator|(
name|Item
operator|)
name|list
operator|.
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
return|return
name|item
operator|==
literal|null
condition|?
literal|null
else|:
name|item
operator|.
name|proxy
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|long
name|nodeId
parameter_list|)
block|{
name|NodeProxy
name|p
decl_stmt|;
name|NodeProxy
name|proxy
init|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
name|p
operator|=
operator|(
operator|(
name|Item
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|proxy
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|compareTo
argument_list|(
name|proxy
argument_list|)
operator|==
literal|0
condition|)
return|return
name|p
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|NodeProxy
name|get
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
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
name|p
operator|=
operator|(
operator|(
name|Item
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|proxy
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|compareTo
argument_list|(
name|proxy
argument_list|)
operator|==
literal|0
condition|)
return|return
name|p
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|Node
name|item
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|NodeProxy
name|p
init|=
operator|(
operator|(
name|Item
operator|)
name|list
operator|.
name|get
argument_list|(
name|pos
argument_list|)
operator|)
operator|.
name|proxy
decl_stmt|;
return|return
name|p
operator|==
literal|null
condition|?
literal|null
else|:
name|p
operator|.
name|doc
operator|.
name|getNode
argument_list|(
name|p
argument_list|)
return|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|SortedNodeSetIterator
argument_list|(
name|list
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|SortedNodeSetIterator
implements|implements
name|Iterator
block|{
name|Iterator
name|pi
decl_stmt|;
specifier|public
name|SortedNodeSetIterator
parameter_list|(
name|Iterator
name|i
parameter_list|)
block|{
name|pi
operator|=
name|i
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pi
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
operator|!
name|pi
operator|.
name|hasNext
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
operator|(
operator|(
name|Item
operator|)
name|pi
operator|.
name|next
argument_list|()
operator|)
operator|.
name|proxy
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|Item
implements|implements
name|Comparable
block|{
name|NodeProxy
name|proxy
decl_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
specifier|public
name|Item
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|,
name|PathExpr
name|expr
parameter_list|,
name|DocumentSet
name|ndocs
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
name|ArraySet
name|context
init|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|context
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
name|Value
name|v
init|=
name|expr
operator|.
name|eval
argument_list|(
name|ndocs
argument_list|,
name|context
argument_list|,
name|proxy
argument_list|)
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|OrderedLinkedList
name|strings
init|=
operator|new
name|OrderedLinkedList
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|v
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Value
operator|.
name|isNodeList
case|:
name|NodeSet
name|resultSet
init|=
operator|(
name|NodeSet
operator|)
name|v
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
if|if
condition|(
name|resultSet
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|resultSet
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
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|strings
operator|.
name|add
argument_list|(
name|broker
operator|.
name|getNodeValue
argument_list|(
name|p
argument_list|)
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|j
init|=
name|strings
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|buf
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|j
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
default|default :
name|ValueSet
name|valueSet
init|=
name|v
operator|.
name|getValueSet
argument_list|()
decl_stmt|;
if|if
condition|(
name|valueSet
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|valueSet
operator|.
name|getLength
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|v
operator|=
name|valueSet
operator|.
name|get
argument_list|(
name|k
argument_list|)
expr_stmt|;
name|strings
operator|.
name|add
argument_list|(
name|v
operator|.
name|getStringValue
argument_list|()
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|j
init|=
name|strings
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|buf
operator|.
name|append
argument_list|(
operator|(
name|String
operator|)
name|j
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|value
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
block|}
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
name|Item
name|o
init|=
operator|(
name|Item
operator|)
name|other
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
name|o
operator|.
name|value
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
return|;
if|if
condition|(
name|o
operator|.
name|value
operator|==
literal|null
condition|)
return|return
name|value
operator|==
literal|null
condition|?
literal|0
else|:
operator|-
literal|1
return|;
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|value
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

