begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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

begin_comment
comment|/**  * Declares various constants and flags used by the query engine:  * axis specifiers, operators.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Constants
block|{
comment|//TODO : move this to a dedicated Axis class
comment|/** Axis names */
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|AXISSPECIFIERS
init|=
block|{
literal|"ancestor"
block|,
literal|"ancestor-or-self"
block|,
literal|"parent"
block|,
literal|"preceding"
block|,
literal|"preceding-sibling"
block|,
literal|"child"
block|,
literal|"attribute"
block|,
literal|"descendant"
block|,
literal|"descendant-or-self"
block|,
literal|"following"
block|,
literal|"following-sibling"
block|,
literal|"namespace"
block|,
literal|"self"
block|,
literal|"attribute-descendant"
block|}
decl_stmt|;
comment|/**      * XPath axis constants:      */
specifier|public
specifier|final
specifier|static
name|int
name|UNKNOWN_AXIS
init|=
operator|-
literal|1
decl_stmt|;
comment|/** Reverse axes */
specifier|public
specifier|final
specifier|static
name|int
name|ANCESTOR_AXIS
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ANCESTOR_SELF_AXIS
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PARENT_AXIS
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PRECEDING_AXIS
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PRECEDING_SIBLING_AXIS
init|=
literal|4
decl_stmt|;
comment|/** Forward axes */
specifier|public
specifier|final
specifier|static
name|int
name|CHILD_AXIS
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ATTRIBUTE_AXIS
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DESCENDANT_AXIS
init|=
literal|7
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DESCENDANT_SELF_AXIS
init|=
literal|8
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FOLLOWING_AXIS
init|=
literal|9
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FOLLOWING_SIBLING_AXIS
init|=
literal|10
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NAMESPACE_AXIS
init|=
literal|11
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SELF_AXIS
init|=
literal|12
decl_stmt|;
comment|//combines /descendant-or-self::node()/attribute:*
specifier|public
specifier|final
specifier|static
name|int
name|DESCENDANT_ATTRIBUTE_AXIS
init|=
literal|13
decl_stmt|;
comment|/**      * These constants represent the      * different node types in<i>XPath</i>.      */
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
literal|"root"
block|,
literal|"*"
block|,
literal|"text"
block|,
literal|"attribute"
block|,
literal|"namespace"
block|,
literal|"comment"
block|,
literal|"processing-instruction"
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|short
name|TYPE_UNKNOWN
init|=
operator|-
literal|1
decl_stmt|;
comment|/**      * Node types      */
specifier|public
specifier|final
specifier|static
name|int
name|NODE_TYPE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ROOT_NODE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ELEMENT_NODE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TEXT_NODE
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ATTRIBUTE_NODE
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NAMESPACE_NODE
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|COMMENT_NODE
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PROCESSING_NODE
init|=
literal|7
decl_stmt|;
comment|/**      * Value and General Comparison operators      */
enum|enum
name|Comparison
block|{
name|LT
argument_list|(
literal|"lt"
argument_list|,
literal|"<"
argument_list|)
block|,
name|GT
argument_list|(
literal|"gt"
argument_list|,
literal|">"
argument_list|)
block|,
name|GTEQ
argument_list|(
literal|"ge"
argument_list|,
literal|">="
argument_list|)
block|,
name|LTEQ
argument_list|(
literal|"le"
argument_list|,
literal|"<="
argument_list|)
block|,
name|EQ
argument_list|(
literal|"eq"
argument_list|,
literal|"="
argument_list|)
block|,
name|NEQ
argument_list|(
literal|"ne"
argument_list|,
literal|"!="
argument_list|)
block|,
name|IN
argument_list|(
literal|null
argument_list|,
literal|"IN"
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|valueComparisonSymbol
decl_stmt|;
specifier|public
specifier|final
name|String
name|generalComparisonSymbol
decl_stmt|;
name|Comparison
parameter_list|(
specifier|final
name|String
name|valueComparisonSymbol
parameter_list|,
specifier|final
name|String
name|generalComparisonSymbol
parameter_list|)
block|{
name|this
operator|.
name|valueComparisonSymbol
operator|=
name|valueComparisonSymbol
expr_stmt|;
name|this
operator|.
name|generalComparisonSymbol
operator|=
name|generalComparisonSymbol
expr_stmt|;
block|}
block|}
comment|/**      * String truncation operators      */
enum|enum
name|StringTruncationOperator
block|{
name|NONE
block|,
name|RIGHT
block|,
name|LEFT
block|,
name|BOTH
block|,
name|EQUALS
block|}
comment|/**      * Arithmetic operators      */
enum|enum
name|ArithmeticOperator
block|{
name|ADDITION
argument_list|(
literal|"+"
argument_list|)
block|,
name|SUBTRACTION
argument_list|(
literal|"-"
argument_list|)
block|,
name|MULTIPLICATION
argument_list|(
literal|"*"
argument_list|)
block|,
name|DIVISION
argument_list|(
literal|"div"
argument_list|)
block|,
name|MODULUS
argument_list|(
literal|"MOD"
argument_list|)
block|,
name|DIVISION_INTEGER
argument_list|(
literal|"idiv"
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|symbol
decl_stmt|;
name|ArithmeticOperator
parameter_list|(
specifier|final
name|String
name|symbol
parameter_list|)
block|{
name|this
operator|.
name|symbol
operator|=
name|symbol
expr_stmt|;
block|}
block|}
comment|/** 	 * Node Identity Comparison operators 	 */
enum|enum
name|NodeComparisonOperator
block|{
name|IS
argument_list|(
literal|"is"
argument_list|)
block|,
name|BEFORE
argument_list|(
literal|"<<"
argument_list|)
block|,
name|AFTER
argument_list|(
literal|">>"
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|symbol
decl_stmt|;
name|NodeComparisonOperator
parameter_list|(
specifier|final
name|String
name|symbol
parameter_list|)
block|{
name|this
operator|.
name|symbol
operator|=
name|symbol
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|int
name|KEEP_UNION
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|KEEP_INTER
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|KEEP_AFTER
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|KEEP_BEFORE
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TYPE_ANY
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TYPE_NODELIST
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TYPE_NODE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TYPE_STRING
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TYPE_NUM
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TYPE_BOOL
init|=
literal|5
decl_stmt|;
comment|//TODO : move the following to an org.exist.utils.Constants.java file
comment|//The definitive missing constant in java.lang.String
specifier|public
specifier|final
specifier|static
name|int
name|STRING_NOT_FOUND
init|=
operator|-
literal|1
decl_stmt|;
comment|//The definitive missing constants in java.lang.Comparable
specifier|public
specifier|final
specifier|static
name|int
name|INFERIOR
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EQUAL
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SUPERIOR
init|=
literal|1
decl_stmt|;
comment|//
specifier|public
specifier|final
specifier|static
name|int
name|NO_SIZE_HINT
init|=
operator|-
literal|1
decl_stmt|;
block|}
end_interface

end_unit

