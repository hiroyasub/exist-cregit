begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000,  Wolfgang Meier (wolfgang@exist-db.org)  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|XMLString
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
name|CharacterData
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
name|ext
operator|.
name|LexicalHandler
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
comment|/**  *  Description of the Class  *  *@author     Wolfgang Meier<wolfgang@exist-db.org>  *@created    27. Juni 2002  */
end_comment

begin_class
specifier|public
class|class
name|CharacterDataImpl
extends|extends
name|NodeImpl
implements|implements
name|CharacterData
block|{
specifier|protected
name|XMLString
name|cdata
init|=
literal|null
decl_stmt|;
specifier|public
name|CharacterDataImpl
parameter_list|(
name|short
name|nodeType
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|QName
operator|.
name|TEXT_QNAME
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the CharacterDataImpl object      *      *@param  nodeType  Description of the Parameter      *@param  gid       Description of the Parameter      */
specifier|public
name|CharacterDataImpl
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|long
name|gid
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|QName
operator|.
name|TEXT_QNAME
argument_list|,
name|gid
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the CharacterDataImpl object      *      *@param  nodeType  Description of the Parameter      *@param  gid       Description of the Parameter      *@param  data      Description of the Parameter      */
specifier|public
name|CharacterDataImpl
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|long
name|gid
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|QName
operator|.
name|TEXT_QNAME
argument_list|,
name|gid
argument_list|)
expr_stmt|;
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|data
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the CharacterDataImpl object      *      *@param  nodeType  Description of the Parameter      *@param  data      Description of the Parameter      */
specifier|public
name|CharacterDataImpl
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|String
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|QName
operator|.
name|TEXT_QNAME
argument_list|)
expr_stmt|;
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|data
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Constructor for the CharacterDataImpl object      *      *@param  nodeType  Description of the Parameter      *@param  data      Description of the Parameter      *@param  start     Description of the Parameter      *@param  howmany   Description of the Parameter      */
specifier|public
name|CharacterDataImpl
parameter_list|(
name|short
name|nodeType
parameter_list|,
name|char
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|)
block|{
name|super
argument_list|(
name|nodeType
argument_list|,
name|QName
operator|.
name|TEXT_QNAME
argument_list|)
expr_stmt|;
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|cdata
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  arg               Description of the Parameter      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|appendData
parameter_list|(
name|String
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|arg
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|cdata
operator|.
name|append
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  data              Description of the Parameter      *@param  start             Description of the Parameter      *@param  howmany           Description of the Parameter      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|appendData
parameter_list|(
name|char
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
else|else
name|cdata
operator|.
name|append
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  offset            Description of the Parameter      *@param  count             Description of the Parameter      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|deleteData
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|!=
literal|null
condition|)
name|cdata
operator|.
name|delete
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Gets the data attribute of the CharacterDataImpl object      *      *@return                   The data value      *@exception  DOMException  Description of the Exception      */
specifier|public
name|String
name|getData
parameter_list|()
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
name|cdata
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|XMLString
name|getXMLString
parameter_list|()
block|{
return|return
name|cdata
return|;
block|}
specifier|public
name|String
name|getLowerCaseData
parameter_list|()
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
name|cdata
operator|.
name|toString
argument_list|()
operator|.
name|toLowerCase
argument_list|()
return|;
block|}
comment|/**      *  Gets the length attribute of the CharacterDataImpl object      *      *@return    The length value      */
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|cdata
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**      *  Gets the nodeValue attribute of the CharacterDataImpl object      *      *@return    The nodeValue value      */
specifier|public
name|String
name|getNodeValue
parameter_list|()
block|{
return|return
name|cdata
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  offset            Description of the Parameter      *@param  arg               Description of the Parameter      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|insertData
parameter_list|(
name|int
name|offset
parameter_list|,
name|String
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|arg
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|cdata
operator|.
name|insert
argument_list|(
name|offset
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  offset            Description of the Parameter      *@param  count             Description of the Parameter      *@param  arg               Description of the Parameter      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|replaceData
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|,
name|String
name|arg
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|DOMSTRING_SIZE_ERR
argument_list|,
literal|"string index out of bounds"
argument_list|)
throw|;
name|cdata
operator|.
name|replace
argument_list|(
name|offset
argument_list|,
name|count
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Sets the data attribute of the CharacterDataImpl object      *      *@param  data              The new data value      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|setData
parameter_list|(
name|String
name|data
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|data
operator|.
name|toCharArray
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|cdata
operator|.
name|setData
argument_list|(
name|data
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setData
parameter_list|(
name|XMLString
name|data
parameter_list|)
throws|throws
name|DOMException
block|{
name|cdata
operator|=
name|data
expr_stmt|;
block|}
comment|/**      *  Sets the data attribute of the CharacterDataImpl object      *      *@param  data              The new data value      *@param  start             The new data value      *@param  howmany           The new data value      *@exception  DOMException  Description of the Exception      */
specifier|public
name|void
name|setData
parameter_list|(
name|char
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|howmany
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
name|cdata
operator|=
operator|new
name|XMLString
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
else|else
name|cdata
operator|.
name|setData
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|howmany
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@param  offset            Description of the Parameter      *@param  count             Description of the Parameter      *@return                   Description of the Return Value      *@exception  DOMException  Description of the Exception      */
specifier|public
name|String
name|substringData
parameter_list|(
name|int
name|offset
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|DOMException
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DOMException
argument_list|(
name|DOMException
operator|.
name|DOMSTRING_SIZE_ERR
argument_list|,
literal|"string index out of bounds"
argument_list|)
throw|;
return|return
name|cdata
operator|.
name|substring
argument_list|(
name|offset
argument_list|,
name|count
argument_list|)
return|;
block|}
comment|/**      *  Description of the Method      *      *@param  contentHandler    Description of the Parameter      *@param  lexicalHandler    Description of the Parameter      *@param  first             Description of the Parameter      *@param  prefixes          Description of the Parameter      *@exception  SAXException  Description of the Exception      */
specifier|public
name|void
name|toSAX
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|LexicalHandler
name|lexicalHandler
parameter_list|,
name|boolean
name|first
parameter_list|,
name|Set
name|namespaces
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|first
condition|)
block|{
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"id"
argument_list|,
literal|"exist:id"
argument_list|,
literal|"CDATA"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|gid
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"source"
argument_list|,
literal|"exist:source"
argument_list|,
literal|"CDATA"
argument_list|,
name|ownerDocument
operator|.
name|getFileName
argument_list|()
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|startElement
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"text"
argument_list|,
literal|"exist:text"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
name|char
name|ch
index|[]
init|=
operator|new
name|char
index|[
name|cdata
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
name|cdata
operator|.
name|toString
argument_list|()
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|,
name|ch
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|characters
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|ch
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|first
condition|)
name|contentHandler
operator|.
name|endElement
argument_list|(
literal|"http://exist.sourceforge.net/NS/exist"
argument_list|,
literal|"text"
argument_list|,
literal|"exist:text"
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Description of the Method      *      *@return    Description of the Return Value      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|cdata
operator|==
literal|null
condition|)
return|return
literal|null
return|;
else|else
return|return
name|cdata
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

