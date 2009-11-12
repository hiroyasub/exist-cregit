begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|AnalyzeContextInfo
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
name|NodeTest
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
name|TypeTest
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
name|Variable
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
name|util
operator|.
name|ExpressionDumper
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
name|ValueSequence
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
name|xslt
operator|.
name|expression
operator|.
name|i
operator|.
name|Parameted
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
name|pattern
operator|.
name|Pattern
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  *<!-- Category: declaration -->  *<xsl:template  * 	match? = pattern  * 	name? = qname  * 	priority? = number  * 	mode? = tokens  * 	as? = sequence-type>  *<!-- Content: (xsl:param*, sequence-constructor) -->  *</xsl:template>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Template
extends|extends
name|Declaration
implements|implements
name|Parameted
implements|,
name|Comparable
argument_list|<
name|Template
argument_list|>
block|{
specifier|private
name|String
name|attr_match
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|attr_priority
init|=
literal|null
decl_stmt|;
specifier|private
name|PathExpr
name|match
init|=
literal|null
decl_stmt|;
specifier|private
name|QName
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|QName
index|[]
name|mode
init|=
literal|null
decl_stmt|;
specifier|private
name|Double
name|priority
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|as
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
name|params
init|=
literal|null
decl_stmt|;
specifier|public
name|Template
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
block|}
specifier|public
name|void
name|setToDefaults
parameter_list|()
block|{
name|attr_match
operator|=
literal|null
expr_stmt|;
name|attr_priority
operator|=
literal|null
expr_stmt|;
name|match
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
name|mode
operator|=
literal|null
expr_stmt|;
name|priority
operator|=
literal|0.5
expr_stmt|;
comment|//UNDERSTAND: what should be default
name|as
operator|=
literal|"item()*"
expr_stmt|;
block|}
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
name|String
name|attr_name
init|=
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|MATCH
argument_list|)
condition|)
block|{
name|attr_match
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|NAME
argument_list|)
condition|)
block|{
name|name
operator|=
operator|new
name|QName
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|PRIORITY
argument_list|)
condition|)
block|{
name|attr_priority
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|MODE
argument_list|)
condition|)
block|{
comment|//			mode = attr.getValue();//TODO: write
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|AS
argument_list|)
condition|)
block|{
name|as
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|isRootMatch
argument_list|()
condition|)
name|contextInfo
operator|.
name|addFlag
argument_list|(
name|DOT_TEST
argument_list|)
expr_stmt|;
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|attr_match
operator|!=
literal|null
condition|)
block|{
name|match
operator|=
operator|new
name|PathExpr
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|Pattern
operator|.
name|parse
argument_list|(
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|attr_match
argument_list|,
name|match
argument_list|)
expr_stmt|;
name|_check_
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|attr_priority
operator|!=
literal|null
condition|)
try|try
block|{
name|priority
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|attr_priority
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|compileError
argument_list|(
name|XSLExceptions
operator|.
name|ERR_XTSE0530
argument_list|)
expr_stmt|;
block|}
else|else
name|priority
operator|=
name|computedPriority
argument_list|()
expr_stmt|;
name|setUseStaticContext
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|validate
parameter_list|()
throws|throws
name|XPathException
block|{
name|super
operator|.
name|validate
argument_list|()
expr_stmt|;
block|}
specifier|private
name|double
name|computedPriority
parameter_list|()
block|{
name|double
name|priority
init|=
literal|0.5
decl_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
if|if
condition|(
name|match
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Expression
name|expr
init|=
name|match
operator|.
name|getExpression
argument_list|(
literal|0
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
name|locationStep
init|=
operator|(
name|LocationStep
operator|)
name|expr
decl_stmt|;
name|NodeTest
name|test
init|=
name|locationStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|test
operator|.
name|getName
argument_list|()
operator|==
literal|null
operator|)
operator|||
operator|(
name|test
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
operator|==
literal|null
operator|)
condition|)
name|priority
operator|=
operator|-
literal|0.5
expr_stmt|;
if|else if
condition|(
name|locationStep
operator|.
name|getPredicates
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|priority
operator|=
literal|0.25
expr_stmt|;
else|else
name|priority
operator|=
literal|0
expr_stmt|;
comment|//TODO: else (element(E,T) 0.25 (matches by name and type) ...)
block|}
block|}
return|return
name|priority
return|;
block|}
specifier|public
name|boolean
name|isSmallWildcard
parameter_list|()
block|{
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
if|if
condition|(
name|match
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Expression
name|expr
init|=
name|match
operator|.
name|getExpression
argument_list|(
literal|0
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
name|locationStep
init|=
operator|(
name|LocationStep
operator|)
name|expr
decl_stmt|;
name|NodeTest
name|test
init|=
name|locationStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
name|test
operator|instanceof
name|TypeTest
condition|)
block|{
if|if
condition|(
name|test
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|true
return|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
comment|//	private void _check_(PathExpr path) {
comment|//		for (int pos = 0; pos< path.getLength(); pos++) {
comment|//			Expression expr = path.getExpression(pos);
comment|//			if ((pos == 0)&& (expr instanceof LocationStep)) {
comment|//				LocationStep location = (LocationStep) expr;
comment|//				if (location.getAxis() == Constants.CHILD_AXIS) {
comment|//					location.setAxis(Constants.SELF_AXIS);
comment|//				}
comment|//			} else if (expr instanceof PathExpr) {
comment|//				_check_((PathExpr) expr);
comment|//			}
comment|//		}
comment|//	}
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
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|Sequence
name|answer
init|=
name|super
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|//	public Sequence eval(Sequence contextSequence, Item contextItem) throws XPathException {
comment|//		Sequence result = new ValueSequence();
comment|//
comment|//		if ((contextItem == null)&& (isSmallWildcard()))
comment|//			return result; //UNDERSTAND: is it ok??? maybe better null or check at XSLComp
comment|//
comment|////		if ((contextSequence == null)&& (isBigWildcard()))
comment|////			return result; //UNDERSTAND: is it ok??? maybe better null or check at XSLComp
comment|//
comment|//		Sequence matched = match.eval(contextSequence, contextItem);
comment|//		for (Item item : matched) {
comment|//			Sequence answer = super.eval(item.toSequence(), item);//item
comment|//			result.addAll(answer);
comment|//		}
comment|//
comment|//		return result;
comment|//	}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Template
name|template
parameter_list|)
block|{
if|if
condition|(
name|priority
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Priority can't be null."
argument_list|)
throw|;
if|if
condition|(
name|template
operator|.
name|priority
operator|==
literal|null
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Priority can't be null."
argument_list|)
throw|;
comment|//-compareTo  to make order from high to low
name|int
name|compared
init|=
name|priority
operator|.
name|compareTo
argument_list|(
name|template
operator|.
name|priority
argument_list|)
decl_stmt|;
if|if
condition|(
name|compared
operator|==
literal|0
condition|)
block|{
name|int
name|thisVal
init|=
name|getExpressionId
argument_list|()
decl_stmt|;
name|int
name|anotherVal
init|=
name|template
operator|.
name|getExpressionId
argument_list|()
decl_stmt|;
return|return
operator|(
name|thisVal
operator|<
name|anotherVal
condition|?
operator|+
literal|1
else|:
operator|(
name|thisVal
operator|==
name|anotherVal
condition|?
literal|0
else|:
operator|-
literal|1
operator|)
operator|)
return|;
block|}
return|return
operator|-
name|compared
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"<xsl:template"
argument_list|)
expr_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" match = "
argument_list|)
expr_stmt|;
name|match
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" name = "
operator|+
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" mode = "
operator|+
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|attr_priority
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" priority = "
operator|+
name|attr_priority
argument_list|)
expr_stmt|;
if|if
condition|(
name|as
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" as = "
operator|+
name|as
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
name|super
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"</xsl:template>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<xsl:template"
argument_list|)
expr_stmt|;
if|if
condition|(
name|match
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" match = "
operator|+
name|match
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" name = "
operator|+
name|name
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mode
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" mode = "
operator|+
name|mode
argument_list|)
expr_stmt|;
if|if
condition|(
name|attr_priority
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" priority = "
operator|+
name|attr_priority
argument_list|)
expr_stmt|;
if|if
condition|(
name|as
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" as = "
operator|+
name|as
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
comment|//        result.append(super.toString());
comment|//        result.append("</xsl:template> ");
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|matched
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|match
operator|==
literal|null
condition|)
return|return
literal|false
return|;
name|boolean
name|matched
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|match
operator|.
name|getLength
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|Expression
name|expr
init|=
name|match
operator|.
name|getExpression
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|expr
operator|.
name|match
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|expr
operator|instanceof
name|LocationStep
condition|)
block|{
name|item
operator|=
operator|(
name|Item
operator|)
operator|(
operator|(
name|Node
operator|)
name|item
operator|)
operator|.
name|getParentNode
argument_list|()
expr_stmt|;
block|}
name|matched
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|matched
return|;
block|}
specifier|public
name|boolean
name|isRootMatch
parameter_list|()
block|{
return|return
operator|(
literal|"/"
operator|.
name|equals
argument_list|(
name|attr_match
argument_list|)
operator|)
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
name|getXSLParams
parameter_list|()
block|{
if|if
condition|(
name|params
operator|==
literal|null
condition|)
name|params
operator|=
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
argument_list|()
expr_stmt|;
return|return
name|params
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xslt.expression.i.Parameted#addXSLParam(org.exist.xslt.expression.Param) 	 */
specifier|public
name|void
name|addXSLParam
parameter_list|(
name|Param
name|param
parameter_list|)
throws|throws
name|XPathException
block|{
name|Map
argument_list|<
name|QName
argument_list|,
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Variable
argument_list|>
name|params
init|=
name|getXSLParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|params
operator|.
name|containsKey
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
name|compileError
argument_list|(
name|XSLExceptions
operator|.
name|ERR_XTSE0580
argument_list|)
expr_stmt|;
name|Variable
name|variable
init|=
name|context
operator|.
name|declareVariable
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|,
name|param
argument_list|)
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|param
operator|.
name|getName
argument_list|()
argument_list|,
name|variable
argument_list|)
expr_stmt|;
block|}
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

end_unit

