begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * DOM.java  *  * Created on June 20, 2006, 12:31 PM  *  * (C) R. Alexander Milowski alex@milowski.com  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|util
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
name|persistent
operator|.
name|ElementImpl
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
name|NodeListImpl
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
name|txn
operator|.
name|Txn
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
name|Element
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
comment|/**  *  * @author R. Alexander Milowski  */
end_comment

begin_class
specifier|public
class|class
name|DOMDB
block|{
comment|/** Creates a new instance of DOM */
specifier|private
name|DOMDB
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|Element
name|replaceTextElement
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|ElementImpl
name|parent
parameter_list|,
name|String
name|namespaceName
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|firstChild
parameter_list|)
block|{
name|ElementImpl
name|textE
init|=
operator|(
name|ElementImpl
operator|)
name|DOM
operator|.
name|findChild
argument_list|(
name|parent
argument_list|,
name|namespaceName
argument_list|,
name|localName
argument_list|)
decl_stmt|;
if|if
condition|(
name|textE
operator|==
literal|null
condition|)
block|{
name|textE
operator|=
operator|(
name|ElementImpl
operator|)
name|parent
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createElementNS
argument_list|(
name|namespaceName
argument_list|,
name|localName
argument_list|)
expr_stmt|;
specifier|final
name|NodeListImpl
name|nl
init|=
operator|new
name|NodeListImpl
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|textE
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstChild
condition|)
block|{
name|parent
operator|.
name|insertAfter
argument_list|(
name|txn
argument_list|,
name|nl
argument_list|,
name|parent
operator|.
name|getFirstChild
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parent
operator|.
name|appendChildren
argument_list|(
name|txn
argument_list|,
name|nl
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
name|DOMDB
operator|.
name|removeChildren
argument_list|(
name|txn
argument_list|,
name|textE
argument_list|)
expr_stmt|;
name|textE
operator|.
name|appendChild
argument_list|(
name|parent
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createTextNode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|textE
return|;
block|}
specifier|public
specifier|static
name|void
name|appendChild
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|ElementImpl
name|parent
parameter_list|,
name|Node
name|child
parameter_list|)
block|{
specifier|final
name|NodeListImpl
name|nl
init|=
operator|new
name|NodeListImpl
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|parent
operator|.
name|appendChildren
argument_list|(
name|txn
argument_list|,
name|nl
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Node
name|insertBefore
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|ElementImpl
name|parent
parameter_list|,
name|Node
name|child
parameter_list|,
name|Node
name|refChild
parameter_list|)
block|{
specifier|final
name|NodeListImpl
name|nl
init|=
operator|new
name|NodeListImpl
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|nl
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
name|parent
operator|.
name|insertBefore
argument_list|(
name|txn
argument_list|,
name|nl
argument_list|,
name|refChild
argument_list|)
expr_stmt|;
return|return
name|child
return|;
block|}
specifier|public
specifier|static
name|void
name|replaceText
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|ElementImpl
name|textE
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|DOMDB
operator|.
name|removeChildren
argument_list|(
name|txn
argument_list|,
name|textE
argument_list|)
expr_stmt|;
name|textE
operator|.
name|appendChild
argument_list|(
name|textE
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|createTextNode
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|removeChildren
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|ElementImpl
name|parent
parameter_list|)
block|{
name|Node
name|current
init|=
name|parent
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Node
name|toRemove
init|=
name|current
decl_stmt|;
name|current
operator|=
name|current
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
name|parent
operator|.
name|removeChild
argument_list|(
name|txn
argument_list|,
name|toRemove
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

