
import java.io.File


	var fileListArray: Array[String] = Array()
	
	def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
 		val p = new java.io.PrintWriter(f)
  		try {
			 op(p) 
		} finally { 
			p.close() 
		}
	}

	def listFilesRecursively(path: String) {
		for (file <- new File(path).listFiles) 
		{ 
			if(file.isDirectory)
				listFilesRecursively(file.toString)
			else 
			{
				if(file.toString.contains(".htm"))
				{
					fileListArray +:= file.toString
				}
			}
		}
	}


		
		var inFile = "C:\\wpcd\\wp"
		var outFile = "filelist.txt"
		
		listFilesRecursively(inFile)
		printToFile(new File(outFile)){p=> fileListArray.foreach(p.println)}
