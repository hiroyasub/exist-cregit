begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|*
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
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  *  Description of the Class  *  *@author     wolf  *@created    17. Juni 2002  */
end_comment

begin_class
specifier|public
class|class
name|IncludeXMLFilter
extends|extends
name|DefaultHandler
block|{
specifier|private
name|ContentHandler
name|handler
decl_stmt|;
comment|/**      *  Constructor for the WhitespaceFilter object      *      *@param  handler  Description of the Parameter      */
specifier|public
name|IncludeXMLFilter
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|handler
operator|=
name|handler
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  ch                Description of the Parameter      *@param  start             Description of the Parameter      *@param  length            Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|characters
parameter_list|(
name|char
name|ch
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|handler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
comment|// remove
block|}
comment|/**      *  Description of the Method      *      *@param  namespaceURI      Description of the Parameter      *@param  localName         Description of the Parameter      *@param  qName             Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|localName
operator|==
literal|null
operator|||
name|localName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|localName
operator|=
name|qName
expr_stmt|;
name|handler
operator|.
name|endElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  prefix            Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|endPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|)
throws|throws
name|SAXException
block|{
name|handler
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  ch                Description of the Parameter      *@param  start             Description of the Parameter      *@param  length            Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|ignorableWhitespace
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|SAXException
block|{
name|handler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  target            Description of the Parameter      *@param  data              Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|processingInstruction
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|SAXException
block|{
name|handler
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  name              Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|skippedEntity
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|SAXException
block|{
name|handler
operator|.
name|skippedEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
comment|// remove
block|}
comment|/**      *  Description of the Method      *      *@param  namespaceURI      Description of the Parameter      *@param  localName         Description of the Parameter      *@param  qName             Description of the Parameter      *@param  atts              Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|namespaceURI
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|localName
operator|==
literal|null
operator|||
name|localName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|localName
operator|=
name|qName
expr_stmt|;
name|handler
operator|.
name|startElement
argument_list|(
name|namespaceURI
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|atts
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  prefix            Description of the Parameter      *@param  uri               Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|SAXException
block|{
name|handler
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

