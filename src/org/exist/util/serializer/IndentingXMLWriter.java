begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|serializer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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
name|storage
operator|.
name|serializers
operator|.
name|EXistOutputKeys
import|;
end_import

begin_class
specifier|public
class|class
name|IndentingXMLWriter
extends|extends
name|XMLWriter
block|{
specifier|private
name|boolean
name|indent
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|indentAmount
init|=
literal|4
decl_stmt|;
specifier|private
name|String
name|indentChars
init|=
literal|"                                                                                           "
decl_stmt|;
specifier|private
name|int
name|level
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|afterTag
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|sameline
init|=
literal|false
decl_stmt|;
specifier|public
name|IndentingXMLWriter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * @param writer 	 */
specifier|public
name|IndentingXMLWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#setWriter(java.io.Writer) 	 */
specifier|public
name|void
name|setWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|super
operator|.
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|level
operator|=
literal|0
expr_stmt|;
name|afterTag
operator|=
literal|false
expr_stmt|;
name|sameline
operator|=
literal|false
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#startElement(java.lang.String) 	 */
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|afterTag
condition|)
name|indent
argument_list|()
expr_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|level
operator|++
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
name|sameline
operator|=
literal|true
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#startElement(org.exist.dom.QName) 	 */
specifier|public
name|void
name|startElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
if|if
condition|(
name|afterTag
condition|)
name|indent
argument_list|()
expr_stmt|;
name|super
operator|.
name|startElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|level
operator|++
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
name|sameline
operator|=
literal|true
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#endElement() 	 */
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|level
operator|--
expr_stmt|;
if|if
condition|(
name|afterTag
operator|&&
operator|!
name|sameline
condition|)
name|indent
argument_list|()
expr_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|sameline
operator|=
literal|false
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#endElement(org.exist.dom.QName) 	 */
specifier|public
name|void
name|endElement
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|TransformerException
block|{
name|level
operator|--
expr_stmt|;
if|if
condition|(
name|afterTag
operator|&&
operator|!
name|sameline
condition|)
name|indent
argument_list|()
expr_stmt|;
name|super
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|sameline
operator|=
literal|false
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#characters(java.lang.CharSequence) 	 */
specifier|public
name|void
name|characters
parameter_list|(
name|CharSequence
name|chars
parameter_list|)
throws|throws
name|TransformerException
block|{
name|int
name|start
init|=
literal|0
decl_stmt|,
name|length
init|=
name|chars
operator|.
name|length
argument_list|()
decl_stmt|;
comment|//		while (length> 0&& isWhiteSpace(chars.charAt(start))) {
comment|//			--length;
comment|//			if(length> 0)
comment|//				++start;
comment|//		}
comment|//		while (length> 0&& isWhiteSpace(chars.charAt(start + length - 1))) {
comment|//			--length;
comment|//		}
if|if
condition|(
name|length
operator|==
literal|0
condition|)
return|return;
comment|// whitespace only: skip
if|if
condition|(
name|start
operator|>
literal|0
operator|||
name|length
operator|<
name|chars
operator|.
name|length
argument_list|()
condition|)
block|{
name|chars
operator|=
name|chars
operator|.
name|subSequence
argument_list|(
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
comment|// drop whitespace
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
name|chars
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|chars
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'\n'
condition|)
block|{
name|sameline
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|afterTag
operator|=
literal|false
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
name|chars
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#comment(java.lang.String) 	 */
specifier|public
name|void
name|comment
parameter_list|(
name|CharSequence
name|data
parameter_list|)
throws|throws
name|TransformerException
block|{
name|super
operator|.
name|comment
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#processingInstruction(java.lang.String, java.lang.String) 	 */
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
name|TransformerException
block|{
name|super
operator|.
name|processingInstruction
argument_list|(
name|target
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|afterTag
operator|=
literal|true
expr_stmt|;
block|}
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
name|TransformerException
block|{
name|super
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
name|super
operator|.
name|characters
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|sameline
operator|=
literal|false
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.serializer.XMLWriter#setOutputProperties(java.util.Properties) 	 */
specifier|public
name|void
name|setOutputProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|super
operator|.
name|setOutputProperties
argument_list|(
name|properties
argument_list|)
expr_stmt|;
name|String
name|option
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|INDENT_SPACES
argument_list|,
literal|"4"
argument_list|)
decl_stmt|;
try|try
block|{
name|indentAmount
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
block|}
name|indent
operator|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|indent
parameter_list|()
throws|throws
name|TransformerException
block|{
if|if
condition|(
operator|!
name|indent
condition|)
return|return;
name|int
name|spaces
init|=
name|indentAmount
operator|*
name|level
decl_stmt|;
while|while
condition|(
name|spaces
operator|>=
name|indentChars
operator|.
name|length
argument_list|()
condition|)
name|indentChars
operator|+=
name|indentChars
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|super
operator|.
name|characters
argument_list|(
name|indentChars
operator|.
name|subSequence
argument_list|(
literal|0
argument_list|,
name|spaces
argument_list|)
argument_list|)
expr_stmt|;
name|sameline
operator|=
literal|false
expr_stmt|;
block|}
specifier|protected
specifier|final
specifier|static
name|boolean
name|isWhiteSpace
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
operator|(
name|ch
operator|==
literal|0x20
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0x09
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0xD
operator|)
operator|||
operator|(
name|ch
operator|==
literal|0xA
operator|)
return|;
block|}
block|}
end_class

end_unit

