begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/****************************************************************************/
end_comment

begin_comment
comment|/*  File:       ExistPkgExtension.java                                      */
end_comment

begin_comment
comment|/*  Author:     F. Georges - H2O Consulting                                 */
end_comment

begin_comment
comment|/*  Date:       2010-09-21                                                  */
end_comment

begin_comment
comment|/*  Tags:                                                                   */
end_comment

begin_comment
comment|/*      Copyright (c) 2010 Florent Georges (see end of file.)               */
end_comment

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|repo
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamConstants
import|;
end_import

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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|DescriptorExtension
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|FileSystemStorage
operator|.
name|FileSystemResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Package
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|PackageException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Repository
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|Storage
operator|.
name|NotExistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|pkg
operator|.
name|repo
operator|.
name|parser
operator|.
name|XMLStreamHelper
import|;
end_import

begin_comment
comment|/**  * Handle the exist.xml descriptor in an EXPath package.  *  * @author Florent Georges  * @since 2010-09-21  */
end_comment

begin_class
specifier|public
class|class
name|ExistPkgExtension
extends|extends
name|DescriptorExtension
block|{
specifier|public
name|ExistPkgExtension
parameter_list|()
block|{
name|super
argument_list|(
literal|"exist"
argument_list|,
literal|"exist.xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|parseDescriptor
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|Package
name|pkg
parameter_list|)
throws|throws
name|PackageException
block|{
name|myXSHelper
operator|.
name|ensureNextElement
argument_list|(
name|parser
argument_list|,
literal|"package"
argument_list|)
expr_stmt|;
specifier|final
name|ExistPkgInfo
name|info
init|=
operator|new
name|ExistPkgInfo
argument_list|(
name|pkg
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|parser
operator|.
name|getEventType
argument_list|()
operator|==
name|XMLStreamConstants
operator|.
name|START_ELEMENT
condition|)
block|{
if|if
condition|(
name|EXIST_PKG_NS
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
name|handleElement
argument_list|(
name|parser
argument_list|,
name|pkg
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// ignore elements not in the eXist Pkg namespace
comment|// TODO: FIXME: Actually ignore (pass it.)
throw|throw
operator|new
name|PackageException
argument_list|(
literal|"TODO: Ignore elements in other namespace"
argument_list|)
throw|;
block|}
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
comment|// position to</package>
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLStreamException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|PackageException
argument_list|(
literal|"Error reading the exist descriptor"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
name|pkg
operator|.
name|addInfo
argument_list|(
name|getName
argument_list|()
argument_list|,
name|info
argument_list|)
expr_stmt|;
comment|// if the package has never been installed, install it now
comment|// TODO: This is not an ideal solution, but this should work in most of
comment|// the cases, and does not need xrepo to depend on any processor-specific
comment|// stuff.  We need to find a proper way to make that at the real install
comment|// phase though (during the "xrepo install").
if|if
condition|(
operator|!
name|info
operator|.
name|getJars
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
try|try
block|{
name|pkg
operator|.
name|getResolver
argument_list|()
operator|.
name|resolveResource
argument_list|(
literal|".exist/classpath.txt"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NotExistException
name|ex
parameter_list|)
block|{
name|setupPackage
argument_list|(
name|pkg
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|handleElement
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|Package
name|pkg
parameter_list|,
name|ExistPkgInfo
name|info
parameter_list|)
throws|throws
name|PackageException
throws|,
name|XMLStreamException
block|{
specifier|final
name|String
name|local
init|=
name|parser
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"jar"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
specifier|final
name|String
name|jar
init|=
name|myXSHelper
operator|.
name|getElementValue
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|info
operator|.
name|addJar
argument_list|(
name|jar
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"java"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|handleJava
argument_list|(
name|parser
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"xquery"
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
name|handleXQuery
argument_list|(
name|parser
argument_list|,
name|pkg
argument_list|,
name|info
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|PackageException
argument_list|(
literal|"Unknown eXist component type: "
operator|+
name|local
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|handleJava
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|ExistPkgInfo
name|info
parameter_list|)
throws|throws
name|PackageException
throws|,
name|XMLStreamException
block|{
name|myXSHelper
operator|.
name|ensureNextElement
argument_list|(
name|parser
argument_list|,
literal|"namespace"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|href
init|=
name|myXSHelper
operator|.
name|getElementValue
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|myXSHelper
operator|.
name|ensureNextElement
argument_list|(
name|parser
argument_list|,
literal|"class"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|clazz
init|=
name|myXSHelper
operator|.
name|getElementValue
argument_list|(
name|parser
argument_list|)
decl_stmt|;
comment|// position to</java>
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
try|try
block|{
name|info
operator|.
name|addJava
argument_list|(
operator|new
name|URI
argument_list|(
name|href
argument_list|)
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|PackageException
argument_list|(
literal|"Invalid URI: "
operator|+
name|href
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|handleXQuery
parameter_list|(
name|XMLStreamReader
name|parser
parameter_list|,
name|Package
name|pkg
parameter_list|,
name|ExistPkgInfo
name|info
parameter_list|)
throws|throws
name|PackageException
throws|,
name|XMLStreamException
block|{
if|if
condition|(
operator|!
name|myXSHelper
operator|.
name|isNextElement
argument_list|(
name|parser
argument_list|,
literal|"import-uri"
argument_list|)
condition|)
block|{
name|myXSHelper
operator|.
name|ensureElement
argument_list|(
name|parser
argument_list|,
literal|"namespace"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|href
init|=
name|myXSHelper
operator|.
name|getElementValue
argument_list|(
name|parser
argument_list|)
decl_stmt|;
name|myXSHelper
operator|.
name|ensureNextElement
argument_list|(
name|parser
argument_list|,
literal|"file"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|file
init|=
name|myXSHelper
operator|.
name|getElementValue
argument_list|(
name|parser
argument_list|)
decl_stmt|;
comment|// position to</xquery>
name|parser
operator|.
name|next
argument_list|()
expr_stmt|;
try|try
block|{
name|info
operator|.
name|addXQuery
argument_list|(
operator|new
name|URI
argument_list|(
name|href
argument_list|)
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|PackageException
argument_list|(
literal|"Invalid URI: "
operator|+
name|href
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|// TODO: Must not be here (in the parsing class).  See the comment at the
comment|// end of parseDescriptor().
specifier|private
name|void
name|setupPackage
parameter_list|(
name|Package
name|pkg
parameter_list|,
name|ExistPkgInfo
name|info
parameter_list|)
throws|throws
name|PackageException
block|{
comment|// TODO: FIXME: Bad, BAD design!  But will be resolved naturally by moving the
comment|// install code within the storage class (because we are writing on disk)...
specifier|final
name|FileSystemResolver
name|res
init|=
operator|(
name|FileSystemResolver
operator|)
name|pkg
operator|.
name|getResolver
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|classpath
init|=
name|res
operator|.
name|resolveResourceAsFile
argument_list|(
literal|".exist/classpath.txt"
argument_list|)
decl_stmt|;
comment|// create [pkg_dir]/.exist/classpath.txt if not already
specifier|final
name|Path
name|exist
init|=
name|classpath
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|exist
argument_list|)
condition|)
block|{
try|try
block|{
name|Files
operator|.
name|createDirectories
argument_list|(
name|exist
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|PackageException
argument_list|(
literal|"Impossible to create directory: "
operator|+
name|exist
argument_list|)
throw|;
block|}
block|}
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|jars
init|=
name|info
operator|.
name|getJars
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|Writer
name|out
init|=
name|Files
operator|.
name|newBufferedWriter
argument_list|(
name|classpath
argument_list|)
init|)
block|{
for|for
control|(
specifier|final
name|String
name|jar
range|:
name|jars
control|)
block|{
name|StreamSource
name|jar_src
decl_stmt|;
try|try
block|{
name|jar_src
operator|=
name|res
operator|.
name|resolveComponent
argument_list|(
name|jar
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NotExistException
name|ex
parameter_list|)
block|{
specifier|final
name|String
name|msg
init|=
literal|"Inconsistent package descriptor, the JAR file is not in the package: "
decl_stmt|;
throw|throw
operator|new
name|PackageException
argument_list|(
name|msg
operator|+
name|jar
argument_list|,
name|ex
argument_list|)
throw|;
block|}
specifier|final
name|URI
name|uri
init|=
name|URI
operator|.
name|create
argument_list|(
name|jar_src
operator|.
name|getSystemId
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|file
init|=
name|Paths
operator|.
name|get
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|file
operator|.
name|normalize
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|PackageException
argument_list|(
literal|"Error writing the eXist classpath file: "
operator|+
name|classpath
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
specifier|final
name|String
name|EXIST_PKG_NS
init|=
literal|"http://exist-db.org/ns/expath-pkg"
decl_stmt|;
specifier|private
name|XMLStreamHelper
name|myXSHelper
init|=
operator|new
name|XMLStreamHelper
argument_list|(
name|EXIST_PKG_NS
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|install
parameter_list|(
name|Repository
name|repository
parameter_list|,
name|Package
name|pkg
parameter_list|)
throws|throws
name|PackageException
block|{
name|init
argument_list|(
name|repository
argument_list|,
name|pkg
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

begin_comment
comment|/*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.               */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The contents of this file are subject to the Mozilla Public License     */
end_comment

begin_comment
comment|/*  Version 1.0 (the "License"); you may not use this file except in        */
end_comment

begin_comment
comment|/*  compliance with the License. You may obtain a copy of the License at    */
end_comment

begin_comment
comment|/*  http://www.mozilla.org/MPL/.                                            */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  Software distributed under the License is distributed on an "AS IS"     */
end_comment

begin_comment
comment|/*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See    */
end_comment

begin_comment
comment|/*  the License for the specific language governing rights and limitations  */
end_comment

begin_comment
comment|/*  under the License.                                                      */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The Original Code is: all this file.                                    */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  The Initial Developer of the Original Code is Florent Georges.          */
end_comment

begin_comment
comment|/*                                                                          */
end_comment

begin_comment
comment|/*  Contributor(s): none.                                                   */
end_comment

begin_comment
comment|/* ------------------------------------------------------------------------ */
end_comment

end_unit

