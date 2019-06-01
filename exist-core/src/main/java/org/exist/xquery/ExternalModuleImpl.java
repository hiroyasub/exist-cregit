begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|*
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
name|source
operator|.
name|Source
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
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * Default implementation of an {@link org.exist.xquery.ExternalModule}.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ExternalModuleImpl
implements|implements
name|ExternalModule
block|{
specifier|private
name|String
name|mNamespaceURI
decl_stmt|;
specifier|private
name|String
name|mPrefix
decl_stmt|;
specifier|private
name|String
name|description
init|=
literal|"User Defined Module"
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isReady
init|=
literal|false
decl_stmt|;
specifier|final
specifier|private
name|TreeMap
argument_list|<
name|FunctionId
argument_list|,
name|UserDefinedFunction
argument_list|>
name|mFunctionMap
init|=
operator|new
name|TreeMap
argument_list|<
name|FunctionId
argument_list|,
name|UserDefinedFunction
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
specifier|private
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|VariableDeclaration
argument_list|>
name|mGlobalVariables
init|=
operator|new
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|VariableDeclaration
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
specifier|private
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|mStaticVariables
init|=
operator|new
name|TreeMap
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|Source
name|mSource
init|=
literal|null
decl_stmt|;
specifier|private
name|XQueryContext
name|mContext
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|needsReset
init|=
literal|true
decl_stmt|;
specifier|public
name|ExternalModuleImpl
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|prefix
parameter_list|)
block|{
name|mNamespaceURI
operator|=
name|namespaceURI
expr_stmt|;
name|mPrefix
operator|=
name|prefix
expr_stmt|;
block|}
specifier|public
name|void
name|setNamespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespace
parameter_list|)
block|{
name|this
operator|.
name|mPrefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|mNamespaceURI
operator|=
name|namespace
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContextItem
parameter_list|(
name|Sequence
name|contextItem
parameter_list|)
block|{
name|mContext
operator|.
name|setContextItem
argument_list|(
name|contextItem
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setIsReady
parameter_list|(
name|boolean
name|ready
parameter_list|)
block|{
name|this
operator|.
name|isReady
operator|=
name|ready
expr_stmt|;
block|}
specifier|public
name|boolean
name|isReady
parameter_list|()
block|{
return|return
name|isReady
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#getDescription()      */
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|desc
expr_stmt|;
block|}
specifier|public
name|void
name|addMetadata
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|metadata
operator|==
literal|null
condition|)
block|{
name|metadata
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|old
init|=
name|metadata
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|value
operator|=
name|old
operator|+
literal|", "
operator|+
name|value
expr_stmt|;
block|}
name|metadata
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
block|{
return|return
name|metadata
return|;
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.Module#getReleaseVersion()     */
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
literal|"user-defined"
return|;
block|}
specifier|public
name|UserDefinedFunction
name|getFunction
parameter_list|(
name|QName
name|qname
parameter_list|,
name|int
name|arity
parameter_list|,
name|XQueryContext
name|callerContext
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|FunctionId
name|id
init|=
operator|new
name|FunctionId
argument_list|(
name|qname
argument_list|,
name|arity
argument_list|)
decl_stmt|;
specifier|final
name|UserDefinedFunction
name|fn
init|=
name|mFunctionMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|fn
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|callerContext
operator|!=
name|getContext
argument_list|()
operator|&&
name|fn
operator|.
name|getSignature
argument_list|()
operator|.
name|isPrivate
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0017
argument_list|,
literal|"Calling a private function from outside its module"
argument_list|)
throw|;
block|}
return|return
name|fn
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.ExternalModule#declareFunction(org.exist.xquery.UserDefinedFunction)      */
specifier|public
name|void
name|declareFunction
parameter_list|(
name|UserDefinedFunction
name|func
parameter_list|)
block|{
name|mFunctionMap
operator|.
name|put
argument_list|(
name|func
operator|.
name|getSignature
argument_list|()
operator|.
name|getFunctionId
argument_list|()
argument_list|,
name|func
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#getNamespaceURI()      */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|mNamespaceURI
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#getDefaultPrefix()      */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|mPrefix
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#isInternalModule()      */
specifier|public
name|boolean
name|isInternalModule
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#listFunctions()      */
specifier|public
name|FunctionSignature
index|[]
name|listFunctions
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|FunctionSignature
argument_list|>
name|signatures
init|=
operator|new
name|ArrayList
argument_list|<
name|FunctionSignature
argument_list|>
argument_list|(
name|mFunctionMap
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|UserDefinedFunction
argument_list|>
name|i
init|=
name|mFunctionMap
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
specifier|final
name|FunctionSignature
name|signature
init|=
name|i
operator|.
name|next
argument_list|()
operator|.
name|getSignature
argument_list|()
decl_stmt|;
name|signatures
operator|.
name|add
argument_list|(
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FunctionSignature
index|[]
name|result
init|=
operator|new
name|FunctionSignature
index|[
name|signatures
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
name|signatures
operator|.
name|toArray
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#getSignatureForFunction(org.exist.dom.QName)      */
specifier|public
name|Iterator
argument_list|<
name|FunctionSignature
argument_list|>
name|getSignaturesForFunction
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|FunctionSignature
argument_list|>
name|signatures
init|=
operator|new
name|ArrayList
argument_list|<
name|FunctionSignature
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|UserDefinedFunction
name|func
range|:
name|mFunctionMap
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|func
operator|.
name|getName
argument_list|()
operator|.
name|compareTo
argument_list|(
name|qname
argument_list|)
operator|==
literal|0
condition|)
block|{
name|signatures
operator|.
name|add
argument_list|(
name|func
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|signatures
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|QName
argument_list|>
name|getGlobalVariables
parameter_list|()
block|{
return|return
name|mGlobalVariables
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|VariableDeclaration
argument_list|>
name|getVariableDeclarations
parameter_list|()
block|{
return|return
name|mGlobalVariables
operator|.
name|values
argument_list|()
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#declareVariable(org.exist.dom.QName, java.lang.Object)      */
specifier|public
name|Variable
name|declareVariable
parameter_list|(
name|QName
name|qname
parameter_list|,
name|Object
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|val
init|=
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|value
argument_list|,
name|mContext
argument_list|)
decl_stmt|;
name|Variable
name|var
init|=
name|mStaticVariables
operator|.
name|get
argument_list|(
name|qname
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
operator|new
name|VariableImpl
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|mStaticVariables
operator|.
name|put
argument_list|(
name|qname
argument_list|,
name|var
argument_list|)
expr_stmt|;
block|}
name|var
operator|.
name|setValue
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|var
return|;
block|}
specifier|public
name|Variable
name|declareVariable
parameter_list|(
name|Variable
name|var
parameter_list|)
block|{
name|mStaticVariables
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
return|return
name|var
return|;
block|}
specifier|public
name|void
name|declareVariable
parameter_list|(
name|QName
name|qname
parameter_list|,
name|VariableDeclaration
name|decl
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|decl
argument_list|,
name|ErrorCodes
operator|.
name|XQST0048
argument_list|,
literal|"It is a static error"
operator|+
literal|" if a function or variable declared in a library module is"
operator|+
literal|" not in the target namespace of the library module."
argument_list|)
throw|;
block|}
name|mGlobalVariables
operator|.
name|put
argument_list|(
name|qname
argument_list|,
name|decl
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isVarDeclared
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
if|if
condition|(
name|mGlobalVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|mStaticVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Module#resolveVariable(org.exist.dom.QName)      */
specifier|public
name|Variable
name|resolveVariable
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|VariableDeclaration
name|decl
init|=
name|mGlobalVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
decl_stmt|;
name|Variable
name|var
init|=
name|mStaticVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|isReady
operator|&&
name|decl
operator|!=
literal|null
operator|&&
operator|(
name|var
operator|==
literal|null
operator|||
name|var
operator|.
name|getValue
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
name|decl
operator|.
name|eval
argument_list|(
name|getContext
argument_list|()
operator|.
name|getContextItem
argument_list|()
argument_list|)
expr_stmt|;
name|var
operator|=
name|mStaticVariables
operator|.
name|get
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|var
operator|==
literal|null
condition|)
block|{
comment|// external variable may be defined in root context importing this module
specifier|final
name|Variable
name|rootVar
init|=
name|getContext
argument_list|()
operator|.
name|getRootContext
argument_list|()
operator|.
name|resolveGlobalVariable
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|rootVar
operator|!=
literal|null
condition|)
block|{
name|var
operator|=
name|declareVariable
argument_list|(
name|rootVar
argument_list|)
expr_stmt|;
block|}
block|}
comment|// set sequence type if decl != null (might be null if called by parser before declaration)
if|if
condition|(
name|var
operator|!=
literal|null
operator|&&
name|decl
operator|!=
literal|null
condition|)
block|{
name|var
operator|.
name|setSequenceType
argument_list|(
name|decl
operator|.
name|getSequenceType
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|var
return|;
block|}
specifier|public
name|void
name|analyzeGlobalVars
parameter_list|()
throws|throws
name|XPathException
block|{
for|for
control|(
specifier|final
name|VariableDeclaration
name|decl
range|:
name|mGlobalVariables
operator|.
name|values
argument_list|()
control|)
block|{
name|decl
operator|.
name|analyzeExpression
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Source
name|getSource
parameter_list|()
block|{
return|return
name|mSource
return|;
block|}
specifier|public
name|void
name|setSource
parameter_list|(
name|Source
name|source
parameter_list|)
block|{
name|mSource
operator|=
name|source
expr_stmt|;
block|}
specifier|public
name|void
name|setContext
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|mContext
operator|=
name|context
expr_stmt|;
block|}
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|mContext
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moduleIsValid
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
block|{
return|return
name|mSource
operator|!=
literal|null
operator|&&
name|mSource
operator|.
name|isValid
argument_list|(
name|broker
argument_list|)
operator|==
name|Source
operator|.
name|Validity
operator|.
name|VALID
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
comment|// deprecated, ignore
block|}
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|,
name|boolean
name|keepGlobals
parameter_list|)
block|{
comment|// prevent recursive calls by checking needsReset
if|if
condition|(
name|needsReset
condition|)
block|{
name|needsReset
operator|=
literal|false
expr_stmt|;
name|mContext
operator|.
name|reset
argument_list|(
name|keepGlobals
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|keepGlobals
condition|)
block|{
name|mStaticVariables
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|// reset state of variable declarations
name|mGlobalVariables
operator|.
name|values
argument_list|()
operator|.
name|forEach
argument_list|(
name|v
lambda|->
name|v
operator|.
name|resetState
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|needsReset
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|private
name|Expression
name|rootExpression
init|=
literal|null
decl_stmt|;
comment|/**      * Set the root expression for this context.      *      * @param  expr      */
specifier|protected
name|void
name|setRootExpression
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|rootExpression
operator|=
name|expr
expr_stmt|;
block|}
comment|/**      * Returns the root expression associated with this context.      *      * @return  root expression      */
specifier|public
name|Expression
name|getRootExpression
parameter_list|()
block|{
return|return
name|rootExpression
return|;
block|}
block|}
end_class

end_unit
