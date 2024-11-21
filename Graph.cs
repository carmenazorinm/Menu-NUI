using System;
using System.Collections.Generic;
using System.Linq;
using Unity.VisualScripting;
using UnityEditor;
using UnityEngine;

public class Graph {

    private GameObject gameObject;

    private float[,] edgeMap;
    private Node[] nodes;
    /*public Graph(float[][] edgeMap, Node[] nodes){
        this.edgeMap = edgeMap;
        this.nodes = nodes;
    }*/

    public Graph(float[,] edgeMap, Vector3[] nodes, GameObject gameObject){
        this.edgeMap = edgeMap; //only positive values
        this.nodes = new Node[nodes.Length];
        for(int i=0; i < nodes.Length; i++){
            this.nodes[i] = new Node(nodes[i]);
        }
        this.gameObject = gameObject;
    }

    public void computeDijkstra(int start, int end){
        //start node, end node
        /*for(int i = 0; i < this.nodes.Length; i++){
            if(a.Equals(this.nodes[i])) indicesOfNodes[0] = i;
            if(b.Equals(this.nodes[i])) indicesOfNodes[1] = i;
        }*/

        nodes[start].setIsSelected(true);
        nodes[end].setIsSelected(true);

        int[] predecessor = new int[this.nodes.Length];
        float[] distance = new float[this.nodes.Length];
        bool[] done = new bool[this.nodes.Length];

        for(int i = 0; i < this.nodes.Length; i++){
            predecessor[i] = -1;
            distance[i] = float.PositiveInfinity;
            done[i] = false;
        }
        distance[start] = 0;

        List<int> nextValues = new List<int>
        {
            start
        };

        while(nextValues.Count > 0){
            int minimum = 0;
            for(int i = 1; i < nextValues.Count; i++){
                if (distance[nextValues[i]] < distance[nextValues[minimum]]){
                    minimum = i;
                }
            }

            for(int i = 0; i < this.edgeMap.GetLength(1); i++){
                if(done[i]) continue;
                var element = this.edgeMap[nextValues[minimum],i];
                if(element>0 && element+distance[nextValues[minimum]] < distance[i]){
                    distance[i] = element+distance[nextValues[minimum]];
                    predecessor[i] = nextValues[minimum];
                    if(!nextValues.Contains(i)){
                        nextValues.Add(i);
                    }
                }
            }

            done[nextValues[minimum]] = true;
            nextValues.RemoveAt(minimum);
        }

        List<Node> path = new List<Node>();
        if(done[end]){
            var current = end;
            var pred = predecessor[end];
            while(pred != -1){
                if(current != end) {
                    nodes[current].setIsPathMember(true);
                }

                path.Add(nodes[current]);

                //next loop preparation
                current = pred;
                pred = predecessor[current];
            }

            path.Add(nodes[current]);

        }

        createLineBetweenNodes(path);
        
    }

    private void createLineBetweenNodes(List<Node> path){
        LineRenderer lineRenderer = gameObject.AddComponent<LineRenderer>();

        // Material und Farbe einstellen
        lineRenderer.material = new Material(Shader.Find("Sprites/Default"));
        lineRenderer.sharedMaterial.SetColor("_Color", new Color(138,43,226));
        //lineRenderer.startColor =  new Color(138,43,226);
        //lineRenderer.endColor = new Color(138,43,226);

        // Breite der Linie einstellen
        lineRenderer.startWidth = 0.1f;
        lineRenderer.endWidth = 0.1f;

        // Anzahl der Punkte (Start und Ende)
        lineRenderer.positionCount = path.Count;
        for(int i = 0; i < lineRenderer.positionCount; i++){
            lineRenderer.SetPosition(i, path[i].getPosition());
        }
        
    }

}