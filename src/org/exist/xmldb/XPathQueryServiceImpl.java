begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|XMLDBException
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
name|modules
operator|.
name|XMLResource
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
name|modules
operator|.
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  * Extends {@link org.xmldb.api.modules.XPathQueryService} by additional  * methods specific to eXist.  *   * @author Wolfgang<wolfgang@exist-db.org>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|XPathQueryServiceImpl
extends|extends
name|XPathQueryService
block|{
comment|/** 	 * Process an XPath query based on the result of a previous query. 	 * The XMLResource contains the result received from a previous 	 * query. 	 *  	 * @param res an XMLResource as obtained from a previous query. 	 * @param query the XPath query 	 */
specifier|public
name|ResourceSet
name|query
parameter_list|(
name|XMLResource
name|res
parameter_list|,
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Process an XPath query based on the result of a previous query and sort the      * results using the second XPath expression. The XMLResource contains       * the result received from a previous query. 	 *       * @param res an XMLResource as obtained from a previous query      * @param query the XPath query      * @param sortExpr another XPath expression, which is executed relative to      * the results of the primary expression. The result of applying sortExpr is converted      * to a string value, which is then used to sort the results.       * @throws XMLDBException      */
specifier|public
name|ResourceSet
name|query
parameter_list|(
name|XMLResource
name|res
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Process an XPath query and sort the results by applying a second XPath expression      * to each of the search results. The result of applying the sort expression is converted      * into a string, which is then used to sort the set of results.      *       * @param query the XPath query      * @param sortExpr another XPath expression, which is executed relative to the      * results of the primary expression.      * @throws XMLDBException      */
specifier|public
name|ResourceSet
name|query
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Executes a query which is already stored in the database      *       * @param uri The URI of the query in the database      *       * @throws XMLDBException      */
specifier|public
name|ResourceSet
name|executeStoredQuery
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Declare an external XPath variable and assign a value to it.      *       * A variable can be referenced inside an XPath expression as      *<b>$variable</b>. For example, if you declare a variable with      *       *<pre>      * 	declareVariable("name", "HAMLET");      *</pre>      *       * you may use the variable in an XPath expression as follows:      *       *<pre>      * 	//SPEECH[SPEAKER=$name]      *</pre>      *       * Any Java object may be passed as initial value. The query engine will try      * to map this into a corresponding XPath value. You may also pass an       * XMLResource as obtained from another XPath expression. This will be      * converted into a node.      *        * @param qname a valid QName by which the variable is identified. Any      * prefix should have been mapped to a namespace, i.e. if a variable is called      *<b>x:name</b>, there should be a prefix/namespace mapping for the prefix      * x      * @param initialValue the initial value, which is assigned to the variable      *       * @throws XMLDBException      */
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
comment|/** 	 * Execute all following queries in a protected environment. Acquire a write lock      * on all resources in the current collection (i.e. the one from which this service      * was obtained) before executing the query. If a query spans multiple collections,      * call beginProtected on the outer collection which contains all the other      * collections.      * 	 * It is thus guaranteed that documents referenced by the 	 * query or the result set are not modified by other threads 	 * until {@link #endProtected} is called. 	 */
specifier|public
name|void
name|beginProtected
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/** 	 * Close the protected environment. All locks held 	 * by the current thread are released. The query result set 	 * is no longer guaranteed to be stable.      *      * Note: if beginProtected was used, you have to make sure      * endProtected is called in ALL cases. Otherwise some resource      * locks may not be released properly. 	 */
specifier|public
name|void
name|endProtected
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

