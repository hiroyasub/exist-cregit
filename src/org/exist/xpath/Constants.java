begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

begin_interface
specifier|public
interface|interface
name|Constants
block|{
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
literal|"attribute"
block|,
literal|"child"
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
literal|"parent"
block|,
literal|"preceding"
block|,
literal|"preceding-sibling"
block|,
literal|"self"
block|,
literal|"attribute"
block|}
decl_stmt|;
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
name|ATTRIBUTE_AXIS
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|CHILD_AXIS
init|=
literal|3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DESCENDANT_AXIS
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DESCENDANT_SELF_AXIS
init|=
literal|5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FOLLOWING_AXIS
init|=
literal|6
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FOLLOWING_SIBLING_AXIS
init|=
literal|7
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NAMESPACE_AXIS
init|=
literal|8
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PARENT_AXIS
init|=
literal|9
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PRECEDING_AXIS
init|=
literal|10
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PRECEDING_SIBLING_AXIS
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
specifier|public
specifier|final
specifier|static
name|int
name|SAME
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|BEFORE
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|AFTER
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NOT_COMPARABLE
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FULLTEXT_OR
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|FULLTEXT_AND
init|=
literal|1
decl_stmt|;
block|}
end_interface

end_unit

