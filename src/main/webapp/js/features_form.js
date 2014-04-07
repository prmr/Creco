/**
 * Global object to store the
 * user's selected features
 */
var specGlobalFeatureObject = {
    names: [],
    values: []
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

    for (var i = 0; i < specGlobalFeatureObject.names.length; i++) {
        console.log("specGlobalFeatureObject.names[i] " + specGlobalFeatureObject.names[i]);
        if (specGlobalFeatureObject.names[i] == name) {
            exist = true;
            removeElem(specGlobalFeatureObject.names, name);
            removeElem(specGlobalFeatureObject.values, val);
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

    for (var i = 0; i < specGlobalFeatureObject.names.length; i++) {
        if (specGlobalFeatureObject.names[i] == name) {
            specGlobalFeatureObject.values[i] = val;
            exist = true;
            break;
        }
    }
    if (!exist) {
        specGlobalFeatureObject.names.push(name);
        specGlobalFeatureObject.values.push(val);
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
    var sjData = JSON.stringify(specGlobalFeatureObject);
    startSpinner();
    $.ajax({
        async: true,
        url: '/sendFeatures',
        data: ({
            dataSpec: sjData,
            pCategoryId: $("#categoryMarker").attr('title')
        }),
        success: function (response) {
        	
        	// Spinner 
        	stopSpinner();
        	$("#product-area").empty();
        	var spinner_div = $("<div>").attr("id", "spinner-wrapper").appendTo($("#product-area"));
        	var spinner_div_mask = $("<div>").attr("id", "spinner-content-mask").appendTo(spinner_div);
        	var spinner_div_mask = $("<div>").attr("id", "spinner").appendTo(spinner_div);
        	console.log(" in success");
        	
        	// Products
        	var productList = response.split(";");
        	var explanationList = response.split("|");
        	for (var i = 0; i < productList.length - 1; i++) 
        	{
        	//	console.log(" productList "+ i+ " "+ productList[i])
        		var product_str = productList[i];
        		var product = product_str.split(",");
        		var product_div = $("<div>").addClass("rankedproduct-result-entry");
        		var product_div_image = $("<div>").addClass("no-product-image").text("No image");
        		var product_div_content = $("<div>").addClass("product-description-area");  
        		var product_div_name = null;
        		if (product[2] == "") {
        			product_div_name = $("<p>").addClass("rankedproduct-result-name").text(product[1]);
        		} else {
        			product_div_name = $("<a>").addClass("rankedproduct-result-name").text(product[1]).attr("href", product[2]);
        		}
        		//Explanations
        		//=for(var j=0 ; j < explanationList.length -1; j++)
        		//={
        			var exp = explanationList[i+1].split(",");
        			console.log(" explanation 0 "+JSON.stringify(exp[0]));          
        			console.log(" explanation 1 "+JSON.stringify(exp[1]));            		
        			console.log(" explanation 2 "+JSON.stringify(exp[2]));            		
        			console.log(" explanation 3 "+JSON.stringify(exp[3]));            		
            		
        		//=}
        		
        		product_div.append(product_div_image);
        		product_div.append(product_div_content);
        		product_div_content.append(product_div_name);
        		$("#product-area").append(product_div);
        	}
        }
    });
}