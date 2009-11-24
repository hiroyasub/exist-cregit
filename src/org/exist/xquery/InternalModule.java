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
name|List
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

begin_comment
comment|/**  * Defines an internal module implemented in Java. The class maintains a collection of   * Java classes each being a subclass of {@link org.exist.xquery.Function}. For internal  * modules, a new function object is created from its class for each function reference in the  * XQuery script.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|InternalModule
extends|extends
name|Module
block|{
comment|/** 	 * Returns the implementing class for the function identified 	 * by qname or null if it is not defined. Called by 	 * {@link FunctionFactory}. 	 *  	 * @param qname 	 * @return implementing class for the function 	 */
specifier|public
name|FunctionDef
name|getFunctionDef
parameter_list|(
name|QName
name|qname
parameter_list|,
name|int
name|argCount
parameter_list|)
function_decl|;
comment|/** 	 * Returns all functions defined in this module matching the 	 * specified qname. 	 *  	 * @param qname 	 * @return all functions defined in this module 	 */
specifier|public
name|List
argument_list|<
name|FunctionSignature
argument_list|>
name|getFunctionsByName
parameter_list|(
name|QName
name|qname
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

