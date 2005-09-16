begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

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
name|Iterator
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
name|memtree
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
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
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
name|StringValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  * Constructor for element nodes. This class handles both, direct and dynamic  * element constructors.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ElementConstructor
extends|extends
name|NodeConstructor
block|{
specifier|private
name|Expression
name|qnameExpr
decl_stmt|;
specifier|private
name|PathExpr
name|content
init|=
literal|null
decl_stmt|;
specifier|private
name|AttributeConstructor
name|attributes
index|[]
init|=
literal|null
decl_stmt|;
specifier|private
name|QName
name|namespaceDecls
index|[]
init|=
literal|null
decl_stmt|;
specifier|public
name|ElementConstructor
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
name|ElementConstructor
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|qname
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|qnameExpr
operator|=
operator|new
name|LiteralValue
argument_list|(
name|context
argument_list|,
operator|new
name|StringValue
argument_list|(
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContent
parameter_list|(
name|PathExpr
name|path
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|path
expr_stmt|;
block|}
specifier|public
name|void
name|setNameExpr
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|qnameExpr
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addAttribute
parameter_list|(
name|AttributeConstructor
name|attr
parameter_list|)
block|{
if|if
condition|(
name|attr
operator|.
name|isNamespaceDeclaration
argument_list|()
condition|)
block|{
if|if
condition|(
name|attr
operator|.
name|getQName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"xmlns"
argument_list|)
condition|)
name|addNamespaceDecl
argument_list|(
literal|"xmlns"
argument_list|,
name|attr
operator|.
name|getLiteralValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|addNamespaceDecl
argument_list|(
name|QName
operator|.
name|extractLocalName
argument_list|(
name|attr
operator|.
name|getQName
argument_list|()
argument_list|)
argument_list|,
name|attr
operator|.
name|getLiteralValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else	if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
operator|new
name|AttributeConstructor
index|[
literal|1
index|]
expr_stmt|;
name|attributes
index|[
literal|0
index|]
operator|=
name|attr
expr_stmt|;
block|}
else|else
block|{
name|AttributeConstructor
name|natts
index|[]
init|=
operator|new
name|AttributeConstructor
index|[
name|attributes
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|attributes
argument_list|,
literal|0
argument_list|,
name|natts
argument_list|,
literal|0
argument_list|,
name|attributes
operator|.
name|length
argument_list|)
expr_stmt|;
name|natts
index|[
name|attributes
operator|.
name|length
index|]
operator|=
name|attr
expr_stmt|;
name|attributes
operator|=
name|natts
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addNamespaceDecl
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
if|if
condition|(
name|namespaceDecls
operator|==
literal|null
condition|)
block|{
name|namespaceDecls
operator|=
operator|new
name|QName
index|[
literal|1
index|]
expr_stmt|;
name|namespaceDecls
index|[
literal|0
index|]
operator|=
operator|new
name|QName
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|,
literal|"xmlns"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|QName
name|decls
index|[]
init|=
operator|new
name|QName
index|[
name|namespaceDecls
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|namespaceDecls
argument_list|,
literal|0
argument_list|,
name|decls
argument_list|,
literal|0
argument_list|,
name|namespaceDecls
operator|.
name|length
argument_list|)
expr_stmt|;
name|decls
index|[
name|namespaceDecls
operator|.
name|length
index|]
operator|=
operator|new
name|QName
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|,
literal|"xmlns"
argument_list|)
expr_stmt|;
name|namespaceDecls
operator|=
name|decls
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XPathException
block|{
name|qnameExpr
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
argument_list|)
expr_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
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
name|attributes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|attributes
index|[
name|i
index|]
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
name|content
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|context
operator|.
name|pushInScopeNamespaces
argument_list|()
expr_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
comment|// declare namespaces
if|if
condition|(
name|namespaceDecls
operator|!=
literal|null
condition|)
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
name|namespaceDecls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|namespaceDecls
index|[
name|i
index|]
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|namespaceDecls
index|[
name|i
index|]
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// process attributes
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
name|AttributeConstructor
name|constructor
decl_stmt|;
name|Sequence
name|attrValues
decl_stmt|;
name|QName
name|attrQName
decl_stmt|;
comment|// first, search for xmlns attributes and declare in-scope namespaces
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attributes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|constructor
operator|=
operator|(
name|AttributeConstructor
operator|)
name|attributes
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|constructor
operator|.
name|isNamespaceDeclaration
argument_list|()
condition|)
block|{
name|int
name|p
init|=
name|constructor
operator|.
name|getQName
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|<
literal|0
condition|)
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
literal|""
argument_list|,
name|constructor
operator|.
name|getLiteralValue
argument_list|()
argument_list|)
expr_stmt|;
else|else
block|{
name|String
name|prefix
init|=
name|constructor
operator|.
name|getQName
argument_list|()
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
decl_stmt|;
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|prefix
argument_list|,
name|constructor
operator|.
name|getLiteralValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// process the remaining attributesCharArr
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attributes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|constructor
operator|=
operator|(
name|AttributeConstructor
operator|)
name|attributes
index|[
name|i
index|]
expr_stmt|;
name|attrValues
operator|=
name|constructor
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
name|attrQName
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|constructor
operator|.
name|getQName
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
name|attrQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|attrQName
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|attrQName
operator|.
name|toString
argument_list|()
argument_list|,
literal|"CDATA"
argument_list|,
name|attrValues
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
comment|// create the element
name|Sequence
name|qnameSeq
init|=
name|qnameExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|qnameSeq
operator|.
name|getLength
argument_list|()
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the node name should evaluate to a single string"
argument_list|)
throw|;
name|QName
name|qn
init|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|qnameSeq
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
comment|// add namespace declaration nodes
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|qn
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaceDecls
operator|!=
literal|null
condition|)
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
name|namespaceDecls
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|namespaceNode
argument_list|(
name|namespaceDecls
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// process element contents
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|content
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|NodeImpl
name|node
init|=
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
decl_stmt|;
name|context
operator|.
name|popInScopeNamespaces
argument_list|()
expr_stmt|;
return|return
name|node
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
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
literal|"element { "
argument_list|)
expr_stmt|;
name|qnameExpr
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
literal|" } {"
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
name|AttributeConstructor
name|attr
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
name|attributes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|dumper
operator|.
name|nl
argument_list|()
expr_stmt|;
name|attr
operator|=
operator|(
name|AttributeConstructor
operator|)
name|attributes
index|[
name|i
index|]
expr_stmt|;
name|attr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|content
operator|.
name|steps
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
name|Expression
name|expr
init|=
operator|(
name|Expression
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|expr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
name|dumper
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
block|}
name|dumper
operator|.
name|endIndent
argument_list|()
operator|.
name|nl
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#setPrimaryAxis(int) 	 */
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
name|content
operator|.
name|setPrimaryAxis
argument_list|(
name|axis
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
name|content
operator|.
name|resetState
argument_list|()
expr_stmt|;
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attributes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|next
init|=
operator|(
name|Expression
operator|)
name|attributes
index|[
name|i
index|]
decl_stmt|;
name|next
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

