/**
 * webAPI.js's purpose is to provide front end functionality to the webpage made for
 * CE29x challenge week
 *
 * created by Mal - Ma18533 - 1802882, jan 2020
 */

/**
 * query's main purpose is to take a URL, method, element, and query type, and send an a-synchronous
 * http request to the HTTP backend. The element, DOM object, then has the result inserted either
 * as an error message or a successful json string by utilizing InjectCustomJson class
 * 	@params
 *		url, String, the URL/URI to be queried
 *		method, String, the request method i.e "GET", "PUT"..
 *		inputElement, DOM Object, element for result to be injected into
 *		queryType, String, identifies what query is being made i.e "cars", "users"..
 * @returns
 *		none
 */
function query(url, method, inputElement, queryType)
{
	$.ajax({
		type: method,
		beforeSend: (request) =>
		{
			request.setRequestHeader("Content-Type", "application/json");
		},
		url: url,
		dataType: "json",
		success: (result) =>
		{
			/*inputElement.innerHTML = JSON.stringify(result, null, 2);
			 inputElement.value = JSON.stringify(result, null, 2); */
			var injector = new InjectCustomJson(queryType, inputElement);
			injector.insert(result);
		},
		error: (xhr, status, error) =>
		{
			inputElement.innerHTML = xhr.responseText + ", " + status + ", " + error;
			inputElement.value = xhr.responseText + ", " + status + ", " + error;
		}
	});
}

/**
 * class InjectCustomJson's purpose is to contain member variables indication which URI query
 * has been made, alongside the output element requiring injection. Member functions are then
 * made to convert the json result to a valid string, including float number prices which
 * JSONStringify modify, hence requirement for this class
 */
class InjectCustomJson
{
	/**
	 * default constructor
	 * @params
	 *		type, String, indicating what type of query has been used "cars", "users", or "manufacturers"
	 *		element, html DOM object for text to be injected into
	 */
	constructor(type, element)
	{
		this.type = type;
		this.element = element;
		this.tab = "";
	}
	
	/**
	 * insert's purpose is to take a result, and depending on the type constructed call function for that type
	 * @params
	 *		result, json object returned by AJAX call
	 * @returns
	 *		none
	 */
	insert(result)
	{
		switch(this.type)
		{
			case "cars":
				this.insertCar(result);
				break;
			case "users":
				this.insertUser(result);
				break;
			case "manufacturers":
				this.insertManufacturer(result);
				break;
		}
	}
	
	/**
	 * insertCar's purpose is to take a result, create a pretty json string of the type cars and inject it
	 * into the instances element object
	 * @params
	 *		result, json object returned by AJAX call
	 * @returns
	 *		none
	 */
	insertCar(result)
	{
		var string = "[\n";
		this.tab = "   ";
		var counter = 0;
		for (var car in result)
		{
			string += this.tab + "{\n";
			this.tab += "    ";
			string += this.tab + "\"id\"" + ": \"" + result[car]["id"] + "\",\n" + this.tab +"\"manufacturerName\": \""
			+ result[car]["manufacturerName"] + "\",\n" + this.tab + "\"model\": \"" + result[car]["model"] 
			+ "\",\n" + this.tab + "\"bodyType\": \"" + result[car]["bodyType"] + "\",\n" + this.tab
			+ "\"year\": " + result[car]["year"] + ",\n" + this.tab + "\"retailPrice\": " + parseFloat(result[car]["retailPrice"]).toFixed(2)
			+ "\n";
			this.tab = "    ";
			string += this.tab + "}";
			if (counter != result.length - 1) 
			{
				string += ",\n";
			}
			counter++;
		}
		string += "\n]";
		this.element.innerHTML = string;
		this.element.value = string;
	}
	
	/**
	 * insertUser's purpose is to take a result, create a pretty json string of the type cars and inject it
	 * into the instances element object
	 * @params
	 *		result, json object returned by AJAX call
	 * @returns
	 *		none
	 */	
	insertUser(result)
	{
		var string = "[\n";
		this.tab = "  ";
		var counter = 0;
		for (var user in result)
		{
			string += this.tab + "{\n";
			this.tab += "  ";
			string += this.tab + "\"id\"" + ": \"" + result[user]["id"] + "\",\n" + this.tab + "\"fullname\": \""
			+ result[user]["fullname"] + "\",\n" + this.tab + "\"postCode\": \"" + result[user]["postCode"] + "\",\n" + this.tab 
			+ "\"cars\": [\n";
			this.tab += "  ";
			var counter2 = 0;
			for (var car in result[user]["cars"])
			{
				string += this.tab + "{\n";
				this.tab += "  ";
				string += this.tab + "\"id\"" + ": \"" + result[user]["cars"][car]["id"] + "\",\n" + this.tab + "\"plateNumber\": \"" + result[user]["cars"][car]["plateNumber"] + "\",\n" 
				+ this.tab + "\"manufacturerName\": \"" + result[user]["cars"][car]["manufacturerName"] + "\",\n" + this.tab + "\"model\": \"" 
				+ result[user]["cars"][car]["model"] + "\",\n" + this.tab + "\"bodyType\": \"" + result[user]["cars"][car]["bodyType"] + "\",\n" + this.tab
				+ "\"year\": " + result[user]["cars"][car]["year"] + ",\n" + this.tab + "\"purchasePrice\": " + parseFloat(result[user]["cars"][car]["purchasePrice"]).toFixed(2)
				+ "\n" + this.tab +"\"retailPrice\": " + parseFloat(result[user]["cars"][car]["retailPrice"]).toFixed(2) + "\n";
				this.tab = "      ";
				string += this.tab + "}";
				if (counter2 != result[user]["cars"].length) string += ",\n";
				counter2++;
			}
			this.tab = "    ";
			string += this.tab + "]\n";
			this.tab = "  ";
			string += this.tab + "}";
			if (counter != result.length - 1) 
			{
				string += ",\n";
			}
			counter++;
		}
		string += "\n]";
		this.element.innerHTML = string;
		this.element.value = string;
	}

	/**
	 * insertManufacturer's purpose is to take a result, create a pretty json string of the type cars and inject it
	 * into the instances element object
	 * @params
	 *		result, json object returned by AJAX call
	 * @returns
	 *		none
	 */	
	insertManufacturer(result)
	{
		var string = "[\n";
		this.tab = "  ";
		var counter = 0;
		for (var manufacturer in result)
		{
			string += this.tab + "{\n";
			this.tab += "  ";
			string += this.tab + "\"id\"" + ": \"" + result[manufacturer]["id"] + "\",\n" + this.tab + "\"name\": \""
			+ result[manufacturer]["name"] + "\",\n" + this.tab + "\"country\": \"" + result[manufacturer]["country"] + "\",\n" + this.tab 
			+ "\"cars\": [\n";
			this.tab += "  ";
			var counter2 = 0;
			for (var car in result[manufacturer]["cars"])
			{
				string += this.tab + "{\n";
				this.tab += "  ";
				string += this.tab + "\"id\"" + ": \"" + result[manufacturer]["cars"][car]["id"] + "\",\n" + this.tab
				+ "\"manufacturerName\": \"" + result[manufacturer]["cars"][car]["manufacturerName"] + "\",\n" + this.tab + "\"model\": \"" 
				+ result[manufacturer]["cars"][car]["model"] + "\",\n" + this.tab + "\"bodyType\": \"" + result[manufacturer]["cars"][car]["bodyType"] + "\",\n" + this.tab
				+ "\"year\": " + result[manufacturer]["cars"][car]["year"] + ",\n"
				+ this.tab +"\"retailPrice\": " + parseFloat(result[manufacturer]["cars"][car]["retailPrice"]).toFixed(2) + "\n";
				this.tab = "      ";
				string += this.tab + "}";
				if (counter2 != result[manufacturer]["cars"].length) string += ",\n";
				counter2++;
			}
			this.tab = "    ";
			string += this.tab + "]\n";
			this.tab = "  ";
			string += this.tab + "}";
			if (counter != result.length - 1) 
			{
				string += ",\n";
			}
			counter++;
		}
		string += "\n]";
		this.element.innerHTML = string;
		this.element.value = string;
	}
}

/**
 * checkString's purpose is to check if string contains any MySql escape characters and return
 * false if it does
 *
 * @params
 *		value - String, string to be checked
 * @returns 
 *		boolean, true if string is valid without escape characters, false otherwise
 */
function checkString(value)
{
	var isit = String(value);
	for (let i = 0; i < value.length; i++)
	{
		if (value.charAt(i) == ';' || value.charAt(i) == '\\' || value.charAt(i) == '\''
		|| value.charAt(i) == '\"') isit = false;
	}
	if (value == "") isit = false;
	return isit;
};

/**
 * checkDigit's purpose is to check if digit is a valid positive integer
 *
 * @params
 *		value - int/float, digit to be checked
 * @returns 
 *		boolean, true if string is valid without escape characters, false otherwise
 */
function checkDigit(value)
{
	var isnum = parseInt(value);
	if (value < 0) isnum = false;
	if (value == 0) isnum = true;
	if (value == "") isnum = false;
	return isnum;
};

/**
 * Attaches event listeners to the SHOW buttons which in turn shows/hides the proceeding rows
 * for that api query
 */
var showButtons = document.getElementsByClassName("button");
for (let i = 0; i < showButtons.length; i++)
{
	showButtons[i].onclick = function (e)
	{
		switch(this.id)
		{
			case "displayCars":
				var rows = document.getElementsByClassName("carsRow");
				for (let j = 0; j < rows.length; j++)
				{
					if (rows[j].style.display == "table-row" || rows[j].style.display == null) 
					{
						rows[j].style.display = "none";
						this.innerHTML = "SHOW";
					}
					else 
					{
						rows[j].style.display = "table-row";
						this.innerHTML = "HIDE";
					}
				}
				break;
			case "displayCar":
				var rows = document.getElementsByClassName("carRow");
				for (let j = 0; j < rows.length; j++)
				{
					if (rows[j].style.display == "table-row" || rows[j].style.display == null) 
					{
						rows[j].style.display = "none";
						this.innerHTML = "SHOW";
					}
					else 
					{
						rows[j].style.display = "table-row";
						this.innerHTML = "HIDE";
					}
				}
				break;
			case "displayUsers":
				var rows = document.getElementsByClassName("usersRow");
				for (let j = 0; j < rows.length; j++)
				{
					if (rows[j].style.display == "table-row" || rows[j].style.display == null) 
					{
						rows[j].style.display = "none";
						this.innerHTML = "SHOW";
					}
					else 
					{
						rows[j].style.display = "table-row";
						this.innerHTML = "HIDE";
					}
				}
				break;
			case "displayManufacturers":
				var rows = document.getElementsByClassName("manufacturerRow");
				for (let j = 0; j < rows.length; j++)
				{
					if (rows[j].style.display == "table-row" || rows[j].style.display == null) 
					{
						rows[j].style.display = "none";
						this.innerHTML = "SHOW";
					}
					else 
					{
						rows[j].style.display = "table-row";
						this.innerHTML = "HIDE";
					}
				}
				break;
			case "addCar":
				var rows = document.getElementsByClassName("carsAddRow");
				for (let j = 0; j < rows.length; j++)
				{
					if (rows[j].style.display == "table-row" || rows[j].style.display == null) 
					{
						rows[j].style.display = "none";
						this.innerHTML = "SHOW";
					}
					else 
					{
						rows[j].style.display = "table-row";
						this.innerHTML = "HIDE";
					}
				}
				break;
		}
	};
}

/**
 * Attaches event listeners for the QUERY buttons, sending the relevant query by calling
 * query function with required information
 */
var queryButtons = document.getElementsByClassName("sendQuery");
var carsFields = document.getElementsByClassName("carsInput");
var manufacturerFields = document.getElementsByClassName("manufacturerInput");
var carsAddFields = document.getElementsByClassName("carsAddInput");
for (let i = 0; i < queryButtons.length; i++)
{
	queryButtons[i].onclick = function (e)
	{
		/* hides any previously displayed invalid inputs */
		var invalidInputs = document.getElementsByClassName("invalidInput");
		for(let span = 0; span < invalidInputs.length; span++)
		{
			invalidInputs[span].style.display = "none";
		}
		/* each case for specific URI call */
		var valid = true;
		var firstField = true;
		switch(this.id)
		{
			case "sendCars":
				var url = "/cars";
				/* loops over input fields adding to URL for query*/
				for (let j = 0; j < carsFields.length; j++)
				{
					if (carsFields[j].value != "")
					{
						/* input validation */
						valid = (valid && checkDigit(carsFields[j].value))? true: false;
						if (valid)
						{
							if (firstField)
							{
								url += "?" + carsFields[j].id + "=" + carsFields[j].value;
								firstField = false;
							}
							else
							{
								url += "&" + carsFields[j].id + "=" + carsFields[j].value;
							}
						}
						/* displays invalid input*/
						else 
						{
							carsFields[j].parentNode.children[1].style.display = "inline";
							document.getElementById("carsOutput").innerHTML = "> Output";
							document.getElementById("carsOutput").value = "> Output";
						}
					}
				}
				if (valid)
				{
					query(url, "GET", document.getElementById("carsOutput"), "cars");
				}
				break;
			case "sendCarsAdd":
				var url = "/cars";
				/* loops over input fields  adding to URL for query*/
				for (let j = 0; j < carsAddFields.length; j++)
				{
					switch(carsAddFields[j].id)
					{
						case "manufacturerID2":
						case "year3":
						case "retailPrice":
							valid = (valid && checkDigit(carsAddFields[j].value))? true: false;
							if (valid)
							{
								if (firstField)
								{
									url += "?" + ((carsAddFields[j].id == "year3")? "year": "")
									+ ((carsAddFields[j].id == "manufacturerID2")? "manufacturerID": "") + "=" + carsAddFields[j].value;
									firstField = false;
								}
								else
								{
									url += "&" + ((carsAddFields[j].id == "year3")? "year": carsAddFields[j].id)
									+ ((carsAddFields[j].id == "manufacturerID2")? "manufacturerID": "") + "=" + carsAddFields[j].value;
								}
							}
							/* input validation */
							else if (!checkDigit(carsAddFields[j].value))
							{
								carsAddFields[j].parentNode.children[1].style.display = "inline";
								document.getElementById("carsAddOutput").innerHTML = "> Output";
								document.getElementById("carsAddOutput").value = "> Output";
							}
							break;
						case "model":
						case "body":
							valid = (valid && checkString(carsAddFields[j].value))? true: false;
							if (valid)
							{
								if (firstField)
								{
									url += "?" + carsAddFields[j].id + "=" + carsAddFields[j].value;
									firstField = false;
								}
								else
								{
									url += "&" + carsAddFields[j].id + "=" + carsAddFields[j].value;
								}
							}
							else if (!checkString(carsAddFields[j].value))
							{
								carsAddFields[j].parentNode.children[1].style.display = "inline";
								document.getElementById("carsAddOutput").innerHTML = "> Output";
								document.getElementById("carsAddOutput").value = "> Output";
							}
							break;
					}					
				}
				if (valid)
				{
					query(url, "PUT", document.getElementById("carsAddOutput"), "cars");
				}
				break;
			case "sendCar":
				var url = "/car";
				var carInput = document.getElementById("modelID");
				if (carInput.value != "")
				{
					valid = checkDigit(carInput.value);
					if (valid)
					{
						url += "?" + carInput.id + "=" + carInput.value;
						query(url, "GET", document.getElementById("carOutput"), "cars");
						break;
					}
				}
				carInput.parentNode.children[1].style.display = "inline";
				document.getElementById("carOutput").innerHTML = "> Output";
				document.getElementById("carOutput").value = "> Output";
				break;
			case "sendUsers":
				var url = "/users";
				var carCount = document.getElementById("carCount");
				if (carCount.value != "")
				{
					valid = checkDigit(carCount.value);
				}
				if (valid)
				{
					url += (carCount.value != "")? "?" + carCount.id + "=" + carCount.value: "";
					query(url, "GET", document.getElementById("usersOutput"), "users");
					break;
				}
				carCount.parentNode.children[1].style.display = "inline";
				document.getElementById("usersOutput").innerHTML = "> Output";
				document.getElementById("usersOutput").value = "> Output";
				break;
			case "sendManufacturers":
				var url = "/manufacturer";
				/* loops over input fields  adding to URL for query*/
				for (let j = 0; j < manufacturerFields.length; j++)
				{
					if (manufacturerFields[j].value != "")
					{
						if (manufacturerFields[j].id == "year2")
							valid = (valid && checkDigit(manufacturerFields[j].value))? true: false;
						else valid = (valid && checkString(manufacturerFields[j].value))? true: false;
						if (valid)
						{
							if (firstField)
							{
								url += "?" + ((manufacturerFields[j].id == "year2")? "year": manufacturerFields[j].id) + "=" + manufacturerFields[j].value;
								firstField = false;
							}
							else
							{
								url += "&" + ((manufacturerFields[j].id == "year2")? "year": manufacturerFields[j].id) + "=" + manufacturerFields[j].value;
							}
						}
						else 
						{
							manufacturerFields[j].parentNode.children[1].style.display = "inline";
							document.getElementById("manufacturersOutput").innerHTML = "> Output";
							document.getElementById("manufacturersOutput").value = "> Output";
						}
					}
				}
				if (valid)
				{
					query(url, "GET", document.getElementById("manufacturersOutput"), "manufacturers");
				}
				break;
		}
	};
}

