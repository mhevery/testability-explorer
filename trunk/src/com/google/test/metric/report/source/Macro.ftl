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
