begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|util
package|;
end_package

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|EXistException
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
name|QName
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
name|StoredNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|EmbeddedXMLStreamReader
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
name|xquery
operator|.
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XQueryContext
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|NodeValue
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
name|SequenceType
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
name|StringValue
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NamedNodeMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_comment
comment|/**  * Delivers the fragment between two nodes (normally milestones) of a document.  * It leads to more performance for most XML documents because it  * determines the fragment directly by the EmbeddedXmlReader and not by   * XQL operators.  * @author Josef Willenborg, Max Planck Institute for the history of science,  * http://www.mpiwg-berlin.mpg.de, jwillenborg@mpiwg-berlin.mpg.de   */
end_comment

begin_class
specifier|public
class|class
name|GetFragmentBetween
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|GetFragmentBetween
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-fragment-between"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns an xml fragment or a sequence of nodes between two elements (normally milestone elements). "
operator|+
literal|"The $beginning-node represents the first node/milestone element, $ending-node, the second one. "
operator|+
literal|"The third argument, $make-fragment, is "
operator|+
literal|"a boolean value for the path completion. If it is set to true() the "
operator|+
literal|"result sequence is wrapped into a parent element node. "
operator|+
literal|"Example call of the function for getting the fragment between two TEI page break element nodes: "
operator|+
literal|"  let $fragment := util:get-fragment-between(//pb[1], //pb[2], true())"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"beginning-node"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The first node/milestone element"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"ending-node"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The second node/milestone element"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"make-fragment"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The flag make a fragment."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"the string containing the fragments between the two node/milestone elements."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetFragmentBetween
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the fragment between two elements (normally milestone elements) of a document     * @param args 1. first node (e.g. pb[10])  2. second node (e.g.: pb[11]) 3. pathCompletion:    * open and closing tags before and after the fragment are appended (Default: true)      * @return the fragment between the two nodes    * @throws XPathException    */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|Sequence
name|ms1
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|Sequence
name|ms2
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|ms1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"your first argument delivers an empty node (no valid node position in document)"
argument_list|)
throw|;
block|}
name|Node
name|ms1Node
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ms1
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|null
operator|)
condition|)
name|ms1Node
operator|=
operator|(
operator|(
name|NodeValue
operator|)
name|ms1
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|Node
name|ms2Node
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ms2
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|null
operator|)
condition|)
name|ms2Node
operator|=
operator|(
operator|(
name|NodeValue
operator|)
name|ms2
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
expr_stmt|;
name|Sequence
name|seqPathCompletion
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
name|boolean
name|pathCompletion
init|=
literal|true
decl_stmt|;
comment|// default
if|if
condition|(
operator|!
operator|(
name|seqPathCompletion
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|null
operator|)
condition|)
block|{
name|pathCompletion
operator|=
name|seqPathCompletion
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
block|}
comment|// fetch the fragment between the two milestones
name|StringBuilder
name|fragment
init|=
name|getFragmentBetween
argument_list|(
name|ms1Node
argument_list|,
name|ms2Node
argument_list|)
decl_stmt|;
if|if
condition|(
name|pathCompletion
condition|)
block|{
name|String
name|msFromPathName
init|=
name|getNodeXPath
argument_list|(
name|ms1Node
operator|.
name|getParentNode
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|openElementsOfMsFrom
init|=
name|pathName2XmlTags
argument_list|(
name|msFromPathName
argument_list|,
literal|"open"
argument_list|)
decl_stmt|;
name|String
name|closingElementsOfMsTo
init|=
literal|""
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|ms2Node
operator|==
literal|null
operator|)
condition|)
block|{
name|String
name|msToPathName
init|=
name|getNodeXPath
argument_list|(
name|ms2Node
operator|.
name|getParentNode
argument_list|()
argument_list|)
decl_stmt|;
name|closingElementsOfMsTo
operator|=
name|pathName2XmlTags
argument_list|(
name|msToPathName
argument_list|,
literal|"close"
argument_list|)
expr_stmt|;
block|}
name|fragment
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|openElementsOfMsFrom
argument_list|)
expr_stmt|;
name|fragment
operator|.
name|append
argument_list|(
name|closingElementsOfMsTo
argument_list|)
expr_stmt|;
block|}
name|StringValue
name|strValFragment
init|=
operator|new
name|StringValue
argument_list|(
name|fragment
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|ValueSequence
name|resultFragment
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|resultFragment
operator|.
name|add
argument_list|(
name|strValFragment
argument_list|)
expr_stmt|;
return|return
name|resultFragment
return|;
block|}
comment|/**    * Fetch the fragment between two nodes (normally milestones) in an XML document    * @param node1 first node from which down to the node node2 the XML fragment is delivered as a string    * @param node2 the node to which down the XML fragment is delivered as a string    * @return fragment between the two nodes    * @throws XPathException    */
specifier|private
name|StringBuilder
name|getFragmentBetween
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|)
throws|throws
name|XPathException
block|{
name|StoredNode
name|storedNode1
init|=
operator|(
name|StoredNode
operator|)
name|node1
decl_stmt|;
name|StoredNode
name|storedNode2
init|=
operator|(
name|StoredNode
operator|)
name|node2
decl_stmt|;
name|String
name|node1NodeId
init|=
name|storedNode1
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|node2NodeId
init|=
literal|"-1"
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|node2
operator|==
literal|null
operator|)
condition|)
name|node2NodeId
operator|=
name|storedNode2
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|DocumentImpl
name|docImpl
init|=
operator|(
name|DocumentImpl
operator|)
name|node1
operator|.
name|getOwnerDocument
argument_list|()
decl_stmt|;
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
name|DBBroker
name|dbBroker
init|=
literal|null
decl_stmt|;
name|StringBuilder
name|resultFragment
init|=
operator|new
name|StringBuilder
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|String
name|actualNodeId
init|=
literal|"-2"
decl_stmt|;
name|boolean
name|getFragmentMode
init|=
literal|false
decl_stmt|;
try|try
block|{
name|brokerPool
operator|=
name|docImpl
operator|.
name|getBrokerPool
argument_list|()
expr_stmt|;
name|dbBroker
operator|=
name|brokerPool
operator|.
name|get
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|EmbeddedXMLStreamReader
name|reader
init|=
literal|null
decl_stmt|;
name|NodeList
name|children
init|=
name|docImpl
operator|.
name|getChildNodes
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
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|StoredNode
name|docChildStoredNode
init|=
operator|(
name|StoredNode
operator|)
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|reader
operator|=
name|dbBroker
operator|.
name|getXMLStreamReader
argument_list|(
name|docChildStoredNode
argument_list|,
literal|false
argument_list|)
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
operator|&&
operator|!
name|node2NodeId
operator|.
name|equals
argument_list|(
name|actualNodeId
argument_list|)
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|status
condition|)
block|{
case|case
name|XMLStreamReader
operator|.
name|START_DOCUMENT
case|:
case|case
name|XMLStreamReader
operator|.
name|END_DOCUMENT
case|:
break|break;
case|case
name|XMLStreamReader
operator|.
name|START_ELEMENT
case|:
name|actualNodeId
operator|=
name|reader
operator|.
name|getNode
argument_list|()
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|actualNodeId
operator|.
name|equals
argument_list|(
name|node1NodeId
argument_list|)
condition|)
name|getFragmentMode
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|actualNodeId
operator|.
name|equals
argument_list|(
name|node2NodeId
argument_list|)
condition|)
name|getFragmentMode
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|getFragmentMode
condition|)
block|{
name|String
name|startElementTag
init|=
name|getStartElementTag
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|resultFragment
operator|.
name|append
argument_list|(
name|startElementTag
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamReader
operator|.
name|END_ELEMENT
case|:
if|if
condition|(
name|getFragmentMode
condition|)
block|{
name|String
name|endElementTag
init|=
name|getEndElementTag
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|resultFragment
operator|.
name|append
argument_list|(
name|endElementTag
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamReader
operator|.
name|CHARACTERS
case|:
if|if
condition|(
name|getFragmentMode
condition|)
block|{
name|String
name|characters
init|=
name|getCharacters
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|resultFragment
operator|.
name|append
argument_list|(
name|characters
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamReader
operator|.
name|CDATA
case|:
if|if
condition|(
name|getFragmentMode
condition|)
block|{
name|String
name|cdata
init|=
name|getCDataTag
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|resultFragment
operator|.
name|append
argument_list|(
name|cdata
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamReader
operator|.
name|COMMENT
case|:
if|if
condition|(
name|getFragmentMode
condition|)
block|{
name|String
name|comment
init|=
name|getCommentTag
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|resultFragment
operator|.
name|append
argument_list|(
name|comment
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|XMLStreamReader
operator|.
name|PROCESSING_INSTRUCTION
case|:
if|if
condition|(
name|getFragmentMode
condition|)
block|{
name|String
name|piTag
init|=
name|getPITag
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|resultFragment
operator|.
name|append
argument_list|(
name|piTag
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An error occurred while getFragmentBetween: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An error occurred while getFragmentBetween: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An error occurred while getFragmentBetween: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|brokerPool
operator|!=
literal|null
condition|)
name|brokerPool
operator|.
name|release
argument_list|(
name|dbBroker
argument_list|)
expr_stmt|;
block|}
return|return
name|resultFragment
return|;
block|}
specifier|private
name|String
name|getStartElementTag
parameter_list|(
name|EmbeddedXMLStreamReader
name|reader
parameter_list|)
block|{
name|String
name|elemName
init|=
name|reader
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
name|String
name|elemAttrString
init|=
literal|""
decl_stmt|;
name|String
name|elemNsString
init|=
literal|""
decl_stmt|;
name|int
name|nsCount
init|=
name|reader
operator|.
name|getNamespaceCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ni
init|=
literal|0
init|;
name|ni
operator|<
name|nsCount
condition|;
name|ni
operator|++
control|)
block|{
name|String
name|nsPrefix
init|=
name|reader
operator|.
name|getNamespacePrefix
argument_list|(
name|ni
argument_list|)
decl_stmt|;
name|String
name|nsUri
init|=
name|reader
operator|.
name|getNamespaceURI
argument_list|(
name|ni
argument_list|)
decl_stmt|;
name|String
name|nsString
init|=
literal|"xmlns:"
operator|+
name|nsPrefix
operator|+
literal|"=\""
operator|+
name|nsUri
operator|+
literal|"\""
decl_stmt|;
name|elemNsString
operator|=
name|elemNsString
operator|+
literal|" "
operator|+
name|nsString
expr_stmt|;
block|}
name|int
name|attrCount
init|=
name|reader
operator|.
name|getAttributeCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|attrCount
condition|;
name|j
operator|++
control|)
block|{
name|String
name|attrNamePrefix
init|=
name|reader
operator|.
name|getAttributePrefix
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|String
name|attrName
init|=
name|reader
operator|.
name|getAttributeLocalName
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|String
name|attrValue
init|=
name|reader
operator|.
name|getAttributeValue
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|attrValue
operator|=
name|escape
argument_list|(
name|attrValue
argument_list|)
expr_stmt|;
name|String
name|attrString
init|=
literal|""
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|attrNamePrefix
operator|==
literal|null
operator|||
name|attrNamePrefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
name|attrString
operator|=
name|attrNamePrefix
operator|+
literal|":"
expr_stmt|;
name|attrString
operator|=
name|attrString
operator|+
name|attrName
operator|+
literal|"=\""
operator|+
name|attrValue
operator|+
literal|"\""
expr_stmt|;
name|elemAttrString
operator|=
name|elemAttrString
operator|+
literal|" "
operator|+
name|attrString
expr_stmt|;
block|}
name|String
name|elemPrefix
init|=
name|reader
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|String
name|elemPart
init|=
literal|""
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|elemPrefix
operator|==
literal|null
operator|||
name|elemPrefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
name|elemPart
operator|=
name|elemPrefix
operator|+
literal|":"
expr_stmt|;
name|elemPart
operator|=
name|elemPart
operator|+
name|elemName
expr_stmt|;
name|String
name|elementString
init|=
literal|"<"
operator|+
name|elemPart
operator|+
name|elemNsString
operator|+
name|elemAttrString
operator|+
literal|">"
decl_stmt|;
return|return
name|elementString
return|;
block|}
specifier|private
name|String
name|getEndElementTag
parameter_list|(
name|EmbeddedXMLStreamReader
name|reader
parameter_list|)
block|{
name|String
name|elemName
init|=
name|reader
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
name|String
name|elemPrefix
init|=
name|reader
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|String
name|elemPart
init|=
literal|""
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|elemPrefix
operator|==
literal|null
operator|||
name|elemPrefix
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
name|elemPart
operator|=
name|elemPrefix
operator|+
literal|":"
expr_stmt|;
name|elemPart
operator|=
name|elemPart
operator|+
name|elemName
expr_stmt|;
return|return
literal|"</"
operator|+
name|elemPart
operator|+
literal|">"
return|;
block|}
specifier|private
name|String
name|getCharacters
parameter_list|(
name|EmbeddedXMLStreamReader
name|reader
parameter_list|)
block|{
name|String
name|xmlChars
init|=
name|reader
operator|.
name|getText
argument_list|()
decl_stmt|;
name|xmlChars
operator|=
name|escape
argument_list|(
name|xmlChars
argument_list|)
expr_stmt|;
return|return
name|xmlChars
return|;
block|}
specifier|private
name|String
name|getCDataTag
parameter_list|(
name|EmbeddedXMLStreamReader
name|reader
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
name|reader
operator|.
name|getTextCharacters
argument_list|()
decl_stmt|;
return|return
literal|"<![CDATA[\n"
operator|+
operator|new
name|String
argument_list|(
name|chars
argument_list|)
operator|+
literal|"\n]]>"
return|;
block|}
specifier|private
name|String
name|getCommentTag
parameter_list|(
name|EmbeddedXMLStreamReader
name|reader
parameter_list|)
block|{
name|char
index|[]
name|chars
init|=
name|reader
operator|.
name|getTextCharacters
argument_list|()
decl_stmt|;
return|return
literal|"<!--"
operator|+
operator|new
name|String
argument_list|(
name|chars
argument_list|)
operator|+
literal|"-->"
return|;
block|}
specifier|private
name|String
name|getPITag
parameter_list|(
name|EmbeddedXMLStreamReader
name|reader
parameter_list|)
block|{
name|String
name|piTarget
init|=
name|reader
operator|.
name|getPITarget
argument_list|()
decl_stmt|;
name|String
name|piData
init|=
name|reader
operator|.
name|getPIData
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|piData
operator|==
literal|null
operator|||
name|piData
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
name|piData
operator|=
literal|" "
operator|+
name|piData
expr_stmt|;
else|else
name|piData
operator|=
literal|""
expr_stmt|;
return|return
literal|"<?"
operator|+
name|piTarget
operator|+
name|piData
operator|+
literal|"?>"
return|;
block|}
specifier|private
name|String
name|escape
parameter_list|(
name|String
name|inputStr
parameter_list|)
block|{
name|StringBuilder
name|resultStrBuf
init|=
operator|new
name|StringBuilder
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
name|inputStr
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|inputStr
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'<'
case|:
name|resultStrBuf
operator|.
name|append
argument_list|(
literal|"&lt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'>'
case|:
name|resultStrBuf
operator|.
name|append
argument_list|(
literal|"&gt;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'&'
case|:
name|resultStrBuf
operator|.
name|append
argument_list|(
literal|"&amp;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\"'
case|:
name|resultStrBuf
operator|.
name|append
argument_list|(
literal|"&quot;"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\''
case|:
name|resultStrBuf
operator|.
name|append
argument_list|(
literal|"&#039;"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|resultStrBuf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
return|return
name|resultStrBuf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * A path name delivered by function xnode-path (with special strings such as     * "@", "[", "]", " eq ") is converted to an XML String with xml tags,     * opened or closed such as the mode says    * @param pathName delivered by function xnode-path: Example: /archimedes[@xmlns:xlink eq "http://www.w3.org/1999/xlink"]/text/body/chap/p[@type eq "main"]/s/foreign[@lang eq "en"]    * @param mode open or close    * @return xml tags opened or closed    */
specifier|private
name|String
name|pathName2XmlTags
parameter_list|(
name|String
name|pathName
parameter_list|,
name|String
name|mode
parameter_list|)
block|{
name|String
name|result
init|=
literal|""
decl_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|elements
init|=
name|pathName2ElementsWithAttributes
argument_list|(
name|pathName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|.
name|equals
argument_list|(
literal|"open"
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|elements
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|element
init|=
name|elements
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|element
operator|=
name|element
operator|.
name|replaceAll
argument_list|(
literal|"\\["
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
comment|// opening element: replace open bracket with space
name|element
operator|=
name|element
operator|.
name|replaceAll
argument_list|(
literal|" eq "
argument_list|,
literal|"="
argument_list|)
expr_stmt|;
comment|// opening element: remove @ character
name|element
operator|=
name|element
operator|.
name|replaceAll
argument_list|(
literal|"@"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// opening element: remove @ character
name|element
operator|=
name|element
operator|.
name|replaceAll
argument_list|(
literal|"\\]"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// opening element: remove closing bracket
if|if
condition|(
operator|!
operator|(
name|element
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
name|result
operator|+=
literal|"<"
operator|+
name|element
operator|+
literal|">\n"
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|mode
operator|.
name|equals
argument_list|(
literal|"close"
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|elements
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|String
name|element
init|=
name|elements
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|element
operator|=
name|element
operator|.
name|replaceAll
argument_list|(
literal|"\\[[^\\]]*\\]"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// closing element: remove brackets with attributes
if|if
condition|(
operator|!
operator|(
name|element
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|)
condition|)
name|result
operator|+=
literal|"</"
operator|+
name|element
operator|+
literal|">\n"
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|pathName2ElementsWithAttributes
parameter_list|(
name|String
name|pathName
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathName
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'/'
condition|)
name|pathName
operator|=
name|pathName
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|pathName
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|// without first "/" character
name|String
name|regExpr
init|=
literal|"[a-zA-Z0-9:]+?\\[.+?\\]/"
operator|+
literal|"|"
operator|+
literal|"[a-zA-Z0-9:]+?/"
operator|+
literal|"|"
operator|+
literal|"[a-zA-Z0-9:]+?\\[.+\\]$"
operator|+
literal|"|"
operator|+
literal|"[a-zA-Z0-9:]+?$"
decl_stmt|;
comment|// pathName example: "/archimedes[@xmlns:xlink eq "http://www.w3.org/1999/xlink"]/text/body/chap/p[@type eq "main"]/s/foreign[@lang eq "en"]"
name|Pattern
name|p
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|regExpr
argument_list|,
name|Pattern
operator|.
name|CASE_INSENSITIVE
operator||
name|Pattern
operator|.
name|MULTILINE
argument_list|)
decl_stmt|;
comment|// both flags enabled
name|Matcher
name|m
init|=
name|p
operator|.
name|matcher
argument_list|(
name|pathName
argument_list|)
decl_stmt|;
while|while
condition|(
name|m
operator|.
name|find
argument_list|()
condition|)
block|{
name|int
name|msBeginPos
init|=
name|m
operator|.
name|start
argument_list|()
decl_stmt|;
name|int
name|msEndPos
init|=
name|m
operator|.
name|end
argument_list|()
decl_stmt|;
name|String
name|elementName
init|=
name|pathName
operator|.
name|substring
argument_list|(
name|msBeginPos
argument_list|,
name|msEndPos
argument_list|)
decl_stmt|;
name|int
name|elemNameSize
init|=
name|elementName
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|elemNameSize
operator|>
literal|0
operator|&&
name|elementName
operator|.
name|charAt
argument_list|(
name|elemNameSize
operator|-
literal|1
argument_list|)
operator|==
literal|'/'
condition|)
name|elementName
operator|=
name|elementName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|elemNameSize
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// without last "/" character
name|result
operator|.
name|add
argument_list|(
name|elementName
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|String
name|getNodeXPath
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
comment|//if at the document level just return /
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
return|return
literal|"/"
return|;
comment|/* walk up the node hierarchy      * - node names become path names       * - attributes become predicates      */
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|nodeToXPath
argument_list|(
name|n
argument_list|)
argument_list|)
decl_stmt|;
while|while
condition|(
operator|(
name|n
operator|=
name|n
operator|.
name|getParentNode
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|buf
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|nodeToXPath
argument_list|(
name|n
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Creates an XPath for a Node    * The nodes attribute's become predicates    *     * @param n The Node to generate an XPath for    * @return StringBuilder containing the XPath    */
specifier|private
name|StringBuilder
name|nodeToXPath
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
name|StringBuilder
name|xpath
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"/"
operator|+
name|getFullNodeName
argument_list|(
name|n
argument_list|)
argument_list|)
decl_stmt|;
name|NamedNodeMap
name|attrs
init|=
name|n
operator|.
name|getAttributes
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
name|attrs
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Node
name|attr
init|=
name|attrs
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|fullNodeName
init|=
name|getFullNodeName
argument_list|(
name|attr
argument_list|)
decl_stmt|;
name|String
name|attrNodeValue
init|=
name|attr
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|fullNodeName
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
operator|&&
operator|(
operator|!
operator|(
name|fullNodeName
operator|==
literal|null
operator|)
operator|)
condition|)
name|xpath
operator|.
name|append
argument_list|(
literal|"[@"
operator|+
name|fullNodeName
operator|+
literal|" eq \""
operator|+
name|attrNodeValue
operator|+
literal|"\"]"
argument_list|)
expr_stmt|;
block|}
return|return
name|xpath
return|;
block|}
comment|/**    * Returns the full node name including the prefix if present    *     * @param n The node to get the name for    * @return The full name of the node    */
specifier|private
name|String
name|getFullNodeName
parameter_list|(
name|Node
name|n
parameter_list|)
block|{
name|String
name|prefix
init|=
name|n
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
name|String
name|localName
init|=
name|n
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|prefix
operator|==
literal|null
operator|||
name|prefix
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
if|if
condition|(
name|localName
operator|==
literal|null
operator|||
name|localName
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
return|return
literal|""
return|;
else|else
return|return
name|localName
return|;
block|}
else|else
block|{
if|if
condition|(
name|localName
operator|==
literal|null
operator|||
name|localName
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
return|return
literal|""
return|;
else|else
return|return
name|prefix
operator|+
literal|":"
operator|+
name|localName
return|;
block|}
block|}
block|}
end_class

end_unit

