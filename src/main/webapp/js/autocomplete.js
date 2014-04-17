var count =0;
var check_set= [];
function getCompletions(val) {
//	if(Math.abs(count-val.length)>1)
//		{
//		return;
//		}
	if(val.length<3)
		{
		check_set=[];
		return;
		}
	if(check_set.indexOf(val)>=0 && val.length>2&&count-1!=val.length)
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
            mySplitResult =JSON.parse(response);
            var index = mySplitResult.length;
            var i;
            var temporary="";
            if(index>1)
        	{
        	for( i =0;i<10;i=i+1)
        		{
        		temporary = temporary.concat(mySplitResult[i].name);
        		temporary = temporary.concat("| ");
        		data.push({label : mySplitResult[i].name,category : mySplitResult[i].type});
        		}
        	check_set= temporary;
        	}
            else
            	check_set=[];
            $("#main_search_text").catcomplete({
                delay: 0,
                source: data
              });
            $("#main_search_text").catcomplete('search', val);
        
        }
    });

}