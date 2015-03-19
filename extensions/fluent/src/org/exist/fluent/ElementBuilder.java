begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
comment|/**  * A builder of DOM trees, meant to be either stand alone or be inserted into  * pre-existing ones.  Cannot remove nodes from the base tree.  You must {@link #commit()}  * the builder to persist the recorded changes in the database.  *   * All the nodes in the tree being built must be from the same implementation.  If you attempt  * to add foreign nodes -- for example, persistent nodes from the database -- using the  * {@link #node} or {@link #nodes} methods to a builder that already has temporary nodes  * built using its other methods, they'll be imported.  Importing nodes will usually make a deep  * copy of the tree in memory, which could cause problems if you're trying to add a big stored  * node tree.  *   * @param<K> the type of object returned upon completion of the builder,  * 	depends on the context in which the builder is used  *   * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  * @version $Revision: 1.18 $ ($Date: 2006/04/13 19:12:16 $)  */
end_comment

begin_class
specifier|public
class|class
name|ElementBuilder
parameter_list|<
name|K
parameter_list|>
block|{
interface|interface
name|CompletedCallback
parameter_list|<
name|T
parameter_list|>
block|{
specifier|public
name|T
name|completed
parameter_list|(
name|Node
index|[]
name|nodes
parameter_list|)
function_decl|;
block|}
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ElementBuilder
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
comment|/* final */
name|CompletedCallback
argument_list|<
name|K
argument_list|>
name|callback
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|allowFragment
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Element
argument_list|>
name|stack
init|=
operator|new
name|LinkedList
argument_list|<
name|Element
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Node
argument_list|>
name|top
init|=
operator|new
name|LinkedList
argument_list|<
name|Node
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|done
decl_stmt|;
specifier|private
specifier|final
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|doc
decl_stmt|;
specifier|private
name|NamespaceMap
name|namespaceBindings
decl_stmt|;
specifier|static
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
name|createDocumentNode
parameter_list|()
block|{
try|try
block|{
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dbf
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|newDocument
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unable to create new memory DOM"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|ScratchCallback
implements|implements
name|CompletedCallback
argument_list|<
name|Node
argument_list|>
block|{
specifier|public
name|Node
name|completed
parameter_list|(
name|Node
index|[]
name|nodes
parameter_list|)
block|{
if|if
condition|(
name|nodes
operator|.
name|length
operator|==
literal|1
condition|)
return|return
name|nodes
index|[
literal|0
index|]
return|;
name|DocumentFragment
name|frag
init|=
name|doc
operator|.
name|createDocumentFragment
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|frag
operator|.
name|appendChild
argument_list|(
name|adopt
argument_list|(
name|nodes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|frag
return|;
block|}
block|}
comment|/** 	 * Create a new element builder that creates an in-memory DOM tree and 	 * returns the top node (possibly a fragment node) upon commit. 	 * 	 * @param namespaceBindings the namespace bindings to use, or<code>null</code> for none 	 * @return a scratch in-memory builder 	 */
specifier|public
specifier|static
name|ElementBuilder
argument_list|<
name|Node
argument_list|>
name|createScratch
parameter_list|(
name|NamespaceMap
name|namespaceBindings
parameter_list|)
block|{
name|ElementBuilder
argument_list|<
name|Node
argument_list|>
name|builder
init|=
operator|new
name|ElementBuilder
argument_list|<
name|Node
argument_list|>
argument_list|(
name|namespaceBindings
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setCallback
argument_list|(
name|builder
operator|.
expr|new
name|ScratchCallback
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|builder
return|;
block|}
name|ElementBuilder
parameter_list|(
name|NamespaceMap
name|namespaceBindings
parameter_list|,
name|boolean
name|allowFragment
parameter_list|,
name|CompletedCallback
argument_list|<
name|K
argument_list|>
name|callback
parameter_list|)
block|{
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
name|this
operator|.
name|allowFragment
operator|=
name|allowFragment
expr_stmt|;
name|this
operator|.
name|namespaceBindings
operator|=
name|namespaceBindings
operator|==
literal|null
condition|?
operator|new
name|NamespaceMap
argument_list|()
else|:
name|namespaceBindings
operator|.
name|extend
argument_list|()
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|createDocumentNode
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|setCallback
parameter_list|(
name|CompletedCallback
argument_list|<
name|K
argument_list|>
name|callback
parameter_list|)
block|{
name|this
operator|.
name|callback
operator|=
name|callback
expr_stmt|;
block|}
specifier|private
name|Node
name|current
parameter_list|()
block|{
if|if
condition|(
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no current node"
argument_list|)
throw|;
return|return
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkDone
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"builder already done"
argument_list|)
throw|;
block|}
comment|/** 	 * Insert a namespace binding scoped to this builder only. 	 * 	 * @param key the prefix to bind 	 * @param uri the namespace uri 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|namespace
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
name|namespaceBindings
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|uri
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** 	 * Insert a copy of the given node. 	 *  	 * @param node the node to insert 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|node
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|fluent
operator|.
name|Node
name|node
parameter_list|)
block|{
return|return
name|node
argument_list|(
name|node
operator|.
name|getDOMNode
argument_list|()
argument_list|)
return|;
block|}
comment|/** 	 * Insert copies of the given nodes. 	 *  	 * @param nodes the nodes to insert 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|nodes
parameter_list|(
name|ItemList
operator|.
name|NodesFacet
name|nodes
parameter_list|)
block|{
for|for
control|(
name|org
operator|.
name|exist
operator|.
name|fluent
operator|.
name|Node
name|node
range|:
name|nodes
control|)
name|node
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** 	 * Insert a copy of the given node.  The node can be an element node, a text node, 	 * a document node or a fragment node.  In the case of the latter two, their children 	 * are inserted instead. 	 *  	 * @param node the node to insert 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|node
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|checkDone
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
condition|)
operator|(
operator|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
operator|)
name|node
operator|)
operator|.
name|expand
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
case|case
name|Node
operator|.
name|DOCUMENT_FRAGMENT_NODE
case|:
block|{
name|NodeList
name|children
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
name|node
argument_list|(
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|appendElem
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
name|appendText
argument_list|(
name|node
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"can't append node type "
operator|+
name|node
operator|.
name|getNodeType
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|this
return|;
block|}
comment|/** 	 * Open a new element with the given tag.  The tag should be in the same format as in 	 * XML files (i.e. "prefix:localName") and is parsed into a QName according to the namespace 	 * bindings in effect for this builder.  The element must be closed with {@link #end(String)} 	 * before the builder is committed.  	 * 	 * @param tag the tag of the element to insert 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|elem
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
name|checkDone
argument_list|()
expr_stmt|;
name|Element
name|elem
init|=
name|QName
operator|.
name|parse
argument_list|(
name|tag
argument_list|,
name|namespaceBindings
argument_list|)
operator|.
name|createElement
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|appendElem
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|stack
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|elem
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|Node
name|adopt
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getOwnerDocument
argument_list|()
operator|==
name|doc
condition|)
return|return
name|node
return|;
if|if
condition|(
name|node
operator|.
name|getParentNode
argument_list|()
operator|==
literal|null
condition|)
try|try
block|{
name|Node
name|result
init|=
name|doc
operator|.
name|adoptNode
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|DOMException
name|e
parameter_list|)
block|{
block|}
return|return
name|doc
operator|.
name|importNode
argument_list|(
name|node
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|void
name|appendElem
parameter_list|(
name|Element
name|elem
parameter_list|)
block|{
if|if
condition|(
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|allowFragment
operator|&&
operator|!
name|top
operator|.
name|isEmpty
argument_list|()
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unable to build document fragment with multiple root nodes in current context"
argument_list|)
throw|;
name|top
operator|.
name|add
argument_list|(
name|elem
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|current
argument_list|()
operator|.
name|appendChild
argument_list|(
name|adopt
argument_list|(
name|elem
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Close the currently open element, matching it to the given tag. 	 * 	 * @param tag the tag of the element to be ended 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|end
parameter_list|(
name|String
name|tag
parameter_list|)
block|{
name|checkDone
argument_list|()
expr_stmt|;
try|try
block|{
name|Element
name|elem
init|=
name|stack
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|QName
name|elemName
init|=
name|QName
operator|.
name|of
argument_list|(
name|elem
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|elemName
operator|.
name|equals
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|tag
argument_list|,
name|namespaceBindings
argument_list|)
argument_list|)
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"element on top of stack is '"
operator|+
name|elemName
operator|+
literal|"' not '"
operator|+
name|tag
operator|+
literal|"'"
argument_list|)
throw|;
return|return
name|this
return|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no open elements to match end("
operator|+
name|tag
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Close the currently open element, matching it to one of the given tags.  Use 	 * this method when the element could be one of many, but it's not convenient to remember 	 * precisely which element name was used.  This is still safer than just popping the top element 	 * off arbitrarily. 	 * 	 * @param tags the possible tags of the element to be ended 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|end
parameter_list|(
name|String
modifier|...
name|tags
parameter_list|)
block|{
name|checkDone
argument_list|()
expr_stmt|;
try|try
block|{
name|Element
name|elem
init|=
name|stack
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|QName
name|elemName
init|=
name|QName
operator|.
name|of
argument_list|(
name|elem
argument_list|)
decl_stmt|;
name|boolean
name|matched
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|tag
range|:
name|tags
control|)
block|{
if|if
condition|(
name|elemName
operator|.
name|equals
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|tag
argument_list|,
name|namespaceBindings
argument_list|)
argument_list|)
condition|)
block|{
name|matched
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|matched
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"element on top of stack is '"
operator|+
name|elemName
operator|+
literal|"' not one of "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|tags
argument_list|)
argument_list|)
throw|;
return|return
name|this
return|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"no open elements to match end("
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|tags
argument_list|)
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Add an attribute to the currently open element.  If an attribute with the same name was 	 * previously added to the element, overwrite its value. 	 * 	 * @param name the name of the attribute to add 	 * @param value the value of the attribute, will be converted to a string using {@link DataUtils#toXMLString(Object)} 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|attr
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|checkDone
argument_list|()
expr_stmt|;
if|if
condition|(
name|current
argument_list|()
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"current node is not an element"
argument_list|)
throw|;
name|QName
operator|.
name|parse
argument_list|(
name|name
argument_list|,
name|namespaceBindings
argument_list|,
literal|null
argument_list|)
operator|.
name|setAttribute
argument_list|(
operator|(
name|Element
operator|)
name|current
argument_list|()
argument_list|,
name|DataUtils
operator|.
name|toXMLString
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** 	 * Add an attribute to the currently open element if the given condition holds.  If<code>condition</code> 	 * is true, this behaves as {@link #attr(String, Object)}, otherwise it does nothing. 	 * 	 * @param condition the condition to satisfy before adding the attribute 	 * @param name the name of the attribute to add 	 * @param value the value of the attribute 	 * @return the element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|attrIf
parameter_list|(
name|boolean
name|condition
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|condition
condition|)
name|attr
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** 	 * Insert text into the currenly open element. 	 * 	 * @param text the text to insert 	 * @return this element builder, for chaining calls 	 */
specifier|public
name|ElementBuilder
argument_list|<
name|K
argument_list|>
name|text
parameter_list|(
name|Object
name|text
parameter_list|)
block|{
name|checkDone
argument_list|()
expr_stmt|;
name|appendText
argument_list|(
name|DataUtils
operator|.
name|toXMLString
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|void
name|appendText
parameter_list|(
name|String
name|text
parameter_list|)
block|{
name|Node
name|textNode
init|=
name|doc
operator|.
name|createTextNode
argument_list|(
name|text
argument_list|)
decl_stmt|;
if|if
condition|(
name|stack
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|allowFragment
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"unable to add a root text node in current context"
argument_list|)
throw|;
name|top
operator|.
name|add
argument_list|(
name|textNode
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|current
argument_list|()
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"current node is not an element"
argument_list|)
throw|;
name|current
argument_list|()
operator|.
name|appendChild
argument_list|(
name|textNode
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Commit this element builder, persisting the recorded elements into the database. 	 * 	 * @return the newly created resource, as appropriate 	 */
specifier|public
name|K
name|commit
parameter_list|()
block|{
name|checkDone
argument_list|()
expr_stmt|;
name|done
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"can't commit with "
operator|+
name|stack
operator|.
name|size
argument_list|()
operator|+
literal|" elements left open"
argument_list|)
throw|;
if|if
condition|(
name|top
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
name|callback
operator|.
name|completed
argument_list|(
name|top
operator|.
name|toArray
argument_list|(
operator|new
name|Node
index|[
name|top
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|finalize
parameter_list|()
block|{
if|if
condition|(
operator|!
name|done
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"disposed without commit"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

