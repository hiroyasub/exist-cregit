begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|// $ANTLR 2.7.2rc2 (20030105): "XPathParser.g" -> "XPathLexer.java"$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|*
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
name|*
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
name|*
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
name|*
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
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|*
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
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|BasicConfigurator
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

begin_interface
specifier|public
interface|interface
name|XPathParserTokenTypes
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
name|LITERAL_xpointer
init|=
literal|4
decl_stmt|;
name|int
name|LPAREN
init|=
literal|5
decl_stmt|;
name|int
name|RPAREN
init|=
literal|6
decl_stmt|;
name|int
name|NCNAME
init|=
literal|7
decl_stmt|;
name|int
name|LITERAL_or
init|=
literal|8
decl_stmt|;
name|int
name|LITERAL_and
init|=
literal|9
decl_stmt|;
name|int
name|CONST
init|=
literal|10
decl_stmt|;
name|int
name|ANDEQ
init|=
literal|11
decl_stmt|;
name|int
name|OREQ
init|=
literal|12
decl_stmt|;
name|int
name|EQ
init|=
literal|13
decl_stmt|;
name|int
name|NEQ
init|=
literal|14
decl_stmt|;
name|int
name|UNION
init|=
literal|15
decl_stmt|;
name|int
name|LT
init|=
literal|16
decl_stmt|;
name|int
name|GT
init|=
literal|17
decl_stmt|;
name|int
name|LTEQ
init|=
literal|18
decl_stmt|;
name|int
name|GTEQ
init|=
literal|19
decl_stmt|;
name|int
name|PLUS
init|=
literal|20
decl_stmt|;
name|int
name|LITERAL_doctype
init|=
literal|21
decl_stmt|;
name|int
name|LITERAL_document
init|=
literal|22
decl_stmt|;
name|int
name|STAR
init|=
literal|23
decl_stmt|;
name|int
name|COMMA
init|=
literal|24
decl_stmt|;
name|int
name|LITERAL_collection
init|=
literal|25
decl_stmt|;
name|int
name|LITERAL_xcollection
init|=
literal|26
decl_stmt|;
name|int
name|INT
init|=
literal|27
decl_stmt|;
name|int
name|LITERAL_text
init|=
literal|28
decl_stmt|;
comment|// "starts-with" = 29
comment|// "ends-with" = 30
name|int
name|LITERAL_contains
init|=
literal|31
decl_stmt|;
name|int
name|LITERAL_match
init|=
literal|32
decl_stmt|;
name|int
name|LITERAL_near
init|=
literal|33
decl_stmt|;
name|int
name|SLASH
init|=
literal|34
decl_stmt|;
name|int
name|DSLASH
init|=
literal|35
decl_stmt|;
name|int
name|AT
init|=
literal|36
decl_stmt|;
name|int
name|ATTRIB_STAR
init|=
literal|37
decl_stmt|;
name|int
name|LITERAL_node
init|=
literal|38
decl_stmt|;
name|int
name|PARENT
init|=
literal|39
decl_stmt|;
name|int
name|SELF
init|=
literal|40
decl_stmt|;
name|int
name|COLON
init|=
literal|41
decl_stmt|;
name|int
name|LITERAL_descendant
init|=
literal|42
decl_stmt|;
comment|// "descendant-or-self" = 43
name|int
name|LITERAL_child
init|=
literal|44
decl_stmt|;
name|int
name|LITERAL_parent
init|=
literal|45
decl_stmt|;
name|int
name|LITERAL_self
init|=
literal|46
decl_stmt|;
name|int
name|LITERAL_attribute
init|=
literal|47
decl_stmt|;
name|int
name|LITERAL_ancestor
init|=
literal|48
decl_stmt|;
comment|// "ancestor-or-self" = 49
name|int
name|LPPAREN
init|=
literal|50
decl_stmt|;
name|int
name|RPPAREN
init|=
literal|51
decl_stmt|;
name|int
name|WS
init|=
literal|52
decl_stmt|;
name|int
name|BASECHAR
init|=
literal|53
decl_stmt|;
name|int
name|IDEOGRAPHIC
init|=
literal|54
decl_stmt|;
name|int
name|DIGIT
init|=
literal|55
decl_stmt|;
name|int
name|NMSTART
init|=
literal|56
decl_stmt|;
name|int
name|NMCHAR
init|=
literal|57
decl_stmt|;
name|int
name|VARIABLE
init|=
literal|58
decl_stmt|;
block|}
end_interface

end_unit

