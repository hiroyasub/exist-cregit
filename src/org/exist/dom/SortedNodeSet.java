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
name|numbering
operator|.
name|NodeId
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
name|security
operator|.
name|xacml
operator|.
name|AccessContext
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
name|xacml
operator|.
name|NullAccessContextException
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
name|xquery
operator|.
name|AnalyzeContextInfo
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
name|Constants
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
name|PathExpr
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
name|XPathException
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
name|XQueryContext
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
name|XQueryLexer
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
name|XQueryParser
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
name|XQueryTreeParser
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
name|SequenceIterator
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

begin_import
import|import
name|antlr
operator|.
name|collections
operator|.
name|AST
import|;
end_import

begin_class
specifier|public
class|class
name|SortedNodeSet
extends|extends
name|AbstractNodeSet
block|{
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
specifier|private
name|AccessContext
name|accessCtx
decl_stmt|;
specifier|private
name|SortedNodeSet
parameter_list|()
block|{
block|}
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
parameter_list|,
name|AccessContext
name|accessCtx
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
if|if
condition|(
name|accessCtx
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullAccessContextException
argument_list|()
throw|;
name|this
operator|.
name|accessCtx
operator|=
name|accessCtx
expr_stmt|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
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
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
return|;
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
name|addAll
argument_list|(
name|other
operator|.
name|toNodeSet
argument_list|()
argument_list|)
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
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
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
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|p
operator|.
name|getDocument
argument_list|()
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
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|broker
argument_list|,
name|accessCtx
argument_list|)
decl_stmt|;
name|XQueryLexer
name|lexer
init|=
operator|new
name|XQueryLexer
argument_list|(
name|context
argument_list|,
operator|new
name|StringReader
argument_list|(
name|sortExpr
argument_list|)
argument_list|)
decl_stmt|;
name|XQueryParser
name|parser
init|=
operator|new
name|XQueryParser
argument_list|(
name|lexer
argument_list|)
decl_stmt|;
name|XQueryTreeParser
name|treeParser
init|=
operator|new
name|XQueryTreeParser
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|parser
operator|.
name|xpath
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
comment|//TODO : error ?
name|LOG
operator|.
name|debug
argument_list|(
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|AST
name|ast
init|=
name|parser
operator|.
name|getAST
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"generated AST: "
operator|+
name|ast
operator|.
name|toStringTree
argument_list|()
argument_list|)
expr_stmt|;
name|expr
operator|=
operator|new
name|PathExpr
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|treeParser
operator|.
name|xpath
argument_list|(
name|ast
argument_list|,
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
name|treeParser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|treeParser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|expr
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|other
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|p
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|IteratorItem
name|item
init|=
operator|new
name|IteratorItem
argument_list|(
name|broker
argument_list|,
name|p
argument_list|,
name|expr
argument_list|,
name|docs
argument_list|,
name|context
argument_list|)
decl_stmt|;
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
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception during sort"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Exception during sort"
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
name|NodeProxy
name|proxy
parameter_list|)
block|{
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
name|NodeProxy
name|p
init|=
operator|(
operator|(
name|IteratorItem
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|proxy
decl_stmt|;
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
name|IteratorItem
name|item
init|=
operator|(
name|IteratorItem
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
name|NodeId
name|nodeId
parameter_list|)
block|{
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
name|NodeProxy
name|p
init|=
operator|(
operator|(
name|IteratorItem
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|proxy
decl_stmt|;
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
name|NodeProxy
name|p
init|=
operator|(
operator|(
name|IteratorItem
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|proxy
decl_stmt|;
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
comment|//TODO : evaluate both semantics
specifier|public
name|int
name|getItemCount
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
name|IteratorItem
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
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|p
argument_list|)
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
name|NodeProxy
name|p
init|=
operator|(
operator|(
name|IteratorItem
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
return|;
block|}
specifier|public
name|NodeSetIterator
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
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#iterate() 	 */
specifier|public
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
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
comment|/* (non-Javadoc) 	 * @see org.exist.dom.AbstractNodeSet#unorderedIterator() 	 */
specifier|public
name|SequenceIterator
name|unorderedIterator
parameter_list|()
throws|throws
name|XPathException
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
name|NodeSetIterator
implements|,
name|SequenceIterator
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
name|IteratorItem
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
name|NodeProxy
name|peekNode
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.xquery.value.SequenceIterator#nextItem() 		 */
specifier|public
name|Item
name|nextItem
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
name|IteratorItem
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
specifier|public
name|void
name|setPosition
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"NodeSetIterator.setPosition() is not supported by SortedNodeSetIterator"
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
specifier|final
class|class
name|IteratorItem
extends|extends
name|OrderedLinkedList
operator|.
name|Node
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
name|IteratorItem
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
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|proxy
operator|=
name|proxy
expr_stmt|;
try|try
block|{
name|Sequence
name|seq
init|=
name|expr
operator|.
name|eval
argument_list|(
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
name|Item
name|item
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|item
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|strings
operator|.
name|add
argument_list|(
operator|new
name|OrderedLinkedList
operator|.
name|SimpleNode
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
operator|.
name|toUpperCase
argument_list|()
argument_list|)
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
operator|(
name|OrderedLinkedList
operator|.
name|SimpleNode
operator|)
name|j
operator|.
name|next
argument_list|()
operator|)
operator|.
name|getData
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
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|OrderedLinkedList
operator|.
name|Node
name|other
parameter_list|)
block|{
name|IteratorItem
name|o
init|=
operator|(
name|IteratorItem
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
name|Constants
operator|.
name|EQUAL
else|:
name|Constants
operator|.
name|SUPERIOR
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
name|Constants
operator|.
name|EQUAL
else|:
name|Constants
operator|.
name|INFERIOR
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
specifier|public
name|boolean
name|equals
parameter_list|(
name|OrderedLinkedList
operator|.
name|Node
name|other
parameter_list|)
block|{
name|IteratorItem
name|o
init|=
operator|(
name|IteratorItem
operator|)
name|other
decl_stmt|;
return|return
name|value
operator|.
name|equals
argument_list|(
name|o
operator|.
name|value
argument_list|)
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.dom.NodeSet#add(org.exist.dom.NodeProxy) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Called SortedNodeSet.add()"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

