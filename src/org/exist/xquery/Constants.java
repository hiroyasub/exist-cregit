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
comment|/**      * Comparison operators      */
specifier|public
specifier|final
specifier|static
name|int
name|LT
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|GT
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|GTEQ
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|LTEQ
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|EQ
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NEQ
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IN
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|REGEXP
init|=
literal|7
decl_stmt|;
comment|/**      * String truncation operators      */
specifier|public
specifier|final
specifier|static
name|int
name|TRUNC_NONE
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRUNC_RIGHT
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRUNC_LEFT
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRUNC_BOTH
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|TRUNC_EQUALS
init|=
literal|3
decl_stmt|;
comment|/**      * Arithmetic operators      */
specifier|public
specifier|final
specifier|static
name|int
name|PLUS
init|=
literal|8
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MINUS
init|=
literal|9
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MULT
init|=
literal|10
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DIV
init|=
literal|11
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MOD
init|=
literal|12
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|IDIV
init|=
literal|13
decl_stmt|;
comment|/** 	 * Identity operators 	 */
specifier|public
specifier|final
specifier|static
name|int
name|IS
init|=
literal|14
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|ISNOT
init|=
literal|15
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|BEFORE
init|=
literal|16
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|AFTER
init|=
literal|17
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|OPS
init|=
block|{
literal|"<"
block|,
literal|">"
block|,
literal|">="
block|,
literal|"<="
block|,
literal|"="
block|,
literal|"!="
block|,
literal|"IN"
block|,
literal|"=~"
block|,
literal|"+"
block|,
literal|"-"
block|,
literal|"*"
block|,
literal|"div"
block|,
literal|"mod"
block|,
literal|"idiv"
block|,
literal|"is"
block|,
literal|"isnot"
block|,
literal|"<<"
block|,
literal|">>"
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
index|[]
name|VOPS
init|=
block|{
literal|"lt"
block|,
literal|"gt"
block|,
literal|"ge"
block|,
literal|"le"
block|,
literal|"eq"
block|,
literal|"ne"
block|}
decl_stmt|;
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

