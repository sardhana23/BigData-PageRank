import java.io._
import scala.util.control._
import java.util.Properties
import sys.process._

def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
 		val p = new java.io.PrintWriter(f)
  		try {
			 op(p) 
		} finally { 
			p.close() 
		}
	}

val inFile = "edgelist_unprocessed.txt"

val fileToRead = sc.textFile(inFile).toArray.drop(3)
val splitrecord =  fileToRead.map(line=>
	{
		if(line.split("\\|").size >=3)
			line.split("\\|")(1).trim+" "+line.split("\\|")(2).trim
	});

var outFile = "edgelist.txt"
printToFile(new File(outFile)){p=> splitrecord.foreach(p.println)}

System.exit(0)