begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|jfreechart
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
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
name|jfree
operator|.
name|chart
operator|.
name|plot
operator|.
name|PlotOrientation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jfree
operator|.
name|util
operator|.
name|TableOrder
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
comment|/**  * Class for storing all configuration items for charts, except chart type.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Configuration
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Default dimension of image
specifier|private
name|int
name|imageHeight
init|=
literal|300
decl_stmt|;
specifier|private
name|int
name|imageWidth
init|=
literal|400
decl_stmt|;
comment|// Chart title
specifier|private
name|String
name|title
decl_stmt|;
comment|// Labels
specifier|private
name|String
name|categoryAxisLabel
decl_stmt|;
specifier|private
name|String
name|domainAxisLabel
decl_stmt|;
specifier|private
name|String
name|rangeAxisLabel
decl_stmt|;
specifier|private
name|String
name|timeAxisLabel
decl_stmt|;
specifier|private
name|String
name|valueAxisLabel
decl_stmt|;
comment|// Orientation and Order
specifier|private
name|TableOrder
name|order
init|=
name|TableOrder
operator|.
name|BY_COLUMN
decl_stmt|;
specifier|private
name|PlotOrientation
name|orientation
init|=
name|PlotOrientation
operator|.
name|HORIZONTAL
decl_stmt|;
comment|// Misc flags
specifier|private
name|boolean
name|generateLegend
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateTooltips
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|generateUrls
init|=
literal|false
decl_stmt|;
comment|// =========================
comment|// Getters
specifier|public
name|String
name|getTimeAxisLabel
parameter_list|()
block|{
return|return
name|timeAxisLabel
return|;
block|}
specifier|public
name|String
name|getCategoryAxisLabel
parameter_list|()
block|{
return|return
name|categoryAxisLabel
return|;
block|}
specifier|public
name|boolean
name|isGenerateLegend
parameter_list|()
block|{
return|return
name|generateLegend
return|;
block|}
specifier|public
name|boolean
name|isGenerateTooltips
parameter_list|()
block|{
return|return
name|generateTooltips
return|;
block|}
specifier|public
name|boolean
name|isGenerateUrls
parameter_list|()
block|{
return|return
name|generateUrls
return|;
block|}
specifier|public
name|PlotOrientation
name|getOrientation
parameter_list|()
block|{
return|return
name|orientation
return|;
block|}
specifier|public
name|TableOrder
name|getOrder
parameter_list|()
block|{
return|return
name|order
return|;
block|}
specifier|public
name|String
name|getTitle
parameter_list|()
block|{
return|return
name|title
return|;
block|}
specifier|public
name|String
name|getValueAxisLabel
parameter_list|()
block|{
return|return
name|valueAxisLabel
return|;
block|}
specifier|public
name|int
name|getImageHeight
parameter_list|()
block|{
return|return
name|imageHeight
return|;
block|}
specifier|public
name|int
name|getImageWidth
parameter_list|()
block|{
return|return
name|imageWidth
return|;
block|}
specifier|public
name|String
name|getDomainAxisLabel
parameter_list|()
block|{
return|return
name|domainAxisLabel
return|;
block|}
specifier|public
name|String
name|getRangeAxisLabel
parameter_list|()
block|{
return|return
name|rangeAxisLabel
return|;
block|}
comment|/**      *  Read configuration from node and initialize configuration.      * @throws XPathException Thrown when an element cannot be read.      */
specifier|public
name|void
name|parse
parameter_list|(
name|Node
name|configuration
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|configuration
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|configuration
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"configuration"
argument_list|)
condition|)
block|{
comment|//Get the First Child
name|Node
name|child
init|=
name|configuration
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
comment|//Parse each of the child nodes
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|child
operator|.
name|hasChildNodes
argument_list|()
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"title"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'title' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|title
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"categoryAxisLabel"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'categoryAxisLabel' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|categoryAxisLabel
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"valueAxisLabel"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'valueAxisLabel' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|valueAxisLabel
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"timeAxisLabel"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'timeAxisLabel' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|timeAxisLabel
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"domainAxisLabel"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'domainAxisLabel' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|domainAxisLabel
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"rangeAxisLabel"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'rangeAxisLabel' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|rangeAxisLabel
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"orientation"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'orientation' cannot be parsed"
argument_list|)
throw|;
block|}
if|else if
condition|(
literal|"HORIZONTAL"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|orientation
operator|=
name|PlotOrientation
operator|.
name|HORIZONTAL
expr_stmt|;
block|}
if|else if
condition|(
literal|"VERTICAL"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|orientation
operator|=
name|PlotOrientation
operator|.
name|VERTICAL
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Wrong value for 'orientation'"
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"tableOrder"
argument_list|)
condition|)
block|{
name|String
name|value
init|=
name|getValue
argument_list|(
name|child
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'tableOrder' cannot be parsed"
argument_list|)
throw|;
block|}
if|else if
condition|(
literal|"COLUMN"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|order
operator|=
name|TableOrder
operator|.
name|BY_COLUMN
expr_stmt|;
block|}
if|else if
condition|(
literal|"ROW"
operator|.
name|equalsIgnoreCase
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|order
operator|=
name|TableOrder
operator|.
name|BY_ROW
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Wrong value for 'tableOrder'"
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"legend"
argument_list|)
condition|)
block|{
name|Boolean
name|value
init|=
name|parseBoolean
argument_list|(
name|getValue
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'legend' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|generateLegend
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"tooltips"
argument_list|)
condition|)
block|{
name|Boolean
name|value
init|=
name|parseBoolean
argument_list|(
name|getValue
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'tooltips' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|generateTooltips
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"urls"
argument_list|)
condition|)
block|{
name|Boolean
name|value
init|=
name|parseBoolean
argument_list|(
name|getValue
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'urls' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|generateUrls
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"width"
argument_list|)
condition|)
block|{
name|Integer
name|value
init|=
name|parseInteger
argument_list|(
name|getValue
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'width' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|imageWidth
operator|=
name|value
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|child
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"height"
argument_list|)
condition|)
block|{
name|Integer
name|value
init|=
name|parseInteger
argument_list|(
name|getValue
argument_list|(
name|child
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Value for 'height' cannot be parsed"
argument_list|)
throw|;
block|}
else|else
block|{
name|imageHeight
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
comment|//next node
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Parse text and return boolean. Accepted values Yes No True False,      * otherwise NULL is returned.      */
specifier|private
name|Boolean
name|parseBoolean
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
operator|||
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"false"
argument_list|)
operator|||
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      *  Parse text and return Integer. NULL is returned when value      * cannot be converted.      */
specifier|private
name|Integer
name|parseInteger
parameter_list|(
name|String
name|value
parameter_list|)
block|{
try|try
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Helper method for getting the value of the (first) node.      */
specifier|private
name|String
name|getValue
parameter_list|(
name|Node
name|child
parameter_list|)
block|{
return|return
name|child
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

