begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2006 The eXist team  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
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
name|Dependency
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
name|Expression
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
name|Function
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
name|FunctionSignature
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
name|Profiler
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
name|NumericValue
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
name|SequenceType
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
name|StringValue
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
comment|/**  * Built-in function fn:substring().  *  *	@author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|FunSubstring
extends|extends
name|Function
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"substring"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the portion of the value of $a beginning at the position indicated by the value of $b and continuing to the end of $a. The characters returned do not extend beyond the end of $a. If $b is zero or negative, only those characters in positions greater than zero are returned. If the value of $a is the empty sequence, the zero-length string is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"substring"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the portion of the value of $a beginning at the position indicated by the value of $b and continuing for the number of characters indicated by the value of $c. The characters returned do not extend beyond the end of $a. If $b is zero or negative, only those characters in positions greater than zero are returned. If the value of $a is the empty sequence, the zero-length string is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunSubstring
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|STRING
return|;
block|}
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
comment|//start profiler
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//get arguments
name|Expression
name|argSourceString
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Expression
name|argStartingLoc
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Expression
name|argLength
init|=
literal|null
decl_stmt|;
comment|//get the context sequence
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|Sequence
name|result
decl_stmt|;
name|Sequence
name|seqSourceString
init|=
name|argSourceString
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
comment|//If the value of $sourceString is the empty sequence return EMPTY_STRING, there must be a string to operate on!
if|if
condition|(
name|seqSourceString
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|result
operator|=
name|StringValue
operator|.
name|EMPTY_STRING
expr_stmt|;
block|}
else|else
block|{
comment|//get the string to substring
name|String
name|sourceString
init|=
name|seqSourceString
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|//check for a valid start position for the substring
name|NumericValue
name|startingLoc
init|=
operator|(
operator|(
name|NumericValue
operator|)
operator|(
name|argStartingLoc
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
operator|)
operator|)
operator|.
name|round
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|validStartPosition
argument_list|(
name|startingLoc
argument_list|,
name|sourceString
operator|.
name|length
argument_list|()
argument_list|)
condition|)
block|{
comment|//invalid start position
name|result
operator|=
name|StringValue
operator|.
name|EMPTY_STRING
expr_stmt|;
block|}
else|else
block|{
comment|//are there 2 or 3 arguments to this function?
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|2
condition|)
block|{
comment|//three arguments, get the third argument value for the length
name|argLength
operator|=
name|getArgument
argument_list|(
literal|2
argument_list|)
expr_stmt|;
comment|//check for a valid length for the substring
name|NumericValue
name|length
init|=
operator|(
operator|(
name|NumericValue
operator|)
operator|(
name|argLength
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|NUMBER
argument_list|)
operator|)
operator|)
operator|.
name|round
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|validLength
argument_list|(
name|length
argument_list|)
condition|)
block|{
comment|//invalid length
name|result
operator|=
name|StringValue
operator|.
name|EMPTY_STRING
expr_stmt|;
block|}
else|else
block|{
comment|//if the length extends past the end of the string, just return the string from the start position
if|if
condition|(
name|length
operator|.
name|getInt
argument_list|()
operator|>
name|sourceString
operator|.
name|length
argument_list|()
operator|||
name|startingLoc
operator|.
name|getInt
argument_list|()
operator|-
literal|1
operator|+
name|length
operator|.
name|getInt
argument_list|()
operator|>
name|sourceString
operator|.
name|length
argument_list|()
operator|||
name|length
operator|.
name|isInfinite
argument_list|()
condition|)
block|{
comment|//fallback to fn:substring(string, start)
name|result
operator|=
name|substring
argument_list|(
name|sourceString
argument_list|,
name|startingLoc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//three arguments fn:substring(string, start, length)
name|result
operator|=
name|substring
argument_list|(
name|sourceString
argument_list|,
name|startingLoc
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
comment|//two arguments fn:substring(string, start)
name|result
operator|=
name|substring
argument_list|(
name|sourceString
argument_list|,
name|startingLoc
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//end profiler
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/** 	 * Checks that the startPosition is valid for the length of the $sourceString 	 *  	 * @param startPosition		The user specified startPosition for the fn:substring(), start index is 1 	 * @param stringLength		The length of the $sourceString passed to fn:substring() 	 *  	 * @return true if the startPosition is valid, false otherwise 	 */
specifier|private
name|boolean
name|validStartPosition
parameter_list|(
name|NumericValue
name|startPosition
parameter_list|,
name|int
name|stringLength
parameter_list|)
block|{
comment|//if start position is not a number return false
if|if
condition|(
name|startPosition
operator|.
name|isNaN
argument_list|()
condition|)
return|return
literal|false
return|;
comment|//if start position is infinite return false
if|if
condition|(
name|startPosition
operator|.
name|isInfinite
argument_list|()
condition|)
return|return
literal|false
return|;
comment|//if the start position extends beyond $sourceString return EMPTY_STRING
try|try
block|{
if|if
condition|(
name|startPosition
operator|.
name|getInt
argument_list|()
operator|>=
name|stringLength
condition|)
return|return
literal|false
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|//start position is valid
return|return
literal|true
return|;
block|}
comment|/** 	 * Checks that the length is valid for the $sourceString 	 *  	 * @param length		The user specified length for the fn:substring() 	 *  	 * @return true if the length is valid, false otherwise 	 */
specifier|private
name|boolean
name|validLength
parameter_list|(
name|NumericValue
name|length
parameter_list|)
block|{
comment|//if length is not a number return false
if|if
condition|(
name|length
operator|.
name|isNaN
argument_list|()
condition|)
return|return
literal|false
return|;
comment|//length is valid
return|return
literal|true
return|;
block|}
comment|/** 	 * fn:substring($sourceString, $startingLoc) 	 *  	 * @see http://www.w3.org/TR/xpath-functions/#func-substring 	 *  	 * @param stringSource	The source string to substring 	 * @param startingLoc	The Starting Location for the substring, start index is 1 	 *  	 * @return The StringValue of the substring 	 */
specifier|private
name|StringValue
name|substring
parameter_list|(
name|String
name|sourceString
parameter_list|,
name|NumericValue
name|startingLoc
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|startingLoc
operator|.
name|getInt
argument_list|()
operator|<=
literal|1
condition|)
block|{
comment|//start value is 1 or less, so just return the string
return|return
operator|new
name|StringValue
argument_list|(
name|sourceString
argument_list|)
return|;
block|}
comment|//start index of xs:string is 1, whereas java string is 0; so subtract 1
return|return
operator|new
name|StringValue
argument_list|(
name|sourceString
operator|.
name|substring
argument_list|(
name|startingLoc
operator|.
name|getInt
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 * fn:substring($sourceString, $startingLoc, $length) 	 *  	 * @see http://www.w3.org/TR/xpath-functions/#func-substring 	 *  	 * @param stringSource	The source string to substring 	 * @param startingLoc	The Starting Location for the substring, start index is 1 	 * @param length	The length of the substring 	 *  	 * @return The StringValue of the substring 	 */
specifier|private
name|StringValue
name|substring
parameter_list|(
name|String
name|sourceString
parameter_list|,
name|NumericValue
name|startingLoc
parameter_list|,
name|NumericValue
name|length
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//if start value is 1 or less, start at the start of the string and adjust the length appropriately
if|if
condition|(
name|startingLoc
operator|.
name|getInt
argument_list|()
operator|<=
literal|1
condition|)
block|{
comment|//the -1 is to transition from xs:string index which starts at 1 to Java string index which starts at 0
name|int
name|endIndex
init|=
name|length
operator|.
name|getInt
argument_list|()
operator|+
operator|(
operator|-
literal|1
operator|+
name|startingLoc
operator|.
name|getInt
argument_list|()
operator|)
decl_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|sourceString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|endIndex
argument_list|)
argument_list|)
return|;
block|}
return|return
operator|new
name|StringValue
argument_list|(
name|sourceString
operator|.
name|substring
argument_list|(
name|startingLoc
operator|.
name|getInt
argument_list|()
operator|-
literal|1
argument_list|,
name|startingLoc
operator|.
name|getInt
argument_list|()
operator|-
literal|1
operator|+
name|length
operator|.
name|getInt
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

