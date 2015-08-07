

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


	var count = 1
	var nodeCreationArray: Array[String] = Array()
	
	val conf = new SparkConf().setAppName("KeywordParser")
    conf.setMaster("local[2]")
    val sc = new SparkContext(conf)
	
	def isAllDigits(x: String) = x forall Character.isDigit
	
	def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
 		val p = new java.io.PrintWriter(f)
  		try {
			 op(p) 
		} finally { 
			p.close() 
		}
	}
	
	def getKeywordAndFrameStatement(fname: String) {
		val fileToRead = sc.textFile(fname.replace("\\","\\\\"))
		val keywordsmeta = fileToRead.filter(line=>((line.contains("keywords")) && (line.contains("meta")))).toArray
		
		val rest = fileToRead.filter(line=>(!line.contains("keywords"))).toArray.mkString("")
		var result = new String()
		if(keywordsmeta.length > 0){
               		keywordsmeta.foreach(line=>{result=line.split("\"")(1).split(",").filter(word=>rest.contains(word)).filter(line=>(line!=fname)).filter(w=>(!w.contains("."))).filter(w=>(!w.contains("\\/"))).toArray.mkString("\",\"")})
		} 
		
		var nodeNameSplit = fname.replace("\\", ":::").split(":::")
		var nodeName = nodeNameSplit(nodeNameSplit.size-2)+"_"+nodeNameSplit(nodeNameSplit.size-1)
		
		var statement = "CREATE (node_" + nodeName.replace(".","_").replace("%", "_").replace("-","_") + ": Page {id: "+count+", unique_name: \"" + nodeName + "\", name: \"" + fname.replace("\\","\\\\") + "\", keywords: [\"" + result.replace(" ","\",\"").toLowerCase() + "\"]});"
		count += 1
		
		nodeCreationArray +:= statement
	}


		
		val listOfFiles = sc.textFile("filelist.txt").toArray.foreach(line => getKeywordAndFrameStatement(line))
		
		printToFile(new File("nodeCreation.txt")){p=> nodeCreationArray.foreach(p.println)}
