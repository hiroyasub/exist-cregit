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
name|xpath
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|java
operator|.
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|SymbolTable
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
name|DBBroker
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
name|functions
operator|.
name|UserDefinedFunction
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

begin_class
specifier|public
class|class
name|StaticContext
block|{
specifier|public
specifier|final
specifier|static
name|String
name|XML_NS
init|=
literal|"http://www.w3.org/XML/1998/namespace"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SCHEMA_NS
init|=
literal|"http://www.w3.org/2001/XMLSchema"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SCHEMA_DATATYPES_NS
init|=
literal|"http://www.w3.org/2001/XMLSchema-datatypes"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SCHEMA_INSTANCE_NS
init|=
literal|"http://www.w3.org/2001/XMLSchema-instance"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XPATH_DATATYPES_NS
init|=
literal|"http://www.w3.org/2003/05/xpath-datatypes"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XQUERY_LOCAL_NS
init|=
literal|"http://www.w3.org/2003/08/xquery-local-functions"
decl_stmt|;
specifier|private
name|HashMap
name|namespaces
decl_stmt|;
specifier|private
name|HashMap
name|inScopeNamespaces
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|Stack
name|namespaceStack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
name|TreeMap
name|builtinFunctions
decl_stmt|;
specifier|private
name|TreeMap
name|declaredFunctions
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|TreeMap
name|globalVariables
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|TreeMap
name|variables
init|=
operator|new
name|TreeMap
argument_list|()
decl_stmt|;
specifier|private
name|Stack
name|variableStack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
name|DocumentSet
name|staticDocuments
init|=
literal|null
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
specifier|private
name|String
name|baseURI
init|=
literal|""
decl_stmt|;
specifier|private
name|String
name|defaultFunctionNamespace
init|=
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
decl_stmt|;
comment|/** 	 * Set to true to enable XPath 1.0 	 * backwards compatibility. 	 */
specifier|private
name|boolean
name|backwardsCompatible
init|=
literal|true
decl_stmt|;
comment|/** 	 * The position of the currently processed item in the context  	 * sequence. This field has to be set on demand, for example, 	 * before calling the fn:position() function.  	 */
specifier|private
name|int
name|contextPosition
init|=
literal|0
decl_stmt|;
comment|/** 	 * The builder used for creating in-memory document  	 * fragments 	 */
specifier|private
name|MemTreeBuilder
name|builder
init|=
literal|null
decl_stmt|;
specifier|private
name|Stack
name|fragmentStack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|public
name|StaticContext
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
name|loadDefaults
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Declare a user-defined prefix/namespace mapping. 	 *  	 * eXist internally keeps a table containing all prefix/namespace 	 * mappings it found in documents, which have been previously 	 * stored into the database. These default mappings need not to be 	 * declared explicitely. 	 *  	 * @param prefix 	 * @param uri 	 */
specifier|public
name|void
name|declareNamespace
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
name|prefix
operator|==
literal|null
operator|||
name|uri
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null argument passed to declareNamespace"
argument_list|)
throw|;
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Declare an in-scope namespace. This is called during query execution. 	 *  	 * @param prefix 	 * @param uri 	 */
specifier|public
name|void
name|declareInScopeNamespace
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
name|prefix
operator|==
literal|null
operator|||
name|uri
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"null argument passed to declareNamespace"
argument_list|)
throw|;
if|if
condition|(
name|inScopeNamespaces
operator|==
literal|null
condition|)
name|inScopeNamespaces
operator|=
operator|new
name|HashMap
argument_list|()
expr_stmt|;
name|inScopeNamespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getDefaultFunctionNamespace
parameter_list|()
block|{
return|return
name|defaultFunctionNamespace
return|;
block|}
comment|/** 	 * Set the default function namespace. By default, this 	 * points to the namespace for XPath built-in functions. 	 *  	 * @param uri 	 */
specifier|public
name|void
name|setDefaultFunctionNamespace
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|defaultFunctionNamespace
operator|=
name|uri
expr_stmt|;
block|}
comment|/** 	 * Returns a namespace URI for the prefix or 	 * null if no such prefix exists. 	 *  	 * @param prefix 	 * @return 	 */
specifier|public
name|String
name|getURIForPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
name|String
name|ns
init|=
operator|(
name|String
operator|)
name|namespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
decl_stmt|;
if|if
condition|(
name|ns
operator|==
literal|null
condition|)
comment|// try in-scope namespace declarations
return|return
name|inScopeNamespaces
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|String
operator|)
name|inScopeNamespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
return|;
else|else
return|return
name|ns
return|;
block|}
specifier|public
name|String
name|getPrefixForURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
return|return
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
return|;
block|}
if|if
condition|(
name|inScopeNamespaces
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|inScopeNamespaces
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
return|return
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** 	 * Removes the namespace URI from the prefix/namespace  	 * mappings table. 	 *  	 * @param uri 	 */
specifier|public
name|void
name|removeNamespace
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|namespaces
operator|.
name|values
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
if|if
condition|(
operator|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
if|if
condition|(
name|inScopeNamespaces
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|inScopeNamespaces
operator|.
name|values
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
if|if
condition|(
operator|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
operator|)
operator|.
name|equals
argument_list|(
name|uri
argument_list|)
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
comment|/** 	 * Clear all user-defined prefix/namespace mappings. 	 * 	 */
specifier|public
name|void
name|clearNamespaces
parameter_list|()
block|{
name|namespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|inScopeNamespaces
operator|!=
literal|null
condition|)
name|inScopeNamespaces
operator|.
name|clear
argument_list|()
expr_stmt|;
name|loadDefaults
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setStaticallyKnownDocuments
parameter_list|(
name|DocumentSet
name|docs
parameter_list|)
block|{
name|staticDocuments
operator|=
name|docs
expr_stmt|;
block|}
specifier|public
name|DocumentSet
name|getStaticallyKnownDocuments
parameter_list|()
block|{
return|return
name|staticDocuments
return|;
block|}
comment|/** 	 * Find the implementing class for a function name. 	 *  	 * @param fnName 	 * @return 	 */
specifier|public
name|Class
name|getClassForFunction
parameter_list|(
name|QName
name|fnName
parameter_list|)
block|{
return|return
operator|(
name|Class
operator|)
name|builtinFunctions
operator|.
name|get
argument_list|(
name|fnName
argument_list|)
return|;
block|}
specifier|public
name|void
name|declareFunction
parameter_list|(
name|UserDefinedFunction
name|function
parameter_list|)
throws|throws
name|XPathException
block|{
name|declaredFunctions
operator|.
name|put
argument_list|(
name|function
operator|.
name|getName
argument_list|()
argument_list|,
name|function
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UserDefinedFunction
name|resolveFunction
parameter_list|(
name|QName
name|name
parameter_list|)
throws|throws
name|XPathException
block|{
name|UserDefinedFunction
name|func
init|=
operator|(
name|UserDefinedFunction
operator|)
name|declaredFunctions
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|func
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Function "
operator|+
name|name
operator|+
literal|" is unknown"
argument_list|)
throw|;
return|return
name|func
return|;
block|}
specifier|public
name|Iterator
name|getBuiltinFunctions
parameter_list|()
block|{
return|return
name|builtinFunctions
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
comment|/** 	 * Declare a variable. This is called by variable binding expressions like 	 * "let" and "for". 	 *  	 * @param var 	 * @return 	 * @throws XPathException 	 */
specifier|public
name|Variable
name|declareVariable
parameter_list|(
name|Variable
name|var
parameter_list|)
throws|throws
name|XPathException
block|{
name|variables
operator|.
name|put
argument_list|(
name|var
operator|.
name|getQName
argument_list|()
argument_list|,
name|var
argument_list|)
expr_stmt|;
name|var
operator|.
name|setStackPosition
argument_list|(
name|variableStack
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|var
return|;
block|}
comment|/** 	 * Declare a user-defined variable. 	 *  	 * The value argument is converted into an XPath value 	 * (@see XPathUtil#javaObjectToXPath(Object)). 	 *  	 * @param qname the qualified name of the new variable. Any namespaces should 	 * have been declared before. 	 * @param value a Java object, representing the fixed value of the variable 	 * @return the created Variable object 	 * @throws XPathException if the value cannot be converted into a known XPath value 	 * or the variable QName references an unknown namespace-prefix.  	 */
specifier|public
name|Variable
name|declareVariable
parameter_list|(
name|String
name|qname
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|val
init|=
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|QName
name|qn
init|=
name|QName
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|qname
argument_list|)
decl_stmt|;
name|Variable
name|var
init|=
operator|new
name|Variable
argument_list|(
name|qn
argument_list|)
decl_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|globalVariables
operator|.
name|put
argument_list|(
name|qn
argument_list|,
name|var
argument_list|)
expr_stmt|;
return|return
name|var
return|;
block|}
comment|/** 	 * Try to resolve a variable. 	 *  	 * @param qname the qualified name of the variable 	 * @return the declared Variable object 	 * @throws XPathException if the variable is unknown 	 */
specifier|public
name|Variable
name|resolveVariable
parameter_list|(
name|String
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
name|QName
name|qn
init|=
name|QName
operator|.
name|parse
argument_list|(
name|this
argument_list|,
name|qname
argument_list|)
decl_stmt|;
name|Variable
name|var
init|=
operator|(
name|Variable
operator|)
name|variables
operator|.
name|get
argument_list|(
name|qn
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|==
literal|null
condition|)
block|{
name|var
operator|=
operator|(
name|Variable
operator|)
name|globalVariables
operator|.
name|get
argument_list|(
name|qn
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|var
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"variable "
operator|+
name|qname
operator|+
literal|" is not bound"
argument_list|)
throw|;
return|return
name|var
return|;
block|}
comment|/** 	 * Turn on/off XPath 1.0 backwards compatibility. 	 *  	 * If turned on, comparison expressions will behave like 	 * in XPath 1.0, i.e. if any one of the operands is a number, 	 * the other operand will be cast to a double. 	 *  	 * @param backwardsCompatible 	 */
specifier|public
name|void
name|setBackwardsCompatibility
parameter_list|(
name|boolean
name|backwardsCompatible
parameter_list|)
block|{
name|this
operator|.
name|backwardsCompatible
operator|=
name|backwardsCompatible
expr_stmt|;
block|}
comment|/** 	 * XPath 1.0 backwards compatibility turned on? 	 *  	 * In XPath 1.0 compatible mode, additional conversions 	 * will be applied to values if a numeric value is expected. 	 *   	 * @return 	 */
specifier|public
name|boolean
name|isBackwardsCompatible
parameter_list|()
block|{
return|return
name|this
operator|.
name|backwardsCompatible
return|;
block|}
comment|/** 	 * Get the DBBroker instance used for the current query. 	 *  	 * The DBBroker is the main database access object, providing 	 * access to all internal database functions. 	 *  	 * @return 	 */
specifier|public
name|DBBroker
name|getBroker
parameter_list|()
block|{
return|return
name|broker
return|;
block|}
comment|/** 	 * Get the user which executes the current query. 	 *  	 * @return 	 */
specifier|public
name|User
name|getUser
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getUser
argument_list|()
return|;
block|}
comment|/** 	 * Get the document builder currently used for creating 	 * temporary document fragments. A new document builder 	 * will be created on demand. 	 *  	 * @return 	 */
specifier|public
name|MemTreeBuilder
name|getDocumentBuilder
parameter_list|()
block|{
if|if
condition|(
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
operator|new
name|MemTreeBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
specifier|protected
name|void
name|pushDocumentContext
parameter_list|()
block|{
if|if
condition|(
name|builder
operator|!=
literal|null
condition|)
name|fragmentStack
operator|.
name|push
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|void
name|popDocumentContext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|fragmentStack
operator|.
name|isEmpty
argument_list|()
condition|)
name|builder
operator|=
operator|(
name|MemTreeBuilder
operator|)
name|fragmentStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Set the base URI for the evaluation context. 	 *  	 * This is the URI returned by the fn:base-uri() 	 * function. 	 *  	 * @param uri 	 */
specifier|public
name|void
name|setBaseURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|baseURI
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
return|return
name|baseURI
return|;
block|}
comment|/** 	 * Set the current context position, i.e. the position 	 * of the currently processed item in the context sequence. 	 * This value is required by some expressions, e.g. fn:position(). 	 *  	 * @param pos 	 */
specifier|public
name|void
name|setContextPosition
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|contextPosition
operator|=
name|pos
expr_stmt|;
block|}
specifier|public
name|int
name|getContextPosition
parameter_list|()
block|{
return|return
name|contextPosition
return|;
block|}
specifier|public
name|void
name|pushNamespaceContext
parameter_list|()
block|{
name|HashMap
name|m
init|=
operator|(
name|HashMap
operator|)
name|inScopeNamespaces
operator|.
name|clone
argument_list|()
decl_stmt|;
name|namespaceStack
operator|.
name|push
argument_list|(
name|inScopeNamespaces
argument_list|)
expr_stmt|;
name|inScopeNamespaces
operator|=
name|m
expr_stmt|;
block|}
specifier|public
name|void
name|popNamespaceContext
parameter_list|()
block|{
name|inScopeNamespaces
operator|=
operator|(
name|HashMap
operator|)
name|namespaceStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Save the current context on top of a stack.  	 *  	 * Use {@link popContext()} to restore the current state. 	 * This method saves the current in-scope variable 	 * definitions. 	 */
specifier|public
name|void
name|pushLocalContext
parameter_list|(
name|boolean
name|emptyContext
parameter_list|)
block|{
name|variableStack
operator|.
name|push
argument_list|(
name|variables
argument_list|)
expr_stmt|;
if|if
condition|(
name|emptyContext
condition|)
name|variables
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
else|else
name|variables
operator|=
operator|new
name|TreeMap
argument_list|(
name|variables
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Restore previous state. 	 */
specifier|public
name|void
name|popLocalContext
parameter_list|()
block|{
name|variables
operator|=
operator|(
name|TreeMap
operator|)
name|variableStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Returns the current size of the stack. This is used to determine 	 * where a variable has been declared. 	 *  	 * @return 	 */
specifier|public
name|int
name|getCurrentStackSize
parameter_list|()
block|{
return|return
name|variableStack
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 * Load the default prefix/namespace mappings table and set up 	 * internal functions. 	 */
specifier|private
name|void
name|loadDefaults
parameter_list|()
block|{
name|SymbolTable
name|syms
init|=
name|DBBroker
operator|.
name|getSymbols
argument_list|()
decl_stmt|;
name|String
index|[]
name|prefixes
init|=
name|syms
operator|.
name|defaultPrefixList
argument_list|()
decl_stmt|;
name|namespaces
operator|=
operator|new
name|HashMap
argument_list|(
name|prefixes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|namespaces
operator|.
name|put
argument_list|(
name|prefixes
index|[
name|i
index|]
argument_list|,
name|syms
operator|.
name|getDefaultNamespace
argument_list|(
name|prefixes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// default namespaces
name|declareNamespace
argument_list|(
literal|"xml"
argument_list|,
name|XML_NS
argument_list|)
expr_stmt|;
name|declareNamespace
argument_list|(
literal|"xs"
argument_list|,
name|SCHEMA_NS
argument_list|)
expr_stmt|;
name|declareNamespace
argument_list|(
literal|"xdt"
argument_list|,
name|XPATH_DATATYPES_NS
argument_list|)
expr_stmt|;
name|declareNamespace
argument_list|(
literal|"local"
argument_list|,
name|XQUERY_LOCAL_NS
argument_list|)
expr_stmt|;
name|declareNamespace
argument_list|(
literal|"fn"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
expr_stmt|;
name|declareNamespace
argument_list|(
literal|"util"
argument_list|,
name|Function
operator|.
name|UTIL_FUNCTION_NS
argument_list|)
expr_stmt|;
name|declareNamespace
argument_list|(
literal|"xmldb"
argument_list|,
name|Function
operator|.
name|XMLDB_FUNCTION_NS
argument_list|)
expr_stmt|;
name|declareNamespace
argument_list|(
literal|"request"
argument_list|,
name|Function
operator|.
name|REQUEST_FUNCTION_NS
argument_list|)
expr_stmt|;
name|builtinFunctions
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|SystemFunctions
operator|.
name|internalFunctions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|Class
name|fclass
init|=
name|lookup
argument_list|(
name|SystemFunctions
operator|.
name|internalFunctions
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|Field
name|field
init|=
name|fclass
operator|.
name|getDeclaredField
argument_list|(
literal|"signature"
argument_list|)
decl_stmt|;
name|FunctionSignature
name|signature
init|=
operator|(
name|FunctionSignature
operator|)
name|field
operator|.
name|get
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|QName
name|name
init|=
name|signature
operator|.
name|getName
argument_list|()
decl_stmt|;
name|builtinFunctions
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|fclass
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"no instance found for "
operator|+
name|SystemFunctions
operator|.
name|internalFunctions
index|[
name|i
index|]
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
name|Class
name|lookup
parameter_list|(
name|String
name|clazzName
parameter_list|)
throws|throws
name|ClassNotFoundException
block|{
return|return
name|Class
operator|.
name|forName
argument_list|(
name|clazzName
argument_list|)
return|;
block|}
block|}
end_class

end_unit

