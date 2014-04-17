/**
 * Global object to store the
 * user's selected features
 */
var globalFeatureObject = {
    aNames: [],
    aValues: []
};

function findParentID(node, id) {
    parent = node.parentNode;
    while (parent) {
        if (parent.id && parent.id == id)
            return true;
        parent = parent.parentNode;
    }
    return false;
}

/**
 * Check if the user checked/unchecked a check box element.
 * And add/remove it from the global feature object accordingly.
 *
 * @param chBox : check box input element
 * @param name  : name of feature
 * @param val	: value of feature
 */

function isChecked(chBox, name, val) {
    if ($(chBox).prop('checked')) {
        addFeature(chBox, name, val);
    } else {
        removeFeature(chBox, name, val);
    }
    sendFeatures();
}

//remove element from an array
function removeElem(arr) {
    var what, a = arguments,
        L = a.length,
        ax;
    while (L > 1 && arr.length) {
        what = a[--L];
        while ((ax = arr.indexOf(what)) !== -1) {
            arr.splice(ax, 1);
        }
    }
    return arr;
}

/**Remove unselected feature from the global feature object
 *
 * @param name of the feature to be removed
 * @param val of the feature to be removed
 */
function removeFeature(elem, name, val) {
    var exist = false;
    var isSpec = findParentID(elem, "main_specs");

    for (var i = 0; i < globalFeatureObject.aNames.length; i++) {
        if (globalFeatureObject.aNames[i] == name) {
            exist = true;
            removeElem(globalFeatureObject.aNames, name);
            removeElem(globalFeatureObject.aValues, val);
            break;
        }
    }
}

/** 
 * Add feature to the global feature object
 * @param name of feature
 * @param val of feature
 */
function addFeature(elem, name, val) {
    var exist = false;
    var isSpec = findParentID(elem, "main_specs")

    for (var i = 0; i < globalFeatureObject.aNames.length; i++) {
        if (globalFeatureObject.aNames[i] == name) {
            globalFeatureObject.aValues[i] = val;
            exist = true;
            break;
        }
    }
    if (!exist) {
        globalFeatureObject.aNames.push(name);
        globalFeatureObject.aValues.push(val);
    }

}

function startSpinner() {
	var height = $('#product-area').outerHeight();
	$('#spinner-content-mask').height(height);
	$('#spinner-wrapper').css('display', 'block');
}

function stopSpinner() {
	$('#spinner-wrapper').css('display', 'none');
}

/**
 * Once submit button is clicked
 * create an AJAX call to send the tweaked features
 * to the UI controller
 */
function sendFeatures() {
	var checker=0;
    var featureObj = JSON.stringify(globalFeatureObject);
    startSpinner();
    $.ajax({
        async: true,
        url: '/sendFeatures',
        data: ({
        	pUserFeatureList: featureObj,
            pCategoryId: $("#categoryMarker").attr('title')
        }),
        success: function (response) {
        	// Spinner 
        	stopSpinner();
        	$("#product-area").empty();
        	var spinner_div = $("<div>").attr("id", "spinner-wrapper").appendTo($("#product-area"));
        	var spinner_div_mask = $("<div>").attr("id", "spinner-content-mask").appendTo(spinner_div);
        	var spinner_div_mask = $("<div>").attr("id", "spinner").appendTo(spinner_div);
        	
        	var splittingpart = response.split("|||");
        	//Products
        	var jsonResponse = JSON.parse(splittingpart[0]);
        	var completeResponse = JSON.parse(splittingpart[1]);
        	
        	var name1 = completeResponse[0].explanation[0].name;
        	var name2 = completeResponse[0].explanation[1].name;
        	var name3 = completeResponse[0].explanation[2].name;
        	
        	console.log("Name1 "+name1);
        	console.log("Name 2"+name2);
        	console.log("Name 3"+name3);
        	
        	var attachment1 = $("<div>").addClass("graph");
        	var attachment2 = $("<div>").addClass("graph");
        	var attachment3 = $("<div>").addClass("graph");
        	
        	var name1_array = getarray(completeResponse,name1);
        	var name2_array = getarray(completeResponse,name2);
        	var name3_array = getarray(completeResponse,name3);
        	
        //	console.log("Array 1 is "+name1_array);
        	
        	for(var x=0;x<name1_array.length;x++)
        		{
        		var temp = $("<div>").addClass("line").attr("style","width: "+230/name1_array.length+"px;height : "+name1_array[x]+"px;top:"+(100-name1_array[x])+"px;");
        			attachment1.append(temp);
        		}
        	for(var x=0;x<name2_array.length;x++)
    		{
    		 var temp = $("<div>").addClass("line").attr("style","width: "+230/name2_array.length+"px;height : "+name2_array[x]+"px;top:"+(100-name2_array[x])+"px;");
    			attachment2.append(temp);
    		}
        	
        	for(var x=0;x<name3_array.length;x++)
    		{
    		 var temp = $("<div>").addClass("line").attr("style","width: "+230/name3_array.length+"px;height : "+name3_array[x]+"px;top:"+(100-name3_array[x])+"px;");
    			attachment3.append(temp);
    		}
        	
        	console.log("HTML class is "+attachment1);
        	//console.log("Array 2 is "+name2_array);
        	//console.log("Array 3 is "+name3_array);
        	
        	for(var r = 0; r<jsonResponse.length; r++)
        	{
        		var added_explanations=0;
        		var product_div = $("<div>").addClass("rankedproduct-result-entry");
        		var product_div_image = $("<div>").addClass("product-image").css("background-image", "url('"+jsonResponse[r].productIMAGE+"')");
        		var product_div_content = $("<div>").addClass("product-description-area"); 

        		
// Display of detailed explanations        		
//Finding the position
        		var counter_variable=0;
        		for(counter_variable=0;counter_variable<completeResponse.length;counter_variable++)
        			{
        				if(jsonResponse[r].productID==completeResponse[counter_variable].productID)
        						break;
        			}

        			text = $("<a>").addClass("atrigger").text(" Detailed Explanation").attr("id",jsonResponse[r].productID);
        		var display = $("<div>").addClass("pop-up").attr("id","explanation"+jsonResponse[r].productID).text("");
        		var row = $("<div>").attr("style","display:table-row");
        		var column1 =$("<div>").attr("style","display:table-cell;width:40%").text( completeResponse[counter_variable].productName);
        		row.append(column1);
        		display.append(row);
        		
        		for(var j = 0 ; j < completeResponse[counter_variable].explanation.length; j++)
        		{
        			var exp = completeResponse[counter_variable].explanation[j];
            		var rankValue=0;
        			if(exp.boolean)
            		{
            		}
            		else
            		{
            			if(exp.isExplained == -1) //feature doesn't exist for this product
                		{
                			
                		}
            			else // Added only features which can be measured
                		{
            				added_explanations++;
                			rankValue = (exp.productsNum - exp.rank + 1)/exp.productsNum;
                    		rankValue = rankValue * 100;
                    		var row = $("<div>").attr("style","display:table-row");
                    		var column1 =$("<div>").attr("style","display:table-cell;width:40%").text(exp.name);
                    		var color = stringToColorCode(exp.name);
                    		var column2 = $("<div>").addClass("progress-bar").attr("style","display: table-cell;width:"+rankValue+"%;background:#"+color.split("").reverse().join("")+";");
                    		var temporary2 = $("<div>").text("&nbsp;").html();
                    		temporary2 = temporary2.replace("&amp;","&");
                    		var buffer = column2;
                    		buffer.append(temporary2);
                    		column2 =$("<div>").attr("style","display:table-cell;");
                    		column2.append(buffer);
                    		row.append(column1);
                    		row.append(column2);
                    		display.append(row);
                    


                		}
            		}   
        			
        			
        		}

        		product_div_content.append(display);		
 // End of detailied explanations adding       		
        		
   //Start of bar graph calculations
        		
        		
        		
   //End of bar graph calculations
        		
        		
        		var product_div_name = null;
        		var product_div_exp = null;
        		var product_div_exp_value = null;
        		
        		if (jsonResponse[r].productURL == "") {
        			product_div_name = $("<p>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName);
        		} else {
        			product_div_name = $("<a>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName).attr("href", jsonResponse[r].productURL);
        		}
        		var parentbloc1 =  $("<div>").css({"width":"50%","margin":"0 auto"})
        		
        		
        		
        		var abcde= $("<div>").addClass("rankexplanation-attr-name").text(name1+ ":");
        		abcde.appendTo(parentbloc1);
        		
        		var attachment1 = $("<div>").addClass("graph");
            	
        		checker =  search (name1_array,jsonResponse[r].productID);
        		
        		if(checker>0)
        			{
        				for(var x=0;x<name1_array.length;x++)
        				{
        					var rankValue = (name1_array[x].aSize- name1_array[x].aRank + 1)/name1_array[x].aSize;
        					rankValue = rankValue * 100;
        					var temp = $("<div>").addClass("line").attr("style","width: "+230/name1_array.length+"px;height : "+rankValue+"px;top:"+(100-rankValue)+"px;").attr("id",name1_array[x].aID);
        					temp.attr("url",name1_array[x].aURL);	
        					if(name1_array[x].aID ==jsonResponse[r].productID )
        					{
        						temp = $("<div>").addClass("selected").attr("style","width: "+230/name1_array.length+"px;height : "+rankValue+"px;top:"+(100-rankValue)+"px;background-color:#000000").attr("id",name1_array[x].aID);
        						temp.attr("url",name1_array[x].aURL);	
        					}
        					temp.clone().appendTo(attachment1);
        				}
        			}
        		else
        			{
        				abcde= $("<div>").addClass("rankexplanation-attr-name").text("No information");
        				abcde.clone().appendTo(attachment2);
        			}
         	        		
                attachment1.appendTo(parentbloc1);
                
                abcde= $("<div>").addClass("rankexplanation-attr-name").text(name2+ ":");
                abcde.appendTo(parentbloc1);
                
        		var attachment2 = $("<div>").addClass("graph");
            	
        		checker =  search (name2_array,jsonResponse[r].productID);
        		
        		if(checker>0)
        		{
        			for(var x=0;x<name2_array.length;x++)
        			{
        				var rankValue = (name2_array[x].aSize- name2_array[x].aRank + 1)/name2_array[x].aSize;
        				rankValue = rankValue * 100;
        				var temp = $("<div>").addClass("line").attr("style","width: "+230/name2_array.length+"px;height : "+rankValue+"px;top:"+(100-rankValue)+"px;").attr("id",name2_array[x].aID);
        				temp.attr("url",name2_array[x].aURL);
        				if(name2_array[x].aID ==jsonResponse[r].productID )
        				{
        					temp = $("<div>").addClass("selected").attr("style","width: "+230/name2_array.length+"px;height : "+rankValue+"px;top:"+(100-rankValue)+"px;background-color:#000000").attr("id",name2_array[x].aID);
        					temp.attr("url",name2_array[x].aURL);
        				}
        				temp.clone().appendTo(attachment2);
        			}
        		}
        		else
        			{
        			 abcde= $("<div>").addClass("rankexplanation-attr-name").text("No information");
            		 abcde.clone().appendTo(attachment2);
        			}
                attachment2.appendTo(parentbloc1);
                
                abcde= $("<div>").addClass("rankexplanation-attr-name").text(name3+ ":");
                abcde.appendTo(parentbloc1);
                
                
        		var attachment3 = $("<div>").addClass("graph");
            	checker = search (name3_array,jsonResponse[r].productID);
            	if(checker>0)
            		{
            			for(var x=0;x<name3_array.length;x++)
            				{
            					var rankValue = (name3_array[x].aSize- name3_array[x].aRank + 1)/name3_array[x].aSize;
            					rankValue = rankValue * 100;
            					var temp = $("<div>").addClass("line").attr("style","width: "+230/name3_array.length+"px;height : "+rankValue+"px;top:"+(100-rankValue)+"px;").attr("id",name3_array[x].aID);
            					temp.attr("url",name3_array[x].aURL);
            					if(name3_array[x].aID ==jsonResponse[r].productID )
            					{
            						temp = $("<div>").addClass("selected").attr("style","width: "+230/name3_array.length+"px;height : "+rankValue+"px;top:"+(100-rankValue)+"px;background-color:#000000").attr("id",name3_array[x].aID);
            						temp.attr("url",name3_array[x].aURL);
            					}
            					temp.clone().appendTo(attachment3);
            				}
            		}
            	else
            		{
            		 abcde= $("<div>").addClass("rankexplanation-attr-name").text("No information");
            		 abcde.clone().appendTo(attachment3);
            		}
                attachment3.appendTo(parentbloc1);
        		
                
        		for(var j = 0 ; j < jsonResponse[r].explanation.length; j++)
        		{
            			var exp = jsonResponse[r].explanation[j];
                     	
            			product_div_exp = $("<p>").addClass("rankexplanation-attr-name").text(exp.name);
            			product_div_exp_value = $("<p>").addClass("rankexplanation-attr-value").text(exp.productsNum);//total Num of products
            			var bloc1 = $("<div>").addClass("bloc1");
                    	
                		var explanation_attribute = $("<div>").addClass("rankexplanation-attr-name").text(exp.name+ ":");
                		explanation_attribute.appendTo(bloc1);
                		var progress = $('<div>', {class: 'progress progress-info'});
                		var progress1 = null;
                		var progress2 = null;
                		var progress3 = null;
                		var rankValue = 0;
                		
                		
                		if(exp.boolean)
                		{
                			progress1 = $("<div>").addClass("boolean-explanation").text(exp.boolValue).css("color","#2a6496");                		                			
                			progress1.appendTo(bloc1);
                		}
                		else
                		{
                			if(exp.isExplained == -1) //feature doesn't exist for this product
                    		{
                    			progress1 = $("<div>").addClass("boolean-explanation").text("Not Available").css("font-style","italic");                		                			
                    			progress1.appendTo(bloc1);
                    			
                    		}else
                    		{
                    			rankValue = (exp.productsNum - exp.rank + 1)/exp.productsNum;
                        		rankValue = rankValue * 100;
                        		progress1 = $("<div>").addClass("progress-bar").text("Rank:"+exp.rank).attr("aria-valuenow",rankValue).css("width",rankValue+"%");                		
                        		progress1.attr("aria-valuemax",exp.productsNum);
                        		progress1.appendTo(progress);                    		
                        		progress.appendTo(bloc1);                    		

                    		}
                		}    
                		
                		bloc1.appendTo(parentbloc1);
            	}
        		
        		
        		product_div.append(product_div_image);
        	// Checking if any recommendations exists
        		if(globalFeatureObject.aNames.length>0 && added_explanations>0)
        			{
        			product_div.append(text);
        			
        			}
        		product_div.append(product_div_content);		
        		product_div_content.append(product_div_name);


        		product_div.append(parentbloc1);
        		$("#product-area").append(product_div);   
        		initialise2();
        		initialise();
        		initialise3();

        	}

        }
    });
    
}


// Intilisating the event listeners in javascript
function initialise() {
    var moveLeft = 20;
    var moveDown = 10;
    
    $(".atrigger").hover(function(e) {
  	 var id = $(this).attr("id");
  	// console.log(id);
      $("div#explanation"+id).css( "display", "block")
      //.css('top', e.pageY + moveDown)
      //.css('left', e.pageX + moveLeft)
      //.appendTo('body');
    }, function() {
     	 var id = $(this).attr("id");
      $("div#explanation"+id).hide();
    });
    
    $(".atrigger").mousemove(function(e) {
     	 var id = $(this).attr("id");
      $("div#explanation"+id).css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
    });
    
  }


// Converting from string to color

function stringToColorCode(str) {
	  var code = stringToHex(str);
	  code = code.substr( 0, 6);
	  return code;
	  
}


// Converting from string to hex
function stringToHex (tmp) {
	tmp=tmp.split("").reverse().join("");
    var str = '',
        i = 0,
        tmp_len = tmp.length,
        c;
 
    for (; i < tmp_len; i += 1) {
        c = tmp.charCodeAt(i);
        str += d2h(c) + '';
    }
    return str;
}


// Helper function
function d2h(d) {
    return d.toString(16);
}

function getarray(splittingpart,name)
{
		var j=0;
		var k=0;
		var array =[{aID:[],aRank:[],aIndex:[],aSize:[],aURL:[]}];
		var completeResponse =splittingpart;
    	console.log("Size is is "+completeResponse.length);
    	for(j=0;j<completeResponse.length;j++)
    		{
    		 	for(k=0;k<completeResponse[j].explanation.length;k++)
    		 		{
    		 			if(name == completeResponse[j].explanation[k].name)
    		 				{
    		 					var exp = completeResponse[j].explanation[k];
    		 					var rankValue = (exp.productsNum - exp.rank + 1)/exp.productsNum;
    		 					rankValue = rankValue * 100;
    		 					if(rankValue>100)
    		 						rankValue =1;
    		 					array.push ({aID:completeResponse[j].productID,
    		 						aRank: parseInt(exp.rank),
    		 						aIndex:k,
    		 						aSize:completeResponse.length,
    		 						aURL :completeResponse[j].productURL });
    		 		
    		 				}
    		 		}
    		}
    	function mycomparator(a,b) {   return parseInt(b.aRank) - parseInt(a.aRank);  }
    	array= array.sort(mycomparator );
    	
        for(var i = array.length; i--;) {
            if(array[i].aRank === -1) {
                array.splice(i, 1);
            }
        }
        array.splice(0,1);
		return array;
	}

function initialise2()
{
    var moveLeft = 20;
    var moveDown = 10;
    
    $('.line').click(function(){
    	if($(this).attr("url").length>2)
    	window.open( $(this).attr("url"));
    });
    
    $('.selected').click(function(){
    	if($(this).attr("url").length>2)
    	window.open( $(this).attr("url"));
    });
    
    $(".line").hover(function(e) {
  	 var id = $(this).attr("id");
  	// console.log(id);
  	 $(this).css("background-color","#FF0000");
      $("div#explanation"+id).css( "display", "block")
      //.css('top', e.pageY + moveDown)
      //.css('left', e.pageX + moveLeft)
      //.appendTo('body');
    }, function() {
    	 $(this).css("background-color","");
     	 var id = $(this).attr("id");
      $("div#explanation"+id).hide();
    });
    
    $(".line").mousemove(function(e) {
     	 var id = $(this).attr("id");
      $("div#explanation"+id).css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
    });
	
	}


function initialise3()
{
    var moveLeft = 20;
    var moveDown = 10;
    
    $(".selected").hover(function(e) {
  	 var id = $(this).attr("id");
  	// console.log(id);
  	 $(this).css("background-color","#FF0000");
      $("div#explanation"+id).css( "display", "block")
      //.css('top', e.pageY + moveDown)
      //.css('left', e.pageX + moveLeft)
      //.appendTo('body');
    }, function() {
    	 $(this).css("background-color","");
     	 var id = $(this).attr("id");
      $("div#explanation"+id).hide();
    });
    
    $(".selected").mousemove(function(e) {
     	 var id = $(this).attr("id");
      $("div#explanation"+id).css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
    });
	
	}


function search(array,key)
{
	for(var i=0;i<array.length;i++)
		{
			if(array[i].aID==key)
				return 1;
		}
	return 0;
	}