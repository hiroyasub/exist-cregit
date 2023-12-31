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
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * An external library module implemented in XQuery and loaded  * through the "import module" directive.  *   * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExternalModule
extends|extends
name|Module
block|{
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
function_decl|;
specifier|public
name|void
name|setDescription
parameter_list|(
name|String
name|desc
parameter_list|)
function_decl|;
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
function_decl|;
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getMetadata
parameter_list|()
function_decl|;
comment|/**      * Declare a new function. Called by the XQuery compiler      * when parsing a library module for every function declaration.      *       * @param func the function to add      */
specifier|public
name|void
name|declareFunction
parameter_list|(
name|UserDefinedFunction
name|func
parameter_list|)
function_decl|;
comment|/**      * Try to find the function identified by qname. Returns null      * if the function is undefined.      *       * @param qname the name of the function to look for      * @param arity arity of the function to look for      * @param callerContext context of the caller - needed to check if      *                      found function should be visible      * @throws XPathException in case of a dynamic error      * @return the function found      */
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
function_decl|;
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
function_decl|;
comment|/**      * Analyze declared variables. Needs to be called when the module was imported dynamically.      *      * @throws XPathException in case of static errors      */
specifier|public
name|void
name|analyzeGlobalVars
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
name|Collection
argument_list|<
name|VariableDeclaration
argument_list|>
name|getVariableDeclarations
parameter_list|()
function_decl|;
comment|/**      * Get the source object this module has been read from.      *      * This is required for query access control.      * @return The source object this module has been read from.      */
specifier|public
name|Source
name|getSource
parameter_list|()
function_decl|;
comment|/**      * Set the source object this module has been read from.      *       * This is required to check the validity of a compiled expression.      * @param source the source instance      */
specifier|public
name|void
name|setSource
parameter_list|(
name|Source
name|source
parameter_list|)
function_decl|;
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
function_decl|;
comment|/**      * Set the XQueryContext of this module. This will be a sub-context      * of the main context as parts of the static context are shared.       *       * @param context the context to set      */
specifier|public
name|void
name|setContext
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
function_decl|;
comment|/**      * Is this module still valid or should it be reloaded from its source?      *      * @param broker the broker to use for checking      * @return true if module should be reloaded      */
specifier|public
name|boolean
name|moduleIsValid
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
comment|/**      * Returns the root expression associated with this context.      *      * @return  root expression      */
specifier|public
name|Expression
name|getRootExpression
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

