begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|DocumentImpl
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
name|NodeSet
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
name|util
operator|.
name|Lock
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
comment|/**  * Base class for all XUpdate modifications.  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Modification
block|{
specifier|protected
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
name|NodeList
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
specifier|protected
name|Map
name|namespaces
decl_stmt|;
specifier|protected
name|DocumentSet
name|lockedDocuments
init|=
literal|null
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
parameter_list|,
name|Map
name|namespaces
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
name|this
operator|.
name|namespaces
operator|=
operator|new
name|HashMap
argument_list|(
name|namespaces
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Process the modification. This is the main method that has to be implemented  	 * by all subclasses. 	 *  	 * @return 	 * @throws PermissionDeniedException 	 * @throws LockException 	 * @throws EXistException 	 * @throws XPathException 	 */
specifier|public
specifier|abstract
name|long
name|process
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
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
name|NodeList
name|nodes
parameter_list|)
block|{
name|content
operator|=
name|nodes
expr_stmt|;
block|}
comment|/** 	 * Evaluate the select expression. 	 *  	 * @param docs 	 * @return 	 * @throws PermissionDeniedException 	 * @throws EXistException 	 * @throws XPathException 	 */
specifier|protected
name|NodeList
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
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|context
operator|.
name|setExclusiveMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|context
operator|.
name|setStaticallyKnownDocuments
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|namespaces
operator|.
name|entrySet
argument_list|()
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
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareNamespace
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|XQueryLexer
name|lexer
init|=
operator|new
name|XQueryLexer
argument_list|(
operator|new
name|StringReader
argument_list|(
name|selectStmt
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
operator|!
operator|(
name|resultSeq
operator|.
name|getLength
argument_list|()
operator|==
literal|0
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|resultSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"select expression should evaluate to a node-set; got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|resultSeq
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found "
operator|+
name|resultSeq
operator|.
name|getLength
argument_list|()
operator|+
literal|" for select: "
operator|+
name|selectStmt
argument_list|)
expr_stmt|;
return|return
operator|(
name|NodeList
operator|)
name|resultSeq
operator|.
name|toNodeSet
argument_list|()
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
comment|/** 	 * Acquire a lock on all documents processed by this modification. 	 * We have to avoid that node positions change during the 	 * operation. 	 *  	 * @param nl 	 * @return 	 * @throws LockException 	 */
specifier|protected
name|NodeImpl
index|[]
name|selectAndLock
parameter_list|()
throws|throws
name|LockException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|XPathException
block|{
name|Lock
name|globalLock
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getGlobalUpdateLock
argument_list|()
decl_stmt|;
try|try
block|{
name|globalLock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|NodeList
name|nl
init|=
name|select
argument_list|(
name|docs
argument_list|)
decl_stmt|;
name|lockedDocuments
operator|=
operator|(
operator|(
name|NodeSet
operator|)
name|nl
operator|)
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
comment|// acquire a lock on all documents
comment|// we have to avoid that node positions change
comment|// during the modification
name|lockedDocuments
operator|.
name|lock
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NodeImpl
name|ql
index|[]
init|=
operator|new
name|NodeImpl
index|[
name|nl
operator|.
name|getLength
argument_list|()
index|]
decl_stmt|;
name|DocumentImpl
name|doc
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
name|ql
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ql
index|[
name|i
index|]
operator|=
operator|(
name|NodeImpl
operator|)
name|nl
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|ql
index|[
name|i
index|]
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
name|doc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
name|ql
return|;
block|}
finally|finally
block|{
name|globalLock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Release all acquired document locks. 	 */
specifier|protected
name|void
name|unlockDocuments
parameter_list|()
block|{
if|if
condition|(
name|lockedDocuments
operator|==
literal|null
condition|)
return|return;
name|lockedDocuments
operator|.
name|unlock
argument_list|(
literal|true
argument_list|)
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
comment|//		buf.append(XMLUtil.dump(content));
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
block|{
name|nodes
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
block|}
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

