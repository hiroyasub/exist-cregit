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
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * Execute XQuery expressions on the database.  *   * This interface is similar to {@link org.xmldb.api.modules.XPathQueryService}, but  * provides additional methods to compile an XQuery into an internal representation, which  * can be executed repeatedly. Since XQuery scripts can be very large, compiling an expression  * in advance can save a lot of time.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|XQueryService
extends|extends
name|Service
block|{
comment|/** 	 * Execute the specified query and return the results as a {@link ResourceSet}. 	 *  	 * @param query 	 * @return 	 * @throws XMLDBException 	 */
specifier|public
name|ResourceSet
name|query
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Compiles the specified XQuery and returns a handle to the compiled 	 * code, which can then be passed to {@link #execute(CompiledExpression)}. 	 *   	 * @param query 	 * @return 	 * @throws XMLDBException 	 */
specifier|public
name|CompiledExpression
name|compile
parameter_list|(
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Execute a compiled XQuery. 	 *  	 * The implementation should pass all namespaces and variables declared through 	 * {@link XQueryService} to the compiled XQuery code. 	 *  	 * @param expression 	 * @return 	 * @throws XMLDBException 	 */
specifier|public
name|ResourceSet
name|execute
parameter_list|(
name|CompiledExpression
name|expression
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	* Returns the URI string associated with<code>prefix</code> from 	* the internal namespace map. If<code>prefix</code> is null or empty the 	* URI for the default namespace will be returned. If a mapping for the 	*<code>prefix</code> can not be found null is returned. 	* 	* @param prefix The prefix to retrieve from the namespace map. 	* @return The URI associated with<code>prefix</code> 	* @exception XMLDBException with expected error codes.<br /> 	*<code>ErrorCodes.VENDOR_ERROR</code> for any vendor 	*  specific errors that occur.<br /> 	*/
specifier|public
name|String
name|getNamespace
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	* Sets a namespace mapping in the internal namespace map used to evaluate 	* queries. If<code>prefix</code> is null or empty the default namespace is 	* associated with the provided URI. A null or empty<code>uri</code> results 	* in an exception being thrown. 	* 	* @param prefix The prefix to set in the map. If 	*<code>prefix</code> is empty or null the 	*  default namespace will be associated with the provided URI. 	* @param uri The URI for the namespace to be associated with prefix. 	* @exception XMLDBException with expected error codes.<br /> 	*<code>ErrorCodes.VENDOR_ERROR</code> for any vendor 	*  specific errors that occur.<br /> 	*  TODO: probably need some special error here. 	*/
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
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	* Removes the namespace mapping associated with<code>prefix</code> from 	* the internal namespace map. If<code>prefix</code> is null or empty the 	* mapping for the default namespace will be removed. 	* 	* @param prefix The prefix to remove from the namespace map. If 	*<code>prefix</code> is null or empty the mapping for the default 	*  namespace will be removed. 	* @exception XMLDBException with expected error codes.<br /> 	*<code>ErrorCodes.VENDOR_ERROR</code> for any vendor 	*  specific errors that occur.<br /> 	*/
specifier|public
name|void
name|removeNamespace
parameter_list|(
name|String
name|ns
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Declare a global, external XQuery variable and assign a value to it. The variable 	 * has the same status as a variable declare through the<code>declare variable</code> 	 * statement in the XQuery prolog. 	 *  	 * The variable can be referenced inside the XQuery expression as 	 *<code>$variable</code>. For example, if you declare a variable with 	 *  	 *<pre> 	 * 	declareVariable("name", "HAMLET"); 	 *</pre> 	 *  	 * you may use the variable in an XQuery expression as follows: 	 *  	 *<pre> 	 * 	//SPEECH[SPEAKER=$name] 	 *</pre> 	 *  	 * Any Java object may be passed as initial value. The implementation will try 	 * to map the Java object into a corresponding XQuery value. In particular, all 	 * basic Java types (Integer, Long, Double, String) should be mapped into the corresponding XML  	 * Schema atomic types. A Java array is mapped to an XQuery sequence. The implemenation 	 * should also recognize all DOM node types. 	 *  	 * As a special case, an XMLResource as obtained from a {@link ResourceSet} will be  	 * converted into a node. 	 *   	 * @param qname a valid QName by which the variable is identified. Any 	 * prefix should have been mapped to a namespace, using {@link #setNamespace(String, String)}. 	 * For example, if a variable is called<b>x:name</b>, a prefix/namespace mapping should have 	 * been defined for prefix<code>x</code> before calling this method. 	 *  	 * @param initialValue the initial value, which is assigned to the variable 	 *  	 * @throws XMLDBException 	 */
specifier|public
name|void
name|declareVariable
parameter_list|(
name|String
name|qname
parameter_list|,
name|Object
name|initialValue
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Enable or disable XPath 1.0 compatibility mode. In XPath 1.0 	 * compatibility mode, some XQuery expressions will behave different. 	 * In particular, additional automatic type conversions will be applied 	 * to the operands of numeric operators. 	 *  	 * @param backwardsCompatible 	 */
specifier|public
name|void
name|setXPathCompatibility
parameter_list|(
name|boolean
name|backwardsCompatible
parameter_list|)
function_decl|;
specifier|public
name|void
name|setModuleLoadPath
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

