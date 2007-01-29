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
name|config
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
literal|"<create qname=\"itemno\" type=\"xs:integer\"/>"
operator|+
comment|//    	"<create-by-qname qname=\"//item/name\" type=\"xs:string\"/>" +
literal|"<create qname=\"name\" type=\"xs:string\"/>"
operator|+
comment|//    	"<create path=\"//item/stock\" type=\"xs:integer\"/>" +
comment|//    	"<create path=\"//item/price\" type=\"xs:double\"/>" +
comment|//    	"<create path=\"//item/price/@specialprice\" type=\"xs:boolean\"/>" +
comment|//    	"<create path=\"//item/x:rating\" type=\"xs:double\"/>" +
literal|"<create qname='@xx:test' type='xs:integer' />"
operator|+
literal|"<create qname='mixed' type='xs:string' />"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|public
name|void
name|testStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|configureCollection
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
name|storeXMLFileAndGetQueryService
argument_list|(
literal|"items.xml"
argument_list|,
literal|"test/src/org/exist/xquery/items.xml"
argument_list|)
decl_stmt|;
comment|// queryResource(service, "items.xml", "//item[name> 'Racing Bicycle']", 4 );
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"util:qname-index-lookup( xs:QName('name'), 'Racing Bicycle' ) / parent::item"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"util:qname-index-lookup( xs:QName('itemno'), 3) / parent::item"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"declare namespace xx='http://test.com'; "
operator|+
literal|"util:qname-index-lookup( xs:QName('xx:test'), 123) "
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//        queryResource(service, "items.xml", "//item[name&= 'Racing Bicycle']", 1);
comment|//        queryResource(service, "items.xml", "//item[mixed = 'uneven']", 1);
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"util:qname-index-lookup( xs:QName('mixed'), 'external' )"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|//		queryResource(service, "items.xml", "//item[fn:matches(mixed, 'un.*')]", 2);
block|}
specifier|protected
name|String
name|getCollectionConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|ValueIndexByQNameTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

