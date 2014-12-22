begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
operator|.
name|text
package|;
end_package

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
name|persistent
operator|.
name|NodeSet
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
name|storage
operator|.
name|TermMatcher
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
name|Constants
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
name|DoubleValue
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
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|Type
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|FuzzyMatchAll
extends|extends
name|AbstractMatchFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"fuzzy-match-all"
argument_list|,
name|TextModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|TextModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Fuzzy keyword search, which compares strings based on the Levenshtein distance "
operator|+
literal|"(or edit distance). The function tries to match each of the keywords specified in the "
operator|+
literal|"keyword string against the string value of each item in the sequence $source."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"source"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The source"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"keyword"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The keyword string"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the sequence of nodes that match the keywords"
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|public
name|FuzzyMatchAll
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|FULLTEXT_AND
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FuzzyMatchAll
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|type
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|type
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|evalQuery
parameter_list|(
name|NodeSet
name|nodes
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|terms
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|terms
operator|==
literal|null
operator|||
name|terms
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|// no search terms
name|double
name|threshold
init|=
literal|0.65
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
specifier|final
name|Sequence
name|thresOpt
init|=
name|getArgument
argument_list|(
literal|2
argument_list|)
operator|.
name|eval
argument_list|(
name|nodes
argument_list|)
decl_stmt|;
comment|//TODO : get rid of getLength()
if|if
condition|(
operator|!
name|thresOpt
operator|.
name|hasOne
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"third argument to "
operator|+
name|getName
argument_list|()
operator|+
literal|"should be a single double value"
argument_list|)
throw|;
block|}
name|threshold
operator|=
operator|(
operator|(
name|DoubleValue
operator|)
name|thresOpt
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
operator|)
operator|.
name|getDouble
argument_list|()
expr_stmt|;
block|}
specifier|final
name|NodeSet
name|hits
index|[]
init|=
operator|new
name|NodeSet
index|[
name|terms
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|String
name|term
decl_stmt|;
name|TermMatcher
name|matcher
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|terms
operator|.
name|size
argument_list|()
condition|;
name|k
operator|++
control|)
block|{
name|term
operator|=
name|terms
operator|.
name|get
argument_list|(
name|k
argument_list|)
expr_stmt|;
if|if
condition|(
name|term
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|hits
index|[
name|k
index|]
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|matcher
operator|=
operator|new
name|FuzzyMatcher
argument_list|(
name|term
argument_list|,
name|threshold
argument_list|)
expr_stmt|;
name|hits
index|[
name|k
index|]
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getTextEngine
argument_list|()
operator|.
name|getNodes
argument_list|(
name|context
argument_list|,
name|nodes
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|nodes
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
literal|null
argument_list|,
name|matcher
argument_list|,
name|term
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|mergeResults
argument_list|(
name|hits
argument_list|)
return|;
block|}
block|}
end_class

end_unit

