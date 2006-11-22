begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Text
import|;
end_import

begin_class
specifier|public
class|class
name|TextImpl
extends|extends
name|NodeImpl
implements|implements
name|Text
block|{
specifier|public
name|TextImpl
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
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
comment|//Quick and (not so ?) dirty...
return|return
name|getData
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Text#splitText(int) 	 */
specifier|public
name|Text
name|splitText
parameter_list|(
name|int
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#getData() 	 */
specifier|public
name|String
name|getData
parameter_list|()
throws|throws
name|DOMException
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#setData(java.lang.String) 	 */
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
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#getLength() 	 */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|document
operator|.
name|alphaLen
index|[
name|nodeNumber
index|]
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.Node#getNodeValue() 	 */
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
name|String
name|getLocalName
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
literal|""
return|;
block|}
specifier|public
name|Node
name|getFirstChild
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#substringData(int, int) 	 */
specifier|public
name|String
name|substringData
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#appendData(java.lang.String) 	 */
specifier|public
name|void
name|appendData
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#insertData(int, java.lang.String) 	 */
specifier|public
name|void
name|insertData
parameter_list|(
name|int
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#deleteData(int, int) 	 */
specifier|public
name|void
name|deleteData
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.w3c.dom.CharacterData#replaceData(int, int, java.lang.String) 	 */
specifier|public
name|void
name|replaceData
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|String
name|arg2
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/** ? @see org.w3c.dom.Text#isElementContentWhitespace() 	 */
specifier|public
name|boolean
name|isElementContentWhitespace
parameter_list|()
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|false
return|;
block|}
comment|/** ? @see org.w3c.dom.Text#getWholeText() 	 */
specifier|public
name|String
name|getWholeText
parameter_list|()
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
block|}
comment|/** ? @see org.w3c.dom.Text#replaceWholeText(java.lang.String) 	 */
specifier|public
name|Text
name|replaceWholeText
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|DOMException
block|{
comment|// maybe TODO - new DOM interfaces - Java 5.0
return|return
literal|null
return|;
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
literal|"text {"
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

