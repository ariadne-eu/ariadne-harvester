<?xml version="1.0" encoding="UTF-8"?>

<!-- This stylesheet converts an ilox into LOM, 
  taking the lom content from the metadata block of the expression description with facet 'main',
  and replacing any existing technical block or creating a new one 
  which locations are the free uris in expression manifestations items with name 'experience'.
  Free urils have no attached metadata    
-->

<xsl:stylesheet 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version="2.0" 
  xmlns:lrelom="http://ltsc.ieee.org/xsd/LOM" 
  xmlns:ilox="http://www.imsglobal.org/xsd/imslorsltitm_v1p0"
  xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
  exclude-result-prefixes="lrelom fn ilox" >
  
  <!-- *********************************************************************** -->
  
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  
  <!-- *********************************************************************** -->
  
  <xsl:template match="/"> 
    
    <!-- Preliminary checks : we abort processing if conditions are not satisfied -->
    
    <xsl:variable name="foundExprDescrFacetMain"      select="count( ilox:expression/ilox:description/ilox:facet//ilox:value[./text()='main'] )"/>
    <xsl:variable name="foundManifestationExpression" select="count( ilox:expression/ilox:manifestation/ilox:name//ilox:value[./text()='experience'] )"/>
    <xsl:variable name="foundLocationURIs"            select="count( ilox:expression/ilox:manifestation/ilox:item/ilox:location//ilox:uri)"/>
    <xsl:variable name="foundLocationMetadata"        select="count( ilox:expression/ilox:manifestation/ilox:item/ilox:location//ilox:metadata)"/>
    <xsl:variable name="foundValidExperienceURIs"     select="count( ilox:expression/ilox:manifestation[./ilox:name/ilox:value/text()='experience']/ilox:item/ilox:location[./ilox:uri and not(./ilox:metadata) ] )"/>
    
    <xsl:if test="$foundExprDescrFacetMain = '0'">      
      <xsl:message terminate="yes">skipping file : no 'main' found  as facet for expression description !</xsl:message>      
    </xsl:if>      
    <xsl:if test="$foundManifestationExpression = '0'">      
      <xsl:message terminate="yes">skipping file : no 'experience' found as manifestation name !</xsl:message>      
    </xsl:if>          
    <xsl:if test="$foundLocationURIs = '0'">      
      <xsl:message terminate="yes">skipping file : no uri found !</xsl:message>      
    </xsl:if>      
    <xsl:if test="$foundLocationURIs = $foundLocationMetadata">      
      <xsl:message terminate="yes">skipping file : no free uri found !</xsl:message>      
    </xsl:if>  
    <xsl:if test="$foundValidExperienceURIs = '0'">      
      <xsl:message terminate="yes">skipping file : no valid 'experience' uri found !</xsl:message>      
    </xsl:if>   
    
    <!-- Extraction of LOM content -->              
    <!-- we have to put the lom with full namespace declarations, 
      excluding any ilox frelated entries thanks to 
      xsl exclude-result-prefixes="lrelom fn ilox" -->
    <!-- we use nod() to use its content, allowing us to close the element when we decide -->
    
    <lom xmlns="http://ltsc.ieee.org/xsd/LOM" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lomLoose.xsd">
      
      <!-- Extraction of LOM content from description with facet 'main' -->
      <!-- any existing technical block will be removed -->
      <xsl:for-each select="ilox:expression/ilox:description">        
        <xsl:if test="ilox:facet//ilox:value[./text()='main'] ">
          <xsl:apply-templates select="ilox:metadata/lrelom:lom/node()"/>
        </xsl:if>            
      </xsl:for-each>  
      
      <!-- Creation of a technical block containing free uris -->
      <technical>
        <xsl:for-each select="ilox:expression/ilox:manifestation[./ilox:name/ilox:value/text()='experience']/ilox:item/ilox:location[./ilox:uri and not(./ilox:metadata) ]">
          <xsl:for-each select="ilox:uri">
            <location><xsl:value-of select="."/></location>      
          </xsl:for-each>    
        </xsl:for-each>
      </technical>   
      
      <xsl:text disable-output-escaping="yes">&#xD;&#xA;</xsl:text>
      
      <!-- LOM creation done -->
    </lom>
  </xsl:template>
  
  
  <!-- *********************************************************************** -->
  
  <!-- We cleanup any existing technical block in the lom -->
  
  <xsl:template match="ilox:expression/ilox:description/ilox:metadata/lrelom:lom/lrelom:technical"> 
  </xsl:template>
  
  <!-- *********************************************************************** -->
  
  <!-- We keep anything else -->
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  
  <!-- *********************************************************************** -->
  
</xsl:stylesheet>
