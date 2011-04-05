//TODO: add inline help bubbles to fields.
//TODO: add copy paste button.
//TODO: Add ability to toggle off the line breaks in results.
//TODO: Add toggle to switch between pure hRecipe Spec and the Google version.
//TODO: Consider adding a non-formatting option that uses all span tags so users can style their own.
//TODO: reverse parse to populate from current page/field for editing.
//TODO: Add Options (Allow toggle of save previous data and when to discard. (no save, only if already generated hRecipe, only on clear button.))

    var nutritionTypes = ["Calories", "Fat", "Total Carbs", "Dietary Fiber", "Protein", "Sodium", "Cholesterol"];

    /**
     * Retrieve form data from localStoreage and parse Json to FormData object. 
     */
	function loadForm() {
		var formData = loadFormData();
		setFormValues(formData);
	}
    
    
/*    function saveFormData() {
        var formData = initData();
        saveFormData(formData);   
        debug('Form data saved');
    }*/
    
	/**
	 * Initializes the Form after load.
	 */
	function initForm() {
		debug("Load Blog posting.");
		//chrome.extension.getBackgroundPage().checkURLAction('load');
		$('#published').datepicker();
		$('#ok').button();
		$('#done').button();
		$('#previewBtn').button();
		$('#clear').button();
		$('#copy').button();
		$( "#tabs" ).tabs({
            show: function(event, ui) {
        }
		});
        //Hide Preview button
        $('#previewBtn').hide();
        
		$( "#tabs" ).bind( "tabsshow", function(event, ui) {
			if(ui.panel.id == "tabs-2") {
                $('#previewBtn').toggle();
                $('#previewBtn').val('Preview');
                $('#results').show();
                $('#preview').hide();
				var formData = initData();
				if($("#recipeForm").valid() && formData){
					var output = processData(formData);
					debug("output="+output);
					$('#results').val(output);
				} else {
					//fails validation, switch back to input form
					$( "#tabs" ).tabs('select',0);
				}
			} else {
                $('#previewBtn').hide();   
			}
            
		});

        $("#recipeForm").validate({
			rules: {
			 // simple rule, converted to {required:true}
				recName: {
                    required: true,
                    minlength: 2
				 },
				ingredient0: {
					required: true,
                    minlength: 2
				},
				cooktime: {
                    checkTime:true
                },
                preptime: {
					checkTime:true
			    }
		   },
		   messages: {
			 recName: {
			   required: "A recipe Title is required.",
			   minlength: jQuery.format("At least {0} characters required!")
			 },
			 ingredient0: {
			   required: "Requires 1 ingrediant.",
			   minlength: jQuery.format("At least {0} characters required!")
			 }			 
		   },
		   errorClass: "ui-state-highlight",
		   focusCleanup: true,
		   wrapper: "div"
		});
			
		/*
         * Custom Validator for checking time validations using regex
         */
		$.validator.addMethod('checkTime', function( value, element, params ) {
			if(/^([0-9]?[0-9])$/.test(value))
				return true; //if just 1-2 digits then its ok assuem mins.
            return ((value.length >= 1 )? /^(([0-1]?[0-9])|([2][0-3])):([0-5]?[0-9])(:([0-5]?[0-9]))?$/.test(value):true);
		}, "Times should be in the hh:mm format.");
		
		appendNutritionTypes("nutritionType");
		loadForm();
        
        //Set height to viewable minus the header.
        var tabs = $(this).find(".tab-content");
        var body = $(this).find("body");
        tabs.height( (body.height() - tabs.position().top-30));      
        
        //bind blur to save data
        $('body').bind('focusout', saveFormData);
        
        var link = document.createElement('link');
        link.type = 'image/x-icon';
        link.rel = 'shortcut icon';
        link.href = 'http://leeclarke.me/projects/favicon.ico';
        document.getElementsByTagName('head')[0].appendChild(link);

	}

	/**
     * Clears form fields and saved data.
     */
	function clearFormData() {
		deleteFormData();
		setFormValues(new FormData());
	}

	/**
     * Loads saved data into form fields.
     */
    function setFormValues(formData) {
		$('#recName').val(formData.$recName);			
		for(var i in formData.$ingredients) {
			if(i==0) {
				$('#ingredient0').val(formData.$ingredients[i]);
			} else {
				addFormField('ingredient', false, formData.$ingredients[i]);				
			}
		}
		if(formData.$ingredients.length == 0) {
			clearArrayFields('ingredient');
		}
		if(formData.$instructions.length == 0) {
					clearArrayFields('instruction');
		}
		
		$('#yield').val((formData.$yield == 0)?"":formData.$yield);
		$('#cooktime').val((formData.$cooktime == 0)?"":formData.$cooktime);
		$('#preptime').val((formData.$preptime == 0)?"":formData.$preptime);
		$('#photo').val((formData.$photos[0])?formData.$photos[0]:"");
		$('#author').val(formData.$author);
		$('#published').val(formData.$published);
		$('#instruction0').val(formData.$instructions[0]);
		for(i in formData.$instructions) {
			if(i==0) {
				$('#instruction0').val(formData.$instructions[i]);
			} else {
				if(formData.$instructions[i].length > 0)
					addFormField('instruction', false, formData.$instructions[i], true);				
			}
		}

		for(n in formData.$nutritions) {
			if(n==0) {
				$('#nutrition').val(formData.$nutritions[n]);
				$('.'+'nutritionType').val(formData.$nutritionTypes[n]);
			} else {
				if(formData.$nutritions[n].length > 0) {
					appendNutrition(formData.$nutritions[n], formData.$nutritionTypes[n]);
				}
			}
		}		
		if(formData.$nutritions.length == 0) {
			clearArrayFields('nutrition');
		}

		$('#summery').val(formData.$summery);
		var tagStrings = (formData.$tags)? formData.$tags.join():''; 
		$('#tag').val(tagStrings);		
	}	

	/* Add listener to catch popup close event. */
	//var background = chrome.extension.getBackgroundPage();
	addEventListener("unload", function (event) {
		initData();
	}, true);

	/**
	 * Clears the array field sets, due to differences in structure it take a bit of non-generic code, might want to revisit this as a TechDebt later.
	 */
	function clearArrayFields(fieldName) {		
		$("[id^='"+fieldName+"Row']").map(function(){
			debug("id=="+this.id);
			$(this).remove();			
		}).get();
		
		if(fieldName == 'instruction') {
			$('#instruction0').val('');
		} else if(fieldName == 'ingredient') {
			$('#ingredient0').val('');
		} else {
			$('#nutrition').val('');
			$('#'+ fieldName+'Type').val(nutritionTypes[0].toLowerCase());	
		}
	}
	
	/**
	 * collect data and create dto. Save to local.
	 */
	function initData() {

		debug("is valid=="+	$("#recipeForm").valid());

		var formData = new FormData();		
		formData.$recName = $('#recName').val();	
		var inglist = getFormArray('ingredient');
		
		inglist.unshift($('#ingredient0').val());
		formData.$ingredients = inglist;
		formData.$yield = $('#yield').val();
		formData.$cooktime = $('#cooktime').val();
		formData.$preptime = $('#preptime').val();
		formData.$photos = [$('#photo').val()];
		formData.$author = $('#author').val();
		formData.$published = $('#published').val();

		var instlist = getTextAreaArray('instruction');
		instlist.unshift($('#instruction0').val());

		debug("instlist=="+instlist);
		formData.$instructions = instlist;
		//formData.$instructions = $('#instructions').val();

		
		formData.$nutritionTypes = $("[id^=nutritionType]").map(function(){return $(this).val();}).get();
		formData.$nutritions = getFormArray('nutrition');
		debug('nutrition==' + formData.$nutritions.toString());
		formData.$summery = $('#summery').val('');
		formData.$tags = $('#tag').val().split(',');
		
		debug("formData.nTypes="+ formData.$nutritionTypes );
		saveFormData(formData);
		return formData;
	}	

	/**
	 * Retrieve array of data values for same name input fields.
	 */
	function getFormArray(id) {
		return $("input[id='"+id+"']").map(function(){return $(this).val();}).get();
	}

	function getTextAreaArray(id) {
		return $("textarea[id='"+id+"']").map(function(){return $(this).val();}).get();
	}

	/**
     * Adds new nutrition input fields
     */
	function appendNutrition(value,selectedType) {
		var elmtNumber = addFormField('nutrition', true, value);		
		var selectName = 'nutritionType'+elmtNumber;
		appendNutritionTypes(selectName);
		if(selectedType) {
			$('.'+selectName).val(selectedType);
		}
	}

	/**
     * Adds the array of Types to the new select
	 */
	function appendNutritionTypes(nutritionTypeFieldName) {
		var output = [];
		$.each(nutritionTypes, function(key, value) {
            output.push('<option value="'+ (value.replace(" ","")).toLowerCase() +'">'+ value +'</option>');
		});
		$("."+nutritionTypeFieldName).html(output.join(''));
		debug("##updated select name==" +"."+nutritionTypeFieldName);
	}
	
	/**
	 * Adds array type field to form.
	 * @param textarea - indicates the field should be a textarea.
	 * @return - element name where added
	 */
	function addFormField(fieldName, addSelect, value, textarea) {
		var rowCt = $("#"+fieldName + "List li").size();
		debug(rowCt);
		var newElmName = (fieldName + "Row"+rowCt);
		var newHtml = "<li id='"+newElmName+"' >";
		if(textarea) {
			newHtml += "<textarea id=\""+fieldName+"\" name=\""+ fieldName+"\" rows=\"2\" cols=\"43\" class=\"midItem xlarge-field-inset\">";
			if(value) {
				newHtml += value;
			}
			newHtml += "</textarea>";
		} else {
			newHtml += "<input type=\"text\" id=\""+fieldName+"\" name=\""+ fieldName+"\" ";
			if(value) {
				newHtml += " value=\""+value+"\" ";
			}
			if(addSelect) {
				newHtml += "/> <select id=\""+fieldName+"Type\" class=\""+fieldName+"Type"+ rowCt + "\"></select>";
			} else {
				newHtml += " class=\"xlarge-field-inset\"/>";
			}
		}

		newHtml += "</li>";
		
		$("#"+fieldName + "List").append(newHtml);
		$("#"+fieldName + "Row"+rowCt).effect("highlight", {}, 3000);		
		return rowCt;
	}
	

	/**
	 * enable tabs
	 */
	$(function() {
		$( "#tabs" ).tabs();
	});	

    var BLOGGER = "www.blogger.com";
	var BLOGGER_POST = "/post-edit.g";
	var BLOGGER_CREATE = "/post-create.g";

	
	/**
	 * //Cant use Chrome with the Blogger api with out getting an ugly popup... so odd, come on google really?
	 * Moving output to the popup to display for copy/paste
	 */
	function processData(data) {
		var currentURL = "";
		//debug		
		var toString = "rec-name:"+ data.$recName + " ingrediants:"+ data.$ingredients + " yield:" + data.$yield;
		debug("got data:  "+toString);

		var hRecipeCode = convertTohRecipe(data);
		debug("hrecipe=="+hRecipeCode);
		
		return hRecipeCode;
	}

	/* a few notes to be used for new features
	chrome.tabs.getSelected(null, function(tab) {    
	var url = tab.url;						
	jQuery.url.setUrl(url)
	debug("HOST="+jQuery.url.attr("host"));

			//var currField == document.activeElement;
			//chrome.tabs.executeScript(null, {code:"updateText('TESTER')"})
			//chrome.tabs.executeScript(null, {code:"console.log('active=='+document.activeElement)"})
	});   
	*/
	

	/**
	 * Converts data to the hRecipe format with default formatting tags.
	 */
	function convertTohRecipe(data) {
		var microformat = "<div class=\"hrecipe\">\n";
		if(data){
			
			microformat += ("<h1 class=\"fn\">" + data.$recName + "</h1>\n");
			//if(data.$summary) microformat += ("<><p class=\"summary\">" + data.$summary + "</p>\n");
			if(data.$author) microformat += ("<p><label>By </label><span class=\"author\">" + data.$author + "</span></p>\n");
			if(data.$published) microformat += ("<p>Published <span class=\"published\">" + data.$published + "</span></p>\n");

			if(data.$photos.length>0 && data.$photos[0].length > 0) {
				microformat += ("<img src=\"" + data.$photos[0] + "\" class=\"photo\" style=\"height:140px;width:140px;border-width:0px;\" alt=\"" + data.$recName + "\">\n");
			}
			if(data.$summery) microformat += ("<p class=\"summery\">" + data.$summery + "</p>\n");
			microformat +="<h2>Ingredients</h2>\n";
			microformat +="<ul>\n";
			for(var i in data.$ingredients) {
				if(data.$ingredients[i].length >0) {
					microformat += ("<li class=\"ingredient\">" + data.$ingredients[i] + "</li>\n");
				}
			}
			microformat +="</ul>\n";
			
			if(data.$instructions) {
				microformat +="<h2>Instructions</h2>\n";
				var listType = (data.$instructions.length==1)?"ul":"ol";
				microformat +="<" + listType + ">\n";
				for(i in data.$instructions) {
					if(data.$instructions[i].length >0) {
						microformat += ("<li class=\"ingredient\">" + data.$instructions[i] + "</li>\n");
					}
				}
				microformat +="</" + listType + ">\n";
			}
			
			if(data.$yield) microformat += ("<p><span style='font-weight:bold;'>Yield:</span><span class=\"yield\"> "+data.$yield+"</span></p>\n");
			
			if(data.$preptime || data.$cooktime) {
				microformat += "<span class=\"duration\">\n";
				var prep = "0M";
				var cook = "0M";
				
				
				if(data.$preptime) {
					prep = parseTime(data.$preptime);
					microformat += ("<p><span style='font-weight:bold;'>Prep Time:</span><span class=\"preptime\"><span class=\"value-title\" title=\""+prep+"\"></span> "+data.$preptime+"</span></p>\n");
					
				}
				if(data.$cooktime) {
					cook = parseTime(data.$cooktime);
					microformat += ("<p><span style='font-weight:bold;'>Cook time:</span><span class=\"cooktime\"><span class=\"value-title\" title=\""+cook+"\"></span> "+data.$cooktime+"</span></p>\n");				
				}
				
				microformat += "</span>\n";
			}
			
			if(data.$nutritions.length>0 && data.$nutritions[0].length > 0) {
				microformat +="<h2>Nutrition</h2>\n";
				microformat +="<p>\n";
				microformat +="<ul class=\"nutrition\">";
				for(var n in data.$nutritions) {
					if(data.$nutritionTypes[n].length >0) {
					microformat += "<li>"+toProperCase(data.$nutritionTypes[n])+": <span class=\""+ (data.$nutritionTypes[n].replace(" ","")).toLowerCase() + "\">" +(data.$nutritions[n] + "</span></li>\n");
					}
				}
				microformat +="</ul>\n</p>\n";
			}    
			if(data.$tags && data.$tags.length >0 && data.$tags[0].length > 0) {
				var tagCode = [];
				microformat +="<span style='font-weight:bold;'>Tags: </span>";
				for(var t in data.$tags) {
					tagCode.push("<span class=\"tag\">"+data.$tags[t]+"</span>");
				}	
				microformat += tagCode.join(',');
				microformat +="</span>";
			}
		}
		
		microformat += "</div>";
		return microformat;
	}

	/**
	 * Makes first letter of each word uppercase.
	 */
	function toProperCase(strIn) {
		var results = [];
		if(strIn) {
			var words = strIn.toLowerCase().split(" ");
			for(var w in words) {
				var first = words[w].charAt(0).toUpperCase();
				results.push(first + words[w].substr(1));
			}
		}
		return results.join(" ");
	}

	/**
	 * Parse method expects to recieve time formatted in the hh:mm format and simply yanks out the : and adds markers
	 */
	function parseTime(timeValue) {
		var result = "PT";
		var timeSplit = timeValue.split(":");
		if(timeSplit.length == 2) {
			result += (timeSplit[0]+"H"+timeSplit[1]+"M");
		} else {
			result += (timeValue+"M");
		}		
		
		return result;
	}

    /**
     * 
     */
    function showPreview() {
        debug($('#previewBtn').val());
        if($('#previewBtn').val() == 'Preview') {
            $('#previewBtn').val('Results');
        } else {
            $('#previewBtn').val('Preview');
        }
        $('#preview').html($('#results').val());
        $('#results').toggle();
        $('#preview').toggle();
    }

$(document).ready(initForm);
