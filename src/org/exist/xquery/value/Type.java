begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|hashtable
operator|.
name|Int2ObjectHashMap
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
name|hashtable
operator|.
name|Object2IntHashMap
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
comment|/**  * Defines all built-in types and their relations.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Type
block|{
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|NODETYPES
init|=
block|{
literal|"node"
block|,
literal|"element"
block|,
literal|"attribute"
block|,
literal|"text"
block|,
literal|"processing-instruction"
block|,
literal|"comment"
block|,
literal|"document"
block|,
literal|"namespace"
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NODE
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ELEMENT
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ATTRIBUTE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TEXT
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PROCESSING_INSTRUCTION
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|COMMENT
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DOCUMENT
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NAMESPACE
init|=
literal|500
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EMPTY
init|=
literal|10
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ITEM
init|=
literal|11
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ANY_TYPE
init|=
literal|12
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ATOMIC
init|=
literal|20
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UNTYPED_ATOMIC
init|=
literal|21
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|STRING
init|=
literal|22
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|BOOLEAN
init|=
literal|23
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|QNAME
init|=
literal|24
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ANY_URI
init|=
literal|25
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NUMBER
init|=
literal|30
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|INTEGER
init|=
literal|31
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DECIMAL
init|=
literal|32
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FLOAT
init|=
literal|33
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DOUBLE
init|=
literal|34
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NON_POSITIVE_INTEGER
init|=
literal|35
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NEGATIVE_INTEGER
init|=
literal|36
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|LONG
init|=
literal|37
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|INT
init|=
literal|38
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SHORT
init|=
literal|39
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|BYTE
init|=
literal|40
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NON_NEGATIVE_INTEGER
init|=
literal|41
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UNSIGNED_LONG
init|=
literal|42
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UNSIGNED_INT
init|=
literal|43
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UNSIGNED_SHORT
init|=
literal|44
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UNSIGNED_BYTE
init|=
literal|45
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|POSITIVE_INTEGER
init|=
literal|46
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DATE_TIME
init|=
literal|50
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DATE
init|=
literal|51
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TIME
init|=
literal|52
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DURATION
init|=
literal|53
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|YEAR_MONTH_DURATION
init|=
literal|54
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DAY_TIME_DURATION
init|=
literal|55
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TOKEN
init|=
literal|60
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NORMALIZED_STRING
init|=
literal|61
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|LANGUAGE
init|=
literal|62
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NMTOKEN
init|=
literal|63
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NAME
init|=
literal|64
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NCNAME
init|=
literal|65
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ID
init|=
literal|66
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IDREF
init|=
literal|67
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ENTITY
init|=
literal|68
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|JAVA_OBJECT
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Int2ObjectHashMap
name|typeHierarchy
init|=
operator|new
name|Int2ObjectHashMap
argument_list|()
decl_stmt|;
static|static
block|{
name|defineSubType
argument_list|(
name|ITEM
argument_list|,
name|NODE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NODE
argument_list|,
name|ELEMENT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NODE
argument_list|,
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NODE
argument_list|,
name|TEXT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NODE
argument_list|,
name|PROCESSING_INSTRUCTION
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NODE
argument_list|,
name|COMMENT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NODE
argument_list|,
name|DOCUMENT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NODE
argument_list|,
name|NAMESPACE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ITEM
argument_list|,
name|ATOMIC
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|STRING
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|BOOLEAN
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|QNAME
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|ANY_URI
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|NUMBER
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|UNTYPED_ATOMIC
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|JAVA_OBJECT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|DATE_TIME
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|DATE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|TIME
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|ATOMIC
argument_list|,
name|DURATION
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|DURATION
argument_list|,
name|YEAR_MONTH_DURATION
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|DURATION
argument_list|,
name|DAY_TIME_DURATION
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NUMBER
argument_list|,
name|DECIMAL
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NUMBER
argument_list|,
name|FLOAT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NUMBER
argument_list|,
name|DOUBLE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|DECIMAL
argument_list|,
name|INTEGER
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|INTEGER
argument_list|,
name|NON_POSITIVE_INTEGER
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NON_POSITIVE_INTEGER
argument_list|,
name|NEGATIVE_INTEGER
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|INTEGER
argument_list|,
name|LONG
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|LONG
argument_list|,
name|INT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|INT
argument_list|,
name|SHORT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|SHORT
argument_list|,
name|BYTE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|INTEGER
argument_list|,
name|NON_NEGATIVE_INTEGER
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NON_NEGATIVE_INTEGER
argument_list|,
name|POSITIVE_INTEGER
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NON_NEGATIVE_INTEGER
argument_list|,
name|UNSIGNED_LONG
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|UNSIGNED_LONG
argument_list|,
name|UNSIGNED_INT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|UNSIGNED_INT
argument_list|,
name|UNSIGNED_SHORT
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|UNSIGNED_SHORT
argument_list|,
name|UNSIGNED_BYTE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|STRING
argument_list|,
name|NORMALIZED_STRING
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NORMALIZED_STRING
argument_list|,
name|TOKEN
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|TOKEN
argument_list|,
name|LANGUAGE
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|TOKEN
argument_list|,
name|NMTOKEN
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|TOKEN
argument_list|,
name|NAME
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NAME
argument_list|,
name|NCNAME
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NCNAME
argument_list|,
name|ID
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NCNAME
argument_list|,
name|IDREF
argument_list|)
expr_stmt|;
name|defineSubType
argument_list|(
name|NCNAME
argument_list|,
name|ENTITY
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|Int2ObjectHashMap
name|typeNames
init|=
operator|new
name|Int2ObjectHashMap
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Object2IntHashMap
name|typeCodes
init|=
operator|new
name|Object2IntHashMap
argument_list|(
literal|100
argument_list|)
decl_stmt|;
static|static
block|{
name|defineBuiltInType
argument_list|(
name|NODE
argument_list|,
literal|"node"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ITEM
argument_list|,
literal|"item"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|EMPTY
argument_list|,
literal|"empty"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NUMBER
argument_list|,
literal|"number"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ELEMENT
argument_list|,
literal|"element"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|DOCUMENT
argument_list|,
literal|"document"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ATTRIBUTE
argument_list|,
literal|"attribute"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|TEXT
argument_list|,
literal|"text"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|PROCESSING_INSTRUCTION
argument_list|,
literal|"processing-instruction"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|COMMENT
argument_list|,
literal|"comment"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NAMESPACE
argument_list|,
literal|"namespace"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|JAVA_OBJECT
argument_list|,
literal|"object"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ANY_TYPE
argument_list|,
literal|"xs:anyType"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ATOMIC
argument_list|,
literal|"xdt:anyAtomicType"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|UNTYPED_ATOMIC
argument_list|,
literal|"xdt:untypedAtomic"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|BOOLEAN
argument_list|,
literal|"xs:boolean"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|DECIMAL
argument_list|,
literal|"xs:decimal"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|FLOAT
argument_list|,
literal|"xs:float"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|DOUBLE
argument_list|,
literal|"xs:double"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|INTEGER
argument_list|,
literal|"xs:integer"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NON_POSITIVE_INTEGER
argument_list|,
literal|"xs:nonPositiveInteger"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NEGATIVE_INTEGER
argument_list|,
literal|"xs:negativeInteger"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|LONG
argument_list|,
literal|"xs:long"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|INT
argument_list|,
literal|"xs:int"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|SHORT
argument_list|,
literal|"xs:short"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|BYTE
argument_list|,
literal|"xs:byte"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NON_NEGATIVE_INTEGER
argument_list|,
literal|"xs:nonNegativeInteger"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|UNSIGNED_LONG
argument_list|,
literal|"xs:unsignedLong"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|UNSIGNED_INT
argument_list|,
literal|"xs:unsignedInt"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|UNSIGNED_SHORT
argument_list|,
literal|"xs:unsignedShort"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|UNSIGNED_BYTE
argument_list|,
literal|"xs:unsignedByte"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|POSITIVE_INTEGER
argument_list|,
literal|"xs:positiveInteger"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|STRING
argument_list|,
literal|"xs:string"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|QNAME
argument_list|,
literal|"xs:QName"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ANY_URI
argument_list|,
literal|"xs:anyURI"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|DATE_TIME
argument_list|,
literal|"xs:dateTime"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|DATE
argument_list|,
literal|"xs:date"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|TIME
argument_list|,
literal|"xs:time"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|DURATION
argument_list|,
literal|"xs:duration"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|YEAR_MONTH_DURATION
argument_list|,
literal|"xdt:yearMonthDuration"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|DAY_TIME_DURATION
argument_list|,
literal|"xdt:dayTimeDuration"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NORMALIZED_STRING
argument_list|,
literal|"xs:normalizedString"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|TOKEN
argument_list|,
literal|"xs:token"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|LANGUAGE
argument_list|,
literal|"xs:language"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NMTOKEN
argument_list|,
literal|"xs:NMTOKEN"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NAME
argument_list|,
literal|"xs:Name"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|NCNAME
argument_list|,
literal|"xs:NCName"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ID
argument_list|,
literal|"xs:ID"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|IDREF
argument_list|,
literal|"xs:IDREF"
argument_list|)
expr_stmt|;
name|defineBuiltInType
argument_list|(
name|ENTITY
argument_list|,
literal|"xs:ENTITY"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
specifier|static
name|void
name|defineBuiltInType
parameter_list|(
name|int
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|typeNames
operator|.
name|put
argument_list|(
name|type
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|typeCodes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Get the internal name for the built-in type. 	 *  	 * @param type 	 * @return 	 */
specifier|public
specifier|final
specifier|static
name|String
name|getTypeName
parameter_list|(
name|int
name|type
parameter_list|)
block|{
return|return
operator|(
name|String
operator|)
name|typeNames
operator|.
name|get
argument_list|(
name|type
argument_list|)
return|;
block|}
comment|/** 	 * Get the type code for a type identified by its internal name. 	 *  	 * @param name 	 * @return 	 * @throws XPathException 	 */
specifier|public
specifier|final
specifier|static
name|int
name|getType
parameter_list|(
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
name|equals
argument_list|(
literal|"node"
argument_list|)
condition|)
return|return
name|NODE
return|;
name|int
name|code
init|=
name|typeCodes
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|==
operator|-
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type: "
operator|+
name|name
operator|+
literal|" is not defined"
argument_list|)
throw|;
return|return
name|code
return|;
block|}
comment|/** 	 * Get the type code for a type identified by its QName. 	 *  	 * @param qname 	 * @return 	 * @throws XPathException 	 */
specifier|public
specifier|final
specifier|static
name|int
name|getType
parameter_list|(
name|QName
name|qname
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|uri
init|=
name|qname
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|XQueryContext
operator|.
name|SCHEMA_NS
argument_list|)
condition|)
return|return
name|getType
argument_list|(
literal|"xs:"
operator|+
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
if|else if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|XQueryContext
operator|.
name|XPATH_DATATYPES_NS
argument_list|)
condition|)
return|return
name|getType
argument_list|(
literal|"xdt:"
operator|+
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
else|else
return|return
name|getType
argument_list|(
name|qname
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Define supertype/subtype relation. 	 *  	 * @param supertype 	 * @param subtype 	 */
specifier|public
specifier|final
specifier|static
name|void
name|defineSubType
parameter_list|(
name|int
name|supertype
parameter_list|,
name|int
name|subtype
parameter_list|)
block|{
name|typeHierarchy
operator|.
name|put
argument_list|(
name|subtype
argument_list|,
operator|new
name|Integer
argument_list|(
name|supertype
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Check if the given type code is a subtype of the specified supertype. 	 *  	 * @param subtype 	 * @param supertype 	 * @return 	 */
specifier|public
specifier|final
specifier|static
name|boolean
name|subTypeOf
parameter_list|(
name|int
name|subtype
parameter_list|,
name|int
name|supertype
parameter_list|)
block|{
if|if
condition|(
name|subtype
operator|==
name|supertype
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|supertype
operator|==
name|ITEM
operator|||
name|supertype
operator|==
name|ANY_TYPE
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|subtype
operator|==
name|ITEM
operator|||
name|subtype
operator|==
name|EMPTY
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|typeHierarchy
operator|.
name|containsKey
argument_list|(
name|subtype
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type "
operator|+
name|subtype
operator|+
literal|" is not a valid type"
argument_list|)
throw|;
name|subtype
operator|=
operator|(
operator|(
name|Integer
operator|)
name|typeHierarchy
operator|.
name|get
argument_list|(
name|subtype
argument_list|)
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
return|return
name|subTypeOf
argument_list|(
name|subtype
argument_list|,
name|supertype
argument_list|)
return|;
block|}
comment|/** 	 * Get the type code of the supertype of the specified subtype. 	 *  	 * @param subtype 	 * @return 	 */
specifier|public
specifier|final
specifier|static
name|int
name|getSuperType
parameter_list|(
name|int
name|subtype
parameter_list|)
block|{
if|if
condition|(
name|subtype
operator|==
name|ITEM
condition|)
return|return
name|ITEM
return|;
name|Integer
name|i
init|=
operator|(
name|Integer
operator|)
name|typeHierarchy
operator|.
name|get
argument_list|(
name|subtype
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"no supertype for "
operator|+
name|getTypeName
argument_list|(
name|subtype
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ITEM
return|;
block|}
return|return
name|i
operator|.
name|intValue
argument_list|()
return|;
block|}
comment|/** 	 * Find a common supertype for two given type codes. 	 *  	 * Type.ITEM is returned if no other common supertype 	 * is found. 	 *   	 * @param type1 	 * @param type2 	 * @return 	 */
specifier|public
specifier|static
name|int
name|getCommonSuperType
parameter_list|(
name|int
name|type1
parameter_list|,
name|int
name|type2
parameter_list|)
block|{
if|if
condition|(
name|type1
operator|==
name|type2
condition|)
return|return
name|type1
return|;
name|type1
operator|=
name|getSuperType
argument_list|(
name|type1
argument_list|)
expr_stmt|;
if|if
condition|(
name|type1
operator|==
name|type2
condition|)
return|return
name|type1
return|;
else|else
return|return
name|getCommonSuperType
argument_list|(
name|type1
argument_list|,
name|getSuperType
argument_list|(
name|type2
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

