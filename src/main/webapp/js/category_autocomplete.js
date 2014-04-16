  $.widget( "custom.catcomplete", $.ui.autocomplete, {
    _renderMenu: function( ul, items ) {
      var that = this,
        currentCategory = "";
      $.each( items, function( index, item ) {
    	//  item.category="";
      //  if ( item.category != currentCategory ) {
      //   ul.append( "<li class='ui-autocomplete-category'>" +"" + "</li>" );
       //   currentCategory = item.category;
       // }
        that._renderItemData( ul, item );
      });
    }
  });
  
  

  
  $(function() {
      var moveLeft = 20;
      var moveDown = 10;
      
      $(".atrigger").hover(function(e) {
    	  console.log("triggering1");
        //$('div#pop-up').show();
    	  $('div#pop-up').css( "display", "table-row")
        //.css('top', e.pageY + moveDown)
        //.css('left', e.pageX + moveLeft)
        //.appendTo('body');
      }, function() {
        $('div#pop-up').hide();
      });
      
      $(".atrigger").mousemove(function(e) {
        $("div#pop-up").css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
      });
      
    });