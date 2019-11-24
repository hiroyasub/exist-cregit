begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|xslfo
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|BrokerPool
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
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistEmbeddedServer
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
name|XPathException
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
name|XQuery
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
name|Item
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
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|ApacheFopTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|server
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|simplePdf
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|XPathException
block|{
specifier|final
name|String
name|fopConfig
init|=
literal|"<fop version=\"1.0\">\n"
operator|+
literal|"<strict-configuration>true</strict-configuration>\n"
operator|+
literal|"<strict-validation>false</strict-validation>\n"
operator|+
literal|"<base>./</base>\n"
operator|+
literal|"<renderers>\n"
operator|+
literal|"<renderer mime=\"application/pdf\"></renderer>\n"
operator|+
literal|"</renderers>\n"
operator|+
literal|"</fop>"
decl_stmt|;
specifier|final
name|String
name|fo
init|=
literal|"<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">\n"
operator|+
literal|"<fo:layout-master-set>\n"
operator|+
literal|"<fo:simple-page-master master-name=\"page-left\" page-height=\"297mm\" page-width=\"210mm\" margin-bottom=\"10mm\" margin-top=\"10mm\" margin-left=\"36mm\" margin-right=\"18mm\">\n"
operator|+
literal|"<fo:region-body margin-bottom=\"10mm\" margin-top=\"16mm\"/>\n"
operator|+
literal|"<fo:region-before region-name=\"head-left\" extent=\"10mm\"/>\n"
operator|+
literal|"</fo:simple-page-master>\n"
operator|+
literal|"<fo:simple-page-master master-name=\"page-right\" page-height=\"297mm\" page-width=\"210mm\" margin-bottom=\"10mm\" margin-top=\"10mm\" margin-left=\"18mm\" margin-right=\"36mm\">\n"
operator|+
literal|"<fo:region-body margin-bottom=\"10mm\" margin-top=\"16mm\"/>\n"
operator|+
literal|"<fo:region-before region-name=\"head-right\" extent=\"10mm\"/>\n"
operator|+
literal|"</fo:simple-page-master>\n"
operator|+
literal|"<fo:page-sequence-master master-name=\"page-content\">\n"
operator|+
literal|"<fo:repeatable-page-master-alternatives>\n"
operator|+
literal|"<fo:conditional-page-master-reference master-reference=\"page-right\" odd-or-even=\"odd\"/>\n"
operator|+
literal|"<fo:conditional-page-master-reference master-reference=\"page-left\" odd-or-even=\"even\"/>\n"
operator|+
literal|"</fo:repeatable-page-master-alternatives>\n"
operator|+
literal|"</fo:page-sequence-master>\n"
operator|+
literal|"</fo:layout-master-set>\n"
operator|+
literal|"<fo:page-sequence master-reference=\"page-content\">\n"
operator|+
literal|"<fo:flow flow-name=\"xsl-region-body\" hyphenate=\"true\" language=\"en\" xml:lang=\"en\">\n"
operator|+
literal|"<fo:block id=\"A97060-t\" line-height=\"16pt\" font-size=\"11pt\">\n"
operator|+
literal|"<fo:block id=\"A97060-e0\" page-break-after=\"right\">\n"
operator|+
literal|"<fo:block id=\"A97060-e100\" text-align=\"justify\" space-before=\".5em\" text-indent=\"1.5em\" space-after=\".5em\">\n"
operator|+
literal|"                            Hello World!\n"
operator|+
literal|"</fo:block>\n"
operator|+
literal|"</fo:block>\n"
operator|+
literal|"</fo:block>\n"
operator|+
literal|"</fo:flow>\n"
operator|+
literal|"</fo:page-sequence>\n"
operator|+
literal|"</fo:root>"
decl_stmt|;
specifier|final
name|String
name|xquery
init|=
literal|"xquery version \"3.1\";\n"
operator|+
literal|"\n"
operator|+
literal|"import module namespace xslfo=\"http://exist-db.org/xquery/xslfo\";\n"
operator|+
literal|"\n"
operator|+
literal|"let $config := "
operator|+
name|fopConfig
operator|+
literal|"\n"
operator|+
literal|"let $fo := "
operator|+
name|fo
operator|+
literal|"\n"
operator|+
literal|"\n"
operator|+
literal|"let $pdf := xslfo:render($fo, \"application/pdf\", (), $config)\n"
operator|+
literal|"return $pdf"
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|server
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|getBroker
argument_list|()
init|)
block|{
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|xquery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Item
name|pdf
init|=
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|pdf
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

