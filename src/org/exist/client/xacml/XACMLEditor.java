begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|xacml
package|;
end_package

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Container
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|Dimension
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|KeyEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|WindowEvent
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|WindowListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|javax
operator|.
name|swing
operator|.
name|JFrame
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JMenu
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JMenuBar
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JMenuItem
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JOptionPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JScrollPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JSplitPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JTree
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|KeyStroke
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TreeModelEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TreeModelListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TreeSelectionEvent
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|event
operator|.
name|TreeSelectionListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|tree
operator|.
name|TreePath
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|tree
operator|.
name|TreeSelectionModel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|Policy
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|PolicySet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|Target
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|combine
operator|.
name|OrderedPermitOverridesPolicyAlg
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|combine
operator|.
name|OrderedPermitOverridesRuleAlg
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|combine
operator|.
name|PolicyCombiningAlgorithm
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|combine
operator|.
name|RuleCombiningAlgorithm
import|;
end_import

begin_import
import|import
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|ctx
operator|.
name|Result
import|;
end_import

begin_class
specifier|public
class|class
name|XACMLEditor
extends|extends
name|JFrame
implements|implements
name|ActionListener
implements|,
name|TreeModelListener
implements|,
name|TreeSelectionListener
implements|,
name|WindowListener
block|{
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_DESCRIPTION
init|=
literal|"This is a policy template.  It will match and deny everything until you change the target and add rules."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_RULE_DESCRIPTION
init|=
literal|"This rule denies everything that is not permitted by the rules above it when "
operator|+
literal|"used with the ordered permit overrides combining algorithm.  Any rules below it will not be evaluated, so it should be the last rule"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_POLICY_ID
init|=
literal|"NewPolicy"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_POLICY_SET_ID
init|=
literal|"NewPolicySet"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_RULE_ID
init|=
literal|"NewRule"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CLOSE
init|=
literal|"Close"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SAVE
init|=
literal|"Save"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MIN_FRAME_WIDTH
init|=
literal|600
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MIN_FRAME_HEIGHT
init|=
literal|350
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MINIMUM_TREE_WIDTH
init|=
literal|100
decl_stmt|;
specifier|private
name|DatabaseInterface
name|dbInterface
decl_stmt|;
specifier|private
name|XACMLTreeModel
name|model
decl_stmt|;
specifier|private
name|JTree
name|tree
decl_stmt|;
specifier|private
name|NodeEditor
name|editor
decl_stmt|;
specifier|private
name|JSplitPane
name|split
decl_stmt|;
specifier|private
name|XACMLEditor
parameter_list|()
block|{
block|}
specifier|public
name|XACMLEditor
parameter_list|(
name|Collection
name|systemCollection
parameter_list|)
block|{
name|super
argument_list|(
literal|"XACML Policy Editor"
argument_list|)
expr_stmt|;
name|setDefaultCloseOperation
argument_list|(
name|DO_NOTHING_ON_CLOSE
argument_list|)
expr_stmt|;
name|addWindowListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|dbInterface
operator|=
operator|new
name|DatabaseInterface
argument_list|(
name|systemCollection
argument_list|)
expr_stmt|;
name|setupMenuBar
argument_list|()
expr_stmt|;
name|createInterface
argument_list|()
expr_stmt|;
comment|//setSize(600, 480);
name|pack
argument_list|()
expr_stmt|;
name|Dimension
name|size
init|=
name|getSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|.
name|width
operator|<=
name|MIN_FRAME_WIDTH
condition|)
name|size
operator|.
name|width
operator|=
name|MIN_FRAME_WIDTH
expr_stmt|;
if|if
condition|(
name|size
operator|.
name|height
operator|<=
name|MIN_FRAME_HEIGHT
condition|)
name|size
operator|.
name|height
operator|=
name|MIN_FRAME_HEIGHT
expr_stmt|;
name|setSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createInterface
parameter_list|()
block|{
name|split
operator|=
operator|new
name|JSplitPane
argument_list|(
name|JSplitPane
operator|.
name|HORIZONTAL_SPLIT
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|split
argument_list|)
expr_stmt|;
name|model
operator|=
operator|new
name|XACMLTreeModel
argument_list|(
name|dbInterface
operator|.
name|getPolicies
argument_list|()
argument_list|)
expr_stmt|;
name|model
operator|.
name|addTreeModelListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tree
operator|=
operator|new
name|JTree
argument_list|(
name|model
argument_list|)
expr_stmt|;
name|tree
operator|.
name|getSelectionModel
argument_list|()
operator|.
name|setSelectionMode
argument_list|(
name|TreeSelectionModel
operator|.
name|SINGLE_TREE_SELECTION
argument_list|)
expr_stmt|;
name|TreeMutator
name|mutator
init|=
operator|new
name|TreeMutator
argument_list|(
name|tree
argument_list|)
decl_stmt|;
name|tree
operator|.
name|addTreeSelectionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setCellRenderer
argument_list|(
operator|new
name|CustomRenderer
argument_list|(
name|mutator
argument_list|)
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setEditable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setShowsRootHandles
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|tree
operator|.
name|setRootVisible
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|Dimension
name|minSize
init|=
name|tree
operator|.
name|getMinimumSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|minSize
operator|.
name|width
operator|<
name|MINIMUM_TREE_WIDTH
condition|)
name|minSize
operator|.
name|width
operator|=
name|MINIMUM_TREE_WIDTH
expr_stmt|;
name|tree
operator|.
name|setMinimumSize
argument_list|(
name|minSize
argument_list|)
expr_stmt|;
name|JScrollPane
name|scroll
init|=
operator|new
name|JScrollPane
argument_list|(
name|tree
argument_list|)
decl_stmt|;
name|split
operator|.
name|setLeftComponent
argument_list|(
name|scroll
argument_list|)
expr_stmt|;
name|split
operator|.
name|setRightComponent
argument_list|(
operator|new
name|JScrollPane
argument_list|()
argument_list|)
expr_stmt|;
name|split
operator|.
name|setOneTouchExpandable
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|hasUnsavedChanges
argument_list|()
condition|)
block|{
name|String
name|message
init|=
literal|"There are unsaved changes.  Do you want to save your changes before closing?"
decl_stmt|;
name|String
name|title
init|=
literal|"Save changes?"
decl_stmt|;
name|int
name|ret
init|=
name|JOptionPane
operator|.
name|showConfirmDialog
argument_list|(
name|this
argument_list|,
name|message
argument_list|,
name|title
argument_list|,
name|JOptionPane
operator|.
name|YES_NO_CANCEL_OPTION
argument_list|,
name|JOptionPane
operator|.
name|QUESTION_MESSAGE
argument_list|)
decl_stmt|;
if|if
condition|(
name|ret
operator|==
name|JOptionPane
operator|.
name|CANCEL_OPTION
condition|)
return|return;
if|else if
condition|(
name|ret
operator|==
name|JOptionPane
operator|.
name|YES_OPTION
condition|)
name|saveAll
argument_list|()
expr_stmt|;
block|}
name|dispose
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasUnsavedChanges
parameter_list|()
block|{
return|return
name|model
operator|.
name|hasUnsavedChanges
argument_list|()
return|;
block|}
specifier|public
name|void
name|saveAll
parameter_list|()
block|{
if|if
condition|(
name|editor
operator|!=
literal|null
condition|)
name|editor
operator|.
name|pushChanges
argument_list|()
expr_stmt|;
name|dbInterface
operator|.
name|writePolicies
argument_list|(
operator|(
name|RootNode
operator|)
name|model
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
name|tree
operator|.
name|repaint
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|PolicySet
name|createDefaultPolicySet
parameter_list|(
name|PolicyElementContainer
name|parent
parameter_list|)
block|{
return|return
name|createDefaultPolicySet
argument_list|(
name|createUniqueId
argument_list|(
name|parent
argument_list|,
name|DEFAULT_POLICY_SET_ID
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|PolicySet
name|createDefaultPolicySet
parameter_list|(
name|String
name|policySetID
parameter_list|)
block|{
name|PolicyCombiningAlgorithm
name|alg
init|=
operator|new
name|OrderedPermitOverridesPolicyAlg
argument_list|()
decl_stmt|;
return|return
operator|new
name|PolicySet
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|policySetID
argument_list|)
argument_list|,
name|alg
argument_list|,
name|createEmptyTarget
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Target
name|createEmptyTarget
parameter_list|()
block|{
return|return
operator|new
name|Target
argument_list|(
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|,
name|Collections
operator|.
name|EMPTY_LIST
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Policy
name|createDefaultPolicy
parameter_list|(
name|PolicyElementContainer
name|parent
parameter_list|)
block|{
return|return
name|createDefaultPolicy
argument_list|(
name|createUniqueId
argument_list|(
name|parent
argument_list|,
name|DEFAULT_POLICY_ID
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Policy
name|createDefaultPolicy
parameter_list|(
name|String
name|policyID
parameter_list|)
block|{
name|Target
name|emptyTarget
init|=
name|createEmptyTarget
argument_list|()
decl_stmt|;
name|RuleCombiningAlgorithm
name|alg
init|=
operator|new
name|OrderedPermitOverridesRuleAlg
argument_list|()
decl_stmt|;
name|Rule
name|denyEverythingRule
init|=
name|createDefaultRule
argument_list|(
literal|"DenyAll"
argument_list|)
decl_stmt|;
name|List
name|rules
init|=
name|Collections
operator|.
name|singletonList
argument_list|(
name|denyEverythingRule
argument_list|)
decl_stmt|;
return|return
operator|new
name|Policy
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|policyID
argument_list|)
argument_list|,
name|alg
argument_list|,
name|DEFAULT_DESCRIPTION
argument_list|,
name|emptyTarget
argument_list|,
name|rules
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Rule
name|createDefaultRule
parameter_list|(
name|PolicyElementContainer
name|parent
parameter_list|)
block|{
return|return
name|createDefaultRule
argument_list|(
name|createUniqueId
argument_list|(
name|parent
argument_list|,
name|DEFAULT_RULE_ID
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|createUniqueId
parameter_list|(
name|PolicyElementContainer
name|parent
parameter_list|,
name|String
name|base
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|==
literal|null
condition|)
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"Parent cannot be null"
argument_list|)
throw|;
name|String
name|newId
init|=
name|base
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|parent
operator|.
name|containsId
argument_list|(
name|newId
argument_list|)
condition|;
operator|++
name|i
control|)
name|newId
operator|=
name|base
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|newId
return|;
block|}
specifier|public
specifier|static
name|Rule
name|createDefaultRule
parameter_list|(
name|String
name|id
parameter_list|)
block|{
return|return
operator|new
name|Rule
argument_list|(
name|URI
operator|.
name|create
argument_list|(
name|id
argument_list|)
argument_list|,
name|Result
operator|.
name|DECISION_DENY
argument_list|,
name|DEFAULT_RULE_DESCRIPTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|private
name|void
name|setupMenuBar
parameter_list|()
block|{
name|JMenuBar
name|menuBar
init|=
operator|new
name|JMenuBar
argument_list|()
decl_stmt|;
name|setJMenuBar
argument_list|(
name|menuBar
argument_list|)
expr_stmt|;
name|JMenu
name|file
init|=
operator|new
name|JMenu
argument_list|(
literal|"File"
argument_list|)
decl_stmt|;
name|file
operator|.
name|setMnemonic
argument_list|(
name|KeyEvent
operator|.
name|VK_F
argument_list|)
expr_stmt|;
name|menuBar
operator|.
name|add
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|JMenuItem
name|saveItem
init|=
operator|new
name|JMenuItem
argument_list|(
name|SAVE
argument_list|,
name|KeyEvent
operator|.
name|VK_S
argument_list|)
decl_stmt|;
name|saveItem
operator|.
name|setActionCommand
argument_list|(
name|SAVE
argument_list|)
expr_stmt|;
name|saveItem
operator|.
name|setAccelerator
argument_list|(
name|KeyStroke
operator|.
name|getKeyStroke
argument_list|(
literal|"ctrl S"
argument_list|)
argument_list|)
expr_stmt|;
name|saveItem
operator|.
name|addActionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|file
operator|.
name|add
argument_list|(
name|saveItem
argument_list|)
expr_stmt|;
name|JMenuItem
name|closeItem
init|=
operator|new
name|JMenuItem
argument_list|(
name|CLOSE
argument_list|,
name|KeyEvent
operator|.
name|VK_W
argument_list|)
decl_stmt|;
name|closeItem
operator|.
name|setActionCommand
argument_list|(
name|CLOSE
argument_list|)
expr_stmt|;
name|closeItem
operator|.
name|setAccelerator
argument_list|(
name|KeyStroke
operator|.
name|getKeyStroke
argument_list|(
literal|"ctrl W"
argument_list|)
argument_list|)
expr_stmt|;
name|closeItem
operator|.
name|addActionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|file
operator|.
name|add
argument_list|(
name|closeItem
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|event
parameter_list|)
block|{
name|String
name|actionCommand
init|=
name|event
operator|.
name|getActionCommand
argument_list|()
decl_stmt|;
if|if
condition|(
name|CLOSE
operator|.
name|equals
argument_list|(
name|actionCommand
argument_list|)
condition|)
name|close
argument_list|()
expr_stmt|;
if|else if
condition|(
name|SAVE
operator|.
name|equals
argument_list|(
name|actionCommand
argument_list|)
condition|)
name|saveAll
argument_list|()
expr_stmt|;
block|}
comment|//this method fixes dumb repaint issues
comment|//when components are updated
specifier|private
name|void
name|forceRepaint
parameter_list|()
block|{
name|Container
name|contentPane
init|=
name|getContentPane
argument_list|()
decl_stmt|;
name|contentPane
operator|.
name|invalidate
argument_list|()
expr_stmt|;
name|contentPane
operator|.
name|validate
argument_list|()
expr_stmt|;
name|contentPane
operator|.
name|repaint
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|valueChanged
parameter_list|(
name|TreeSelectionEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|editor
operator|!=
literal|null
condition|)
block|{
name|editor
operator|.
name|pushChanges
argument_list|()
expr_stmt|;
name|editor
operator|=
literal|null
expr_stmt|;
block|}
name|TreePath
name|selectedPath
init|=
name|event
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|Object
name|value
init|=
name|selectedPath
operator|.
name|getLastPathComponent
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|AbstractPolicyNode
condition|)
name|editor
operator|=
operator|new
name|AbstractPolicyEditor
argument_list|()
expr_stmt|;
if|else if
condition|(
name|value
operator|instanceof
name|RuleNode
condition|)
name|editor
operator|=
operator|new
name|RuleEditor
argument_list|()
expr_stmt|;
if|else if
condition|(
name|value
operator|instanceof
name|ConditionNode
condition|)
name|editor
operator|=
literal|null
expr_stmt|;
comment|//TODO: implement condition editing
if|else if
condition|(
name|value
operator|instanceof
name|TargetNode
condition|)
name|editor
operator|=
operator|new
name|TargetEditor
argument_list|(
name|dbInterface
argument_list|)
expr_stmt|;
name|int
name|dividerLocation
init|=
name|split
operator|.
name|getDividerLocation
argument_list|()
decl_stmt|;
name|JScrollPane
name|scroll
init|=
operator|(
operator|(
name|JScrollPane
operator|)
name|split
operator|.
name|getRightComponent
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|editor
operator|==
literal|null
condition|)
name|scroll
operator|.
name|setViewportView
argument_list|(
literal|null
argument_list|)
expr_stmt|;
else|else
block|{
name|editor
operator|.
name|setNode
argument_list|(
operator|(
name|XACMLTreeNode
operator|)
name|value
argument_list|)
expr_stmt|;
name|scroll
operator|.
name|setViewportView
argument_list|(
name|editor
operator|.
name|getComponent
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|split
operator|.
name|setDividerLocation
argument_list|(
name|dividerLocation
argument_list|)
expr_stmt|;
name|forceRepaint
argument_list|()
expr_stmt|;
block|}
comment|//WindowListener methods
specifier|public
name|void
name|windowActivated
parameter_list|(
name|WindowEvent
name|event
parameter_list|)
block|{
block|}
specifier|public
name|void
name|windowClosed
parameter_list|(
name|WindowEvent
name|event
parameter_list|)
block|{
block|}
specifier|public
name|void
name|windowClosing
parameter_list|(
name|WindowEvent
name|event
parameter_list|)
block|{
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|windowDeactivated
parameter_list|(
name|WindowEvent
name|event
parameter_list|)
block|{
block|}
specifier|public
name|void
name|windowDeiconified
parameter_list|(
name|WindowEvent
name|event
parameter_list|)
block|{
block|}
specifier|public
name|void
name|windowIconified
parameter_list|(
name|WindowEvent
name|event
parameter_list|)
block|{
block|}
specifier|public
name|void
name|windowOpened
parameter_list|(
name|WindowEvent
name|event
parameter_list|)
block|{
block|}
specifier|private
name|void
name|treeChanged
parameter_list|(
name|TreeModelEvent
name|event
parameter_list|)
block|{
name|tree
operator|.
name|revalidate
argument_list|()
expr_stmt|;
name|tree
operator|.
name|repaint
argument_list|()
expr_stmt|;
block|}
comment|//TreeModelListener methods
specifier|public
name|void
name|treeNodesChanged
parameter_list|(
name|TreeModelEvent
name|event
parameter_list|)
block|{
name|treeChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|treeNodesInserted
parameter_list|(
name|TreeModelEvent
name|event
parameter_list|)
block|{
name|treeChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|treeNodesRemoved
parameter_list|(
name|TreeModelEvent
name|event
parameter_list|)
block|{
name|treeChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|treeStructureChanged
parameter_list|(
name|TreeModelEvent
name|event
parameter_list|)
block|{
name|treeChanged
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

