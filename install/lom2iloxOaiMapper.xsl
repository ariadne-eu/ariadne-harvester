<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" 
  xmlns:lrelom="http://ltsc.ieee.org/xsd/LOM" xmlns:fn="http://www.w3.org/2005/02/xpath-functions" exclude-result-prefixes="lrelom fn" >
  <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
  
  <!-- Identifier parameter -->
  <!-- <xsl:param name="cmrId" required="yes"/> -->
  
  <!-- Variable -->
  <xsl:variable name="CDATABegin" select="'&lt;![CDATA['"/>
  <xsl:variable name="CDATAEnd" select="']]&gt;'"/>
  <xsl:variable name="entityBegin" select="'&lt;entity&gt;'"/>
  <xsl:variable name="entityEnd" select="'&lt;/entity&gt;'"/>
  <xsl:variable name="sourceBegin" select="'&lt;source&gt;'"/>
  <xsl:variable name="sourceEnd" select="'&lt;/source&gt;'"/>
  <xsl:variable name="metadataSchemaBegin" select="'&lt;metadataSchema&gt;'"/>
  <xsl:variable name="metadataSchemaEnd" select="'&lt;/metadataSchema&gt;'"/>
  
  <!-- To write our own lower-case() which isn't supported by XSLT 1.0 -->
  <xsl:variable name="alphabetUpper" select="'ABCDEFGHIJKLMNOPQRSTUVWXYZ'"/>
  <xsl:variable name="alphabetLower" select="'abcdefghijklmnopqrstuvwxyz'"/>
  
<!-- Wrapper -->
  <xsl:template match="/">
    
<!-- *********************************************************************** -->
<!-- Header -->
    <expression xmlns="http://www.imsglobal.org/xsd/imslorsltitm_v1p0"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://www.imsglobal.org/xsd/imslorsltitm_v1p0 http://fire.eun.org/lode/imslorsltitm_v1p0.xsd">
      
      <!-- Identifier : BEGIN -->
      <!-- <identifier>
        <catalog>expression-cmr-id</catalog>
        <entry><xsl:value-of disable-output-escaping="yes" select="$cmrId"/></entry>
      </identifier>
      -->
      <xsl:for-each select="lrelom:lom/lrelom:general/lrelom:identifier">
        <identifier>
          <catalog>
            <xsl:value-of select="lrelom:catalog"/>
          </catalog>
          <entry>
            <xsl:value-of select="lrelom:entry"/>
          </entry>
        </identifier>
      </xsl:for-each>
      <!-- Identifier : END -->
      
      <description>
        <facet>
          <vocabularyID>LRE.expressionDescriptionFacetValues</vocabularyID>
          <value>main</value>
        </facet>
        <metadata>
          <schema>http://ltsc.ieee.org/xsd/LOM</schema>

          <lom xmlns="http://ltsc.ieee.org/xsd/LOM" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://ltsc.ieee.org/xsd/LOM http://ltsc.ieee.org/xsd/lomv1.0/lomLoose.xsd">
            <!-- Insert 1 blank line to avoid the namespace problem! -->
            <xsl:text disable-output-escaping="yes">&#xD;&#xA;</xsl:text>
<!-- *********************************************************************** -->        
<!-- *********************************************************************** --> 
<!-- Main -->
            <xsl:apply-templates select="@*|node()"/>
<!-- *********************************************************************** -->             
<!-- *********************************************************************** -->          
<!-- Footer -->
            <xsl:text disable-output-escaping="yes">&#xD;&#xA;</xsl:text>
          </lom>

        </metadata>
      </description>
      <manifestation>
        
        <!-- Identifier : BEGIN -->
        <!-- <identifier>
          <catalog>expression-cmr-id</catalog>
          <entry><xsl:value-of disable-output-escaping="yes" select="$cmrId"/></entry>
          </identifier>
        -->
        <xsl:for-each select="lrelom:lom/lrelom:general/lrelom:identifier">
          <identifier>
            <catalog>
              <xsl:value-of select="lrelom:catalog"/>
            </catalog>
            <entry>
              <xsl:value-of select="lrelom:entry"/>
            </entry>
          </identifier>
        </xsl:for-each>
        <!-- Identifier : END -->
        
        <name>
          <vocabularyID>LRE.manifestationNameValues</vocabularyID>
          <value>experience</value>
        </name>
        
        <!-- Parameter for WEB-PAGE : BEGIN -->
        <!-- <lom><technical><format> -->
        <xsl:if test="normalize-space(lrelom:lom/lrelom:technical/lrelom:format[1]/text())">
            <parameter>
              <vocabularyID>MIME</vocabularyID>
              <value><xsl:value-of disable-output-escaping="yes" select="lrelom:lom/lrelom:technical/lrelom:format"/></value>
            </parameter>
        </xsl:if>  
        <!-- Parameter for WEB-PAGE : END -->
        
        <item>
          <!-- URL : BEGIN -->
          <location>
            <uri>
              <!-- <lom><technical><location> -->
              <xsl:value-of select="lrelom:lom/lrelom:technical/lrelom:location"/>
            </uri>
          </location>
          <!-- URL : END -->
        </item>
        
      </manifestation>
    </expression>
<!-- *********************************************************************** -->    
  </xsl:template>
  
<!-- *************************** LOM 2 LOM : BEGIN ***************************** --> 
  
  <!-- *********************************************************************** -->       
  <!-- 1 : Delete -->
  <!-- <technical> -->
  <xsl:template match="lrelom:lom/lrelom:technical">
  </xsl:template>
  <!-- <classification><keyword> -->
  <xsl:template match="lrelom:lom/lrelom:classification/lrelom:keyword">
  </xsl:template>
  <!-- *********************************************************************** --> 
  
  <!-- *********************************************************************** --> 
  <!-- 3 : Change -->
  <!-- <metaMetadata><metadataSchema>-->
  <!-- LREv3.0 => LREv4.0, otherwise => NONE -->
  <xsl:template match="lrelom:lom/lrelom:metaMetadata/lrelom:metadataSchema">
    <xsl:if test="text()='LREv3.0'" >
      <xsl:value-of select="$metadataSchemaBegin" disable-output-escaping="yes"/>
      <xsl:text disable-output-escaping="yes">LREv4.0</xsl:text>
      <xsl:value-of select="$metadataSchemaEnd" disable-output-escaping="yes"/>
    </xsl:if> 
  </xsl:template>
  <!-- *********************************************************************** --> 
  
  <!-- *********************************************************************** --> 
  <!-- 4 : Copy but take care of some special cases -->
  <!-- <entity>: Keep CDATA -->
  <xsl:template match="lrelom:entity">
    <xsl:value-of select="$entityBegin" disable-output-escaping="yes"/>
    <xsl:value-of select="$CDATABegin" disable-output-escaping="yes"/>
    <xsl:value-of select="." disable-output-escaping="yes"/>
    <xsl:value-of select="$CDATAEnd" disable-output-escaping="yes"/>
    <xsl:value-of select="$entityEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <lom>: Do not copy, hard-coded tag is already inserted -->
  <xsl:template match="lrelom:lom">
    <xsl:apply-templates select="@*|node()"/>
  </xsl:template>
  <!-- *********************************************************************** -->
  
  <!-- *********************************************************************** --> 
  <!-- 2 : Set source values -->
  <!-- <general><structure> -->
  <xsl:template match="lrelom:lom/lrelom:general/lrelom:structure/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">structureValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <general><aggregationLevel> -->
  <xsl:template match="lrelom:lom/lrelom:general/lrelom:aggregationLevel/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">aggregationLevelValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <lifeCycle><status> -->
  <xsl:template match="lrelom:lom/lrelom:lifeCycle/lrelom:status/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">statusValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <lifeCycle><contribute><role> -->
  <xsl:template match="lrelom:lom/lrelom:lifeCycle/lrelom:contribute/lrelom:role/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">LRE.roleValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <metaMetadata><contribute><role> -->
  <xsl:template match="lrelom:lom/lrelom:metaMetadata/lrelom:contribute/lrelom:role/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">LRE.roleMetaValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <educational><interactivityType> -->
  <xsl:template match="lrelom:lom/lrelom:educational/lrelom:interactivityType/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">interactivityTypeValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <educational><learningResourceType> -->
  <xsl:template match="lrelom:lom/lrelom:educational/lrelom:learningResourceType/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">LRE.learningResourceTypeValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <educational><interactivityLevel> -->
  <xsl:template match="lrelom:lom/lrelom:educational/lrelom:interactivityLevel/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">interactivityLevelValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <educational><semanticDensity> -->
  <xsl:template match="lrelom:lom/lrelom:educational/lrelom:semanticDensity/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">semanticDensityValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <educational><intendedEndUserRole> -->
  <xsl:template match="lrelom:lom/lrelom:educational/lrelom:intendedEndUserRole/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">LRE.intendedEndUserRoleValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <educational><context> -->
  <xsl:template match="lrelom:lom/lrelom:educational/lrelom:context/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">LRE.contextValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <educational><difficulty> -->
  <xsl:template match="lrelom:lom/lrelom:educational/lrelom:difficulty/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">difficultyValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <rights><cost> -->
  <xsl:template match="lrelom:lom/lrelom:rights/lrelom:cost/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">costValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <rights><copyrightAndOtherRestrictions> -->
  <xsl:template match="lrelom:lom/lrelom:rights/lrelom:copyrightAndOtherRestrictions/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">copyrightAndOtherRestrictionsValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <relation><kind> -->
  <xsl:template match="lrelom:lom/lrelom:relation/lrelom:kind/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">LRE.kindValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- <classification><purpose> -->
  <xsl:template match="lrelom:lom/lrelom:classification/lrelom:purpose/lrelom:source">
    <xsl:value-of select="$sourceBegin" disable-output-escaping="yes"/>
    <xsl:text disable-output-escaping="yes">purposeValues</xsl:text>
    <xsl:value-of select="$sourceEnd" disable-output-escaping="yes"/>
  </xsl:template>
  <!-- *********************************************************************** --> 
  
  <!-- *********************************************************************** -->
  <!-- 2++ : Set source + language -->
  <!-- <classification><taxonPath> with <classification><purpose> -->
  <xsl:template match="lrelom:lom/lrelom:classification/lrelom:taxonPath/lrelom:source/lrelom:string">
    <xsl:choose>
      <!-- 'lre thesaurus' with 'discipline' LOM -->
      <xsl:when test="translate(text(),$alphabetUpper,$alphabetLower)='lre thesaurus' and
        translate(../../../lrelom:purpose/lrelom:value,$alphabetUpper,$alphabetLower)='discipline' " >
        <string xmlns="http://ltsc.ieee.org/xsd/LOM" language="x-none">LRE-0001</string>
      </xsl:when>
      <!-- otherwise: Copy -->      
      <xsl:otherwise>
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  <!-- *********************************************************************** -->
  
  <!-- *********************************************************************** -->
  <!-- 5 : Keep -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
  <!-- *********************************************************************** --> 
  
  <!-- *************************** LOM 2 LOM : END ******************************* -->  
  
</xsl:stylesheet>
