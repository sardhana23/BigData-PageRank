import org.apache.spark._
import org.apache.spark.graphx._
// To make some of the examples work we will also need RDD
import org.apache.spark.rdd.RDD
import java.io._

// Load the edges as a graph
val graph = GraphLoader.edgeListFile(sc, "edgelist.txt")
// Run PageRank
val ranks = graph.pageRank(0.0001).vertices

var r = ranks.map(line => (line._1.toString, line._2)).collectAsMap



r.foreach( p => {
	var statement = "MATCH (n:Page) WHERE n.id=" + p._1 + " SET n.page_rank = " + p._2 + " RETURN n;\n"
	
	val fw = new FileWriter("pageRanks.txt", true)
	try {
		fw.write(statement)
	}
	finally fw.close() 
	
})

System.exit(0)