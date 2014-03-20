/**
 * Global object to store the 
 * user's selected features
*/
	var specGlobalFeatureObject =
	{			
			names:[],
			values:[]
	};
	
	var rateGlobalFeatureObject =
	{
			names:[],
			values:[]			
	};
	 
/**
 * function called on page load.. 
 * Setting the accordion icon and size, 
 * slider ranges, and capture user interaction with sliders.
 * */	
$(function() {
		var icons = {
			      header: "ui-icon-circle-arrow-e",
			      activeHeader: "ui-icon-circle-arrow-s"
			    };
			    
	    $( "#accordion" ).accordion({
	    	  icons: icons,
	    	  heightStyle: "fill",
	    });
	    
	    $( "#accordion-resizer" ).resizable({
	        minHeight: 140,
	        minWidth: 200,
	        resize: function() {
	          $( "#accordion" ).accordion( "refresh" );
	        }
	      });
		
	    var div = $("div.slider-range");
	    var slider= $(".slider-range").slider({
  	        range: true,
  	        step:0.1,
		    values: [0,0],
		    slide: function (event, ui) {
		    	var min= $(this).closest("div").attr("min");		    	
		    	var max= $(this).closest("div").attr("max");
			    	
		    	if (min >=0 && max<=1)
		    	{
		    		$(this).slider("option","min",0);
			    	$(this).slider("option","max",1);			    			    		
		    	}else{
		    		if(min >=0 && max<=5)
			    	{
			    		$(this).slider("option","min",0);
				    	$(this).slider("option","max",5);			    			    				    			
		    		}else{
			    		 $(this).slider("option","min",min);
				    	 $(this).slider("option","max",max);			    	
		    		}
		    	}
		    	 var minVal = $(this).slider("option", "min"),
		    	 maxVal = $(this).slider("option", "max");
	        },
		    stop:function(){
		    	var id_div= $(this).closest("div").attr("id");
		 	 
		 	    var text=$(':input[type="text"][name="'+id_div+'"]');
		 	    text.val($(this).slider("values", 0)+" : "+ $(this).slider("values", 1));
	    	     var minVal = $(this).slider("values", 0),
	    	 	 maxVal = $(this).slider("values", 1);
	    	     var meanVal=(minVal+maxVal)/2;
		    	 addFeature(this,id_div,meanVal);
		    	}, 
		 });
	 
	});
	
	function findParentID(node, id)
	{			
		parent=node.parentNode;
		while(parent){
			if(parent.id && parent.id==id)				
				return true;
			parent=parent.parentNode;
		}
		return false;
			
	}	
	
	function setMinMax(min,max,id)
	{
	       $( "'#"+id+"'").val(min +"-" +max );		      
	}    
	
	//Serialize object before sending to the UI controller   			
	$.fn.serializeObject = function()
	{
	    var o = {};
	    var a = this.serializeArray();
	    $.each(a, function() {
		   if (o[this.name] !== undefined) {
		           if (!o[this.name].push) {
		               o[this.name] = [o[this.name]];
		           }
	            o[this.name].push(this.value || '');
	           } else {
		            o[this.name] = this.value || '';
		        }
		    });
		    return o;
	};
		
	/**
	 * Check if the user checked/unchecked a check box element.
	 * And add/remove it from the global feature object accordingly. 
	 * 
	 * @param chBox : check box input element
	 * @param name  : name of feature
	 * @param val	: value of feature
	 */	 
	 	
	function isChecked(chBox,name,val)
	{
		if($(chBox).prop('checked')){
			addFeature(chBox,name, val);			
		}else{
			removeFeature(chBox,name,val);
		}	
	}
	
	//remove element from an array
	function removeElem(arr) {
	    var what, a = arguments, L = a.length, ax;
	    while (L > 1 && arr.length) {
	        what = a[--L];
	        while ((ax= arr.indexOf(what)) !== -1) {
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
	function removeFeature(elem,name, val)
	{
		var exist=false;
		var isSpec =findParentID(elem,"main_specs");
		var isRate =findParentID(elem,"main_ratings");

		if(isSpec){				
			for(var i=0;i< specGlobalFeatureObject.names.length;i++)
			{
				console.log("specGlobalFeatureObject.names[i] " + specGlobalFeatureObject.names[i]);
				if(specGlobalFeatureObject.names[i] == name)
				{
					exist=true;
					removeElem(specGlobalFeatureObject.names, name);
					removeElem(specGlobalFeatureObject.values, val);				
					break;				
				}			
			}
		}else
		{
			if(isRate)		
			{
				for(var i=0;i< rateGlobalFeatureObject.names.length;i++)
				{
					console.log("rateGlobalFeatureObject.names[i] " + rateGlobalFeatureObject.names[i]);
					if(rateGlobalFeatureObject.names[i] == name)
					{
						rateGlobalFeatureObject.values[i]=val;
						exist=true;
						break;
					}			
				}
				if(!exist)
				{
					rateGlobalFeatureObject.names.push(name);
					rateGlobalFeatureObject.values.push(val);				
				}	
			
			}
		}
		console.log("specGlobalFeatureObject : "+JSON.stringify(specGlobalFeatureObject));
		console.log("rateGlobalFeatureObject : "+JSON.stringify(rateGlobalFeatureObject));

	}	
	
	/** 
	 * Add feature to the global feature object
	 * @param name of feature
	 * @param val of feature
	 */
	function addFeature(elem,name, val)
	{
		console.log("**************");		
		console.log("name : "+name);
		console.log("value : "+val);
		console.log("**************");
		
		var exist=false;
		
		var isSpec =findParentID(elem,"main_specs");
		var isRate =findParentID(elem,"main_ratings");
		
		if(isSpec){
			for(var i=0;i< specGlobalFeatureObject.names.length;i++)
			{
				console.log("specGlobalFeatureObject.names[i] " + specGlobalFeatureObject.names[i]);
				if(specGlobalFeatureObject.names[i] == name)
				{
					specGlobalFeatureObject.values[i]=val;
					exist=true;
					break;
				}			
			}
			if(!exist)
			{
				specGlobalFeatureObject.names.push(name);
				specGlobalFeatureObject.values.push(val);				
			}							
		}else{
			if(isRate){
				for(var i=0;i< rateGlobalFeatureObject.names.length;i++)
				{
					console.log("rateGlobalFeatureObject.names[i] " + rateGlobalFeatureObject.names[i]);
					if(rateGlobalFeatureObject.names[i] == name)
					{
						rateGlobalFeatureObject.values[i]=val;
						exist=true;
						break;
					}			
				}
				if(!exist)
				{
					rateGlobalFeatureObject.names.push(name);
					rateGlobalFeatureObject.values.push(val);				
				}	
				
			}
		}
			
		console.log("specGlobalFeatureObject : "+JSON.stringify(specGlobalFeatureObject));
		console.log("rateGlobalFeatureObject : "+JSON.stringify(rateGlobalFeatureObject));
	}
	
	/**
	 * Once submit button is clicked
	 * create an AJAX call to send the tweaked features
	 * to the UI controller
	 */
	function sendFeatures()
	{
		var sjData=JSON.stringify(specGlobalFeatureObject);
		var rjData=JSON.stringify(rateGlobalFeatureObject);
		 	
	    console.log(sjData);
	    console.log(rjData);

	     	$.ajax({
	 			type: "POST",
		 		dataType: "json",
		        url: 'sendFeatures.html',
		        data: ({dataSpec:sjData,dataRate:rjData}),
		        success: function(data) {
		        	console.log("Success..");
		        },
		        complete: function(data)
		        {
		        //	$("#rankedproduct-result").html(data).fadeIn('fast');
		        	console.log("Complete ");//+JSON.stringify(data));		        	
		        	//$("#rankedproduct-result").load();//.fadeIn('fast');		        	
		        	location.reload();
		        }
		      });			    		
	}