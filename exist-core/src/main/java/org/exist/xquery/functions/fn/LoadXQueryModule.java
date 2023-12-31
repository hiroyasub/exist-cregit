begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|ConsumerE
import|;
end_import

begin_import
import|import
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|IEntry
import|;
end_import

begin_import
import|import
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|IMap
import|;
end_import

begin_import
import|import
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|Maps
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
name|Module
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
name|functions
operator|.
name|map
operator|.
name|AbstractMapType
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
name|functions
operator|.
name|map
operator|.
name|MapType
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
name|XQueryAST
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|map
operator|.
name|MapType
operator|.
name|newLinearMap
import|;
end_import

begin_comment
comment|/**  * Implements fn:load-xquery-module. Creates a temporary context for the imported module, so the  * current XQuery execution context is not polluted.  *  * eXist does currently not support setting external variables in a library module or defining a context  * sequence for variables. The "context-item" and "variables" options are thus ignored.  *  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|LoadXQueryModule
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|LOAD_XQUERY_MODULE_1
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"load-xquery-module"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Provides access to the public functions and global variables of a dynamically-loaded XQuery library module."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"module-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The target namespace of the module"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"a map with two entries: 1) 'variables': a map with one entry for each public global variable declared in "
operator|+
literal|"the library module. The key of the entry is the name of the variable, as an xs:QName value; the "
operator|+
literal|"associated value is the value of the variable; 2) 'functions': a map which contains one "
operator|+
literal|"entry for each public function declared in the library module, except that when two functions have "
operator|+
literal|"the same name (but different arity), they share the same entry. The key of the entry is the name of the "
operator|+
literal|"function(s), as an xs:QName value; the associated value is a map A. This map (A) contains one entry for each "
operator|+
literal|"function with the given name; its key is the arity of the function, as an xs:integer value, and its associated "
operator|+
literal|"value is the function itself, as a function item. The function can be invoked using the rules for dynamic "
operator|+
literal|"function invocation."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|LOAD_XQUERY_MODULE_2
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"load-xquery-module"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Provides access to the public functions and global variables of a dynamically-loaded XQuery library module."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"module-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The target namespace of the module"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"options"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Options for loading the module"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"a map with two entries: 1) 'variables': a map with one entry for each public global variable declared in "
operator|+
literal|"the library module. The key of the entry is the name of the variable, as an xs:QName value; the "
operator|+
literal|"associated value is the value of the variable; 2) 'functions': a map which contains one "
operator|+
literal|"entry for each public function declared in the library module, except that when two functions have "
operator|+
literal|"the same name (but different arity), they share the same entry. The key of the entry is the name of the "
operator|+
literal|"function(s), as an xs:QName value; the associated value is a map A. This map (A) contains one entry for each "
operator|+
literal|"function with the given name; its key is the arity of the function, as an xs:integer value, and its associated "
operator|+
literal|"value is the function itself, as a function item. The function can be invoked using the rules for dynamic "
operator|+
literal|"function invocation."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringValue
name|OPTIONS_LOCATION_HINTS
init|=
operator|new
name|StringValue
argument_list|(
literal|"location-hints"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringValue
name|OPTIONS_XQUERY_VERSION
init|=
operator|new
name|StringValue
argument_list|(
literal|"xquery-version"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringValue
name|OPTIONS_VARIABLES
init|=
operator|new
name|StringValue
argument_list|(
literal|"variables"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringValue
name|OPTIONS_CONTEXT_ITEM
init|=
operator|new
name|StringValue
argument_list|(
literal|"context-item"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringValue
name|OPTIONS_VENDOR
init|=
operator|new
name|StringValue
argument_list|(
literal|"vendor-options"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringValue
name|RESULT_FUNCTIONS
init|=
operator|new
name|StringValue
argument_list|(
literal|"functions"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|StringValue
name|RESULT_VARIABLES
init|=
operator|new
name|StringValue
argument_list|(
literal|"variables"
argument_list|)
decl_stmt|;
specifier|public
name|LoadXQueryModule
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
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
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|targetNamespace
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|targetNamespace
operator|.
name|length
argument_list|()
operator|==
literal|0
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
name|FOQM0001
argument_list|,
literal|"Target namespace must be a string with length> 0"
argument_list|)
throw|;
block|}
name|Sequence
name|locationHints
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|String
name|xqVersion
init|=
name|getXQueryVersion
argument_list|(
name|context
operator|.
name|getXQueryVersion
argument_list|()
argument_list|)
decl_stmt|;
name|AbstractMapType
name|externalVars
init|=
operator|new
name|MapType
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|Sequence
name|contextItem
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
comment|// evaluate options
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
condition|)
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
name|locationHints
operator|=
name|map
operator|.
name|get
argument_list|(
name|OPTIONS_LOCATION_HINTS
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|versions
init|=
name|map
operator|.
name|get
argument_list|(
name|OPTIONS_XQUERY_VERSION
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|versions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|xqVersion
operator|=
name|versions
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Sequence
name|vars
init|=
name|map
operator|.
name|get
argument_list|(
name|OPTIONS_VARIABLES
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|vars
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
name|vars
operator|.
name|hasOne
argument_list|()
operator|&&
name|vars
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|MAP
condition|)
block|{
name|externalVars
operator|=
operator|(
name|AbstractMapType
operator|)
name|vars
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
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
literal|"Option 'variables' must be a map"
argument_list|)
throw|;
block|}
block|}
name|contextItem
operator|=
name|map
operator|.
name|get
argument_list|(
name|OPTIONS_CONTEXT_ITEM
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|.
name|getItemCount
argument_list|()
operator|>
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
name|XPTY0004
argument_list|,
literal|"Option 'context-item' must contain zero or one "
operator|+
literal|"items"
argument_list|)
throw|;
block|}
block|}
comment|// create temporary context so main context is not polluted
specifier|final
name|XQueryContext
name|tempContext
init|=
operator|new
name|XQueryContext
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|context
operator|.
name|getProfiler
argument_list|()
argument_list|)
decl_stmt|;
name|tempContext
operator|.
name|setModuleLoadPath
argument_list|(
name|context
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
expr_stmt|;
name|setExternalVars
argument_list|(
name|externalVars
argument_list|,
name|tempContext
operator|::
name|declareGlobalVariable
argument_list|)
expr_stmt|;
name|tempContext
operator|.
name|prepareForExecution
argument_list|()
expr_stmt|;
name|Module
name|loadedModule
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|locationHints
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// no location hint given, resolve from statically known modules
name|loadedModule
operator|=
name|tempContext
operator|.
name|importModule
argument_list|(
name|targetNamespace
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// try to resolve the module from one of the location hints
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|locationHints
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
specifier|final
name|String
name|location
init|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Module
name|importedModule
init|=
name|tempContext
operator|.
name|importModule
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
name|importedModule
operator|!=
literal|null
operator|&&
name|importedModule
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|targetNamespace
argument_list|)
condition|)
block|{
name|loadedModule
operator|=
name|importedModule
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getErrorCode
argument_list|()
operator|==
name|ErrorCodes
operator|.
name|XQST0059
condition|)
block|{
comment|// importModule may throw exception if no location is given and module cannot be resolved
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOQM0002
argument_list|,
literal|"Module with URI "
operator|+
name|targetNamespace
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOQM0003
argument_list|,
literal|"Error found when importing module: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|// not found, raise error
if|if
condition|(
name|loadedModule
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
name|ErrorCodes
operator|.
name|FOQM0002
argument_list|,
literal|"Module with URI "
operator|+
name|targetNamespace
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|xqVersion
operator|.
name|equals
argument_list|(
name|getXQueryVersion
argument_list|(
name|tempContext
operator|.
name|getXQueryVersion
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOQM0003
argument_list|,
literal|"Imported module has wrong XQuery version: "
operator|+
name|getXQueryVersion
argument_list|(
name|tempContext
operator|.
name|getXQueryVersion
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|Module
name|module
init|=
name|loadedModule
decl_stmt|;
name|module
operator|.
name|setContextItem
argument_list|(
name|contextItem
argument_list|)
expr_stmt|;
name|setExternalVars
argument_list|(
name|externalVars
argument_list|,
name|module
operator|::
name|declareVariable
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
comment|// ensure variable declarations in the imported module are analyzed.
comment|// unlike when using a normal import statement, this is not done automatically
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|analyzeGlobalVars
argument_list|()
expr_stmt|;
block|}
specifier|final
name|ValueSequence
name|functionSeq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|addFunctionRefsFromModule
argument_list|(
name|this
argument_list|,
name|tempContext
argument_list|,
name|functionSeq
argument_list|,
name|module
argument_list|)
expr_stmt|;
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|>
name|functions
init|=
name|newLinearMap
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|functionSeq
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
specifier|final
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|FunctionSignature
name|signature
init|=
name|ref
operator|.
name|getSignature
argument_list|()
decl_stmt|;
specifier|final
name|QNameValue
name|qn
init|=
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|signature
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
init|=
name|functions
operator|.
name|get
argument_list|(
name|qn
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|entry
operator|=
name|newLinearMap
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|functions
operator|.
name|put
argument_list|(
name|qn
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
name|entry
operator|.
name|put
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|signature
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
argument_list|,
name|ref
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|variables
init|=
name|newLinearMap
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|QName
argument_list|>
name|i
init|=
name|module
operator|.
name|getGlobalVariables
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
name|QName
name|name
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Variable
name|var
init|=
name|module
operator|.
name|resolveVariable
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|variables
operator|.
name|put
argument_list|(
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|name
argument_list|)
argument_list|,
name|var
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
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
name|FOQM0005
argument_list|,
literal|"Incorrect type for external variable "
operator|+
name|name
argument_list|)
throw|;
block|}
block|}
specifier|final
name|IMap
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|result
init|=
name|Map
operator|.
name|from
argument_list|(
name|io
operator|.
name|lacuna
operator|.
name|bifurcan
operator|.
name|List
operator|.
name|of
argument_list|(
operator|new
name|Maps
operator|.
name|Entry
argument_list|<>
argument_list|(
name|RESULT_FUNCTIONS
argument_list|,
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
name|functions
operator|.
name|mapValues
argument_list|(
parameter_list|(
name|k
parameter_list|,
name|v
parameter_list|)
lambda|->
operator|(
name|Sequence
operator|)
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
name|v
operator|.
name|forked
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
argument_list|)
operator|.
name|forked
argument_list|()
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|)
argument_list|)
argument_list|,
operator|new
name|Maps
operator|.
name|Entry
argument_list|<>
argument_list|(
name|RESULT_VARIABLES
argument_list|,
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
name|variables
operator|.
name|forked
argument_list|()
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|MapType
argument_list|(
name|context
argument_list|,
name|result
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
specifier|private
name|void
name|setExternalVars
parameter_list|(
specifier|final
name|AbstractMapType
name|externalVars
parameter_list|,
specifier|final
name|ConsumerE
argument_list|<
name|Variable
argument_list|,
name|XPathException
argument_list|>
name|setter
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|IEntry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
range|:
name|externalVars
control|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|entry
operator|.
name|key
argument_list|()
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|QNAME
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
literal|"name of external variable must be a qname: "
operator|+
name|entry
operator|.
name|key
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Variable
name|var
init|=
operator|new
name|VariableImpl
argument_list|(
operator|(
operator|(
name|QNameValue
operator|)
name|entry
operator|.
name|key
argument_list|()
operator|)
operator|.
name|getQName
argument_list|()
argument_list|)
decl_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|entry
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
name|setter
operator|.
name|accept
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|void
name|addFunctionRefsFromModule
parameter_list|(
specifier|final
name|Expression
name|parent
parameter_list|,
specifier|final
name|XQueryContext
name|tempContext
parameter_list|,
specifier|final
name|ValueSequence
name|resultSeq
parameter_list|,
specifier|final
name|Module
name|module
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|FunctionSignature
name|signatures
index|[]
init|=
name|module
operator|.
name|listFunctions
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|FunctionSignature
name|signature
range|:
name|signatures
control|)
block|{
if|if
condition|(
operator|!
name|signature
operator|.
name|isPrivate
argument_list|()
condition|)
block|{
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|int
name|arity
decl_stmt|;
if|if
condition|(
name|signature
operator|.
name|isOverloaded
argument_list|()
condition|)
block|{
name|arity
operator|=
name|signature
operator|.
name|getArgumentTypes
argument_list|()
operator|.
name|length
expr_stmt|;
block|}
else|else
block|{
name|arity
operator|=
name|signature
operator|.
name|getArgumentCount
argument_list|()
expr_stmt|;
block|}
specifier|final
name|FunctionDef
name|def
init|=
operator|(
operator|(
name|InternalModule
operator|)
name|module
operator|)
operator|.
name|getFunctionDef
argument_list|(
name|signature
operator|.
name|getName
argument_list|()
argument_list|,
name|arity
argument_list|)
decl_stmt|;
specifier|final
name|XQueryAST
name|ast
init|=
operator|new
name|XQueryAST
argument_list|()
decl_stmt|;
name|ast
operator|.
name|setLine
argument_list|(
name|parent
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|ast
operator|.
name|setColumn
argument_list|(
name|parent
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|arity
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
name|arity
condition|;
name|i
operator|++
control|)
block|{
name|args
operator|.
name|add
argument_list|(
operator|new
name|Function
operator|.
name|Placeholder
argument_list|(
name|tempContext
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Function
name|fn
init|=
name|Function
operator|.
name|createFunction
argument_list|(
name|tempContext
argument_list|,
name|ast
argument_list|,
name|def
argument_list|)
decl_stmt|;
name|fn
operator|.
name|setArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
specifier|final
name|InternalFunctionCall
name|call
init|=
operator|new
name|InternalFunctionCall
argument_list|(
name|fn
argument_list|)
decl_stmt|;
specifier|final
name|FunctionCall
name|ref
init|=
name|FunctionFactory
operator|.
name|wrap
argument_list|(
name|tempContext
argument_list|,
name|call
argument_list|)
decl_stmt|;
name|resultSeq
operator|.
name|addAll
argument_list|(
operator|new
name|FunctionReference
argument_list|(
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|UserDefinedFunction
name|func
init|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getFunction
argument_list|(
name|signature
operator|.
name|getName
argument_list|()
argument_list|,
name|signature
operator|.
name|getArgumentCount
argument_list|()
argument_list|,
name|tempContext
argument_list|)
decl_stmt|;
comment|// could be null if private function
if|if
condition|(
name|func
operator|!=
literal|null
condition|)
block|{
comment|// create function reference
specifier|final
name|FunctionCall
name|funcCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|tempContext
argument_list|,
name|func
argument_list|)
decl_stmt|;
name|funcCall
operator|.
name|setLocation
argument_list|(
name|parent
operator|.
name|getLine
argument_list|()
argument_list|,
name|parent
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|FunctionReference
argument_list|(
name|funcCall
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|getXQueryVersion
parameter_list|(
specifier|final
name|int
name|version
parameter_list|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|version
operator|/
literal|10
argument_list|)
operator|+
literal|'.'
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|version
operator|%
literal|10
argument_list|)
return|;
block|}
block|}
end_class

end_unit

