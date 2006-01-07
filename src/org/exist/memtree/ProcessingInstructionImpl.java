begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|ProcessingInstruction
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
comment|/** 	 * @param doc 	 * @param nodeNumber 	 */
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.ProcessingInstruction#getTarget() 	 */
specifier|public
name|String
name|getTarget
parameter_list|()
block|{
name|QName
name|qn
init|=
operator|(
name|QName
operator|)
name|document
operator|.
name|namePool
operator|.
name|get
argument_list|(
name|document
operator|.
name|nodeName
index|[
name|nodeNumber
index|]
argument_list|)
decl_stmt|;
return|return
name|qn
operator|!=
literal|null
condition|?
name|qn
operator|.
name|getLocalName
argument_list|()
else|:
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.ProcessingInstruction#getData() 	 */
specifier|public
name|String
name|getData
parameter_list|()
block|{
return|return
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
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.ProcessingInstruction#setData(java.lang.String) 	 */
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
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
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

