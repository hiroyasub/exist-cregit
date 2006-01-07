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

begin_comment
comment|/**  * Defines an XQuery library module. A module consists of function definitions  * and global variables. It is uniquely identified by a namespace URI and an optional  * default namespace prefix. All functions provided by the module have to be defined   * in the module's namespace.  *   * Modules can be either internal or external: internal modules are collections of Java  * classes, each being a subclass of {@link org.exist.xquery.Function}. External modules  * are defined by the XQuery "module" directive and can be loaded with "import module".  *   * Modules are dynamically loaded by class {@link org.exist.xquery.XQueryContext}, either  * during the initialization phase of the query engine (for the standard library modules) or  * upon an "import module" directive.   *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_interface
specifier|public
interface|interface
name|Module
block|{
comment|/** 	 * Namespace for the built-in xmldb module. 	 */
specifier|public
specifier|final
specifier|static
name|String
name|XMLDB_FUNCTION_NS
init|=
literal|"http://exist-db.org/xquery/xmldb"
decl_stmt|;
comment|/** 	 * Namespace for the built-in utility module. 	 */
specifier|public
specifier|final
specifier|static
name|String
name|UTIL_FUNCTION_NS
init|=
literal|"http://exist-db.org/xquery/util"
decl_stmt|;
comment|/** 	 * Namespace for the built-in request module. 	 */
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_FUNCTION_NS
init|=
literal|"http://exist-db.org/xquery/request"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TRANSFORM_FUNCTION_NS
init|=
literal|"http://exist-db.org/xquery/transform"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|TEXT_FUNCTION_NS
init|=
literal|"http://exist-db.org/xquery/text"
decl_stmt|;
comment|/** 	 * Returns the namespace URI that uniquely identifies this module. 	 *  	 * @return 	 */
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
function_decl|;
comment|/** 	 * Returns an optional default prefix (used if no prefix is supplied with 	 * the "import module" directive). 	 *  	 * @return 	 */
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
function_decl|;
comment|/** 	 * Return a short description of this module to be displayed to a user. 	 *  	 * @return 	 */
specifier|public
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/** 	 * Is this an internal module? 	 *  	 * @return 	 */
specifier|public
name|boolean
name|isInternalModule
parameter_list|()
function_decl|;
comment|/** 	 * Returns the signatures of all functions defined within this module. 	 *  	 * @return 	 */
specifier|public
name|FunctionSignature
index|[]
name|listFunctions
parameter_list|()
function_decl|;
comment|/** 	 * Try to find the signature of the function identified by its QName. 	 *  	 * @param qname 	 * @return the function signature or null if the function is not defined. 	 */
specifier|public
name|Iterator
name|getSignaturesForFunction
parameter_list|(
name|QName
name|qname
parameter_list|)
function_decl|;
specifier|public
name|Variable
name|resolveVariable
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
function_decl|;
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
function_decl|;
specifier|public
name|Variable
name|declareVariable
parameter_list|(
name|Variable
name|var
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isVarDeclared
parameter_list|(
name|QName
name|qname
parameter_list|)
function_decl|;
comment|/** 	 * Reset the module's internal state for being reused. 	 * 	 */
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

