begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  * $Id$  */
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
name|IOException
import|;
end_import

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
name|Arrays
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
name|util
operator|.
name|XMLString
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
name|encodings
operator|.
name|CharacterSet
import|;
end_import

begin_comment
comment|/**  * Write PLAIN TEXT to a writer. This class defines methods similar to SAX.   * It deals with opening and closing tags, writing attributes and so on: they  * are all ignored. Only real content is written!  *  * Note this is an initial version. Code cleanup needed. Original code is  * commented for fast repair.  *  * @author dizzz  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|TEXTWriter
extends|extends
name|XMLWriter
block|{
specifier|protected
specifier|final
specifier|static
name|Properties
name|defaultProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|//    static {
comment|//        defaultProperties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
comment|//    }
specifier|protected
name|Writer
name|writer
init|=
literal|null
decl_stmt|;
specifier|protected
name|CharacterSet
name|charSet
init|=
literal|null
decl_stmt|;
comment|//    protected boolean tagIsOpen = false;
comment|//
comment|//    protected boolean tagIsEmpty = true;
comment|//
comment|//    protected boolean declarationWritten = false;
comment|//
comment|//    protected boolean doctypeWritten = false;
specifier|protected
name|Properties
name|outputProperties
decl_stmt|;
specifier|private
name|char
index|[]
name|charref
init|=
operator|new
name|char
index|[
literal|10
index|]
decl_stmt|;
comment|//    private static boolean[] textSpecialChars;
comment|//
comment|//    private static boolean[] attrSpecialChars;
comment|//    static {
comment|//        textSpecialChars = new boolean[128];
comment|//        Arrays.fill(textSpecialChars, false);
comment|//        textSpecialChars['<'] = true;
comment|//        textSpecialChars['>'] = true;
comment|//        // textSpecialChars['\r'] = true;
comment|//        textSpecialChars['&'] = true;
comment|//
comment|//        attrSpecialChars = new boolean[128];
comment|//        Arrays.fill(attrSpecialChars, false);
comment|//        attrSpecialChars['<'] = true;
comment|//        attrSpecialChars['>'] = true;
comment|//        attrSpecialChars['\r'] = true;
comment|//        attrSpecialChars['\n'] = true;
comment|//        attrSpecialChars['\t'] = true;
comment|//        attrSpecialChars['&'] = true;
comment|//        attrSpecialChars['"'] = true;
comment|//    }
specifier|public
name|TEXTWriter
parameter_list|()
block|{
block|}
specifier|public
name|TEXTWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
comment|/**      * Set the output properties.      *      * @param outputProperties      */
specifier|public
name|void
name|setOutputProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
if|if
condition|(
name|properties
operator|==
literal|null
condition|)
name|outputProperties
operator|=
name|defaultProperties
expr_stmt|;
else|else
name|outputProperties
operator|=
name|properties
expr_stmt|;
name|String
name|encoding
init|=
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|charSet
operator|=
name|CharacterSet
operator|.
name|getCharacterSet
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
comment|/**      * Set a new writer. Calling this method will reset the state of the object.      *      * @param writer      */
specifier|public
name|void
name|setWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
comment|//        tagIsOpen = false;
comment|//        tagIsEmpty = true;
comment|//        declarationWritten = false;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
comment|//        tagIsOpen = false;
comment|//        tagIsEmpty = true;
comment|//        declarationWritten = false;
block|}
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|TransformerException
block|{
block|}
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
comment|//        if (!declarationWritten)
comment|//            writeDeclaration();
comment|//        if (!doctypeWritten)
comment|//            writeDoctype(qname.toString());
comment|//        try {
comment|//            if (tagIsOpen)
comment|//                closeStartTag(false);
comment|//            writer.write('<');
comment|//            writer.write(qname);
comment|//            tagIsOpen = true;
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
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
comment|//        if (!declarationWritten)
comment|//            writeDeclaration();
comment|//        if (!doctypeWritten)
comment|//            writeDoctype(qname.toString());
comment|//        try {
comment|//            if (tagIsOpen)
comment|//                closeStartTag(false);
comment|//            writer.write('<');
comment|//            if (qname.getPrefix() != null&& qname.getPrefix().length()> 0) {
comment|//                writer.write(qname.getPrefix());
comment|//                writer.write(':');
comment|//            }
comment|//            writer.write(qname.getLocalName());
comment|//            tagIsOpen = true;
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
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
comment|//        try {
comment|//            if (tagIsOpen)
comment|//                closeStartTag(true);
comment|//            else {
comment|//                writer.write("</");
comment|//                writer.write(qname);
comment|//                writer.write('>');
comment|//            }
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
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
comment|//        try {
comment|//            if (tagIsOpen)
comment|//                closeStartTag(true);
comment|//            else {
comment|//                writer.write("</");
comment|//                if (qname.getPrefix() != null&& qname.getPrefix().length()> 0) {
comment|//                    writer.write(qname.getPrefix());
comment|//                    writer.write(':');
comment|//                }
comment|//                writer.write(qname.getLocalName());
comment|//                writer.write('>');
comment|//            }
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
specifier|public
name|void
name|namespace
parameter_list|(
name|String
name|prefix
parameter_list|,
name|String
name|nsURI
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|//        if ((nsURI == null || nsURI.length() == 0)
comment|//&& (prefix == null || prefix.length() == 0))
comment|//            return;
comment|//        try {
comment|//            if (!tagIsOpen)
comment|//                throw new TransformerException(
comment|//                        "Found a namespace declaration outside an element");
comment|//            writer.write(' ');
comment|//            writer.write("xmlns");
comment|//            if (prefix != null&& prefix.length()> 0) {
comment|//                writer.write(':');
comment|//                writer.write(prefix);
comment|//            }
comment|//            writer.write("=\"");
comment|//            writeChars(nsURI, true);
comment|//            writer.write('"');
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
specifier|public
name|void
name|attribute
parameter_list|(
name|String
name|qname
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|//        try {
comment|//            if (!tagIsOpen) {
comment|//                characters(value);
comment|//                return;
comment|//                // throw new TransformerException("Found an attribute outside an
comment|//                // element");
comment|//            }
comment|//            writer.write(' ');
comment|//            writer.write(qname);
comment|//            writer.write("=\"");
comment|//            writeChars(value, true);
comment|//            writer.write('"');
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
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
name|TransformerException
block|{
comment|//        try {
comment|//            if (!tagIsOpen) {
comment|//                characters(value);
comment|//                return;
comment|//                // throw new TransformerException("Found an attribute outside an
comment|//                // element");
comment|//            }
comment|//            writer.write(' ');
comment|//            if (qname.getPrefix() != null&& qname.getPrefix().length()> 0) {
comment|//                writer.write(qname.getPrefix());
comment|//                writer.write(':');
comment|//            }
comment|//            writer.write(qname.getLocalName());
comment|//            writer.write("=\"");
comment|//            writeChars(value, true);
comment|//            writer.write('"');
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
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
comment|//        if (!declarationWritten)
comment|//            writeDeclaration();
try|try
block|{
comment|//            if (tagIsOpen)
comment|//                closeStartTag(false);
name|writeChars
argument_list|(
name|chars
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
name|len
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|//        if (!declarationWritten)
comment|//            writeDeclaration();
name|XMLString
name|s
init|=
operator|new
name|XMLString
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|characters
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|s
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
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
comment|//        if (!declarationWritten)
comment|//            writeDeclaration();
comment|//        try {
comment|//            if (tagIsOpen)
comment|//                closeStartTag(false);
comment|//            writer.write("<?");
comment|//            writer.write(target);
comment|//            if (data != null&& data.length()> 0) {
comment|//                writer.write(' ');
comment|//                writeChars(data, false);
comment|//            }
comment|//            writer.write("?>");
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
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
comment|//        if (!declarationWritten)
comment|//            writeDeclaration();
comment|//        try {
comment|//            if (tagIsOpen)
comment|//                closeStartTag(false);
comment|//            writer.write("<!--");
comment|//            writeChars(data, false);
comment|//            writer.write("-->");
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
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
name|TransformerException
block|{
comment|//        if (tagIsOpen)
comment|//            closeStartTag(false);
try|try
block|{
comment|//            writer.write("<![CDATA[");
name|writer
operator|.
name|write
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|//            writer.write("]]>");
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
comment|//        if (!declarationWritten)
comment|//            writeDeclaration();
comment|//
comment|//        if (publicId == null&& systemId == null)
comment|//            return;
comment|//
comment|//        try {
comment|//            writer.write("<!DOCTYPE ");
comment|//            writer.write(name);
comment|//            if (publicId != null) {
comment|//                writer.write(" PUBLIC \"" + publicId + "\"");
comment|//            }
comment|//            if (systemId != null) {
comment|//                if (publicId == null)
comment|//                    writer.write(" SYSTEM");
comment|//                writer.write(" \"" + systemId + "\"");
comment|//            }
comment|//            writer.write(">");
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
comment|//        doctypeWritten = true;
block|}
specifier|protected
name|void
name|closeStartTag
parameter_list|(
name|boolean
name|isEmpty
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|//        try {
comment|//            if (tagIsOpen) {
comment|//                if (isEmpty)
comment|//                    writer.write("/>");
comment|//                else
comment|//                    writer.write('>');
comment|//                tagIsOpen = false;
comment|//            }
comment|//        } catch (IOException e) {
comment|//            throw new TransformerException(e.getMessage(), e);
comment|//        }
block|}
specifier|protected
name|void
name|writeDeclaration
parameter_list|()
throws|throws
name|TransformerException
block|{
comment|//        if (declarationWritten)
comment|//            return;
comment|//        if (outputProperties == null)
comment|//            outputProperties = defaultProperties;
comment|//        declarationWritten = true;
comment|//        String omitXmlDecl = outputProperties.getProperty(
comment|//                OutputKeys.OMIT_XML_DECLARATION, "yes");
comment|//        if (omitXmlDecl.equals("no")) {
comment|//            String version = outputProperties.getProperty(OutputKeys.VERSION, "1.0");
comment|//            String standalone = outputProperties.getProperty(OutputKeys.STANDALONE);
comment|//            String encoding = outputProperties.getProperty(OutputKeys.ENCODING,
comment|//                    "UTF-8");
comment|//            try {
comment|//                writer.write("<?xml version=\"");
comment|//                writer.write(version);
comment|//                writer.write("\" encoding=\"");
comment|//                writer.write(encoding);
comment|//                writer.write('"');
comment|//                if (standalone != null) {
comment|//                    writer.write(" standalone=\"");
comment|//                    writer.write(standalone);
comment|//                    writer.write('"');
comment|//                }
comment|//                writer.write("?>\n");
comment|//            } catch (IOException e) {
comment|//                throw new TransformerException(e.getMessage(), e);
comment|//            }
comment|//        }
block|}
specifier|protected
name|void
name|writeDoctype
parameter_list|(
name|String
name|rootElement
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|//        if (doctypeWritten)
comment|//            return;
comment|//        String publicId = outputProperties.getProperty(OutputKeys.DOCTYPE_PUBLIC);
comment|//        String systemId = outputProperties.getProperty(OutputKeys.DOCTYPE_SYSTEM);
comment|//        if (publicId != null || systemId != null)
comment|//            documentType(rootElement, publicId, systemId);
comment|//        doctypeWritten = true;
block|}
specifier|private
specifier|final
name|void
name|writeChars
parameter_list|(
name|CharSequence
name|s
parameter_list|,
name|boolean
name|inAttribute
parameter_list|)
throws|throws
name|IOException
block|{
comment|//        boolean[] specialChars = inAttribute ? attrSpecialChars
comment|//                : textSpecialChars;
name|char
name|ch
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|,
name|i
decl_stmt|;
while|while
condition|(
name|pos
operator|<
name|len
condition|)
block|{
name|i
operator|=
name|pos
expr_stmt|;
comment|// TODO: I am not sure about this loop.
while|while
condition|(
name|i
operator|<
name|len
condition|)
block|{
name|ch
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
comment|//                if (ch< 128) {
comment|//                    if (specialChars[ch])
comment|//                        break;
comment|//                    else
comment|//                        i++;
comment|//                } else if (!charSet.inCharacterSet(ch) || ch == 160)
comment|//                    break;
comment|//                else
name|i
operator|++
expr_stmt|;
block|}
name|writeCharSeq
argument_list|(
name|s
argument_list|,
name|pos
argument_list|,
name|i
argument_list|)
expr_stmt|;
comment|// writer.write(s.subSequence(pos, i).toString());
if|if
condition|(
name|i
operator|>=
name|len
condition|)
return|return;
comment|//            switch (ch) {
comment|//                case '<':
comment|//                    writer.write("&lt;");
comment|//                    break;
comment|//                case '>':
comment|//                    writer.write("&gt;");
comment|//                    break;
comment|//                case '&':
comment|//                    writer.write("&amp;");
comment|//                    break;
comment|//                case '\r':
comment|//                    writer.write("&#xD;");
comment|//                    break;
comment|//                case '\n':
comment|//                    writer.write("&#xA;");
comment|//                    break;
comment|//                case '\t':
comment|//                    writer.write("&#x9;");
comment|//                    break;
comment|//                case '"':
comment|//                    writer.write("&#34;");
comment|//                    break;
comment|//                    // non-breaking space:
comment|//                case 160:
comment|//                    writer.write("&#160;");
comment|//                    break;
comment|//                default:
comment|//                    writeCharacterReference(ch);
comment|//            }
name|writeCharacterReference
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|pos
operator|=
operator|++
name|i
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|writeCharSeq
parameter_list|(
name|CharSequence
name|ch
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|ch
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeCharacterReference
parameter_list|(
name|char
name|charval
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|o
init|=
literal|0
decl_stmt|;
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|'&'
expr_stmt|;
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|'#'
expr_stmt|;
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|'x'
expr_stmt|;
name|String
name|code
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|charval
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|code
operator|.
name|length
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|len
condition|;
name|k
operator|++
control|)
block|{
name|charref
index|[
name|o
operator|++
index|]
operator|=
name|code
operator|.
name|charAt
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
name|charref
index|[
name|o
operator|++
index|]
operator|=
literal|';'
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|charref
argument_list|,
literal|0
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

