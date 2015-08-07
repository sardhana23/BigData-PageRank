function register() {
	document.getElementById('keywords').onkeypress=function(e){
				if(e.keyCode==13){
					document.getElementById('searchButton').click();
				}
			}
}

function performSearch(){
	//validate input
	
	//make the rest call
	
	var inp = document.getElementById("keywords").value.toLowerCase();
	document.getElementById("desc").innerHTML = "Displaying results for : " + document.getElementById("keywords").value;
	var keywords= inp.split(" ");
	var formatkey = "\'"+keywords.join("\',\'")+"\'";

	var query = "MATCH (a:Page) WHERE ANY (x IN a.keywords WHERE x in ["+formatkey+"]) RETURN a.unique_name, a.name, a.page_rank ORDER BY a.page_rank DESC LIMIT 50";
	//alert(query);
	$.post("http://localhost:7474/db/data/cypher",
    {
        "query" : query
    },
    function(data, status){
		var tBody = document.getElementById("tableBody");
		tBody.innerHTML = "";
		
		for (i = 0; i < data.data.length; i++) {
			//console.log(data.data[i]);
			
			var tr = document.createElement('TR');
				
				var td1 = document.createElement('TD')
				var td2 = document.createElement('TD')
				var td3 = document.createElement('TD')
				td1.innerHTML = data.data[i][0]
				td2.innerHTML = "<a target=\"_blank\" href=\"file:\\\\"+data.data[i][1]+"\">" + data.data[i][1] + "</a>"
				td3.innerHTML = data.data[i][2].toFixed(3)
				tr.appendChild(td1)
				tr.appendChild(td2)
				tr.appendChild(td3)
				
			tBody.appendChild(tr);
			
		}
		
    });
		
	//display results
           
}