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

/**
 * Once submit button is clicked
 * create an AJAX call to send the tweaked features
 * to the UI controller
 */
function sendFeatures() {
    var sjData = JSON.stringify(specGlobalFeatureObject);
    $.ajax({
        async: false,
        url: '/sendFeatures',
        data: ({
            dataSpec: sjData
        }),
        success: function (response) {
        	var order = response.split(",");
        	
        	for (var i = 0; i < order.length - 1; i++) 
        	{
            	$.each($("#product-area div"), function () {
            		var attributeId = $(this).attr("id");
            		if (attributeId == order[i])
            		{
            			 $(this).appendTo(this.parentNode);
            		}
            	});
        	}
        }
    });
}

/**
 * Hover effect to display the attributes selection guide
 */
$("#feature_title").hover(
    function () {
        $("#feature_description").toggle();
    },
    function () {
        $("#feature_description").toggle();
    }
);

