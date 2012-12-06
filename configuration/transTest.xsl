<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output omit-xml-declaration="yes"/>
    <xsl:template match="/">
        <table width="100%" border="0">
            <tr>
                <td valign="middle" align="left">
                    <table class="adminheading">
                        <td width="64" valign="middle" align="left" class="user">
                            <xsl:if test="//result[text()='not ok']">
                                <img src="../images/cancel_f2.png" alt="result ok" />
                            </xsl:if>
                            <xsl:if test="not(//result[text()='not ok'])">

                                <img src="../images/checkin.png" border="0" alt="User Manager" />
                            </xsl:if>
                        </td>
                        <td valign="middle" align="left" class="sectionname">
                            Configuration Test Script
                        </td>
                    </table>


        <table class="adminlist">
            <tr>
                <th  class="title">

                </th>                
                <th width="34%" class="title">
                    Description
                </th>
                <th width="34%" class="title" nowrap="nowrap">
                    Error
                </th>
                <th width="34%" class="title">
                    Solution
                </th>
            </tr>
            <xsl:apply-templates/>
        </table>
        </td>
        </tr>
        </table>
    </xsl:template>
    <xsl:template match="test">
        <tr>
            
           <td  class="content">
               <xsl:if test="result = 'ok'">
                   <img src="../images/apply_f2.png" alt="result ok" width="16" height="16"/>
               </xsl:if>
               <xsl:if test="result = 'not ok'">
                   <img src="../images/cancel_f2.png" alt="result ok" width="16" height="16" />
               </xsl:if>
            </td>
            <td  class="content"><xsl:value-of select="desc"/></td>
            <td  class="content"><xsl:value-of select="error"/></td>
            <td  class="content"><xsl:value-of select="solution"/><a><xsl:attribute name="href"><xsl:value-of select="solution/@link"/></xsl:attribute><xsl:value-of select="solution/@tag"/></a><xsl:value-of select="solution/@postfix"/></td>
        </tr>
    </xsl:template>
</xsl:stylesheet>
