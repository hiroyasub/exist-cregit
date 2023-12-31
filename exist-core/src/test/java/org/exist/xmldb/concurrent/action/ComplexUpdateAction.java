begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
operator|.
name|action
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
name|Collection
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
name|XUpdateQueryService
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ComplexUpdateAction
extends|extends
name|Action
block|{
specifier|private
specifier|static
specifier|final
name|String
name|sessionUpdate
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:update select=\"//USER-SESSION-DATA[1]\">"
operator|+
literal|"<xu:element name=\"USER-SESSION-STATUS\">"
operator|+
literal|"<xu:attribute name=\"access-type\">LAST-RESORT</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"authServer\">10.12.1.10</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"authprotocol\">NONE</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"elapsed-time\">60000</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"ip-addr\">192.168.1.97</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"local-id\"></xu:attribute>"
operator|+
literal|"<xu:attribute name=\"mac-addr\">00:3f:cf:7f:8f:da</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"session-id\">4917-AlphaMX3-(MX8)-Thu Sep 30 19:36:03 PDT 2004</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"session-state\">ACTIVE</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"ssid\">TRPZ-ENG</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"start-time\">1096601394656</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"user-name\">user137</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"vlan-name\">default</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"collected-time\">1096601435484</xu:attribute>"
operator|+
literal|"<USER-LOCATION-MEMBER ap-radio=\"1\" ap-type=\"AP\" dap=\"0\" "
operator|+
literal|"dp-system-ip=\"192.168.12.7\" module=\"1\" port=\"3\" "
operator|+
literal|"start-time=\"1096601358656\"/>"
operator|+
literal|"</xu:element>"
operator|+
literal|"<xu:element name=\"USER-SESSION-STATISTICS\">"
operator|+
literal|"<xu:attribute name=\"op-rate\">48</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"rssi\">-65</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"session-id\">4917-AlphaMX3-(MX8)-Thu Sep 30 19:36:03 PDT 2004</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"snr\">50</xu:attribute>"
operator|+
literal|"<xu:attribute name=\"bps\">4448.6</xu:attribute>"
operator|+
literal|"<USER-SESSION-AP-ACCUM rx-badcrypt-bytes=\"55230\" rx-badcrypt-pkts=\"27576\" "
operator|+
literal|"rx-multi-bytes=\"55231\" rx-multi-pkts=\"27623\" rx-uni-bytes=\"55277\" "
operator|+
literal|"rx-uni-pkts=\"27555\" tx-timeouts=\"27554\" tx-uni-bytes=\"55250\" "
operator|+
literal|"tx-uni-pkts=\"27640\" type=\"CURRENT\"/>"
operator|+
literal|"<USER-SESSION-AP-ACCUM rx-badcrypt-bytes=\"88945\" "
operator|+
literal|"rx-badcrypt-pkts=\"29613\" rx-multi-bytes=\"88953\" "
operator|+
literal|"rx-multi-pkts=\"29614\" rx-uni-bytes=\"88998\" "
operator|+
literal|"rx-uni-pkts=\"29687\" tx-timeouts=\"29615\" "
operator|+
literal|"tx-uni-bytes=\"88966\" tx-uni-pkts=\"29614\" type=\"LIFETIME\"/>"
operator|+
literal|"</xu:element>"
operator|+
literal|"</xu:update>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|statusUpdate
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:update select=\"//USER-SESSION-DATA[1]/USER-SESSION-STATUS/@session-state\">INACTIVE</xu:update>"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
specifier|private
specifier|final
name|int
name|repeat
decl_stmt|;
specifier|public
name|ComplexUpdateAction
parameter_list|(
specifier|final
name|String
name|collectionPath
parameter_list|,
specifier|final
name|String
name|resourceName
parameter_list|,
specifier|final
name|int
name|repeat
parameter_list|)
block|{
name|super
argument_list|(
name|collectionPath
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
name|this
operator|.
name|repeat
operator|=
name|repeat
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|execute
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|col
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionPath
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|repeat
condition|;
name|i
operator|++
control|)
block|{
name|query
argument_list|(
name|col
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|col
operator|.
name|close
argument_list|()
expr_stmt|;
name|update
argument_list|(
name|col
argument_list|,
name|sessionUpdate
argument_list|)
expr_stmt|;
comment|// The following update will fail
specifier|final
name|String
name|versionUpdate
init|=
literal|"<xu:modifications xmlns:xu=\"http://www.xmldb.org/xupdate\" version=\"1.0\">"
operator|+
literal|"<xu:update select=\"//USER-SESSION-DATA[1]/@version\">"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|+
literal|"</xu:update></xu:modifications>"
decl_stmt|;
name|update
argument_list|(
name|col
argument_list|,
name|versionUpdate
argument_list|)
expr_stmt|;
name|update
argument_list|(
name|col
argument_list|,
name|statusUpdate
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|query
parameter_list|(
specifier|final
name|Collection
name|col
parameter_list|,
specifier|final
name|int
name|repeat
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|r
init|=
name|service
operator|.
name|query
argument_list|(
literal|"//USER-SESSION-DATA"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|r
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|r
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
decl_stmt|;
block|}
name|r
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"string(//USER-SESSION-DATA[1]/@version)"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|r
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|repeat
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|r
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|update
parameter_list|(
specifier|final
name|Collection
name|col
parameter_list|,
specifier|final
name|String
name|xupdate
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|long
name|mods
init|=
name|service
operator|.
name|updateResource
argument_list|(
name|resourceName
argument_list|,
name|xupdate
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

