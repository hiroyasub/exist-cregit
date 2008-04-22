begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentSet
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
name|EmptyNodeSet
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
name|NodeProxy
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
name|StoredNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|Cardinality
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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
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
name|Iterator
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

begin_comment
comment|/**  * An abstract implementation of {@link org.exist.xquery.value.Sequence} with  * default implementations for some methods.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSequence
implements|implements
name|Sequence
block|{
comment|/** To retain compatibility with eXist versions before september 20th 2005 , 	 * for conversion to boolean; 	 * @see http://cvs.sourceforge.net/viewcvs.py/exist/eXist-1.0/src/org/exist/xquery/value/AbstractSequence.java?r1=1.11&r2=1.12 */
specifier|private
specifier|static
specifier|final
name|boolean
name|OLD_EXIST_VERSION_COMPATIBILITY
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|isEmpty
decl_stmt|;
specifier|protected
name|boolean
name|hasOne
decl_stmt|;
specifier|protected
name|AbstractSequence
parameter_list|()
block|{
name|isEmpty
operator|=
literal|true
expr_stmt|;
name|hasOne
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|int
name|getItemType
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|SequenceIterator
name|iterate
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|SequenceIterator
name|unorderedIterator
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|int
name|getItemCount
parameter_list|()
function_decl|;
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
return|return
name|Cardinality
operator|.
name|EMPTY
return|;
if|if
condition|(
name|hasOne
argument_list|()
condition|)
return|return
name|Cardinality
operator|.
name|EXACTLY_ONE
return|;
if|if
condition|(
name|hasMany
argument_list|()
condition|)
return|return
name|Cardinality
operator|.
name|ONE_OR_MORE
return|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal argument"
argument_list|)
throw|;
block|}
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
name|Item
name|first
init|=
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|first
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
return|return
operator|(
operator|(
name|AtomicValue
operator|)
name|first
operator|)
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
return|;
else|else
comment|//TODO : clean atomization
return|return
operator|new
name|StringValue
argument_list|(
name|first
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|.
name|convertTo
argument_list|(
name|requiredType
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|hasOne
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|hasMany
parameter_list|()
block|{
return|return
operator|!
name|isEmpty
argument_list|()
operator|&&
operator|!
name|hasOne
argument_list|()
return|;
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
return|return
literal|""
return|;
name|Item
name|first
init|=
name|iterate
argument_list|()
operator|.
name|nextItem
argument_list|()
decl_stmt|;
return|return
name|first
operator|.
name|getStringValue
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|boolean
name|gotOne
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
if|if
condition|(
name|gotOne
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
name|gotOne
operator|=
literal|true
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
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
literal|"toString() fails: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#add(org.exist.xquery.value.Item) 	 */
specifier|public
specifier|abstract
name|void
name|add
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|void
name|addAll
parameter_list|(
name|Sequence
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|SequenceIterator
name|i
init|=
name|other
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#itemAt(int) 	 */
specifier|public
specifier|abstract
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
function_decl|;
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#getDocumentSet()      */
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
return|return
name|DocumentSet
operator|.
name|EMPTY_DOCUMENT_SET
return|;
block|}
specifier|public
name|Iterator
name|getCollectionIterator
parameter_list|()
block|{
return|return
name|EmptyNodeSet
operator|.
name|EMPTY_ITERATOR
return|;
block|}
specifier|public
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|StoredNode
name|newNode
parameter_list|)
block|{
block|}
comment|/** See 	 *<a<href="http://www.w3.org/TR/xquery/#id-ebv">2.4.3 Effective Boolean Value</a> 	 * @see org.exist.xquery.value.Sequence#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
return|return
literal|false
return|;
name|Item
name|first
init|=
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|//If its operand is a sequence whose first item is a node, fn:boolean returns true.
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|first
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|hasMany
argument_list|()
condition|)
block|{
if|if
condition|(
name|OLD_EXIST_VERSION_COMPATIBILITY
condition|)
return|return
literal|true
return|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"error FORG0006: effectiveBooleanValue: first item of '"
operator|+
operator|(
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|<
literal|20
condition|?
name|toString
argument_list|()
else|:
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
operator|+
literal|"..."
operator|)
operator|+
literal|"' is not a node, and sequence length> 1"
argument_list|)
throw|;
block|}
comment|//From now, we'll work with singletons...
comment|//Not sure about this one : does it mean than any singleton, including false() and 0 will return true ?
if|if
condition|(
name|OLD_EXIST_VERSION_COMPATIBILITY
condition|)
return|return
literal|true
return|;
else|else
return|return
operator|(
operator|(
name|AtomicValue
operator|)
name|first
operator|)
operator|.
name|effectiveBooleanValue
argument_list|()
return|;
comment|/*  		// If its operand is a singleton value of type xs:string, xs:anyURI, xs:untypedAtomic,  		//or a type derived from one of these, fn:boolean returns false if the operand value has zero length; otherwise it returns true. 		if(first instanceof StringValue) 			return ((StringValue)first).effectiveBooleanValue(); 		else if(first instanceof AnyURIValue) 			return ((AnyURIValue)first).effectiveBooleanValue(); 		else if(first instanceof UntypedAtomicValue) 			return ((UntypedAtomicValue)first).effectiveBooleanValue(); 		//If its operand is a singleton value of type xs:boolean or derived from xs:boolean,  		//fn:boolean returns the value of its operand unchanged. 		else if(first instanceof BooleanValue) 			return ((BooleanValue)first).getValue(); 		//If its operand is a singleton value of any numeric type or derived from a numeric type,  		//fn:boolean returns false if the operand value is NaN or is numerically equal to zero;  		//otherwise it returns true.		 		else if(first instanceof NumericValue) 			return ((NumericValue)first).effectiveBooleanValue(); 		else { 			if (OLD_EXIST_VERSION_COMPATIBILITY) 				return true; 			// In all other cases, fn:boolean raises a type error [err:FORG0006]. 			throw new XPathException( 				"error FORG0006: effectiveBooleanValue: sequence of length 1, " + 				"but not castable to a number or Boolean"); 		} 		 		*/
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#conversionPreference(java.lang.Class) 	 */
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
name|Sequence
operator|.
name|class
argument_list|)
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|List
operator|.
name|class
argument_list|)
operator|||
name|javaClass
operator|.
name|isArray
argument_list|()
condition|)
return|return
literal|1
return|;
if|else if
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
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
return|return
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|conversionPreference
argument_list|(
name|javaClass
argument_list|)
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#toJavaObject(java.lang.Class) 	 */
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
name|Sequence
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|target
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
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|Class
name|componentType
init|=
name|target
operator|.
name|getComponentType
argument_list|()
decl_stmt|;
comment|// assume single-dimensional, then double-check that instance really matches desired type
name|Object
name|array
init|=
name|Array
operator|.
name|newInstance
argument_list|(
name|componentType
argument_list|,
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|target
operator|.
name|isInstance
argument_list|(
name|array
argument_list|)
condition|)
return|return
literal|null
return|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Object
name|obj
init|=
name|item
operator|.
name|toJavaObject
argument_list|(
name|componentType
argument_list|)
decl_stmt|;
name|Array
operator|.
name|set
argument_list|(
name|array
argument_list|,
name|index
argument_list|,
name|obj
argument_list|)
expr_stmt|;
block|}
return|return
name|array
return|;
block|}
if|else if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|List
operator|.
name|class
argument_list|)
condition|)
block|{
name|List
name|l
init|=
operator|new
name|ArrayList
argument_list|(
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|l
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
return|return
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|target
argument_list|)
return|;
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|clearContext
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|Item
name|next
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|unorderedIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|instanceof
name|NodeProxy
condition|)
operator|(
operator|(
name|NodeProxy
operator|)
name|next
operator|)
operator|.
name|clearContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setSelfAsContext
parameter_list|(
name|int
name|contextId
parameter_list|)
block|{
name|Item
name|next
decl_stmt|;
name|NodeValue
name|node
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|unorderedIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|node
operator|=
operator|(
name|NodeValue
operator|)
name|next
expr_stmt|;
name|node
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#isCached() 	 */
specifier|public
name|boolean
name|isCached
parameter_list|()
block|{
comment|// always return false by default
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Sequence#setIsCached(boolean) 	 */
specifier|public
name|void
name|setIsCached
parameter_list|(
name|boolean
name|cached
parameter_list|)
block|{
comment|// ignore by default
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#isPersistentSet()      */
specifier|public
name|boolean
name|isPersistentSet
parameter_list|()
block|{
comment|// always return false by default
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|getState
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|public
name|boolean
name|hasChanged
parameter_list|(
name|int
name|previousState
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

