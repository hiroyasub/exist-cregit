begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|DocumentImpl
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
name|DocumentSet
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
name|NodeProxy
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
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|parser
operator|.
name|XPathLexer2
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|parser
operator|.
name|XPathParser2
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|parser
operator|.
name|XPathTreeParser2
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
name|util
operator|.
name|XMLUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|PathExpr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|StaticContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|Value
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|ValueSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|XPathException
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
name|Attributes
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
name|Locator
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
name|antlr
operator|.
name|RecognitionException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenStreamException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|collections
operator|.
name|AST
import|;
end_import

begin_comment
comment|/**  * Used to filter the SAX stream generated by the  * serializer for XInclude statements.   */
end_comment

begin_class
specifier|public
class|class
name|XIncludeFilter
implements|implements
name|ContentHandler
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XIncludeFilter
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XINCLUDE_NS
init|=
literal|"http://www.w3.org/2001/XInclude"
decl_stmt|;
specifier|private
name|ContentHandler
name|contentHandler
decl_stmt|;
specifier|private
name|Serializer
name|serializer
decl_stmt|;
specifier|private
name|DocumentImpl
name|document
init|=
literal|null
decl_stmt|;
specifier|private
name|HashMap
name|namespaces
init|=
operator|new
name|HashMap
argument_list|(
literal|10
argument_list|)
decl_stmt|;
specifier|public
name|XIncludeFilter
parameter_list|(
name|Serializer
name|serializer
parameter_list|,
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
name|this
operator|.
name|serializer
operator|=
name|serializer
expr_stmt|;
block|}
specifier|public
name|XIncludeFilter
parameter_list|(
name|Serializer
name|serializer
parameter_list|)
block|{
name|this
argument_list|(
name|serializer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setContentHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|contentHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|public
name|ContentHandler
name|getContentHandler
parameter_list|()
block|{
return|return
name|contentHandler
return|;
block|}
specifier|public
name|void
name|setDocument
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|document
operator|=
name|doc
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#characters(char, int, int) 	 */
specifier|public
name|void
name|characters
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
name|contentHandler
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
comment|/** 	 * @see org.xml.sax.ContentHandler#endDocument() 	 */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String) 	 */
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
name|namespaceURI
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|namespaceURI
operator|.
name|equals
argument_list|(
name|XINCLUDE_NS
argument_list|)
operator|)
condition|)
name|contentHandler
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
comment|/** 	 * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String) 	 */
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
name|namespaces
operator|.
name|remove
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#ignorableWhitespace(char, int, int) 	 */
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
name|contentHandler
operator|.
name|ignorableWhitespace
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String) 	 */
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
name|contentHandler
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator) 	 */
specifier|public
name|void
name|setDocumentLocator
parameter_list|(
name|Locator
name|locator
parameter_list|)
block|{
name|contentHandler
operator|.
name|setDocumentLocator
argument_list|(
name|locator
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String) 	 */
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
name|contentHandler
operator|.
name|skippedEntity
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#startDocument() 	 */
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes) 	 */
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
name|namespaceURI
operator|!=
literal|null
operator|&&
name|namespaceURI
operator|.
name|equals
argument_list|(
name|XINCLUDE_NS
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"found xinclude element"
argument_list|)
expr_stmt|;
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
literal|"include"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"processing include ..."
argument_list|)
expr_stmt|;
name|processXInclude
argument_list|(
name|atts
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|contentHandler
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
block|}
specifier|protected
name|void
name|processXInclude
parameter_list|(
name|Attributes
name|atts
parameter_list|)
throws|throws
name|SAXException
block|{
comment|// save some settings
name|DocumentImpl
name|prevDoc
init|=
name|document
decl_stmt|;
name|boolean
name|createContainerElements
init|=
name|serializer
operator|.
name|createContainerElements
decl_stmt|;
name|serializer
operator|.
name|createContainerElements
operator|=
literal|false
expr_stmt|;
comment|// parse the href attribute
name|String
name|href
init|=
name|atts
operator|.
name|getValue
argument_list|(
literal|"href"
argument_list|)
decl_stmt|;
if|if
condition|(
name|href
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"found href=\""
operator|+
name|href
operator|+
literal|"\""
argument_list|)
expr_stmt|;
name|String
name|xpointer
init|=
literal|null
decl_stmt|;
name|String
name|docName
init|=
name|href
decl_stmt|;
comment|// try to find xpointer part
name|int
name|p
init|=
name|href
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
decl_stmt|;
if|if
condition|(
operator|-
literal|1
operator|<
name|p
condition|)
block|{
name|docName
operator|=
name|href
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|xpointer
operator|=
name|XMLUtil
operator|.
name|decodeAttrMarkup
argument_list|(
name|href
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"found xpointer: "
operator|+
name|xpointer
argument_list|)
expr_stmt|;
block|}
comment|// if docName has no collection specified, assume
comment|// current collection
name|p
operator|=
name|docName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
if|if
condition|(
name|p
operator|<
literal|0
condition|)
name|docName
operator|=
name|document
operator|.
name|getCollection
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|'/'
operator|+
name|docName
expr_stmt|;
comment|// retrieve the document
name|LOG
operator|.
name|debug
argument_list|(
literal|"loading "
operator|+
name|docName
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|serializer
operator|.
name|broker
operator|.
name|getDocument
argument_list|(
name|docName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"permission denied"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|/* if document has not been found and xpointer is 			 * null, throw an exception. If xpointer != null 			 * we retry below and interpret docName as 			 * a collection. 			 */
if|if
condition|(
name|doc
operator|==
literal|null
operator|&&
name|xpointer
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"document "
operator|+
name|docName
operator|+
literal|" not found"
argument_list|)
throw|;
if|if
condition|(
name|xpointer
operator|==
literal|null
condition|)
comment|// no xpointer found - just serialize the doc
name|serializer
operator|.
name|serializeToSAX
argument_list|(
name|doc
argument_list|,
literal|false
argument_list|)
expr_stmt|;
else|else
block|{
comment|// process the xpointer
try|try
block|{
comment|// build input document set
name|DocumentSet
name|docs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
comment|// try to read documents from the collection
comment|// specified by docName
name|docs
operator|=
name|serializer
operator|.
name|broker
operator|.
name|getDocumentsByCollection
argument_list|(
name|docName
argument_list|)
expr_stmt|;
comment|// give up
if|if
condition|(
name|docs
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"no document or collection "
operator|+
literal|"called "
operator|+
name|docName
argument_list|)
throw|;
block|}
else|else
block|{
name|docs
operator|=
operator|new
name|DocumentSet
argument_list|()
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|StaticContext
name|context
init|=
operator|new
name|StaticContext
argument_list|(
name|serializer
operator|.
name|broker
argument_list|)
decl_stmt|;
name|xpointer
operator|=
name|checkNamespaces
argument_list|(
name|context
argument_list|,
name|xpointer
argument_list|)
expr_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|namespaces
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|context
operator|.
name|declareNamespace
argument_list|(
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|XPathLexer2
name|lexer
init|=
operator|new
name|XPathLexer2
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xpointer
argument_list|)
argument_list|)
decl_stmt|;
name|XPathParser2
name|parser
init|=
operator|new
name|XPathParser2
argument_list|(
name|lexer
argument_list|)
decl_stmt|;
name|XPathTreeParser2
name|treeParser
init|=
operator|new
name|XPathTreeParser2
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|parser
operator|.
name|xpointer
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
block|}
name|AST
name|ast
init|=
name|parser
operator|.
name|getAST
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"generated AST: "
operator|+
name|ast
operator|.
name|toStringTree
argument_list|()
argument_list|)
expr_stmt|;
name|PathExpr
name|expr
init|=
operator|new
name|PathExpr
argument_list|()
decl_stmt|;
name|treeParser
operator|.
name|xpointer
argument_list|(
name|ast
argument_list|,
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
name|treeParser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|treeParser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"xpointer query: "
operator|+
name|expr
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|docs
operator|=
name|expr
operator|.
name|preselect
argument_list|(
name|docs
argument_list|,
name|context
argument_list|)
expr_stmt|;
if|if
condition|(
name|docs
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|Value
name|resultValue
init|=
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|resultValue
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Value
operator|.
name|isNodeList
case|:
name|NodeSet
name|set
init|=
operator|(
name|NodeSet
operator|)
name|resultValue
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"xpointer found: "
operator|+
name|set
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|proxy
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|proxy
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|serializeToSAX
argument_list|(
name|proxy
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
break|break;
default|default :
name|ValueSet
name|values
init|=
name|resultValue
operator|.
name|getValueSet
argument_list|()
decl_stmt|;
name|String
name|val
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
name|values
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|val
operator|=
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|characters
argument_list|(
name|val
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|val
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
catch|catch
parameter_list|(
name|RecognitionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"xpointer error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TokenStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"xpointer error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"xpointer error"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|// restore settings
name|document
operator|=
name|prevDoc
expr_stmt|;
name|serializer
operator|.
name|createContainerElements
operator|=
name|createContainerElements
expr_stmt|;
block|}
comment|/** 	 * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String) 	 */
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
name|namespaces
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Process xmlns() schema. We process these here, because namespace mappings should 	 * already been known when parsing the xpointer() expression. 	 *  	 * @param context 	 * @param xpointer 	 * @return 	 * @throws XPathException 	 */
specifier|private
name|String
name|checkNamespaces
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|String
name|xpointer
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|p0
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|p0
operator|=
name|xpointer
operator|.
name|indexOf
argument_list|(
literal|"xmlns("
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|p0
operator|<
literal|0
condition|)
return|return
name|xpointer
return|;
name|int
name|p1
init|=
name|xpointer
operator|.
name|indexOf
argument_list|(
literal|')'
argument_list|,
name|p0
operator|+
literal|6
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|<
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"expected ) for xmlns()"
argument_list|)
throw|;
name|String
name|mapping
init|=
name|xpointer
operator|.
name|substring
argument_list|(
name|p0
operator|+
literal|6
argument_list|,
name|p1
argument_list|)
decl_stmt|;
name|xpointer
operator|=
name|xpointer
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p0
argument_list|)
operator|+
name|xpointer
operator|.
name|substring
argument_list|(
name|p1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|mapping
argument_list|,
literal|"= \t\n"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tok
operator|.
name|countTokens
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"expected prefix=namespace mapping in "
operator|+
name|mapping
argument_list|)
throw|;
name|String
name|prefix
init|=
name|tok
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|String
name|namespaceURI
init|=
name|tok
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|prefix
operator|+
literal|" == "
operator|+
name|namespaceURI
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareNamespace
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
return|return
name|xpointer
return|;
block|}
block|}
end_class

end_unit

