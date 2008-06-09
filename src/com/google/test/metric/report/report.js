
function toggle(element) {
  var currentStyle = element.style.display;
  element.style.display = 'block' == currentStyle ? 'none' : 'block';
}

function toggleExpandSign(element) {
  if (element.firstChild.className == 'expand') {
    var expand = element.firstChild;
  } else if (element.className == 'expand') {
    var expand = element;
  }
  expand.innerHTML = expand.innerHTML == '[+]' ? '[-]' : '[+]';
}
     
function clickHandler(event) {
  var element = event.target;
  toggleExpandSign(element);
  if (element.className == 'expand') {
    element = element.parentNode;
  }
  var children = element.childNodes;
  for (var i=0; i < children.length; i++) {
    var child = children[i];
    if (child.nodeName == 'DIV') {
      toggle(child);
    }
  }
}