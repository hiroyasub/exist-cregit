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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|dom
operator|.
name|memtree
operator|.
name|DocumentBuilderReceiver
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|Type
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
name|pattern
operator|.
name|Pattern
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  *<!-- Category: instruction -->  *<xsl:value-of  *   select? = expression  *   separator? = { string }  *   [disable-output-escaping]? = "yes" | "no">  *<!-- Content: sequence-constructor -->  *</xsl:value-of>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ValueOf
extends|extends
name|SimpleConstructor
block|{
specifier|private
name|String
name|attr_select
init|=
literal|null
decl_stmt|;
specifier|private
name|XSLPathExpr
name|select
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|separator
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|disable_output_escaping
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|sequenceItSelf
init|=
literal|false
decl_stmt|;
specifier|public
name|ValueOf
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
name|attr_select
operator|=
literal|null
expr_stmt|;
name|select
operator|=
literal|null
expr_stmt|;
comment|//the default separator is a single space (#x20) when the content is specified using the select attribute,
comment|//or a zero-length string when the content is specified using a sequence constructor.
name|separator
operator|=
literal|null
expr_stmt|;
name|disable_output_escaping
operator|=
literal|null
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
name|getNodeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|SELECT
argument_list|)
condition|)
block|{
name|attr_select
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
name|SEPARATOR
argument_list|)
condition|)
block|{
name|separator
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
name|DISABLE_OUTPUT_ESCAPING
argument_list|)
condition|)
block|{
name|disable_output_escaping
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
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
name|boolean
name|atRootCall
init|=
literal|false
decl_stmt|;
comment|//XXX: rewrite
if|if
condition|(
name|attr_select
operator|!=
literal|null
condition|)
block|{
name|select
operator|=
operator|new
name|XSLPathExpr
argument_list|(
name|getXSLContext
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
name|attr_select
argument_list|,
name|select
argument_list|)
expr_stmt|;
comment|//UNDERSTAND:<node>text<node>  step = "." -> SELF:node(), but need CHILD:node()
comment|//			if ((contextInfo.getFlags()& DOT_TEST) != 0) {
comment|//				atRootCall = true;
comment|//				_check_(select);
comment|//				contextInfo.removeFlag(DOT_TEST);
comment|//			}
comment|//
comment|//			_check_childNodes_(select);
block|}
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|atRootCall
condition|)
name|contextInfo
operator|.
name|addFlag
argument_list|(
name|DOT_TEST
argument_list|)
expr_stmt|;
block|}
comment|//	protected void _check_(Expression path) {
comment|//		for (int pos = 0; pos< path.getLength(); pos++) {
comment|//			Expression expr = path.getExpression(pos);
comment|//			if (expr instanceof RootNode) {
comment|//				expr = new LocationStep(getContext(), Constants.CHILD_AXIS, new AnyNodeTest());
comment|//				path.replaceExpression(pos, expr);
comment|//				continue;
comment|//			}
comment|//			if ((pos == 0)&& (expr instanceof LocationStep)) {
comment|//				LocationStep location = (LocationStep) expr;
comment|//				if (location.getAxis() == Constants.SELF_AXIS) {
comment|//					location.setAxis(Constants.CHILD_AXIS);
comment|//				}
comment|//			} else {
comment|//				_check_(expr);
comment|//			}
comment|//		}
comment|//		if (path.getLength() != 0) {
comment|//			Expression expr = path.getExpression(path.getLength()-1);
comment|//			if (expr instanceof LocationStep) {
comment|//				LocationStep location = (LocationStep) expr;
comment|//				//TODO: rewrite
comment|//				if (!"node()".equals(location.getTest().toString())) {
comment|//					((PathExpr)path).add(new LocationStep(getContext(), Constants.CHILD_AXIS, new AnyNodeTest()));
comment|//				}
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
comment|//        if (contextItem != null) {
comment|//            contextSequence = contextItem.toSequence();
comment|//        }
comment|// evaluate the expression
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|Sequence
name|result
decl_stmt|;
try|try
block|{
comment|//        	System.out.println("=================================================================");
comment|//        	System.out.println("select = "+select);
comment|//        	System.out.println("contextSequence = "+contextSequence);
comment|//        	System.out.println("contextItem     = "+contextItem);
name|result
operator|=
name|select
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceItSelf
condition|)
return|return
name|result
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
comment|// create the output
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|)
decl_stmt|;
try|try
block|{
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|StringBuilder
name|buf
init|=
literal|null
decl_stmt|;
name|boolean
name|allowAttribs
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
comment|// if item is an atomic value, collect the string values of all
comment|// following atomic values and seperate them by a space.
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
if|if
condition|(
name|buf
operator|==
literal|null
condition|)
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
if|else if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|allowAttribs
operator|=
literal|false
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
comment|// if item is a node, flush any collected character data and
comment|//	copy the node to the target doc.
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
name|buf
operator|!=
literal|null
operator|&&
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATTRIBUTE
operator|&&
operator|!
name|allowAttribs
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"XQTY0024: An attribute may not appear after "
operator|+
literal|"another child node."
argument_list|)
throw|;
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
name|allowAttribs
operator|=
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATTRIBUTE
expr_stmt|;
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
block|}
block|}
comment|// flush remaining character data
if|if
condition|(
name|buf
operator|!=
literal|null
operator|&&
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|receiver
operator|.
name|characters
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"SAXException during serialization: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
comment|//throw new XPathException(getASTNode(),
comment|//	"Encountered SAX exception while serializing enclosed expression: "
comment|//		+ ExpressionDumper.dump(this));
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
literal|"<xsl:value-of"
argument_list|)
expr_stmt|;
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" select = "
argument_list|)
expr_stmt|;
name|select
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|separator
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" separator = "
operator|+
name|separator
argument_list|)
expr_stmt|;
if|if
condition|(
name|disable_output_escaping
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" disable-output-escaping = "
operator|+
name|disable_output_escaping
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
literal|"</xsl:value-of>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<xsl:value-of"
argument_list|)
expr_stmt|;
if|if
condition|(
name|select
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" select = "
operator|+
name|select
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|separator
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" separator = "
operator|+
name|separator
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|disable_output_escaping
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" disable-output-escaping = "
operator|+
name|disable_output_escaping
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"</xsl:value-of>"
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
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
try|try
block|{
name|Sequence
name|result
init|=
name|select
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|context
operator|.
name|getResultWriter
argument_list|()
operator|.
name|writeCharacters
argument_list|(
name|result
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLStreamException
name|e
parameter_list|)
block|{
comment|// TODO Auto-generated catch block
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

