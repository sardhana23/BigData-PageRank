
import scala.math._
import java.io._
import scala.util.control._
import java.util.Properties
import scala.xml.XML
import org.apache.spark.rdd.RDD
import scala.collection.JavaConversions._
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

var nodeCreationArray: Array[String] = Array()
	
	def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
 		val p = new java.io.PrintWriter(f)
  		try {
			op(p) 
		} finally { 
			p.close() 
		}
	}
	
	def validateURL(url: String): Boolean = {
		var result = url.replace("/", ":::").split(":::")
		if(result.size<2)
			return false
        if(url.contains("GNU_Free_Documentation") || url.contains("disclaimer.htm"))
            return false
        if(url.contains("png.htm") || url.contains("jpg.htm") || url.contains("/index/") || url.contains(".css") || url.contains(".ico") || url.contains("mailto"))
            return false
        return true
    }
	
	 def extractFilename(url: String): String = {
		if(!validateURL(url))
			return "NULL";
        var result = url.replace("/", ":::").split(":::")
        var finalName = result(result.size-2)+"_"+result(result.size-1)
        return finalName
    }
	
	def extracthref(lines: String, nodeName:String) {
		
		val reg = """\s*(?i)href\s*=\s*(\"([^"]*\")|'[^']*'|([^'">\s]+))""".r
		val hrefString :String = (reg findAllIn lines).mkString(",")
		val splithrefString = hrefString.split(",");
		var hrefVal: Array[String] = Array()
		splithrefString.foreach(
			line => {
				if(line.split("=").size>=2) 
					hrefVal +:= line.split("=")(1).dropRight(1)
		})
		
		val extractedVal = hrefVal.map(line=>(extractFilename(line)))
		var result: Array[String] = Array()
		var result1: Array[String] = Array()
		extractedVal.foreach(line=>{
			if(!(line=="NULL")) {
				result :+= "MATCH (a:Page),(b:Page) WHERE a.unique_name = '"+nodeName+"' AND b.unique_name = '"+line+"' CREATE (a)-[r:LINKS_TO]->(b);\n"
				result1 :+= nodeName + " " + line + "\n"
			}
			
		})
		
		result.foreach(line => {
			val fw = new FileWriter("relations.txt", true)
			try {
				fw.write(line)
			}
			finally fw.close() 
		})
		
		
	}
	
	def getHref(fname: String) {
		val fileToRead = sc.textFile(fname.replace("\\","\\\\"))
		var nodeNameSplit = fname.replace("\\", ":::").split(":::")
		var nodeName = nodeNameSplit(nodeNameSplit.size-2)+"_"+nodeNameSplit(nodeNameSplit.size-1)
		val lineContainHref = fileToRead.filter(line=>((line.contains("href"))))
		val hrefVal = lineContainHref.foreach(line=>(extracthref(line,nodeName)))
		
	}
	

	val listOfFiles = sc.textFile("filelist.txt").toArray.foreach(line => getHref(line))

System.exit(0)