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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
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
name|xquery
operator|.
name|Constants
operator|.
name|Comparison
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
name|ErrorCodes
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
name|XQueryContext
import|;
end_import

begin_comment
comment|/**  * Wrapper class around a {@link org.exist.dom.QName} value which extends  * {@link org.exist.xquery.value.AtomicValue}.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|QNameValue
extends|extends
name|AtomicValue
block|{
specifier|private
specifier|final
name|QName
name|qname
decl_stmt|;
specifier|private
specifier|final
name|String
name|stringValue
decl_stmt|;
comment|/**      * Constructs a new QNameValue by parsing the given name using      * the namespace declarations in context.      *      * @param context current context      * @param name name string to parse into QName      * @throws XPathException in case of dynamic error      */
specifier|public
name|QNameValue
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
literal|"An empty string is not a valid lexical representation of xs:QName."
argument_list|)
throw|;
block|}
try|try
block|{
name|qname
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|name
argument_list|,
name|context
operator|.
name|getURIForPrefix
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|iqe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"No namespace defined for prefix "
operator|+
name|name
argument_list|)
throw|;
block|}
name|stringValue
operator|=
name|computeStringValue
argument_list|()
expr_stmt|;
block|}
specifier|public
name|QNameValue
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|QName
name|name
parameter_list|)
block|{
name|this
operator|.
name|qname
operator|=
name|name
expr_stmt|;
name|stringValue
operator|=
name|computeStringValue
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.exist.xquery.value.AtomicValue#getType()      */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|QNAME
return|;
block|}
comment|/**      * Returns the wrapped QName object.      *      * @return the wrapped QName      */
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
comment|/**      * @see org.exist.xquery.value.Sequence#getStringValue()      */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//TODO : previous approach was to resolve the qname when needed. We now try to keep the original qname
return|return
name|stringValue
return|;
block|}
specifier|private
name|String
name|computeStringValue
parameter_list|()
block|{
comment|//TODO : previous approach was to resolve the qname when needed. We now try to keep the original qname
specifier|final
name|String
name|prefix
init|=
name|qname
operator|.
name|getPrefix
argument_list|()
decl_stmt|;
comment|//Not clear what to work with here...
comment|// WM: Changing the prefix is problematic (e.g. if a module
comment|// defines different prefixes than the main module). We should
comment|// keep the current in-scope prefix.
comment|//	    if((prefix == null || "".equals(prefix))&& qname.hasNamespace()) {
comment|//	    	prefix = context.getPrefixForURI(qname.getNamespaceURI());
comment|//			if (prefix != null)
comment|//				qname.setPrefix(prefix);
comment|//				//throw new XPathException(
comment|//				//	"namespace " + qname.getNamespaceURI() + " is not defined");
comment|//
comment|//	    }
comment|//TODO : check that the prefix matches the URI in the current context ?
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
operator|!
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|prefix
operator|+
literal|':'
operator|+
name|qname
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|qname
operator|.
name|getLocalPart
argument_list|()
return|;
block|}
block|}
comment|/**      * @see org.exist.xquery.value.Sequence#convertTo(int)      */
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
name|ATOMIC
case|:
case|case
name|Type
operator|.
name|ITEM
case|:
case|case
name|Type
operator|.
name|QNAME
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
name|getStringValue
argument_list|()
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
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
literal|"A QName cannot be converted to "
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
annotation|@
name|Override
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|Comparison
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
name|QNAME
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|qname
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|QNameValue
operator|)
name|other
operator|)
operator|.
name|qname
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|EQ
case|:
return|return
name|cmp
operator|==
literal|0
return|;
case|case
name|NEQ
case|:
return|return
name|cmp
operator|!=
literal|0
return|;
comment|/* 				 * QNames are unordered 				case GT : 					return cmp> 0; 				case GTEQ : 					return cmp>= 0; 				case LT : 					return cmp< 0; 				case LTEQ : 					return cmp>= 0; 				*/
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"cannot apply operator to QName"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Type error: cannot compare QName to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.exist.xquery.value.AtomicValue#compareTo(Collator, AtomicValue)      */
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
name|QNAME
condition|)
block|{
return|return
name|qname
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|QNameValue
operator|)
name|other
operator|)
operator|.
name|qname
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare QName to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see org.exist.xquery.value.AtomicValue#max(Collator, AtomicValue)      */
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
literal|"Invalid argument to aggregate function: QName"
argument_list|)
throw|;
block|}
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
literal|"Invalid argument to aggregate function: QName"
argument_list|)
throw|;
block|}
comment|/**      * @see org.exist.xquery.value.Item#conversionPreference(java.lang.Class)      */
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|QNameValue
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
if|if
condition|(
name|javaClass
operator|==
name|String
operator|.
name|class
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|javaClass
operator|==
name|Object
operator|.
name|class
condition|)
block|{
return|return
literal|20
return|;
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/**      * @see org.exist.xquery.value.Item#toJavaObject(java.lang.Class)      */
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|toJavaObject
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
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
name|QNameValue
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|String
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|getStringValue
argument_list|()
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|Object
operator|.
name|class
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|qname
return|;
block|}
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
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
return|return
name|this
operator|.
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0006
argument_list|,
literal|"value of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" has no boolean value."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|instanceof
name|QNameValue
condition|)
block|{
return|return
operator|(
operator|(
name|QNameValue
operator|)
name|obj
operator|)
operator|.
name|qname
operator|.
name|equals
argument_list|(
name|qname
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|qname
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

