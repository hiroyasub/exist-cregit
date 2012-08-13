begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|launcher
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * Display a splash screen showing the eXist-db logo and a status line.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|SplashScreen
extends|extends
name|JFrame
block|{
specifier|private
name|JLabel
name|statusLabel
decl_stmt|;
specifier|public
name|SplashScreen
parameter_list|()
block|{
name|setUndecorated
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setBackground
argument_list|(
operator|new
name|Color
argument_list|(
literal|255
argument_list|,
literal|255
argument_list|,
literal|255
argument_list|,
literal|125
argument_list|)
argument_list|)
expr_stmt|;
name|setAlwaysOnTop
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setDefaultCloseOperation
argument_list|(
name|DO_NOTHING_ON_CLOSE
argument_list|)
expr_stmt|;
name|URL
name|imageURL
init|=
name|SplashScreen
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"logo.jpg"
argument_list|)
decl_stmt|;
name|ImageIcon
name|icon
init|=
operator|new
name|ImageIcon
argument_list|(
name|imageURL
argument_list|,
literal|"eXist-db Logo"
argument_list|)
decl_stmt|;
name|getContentPane
argument_list|()
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
comment|// add the image label
name|JLabel
name|imageLabel
init|=
operator|new
name|JLabel
argument_list|()
decl_stmt|;
name|imageLabel
operator|.
name|setIcon
argument_list|(
name|icon
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|imageLabel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
comment|// message label
name|statusLabel
operator|=
operator|new
name|JLabel
argument_list|(
literal|"Launching eXist-db ..."
argument_list|,
name|SwingConstants
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|statusLabel
operator|.
name|setFont
argument_list|(
operator|new
name|Font
argument_list|(
name|statusLabel
operator|.
name|getFont
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|Font
operator|.
name|BOLD
argument_list|,
literal|14
argument_list|)
argument_list|)
expr_stmt|;
name|statusLabel
operator|.
name|setForeground
argument_list|(
name|Color
operator|.
name|black
argument_list|)
expr_stmt|;
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|statusLabel
argument_list|,
name|BorderLayout
operator|.
name|SOUTH
argument_list|)
expr_stmt|;
comment|// show it
name|setSize
argument_list|(
operator|new
name|Dimension
argument_list|(
name|icon
operator|.
name|getIconWidth
argument_list|()
argument_list|,
name|icon
operator|.
name|getIconHeight
argument_list|()
operator|+
literal|20
argument_list|)
argument_list|)
expr_stmt|;
comment|//pack();
name|this
operator|.
name|setLocationRelativeTo
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|setVisible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setStatus
parameter_list|(
specifier|final
name|String
name|status
parameter_list|)
block|{
name|statusLabel
operator|.
name|setText
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

