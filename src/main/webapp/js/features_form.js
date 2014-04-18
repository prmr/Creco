$(function() {  	  
    var slider = $('.slider'),  
        tooltip = $('.tooltip');  
  
    tooltip.hide();  
  
    slider.slider({  
        range: "min",  
        min: 0,      
        max:100,
        start: function(event,ui) {  
          tooltip.fadeIn('fast');  
        },
        
        slide: function(event, ui) {
            var value = slider.slider('value');                  
            tooltip.css('left', value).text(ui.value);      
        },  
        stop: function(event, ui) {            
        	 console.log("stop value" + ui.value);
        	 var value = ui.value;
        	 var id = $(this).attr('id');
        	 var name =$(this).attr('name');
        	 console.log("data " + id+ " "+ value+" "+name);
        	 addFeature();
        	  tooltip.fadeOut('fast'); 
        },
        create: function(event, ui){
        	var val=$(this).parent().attr('class')*100;
            $(this).slider('value',Math.round(val));
            console.log("value is " + $(this).parent().attr('class'));
            
        },

    });  
  
});  

/**
 * Global object to store the
 * user's selected features
 */
var globalFeatureObject = {
	aIds:[],
    aNames: [],
    aValues: []
};

var userObjectList = [];

function findParentID(node, id) {
    parent = node.parentNode;
    while (parent) {
        if (parent.id && parent.id == id)
            return true;
        parent = parent.parentNode;
    }
    return false;
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

function addFeature()
{
    var exist = false;
    var classElem = $('.slider').each(function(index)
    {
    	 for(var i=0; i< userObjectList.length ; i++) 
         {
    		 if(userObjectList[i].aId == $( this ).attr('id'))
    		 {
    			 userObjectList[i].aValue = $( this ).slider("option", "value");    		     
                 exist = true;
                 break;
    		 }
          }
    	 if(!exist)
    	 {
	    	 var temp = new Object();
	         temp.aId = $( this ).attr('id');
	         temp.aName = $( this ).attr('name');
	         temp.aValue = $( this ).slider("option", "value");
	         userObjectList.push(temp);
    	 }
   	});
    sendFeatures();

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
    var featureObj = JSON.stringify({userFeatures:userObjectList});
    startSpinner();
    $.ajax({
        async: true,
        url: '/sendFeatures',
        data: ({
        	pUserFeatureList: featureObj,
            pCategoryId: $("#categoryMarker").attr('title')
        }),
        success: function (response) {
        	var counter =0 ;
        	// Spinner 
        	stopSpinner();
        	$("#product-area").empty();
        	var spinner_div = $("<div>").attr("id", "spinner-wrapper").appendTo($("#product-area"));
        	var spinner_div_mask = $("<div>").attr("id", "spinner-content-mask").appendTo(spinner_div);
        	var spinner_div_mask = $("<div>").attr("id", "spinner").appendTo(spinner_div);
        	
        	//Products
        	var jsonResponse = JSON.parse(response);
        	var completeResponse = JSON.parse(response);
        	
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

        			text = $("<a>").addClass("atrigger").text(" Detailed Explanation").attr("id",counter);
        		var display = $("<div>").addClass("pop-up").attr("id","explanation"+counter).text("");

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
        		counter++;
        		product_div_content.append(display);
        		        		
        		var product_div_name = null;
        		var product_div_exp = null;
        		var product_div_exp_value = null;
        		
        		if (jsonResponse[r].productURL == "") {
        			product_div_name = $("<p>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName);
        		} else {
        			product_div_name = $("<a>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName).attr("href", jsonResponse[r].productURL);
        		}
        		var parentbloc1 =  $("<div>").css({"width":"50%","margin":"0 auto"})
        		
        		
        		product_div.append(product_div_image);
        		
        	// Checking if any recommendations exists
        		if(userObjectList.length > 0 && added_explanations > 0)
        		{
        			product_div.append(text);
        		}
        		product_div.append(product_div_content);		
        		product_div_content.append(product_div_name);
        		product_div.append(parentbloc1);
        		$("#product-area").append(product_div);       
        		initialise();
        	}//end for

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
