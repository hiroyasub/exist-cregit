begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: LocationImpl.java 11737 2010-05-02 21:25:21Z ixitar $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|model
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|LocationImpl
implements|implements
name|Location
block|{
specifier|private
name|String
name|fileURI
decl_stmt|;
specifier|private
name|int
name|beginColumn
decl_stmt|;
specifier|private
name|int
name|beginLine
decl_stmt|;
comment|//	private int endColumn;
comment|//	private int endLine;
specifier|public
name|LocationImpl
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|NamedNodeMap
name|attrs
init|=
name|node
operator|.
name|getAttributes
argument_list|()
decl_stmt|;
name|Integer
name|level
init|=
literal|null
decl_stmt|;
comment|//UNDERSTAND: is level required???
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
if|if
condition|(
name|attr
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"lineno"
argument_list|)
condition|)
block|{
name|beginLine
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attr
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"filename"
argument_list|)
condition|)
block|{
name|fileURI
operator|=
name|attr
operator|.
name|getNodeValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"level"
argument_list|)
condition|)
block|{
name|level
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|attr
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"cmdbegin"
argument_list|)
condition|)
block|{
name|String
index|[]
name|begin
init|=
name|attr
operator|.
name|getNodeValue
argument_list|()
operator|.
name|split
argument_list|(
literal|":"
argument_list|)
decl_stmt|;
name|beginColumn
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|begin
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.model.Location#getColumnBegin() 	 */
specifier|public
name|int
name|getColumnBegin
parameter_list|()
block|{
return|return
name|beginColumn
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.model.Location#getFileURI() 	 */
specifier|public
name|String
name|getFileURI
parameter_list|()
block|{
return|return
name|fileURI
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debugger.model.Location#getLineBegin() 	 */
specifier|public
name|int
name|getLineBegin
parameter_list|()
block|{
return|return
name|beginLine
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|""
operator|+
name|beginLine
operator|+
literal|":"
operator|+
name|beginColumn
operator|+
literal|"@"
operator|+
name|fileURI
return|;
block|}
block|}
end_class

end_unit

