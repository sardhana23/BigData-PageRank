MATCH (p1:Page)-[:LINKS_TO]->(p2)
RETURN p1.id as id1, p2.id as id2;
