begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|Namespaces
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
name|dom
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
name|util
operator|.
name|XMLNames
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
name|QNameValue
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
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
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
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ElementConstructor
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|ElementConstructor
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
name|ElementConstructor
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
specifier|final
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
name|this
operator|.
name|content
operator|.
name|setUseStaticContext
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|PathExpr
name|getContent
parameter_list|()
block|{
return|return
name|content
return|;
block|}
specifier|public
name|AttributeConstructor
index|[]
name|getAttributes
parameter_list|()
block|{
return|return
name|attributes
return|;
block|}
specifier|public
name|void
name|setNameExpr
parameter_list|(
specifier|final
name|Expression
name|expr
parameter_list|)
block|{
comment|//Deferred atomization (we could have a QNameValue)
comment|//this.qnameExpr = new Atomize(context, expr);
name|this
operator|.
name|qnameExpr
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|Expression
name|getNameExpr
parameter_list|()
block|{
return|return
name|qnameExpr
return|;
block|}
specifier|public
name|void
name|addAttribute
parameter_list|(
specifier|final
name|AttributeConstructor
name|attr
parameter_list|)
throws|throws
name|XPathException
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
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
operator|.
name|equals
argument_list|(
name|attr
operator|.
name|getQName
argument_list|()
argument_list|)
condition|)
block|{
name|addNamespaceDecl
argument_list|(
literal|""
argument_list|,
name|attr
operator|.
name|getLiteralValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
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
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"Invalid qname "
operator|+
name|attr
operator|.
name|getQName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
if|else  if
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
specifier|final
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
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|QName
name|qn
init|=
operator|new
name|QName
argument_list|(
name|name
argument_list|,
name|uri
argument_list|,
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|XMLConstants
operator|.
name|XML_NS_PREFIX
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|Namespaces
operator|.
name|XML_NS
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|)
operator|||
operator|(
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
operator|.
name|equals
argument_list|(
name|name
argument_list|)
operator|&&
operator|!
name|XMLConstants
operator|.
name|NULL_NS_URI
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|)
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
name|XQST0070
argument_list|,
literal|"can not redefine '"
operator|+
name|qn
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Namespaces
operator|.
name|XML_NS
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|&&
operator|!
name|XMLConstants
operator|.
name|XML_NS_PREFIX
operator|.
name|equals
argument_list|(
name|name
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
name|XQST0070
argument_list|,
literal|"'"
operator|+
name|Namespaces
operator|.
name|XML_NS
operator|+
literal|"' can bind only to '"
operator|+
name|XMLConstants
operator|.
name|XML_NS_PREFIX
operator|+
literal|"' prefix"
argument_list|)
throw|;
block|}
if|if
condition|(
name|Namespaces
operator|.
name|XMLNS_NS
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
operator|&&
operator|!
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
operator|.
name|equals
argument_list|(
name|name
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
name|XQST0070
argument_list|,
literal|"'"
operator|+
name|Namespaces
operator|.
name|XMLNS_NS
operator|+
literal|"' can bind only to '"
operator|+
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
operator|+
literal|"' prefix"
argument_list|)
throw|;
block|}
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|name
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
name|uri
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
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
name|XQST0085
argument_list|,
literal|"cannot undeclare a prefix "
operator|+
name|name
operator|+
literal|"."
argument_list|)
throw|;
block|}
name|addNamespaceDecl
argument_list|(
name|qn
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addNamespaceDecl
parameter_list|(
specifier|final
name|QName
name|qn
parameter_list|)
throws|throws
name|XPathException
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
name|qn
expr_stmt|;
block|}
else|else
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
if|if
condition|(
name|qn
operator|.
name|equals
argument_list|(
name|namespaceDecls
index|[
name|i
index|]
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
name|XQST0071
argument_list|,
literal|"duplicate definition for '"
operator|+
name|qn
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
specifier|final
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
name|qn
expr_stmt|;
name|namespaceDecls
operator|=
name|decls
expr_stmt|;
block|}
comment|//context.inScopeNamespaces.put(qn.getLocalPart(), qn.getNamespaceURI());
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
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|context
operator|.
name|pushInScopeNamespaces
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|XMLConstants
operator|.
name|NULL_NS_URI
operator|.
name|equals
argument_list|(
name|namespaceDecls
index|[
name|i
index|]
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
comment|// TODO: the specs are unclear here: should we throw XQST0085 or not?
name|context
operator|.
name|inScopeNamespaces
operator|.
name|remove
argument_list|(
name|namespaceDecls
index|[
name|i
index|]
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
comment|//					if (context.inScopeNamespaces.remove(namespaceDecls[i].getLocalPart()) == null)
comment|//		        		throw new XPathException(getASTNode(), "XQST0085 : can not undefine '" + namespaceDecls[i] + "'");
block|}
else|else
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
name|getLocalPart
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
block|}
specifier|final
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
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|newContextInfo
operator|.
name|addFlag
argument_list|(
name|IN_NODE_CONSTRUCTOR
argument_list|)
expr_stmt|;
name|qnameExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
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
name|newContextInfo
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
name|content
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|popInScopeNamespaces
argument_list|()
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
name|contextSequence
parameter_list|,
specifier|final
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|context
operator|.
name|expressionStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|pushInScopeNamespaces
argument_list|()
expr_stmt|;
if|if
condition|(
name|newDocumentContext
condition|)
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
block|}
try|try
block|{
specifier|final
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
comment|//if ("".equals(namespaceDecls[i].getNamespaceURI())) {
comment|// TODO: the specs are unclear here: should we throw XQST0085 or not?
comment|//	context.inScopeNamespaces.remove(namespaceDecls[i].getLocalPart());
comment|//					if (context.inScopeNamespaces.remove(namespaceDecls[i].getLocalPart()) == null)
comment|//		        		throw new XPathException(getAS      TNode(), "XQST0085 : can not undefine '" + namespaceDecls[i] + "'");
comment|//} else
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|namespaceDecls
index|[
name|i
index|]
operator|.
name|getLocalPart
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
specifier|final
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
specifier|final
name|AttributeConstructor
name|constructor
init|=
name|attributes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|constructor
operator|.
name|isNamespaceDeclaration
argument_list|()
condition|)
block|{
specifier|final
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
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|,
name|constructor
operator|.
name|getLiteralValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
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
name|String
name|v
init|=
literal|null
decl_stmt|;
comment|// process the remaining attributes
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
specifier|final
name|AttributeConstructor
name|constructor
init|=
name|attributes
index|[
name|i
index|]
decl_stmt|;
specifier|final
name|Sequence
name|attrValues
init|=
name|constructor
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|QName
name|attrQName
decl_stmt|;
try|try
block|{
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
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"'"
operator|+
name|constructor
operator|.
name|getQName
argument_list|()
operator|+
literal|"' is not a valid attribute name"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|namespaceURI
init|=
name|attrQName
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceURI
operator|!=
literal|null
operator|&&
operator|!
name|namespaceURI
operator|.
name|isEmpty
argument_list|()
operator|&&
name|attrQName
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
condition|)
block|{
name|String
name|prefix
init|=
name|context
operator|.
name|getPrefixForURI
argument_list|(
name|namespaceURI
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|attrQName
operator|=
operator|new
name|QName
argument_list|(
name|attrQName
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|attrQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//generate prefix
for|for
control|(
specifier|final
name|int
name|n
init|=
literal|1
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|prefix
operator|=
literal|"eXnsp"
operator|+
name|n
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|prefix
argument_list|)
operator|==
literal|null
condition|)
block|{
name|attrQName
operator|=
operator|new
name|QName
argument_list|(
name|attrQName
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|attrQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
break|break;
block|}
name|prefix
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|prefix
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
literal|"Prefix can't be generated."
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|attrs
operator|.
name|getIndex
argument_list|(
name|attrQName
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|attrQName
operator|.
name|getLocalPart
argument_list|()
argument_list|)
operator|!=
operator|-
literal|1
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
name|XQST0040
argument_list|,
literal|"'"
operator|+
name|attrQName
operator|.
name|getLocalPart
argument_list|()
operator|+
literal|"' is a duplicate attribute name"
argument_list|)
throw|;
block|}
name|v
operator|=
name|DynamicAttributeConstructor
operator|.
name|normalize
argument_list|(
name|this
argument_list|,
name|attrQName
argument_list|,
name|attrValues
operator|.
name|getStringValue
argument_list|()
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
name|getLocalPart
argument_list|()
argument_list|,
name|attrQName
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"CDATA"
argument_list|,
name|v
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
specifier|final
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
operator|!
name|qnameSeq
operator|.
name|hasOne
argument_list|()
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
name|XPTY0004
argument_list|,
literal|"Type error: the node name should evaluate to a single item"
argument_list|)
throw|;
block|}
specifier|final
name|Item
name|qnitem
init|=
name|qnameSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|QName
name|qn
decl_stmt|;
if|if
condition|(
name|qnitem
operator|instanceof
name|QNameValue
condition|)
block|{
name|qn
operator|=
operator|(
operator|(
name|QNameValue
operator|)
name|qnitem
operator|)
operator|.
name|getQName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//Do we have the same result than Atomize there ? -pb
try|try
block|{
name|qn
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|qnitem
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"'"
operator|+
name|qnitem
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"' is not a valid element name"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|setLocation
argument_list|(
name|getLine
argument_list|()
argument_list|,
name|getColumn
argument_list|()
argument_list|,
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|//Use the default namespace if specified
comment|/*                  if (qn.getPrefix() == null&& context.inScopeNamespaces.get("xmlns") != null) {                      qn.setNamespaceURI((String)context.inScopeNamespaces.get("xmlns"));                  }                  */
if|if
condition|(
name|qn
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
operator|&&
name|context
operator|.
name|getInScopeNamespace
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|qn
operator|=
operator|new
name|QName
argument_list|(
name|qn
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|context
operator|.
name|getInScopeNamespace
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
argument_list|,
name|qn
operator|.
name|getPrefix
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//Not in the specs but... makes sense
if|if
condition|(
operator|!
name|XMLNames
operator|.
name|isName
argument_list|(
name|qn
operator|.
name|getLocalPart
argument_list|()
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
name|XPTY0004
argument_list|,
literal|"'"
operator|+
name|qnitem
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"' is not a valid element name"
argument_list|)
throw|;
block|}
comment|// add namespace declaration nodes
specifier|final
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
comment|// do we need to add a namespace declaration for the current node?
if|if
condition|(
name|qn
operator|.
name|hasNamespace
argument_list|()
condition|)
block|{
if|if
condition|(
name|context
operator|.
name|getInScopePrefix
argument_list|(
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
name|String
name|prefix
init|=
name|qn
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
condition|)
block|{
name|prefix
operator|=
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
expr_stmt|;
block|}
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|prefix
argument_list|,
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|namespaceNode
argument_list|(
operator|new
name|QName
argument_list|(
name|prefix
argument_list|,
name|qn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|,
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
operator|(
name|qn
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
operator|||
name|qn
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
operator|&&
name|context
operator|.
name|getInheritedNamespace
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
expr_stmt|;
name|builder
operator|.
name|namespaceNode
argument_list|(
operator|new
name|QName
argument_list|(
literal|""
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|,
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|qn
operator|.
name|getPrefix
argument_list|()
operator|==
literal|null
operator|||
name|qn
operator|.
name|getPrefix
argument_list|()
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|context
operator|.
name|declareInScopeNamespace
argument_list|(
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
expr_stmt|;
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
specifier|final
name|NodeImpl
name|node
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
decl_stmt|;
return|return
name|node
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popInScopeNamespaces
argument_list|()
expr_stmt|;
if|if
condition|(
name|newDocumentContext
condition|)
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
name|context
operator|.
name|expressionEnd
argument_list|(
name|this
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
literal|"element "
argument_list|)
expr_stmt|;
comment|//TODO : remove curly braces if Qname
name|dumper
operator|.
name|display
argument_list|(
literal|"{"
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
literal|"} "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"{"
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
block|{
name|dumper
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
specifier|final
name|AttributeConstructor
name|attr
init|=
name|attributes
index|[
name|i
index|]
decl_stmt|;
name|attr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
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
specifier|final
name|Iterator
argument_list|<
name|Expression
argument_list|>
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
specifier|final
name|Expression
name|expr
init|=
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
block|{
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
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"element "
argument_list|)
expr_stmt|;
comment|//TODO : remove curly braces if Qname
name|result
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|qnameExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"{"
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
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AttributeConstructor
name|attr
init|=
name|attributes
index|[
name|i
index|]
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|attr
operator|.
name|toString
argument_list|()
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
specifier|final
name|Iterator
argument_list|<
name|Expression
argument_list|>
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
specifier|final
name|Expression
name|expr
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|.
name|append
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
specifier|final
name|int
name|axis
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|int
name|getPrimaryAxis
parameter_list|()
block|{
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
return|return
name|content
operator|.
name|getPrimaryAxis
argument_list|()
return|;
block|}
return|return
name|Constants
operator|.
name|UNKNOWN_AXIS
return|;
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
name|qnameExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|content
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|Expression
name|next
init|=
name|attributes
index|[
name|i
index|]
decl_stmt|;
name|next
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
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
name|visitor
operator|.
name|visitElementConstructor
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
name|ELEMENT
return|;
block|}
block|}
end_class

end_unit
