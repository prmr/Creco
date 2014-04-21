$(function () {
    var slider = $('.slider');

    slider.slider({
        range: "min",
        min: 0,
        max: 100,
        
       slide: function (event, ui) {
            var value = $(this).slider('value');
            var target = ui.handle || $('.ui-slider-handle'); 
       	 	var id = $(this).attr('id');
            var tooltip = '<div class="tooltip" id="'+id+'"><div class="tooltip-inner">' + ui.value + '</div><div class="tooltip-arrow"></div></div>';
            $(target).html(tooltip);
        },
        stop: function (event, ui) {
            var id = $(this).attr('id');
            addFeature();
            $('.tooltip').attr('id',id).fadeOut('fast');
        },
        create: function (event, ui) {
            var val = $(this).parent().attr('class');         
            $(this).slider('value', Math.round(val));
         },
    });

});

/**
 * Global object to store the
 * user's selected features
 */
var globalFeatureObject = {
    aIds: [],
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

function addFeature() {
    var exist = false;
    var classElem = $('.slider').each(function (index) {
        for (var i = 0; i < userObjectList.length; i++) {
            if (userObjectList[i].aId == $(this).attr('id')) {
                userObjectList[i].aValue = $(this).slider("option", "value");
                exist = true;
                break;
            }
        }
        if (!exist) {
            var temp = new Object();
            temp.aId = $(this).attr('id');
            temp.aName = $(this).attr('name');
            temp.aValue = $(this).slider("option", "value");
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

    var MAX_PRODUCTS = 20;

    var featureObj = JSON.stringify({
        userFeatures: userObjectList
    });

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
            var completeResponse = JSON.parse(response);

            for (var r = 0; r < jsonResponse.length && r < MAX_PRODUCTS; r++) {
                var added_explanations = 0;
                var product_div = $("<div>").addClass("rankedproduct-result-entry");
                var product_div_image = $("<div>").addClass("product-image").css("background-image", "url('" + jsonResponse[r].productIMAGE + "')");
                if (jsonResponse[r].productIMAGE == "") {
                	product_div_image = $("<div>").addClass("no-product-image").text("No Image");
                }
                var product_div_content = $("<div>").addClass("product-description-area");

                //Finding the position

                var counter_variable = 0;
                for (counter_variable = 0; counter_variable < completeResponse.length; counter_variable++) {
                    if (jsonResponse[r].productID == completeResponse[counter_variable].productID)
                        break;
                }
                text = $("<div>").addClass("atrigger").attr("id", jsonResponse[r].productID).text("See More");
                var display = $("<div>").addClass("pop-up").attr("id", "explanation" + jsonResponse[r].productID).text("");

                var row = addTableEntryName(completeResponse[counter_variable].productName);
                display.append(row);

                for (var j = 0; j < completeResponse[counter_variable].explanation.length; j++) {
                    var exp = completeResponse[counter_variable].explanation[j];
                    if (exp.boolean) {} else {
                        if (exp.isExplained == -1) //feature doesn't exist for this product
                        {} else // Added only features which can be measured
                        {
                            added_explanations++;
                            var row = addTableEntry(exp);
                            display.append(row);
                        }
                    }
                }

                product_div_content.append(display);
                // End of detailed explanations adding       		
                var product_div_name = null;
                var product_div_exp = null;
                var product_div_exp_value = null;

                if (jsonResponse[r].productURL == "") {
                    product_div_name = $("<p>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName);
                } else {
                    product_div_name = $("<a>").addClass("rankedproduct-result-name").text(jsonResponse[r].productName).attr("href", jsonResponse[r].productURL);
                }
                var tableDisplay = $("<div>").css({
                    "width": "100%",
                    "margin": "0 auto",
                    "display": "table"
                })
                var graphBloc = $("<div>").css({
                    "display": "table-row",
                    "width": "100%",
                    "margin": "0 auto"
                })
                var labelBloc = $("<div>").css({
                    "display": "table-row",
                    "width": "100%",
                    "margin": "0 auto"
                })

                for (var j = 0; j < jsonResponse[r].explanation.length && j < 3; j++)

                {
                    var exp = jsonResponse[r].explanation[j];
                    //product_div_exp_value = $("<p>").addClass("rankexplanation-attr-value").text(exp.productsNum);//total Num of products
                    var progress = $('<div>', {
                        class: 'rankedproduct-attr-data'
                    });
                    var progressbar = null;

                    var rankValue = 0;

                    if (exp.userScore > 0) {

                        if (exp.boolean) {
                            progress1 = $("<div>").addClass("rankedproduct-attr-data").text(exp.boolValue).css({
                                "color": "#2a6496"
                            });
                            progress1.appendTo(graphBloc);
                        } else {
                            if (exp.isExplained == -1) //feature doesn't exist for this product
                            {
                                progress1 = $("<div>").addClass("rankedproduct-attr-data").text("Not Available").css({
                                    "font-style": "italic"
                                });
                                progress1.appendTo(graphBloc);

                            } else {
                                var graph = addgraph(getarray(completeResponse, exp.name), jsonResponse, r);
                                graph.appendTo(graphBloc);

                            }
                        }
                        var heading_name = $("<div>").addClass("rankedproduct-attr-name").text(exp.name);
                        heading_name.appendTo(labelBloc);
                    }

                }
                text.appendTo(graphBloc);
                graphBloc.appendTo(tableDisplay);
                labelBloc.appendTo(tableDisplay);
                product_div.append(product_div_image);
                product_div.append(product_div_content);
                product_div_content.append(product_div_name);
                product_div.append(tableDisplay);
                $("#product-area").append(product_div);
                //initialise2();
                initialise();
            } //end for

        }
    });

}
// Intilisating the event listeners in javascript
function initialise() {
    var moveLeft = 20;
    var moveRight = -175; //the  - is for the right movement
    var moveDown = 10;

    // Trigger for the tab of detailed explanations
    $(".atrigger").hover(function (e) {
        var id = $(this).attr("id");
        // console.log(id);
        $("div#explanation" + id).css("display", "block")
        //.css('top', e.pageY + moveDown)
        //.css('left', e.pageX + moveLeft)
        //.appendTo('body');
    }, function () {
        var id = $(this).attr("id");
        $("div#explanation" + id).hide();
    });

    // Trigger when mouse moves around inside the div 
    $(".atrigger").mousemove(function (e) {
        var id = $(this).attr("id");
        $("div#explanation" + id).css('top', e.pageY + moveDown).css('left', e.pageX + moveRight);
    });

}


// Converting from string to color

function stringToColorCode(str) {
    var code = stringToHex(str);
    code = code.substr(0, 6);
    return code;
}


// Converting from string to hex
function stringToHex(tmp) {
    tmp = tmp.split("").reverse().join("");
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

/*
 *Function used to get the array arranged in proper order
 *Deletes all the values which are ranked -1
 *Function parameters passed array and name
 * Returns the modified array
 * */

function getarray(arrayPassed, name) {
    var j = 0;
    var k = 0;
    var array = [{
        aID: [],
        aRank: [],
        aIndex: [],
        aSize: [],
        aURL: []
    }];
    var completeResponse = arrayPassed;
    for (j = 0; j < completeResponse.length; j++) {
        for (k = 0; k < completeResponse[j].explanation.length; k++) {
            if (name == completeResponse[j].explanation[k].name) {
                var exp = completeResponse[j].explanation[k];
                var rankValue = (exp.productsNum - exp.rank + 1) / exp.productsNum;
                rankValue = rankValue * 100;
                if (rankValue > 100)
                    rankValue = 1;
                array.push({
                    aID: completeResponse[j].productID,
                    aRank: parseInt(exp.rank),
                    aIndex: k,
                    aSize: completeResponse.length,
                    aURL: completeResponse[j].productURL
                });

            }
        }
    }

    function mycomparator(a, b) {
        return parseInt(b.aRank) - parseInt(a.aRank);
    }
    array = array.sort(mycomparator);

    for (var i = array.length; i--;) {
        if (array[i].aRank === -1) {
            array.splice(i, 1);
        }
    }
    array.splice(0, 1);
    return array;
}


// Initiliase the event listeners for .line and .selected
function initialise2() {
    var moveLeft = 20;
    var moveDown = 10;

    $('.line').click(function () {
        if ($(this).attr("url").length > 2)
            window.open($(this).attr("url"));
    });

    $('.selected').click(function () {
        if ($(this).attr("url").length > 2)
            window.open($(this).attr("url"));
    });

    $(".line").hover(function (e) {
        var id = $(this).attr("id");
        $(this).css("background-color", "#FF0000");
        $("div#explanation" + id).css("display", "block")
    }, function () {
        $(this).css("background-color", "");
        var id = $(this).attr("id");
        $("div#explanation" + id).hide();
    });


    $(".line").mousemove(function (e) {
        var id = $(this).attr("id");
        $("div#explanation" + id).css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
    });
    $(".selected").hover(function (e) {
        var id = $(this).attr("id");
        $(this).css("background-color", "#FF0000");
        $("div#explanation" + id).css("display", "block")
    }, function () {
        $(this).css("background-color", "");
        var id = $(this).attr("id");
        $("div#explanation" + id).hide();
    });

    $(".selected").mousemove(function (e) {
        var id = $(this).attr("id");
        $("div#explanation" + id).css('top', e.pageY + moveDown).css('left', e.pageX + moveLeft);
    });
}


/*
 * Add graph adds a line with the correseponding values or array
 * The jsonResponse contains the values of the passed reference
 * r is the position of the product in the jsonResponse
 * Returns a concatened list of graph elements
 */
function addgraph(array, jsonResponse, r) {
    var attachment = $("<div>").addClass("rankedproduct-attr-data");

    var checker = search(array, jsonResponse[r].productID) - array.length + 50;

    if (checker > 0) {
        //only show the top 50
        var start = Math.max(0, array.length - 50);
        //number of prodcuts in top 50
        var numProducts = Math.min(50, array.length)

        for (var x = start; x < array.length; x++) {
            var rankValue = (array[x].aSize - array[x].aRank + 1) / array[x].aSize;
            rankValue = rankValue * 50;
            var temp = $("<div>").addClass("line").attr("style", "width: " + 100 / numProducts + "px;height : " + rankValue + "px;top:" + (50 - rankValue) + "px;").attr("id", array[x].aID);
            //temp.attr("url",array[x].aURL);	
            if (array[x].aID == jsonResponse[r].productID) {
                temp = $("<div>").addClass("selected").attr("style", "width: " + 100 / numProducts + "px;height : " + rankValue + "px;top:" + (50 - rankValue) + "px;background-color:#000000").attr("id", array[x].aID);
                //temp.attr("url",array[x].aURL);	
            }
            temp.clone().appendTo(attachment);
        }
    } else {
        var heading_name = $("<div>").addClass("rankexplanation-attr-name").text("Rank > 50");
        heading_name.clone().appendTo(attachment);
    }
    return attachment;

}


/*
 * The function search performs a linear search of the location of ID in the array based on the key
 * Input paramters : array and the key to be searched
 * Return value : The index if found else 0
 */
function search(array, key) {
    for (var i = 0; i < array.length; i++) {
        if (array[i].aID == key)
            return i;
    }
    return 0;
}

/*
 * Used in the hover effect where the display is constructed for each row
 * Input parameters : exp - the explanation array
 * Return Values : the div tag with added row
 */

function addTableEntry(exp) {
    var rankValue = (exp.productsNum - exp.rank + 1) / exp.productsNum;
    rankValue = rankValue * 100;
    var row = $("<div>").attr("style", "display:table-row");
    var column1 = $("<div>").attr("style", "display:table-cell;width:40%").text(exp.name);
    var color = stringToColorCode(exp.name);
    var column2 = $("<div>").addClass("progress-bar").attr("style", "display: table-cell;width:" + rankValue + "%;background:#" + color.split("").reverse().join("") + ";");
    var temporary2 = $("<div>").text("&nbsp;").html();
    temporary2 = temporary2.replace("&amp;", "&");
    var buffer = column2;
    buffer.append(temporary2);
    column2 = $("<div>").attr("style", "display:table-cell;");
    column2.append(buffer);
    row.append(column1);
    row.append(column2);

    return row;
}

/*
 * Function adds the name of the attribute to thetable
 * Input paramters : The name to be added
 * Return values : The div tag which the name added
 */
function addTableEntryName(name) {
    var row = $("<div>").attr("style", "display:table-row");
    var column1 = $("<div>").attr("style", "display:table-cell;width:40%").text(name);
    row.append(column1);
    return row;
}

$("#product-area").ready(function () {
    addFeature();
    sendFeatures();
});
