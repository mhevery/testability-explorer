<#function code text>
  <#return "<tt>" + text?html?replace(' ', '&nbsp;') +"</tt>">
</#function>

<#function color value>
  <#if (value <= maxExcellentCost) >
    <#return "style='background-color:#00FF00'">
  <#elseif (value <= maxAcceptableCost)>
    <#return "style='background-color:#FFFF00'">
  <#else>
    <#return "style='background-color:#FF0000'">
  </#if>  
</#function>

<#macro overview report name listName>
<table class="source" cellspacing="0" cellpadding="0">
  <tr>
    <td class="head" width="100">Metric:</td>
    <td class="head" width="100">Value:</td>
    <td class="head" colspan="2" width="100%">Chart:</td>
  </tr>
  <tr>
    <td nowrap class="metric">${name} cost:</td>
    <td class="score" ${color(report.overallCost)}>${report.overallCost}</td>
    <td rowspan="2" align="center" class="score" style="border-right:none">${report.overallCostChart}</td>
    <td class="score" rowspan="7">${report.histogramChart}</td>
  </tr>
  <tr>
    <td class="score">&nbsp;</td>
    <td class="score">&nbsp;</td>
  </tr>
  <tr>
    <td class="metric">${listName} count:</td>
    <td class="score">${report.count}</td>
    <td rowspan="5" align="center" class="score" style="border-right:none">${report.distributionChart}</td>
  </tr>
  <tr>
    <td class="metric">&nbsp;&nbsp;&nbsp;Excellent:</td>
    <td class="score" style="background-color:#00FF00">${report.excellentCount} [${(report.excellentPercent)?string.percent}]</td>
  </tr>
  <tr>
    <td class="metric">&nbsp;&nbsp;&nbsp;Good:</td>
    <td class="score" style="background-color:#FFFF00">${report.goodCount} [${(report.goodPercent)?string.percent}]</td>
  </tr>
  <tr>
    <td class="metric">&nbsp;&nbsp;&nbsp;Needs work:</td>
    <td class="score" style="background-color:#FF0000">${report.needsWorkCount} [${(report.needsWorkPercent)?string.percent}]</td>
  </tr>
  <tr>
    <td class="score">&nbsp;</td>
    <td class="score">&nbsp;</td>
  </tr>
</table>
</#macro>

<#macro unitList list name keys=["cost"] headers=["Cost"]>
<table class="source" cellspacing="0" cellpadding="0">
  <tr>
    <td class="head">${name}:</td>
    <#list headers as header>
    <td class="head">${header}:</td>
    </#list>
  </tr>
  <#list list as unit>
  <tr>
    <td class="score" ${color(unit.cost)}><a href="<#nested unit>"><tt>${unit.name}</tt></a></td>
    <#list keys as key>
    <td nowrap class="score" ${color(unit.cost)}>${unit[key]}&nbsp;</td>
    </#list>
  </tr>
  </#list>
</table>
</#macro>