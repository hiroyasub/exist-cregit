begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|memtree
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|DOMException
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
name|ProcessingInstruction
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
name|persistent
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
name|xquery
operator|.
name|NodeTest
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
name|value
operator|.
name|AtomicValue
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

begin_class
specifier|public
class|class
name|ProcessingInstructionImpl
extends|extends
name|NodeImpl
implements|implements
name|ProcessingInstruction
block|{
comment|/**      * Creates a new ProcessingInstructionImpl object.      *      * @param  doc      * @param  nodeNumber      */
specifier|public
name|ProcessingInstructionImpl
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|int
name|nodeNumber
parameter_list|)
block|{
name|super
argument_list|(
name|doc
argument_list|,
name|nodeNumber
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.w3c.dom.ProcessingInstruction#getTarget()      */
specifier|public
name|String
name|getTarget
parameter_list|()
block|{
specifier|final
name|QName
name|qn
init|=
name|document
operator|.
name|nodeName
index|[
name|nodeNumber
index|]
decl_stmt|;
return|return
operator|(
operator|(
name|qn
operator|!=
literal|null
operator|)
condition|?
name|qn
operator|.
name|getLocalName
argument_list|()
else|:
literal|null
operator|)
return|;
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
comment|// TODO: this could be optimized
return|return
operator|(
name|getData
argument_list|()
operator|.
name|replaceFirst
argument_list|(
literal|"^\\s+"
argument_list|,
literal|""
argument_list|)
operator|)
return|;
block|}
specifier|public
name|String
name|getLocalName
parameter_list|()
block|{
return|return
operator|(
name|getTarget
argument_list|()
operator|)
return|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
operator|(
literal|""
operator|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.w3c.dom.ProcessingInstruction#getData()      */
specifier|public
name|String
name|getData
parameter_list|()
block|{
return|return
operator|(
operator|new
name|String
argument_list|(
name|document
operator|.
name|characters
argument_list|,
name|document
operator|.
name|alpha
index|[
name|nodeNumber
index|]
argument_list|,
name|document
operator|.
name|alphaLen
index|[
name|nodeNumber
index|]
argument_list|)
operator|)
return|;
block|}
specifier|public
name|String
name|getNodeValue
parameter_list|()
throws|throws
name|DOMException
block|{
return|return
name|getData
argument_list|()
return|;
block|}
specifier|public
name|AtomicValue
name|atomize
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
operator|new
name|StringValue
argument_list|(
name|getData
argument_list|()
argument_list|)
operator|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.w3c.dom.ProcessingInstruction#setData(java.lang.String)      */
specifier|public
name|void
name|setData
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
block|}
comment|/**      * ? @see org.w3c.dom.Node#getBaseURI()      *      * @return  DOCUMENT ME!      */
specifier|public
name|String
name|getBaseURI
parameter_list|()
block|{
name|String
name|baseURI
init|=
literal|""
decl_stmt|;
name|int
name|parent
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|test
init|=
operator|-
literal|1
decl_stmt|;
name|test
operator|=
name|document
operator|.
name|getParentNodeFor
argument_list|(
name|nodeNumber
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|nodeKind
index|[
name|test
index|]
operator|!=
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
name|parent
operator|=
name|test
expr_stmt|;
block|}
comment|// fixme! Testa med 0/ljo
while|while
condition|(
operator|(
name|parent
operator|!=
operator|-
literal|1
operator|)
operator|&&
operator|(
name|document
operator|.
name|getNode
argument_list|(
name|parent
argument_list|)
operator|.
name|getBaseURI
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|baseURI
argument_list|)
condition|)
block|{
name|baseURI
operator|=
name|document
operator|.
name|getNode
argument_list|(
name|parent
argument_list|)
operator|.
name|getBaseURI
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|baseURI
operator|=
name|document
operator|.
name|getNode
argument_list|(
name|parent
argument_list|)
operator|.
name|getBaseURI
argument_list|()
operator|+
literal|"/"
operator|+
name|baseURI
expr_stmt|;
block|}
name|test
operator|=
name|document
operator|.
name|getParentNodeFor
argument_list|(
name|parent
argument_list|)
expr_stmt|;
if|if
condition|(
name|document
operator|.
name|nodeKind
index|[
name|test
index|]
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
return|return
operator|(
name|baseURI
operator|)
return|;
block|}
else|else
block|{
name|parent
operator|=
name|test
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|baseURI
argument_list|)
condition|)
block|{
name|baseURI
operator|=
name|getDocument
argument_list|()
operator|.
name|getBaseURI
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|baseURI
operator|)
return|;
block|}
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
comment|//No child
return|return
operator|(
literal|null
operator|)
return|;
block|}
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
operator|(
name|Type
operator|.
name|PROCESSING_INSTRUCTION
operator|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"in-memory#"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"processing-instruction {"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getTarget
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"} {"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getData
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"} "
argument_list|)
expr_stmt|;
return|return
operator|(
name|result
operator|.
name|toString
argument_list|()
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|selectAttributes
parameter_list|(
name|NodeTest
name|test
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|void
name|selectChildren
parameter_list|(
name|NodeTest
name|test
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|void
name|selectDescendantAttributes
parameter_list|(
name|NodeTest
name|test
parameter_list|,
name|Sequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

