(function()
{
  var elem = document.getElementById("indexscrollbar");
  tau.widget.CircularIndexScrollbar(elem);
  elem.addEventListener("select", function(event)
  {
	 var index = event.detail.index;
	 /* Print selected index */
	 console.log(index);

  });  
  

}());