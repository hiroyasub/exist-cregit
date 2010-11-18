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
name|org
operator|.
name|exist
operator|.
name|Namespaces
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
name|memtree
operator|.
name|NodeImpl
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
name|XMLChar
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
name|QNameValue
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
name|xslt
operator|.
name|ErrorCodes
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
name|XSL
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

begin_comment
comment|/**  *<!-- Category: instruction -->  *<xsl:element  *   name = { qname }  *   namespace? = { uri-reference }  *   inherit-namespaces? = "yes" | "no"  *   use-attribute-sets? = qnames  *   type? = qname  *   validation? = "strict" | "lax" | "preserve" | "strip">  *<!-- Content: sequence-constructor -->  *</xsl:element>  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Element
extends|extends
name|SimpleConstructor
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|namespace
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|inherit_namespaces
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|use_attribute_sets
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|type
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|validation
init|=
literal|null
decl_stmt|;
specifier|private
name|XSLPathExpr
name|qnameExpr
init|=
literal|null
decl_stmt|;
specifier|private
name|PathExpr
name|content
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Attribute
argument_list|>
name|attributes
init|=
operator|new
name|ArrayList
argument_list|<
name|Attribute
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Element
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
name|Element
parameter_list|(
name|XSLContext
name|context
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|setContent
parameter_list|(
name|PathExpr
name|content
parameter_list|)
block|{
name|this
operator|.
name|content
operator|=
name|content
expr_stmt|;
block|}
specifier|public
name|void
name|addAttribute
parameter_list|(
name|Attr
name|attr
parameter_list|)
block|{
name|attributes
operator|.
name|add
argument_list|(
operator|new
name|Attribute
argument_list|(
name|getXSLContext
argument_list|()
argument_list|,
name|attr
operator|.
name|getName
argument_list|()
argument_list|,
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setToDefaults
parameter_list|()
block|{
name|name
operator|=
literal|null
expr_stmt|;
name|namespace
operator|=
literal|null
expr_stmt|;
name|inherit_namespaces
operator|=
literal|null
expr_stmt|;
name|use_attribute_sets
operator|=
literal|null
expr_stmt|;
name|type
operator|=
literal|null
expr_stmt|;
name|validation
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
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|Namespaces
operator|.
name|XSL_NS
operator|.
name|equals
argument_list|(
name|attr
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
if|if
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
name|NAMESPACE
argument_list|)
condition|)
block|{
name|namespace
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
name|INHERIT_NAMESPACES
argument_list|)
condition|)
block|{
name|inherit_namespaces
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
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|USE_ATTRIBUTE_SETS
argument_list|)
condition|)
block|{
name|use_attribute_sets
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
name|TYPE
argument_list|)
condition|)
block|{
name|type
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
name|VALIDATION
argument_list|)
condition|)
block|{
name|validation
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
name|addAttribute
argument_list|(
name|attr
argument_list|)
expr_stmt|;
block|}
else|else
name|addAttribute
argument_list|(
name|attr
argument_list|)
expr_stmt|;
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
name|context
operator|.
name|pushInScopeNamespaces
argument_list|()
expr_stmt|;
comment|// declare namespaces
comment|//        if(namespaceDecls != null) {
comment|//            for(int i = 0; i< namespaceDecls.length; i++) {
comment|//                if ("".equals(namespaceDecls[i].getNamespaceURI())) {
comment|//                    // TODO: the specs are unclear here: should we throw XQST0085 or not?
comment|//                    context.inScopeNamespaces.remove(namespaceDecls[i].getLocalName());
comment|////					if (context.inScopeNamespaces.remove(namespaceDecls[i].getLocalName()) == null)
comment|////		        		throw new XPathException(getASTNode(), "XQST0085 : can not undefine '" + namespaceDecls[i] + "'");
comment|//                } else
comment|//                    context.declareInScopeNamespace(namespaceDecls[i].getLocalName(), namespaceDecls[i].getNamespaceURI());
comment|//            }
comment|//        }
name|AnalyzeContextInfo
name|newContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|newContextInfo
operator|.
name|addFlag
argument_list|(
name|IN_NODE_CONSTRUCTOR
argument_list|)
expr_stmt|;
comment|//        qnameExpr.analyze(newContextInfo);
comment|//        if(attributes != null) {
comment|//            for(int i = 0; i< attributes.length; i++) {
comment|//                attributes[i].analyze(newContextInfo);
comment|//            }
comment|//        }
for|for
control|(
name|Attribute
name|attr
range|:
name|attributes
control|)
block|{
name|attr
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
name|qnameExpr
operator|=
name|Pattern
operator|.
name|parse
argument_list|(
name|contextInfo
operator|.
name|getContext
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|qnameExpr
operator|!=
literal|null
condition|)
name|qnameExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
comment|//analyze content
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
name|content
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|context
operator|.
name|popInScopeNamespaces
argument_list|()
expr_stmt|;
block|}
comment|//	private boolean internalCall = false;
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
comment|//		if (!internalCall) {
comment|//			internalCall = true;
comment|//			return constructor.eval(contextSequence, contextItem);
comment|//		}
comment|//		internalCall = false;
comment|//		return super.eval(contextSequence, contextItem);
name|context
operator|.
name|expressionStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|pushInScopeNamespaces
argument_list|()
expr_stmt|;
if|if
condition|(
name|newDocumentContext
condition|)
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
comment|// declare namespaces
comment|//            if(namespaceDecls != null) {
comment|//                for(int i = 0; i< namespaceDecls.length; i++) {
comment|//                    //if ("".equals(namespaceDecls[i].getNamespaceURI())) {
comment|//                        // TODO: the specs are unclear here: should we throw XQST0085 or not?
comment|//                    //	context.inScopeNamespaces.remove(namespaceDecls[i].getLocalName());
comment|////					if (context.inScopeNamespaces.remove(namespaceDecls[i].getLocalName()) == null)
comment|////		        		throw new XPathException(getAS      TNode(), "XQST0085 : can not undefine '" + namespaceDecls[i] + "'");
comment|//                    //} else
comment|//                        context.declareInScopeNamespace(namespaceDecls[i].getLocalName(), namespaceDecls[i].getNamespaceURI());
comment|//                }
comment|//            }
comment|//            AttributesImpl attrs = new AttributesImpl();
comment|//            if(attributes != null) {
comment|//                AttributeConstructor constructor;
comment|//                Sequence attrValues;
comment|//                QName attrQName;
comment|//                // first, search for xmlns attributes and declare in-scope namespaces
comment|//                for (int i = 0; i< attributes.length; i++) {
comment|//                    constructor = attributes[i];
comment|//                    if(constructor.isNamespaceDeclaration()) {
comment|//                        int p = constructor.getQName().indexOf(':');
comment|//                        if(p == Constants.STRING_NOT_FOUND)
comment|//                            context.declareInScopeNamespace("", constructor.getLiteralValue());
comment|//                        else {
comment|//                            String prefix = constructor.getQName().substring(p + 1);
comment|//                            context.declareInScopeNamespace(prefix, constructor.getLiteralValue());
comment|//                        }
comment|//                    }
comment|//                }
comment|//                // process the remaining attributes
comment|//                for (int i = 0; i< attributes.length; i++) {
comment|//                    context.proceed(this, builder);
comment|//                    constructor = attributes[i];
comment|//                    attrValues = constructor.eval(contextSequence, contextItem);
comment|//                    attrQName = QName.parse(context, constructor.getQName(), "");
comment|//                    if (attrs.getIndex(attrQName.getNamespaceURI(), attrQName.getLocalName()) != -1)
comment|//                        throw new XPathException(this, "XQST0040 '" + attrQName.getLocalName() + "' is a duplicate attribute name");
comment|//                    attrs.addAttribute(attrQName.getNamespaceURI(), attrQName.getLocalName(),
comment|//                            attrQName.getStringValue(), "CDATA", attrValues.getStringValue());
comment|//                }
comment|//            }
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|,
name|builder
argument_list|)
expr_stmt|;
comment|// evaluate element tag name
name|QName
name|qn
init|=
literal|null
decl_stmt|;
name|String
name|tagName
init|=
name|name
decl_stmt|;
if|if
condition|(
name|qnameExpr
operator|!=
literal|null
condition|)
block|{
name|Sequence
name|qnameSeq
init|=
name|qnameExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|qnameSeq
operator|.
name|hasOne
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Type error: the node name should evaluate to a single item"
argument_list|)
throw|;
name|Item
name|qnitem
init|=
name|qnameSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|qnitem
operator|instanceof
name|QNameValue
condition|)
block|{
name|qn
operator|=
operator|(
operator|(
name|QNameValue
operator|)
name|qnitem
operator|)
operator|.
name|getQName
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|tagName
operator|=
name|qnitem
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|qn
operator|==
literal|null
condition|)
block|{
comment|//Not in the specs but... makes sense
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidName
argument_list|(
name|tagName
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"'"
operator|+
name|tagName
operator|+
literal|"' is not a valid element name"
argument_list|)
throw|;
try|try
block|{
name|qn
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|tagName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"'"
operator|+
name|tagName
operator|+
literal|"' is not a valid element name"
argument_list|)
throw|;
block|}
block|}
comment|//
comment|//                //Use the default namespace if specified
comment|//                /*
comment|//                 if (qn.getPrefix() == null&& context.inScopeNamespaces.get("xmlns") != null) {
comment|//                     qn.setNamespaceURI((String)context.inScopeNamespaces.get("xmlns"));
comment|//                 }
comment|//                 */
comment|//                if (qn.getPrefix() == null&& context.getInScopeNamespace("") != null) {
comment|//                     qn.setNamespaceURI(context.getInScopeNamespace(""));
comment|//                }
comment|//             }
comment|//
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
name|qn
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// process attributes
if|if
condition|(
name|use_attribute_sets
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|XSLContext
operator|)
name|context
operator|)
operator|.
name|getXSLStylesheet
argument_list|()
operator|.
name|attributeSet
argument_list|(
name|use_attribute_sets
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Attribute
name|attr
range|:
name|attributes
control|)
block|{
name|attr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
comment|// add namespace declaration nodes
comment|//            if(namespaceDecls != null) {
comment|//                for(int i = 0; i< namespaceDecls.length; i++) {
comment|//                    builder.namespaceNode(namespaceDecls[i]);
comment|//                }
comment|//            }
comment|//            // do we need to add a namespace declaration for the current node?
comment|//            if (qn.needsNamespaceDecl()) {
comment|//                if (context.getInScopePrefix(qn.getNamespaceURI()) == null) {
comment|//                    String prefix = qn.getPrefix();
comment|//                    if (prefix == null || prefix.length() == 0)
comment|//                        prefix = "";
comment|//                    context.declareInScopeNamespace(prefix, qn.getNamespaceURI());
comment|//                    builder.namespaceNode(new QName(prefix, qn.getNamespaceURI(), "xmlns"));
comment|//                }
comment|//            } else if ((qn.getPrefix() == null || qn.getPrefix().length() == 0)&&
comment|//                context.getInheritedNamespace("") != null) {
comment|//                context.declareInScopeNamespace("", "");
comment|//                builder.namespaceNode(new QName("", "", "xmlns"));
comment|//            }
comment|// process element contents
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|content
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
block|}
else|else
name|super
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|NodeImpl
name|node
init|=
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
decl_stmt|;
return|return
name|node
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popInScopeNamespaces
argument_list|()
expr_stmt|;
if|if
condition|(
name|newDocumentContext
condition|)
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|expressionEnd
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
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
literal|"<xsl:element"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" name = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|namespace
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" namespace = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|namespace
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|inherit_namespaces
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" inherit_namespaces = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|inherit_namespaces
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|use_attribute_sets
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" use_attribute_sets = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|use_attribute_sets
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" type = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|validation
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" validation = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|validation
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|">"
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
literal|"</xsl:element>"
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
literal|"<xsl:element"
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
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespace
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" namespace = "
operator|+
name|namespace
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|inherit_namespaces
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" inherit_namespaces = "
operator|+
name|inherit_namespaces
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|use_attribute_sets
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" use_attribute_sets = "
operator|+
name|use_attribute_sets
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" type = "
operator|+
name|type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|validation
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" validation = "
operator|+
name|validation
operator|.
name|toString
argument_list|()
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
literal|"</xsl:element> "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

