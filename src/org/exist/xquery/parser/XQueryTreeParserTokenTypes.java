begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|// $ANTLR 2.7.4: "XQueryTree.g" -> "XQueryTreeParser.java"$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|antlr
operator|.
name|debug
operator|.
name|misc
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|Stack
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|analysis
operator|.
name|Tokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|DocumentImpl
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
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|User
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
name|*
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
name|*
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
name|*
import|;
end_import

begin_interface
specifier|public
interface|interface
name|XQueryTreeParserTokenTypes
block|{
name|int
name|EOF
init|=
literal|1
decl_stmt|;
name|int
name|NULL_TREE_LOOKAHEAD
init|=
literal|3
decl_stmt|;
name|int
name|QNAME
init|=
literal|4
decl_stmt|;
name|int
name|PREDICATE
init|=
literal|5
decl_stmt|;
name|int
name|FLWOR
init|=
literal|6
decl_stmt|;
name|int
name|PARENTHESIZED
init|=
literal|7
decl_stmt|;
name|int
name|ABSOLUTE_SLASH
init|=
literal|8
decl_stmt|;
name|int
name|ABSOLUTE_DSLASH
init|=
literal|9
decl_stmt|;
name|int
name|WILDCARD
init|=
literal|10
decl_stmt|;
name|int
name|PREFIX_WILDCARD
init|=
literal|11
decl_stmt|;
name|int
name|FUNCTION
init|=
literal|12
decl_stmt|;
name|int
name|UNARY_MINUS
init|=
literal|13
decl_stmt|;
name|int
name|UNARY_PLUS
init|=
literal|14
decl_stmt|;
name|int
name|XPOINTER
init|=
literal|15
decl_stmt|;
name|int
name|XPOINTER_ID
init|=
literal|16
decl_stmt|;
name|int
name|VARIABLE_REF
init|=
literal|17
decl_stmt|;
name|int
name|VARIABLE_BINDING
init|=
literal|18
decl_stmt|;
name|int
name|ELEMENT
init|=
literal|19
decl_stmt|;
name|int
name|ATTRIBUTE
init|=
literal|20
decl_stmt|;
name|int
name|ATTRIBUTE_CONTENT
init|=
literal|21
decl_stmt|;
name|int
name|TEXT
init|=
literal|22
decl_stmt|;
name|int
name|VERSION_DECL
init|=
literal|23
decl_stmt|;
name|int
name|NAMESPACE_DECL
init|=
literal|24
decl_stmt|;
name|int
name|DEF_NAMESPACE_DECL
init|=
literal|25
decl_stmt|;
name|int
name|DEF_COLLATION_DECL
init|=
literal|26
decl_stmt|;
name|int
name|DEF_FUNCTION_NS_DECL
init|=
literal|27
decl_stmt|;
name|int
name|GLOBAL_VAR
init|=
literal|28
decl_stmt|;
name|int
name|FUNCTION_DECL
init|=
literal|29
decl_stmt|;
name|int
name|PROLOG
init|=
literal|30
decl_stmt|;
name|int
name|ATOMIC_TYPE
init|=
literal|31
decl_stmt|;
name|int
name|MODULE
init|=
literal|32
decl_stmt|;
name|int
name|ORDER_BY
init|=
literal|33
decl_stmt|;
name|int
name|POSITIONAL_VAR
init|=
literal|34
decl_stmt|;
name|int
name|BEFORE
init|=
literal|35
decl_stmt|;
name|int
name|AFTER
init|=
literal|36
decl_stmt|;
name|int
name|MODULE_DECL
init|=
literal|37
decl_stmt|;
name|int
name|ATTRIBUTE_TEST
init|=
literal|38
decl_stmt|;
name|int
name|COMP_ELEM_CONSTRUCTOR
init|=
literal|39
decl_stmt|;
name|int
name|COMP_ATTR_CONSTRUCTOR
init|=
literal|40
decl_stmt|;
name|int
name|COMP_TEXT_CONSTRUCTOR
init|=
literal|41
decl_stmt|;
name|int
name|COMP_COMMENT_CONSTRUCTOR
init|=
literal|42
decl_stmt|;
name|int
name|COMP_PI_CONSTRUCTOR
init|=
literal|43
decl_stmt|;
name|int
name|COMP_NS_CONSTRUCTOR
init|=
literal|44
decl_stmt|;
name|int
name|COMP_DOC_CONSTRUCTOR
init|=
literal|45
decl_stmt|;
name|int
name|LITERAL_xpointer
init|=
literal|46
decl_stmt|;
name|int
name|LPAREN
init|=
literal|47
decl_stmt|;
name|int
name|RPAREN
init|=
literal|48
decl_stmt|;
name|int
name|NCNAME
init|=
literal|49
decl_stmt|;
name|int
name|LITERAL_xquery
init|=
literal|50
decl_stmt|;
name|int
name|LITERAL_version
init|=
literal|51
decl_stmt|;
name|int
name|SEMICOLON
init|=
literal|52
decl_stmt|;
name|int
name|LITERAL_module
init|=
literal|53
decl_stmt|;
name|int
name|LITERAL_namespace
init|=
literal|54
decl_stmt|;
name|int
name|EQ
init|=
literal|55
decl_stmt|;
name|int
name|STRING_LITERAL
init|=
literal|56
decl_stmt|;
name|int
name|LITERAL_import
init|=
literal|57
decl_stmt|;
name|int
name|LITERAL_declare
init|=
literal|58
decl_stmt|;
name|int
name|LITERAL_default
init|=
literal|59
decl_stmt|;
name|int
name|LITERAL_xmlspace
init|=
literal|60
decl_stmt|;
name|int
name|LITERAL_ordering
init|=
literal|61
decl_stmt|;
name|int
name|LITERAL_construction
init|=
literal|62
decl_stmt|;
comment|// "base-uri" = 63
name|int
name|LITERAL_function
init|=
literal|64
decl_stmt|;
name|int
name|LITERAL_variable
init|=
literal|65
decl_stmt|;
name|int
name|LITERAL_encoding
init|=
literal|66
decl_stmt|;
name|int
name|LITERAL_collation
init|=
literal|67
decl_stmt|;
name|int
name|LITERAL_element
init|=
literal|68
decl_stmt|;
name|int
name|LITERAL_preserve
init|=
literal|69
decl_stmt|;
name|int
name|LITERAL_strip
init|=
literal|70
decl_stmt|;
name|int
name|LITERAL_ordered
init|=
literal|71
decl_stmt|;
name|int
name|LITERAL_unordered
init|=
literal|72
decl_stmt|;
name|int
name|DOLLAR
init|=
literal|73
decl_stmt|;
name|int
name|LCURLY
init|=
literal|74
decl_stmt|;
name|int
name|RCURLY
init|=
literal|75
decl_stmt|;
name|int
name|LITERAL_external
init|=
literal|76
decl_stmt|;
name|int
name|LITERAL_at
init|=
literal|77
decl_stmt|;
name|int
name|LITERAL_as
init|=
literal|78
decl_stmt|;
name|int
name|COMMA
init|=
literal|79
decl_stmt|;
name|int
name|LITERAL_empty
init|=
literal|80
decl_stmt|;
name|int
name|QUESTION
init|=
literal|81
decl_stmt|;
name|int
name|STAR
init|=
literal|82
decl_stmt|;
name|int
name|PLUS
init|=
literal|83
decl_stmt|;
name|int
name|LITERAL_item
init|=
literal|84
decl_stmt|;
name|int
name|LITERAL_for
init|=
literal|85
decl_stmt|;
name|int
name|LITERAL_let
init|=
literal|86
decl_stmt|;
name|int
name|LITERAL_some
init|=
literal|87
decl_stmt|;
name|int
name|LITERAL_every
init|=
literal|88
decl_stmt|;
name|int
name|LITERAL_if
init|=
literal|89
decl_stmt|;
name|int
name|LITERAL_where
init|=
literal|90
decl_stmt|;
name|int
name|LITERAL_return
init|=
literal|91
decl_stmt|;
name|int
name|LITERAL_in
init|=
literal|92
decl_stmt|;
name|int
name|COLON
init|=
literal|93
decl_stmt|;
name|int
name|LITERAL_order
init|=
literal|94
decl_stmt|;
name|int
name|LITERAL_by
init|=
literal|95
decl_stmt|;
name|int
name|LITERAL_ascending
init|=
literal|96
decl_stmt|;
name|int
name|LITERAL_descending
init|=
literal|97
decl_stmt|;
name|int
name|LITERAL_greatest
init|=
literal|98
decl_stmt|;
name|int
name|LITERAL_least
init|=
literal|99
decl_stmt|;
name|int
name|LITERAL_satisfies
init|=
literal|100
decl_stmt|;
name|int
name|LITERAL_typeswitch
init|=
literal|101
decl_stmt|;
name|int
name|LITERAL_case
init|=
literal|102
decl_stmt|;
name|int
name|LITERAL_then
init|=
literal|103
decl_stmt|;
name|int
name|LITERAL_else
init|=
literal|104
decl_stmt|;
name|int
name|LITERAL_or
init|=
literal|105
decl_stmt|;
name|int
name|LITERAL_and
init|=
literal|106
decl_stmt|;
name|int
name|LITERAL_instance
init|=
literal|107
decl_stmt|;
name|int
name|LITERAL_of
init|=
literal|108
decl_stmt|;
name|int
name|LITERAL_castable
init|=
literal|109
decl_stmt|;
name|int
name|LITERAL_cast
init|=
literal|110
decl_stmt|;
name|int
name|LT
init|=
literal|111
decl_stmt|;
name|int
name|GT
init|=
literal|112
decl_stmt|;
name|int
name|LITERAL_eq
init|=
literal|113
decl_stmt|;
name|int
name|LITERAL_ne
init|=
literal|114
decl_stmt|;
name|int
name|LITERAL_lt
init|=
literal|115
decl_stmt|;
name|int
name|LITERAL_le
init|=
literal|116
decl_stmt|;
name|int
name|LITERAL_gt
init|=
literal|117
decl_stmt|;
name|int
name|LITERAL_ge
init|=
literal|118
decl_stmt|;
name|int
name|NEQ
init|=
literal|119
decl_stmt|;
name|int
name|GTEQ
init|=
literal|120
decl_stmt|;
name|int
name|LTEQ
init|=
literal|121
decl_stmt|;
name|int
name|LITERAL_is
init|=
literal|122
decl_stmt|;
name|int
name|LITERAL_isnot
init|=
literal|123
decl_stmt|;
name|int
name|ANDEQ
init|=
literal|124
decl_stmt|;
name|int
name|OREQ
init|=
literal|125
decl_stmt|;
name|int
name|LITERAL_to
init|=
literal|126
decl_stmt|;
name|int
name|MINUS
init|=
literal|127
decl_stmt|;
name|int
name|LITERAL_div
init|=
literal|128
decl_stmt|;
name|int
name|LITERAL_idiv
init|=
literal|129
decl_stmt|;
name|int
name|LITERAL_mod
init|=
literal|130
decl_stmt|;
name|int
name|LITERAL_union
init|=
literal|131
decl_stmt|;
name|int
name|UNION
init|=
literal|132
decl_stmt|;
name|int
name|LITERAL_intersect
init|=
literal|133
decl_stmt|;
name|int
name|LITERAL_except
init|=
literal|134
decl_stmt|;
name|int
name|SLASH
init|=
literal|135
decl_stmt|;
name|int
name|DSLASH
init|=
literal|136
decl_stmt|;
name|int
name|LITERAL_text
init|=
literal|137
decl_stmt|;
name|int
name|LITERAL_node
init|=
literal|138
decl_stmt|;
name|int
name|LITERAL_attribute
init|=
literal|139
decl_stmt|;
name|int
name|LITERAL_comment
init|=
literal|140
decl_stmt|;
comment|// "processing-instruction" = 141
comment|// "document-node" = 142
name|int
name|LITERAL_document
init|=
literal|143
decl_stmt|;
name|int
name|SELF
init|=
literal|144
decl_stmt|;
name|int
name|XML_COMMENT
init|=
literal|145
decl_stmt|;
name|int
name|XML_PI
init|=
literal|146
decl_stmt|;
name|int
name|LPPAREN
init|=
literal|147
decl_stmt|;
name|int
name|RPPAREN
init|=
literal|148
decl_stmt|;
name|int
name|AT
init|=
literal|149
decl_stmt|;
name|int
name|PARENT
init|=
literal|150
decl_stmt|;
name|int
name|LITERAL_child
init|=
literal|151
decl_stmt|;
name|int
name|LITERAL_self
init|=
literal|152
decl_stmt|;
name|int
name|LITERAL_descendant
init|=
literal|153
decl_stmt|;
comment|// "descendant-or-self" = 154
comment|// "following-sibling" = 155
name|int
name|LITERAL_following
init|=
literal|156
decl_stmt|;
name|int
name|LITERAL_parent
init|=
literal|157
decl_stmt|;
name|int
name|LITERAL_ancestor
init|=
literal|158
decl_stmt|;
comment|// "ancestor-or-self" = 159
comment|// "preceding-sibling" = 160
name|int
name|DOUBLE_LITERAL
init|=
literal|161
decl_stmt|;
name|int
name|DECIMAL_LITERAL
init|=
literal|162
decl_stmt|;
name|int
name|INTEGER_LITERAL
init|=
literal|163
decl_stmt|;
name|int
name|END_TAG_START
init|=
literal|164
decl_stmt|;
name|int
name|QUOT
init|=
literal|165
decl_stmt|;
name|int
name|APOS
init|=
literal|166
decl_stmt|;
name|int
name|QUOT_ATTRIBUTE_CONTENT
init|=
literal|167
decl_stmt|;
name|int
name|APOS_ATTRIBUTE_CONTENT
init|=
literal|168
decl_stmt|;
name|int
name|ELEMENT_CONTENT
init|=
literal|169
decl_stmt|;
name|int
name|XML_COMMENT_END
init|=
literal|170
decl_stmt|;
name|int
name|XML_PI_END
init|=
literal|171
decl_stmt|;
name|int
name|XML_CDATA
init|=
literal|172
decl_stmt|;
name|int
name|LITERAL_collection
init|=
literal|173
decl_stmt|;
name|int
name|LITERAL_preceding
init|=
literal|174
decl_stmt|;
name|int
name|XML_PI_START
init|=
literal|175
decl_stmt|;
name|int
name|XML_CDATA_START
init|=
literal|176
decl_stmt|;
name|int
name|XML_CDATA_END
init|=
literal|177
decl_stmt|;
name|int
name|LETTER
init|=
literal|178
decl_stmt|;
name|int
name|DIGITS
init|=
literal|179
decl_stmt|;
name|int
name|HEX_DIGITS
init|=
literal|180
decl_stmt|;
name|int
name|NMSTART
init|=
literal|181
decl_stmt|;
name|int
name|NMCHAR
init|=
literal|182
decl_stmt|;
name|int
name|WS
init|=
literal|183
decl_stmt|;
name|int
name|EXPR_COMMENT
init|=
literal|184
decl_stmt|;
name|int
name|PRAGMA
init|=
literal|185
decl_stmt|;
name|int
name|PRAGMA_CONTENT
init|=
literal|186
decl_stmt|;
name|int
name|PRAGMA_QNAME
init|=
literal|187
decl_stmt|;
name|int
name|PREDEFINED_ENTITY_REF
init|=
literal|188
decl_stmt|;
name|int
name|CHAR_REF
init|=
literal|189
decl_stmt|;
name|int
name|NEXT_TOKEN
init|=
literal|190
decl_stmt|;
name|int
name|CHAR
init|=
literal|191
decl_stmt|;
name|int
name|BASECHAR
init|=
literal|192
decl_stmt|;
name|int
name|IDEOGRAPHIC
init|=
literal|193
decl_stmt|;
name|int
name|COMBINING_CHAR
init|=
literal|194
decl_stmt|;
name|int
name|DIGIT
init|=
literal|195
decl_stmt|;
name|int
name|EXTENDER
init|=
literal|196
decl_stmt|;
block|}
end_interface

end_unit

