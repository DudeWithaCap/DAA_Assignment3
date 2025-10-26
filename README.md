Design and Analysis of Algorithms

Assignment 3 – Minimum Spanning Tree, Prim and Kruskal algorithms

Adilet Kabiyev

SE-2433 

This assignment focuses on implementation of Prim's and Kruskal's algorithms to find Minimum Spanning Tree and their analysis.

Prim’s Algorithm:
Prim’s algorithm builds a Minimum Spanning Tree by starting from a single vertex and repeatedly adding the smallest-weight edge that connects a vertex in the MST to a vertex outside, expanding one vertex at a time.

Kruskal’s Algorithm:
Kruskal’s algorithm builds an MST by sorting all edges by weight and adding them one by one, avoiding cycles, until all vertices are connected.

<img width="482" height="194" alt="image" src="https://github.com/user-attachments/assets/5e3db170-013c-4ccf-a5d1-50b9af44e290" />

<img width="516" height="194" alt="image" src="https://github.com/user-attachments/assets/f8e1b6f5-ff6c-4dec-ab2b-a2a228d48517" />

This is summarization of gathered metrics for analysis of algorithms. Analysis was done using 5 different datasets, stored in input.json. 

Based off of these metrics we can compare the algorithms both theory and practice-wise.



Theory:

Prim’s: Better for dense graphs due to adjacency list/priority queue, grows MST from a single node.

Kruskal’s: More efficient on sparse graphs, sorts edges and uses union-find; slightly higher overhead for dense graphs.


Practice:

Execution times are generally similar. Prim's algorithm is slightly faster on bigger graphs, while Kruskal is competitive on small graphs.

Operation counts: Prim's tends to perform more operations as graph size increases, due to repeated priority queue updates; Kruskal’s union-find approach scales more efficiently in large graphs.



Conclusion

Kruskal's algorithm shows less operations count for same graphs where Prim's algorithm operation count almost doubles in number in big graphs, despite Kruskal's execution time being slightly longer. Meanwhile Prim's algorithm is faster and has less operation count for smaller graphs, suggesting it would be better fit for small graphs.


