package com.CK;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<Integer> one = new ArrayList<>();
        one.add(0);
        one.add(1);
        List<Integer> two = new ArrayList<>();
        two.add(1);
        two.add(2);
        List<Integer> three = new ArrayList<>();
        three.add(2);
        three.add(0);
        List<Integer> four = new ArrayList<>();
        four.add(1);
        four.add(3);
        List<List<Integer>> list = new ArrayList<>();
        list.add(one);
        list.add(two);
        list.add(three);
        list.add(four);
        new Solution().criticalConnections(4, list);
    }
}

// TLE
class Solution1 {
    private class Node {
        int val;
        PriorityQueue<Integer> neighbors;

        public Node(int _val) {
            val = _val;
            neighbors = new PriorityQueue<>();
        }

        private void addNeighbor(int neighbor) {
            neighbors.offer(neighbor);
        }
    }

    private Set<String> edges;
    private List<Node> nodes;

    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        nodes = new ArrayList<>();
        edges = new HashSet<>();

        for (int i = 0; i < n; i++) {
            nodes.add(new Node(i));
        }

        for (List<Integer> connection : connections) {
            int from = connection.get(0), to = connection.get(1);
            String edge = from <= to ? from + "-" + to : to + "-" + from;
            edges.add(edge);
            nodes.get(from).addNeighbor(to);
            nodes.get(to).addNeighbor(from);
        }

        boolean[] visited = new boolean[n];
        for (int st = 0; st < n; st++) {
            visited[st] = true;
            List<Integer> dfsList = new ArrayList<>();
            dfsList.add(st);
            findLoop(nodes, st, visited, dfsList);
            visited[st] = false;
        }
        List<List<Integer>> res = new ArrayList<>();
        Iterator itr = edges.iterator();
        while (itr.hasNext()) {
            String edge = (String) itr.next();
            String[] array = edge.split("-");
            List<Integer> singleRes = new ArrayList<>();
            singleRes.add(Integer.valueOf(array[0]));
            singleRes.add(Integer.valueOf(array[1]));
            res.add(singleRes);
        }
        return res;
    }

    private void findLoop(List<Node> nodes, int st, boolean[] visited, List<Integer> dfsList) {
        int curr = dfsList.get(dfsList.size() - 1);

        Iterator itr = nodes.get(curr).neighbors.iterator();
        while (itr.hasNext()) {
            int next = (int) itr.next();

            if (dfsList.size() > 2 && visited[next] && next == st) {
                for (int i = 0; i < dfsList.size(); i++) {
                    int from, to;
                    if (i == 0) {
                        from = dfsList.get(i);
                        to = dfsList.get(dfsList.size() - 1);
                    } else {
                        from = dfsList.get(i - 1);
                        to = dfsList.get(i);
                    }
                    String edge = from <= to ? from + "-" + to : to + "-" + from;
                    edges.remove(edge);
                }
                return;
            }

            if (next <= curr || visited[next])
                continue;

            visited[next] = true;
            dfsList.add(next);
            findLoop(nodes, st, visited, dfsList);
            visited[next] = false;
            dfsList.remove(dfsList.size() - 1);
        }
    }
}

// Tarjan
class Solution {
    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        int[] disc = new int[n], low = new int[n];
        // use adjacency list instead of matrix will save some memory, adjmatrix will cause MLE
        List<Integer>[] graph = new ArrayList[n];
        List<List<Integer>> res = new ArrayList<>();
        Arrays.fill(disc, -1); // use disc to track if visited (disc[i] == -1)
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        // build graph
        for (int i = 0; i < connections.size(); i++) {
            int from = connections.get(i).get(0), to = connections.get(i).get(1);
            graph[from].add(to);
            graph[to].add(from);
        }

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, low, disc, graph, res, i);
            }
        }
        return res;
    }

    int time = 0; // time when discover each vertex

    private void dfs(int current, int[] low, int[] disc, List<Integer>[] graph, List<List<Integer>> res, int parent) {
        disc[current] = low[current] = ++time; // discover current
        for (int j = 0; j < graph[current].size(); j++) {
            int child = graph[current].get(j);
            if (child == parent) {
                continue; // if parent vertex, ignore
            }
            if (disc[child] == -1) { // if not discovered
                dfs(child, low, disc, graph, res, current);
                low[current] = Math.min(low[current], low[child]);
                if (low[child] > disc[current]) {
                    // current - child is critical, there is no path for child to reach back to current or previous vertices of current
                    res.add(Arrays.asList(current, child));
                }
            } else { // if child discovered and is not parent of current, update low[current], cannot use low[child] because current is not subtree of child
                low[current] = Math.min(low[current], disc[child]);
            }
        }
    }
}

