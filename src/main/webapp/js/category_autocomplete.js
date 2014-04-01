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