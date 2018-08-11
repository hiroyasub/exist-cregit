begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|NodeHandle
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
name|util
operator|.
name|serializer
operator|.
name|AttrList
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
name|serializer
operator|.
name|Receiver
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
name|Document
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

begin_comment
comment|/**  * Utility implementation of interface {@link org.exist.indexing.MatchListener} which forwards all  * events to a second receiver. Subclass this class and overwrite the methods you are interested in.  * After processing an event, call the corresponding super method to forward it to the next receiver  * in the chain.  */
end_comment

begin_class
specifier|public
class|class
name|AbstractMatchListener
implements|implements
name|MatchListener
block|{
specifier|protected
name|Receiver
name|nextListener
decl_stmt|;
specifier|protected
name|NodeHandle
name|currentNode
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setNextInChain
parameter_list|(
name|Receiver
name|next
parameter_list|)
block|{
name|this
operator|.
name|nextListener
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Receiver
name|getNextInChain
parameter_list|()
block|{
return|return
name|nextListener
return|;
block|}
annotation|@
name|Override
specifier|public
name|Receiver
name|getLastInChain
parameter_list|()
block|{
name|Receiver
name|last
init|=
name|this
decl_stmt|;
name|Receiver
name|next
init|=
name|getNextInChain
argument_list|()
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|last
operator|=
name|next
expr_stmt|;
name|next
operator|=
operator|(
operator|(
name|MatchListener
operator|)
name|next
operator|)
operator|.
name|getNextInChain
argument_list|()
expr_stmt|;
block|}
return|return
name|last
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCurrentNode
parameter_list|(
specifier|final
name|NodeHandle
name|node
parameter_list|)
block|{
name|this
operator|.
name|currentNode
operator|=
name|node
expr_stmt|;
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|getNextInChain
argument_list|()
operator|.
name|setCurrentNode
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|NodeHandle
name|getCurrentNode
parameter_list|()
block|{
return|return
name|currentNode
return|;
block|}
annotation|@
name|Override
specifier|public
name|Document
name|getDocument
parameter_list|()
block|{
comment|//TODO return currentNode.getDocument() ?
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startPrefixMapping
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|namespaceURI
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|startPrefixMapping
argument_list|(
name|prefix
argument_list|,
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|endPrefixMapping
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|,
name|AttrList
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|startElement
argument_list|(
name|qname
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|characters
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|attribute
parameter_list|(
name|QName
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|attribute
argument_list|(
name|qname
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|comment
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
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|comment
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|cdataSection
parameter_list|(
name|char
index|[]
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|cdataSection
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|documentType
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
name|nextListener
operator|.
name|documentType
argument_list|(
name|name
argument_list|,
name|publicId
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|highlightText
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|nextListener
operator|!=
literal|null
condition|)
block|{
comment|//Nothing to do
block|}
block|}
block|}
end_class

end_unit

