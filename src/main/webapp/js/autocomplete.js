var count =0;
var check_set= [];
function getCompletions(val) {
	if(Math.abs(count-val.length)>1)
		{
		return;
		}
	if(check_set.indexOf(val)>=0)
		{
		return;
		}
		count=val.length;
    $.ajax({
        url: "/autocomplete?input=" + val,
        async: true,
        success: function (response) {
        	var data = [];
            var mySplitResult;
            mySplitResult = response.split(",");
            check_set= response;
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
            $("#main_search_text").catcomplete('search', val);
        
        }
    });

}