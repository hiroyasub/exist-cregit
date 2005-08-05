begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|//$Id$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|cocoon
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|ProcessingException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|generation
operator|.
name|AbstractGenerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|ClusterComunication
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|cluster
operator|.
name|ClusterException
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
name|report
operator|.
name|XMLStatistics
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|stack
operator|.
name|IpAddress
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jgroups
operator|.
name|Address
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_comment
comment|/**  * A Cocoon Generator which generates status information about running database instances,  * buffer usage and the like.  * Created by Nicola Breda.  *  * @author Nicola Breda aka maiale  * @author David Frontini aka spider  *         Date: 05-aug-2005  *         Time: 18.09.08  *         Revision $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ClusterInfoGenerator
extends|extends
name|AbstractGenerator
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE
init|=
literal|"http://exist.sourceforge.net/generators/cluster"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"cluster"
decl_stmt|;
name|XMLStatistics
name|stats
decl_stmt|;
specifier|public
name|ClusterInfoGenerator
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.cocoon.generation.Generator#generate()      */
specifier|public
name|void
name|generate
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|this
operator|.
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
name|PREFIX
argument_list|,
name|NAMESPACE
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
literal|"ci"
argument_list|,
literal|"http://apache.org/cocoon/include/1.0"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"page"
argument_list|,
name|PREFIX
operator|+
literal|":page"
argument_list|,
operator|new
name|AttributesImpl
argument_list|()
argument_list|)
expr_stmt|;
name|AttributesImpl
name|attr
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attr
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"src"
argument_list|,
literal|"src"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"sidebar.xml"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
literal|"http://apache.org/cocoon/include/1.0"
argument_list|,
literal|"include"
argument_list|,
literal|"ci:include"
argument_list|,
name|attr
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
literal|"http://apache.org/cocoon/include/1.0"
argument_list|,
literal|"include"
argument_list|,
literal|"ci:include"
argument_list|)
expr_stmt|;
name|genInfos
argument_list|()
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"page"
argument_list|,
name|PREFIX
operator|+
literal|":page"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
name|PREFIX
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
literal|"ci"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|genInfos
parameter_list|()
throws|throws
name|SAXException
block|{
name|ClusterComunication
name|cluster
init|=
name|ClusterComunication
operator|.
name|getInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|cluster
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|boolean
name|coordinator
init|=
name|cluster
operator|.
name|isCoordinator
argument_list|()
decl_stmt|;
name|Address
name|localaddress
init|=
name|cluster
operator|.
name|getAddress
argument_list|()
decl_stmt|;
name|int
index|[]
index|[]
name|headers
init|=
name|cluster
operator|.
name|getHeaders
argument_list|()
decl_stmt|;
name|AttributesImpl
name|atts
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|AttributesImpl
name|inner
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|atts
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"ismaster"
argument_list|,
literal|"ismaster"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|coordinator
argument_list|)
expr_stmt|;
name|atts
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|localaddress
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"node"
argument_list|,
name|PREFIX
operator|+
literal|":node"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|atts
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|atts
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"headers"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"lastId"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|headers
index|[
literal|0
index|]
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"maxId"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|headers
index|[
literal|0
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"counter"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|headers
index|[
literal|0
index|]
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|coordinator
condition|)
block|{
name|atts
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|atts
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"master-headers"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"lastId"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|headers
index|[
literal|1
index|]
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"maxId"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|headers
index|[
literal|1
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"counter"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|headers
index|[
literal|1
index|]
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|)
expr_stmt|;
block|}
name|Configuration
name|conf
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|atts
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|atts
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"jgroups"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"protocol"
argument_list|)
expr_stmt|;
name|String
name|protocol
init|=
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"cluster.protocol"
argument_list|)
decl_stmt|;
name|StringBuffer
name|prot
init|=
operator|new
name|StringBuffer
argument_list|()
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
name|protocol
operator|.
name|length
argument_list|()
condition|;
name|i
operator|+=
literal|70
control|)
block|{
name|prot
operator|.
name|append
argument_list|(
name|protocol
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|Math
operator|.
name|min
argument_list|(
name|i
operator|+
literal|70
argument_list|,
name|protocol
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|prot
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
name|prot
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|)
expr_stmt|;
name|atts
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|atts
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"type"
argument_list|,
literal|"type"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"journal"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"journal-dir"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|conf
operator|.
name|getProperty
argument_list|(
literal|"cluster.journalDir"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"exclude-dir"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|conf
operator|.
name|getProperty
argument_list|(
literal|"cluster.exclude"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"max-item"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|conf
operator|.
name|getProperty
argument_list|(
literal|"cluster.journal.maxStore"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|inner
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"coordinator-shift"
argument_list|)
expr_stmt|;
name|inner
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"value"
argument_list|,
literal|"value"
argument_list|,
literal|"CDATA"
argument_list|,
literal|""
operator|+
name|conf
operator|.
name|getProperty
argument_list|(
literal|"cluster.journal.shift"
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|,
name|inner
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"data"
argument_list|,
name|PREFIX
operator|+
literal|":data"
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"info"
argument_list|,
name|PREFIX
operator|+
literal|":info"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClusterException
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
name|SAXException
argument_list|(
literal|"ERROR CREATING INFOS "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @param elem      * @param value      * @throws org.xml.sax.SAXException      */
specifier|private
name|void
name|addValue
parameter_list|(
name|String
name|elem
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
name|stats
operator|.
name|addValue
argument_list|(
name|elem
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

