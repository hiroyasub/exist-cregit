begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Created on 30 mai 2005 $Id$ */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|test
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
name|Resource
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
name|modules
operator|.
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  * @author Jean-Marc Vanel http://jmvanel.free.fr/  */
end_comment

begin_class
specifier|public
class|class
name|ValueIndexByQNameTest
extends|extends
name|ValueIndexTest
block|{
specifier|private
name|String
name|CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index xmlns:x=\"http://www.foo.com\" xmlns:xx=\"http://test.com\">"
operator|+
literal|"<fulltext default=\"none\">"
operator|+
literal|"<include path=\"//item/name\"/>"
operator|+
literal|"<include path=\"//item/mixed\"/>"
operator|+
literal|"</fulltext>"
operator|+
comment|//    	"<create path=\"//item/itemno\" type=\"xs:integer\"/>" +
comment|//    	"<create-by-qname qname=\"//item/name\" type=\"xs:string\"/>" +
literal|"<create qname=\"name\" type=\"xs:string\"/>"
operator|+
comment|//    	"<create path=\"//item/stock\" type=\"xs:integer\"/>" +
comment|//    	"<create path=\"//item/price\" type=\"xs:double\"/>" +
comment|//    	"<create path=\"//item/price/@specialprice\" type=\"xs:boolean\"/>" +
comment|//    	"<create path=\"//item/x:rating\" type=\"xs:double\"/>" +
comment|//    	"<create path=\"//item/@xx:test\" type=\"xs:integer\"/>" +
comment|//    	"<create path=\"//item/mixed\" type=\"xs:string\"/>" +
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
comment|/** ? @see org.exist.xquery.test.ValueIndexTest#testStrings() 	 */
specifier|public
name|void
name|testStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCollection
argument_list|()
expr_stmt|;
name|XPathQueryService
name|service
init|=
name|storeXMLFileAndGetQueryService
argument_list|(
literal|"items.xml"
argument_list|,
literal|"src/org/exist/xquery/test/items.xml"
argument_list|)
decl_stmt|;
comment|// queryResource(service, "items.xml", "//item[name = 'Racing Bicycle']", 1);
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"util:qname-index-lookup( xs:QName('name'), 'Racing Bicycle' ) "
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// "util:qname-index-lookup( xs:QName('name'), 'Racing Bicycle' ) / parent::item" , 1 );
comment|//        queryResource(service, "items.xml", "//item[name> 'Racing Bicycle']", 4);
comment|//        queryResource(service, "items.xml", "//item[itemno = 3]", 1);
comment|//        ResourceSet result = queryResource(service, "items.xml", "for $i in //item[stock<= 10] return $i/itemno", 5);
comment|//        for (long i = 0; i< result.getSize(); i++) {
comment|//            Resource res = result.getResource(i);
comment|//            System.out.println(res.getContent());
comment|//        }
comment|//
comment|//        queryResource(service, "items.xml", "//item[stock> 20]", 1);
comment|//        queryResource(service, "items.xml", "declare namespace x=\"http://www.foo.com\"; //item[x:rating> 8.0]", 2);
comment|//        queryResource(service, "items.xml", "declare namespace xx=\"http://test.com\"; //item[@xx:test = 123]", 1);
comment|//        queryResource(service, "items.xml", "//item[name&= 'Racing Bicycle']", 1);
comment|//        queryResource(service, "items.xml", "//item[mixed = 'uneven']", 1);
comment|//		queryResource(service, "items.xml", "//item[mixed = 'external']", 1);
comment|//		queryResource(service, "items.xml", "//item[fn:matches(mixed, 'un.*')]", 2);
block|}
comment|/** ? @see org.exist.xquery.test.ValueIndexTest#getCollectionConfig() 	 */
specifier|protected
name|String
name|getCollectionConfig
parameter_list|()
block|{
return|return
name|CONFIG
return|;
block|}
block|}
end_class

end_unit

