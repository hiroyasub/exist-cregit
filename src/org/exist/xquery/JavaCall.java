begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|AccessibleObject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|value
operator|.
name|Item
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
name|JavaObjectValue
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
name|Type
import|;
end_import

begin_comment
comment|/**  * A special function call to a Java method or constructor.  *   * This class handles all function calls who's namespace URI  * starts with "java:".  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|JavaCall
extends|extends
name|Function
block|{
specifier|private
name|QName
name|qname
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Class
name|myClass
init|=
literal|null
decl_stmt|;
specifier|private
name|List
name|candidateMethods
init|=
operator|new
name|ArrayList
argument_list|(
literal|5
argument_list|)
decl_stmt|;
comment|/** 	 * @param context 	 * @param qname the of the function 	 */
specifier|public
name|JavaCall
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
name|String
name|namespaceURI
init|=
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|qname
operator|.
name|getPrefix
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|namespaceURI
operator|.
name|startsWith
argument_list|(
literal|"java:"
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Internal error: prefix "
operator|+
name|qname
operator|.
name|getPrefix
argument_list|()
operator|+
literal|" does not "
operator|+
literal|"resolve to a Java class"
argument_list|)
throw|;
name|namespaceURI
operator|=
name|namespaceURI
operator|.
name|substring
argument_list|(
literal|"java:"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Trying to find class "
operator|+
name|namespaceURI
argument_list|)
expr_stmt|;
name|myClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
name|namespaceURI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Class: "
operator|+
name|namespaceURI
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
name|name
operator|=
name|qname
operator|.
name|getLocalName
argument_list|()
expr_stmt|;
comment|// convert hyphens into camelCase
if|if
condition|(
name|name
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
operator|>
literal|0
condition|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|boolean
name|afterHyphen
init|=
literal|false
decl_stmt|;
name|char
name|ch
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|name
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ch
operator|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'-'
condition|)
name|afterHyphen
operator|=
literal|true
expr_stmt|;
else|else
block|{
if|if
condition|(
name|afterHyphen
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|Character
operator|.
name|toUpperCase
argument_list|(
name|ch
argument_list|)
argument_list|)
expr_stmt|;
name|afterHyphen
operator|=
literal|false
expr_stmt|;
block|}
else|else
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
name|name
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"converted method name to "
operator|+
name|name
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Function#getName() 	 */
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Function#setArguments(java.util.List) 	 */
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|int
name|argCount
init|=
name|arguments
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|argCount
condition|;
name|i
operator|++
control|)
name|steps
operator|.
name|add
argument_list|(
name|arguments
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
comment|// search for candidate methods matching the given number of arguments
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"new"
argument_list|)
condition|)
block|{
name|Constructor
index|[]
name|constructors
init|=
name|myClass
operator|.
name|getConstructors
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|constructors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isPublic
argument_list|(
name|constructors
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
name|Class
name|paramTypes
index|[]
init|=
name|constructors
index|[
name|i
index|]
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|paramTypes
operator|.
name|length
operator|==
name|argCount
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found constructor "
operator|+
name|constructors
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|candidateMethods
operator|.
name|add
argument_list|(
name|constructors
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|candidateMethods
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"no constructor found with "
operator|+
name|argCount
operator|+
literal|" arguments"
argument_list|)
throw|;
block|}
else|else
block|{
name|Method
index|[]
name|methods
init|=
name|myClass
operator|.
name|getMethods
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|methods
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|Modifier
operator|.
name|isPublic
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
argument_list|)
operator|&&
name|methods
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|Class
name|paramTypes
index|[]
init|=
name|methods
index|[
name|i
index|]
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|Modifier
operator|.
name|isStatic
argument_list|(
name|methods
index|[
name|i
index|]
operator|.
name|getModifiers
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|paramTypes
operator|.
name|length
operator|==
name|argCount
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found static method "
operator|+
name|methods
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|candidateMethods
operator|.
name|add
argument_list|(
name|methods
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|paramTypes
operator|.
name|length
operator|==
name|argCount
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found method "
operator|+
name|methods
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|candidateMethods
operator|.
name|add
argument_list|(
name|methods
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|candidateMethods
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"no method matches "
operator|+
name|name
operator|+
literal|" with "
operator|+
name|argCount
operator|+
literal|" arguments"
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Expression#eval(org.exist.xpath.value.Sequence, org.exist.xpath.value.Item) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// get the actual arguments
name|Sequence
name|args
index|[]
init|=
name|getArguments
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|AccessibleObject
name|bestMethod
init|=
operator|(
name|AccessibleObject
operator|)
name|candidateMethods
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|conversionPrefs
index|[]
init|=
name|getConversionPreferences
argument_list|(
name|bestMethod
argument_list|,
name|args
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|candidateMethods
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|AccessibleObject
name|nextMethod
init|=
operator|(
name|AccessibleObject
operator|)
name|candidateMethods
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|prefs
index|[]
init|=
name|getConversionPreferences
argument_list|(
name|nextMethod
argument_list|,
name|args
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|prefs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|prefs
index|[
name|j
index|]
operator|<
name|conversionPrefs
index|[
name|j
index|]
condition|)
block|{
name|bestMethod
operator|=
name|nextMethod
expr_stmt|;
name|conversionPrefs
operator|=
name|prefs
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|//		LOG.debug("calling method " + bestMethod.toString());
name|Class
name|paramTypes
index|[]
init|=
literal|null
decl_stmt|;
name|boolean
name|isStatic
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|bestMethod
operator|instanceof
name|Constructor
condition|)
name|paramTypes
operator|=
operator|(
operator|(
name|Constructor
operator|)
name|bestMethod
operator|)
operator|.
name|getParameterTypes
argument_list|()
expr_stmt|;
else|else
block|{
name|paramTypes
operator|=
operator|(
operator|(
name|Method
operator|)
name|bestMethod
operator|)
operator|.
name|getParameterTypes
argument_list|()
expr_stmt|;
name|isStatic
operator|=
name|Modifier
operator|.
name|isStatic
argument_list|(
operator|(
operator|(
name|Method
operator|)
name|bestMethod
operator|)
operator|.
name|getModifiers
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Object
index|[]
name|params
init|=
operator|new
name|Object
index|[
name|isStatic
condition|?
name|args
operator|.
name|length
else|:
name|args
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
if|if
condition|(
name|isStatic
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|params
index|[
name|i
index|]
operator|=
name|args
index|[
name|i
index|]
operator|.
name|toJavaObject
argument_list|(
name|paramTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|params
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|args
index|[
name|i
index|]
operator|.
name|toJavaObject
argument_list|(
name|paramTypes
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|bestMethod
operator|instanceof
name|Constructor
condition|)
block|{
try|try
block|{
name|Object
name|object
init|=
operator|(
operator|(
name|Constructor
operator|)
name|bestMethod
operator|)
operator|.
name|newInstance
argument_list|(
name|params
argument_list|)
decl_stmt|;
return|return
operator|new
name|JavaObjectValue
argument_list|(
name|object
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"illegal argument to constructor "
operator|+
name|bestMethod
operator|.
name|toString
argument_list|()
operator|+
literal|": "
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|XPathException
condition|)
throw|throw
operator|(
name|XPathException
operator|)
name|e
throw|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"exception while calling constructor "
operator|+
name|bestMethod
operator|.
name|toString
argument_list|()
operator|+
literal|": "
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
else|else
block|{
try|try
block|{
name|Object
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|isStatic
condition|)
name|result
operator|=
operator|(
operator|(
name|Method
operator|)
name|bestMethod
operator|)
operator|.
name|invoke
argument_list|(
literal|null
argument_list|,
name|params
argument_list|)
expr_stmt|;
else|else
block|{
name|result
operator|=
operator|(
operator|(
name|Method
operator|)
name|bestMethod
operator|)
operator|.
name|invoke
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|toJavaObject
argument_list|(
name|myClass
argument_list|)
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|XPathUtil
operator|.
name|javaObjectToXPath
argument_list|(
name|result
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"illegal argument to method "
operator|+
name|bestMethod
operator|.
name|toString
argument_list|()
operator|+
literal|": "
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|XPathException
condition|)
throw|throw
operator|(
name|XPathException
operator|)
name|e
throw|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"exception while calling method "
operator|+
name|bestMethod
operator|.
name|toString
argument_list|()
operator|+
literal|": "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.Function#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ITEM
return|;
block|}
specifier|private
name|int
index|[]
name|getConversionPreferences
parameter_list|(
name|AccessibleObject
name|method
parameter_list|,
name|Sequence
index|[]
name|args
parameter_list|)
block|{
name|int
name|prefs
index|[]
init|=
operator|new
name|int
index|[
name|args
operator|.
name|length
index|]
decl_stmt|;
name|Class
name|paramTypes
index|[]
init|=
literal|null
decl_stmt|;
name|boolean
name|isStatic
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|method
operator|instanceof
name|Constructor
condition|)
name|paramTypes
operator|=
operator|(
operator|(
name|Constructor
operator|)
name|method
operator|)
operator|.
name|getParameterTypes
argument_list|()
expr_stmt|;
else|else
block|{
name|paramTypes
operator|=
operator|(
operator|(
name|Method
operator|)
name|method
operator|)
operator|.
name|getParameterTypes
argument_list|()
expr_stmt|;
name|isStatic
operator|=
name|Modifier
operator|.
name|isStatic
argument_list|(
operator|(
operator|(
name|Method
operator|)
name|method
operator|)
operator|.
name|getModifiers
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isStatic
condition|)
block|{
name|Class
name|nonStaticTypes
index|[]
init|=
operator|new
name|Class
index|[
name|paramTypes
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|nonStaticTypes
index|[
literal|0
index|]
operator|=
name|myClass
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|paramTypes
argument_list|,
literal|0
argument_list|,
name|nonStaticTypes
argument_list|,
literal|1
argument_list|,
name|paramTypes
operator|.
name|length
argument_list|)
expr_stmt|;
name|paramTypes
operator|=
name|nonStaticTypes
expr_stmt|;
block|}
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|prefs
index|[
name|i
index|]
operator|=
name|args
index|[
name|i
index|]
operator|.
name|conversionPreference
argument_list|(
name|paramTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|prefs
return|;
block|}
block|}
end_class

end_unit

