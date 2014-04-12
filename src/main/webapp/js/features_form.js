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
        	
        	//Products
        	var jsonResponse = JSON.parse(response);

        	for(var r = 0; r<jsonResponse.length; r++)
        	{
        		var product_div = $("<div>").addClass("rankedproduct-result-entry");
        		var product_div_image = $("<div>").addClass("product-image").css("background-image", "url('"+jsonResponse[r].productIMAGE+"')");
        		var product_div_content = $("<div>").addClass("product-description-area");          	
        		var product_div_name = null;
        		var product_div_exp = null;
        		var product_div_exp_value = null;
        		
        		if (jsonResponse[r].productURL == "") {
        			product_div_name = $("<p>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName);
        		} else {
        			product_div_name = $("<a>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName).attr("href", jsonResponse[r].productURL);
        		}
        		var parentbloc1 =  $("<div>").css({"width":"50%","margin":"0 auto"})
        		
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
        		product_div.append(product_div_content);
        		product_div_content.append(product_div_name);
        		product_div.append(parentbloc1);
        		$("#product-area").append(product_div);        		
        	}

        }
    });
}
