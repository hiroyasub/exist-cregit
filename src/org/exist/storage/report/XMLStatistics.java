begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 5 sept. 2004 $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|report
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
name|BufferStats
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
name|NativeBroker
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
name|dom
operator|.
name|DOMFile
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
name|index
operator|.
name|BFile
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
name|ContentHandler
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

begin_comment
comment|/** generate statistics about the XML storage -   * used by {@link org.apache.cocoon.generation.StatusGenerator}  * @author jmv  */
end_comment

begin_class
specifier|public
class|class
name|XMLStatistics
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE
init|=
literal|"http://exist.sourceforge.net/generators/status"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"status"
decl_stmt|;
specifier|public
name|ContentHandler
name|contentHandler
decl_stmt|;
comment|/** 	 * @param contentHandler 	 */
specifier|public
name|XMLStatistics
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|)
block|{
name|this
operator|.
name|contentHandler
operator|=
name|contentHandler
expr_stmt|;
block|}
specifier|public
name|void
name|genInstanceStatus
parameter_list|()
throws|throws
name|SAXException
block|{
name|AttributesImpl
name|atts
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
comment|//TODO : find a way to retrieve the actual instance's name !
name|atts
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"default"
argument_list|,
literal|"default"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"exist"
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
literal|"database-instances"
argument_list|,
name|PREFIX
operator|+
literal|":database-instances"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|atts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|BrokerPool
name|instance
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|BrokerPool
operator|.
name|getInstances
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|instance
operator|=
operator|(
name|BrokerPool
operator|)
name|i
operator|.
name|next
argument_list|()
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
name|instance
operator|.
name|getId
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
literal|"database-instance"
argument_list|,
name|PREFIX
operator|+
literal|":database-instance"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|atts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|addValue
argument_list|(
literal|"configuration"
argument_list|,
name|instance
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"data-directory"
argument_list|,
operator|(
name|String
operator|)
name|instance
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"db-connection.data-dir"
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
literal|"pool"
argument_list|,
name|PREFIX
operator|+
literal|":pool"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"max"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|instance
operator|.
name|getMax
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"active"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|instance
operator|.
name|active
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"available"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|instance
operator|.
name|available
argument_list|()
argument_list|)
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
literal|"pool"
argument_list|,
name|PREFIX
operator|+
literal|":pool"
argument_list|)
expr_stmt|;
name|genBufferStatus
argument_list|(
name|instance
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
literal|"database-instance"
argument_list|,
name|PREFIX
operator|+
literal|":database-instance"
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|contentHandler
operator|.
name|endElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"database-instances"
argument_list|,
name|PREFIX
operator|+
literal|"database-instances"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|genBufferStatus
parameter_list|(
name|BrokerPool
name|instance
parameter_list|)
throws|throws
name|SAXException
block|{
name|AttributesImpl
name|atts
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
literal|"buffers"
argument_list|,
name|PREFIX
operator|+
literal|":buffers"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|instance
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|BFile
name|db
decl_stmt|;
name|db
operator|=
operator|(
name|BFile
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.collections"
argument_list|)
expr_stmt|;
name|genBufferDetails
argument_list|(
name|db
operator|.
name|getIndexBufferStats
argument_list|()
argument_list|,
name|db
operator|.
name|getDataBufferStats
argument_list|()
argument_list|,
literal|"Collections storage ("
operator|+
name|NativeBroker
operator|.
name|COLLECTIONS_DBX
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|DOMFile
name|dom
init|=
operator|(
name|DOMFile
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.dom"
argument_list|)
decl_stmt|;
name|genBufferDetails
argument_list|(
name|dom
operator|.
name|getIndexBufferStats
argument_list|()
argument_list|,
name|dom
operator|.
name|getDataBufferStats
argument_list|()
argument_list|,
literal|"Resource storage ("
operator|+
name|NativeBroker
operator|.
name|DOM_DBX
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|db
operator|=
operator|(
name|BFile
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.elements"
argument_list|)
expr_stmt|;
name|genBufferDetails
argument_list|(
name|db
operator|.
name|getIndexBufferStats
argument_list|()
argument_list|,
name|db
operator|.
name|getDataBufferStats
argument_list|()
argument_list|,
literal|"Structural index ("
operator|+
name|NativeBroker
operator|.
name|ELEMENTS_DBX
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|db
operator|=
operator|(
name|BFile
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.values"
argument_list|)
expr_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
name|genBufferDetails
argument_list|(
name|db
operator|.
name|getIndexBufferStats
argument_list|()
argument_list|,
name|db
operator|.
name|getDataBufferStats
argument_list|()
argument_list|,
literal|"Values index ("
operator|+
name|NativeBroker
operator|.
name|VALUES_DBX
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|db
operator|=
operator|(
name|BFile
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection2.values"
argument_list|)
expr_stmt|;
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
name|genBufferDetails
argument_list|(
name|db
operator|.
name|getIndexBufferStats
argument_list|()
argument_list|,
name|db
operator|.
name|getDataBufferStats
argument_list|()
argument_list|,
literal|"QName values index ("
operator|+
name|NativeBroker
operator|.
name|VALUES_QNAME_DBX
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|db
operator|=
operator|(
name|BFile
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.words"
argument_list|)
expr_stmt|;
name|genBufferDetails
argument_list|(
name|db
operator|.
name|getIndexBufferStats
argument_list|()
argument_list|,
name|db
operator|.
name|getDataBufferStats
argument_list|()
argument_list|,
literal|"Fulltext index ("
operator|+
name|NativeBroker
operator|.
name|WORDS_DBX
operator|+
literal|")"
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
literal|"buffers"
argument_list|,
name|PREFIX
operator|+
literal|":buffers"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|genBufferDetails
parameter_list|(
name|BufferStats
name|index
parameter_list|,
name|BufferStats
name|data
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|AttributesImpl
name|atts
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
literal|"name"
argument_list|,
literal|"name"
argument_list|,
literal|"CDATA"
argument_list|,
name|name
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
literal|"file"
argument_list|,
name|PREFIX
operator|+
literal|":file"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|atts
operator|.
name|clear
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
literal|"btree"
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
literal|"buffer"
argument_list|,
name|PREFIX
operator|+
literal|":buffer"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|atts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|addValue
argument_list|(
literal|"size"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|index
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"used"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|index
operator|.
name|getUsed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"hits"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|index
operator|.
name|getPageHits
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"fails"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|index
operator|.
name|getPageFails
argument_list|()
argument_list|)
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
literal|"buffer"
argument_list|,
name|PREFIX
operator|+
literal|":buffer"
argument_list|)
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
literal|"data"
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
literal|"buffer"
argument_list|,
name|PREFIX
operator|+
literal|":buffer"
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|atts
operator|.
name|clear
argument_list|()
expr_stmt|;
name|addValue
argument_list|(
literal|"size"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|data
operator|.
name|getSize
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"used"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|data
operator|.
name|getUsed
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"hits"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|data
operator|.
name|getPageHits
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|addValue
argument_list|(
literal|"fails"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|data
operator|.
name|getPageFails
argument_list|()
argument_list|)
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
literal|"buffer"
argument_list|,
name|PREFIX
operator|+
literal|":buffer"
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
literal|"file"
argument_list|,
name|PREFIX
operator|+
literal|":file"
argument_list|)
expr_stmt|;
block|}
specifier|public
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
name|AttributesImpl
name|atts
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|startElement
argument_list|(
name|NAMESPACE
argument_list|,
name|elem
argument_list|,
name|PREFIX
operator|+
literal|':'
operator|+
name|elem
argument_list|,
name|atts
argument_list|)
expr_stmt|;
name|this
operator|.
name|contentHandler
operator|.
name|characters
argument_list|(
name|value
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|()
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
name|elem
argument_list|,
name|PREFIX
operator|+
literal|':'
operator|+
name|elem
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param contentHandler 	 */
specifier|public
name|void
name|setContentHandler
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|)
block|{
name|this
operator|.
name|contentHandler
operator|=
name|contentHandler
expr_stmt|;
block|}
block|}
end_class

end_unit

