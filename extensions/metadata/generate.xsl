<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:template match="build">
        <project basedir="." default="jar" name="metadata">
            
            <xsl:comment>
This is a generated build file. Do not edit. 
Change the stylesheet generate.xsl instead.</xsl:comment>
            
		    <!--
		        Get values from properties files. Note that the values in "local.build.properties" 
		        are leading to "build.properties".
    		-->
    		<property file="../local.build.properties"/>
    		<property file="../build.properties"/>

            <property file="local.properties"/>
            <property file="build.properties"/>
            
            <condition property="include.feature.metadata.config">
                <istrue value="${{include.feature.metadata}}"/>
            </condition>

    		<target
        		name="interface"
        		if="include.feature.metadata.config" >

        		<ant
            		antfile="build.xml"
		            dir="interface"
            		target="${{target}}" >

            		<property
                		name="module"
                		value="interface" />
       			</ant>
    		</target>
    		
            <xsl:apply-templates select="backends"/>
            
            <target name="compile">
                <echo message="---------------------------"/>
                <echo message="Compiling Metadata Storage"/>
                <echo message="---------------------------"/>

                <iterate target="compile"/>
            </target>
            
            <target name="compile-tests">
                <echo message="---------------------------"/>
                <echo message="Compiling Metadata Storage tests"/>
                <echo message="---------------------------"/>

                <iterate target="compile-tests"/>
            </target>
            
            <target name="jar">
                <echo message="---------------------------"/>
                <echo message="Creating Metadata Storage jars"/>
                <echo message="---------------------------"/>

                <iterate target="jar"/>
            </target>
            
            <target name="clean">
                <echo message="-------------------------------------"/>
                <echo message="Cleaning Metadata Storage ..."/>
                <echo message="-------------------------------------"/>

                <iterate target="clean"/>
                <delete file="build.xml" failonerror="false"/>
            </target>
            
            <target name="all-clean">

	            <ant antfile="build.xml" dir="interface" target="all-clean">
    	            <property name="module" value="interface"/>
        	    </ant>

                <iterate target="all-clean"/>
            </target>
            
            <target name="test">
                <echo message="------------------------------------------"/>
                <echo message="Running Metadata Storage tests"/>
                <echo message="------------------------------------------"/>

                <iterate target="test"/>
            </target>
            
        </project>
    </xsl:template>
    
    <xsl:template match="backends">
        <xsl:for-each select="backend">
            <condition property="include.feature.metadata.{@name}.config">
                <istrue value="${{include.feature.metadata.{@name}}}"/>
            </condition>
        </xsl:for-each>
        <xsl:apply-templates select="backend"/>
        <macrodef name="iterate">
            <attribute name="target"/>
            <sequential>
				<antcall target="interface">
					<param name="target" value="@{{target}}"/>
				</antcall>
                <xsl:for-each select="backend">
                    <antcall target="{@name}">
                        <param name="target" value="@{{target}}"/>
                    </antcall>
                </xsl:for-each>
            </sequential>
        </macrodef>
    </xsl:template>
    
    <xsl:template match="backend">
        <target name="{@name}" if="include.feature.metadata.{@name}.config">
            <ant antfile="{@antfile}" dir="{@dir}" target="${{target}}">
                <property name="module" value="{@name}"/>
            </ant>
        </target>
    </xsl:template>
    
</xsl:stylesheet>
