begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
operator|.
name|test
package|;
end_package

begin_comment
comment|/*  *  The XML:DB Initiative Software License, Version 1.0  *  *  * Copyright (c) 2000-2003 The XML:DB Initiative.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        XML:DB Initiative (http://www.xmldb.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The name "XML:DB Initiative" must not be used to endorse or  *    promote products derived from this software without prior written  *    permission. For written permission, please contact info@xmldb.org.  *  * 5. Products derived from this software may not be called "XML:DB",  *    nor may "XML:DB" appear in their name, without prior written  *    permission of the XML:DB Initiative.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the XML:DB Initiative. For more information  * on the XML:DB Initiative, please see<http://www.xmldb.org/>.  */
end_comment

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
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
comment|/** */
end_comment

begin_class
specifier|public
class|class
name|CompareDocuments
block|{
comment|/**      * Constructor      */
specifier|public
name|CompareDocuments
parameter_list|()
block|{
block|}
specifier|public
name|void
name|compare
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|)
throws|throws
name|Exception
block|{
name|compare
argument_list|(
name|node1
argument_list|,
name|node2
argument_list|,
literal|""
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|compare
parameter_list|(
name|Node
name|node1
parameter_list|,
name|Node
name|node2
parameter_list|,
name|String
name|space
parameter_list|,
name|boolean
name|show
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|node1
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
operator|&&
name|node2
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
name|compare
argument_list|(
operator|(
operator|(
name|Document
operator|)
name|node1
operator|)
operator|.
name|getDocumentElement
argument_list|( )
argument_list|,
operator|(
operator|(
name|Document
operator|)
name|node2
operator|)
operator|.
name|getDocumentElement
argument_list|( )
argument_list|,
name|space
argument_list|,
name|show
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|show
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|space
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|node1
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
default|default:
block|}
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
name|node1
operator|+
literal|"["
operator|+
name|node1
operator|.
name|getNodeType
argument_list|()
operator|+
literal|"]<==> "
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|node2
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"@"
argument_list|)
expr_stmt|;
default|default:
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|node2
operator|+
literal|"["
operator|+
name|node2
operator|.
name|getNodeType
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node1
operator|.
name|getNodeType
argument_list|()
operator|!=
name|node2
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"different node types ("
operator|+
name|node1
operator|.
name|getNodeType
argument_list|()
operator|+
literal|"!="
operator|+
name|node2
operator|.
name|getNodeType
argument_list|()
operator|+
literal|")..."
argument_list|)
throw|;
block|}
if|if
condition|(
name|node1
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
operator|^
name|node2
operator|.
name|getNamespaceURI
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"only one node has a Namespace"
argument_list|)
throw|;
block|}
if|if
condition|(
name|node1
operator|.
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
operator|&&
name|node2
operator|.
name|getNamespaceURI
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|node1
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|node2
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"different NamespaceURI's ("
operator|+
name|node1
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|"!="
operator|+
name|node2
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|")..."
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|node1
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
name|node2
operator|.
name|getNodeName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"different node names ("
operator|+
name|node1
operator|.
name|getNodeName
argument_list|()
operator|+
literal|"!="
operator|+
name|node2
operator|.
name|getNodeName
argument_list|()
operator|+
literal|"..."
argument_list|)
throw|;
block|}
if|if
condition|(
name|node1
operator|.
name|getNodeValue
argument_list|()
operator|!=
literal|null
operator|&&
name|node2
operator|.
name|getNodeValue
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|node1
operator|.
name|getNodeValue
argument_list|()
operator|.
name|equals
argument_list|(
name|node2
operator|.
name|getNodeValue
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"different node values ("
operator|+
name|node1
operator|.
name|getNodeValue
argument_list|()
operator|+
literal|"!="
operator|+
name|node2
operator|.
name|getNodeValue
argument_list|()
operator|+
literal|")..."
argument_list|)
throw|;
block|}
name|NamedNodeMap
name|attr1
init|=
name|node1
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|NamedNodeMap
name|attr2
init|=
name|node2
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr1
operator|!=
literal|null
operator|&&
name|attr2
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|attr1
operator|.
name|getLength
argument_list|()
operator|!=
name|attr2
operator|.
name|getLength
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"different attribute counts: node1: "
operator|+
name|attr1
operator|.
name|getLength
argument_list|()
operator|+
literal|"; node2: "
operator|+
name|attr2
operator|.
name|getLength
argument_list|()
argument_list|)
throw|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|attr1
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|compare
argument_list|(
name|attr1
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
name|attr2
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|,
name|space
argument_list|,
name|show
argument_list|)
expr_stmt|;
block|}
block|}
name|NodeList
name|list1
init|=
name|node1
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|NodeList
name|list2
init|=
name|node2
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
if|if
condition|(
name|list1
operator|.
name|getLength
argument_list|()
operator|!=
name|list2
operator|.
name|getLength
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"different child node counts("
operator|+
name|list1
operator|.
name|getLength
argument_list|()
operator|+
literal|"!="
operator|+
name|list2
operator|.
name|getLength
argument_list|()
operator|+
literal|")..."
argument_list|)
throw|;
block|}
comment|/*         Node child1 = node1.getFirstChild();         Node last1  = node1.getLastChild();         Node child2 = node2.getFirstChild();         if (!(child1==child2&& child1==null)) {             switch (node1.getNodeType()) {                 case Node.ATTRIBUTE_NODE:                     space += "   @";                     break;                 default:                     space += "    ";             }             while (child1!=last1){                 compare (child1, child2, space, show);                 child1 = child1.getNextSibling();                 child2 = child2.getNextSibling();             }             compare(child1, child2, space, show);         } */
name|Node
name|child1
init|=
name|node1
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|Node
name|child2
init|=
name|node2
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
name|space
operator|+=
literal|"    "
expr_stmt|;
while|while
condition|(
name|child1
operator|!=
literal|null
condition|)
block|{
name|compare
argument_list|(
name|child1
argument_list|,
name|child2
argument_list|,
name|space
argument_list|,
name|show
argument_list|)
expr_stmt|;
name|child1
operator|=
name|child1
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|child2
operator|=
name|child2
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

