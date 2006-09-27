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
name|xquery
operator|.
name|value
package|;
end_package

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

begin_comment
comment|/**  * Wrapper class around a {@link org.exist.dom.QName} value which extends  * {@link org.exist.xquery.value.AtomicValue}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|QNameValue
extends|extends
name|AtomicValue
block|{
specifier|private
name|XQueryContext
name|context
decl_stmt|;
specifier|private
name|QName
name|qname
decl_stmt|;
comment|/**      * Constructs a new QNameValue by parsing the given name using      * the namespace declarations in context.      *       * @param context      * @param name      * @throws XPathException      */
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
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
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
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|name
expr_stmt|;
block|}
comment|/** 	 * @see org.exist.xquery.value.AtomicValue#getType() 	 */
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
comment|/**      * Returns the wrapped QName object.      */
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
comment|/** 	 * @see org.exist.xquery.value.Sequence#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|String
name|prefix
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|qname
operator|.
name|needsNamespaceDecl
argument_list|()
condition|)
block|{
name|prefix
operator|=
name|context
operator|.
name|getPrefixForURI
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
name|qname
operator|.
name|setPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
comment|//throw new XPathException(
comment|//	"namespace " + qname.getNamespaceURI() + " is not defined");
block|}
if|if
condition|(
name|prefix
operator|!=
literal|null
operator|&&
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
return|return
name|prefix
operator|+
literal|':'
operator|+
name|qname
operator|.
name|getLocalName
argument_list|()
return|;
else|else
return|return
name|qname
operator|.
name|getLocalName
argument_list|()
return|;
block|}
comment|/** 	 * @see org.exist.xquery.value.Sequence#convertTo(int) 	 */
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
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
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
comment|/** 	 * @see org.exist.xquery.value.AtomicValue#compareTo(Collator, int, AtomicValue) 	 */
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
name|QNAME
condition|)
block|{
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
operator|>=
literal|0
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot apply operator to QName"
argument_list|)
throw|;
block|}
block|}
else|else
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
comment|/** 	 * @see org.exist.xquery.value.AtomicValue#compareTo(Collator, AtomicValue) 	 */
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
comment|/** 	 * @see org.exist.xquery.value.AtomicValue#max(Collator, AtomicValue) 	 */
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
comment|/** 	 * @see org.exist.xquery.value.Item#conversionPreference(java.lang.Class) 	 */
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
name|QNameValue
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
name|String
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
comment|/** 	 * @see org.exist.xquery.value.Item#toJavaObject(java.lang.Class) 	 */
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
name|QNameValue
operator|.
name|class
argument_list|)
condition|)
return|return
name|this
return|;
if|else if
condition|(
name|target
operator|==
name|String
operator|.
name|class
condition|)
return|return
name|getStringValue
argument_list|()
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
name|qname
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
block|}
end_class

end_unit

