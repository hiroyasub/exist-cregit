<p:pipeline xmlns:c="http://www.w3.org/ns/xproc-step" xmlns:p="http://www.w3.org/ns/xproc" name="pipeline">
<p:input port="source" primary="true">
<p:inline>
<html xmlns="f">
<body>
<h1>It Worked</h1>
<p>I was passed through an XSLT stylesheet.</p>
</body>
</html>
</p:inline>
</p:input>
<p:xslt>
<p:input port="stylesheet">
<p:inline>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
<xsl:template match="node()|@*">
<xsl:copy>
<xsl:attribute name="test">test</xsl:attribute>
<xsl:apply-templates/>
</xsl:copy>
</xsl:template>
</xsl:stylesheet>
</p:inline>
</p:input>
</p:xslt>
</p:pipeline>   