begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|analysis
package|;
end_package

begin_comment
comment|/**  *  This is the default class used by the fulltext indexer for  * tokenizing a string into words. Known token types are defined  * by class Token.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  *@created    July 30, 2002  */
end_comment

begin_class
specifier|public
class|class
name|SimpleTokenizer
implements|implements
name|Tokenizer
block|{
specifier|private
name|int
name|pos
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|stem
init|=
literal|false
decl_stmt|;
specifier|private
name|CharSequence
name|text
decl_stmt|;
specifier|private
name|int
name|len
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
name|TextToken
name|temp
init|=
operator|new
name|TextToken
argument_list|()
decl_stmt|;
specifier|public
name|SimpleTokenizer
parameter_list|()
block|{
block|}
specifier|public
name|SimpleTokenizer
parameter_list|(
name|boolean
name|stem
parameter_list|)
block|{
name|this
operator|.
name|stem
operator|=
name|stem
expr_stmt|;
block|}
specifier|public
name|void
name|setStemming
parameter_list|(
name|boolean
name|stem
parameter_list|)
block|{
name|this
operator|.
name|stem
operator|=
name|stem
expr_stmt|;
block|}
specifier|private
specifier|final
name|char
name|LA
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|int
name|current
init|=
name|pos
operator|+
name|i
decl_stmt|;
return|return
name|current
operator|>
name|len
condition|?
operator|(
name|char
operator|)
operator|-
literal|1
else|:
name|text
operator|.
name|charAt
argument_list|(
name|current
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|protected
name|TextToken
name|alpha
parameter_list|(
name|TextToken
name|token
parameter_list|,
name|boolean
name|allowWildcards
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
name|token
operator|=
operator|new
name|TextToken
argument_list|(
name|TextToken
operator|.
name|ALPHA
argument_list|,
name|text
argument_list|,
name|pos
argument_list|)
expr_stmt|;
else|else
name|token
operator|.
name|setType
argument_list|(
name|TextToken
operator|.
name|ALPHA
argument_list|)
expr_stmt|;
name|int
name|oldPos
init|=
name|pos
decl_stmt|;
comment|// consume letters
name|char
name|ch
init|=
name|LA
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|ch
operator|!=
operator|(
name|char
operator|)
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'\\'
operator|&&
name|isWildcard
argument_list|(
name|LA
argument_list|(
literal|2
argument_list|)
argument_list|)
condition|)
block|{
break|break;
block|}
if|else if
condition|(
name|ch
operator|>
literal|'\u2E80'
operator|&&
name|singleCharToken
argument_list|(
name|ch
argument_list|)
condition|)
block|{
comment|// if this is a single char token and first in the sequence,
comment|// consume it
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
name|token
operator|.
name|consumeNext
argument_list|()
expr_stmt|;
name|consume
argument_list|()
expr_stmt|;
name|ch
operator|=
name|LA
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
if|else if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|ch
argument_list|)
operator|||
name|is_mark
argument_list|(
name|ch
argument_list|)
operator|||
name|nonBreakingChar
argument_list|(
name|ch
argument_list|)
operator|||
operator|(
name|allowWildcards
operator|&&
name|isWildcard
argument_list|(
name|ch
argument_list|)
operator|)
condition|)
block|{
name|token
operator|.
name|consumeNext
argument_list|()
expr_stmt|;
name|consume
argument_list|()
expr_stmt|;
name|ch
operator|=
name|LA
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
if|if
condition|(
name|Character
operator|.
name|isDigit
argument_list|(
name|ch
argument_list|)
condition|)
block|{
comment|// found non-letter character
comment|// call alphanum()
comment|//pos = oldPos;
return|return
name|alphanum
argument_list|(
name|token
argument_list|,
name|allowWildcards
argument_list|)
return|;
block|}
return|return
name|token
return|;
block|}
specifier|private
specifier|final
specifier|static
name|boolean
name|isWildcard
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'?'
operator|||
name|ch
operator|==
literal|'*'
operator|||
name|ch
operator|==
literal|'['
operator|||
name|ch
operator|==
literal|']'
condition|)
return|return
literal|true
return|;
return|return
literal|false
return|;
block|}
specifier|protected
name|TextToken
name|alphanum
parameter_list|(
name|TextToken
name|token
parameter_list|,
name|boolean
name|allowWildcards
parameter_list|)
block|{
if|if
condition|(
name|token
operator|==
literal|null
condition|)
name|token
operator|=
operator|new
name|TextToken
argument_list|(
name|TextToken
operator|.
name|ALPHANUM
argument_list|,
name|text
argument_list|,
name|pos
argument_list|)
expr_stmt|;
else|else
name|token
operator|.
name|setType
argument_list|(
name|TextToken
operator|.
name|ALPHANUM
argument_list|)
expr_stmt|;
while|while
condition|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|!=
operator|(
name|char
operator|)
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|LA
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|token
operator|.
name|consumeNext
argument_list|()
expr_stmt|;
name|consume
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|allowWildcards
operator|&&
name|isWildcard
argument_list|(
name|LA
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|token
operator|.
name|consumeNext
argument_list|()
expr_stmt|;
name|consume
argument_list|()
expr_stmt|;
continue|continue;
block|}
else|else
break|break;
block|}
return|return
name|token
return|;
block|}
specifier|protected
name|void
name|consume
parameter_list|()
block|{
name|pos
operator|++
expr_stmt|;
block|}
specifier|protected
name|TextToken
name|eof
parameter_list|()
block|{
name|consume
argument_list|()
expr_stmt|;
return|return
name|TextToken
operator|.
name|EOF_TOKEN
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|len
return|;
block|}
specifier|public
name|String
name|getText
parameter_list|()
block|{
return|return
name|text
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|TextToken
name|nextTerminalToken
parameter_list|(
name|boolean
name|wildcards
parameter_list|)
block|{
name|TextToken
name|token
init|=
literal|null
decl_stmt|;
name|char
name|ch
init|=
name|LA
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|==
operator|(
name|char
operator|)
operator|-
literal|1
condition|)
return|return
name|eof
argument_list|()
return|;
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|ch
argument_list|)
operator|||
name|is_mark
argument_list|(
name|ch
argument_list|)
operator|||
name|nonBreakingChar
argument_list|(
name|ch
argument_list|)
operator|||
name|singleCharToken
argument_list|(
name|ch
argument_list|)
operator|||
operator|(
name|wildcards
operator|&&
name|isWildcard
argument_list|(
name|ch
argument_list|)
operator|)
condition|)
block|{
name|token
operator|=
name|alpha
argument_list|(
literal|null
argument_list|,
name|wildcards
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|token
operator|==
literal|null
operator|&&
operator|(
name|Character
operator|.
name|isLetterOrDigit
argument_list|(
name|ch
argument_list|)
operator|||
operator|(
name|wildcards
operator|&&
name|isWildcard
argument_list|(
name|ch
argument_list|)
operator|)
operator|)
condition|)
name|token
operator|=
name|alphanum
argument_list|(
literal|null
argument_list|,
name|wildcards
argument_list|)
expr_stmt|;
if|if
condition|(
name|token
operator|==
literal|null
condition|)
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'\\'
case|:
if|if
condition|(
name|isWildcard
argument_list|(
name|LA
argument_list|(
literal|2
argument_list|)
argument_list|)
condition|)
block|{
name|consume
argument_list|()
expr_stmt|;
block|}
case|case
literal|'*'
case|:
case|case
literal|','
case|:
case|case
literal|'-'
case|:
case|case
literal|'_'
case|:
case|case
literal|':'
case|:
case|case
literal|'.'
case|:
case|case
literal|'@'
case|:
name|token
operator|=
name|p
argument_list|()
expr_stmt|;
break|break;
default|default :
name|token
operator|=
name|whitespace
argument_list|()
expr_stmt|;
break|break;
block|}
return|return
name|token
return|;
block|}
specifier|public
name|TextToken
name|nextToken
parameter_list|()
block|{
return|return
name|nextToken
argument_list|(
literal|false
argument_list|)
return|;
block|}
specifier|public
name|TextToken
name|nextToken
parameter_list|(
name|boolean
name|wildcards
parameter_list|)
block|{
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|TextToken
name|token
init|=
name|nextTerminalToken
argument_list|(
name|wildcards
argument_list|)
decl_stmt|;
name|TextToken
name|next
decl_stmt|;
name|StringBuffer
name|buf
decl_stmt|;
name|int
name|oldPos
init|=
name|pos
decl_stmt|;
name|boolean
name|found
decl_stmt|;
name|char
name|LA1
init|=
name|LA
argument_list|(
literal|1
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|token
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|TextToken
operator|.
name|EOF
case|:
return|return
literal|null
return|;
case|case
name|TextToken
operator|.
name|ALPHA
case|:
name|found
operator|=
literal|false
expr_stmt|;
comment|// text with apostrophe like Peter's
if|if
condition|(
name|LA1
operator|==
literal|'\''
condition|)
block|{
name|consume
argument_list|()
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuffer
argument_list|(
name|token
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|next
operator|=
name|nextTerminalToken
argument_list|(
name|wildcards
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|ALPHA
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|TextToken
argument_list|(
name|TextToken
operator|.
name|ALPHA
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
name|pos
operator|=
name|oldPos
expr_stmt|;
block|}
comment|// text with some alphanumeric sequence attached
comment|// like Q/22/A4.5 or 12/09/1989
switch|switch
condition|(
name|LA1
condition|)
block|{
case|case
literal|'_'
case|:
case|case
literal|':'
case|:
case|case
literal|'.'
case|:
case|case
literal|'/'
case|:
if|if
condition|(
name|LA
argument_list|(
literal|2
argument_list|)
operator|==
operator|(
name|char
operator|)
operator|-
literal|1
operator|||
name|Character
operator|.
name|isWhitespace
argument_list|(
name|LA
argument_list|(
literal|2
argument_list|)
argument_list|)
condition|)
block|{
name|consume
argument_list|()
expr_stmt|;
break|break;
block|}
name|found
operator|=
literal|false
expr_stmt|;
name|buf
operator|=
operator|new
name|StringBuffer
argument_list|(
name|token
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|nextTerminalToken
argument_list|(
name|wildcards
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|EOF
operator|||
name|next
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|WS
condition|)
break|break;
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|P
operator|&&
operator|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
operator|(
name|char
operator|)
operator|-
literal|1
operator|||
name|Character
operator|.
name|isWhitespace
argument_list|(
name|LA
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|)
condition|)
break|break;
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|ALPHANUM
condition|)
name|found
operator|=
literal|true
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|found
condition|)
name|token
operator|=
operator|new
name|TextToken
argument_list|(
name|TextToken
operator|.
name|ALPHANUM
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|pos
operator|=
name|oldPos
expr_stmt|;
block|}
return|return
name|token
return|;
case|case
name|TextToken
operator|.
name|ALPHANUM
case|:
switch|switch
condition|(
name|LA1
condition|)
block|{
case|case
literal|'/'
case|:
case|case
literal|'*'
case|:
case|case
literal|','
case|:
case|case
literal|'-'
case|:
case|case
literal|'_'
case|:
case|case
literal|':'
case|:
case|case
literal|'.'
case|:
case|case
literal|'@'
case|:
if|if
condition|(
name|LA
argument_list|(
literal|2
argument_list|)
operator|==
operator|(
name|char
operator|)
operator|-
literal|1
operator|||
name|Character
operator|.
name|isWhitespace
argument_list|(
name|LA
argument_list|(
literal|2
argument_list|)
argument_list|)
condition|)
block|{
name|consume
argument_list|()
expr_stmt|;
break|break;
block|}
name|buf
operator|=
operator|new
name|StringBuffer
argument_list|(
name|token
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|nextTerminalToken
argument_list|(
name|wildcards
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|EOF
operator|||
name|next
operator|.
name|getType
argument_list|()
operator|==
name|TextToken
operator|.
name|WS
condition|)
break|break;
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|token
operator|=
operator|new
name|TextToken
argument_list|(
name|TextToken
operator|.
name|ALPHANUM
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|token
return|;
default|default :
comment|// fall through to start of while loop
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"text: "
operator|+
name|text
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|protected
name|TextToken
name|number
parameter_list|()
block|{
name|TextToken
name|token
init|=
operator|new
name|TextToken
argument_list|(
name|TextToken
operator|.
name|NUMBER
argument_list|,
name|text
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|int
name|oldPos
init|=
name|pos
decl_stmt|;
while|while
condition|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|!=
operator|(
name|char
operator|)
operator|-
literal|1
operator|&&
name|Character
operator|.
name|isDigit
argument_list|(
name|LA
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|token
operator|.
name|consumeNext
argument_list|()
expr_stmt|;
name|consume
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|LA
argument_list|(
literal|1
argument_list|)
argument_list|)
condition|)
block|{
name|pos
operator|=
name|oldPos
expr_stmt|;
return|return
literal|null
return|;
block|}
return|return
name|token
return|;
block|}
specifier|protected
name|TextToken
name|p
parameter_list|()
block|{
name|temp
operator|.
name|set
argument_list|(
name|TextToken
operator|.
name|P
argument_list|,
name|text
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|temp
operator|.
name|consumeNext
argument_list|()
expr_stmt|;
name|consume
argument_list|()
expr_stmt|;
return|return
name|temp
return|;
block|}
specifier|public
name|void
name|setText
parameter_list|(
name|CharSequence
name|text
parameter_list|)
block|{
name|pos
operator|=
literal|0
expr_stmt|;
name|len
operator|=
name|text
operator|.
name|length
argument_list|()
expr_stmt|;
name|this
operator|.
name|text
operator|=
name|text
expr_stmt|;
block|}
specifier|protected
name|TextToken
name|whitespace
parameter_list|()
block|{
name|consume
argument_list|()
expr_stmt|;
return|return
name|TextToken
operator|.
name|WS_TOKEN
return|;
block|}
comment|/** 	 * The code ranges defined here should be interpreted as 1-char 	 * tokens. 	 */
specifier|private
specifier|static
specifier|final
name|boolean
name|singleCharToken
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
comment|// CJK Radicals Supplement
operator|(
name|ch
operator|>=
literal|'\u2E80'
operator|&&
name|ch
operator|<=
literal|'\u2EFF'
operator|)
operator|||
comment|// KangXi Radicals
operator|(
name|ch
operator|>=
literal|'\u2F00'
operator|&&
name|ch
operator|<=
literal|'\u2FDF'
operator|)
operator|||
comment|// Ideographic Description Characters
operator|(
name|ch
operator|>=
literal|'\u2FF0'
operator|&&
name|ch
operator|<=
literal|'\u2FFF'
operator|)
operator|||
comment|// Enclosed CJK Letters and Months
operator|(
name|ch
operator|>=
literal|'\u3200'
operator|&&
name|ch
operator|<=
literal|'\u32FF'
operator|)
operator|||
comment|// CJK Compatibility
operator|(
name|ch
operator|>=
literal|'\u3300'
operator|&&
name|ch
operator|<=
literal|'\u33FF'
operator|)
operator|||
comment|// CJK Unified Ideographs Extension A
operator|(
name|ch
operator|>=
literal|'\u3400'
operator|&&
name|ch
operator|<=
literal|'\u4DB5'
operator|)
operator|||
comment|// Yijing Hexagram Symbols
operator|(
name|ch
operator|>=
literal|'\u4DC0'
operator|&&
name|ch
operator|<=
literal|'\u4DFF'
operator|)
operator|||
comment|// CJK Unified Ideographs
operator|(
name|ch
operator|>=
literal|'\u4E00'
operator|&&
name|ch
operator|<=
literal|'\u9FFF'
operator|)
operator|||
comment|// CJK Compatibility Ideographs
operator|(
name|ch
operator|>=
literal|'\uF900'
operator|&&
name|ch
operator|<=
literal|'\uFAFF'
operator|)
operator|||
comment|// CJK Compatibility Forms
operator|(
name|ch
operator|>=
literal|'\uFE30'
operator|&&
name|ch
operator|<=
literal|'\uFE4F'
operator|)
return|;
block|}
comment|/** 	 * These codepoints should not be broken into tokens. 	 */
specifier|private
specifier|final
specifier|static
name|boolean
name|nonBreakingChar
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
comment|// Hiragana
operator|(
name|ch
operator|>=
literal|'\u3040'
operator|&&
name|ch
operator|<=
literal|'\u309F'
operator|)
operator|||
comment|// Katakana
operator|(
name|ch
operator|>=
literal|'\u30A0'
operator|&&
name|ch
operator|<=
literal|'\u30FF'
operator|)
operator|||
comment|// Bopomofo
operator|(
name|ch
operator|>=
literal|'\u3100'
operator|&&
name|ch
operator|<=
literal|'\u312F'
operator|)
operator|||
comment|// Hangul Compatibility Jamo
operator|(
name|ch
operator|>=
literal|'\u3130'
operator|&&
name|ch
operator|<=
literal|'\u318F'
operator|)
operator|||
comment|// Kanbun
operator|(
name|ch
operator|>=
literal|'\u3190'
operator|&&
name|ch
operator|<=
literal|'\u319F'
operator|)
operator|||
comment|// Bopomofo Extended
operator|(
name|ch
operator|>=
literal|'\u31A0'
operator|&&
name|ch
operator|<=
literal|'\u31BF'
operator|)
operator|||
comment|// Katakana Phonetic Extensions
operator|(
name|ch
operator|>=
literal|'\u31F0'
operator|&&
name|ch
operator|<=
literal|'\u31FF'
operator|)
operator|||
comment|// Hangul Syllables
operator|(
name|ch
operator|>=
literal|'\uAC00'
operator|&&
name|ch
operator|<=
literal|'\uD7A3'
operator|)
return|;
block|}
specifier|private
specifier|final
name|boolean
name|is_mark
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
operator|(
name|ch
operator|>
literal|'\u093d'
operator|&&
name|ch
operator|<
literal|'\u094c'
operator|)
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|String
name|t1
init|=
literal|"\u30A8\u30FB\u31A1\uACFF\u2FAA\u312A\u3045"
decl_stmt|;
name|String
name|t2
init|=
literal|"é¸å®çè¥ç©¶åºä¸æ³ä»¥é¸ä½éªå¸«åå¶è¨æ¯æ¬²è¶"
decl_stmt|;
name|String
name|t3
init|=
literal|"ë¬¸ì ì¬ì© ìì ì¤ë¥ë¥¼ ì°¾ìë´ê¸° ìí´ ê²ì¦ë ì¤êµ­ì´ íì ì¬ê²í íê³ , ë³´ë¤ ì½ê¸° ì½ê² íê¸° ìí´ ì¸ì´ì  ííì ë¤ë¬ëë¤."
decl_stmt|;
comment|//		for(int i = 0; i< t2.length(); i++) {
comment|//			char ch = t2.charAt(i);
comment|//			System.out.print(
comment|//				Integer.toHexString(ch) + ' '
comment|//			);
comment|//		}
name|SimpleTokenizer
name|tokenizer
init|=
operator|new
name|SimpleTokenizer
argument_list|()
decl_stmt|;
name|tokenizer
operator|.
name|setText
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|TextToken
name|token
init|=
name|tokenizer
operator|.
name|nextToken
argument_list|(
literal|true
argument_list|)
decl_stmt|;
while|while
condition|(
name|token
operator|!=
literal|null
operator|&&
name|token
operator|.
name|getType
argument_list|()
operator|!=
name|TextToken
operator|.
name|EOF
condition|)
block|{
comment|//System.out.println(token.getText());
name|token
operator|=
name|tokenizer
operator|.
name|nextToken
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

