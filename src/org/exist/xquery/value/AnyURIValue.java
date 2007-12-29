begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|Constants
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
name|functions
operator|.
name|FunEscapeURI
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|AnyURIValue
extends|extends
name|AtomicValue
block|{
specifier|static
name|BitSet
name|needEncoding
decl_stmt|;
specifier|static
specifier|final
name|int
name|caseDiff
init|=
operator|(
literal|'a'
operator|-
literal|'A'
operator|)
decl_stmt|;
static|static
block|{
name|needEncoding
operator|=
operator|new
name|BitSet
argument_list|(
literal|128
argument_list|)
expr_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0x00
init|;
name|i
operator|<=
literal|0x1F
condition|;
name|i
operator|++
control|)
block|{
name|needEncoding
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|needEncoding
operator|.
name|set
argument_list|(
literal|0x7F
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|0x20
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'<'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'>'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'|'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'^'
argument_list|)
expr_stmt|;
name|needEncoding
operator|.
name|set
argument_list|(
literal|'`'
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
specifier|final
name|AnyURIValue
name|EMPTY_URI
init|=
operator|new
name|AnyURIValue
argument_list|()
decl_stmt|;
comment|/* Very important - this string does not need to be a valid uri. 	 *  	 * From XML Linking (see below for link), with some wording changes: 	 * The value of the [anyURI] must be a URI reference as defined in 	 * [IETF RFC 2396], or must result in a URI reference after the escaping 	 * procedure described below is applied. The procedure is applied when 	 * passing the URI reference to a URI resolver. 	 *  	 * Some characters are disallowed in URI references, even if they are 	 * allowed in XML; the disallowed characters include all non-ASCII 	 * characters, plus the excluded characters listed in Section 2.4 of 	 * [IETF RFC 2396], except for the number sign (#) and percent sign (%) 	 * and the square bracket characters re-allowed in [IETF RFC 2732]. 	 * Disallowed characters must be escaped as follows: 	 * 1. Each disallowed character is converted to UTF-8 [IETF RFC 2279] 	 *    as one or more bytes. 	 * 2. Any bytes corresponding to a disallowed character are escaped 	 *    with the URI escaping mechanism (that is, converted to %HH, 	 *    where HH is the hexadecimal notation of the byte value). 	 * 3. The original character is replaced by the resulting character 	 *    sequence. 	 *  	 * See Section 5.4 of XML Linking: 	 * http://www.w3.org/TR/2000/PR-xlink-20001220/#link-locators 	 */
specifier|private
name|String
name|uri
decl_stmt|;
comment|//TODO: save escaped(URI) version?
name|AnyURIValue
parameter_list|()
block|{
name|this
operator|.
name|uri
operator|=
literal|""
expr_stmt|;
block|}
specifier|public
name|AnyURIValue
parameter_list|(
name|URI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|AnyURIValue
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
specifier|public
name|AnyURIValue
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|escapedString
init|=
name|escape
argument_list|(
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|URI
argument_list|(
name|escapedString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
try|try
block|{
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|escapedString
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: the given string '"
operator|+
name|s
operator|+
literal|"' cannot be cast to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/* 		The URI value is whitespace normalized according to the rules for the xs:anyURI type in [XML Schema].<xs:simpleType name="anyURI" id="anyURI"> 			...<xs:restriction base="xs:anySimpleType"><xs:whiteSpace fixed="true" value="collapse" id="anyURI.whiteSpace"/></xs:restriction></xs:simpleType> 		*/
comment|//TODO : find a way to perform the 3 operations at the same time
comment|//s = StringValue.expand(s); //Should we have character entities
name|s
operator|=
name|StringValue
operator|.
name|normalizeWhitespace
argument_list|(
name|s
argument_list|)
expr_stmt|;
comment|//Should we have TABs, new lines...
name|this
operator|.
name|uri
operator|=
name|StringValue
operator|.
name|collapseWhitespace
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * This function accepts a String representation of an xs:anyURI and applies 	 * the escaping method described in Section 5.4 of XML Linking (http://www.w3.org/TR/2000/PR-xlink-20001220/#link-locators) 	 * to turn it into a valid URI 	 *           * @see<a href="http://www.w3.org/TR/2000/PR-xlink-20001220/#link-locators">http://www.w3.org/TR/2000/PR-xlink-20001220/#link-locators</A> 	 * @param uri The xs:anyURI to escape into a valid URI 	 * @return An escaped string representation of the provided xs:anyURI 	 */
specifier|public
specifier|static
name|String
name|escape
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
return|return
name|FunEscapeURI
operator|.
name|escape
argument_list|(
name|uri
argument_list|,
literal|false
argument_list|)
return|;
comment|//TODO: TEST TEST TEST!
comment|//			// basically copied from URLEncoder.encode
comment|//		try {
comment|//			boolean needToChange = false;
comment|//			boolean wroteUnencodedChar = false;
comment|//			int maxBytesPerChar = 10; // rather arbitrary limit, but safe for now
comment|//			StringBuffer out = new StringBuffer(uri.length());
comment|//			ByteArrayOutputStream buf = new ByteArrayOutputStream(maxBytesPerChar);
comment|//
comment|//			OutputStreamWriter writer = new OutputStreamWriter(buf, "UTF-8");
comment|//
comment|//			for (int i = 0; i< uri.length(); i++) {
comment|//				int c = (int) uri.charAt(i);
comment|//				if (c>127 || needEncoding.get(c)) {
comment|//					try {
comment|//						if (wroteUnencodedChar) { // Fix for 4407610
comment|//							writer = new OutputStreamWriter(buf, "UTF-8");
comment|//							wroteUnencodedChar = false;
comment|//						}
comment|//						writer.write(c);
comment|//						/*
comment|//						 * If this character represents the start of a Unicode
comment|//						 * surrogate pair, then pass in two characters. It's not
comment|//						 * clear what should be done if a bytes reserved in the
comment|//						 * surrogate pairs range occurs outside of a legal
comment|//						 * surrogate pair. For now, just treat it as if it were
comment|//						 * any other character.
comment|//						 */
comment|//						if (c>= 0xD800&& c<= 0xDBFF) {
comment|//							/*
comment|//							 System.out.println(Integer.toHexString(c)
comment|//							 + " is high surrogate");
comment|//							 */
comment|//							if ( (i+1)< uri.length()) {
comment|//								int d = (int) uri.charAt(i+1);
comment|//								/*
comment|//								 System.out.println("\tExamining "
comment|//								 + Integer.toHexString(d));
comment|//								 */
comment|//								if (d>= 0xDC00&& d<= 0xDFFF) {
comment|//									/*
comment|//									 System.out.println("\t"
comment|//									 + Integer.toHexString(d)
comment|//									 + " is low surrogate");
comment|//									 */
comment|//									writer.write(d);
comment|//									i++;
comment|//								}
comment|//							}
comment|//						}
comment|//						writer.flush();
comment|//					} catch(IOException e) {
comment|//						buf.reset();
comment|//						continue;
comment|//					}
comment|//					byte[] ba = buf.toByteArray();
comment|//					for (int j = 0; j< ba.length; j++) {
comment|//						out.append('%');
comment|//						char ch = Character.forDigit((ba[j]>> 4)& 0xF, 16);
comment|//						// converting to use uppercase letter as part of
comment|//						// the hex value if ch is a letter.
comment|//						if (Character.isLetter(ch)) {
comment|//							ch -= caseDiff;
comment|//						}
comment|//						out.append(ch);
comment|//						ch = Character.forDigit(ba[j]& 0xF, 16);
comment|//						if (Character.isLetter(ch)) {
comment|//							ch -= caseDiff;
comment|//						}
comment|//						out.append(ch);
comment|//					}
comment|//					buf.reset();
comment|//					needToChange = true;
comment|//				} else {
comment|//					out.append((char)c);
comment|//					wroteUnencodedChar = true;
comment|//				}
comment|//			}
comment|//
comment|//			return (needToChange? out.toString() : uri);
comment|//		} catch(UnsupportedEncodingException e) {
comment|//			throw new RuntimeException(e);
comment|//		}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ANY_URI
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|uri
return|;
block|}
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
comment|// If its operand is a singleton value of type xs:string, xs:anyURI, xs:untypedAtomic,
comment|//or a type derived from one of these, fn:boolean returns false if the operand value has zero length; otherwise it returns true.
return|return
name|uri
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#convertTo(int) 	 */
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|requiredType
condition|)
block|{
case|case
name|Type
operator|.
name|ITEM
case|:
case|case
name|Type
operator|.
name|ATOMIC
case|:
case|case
name|Type
operator|.
name|ANY_URI
case|:
return|return
name|this
return|;
case|case
name|Type
operator|.
name|STRING
case|:
return|return
operator|new
name|StringValue
argument_list|(
name|uri
argument_list|)
return|;
case|case
name|Type
operator|.
name|UNTYPED_ATOMIC
case|:
return|return
operator|new
name|UntypedAtomicValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot cast xs:anyURI to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#compareTo(int, org.exist.xquery.value.AtomicValue) 	 */
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|int
name|operator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ANY_URI
condition|)
block|{
name|String
name|otherURI
init|=
name|other
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|int
name|cmp
init|=
name|uri
operator|.
name|compareTo
argument_list|(
name|otherURI
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|Constants
operator|.
name|EQ
case|:
return|return
name|cmp
operator|==
literal|0
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
name|cmp
operator|!=
literal|0
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
name|cmp
operator|>
literal|0
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
name|cmp
operator|>=
literal|0
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
name|cmp
operator|<
literal|0
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
name|cmp
operator|<=
literal|0
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XPTY0004: cannot apply operator "
operator|+
name|Constants
operator|.
name|OPS
index|[
name|operator
index|]
operator|+
literal|" to xs:anyURI"
argument_list|)
throw|;
block|}
block|}
else|else
return|return
name|compareTo
argument_list|(
name|collator
argument_list|,
name|operator
argument_list|,
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#compareTo(org.exist.xquery.value.AtomicValue) 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ANY_URI
condition|)
block|{
name|String
name|otherURI
init|=
name|other
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
return|return
name|uri
operator|.
name|compareTo
argument_list|(
name|otherURI
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|compareTo
argument_list|(
name|collator
argument_list|,
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#max(org.exist.xquery.value.AtomicValue) 	 */
specifier|public
name|AtomicValue
name|max
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"max is not supported for values of type xs:anyURI"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#min(org.exist.xquery.value.AtomicValue) 	 */
specifier|public
name|AtomicValue
name|min
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"min is not supported for values of type xs:anyURI"
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#conversionPreference(java.lang.Class) 	 */
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|AnyURIValue
operator|.
name|class
argument_list|)
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|javaClass
operator|==
name|XmldbURI
operator|.
name|class
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|javaClass
operator|==
name|URI
operator|.
name|class
condition|)
return|return
literal|2
return|;
if|if
condition|(
name|javaClass
operator|==
name|URL
operator|.
name|class
condition|)
return|return
literal|3
return|;
if|if
condition|(
name|javaClass
operator|==
name|String
operator|.
name|class
operator|||
name|javaClass
operator|==
name|CharSequence
operator|.
name|class
condition|)
return|return
literal|4
return|;
if|if
condition|(
name|javaClass
operator|==
name|Object
operator|.
name|class
condition|)
return|return
literal|20
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#toJavaObject(java.lang.Class) 	 */
specifier|public
name|Object
name|toJavaObject
parameter_list|(
name|Class
name|target
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|AnyURIValue
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|XmldbURI
operator|.
name|class
condition|)
block|{
return|return
name|toXmldbURI
argument_list|()
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|URI
operator|.
name|class
condition|)
block|{
return|return
name|toURI
argument_list|()
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|URL
operator|.
name|class
condition|)
block|{
try|try
block|{
return|return
operator|new
name|URL
argument_list|(
name|uri
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"failed to convert "
operator|+
name|uri
operator|+
literal|" into a Java URL: "
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
block|}
if|else if
condition|(
name|target
operator|==
name|String
operator|.
name|class
operator|||
name|target
operator|==
name|CharSequence
operator|.
name|class
condition|)
return|return
name|uri
return|;
if|else if
condition|(
name|target
operator|==
name|Object
operator|.
name|class
condition|)
return|return
name|uri
return|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert value of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to Java object of type "
operator|+
name|target
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|public
name|XmldbURI
name|toXmldbURI
parameter_list|()
throws|throws
name|XPathException
block|{
try|try
block|{
return|return
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
argument_list|,
literal|false
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"failed to convert "
operator|+
name|uri
operator|+
literal|" into an XmldbURI: "
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
block|}
specifier|public
name|URI
name|toURI
parameter_list|()
throws|throws
name|XPathException
block|{
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
name|escape
argument_list|(
name|uri
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"failed to convert "
operator|+
name|uri
operator|+
literal|" into an URI: "
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
block|}
block|}
end_class

end_unit

