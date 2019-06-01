begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|util
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|fn
operator|.
name|FunOnFunctions
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
name|inspect
operator|.
name|ModuleFunctions
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

begin_comment
comment|/**  * Returns a sequence containing the QNames of all built-in functions  * currently registered in the query engine.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|BuiltinFunctions
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|BuiltinFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"registered-functions"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence containing the QNames of all functions "
operator|+
literal|"declared in the module identified by the specified namespace URI. "
operator|+
literal|"An error is raised if no module is found for the specified URI."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"namespace-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The namespace URI of the function module"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"the sequence of function names"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"registered-functions"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence containing the QNames of all functions "
operator|+
literal|"currently known to the system, including functions in imported and built-in modules."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"the sequence of function names"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"declared-variables"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence containing the QNames of all functions "
operator|+
literal|"declared in the module identified by the specified namespace URI. "
operator|+
literal|"An error is raised if no module is found for the specified URI."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"namespace-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The namespace URI of the function module"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"the sequence of function names"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"list-functions"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence of function items for each function in the current module."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"sequence of function references"
argument_list|)
argument_list|,
name|ModuleFunctions
operator|.
name|FNS_MODULE_FUNCTIONS_CURRENT
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"list-functions"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence of function items for each function in the specified module."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"namespace-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The namespace URI of the function module"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"sequence of function references"
argument_list|)
argument_list|,
name|ModuleFunctions
operator|.
name|FNS_MODULE_FUNCTIONS_OTHER
argument_list|)
block|}
decl_stmt|;
specifier|public
name|BuiltinFunctions
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|ValueSequence
name|resultSeq
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|String
name|uri
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
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
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
literal|"No module found matching namespace URI: "
operator|+
name|uri
argument_list|)
throw|;
block|}
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"declared-variables"
argument_list|)
condition|)
block|{
name|addVariablesFromModule
argument_list|(
name|resultSeq
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"list-functions"
argument_list|)
condition|)
block|{
name|addFunctionRefsFromModule
argument_list|(
name|resultSeq
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addFunctionsFromModule
argument_list|(
name|resultSeq
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"list-functions"
argument_list|)
condition|)
block|{
name|addFunctionRefsFromContext
argument_list|(
name|resultSeq
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|Module
argument_list|>
name|i
init|=
name|context
operator|.
name|getModules
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
name|Module
name|module
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|addFunctionsFromModule
argument_list|(
name|resultSeq
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
comment|// Add all functions declared in the local module
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|UserDefinedFunction
argument_list|>
name|i
init|=
name|context
operator|.
name|localFunctions
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
name|UserDefinedFunction
name|func
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|FunctionSignature
name|sig
init|=
name|func
operator|.
name|getSignature
argument_list|()
decl_stmt|;
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|sig
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|resultSeq
return|;
block|}
specifier|private
name|void
name|addFunctionsFromModule
parameter_list|(
name|ValueSequence
name|resultSeq
parameter_list|,
name|Module
name|module
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|QName
argument_list|>
name|set
init|=
operator|new
name|TreeSet
argument_list|<
name|QName
argument_list|>
argument_list|()
decl_stmt|;
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
comment|// add to set to remove duplicate QName's
for|for
control|(
name|FunctionSignature
name|signature
range|:
name|signatures
control|)
block|{
specifier|final
name|QName
name|qname
init|=
name|signature
operator|.
name|getName
argument_list|()
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|QName
name|qname
range|:
name|set
control|)
block|{
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|qname
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|addFunctionRefsFromModule
parameter_list|(
name|ValueSequence
name|resultSeq
parameter_list|,
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
specifier|final
name|FunctionCall
name|call
init|=
name|FunOnFunctions
operator|.
name|lookupFunction
argument_list|(
name|this
argument_list|,
name|signature
operator|.
name|getName
argument_list|()
argument_list|,
name|signature
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
condition|)
block|{
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|FunctionReference
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addFunctionRefsFromContext
parameter_list|(
name|ValueSequence
name|resultSeq
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|UserDefinedFunction
argument_list|>
name|i
init|=
name|context
operator|.
name|localFunctions
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
name|UserDefinedFunction
name|f
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|FunctionCall
name|call
init|=
name|FunOnFunctions
operator|.
name|lookupFunction
argument_list|(
name|this
argument_list|,
name|f
operator|.
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|f
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
condition|)
block|{
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|FunctionReference
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addVariablesFromModule
parameter_list|(
name|ValueSequence
name|resultSeq
parameter_list|,
name|Module
name|module
parameter_list|)
block|{
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
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|i
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
