begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|Logger
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
name|dom
operator|.
name|DocumentSet
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
name|NodeImpl
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
name|NodeIndexListener
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
name|XMLUtil
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
name|XPathLexer2
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
name|XPathParser2
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
name|XPathTreeParser2
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
name|storage
operator|.
name|store
operator|.
name|StorageAddress
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
name|StaticContext
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
name|XPathException
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
name|xpath
operator|.
name|value
operator|.
name|Type
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
name|DocumentFragment
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
name|RecognitionException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenStreamException
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

begin_comment
comment|/**  * Modification.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Modification
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
name|Modification
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|selectStmt
init|=
literal|null
decl_stmt|;
specifier|protected
name|DocumentFragment
name|content
init|=
literal|null
decl_stmt|;
specifier|protected
name|DBBroker
name|broker
decl_stmt|;
specifier|protected
name|DocumentSet
name|docs
decl_stmt|;
comment|/** 	 * Constructor for Modification. 	 */
specifier|public
name|Modification
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|String
name|selectStmt
parameter_list|)
block|{
name|this
operator|.
name|selectStmt
operator|=
name|selectStmt
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|long
name|process
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
specifier|public
name|void
name|setContent
parameter_list|(
name|DocumentFragment
name|node
parameter_list|)
block|{
name|content
operator|=
name|node
expr_stmt|;
block|}
specifier|protected
name|NodeImpl
index|[]
name|select
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
try|try
block|{
name|StaticContext
name|context
init|=
operator|new
name|StaticContext
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|XPathLexer2
name|lexer
init|=
operator|new
name|XPathLexer2
argument_list|(
operator|new
name|StringReader
argument_list|(
name|selectStmt
argument_list|)
argument_list|)
decl_stmt|;
name|XPathParser2
name|parser
init|=
operator|new
name|XPathParser2
argument_list|(
name|lexer
argument_list|)
decl_stmt|;
name|XPathTreeParser2
name|treeParser
init|=
operator|new
name|XPathTreeParser2
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
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
name|PathExpr
name|expr
init|=
operator|new
name|PathExpr
argument_list|(
name|context
argument_list|)
decl_stmt|;
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|treeParser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"modification select: "
operator|+
name|expr
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|Sequence
name|resultSeq
init|=
name|expr
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|resultSeq
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|NODE
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"select expression should evaluate to a"
operator|+
literal|"node-set"
argument_list|)
throw|;
name|NodeList
name|set
init|=
operator|(
name|NodeList
operator|)
name|resultSeq
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"found "
operator|+
name|set
operator|.
name|getLength
argument_list|()
operator|+
literal|" for select; retrieving nodes..."
argument_list|)
expr_stmt|;
name|ArrayList
name|out
init|=
operator|new
name|ArrayList
argument_list|(
name|set
operator|.
name|getLength
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|set
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|add
argument_list|(
name|set
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|NodeImpl
name|result
index|[]
init|=
operator|new
name|NodeImpl
index|[
name|out
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|out
operator|.
name|toArray
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|RecognitionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error while parsing select expression"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TokenStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"error while parsing select expression"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
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
literal|"<xu:"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" select=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|selectStmt
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|XMLUtil
operator|.
name|dump
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</xu:"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|final
specifier|static
class|class
name|IndexListener
implements|implements
name|NodeIndexListener
block|{
name|NodeImpl
index|[]
name|nodes
decl_stmt|;
specifier|public
name|IndexListener
parameter_list|(
name|NodeImpl
index|[]
name|nodes
parameter_list|)
block|{
name|this
operator|.
name|nodes
operator|=
name|nodes
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.dom.NodeIndexListener#nodeChanged(org.exist.dom.NodeImpl) 		 */
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|NodeImpl
name|node
parameter_list|)
block|{
specifier|final
name|long
name|address
init|=
name|node
operator|.
name|getInternalAddress
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|StorageAddress
operator|.
name|equals
argument_list|(
name|nodes
index|[
name|i
index|]
operator|.
name|getInternalAddress
argument_list|()
argument_list|,
name|address
argument_list|)
condition|)
name|nodes
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 		 * @see org.exist.dom.NodeIndexListener#nodeChanged(long, long) 		 */
specifier|public
name|void
name|nodeChanged
parameter_list|(
name|long
name|oldAddress
parameter_list|,
name|long
name|newAddress
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|StorageAddress
operator|.
name|equals
argument_list|(
name|nodes
index|[
name|i
index|]
operator|.
name|getInternalAddress
argument_list|()
argument_list|,
name|oldAddress
argument_list|)
condition|)
block|{
name|nodes
index|[
name|i
index|]
operator|.
name|setInternalAddress
argument_list|(
name|newAddress
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|final
specifier|static
class|class
name|NodeComparator
implements|implements
name|Comparator
block|{
comment|/* (non-Javadoc) 		* @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) 		*/
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|NodeImpl
name|n1
init|=
operator|(
name|NodeImpl
operator|)
name|o1
decl_stmt|;
name|NodeImpl
name|n2
init|=
operator|(
name|NodeImpl
operator|)
name|o2
decl_stmt|;
if|if
condition|(
name|n1
operator|.
name|getInternalAddress
argument_list|()
operator|==
name|n2
operator|.
name|getInternalAddress
argument_list|()
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|n1
operator|.
name|getInternalAddress
argument_list|()
operator|<
name|n2
operator|.
name|getInternalAddress
argument_list|()
condition|)
return|return
operator|-
literal|1
return|;
else|else
return|return
literal|1
return|;
block|}
block|}
block|}
end_class

end_unit

