begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|interpreter
operator|.
name|ContextAtExist
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
name|AnyNodeTest
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
name|ErrorCodes
operator|.
name|ErrorCode
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
name|SequenceIterator
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
name|ValueSequence
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
name|LocationStep
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
name|PathExpr
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
name|xslt
operator|.
name|XSLContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|XSLExceptions
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
name|Attr
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSLPathExpr
extends|extends
name|PathExpr
implements|implements
name|XSLExpression
block|{
specifier|public
name|XSLPathExpr
parameter_list|(
name|XSLContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|setToDefaults
argument_list|()
expr_stmt|;
block|}
specifier|public
name|XSLContext
name|getXSLContext
parameter_list|()
block|{
return|return
operator|(
name|XSLContext
operator|)
name|getContext
argument_list|()
return|;
block|}
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|XPathException
block|{
for|for
control|(
name|int
name|pos
init|=
literal|0
init|;
name|pos
operator|<
name|this
operator|.
name|getLength
argument_list|()
condition|;
name|pos
operator|++
control|)
block|{
name|Expression
name|expr
init|=
name|this
operator|.
name|getExpression
argument_list|(
name|pos
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|XSLPathExpr
condition|)
block|{
name|XSLPathExpr
name|xsl
init|=
operator|(
name|XSLPathExpr
operator|)
name|expr
decl_stmt|;
name|xsl
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xslt.instruct.Expression#compileError(java.lang.String) 	 */
specifier|public
name|void
name|compileError
parameter_list|(
name|String
name|code
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|code
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xslt.instruct.Expression#compileError(java.lang.String, java.lang.String) 	 */
specifier|public
name|void
name|compileError
parameter_list|(
name|ErrorCode
name|code
parameter_list|,
name|String
name|description
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|code
argument_list|,
name|description
argument_list|)
throw|;
block|}
specifier|public
name|Boolean
name|getBoolean
parameter_list|(
name|String
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|YES
argument_list|)
condition|)
return|return
literal|true
return|;
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|NO
argument_list|)
condition|)
return|return
literal|false
return|;
name|compileError
argument_list|(
name|XSLExceptions
operator|.
name|ERR_XTSE0020
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|_check_
parameter_list|(
name|Expression
name|path
parameter_list|)
block|{
for|for
control|(
name|int
name|index
init|=
literal|0
init|;
name|index
operator|<
name|path
operator|.
name|getSubExpressionCount
argument_list|()
condition|;
name|index
operator|++
control|)
block|{
name|Expression
name|expr
init|=
name|path
operator|.
name|getSubExpression
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|==
literal|0
operator|)
operator|&&
operator|(
name|expr
operator|instanceof
name|LocationStep
operator|)
condition|)
block|{
name|LocationStep
name|location
init|=
operator|(
name|LocationStep
operator|)
name|expr
decl_stmt|;
if|if
condition|(
name|location
operator|.
name|getTest
argument_list|()
operator|.
name|isWildcardTest
argument_list|()
condition|)
empty_stmt|;
if|else if
condition|(
name|location
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|CHILD_AXIS
condition|)
block|{
name|location
operator|.
name|setAxis
argument_list|(
name|Constants
operator|.
name|SELF_AXIS
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|_check_
argument_list|(
name|expr
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|_check_childNodes_
parameter_list|(
name|Expression
name|path
parameter_list|)
block|{
if|if
condition|(
name|path
operator|.
name|getSubExpressionCount
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|Expression
name|expr
init|=
name|path
operator|.
name|getSubExpression
argument_list|(
name|path
operator|.
name|getSubExpressionCount
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|LocationStep
condition|)
block|{
name|LocationStep
name|location
init|=
operator|(
name|LocationStep
operator|)
name|expr
decl_stmt|;
comment|//TODO: rewrite
if|if
condition|(
name|location
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
condition|)
empty_stmt|;
if|else if
condition|(
operator|!
literal|"node()"
operator|.
name|equals
argument_list|(
name|location
operator|.
name|getTest
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
operator|(
operator|(
name|PathExpr
operator|)
name|path
operator|)
operator|.
name|add
argument_list|(
operator|new
name|LocationStep
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|Constants
operator|.
name|CHILD_AXIS
argument_list|,
operator|new
name|AnyNodeTest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|location
operator|.
name|setAxis
argument_list|(
name|Constants
operator|.
name|CHILD_AXIS
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
name|void
name|_check_
parameter_list|(
name|Expression
name|path
parameter_list|,
name|boolean
name|childNodes
parameter_list|)
block|{
name|_check_
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|childNodes
condition|)
name|_check_childNodes_
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addText
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//UNDERSTAND: what is whitespace?		text = StringValue.trimWhitespace(text);
name|Text
name|constructer
init|=
operator|new
name|Text
argument_list|(
operator|(
name|XSLContext
operator|)
name|getContext
argument_list|()
argument_list|,
name|text
argument_list|)
decl_stmt|;
name|add
argument_list|(
name|constructer
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|SimpleConstructor
name|constructor
parameter_list|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|constructor
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setToDefaults
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|ContextAtExist
name|context
parameter_list|,
name|Attr
name|attr
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// TODO Auto-generated method stub
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
name|Sequence
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
name|Sequence
name|currentContext
decl_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|currentContext
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
else|else
name|currentContext
operator|=
name|contextSequence
expr_stmt|;
for|for
control|(
name|Expression
name|expr
range|:
name|steps
control|)
block|{
if|if
condition|(
name|currentContext
operator|==
literal|null
condition|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|expr
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|//Restore a position which may have been modified by inner expressions
name|int
name|p
init|=
name|context
operator|.
name|getContextPosition
argument_list|()
decl_stmt|;
name|Sequence
name|seq
init|=
name|context
operator|.
name|getContextSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|iterInner
init|=
name|currentContext
operator|.
name|iterate
argument_list|()
init|;
name|iterInner
operator|.
name|hasNext
argument_list|()
condition|;
name|p
operator|++
control|)
block|{
name|context
operator|.
name|setContextSequencePosition
argument_list|(
name|p
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|expr
operator|.
name|eval
argument_list|(
name|currentContext
argument_list|,
name|iterInner
operator|.
name|nextItem
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
comment|/** 	 * @deprecated Use {@link #process(XSLContext,SequenceIterator)} instead 	 */
specifier|public
name|void
name|process
parameter_list|(
name|SequenceIterator
name|sequenceIterator
parameter_list|,
name|XSLContext
name|context
parameter_list|)
block|{
name|process
argument_list|(
name|context
argument_list|,
name|sequenceIterator
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|process
parameter_list|(
name|XSLContext
name|context
parameter_list|,
name|SequenceIterator
name|sequenceIterator
parameter_list|)
block|{
for|for
control|(
name|Expression
name|step
range|:
name|steps
control|)
block|{
operator|(
operator|(
name|XSLPathExpr
operator|)
name|step
operator|)
operator|.
name|process
argument_list|(
name|context
argument_list|,
name|sequenceIterator
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

