begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|DatabaseManager
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
name|*
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|DeepEqualTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
name|XmldbURI
operator|.
name|LOCAL_DB
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
name|XPathQueryService
name|query
decl_stmt|;
specifier|private
name|Collection
name|c
decl_stmt|;
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
name|TestRunner
operator|.
name|run
argument_list|(
name|DeepEqualTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DeepEqualTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAtomic1
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal('hello', 'hello')"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAtomic2
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal('hello', 'goodbye')"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAtomic3
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(42, 42)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAtomic4
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(42, 17)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAtomic5
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(42, 'hello')"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAtomic6
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal( 1. , xs:integer(1) )"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal( xs:double(1) , xs:integer(1) )"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testEmptySeq
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal((), ())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDiffLengthSeq1
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal((), 42)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDiffLengthSeq2
parameter_list|()
block|{
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal((), (42, 'hello'))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDiffKindNodes1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test, /test/@key)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDiffKindNodes2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test, /test/text())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDiffKindNodes3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/@key, /test/text())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSameNode1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test, /test)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSameNode2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/@key, /test/@key)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSameNode3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/text(), /test/text())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDocuments1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test1"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|createDocument
argument_list|(
literal|"test2"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(xmldb:document('test1'), xmldb:document('test2'))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDocuments2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test1"
argument_list|,
literal|"<test key='value'>hello</test>"
argument_list|)
expr_stmt|;
name|createDocument
argument_list|(
literal|"test2"
argument_list|,
literal|"<notatest/>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(xmldb:document('test1'), xmldb:document('test2'))"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testText1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><g1><a>1</a><b>2</b></g1><g2><c>1</c><d>2</d></g2></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(//a/text(), //c/text())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testText2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><g1><a>1</a><b>2</b></g1><g2><c>1</c><d>2</d></g2></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(//a/text(), //b/text())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testText3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><g1><a>1</a><b>2</b></g1><g2><c>1</c><d>2</d></g2></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(//g1/text(), //g2/text())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testText4
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a>12</a><b>1<!--blah-->2</b></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(//a/text(), //b/text())"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAttributes1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><e1 a='1'/><e2 a='1' b='2' c='1'/><e3 a='2'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(//e1/@a, //e2/@a)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAttributes2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><e1 a='1'/><e2 a='1' b='2' c='1'/><e3 a='2'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(//e1/@a, //e2/@b)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAttributes3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><e1 a='1'/><e2 a='1' b='2' c='1'/><e3 a='2'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(//e1/@a, //e2/@c)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAttributes4
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><e1 a='1'/><e2 a='1' b='2' c='1'/><e3 a='2'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(//e1/@a, //e3/@a)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNSAttributes1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test xmlns:n='urn:blah' xmlns:p='urn:foo' xmlns:q='urn:blah'><e1 n:a='1'/><e2 n:a='1' p:a='1' p:b='1'/><e3 n:a='2'/><e4 q:a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"declare namespace n = 'urn:blah'; declare namespace p = 'urn:foo'; declare namespace q = 'urn:blah'; deep-equal(//e1/@n:a, //e2/@n:a)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNSAttributes2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test xmlns:n='urn:blah' xmlns:p='urn:foo' xmlns:q='urn:blah'><e1 n:a='1'/><e2 n:a='1' p:a='1' p:b='1'/><e3 n:a='2'/><e4 q:a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"declare namespace n = 'urn:blah'; declare namespace p = 'urn:foo'; declare namespace q = 'urn:blah'; deep-equal(//e1/@q:a, //e4/@n:a)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNSAttributes3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test xmlns:n='urn:blah' xmlns:p='urn:foo' xmlns:q='urn:blah'><e1 n:a='1'/><e2 n:a='1' p:a='1' p:b='1'/><e3 n:a='2'/><e4 q:a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"declare namespace n = 'urn:blah'; declare namespace p = 'urn:foo'; declare namespace q = 'urn:blah'; deep-equal(//e1/@n:a, //e2/@p:a)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNSAttributes4
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test xmlns:n='urn:blah' xmlns:p='urn:foo' xmlns:q='urn:blah'><e1 n:a='1'/><e2 n:a='1' p:a='1' p:b='1'/><e3 n:a='2'/><e4 q:a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"declare namespace n = 'urn:blah'; declare namespace p = 'urn:foo'; declare namespace q = 'urn:blah'; deep-equal(//e1/@n:a, //e2/@p:b)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNSAttributes5
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test xmlns:n='urn:blah' xmlns:p='urn:foo' xmlns:q='urn:blah'><e1 n:a='1'/><e2 n:a='1' p:a='1' p:b='1'/><e3 n:a='2'/><e4 q:a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"declare namespace n = 'urn:blah'; declare namespace p = 'urn:foo'; declare namespace q = 'urn:blah'; deep-equal(//e1/@n:a, //e3/@n:a)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a/><a/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a/><b/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a a='1' b='2'/><a b='2' a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements4
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a a='1'/><a b='2' a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements5
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a a='1' c='2'/><a b='2' a='1'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements6
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a a='1' b='2'/><a a='2' b='2'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements7
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a>hello</a><a>hello</a></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements8
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a>hello</a><a>bye</a></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements9
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a><!--blah--></a><a/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements10
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a><b/><!--blah-->hello</a><a><b/>hello</a></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements11
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a><b/>hello</a><a>hello</a></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements12
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a><b/></a><a>hello</a></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements13
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a><b/></a><a><b/>hello</a></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
comment|//Courtesy : Dizzz
specifier|public
name|void
name|testElements14
parameter_list|()
block|{
comment|//Includes a reference node
name|String
name|query
init|=
literal|"let $parSpecs1 :=<ParameterSpecifications/> "
operator|+
literal|"let $funSpecs2 := "
operator|+
literal|"<FunctionSpecifications>"
operator|+
literal|"<FunctionName>Func2</FunctionName>"
operator|+
literal|"  { $parSpecs1 }"
operator|+
literal|"</FunctionSpecifications>"
operator|+
literal|"return "
operator|+
literal|" deep-equal("
operator|+
literal|"<FunctionVerifications>"
operator|+
literal|"<FunctionName>Func2</FunctionName>"
operator|+
literal|"</FunctionVerifications>"
operator|+
literal|","
operator|+
literal|"<FunctionVerifications>"
operator|+
literal|"   { $funSpecs2/FunctionName }"
operator|+
literal|"</FunctionVerifications>"
operator|+
literal|" )"
decl_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements15
parameter_list|()
block|{
name|String
name|query
init|=
literal|"let $funSpecs :="
operator|+
literal|"<FunctionSpecifications>"
operator|+
literal|"<FunctionName>Func2</FunctionName>"
operator|+
literal|"</FunctionSpecifications>"
operator|+
literal|"let $funVers1 :="
operator|+
literal|"<FunctionVerifications>"
operator|+
literal|"<FunctionName>Func2</FunctionName>"
operator|+
literal|"</FunctionVerifications>"
operator|+
literal|"let $funVers2 :="
operator|+
literal|"<FunctionVerifications>"
operator|+
literal|"{$funSpecs/FunctionName}"
operator|+
literal|"</FunctionVerifications>"
operator|+
literal|"return "
operator|+
literal|"deep-equal($funVers1, $funVers2)"
decl_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements16
parameter_list|()
block|{
comment|// [ 1462061 ] Issue with deep-equal() "DeepestEqualBug"
name|String
name|query
init|=
literal|"declare namespace ve = \"ournamespace\";"
operator|+
literal|"declare function ve:functionVerifications($pars as element()*) as element() {"
operator|+
literal|"<FunctionVerifications>"
operator|+
literal|"<ParameterVerifications>{$pars[Name eq \"Par1\"]}</ParameterVerifications>"
operator|+
literal|"</FunctionVerifications>"
operator|+
literal|"};"
operator|+
literal|"let $par1 :=<Parameter><Name>Par1</Name></Parameter>"
operator|+
literal|"let $funVers2 := "
operator|+
literal|"<FunctionVerifications><ParameterVerifications> {$par1}"
operator|+
literal|"</ParameterVerifications></FunctionVerifications> "
operator|+
literal|"return "
operator|+
literal|"deep-equal($funVers2, ve:functionVerifications($par1))"
decl_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testElements17
parameter_list|()
block|{
comment|// Test deep-equal is used with in-memory nodes
name|String
name|query
init|=
literal|"let $one :=<foo/> "
operator|+
literal|"let $two :=<bar/> "
operator|+
literal|"return "
operator|+
literal|"deep-equal($one, $two)"
decl_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReferenceNode
parameter_list|()
block|{
name|String
name|query
init|=
literal|"let $expr1 :=<Value>Hello</Value> "
operator|+
literal|"return "
operator|+
literal|"deep-equal(<Result><Value>Hello</Value></Result>,"
operator|+
literal|"<Result><Value>{$expr1/node()}</Value></Result> )"
decl_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReferenceNode2
parameter_list|()
block|{
name|String
name|query
init|=
literal|"declare namespace dst = \"http://www.test.com/DeeperEqualTest\"; "
operator|+
literal|"declare function dst:value($value as element(Value), "
operator|+
literal|"$result as element(Result)) as element(Result) { "
operator|+
literal|"<Result><Value>{($result/Value/node(), $value/node())}</Value></Result>}; "
operator|+
literal|"let $value1 :=<Value>hello</Value> "
operator|+
literal|"let $result0 :=<Result><Value/></Result> "
operator|+
literal|"let $result1 := dst:value($value1, $result0) "
operator|+
literal|"let $value2 :=<Value/> "
operator|+
literal|"let $result2 := dst:value($value2, $result1) "
operator|+
literal|"return deep-equal($result1, $result2)"
decl_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testReferenceNode3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<root><value>A</value><value>B</value></root>"
argument_list|)
expr_stmt|;
comment|// two adjacent reference text nodes from another document should be merged into one
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"let $a :=<v>{/root/value[1]/node(), /root/value[2]/node()}</v>"
operator|+
literal|"let $b :=<v>AB</v>"
operator|+
literal|"return deep-equal($a, $b)"
argument_list|)
expr_stmt|;
comment|// one reference node after a text node
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"let $a :=<v>{/root/value[1]/node(), /root/value[2]/node()}</v>"
operator|+
literal|"let $b :=<v>A{/root/value[2]/node()}</v>"
operator|+
literal|"return deep-equal($a, $b)"
argument_list|)
expr_stmt|;
comment|// reference node before a text node
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"let $a :=<v>{/root/value[1]/node(), /root/value[2]/node()}</v>"
operator|+
literal|"let $b :=<v>{/root/value[1]/node()}B</v>"
operator|+
literal|"return deep-equal($a, $b)"
argument_list|)
expr_stmt|;
comment|// reference node before an atomic value
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"let $a :=<v>{/root/value[1]/node(), 'B'}</v>"
operator|+
literal|"let $b :=<v>AB</v>"
operator|+
literal|"return deep-equal($a, $b)"
argument_list|)
expr_stmt|;
comment|// reference node after an atomic value
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"let $a :=<v>{'A', /root/value[2]/node()}</v>"
operator|+
literal|"let $b :=<v>AB</v>"
operator|+
literal|"return deep-equal($a, $b)"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testsiblingCornerCase
parameter_list|()
block|{
name|String
name|query
init|=
literal|"declare  namespace ve = 'http://www.test.com/deepestEqualError'; "
operator|+
literal|"declare function ve:functionVerifications() as element(FunctionVerifications) { "
operator|+
literal|"let $parVers := "
operator|+
literal|"<ParameterVerifications> "
operator|+
literal|"<Parameter/> "
operator|+
literal|"<PassedLevel>ATP</PassedLevel> "
operator|+
literal|"<PassedLevel>PE</PassedLevel> "
operator|+
literal|"<PassedLevel>SPC</PassedLevel> "
operator|+
literal|"<Specification>ATP</Specification> "
operator|+
literal|"<Specification>PE</Specification> "
operator|+
literal|"<Specification>SPC</Specification> "
operator|+
literal|"</ParameterVerifications> "
operator|+
literal|"let $dummy := $parVers/PassedLevel  (: cause deep-equal bug!!! :) "
operator|+
literal|"return "
operator|+
literal|"<FunctionVerifications> "
operator|+
literal|"<PassedLevel>ATP</PassedLevel> "
operator|+
literal|"<PassedLevel>PE</PassedLevel> "
operator|+
literal|"<PassedLevel>SPC</PassedLevel> "
operator|+
literal|"  {$parVers} "
operator|+
literal|"</FunctionVerifications> "
operator|+
literal|"}; "
operator|+
literal|"let $expected := "
operator|+
literal|"<FunctionVerifications> "
operator|+
literal|"<PassedLevel>ATP</PassedLevel> "
operator|+
literal|"<PassedLevel>PE</PassedLevel> "
operator|+
literal|"<PassedLevel>SPC</PassedLevel> "
operator|+
literal|"<ParameterVerifications> "
operator|+
literal|"<Parameter/> "
operator|+
literal|"<PassedLevel>ATP</PassedLevel> "
operator|+
literal|"<PassedLevel>PE</PassedLevel> "
operator|+
literal|"<PassedLevel>SPC</PassedLevel> "
operator|+
literal|"<Specification>ATP</Specification> "
operator|+
literal|"<Specification>PE</Specification> "
operator|+
literal|"<Specification>SPC</Specification> "
operator|+
literal|"</ParameterVerifications> "
operator|+
literal|"</FunctionVerifications> "
operator|+
literal|"let $got := ve:functionVerifications() "
operator|+
literal|"return deep-equal($expected, $got)"
decl_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSequenceError1
parameter_list|()
block|{
name|String
name|query
init|=
literal|"declare namespace ds = \"http://www.test.com/SequenceError\"; "
operator|+
literal|"declare function ds:result(  $current as element(Result)?, "
operator|+
literal|"$value  as element(Value)?) as element(Result) {"
operator|+
literal|"<Result><Value>{ ($current/Value/node(), $value/node()) }</Value></Result> };"
operator|+
literal|"let $v1 :=<Value>1234</Value> "
operator|+
literal|"let $result1 := ds:result((), $v1) "
operator|+
literal|"let $v2 :=<Value>abcd</Value> "
operator|+
literal|"let $expected :=<Value>{($v1, $v2)/node()}</Value> "
operator|+
literal|"let $result2 := ds:result($result1, $v2) "
operator|+
literal|"return deep-equal($expected, $result2/Value)"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"nr="
operator|+
name|i
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
block|}
empty_stmt|;
specifier|public
name|void
name|testNSElements1
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test xmlns:p='urn:foo' xmlns:q='urn:foo'><p:a/><q:a/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNSElements2
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test xmlns:p='urn:foo' xmlns:q='urn:bar'><p:a/><q:a/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|false
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testNSElements3
parameter_list|()
block|{
name|createDocument
argument_list|(
literal|"test"
argument_list|,
literal|"<test><a/><a xmlns:z='foo'/></test>"
argument_list|)
expr_stmt|;
name|assertQuery
argument_list|(
literal|true
argument_list|,
literal|"deep-equal(/test/*[1], /test/*[2])"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testForLoop
parameter_list|()
block|{
try|try
block|{
name|ResourceSet
name|rs
init|=
name|query
operator|.
name|query
argument_list|(
literal|"let $set :=<root><b>test</b><c><a>test</a></c><d><a>test</a></d></root>, $test :=<c><a>test</a></c> for $node in $set/* return deep-equal($node, $test)"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|rs
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|rs
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|rs
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|rs
operator|.
name|getResource
argument_list|(
literal|2
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|assertQuery
parameter_list|(
name|boolean
name|expected
parameter_list|,
name|String
name|q
parameter_list|)
block|{
try|try
block|{
name|ResourceSet
name|rs
init|=
name|query
operator|.
name|query
argument_list|(
name|q
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rs
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Boolean
operator|.
name|toString
argument_list|(
name|expected
argument_list|)
argument_list|,
name|rs
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|XMLResource
name|createDocument
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|content
parameter_list|)
block|{
try|try
block|{
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|c
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|c
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Collection
name|setupTestCollection
parameter_list|()
block|{
try|try
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|rootcms
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|cc
init|=
name|root
operator|.
name|getChildCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
name|rootcms
operator|.
name|removeCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|rootcms
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|cc
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/test"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|cc
argument_list|)
expr_stmt|;
return|return
name|cc
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
comment|// initialize driver
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|c
operator|=
name|setupTestCollection
argument_list|()
expr_stmt|;
name|query
operator|=
operator|(
name|XPathQueryService
operator|)
name|c
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failed setup"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
name|c
operator|=
literal|null
expr_stmt|;
name|query
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"failed teardown"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

