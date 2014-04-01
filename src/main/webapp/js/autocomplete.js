function getCompletions(val) {
    $.ajax({
        url: "/autocomplete?input=" + val,
        async: true,
        success: function (response) {
        	var data = [];
            var mySplitResult;
            mySplitResult = response.split(",");
            var index = mySplitResult.length;
            if(index>1)
        	{
        	for( var i =0;i<19;i=i+2)
        		data.push({label : mySplitResult[i],category : mySplitResult[i+1]});
        	}
            $("#main_search_text").catcomplete({
                delay: 0,
                source: data
              });


        }
    });

}